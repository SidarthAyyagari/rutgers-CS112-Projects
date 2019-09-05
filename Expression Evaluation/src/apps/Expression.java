package apps;

import java.io.*;
import java.util.*;
import java.util.regex.*;

import jdk.nashorn.internal.runtime.regexp.joni.encoding.CharacterType;
import jdk.nashorn.internal.runtime.regexp.joni.encoding.IntHolder;
import structures.Stack;

public class Expression {

	/**
	 * Expression to be evaluated
	 */
	String expr;                
    
	/**
	 * Scalar symbols in the expression 
	 */
	ArrayList<ScalarSymbol> scalars;   
	
	/**
	 * Array symbols in the expression
	 */
	ArrayList<ArraySymbol> arrays;
    
    /**
     * String containing all delimiters (characters other than variables and constants), 
     * to be used with StringTokenizer
     */
    public static final String delims = " \t*+-/()\\[]";
    
    /**
     * Initializes this Expression object with an input expression. Sets all other
     * fields to null.
     * 
     * @param expr Expression
     */
    public Expression(String expr) {
        this.expr = expr;
    }

    /**
     * Populates the scalars and arrays lists with symbols for scalar and array
     * variables in the expression. For every variable, a SINGLE symbol is created and stored,
     * even if it appears more than once in the expression.
     * At this time, values for all variables are set to
     * zero - they will be loaded from a file in the loadSymbolValues method.
     */
    public void buildSymbols() {
    		/** COMPLETE THIS METHOD **/
        arrays = new ArrayList<ArraySymbol>();
        scalars = new ArrayList<ScalarSymbol>();

        String line = expr;
        line = line.replaceAll("\\s+", "");

        String patternString = "([a-zA-Z]+\\[)";
        Pattern pattern = Pattern.compile(patternString);
        Matcher matcher = pattern.matcher(line);

        //everytime there is an array in the string, create ArraySymbol then store in arrays
        while(matcher.find()){
            String arraytemp = matcher.group(1);
            arraytemp = arraytemp.substring(0,arraytemp.length()-1);
            ArraySymbol y = new ArraySymbol(arraytemp);
            arrays.add(y);
        }

        //" \t*+-/()\\[]";
        String line2 = line.replaceAll("[/\\(\\)\\t\\[\\]*+-]"," ");
        String[] possScalars = line2.split("\\s+");

        for (String s: possScalars){
            ArraySymbol t1 = new ArraySymbol(s);
            if (!arrays.contains(t1)){
                ScalarSymbol t2 = new ScalarSymbol(s);
                scalars.add(t2);
            }
        }
    }


    
    /**
     * Loads values for symbols in the expression
     * 
     * @param sc Scanner for values input
     * @throws IOException If there is a problem with the input 
     */
    public void loadSymbolValues(Scanner sc) 
    throws IOException {
        while (sc.hasNextLine()) {
            StringTokenizer st = new StringTokenizer(sc.nextLine().trim());
            int numTokens = st.countTokens();
            String sym = st.nextToken();
            ScalarSymbol ssymbol = new ScalarSymbol(sym);
            ArraySymbol asymbol = new ArraySymbol(sym);
            int ssi = scalars.indexOf(ssymbol);
            int asi = arrays.indexOf(asymbol);
            if (ssi == -1 && asi == -1) {
            	continue;
            }
            int num = Integer.parseInt(st.nextToken());
            if (numTokens == 2) { // scalar symbol
                scalars.get(ssi).value = num;
            } else { // array symbo
            	asymbol = arrays.get(asi);
            	asymbol.values = new int[num];
                // following are (index,val) pairs
                while (st.hasMoreTokens()) {
                    String tok = st.nextToken();
                    StringTokenizer stt = new StringTokenizer(tok," (,)");
                    int index = Integer.parseInt(stt.nextToken());
                    int val = Integer.parseInt(stt.nextToken());
                    asymbol.values[index] = val;              
                }
            }
        }
    }
    
    
    /**
     * Evaluates the expression, using RECURSION to evaluate subexpressions and to evaluate array 
     * subscript expressions.
     * 
     * @return Result of evaluation
     */
    public float evaluate() {
    		/** COMPLETE THIS METHOD **/
    		// following line just a placeholder for compilation

        return (float) evaluator(expr);
    }

