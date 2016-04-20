package net.tankers.main.screenControllers;

import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.NiftyEventSubscriber;
import de.lessvoid.nifty.controls.ButtonClickedEvent;
import de.lessvoid.nifty.controls.ChatTextSendEvent;
import de.lessvoid.nifty.screen.DefaultScreenController;
import de.lessvoid.nifty.screen.Screen;
import net.tankers.client.Client;
import net.tankers.main.Game;

/**
 * Created by idrol on 13-04-2016.
 */
public class LobbyController extends DefaultScreenController {
    private Nifty nifty = null;
    private Screen screen = null;
    private Client client = null;
    private Game game;

    public LobbyController(Game game){
        this.game = game;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    @Override
    public void bind(Nifty nifty, Screen screen) {
        this.nifty = nifty;
        this.screen = screen;
        System.out.println("Bind succesfull");
    }

    @NiftyEventSubscriber(id="search-match")
    public void searchmatch(final String id, final ButtonClickedEvent event){
        client.writeMessage("search_match");
    }


    @NiftyEventSubscriber(id="chat")
    public void sendMessage(final String id, final ChatTextSendEvent event) {
        client.writeMessage(event.getText());
    }
}
