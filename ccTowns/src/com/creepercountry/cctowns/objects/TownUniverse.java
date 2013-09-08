package com.creepercountry.cctowns.objects;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import net.milkbowl.vault.economy.EconomyResponse;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.block.SignChangeEvent;
import com.creepercountry.cctowns.hooks.Vault;
import com.creepercountry.cctowns.main.CTEngine;
import com.creepercountry.cctowns.main.CTPlugin;
import com.creepercountry.cctowns.main.config.MainConfigObject;
import com.creepercountry.cctowns.objects.Handlers.PvPHandler;
import com.creepercountry.cctowns.objects.town.Portal;
import com.creepercountry.cctowns.objects.town.Town;
import com.creepercountry.cctowns.objects.user.User;
import com.creepercountry.cctowns.storage.DataSource;
import com.creepercountry.cctowns.storage.FileMgmt;
import com.creepercountry.cctowns.storage.FlatFileSource;
import com.creepercountry.cctowns.util.AlreadyRegisteredException;
import com.creepercountry.cctowns.util.BukkitUtils;
import com.creepercountry.cctowns.util.Colors;
import com.creepercountry.cctowns.util.DebugMode;
import com.creepercountry.cctowns.util.DonationExpiredException;
import com.creepercountry.cctowns.util.NotRegisteredException;
import com.creepercountry.cctowns.util.tasks.ViolationDecremationTask;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;

public class TownUniverse extends MasterObject
{
	private static CTPlugin plugin;

	protected Hashtable<String, User> users = new Hashtable<String, User>();
	protected Hashtable<String, Town> towns = new Hashtable<String, Town>();
	protected Hashtable<String, Portal> portals = new Hashtable<String, Portal>();
	protected Hashtable<String, List<String>> watchedcmds = new Hashtable<String, List<String>>();
	protected Hashtable<Integer, PvPHandler> pvphandlers = new Hashtable<Integer, PvPHandler>();
	
	private static DataSource dataSource;
	private String rootFolder;
	private HashSet<User> onlineDonators = new HashSet<User>();

	public TownUniverse()
	{
		setName("");
		rootFolder = "";
	}

	public TownUniverse(String rootFolder)
	{
		setName("");
		this.rootFolder = rootFolder;
	}

	public TownUniverse(CTPlugin plugin)
	{
		setName("");
		TownUniverse.plugin = plugin;
	}
	
