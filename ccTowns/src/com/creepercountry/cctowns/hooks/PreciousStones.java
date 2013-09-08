package com.creepercountry.cctowns.hooks;

import org.bukkit.plugin.Plugin;

import com.creepercountry.cctowns.main.CTPlugin;

public class PreciousStones implements Hook
{
    private Plugin pl;
	
	@Override
	public void onEnable(CTPlugin plugin)
	{
		this.pl = plugin.getServer().getPluginManager().getPlugin("PreciousStones");
	}

	@Override
	public void onDisable(CTPlugin plugin)
	{
		this.pl = null;
	}

	@Override
	public int getUniqueID()
	{
		return pl.hashCode();
	}

	@Override
	public String getName()
	{
		return pl.getName();
	}

	@Override
	public boolean isEnabled()
	{
		return pl != null;
	}

	@Override
	public Plugin getPlugin()
	{
		return pl;
	}
}