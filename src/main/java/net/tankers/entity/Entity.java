package net.tankers.entity;

import static org.lwjgl.opengl.GL11.*;

/**
 * Created by idrol on 13-04-2016.
 */
public class Entity {

    protected int x, y;
    protected int sizeX, sizeY;
    private float r = 1f, g = 0, b = 0;

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

    public Entity setColor(float r, float g, float b) {
        this.r = r;
        this.g = g;
        this.b = b;
        return this;
    }

    public void render() {
        //System.out.println("Entity def render" + x + ", " + y + ", " + sizeX + ", " + sizeY);
        glPushMatrix();
            glBegin(GL_QUADS);
                glColor3f(r, g, b);
                glVertex2i(x, y);
                glVertex2i(x, y+sizeY);
                glVertex2i(x+sizeX, y+sizeY);
                glVertex2i(x+sizeX, y);
            glEnd();
        glPopMatrix();
    }
}
