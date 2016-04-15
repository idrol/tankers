package net.tankers.main;

import net.tankers.client.Client;
import net.tankers.main.screenControllers.LobbyController;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

/**
 * Created by idrol on 13-04-2016.
 */
public class Lobby extends GameState {

    private LobbyController lobbyController = null;
    private Client client = null;

    @Override
    public void init() {
        nifty.loadStyleFile("nifty-default-styles.xml");
        nifty.loadControlFile("nifty-default-controls.xml");
        client = new Client("localhost", 25565);
        client.run();
        lobbyController = new LobbyController();
        lobbyController.setClient(client);
        try {
            nifty.fromXml("main-screen", new FileInputStream(Main.pathPrefix+"lobby.nifty"), "lobby", lobbyController);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        nifty.gotoScreen("lobby");

    }

    @Override
    public void update(float delta) {
        if(nifty.update()){
            isRunning = false;
        }
    }

    @Override
    public void render()  {
        nifty.render(false);
    }

    @Override
    public void cleanUp() {
        client.stop();
    }
}
