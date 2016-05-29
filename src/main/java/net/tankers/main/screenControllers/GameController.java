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
    private Screen screen = null;


    @Override
    public void bind(Nifty nifty, Screen screen) {
        this.nifty = nifty;
        this.screen = screen;
        System.out.println("Bind to GameController");
        map = new DefaultMap();
        map.init();
    }

    @Override
    public void render() {
        if(Client.renderResult) {
            screen.findNiftyControl("notification", Label.class).setText(Client.currentNotification);
            screen.findElementById("backtolobby").setVisible(true);
        }

        Client.render();
        map.render();
    }

    @Override
    public void update(float delta) {
        Client.update(delta);
    }

    @NiftyEventSubscriber(id="backtolobby")
    public void backtolobby(final String id, final ButtonClickedEvent event) {
        Client.currentNotification = "";
        screen.findNiftyControl("notification", Label.class).setText(Client.currentNotification);
        Client.renderResult = false;
        screen.findElementById("backtolobby").setVisible(false);
        nifty.gotoScreen("lobby");
    }
}
