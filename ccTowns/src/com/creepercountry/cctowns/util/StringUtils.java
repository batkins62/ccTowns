package com.creepercountry.cctowns.util;

import java.text.DecimalFormat;
import java.util.List;

import org.bukkit.Location;

public class StringUtils
{
	/**
	 * check the start of a string for an int
	 * 
	 * @param string
	 * @return the starting string int, returns 0 if no int
	 */
	public static int startsWithInt(String string)
	{
		int len = string.length();
		
		if (Character.isDigit(string.charAt(0)))
		{
			for (int i=0; i<=len; i++)
			{
				if (!Character.isDigit(string.charAt(i)))
				{
					String numstring = string.substring(0, i + 1);
					int num = Integer.parseInt(numstring);
					i = len;
					return num;
				}
			}	
		}
		return 0;
	}
	
	/**
	 * splits the string
	 * 
	 * @param s
	 * @param interval
	 * @return
	 */
	public static String[] splitStringEvery(String s, int interval)
	{
	    int arrayLength = (int) Math.ceil(((s.length() / (double)interval)));
	    String[] result = new String[arrayLength];

	    int j = 0;
	    int lastIndex = result.length - 1;
	    for (int i = 0; i < lastIndex; i++)
	    {
	        result[i] = s.substring(j, j + interval);
	        j += interval;
	    } //Add the last bit
	    result[lastIndex] = s.substring(j);

	    return result;
	}
	
	/**
	 * 
	 * @param loc
	 */
	public static String cleanLoc(Location loc)
	{
		DecimalFormat myFormatter = new DecimalFormat("##0");
		String x = myFormatter.format(loc.getX());
		String y = myFormatter.format(loc.getY());
		String z = myFormatter.format(loc.getZ());
		
		return x + ", " + y + ", " + z;
	}

	@SuppressWarnings("rawtypes")
	public static String join(List arr, String separator)
	{
		if (arr == null || arr.size() == 0)
			return "";
		String out = arr.get(0).toString();
		for (int i = 1; i < arr.size(); i++)
			out += separator + arr.get(i);
		return out;
	}
}
