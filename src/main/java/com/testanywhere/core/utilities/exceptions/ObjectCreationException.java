package com.testanywhere.core.utilities.exceptions;


public class ObjectCreationException extends RuntimeException
{
    private static final String DEFAULT_MESSAGE = "Unable to create object requested";

    public ObjectCreationException()
    {
        this(ObjectCreationException.DEFAULT_MESSAGE);
    }

    public ObjectCreationException( final Class<?> triedClass )
    {
        this(ObjectCreationException.DEFAULT_MESSAGE + " : type --> " + triedClass.getSimpleName());
    }

    public ObjectCreationException( final String msg )
    {
        super(msg);
    }

    public ObjectCreationException( final Throwable cause )
    {
        super(cause);
    }

    public ObjectCreationException( final String msg, final Throwable cause )
    {
        super(msg, cause);
    }
}
