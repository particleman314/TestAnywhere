package com.testanywhere.core.encryption.utilities;

import com.testanywhere.core.utilities.class_support.Cast;
import com.testanywhere.core.classes.class_support.CompartmentObject;
import com.testanywhere.core.encryption.credentials.Password;
import com.testanywhere.core.encryption.credentials.Credentials;
import com.testanywhere.core.encryption.exceptions.DecryptionException;
import com.testanywhere.core.encryption.managers.EncryptionManager;
import com.testanywhere.core.encryption.plugins.encryption.EncryptionStrategy;
import com.testanywhere.core.encryption.plugins.encryption.NullEncryptionStrategy;
import com.testanywhere.core.utilities.logging.TextManager;
import com.testanywhere.core.classes.managers.Registrar;
import com.testanywhere.core.classes.utilities.ClassUtils;
import com.testanywhere.core.encryption.EncryptionConstants;

import org.apache.commons.lang3.StringUtils;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.*;
import java.util.regex.Pattern;

public class EncryptionUtils 
{
	public final static String STRATEGYNAME = "EncryptionStrategy";

	public static String DEFAULT_ENCRYPTION_METHOD = EncryptionUtils.makeEncryptionMethodName(EncryptionConstants.NULL_ENCRYPTION_ID);
	public static String DEFAULT_ENCRYPTION_SPECIFICATION = EncryptionUtils.buildCipher(EncryptionConstants.NULL_ENCRYPTION_ID, null, null);

	public static byte[] getRandomBytes( Integer size )
	{
		if ( size <= 0 ) return new byte[0];
		byte[] rndbytes;

		rndbytes = new byte[size];
		
		SecureRandom sr = new SecureRandom();
		sr.nextBytes(rndbytes);
		
		return rndbytes;
	}

	public static boolean isEncrypted( String input )
	{
		return input.startsWith(EncryptionConstants.encryptionPrefix) && input.endsWith(EncryptionConstants.encryptionSuffix);
	}

	public static String buildCipher( String name, String type, String padding )
	{
		if ( ! TextManager.validString(name) ) return EncryptionConstants.NULL_ENCRYPTION_ID;
		List<String> sb = new LinkedList<>();

		sb.add(name);
		if ( TextManager.validString(type))
			sb.add(type);

		if ( TextManager.validString(padding))
			sb.add(padding);
		
		String[] conversion = new String[sb.size()];
		return StringUtils.join(sb.toArray(conversion), EncryptionConstants.CIPHER_CONNECTOR);
	}

	public static String buildCipher( Map<String, String> cipherInfoMap )
	{
		if ( cipherInfoMap == null ) return EncryptionUtils.buildCipher(EncryptionConstants.NULL_ENCRYPTION_ID, null, null);

		String name = EncryptionConstants.NULL_ENCRYPTION_ID;
		if ( cipherInfoMap.containsKey(EncryptionConstants.CIPHERMAP_NAMEFIELD) )
			name = cipherInfoMap.get(EncryptionConstants.CIPHERMAP_NAMEFIELD);

		String type = null;
		if ( cipherInfoMap.containsKey(EncryptionConstants.CIPHERMAP_TYPEFIELD) )
			type = cipherInfoMap.get(EncryptionConstants.CIPHERMAP_TYPEFIELD);

		String pad = cipherInfoMap.get(EncryptionConstants.CIPHERMAP_PADFIELD);

		return EncryptionUtils.buildCipher(name, type, pad);
	}

	public static Map<String, String> decomposeCipher( String cipherID )
	{
		Map<String, String> cipherInfoMap = new HashMap<>();
		
		if ( ! TextManager.validString(cipherID) )
		{
			cipherInfoMap.put(EncryptionConstants.CIPHERMAP_NAMEFIELD, EncryptionConstants.NULL_ENCRYPTION_ID);
			return cipherInfoMap;
		}

		String[] comps = cipherID.split(EncryptionConstants.CIPHER_CONNECTOR);

		if ( comps.length == 1 )
			cipherInfoMap.put(EncryptionConstants.CIPHERMAP_NAMEFIELD, cipherID);
		else
		{
			if (comps.length >= 1) cipherInfoMap.put(EncryptionConstants.CIPHERMAP_NAMEFIELD, comps[0]);
			if (comps.length >= 2) cipherInfoMap.put(EncryptionConstants.CIPHERMAP_TYPEFIELD, comps[1]);
			if (comps.length >= 3) cipherInfoMap.put(EncryptionConstants.CIPHERMAP_PADFIELD, comps[2]);
		}
		return cipherInfoMap;
	}

