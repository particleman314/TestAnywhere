package com.testanywhere.core.classes.support.version;

import com.testanywhere.core.utilities.exceptions.ObjectCreationException;

public class VersionException extends ObjectCreationException
{
	private static final String DEFAULT_MESSAGE = "Unable to create version object requested";

	public VersionException()
	{
		this(VersionException.DEFAULT_MESSAGE);
	}

	public VersionException( final Class<?> triedClass )
	{
		this(VersionException.DEFAULT_MESSAGE + " : type --> " + triedClass.getSimpleName());
	}

	public VersionException( final String msg )
	{
		super(msg);
	}

	public VersionException( final Throwable cause )
	{
		super(cause);
	}

	public VersionException( final String msg, final Throwable cause )
	{
		super(msg, cause);
	}
}
