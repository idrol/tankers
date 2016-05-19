package net.tankers.server;

import net.tankers.entity.Player;

/**
 * Created by local-admin on 19-05-2016.
 */
public class MatchHandler {

    public static void handleMessage(Match match, Player player, String data){
        match.receivedMessage(data, player);
    }

    public static boolean validateData(Player player, Match match){
        return match.hasPlayer(player);
    }
}
