package app;

import java.io.*;
import java.util.*;
import java.util.regex.*;

import structures.Stack;

public class Expression {

	public static String delims = " \t*+-/()[]";

	/**
	 * Populates the vars list with simple variables, and arrays lists with arrays
	 * in the expression. For every variable (simple or array), a SINGLE instance is
	 * created and stored, even if it appears more than once in the expression. At
	 * this time, values for all variables and all array items are set to zero -
	 * they will be loaded from a file in the loadVariableValues method.
	 * 
	 * @param expr
	 *            The expression
	 * @param vars
	 *            The variables array list - already created by the caller
	 * @param arrays
	 *            The arrays array list - already created by the caller
	 */
	public static void makeVariableLists(String expr, ArrayList<Variable> vars, ArrayList<Array> arrays) {

		StringTokenizer tokenizer = new StringTokenizer(expr, delims, true); // breaks up tokens
		while (tokenizer.hasMoreTokens()) {
			String token = tokenizer.nextToken();
			
			if (!Character.isDigit(token.charAt(0))) { // checks if token is not a digit

				if (Character.isLetter(token.charAt(0))) { // checks if it is a letter, rather than a symbol
					String Brack = "[";
					String tempLetter = token;

					if (tokenizer.countTokens() > 1) { // if the next token is not null
						String tempBracket = tokenizer.nextToken(); // declare tempBracket as the next token

						token = tempBracket; // declare token as tempBracket so it can move to next token afterward
						if (tempBracket.equals(Brack)) {
							Array temp = new Array(tempLetter);
							if (arrays.size() < 1) {
								arrays.add(temp);
							} else if (!arrays.contains(temp)) {
								arrays.add(temp);
							}

						} else {

							Variable temp = new Variable(tempLetter);
							if (vars.size() < 1) {
								vars.add(temp);
							}else if(!vars.contains(temp)) {
								vars.add(temp);
							}
						}
					} else {

						Variable temp = new Variable(tempLetter);
						if (vars.size() < 1) {
							vars.add(temp);
						} else if (!vars.contains(temp)) {
							vars.add(temp);
						}

						
					}
				}
			}
		}
	}

	/**
	 * Loads values for variables and arrays in the expression
	 * 
	 * @param sc
	 *            Scanner for values input
	 * @throws IOException
	 *             If there is a problem with the input
	 * @param vars
	 *            The variables array list, previously populated by
	 *            makeVariableLists
	 * @param arrays
	 *            The arrays array list - previously populated by makeVariableLists
	 */
	public static void loadVariableValues(Scanner sc, ArrayList<Variable> vars, ArrayList<Array> arrays)
			throws IOException {
		while (sc.hasNextLine()) {
			StringTokenizer st = new StringTokenizer(sc.nextLine().trim());
			int numTokens = st.countTokens();
			String tok = st.nextToken();
			Variable var = new Variable(tok);
			Array arr = new Array(tok);
			int vari = vars.indexOf(var);
			int arri = arrays.indexOf(arr);
			if (vari == -1 && arri == -1) {
				continue;
			}
			int num = Integer.parseInt(st.nextToken());
			if (numTokens == 2) { // scalar symbol
				vars.get(vari).value = num;
			} else { // array symbol
				arr = arrays.get(arri);
				arr.values = new int[num];
				// following are (index,val) pairs
				while (st.hasMoreTokens()) {
					tok = st.nextToken();
					StringTokenizer stt = new StringTokenizer(tok, " (,)");
					int index = Integer.parseInt(stt.nextToken());
					int val = Integer.parseInt(stt.nextToken());
					arr.values[index] = val;
				}
			}
		}
	}

