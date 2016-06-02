package net.tankers.main.screenControllers;


import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.NiftyEventSubscriber;
import de.lessvoid.nifty.controls.ButtonClickedEvent;
import de.lessvoid.nifty.controls.Label;
import de.lessvoid.nifty.controls.TextField;
import de.lessvoid.nifty.screen.DefaultScreenController;
import de.lessvoid.nifty.screen.Screen;
import net.tankers.client.Client;
import net.tankers.main.AdminPanelInfo;

public class AdminController extends DefaultScreenController {
    private Nifty nifty = null;
    private Screen screen = null;

    @Override
    public void bind(Nifty nifty, Screen screen) {
        this.nifty = nifty;
        this.screen = screen;
        System.out.println("Bind succesfull");
    }

    @Override
    public void onStartScreen() {
        if(AdminPanelInfo.getAvgSessionTime() != null) {
            screen.findNiftyControl("avgsessiontime", Label.class).setText("Average session time: " + AdminPanelInfo.getAvgSessionTime());
        }

        if(AdminPanelInfo.getAvgMatchTime() != null) {
            screen.findNiftyControl("avgmatchtime", Label.class).setText("Average match time: " + AdminPanelInfo.getAvgMatchTime());
        }

        if(AdminPanelInfo.getTotalMatches() != null) {
            screen.findNiftyControl("totalmatches", Label.class).setText("Total played matches: " + AdminPanelInfo.getTotalMatches());
        }

        if(AdminPanelInfo.getUserNumber() != null) {
            screen.findNiftyControl("totalusernumber", Label.class).setText("Number of users: " + AdminPanelInfo.getUserNumber());
        }

    }

    @NiftyEventSubscriber(id="logout")
    public void logout(final String id, final ButtonClickedEvent event) {
        nifty.getScreen("login").findNiftyControl("username", TextField.class).setText("");
        nifty.gotoScreen("start");
        Client.username = "";
        Client.writeMessage("logout");
    }
}
