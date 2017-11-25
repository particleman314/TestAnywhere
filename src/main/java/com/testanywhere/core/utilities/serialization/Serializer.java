package com.testanywhere.core.utilities.serialization;

import com.rits.cloning.Cloner;
import com.testanywhere.core.utilities.logging.LogConfiguration;
import org.apache.log4j.Logger;

public abstract class Serializer
{
    protected static Logger logger;
    protected static final Cloner cloneStd;
    //protected static Cloner cloneShrd;

    static
    {
        Serializer.logger = Logger.getLogger("Serializer");
        LogConfiguration.configure();

        cloneStd = Cloner.standard();
        //cloneShrd = Cloner.shared();

        cloneStd.setCloningEnabled(true);
        //cloneShrd.setCloningEnabled(true);
    }

    public String serialize()
    {
        return Serialization.serialize(this);
    }

    public<T> T deserialize( final String strData, Class<T> clazz )
    {
        return Serialization.deserialize(strData, clazz);
    }
}
