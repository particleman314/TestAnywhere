package com.testanywhere.core.machines.exceptions;

import com.testanywhere.core.utilities.exceptions.ObjectCreationException;

public class MachineGenerationException extends ObjectCreationException {

	private static final String DEFAULT_MESSAGE = "Unable to create access to machine requested";

	public MachineGenerationException()
	{
		this(MachineGenerationException.DEFAULT_MESSAGE);
	}

	public MachineGenerationException( Class<?> triedClass )
	{
		this(MachineGenerationException.DEFAULT_MESSAGE + " : type --> " + triedClass.getSimpleName());
	}

	public MachineGenerationException( String msg )
	{
		super(msg);
	}

	public MachineGenerationException( Throwable cause )
	{
		super(cause);
	}

	public MachineGenerationException( String msg, Throwable cause )
	{
		super(msg, cause);
	}
}
