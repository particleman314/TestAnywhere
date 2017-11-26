package com.nimsoft.managers;

import com.testanywhere.core.utilities.exceptions.ObjectCreationException;

public class ManagerException extends ObjectCreationException
{
    private static final String DEFAULT_MESSAGE = "Unable to create manager requested";

    public ManagerException()
    {
        this(ManagerException.DEFAULT_MESSAGE);
    }

    public ManagerException( final Class<?> triedClass )
    {
        this(ManagerException.DEFAULT_MESSAGE + " : type --> " + triedClass.getSimpleName());
    }

    public ManagerException( final String msg )
    {
        super(msg);
    }

    public ManagerException( final Throwable cause )
    {
        super(cause);
    }

    public ManagerException( final String msg, final Throwable cause )
    {
        super(msg, cause);
    }
}
