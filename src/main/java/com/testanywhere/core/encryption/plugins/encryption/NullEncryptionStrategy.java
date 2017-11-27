package com.testanywhere.core.encryption.plugins.encryption;

public class NullEncryptionStrategy extends EncryptionStrategy
{
	public NullEncryptionStrategy()
	{
		super();
		this.setLog(this);
		this.setup(this.getClass().getSimpleName());
	}

	public NullEncryptionStrategy(String eType, String ePaddingMethod )
	{
		this();
	}
	
	@Override
	public String encode(String input) 
	{
		return input;
	}

	@Override
	public String decode(String input) 
	{
		return input;
	}

	@Override
	public void prepare() 
	{}

	@Override
	public void buildObjectOutput(int numTabs)
	{}
}
