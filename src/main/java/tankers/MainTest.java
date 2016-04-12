package tankers;
import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
 
public class MainTest {
    public void start() {
        try {
            Display.setDisplayMode(new DisplayMode(800,600));
            Display.create();
        } catch (LWJGLException e) {
            e.printStackTrace();
            System.exit(0);
        }
         
        // init OpenGL here
         
        while (!Display.isCloseRequested()) {
             
            // render OpenGL here
             
            Display.update();
        }
         
        Display.destroy();
    }
     
    public static void main(String[] argv) {
        MainTest displayExample = new MainTest();
        displayExample.start();
    }
}