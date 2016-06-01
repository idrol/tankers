package net.tankers.entity;

import net.tankers.server.EntityUserData;
import net.tankers.server.Match;
import net.tankers.server.Server;
import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.dynamics.*;
import org.newdawn.slick.Color;
import org.newdawn.slick.opengl.TextureLoader;
import org.newdawn.slick.util.ResourceLoader;

import java.io.IOException;
import java.util.Set;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL11.glPopMatrix;

/**
 * Created by local-admin on 30-05-2016.
 */
public class NonCollidable extends NetworkedGameEntity {
    public NonCollidable(Integer instanceID) {
        super(instanceID);
    }

    public NonCollidable(Server server, Match match){
        super(server, match);
    }

    @Override
    public void setup(World world, Set<Body> bodies) {
        BodyDef boxDef = new BodyDef();
        boxDef.position.set(Match.toMeters(x), Match.toMeters(y));
        boxDef.type = BodyType.STATIC;
        PolygonShape boxShape = new PolygonShape();
        boxShape.setAsBox(Match.toMeters(sizeX)/2, Match.toMeters(sizeY)/2);
        Body box = world.createBody(boxDef);
        FixtureDef boxFixture = new FixtureDef();
        boxFixture.density = 1f;
        boxFixture.shape = boxShape;
        boxFixture.userData = new EntityUserData(this);
        boxFixture.filter.groupIndex = -1;
        box.createFixture(boxFixture);
        body = box;
        bodies.add(box);
    }

    @Override
    public void render() {
        Color.white.bind();
        glDisable(GL_TEXTURE_GEN_S);
        glDisable(GL_TEXTURE_GEN_T);
        if(texture == null) {
            try {
                texture = TextureLoader.getTexture("PNG", ResourceLoader.getResourceAsStream("bg.png"));
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
