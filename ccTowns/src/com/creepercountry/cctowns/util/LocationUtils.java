package com.creepercountry.cctowns.util;

import org.bukkit.Bukkit;
import org.bukkit.Location;

public class LocationUtils
{
	/**
	 * Whether the two locations refer to the same block
	 *
	 * @param loc
	 * @param loc2
	 * @return
	 */
	public static boolean isSameBlock(Location loc, Location loc2)
	{
		if (loc == null && loc2 == null)
		{
			return true;
		}

		if (loc == null || loc2 == null)
		{
			return false;
		}

		if (loc.getBlockX() == loc2.getBlockX() && loc.getBlockY() == loc2.getBlockY() && loc.getBlockZ() == loc2.getBlockZ())
		{
			return true;
		}
		return false;
	}
	
	/**
	 * return true if location.
	 * 
	 * @param check
	 * @param start
	 * @param end
	 * @return
	 */
	public static boolean isInRange(Location check, Location start, Location end)
	{
		return check.toVector().isInAABB(start.toVector(), end.toVector());
	}
	
	/**
	 * Create a location based on a specification of coords.
	 * 
	 * @param world
	 * @param x
	 * @param y
	 * @param z
	 * @return
	 */
	public static Location coordsToLocation(String world, double x, double y, double z)
	{
		return new Location(Bukkit.getWorld(world), x, y, z);
	}

	/**
	 * Whether the two locations refer to the same location, ignoring pitch and yaw
	 *
	 * @param loc
	 * @param loc2
	 * @return
	 */
	public static boolean isSameLocation(Location loc, Location loc2)
	{
		if (loc == null && loc2 == null)
		{
			return true;
		}

		if (loc == null || loc2 == null)
		{
			return false;
		}

		if (loc.getX() == loc2.getX() && loc.getY() == loc2.getY() && loc.getZ() == loc2.getZ())
		{
			return true;
		}
		return false;
	}
}
