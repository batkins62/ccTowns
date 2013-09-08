package com.creepercountry.cctowns.hooks;

import org.bukkit.plugin.Plugin;

import com.creepercountry.cctowns.main.CTPlugin;

public interface Hook
{
	public void onEnable(CTPlugin plugin);
	
	public void onDisable(CTPlugin plugin);
	
	public int getUniqueID();
	
	public String getName();
	
	public String toString();
	
	public boolean isEnabled();
	
	public Plugin getPlugin();
}
