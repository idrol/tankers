package net.tankers.main.screenControllers;

import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.NiftyEventSubscriber;
import de.lessvoid.nifty.controls.ButtonClickedEvent;
import de.lessvoid.nifty.screen.DefaultScreenController;
import de.lessvoid.nifty.screen.Screen;
import net.tankers.main.Lobby;
import net.tankers.main.Main;

/**
 * Created by idrol on 13-04-2016.
 */
public class Login extends DefaultScreenController {
    private Nifty nifty = null;
    private Screen screen = null;

    @Override
    public void bind(Nifty nifty, Screen screen) {
        this.nifty = nifty;
        this.screen = screen;
        System.out.println("Bind succesfull");
    }

    @NiftyEventSubscriber(id="back")
    public void back(final String id, final ButtonClickedEvent event) {
        nifty.gotoScreen("start");
    }

    @NiftyEventSubscriber(id="login")
    public void login(final String id, final ButtonClickedEvent event) {

        Main.switchState(new Lobby());
    }
}