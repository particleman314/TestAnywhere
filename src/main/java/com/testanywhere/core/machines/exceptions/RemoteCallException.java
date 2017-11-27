package com.testanywhere.core.machines.exceptions;

import com.testanywhere.core.utilities.exceptions.ObjectCreationException;

public class RemoteCallException extends ObjectCreationException
{
    private static final String DEFAULT_MESSAGE = "Unable to generate connection for requested machine type";

    public RemoteCallException()
    {
        this(RemoteCallException.DEFAULT_MESSAGE);
    }

    public RemoteCallException( Class<?> triedClass )
    {
        this(RemoteCallException.DEFAULT_MESSAGE + " : types --> " + triedClass.getSimpleName());
    }

    public RemoteCallException( String msg )
    {
        super(msg);
    }

    public RemoteCallException( Throwable cause )
    {
        super(cause);
    }

    public RemoteCallException( String msg, Throwable cause )
    {
        super(msg, cause);
    }
}