package com.creepercountry.cctowns.listeners;

import java.util.Calendar;
import java.util.GregorianCalendar;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.weather.ThunderChangeEvent;

import com.creepercountry.cctowns.main.CTPlugin;

public class CTWeatherListener implements Listener
{
	/**
     * The plugin instance
     */
	private CTPlugin plugin;

	/**
	 * constructor
	 * @param plugin
	 */
	public CTWeatherListener(CTPlugin instance)
    {
		plugin = instance;
    }
	
	@EventHandler // EventPriority.NORMAL by default
	public void onThunderChangeEvent(ThunderChangeEvent event)
	{
		if (!event.toThunderState())
			return;
		
		Calendar next = new GregorianCalendar();
		Calendar current = new GregorianCalendar();
		next.setTimeInMillis(plugin.getConfig().getLong("weather.nextChange", System.currentTimeMillis()));
		current.setTimeInMillis(System.currentTimeMillis());
		
		if (current.before(next))
		{
			event.setCancelled(true);
		}
		
		//TODO: create 15 as a setable time.
		current.add(Calendar.MINUTE, 15);
		plugin.getConfig().set("weather.nextChange", current.getTimeInMillis());
	}
}
