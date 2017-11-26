package com.testanywhere.core.classes.support.md5;

import com.testanywhere.core.utilities.class_support.Cast;
import com.testanywhere.core.utilities.exceptions.ObjectCreationException;
import com.testanywhere.core.utilities.logging.*;
import org.apache.commons.lang3.StringUtils;

import java.io.Externalizable;
import java.nio.charset.Charset;
import java.util.regex.Matcher;

public class MD5 extends OutputDisplay implements Externalizable
{
	private boolean wasRepaired;
	private boolean valid;
	private String  checkSum;
	private String  issue;

	public MD5()
	{
		super();
		this.initialize();
	}
	
	public MD5( final String checkSum )
	{
		this();
		this.checkSum = checkSum;
		this.validateCheckSum();
	}

	public MD5( final MD5 other ) throws ObjectCreationException
	{
		this();
		if ( other == null ) throw new ObjectCreationException(MD5.class);

		this.wasRepaired = other.wasRepaired;
		this.valid = other.valid;
		this.checkSum = other.checkSum;
		this.issue = other.issue;
	}

	@SuppressWarnings("EqualsWhichDoesntCheckParameterClass")
	@Override
	public boolean equals( final Object other )
	{
		if (other == null) return false;

		MD5 otherMD5 = Cast.cast(other);
		return ( otherMD5 != null && this.getCheckSum().equals(otherMD5.getCheckSum()) );
	}

	@Override
	public boolean isAssignable()
	{
		return false;
	}

	@Override
	public String toString() 
	{
		return "MD5 : " + TextManager.specializeName(this.getCheckSum());
	}

	@Override
	public boolean isNull() { return false; }

	@Override
	public void buildObjectOutput( int numTabs )
	{
		if ( numTabs < 0 ) numTabs = 0;
		Tabbing tabEnvironment = new Tabbing(numTabs);
		DisplayManager dm = this.getDM();

		String outerSpacer = tabEnvironment.getSpacer();
		dm.append(outerSpacer + "MD5 :", DisplayType.TEXTTYPES.LABEL);

		tabEnvironment.increment();
		String innerSpacer = tabEnvironment.getSpacer();

		dm.append(innerSpacer + "CheckSum     : " + this.getCheckSum());
		dm.append(innerSpacer + "Is Valid     : " + this.isValid());
		dm.append(innerSpacer + "Was Repaired : " + this.wasRepaired());
		if ( this.wasRepaired() )
			dm.append(innerSpacer + "Issue        : " + this.issue);
	}

	public String getCheckSum()
	{
		return this.checkSum;
	}
	
	public boolean isValid()
	{
		return this.valid;
	}
	
	public boolean wasRepaired() 
	{
		return this.wasRepaired;
	}
	
	public void validateCheckSum() 
	{
		this.validateCheckSum(true);
	}
	
	public void validateCheckSum( final boolean repair )
	{
		this.valid = this.__validate(repair);
	}

	public static String generate( final String data, final Charset encoding )
	{
		if ( ! TextManager.validString( data ) ) return null;
		return MD5Generator.getDigest(data, encoding);
	}

	public static String generate( final String data, String encoding )
	{
		if ( ! TextManager.validString( data ) ) return null;
		if ( ! TextManager.validString( encoding ) ) encoding = "UTF-8";
		return MD5.generate(data, Charset.forName(encoding) );
	}

	public static String generate( final String data )
	{
		return MD5.generate(data, Charset.defaultCharset());
	}

	private boolean __validate( final boolean repair )
	{
		int len = MD5Constants.getInstance().ZERO.length();
		boolean result = false;
		
		if ( this.getCheckSum().length() != len )
		{
			if ( repair ) 
			{
				int difference = len - this.getCheckSum().length();
				StringBuilder sb = new StringBuilder();
				if ( difference > 0 ) 
				{
					sb.append(this.getCheckSum()).append(StringUtils.repeat("0", difference));
					this.issue = "MD5 too short";
				} 
				else
				{
					sb.append(this.getCheckSum().substring(0, len));
					this.issue = "MD5 too long";
				}
				this.checkSum = sb.toString();
				this.wasRepaired = true;
			}
			else
			{
				return false;
			}
		}
		
		Matcher md5m = MD5Constants.getInstance().MD5_PATTERN.matcher(this.getCheckSum());

		if ( md5m.find(0) ) result = true;
		return result;
	}
	
	private void initialize()
	{
		this.wasRepaired = false;
		this.valid       = true;
		this.checkSum    = MD5Constants.getInstance().ZERO;
		this.issue       = null;
	}
}