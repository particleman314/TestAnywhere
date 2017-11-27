package com.testanywhere.core.machines.exceptions;

import com.testanywhere.core.utilities.exceptions.ObjectCreationException;

public class ActionException extends ObjectCreationException
{
    private static final String DEFAULT_MESSAGE = "Unable to properly execute requested action";

    public ActionException()
    {
        this(ActionException.DEFAULT_MESSAGE);
    }

    public ActionException( Class<?> triedClass )
    {
        this(ActionException.DEFAULT_MESSAGE + " : types --> " + triedClass.getSimpleName());
    }

    public ActionException( String msg )
    {
        super(msg);
    }

    public ActionException( Throwable cause )
    {
        super(cause);
    }

    public ActionException( String msg, Throwable cause )
    {
        super(msg, cause);
    }
}
