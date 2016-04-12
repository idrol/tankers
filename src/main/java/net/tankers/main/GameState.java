package net.tankers.main;

import org.lwjgl.Sys;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;

public abstract class GameState {
	
	private long lastFrameTime = 0;
	private boolean isRunning = true;
	
	public void start() {
		lastFrameTime = getTime();
		while(!Display.isCloseRequested() && isRunning){
			float delta = getDelta();
			update(delta);
			
			// Clear the screen and depth buffer
			GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);  
			         
			// set the color of the quad (R,G,B,A)
			GL11.glColor3f(0.5f,0.5f,1.0f);
			render();
			Display.update();
		}
		cleanUp();
	}
	
	public long getTime() {
		return (Sys.getTime() * 1000) / Sys.getTimerResolution();
	}
	
	public float getDelta() {
		long time = getTime();
		float delta = (time - lastFrameTime);
		lastFrameTime = time;
		return delta;
	}
	
	public abstract void update(float delta);
	
	public abstract void render();
	
	public void halt() {
		isRunning = false;
	}
	
	public abstract void cleanUp();
	
}
