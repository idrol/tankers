package net.tankers.main;

import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.nulldevice.NullSoundDevice;
import de.lessvoid.nifty.render.batch.BatchRenderDevice;
import de.lessvoid.nifty.renderer.lwjgl.input.LwjglInputSystem;
import de.lessvoid.nifty.renderer.lwjgl.render.LwjglBatchRenderBackendCoreProfileFactory;
import de.lessvoid.nifty.spi.time.impl.AccurateTimeProvider;
import net.tankers.main.screenControllers.MainScreenController;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

import org.lwjgl.LWJGLException;
import org.lwjgl.Sys;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.GLU;
 
public class Main {
	public static String pathPrefix = "";
    private static String intellijResourcePrefix = "build/resources/main/";
    private static String eclipseResourcePrefix = "bin/";
	private LwjglInputSystem lwjglInputSystem = null;
	
	private Nifty nifty = null;
	private long lastFrameTime = 0;
	protected boolean isRunning = true;
	
    public static void main(String[] argv) {
        System.out.println(argv[0]);
        if(argv[0].equals("ECLIPSE")) {
            pathPrefix = eclipseResourcePrefix;
        }else if(argv[0].equals("INTELLIJ")) {
            System.out.println("Worked");
            pathPrefix = intellijResourcePrefix;
        }
        
    	Main main = new Main();
    	main.start();
    }
	
	public void start() {
		
        try {
            Display.setDisplayMode(new DisplayMode(1366,768));
            Display.create();
        } catch (LWJGLException e) {
            e.printStackTrace();
            System.exit(0);
        }
        
        initGL();
        initLWJGLInputSystem();
        nifty = initNifty(lwjglInputSystem);
		loadNiftyXML();
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
        shutdown(lwjglInputSystem);
    }
	
	private static void initGL(){
		GL11.glMatrixMode(GL11.GL_PROJECTION);
		GL11.glLoadIdentity();
		GL11.glOrtho(0, 800, 0, 600, 1, -1);
		GL11.glMatrixMode(GL11.GL_MODELVIEW);
	}
	
	
	private Nifty initNifty(final LwjglInputSystem lwjglInputSystem) {
		return new Nifty(
				new BatchRenderDevice(LwjglBatchRenderBackendCoreProfileFactory.create()), 
				new NullSoundDevice(), 
				lwjglInputSystem, 
				new AccurateTimeProvider());
	}
	
	public void loadNiftyXML() {
		nifty.loadStyleFile("nifty-default-styles.xml");
		nifty.loadControlFile("nifty-default-controls.xml");
		try {
			nifty.fromXml("main-screen", new FileInputStream(Main.pathPrefix+"mainMenu.nifty"), "start", new MainScreenController());
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
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
	
	public void update(float delta) {
        if(nifty.update()){
            isRunning = false;
        }
	}
	
	public void render() {
        nifty.render(false);
	}
	
	private void initLWJGLInputSystem() {
		lwjglInputSystem = new LwjglInputSystem();
		try {
			lwjglInputSystem.startup();
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(-1);
		}
	}
   
	private static void shutdown(final LwjglInputSystem inputSystem) {
	    inputSystem.shutdown();
	    Display.destroy();
	    System.exit(0);
	  }
}