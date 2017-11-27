package com.testanywhere.core.machines.exceptions;

import com.testanywhere.core.utilities.exceptions.ObjectCreationException;

public class FSException extends ObjectCreationException
{
	private static final String DEFAULT_MESSAGE = "Unable to manage FileSystem Object";

	public FSException()
	{
		this(FSException.DEFAULT_MESSAGE);
	}

	public FSException(Class<?> triedClass)
	{
		this(FSException.DEFAULT_MESSAGE + " : type --> " + triedClass.getSimpleName());
	}

	public FSException(String msg)
	{
		super(msg);
	}

	public FSException(Throwable cause)
	{
		super(cause);
	}

	public FSException(String msg, Throwable cause)
	{
		super(msg, cause);
	}
}
