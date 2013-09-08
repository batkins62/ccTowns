package com.creepercountry.cctowns.listeners;

import net.milkbowl.vault.economy.EconomyResponse;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerPortalEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.bukkit.event.player.PlayerToggleFlightEvent;
import org.bukkit.scheduler.BukkitTask;

import com.creepercountry.cctowns.hooks.Vault;
import com.creepercountry.cctowns.main.CTEngine;
import com.creepercountry.cctowns.main.CTPlugin;
import com.creepercountry.cctowns.main.config.MainConfigObject;
import com.creepercountry.cctowns.objects.TownUniverse;
import com.creepercountry.cctowns.objects.town.Town;
import com.creepercountry.cctowns.objects.town.Portal;
import com.creepercountry.cctowns.objects.user.User;
import com.creepercountry.cctowns.util.BukkitUtils;
import com.creepercountry.cctowns.util.Colors;
import com.creepercountry.cctowns.util.DebugMode;
import com.creepercountry.cctowns.util.LocationUtils;
import com.creepercountry.cctowns.util.NotRegisteredException;
import com.creepercountry.cctowns.util.StopWatch;
import com.creepercountry.cctowns.util.TownException;
import com.creepercountry.cctowns.util.tasks.CheckFlyTask;
import com.creepercountry.cctowns.util.tasks.NagPstoneTask;
import com.creepercountry.cctowns.util.tasks.WatchedPlayer;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;

public class CTPlayerListener implements Listener
{
	/**
     * The plugin instance
     */
	private CTPlugin plugin;
	
	/**
	 * The StopWatch object
	 */
	private StopWatch sw;

	/**
	 * constructor
	 * @param plugin
	 */
	public CTPlayerListener(CTPlugin instance)
    {
		plugin = instance;
		sw = instance.getStopWatch();
    }

	@EventHandler // EventPriority.NORMAL by default
	public void onPlayerJoin(PlayerJoinEvent event)
	{
		Player player = event.getPlayer();

		if (!CTEngine.ENABLED)
		{
			player.kickPlayer("Error x63CTPL: count to 10 then try again.");
		}
		
		if (DebugMode.getDEVMODE())
		{
			BukkitUtils.sendMessage(player, Colors.Gold, "Development mode is active. please note that the server may reboot " +
					"randomly and frequently, while we work to bring new content, updates, and general programming to this server. " +
					"We do not currently have an ETA on how long this will take us, but strive to minimize any downtime.");
		}
		
		try
		{
			plugin.getTownUniverse().onLogin(player);
		}
		catch (TownException x)
		{
			BukkitUtils.sendMessage(player, Colors.Red, x.getMessage());
		}
	}

	@EventHandler // EventPriority.NORMAL by default
	public void onPlayerQuit(PlayerQuitEvent event)
	{
		plugin.getTownUniverse().onLogout(event.getPlayer());
	}
	
	@EventHandler // EventPriority.NORMAL: by default
	public void onPlayerCommandPreprocessEvent(PlayerCommandPreprocessEvent event)
	{
		if (event.getMessage().equalsIgnoreCase("penis"))
			new NagPstoneTask(plugin, event.getPlayer());
		for (String prefix : MainConfigObject.pstone_nag_words)
			if (event.getMessage().startsWith(prefix))
			{
				
			}
		
		if (TownUniverse.getDataSource().hasWatchedCmdsPlayer(event.getPlayer().getName()))
		{
			BukkitTask task = new WatchedPlayer(plugin, event).runTask(plugin);
			DebugMode.log("TaskId: " + task.getTaskId() + ", Owner: " + task.getOwner().getName());
		}
		
		if (event.getMessage().contains("fly") && CTEngine.CHECKFLY)
		{
			DebugMode.log("Pre-Process command listener recognised the fly command.");

			new CheckFlyTask(plugin.getWorldGuard(), event.getPlayer(), event.getPlayer().getLocation()).runTask(plugin);
		}
		
		if (event.getMessage().contains("spawn") && CTEngine.CHECKFLY)
		{
			if (event.getPlayer().isFlying() && !Vault.perms.has(event.getPlayer(), "ct.bypass.fly"))
			{
				DebugMode.log("Fly mode was enabled for .");
				BukkitUtils.sendMessage(event.getPlayer(), Colors.Red, "WARNING! FLY MODE " + Colors.Green + "ENABLED" + Colors.Red + ", Plant two feet on the ground before typing /spawn");
				event.getPlayer().setFlying(false);
				event.setCancelled(true);
			}
		}
	}
	
