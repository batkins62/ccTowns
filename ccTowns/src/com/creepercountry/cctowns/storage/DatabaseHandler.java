package com.creepercountry.cctowns.storage;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.creepercountry.cctowns.hooks.Vault;
import com.creepercountry.cctowns.objects.Handlers.PvPHandler;
import com.creepercountry.cctowns.objects.town.Town;
import com.creepercountry.cctowns.objects.town.Portal;
import com.creepercountry.cctowns.objects.user.User;
import com.creepercountry.cctowns.util.AlreadyRegisteredException;
import com.creepercountry.cctowns.util.BukkitUtils;
import com.creepercountry.cctowns.util.Colors;
import com.creepercountry.cctowns.util.DebugMode;
import com.creepercountry.cctowns.util.EmptyTownException;
import com.creepercountry.cctowns.util.NotRegisteredException;
import com.creepercountry.cctowns.util.TownException;

public abstract class DatabaseHandler extends DataSource
{
	@Override
	public boolean hasUser(String name)
	{
		return universe.getUserMap().containsKey(name.toLowerCase());
	}
	
	@Override
	public boolean hasPvPHandler(Integer name)
	{
		return universe.getPvPHandlerMap().containsKey(name);
	}

	@Override
	public boolean hasTown(String name)
	{
		return universe.getTownsMap().containsKey(name.toLowerCase());
	}
	
	@Override
	public boolean hasPortal(String name)
	{
		return universe.getPortalMap().containsKey(name.toLowerCase());
	}
	
	@Override
	public boolean hasWatchedCmdsPlayer(String plr)
	{
		return universe.getWatchedCmdsMap().containsKey(plr);
	}
	
	@Override
	public List<User> getUsers(Player player, String[] names)
	{
		List<User> invited = new ArrayList<User>();
		for (String name : names)
		{
			try
			{
				User target = getUser(name);
				invited.add(target);
			}
			catch (TownException x)
			{
				BukkitUtils.sendMessage((CommandSender)player, Colors.Red, x.getMessage());
			}
		}
		return invited;
	}

	@Override
	public List<User> getUsers(String[] names)
	{
		List<User> matches = new ArrayList<User>();
		for (String name : names)
		{
			try
			{
				matches.add(getUser(name));
			}
			catch (NotRegisteredException e)
			{
			}
		}
		return matches;
	}
	
	@Override
	public Portal getPortal(String name) throws NotRegisteredException
	{
		Portal portal = universe.getPortalMap().get(name.toLowerCase());
		DebugMode.log("getPortal(" + name + ")");
		if (portal == null)
			throw new NotRegisteredException("This portal is not found.");
		
		return portal;
	}
	
	@Override
	public PvPHandler getPvPHandler(Integer name) throws NotRegisteredException
	{
		PvPHandler pvphandler = universe.getPvPHandlerMap().get(name);
		DebugMode.log("getPvPHandler(" + name + ")");
		if (pvphandler == null)
			throw new NotRegisteredException("This PvPHandler was not found");
		
		return pvphandler;
	}
	
	@Override
	public List<String> getWatchedCmdsPlayer(String plr) throws NotRegisteredException
	{
		List<String> cmds = universe.getWatchedCmdsMap().get(plr);
		if (cmds == null)
			throw new NotRegisteredException("This player is not found.");
		
		return cmds;
	}

	@Override
	public List<User> getUsers()
	{
		return new ArrayList<User>(universe.getUserMap().values());
	}

	@Override
	public User getUser(String name) throws NotRegisteredException
	{
		User user;
		user = universe.getUserMap().get(name.toLowerCase());
		
		if (user == null)
			throw new NotRegisteredException(String.format("The user '%s' is not registered.", name));

		return user;
	}
	
	@Override
	public User getUserL(String name)
	{
		User user = universe.getUserMap().get(name.toLowerCase());
		return user;
	}

	@Override
	public List<Town> getTowns(String[] names)
	{
		List<Town> matches = new ArrayList<Town>();
		
		for (String name : names)
			try
			{
				matches.add(getTown(name));
			}
			catch (NotRegisteredException e)
			{
			}
		
		return matches;
	}
	
	@Override
	public List<PvPHandler> getPvPHandlers()
	{
		return new ArrayList<PvPHandler>(universe.getPvPHandlerMap().values());
	}

	@Override
	public List<Town> getTowns()
	{
		return new ArrayList<Town>(universe.getTownsMap().values());
	}
	
	@Override
	public List<Portal> getPortals()
	{
		return new ArrayList<Portal>(universe.getPortalMap().values());
	}

	@Override
	public Town getTown(String name) throws NotRegisteredException
	{
		Town town = universe.getTownsMap().get(name.toLowerCase());
		
		if (town == null)
			throw new NotRegisteredException(String.format("The town '%s' is not registered.", name));
		
		return town;
	}
	
