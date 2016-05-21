package net.tankers.main.screenControllers;

import de.lessvoid.nifty.screen.DefaultScreenController;

/**
 * Created by Adrian on 21-05-2016.
 */
public abstract class RenderableScreenController extends DefaultScreenController {
    public abstract void render();

    public abstract void update(float delta);
}
