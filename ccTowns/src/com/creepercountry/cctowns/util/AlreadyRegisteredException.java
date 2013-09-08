package com.creepercountry.cctowns.util;

public class AlreadyRegisteredException extends TownException
{
	private static final long serialVersionUID = 4191685552690886161L;

	public AlreadyRegisteredException()
	{
		super("Already registered.");
	}

	public AlreadyRegisteredException(String message)
	{
		super(message);
	}
}