package com.testanywhere.core.encryption.exceptions;

import com.testanywhere.core.utilities.exceptions.ObjectCreationException;

public class DecryptionException extends ObjectCreationException
{
	private static final String DEFAULT_MESSAGE = "Unable to decrypt requested information";

	public DecryptionException()
	{
		this(DecryptionException.DEFAULT_MESSAGE);
	}

	public DecryptionException( Class<?> triedClass )
	{
		this(DecryptionException.DEFAULT_MESSAGE + " : type --> " + triedClass.getSimpleName());
	}

	public DecryptionException( String msg )
	{
		super(msg);
	}

	public DecryptionException( Throwable cause )
	{
		super(cause);
	}

	public DecryptionException( String msg, Throwable cause )
	{
		super(msg, cause);
	}
}
