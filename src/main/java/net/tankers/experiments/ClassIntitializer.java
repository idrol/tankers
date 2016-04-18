package net.tankers.experiments;

import net.tankers.entity.NetworkedEntity;
import net.tankers.entity.Tank;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

/**
 * Created by idrol on 16-04-2016.
 */
public class ClassIntitializer {

    public static void main(String[] args) throws NoSuchMethodException, ClassNotFoundException, IllegalAccessException, InvocationTargetException, InstantiationException {
        Class<?> clazz = Class.forName(Tank.class.getName());
        Constructor<?> ctor = clazz.getConstructor(Boolean.class, Integer.class);
        Object object = ctor.newInstance(new Object[]{false, 1});
        if(object instanceof NetworkedEntity) {
            System.out.println("Worked");
        }else{
            System.err.println("Something went wrong");
        }
    }
}
