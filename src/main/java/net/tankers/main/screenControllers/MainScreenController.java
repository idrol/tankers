package net.tankers.main.screenControllers;

import java.nio.channels.UnresolvedAddressException;

import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.NiftyEventSubscriber;
import de.lessvoid.nifty.controls.Button;
import de.lessvoid.nifty.controls.ButtonClickedEvent;
import de.lessvoid.nifty.controls.Label;
import de.lessvoid.nifty.controls.TextField;
import de.lessvoid.nifty.screen.DefaultScreenController;
import de.lessvoid.nifty.screen.Screen;
import de.lessvoid.nifty.tools.Color;
import net.tankers.client.Client;

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
	
	@NiftyEventSubscriber(id="connect")
    public void connect(final String id, final ButtonClickedEvent event) {
        TextField serverHostField = screen.findNiftyControl("serverhost", TextField.class);
        String serverhost = serverHostField.getDisplayedText();
    	Label notificationField = screen.findNiftyControl("notification", Label.class);
        Button connectButton = screen.findNiftyControl("connect", Button.class);

        try {
            Client.setHost(serverhost);
        	Client.run();
        	notificationField.setColor(new Color("#077A00"));
        	
        	if (notificationField.getText().equals("")) {
        		serverhost = "localhost";
        	}

            notificationField.setText("Successfully connected to '" + serverhost + "'");

            screen.findElementById("disconnect").setVisible(true);
            screen.findElementById("Login").setVisible(true);
            screen.findElementById("Register").setVisible(true);
            screen.findElementById("connectPanel").setVisible(false);
        	
        } catch (Exception e) {
        	e.printStackTrace();
        	notificationField.setColor(new Color("#7A0000"));
            notificationField.setText("Could not connect to host '" + serverhost + "'");
            System.err.println("Could not connect to host '" + serverhost + "'");
        }
    }

    @NiftyEventSubscriber(id="disconnect")
    public void disconnect(final String id, final ButtonClickedEvent event) {
        Client.stop();
        screen.findElementById("disconnect").setVisible(false);
        screen.findElementById("Login").setVisible(false);
        screen.findElementById("Register").setVisible(false);
        screen.findElementById("connectPanel").setVisible(true);
        Label notificationField = screen.findNiftyControl("notification", Label.class);
        notificationField.setColor(new Color("#000000"));
        notificationField.setText("Disconnected from the server");
    }

    @NiftyEventSubscriber(id="Register")
    public void register(final String id, final ButtonClickedEvent event) {
        nifty.gotoScreen("register");
    }

    @NiftyEventSubscriber(id="quit")
    public void quit(final String id, final ButtonClickedEvent event) {
        nifty.exit();
    }
}