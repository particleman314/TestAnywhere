package com.testanywhere.core.machines.exceptions;

import com.testanywhere.core.utilities.exceptions.ObjectCreationException;

public class LoginException extends ObjectCreationException
{
    private static final String DEFAULT_MESSAGE = "Unable to login to requested machine";

    public LoginException()
    {
        this(LoginException.DEFAULT_MESSAGE);
    }

    public LoginException(Class<?> triedClass )
    {
        this(LoginException.DEFAULT_MESSAGE + " : types --> " + triedClass.getSimpleName());
    }

    public LoginException(String msg )
    {
        super(msg);
    }

    public LoginException(Throwable cause )
    {
        super(cause);
    }

    public LoginException(String msg, Throwable cause )
    {
        super(msg, cause);
    }

}
