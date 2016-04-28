package net.tankers.main;

import net.tankers.client.Client;
import net.tankers.main.screenControllers.LobbyController;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

/**
 * Created by idrol on 13-04-2016.
 */
public class Game {

    private LobbyController lobbyController = null;
    private Client client = null;
    private String username;
    private String password;
    
    public Game(String username, String password) {
        this.username = username;
        this.password = password;
    }
}
