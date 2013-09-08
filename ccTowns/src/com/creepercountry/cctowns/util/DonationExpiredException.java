package com.creepercountry.cctowns.util;

public class DonationExpiredException extends Exception
{
	private static final long serialVersionUID = 6401105343957587628L;
	
	public DonationExpiredException(String msg)
	{
		super(msg);
	}
}
