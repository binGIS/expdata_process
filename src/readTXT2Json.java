/**
 * @FileName readTXT2Json
 * @Author Bin
 * @Date 2018/12/19 11:40
 * @Version 1.0
 **/

import com.sun.istack.internal.NotNull;

import java.io.*;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * ?????txt??json
 *
 * @Author Bin
 * @Date 2018/12/19 11:40
 * @Version 1.0
 */
public class readTXT2Json {
    public static void main(String[] args) {
        // ??ArrayList????????????
        ArrayList<String> temp = new ArrayList<>();
        ArrayList<String[]> arrayList = new ArrayList<>();//???????????????????
        String[] arr;
        try {

            FileReader fr = new FileReader("C:\\Users\\Bin\\Desktop\\zstp.txt");
            BufferedReader bf = new BufferedReader(fr);
            String str;
            // ???????
            while ((str = bf.readLine()) != null) {
                temp.add(str);
            }
            bf.close();
            fr.close();
            int count = 0;
            while (count < 9910) {
                count += 5;
                arr = new String[3];
                arr[0] = GetStr(temp.get(count - 4));
                arr[1] = GetStr(temp.get(count - 3));
                arr[2] = GetStr(temp.get(count - 2));
                arrayList.add(arr);
            }
            //List<Object> list=new ArrayList<>();
            //??????????
            ArrayList<ArrayList<Object>> tree = new ArrayList<>();
            String[] label;
            ArrayList<Object> temps;
            ArrayList<String[]> keyvalues;
            for (int i = 0; i < arrayList.size(); i++) {
                label = arrayList.get(i);
                temps = new ArrayList<>();
                keyvalues = new ArrayList<>();
                //tree??????????????????Label,SubLabel?Vaule?????
                if (tree.size() == 0) {
                    temps.add("雁荡山");
                    String[] keyvalue = {label[1], label[2]};
                    keyvalues.add(keyvalue);
                    temps.add(keyvalues);
                    tree.add(temps);
                } else {
                    boolean flag = false;//?????????????
                    int index = -1;//???????????
                    //??tree????????Label
                    for (int j = 0; j < tree.size(); j++) {
                        String ss = tree.get(j).get(0).toString();
                        if (label[0].equals(ss)) {
                            flag = true;
                            index = j;
                            break;
                        }
                    }
                    if (flag) {
                        String[] keyvaluenext = {label[1], label[2]};
                        //???????SubLabel?Value???tree.get(index)?
                        ((ArrayList<String[]>) (tree.get(index).get(1))).add(keyvaluenext);
                    } else {
                        temps.add(label[0]);
                        String[] keyvalue = {label[1], label[2]};
                        keyvalues.add(keyvalue);
                        temps.add(keyvalues);
                        tree.add(temps);
                    }
                }
            }
            File file = new File("D:\\result.txt");
            //文件不存在时候，主动创建文件。
            if (!file.exists()) {
                file.createNewFile();
            }
            FileWriter fileWriter = new FileWriter(file, true);
            BufferedWriter bw = new BufferedWriter(fileWriter);
            bw.write("{\"name\":\"旅游景区\",\"children\":[");
            //对规则化的tree进行遍历，构建json格式，并写入txt中
            for (int i = 0; i < tree.size(); i++) {
                if (i == 0) {
                    bw.write(String.format("{\"name\":\"%s\",\"children\":[", tree.get(i).get(0).toString()));
                } else {
                    bw.write(String.format(",{\"name\":\"%s\",\"children\":[", tree.get(i).get(0).toString()));
                }
                keyvalues = (ArrayList<String[]>) (tree.get(i).get(1));
                for (int j = 0; j < keyvalues.size(); j++) {
                    if (j == 0) {
                        bw.write("{\"name\":\"" + keyvalues.get(j)[0] + "\",\"children\":[{\"name\":\"" + keyvalues.get(j)[1] + "\"}]}");
                    } else {
                        bw.write(",{\"name\":\"" + keyvalues.get(j)[0] + "\",\"children\":[{\"name\":\"" + keyvalues.get(j)[1] + "\"}]}");
                    }
                }
                bw.write("]}");
            }
            bw.write("]}");
            bw.close();
            fileWriter.close();
            System.out.println("OK");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * ????????????????????????
     *
     * @param str ???????
     * @return String
     */
    public static String GetStr(@NotNull String str) {
        //???????????
        String pattStr = "(?<=\").*?(?=\")";
        //??Pattern?????
        Pattern pattern = Pattern.compile(pattStr);
        Matcher matcher = pattern.matcher(str);
        if (matcher.find()) {
            return matcher.group();
        } else {
            return "";
        }
    }
}
