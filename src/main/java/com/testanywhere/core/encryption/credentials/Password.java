package com.testanywhere.core.encryption.credentials;

import com.testanywhere.core.encryption.EncryptionConstants;
import com.testanywhere.core.encryption.utilities.EncryptionUtils;
import com.testanywhere.core.encryption.exceptions.DecryptionException;
import com.testanywhere.core.encryption.plugins.encryption.EncryptionStrategy;
import com.testanywhere.core.utilities.exceptions.ObjectCreationException;
import com.testanywhere.core.utilities.logging.*;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.*;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.regex.Pattern;

public class Password extends OutputDisplay
{
	private String password;
	private EncryptionStrategy encMethod;

	// Need to use full specification for cryptor to properly disambiguate like methods with different parameters
	public Password(String pass, EncryptionStrategy cryptor, boolean isEncrypted ) throws InvalidKeyException,
	                                                                                     NoSuchAlgorithmException,
	                                                                                     NoSuchPaddingException,
	                                                                                     IllegalBlockSizeException,
	                                                                                     BadPaddingException,
			DecryptionException,
	                                                                                     InvalidAlgorithmParameterException
	{
		super();
		this.__initialize();
		if ( cryptor == null )
			// This allows for specification of name/type/padding used by encryption methods
			cryptor = EncryptionUtils.getEncryptionMethod(EncryptionUtils.DEFAULT_ENCRYPTION_SPECIFICATION);
		else
			this.encMethod = cryptor;
		this.__validate(pass, cryptor, isEncrypted);
	}

	public Password( String pass, boolean isEncrypted ) throws InvalidKeyException,
	                                                           NoSuchAlgorithmException,
	                                                           NoSuchPaddingException,
	                                                           IllegalBlockSizeException,
	                                                           BadPaddingException,
	                                                           DecryptionException,
	                                                           InvalidAlgorithmParameterException
	{
		this(pass, null, isEncrypted);
	}

	public Password( final String pass ) throws InvalidKeyException,
                                          NoSuchAlgorithmException,
                                          NoSuchPaddingException,
                                          IllegalBlockSizeException,
                                          BadPaddingException,
                                          DecryptionException,
                                          InvalidAlgorithmParameterException
	{
		super();
		this.__initialize();
		boolean isEncrypted = EncryptionUtils.isEncrypted(pass);

		EncryptionStrategy cryptor = EncryptionUtils.getEncryptionMethod(EncryptionUtils.DEFAULT_ENCRYPTION_SPECIFICATION);
		this.__validate(pass, cryptor, isEncrypted);
	}

	public Password ( final Password pwd ) throws ObjectCreationException
	{
		super();
		if ( pwd == null ) throw new ObjectCreationException(Password.class);

		this.password = pwd.password;
		this.encMethod = copy(pwd.encMethod);
	}

	@Override
	public void buildObjectOutput(int numTabs)
	{
		if ( numTabs < 0 ) numTabs = 0;
		Tabbing tabEnvironment = new Tabbing(numTabs);
		DisplayManager dm = this.getDM();

		String outerSpacer = tabEnvironment.getSpacer();
		dm.append(outerSpacer + "Password :", DisplayType.TEXTTYPES.LABEL);

		tabEnvironment.increment();
		String innerSpacer = tabEnvironment.getSpacer();
		dm.append(innerSpacer + "Encrypted : " + this.getEncoded());
		if ( this.encMethod != null )
		{
			this.encMethod.buildObjectOutput(tabEnvironment.numberTabs());
			dm.addFormatLines(this.encMethod.getDM().getLines());
		}
	}

	@Override
	public String toString()
	{
		return this.getEncoded();
	}

	@Override
	public boolean isNull()
	{
		return false;
	}

	public String getEncoded() {
		if ( TextManager.validString(this.password) )
		{
			String emType = (this.encMethod != null) ? this.encMethod.buildCipher() : EncryptionUtils.DEFAULT_ENCRYPTION_SPECIFICATION;
			return Password.encode(this.password, emType);
		}
		return null;
	}

	public String getDecoded()
	{
		return this.password;
	}

	protected String get() {
		return this.getEncoded();
	}

