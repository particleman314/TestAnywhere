package com.testanywhere.core.utilities.logging;

import com.testanywhere.core.utilities.class_support.functional_support.MathFunctions;
import org.apache.commons.collections4.BidiMap;
import org.apache.commons.collections4.bidimap.TreeBidiMap;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

public class TextManager 
{
	public static Logger logger;

	static
	{
		TextManager.logger = Logger.getLogger("TextManager");
		LogConfiguration.configure();
	}

	private static final String DEFAULT_STR_OUTPUTSEPARATOR = "|";
	private static final String DEFAULT_SPECIALIZE_BEGINMARKER = "<";
	private static final String DEFAULT_SPECIALIZE_ENDMARKER = ">";

	public static final String EOL = System.getProperty("line.separator");
	public static String STR_OUTPUTSEPARATOR = DEFAULT_STR_OUTPUTSEPARATOR;
	public static String SPECIALIZE_BEGINMARKER = DEFAULT_SPECIALIZE_BEGINMARKER;
	public static String SPECIALIZE_ENDMARKER = DEFAULT_SPECIALIZE_ENDMARKER;

	private static final Map<String, BidiMap<Boolean, String>> booleanMapping = Collections.synchronizedMap(new LinkedHashMap<String, BidiMap<Boolean, String>>());

	static 
	{
		booleanMapping.put("std", null);
		
		BidiMap<Boolean, String> trueFalseMap = new TreeBidiMap<>();
		trueFalseMap.put(true, TextManager.capitalize("true"));
		trueFalseMap.put(false, TextManager.capitalize("false"));
		booleanMapping.put("tf", trueFalseMap);
		
		BidiMap<Boolean, String> passFailMap = new TreeBidiMap<>();
		passFailMap.put(true, TextManager.capitalize("pass"));
		passFailMap.put(false, TextManager.capitalize("fail"));
		booleanMapping.put("pf", passFailMap);
		
		BidiMap<Boolean, String> yesNoMap = new TreeBidiMap<>();
		yesNoMap.put(true, TextManager.capitalize("yes"));
		yesNoMap.put(false, TextManager.capitalize("no"));
		booleanMapping.put("yn", yesNoMap);

		resetSpecializationMarkers();
	}

	public static String getMethodNameFromStackTrace(final int depth)
	{
		final StackTraceElement[] ste = Thread.currentThread().getStackTrace();
		return ste[ste.length - 1 - depth].getMethodName();
	}

	public static int getCenterStartPosition(final String text, final int totalLength)
	{
		int result = 0;
		if ( ! TextManager.validString(text, null ) ) return result;
		
		int txtlgt = text.length();
		
    	if ( txtlgt < totalLength ) 
    	{
    		result = ( totalLength - txtlgt )/2;
    	}
    	return result;
    }
    
	public static String makeBanner(final String[] text, final int totalLength)
	{
		if ( text == null ) return null;
		
		StringBuilder sb = new StringBuilder();
		String newLine   = EOL;
		
		String leveler = StringUtils.repeat("*",totalLength);
		sb.append(leveler).append(newLine);
		if ( text.length > 0 ) sb.append("*" + StringUtils.repeat(" ", totalLength - 2) + "*").append(newLine);
		
		int startPt;
		for ( String line : text ) 
		{
			startPt = TextManager.getCenterStartPosition(line, totalLength);

			String lspacer = StringUtils.repeat(" ", startPt - 1);
			if ( MathFunctions.isOdd(startPt) ) lspacer = lspacer + " ";

			String rspacer;
			if ( MathFunctions.isOdd(line.length()) && ! MathFunctions.isOdd(totalLength) )
			{
				rspacer = StringUtils.repeat(" ", startPt);
			}
			else
			{
				rspacer = lspacer;
			}
			
			if ( MathFunctions.isOdd(startPt) ) rspacer = StringUtils.repeat(" ", startPt - 1);
		
			sb.append("*" + lspacer + line + rspacer + "*").append(newLine);
		}
		
		if ( text.length > 0 ) sb.append("*" + StringUtils.repeat(" ", totalLength - 2) + "*").append(newLine);
		sb.append(leveler).append(newLine).append(newLine);
		return sb.toString();
	}
	
    public static String specializeName(final String s)
    {
		if ( ! TextManager.validString(s, true) ) return "NULL";
		return TextManager.SPECIALIZE_BEGINMARKER + s + TextManager.SPECIALIZE_ENDMARKER;
    }

	public static void setNameSpecializationMarkers( String bm, String em )
	{
		if ( !TextManager.validString(bm) || !TextManager.validString(em) ) return;

		TextManager.SPECIALIZE_BEGINMARKER = bm;
		TextManager.SPECIALIZE_ENDMARKER = em;
	}

    public static String capitalize(final String s) 
    {
		if ( ! TextManager.validString(s, null ) ) return null;
        return s.substring(0, 1).toUpperCase() + s.substring(1).toLowerCase();
    }
    
	public static String StringRepOfBool(final Boolean b, String boolRepType)
	{
		if ( ! TextManager.validString(boolRepType, null ) ) boolRepType = "std";
		if ( ! booleanMapping.containsKey(boolRepType) ) 
		{
			boolRepType = "std";
			return TextManager.StringRepOfBool(b, boolRepType);
		} 
		else 
		{
			if ( booleanMapping.get(boolRepType) == null ) return b.toString();
			return booleanMapping.get(boolRepType).get(b);
		}
	}
	
	public static boolean BoolRepOfString(final String input, String boolRepType)
	{
		if ( ! TextManager.validString(boolRepType, null ) ) boolRepType = "std";
		if ( ! booleanMapping.containsKey(boolRepType) ) 
		{
			boolRepType = "std";
			return TextManager.BoolRepOfString(input, boolRepType);
		}
		else
		{
			BidiMap<Boolean, String> subMap = booleanMapping.get(boolRepType);
			if ( subMap == null ) return false;
			if ( ! subMap.inverseBidiMap().containsKey(input) ) return false;
			return subMap.inverseBidiMap().get(input);
		}
	}
	
	public static boolean validString( final String input )
	{
		return TextManager.validString(input, null);
	}
	
	public static boolean validString( final String input, Boolean allowEmpty )
	{
		if ( allowEmpty == null ) allowEmpty = false;
		
		if ( input == null ) return false;
		if ( ! allowEmpty )
		{
			if ( "".equals(input.trim()) ) return false;
		}
		return true;
	}

	public static String removeCommentFromLine( final String input )
	{
		String reduced = input.trim();

		return null;
	}

	private static void resetSpecializationMarkers()
	{
		TextManager.SPECIALIZE_BEGINMARKER = TextManager.DEFAULT_SPECIALIZE_BEGINMARKER;
		TextManager.SPECIALIZE_ENDMARKER = TextManager.DEFAULT_SPECIALIZE_ENDMARKER;
	}

	private TextManager() {}
}