	@EventHandler // EventPriority.NORMAL: by default
	public void onPlayerMoveEvent(PlayerMoveEvent event)
	{		
		if (CTEngine.CHECKFLY)
		{
			if (event.isCancelled())
			{
				return;
			}
	
			if (event.getFrom() == null || event.getTo() == null)
			{
				return;
			}
	
			if (LocationUtils.isSameLocation(event.getFrom(), event.getTo()))
			{
				return;
			}

			if (LocationUtils.isSameBlock(event.getFrom(), event.getTo()))
			{
				return;
			}

			if (event.getPlayer().getGameMode().equals(GameMode.CREATIVE))
			{
				return;
			}

			new CheckFlyTask(plugin.getWorldGuard(), event.getPlayer(), event.getPlayer().getLocation()).runTask(plugin);
		}
	}
	
	@EventHandler // EventPriority.NORMAL: by default
	public void onPlayerInteractEvent(PlayerInteractEvent event)
	{
		if (event.getAction().equals(Action.RIGHT_CLICK_BLOCK) && (event.getClickedBlock().getType().equals(Material.SIGN) || event.getClickedBlock().getType().equals(Material.WALL_SIGN) || event.getClickedBlock().getType().equals(Material.SIGN_POST)))
		{
			Sign sign = (Sign) event.getClickedBlock().getState();
			if (sign.getLine(0).equalsIgnoreCase(Colors.Navy + "[FOR SALE]"))
			{
				// check for perms
				if (!Vault.perms.has(event.getPlayer(), "ct.plot.use"))
				{
					BukkitUtils.sendMessage(event.getPlayer(), Colors.Red, "You do not have permission to use plots!");
					return;
				}
				
				try
				{
					User user = TownUniverse.getDataSource().getUser(event.getPlayer().getName());
					Town town = user.getTown();
					
					if (!user.hasChunk())
						BukkitUtils.sendMessage(event.getPlayer(), Colors.Red, "Only one plot may be owned at a time!");
					
					//TODO: output message telling player 1 plot per user.
					if (town.hasChunk(event.getClickedBlock().getChunk()) && !user.hasChunk())
					{
						EconomyResponse e = Vault.econ.withdrawPlayer(event.getPlayer().getName(), Double.parseDouble(sign.getLine(1).replace("$", "")));
						if (e.transactionSuccess())
						{
							BukkitUtils.sendMessage(event.getPlayer(), Colors.Green, String.format("You just had %s removed from your account.", e.amount));
							user.setChunk(event.getClickedBlock().getChunk());
						}
						else
						{
							BukkitUtils.sendMessage(event.getPlayer(), Colors.Red, e.errorMessage);
							return;
						}
						
						// change the sign
						String name = event.getPlayer().getName();
						int len = name.length();
						
						sign.setLine(0, Colors.Red + "[SOLD]");
						sign.setLine(1, name.substring(0, (len > 15) ? 15 : len));
						sign.setLine(2, "|---------");
						sign.setLine(3, Integer.toString(event.getClickedBlock().getChunk().getX()) + "," + Integer.toString(event.getClickedBlock().getChunk().getZ()));
						
						BukkitUtils.sendMessage(event.getPlayer(), Colors.Gold, "You now own this chunk of land! :)");
						TownUniverse.getDataSource().saveUser(user);
					}
				}
				catch (NotRegisteredException nre)
				{
					BukkitUtils.sendMessage(event.getPlayer(), Colors.Red, nre.getMessage());
					return;
				}
			}
			else if (sign.getLine(0).equalsIgnoreCase("[SOLD]"))
			{
				// check for perms
				if (!Vault.perms.has(event.getPlayer(), "ct.plot.use"))
				{
					BukkitUtils.sendMessage(event.getPlayer(), Colors.Red, "You do not have permission to use plots!");
					return; 
				}
				
				Town town = null;
				Chunk chunk = null;
				User user = null;
				User clicker = null;
				try { clicker = TownUniverse.getDataSource().getUser(event.getPlayer().getName()); } catch (NotRegisteredException nre) { return; }
				
				for (Town t : TownUniverse.getDataSource().getTowns())
				{
					for (Chunk c : town.getChunks())
					{
						if (c.equals(sign.getChunk()))
						{
							chunk = c;
							town = t;
							
							for (User r : t.getUsers())
							{
								if (r.getChunk().equals(c))
								{
									user = r;
								}
							}
						}
					}
				}
				
				if ((town == null) || (chunk == null) || (user == null) || (clicker == null))
					return;
				
				if (town.isOwner(clicker))
				{
					// TODO
				}
				
				if (user.equals(clicker))
				{
					// TODO
				}
			}
		}
	}
	
