package com.testanywhere.core.utilities.reflection;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class ClassLoaderUtils {

    /**
     * @param className a fully qualified class name (e.g. org.dspace.MyClass)
     * @return the Class if it can be found in the context or current {@link ClassLoader} OR null if no class can be found
     */
    public static Class<?> getClassFromString(String className)
    {
        Class<?> c = null;
        try
        {
            ClassLoader cl = ClassLoaderUtils.getCurrentClassLoader();
            c = Class.forName(className, true, cl);
        }
        catch (ClassNotFoundException e)
        {
            try
            {
                ClassLoader cl = ClassLoaderUtils.class.getClassLoader();
                c = Class.forName(className, true, cl);
            }
            catch (ClassNotFoundException e1)
            {
                try
                {
                    c = Class.forName(className);
                }
                catch (ClassNotFoundException e2)
                {
                    c = null;
                }
            }
        }
        return c;
    }

    /**
     * @return the current context {@link ClassLoader} or the nearest thing that can be found
     */
    public static ClassLoader getCurrentClassLoader()
    {
        ClassLoader cl = Thread.currentThread().getContextClassLoader();
        if (cl == null) cl = ClassLoaderUtils.class.getClassLoader();
        return cl;
    }


    /**
     * Returns a list of all superclasses and implemented interfaces by the supplied class,
     * recursively to the base, up to but excluding Object.class. These will be listed in order from
     * the supplied class, all concrete superclasses in ascending order, and then finally all
     * interfaces in recursive ascending order.<br/>
     * This will include duplicates if any superclasses implement the same classes 
     * 
     * Taken from PonderUtilCore around version 1.2.2
     *  - Antranig Basman (antranig@caret.cam.ac.uk)
     */
    public static Collection<Class<?>> getSuperclasses(Class<?> clazz)
    {
        List<Class<?>> accumulate = new ArrayList<>();
        while (!clazz.equals(Object.class))
        {
            accumulate.add(clazz);
            clazz = clazz.getSuperclass();
        }
        int supers = accumulate.size();
        for (int i = 0; i < supers; ++i)
        {
            ClassLoaderUtils.appendSuperclasses(accumulate.get(i), accumulate);
        }
        return accumulate;
    }

    /**
     * Taken from PonderUtilCore around version 1.2.2
     *  - Antranig Basman (antranig@caret.cam.ac.uk)
     */
    private static void appendSuperclasses(Class<?> clazz, List<Class<?>> accrete)
    {
        Class<?>[] interfaces = clazz.getInterfaces();
        for (int i = 0; i < interfaces.length; ++i)
        {
            accrete.add(interfaces[i]);
        }
        for (int i = 0; i < interfaces.length; ++i)
        {
            ClassLoaderUtils.appendSuperclasses(interfaces[i], accrete);
        }
    }


    /**
     * Finds a class type that is in the containing collection,
     * will always return something (failsafe to Object.class)
     * @param collection any collection (List, Set, etc.)
     * @return the class type contained in this collecion
     */
    @SuppressWarnings("unchecked")
    public static Class<?> getClassFromCollection(Collection<?> collection)
    {
        // try to get the type of entities out of this collection
        Class<?> c = Object.class;
        if (collection != null)
        {
            if (! collection.isEmpty())
            {
                c = collection.iterator().next().getClass();
            }
            else {
                // this always gets Object.class -AZ
                //c = collection.toArray().getClass().getComponentType();
                c = Object.class;
            }
        }
        return c;
    }
}
