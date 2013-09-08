package com.creepercountry.cctowns.hooks;

import org.bukkit.plugin.Plugin;

import com.creepercountry.cctowns.main.CTPlugin;

public class Essentials implements Hook
{
    private Plugin pl;
	
	@Override
	public void onEnable(CTPlugin plugin)
	{
		this.pl = plugin.getServer().getPluginManager().getPlugin("Essentials");
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
	
    /**
     * Using essentials will return an epoch long  of players lastActive date
     * 
     * @param player <code>the Player</code>
     * @return epoch <code>in the form long</code>
     */
    public long lastActive(String playerName)
    {	
		return ((com.earth2me.essentials.Essentials) this.pl).getOfflineUser(playerName).getLastLogin();
    }
}
