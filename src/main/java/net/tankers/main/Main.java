package net.tankers.main;

import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GL11;
 
public class Main {
	public static GameState currentState = null;
	public static GameState nextState = null;
	private static boolean stopGame = false;
	
	private static void init(){
		currentState = new MainMenu();
		GL11.glMatrixMode(GL11.GL_PROJECTION);
		GL11.glLoadIdentity();
		GL11.glOrtho(0, 800, 0, 600, 1, -1);
		GL11.glMatrixMode(GL11.GL_MODELVIEW);
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
            	currentState = nextState;
                nextState = null;
            }else{
            	System.out.println("Next state was null shuting down game!");
            	stopGame = true;
            }
            
        }
         
        Display.destroy();
    }
    
    public static void switchState(GameState gameState) {
    	nextState = gameState;
    	currentState.halt();
    }
     
    public static void main(String[] argv) {
    	Main.start();
    }
}