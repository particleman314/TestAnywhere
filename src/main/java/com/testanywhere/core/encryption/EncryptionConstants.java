package com.testanywhere.core.encryption;

import com.testanywhere.core.utilities.class_support.functional_support.ConstantsInterface;

public class EncryptionConstants implements ConstantsInterface
{
	public static final String encryptionPrefix = "ENC{";
	public static final String encryptionSuffix = "}";

	public static final String CIPHER_CONNECTOR = "/";

	public static final String CIPHERMAP_NAMEFIELD = "name";
	public static final String CIPHERMAP_TYPEFIELD = "type";
	public static final String CIPHERMAP_PADFIELD  = "padding";

	public static final String NULL_ENCRYPTION_ID = "Null";

	public static final int MINIMUM_KEY_BITSIZE = 256;
	public static final int MINIMUM_KEY_BYTESIZE = MINIMUM_KEY_BITSIZE / 8;

	// This ensures it is initialized when import by any class
	static
	{
		EncryptionConstants.getInstance();
	}

	private static boolean isInitialized = false;

	public static EncryptionConstants getInstance()
    {
    	if ( ! EncryptionConstants.isInitialized ) 
    	{
    		EncryptionConstants.isInitialized = true;
    		EncryptionConstants.initialize();
    	}
    	return EncryptionConstantsHolder.INSTANCE;
    }

	@Override
	public void reset()
	{
		EncryptionConstants.initialize();
	}

	private static class EncryptionConstantsHolder
	{ 
    	public static final EncryptionConstants INSTANCE = new EncryptionConstants();
    }

	@SuppressWarnings("EmptyMethod")
	private static void initialize()
	{}

	private EncryptionConstants() {}
}
