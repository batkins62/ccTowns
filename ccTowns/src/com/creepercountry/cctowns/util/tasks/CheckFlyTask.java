package com.creepercountry.cctowns.util.tasks;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import com.creepercountry.cctowns.hooks.Vault;
import com.creepercountry.cctowns.main.CTEngine;
import com.creepercountry.cctowns.main.CTPlugin;
import com.creepercountry.cctowns.main.config.MainConfigObject;
import com.creepercountry.cctowns.objects.TownUniverse;
import com.creepercountry.cctowns.objects.user.User;
import com.creepercountry.cctowns.util.BukkitUtils;
import com.creepercountry.cctowns.util.Colors;
import com.creepercountry.cctowns.util.DebugMode;
import com.creepercountry.cctowns.util.NotRegisteredException;
import com.creepercountry.cctowns.util.StopWatch;
import com.creepercountry.cctowns.util.StringUtils;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;

public class CheckFlyTask extends BukkitRunnable
{
	private final WorldGuardPlugin plugin;
	private final Player plr;
	private final Location loc;
	private final StopWatch sw;
	 
    public CheckFlyTask(WorldGuardPlugin plugin, Player player, Location location)
    {
        this.plugin = plugin;
        this.plr = player;
        this.loc = location;
        this.sw = CTPlugin.getInstance().getStopWatch();
    }
    
	@Override
	public void run()
	{
		// Get the current time for StopWatch
		long start = System.nanoTime();
		
    	if (plr.isFlying())
		{		
			if (!loc.getWorld().getName().equals("world"))
				if (!Vault.perms.has(plr, "ct.bypass.fly"))
				{
					DebugMode.log("Player cant fly in this world");
					deny(true);
					// log to StopWatch
			        sw.setLoadNoChirp("CheckFlyTask", System.nanoTime() - start);
			        return;
				}
			
			if (!plugin.canBuild(plr, plr.getLocation().getBlock().getRelative(0, -1, 0)))
			{
				DebugMode.log("Player can not wg build at location");
				
				if (!Vault.perms.has(plr, "ct.bypass.fly"))
				{
					DebugMode.log("player doesnt have bypass permission, check has passed, processing termination.");
					deny(true);
				}
			}
		}
    	
		// log to StopWatch
        sw.setLoadNoChirp("CheckFlyTask", System.nanoTime() - start);
	}

	private void deny(boolean kick)
	{
		try
		{
			plr.setFlying(false);
			plugin.broadcastNotification(String.format("Player %s was caught flying at " + StringUtils.cleanLoc(loc), Colors.Green + plr.getName() + Colors.White));
			
			User user = TownUniverse.getDataSource().getUser(plr.getName());
			user.addFlyViolation(1);
			plugin.broadcastNotification(Colors.Blue + plr.getName() + Colors.LightBlue + " recieved 1 violation totaling: " + ((user.getFlyViolation() > 3) ? Colors.Red : Colors.Gold) + "VL" + user.getFlyViolation());
			BukkitUtils.sendMessage(plr, Colors.Red, "NO FLYING HERE!");
			
			if (user.getFlyViolation() > MainConfigObject.punish_fly)
			{
				plr.teleport(MainConfigObject.fly_punish_location.toLocation(Bukkit.getWorld(MainConfigObject.fly_punish_world)));
				if (kick)
					plr.kickPlayer("Do not fly in areas you do not have permissions to.");
			}
			
			TownUniverse.getDataSource().saveUser(user);
		}
		catch (NotRegisteredException nre) {}
		
		if (!CTEngine.DECREMENTATION)
		{
			CTEngine.DECREMENTATION = true;
			new ViolationDecremationTask().runTaskTimerAsynchronously(CTPlugin.getInstance(), 60L, 36000L);
			BukkitUtils.warning("========================================");
			BukkitUtils.warning(Colors.Red + "Online User has VL! Creating Check Task (DECREMENTATION)");
			BukkitUtils.warning("This Task will be disabled when either no donators online, or no VL detected.");
			BukkitUtils.warning("========================================");
		}
	}
}
