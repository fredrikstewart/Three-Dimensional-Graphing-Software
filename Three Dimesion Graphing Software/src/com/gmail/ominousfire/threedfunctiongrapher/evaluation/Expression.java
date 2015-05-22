package com.gmail.ominousfire.threedfunctiongrapher.evaluation;

import java.util.ArrayList;
import java.util.List;

public class Expression {

	private double[] intStorage;
	private static final String megaRegex = "[)(]*[/*%^][)(]*|[)(]*[+][)(]*|[)(]*[^E/*%)(+-^][-][)(]*";
	//x^-2+y^-(2)-z^2=1
	public Expression(String expression) {
		expression = bufferVariables(expression);
		System.out.println("Buffered Expression: " + expression);
		expression = decimalAllNumbers(expression);
		System.out.println("Decimaled Expression: " + expression);
		String[] sa = expression.split(megaRegex, 0);
		totalIndecies = sa.length;
		for (String s: sa) {
			s = s.replaceAll("asin", "");
			s = s.replaceAll("sin", "");
			s = s.replaceAll("atan", "");
			s = s.replaceAll("tan", "");
			s = s.replaceAll("acos", "");
			s = s.replaceAll("cos", "");
			s = s.replaceAll("sqrt", "");
			s = s.replaceAll("toRadians", "");
			s = s.replaceAll("ln", "");
			variablesInOrder.add(s.replaceAll("[-]*[)(]", ""));
			System.out.println("variable:" + s.replaceAll("[-]*[)(]", ""));
		}
		String orderedContents = expression.replaceAll("[\t \n\r]", "");
		for (int i = 0; i < totalIndecies; i++) {
			orderedContents = orderedContents.replaceFirst(variablesInOrder.get(i), "" + i);
			System.out.println("ordered contents:" + orderedContents);
		}
		System.out.println("ordered contents:" + orderedContents + "\nexpression: " + expression);

		createOrder(orderedContents);

		intStorage = new double[totalIndecies];
	}

	private String bufferVariables(String contents) {
		for (int i = 0; i < contents.length() - 1; i++) {
			if (contents.charAt(i + 1) == '-' && (contents.charAt(i) != 'E' && ("" + contents.charAt(i)).matches("[xyz0123456789)-]"))) {
				contents = contents.substring(0, i + 1) + " " + contents.substring(i + 1, contents.length());
			}
		}
		return contents;
	}

	private String decimalAllNumbers(String expression) {
		String[] sa = expression.split(megaRegex , 0);
		int currentIndex = 0;
		for (String s: sa) {
			System.out.println("decimaling number:" + s);
			if (s.matches("[-0-9)(]+$") && s.matches(".*[0-9]*.*")) {
				expression = expression.substring(0, currentIndex) + expression.substring(currentIndex).replaceFirst(s.replaceAll("[(]", "[(]"), s + ".0");
				currentIndex += s.length() + 3;
			} else {
				currentIndex += s.length() + 1;
			}
		}
		return expression;
	}

