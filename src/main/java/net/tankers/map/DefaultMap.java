package net.tankers.map;

import net.tankers.entity.HiddenWall;
import net.tankers.entity.NonCollidable;
import net.tankers.entity.Wall;
import net.tankers.server.Match;
import net.tankers.server.Server;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.World;

import java.util.Set;

/**
 * Created by Adrian on 22-05-2016.
 */
public class DefaultMap extends Map {

    public DefaultMap(World world, Set<Body> bodies) {
        super(world, bodies);
    }

    @Override
    public void init(Server server, Match match) {
        HiddenWall container_wall_1 = new HiddenWall(server, match);
        container_wall_1.setPos(-5, 768/2);
        container_wall_1.setSize(10, 768);
        addObject(container_wall_1);

        HiddenWall container_wall_2 = new HiddenWall(server, match);
        container_wall_2.setPos(1371, 768/2);
        container_wall_2.setSize(10, 768);
        addObject(container_wall_2);

        HiddenWall container_wall_3 = new HiddenWall(server, match);
        container_wall_3.setPos(1366/2, -5);
        container_wall_3.setSize(1366, 10);
        addObject(container_wall_3);

        HiddenWall container_wall_4 = new HiddenWall(server, match);
        container_wall_4.setPos(1366/2, 768+5);
        container_wall_4.setSize(1366, 10);
        addObject(container_wall_4);

        //NonCollidable floor = new NonCollidable(server, match);
        //floor.setPos((int)(1366f/2f), (int)(768f/2f));
        //floor.setSize(1366, 768);
        //addObject(floor);

        Wall wall1 = new Wall(server, match);
        wall1.setPos(200-5, (768/2));
        wall1.setSize(10, 400);
        addObject(wall1);

        Wall wall2 = new Wall(server, match);
        wall2.setPos(1166-5, (768/2));
        wall2.setSize(10, 400);
        addObject(wall2);
    }

}