	@Override
	public void removeUser(User user)
	{
		Town town = null;

		if (user.hasTown())
			try
			{
				town = user.getTown();
			}
			catch (NotRegisteredException e1)
			{
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		
		try
		{
			if (town != null)
			{
				town.removeUser(user);
				saveTown(town);
			}
			user.clear();
		}
		catch (EmptyTownException e)
		{
			try
			{
				removeTown(town);
			}
			catch (TownException t)
			{
				return;
			}

		}
		catch (NotRegisteredException e)
		{
			// town not registered
			e.printStackTrace();
		}
	}
	
	@Override
	public void removePortal(Portal por)
	{
		Town town = null;

		if (por.hasTown())
			try
			{
				town = por.getTown();
			}
			catch (NotRegisteredException e1)
			{
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		
		try
		{
			if (town != null)
			{
				town.removePortal(por);
				saveTown(town);
			}
			por.clear();
		}
		catch (NotRegisteredException e)
		{
			// town not registered
			e.printStackTrace();
		}
	}
	
	@Override
	public void newUser(String name) throws AlreadyRegisteredException
	{	
		if (universe.getUserMap().containsKey(name.toLowerCase()))
			throw new AlreadyRegisteredException("A user with the name " + name + " is already in use.");

		universe.getUserMap().put(name.toLowerCase(), new User(name));
	}
	
	@Override
	public void newPvPHandler(Integer name, PvPHandler obj) throws AlreadyRegisteredException
	{
		if (universe.getPvPHandlerMap().containsKey(name))
			throw new AlreadyRegisteredException("A PvPHandler with this name is already in use");
		
		universe.getPvPHandlerMap().put(name, obj);
	}
	
	@Override
	public void newWatchedCmdsPlayer(String plr) throws AlreadyRegisteredException
	{
		if (universe.getWatchedCmdsMap().containsKey(plr))
			throw new AlreadyRegisteredException("This player is already being watched.");
		
		universe.getWatchedCmdsMap().put(plr, new ArrayList<String>());
	}
	
	@Override
	public void newPortal(String name) throws AlreadyRegisteredException
	{
		if (universe.getPortalMap().containsKey(name.toLowerCase()))
			throw new AlreadyRegisteredException("that portal was already created.");
		
		universe.getPortalMap().put(name.toLowerCase(), new Portal(name));
	}

	@Override
	public void newTown(String name) throws AlreadyRegisteredException
	{
		if (universe.getTownsMap().containsKey(name.toLowerCase()))
			BukkitUtils.info("The town " + name + " is already in use.");
		
		if (universe.getTownsMap().containsKey(name.toLowerCase()))
			throw new AlreadyRegisteredException("The town " + name + " is already in use.");

		universe.getTownsMap().put(name.toLowerCase(), new Town(name));
	}
	
	@Override
	public void removeUserList(User user)
	{
		String name = user.getName();

		//search and remove from all friends lists
		List<User> toSave = new ArrayList<User>();

		for (User toCheck : toSave)
			saveUser(toCheck);

		//Wipe and delete user
		try
		{
			user.clear();
		}
		catch (EmptyTownException e)
		{
			e.printStackTrace();
		}
		deleteUser(user);

		universe.getUserMap().remove(name.toLowerCase());
		saveUserList();
	}
	
	@Override
	public void removePvPHandler(PvPHandler name)
	{
		universe.getPvPHandlerMap().remove(name);
	}
	
	@Override
	public void removeWatchedList(String name)
	{
		deleteWatchedCmdsPlayer(name);
		universe.getWatchedCmdsMap().remove(name);
		saveWatchedCmdsPlayerList();
	}
	
	@Override
	public void removePortalList(Portal portal)
	{
		String name = portal.getName();

		//search and remove from all friends lists
		List<Portal> toSave = new ArrayList<Portal>();

		for (Portal toCheck : toSave)
			savePortal(toCheck);

		portal.clear();
		deletePortal(portal);

		universe.getPortalMap().remove(name.toLowerCase());
		savePortalList();
	}

	@Override
	public void removeTown(Town town) throws TownException
	{
		List<User> toSave = new ArrayList<User>(town.getUsers());
		boolean refundfailed = false;
		String owner = null;
		double bal = 0.0;
		
		for (User user : toSave)
		{
			if (user.isOwner())
			{
				Player player = Bukkit.getPlayer(user.getName());
				if (!(player == null))
				{
					try
					{
						((Vault)plugin.getDependancyManager().getHook("Vault")).payuser(player, town.getBalance(), "refund on delete town.");
						town.setBalance(0);
					}
					catch (NotRegisteredException nre)
					{
						BukkitUtils.severe(nre.getMessage() + town.getBalance() + player.getDisplayName());
					}
				}
				else
				{
					refundfailed = true;
					bal = town.getBalance();
					owner = town.getOwner().getName();
				}
			}
			removeUser(user);
			saveUser(user);
		}

		universe.getTownsMap().remove(town.getName().toLowerCase());

		deleteTown(town);
		saveTownList();
		
		if (refundfailed)
			throw new TownException("Town was deleted but " + bal + " was not refunded to " + owner);
	}
	
	@Override
	public Set<String> getUserKeys()
	{
		return universe.getUserMap().keySet();
	}
	
	@Override
	public Set<String> getWatchedCmdsPlayersKeys()
	{
		return universe.getWatchedCmdsMap().keySet();
	}
	
	@Override
	public Set<String> getPortalKeys()
	{
		return universe.getPortalMap().keySet();
	}

	@Override
	public Set<String> getTownsKeys()
	{
		return universe.getTownsMap().keySet();
	}
	
	@Override
	public List<Town> getTownsWithoutNation()
	{
		List<Town> townFilter = new ArrayList<Town>();
		
		for (Town town : getTowns())
			townFilter.add(town);
		
		return townFilter;
	}

	@Override
	public List<User> getUsersWithoutTown()
	{
		List<User> userFilter = new ArrayList<User>();
		
		for (User user : universe.getUserMap().values())
			if (!user.hasTown())
				userFilter.add(user);
		
		return userFilter;
	}
}
