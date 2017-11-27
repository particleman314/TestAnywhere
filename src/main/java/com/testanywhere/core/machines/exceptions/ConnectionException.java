package com.testanywhere.core.machines.exceptions;

import com.testanywhere.core.utilities.exceptions.ObjectCreationException;

public class ConnectionException extends ObjectCreationException
{
	private static final String DEFAULT_MESSAGE = "Unable to generate connection for requested machine type";

	public ConnectionException()
	{
		this(ConnectionException.DEFAULT_MESSAGE);
	}

	public ConnectionException( Class<?> triedClass )
	{
		this(ConnectionException.DEFAULT_MESSAGE + " : types --> " + triedClass.getSimpleName());
	}

	public ConnectionException( String msg )
	{
		super(msg);
	}

	public ConnectionException( Throwable cause )
	{
		super(cause);
	}

	public ConnectionException( String msg, Throwable cause )
	{
		super(msg, cause);
	}
}
