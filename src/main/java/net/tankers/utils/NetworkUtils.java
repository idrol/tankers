package net.tankers.utils;

import net.tankers.entity.NetworkedEntity;

/**
 * Created by idrol on 18-04-2016.
 */
public final class NetworkUtils {

    public static final int CREATE = 1;
    public static final int UPDATE = 2;
    public static final int DELETE = 3;
    public static final String ENDING = "\r\n";

    public static String encodeBase(NetworkedEntity entity, int action) {
        String base = "object;";
        base += entity.getClass().getName()+":";
        base += entity.getInstanceID()+":";
        if(action == CREATE){
            base += "create";
        }else if(action == UPDATE) {
            base += "update:";
        }else if(action == DELETE) {
            base += "delete";
        }
        return base;
    }

}
