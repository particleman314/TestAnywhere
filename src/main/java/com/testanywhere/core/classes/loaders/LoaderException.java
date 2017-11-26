package com.testanywhere.core.classes.loaders;

import com.testanywhere.core.utilities.exceptions.ObjectCreationException;

public class LoaderException extends ObjectCreationException
{
    private static final String DEFAULT_MESSAGE = "Unable to create class loader requested";

    public LoaderException()
    {
        this(LoaderException.DEFAULT_MESSAGE);
    }

    public LoaderException( final Class<?> triedClass )
    {
        this(LoaderException.DEFAULT_MESSAGE + " : type --> " + triedClass.getSimpleName());
    }

    public LoaderException( final String msg )
    {
        super(msg);
    }

    public LoaderException( final Throwable cause )
    {
        super(cause);
    }

    public LoaderException( final String msg, final Throwable cause )
    {
        super(msg, cause);
    }
}
