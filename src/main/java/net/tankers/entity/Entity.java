package net.tankers.entity;

import static org.lwjgl.opengl.GL11.*;

/**
 * Created by idrol on 13-04-2016.
 */
public class Entity {

    protected int x, y;
    protected float rotZ = 0.5f;
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
            glTranslatef(x, y, 0);
            glBegin(GL_QUADS);
                glColor3f(r, g, b);
                glRotatef(rotZ, 0, 0, 1);
                glVertex2i(0, 0);
                glVertex2i(0, sizeY);
                glVertex2i(sizeX, sizeY);
                glVertex2i(sizeX, 0);
            glEnd();
        glPopMatrix();
    }
}
