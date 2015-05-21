package com.gmail.ominousfire.threedfunctiongrapher.evaluation;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Scanner;

import javax.imageio.ImageIO;

import org.lwjgl.BufferUtils;
import org.lwjgl.LWJGLException;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.GLU;

public class Renderer implements Runnable {

	private int depthRender = 100;
	private double yRender = 100;
	private double xRender = 100;
	private static String functionString = "sin(y + x) = 2 * x * (z + x * y + y * z)";
	private int takeImageTimer;
	private static double renderCubesize = 5;
	private static double renderStepsize = .05;
	public static boolean cinematicMode = false;



	public Renderer() {
		(new Thread(this, "Render")).start();
	}

	void init() {
		try {
			Display.setDisplayMode(new DisplayMode(1024,724));
			Display.create();
		} catch (LWJGLException e) {
			e.printStackTrace();
		}
		GL11.glOrtho(-xRender, xRender  , -yRender, yRender , -depthRender , depthRender);   
		GL11.glMatrixMode(GL11.GL_PROJECTION);
		GL11.glLoadIdentity();
		GL11.glEnable(GL11.GL_TEXTURE_2D); // Enable Texture Mapping ( NEW )
		GL11.glShadeModel(GL11.GL_SMOOTH);
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA,GL11.GL_ONE_MINUS_SRC_ALPHA);
	}


	@Override
	public void run() {
		init();
		double cores = Runtime.getRuntime().availableProcessors();
		Function[] functions = new Function[(int) cores];
		for (int i = 0; i < cores; i++) {
			Function f = new Function(functionString, -renderCubesize, renderCubesize, -renderCubesize, renderCubesize, -renderCubesize + 2 * (i) / cores * (renderCubesize + renderStepsize), -renderCubesize + 2 * (i + 1) / cores * (renderCubesize + renderStepsize), renderStepsize, new String[] {"x" , "y", "z"}, i + 1);
			Thread t = new Thread(f);
			t.start();
			functions[i] = f;
		}
		boolean flag0 = false;
		while (!flag0) {
			flag0 = true;
			for (int i = 0; i < cores; i++) {
				flag0 = flag0 && functions[i].isReady();
			}
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		while (!Keyboard.isKeyDown(Keyboard.KEY_ESCAPE) && !Display.isCloseRequested()) 
		{
			prerender();
			MovementHelper.moveToCharacter();
			drawAxii();
			for (int i = 0; i < cores; i++) {
				functions[i].render();
			}
			Display.update();
			MovementHelper.handleMovement();
			if (takeImageTimer++ % 12 == 0 && cinematicMode) takeScreenshot();
		}
	}

	private void drawAxii() {
		//draws simple axiis
		GL11.glBegin(GL11.GL_LINES);
		GL11.glColor3f(1, 0, 0);
		GL11.glVertex3f((float) renderCubesize, 0, 0);
		GL11.glVertex3f((float) -renderCubesize, 0, 0);
		GL11.glColor3f(0, 1, 0);
		GL11.glVertex3f(0, (float) renderCubesize, 0);
		GL11.glVertex3f(0, (float) -renderCubesize, 0);
		GL11.glColor3f(0, 0, 1);
		GL11.glVertex3f(0, 0,(float) renderCubesize);
		GL11.glVertex3f(0, 0,(float) -renderCubesize);
		GL11.glEnd();
	}

	private void prerender() {
		float width = (float)Display.getWidth();
		float height = (float)Display.getWidth();

		GL11.glClearColor(0.999f, 0.999f, 0.999f, 1.0f);               //This Will Clear The Background Color To Black
		GL11.glClearDepth(1.0);                                  //Enables Clearing Of The Depth Buffer
		GL11.glHint(GL11.GL_PERSPECTIVE_CORRECTION_HINT, GL11.GL_NICEST);  // Really Nice Perspective Calculations
		GL11.glViewport(0, 0, (int)width, (int)height);
		GL11.glMatrixMode(GL11.GL_PROJECTION);
		GL11.glLoadIdentity();
		GLU.gluPerspective(45.0f, width / height, 0.1f, 100.0f); 
		GL11.glMatrixMode(GL11.GL_MODELVIEW);                    
		GL11.glLoadIdentity();
		GL11.glRenderMode(GL11.GL_RGBA);

		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);  
	}

	public static void main(String[] args) {
		Scanner scanner = new Scanner(System.in);
		System.out.println("Enter equation. (String) (More complicated equation means longer to load)");
		System.out.println("Reminder: Equation should be in the form of constants, coefficents, and the variables x, y, and z");
		functionString = scanner.nextLine();
		System.out.println("Enter cube size to render (Bigger area means longer to load but more of the function will be shown (Decimal allowed)");
		System.out.println("Reminder: cube size stretches on both the positive AND negative axii, so a cube size of 10 will render from -5 to 5.");
		renderCubesize = scanner.nextDouble() / 2;
		System.out.println("Enter render stepsize (smaller step size means more detail but longer to load) (Decimal allowed)");
		renderStepsize = scanner.nextDouble();
		System.out.println("Enter if you want to set this up in cinematic mode (for GIF making) (true/false) \nWARNING: In this mode, screenshots will automatically be added to your screenshots folder every second. \nDo NOT leave this mode running or it will fill your harddrive.");
		cinematicMode = scanner.nextBoolean();
		scanner.close();
		new Renderer();
	}

	private static void takeScreenshot() {
		int width = Display.getWidth();
		int height = Display.getHeight();
		ByteBuffer buffer = BufferUtils.createByteBuffer(width * height * 4);
		GL11.glReadBuffer(GL11.GL_BACK);
		GL11.glReadPixels(0, 0, width, height, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, buffer);
		int[] data = new int[width * height];
		for (int i = 0; i < buffer.capacity(); i+=4) {
			int r = buffer.get(i) & 0xFF;
			int g = buffer.get(i + 1)& 0xFF;
			int b = buffer.get(i + 2)& 0xFF;
			int a = buffer.get(i + 3)& 0xFF;
			int modifier = width - (((i / 4) % width));
			data[data.length - ((i / 4 / width * width) + modifier)] = ((a << 24) + (r << 16) + (g << 8)) + b;
		}
		BufferedImage bi = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		bi.setRGB(0, 0, width, height, data, 0, width);
		try {
			ImageIO.write(bi, "PNG", new File("screenshots/" + System.currentTimeMillis() + ".png"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
