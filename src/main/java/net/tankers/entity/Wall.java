package net.tankers.entity;

import net.tankers.server.Match;
import net.tankers.server.Server;
import org.newdawn.slick.Color;
import org.newdawn.slick.opengl.TextureLoader;
import org.newdawn.slick.util.ResourceLoader;

import java.io.IOException;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL11.glPopMatrix;

/**
 * Created by local-admin on 30-05-2016.
 */
public class Wall extends NetworkedGameEntity {

    public Wall(Integer instanceID) {
        super(instanceID);
    }

    public Wall(Server server, Match match) {
        super(server, match);
    }

    @Override
    public void render() {
        Color.white.bind();
        glDisable(GL_TEXTURE_GEN_S);
        glDisable(GL_TEXTURE_GEN_T);
        if(texture == null) {
            try {
                texture = TextureLoader.getTexture("PNG", ResourceLoader.getResourceAsStream("block.png"));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        texture.bind();
        glPushMatrix();
            glTranslatef(x, y, 0);
            glRotatef(rotZ, 0, 0, 1);
            glBegin(GL_QUADS);
                glTexCoord2f(1f,0f);
                glVertex2i(-sizeX/2, -sizeY/2);
                glTexCoord2f(1f,0f);
                glVertex2i(-sizeX/2, sizeY/2);
                glTexCoord2f(1f,1f);
                glVertex2i(sizeX/2, sizeY/2);
                glTexCoord2f(1f,1f);
                glVertex2i(sizeX/2, -sizeY/2);
            glEnd();
        glPopMatrix();
    }

}
