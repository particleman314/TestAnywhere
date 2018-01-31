package com.testanywhere.core.classes.test.identification;

import java.util.HashMap;
import java.util.Map;

public class TestIdGenerator
{
    private static Map<Class<?>, Integer> idmap = new HashMap<>();

    static {
        TestIdGenerator.idmap.clear();
    }

    public static synchronized boolean contains(TestDesignation td) {
        if ( td == null || td.getClassType() == null ) { return false; }
        if ( idmap.containsKey(td.getClassType()) ) { return true; }
        return false;
    }

    public static synchronized int generate(TestDesignation td) {
        if ( td == null ) { return -1; }

        Class<?> clazz = td.getClassType();

        if ( clazz == null ) { return -1; }

        int currentID = 1;
        if ( idmap.containsKey(clazz) ) {
            currentID = idmap.get(clazz) + 1;
        }
        idmap.put(clazz, currentID);

        return currentID;
    }
}
