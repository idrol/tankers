package net.tankers.main;

import org.lwjgl.Sys;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.GLU;

import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.nulldevice.NullSoundDevice;
import de.lessvoid.nifty.render.batch.BatchRenderDevice;
import de.lessvoid.nifty.renderer.lwjgl.input.LwjglInputSystem;
import de.lessvoid.nifty.renderer.lwjgl.render.LwjglBatchRenderBackendCoreProfileFactory;
import de.lessvoid.nifty.spi.time.impl.AccurateTimeProvider;

public abstract class GameState {
	
	private long lastFrameTime = 0;
	protected boolean isRunning = true;
	
	protected Nifty nifty = null;
	
	private Nifty initNifty(final LwjglInputSystem lwjglInputSystem) {
		return new Nifty(
				new BatchRenderDevice(LwjglBatchRenderBackendCoreProfileFactory.create()), 
				new NullSoundDevice(), 
				lwjglInputSystem, 
				new AccurateTimeProvider());
	}
	
	public void start() {
		nifty = initNifty(Main.lwjglInputSystem);
		init();
		lastFrameTime = getTime();
		while(!Display.isCloseRequested() && isRunning){
			float delta = getDelta();
			update(delta);
			
			// Clear the screen and depth buffer
			GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);  
			         
			// set the color of the quad (R,G,B,A)
			GL11.glColor3f(0.5f,0.5f,1.0f);
			GL11.glEnable(GL11.GL_BLEND);
			GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
			render();
			int error = GL11.glGetError();
	      	if (error != GL11.GL_NO_ERROR) {
		        String glerrmsg = GLU.gluErrorString(error);
		        System.err.println(glerrmsg);
		    }
			Display.update();
		}
		cleanUp();
	}
	
	public abstract void init();
	
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