	public int createOrder(String contents) {
		contents = contents.replaceAll("[\t \n\r]", "");
		contents = contents.replaceAll("ee", "" + Math.E);
		contents = contents.replaceAll("pi", "" + Math.PI);
		while (contents.matches(".*ln[(].*[)].*")) {
			String fun = "ln(";
			String subContents = contents.substring(contents.indexOf(fun) + fun.length(), getMatchingParenIndex(contents, contents.indexOf(fun) + fun.length()));
			int d = createOrder(subContents);
			contents = contents.replace(fun + subContents + ")", "" + d);
			operations.add(new FunctionOperation(FunctionOperation.LN, d, -1, d));
		}
		while (contents.matches(".*asin[(].*[)].*")) {
			String fun = "asin(";
			String subContents = contents.substring(contents.indexOf(fun) + fun.length(), getMatchingParenIndex(contents, contents.indexOf(fun) + fun.length()));
			int d = createOrder(subContents);
			contents = contents.replace(fun + subContents + ")", "" + d);
			operations.add(new FunctionOperation(FunctionOperation.ASIN, d, -1, d));
		}
		while (contents.matches(".*acos[(].*[)].*")) {
			String fun = "acos(";
			String subContents = contents.substring(contents.indexOf(fun) + fun.length(), getMatchingParenIndex(contents, contents.indexOf(fun) + fun.length()));
			int d = createOrder(subContents);
			contents = contents.replace(fun + subContents + ")", "" + d);
			operations.add(new FunctionOperation(FunctionOperation.ACOS, d, -1, d));
		}
		while (contents.matches(".*atan[(].*[)].*")) {
			String fun = "atan(";
			String subContents = contents.substring(contents.indexOf(fun) + fun.length(), getMatchingParenIndex(contents, contents.indexOf(fun) + fun.length()));
			int d = createOrder(subContents);
			contents = contents.replace(fun + subContents + ")", "" + d);
			operations.add(new FunctionOperation(FunctionOperation.ATAN, d, -1, d));
		}
		while (contents.matches(".*toRadians[(].*[)].*")) {
			String fun = "toRadians(";
			String subContents = contents.substring(contents.indexOf(fun) + fun.length(), getMatchingParenIndex(contents, contents.indexOf(fun) + fun.length()));
			int d = createOrder(subContents);
			contents = contents.replace(fun + subContents + ")", "" + d);
			operations.add(new FunctionOperation(FunctionOperation.TO_RADIANS, d, -1, d));
		}
		while (contents.matches(".*tan[(].*[)].*")) {
			String fun = "tan(";
			String subContents = contents.substring(contents.indexOf(fun) + fun.length(), getMatchingParenIndex(contents, contents.indexOf(fun) + fun.length()));
			int d = createOrder(subContents);
			contents = contents.replace(fun + subContents + ")", "" + d);
			operations.add(new FunctionOperation(FunctionOperation.TAN, d, -1, d));
		}
		while (contents.matches(".*cos[(].*[)].*")) {
			String fun = "cos(";
			String subContents = contents.substring(contents.indexOf(fun) + fun.length(), getMatchingParenIndex(contents, contents.indexOf(fun) + fun.length()));
			int d = createOrder(subContents);
			contents = contents.replace(fun + subContents + ")", "" + d);
			operations.add(new FunctionOperation(FunctionOperation.COS, d, -1, d));
		}
		while (contents.matches(".*sqrt[(].*[)].*")) {
			String subContents = contents.substring(contents.indexOf("sqrt(") + 5, getMatchingParenIndex(contents, contents.indexOf("sqrt(") + 5));
			int d = createOrder(subContents);
			contents = contents.replace("sqrt(" + subContents + ")", "" + d);
			operations.add(new FunctionOperation(FunctionOperation.SQRT, d, -1, d));
		}
		while (contents.matches(".*sin[(].*[)].*")) {
			String subContents = contents.substring(contents.indexOf("sin(") + 4, getMatchingParenIndex(contents, contents.indexOf("sin(") + 4));
			int d = createOrder(subContents);
			contents = contents.replace("sin(" + subContents + ")", "" + d);
			operations.add(new FunctionOperation(FunctionOperation.SIN, d, -1, d));
		}
		while (contents.matches(".*[(].*[)].*")) {
			int  i = contents.indexOf("(");
			String subContents = contents.substring(i + 1, getMatchingParenIndex(contents, i));
			int d = createOrder(subContents);
			if (contents.charAt(i- 1) == '-') {
				operations.add(new FunctionOperation(FunctionOperation.NEGATE, d, -1, d));
				contents = contents.replace("-(" + subContents + ")", "" + d);
			} else {
				contents = contents.replace("(" + subContents + ")", "" + d);
			}
		}
		while (contents.startsWith("-")) {
			String subContents = contents.substring(1, getNumberFromBeginning(contents).length());
			int d = createOrder(subContents);
			operations.add(new FunctionOperation(FunctionOperation.NEGATE, d, -1, d));
			contents = contents.replace("-" + subContents, "" + d);
		}
		//Now that all parens are gone, do the rest of PEMDAS (Exponents, Multiply, Divide, Add, Sub) 
		while (contents.contains("^")) {
			int index = contents.indexOf('^');
			String[] unfilteredHalves = new String[] {contents.substring(0, index), contents.substring(index + 1)};
			String number1 = getNumberFromEnd(unfilteredHalves[0]);
			String number2 = getNumberFromBeginning(unfilteredHalves[1]);
			int parsedNum1 = Integer.parseInt(number1);
			int parsedNum2 = Integer.parseInt(number2);
			contents = contents.replace(number1 + "^" + number2, "" + totalIndecies++);
			operations.add(new FunctionOperation(FunctionOperation.EXPONENT, parsedNum1, parsedNum2, totalIndecies - 1));
		}
		while (contents.contains("*") || contents.contains("/")) {
			String[] unfilteredHalves = contents.split("[*/]", 2);
			String number1 = getNumberFromEnd(unfilteredHalves[0]);
			String number2 = getNumberFromBeginning(unfilteredHalves[1]);
			int parsedNum1 = Integer.parseInt(number1);
			int parsedNum2 = Integer.parseInt(number2);
			if (contents.indexOf("*") != -1 && (contents.indexOf("*") < contents.indexOf("/") || contents.indexOf("/") == -1)) {
				contents = contents.replace("" + number1 + "*" + number2, "" + (totalIndecies++));
				operations.add(new FunctionOperation(FunctionOperation.MULTIPLY, parsedNum1, parsedNum2, totalIndecies - 1));
			} else {
				contents = contents.replace("" + number1 + "/" + number2, "" + (totalIndecies++));
				operations.add(new FunctionOperation(FunctionOperation.DIVIDE, parsedNum1, parsedNum2, totalIndecies - 1));

			}
		}
		while (contents.contains("+") || (contents.lastIndexOf("-") != 0 && (contents.split("-").length - (contents.charAt(0) == '-' ? 1 : 0)) > contents.split("E-").length)) {
			String[] unfilteredHalves = null;
			if (contents.indexOf("+") != -1 && (contents.indexOf("-") != -1)) { //Contains both

				if (contents.matches("[^+E]*[-].*[+].*&&[^-].*")) {
					unfilteredHalves = getFirstSubtractionSignAndBreak(contents);
				} else {
					unfilteredHalves = contents.split("[+]");
				}
			} else {
				if (contents.contains("+")) {
					unfilteredHalves = contents.split("[+]");
				} else {
					unfilteredHalves = getFirstSubtractionSignAndBreak(contents);
				}
			}
			try {
				String number1 = getNumberFromEnd(unfilteredHalves[0]);
				String number2 = getNumberFromBeginning(unfilteredHalves[1]);
				int parsedNum1 = Integer.parseInt(number1);
				int parsedNum2 = Integer.parseInt(number2);
				if (contents.indexOf("+") != -1 && (contents.indexOf("-") != -1)) { //Contains both
					if (contents.matches("[^+E]*[-].*[+].*&&[^-].*")) { // Does SUBTRACTION sign occur first
						contents = contents.replace("" + number1 + "-" + number2, "" + totalIndecies++);
						operations.add(new FunctionOperation(FunctionOperation.SUBTRACT, parsedNum1, parsedNum2, totalIndecies - 1));
					} else {
						contents = contents.replace("" + number1 + "+" + number2, "" + totalIndecies++);
						operations.add(new FunctionOperation(FunctionOperation.ADD, parsedNum1, parsedNum2, totalIndecies - 1));

					}
				} else { // Contains only one or the other.
					if (contents.contains("+")) {
						contents = contents.replace("" + number1 + "+" + number2, "" + totalIndecies++);
						operations.add(new FunctionOperation(FunctionOperation.ADD, parsedNum1, parsedNum2, totalIndecies - 1));
					} else {
						contents = contents.replace("" + number1 + "-" + number2, "" + totalIndecies++);
						operations.add(new FunctionOperation(FunctionOperation.SUBTRACT, parsedNum1, parsedNum2, totalIndecies - 1));
					}
				}
				//System.out.println(contents);
			} catch (Exception e) {
				System.out.println(contents + "|" + unfilteredHalves[0] + "|");
				throw e;
			}
		}
		try {
			return Integer.parseInt(contents);
		} catch (Exception e) {
			System.out.println(contents);
			throw new RuntimeException();
		}

	}

