package net.tankers.main;

import de.lessvoid.nifty.controls.Chat;
import de.lessvoid.nifty.screen.Screen;
import net.tankers.client.Client;
import net.tankers.entity.Player;
import net.tankers.main.screenControllers.LobbyController;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

/**
 * Created by idrol on 13-04-2016.
 */
public class Game extends GameState {

    private LobbyController lobbyController = null;
    private Client client = null;
    private String username;
    private String password;

    public Game(String username, String password) {
        this.username = username;
        this.password = password;
    }

    @Override
    public void init() {
        nifty.loadStyleFile("nifty-default-styles.xml");
        nifty.loadControlFile("nifty-default-controls.xml");
        client = new Client("localhost", 25565, this, username, password);
        client.run();
        lobbyController = new LobbyController(this);
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
