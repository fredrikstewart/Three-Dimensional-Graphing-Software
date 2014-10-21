package com.gmail.ominousfire.threedfunctiongrapher.evaluation;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import org.lwjgl.opengl.GL11;

public class Function implements Runnable {

	private String function;

	private List<Line3D> lines = new ArrayList<Line3D>();

	private double a = 7;

	private double stepSize;

	private double xMax;

	private double xMin;

	private double yMin;

	private double yMax;

	private double zMax;

	private double zMin;

	private boolean isReady;

	private Expression e2;

	private Expression e1;

	private int threadNumber;

	private static final String[] division = {"/"};
	private static final String[] addition = {"+"};
	private static final String[] subtraction = {"-"};
	private static final String[] integerDivision = {"//"};
	private static final String[] multiplication = {"*"};
	private static final String[] modulus = {"%"};
	private static final String[] exponent = {"^"};
	private static final String[] parens = {"("};
	private static final String[] sin = {"sin"};
	private static final String[] arcsin = {"arcsin", "sin^-1"};
	private static final String[] cos = {"cos"};
	private static final String[] arccos = {"arccos", "cos^-1"};
	private static final String[] tan = {"tan"};
	private static final String[] arctan = {"arctan"};
	private static final String[] e = {"e"};
	private static final String[] pi = {"pi"};


	public Function(String contents, double xMin, double xMax, double yMin, 
			double yMax, double zMin, double zMax, double stepSize, String[] variables, int threadNumber) {
		this.threadNumber = threadNumber;
		function = contents.replaceAll("[\t \n\r]", "");
		this.xMax = xMax;
		this.xMin = xMin;
		this.yMin = yMin;
		this.yMax = yMax;
		this.zMax = zMax;
		this.zMin = zMin;
		this.stepSize = stepSize;
		String s1 = function.split("=")[0];
		System.out.println(s1);
		System.out.println(function.split("=")[1]);
		e1 = new Expression(s1);
		e2 = new Expression(function.split("=")[1]);
		
	}

	private boolean isValid() {
		return false;
	}

