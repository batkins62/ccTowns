package com.creepercountry.cctowns.util;

import com.creepercountry.cctowns.objects.town.Town;

public class EmptyTownException extends Exception
{
	private static final long serialVersionUID = 5058583908170407803L;
	private Town town;

	public EmptyTownException(Town town)
	{
		setTown(town);
	}

	public void setTown(Town town)
	{
		this.town = town;
	}

	public Town getTown()
	{
		return town;
	}
}