package net.tankers.main.screenControllers;

import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.NiftyEventSubscriber;
import de.lessvoid.nifty.controls.ButtonClickedEvent;
import de.lessvoid.nifty.controls.Label;
import de.lessvoid.nifty.controls.TextField;
import de.lessvoid.nifty.screen.DefaultScreenController;
import de.lessvoid.nifty.screen.Screen;
import de.lessvoid.nifty.tools.Color;
import de.lessvoid.nifty.tools.SizeValue;
import net.tankers.client.Client;
import net.tankers.exceptions.DuplicateUserException;
import net.tankers.main.Game;
import net.tankers.server.sqlite.SQLiteJDBC;

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
    	
    	Client client = new Client("localhost", 25565,nifty);
    	client.run();
    	client.registerUser(username, password, verifyPassword);
    }

    @NiftyEventSubscriber(id="back")
    public void back(final String id, final ButtonClickedEvent event) {
        nifty.gotoScreen("start");
    }
    
}