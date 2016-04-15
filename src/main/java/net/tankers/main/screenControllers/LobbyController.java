package net.tankers.main.screenControllers;

import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.NiftyEventSubscriber;
import de.lessvoid.nifty.controls.ChatTextSendEvent;
import de.lessvoid.nifty.screen.DefaultScreenController;
import de.lessvoid.nifty.screen.Screen;
import net.tankers.client.Client;

/**
 * Created by idrol on 13-04-2016.
 */
public class LobbyController extends DefaultScreenController {
    private Nifty nifty = null;
    private Screen screen = null;
    private Client client = null;

    public void setClient(Client client) {
        this.client = client;
    }

    @Override
    public void bind(Nifty nifty, Screen screen) {
        this.nifty = nifty;
        this.screen = screen;
        System.out.println("Bind succesfull");
    }

    @NiftyEventSubscriber(id="chat")
    public void sendMessage(final String id, final ChatTextSendEvent event) {
        client.writeMessage(event.getText());
    }
}