	// Need to use buildCipher to allow for variations in same encryption algorithm
	public static void setDefaultEncryptionMethod( String nameSpecification )
	{
		Map<String, String> cipherInfoMap = EncryptionUtils.decomposeCipher(nameSpecification);
		String name = cipherInfoMap.get(EncryptionConstants.CIPHERMAP_NAMEFIELD);
		if ( ! TextManager.validString(name) )
		{
			EncryptionUtils.DEFAULT_ENCRYPTION_METHOD = EncryptionUtils.makeEncryptionMethodName(name);
			return;
		}

		name = EncryptionUtils.determineEncryptionType(name);
		//EncryptionUtils.DEFAULT_ENCRYPTION_METHOD = EncryptionUtils.makeEncryptionMethodName(name);
		EncryptionUtils.DEFAULT_ENCRYPTION_METHOD = name;

		if ( "".equals(EncryptionUtils.DEFAULT_ENCRYPTION_METHOD) )
			EncryptionUtils.DEFAULT_ENCRYPTION_METHOD = EncryptionUtils.makeEncryptionMethodName(EncryptionConstants.NULL_ENCRYPTION_ID);

		String type = null;
		if ( cipherInfoMap.containsKey(EncryptionConstants.CIPHERMAP_TYPEFIELD) )
			type = cipherInfoMap.get(EncryptionConstants.CIPHERMAP_TYPEFIELD);

		String pad = null;
		if ( cipherInfoMap.containsKey(EncryptionConstants.CIPHERMAP_PADFIELD) )
			pad = cipherInfoMap.get(EncryptionConstants.CIPHERMAP_PADFIELD);

		EncryptionUtils.DEFAULT_ENCRYPTION_SPECIFICATION = EncryptionUtils.buildCipher(name, type, pad);
	}

	public static String getDefaultEncryptionMethod()
	{
		return EncryptionUtils.determineEncryptionType(EncryptionUtils.DEFAULT_ENCRYPTION_METHOD);
	}

	public static String makeEncryptionMethodName( String type )
	{
		if ( ! TextManager.validString(type) ) return "";
		type = EncryptionUtils.determineEncryptionType(type);
		return type + EncryptionUtils.STRATEGYNAME;
	}

	public static String determineEncryptionType( String encryptorName )
	{
		if ( ! TextManager.validString(encryptorName) ) return "";
		return encryptorName.replaceFirst(Pattern.quote(EncryptionUtils.STRATEGYNAME), "");
	}

	public static EncryptionStrategy getEncryptionMethod(String nameSpecification )
	{
		EncryptionStrategy cryptor = new NullEncryptionStrategy();

		if ( ! TextManager.validString(nameSpecification) ) return cryptor;
		EncryptionManager em = Registrar.getManager(EncryptionUtils.class.getPackage().getName() + ".managers", "EncryptionManager");
		if ( em == null )
			EncryptionUtils.DEFAULT_ENCRYPTION_METHOD = EncryptionConstants.NULL_ENCRYPTION_ID;
		else
		{
			Map<String, String> ntp = EncryptionUtils.decomposeCipher(nameSpecification);
			Collection<CompartmentObject<?>> identifiers = ClassUtils.makeCollection(ntp,
					new String[]{EncryptionConstants.CIPHERMAP_TYPEFIELD,
							EncryptionConstants.CIPHERMAP_PADFIELD});
			EncryptionStrategy possibleCryptor = em.getEncryption(ntp.get(EncryptionConstants.CIPHERMAP_NAMEFIELD),
					                                            ntp.get(EncryptionConstants.CIPHERMAP_TYPEFIELD),
					                                            ntp.get(EncryptionConstants.CIPHERMAP_PADFIELD));
			if ( possibleCryptor != null )
				cryptor = possibleCryptor;
			else
			{
				EncryptionUtils.DEFAULT_ENCRYPTION_METHOD = EncryptionConstants.NULL_ENCRYPTION_ID;
				cryptor = new NullEncryptionStrategy();
			}
		}

		return cryptor;
	}

	public static Credentials makeCredentials(String userName, Password passwdObj )
	{
		if ( ! TextManager.validString(userName) || passwdObj == null ) return null;
		return new Credentials(userName, passwdObj);
	}

	public static Credentials makeCredentials(String userName, String passwd )
	{
		return EncryptionUtils.makeCredentials(userName, passwd, null);
	}

	public static Credentials makeCredentials(String userName, String passwd, String cryptor )
	{
		Password passwdObj = EncryptionUtils.makePassword(passwd, cryptor);
		return new Credentials(userName, passwdObj);
	}

	public static Password makePassword( String passwd )
	{
		return EncryptionUtils.makePassword(passwd, (EncryptionStrategy) null);
	}

	public static Password makePassword( String passwd, String cryptor )
	{
		return EncryptionUtils.makePassword(passwd, (Object) cryptor);
	}

	public static Password makePassword( String passwd, Object cryptor )
	{
		if ( ! TextManager.validString(passwd) ) return null;

		boolean isEncrypted = EncryptionUtils.isEncrypted(passwd);
		Password passwdObj;
		try
		{
			EncryptionStrategy cryptMethod = null;
			String cryptType = null;
			if ( cryptor instanceof EncryptionStrategy )
				cryptMethod = Cast.cast(cryptor);
			if ( cryptor instanceof String )
				cryptType = Cast.cast(cryptor);

			if ( cryptMethod == null && cryptType == null ) cryptMethod = EncryptionUtils.getEncryptionMethod(null);
			if ( cryptMethod != null )
				passwdObj = new Password(passwd, cryptMethod, isEncrypted );
			else
				passwdObj = new Password(passwd, EncryptionUtils.getEncryptionMethod(cryptType), isEncrypted );
		}
		catch (InvalidKeyException | NoSuchAlgorithmException | NoSuchPaddingException |
				IllegalBlockSizeException | BadPaddingException | DecryptionException |
				InvalidAlgorithmParameterException e)
		{
			return null;
		}
		return passwdObj;
	}
}
