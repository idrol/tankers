package net.tankers.main.screenControllers;

import de.lessvoid.nifty.NiftyEventSubscriber;
import de.lessvoid.nifty.controls.ButtonClickedEvent;
import de.lessvoid.nifty.screen.DefaultScreenController;
import net.tankers.main.Game;
import net.tankers.main.Main;

public class MainScreenController extends DefaultScreenController {
	@NiftyEventSubscriber(id="StartButton")
    public void StartButton(final String id, final ButtonClickedEvent event) {
        System.out.println("Button was clicked");
        Main.switchState(new Game());
    }
}