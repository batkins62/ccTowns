package com.creepercountry.cctowns.objects.town;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

import com.creepercountry.cctowns.objects.MasterObject;
import com.creepercountry.cctowns.util.NotRegisteredException;

public class Portal extends MasterObject
{
	private Town town;
	private boolean enabled, nether;
	private double sx, sy, sz;
	private World sworld;
	
	public Portal(String name)
	{
		setName(name);
	}
	
	public void setXLocation(double x)
	{
		this.sx = x;
	}
	
	public void setYLocation(double y)
	{
		this.sy = y;
	}
	
	public void setZLocation(double z)
	{
		this.sz = z;
	}
	
	public double getXLocation()
	{
		return this.sx;
	}
	
	public double getYLocation()
	{
		return this.sy;
	}
	
	public double getZLocation()
	{
		return this.sz;
	}
	
	public World getWLocation()
	{
		return this.sworld;
	}
	
	public void setWLocation(String world)
	{
		this.sworld = Bukkit.getWorld(world);
	}
	
	public void setEnabled(boolean en)
	{
		this.enabled = en;
	}
	
	public boolean isEnabled()
	{
		return this.enabled;
	}
	
	public void setIsNether(boolean is)
	{
		this.nether = is;
	}
	
	public boolean getIsNether()
	{
		return nether;
	}
	
	public Location getSendLocation()
	{
		return new Location(sworld, sx, sy, sz);
	}
	
	public boolean hasTown()
	{
		return !(this.town == null);
	}
	
	public boolean hasTo()
	{
		return (!(sz == 0) && !(sy == 0) && !(sx == 0) && (sworld != null));
	}
	
	public Town getTown() throws NotRegisteredException
	{
		if (hasTown())
			return this.town;
		
		else
			throw new NotRegisteredException("Portal doesn't point to any town");
	}
	
	public void setTown(Town town)
	{
		if (town == null)
		{
			this.town = null;
			return;
		}
		
		if (this.town == town)
			return;
		
		this.town = town;
	}

	public void clear()
	{
		try { town.removePortal(this); } catch (NotRegisteredException e) { }
	}
}
