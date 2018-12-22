/**
 * @FileName test
 * @Author Bin
 * @Date 2018/12/19 9:40
 * @Version 1.0
 **/

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @Author Bin
 * @Date 2018/12/19 9:40
 * @Version 1.0
 */
public class test {
    public static void main(String[] args) {
        //待匹配的字符串："Hello"Hi"Nice"Good
        String content = "\"Hello\"Hi\"Nice\"Good";
        System.out.println(content);
        //匹配双引号的正则表达式
        String pattStr = "(?<=\").*?(?=\")";
        //创建Pattern并进行匹配
        Pattern pattern = Pattern.compile(pattStr);
        Matcher matcher = pattern.matcher(content);
        //将所有匹配的结果打印输出
        while (matcher.find()) {
            System.out.println(matcher.group());
        }

    }
}
