/**
 *
 * @FileName Levenshtein
 * @Author Bin
 * @Date 2018/11/21 9:05
 * @Version 1.0
 **/

/**
 * 文本编辑距离（相似度）计算
 * @Author Bin
 * @Date 2018/11/21 9:05
 * @Version 1.0
 */
public class Levenshtein {
    private int[][] array;
    private String str1;//文本字符串1
    private String str2;//文本字符串2
    private int max1=0;//文本字符串1的长度
    private int max2=0;//文本字符串2的长度
    public Levenshtein(){

    }
    public Levenshtein(String str1, String str2) {
        this.str1 = str1;
        this.str2 = str2;
        this.max1=str1.length();
        this.max2=str2.length();
    }
/**
 * 计算两个字符串的编辑距离
 * @Author Bin
 * @Date 2018/11/21 9:18
 * @Param []
 * @return int 最小编辑次数
 **/
    public int edit() {
        //建立数组，比字符长度大一个空间
        array = new int[max2 + 1][max1 + 1];
        for (int i = 0; i <= max1; i++) {
            array[0][i] = i;
        }
        for (int j = 0; j <= max2; j++) {
            array[j][0] = j;
        }
        for (int i = 1; i <= max1; i++) {
            for (int j = 1; j <= max2; j++) {
                array[j][i] = levenshtein(i, j, str1.charAt(i - 1), str2.charAt(j - 1));
                //System.out.println("j==" + j + " i==" + i + "   " + array[j][i]);
            }
        }
        //System.out.println("*********" + array[max2][max1]);
        return array[max2][max1];

    }

    public int levenshtein(int i, int j, char si, char sj) {
        int result = 0;

        if (i >= 1 && j >= 1) {
            int a = array[j - 1][i] + 1;
            int b = array[j][i - 1] + 1;
            int c = array[j - 1][i - 1] + ((si != sj) ? 1 : 0);
            result = min(a, b, c);
        }

        return result;
    }

    public int min(int a, int b, int c) {
        int temp = a < b ? a : b;
        return temp < c ? temp : c;
    }
    /**
     * 计算两个字符串的相似度
     * @Author Bin
     * @Date 2018/11/21 9:19
     * @return float相似度
     **/
    //计算相似度
    public float similarity() {
        this.edit();
        float similarity = 1 - (float) array[max2][max1] / Math.max(str1.length(), str2.length());
        return similarity;
    }
    public float similarity(String _str1,String _str2) {
        this.str1 = _str1;
        this.str2 = _str2;
        this.max1=str1.length();
        this.max2=str2.length();
        this.edit();
        float similarity = 1 - (float) array[max2][max1] / Math.max(str1.length(), str2.length());
        return similarity;
    }
}
