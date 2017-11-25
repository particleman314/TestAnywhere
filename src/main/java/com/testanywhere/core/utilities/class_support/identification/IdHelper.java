package com.testanywhere.core.utilities.class_support.identification;

import com.testanywhere.core.utilities.class_support.Cast;
import com.testanywhere.core.utilities.logging.OutputDisplay;
import com.testanywhere.core.utilities.logging.TextManager;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.TreeMap;

public class IdHelper
{
    private static final Map<String, Collection<Long>> objectStorage = new TreeMap<>();

    public static<T extends OutputDisplay> void account( final T obj )
    {
        if ( obj == null ) return;
        Class<?> clazz = obj.getClass();
        Long id = obj.id();

        if ( IdHelper.objectStorage.isEmpty() || ! IdHelper.objectStorage.containsKey(clazz.getSimpleName()) )
        {
            Collection<Long> ids = new ArrayList<>();
            ids.add(id);
            IdHelper.objectStorage.put(clazz.getSimpleName(), ids);
        }
        else
        {
            ArrayList<Long> ids = Cast.cast(IdHelper.objectStorage.get(clazz.getSimpleName()));
            if ( !(ids != null ? ids.contains(id) : false) )
                ids.add(id);
        }
    }

    public static String showObjectCreationMap()
    {
        StringBuilder sb = new StringBuilder();
        sb.append("Object Storage Unit :" + TextManager.EOL);
        for ( String s : IdHelper.objectStorage.keySet() )
        {
            String plurality = "object";
            int numberItems = IdHelper.objectStorage.get(s).size();
            if ( numberItems > 1 ) plurality = plurality + "s";

            sb.append("Object Type --> [ " + s + " ] has " + numberItems+ " " + plurality + " created" + TextManager.EOL);
        }

        return sb.toString();
    }

    private IdHelper()
    {}
}
