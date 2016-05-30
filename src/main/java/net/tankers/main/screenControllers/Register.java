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
public class Register extends DefaultScreenController {
    private Nifty nifty = null;
    private Screen screen = null;

    @Override
    public void bind(Nifty nifty, Screen screen) {
        this.nifty = nifty;
        this.screen = screen;
        System.out.println("Bind succesfull");
    }
    
    @NiftyEventSubscriber(id="Register")
    public void register(final String id, final ButtonClickedEvent event) {
    	TextField usernameField = screen.findNiftyControl("username", TextField.class);
    	TextField passwordField = screen.findNiftyControl("password", TextField.class);
    	TextField verifyPasswordField = screen.findNiftyControl("password-verify", TextField.class);
    	
    	String username = usernameField.getDisplayedText();
    	String password = passwordField.getRealText();
    	String verifyPassword = verifyPasswordField.getRealText();
    	
    	Client.registerUser(username, password, verifyPassword);

        passwordField.setText("");
        verifyPasswordField.setText("");
    }

    @NiftyEventSubscriber(id="back")
    public void back(final String id, final ButtonClickedEvent event) {
        screen.findNiftyControl("username", TextField.class).setText("");
        screen.findNiftyControl("notification", Label.class).setText("");
        nifty.gotoScreen("start");
    }
    
}