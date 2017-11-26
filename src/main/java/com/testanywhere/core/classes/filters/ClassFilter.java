package com.testanywhere.core.classes.filters;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ClassFilter extends Filter<Class<?>> 
{
	private Pattern match;
	
	public ClassFilter(final String pattern)
	{
		this();
		this.match = Pattern.compile(pattern);
	}
	
	public ClassFilter(final Pattern pattern)
	{
		this();
		this.match = pattern;
	}
	
	@Override
	public boolean passes(final Class<?> object)
	{
		if ( object != null ) 
		{
			Matcher m = this.match.matcher(object.getName());
			if ( m.find() ) { return true; }
		}
		
		return false;
	}

	@SuppressWarnings("unused")
	private ClassFilter()
	{
		this.match = null;
	}
}
