package com.testanywhere.core.utilities.map;

import com.testanywhere.core.utilities.logging.LogConfiguration;
import org.apache.log4j.Logger;

import java.util.*;

public class MapUtils
{
    public static Logger logger;

    static
    {
        MapUtils.logger = Logger.getLogger("MapUtils");
        LogConfiguration.configure();
    }

    public static <K, V extends Comparable<? super V>> Map<K, V> sortByValue( final Map<K, V> map )
    {
        List<Map.Entry<K, V>> list =
            new LinkedList<>( map.entrySet() );
        Collections.sort( list, new Comparator<Map.Entry<K, V>>()
        {
            public int compare( Map.Entry<K, V> o1, Map.Entry<K, V> o2 )
            {
                return (o1.getValue()).compareTo( o2.getValue() );
            }
        } );

        Map<K, V> result = new LinkedHashMap<>();
        for (Map.Entry<K, V> entry : list)
        {
            result.put( entry.getKey(), entry.getValue() );
        }
        return result;
    }

    private MapUtils() {}
}