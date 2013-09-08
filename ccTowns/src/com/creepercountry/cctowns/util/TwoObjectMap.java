package com.creepercountry.cctowns.util;

public class TwoObjectMap
{
	private Object one, two;
	
	public TwoObjectMap(Object o, Object t)
	{
		this.one = o;
		this.two = t;
	}
	
	public Object getFirst()
	{
		return this.one;
	}
	
	public Object getSecond()
	{
		return this.two;
	}
}
