package com.testanywhere.core.classes.filters;

import com.testanywhere.core.utilities.logging.TextManager;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringFilter extends Filter<String> 
{
	private Pattern regex;
	private String  pattern;

	public StringFilter( final Pattern filterPattern )
	{
		this();
		this.regex = filterPattern;
	}
	
	public StringFilter( final String stringPattern )
	{
		this();
		this.pattern = stringPattern;
	}
	
	@Override
	public boolean passes(final String name)
	{		
		if ( ! TextManager.validString(name) ) return false;
		if ( this.regex != null )
		{
			Matcher m = this.regex.matcher(name);
			if ( m.find() ) return true;
		} 
		else
		{
			Pattern p = Pattern.compile(this.pattern);
			Matcher m = p.matcher(name);
			if ( m.find() ) return true;
		}
		return false;
	}

	private StringFilter()
	{
		this.regex = null;
		this.pattern = null;
	}
}