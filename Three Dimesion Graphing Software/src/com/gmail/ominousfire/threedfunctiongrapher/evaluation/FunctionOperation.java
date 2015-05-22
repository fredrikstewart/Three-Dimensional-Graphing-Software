package com.gmail.ominousfire.threedfunctiongrapher.evaluation;

public class FunctionOperation {
	
	public static final int ADD = 1;
	public static final int SUBTRACT = 2;
	public static final int DIVIDE = 3;
	public static final int MULTIPLY = 4;
	public static final int EXPONENT = 5;
	public static final int SIN = 6;
	public static final int SQRT = 7;
	public static final int COS = 8;
	public static final int TAN = 9;
	public static final int TO_RADIANS = 10;
	public static final int ATAN = 11;
	public static final int ACOS = 12;
	public static final int ASIN = 13;
	public static final int LN = 14;
	public static final int NEGATE = 15;
	
	public FunctionOperation(int operation, int i, int j, int k) {
		this.operation = operation;
		this.index1 = i;
		this.index2 = j;
		this.storageIndex = k;
	}
	
	public int operation;
	public int index1;
	public int index2;
	public int storageIndex;

}
