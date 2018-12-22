import java.io.FileOutputStream;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 实验数据处理类包括去重复、繁体转简体等
 *
 * @Author Bin
 * @Date 2018/11/22 17:45
 * @Param
 * @return
 **/
public class data_process {
    private ResultSet rs = null;
    private mysqlcon con = null;
    private List<String> atts = new ArrayList<String>();
    private Levenshtein levenshtein;
    private ZHConverter converter;

    /**
     * 获取所有景点名称的List集合
     *
     * @return java.util.List<java.lang.String>
     * @Author Bin
     * @Date 2018/11/22 17:43
     * @Param []
     **/
    public List<String> GetAttractions() {
        con = new mysqlcon();
        String sql = "SELECT DISTINCT t.ATTRACTION FROM reviews t";
        rs = con.ExcuteWithRS(sql);
        try {
            while (rs.next()) {
                atts.add(rs.getString("ATTRACTION"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return atts;

    }

    /**
     * 对每一个景点的评论，进行内部的相似度计算，如果相似度大于0.8，则标记评论时间靠后的评论为重复评论
     *
     * @return void
     * @Author Bin
     * @Date 2018/11/22 9:46
     * @Param []
     **/
    public void TagRecover() {
        List<String> atts = this.GetAttractions();
        con = new mysqlcon();
        levenshtein = new Levenshtein();
        String sql = "";
        int rowsCount = 0;//当前景点的总评论数
        int currentID;//当前评论的RID（评论编号）
        int nextID;//下一条评论的RID
        String currentContent;//当前评论内容
        String nextContent;//下一条评论内容
        float sim;//两条评论的相似度
        Boolean isSuccess;//Update是否执行成功
        //循环各个景点，对每个景点分别根据编辑距离进行去重
        for (int i = 0; i <= atts.size(); i++) {
            //获取当前景点的评论集,并按时间升序排列
            sql = String.format("SELECT * FROM exp.reviews t WHERE t.ATTRACTION='%s' ORDER BY t.RTIME ASC", atts.get(i).toString());
            ResultSet rs = con.ExcuteWithRS(sql);
            try {
                //将光标移动到最后一行，获取当前景点的总行数
                rs.last();
                rowsCount = rs.getRow();
                //开始遍历每一条评论数据
                for (int j = 0; j < rowsCount; j++) {
                    //重新移动光标，移动至当前评论
                    rs.absolute(j + 1);
                    //获取当前评论的RID和评论内容
                    currentID = rs.getInt("RID");
                    currentContent = rs.getString("RCONTENT");
                    //遍历当前评论后面的所有评论，依次与当前评论进行相似度计算
                    for (int k = j + 1; k < rowsCount; k++) {
                        //重新移动光标
                        rs.absolute(k + 1);
                        //获取后面的评论的RID和评论内容
                        nextID = rs.getInt("RID");
                        nextContent = rs.getString("RCONTENT");
                        //相似度计算
                        sim = levenshtein.similarity(currentContent, nextContent);
                        //如果相似度大于0.8，则将下一条评论标记重复（通过reviews表中ISCOVER字段标识）
                        //因为在查询时按时间升序排序，所以后面的评论内容晚于当前评论时间，重复时应当删去时间靠后的评论
                        if (sim >= 0.8) {
                            sql = "UPDATE reviews t SET t.ISCOVER=1 WHERE t.RID=" + nextID;
                            isSuccess = con.ExcuteUpdate(sql);
                            if (isSuccess) {
                                con.Commit();

                                con.Close();
                                System.out.println("提交成功！当前评论RID：" + currentID + "   ;对比评论RID：" + nextID + "sim:" + sim);
                            }
                        }

                    }
                    System.out.println("RID为：" + currentID + "的评论，执行完毕！");
                }
                System.out.println("景点：" + atts.get(i) + "执行完毕！！！");
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        System.out.println("全部景点，执行完毕！！！");
    }

    /**
     * 将评论内容中的繁体转换成简体
     *
     * @return void
     * @Author Bin
     * @Date 2018/11/22 16:01
     * @Param []
     **/
    public void ConvertTradi2Simp() {
        con = new mysqlcon();
        converter = ZHConverter.getInstance(ZHConverter.SIMPLIFIED);
        String sql = "SELECT t.RID,t.RCONTENT FROM reviews t WHERE t.ISCOVER=0";
        String content = "";
        int rid;
        rs = con.ExcuteWithRS(sql);
        try {
            while (rs.next()) {
                content = rs.getString("RCONTENT");
                rid = rs.getInt("RID");
                //开始转换
                content = converter.convert(content);
                //转换后更新数据库
                sql = String.format("UPDATE reviews SET RCONTENT='%s' WHERE RID=%d", content, rid);
                con.ExcuteUpdate(sql);
                con.Commit();
                System.out.println(rid);
            }
            con.Close();
            System.out.println("OK!");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
/**
 * 去除特殊字符（除中文和中文标点符号以外的字符）
 * @Author Bin
 * @Date 2018/11/26 17:27
 * @Param []
 * @return void
 **/
    public void RemoveSpecChara() {
        //实例化数据库连接对象
        con = new mysqlcon();
        String sql = "SELECT t.RID,t.RCONTENT FROM test t WHERE t.ISCOVER=0";
        //查询所有有效评论集
        rs = con.ExcuteWithRS(sql);
        //构建正则匹配，匹配特殊字符（除中文，英文字母和中文标点符号以外的字符）
        String regZh = "[\\u4e00-\\u9fa5]";//匹配中文的正则正则
        String regSpCha = "[^\\u4e00-\\u9fa5|\\，|\\！|\\。|\\？|\\：|\\；]";//匹配特殊字符的正则
        Matcher matZh;
        Pattern patZh = Pattern.compile(regZh);
        String content = "";
        String newContent;
        String temp="";
        String sql2;
        int rid;
        try {
            while (rs.next()) {
                content = rs.getString("RCONTENT");
                newContent = content.replaceAll(regSpCha, "");
                rid = rs.getInt("RID");
                matZh = patZh.matcher(content);
                //如果评论中不包含任何中文字符则将该条评论标记为无效评论
                if (!matZh.find()) {
                    sql2 = "UPDATE test t SET t.ISCOVER=1 WHERE t.RID=" + rid;
                    con.ExcuteUpdate(sql2);
                    con.Commit();
                } else {
                    if (newContent != content) {
                        sql2 = String.format("UPDATE test SET RCONTENT='%s' WHERE RID=%d", newContent, rid);
                        con.ExcuteUpdate(sql2);
                        con.Commit();
                        temp="1";
                    }
                    else{
                        temp="0";
                    }
                }
                System.out.println("当前评论RID："+rid+"  -----"+temp);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
