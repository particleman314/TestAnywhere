package com.testanywhere.core.encryption.plugins.encryption;

import com.testanywhere.core.utilities.class_support.BaseClass;
import com.testanywhere.core.encryption.EncryptionConstants;
import com.testanywhere.core.encryption.utilities.EncryptionUtils;
import com.testanywhere.core.classes.support.FactoryType;
import com.testanywhere.core.encryption.exceptions.DecryptionException;
import com.testanywhere.core.utilities.logging.DisplayManager;
import com.testanywhere.core.utilities.logging.DisplayType;
import com.testanywhere.core.utilities.logging.OutputDisplay;
import com.testanywhere.core.utilities.logging.Tabbing;

import javax.crypto.*;
import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.regex.Pattern;

import static com.testanywhere.core.utilities.logging.TextManager.validString;

public abstract class EncryptionStrategy extends OutputDisplay implements FactoryType
{
	protected String encryptName;
	protected String encryptType;
	protected String encryptPadding;
	protected int keySize;
	
	protected String key;
	protected byte[] ivBytes;
	
	protected KeyGenerator keyGen;
	protected SecretKey secretKey;
	
	protected boolean prepared;
	
	protected EncryptionStrategy()
	{
		super();
		this.initialize();
	}
	
	public abstract void prepare();
	
	public abstract String encode( String input ) throws NoSuchAlgorithmException,
	                                                     NoSuchPaddingException,
	                                                     InvalidKeyException,
	                                                     IllegalBlockSizeException,
	                                                     BadPaddingException,
			                                             InvalidAlgorithmParameterException,
		                                                 UnsupportedEncodingException;
	public abstract String decode( String input ) throws NoSuchAlgorithmException,
	                                                     NoSuchPaddingException,
	                                                     InvalidKeyException,
	                                                     IllegalBlockSizeException,
	                                                     BadPaddingException,
			DecryptionException, InvalidAlgorithmParameterException;

	@Override
	public boolean isCopyable()
	{
		return false;
	}

	@Override
	public boolean isAssignable()
	{
		return false;
	}

	@Override
	public void buildObjectOutput(int numTabs)
	{
		if ( numTabs < 0 ) numTabs = 0;
		Tabbing tabEnvironment = new Tabbing(numTabs);
		DisplayManager dm = this.getDM();

		String outerSpacer = tabEnvironment.getSpacer();
		dm.append(outerSpacer + "EncryptionMethod :", DisplayType.TEXTTYPES.LABEL);

		tabEnvironment.increment();
		String innerSpacer = tabEnvironment.getSpacer();

		dm.append(innerSpacer + "Name : " + BaseClass.checkIsNull(this.encryptName));
		dm.append(innerSpacer + "Type : " + BaseClass.checkIsNull(this.encryptType));
		dm.append(innerSpacer + "Padding : " + BaseClass.checkIsNull(this.encryptPadding));
		dm.append(innerSpacer + "Key size : " + BaseClass.checkIsNull(this.getKeySize()));
		dm.append(innerSpacer + "Key : " + BaseClass.checkIsNull(this.key));
	}

	@Override
	public boolean isNull() {
		return false;
	}

	public String getName() { return this.encryptName; }

	public boolean isPrepared()
	{
		return this.prepared;
	}
	
	public int getKeySize()
	{
		if ( this.keySize <= 0 ) this.setKeySize(this.keySize);
		return this.keySize;
	}
	
	public void setBitSize( Integer bitSize )
	{
		if ( bitSize == null || bitSize <= 0 ) this.keySize = EncryptionConstants.MINIMUM_KEY_BYTESIZE;
		else this.keySize = bitSize / 8;
	}
	
	public void setInitVectorBytes( byte[] iv )
	{
		this.ivBytes = iv;
	}
	
	public void setKeySize( Integer keySize )
	{
		if ( keySize == null || keySize <= 0 ) keySize = EncryptionConstants.MINIMUM_KEY_BYTESIZE;
		if ( keySize != this.keySize ) {
			this.keySize = keySize;
			this.prepare();
		}
	}

	protected String __finalizeEncryption( String input )
	{
		if ( ! validString(input) ) return input;
		return EncryptionConstants.encryptionPrefix + input + EncryptionConstants.encryptionSuffix;
	}
	
	protected String __prepareDecryption( String input )
	{
		if ( ! validString(input) ) return input;
		input = input.replaceFirst(Pattern.quote(EncryptionConstants.encryptionPrefix), "");
		input = input.replaceFirst(Pattern.quote(EncryptionConstants.encryptionSuffix), "");
		return input;
	}
	
	protected void setup( String clazzName )
	{
		this.encryptName = EncryptionUtils.determineEncryptionType(clazzName);
	}
	
	public String buildCipher()
	{
		return EncryptionUtils.buildCipher(this.getEncryptName(), this.getEncryptType(), this.getEncryptPadding());
	}

	public String getEncryptType()
	{
		if ( ! validString(this.encryptType) ) return EncryptionConstants.NULL_ENCRYPTION_ID;
		return this.encryptType;
	}

	public String getEncryptName()
	{
		return this.encryptName;
	}

	public String getEncryptPadding()
	{
		return this.encryptPadding;
	}

	protected byte[] getIVSpecification()
	{
		return this.ivBytes;
	}
	
	private void initialize()
	{
		this.keySize = 0;
		this.encryptName = null;
		this.encryptType = null;
		this.encryptPadding = null;
		
		this.key = null;
		this.keyGen = null;
		this.secretKey = null;
	}
}
