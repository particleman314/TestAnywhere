package com.testanywhere.core.classes.support.md5;

import com.testanywhere.core.utilities.logging.TextManager;

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MD5Generator 
{
	public static String getDigest( final String str, Charset encoding )
	{
		if ( ! TextManager.validString(str) ) return null;
		if ( encoding == null ) encoding = Charset.defaultCharset();

		byte[] bytesOfMessage;
		try
		{
			bytesOfMessage    = str.getBytes(encoding.name());
			MessageDigest md5 = MessageDigest.getInstance("MD5");
			byte[] theDigest  = md5.digest(bytesOfMessage);

			return MD5Generator.bytesToHex(theDigest);
		}
		catch ( UnsupportedEncodingException | NoSuchAlgorithmException e )
		{
			return null;
		}
	}

	public static String getDigest( final String str )
	{
		return MD5Generator.getDigest(str, null);
	}
	
	private static String bytesToHex( final byte[] b )
	{
		char hexDigit[] = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
							'a', 'b', 'c', 'd', 'e', 'f' };
		StringBuilder buf = new StringBuilder();

		for (byte aB : b)
		{
			buf.append(hexDigit[(aB >> 4) & 0x0f]);
			buf.append(hexDigit[aB & 0x0f]);
		}
		return buf.toString();
	}

	private MD5Generator() {}
}