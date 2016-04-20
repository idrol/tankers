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
import net.tankers.server.sqlite.DuplicateUserException;
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
    	Label notificationLabel = screen.findNiftyControl("notification", Label.class);
    	notificationLabel.setWidth(new SizeValue("500px"));
    	
    	String username = usernameField.getDisplayedText();
    	String password = passwordField.getRealText();
    	String verifyPassword = verifyPasswordField.getRealText();
    	
    	//Samplecode, will actually happen on serverside
    	if (password.length() > 3) {
	    	if (verifyPassword(username, password, verifyPassword)) {
	    		SQLiteJDBC sqlite = new SQLiteJDBC();
	    		sqlite.connectToDatabase("database");
	    		
	    		//In case users table does not exist already
	    		sqlite.createTable("users", 
	    				"uniqueid integer PRIMARY KEY AUTOINCREMENT," +
	    				" username TEXT NOT NULL," +
	    				" password TEXT NOT NULL");
	    		
	    		
	    		try {
					sqlite.createUser(username, password);
					notificationLabel.setColor(new Color("#00ff00"));
					notificationLabel.setText("Successfully registered user " + username + "!");
				} catch (DuplicateUserException e) {
					notificationLabel.setColor(new Color("#ff0000"));
					notificationLabel.setText("A user called '" + username + "' already exists");
				}
	
	    	} else {
	    		notificationLabel.setColor(new Color("#ff0000"));
				notificationLabel.setText("The passwords don't match");
	    	}
    	} else {
    		notificationLabel.setColor(new Color("#ff0000"));
			notificationLabel.setText("The password and username have to be at least 4 characters long");
    	}
    }

    @NiftyEventSubscriber(id="back")
    public void back(final String id, final ButtonClickedEvent event) {
        nifty.gotoScreen("start");
    }
    
    private boolean verifyPassword(String username, String password, String verifyPassword) {
    	if(password.equals(verifyPassword)) {
    		
    		return true;
    	} else {
    		return false;
    	}
    }
}