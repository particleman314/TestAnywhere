package com.testanywhere.core.utilities.serialization;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.testanywhere.core.utilities.class_support.BaseClass;
import com.testanywhere.core.utilities.class_support.Cast;
import com.testanywhere.core.utilities.classes.Pair;
import com.testanywhere.core.utilities.logging.LogConfiguration;
import com.testanywhere.core.utilities.logging.TextManager;
import com.owtelse.codec.Base64;
import org.apache.log4j.Logger;

import java.io.*;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.TreeMap;

public class Serialization
{
    public static Logger logger;

    static
    {
        Serialization.logger = Logger.getLogger("Serialization");
        LogConfiguration.configure();
    }

    public static boolean isSerializable( final Object o )
    {
        Method[] methods = o.getClass().getDeclaredMethods();

        Map<String, Pair<String, Boolean>> MRT = new TreeMap<>();

        Serialization.logger.info("Looking for 'serialize' and 'deserialize' methods for class " + TextManager.specializeName(o.getClass().getSimpleName()));
        MRT.put("serialize", new Pair<>("String", false));
        MRT.put("deserialize", new Pair<>(o.getClass().getSimpleName(), false));

        for ( Method m : methods )
        {
            if ( "serialize".equals(m.getName()) || "deserialize".equals(m.getName()) )
            {
                if ( m.getReturnType().getName().contains(MRT.get(m.getName()).first()) )
                {
                    MRT.get(m.getName()).setR(true);
                }
            }
        }

        return MRT.get("serialize").getR() && MRT.get("deserialize").getR();
    }

    private static<T> T __deserialize_via_java( final String data )
    {
        T fromStr = null;

        if ( !TextManager.validString(data) ) return fromStr;
        try {
            ByteArrayInputStream bios = new ByteArrayInputStream(Base64.decode(data));
            ObjectInputStream ois = new ObjectInputStream(bios);
            fromStr = Cast.cast(ois.readObject());

            BaseClass testCast = (BaseClass) fromStr;
            if ( testCast != null && ! testCast.isSerializable() )
            {
                Serialization.logger.warn("Unable to deserialize object " + TextManager.specializeName(fromStr.getClass().getName()));
                return null;
            }

            ois.close();
            return fromStr;
        }
        catch ( Exception e )
        {
            Serialization.logger.error(e.getLocalizedMessage());
            return fromStr;
        }
    }

    private static<T> T __deserialize_via_jackson( final String data, final Class<T> clazz ) throws IOException
    {
        ObjectMapper mapper = new ObjectMapper();
        return Cast.cast(mapper.readValue(data, clazz));
    }

    public static<T> T deserialize( final String data, Class<T> clazz )
    {
        //try
        //{
            return Serialization.__deserialize_via_java(data);
        //}
        //catch (IOException e)
        //{
        //    Serialization.logger.error("Unable to deserialize requested object " + clazz);
        //    return null;
        //}
    }

    private static<T> String __serialize_via_java( final T o ) {
        if (o instanceof BaseClass) {
            BaseClass testCast = (BaseClass) o;
            if (!testCast.isSerializable()) {
                Serialization.logger.warn("Unable to serialize object " + TextManager.specializeName(o.getClass().getName()));
                return null;
            }
        }

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos;
        try
        {
            oos = new ObjectOutputStream(baos);
            oos.writeObject(o);
            oos.flush();
            oos.close();
        }
        catch ( Exception e )
        {
            Serialization.logger.warn("Unable to serialize object", e);
            return null;
        }


        try
        {
            return Base64.encode(baos.toByteArray());
        }
        catch ( UnsupportedEncodingException e)
        {
            Serialization.logger.warn("Unable to serialize object " + TextManager.specializeName(o.toString()) );
            return null;
        }
    }

    private static<T> String __serialize_via_jackson( final T o ) throws JsonProcessingException
    {
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        return mapper.writeValueAsString(o);
    }

    public static<T> String serialize( final T o )
    {
        //try
        //{
            return Serialization.__serialize_via_java(o);
        //}
        //catch (IOException e)
        //{
        //    Serialization.logger.error("Unable to serialize requested object " + o.getClass() + " due to " + e.getLocalizedMessage());
        //    return null;
        //}
    }

    private Serialization() {}
}
