package com.creepercountry.cctowns.storage;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

import org.bukkit.entity.Player;

import com.creepercountry.cctowns.main.CTPlugin;
import com.creepercountry.cctowns.objects.TownUniverse;
import com.creepercountry.cctowns.objects.Handlers.PvPHandler;
import com.creepercountry.cctowns.objects.town.Portal;
import com.creepercountry.cctowns.objects.town.Town;
import com.creepercountry.cctowns.objects.user.User;
import com.creepercountry.cctowns.util.AlreadyRegisteredException;
import com.creepercountry.cctowns.util.DebugMode;
import com.creepercountry.cctowns.util.NotRegisteredException;
import com.creepercountry.cctowns.util.TownException;

public abstract class DataSource
{
	protected TownUniverse universe;
	protected CTPlugin plugin;
	protected boolean firstRun = true;

	public void initialize(CTPlugin plugin, TownUniverse universe)
	{
		this.universe = universe;
		this.plugin = plugin;
	}
	
	public void deleteUnusedUserFiles()
	{

	}

	public boolean confirmContinuation(String msg)
	{
		Boolean choice = null;
		String input = null;
		while (choice == null) {
			System.out.println(msg);
			System.out.print(" Continue (y/n): ");
			Scanner in = new Scanner(System.in);
			input = in.next();
			input = input.toLowerCase();
			if (input.equals("y") || input.equals("yes"))
			{
				in.close();
				return true;
			}
			else if (input.equals("n") || input.equals("no"))
			{
				in.close();
				return false;
			}
		}
		System.out.println("[ccTowns] Error recieving input, exiting.");
		return false;
	}

	public boolean loadAll()
	{
		System.out.println("[ccTowns] Loading data...");
		return loadTownList() && loadUserList() && loadUsers() && loadPortalList()
				&& loadPortals() && loadTowns() && loadWatchedList() && loadWatched();
	}

	public boolean saveAll()
	{
		return saveTownList() && saveUserList() && saveTowns() && saveUsers() && savePortalList()
				 && savePortals() && saveWatchedCmdsPlayerList() && saveWatched();
	}

	abstract public boolean loadUserList();
	abstract public boolean loadTownList();
	abstract public boolean loadPortalList();

	abstract public boolean loadUser(User user);
	abstract public boolean loadTown(Town town);
	abstract public boolean loadPortal(Portal portal);

	abstract public boolean saveUserList();
	abstract public boolean saveTownList();
	abstract public boolean savePortalList();

	abstract public boolean saveUser(User user);
	abstract public boolean savePortal(Portal portal);
	abstract public boolean saveTown(Town town);

	abstract public void deleteUser(User user);
	abstract public void deletePortal(Portal portal);
	abstract public void deleteTown(Town town);
	abstract public void deleteFile(String file);

	/*
	 * Load Functions
	 */
	
	public boolean loadUsers()
	{
		DebugMode.log("Loading Users");

		List<User> toRemove = new ArrayList<User>();

		for (User user : new ArrayList<User>(getUsers()))
			if (!loadUser(user))
			{
				System.out.println("[ccTowns] Loading Error: Could not read user data '" + user.getName() + "'.");
				toRemove.add(user);
				//return false;
			}

		// Remove any user which failed to load.
		for (User user : toRemove)
		{
			System.out.println("[ccTowns] Loading Error: Removing user data for '" + user.getName() + "'.");
			removeUserList(user);
		}

		return true;
	}

	public boolean loadTowns()
	{
		DebugMode.log("Loading Towns");
		for (Town town : getTowns())
			if (!loadTown(town))
			{
				System.out.println("[ccTowns] Loading Error: Could not read town data " + town.getName() + "'.");
				return false;
			}
		return true;
	}
	
	public boolean loadWatched()
	{
		DebugMode.log("Loading Watched");
		for (String name : getWatchedCmdsPlayersKeys())
			if (!loadWatchedCmdsPlayer(name))
			{
				System.out.println("[ccTowns] Loading Error: Could not read watched data " + name + "'.");
				return false;
			}
		return true;
	}
	
	public boolean loadPortals()
	{
		DebugMode.log("Loading Portals");
		for (Portal portal : getPortals())
			if (!loadPortal(portal))
			{
				System.out.println("[ccTowns] Loading Error: Could not read portal data " + portal.getName() + "'.");
				return false;
			}
		return true;
	}
	
	/*
	 * Save functions
	 */
	
	public boolean saveUsers()
	{
		DebugMode.log("Saving Users");
		for (User user : getUsers())
			saveUser(user);
		return true;
	}
	
	public boolean savePortals()
	{
		DebugMode.log("Saving Portals");
		for (Portal portal : getPortals())
			savePortal(portal);
		return true;
	}
	
	public boolean saveWatched()
	{
		DebugMode.log("Saving Watched");
		for (String name : getWatchedCmdsPlayersKeys())
			saveWatchedPlayer(name);
		return true;
	}

	public boolean saveTowns()
	{
		DebugMode.log("Saving Towns");
		for (Town town : getTowns())
			saveTown(town);
		return true;
	}
	
	// Database functions
	abstract public List<User> getUsers(Player player, String[] names);
	abstract public List<User> getUsers();
	abstract public List<User> getUsers(String[] names);
	abstract public User getUser(String name) throws NotRegisteredException;
	abstract public User getUserL(String name);

	abstract public void removeUserList(User user);
	abstract public void removePortalList(Portal portal);
	abstract public boolean hasUser(String name);
	abstract public boolean hasTown(String name);
	abstract public boolean hasPortal(String name);
	abstract public boolean hasPvPHandler(Integer name);

	abstract public List<Town> getTowns(String[] names);
	abstract public List<Town> getTowns();
	abstract public List<Portal> getPortals();
	abstract public List<PvPHandler> getPvPHandlers();
	abstract public Town getTown(String name) throws NotRegisteredException;
	abstract public Portal getPortal(String name) throws NotRegisteredException;
	abstract public PvPHandler getPvPHandler(Integer name) throws NotRegisteredException;
	abstract public void removeUser(User user);
	abstract public void removePvPHandler(PvPHandler name);
	abstract public void newUser(String name) throws AlreadyRegisteredException;
	abstract public void newTown(String name) throws AlreadyRegisteredException;
	abstract public void newPvPHandler(Integer name, PvPHandler obj) throws AlreadyRegisteredException;
	abstract public void newPortal(String name) throws AlreadyRegisteredException;
	abstract public void removeTown(Town town) throws TownException;
	abstract public void removePortal(Portal por) throws TownException;

	abstract public Set<String> getUserKeys();
	abstract public Set<String> getTownsKeys();
	abstract public Set<String> getPortalKeys();

	abstract public List<Town> getTownsWithoutNation();
	abstract public List<User> getUsersWithoutTown();

	abstract public List<String> getWatchedCmdsPlayer(String plr) throws NotRegisteredException;
	abstract public boolean hasWatchedCmdsPlayer(String plr);
	abstract public void newWatchedCmdsPlayer(String plr) throws AlreadyRegisteredException;
	abstract public Set<String> getWatchedCmdsPlayersKeys();
	abstract public boolean saveWatchedCmdsPlayerList();
	abstract public boolean loadWatchedList();
	abstract public boolean loadWatchedCmdsPlayer(String name);
	abstract public boolean saveWatchedPlayer(String name);
	abstract public void deleteWatchedCmdsPlayer(String name);
	abstract public void removeWatchedList(String name);
}
