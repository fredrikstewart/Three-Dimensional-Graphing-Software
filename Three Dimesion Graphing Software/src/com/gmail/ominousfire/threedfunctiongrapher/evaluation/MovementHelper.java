package com.gmail.ominousfire.threedfunctiongrapher.evaluation;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

public class MovementHelper {
	
	static float posX = 	0;
	static float posY = 	0;
	static float posZ = 	0;
	static float velX = 	0;
	static float velY = 	0;
	static float velZ = 	0;
	static float yaw = 		0;
	static float pitch = 	0;
	private static float maxPitch = 70F;
	private static float pitchMod = 1.2F;
	private static float yawMod = 1.2F;
	private static double moveMod = .001F;
	private static float friction = .98F;
	private static float yMoveMod = .001F;
	public static boolean wait    = false;

	public static void handleMovement() {
		if (Renderer.cinematicMode) {
			velX += moveMod;

		} else {
			if (Keyboard.isKeyDown(Keyboard.KEY_A)) {
				velX -=  Math.cos(Math.toRadians(yaw)) * moveMod;
				velZ -=  Math.sin(Math.toRadians(yaw)) * moveMod;
			}
			if (Keyboard.isKeyDown(Keyboard.KEY_D)) {
				velX +=  Math.cos(Math.toRadians(yaw)) * moveMod;
				velZ +=  Math.sin(Math.toRadians(yaw)) * moveMod;
			}
			if (Keyboard.isKeyDown(Keyboard.KEY_W)) {
				velX -=  Math.cos(Math.toRadians(yaw + 90)) * moveMod;
				velZ -=  Math.sin(Math.toRadians(yaw + 90)) * moveMod;
			}
			if (Keyboard.isKeyDown(Keyboard.KEY_S)) {
				velX +=  Math.cos(Math.toRadians(90 + yaw)) * moveMod;
				velZ +=  Math.sin(Math.toRadians(90 + yaw)) * moveMod;
			}
			if (Keyboard.isKeyDown(Keyboard.KEY_SPACE)) {
				velY += yMoveMod;
			}
			if (Keyboard.isKeyDown(Keyboard.KEY_LSHIFT)) {
				velY -= yMoveMod;
			}
			if (Keyboard.isKeyDown(Keyboard.KEY_E)) {
				wait = !wait;
			}
		}
		pitch -= Mouse.getDY() * pitchMod;
		yaw += Mouse.getDX() * yawMod;
		posX += velX;
		posY += velY;
		posZ += velZ;
		velX *= friction;
		velY *= friction;
		velZ *= friction;
		if(pitch > maxPitch) pitch = maxPitch;
		if(pitch < -maxPitch) pitch = -maxPitch;
		
	}

	public static void moveToCharacter() {
		if (Renderer.cinematicMode) {
			GL11.glLoadIdentity();
			//GL11.glRotatef(pitch,1,0,0);
			//GL11.glRotatef(yaw,0,1,0);
			//GL11.glTranslatef(-posX, -posY, -posZ);
			GL11.glTranslatef(0,0,-19);
			GL11.glRotated(-posX * 15 / Math.PI,0,1,0);
		} else {
			GL11.glLoadIdentity();
			GL11.glRotatef(pitch,1,0,0);
			GL11.glRotatef(yaw,0,1,0);
			GL11.glTranslatef(-posX, -posY, -posZ);
		}
		
	}

}