    private int evaluator(String expression) {
        expression = expression.replaceAll("\\s+", "");

        Stack<String> operands = new Stack<String>();
        Stack<Character> operators = new Stack<Character>();

        if (!(expression.contains("[") || expression.contains(")"))) {
            //LEAF EXPRESSION

            //if its just a number no operators
            if (isNumber(expression)) {
                return Integer.parseInt(expression);
            }
            //if its just a scalar no operators
            if (isScalar(expression)) {
                return getScalar(expression);
            }

            //if it has operators
            boolean WithOpps = false;
            for (char c : expression.toCharArray()) {
                if (isOperator(c)) {
                    WithOpps = true;
                    break;
                }
            }
            //Leaf expression with opps
            String lhs = "";
            String rhs = "";
            char operator = 0;
            char next_oper = 0;


            for (char c : expression.toCharArray()) {
                //LHS
                if (operator == 0 && !isOperator(c)) {
                    lhs = lhs + c;
                    continue;
                }

                //get operators
                if (isOperator(c)) {
                    //if its the first operator
                    if (operator == 0) {
                        operator = c;
                        continue;
                    }
                    //if its the next operator
                    else {
                        next_oper = c;
                        if (isAddOperator(operator) || isSubtractOperator(operator)) {
                            Integer x = 0;
                            if(isScalar(lhs)){
                                x = getScalar(lhs);
                                operands.push(Integer.toString(x));
                            }
                            else{
                                operands.push(lhs);
                            }
                            operators.push(operator);
                            lhs = rhs;
                            operator = next_oper;
                            rhs = "";
                            next_oper = 0;
                            continue;
                        }
                        else if (isDivideOperator(operator) || isMultiplyOperator(operator)){
                            //evaluate3
                            int x = 0;
                            int y = 0;
                            if(isScalar(lhs)){
                                x = getScalar(lhs);
                            }
                            else{
                                x = Integer.parseInt(lhs);
                            }
                            if(isScalar(rhs)){
                                y = getScalar(rhs);
                            }
                            else{
                                y = Integer.parseInt(rhs);

                            }

                            //actual evaluation
                            if(operator == '*'){
                                lhs = Integer.toString(x*y);
                            }
                            else if (operator == '/'){
                                lhs = Integer.toString(x/y);
                            }

                            rhs = "";
                            operator = next_oper;
                            next_oper = 0;
                            continue;
                        }
                    }
                }

                //RHS
                if (operator != 0 && next_oper == 0) {
                    rhs = rhs + c;
                    continue;
                }
            }
            //Last piece
            if(isAddOperator(operator) || isSubtractOperator(operator)){
                int x = 0;
                int y = 0;
                if(isScalar(lhs)){
                    x = getScalar(lhs);
                }
                else{
                    x = Integer.parseInt(lhs);
                }
                if(isScalar(rhs)){
                    y = getScalar(rhs);
                }
                else{
                    y = Integer.parseInt(rhs);
                }
                operands.push(Integer.toString(x));
                operands.push(Integer.toString(y));
                operators.push(operator);
            }
            else{
                int x = 0;
                int y = 0;
                if(isScalar(lhs)){
                    x = getScalar(lhs);
                }
                else{
                    x = Integer.parseInt(lhs);
                }
                if(isScalar(rhs)){
                    y = getScalar(rhs);
                }
                else{
                    y = Integer.parseInt(rhs);
                }

                //actual evaluation
                if(operator == '*'){
                    lhs = Integer.toString(x*y);
                }
                else if (operator == '/'){
                    lhs = Integer.toString(x/y);
                }
                operands.push(lhs);
            }
        }
        //expand subexpressions
        else if (expression.contains("(")&&!expression.contains("[")) {
            int counter = 0;
            int startindex = 0;
            int endindex = 0;
            String left = "";
            String middle = "";
            String right = "";

            for (int i = 0; i < expression.length(); i++) {
                if (isCharOpenParentheses(expression.charAt(i))) {
                    if (counter == 0) {
                        left = expression.substring(0, i);
                        startindex = i + 1;
                    }
                    counter = counter + 1;
                }
                else if (isCharClosedParentheses(expression.charAt(i))) {
                    counter = counter - 1;
                    if (counter == 0) {
                        endindex = i;
                        right = expression.substring(endindex+1);
                    }
                }
            }
            middle = expression.substring(startindex, endindex);
            String m = Integer.toString(evaluator(middle));
            return evaluator(left + m + right);
        }
        //if it has arrays
        if (expression.contains("[")){
            int count = 0;
            int pre = 0;
            int post = 0;
            String lef = "";
            String middl = "";
            int midstart = 0;
            String righ = "";
            String arrayname = "";
            int arrayindex = 0;

            for (int i = 0; i < expression.length(); i++) {
                if (isCharOpenBracket(expression.charAt(i)) ){
                    //do counter
                    if (count == 0) {
                        midstart = i+1;
                        //get array name
                        int j = i-1;
                        while(j>=0 && Character.isLetter(expression.charAt(j))&&(count ==0)){
                            arrayname = expression.charAt(j) + arrayname;
                            j--;
                        }

                        pre = j +1;
                        lef = expression.substring(0, pre);
                    }
                    count = count + 1;
                    continue;
                }
                else if (isCharClosedBracket(expression.charAt(i))){
                    count = count - 1;
                    if (count == 0) {
                        post = i+1;
                        righ = expression.substring(post);
                        middl = expression.substring(midstart, post-1);
                        break;
                    }
                    continue;
                }
            }

            arrayindex = evaluator(middl);
            String mid = Integer.toString(getArray(arrayname, arrayindex));
            return evaluator(lef + mid + righ);
        }

        return stacksolver(operators,operands);
    }



