package com.nimsoft.class_support.functional_support;

import com.nimsoft.classes.Pair;
import com.nimsoft.logging.LogConfiguration;
import com.nimsoft.logging.TextManager;
import com.nimsoft.reflection.ReflectUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.*;

public class MethodFunctions
{
    public static Logger logger;

    static
    {
        MethodFunctions.logger = Logger.getLogger("MethodFunctions");
        LogConfiguration.configure();
    }

    public static Collection<String> getFieldNames(Collection<Field> fields)
    {
        Collection<String> allFieldNames = new ArrayList<>();
        if ( fields == null || fields.isEmpty() ) return allFieldNames;

        for ( Field f : fields )
        {
            String fn = f.getName();
            if ( allFieldNames.contains(fn) ) continue;

            int fieldModifier = f.getModifiers();

            if ( Modifier.isStatic(fieldModifier) || f.isSynthetic() ) continue;
            MethodFunctions.logger.debug(TextManager.specializeName(fn) + " added to field list...");
            allFieldNames.add(fn);
        }

        return allFieldNames;
    }

    public static Collection<Field> selectFields(Collection<Field> fields, Collection<String> selectedFields)
    {
        Collection<Field> returnedFields = new ArrayList<>();
        if ( fields == null || fields.isEmpty() ) return returnedFields;

        if( selectedFields == null || selectedFields.isEmpty() )
        {
            returnedFields.addAll(fields);
            return returnedFields;
        }

        for ( Field f : fields )
        {
            if ( selectedFields.contains(f.getName()) )
                returnedFields.add(f);
        }

        return returnedFields;
    }

    public static<T> Collection<Field> getObjectFields(T obj)
    {
        return MethodFunctions.getObjectFields(obj, null);
    }

    public static<T> Map<String, Pair<Field, Object>> decomposeObject(T obj, Collection<String> skippedFieldIDs)
    {
        return MethodFunctions.decomposeObject(obj, skippedFieldIDs, true);
    }

    public static<T> Map<String, Pair<Field, Object>> decomposeObject(T obj, Collection<String> skippedFieldIDs, boolean saveFieldObjects )
    {
        Map<String, Pair<Field, Object>> decomposition = new TreeMap<>();

        if ( obj == null )
        {
            MethodFunctions.logger.log(Level.ALL, "Object provided for field discovery is a primitive null type or has no fields");
            return decomposition;
        }

        Class<?> tmp = obj.getClass();
        Object value;

        do {
            Collection<Field> allFields = new ArrayList<>();
            Collection<Field> publicFields = Arrays.asList(tmp.getFields());
            Collection<Field> nonpublicFields = Arrays.asList(tmp.getDeclaredFields());

            allFields = CollectionUtils.union(allFields, publicFields);
            allFields = CollectionUtils.union(allFields, nonpublicFields);

            for ( Field f : allFields )
            {
                if ( Modifier.isFinal(f.getModifiers()) ) continue;
                String fName = f.getName();
                if ( skippedFieldIDs != null && skippedFieldIDs.contains(fName) ) continue;
                Class<?> owner = f.getDeclaringClass();
                if ( saveFieldObjects ) {
                    value = ReflectUtils.getInstance().getFieldValue(obj, fName);
                    decomposition.put(owner.getName() + TextManager.STR_OUTPUTSEPARATOR + fName, new Pair<>(f, value));
                }
                else
                    decomposition.put(owner.getName() + TextManager.STR_OUTPUTSEPARATOR + fName, new Pair<>(f, null));
            }

            tmp = tmp.getSuperclass();
        } while (tmp != null);

        return decomposition;
    }

    public static<T> Collection<Field> getObjectFields(T obj, Collection<String> skippedFieldIDs )
    {
        Map<String, Pair<Field, Object>> decomposition = MethodFunctions.decomposeObject(obj, skippedFieldIDs);
        Collection<Field> fields = new ArrayList<>();

        for ( String fs : decomposition.keySet() )
            fields.add(decomposition.get(fs).getL());

        return fields;
    }

    private MethodFunctions() {}
}