	private String[] getFirstSubtractionSignAndBreak(String contents) {
		for (int i = 0; i < contents.length() - 1; i++) {
			if (contents.charAt(i + 1) == '-' && contents.charAt(i) != 'E') return new String[] {contents.substring(0, i + 1), contents.substring(i + 2)};
		}
		return null;
	}

	private String getNumberFromBeginning(String string) {
		for (int i = 0; i < string.length(); i++) {
			if (!allNumberCharacters.contains("" + string.charAt(i))) {
				return string.substring(0, i);
			}
			if (i > 0 && (string.charAt(i) == '-' && !(string.charAt(i - 1) == 'E'))) {
				return string.substring(0, i);
			}
		}
		return string;
	}

	private static final String allNumberCharacters = "E-0123456789.";

	private String getNumberFromEnd(String string) {
		for (int i = string.length() - 1; i >= 0; i--) {
			if (!allNumberCharacters.contains("" + string.charAt(i))) {
				return string.substring(i + 1);
			}
			if (i > 0 && (string.charAt(i) == '-' && !(string.charAt(i - 1) == 'E'))) {
				return string.substring(i + 1);
			}
		}
		return string;
	}

	private int totalIndecies;
	private List<String> variablesInOrder = new ArrayList<String>();
	private List<FunctionOperation> operations = new ArrayList<FunctionOperation>();

