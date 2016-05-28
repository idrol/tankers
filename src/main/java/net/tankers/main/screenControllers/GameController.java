package net.tankers.main.screenControllers;

import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.NiftyEventSubscriber;
import de.lessvoid.nifty.controls.ButtonClickedEvent;
import de.lessvoid.nifty.controls.Label;
import de.lessvoid.nifty.screen.Screen;
import net.tankers.client.Client;
import net.tankers.map.DefaultMap;
import net.tankers.map.Map;
import org.lwjgl.input.Keyboard;

/**
 * Created by Adrian on 21-05-2016.
 */
public class GameController extends RenderableScreenController {
    private Nifty nifty;
    private Map map;

    @Override
    public void bind(Nifty nifty, Screen screen) {
        this.nifty = nifty;
        map = new DefaultMap();
        map.init();
    }

    @Override
    public void render() {
        nifty.getCurrentScreen().findNiftyControl("notification", Label.class).setText(Client.currentNotification);
        Client.render();
        map.render();
    }

    @Override
    public void update(float delta) {
        Client.update(delta);
    }

    @NiftyEventSubscriber(id="backtolobby")
    public void logout(final String id, final ButtonClickedEvent event) {
        Client.currentNotification = "";
        nifty.gotoScreen("lobby");
    }
}
