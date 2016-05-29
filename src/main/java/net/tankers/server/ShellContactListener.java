package net.tankers.server;

import net.tankers.entity.Entity;
import net.tankers.entity.Shell;
import net.tankers.entity.Tank;
import org.jbox2d.callbacks.ContactImpulse;
import org.jbox2d.callbacks.ContactListener;
import org.jbox2d.collision.Manifold;
import org.jbox2d.dynamics.Fixture;
import org.jbox2d.dynamics.contacts.Contact;

/**
 * Created by local-admin on 29-05-2016.
 */
public class ShellContactListener implements ContactListener {

    public Fixture getShellFixture(Fixture fixture1, Fixture fixture2) {
        if(fixture1.getUserData() != null && fixture1.getUserData() instanceof EntityUserData && ((EntityUserData) fixture1.getUserData()).sensor_id == Entity.SHELL_SENSOR){
            return fixture1;
        }else if(fixture2.getUserData() != null && fixture2.getUserData() instanceof EntityUserData && ((EntityUserData) fixture2.getUserData()).sensor_id == Entity.SHELL_SENSOR){
            return fixture2;
        }
        return null;
    }

    public Fixture getTankFixture(Fixture fixture1, Fixture fixture2){
        if(fixture1.getUserData() != null && fixture1.getUserData() instanceof EntityUserData && ((EntityUserData) fixture1.getUserData()).sensor_id == Entity.TANK_SENSOR){
            return fixture1;
        }else if(fixture2.getUserData() != null && fixture2.getUserData() instanceof EntityUserData && ((EntityUserData) fixture2.getUserData()).sensor_id == Entity.TANK_SENSOR){
            return fixture2;
        }
        return null;
    }

    public boolean bothShell(Fixture fixture1, Fixture fixture2) {
        if(fixture1.getUserData() != null && fixture1.getUserData() instanceof EntityUserData && ((EntityUserData) fixture1.getUserData()).sensor_id == Entity.SHELL_SENSOR){
            if(fixture2.getUserData() != null && fixture2.getUserData() instanceof EntityUserData && ((EntityUserData) fixture2.getUserData()).sensor_id == Entity.SHELL_SENSOR){
                return true;
            }
        }
        return false;
    }

    @Override
    public void beginContact(Contact contact) {
        System.out.println("A contact was registered");
        System.out.println("The hit contained these 2 fixtures with userData " + contact.getFixtureA().getUserData() + ", " + contact.getFixtureB().getUserData());
        if(bothShell(contact.getFixtureA(), contact.getFixtureB())){
            // Shells colliding
            Shell shell1 = (Shell)((EntityUserData)contact.getFixtureA().getUserData()).entity;
            Shell shell2 = (Shell)((EntityUserData)contact.getFixtureB().getUserData()).entity;
            shell1.shouldDie = true;
            shell2.shouldDie = true;
        }else{
            // 1 Shell colliding with another jbox2d object
            Fixture shellFixture = getShellFixture(contact.getFixtureA(), contact.getFixtureB());
            System.out.println("Shell fixture retrieved was " + shellFixture);
            if(shellFixture != null){
                System.out.println("A shell collided");
                Fixture tankFixture = getTankFixture(contact.getFixtureA(), contact.getFixtureB());
                if(tankFixture != null){
                    // A tank was hit;
                    System.out.println("O holy moly a tank was hit");
                    Tank tank = ((Tank)((EntityUserData)tankFixture.getUserData()).entity);
                    tank.wasHitByShell(((Shell)((EntityUserData)shellFixture.getUserData()).entity));
                }
                Shell shell = (Shell)((EntityUserData)shellFixture.getUserData()).entity;
                shell.shouldDie = true;
            }
        }
    }

    @Override
    public void endContact(Contact contact) {

    }

    @Override
    public void preSolve(Contact contact, Manifold oldManifold) {

    }

    @Override
    public void postSolve(Contact contact, ContactImpulse impulse) {

    }
}
