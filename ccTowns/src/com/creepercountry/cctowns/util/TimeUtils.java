package com.creepercountry.cctowns.util;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

public class TimeUtils
{
	GregorianCalendar calendar;
	
	/**
	 * The constructor to create this object.
	 * 
	 * @param time in epoch
	 */
	public TimeUtils(long in)
	{
		this.calendar = new GregorianCalendar();
		this.calendar.setTimeInMillis(in);
	}
	
	/**
	 * set the timezone.
	 * 
	 * @param offset from UTC (+/-) example: -7
	 * @param dst set to true to enable day light savings
	 * @return false if something went wrong
	 */
	public boolean setTimeZone(int offset, boolean dst)
	{
		// get the supported ids for the offset
		String[] ids = TimeZone.getAvailableIDs(offset * 60 * 60 * 1000);
		// if no ids were returned, something is wrong. get out.
		if (ids.length == 0)
			return false;
		// create the timezone
		TimeZone tz = TimeZone.getDefault();
		for (String id : ids)
			if (dst && TimeZone.getTimeZone(id).useDaylightTime())
			{
				DebugMode.log(id);
				tz = TimeZone.getTimeZone(id);
			}
		this.calendar.setTimeZone(tz);
		return true;
	}
	
	/**
	 * set the timezone.
	 * 
	 * @param tz the TimeZone to set to
	 */
	public void setTimeZone(TimeZone zone)
	{
		this.calendar.setTimeZone(zone);
	}
	
	/**
	 * get the field, add the amount
	 * use: d for day, s for seconds, m for month
	 * use: h for hour, i for minute, l for millisecond(default)
	 * example: 6m would be 6 months
	 * DO NOT specify more then 1 field.
	 * TODO: eventually would like to
	 * 
	 * @param toadd (see above description)
	 */
	public void add(String toadd)
	{
		int field = Calendar.MILLISECOND;
		if (toadd.startsWith("s"))
			field = Calendar.SECOND;
		else if (toadd.startsWith("i"))
			field = Calendar.MINUTE;
		else if (toadd.startsWith("h"))
			field = Calendar.HOUR;
		else if (toadd.startsWith("d"))
			field = Calendar.DAY_OF_YEAR;
		else if (toadd.startsWith("m"))
			field = Calendar.MONTH;
		
		if (toadd.length() == 2)
			add(field, Integer.parseInt(toadd.substring(1)));
	}
	
	/**
	 * Add time to the object.
	 * 
	 * @param field use Calendar.OPTION
	 * @param amount the int amount for the field
	 */
	public void add(int field, int amount)
	{
		this.calendar.add(field, amount);
	}
	
	/**
	 * @return time in milliseconds
	 */
	public long getTimeInMillis()
	{
		return this.calendar.getTimeInMillis();
	}
	
	/**
	 * @return GregorianCalendar
	 */
	public GregorianCalendar getCalendar()
	{
		return this.calendar;
	}
	
	/**
	 * @return Date
	 */
	public Date getDate()
	{
		return this.calendar.getTime();
	}
	
	/**
	 * gets the field for the Calendar.
	 * returns: d for day, s for seconds, m for month
	 * returns: h for hour, i for minute, l for millisecond(default)
	 * example: 6m would be 6 months
	 * 
	 * @param source the string with the value to get (1d or 2m.. etc)
	 * @return the field as an int
	 */
	public static int getField(String source)
	{
		int field = Calendar.MILLISECOND;
		if (source.startsWith("s"))
			field = Calendar.SECOND;
		else if (source.startsWith("i"))
			field = Calendar.MINUTE;
		else if (source.startsWith("h"))
			field = Calendar.HOUR;
		else if (source.startsWith("d"))
			field = Calendar.DAY_OF_YEAR;
		else if (source.startsWith("m"))
			field = Calendar.MONTH;
		
		return field;
	}
}
