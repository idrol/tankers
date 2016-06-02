package net.tankers.main;

import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.nulldevice.NullSoundDevice;
import de.lessvoid.nifty.render.batch.BatchRenderDevice;
import de.lessvoid.nifty.renderer.lwjgl.input.LwjglInputSystem;
import de.lessvoid.nifty.renderer.lwjgl.render.LwjglBatchRenderBackendFactory;
import de.lessvoid.nifty.screen.ScreenController;
import de.lessvoid.nifty.spi.time.impl.AccurateTimeProvider;
import net.tankers.client.Client;
import net.tankers.client.analytics.MatchesPlayed;
import net.tankers.client.analytics.TimePlayed;
import net.tankers.main.screenControllers.MainScreenController;
import net.tankers.main.screenControllers.RenderableScreenController;
import org.lwjgl.LWJGLException;
import org.lwjgl.Sys;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.GLU;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
 
public class Main {
	private static final boolean DEBUG_GAME = false;
	public static String pathPrefix = "";
    private static String intellijResourcePrefix = "build/";
    private static String eclipseResourcePrefix = "bin/";
	private static String buildResourcePrefixed = "";
	private LwjglInputSystem lwjglInputSystem = null;
	
	private Nifty nifty = null;
	private long lastFrameTime = 0;
	protected boolean isRunning = true;
	
    public static void main(String[] argv) {
		if(argv.length != 0){
			System.out.println(argv[0]);
			if(argv[0].equals("ECLIPSE")) {
				pathPrefix = eclipseResourcePrefix;
			}else if(argv[0].equals("INTELLIJ")) {
				System.out.println("Worked");
				pathPrefix = intellijResourcePrefix;
			}
		}else{
			pathPrefix = buildResourcePrefixed;
		}
        
    	Main main = new Main();
    	main.start();
    }
	
	public void start() {
		TimePlayed.setGameStartTime(System.currentTimeMillis());
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
        
	    Client.init(nifty);
	    
        
	    	
		if(DEBUG_GAME){
			Client.setHost("localhost");
	    	Client.run();
			Client.loginUser("idrol", "pass");
			try {
				Thread.sleep(200);
				Client.writeMessage("search_match");
				nifty.gotoScreen("search");
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

		}

		while(!Display.isCloseRequested() && isRunning){
			float delta = getDelta();
			update(delta);
			
			// Clear the screen and depth buffer
			GL11.glClear(GL11.GL_COLOR_BUFFER_BIT);
			         
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
			Display.sync(60);
		}
        shutdown(lwjglInputSystem);
    }
	
	private static void initGL(){
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		GL11.glMatrixMode(GL11.GL_PROJECTION);
		GL11.glLoadIdentity();
		GL11.glOrtho(0, 800, 0, 600, 1, -1);
		GL11.glMatrixMode(GL11.GL_MODELVIEW);
	}
	
	
	private Nifty initNifty(final LwjglInputSystem lwjglInputSystem) {
		return new Nifty(
				new BatchRenderDevice(LwjglBatchRenderBackendFactory.create()),
				new NullSoundDevice(), 
				lwjglInputSystem, 
				new AccurateTimeProvider());
	}
	
	public void loadNiftyXML() {
		nifty.loadStyleFile("nifty-default-styles.xml");
		nifty.loadControlFile("nifty-default-controls.xml");
		nifty.fromXml("main-screen", getClass().getResourceAsStream("/niftyScreens.nifty"), "start", new MainScreenController());
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
		ScreenController screenController = nifty.getCurrentScreen().getScreenController();
		if(screenController instanceof RenderableScreenController){
			((RenderableScreenController) screenController).update(delta);
		}
        if(nifty.update()){
            isRunning = false;
        }
	}
	
	public void render() {
		if(nifty.getCurrentScreen() != null) {
			ScreenController screenController = nifty.getCurrentScreen().getScreenController();
			if(screenController instanceof RenderableScreenController){
				((RenderableScreenController) screenController).render();
			}
			nifty.render(false);
		}
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
		TimePlayed.setGameQuitTime(System.currentTimeMillis());
		Client.writeMessage("timeplayed;"+TimePlayed.getGameStartTime() + ":" + TimePlayed.getGameQuitTime());
	    inputSystem.shutdown();
	    Display.destroy();

	    Client.stop();
	    
	    System.out.println("Played for " 
	    		+ (TimePlayed.getGameQuitTime() - TimePlayed.getGameStartTime())/1000.0f
	    		+ "s");
	    System.exit(0);
	  }
}