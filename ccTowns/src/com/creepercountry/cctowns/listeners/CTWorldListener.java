package com.creepercountry.cctowns.listeners;

import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.world.PortalCreateEvent;
import org.bukkit.event.world.PortalCreateEvent.CreateReason;

import com.creepercountry.cctowns.main.CTPlugin;
import com.creepercountry.cctowns.util.Colors;

public class CTWorldListener implements Listener
{
	/**
     * The plugin instance
     */
	private final CTPlugin plugin;

	/**
	 * constructor
	 * @param plugin
	 */
	public CTWorldListener(CTPlugin instance)
    {
		plugin = instance;
    }

	@EventHandler(priority = EventPriority.MONITOR)
	public void onPortalCreateEvent(PortalCreateEvent event)
	{
		if (event.getReason().equals(CreateReason.FIRE))
		{
			plugin.getServer().broadcastMessage(Colors.Yellow + ChatColor.ITALIC + "Linking portal to warp hub...");
		}
	}
}
