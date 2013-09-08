package com.creepercountry.cctowns.util.tasks;

import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.scheduler.BukkitRunnable;

import com.creepercountry.cctowns.hooks.Vault;
import com.creepercountry.cctowns.main.CTPlugin;
import com.creepercountry.cctowns.main.config.MainConfigObject;
import com.creepercountry.cctowns.objects.TownUniverse;
import com.creepercountry.cctowns.util.BukkitUtils;
import com.creepercountry.cctowns.util.Colors;
import com.creepercountry.cctowns.util.NotRegisteredException;

public class WatchedPlayer extends BukkitRunnable
{
	private final Player player;
	private final CTPlugin plugin;
	private final PlayerCommandPreprocessEvent event;
	
	public WatchedPlayer(CTPlugin main, PlayerCommandPreprocessEvent event)
	{
		this.plugin = main;
		this.event = event;
		this.player = event.getPlayer();
	}
	
	@Override
	public void run()
	{
		for (String prefix : MainConfigObject.blacklist_commands)
			if (event.getMessage().startsWith(prefix))
				return;
		
		try
		{
			for (Player plr : plugin.getServer().getOnlinePlayers())
				if (Vault.perms.has(plr, "cc.watched.broadcast"))
					BukkitUtils.sendMessage(plr, Colors.Gray, "[CC] [INFO] " + player.getName() + " issued server command: " + event.getMessage());
				
			TownUniverse.getDataSource().getWatchedCmdsPlayer(player.getName()).add(event.getMessage());
			TownUniverse.getDataSource().saveWatchedPlayer(player.getName());
		}
		catch (NotRegisteredException e) {}
	}

}
