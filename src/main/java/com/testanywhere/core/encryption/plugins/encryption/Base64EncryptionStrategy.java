package com.testanywhere.core.encryption.plugins.encryption;

import com.owtelse.codec.Base64;

import java.io.UnsupportedEncodingException;

public class Base64EncryptionStrategy extends EncryptionStrategy
{
	public Base64EncryptionStrategy()
	{
		super();
		this.setup(this.getClass().getSimpleName());
	}

	public Base64EncryptionStrategy(String eType, String ePaddingMethod )
	{
		this();
	}

	@Override
	public void buildObjectOutput(int numTabs)
	{}

	@Override
	public String encode(final String input) throws UnsupportedEncodingException
	{
		if ( input == null ) return null;
		if ( "".equals(input.trim()) ) return "";
		
		return this.__finalizeEncryption(Base64.encode(input.getBytes()));
	}

	@Override
	public String decode(final String input) 
	{
		if ( input == null ) return null;
		if ( "".equals(input.trim()) ) return "";
		
		try 
		{
			String stripped = this.__prepareDecryption(input);
			return Base64.decode(stripped.getBytes());
		} 
		catch ( UnsupportedEncodingException | IllegalArgumentException e )
		{
			this.error(e.getLocalizedMessage());
			return null;
		}
	}

	@Override
	public void prepare() 
	{}
}
