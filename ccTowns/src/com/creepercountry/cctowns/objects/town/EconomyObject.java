package com.creepercountry.cctowns.objects.town;

import com.creepercountry.cctowns.objects.MasterObject;

public abstract class EconomyObject extends MasterObject
{
	protected double balance;
	
	public double getBalance()
	{
		return balance;
	}
	
	public void setBalance(double bal)
	{
		this.balance = bal;
	}
}
