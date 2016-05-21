package net.tankers.main.screenControllers;

import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.screen.Screen;
import static org.lwjgl.opengl.GL11.*;

/**
 * Created by Adrian on 21-05-2016.
 */
public class GameController extends RenderableScreenController {
    private Nifty nifty;

    @Override
    public void bind(Nifty nifty, Screen screen) {
        this.nifty = nifty;

    }

    @Override
    public void render() {
        glBegin(GL_QUADS);
            glColor3f(1, 0, 0);
            glVertex2i(200, 0);
            glVertex2i(100, 0);
            glVertex2i(100, 100);
            glVertex2i(200, 100);
        glEnd();
    }

    @Override
    public void update(float delta) {

    }
}
