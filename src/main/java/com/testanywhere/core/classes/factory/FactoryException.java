package com.testanywhere.core.classes.factory;

import com.testanywhere.core.utilities.exceptions.ObjectCreationException;

public class FactoryException extends ObjectCreationException
{
	private static final String DEFAULT_MESSAGE = "Unable to create factory requested";

	public FactoryException()
	{
		this(FactoryException.DEFAULT_MESSAGE);
	}

	public FactoryException( final Class<?> triedClass )
	{
		this(FactoryException.DEFAULT_MESSAGE + " : type --> " + triedClass.getSimpleName());
	}

	public FactoryException( final String msg )
	{
		super(msg);
	}

	public FactoryException( final Throwable cause )
	{
		super(cause);
	}

	public FactoryException( final String msg, final Throwable cause )
	{
		super(msg, cause);
	}
}
