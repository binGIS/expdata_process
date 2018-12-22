/**
 * @FileName mysqlcon
 * @Description mysql数据库连接操作类
 * @Author Bin
 * @Date 2018/11/20 21:57
 * @Version 1.0
 **/

import java.sql.*;

/**
 * @Description mysql5.7.17数据库操作类
 * @Author Bin
 * @Date 2018/11/20 21:57
 * @Version 1.0
 */
public class mysqlcon {

    private static final String conStr = "jdbc:mysql://localhost/exp?"
            + "useSSL=false&user=root&password=root"
            + "&useUnicode=true&characterEncoding=UTF8";
    private Connection conn = null;
    private Statement stmt = null;
    private ResultSet rs = null;

    /**
     * @return
     * @Author Bin
     * @Description 构造函数，实例化时创建数据库连接
     * @Date 2018/11/22 14:47
     * @Param []
     **/
    public mysqlcon() {
        try {
            Class.forName("com.mysql.jdbc.Driver");
            conn = DriverManager.getConnection(conStr);
            conn.setAutoCommit(false);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    /**
     * @return java.sql.Connection
     * @Author Bin
     * @Description 获取数据库连接对象
     * @Date 2018/11/21 8:07
     * @Param []
     **/
//    public Connection GetCon() {
//        try {
//            Class.forName("com.mysql.jdbc.Driver");
//            conn = DriverManager.getConnection(conStr);
//            conn.setAutoCommit(false);
//
//        } catch (ClassNotFoundException e) {
//            e.printStackTrace();
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//
//        return conn;
//    }

    public ResultSet ExcuteWithRS(String sql) {
        ResultSet rs = null;
        try {
            Statement statement = conn.createStatement();
            rs = statement.executeQuery(sql);

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return rs;
    }

    public Boolean ExcuteUpdate(String sql) {
        Boolean issuccess = true;
        try {
            Statement statement = conn.createStatement();
            statement.executeUpdate(sql);

        } catch (SQLException e) {
            issuccess = false;
            e.printStackTrace();
        }
        return issuccess;
    }

    /**
     * @return void
     * @Author Bin
     * @Description 关闭数据库连接
     * @Date 2018/11/21 18:08
     * @Param []
     **/
    public void Close() {
        try {
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * @return void
     * @Author Bin
     * @Description 提交对数据库的操作
     * @Date 2018/11/22 9:36
     * @Param []
     **/
    public void Commit() {
        try {
            conn.commit();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}