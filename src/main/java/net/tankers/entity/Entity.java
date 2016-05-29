package net.tankers.entity;

import net.tankers.server.EntityUserData;
import net.tankers.server.Match;
import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.dynamics.*;

import java.util.Set;

import static org.lwjgl.opengl.GL11.*;

/**
 * Created by idrol on 13-04-2016.
 */
public class Entity {
    public static final int GENERIC_ENTITY_SENSOR = 1;
    public static final int SHELL_SENSOR = 2;
    public static final int TANK_SENSOR = 3;
    protected int x, y;
    protected int rotZ = 0;
    protected int sizeX, sizeY;
    private float r = 1f, g = 0, b = 0;
    protected Body body;

    public Entity setPos(int x, int y) {
        this.x = x;
        this.y = y;
        return this;
    }

    public Entity setSize(int sizeX, int sizeY){
        this.sizeX = sizeX;
        this.sizeY = sizeY;
        return this;
    }

    public void wasHitByShell(Shell shell) {

    }

    public Entity setRot(int z) {
        rotZ = z;
        return this;
    }

    public Entity setColor(float r, float g, float b) {
        this.r = r;
        this.g = g;
        this.b = b;
        return this;
    }

    public void destroy(){
        body.getWorld().destroyBody(body);
    }

    public void setup(World world, Set<Body> bodies) {
        BodyDef boxDef = new BodyDef();
        boxDef.position.set(Match.toMeters(x), Match.toMeters(y));
        boxDef.type = BodyType.DYNAMIC;
        PolygonShape boxShape = new PolygonShape();
        boxShape.setAsBox(Match.toMeters(sizeX)/2, Match.toMeters(sizeY)/2);
        Body box = world.createBody(boxDef);
        FixtureDef boxFixture = new FixtureDef();
        boxFixture.density = 1f;
        boxFixture.shape = boxShape;
        boxFixture.userData = new EntityUserData(this);
        box.createFixture(boxFixture);
        body = box;
        bodies.add(box);
    }

    public void render() {
        //System.out.println("Entity def render" + x + ", " + y + ", " + sizeX + ", " + sizeY);
        glPushMatrix();
            glTranslatef(x, y, 0);
            glRotatef(rotZ, 0, 0, 1);
            glBegin(GL_QUADS);
                glColor3f(r, g, b);
                glVertex2i(-sizeX/2, -sizeY/2);
                glVertex2i(-sizeX/2, sizeY/2);
                glVertex2i(sizeX/2, sizeY/2);
                glVertex2i(sizeX/2, -sizeY/2);
            glEnd();
        glPopMatrix();
    }
}
