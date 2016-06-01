package net.tankers.main.screenControllers;

import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.NiftyEventSubscriber;
import de.lessvoid.nifty.controls.ButtonClickedEvent;
import de.lessvoid.nifty.controls.Label;
import de.lessvoid.nifty.controls.TextField;
import de.lessvoid.nifty.screen.DefaultScreenController;
import de.lessvoid.nifty.screen.Screen;
import net.tankers.client.Client;
import net.tankers.main.Game;

/**
 * Created by idrol on 13-04-2016.
 */
public class LobbyController extends DefaultScreenController {
    private Nifty nifty;
    private Screen screen;

    @Override
    public void bind(Nifty nifty, Screen screen) {
        this.nifty = nifty;
        this.screen = screen;
        System.out.println("Bind succesfull - LobbyController");
    }

    @Override
    public void onStartScreen() {
        screen.findNiftyControl("username", Label.class).setText("Logged in as '" + Client.username+"'");
        System.out.println("Client username: " + Client.username);
        screen.findNiftyControl("played", Label.class).setText("Matches played: " + Client.totalMatches);
        screen.findNiftyControl("won", Label.class).setText("Matches won: " + Client.wonMatches);
        screen.findElementById("cancelsearch").setVisible(false);
        screen.findNiftyControl("gamesearchlabel", Label.class).setText("");
    }

    @NiftyEventSubscriber(id="search-match")
    public void searchmatch(final String id, final ButtonClickedEvent event){
        screen.findNiftyControl("gamesearchlabel", Label.class).setText("Searching for a match..");
        screen.findElementById("cancelsearch").setVisible(true);
    	Client.writeMessage("search_match");
    }

    @NiftyEventSubscriber(id="logout")
    public void logout(final String id, final ButtonClickedEvent event) {
        screen.findElementById("cancelsearch").setVisible(false);
        screen.findNiftyControl("gamesearchlabel", Label.class).setText("");
        nifty.getScreen("login").findNiftyControl("username", TextField.class).setText("");
        nifty.gotoScreen("start");
        Client.username = "";
        Client.writeMessage("cancel_search");
        Client.writeMessage("logout");
    }

    @NiftyEventSubscriber(id="cancelsearch")
    public void cancelsearch(final String id, final ButtonClickedEvent event) {
        screen.findElementById("cancelsearch").setVisible(false);
        screen.findNiftyControl("gamesearchlabel", Label.class).setText("");
        Client.writeMessage("cancel_search");
    }
}