	@EventHandler // EventPriority.NORMAL: by default
	public void onPlayerToggleFlightEvent(PlayerToggleFlightEvent event)
	{
		if (!event.isFlying())
		{
			return;
		}
		
		if (CTEngine.CHECKFLY)
		{
			new CheckFlyTask(plugin.getWorldGuard(), event.getPlayer(), event.getPlayer().getLocation()).runTask(plugin);
		}
	}
	
	@EventHandler(priority = EventPriority.HIGH)
	public void onPlayerPortalEvent(PlayerPortalEvent event)
	{
		long start = System.nanoTime();
		
		TeleportCause cause = event.getCause();
		if (cause.compareTo(TeleportCause.NETHER_PORTAL) != 0)
		{
			// log to StopWatch
	        sw.setLoad("onPlayerPortalEvent", System.nanoTime() - start);
			return;
		}
		
		event.setCancelled(true);
		
		try
		{
			User user = TownUniverse.getDataSource().getUser(event.getPlayer().getName());
			
			for (String name : TownUniverse.getDataSource().getPortalKeys())
			{
				Portal por = TownUniverse.getDataSource().getPortal(name);
				
				if (por.getIsNether() && !user.getAllowedNether())
				{
					BukkitUtils.sendMessage(event.getPlayer(), Colors.Red, "Whoa there! it appears you have been banned from the nether.");
				}
				
				ProtectedRegion pr = plugin.getWorldGuard().getRegionManager(event.getPlayer().getWorld()).getRegion(name);
				int x = (int) Math.round(event.getPlayer().getLocation().getX());
				int y = (int) Math.round(event.getPlayer().getLocation().getY());
				int z = (int) Math.round(event.getPlayer().getLocation().getZ());
				if ((name != null) && (pr != null))
				{
					DebugMode.log("match: " +  por.getName() + " == " + pr.getId());
					if (pr.contains(x, y, z))
					{
						if (por.isEnabled())
								event.getPlayer().teleport(por.getSendLocation());
						else
							BukkitUtils.sendMessage(event.getPlayer(), Colors.Red, "This portal has been disabled.");
							
						// log to StopWatch
				        sw.setLoad("onPlayerPortalEvent", System.nanoTime() - start);
							
						return;
					}
				}
			}
		}
		catch (NotRegisteredException nre)
		{
			BukkitUtils.severe("A key for the Map didnt point to a value.");
	        sw.setLoad("onPlayerPortalEvent", System.nanoTime() - start);
			return;
		}
		
		// teleport user to default spot.
		event.getPlayer().teleport(MainConfigObject.default_portal.toLocation(Bukkit.getWorld(MainConfigObject.default_portal_world)));
		
		// log to StopWatch
        sw.setLoad("onPlayerPortalEvent", System.nanoTime() - start);
	}
}
