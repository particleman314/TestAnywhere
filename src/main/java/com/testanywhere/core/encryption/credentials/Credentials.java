package com.testanywhere.core.encryption.credentials;

import com.testanywhere.core.encryption.plugins.encryption.EncryptionStrategy;
import com.testanywhere.core.encryption.utilities.EncryptionUtils;
import com.testanywhere.core.utilities.class_support.Cast;
import com.testanywhere.core.utilities.classes.Pair;
import com.testanywhere.core.utilities.exceptions.ObjectCreationException;
import com.testanywhere.core.utilities.logging.DisplayManager;
import com.testanywhere.core.utilities.logging.DisplayType;
import com.testanywhere.core.utilities.logging.Tabbing;
import com.testanywhere.core.utilities.logging.TextManager;

public class Credentials extends Pair<String, Password>
{
	public Credentials()
	{
		super();
	}

	public Credentials( String user, String pass, EncryptionStrategy cryptor )
	{
		this();
		Password passwdObj = EncryptionUtils.makePassword(pass, cryptor);
		this.set( user, passwdObj );
	}

	public Credentials( String user, String pass, String cryptor )
	{
		this();
		Password passwdObj = EncryptionUtils.makePassword(pass, cryptor);
		this.set( user, passwdObj );
	}

	// This is a less SECURE way to set up a credentials object
	public Credentials( String user, String pass )
	{
		this( user, pass, (EncryptionStrategy) null );
	}

	// This is a more SECURE way to set up a credentials object
	public Credentials( String user, Password pass )
	{
		this();
		this.set( user, pass );
	}

	public Credentials( final Credentials creds ) throws ObjectCreationException
	{
		this();
		if ( creds == null ) throw new ObjectCreationException(Credentials.class);

		try {
			this.set(creds.getL(), (Password) creds.getR().copy());
		} catch (CloneNotSupportedException e) {
			throw new ObjectCreationException(Credentials.class);
		}
	}

	@Override
	public String toString()
	{
		StringBuilder sb = new StringBuilder();
		sb.append("U:" + this.getUserID() + TextManager.STR_OUTPUTSEPARATOR);
		sb.append("P:" + this.getPassword());
		return sb.toString();
	}

	@Override
	public void buildObjectOutput( int numTabs )
	{
		if ( numTabs < 0 ) numTabs = 0;
		DisplayManager dm = this.getDM();

		Tabbing tabEnvironment = new Tabbing(numTabs);

		String outerSpacer = tabEnvironment.getSpacer();
		dm.append(outerSpacer + "Credentials : " , DisplayType.TEXTTYPES.LABEL);

		tabEnvironment.increment();
		String innerSpacer = tabEnvironment.getSpacer();

		dm.append(innerSpacer + "User ID  : " + this.getUserID());
		dm.append(innerSpacer + "Password : " + this.getPassword().getEncoded());
	}

	@Override
	public boolean equals( final Object o )
	{
		if ( o instanceof Credentials )
		{
			Credentials oT = Cast.cast(o);
			return oT != null && (this.getUserID().equals(oT.getUserID()) && this.getPassword().equals(oT.getPassword()));
		}
		return false;
	}

	public String getUserID()
	{
		return this.first();
	}
	
	public Password getPassword()
	{
		return this.second();
	}
	
	public String getDecodedPassword()
	{
		return this.second().getDecoded();
	}
}
