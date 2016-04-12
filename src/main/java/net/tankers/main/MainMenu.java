package net.tankers.main;

import net.tankers.main.screenControllers.MainScreenController;

public class MainMenu extends GameState{

	@Override
	public void update(float delta) {
		isRunning = !nifty.update();
		
	}

	@Override
	public void render() {
		nifty.render(false);
	}

	@Override
	public void cleanUp() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void init() {
		nifty.loadStyleFile("nifty-default-styles.xml");
		nifty.loadControlFile("nifty-default-controls.xml");
		nifty.fromXml("main-screen", this.getClass().getResourceAsStream("mainMenu.nifty"), "start", new MainScreenController());
		nifty.gotoScreen("start");
	}

}
