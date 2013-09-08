package com.creepercountry.cctowns.listeners;

import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import com.creepercountry.cctowns.hooks.Vault;
import com.creepercountry.cctowns.main.CTEngine;
import com.creepercountry.cctowns.main.CTPlugin;
import com.creepercountry.cctowns.objects.TownUniverse;
import com.creepercountry.cctowns.objects.user.User;
import com.creepercountry.cctowns.util.BukkitUtils;
import com.creepercountry.cctowns.util.Colors;
import com.creepercountry.cctowns.util.NotRegisteredException;

public class CTEntityListener implements Listener
{
	/**
	 * The plugin instance
	 */
	@SuppressWarnings("unused")
	private CTPlugin plugin;

	/**
	 * constructor
	 * @param plugin
	 */
	public CTEntityListener(CTPlugin instance)
	{
		plugin = instance;
	}
	
	@EventHandler // EventPriority.NORMAL by default
	public void onEntityDamageByEntityEvent(EntityDamageByEntityEvent event)
	{	
		if (event.isCancelled())
			return;

		if (!CTEngine.CHECKFLY)
			return;
		
		if ((event.getDamager() instanceof Player))
		{
			Player player = (Player)event.getDamager();
			
			if (player.isOp() || player.getGameMode().equals(GameMode.CREATIVE) || Vault.perms.has(player, "ct.fly.attack.override"))
				return;
			
			if (player.isFlying())
			{
				try
				{
					User user = TownUniverse.getDataSource().getUser(player.getName());
					
					event.setCancelled(true);
					BukkitUtils.sendMessage(player, Colors.Red, "no flying while attacking!");
					user.addFlyViolation(1);
					TownUniverse.getDataSource().saveUser(user);
				}
				catch (NotRegisteredException e) {}
			}
		}
	}
}