	public void createRenderData() {
		try {
			long l = System.nanoTime();
			Point3D dummyPoint = new Point3D(1e4,1e4,1e4);

			double b = 1;
			for (double x = xMin; x < xMax; x+=stepSize) {

				List<Point3D> points = new ArrayList<Point3D>();

				String traceFunction = function.replace("x", "" + x);
				String[] traceExpressions = traceFunction.split("=");
				for (double y = yMin; y < yMax; y+=stepSize) {
					for (double z = zMin; z < zMax; z+=stepSize) {
						//long l = System.nanoTime();
						double f = e1.evaluate(new double[] {x, y, z});
						//System.out.println(System.nanoTime() - l);
						double g = e2.evaluate(new double[] {x, y, z});
						//System.out.println(f + "|" + g + "|" + (f - g) + "|" + (Math.abs(f - g) < stepSize));
						if (Math.abs(f - g) < stepSize * b ) {
							points.add(new Point3D(x, y, z));
						}
					}
				}
				System.out.println((System.nanoTime() - l) / (2.5e9 / 4));
				while (points.size() > 0) {
					Point3D pointToRemove = points.get(0);
					Point3D closestPoint1 = dummyPoint;
					Point3D closestPoint2 = null;
					for (Point3D o : points) {
						if (o == pointToRemove) continue;
						if (pointToRemove.distance(o) < pointToRemove.distance(closestPoint1)) {
							if (pointToRemove.distance(o) < stepSize * a) {
								closestPoint2 = closestPoint1;
								closestPoint1 = o;
							}
						}
					}
					//System.out.println(pointToRemove.distance(closestPoint1));
					synchronized (lines) {
						if (dummyPoint != closestPoint1) lines.add(new Line3D(closestPoint1, pointToRemove));
						if (closestPoint2 != null && closestPoint2 != dummyPoint) lines.add(new Line3D(closestPoint2, pointToRemove));

					}
					points.remove(0);
				}
				System.out.println("Thread " + threadNumber + " " + (((x - xMin) / (xMax - xMin) * 33.33333 + 0)) + "% finished (" + ((System.nanoTime() - l) / (2.5e9 / 4)) + " seconds elapsed.");
			}
			
			for (double y = yMin; y < yMax; y+=stepSize) {

				List<Point3D> points = new ArrayList<Point3D>();

				String traceFunction = function.replace("y", "" + y);
				String[] traceExpressions = traceFunction.split("=");
				for (double x = xMin; x < xMax; x+=stepSize) {

					for (double z = zMin; z < zMax; z+=stepSize) {
						//long l = System.currentTimeMillis();
						double f = e1.evaluate(new double[] {x, y, z});
						//System.out.println(System.nanoTime() - l);
						double g = e2.evaluate(new double[] {x, y, z});
						//System.out.println(f + "|" + g + "|" + (f - g) + "|" + (Math.abs(f - g) < stepSize));
						if (Math.abs(f - g) < stepSize * b) {
							points.add(new Point3D(x, y, z));
						}
					}
				}
				while (points.size() > 0) {
					Point3D pointToRemove = points.get(0);
					Point3D closestPoint1 = dummyPoint;
					Point3D closestPoint2 = null;
					for (Point3D o : points) {
						if (o == pointToRemove) continue;
						if (pointToRemove.distance(o) < pointToRemove.distance(closestPoint1)) {
							if (pointToRemove.distance(o) < stepSize * a) {
								closestPoint2 = closestPoint1;
								closestPoint1 = o;
							}
						}
					}
					//System.out.println(pointToRemove.distance(closestPoint1));
					synchronized (lines) {
						if (dummyPoint != closestPoint1) lines.add(new Line3D(closestPoint1, pointToRemove));
						if (closestPoint2 != null && closestPoint2 != dummyPoint) lines.add(new Line3D(closestPoint2, pointToRemove));

					}
					points.remove(0);

				}
				System.out.println("Thread " + threadNumber + " " + (((y - yMin) / (yMax - yMin) * 33.33333 + 33.33333333)) + "% finished");
			}
			for (double x = xMin; x < xMax; x+=stepSize) {

				List<Point3D> points = new ArrayList<Point3D>();

				String traceFunction = function.replace("z", "" + x);
				String[] traceExpressions = traceFunction.split("=");
				for (double y = yMin; y < yMax; y+=stepSize) {
					for (double z = zMin; z < zMax; z+=stepSize) {
						//long l = System.currentTimeMillis();
						double f = e1.evaluate(new double[] {x, y, z});
						//System.out.println(System.nanoTime() - l);
						double g = e2.evaluate(new double[] {x, y, z});
						//System.out.println(f + "|" + g + "|" + (f - g) + "|" + (Math.abs(f - g) < stepSize));
						if (Math.abs(f - g) < stepSize * b) {
							points.add(new Point3D(z, y, x));
						}
					}
				}
				while (points.size() > 0) {
					Point3D pointToRemove = points.get(0);
					Point3D closestPoint1 = dummyPoint;
					Point3D closestPoint2 = null;
					for (Point3D o : points) {
						if (o == pointToRemove) continue;
						if (pointToRemove.distance(o) < pointToRemove.distance(closestPoint1)) {
							if (pointToRemove.distance(o) < stepSize * a) {
								closestPoint2 = closestPoint1;
								closestPoint1 = o;
							}
						}
					}
					//System.out.println(pointToRemove.distance(closestPoint1));
					//if (dummyPoint != closestPoint1) lines.add(new Line3D(closestPoint1, pointToRemove));
					//if (closestPoint2 != null && closestPoint2 != dummyPoint) lines.add(new Line3D(closestPoint2, pointToRemove));
					points.remove(0);
				}
				System.out.println("Thread " + threadNumber + " " + (((x - xMin) / (xMax - xMin) * 33.33333 + 66.6666666)) + "% finished");
			}
			System.out.println("Thread " + threadNumber + " 100% finished");
			isReady = true;
		} catch (Exception e) {
			System.err.println("A thread has crashed! Are you sure that was a valid function?");
			System.exit(-1);
		}
	}

	public void render() {
		GL11.glColor3f(0, 0, 0);
		GL11.glBegin(GL11.GL_LINES);

		synchronized (lines) {
			for (Line3D l3d: lines) {
				GL11.glVertex3d(l3d.p1.x, l3d.p1.y, l3d.p1.z);
				GL11.glVertex3d(l3d.p2.x, l3d.p2.y, l3d.p2.z);
			}
		}
		GL11.glEnd();
	}

	public int evaluate(String[] variables, double... values) {
		return 0;
	}

	

	private String getNumberFromBeginning(String string) {
		for (int i = 0; i < string.length(); i++) {
			if (!allNumberCharacters.contains("" + string.charAt(i))) {
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
		}
		return string;
	}

	@Override
	public void run() {
		createRenderData();
	}

	public boolean isReady() {
		return isReady;
	}

	private int totalIndecies;

	private List<String> variablesInOrder = new ArrayList<String>();
	private List<FunctionOperation> operations = new ArrayList<FunctionOperation>();



} class Triangle {
	Point3D p1;
	Point3D p2;
	Point3D p3;
} class Point3D {
	public Point3D(double x2, double y2, double z2) {
		x = x2;
		y = y2;
		z = z2;
	}
	public double x;
	public double y;
	public double z;

	public double distance(Point3D o) {
		return Math.sqrt((x - o.x) * (x - o.x) + (y - o.y) * (y - o.y) + (z - o.z) * (z - o.z));
	}
} class Line3D {
	public Line3D(Point3D p1o, Point3D p2o) {
		p1 = p1o;
		p2 = p2o;
	}
	Point3D p1;
	Point3D p2;
}