	/**
	 * Evaluates the expression.
	 * 
	 * @param vars
	 *            The variables array list, with values for all variables in the
	 *            expression
	 * @param arrays
	 *            The arrays array list, with values for all array items
	 * @return Result of evaluation
	 */
	public static float evaluate(String expr, ArrayList<Variable> vars, ArrayList<Array> arrays) {
		float num = 0;
		Stack <Float>numbers = new Stack();
		Stack <String> operations = new Stack();
		StringTokenizer tokenizer = new StringTokenizer(expr, delims, true);
		while(tokenizer.hasMoreTokens()) {	//adding tokens into proper stack
			String token = tokenizer.nextToken();
			if(Character.isDigit(token.charAt(0))){	//if number add to number stack
				num = Float.parseFloat(token);
				numbers.push(num);
			} else if(Character.isLetter(token.charAt(0))) {	//if letter add to vars stack
					Variable temp = new Variable (token);		//holds the token as a temp
					if(vars.contains(temp)) {					//checks if the temp is in the variables
						Variable tempNumber = vars.get(vars.indexOf(temp));	
						
						num = tempNumber.value;		
						numbers.push(num);		//puts variable into the stack
					}
				
			}else if(token.equals("+")|| token.equals("-")) {
				operations.push(token);
			}else if(token.equals("*")){	//if multiplication
				operations.push(token);		//pushes token into operations stack
				String tempNum = tokenizer.nextToken();
				if(Character.isDigit(tempNum.charAt(0))) {	//checks if the next token is a digit
					num = Float.parseFloat(tempNum);
					numbers.push(num);
					float multNum1 = numbers.pop();
					float multNum2 = numbers.pop();
					float multAns = multNum1*multNum2;
					numbers.push(multAns);
					operations.pop();
					token = tempNum;
				}else if(Character.isLetter(tempNum.charAt(0))){	//checks if the next token is a letter
					Variable tempNumber = new Variable (tempNum);	
					Variable Number = vars.get(vars.indexOf(tempNumber));
					if(vars.contains(Number)) {
						num = Number.value;
						numbers.push(num);	
					}
					
					float multNum1 = numbers.pop();
					float multNum2 = numbers.pop();
					float multAns = multNum1*multNum2;
					numbers.push(multAns);
					operations.pop();
					token = tempNum;
				}else if(tempNum.equals("(")) {	//checks if token is parenthesis after multiplication
					for(int i =0; i<expr.length(); i++) {
						if(expr.charAt(i)== '(') { //loops through original string to find '('
							String tempExpr = expr.substring(i+1);
							expr = tempExpr;
							break;
						}
					}
					String tempStr ="";
					for(int j = 0; j<expr.length();j++) {
						
						if(expr.charAt(j)== (')')) {
							tempStr = expr.substring(0,j);
							
						}
						
					}
					while(!token.equals(")")){
						String tempToken = tokenizer.nextToken();
						token = tempToken;
					}
					float tempNum1 = evaluate(tempStr, vars, arrays);
					numbers.push(tempNum1);
					/*while(!token.equals(")")){
						String tempToken = tokenizer.nextToken();
						token = tempToken;
					}*/
					if(operations.peek().equals("*")) {
						float multNum1 = numbers.pop();
						float multNum2 = numbers.pop();
						float multAns = multNum1*multNum2;
						numbers.push(multAns);
						operations.pop();
					}
					
				}
			
			}else if(token.equals("/")){	//if division
				operations.push(token);		//pushes token into operations stack
				String tempNum = tokenizer.nextToken();
				if(Character.isDigit(tempNum.charAt(0))) {	//checks if the next token is a digit
					num = Float.parseFloat(tempNum);
					numbers.push(num);
					float divNum1 = numbers.pop();
					float divNum2 = numbers.pop();
					float divAns = divNum2/divNum1;
					numbers.push(divAns);
					operations.pop();
					token = tempNum;
					
				}else if(Character.isLetter(tempNum.charAt(0))){	//checks if the next token is a letter
					Variable tempNumber = new Variable (tempNum);	
					Variable Number = vars.get(vars.indexOf(tempNumber));
					if(vars.contains(Number)) {
						num = Number.value;
						numbers.push(num);	
					}
					
					float divNum1 = numbers.pop();
					float divNum2 = numbers.pop();
					float divAns = divNum2/divNum1;
					numbers.push(divAns);
					operations.pop();
					token = tempNum;
					
				}else if(tempNum.equals("(")) {	//checks if token is parenthesis after multiplication
					for(int i =0; i<expr.length(); i++) {
						if(expr.charAt(i)== '(') { //loops through original string to find '('
							String tempExpr = expr.substring(i+1);
							expr = tempExpr;
							break;
						}
					}
					String tempStr ="";
					for(int j = 0; j<expr.length();j++) {
						
						if(expr.charAt(j)== (')')) {
							tempStr = expr.substring(0,j);
							
						}
						
					}
					while(!token.equals(")")){
						String tempToken = tokenizer.nextToken();
						token = tempToken;
					}
					float tempNum1 = evaluate(tempStr, vars, arrays);
					numbers.push(tempNum1);
					/*while(!token.equals(")")){
						String tempToken = tokenizer.nextToken();
						token = tempToken;
					}*/
					if(operations.peek().equals("/")) {
						float divNum1 = numbers.pop();
						float divNum2 = numbers.pop();
						float divAns = divNum2/divNum1;
						numbers.push(divAns);
						operations.pop();
					}
					
				}
				
			}else if(token.equals("(")) {	//checks if token is parenthesis
				for(int i =0; i<expr.length(); i++) {
					if(expr.charAt(i)== '(') { //loops through original string to find '('
						String tempExpr = expr.substring(i+1);
						expr = tempExpr;
						break;
					}
				}
				String tempStr ="";
				for(int j = 0; j<expr.length();j++) {
					
					if(expr.charAt(j)== (')')) {
						tempStr = expr.substring(0,j);
						
					}
					
				}
				while(!token.equals(")")){
					String tempToken = tokenizer.nextToken();
					token = tempToken;
				}
				float tempNum = evaluate(tempStr, vars, arrays);
				numbers.push(tempNum);
				/*while(!token.equals(")")){
					String tempToken = tokenizer.nextToken();
					token = tempToken;
				}*/
				//token = tokenizer.nextToken();
			}
			
		}
		 Stack <Float> numCorrect = new Stack();
		 Stack<String> opsCorrect = new Stack();
		 while(!numbers.isEmpty()) {
			 numCorrect.push(numbers.pop());
		 }
		 while(!operations.isEmpty()) {
			 opsCorrect.push(operations.pop());
		 }
		 float newSum = 0;
		 while(!opsCorrect.isEmpty()) {
			 if(opsCorrect.peek().equals("+")) {
				 float num1 = numCorrect.pop();
				 float num2 = numCorrect.pop();
				 float sum = num1+num2;
				 numCorrect.push(sum);
				 opsCorrect.pop();
			 }else {
				 float num1 = numCorrect.pop();
				 float num2 = numCorrect.pop();
				 float sum = num1-num2;
				 numCorrect.push(sum);
				 opsCorrect.pop();
			 }
			 
		 }
		 float answer = numCorrect.pop();
		
		// following line just a placeholder for compilation
		return answer;
	}
}

