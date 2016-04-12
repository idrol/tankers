package net.tankers.main.screenControllers;

import de.lessvoid.nifty.NiftyEventSubscriber;
import de.lessvoid.nifty.controls.ButtonClickedEvent;
import de.lessvoid.nifty.screen.DefaultScreenController;

public class MainScreenController extends DefaultScreenController {
	@NiftyEventSubscriber(id="exit")
    public void exit(final String id, final ButtonClickedEvent event) {
		nifty.exit();
    }
}