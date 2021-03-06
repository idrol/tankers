package net.tankers.main.screenControllers;

import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.NiftyEventSubscriber;
import de.lessvoid.nifty.controls.ButtonClickedEvent;
import de.lessvoid.nifty.controls.Label;
import de.lessvoid.nifty.controls.TextField;
import de.lessvoid.nifty.screen.DefaultScreenController;
import de.lessvoid.nifty.screen.Screen;
import net.tankers.client.Client;

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
        TextField usernameField = screen.findNiftyControl("username", TextField.class);
        TextField passwordField = screen.findNiftyControl("password", TextField.class);
        screen.findNiftyControl("notification", Label.class).setText("");
        usernameField.setText("");
        passwordField.setText("");
        nifty.gotoScreen("start");
    }

    @NiftyEventSubscriber(id="login")
    public void login(final String id, final ButtonClickedEvent event) {
        TextField usernameField = screen.findNiftyControl("username", TextField.class);
        TextField passwordField = screen.findNiftyControl("password", TextField.class);
        String username = usernameField.getRealText();
        String password = passwordField.getRealText();

    	Client.loginUser(username, password);
        Client.username = username;
        passwordField.setText("");
    }
}