	public double evaluate(double[] values) {
		int i = 0;
		for (String s : variablesInOrder) {
			if (s.contains("x")) intStorage[i] = values[0];
			else if (s.contains("y")) intStorage[i] = values[1];
			else if (s.contains("z")) intStorage[i] = values[2];
			else intStorage[i] = Double.parseDouble(s);
			i++;
		}
		for (FunctionOperation fo : operations) {
			switch (fo.operation) {
			case FunctionOperation.ADD:
				intStorage[fo.storageIndex] = intStorage[fo.index1] + intStorage[fo.index2];
				break;
			case FunctionOperation.SUBTRACT:
				intStorage[fo.storageIndex] = intStorage[fo.index1] - intStorage[fo.index2];
				break;
			case FunctionOperation.MULTIPLY:
				intStorage[fo.storageIndex] = intStorage[fo.index1] * intStorage[fo.index2];
				break;
			case FunctionOperation.DIVIDE:
				intStorage[fo.storageIndex] = intStorage[fo.index1] / intStorage[fo.index2];
				break;
			case FunctionOperation.EXPONENT:
				intStorage[fo.storageIndex] = Math.pow(intStorage[fo.index1],intStorage[fo.index2]);
				break;
			case FunctionOperation.SIN:
				intStorage[fo.storageIndex] = Math.sin(intStorage[fo.index1]);
				break;
			case FunctionOperation.SQRT:
				intStorage[fo.storageIndex] = Math.sqrt(intStorage[fo.index1]);
				break;
			case FunctionOperation.COS:
				intStorage[fo.storageIndex] = Math.cos(intStorage[fo.index1]);
				break;
			case FunctionOperation.ACOS:
				intStorage[fo.storageIndex] = Math.acos(intStorage[fo.index1]);
				break;
			case FunctionOperation.ASIN:
				intStorage[fo.storageIndex] = Math.asin(intStorage[fo.index1]);
				break;
			case FunctionOperation.TAN:
				intStorage[fo.storageIndex] = Math.tan(intStorage[fo.index1]);
				break;
			case FunctionOperation.ATAN:
				intStorage[fo.storageIndex] = Math.atan(intStorage[fo.index1]);
				break;
			case FunctionOperation.LN:
				intStorage[fo.storageIndex] = Math.log(intStorage[fo.index1]);
				break;
			case FunctionOperation.TO_RADIANS:
				intStorage[fo.storageIndex] = Math.toRadians(intStorage[fo.index1]);
				break;
			case FunctionOperation.NEGATE:
				intStorage[fo.storageIndex] = -(intStorage[fo.index1]);
				break;
			}
		}
		//System.out.println(intStorage[intStorage.length - 1]);
		return intStorage[intStorage.length - 1];

	}

	private int getMatchingParenIndex(String contents, int indexOf) {
		int i = 1;
		for (int i1 = indexOf + 1; i1 < contents.length(); i1++) {
			if (contents.charAt(i1) == '(') i++;
			if (contents.charAt(i1) == ')') i--;
			if (i <= 0) return i1;
		}
		return -1;
	}

}
