package net.tankers.main.screenControllers;

import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.NiftyEventSubscriber;
import de.lessvoid.nifty.controls.ButtonClickedEvent;
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
        nifty.gotoScreen("start");
    }

    @NiftyEventSubscriber(id="login")
    public void login(final String id, final ButtonClickedEvent event) {
        TextField usernameField = screen.findNiftyControl("username", TextField.class);
        TextField passwordField = screen.findNiftyControl("password", TextField.class);
        String username = usernameField.getDisplayedText();
        String password = passwordField.getRealText();
        
        Client client = new Client("localhost", 25565, nifty);
    	client.run();
    	client.loginUser(username, password);
    	
    	try {
    		//Because of stuff being in separate threads and
    		//it takes time for client to receive response from server
			Thread.sleep(200);
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
    	
    	if(client.isLoggedIn()) {
			LobbyController lobbyController = new LobbyController();
			try {
	    		//Because of stuff being in separate threads and
	    		//it takes time for client to receive response from server
				Thread.sleep(200);
			} catch (InterruptedException e1) {
				e1.printStackTrace();
			}
	    	lobbyController.setClient(client);
			nifty.gotoScreen("lobby");
    	}
    }
}