    /**
     * Utility method, prints the symbols in the scalars list
     */
    public void printScalars() {
        for (ScalarSymbol ss: scalars) {
            System.out.println(ss);
        }
    }
    
    /**
     * Utility method, prints the symbols in the arrays list
     */
    public void printArrays() {
    		for (ArraySymbol as: arrays) {
    			System.out.println(as);
    		}
    }

    //************************
    //private helper functions
    //************************
    private boolean isCharOpenBracket(char x){
        return x == '[';
    }
    private boolean isCharOpenParentheses(char x){
        return x == '(';
    }
    private boolean isCharClosedBracket(char x){
        return x == ']';
    }
    private boolean isCharClosedParentheses(char x){
        return x == ')';
    }
    private boolean isNumber(String s){
        for(char c : s.toCharArray()){
            if (!Character.isDigit(c)){
                return false;
            }
        }
        return true;
    }
    private boolean isScalar(String s){
        for(char c : s.toCharArray()){
            if (!Character.isLetter(c)){
                return false;
            }
        }
        return true;
    }

    private boolean isArray(String s) {
        for (int i = 0; i < s.length(); i++) {
            if(s.charAt(i) == '['){
                return true;
            }
        }
        return false;
    }

    private boolean isOperator(char c){
        return (isAddOperator(c)||isSubtractOperator(c)||isMultiplyOperator(c)||isDivideOperator(c));
    }
    private boolean isAddOperator(char c){
        return c == '+';
    }
    private boolean isSubtractOperator(char c) {
        return c == '-';
    }
    private boolean isMultiplyOperator(char c){
        return c=='*';
    }
    private boolean isDivideOperator(char c){
        return c == '/';
    }

    private int getScalar(String expression){
        for(ScalarSymbol ss : scalars){
            if(ss.name.equals(expression)){
                return ss.value;
            }
        }
        return -1;
    }

    private int getArray(String arname, int index){
        for(ArraySymbol as : arrays){
            if(as.name.equals(arname)){
                return as.values[index];
            }
        }
        return -1;
    }
    private int stacksolver(Stack<Character> operators, Stack<String> operands){
        int numba = 0;
        char opp = 0;
        int result = 0;

        if(operators.isEmpty()&& operands.size() == 1){
            result = Integer.parseInt(operands.pop());
            operands.clear();
            operators.clear();
            return result;
        }

        while(!operators.isEmpty()){
            opp = operators.pop();
            numba = Integer.parseInt(operands.pop());
            if(isAddOperator(opp)){
                result = result + numba;
            }
            else{
                result = result - numba;
            }
        }
        if(!operands.isEmpty())
            result = result + Integer.parseInt(operands.pop());

        operands.clear();
        operators.clear();
        return result;
    }
}
