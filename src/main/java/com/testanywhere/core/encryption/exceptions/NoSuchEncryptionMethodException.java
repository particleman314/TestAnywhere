package com.testanywhere.core.encryption.exceptions;

import com.testanywhere.core.utilities.exceptions.ObjectCreationException;

public class NoSuchEncryptionMethodException extends ObjectCreationException
{
	private static final String DEFAULT_MESSAGE = "Unable to find requested decryption method";

	public NoSuchEncryptionMethodException()
	{
		this(NoSuchEncryptionMethodException.DEFAULT_MESSAGE);
	}

	public NoSuchEncryptionMethodException(Class<?> triedClass)
	{
		this(NoSuchEncryptionMethodException.DEFAULT_MESSAGE + " : type --> " + triedClass.getSimpleName());
	}

	public NoSuchEncryptionMethodException(String msg)
	{
		super(msg);
	}

	public NoSuchEncryptionMethodException(Throwable cause)
	{
		super(cause);
	}

	public NoSuchEncryptionMethodException(String msg, Throwable cause)
	{
		super(msg, cause);
	}
}
