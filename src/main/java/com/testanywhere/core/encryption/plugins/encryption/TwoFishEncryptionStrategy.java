package com.testanywhere.core.encryption.plugins.encryption;

import com.owtelse.codec.Base64;
import com.testanywhere.core.encryption.exceptions.DecryptionException;
import com.testanywhere.core.encryption.utilities.EncryptionUtils;

import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

public class TwoFishEncryptionStrategy extends EncryptionStrategy
{
	public TwoFishEncryptionStrategy(String eType, String ePaddingMethod )
	{
		super();
		this.encryptType = eType;
		this.encryptPadding = ePaddingMethod;
		
		this.setup(this.getClass().getSimpleName());
	}

	@Override
	public void buildObjectOutput(int numTabs)
	{}

	@Override
	public String encode(String input) throws NoSuchAlgorithmException,
	                                          NoSuchPaddingException,
	                                          InvalidKeyException,
	                                          IllegalBlockSizeException,
	                                          BadPaddingException,
			                                  InvalidAlgorithmParameterException,
			                                  UnsupportedEncodingException
	{
		if ( ! this.isPrepared() ) this.prepare();
		if ( ! this.isPrepared() ) throw new NoSuchAlgorithmException();

		Cipher cipher = Cipher.getInstance(this.buildCipher());
		cipher.init(Cipher.ENCRYPT_MODE, this.secretKey, new IvParameterSpec(this.ivBytes));
			
		byte[] encryptedInput = cipher.doFinal(input.getBytes());
		
		return this.__finalizeEncryption(Base64.encode(encryptedInput));
	}

	@Override
	public String decode(final String input) throws NoSuchAlgorithmException,
	                                          NoSuchPaddingException,
	                                          InvalidKeyException,
	                                          IllegalBlockSizeException,
	                                          BadPaddingException,
			DecryptionException, InvalidAlgorithmParameterException
	{
		if ( ! this.isPrepared() ) this.prepare();
		if ( ! this.isPrepared() ) throw new NoSuchAlgorithmException();
		
		String in = input;
		in = this.__prepareDecryption(in);
		
		Cipher cipher = Cipher.getInstance(this.buildCipher());
	    cipher.init(Cipher.DECRYPT_MODE, this.secretKey, new IvParameterSpec(this.ivBytes));
	    byte[] decrypted;
		try 
		{
			decrypted = cipher.doFinal(Base64.decode(in));
		} 
		catch ( UnsupportedEncodingException e)
		{
			this.error(e.getLocalizedMessage());
			throw new DecryptionException(e.getLocalizedMessage(), e.getCause());
		}
	  
	    return new String(decrypted);
	}

	@Override
	public void prepare() 
	{
		try
		{
			SecureRandom sr = null;
			String key = this.key;
		
			if ( key == null )
			{
				key = new String(EncryptionUtils.getRandomBytes(this.getKeySize()));
				this.key = key;
				sr = new SecureRandom(key.getBytes());
			}
		
			KeyGenerator keygen = this.keyGen;
			if ( keygen == null )
				keygen = KeyGenerator.getInstance(this.getEncryptName().toLowerCase());

			this.keyGen = keygen;
		
			SecretKey sk = this.secretKey;
			if ( sk == null )
				sk = keygen.generateKey();

			keygen.init(sr);
			this.secretKey = sk;
		
			byte[] ivSpec = this.ivBytes;
			if ( ivSpec == null )
				ivSpec = EncryptionUtils.getRandomBytes(16);
			
			this.ivBytes = ivSpec;
			this.prepared = true;
		}
		catch ( NoSuchAlgorithmException e )
		{
			this.prepared = false;
		}
	}
}
