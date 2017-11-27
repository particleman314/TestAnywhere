package com.testanywhere.core.machines.exceptions;

public class NoActionsInListException extends Exception
{
	private static final String DEFAULT_MESSAGE = "No actions defined in list";

	public NoActionsInListException()
	{
		this(NoActionsInListException.DEFAULT_MESSAGE);
	}

	public NoActionsInListException(Class<?> triedClass )
	{
		this(NoActionsInListException.DEFAULT_MESSAGE + " : types --> " + triedClass.getSimpleName());
	}

	public NoActionsInListException(String msg )
	{
		super(msg);
	}

	public NoActionsInListException(Throwable cause )
	{
		super(cause);
	}

	public NoActionsInListException(String msg, Throwable cause )
	{
		super(msg, cause);
	}
}
