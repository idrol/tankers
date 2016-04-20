package net.tankers.main;

import net.tankers.main.screenControllers.MainScreenController;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

public class MainMenu extends GameState{

	@Override
	public void update(float delta) {
        if(nifty.update()){
            isRunning = false;
        }

		
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
		try {
			nifty.fromXml("main-screen", new FileInputStream(Main.pathPrefix+"mainMenu.nifty"), "start", new MainScreenController());
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		nifty.gotoScreen("start");

	}

}