	public void onLogin(Player player) throws AlreadyRegisteredException, NotRegisteredException
	{
		if (!player.isOnline())
			return;
		
		if (!Vault.perms.has(player, "ct.account.create.self"))
			return;

		// Test and kick any players with invalid names.
		if ((player.getName().trim() == null) || (player.getName().contains(" ")))
		{
			player.kickPlayer("Invalid name!");
			return;
		}
		else if ((player.getName().contains("notch") || player.getName().contains("jeb_")))
		{
			player.kickPlayer("This login has been recorded, violation in accordance to C.R.S § 1 b");
			return;
		}
		
		User user;

		if (!getDataSource().hasUser(player.getName()))
		{
			getDataSource().newUser(player.getName());
			user = getDataSource().getUser(player.getName());

			BukkitUtils.sendMessage(player, Colors.Blue, "Welcome! type \'/town help\' to explore towns to join.");

			getDataSource().saveUser(user);
			getDataSource().saveUserList();

		}
		else
		{		
			user = getDataSource().getUser(player.getName());
			user.setLastOnline(System.currentTimeMillis());

			if (user.hasMessages())
				BukkitUtils.sendMessage(player, Colors.Blue, "You have " + Colors.Red + user.msgQuantity() + Colors.Blue + " TOWN messages. use \'/town mail read\' to read.");
			
			getDataSource().saveUser(user);
		}
		
		try
		{
			boolean isDonator = validateDonator(user);
			
			if (isDonator)
			{
				Calendar ter = GregorianCalendar.getInstance(plugin.getTimeZone(), plugin.getLocale());
				ter.setTimeInMillis(user.getPurchaseDate());
				
				BukkitUtils.sendMessage(Bukkit.getPlayer(user.getName()), Colors.Green, String.format(plugin.getLocale(), "Welcome Back Valid Donator!%s purchase date: %s, package: %s",
						Colors.Gold,
						DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT, plugin.getLocale()).format(ter.getTime()),
						user.getDonationPackage()));
				
				if (user.hasFlyViolation())
					BukkitUtils.sendMessage(player, Colors.Red, "You have a Fly Violation Level of: " + Colors.Gold + Integer.toString(user.getFlyViolation()));
				
				CTEngine.CHECKFLY = true;
				CTEngine.DECREMENTATION = true;
			}
		}
		catch (DonationExpiredException dee)
		{
			for (OfflinePlayer op : Bukkit.getOperators())
				try
				{
					if (op.isOnline())
					{
						BukkitUtils.sendMessage(op.getPlayer(), Colors.Red, dee.getMessage());
						continue;
					}
					
					User uop = dataSource.getUser(op.getName());
					uop.addMessage(dee.getMessage());
					dataSource.saveUser(uop);
				}
				catch (NotRegisteredException nre)
				{
					continue;
				}
		}
	}

	public void onLogout(Player player)
	{
		User user;
		
		try
		{
			user = getDataSource().getUser(player.getName());
		}
		catch (NotRegisteredException e)
		{
			return;
		}
		
		if (onlineDonators.contains(user))
			onlineDonators.remove(user);
		
		/**
		 * @deprecated 8/8/2013
		 * TODO: REMOVE TO ENABLE (BROKE)
		 */
		if (onlineDonators.isEmpty() && plugin.isOnline("notch"))
		{
			CTEngine.DECREMENTATION = false;
			CTEngine.CHECKFLY = false;
			BukkitUtils.warning("========================================");
			BukkitUtils.warning(Colors.Red + "No donators online! Cancelling Check Tasks (DECREMENTATION & CHECKFLY");
			BukkitUtils.warning("Check Tasks will be enabled next time a donator joins!");
			BukkitUtils.warning("========================================");
		}
			
		user.setLastOnline(System.currentTimeMillis());
		
		getDataSource().saveUser(user);
	}
	
	public void runSweeps()
	{
		if (!CTEngine.DECREMENTATION)
		{	
			for (User usr : getOnlineRegisteredUsers())
				if (usr.hasFlyViolation())
				{
					new ViolationDecremationTask().runTaskTimerAsynchronously(plugin, 60L, 36000L);
					CTEngine.DECREMENTATION = true;
					BukkitUtils.warning("========================================");
					BukkitUtils.warning(Colors.Red + "Donators online with VL! Creating Check Task (DECREMENTATION)");
					BukkitUtils.warning("This Task will be disabled when either no donators online, or no VL detected.");
					BukkitUtils.warning("========================================");
					break;
				}
		}
			
		if (!CTEngine.CHECKFLY)
		{
			CTEngine.CHECKFLY = true;
		}
	}
	
	/**
	 * @param user to be validated.
	 * @return false if not a donator
	 * @throws DonationExpiredException if donator expired.
	 */
	public boolean validateDonator(User user) throws DonationExpiredException
	{
		if (user.isValid())
		{
			Calendar cur = GregorianCalendar.getInstance(plugin.getTimeZone(), plugin.getLocale());
			cur.setTimeInMillis(System.currentTimeMillis());
			Calendar ter = GregorianCalendar.getInstance(plugin.getTimeZone(), plugin.getLocale());
			ter.setTimeInMillis(user.getPurchaseDate());
			ter.add(Calendar.MILLISECOND, Long.valueOf(user.getTerm()).intValue());
			
			if (cur.getTime().after(ter.getTime()))
				throw new DonationExpiredException(String.format("User %s has an expired donation package, but valid is still set", user.getName()));
		}
		else
			return false;
		
		if (!onlineDonators.contains(user) && plugin.isOnline(user.getName()))
			onlineDonators.add(user);
		
		return true;
	}
	
	public void onPlotSignEvent(SignChangeEvent event)
	{
		// check for global perms
		DebugMode.log("checking for global perms");
		if (!Vault.perms.has(event.getPlayer(), "ct.plot.create"))
		{
			BukkitUtils.sendMessage(event.getPlayer(), Colors.Red, "You do not have permission to create plots!");
			if (event.getBlock().breakNaturally())
				return;
		}
		
		// get the variables
		DebugMode.log("getting the variables");
		User user;
		Town town;
		double price = 0;
		try { user = TownUniverse.getDataSource().getUser(event.getPlayer().getName()); } catch (NotRegisteredException nre) { event.getBlock().breakNaturally(); return; }
		try { town = user.getTown(); } catch (NotRegisteredException nre) { event.getBlock().breakNaturally(); return; }
		try
		{
			price = Double.parseDouble(event.getLine(1).replace("$", ""));
			if (price > 20000)
				if (event.getBlock().breakNaturally())
					return;
		}
		catch (NumberFormatException nfe)
		{
			BukkitUtils.sendMessage(event.getPlayer(), Colors.Red, "[ERRORxCTBL64] " + "Line 2 of the sign must be the price: $1.00");
			if (event.getBlock().breakNaturally())
				return;
		}
		
		ProtectedRegion pr = plugin.getWorldGuard().getRegionManager(event.getPlayer().getWorld()).getRegionExact(town.getRegion());
		Player player = event.getPlayer();
		
		// check town perms
		DebugMode.log("checking for town perms");
		if (town.hasChunk(event.getBlock().getChunk()))
			if (event.getBlock().breakNaturally())
				return;
		if (!town.isStaff(user))
			if (event.getBlock().breakNaturally())
				return;
		Location loc = event.getBlock().getLocation();
		if (!plugin.getWorldGuard().canBuild(player, loc) && !pr.contains(new Vector(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ())))
			if (event.getBlock().breakNaturally())
				return;
		
		// withdraw the costs to make plot
		DebugMode.log("withdrawing the costs of plot");
		EconomyResponse r = Vault.econ.withdrawPlayer(player.getName(), MainConfigObject.plotcost);
		if (r.transactionSuccess())
		{
			BukkitUtils.sendMessage(player, Colors.Rose, String.format(plugin.getLocale(), "You had %s removed from your holdings.", Double.toString(r.amount)));
			BukkitUtils.sendMessage(player, Colors.Gold, String.format(plugin.getLocale(), "You now have" + Colors.Green + " %s " + Colors.Gold + "in your holdings", Double.toString(r.balance)));
		}
		else
		{
			BukkitUtils.sendMessage(player, Colors.Red, "[ERRORxCTBL56] " + r.errorMessage);
			if (event.getBlock().breakNaturally())
				return;
		}
					
		// create the plot
		DebugMode.log("creating the plot");
		try
		{
			town = user.getTown();
			try
			{
				town.addChunk(event.getBlock().getChunk());
			}
			catch (AlreadyRegisteredException are)
			{
				BukkitUtils.sendMessage(player, Colors.Red, "Please alert administration, something went wrong with this.");
				BukkitUtils.severe(are.getLocalizedMessage());
			}
		}
		catch (NotRegisteredException nre)
		{
			BukkitUtils.sendMessage(player, Colors.Red, "[ERRORxCTBL82] " + nre.getMessage());
			EconomyResponse refund = Vault.econ.depositPlayer(player.getName(), MainConfigObject.plotcost);
			if (refund.transactionSuccess())
				if (event.getBlock().breakNaturally())
				{
					BukkitUtils.sendMessage(player, Colors.Rose, String.format(plugin.getLocale(), "You had %s refunded back to your holdings.", r.amount));
					return;
				}
		}
					
		// alter the sign
		DebugMode.log("Setting final sign");
		try
		{
			event.setLine(0, Colors.Navy + "[FOR SALE]");
			event.setLine(1, "$" + Double.toString(price).intern());
			event.setLine(2, "right click");
			event.setLine(3, "to purchase");
			
			// save all this
			dataSource.saveTown(town);
			BukkitUtils.info("Saving town: " + town.getName());
		}
		catch (IndexOutOfBoundsException ioob)
		{
			BukkitUtils.severe(ioob.getLocalizedMessage());
			BukkitUtils.sendMessage(player, Colors.Red, "[ERRORxCTBL113] SCREENSHOT THIS ERROR, SEND TO ADMINISTRATION AT ONCE");
			BukkitUtils.sendMessage(player, Colors.Red, Double.toString(MainConfigObject.plotcost) + " was NOT refunded to this user");
			BukkitUtils.sendMessage(player, Colors.Red, "Date: " + new Date(System.currentTimeMillis()).toString());
			BukkitUtils.severe("[ERRORxCTBL113] SCREENSHOT THIS ERROR, SEND TO ADMINISTRATION AT ONCE");
			BukkitUtils.severe(Double.toString(MainConfigObject.plotcost) + " was NOT refunded to this user");
			BukkitUtils.severe("Date: " + new Date(System.currentTimeMillis()).toString());
		}
	}
	
	public static Player getPlayer(User user) throws Exception
	{
		for (Player player : getOnlinePlayers())
			if (player.getName().equals(user.getName()))
				return player;
		
		throw new Exception(String.format("%s is not online", user.getName()));
	}

	public static Player[] getOnlinePlayers()
	{
		return Bukkit.getOnlinePlayers();
	}
	
	public static List<User> getOnlineRegisteredUsers()
	{
		List<User> users = new ArrayList<User>();
		try
		{
			for (Player plr : getOnlinePlayers())
			{
				users.add(dataSource.getUser(plr.getName()));
			}
		}
		catch (NotRegisteredException nre)
		{
			BukkitUtils.severe(nre.getMessage());
		}
		return users;
	}

	public static List<Player> getOnlinePlayers(Town town)
	{
		ArrayList<Player> players = new ArrayList<Player>();
		for (Player player : getOnlinePlayers())
			if (town.hasUser(player.getName()))
				players.add(player);
		return players;
	}
	
	public boolean isActiveUser(User user)
	{
		return ((System.currentTimeMillis() - user.getLastOnline() < (20 * MainConfigObject.user_inactive)) || (plugin.isOnline(user.getName())));
	}
	
	public boolean loadSettings()
	{
		System.out.println("[ccTowns] Loading Settings...");
		FileMgmt.checkFolders(new String[] { getRootFolder(), getRootFolder() });

		towns.clear();
		users.clear();
		portals.clear();
		System.out.println("[ccTowns] Cleared variables.");
		
		if (!loadDatabase("FlatFileSource"))
		{
			System.out.println("[ccTowns] Error: Failed to load!");
			return false;
		}

		return true;
	}
	
	public String getRootFolder()
	{
		if (plugin != null)
			return plugin.getDataFolder().getPath();
		else
			return rootFolder;
	}
	
	public boolean loadDatabase(String databaseType)
	{
		System.out.println("[ccTowns] Loading Database...");
		try
		{
			setDataSource(databaseType);
		}
		catch (UnsupportedOperationException e)
		{
			return false;
		}
		getDataSource().initialize(plugin, this);

		return getDataSource().loadAll();
	}

	public void setDataSource(String databaseType)
	{
		setDataSource(new FlatFileSource());
	}

	public void setDataSource(DataSource dataSource)
	{
		TownUniverse.dataSource = dataSource;
	}

	public static DataSource getDataSource()
	{
		return dataSource;
	}
	
	/**
	 * @return Hashtable of watchedcmds
	 */
	public Hashtable<String, List<String>> getWatchedCmdsMap()
	{
		return this.watchedcmds;
	}
	
	/**
	 * @return Hashtable of PvPHandler
	 */
	public Hashtable<Integer, PvPHandler> getPvPHandlerMap()
	{
		return this.pvphandlers;
	}
	
	/**
	 * @return Hashtable of users
	 */
	public Hashtable<String, User> getUserMap()
	{
		return this.users;
	}
	
	/**
	 * @return Hashtable of Towns
	 */
	public Hashtable<String, Town> getTownsMap()
	{
		return this.towns;
	}
	
	/**
	 * @return Hashtable of Portals
	 */
	public Hashtable<String, Portal> getPortalMap()
	{
		return this.portals;
	}
	
	public static CTPlugin getPlugin()
	{
		return plugin;
	}

	public void setPlugin(CTPlugin plugin)
	{
		TownUniverse.plugin = plugin;
	}
}
