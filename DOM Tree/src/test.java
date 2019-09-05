/**
 * Created by sid on 3/27/16.
 */
public class test {
    public static void main(String[] args) {
        String text = "The cOw!       jumped over cow";
        String word = "CoW";

        String [] bits= text.split("\\s+");

        for (String s: bits){
            System.out.println(s + ";");
        }
        StringBuffer sb = new StringBuffer();
        sb = sb.append("the");
        sb = sb.append("moon");

        System.out.print(sb.toString());
    }

}
