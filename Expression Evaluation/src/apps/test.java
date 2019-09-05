package apps;

import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by sid on 3/2/17.
 */
public class test {
    public static void main(String[] args){

     /*   String line = "(varx + vary*varz[(vara+varb[(a+b)*33])])/55";
        String line2 = "(varx + vary*varz10[(vara+varb[(a+b)*33])])/55";

        line = "a-(b+A[B[2]])*d+3";
        line = "Sidarth";

        System.out.println(line.substring(0,line.length()));

        String expression = "a[b*46] +4(36)";

        if(!(expression.contains("[")||expression.contains(")"))){
            System.out.println("no [], no ()");
        }

        else if(expression.contains("(") && !(expression.contains("["))){
            System.out.println("no [], yes ()");
        }
        else if((!expression.contains("(")) && expression.contains("[")){
            System.out.println("yes [], no ()");
        }
        else{
            System.out.println("yes [], yes ()");
        }
        float lhs = (2/5);
        System.out.println(lhs);
        System.exit(0);
        */
        final String delims = " \t*+-/()\\[]";
        String expression = "6+a*(b/d) - A[]";
        StringTokenizer st = new StringTokenizer(expression, delims);
        while(st.hasMoreTokens()){
            System.out.println(st.nextToken());
        }




        //line2 = line.replaceAll("[/\\(\\)\\t\\[\\]*+-]"," ");

       // System.out.println("Line 1 = " + line);
       // System.out.println("Line 2 = " + line2);


        /*
       // String patternString = "([a-zA-Z]+\\[)";
        String patternString = "([a-zA-Z][b-zA-Z]+\\[)";
        Pattern pattern = Pattern.compile(patternString);
        Matcher matcher = pattern.matcher(line2);


        //everytime there is an array in the string, create ArraySymbol then store in arrays
        while(matcher.find()){
            String arraytemp = matcher.group(1);
            String x = arraytemp.replaceAll("\\[", "");
            System.out.println(x);
        }

        String myline = "I am  doing work     right now";
        String[] pieces = myline.split("\\s+");
        for(String s: pieces){
            System.out.println("***" + s + "***");
        }




        final String delims = " \t*+-/()\\[]";

        String text = "Sid is in Rutgers. By the way Sid is 20 years old. Sid also plays guitar. Good luck Sid.";
        String pattern1 = "(Sid)";

      //  Pattern pattern = Pattern.compile(pattern1);
        //Matcher matcher = pattern.matcher(text);

        while(matcher.find()){
            System.out.println(matcher.group(1));
        }










        /*
        //STRINGTOKENIZER
        StringTokenizer st = new StringTokenizer(x, delims);

        while(st.hasMoreTokens()){
            System.out.println(st.nextToken());
        }

        //PATTERN
        Pattern p = Pattern.compile("bxy");
        Matcher m = p.matcher(x);
        if(m.matches()){
            System.out.print("i found the pattern");
        }
        else{
            System.out.print("pattern not found");
        }
        */

        //String[] tokens = x.split("\\*|\\+");

       // for (String s : tokens){
        //    System.out.println(s);
      //  }

    }
}