	public void put( String pass, String cryptor ) {
		if ( ! TextManager.validString(pass) ) return;

		if ( EncryptionUtils.isEncrypted(pass) ) {
			String cryptorMethodToUse = this.__determineEncryptionMethod(cryptor);
			if (TextManager.validString(cryptorMethodToUse))
				this.password = Password.decode(pass, EncryptionUtils.determineEncryptionType(cryptorMethodToUse));
			else
				this.password = pass;
		}
		else
			this.password = pass;

		if ( TextManager.validString(cryptor) )
		{
			if ( this.encMethod != null )
			{
				if ( ! cryptor.equals(this.encMethod.buildCipher()) )
					this.setCryptor(cryptor);
			}
			else
				this.setCryptor(cryptor);
		}
	}

	public void setCryptor( String cryptorID )
	{
		EncryptionStrategy ecm = EncryptionUtils.getEncryptionMethod(cryptorID);
		if ( ecm != null ) this.encMethod = ecm;
	}

	public static String decode( String input )
	{
		return Password.decode(input, null);
	}

	public static String decode( String input, String encryption )
	{
		EncryptionStrategy encMethod;

		if ( ! TextManager.validString(encryption) )
		{
			encryption = EncryptionUtils.DEFAULT_ENCRYPTION_METHOD;
			encMethod  = EncryptionUtils.getEncryptionMethod(encryption);
		}
		else
			encMethod = EncryptionUtils.getEncryptionMethod(encryption);

		if ( encMethod == null ) return input;
		if ( ! input.startsWith(EncryptionConstants.encryptionPrefix) ) return input;
		if ( ! input.endsWith(EncryptionConstants.encryptionSuffix)) return input;

		String buffer = Password.removeInputEncoding(input);

		try
		{
			return encMethod.decode(buffer);
		}
		catch (InvalidKeyException | NoSuchAlgorithmException
				| NoSuchPaddingException | IllegalBlockSizeException
				| BadPaddingException | InvalidAlgorithmParameterException | DecryptionException e) {
			return input;
		}
	}

	public static String encode( String input )
	{
		return Password.encode(input, null);
	}

	public static String encode( String input, String encryption )
	{
		EncryptionStrategy encMethod;

		if ( ! TextManager.validString(encryption) )
		{
			encryption = EncryptionUtils.DEFAULT_ENCRYPTION_METHOD;
			encMethod  = EncryptionUtils.getEncryptionMethod(encryption);
		}
		else
			encMethod = EncryptionUtils.getEncryptionMethod(encryption);

		if ( encMethod == null ) return input;
		if ( input.startsWith(EncryptionConstants.encryptionPrefix) ) return input;
		if ( input.endsWith(EncryptionConstants.encryptionSuffix)) return input;

		try
		{
			return encMethod.encode(input);
		} 
		catch (InvalidKeyException | NoSuchAlgorithmException
				| NoSuchPaddingException | IllegalBlockSizeException
				| BadPaddingException | InvalidAlgorithmParameterException | UnsupportedEncodingException e)
		{
			return input;
		}
	}

	private static String removeInputEncoding(String pass)
	{
		String buffer = pass;
		buffer = buffer.replaceFirst(Pattern.quote(EncryptionConstants.encryptionPrefix), "");
		buffer = buffer.replaceFirst(Pattern.quote(EncryptionConstants.encryptionSuffix), "");
		return buffer;
	}

	private String __determineEncryptionMethod( String cryptorRequested )
	{
		if ( TextManager.validString(cryptorRequested) )
		{
			EncryptionStrategy em = EncryptionUtils.getEncryptionMethod(cryptorRequested);
			if (em == null) cryptorRequested = null;
		}
		else
		if ( this.encMethod != null )
			cryptorRequested = this.encMethod.buildCipher();
		else
			cryptorRequested = null;

		return cryptorRequested;
	}

	private void __validate(String pass, EncryptionStrategy cryptor, boolean isEncrypted) throws InvalidKeyException,
                                                                                               NoSuchAlgorithmException,
                                                                                               NoSuchPaddingException, 
                                                                                               IllegalBlockSizeException, 
                                                                                               BadPaddingException, 
                                                                                               InvalidAlgorithmParameterException,
                                                                                               DecryptionException
	{
		if ( isEncrypted )
		{
			pass = removeInputEncoding(pass);
			this.password = cryptor.decode(pass);
		}
		else
			this.password = pass;
	}

	private void __initialize()
	{
		this.password = null;
		this.encMethod = null;
	}
}
