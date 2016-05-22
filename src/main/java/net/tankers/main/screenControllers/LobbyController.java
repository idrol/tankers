package net.tankers.main.screenControllers;

import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.NiftyEventSubscriber;
import de.lessvoid.nifty.controls.ButtonClickedEvent;
import de.lessvoid.nifty.screen.DefaultScreenController;
import de.lessvoid.nifty.screen.Screen;
import net.tankers.client.Client;
import net.tankers.main.Game;

/**
 * Created by idrol on 13-04-2016.
 */
public class LobbyController extends DefaultScreenController {
    private Nifty nifty;
    private Client client;
    private Game game;

    public void setClient(Client client) {
        this.client = client;
    }

    @Override
    public void bind(Nifty nifty, Screen screen) {
        this.nifty = nifty;
        System.out.println("Bind succesfull - LobbyController");
    }

    @NiftyEventSubscriber(id="search-match")
    public void searchmatch(final String id, final ButtonClickedEvent event){
    	Client.writeMessage("search_match");
        nifty.gotoScreen("search");
    }
}
