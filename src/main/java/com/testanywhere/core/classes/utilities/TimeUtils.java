package com.testanywhere.core.classes.utilities;

import com.testanywhere.core.utilities.logging.LogConfiguration;
import com.testanywhere.core.utilities.logging.TextManager;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class TimeUtils 
{
	public static Logger logger;

	static
	{
		TimeUtils.logger = Logger.getLogger("TimeUtils");
		LogConfiguration.configure();
	}

	public static Long getCurrentDateAsSeconds()
	{
		return System.currentTimeMillis() / 1000L;
	}

	public static Date getCurrentDate()
	{
		return Calendar.getInstance().getTime();
	}
	
	public static String getCurrentDateAsString()
	{
		return TimeUtils.getCurrentDateAsString(null);
	}
	
	public static String getCurrentDateAsString( String connector )
	{
		if ( ! TextManager.validString(connector) ) connector = "/";
		
		String dayRep = TimeUtils.getCurrentDate().toString();
		String[] dayParts = dayRep.split(" ");
		return StringUtils.join(new String[]{dayParts[1], dayParts[2], dayParts[5]}, connector);
	}

	public static String getCurrentTime()
	{
		String dayRep = TimeUtils.getCurrentDate().toString();
		String[] dayParts = dayRep.split(" ");
		return dayParts[3];
	}
	
	public static String getDateAsYMDFormat( String format )
	{
		if ( ! TextManager.validString(format) ) format = "yyyy_mm_dd";
		Date d = TimeUtils.getCurrentDate();
		SimpleDateFormat sdf = new SimpleDateFormat(format);
		return sdf.format(d);
	}
}
