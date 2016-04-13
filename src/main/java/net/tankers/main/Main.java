package net.tankers.main;

import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GL11;

import de.lessvoid.nifty.renderer.lwjgl.input.LwjglInputSystem;
 
public class Main {
	public static String pathPrefix = "";
    private static String intellijResourcePrefix = "build/resources/main/";
    private static String eclipseResourcePrefix = "";
	public static GameState currentState = null;
	public static GameState nextState = null;
	private static boolean stopGame = false;
	public static LwjglInputSystem lwjglInputSystem = null;
	
	private static void init(){
		initInput();
		currentState = new MainMenu();
		GL11.glMatrixMode(GL11.GL_PROJECTION);
		GL11.glLoadIdentity();
		GL11.glOrtho(0, 800, 0, 600, 1, -1);
		GL11.glMatrixMode(GL11.GL_MODELVIEW);
	}
	
	private static void initInput() {
		lwjglInputSystem = new LwjglInputSystem();
		try {
			lwjglInputSystem.startup();
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(-1);
		}
	}
	
    public static void start() {
        try {
            Display.setDisplayMode(new DisplayMode(1366,768));
            Display.create();
        } catch (LWJGLException e) {
            e.printStackTrace();
            System.exit(0);
        }
        init();
        while (!stopGame) {
            currentState.start();
            if(nextState != null){
				System.out.println("Switching to gamestate " + nextState.getClass());
            	currentState = nextState;
                nextState = null;
            }else{
            	System.out.println("Next state was null shutting down game!");
            	stopGame = true;
            }
            
        }
         
        Display.destroy();
        lwjglInputSystem.shutdown();
    }
    
    public static void switchState(GameState gameState) {
    	nextState = gameState;
    	currentState.halt();
    }
     
    public static void main(String[] argv) {
        System.out.println(argv[0]);
        if(argv[0].equals("ECLIPSE")) {
            pathPrefix = eclipseResourcePrefix;
        }else if(argv[0].equals("INTELLIJ")) {
            System.out.println("Worked");
            pathPrefix = intellijResourcePrefix;
        }
    	Main.start();
    }
}