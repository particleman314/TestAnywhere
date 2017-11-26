package com.testanywhere.core.classes.support;

import com.testanywhere.core.utilities.class_support.Cast;
import com.testanywhere.core.utilities.exceptions.ObjectCreationException;
import com.testanywhere.core.utilities.logging.*;
import com.testanywhere.core.classes.utilities.OSUtils;

import java.io.File;

public class Filename extends OutputDisplay
{
	private String fullPath;
	private char pathSeparator, extensionSeparator;

	public Filename(final String str)
	{
		this();
		this.fullPath = str;
		this.pathSeparator = File.separatorChar;
		this.extensionSeparator = '.';
	}

	public Filename(final String str, final char sep, final char ext)
	{
		this();
		this.fullPath = str;
		this.pathSeparator = sep;
		this.extensionSeparator = ext;
	}

	public Filename(final String str, final String sep, final String ext)
	{
		this();
		this.fullPath = str;
		
		if ( TextManager.validString(sep)) this.pathSeparator = sep.charAt(0);
		if ( TextManager.validString(ext)) this.extensionSeparator = ext.charAt(0);
	}

	public Filename( final Filename fn ) throws ObjectCreationException
	{
		this();
		if ( fn == null ) throw new ObjectCreationException(Filename.class);

		this.fullPath = fn.fullPath;
		this.pathSeparator = fn.pathSeparator;
		this.extensionSeparator = fn.extensionSeparator;
	}

	@Override
	public String toString()
	{
		StringBuilder sb = new StringBuilder();
		
		sb.append("FP:" + TextManager.specializeName(this.fullPath) + TextManager.STR_OUTPUTSEPARATOR);
		sb.append("pS:" + TextManager.specializeName(String.valueOf(this.pathSeparator)) + TextManager.STR_OUTPUTSEPARATOR);
		sb.append("eS:" + TextManager.specializeName(String.valueOf(this.extensionSeparator)));
		
		return sb.toString();
	}
	
	@SuppressWarnings("EqualsWhichDoesntCheckParameterClass")
	@Override
	public boolean equals( final Object o ) {
		if (o == null) return false;

		Filename fn = Cast.cast(o);
		return fn != null && (this.fullPath.equals(fn.fullPath) && this.pathSeparator == fn.pathSeparator && this.extensionSeparator == fn.extensionSeparator);

	}

	@Override
	public boolean isNull() { return false; }

	@Override
	public void buildObjectOutput(int numTabs)
	{
		if ( numTabs < 0 ) numTabs = 0;
		Tabbing tabEnvironment = new Tabbing(numTabs);
		DisplayManager dm = this.getDM();

		String outerSpacer = tabEnvironment.getSpacer();
		dm.append(outerSpacer + "Filename Class :", DisplayType.TEXTTYPES.LABEL);

		tabEnvironment.increment();
		String innerSpacer = tabEnvironment.getSpacer();

		dm.append(innerSpacer + "Full Path           : " + this.fullPath);
		dm.append(innerSpacer + "Path Separator      : " + this.pathSeparator);
		dm.append(innerSpacer + "Extension Separator : " + this.extensionSeparator);
	}

	public String getName()
	{
		return this.fullPath;
	}
	
	public String extension() 
	{
		int dot = this.fullPath.lastIndexOf(this.extensionSeparator);
		return this.fullPath.substring(dot + 1);
	}

	public String basename()
	{
		// gets filename with extension
		int sep = this.fullPath.lastIndexOf(this.pathSeparator);
		return this.fullPath.substring(sep + 1);
	}

	public String basename_no_extension()
	{ 
		// gets filename without extension
		int dot = this.fullPath.lastIndexOf(this.extensionSeparator);
		int sep = this.fullPath.lastIndexOf(this.pathSeparator);
		return this.fullPath.substring(sep + 1, dot);
	}

	public String path() 
	{
		int sep = this.fullPath.lastIndexOf(this.pathSeparator);
		String possiblePath = "";
		if ( sep >= 0 )
			possiblePath = this.fullPath.substring(0, sep);
		
		if ( possiblePath.length() < 1 )
		{
			if ( OSUtils.isWindows() )
				possiblePath = "C:\\";
			else
				possiblePath = "/";
		}
		return possiblePath;
	}
	
	public static String makePath( final String ... args )
	{
		if ( args.length < 1 ) return null;
		String delimiter = args[0];
		
		StringBuilder sb = new StringBuilder();
		for ( int loop = 1; loop < args.length; ++loop )
		{
			sb.append(args[loop]);
			if ( loop != ( args.length - 1 ) )
			{
				sb.append(delimiter);
			}
		}
		return sb.toString();
	}

	private Filename()
	{
		super();
		this.initialize();
	}

	private void initialize()
	{
		this.fullPath = null;
		this.pathSeparator = File.separatorChar;
		this.extensionSeparator = '.';		
	}
}
