package net.tankers.main.screenControllers;

import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.NiftyEventSubscriber;
import de.lessvoid.nifty.controls.ButtonClickedEvent;
import de.lessvoid.nifty.screen.DefaultScreenController;
import de.lessvoid.nifty.screen.Screen;
import net.tankers.main.Game;
import net.tankers.main.Main;

public class MainScreenController extends DefaultScreenController {
    private Nifty nifty = null;
    private Screen screen = null;

    @Override
    public void bind(Nifty nifty, Screen screen) {
        this.nifty = nifty;
        this.screen = screen;
        System.out.println("Bind succesfull");
    }

	@NiftyEventSubscriber(id="Login")
    public void login(final String id, final ButtonClickedEvent event) {
        nifty.gotoScreen("login");
    }

    @NiftyEventSubscriber(id="Register")
    public void register(final String id, final ButtonClickedEvent event) {
        nifty.gotoScreen("register");
    }

    @NiftyEventSubscriber(id="quit")
    public void quit(final String id, final ButtonClickedEvent event) {
        Main.currentState.halt();
    }
}