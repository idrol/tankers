package net.tankers.main.screenControllers;

import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.screen.Screen;
import net.tankers.client.Client;
import net.tankers.map.DefaultMap;
import net.tankers.map.Map;
import org.lwjgl.input.Keyboard;

/**
 * Created by Adrian on 21-05-2016.
 */
public class GameController extends RenderableScreenController {
    private Nifty nifty;
    private Map map;

    @Override
    public void bind(Nifty nifty, Screen screen) {
        this.nifty = nifty;
        map = new DefaultMap();
        map.init();

    }

    @Override
    public void render() {
        map.render();
    }

    @Override
    public void update(float delta) {
        if(Keyboard.isKeyDown(Keyboard.KEY_W)){
            if(!Keyboard.isKeyDown(Keyboard.KEY_S)){
                Client.writeMessage("key_pressed;key_w");
            }
        }else{
            if(Keyboard.isKeyDown(Keyboard.KEY_S)){
                Client.writeMessage("key_pressed;key_s");
            }
        }

        if(Keyboard.isKeyDown(Keyboard.KEY_A)){
            if(!Keyboard.isKeyDown(Keyboard.KEY_D)){
                Client.writeMessage("key_pressed;key_a");
            }
        }else{
            if(Keyboard.isKeyDown(Keyboard.KEY_D)){
                Client.writeMessage("key_pressed;key_d");
            }
        }
    }
}
