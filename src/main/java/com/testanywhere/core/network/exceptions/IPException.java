package com.testanywhere.core.network.exceptions;

import com.testanywhere.core.utilities.exceptions.ObjectCreationException;

public class IPException extends ObjectCreationException
{
    private static final String DEFAULT_MESSAGE = "Unable to use requested IP";

    public IPException()
    {
        this(IPException.DEFAULT_MESSAGE);
    }

    public IPException(final Class<?> triedClass)
    {
        this(IPException.DEFAULT_MESSAGE + " : type --> " + triedClass.getSimpleName());
    }

    public IPException(final String msg)
    {
        super(msg);
    }

    public IPException(final Throwable cause)
    {
        super(cause);
    }

    public IPException(final String msg, final Throwable cause)
    {
        super(msg, cause);
    }
}
