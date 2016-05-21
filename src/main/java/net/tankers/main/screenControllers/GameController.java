package net.tankers.main.screenControllers;

import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.screen.Screen;

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

    }

    @Override
    public void update(float delta) {
        System.out.println("Update test");
    }
}
