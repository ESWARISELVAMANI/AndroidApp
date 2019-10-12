package news.TamilNewsPaper;

/**
 * Created by chittanbm on 10/14/2016.
 */
public class DataHolder {
    private static int cnt = 0;
    public static int getData() {return cnt;}
    public static void setData() {DataHolder.cnt = DataHolder.cnt  + 1;}
    public static void setCNTAs4() {DataHolder.cnt = 5;}
    public static void initZeroCNT() {DataHolder.cnt = 0;}

    public static void reduceBy1() {DataHolder.cnt = DataHolder.cnt  - 1;}
}