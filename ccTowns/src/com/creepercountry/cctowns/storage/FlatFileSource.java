package com.creepercountry.cctowns.storage;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;

import com.creepercountry.cctowns.main.CTPlugin;
import com.creepercountry.cctowns.objects.TownUniverse;
import com.creepercountry.cctowns.objects.town.Portal;
import com.creepercountry.cctowns.objects.town.Town;
import com.creepercountry.cctowns.objects.user.User;
import com.creepercountry.cctowns.util.AlreadyRegisteredException;
import com.creepercountry.cctowns.util.BukkitUtils;
import com.creepercountry.cctowns.util.DebugMode;

public class FlatFileSource extends DatabaseHandler
{
	protected final String newLine = System.getProperty("line.separator");
	protected String rootFolder = "";
	protected String dataFolder = FileMgmt.fileSeparator() + "data";

	@Override
	public void initialize(CTPlugin plugin, TownUniverse universe)
	{
		System.out.println("[ccTowns] Initialising dataSource for use...");
		this.universe = universe;
		this.plugin = plugin;
		this.rootFolder = universe.getRootFolder();

		// Create files and folders if non-existent
		try
		{
			FileMgmt.checkFolders(new String[]
				{ 
					rootFolder,
					rootFolder + dataFolder,
					rootFolder + dataFolder + FileMgmt.fileSeparator() + "users",
					rootFolder + dataFolder + FileMgmt.fileSeparator() + "users" + FileMgmt.fileSeparator() + "banned",
					rootFolder + dataFolder + FileMgmt.fileSeparator() + "watched",
					rootFolder + dataFolder + FileMgmt.fileSeparator() + "towns",
					rootFolder + dataFolder + FileMgmt.fileSeparator() + "towns" + FileMgmt.fileSeparator() + "deleted",
					rootFolder + dataFolder + FileMgmt.fileSeparator() + "staff",
					rootFolder + dataFolder + FileMgmt.fileSeparator() + "portals",
					rootFolder + dataFolder + FileMgmt.fileSeparator() + "portals" + FileMgmt.fileSeparator() + "removed",
				});
				FileMgmt.checkFiles(new String[]
					{
						rootFolder + dataFolder + FileMgmt.fileSeparator() + "users.txt",
						rootFolder + dataFolder + FileMgmt.fileSeparator() + "towns.txt",
						rootFolder + dataFolder + FileMgmt.fileSeparator() + "staff" + FileMgmt.fileSeparator() + "serveradmins.txt",
						rootFolder + dataFolder + FileMgmt.fileSeparator() + "staff" + FileMgmt.fileSeparator() + "servermods.txt",
						rootFolder + dataFolder + FileMgmt.fileSeparator() + "portals.txt",
						rootFolder + dataFolder + FileMgmt.fileSeparator() + "watchedcmdsplayers.txt",
					});
		}
		catch (IOException e)
		{
			System.out.println("[ccTowns] Error: Could not create flatfile default files and folders.");
		}
	}
	
	@Override
	public void deleteUnusedUserFiles()
	{
		String path;
		Set<String> names;

		path = rootFolder + dataFolder + FileMgmt.fileSeparator() + "users";
		names = getUserKeys();

		FileMgmt.deleteUnusedFiles(new File(path), names);

		path = rootFolder + dataFolder + FileMgmt.fileSeparator() + "towns";
		names = getTownsKeys();

		FileMgmt.deleteUnusedFiles(new File(path), names);
	}
	
	public String getUserFilename(User user)
	{
		return rootFolder + dataFolder + FileMgmt.fileSeparator() + "users" + FileMgmt.fileSeparator() + user.getName() + ".txt";
	}
	
	public String getPortalFilename(Portal portal)
	{
		return rootFolder + dataFolder + FileMgmt.fileSeparator() + "portals"+ FileMgmt.fileSeparator() + portal.getName() + ".txt";
	}

	public String getTownFilename(Town town)
	{
		return rootFolder + dataFolder + FileMgmt.fileSeparator() + "towns" + FileMgmt.fileSeparator() + town.getName() + ".txt";
	}
	
	public String getWatchedFilename(String name)
	{
		return rootFolder + dataFolder + FileMgmt.fileSeparator() + "watched" + FileMgmt.fileSeparator() + name + ".txt"; 
	}
	
	/*
	 * Load keys
	 */
	
	@Override
	public boolean loadWatchedList()
	{
		DebugMode.log("Loading WatchedCmdsPlayers List");
		String line;
		BufferedReader fin;

		try
		{
			fin = new BufferedReader(new FileReader(rootFolder + dataFolder + FileMgmt.fileSeparator() + "watchedcmdsplayers.txt"));
		}
		catch (FileNotFoundException e)
		{
			e.printStackTrace();
			return false;
		}
		try
		{
			while ((line = fin.readLine()) != null)
				if (!line.equals(""))
					newWatchedCmdsPlayer(line);
		}
		catch (AlreadyRegisteredException e)
		{
			e.printStackTrace();
			confirmContinuation(e.getMessage() + " | Continuing will delete it's data.");
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return false;
		}
		finally
		{
			try
			{
				fin.close();
			}
			catch (IOException e)
			{
				// Failed to close file.
			}
		}
		return true;
	}

	@Override
	public boolean loadUserList()
	{
		DebugMode.log("Loading User List");
		String line;
		BufferedReader fin;

		try
		{
			fin = new BufferedReader(new FileReader(rootFolder + dataFolder + FileMgmt.fileSeparator() + "users.txt"));
		}
		catch (FileNotFoundException e)
		{
			e.printStackTrace();
			return false;
		}
		try
		{
			while ((line = fin.readLine()) != null)
				if (!line.equals(""))
					newUser(line);
		}
		catch (AlreadyRegisteredException e)
		{
			e.printStackTrace();
			confirmContinuation(e.getMessage() + " | Continuing will delete it's data.");
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return false;
		}
		finally
		{
			try
			{
				fin.close();
			}
			catch (IOException e)
			{
				// Failed to close file.
			}
		}
		return true;
	}
	
	@Override
	public boolean loadPortalList()
	{
		DebugMode.log("Loading Portal List");
		String line;
		BufferedReader fin;

		try
		{
			fin = new BufferedReader(new FileReader(rootFolder + dataFolder + FileMgmt.fileSeparator() + "portals.txt"));
		}
		catch (FileNotFoundException e)
		{
			e.printStackTrace();
			return false;
		}
		try
		{
			while ((line = fin.readLine()) != null)
				if (!line.equals(""))
					newPortal(line);
		}
		catch (AlreadyRegisteredException e)
		{
			e.printStackTrace();
			confirmContinuation(e.getMessage() + " | Continuing will delete it's data.");
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return false;
		}
		finally
		{
			try
			{
				fin.close();
			}
			catch (IOException e)
			{
				// Failed to close file.
			}
		}
		return true;
	}

	@Override
	public boolean loadTownList()
	{
		DebugMode.log("Loading Town List");
		String line;
		BufferedReader fin;

		try
		{
			fin = new BufferedReader(new FileReader(rootFolder + dataFolder + FileMgmt.fileSeparator() + "towns.txt"));
		}
		catch (FileNotFoundException e)
		{
			e.printStackTrace();
			return false;
		}
		try
		{
			while ((line = fin.readLine()) != null)
				if (!line.equals(""))
					newTown(line);
		}
		catch (AlreadyRegisteredException e)
		{
			e.printStackTrace();
			confirmContinuation(e.getMessage() + " | Continuing will delete it's data.");
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return false;
		}
		finally
		{
			try
			{
				fin.close();
			}
			catch (IOException e)
			{
				// Failed to close file.
			}
		}
		return true;
	}
	
	@Override
	public boolean loadWatchedCmdsPlayer(String name)
	{
		DebugMode.log("Loading Watched Player " + name);
		String line;
		BufferedReader wfin;
		
		try
		{
			wfin = new BufferedReader(new FileReader(getWatchedFilename(name)));
		}
		catch (FileNotFoundException e)
		{
			e.printStackTrace();
			return false;
		}
		
		try
		{
			List<String> cmds = new ArrayList<String>();
			while ((line = wfin.readLine()) != null)
				if (!line.equals(""))
					cmds.add(line);
			universe.getWatchedCmdsMap().put(name, cmds);
		}
		catch (IOException io)
		{
			BukkitUtils.severe(io.getMessage());
		}
		finally
		{
			try
			{
				wfin.close();
			}
			catch (IOException ioe)
			{
			}
		}

		return true;
	}
	
	/*
	 * Load individual town object
	 */

	@Override
	public boolean loadUser(User user)
	{
		String line;
		String[] tokens;
		String path = getUserFilename(user);
		File fileUser = new File(path);
		
		if (fileUser.exists() && fileUser.isFile())
		{
			try
			{
				KeyValueFile kvFile = new KeyValueFile(path);
				
				/*
				 * Load IDonator object
				 */
				
				line = kvFile.get("purchasedate");
				if (line != null)
					user.setPurchaseDate(Long.parseLong(line));
				
				line = kvFile.get("donationpackage");
				if (line != null)
					user.setDonationPackage(line);
				
				line = kvFile.get("flyviolation");
				if (line != null)
					user.setFlyViolation(Integer.parseInt(line));
				
				line = kvFile.get("canfly");
				if (line != null)
					user.setCanFly(Boolean.parseBoolean(line));
				
				line = kvFile.get("valid");
				if (line != null)
					user.setValid(Boolean.parseBoolean(line));
				
				line = kvFile.get("term");
				if (line != null)
					user.setTerm(Long.parseLong(line));
				
				/*
				 * Load IFighter object
				 */
				
				line = kvFile.get("lastpvp");
				if (line != null)
					user.setLastPvP(Long.parseLong(line));
				
				line = kvFile.get("pvpchamp");
				if (line != null)
					user.setPvPChamp(line);
				
				line = kvFile.get("pvplost");
				if (line != null)
					user.setPvPLost(Integer.parseInt(line));
				
				line = kvFile.get("pvpwon");
				if (line != null)
					user.setPvPWon(Boolean.parseBoolean(line));
				
				/*
				 * Load IResident object
				 */
				
				line = kvFile.get("chunk");
				if (line != null)
				{
					tokens = line.split(":");
					user.setChunk(Bukkit.getWorld(tokens[0]).getChunkAt(Integer.parseInt(tokens[1]), Integer.parseInt(tokens[2])));
				}
				
				line = kvFile.get("nagpstone");
				if (line != null)
					user.setNagPstone(Boolean.parseBoolean(line));	
				
				line = kvFile.get("lastOnline");
				if (line != null)
					user.setLastOnline(Long.parseLong(kvFile.get("lastOnline")));
				
				line = kvFile.get("messages");
				if (line != null)
				{
					tokens = line.split(",");
					for (String token : tokens)
						if (!token.isEmpty())
							if (token != null)
								user.addMessage(token);
				}
				
				line = kvFile.get("rank");
				if (line != null)
					user.setRank(line);
				
				line = kvFile.get("allowedNether");
				if (line != null)
					user.setAllowedNether(Boolean.parseBoolean(line));

				line = kvFile.get("town");
				if (line != null)
					user.setTown(getTown(line));
				
				line = kvFile.get("raffletickets");
				if (line != null)
				{
					tokens = line.split(",");
					for (String token : tokens)
						if (!token.isEmpty())
							if (token != null)
								user.addRaffleTicket(Integer.parseInt(token));
				}
			}
			catch (Exception e)
			{
				System.out.println("[ccTowns] Loading Error: Exception while reading user file " + user.getName());
				return false;
			}

			return true;
		}
		else
			return false;
	}
	
	@Override
	public boolean loadPortal(Portal portal)
	{
		System.out.println("[ccTowns] Loading Portal " + portal.getName());
		String line;
		String path = getPortalFilename(portal);
		File filePortal = new File(path);
		
		if (filePortal.exists() && filePortal.isFile())
		{
			try
			{
				KeyValueFile kvFile = new KeyValueFile(path);
				line = kvFile.get("town");
				if (line != null)
					portal.setTown(getTown(line));
				
				line = kvFile.get("world");
				if (line != null)
					portal.setWLocation(line);
				
				line = kvFile.get("x");
				if (line != null)
					portal.setXLocation(Double.parseDouble(line));
				
				line = kvFile.get("y");
				if (line != null)
					portal.setYLocation(Double.parseDouble(line));
				
				line = kvFile.get("z");
				if (line != null)
					portal.setZLocation(Double.parseDouble(line));
				
				line = kvFile.get("enabled");
				if (line != null)
					portal.setEnabled(Boolean.parseBoolean(line));
				
				line = kvFile.get("isnether");
				if (line != null)
					portal.setIsNether(Boolean.parseBoolean(line));
			}
			catch (Exception e)
			{
				System.out.println("[ccTowns] Loading Error: Exception while reading portal file " + portal.getName());
				return false;
			}

			return true;
		}
		else
			return false;
	}

	@Override
	public boolean loadTown(Town town)
	{
		String line, line2, line3, line4, line5, line6;
		String[] tokens, tokens2;
		String path = getTownFilename(town);
		File fileTown = new File(path);
		
		if (fileTown.exists() && fileTown.isFile())
		{
			try
			{
				KeyValueFile kvFile = new KeyValueFile(path);

				line = kvFile.get("users");
				if (line != null)
				{
					tokens = line.split(",");
					for (String token : tokens)
					{
						if (!token.isEmpty())
						{
							User user = getUser(token);
							if (user != null)
								town.addUser(user);
						}
					}
				}

				line = kvFile.get("puser");
				town.setOwner(getUser(line));
				
				line = kvFile.get("vault-world");
				line2 = kvFile.get("vault-x");
				line3 = kvFile.get("vault-y");
				line4 = kvFile.get("vault-z");
				line5 = kvFile.get("vault-yaw");
				line6 = kvFile.get("vault-pitch");
				if ((line != null) && (line2 != null) && (line3 != null) && (line4 != null) && (line5 != null) && (line6 != null))
					town.setVault(new Location(Bukkit.getWorld(line), Double.parseDouble(line2), Double.parseDouble(line3), Double.parseDouble(line4), Float.parseFloat(line5), Float.parseFloat(line6)));
				
				line = kvFile.get("vice");
				if (line != null)
					town.setVice(getUser(line));
				
				line = kvFile.get("chunks");
				if (line != null)
				{
					tokens = line.split(",");
					for (String token : tokens)
					{
						tokens2 = token.split(":");
						if (!token.isEmpty())
						{
							town.addChunk(Bukkit.getWorld(tokens2[0]).getChunkAt(Integer.parseInt(tokens2[1]), Integer.parseInt(tokens2[2])));
						}
					}
				}
				
				line = kvFile.get("region");
				if (line != null)
					town.setRegion(line);

				line = kvFile.get("assistants");
				if (line != null) {
					tokens = line.split(",");
					for (String token : tokens)
					{
						if (!token.isEmpty())
						{
							User assistant = getUserL(token);
							if ((assistant != null) && (town.hasUser(assistant)))
								town.addAssistant(assistant);
						}
					}
				}
				
				line = kvFile.get("townrules");
				if (line != null)
				{
					tokens = line.split(",");
					for (String token : tokens)
					{
						token.replace("_", " ");
						if (!token.isEmpty())
						{
							town.addRule(token);
						}
					}
				}
				
				line = kvFile.get("flags");
				if (line != null)
				{
					tokens = line.split(",");
					for (String token : tokens)
					{
						if (!token.isEmpty())
						{
							town.addFlag(token);
						}
					}
				}

				line = kvFile.get("townPrice");
				if (line != null)
					try {
						town.setTownPrice(Double.parseDouble(line));
					} catch (Exception e) {
						town.setTownPrice(0);
					}
				
				line = kvFile.get("townBalance");
				if (line != null)
					try {
						town.setBalance(Double.parseDouble(line));
					} catch (Exception e) {
						town.setBalance(0);
					}
				
                line = kvFile.get("public");
                if (line != null)
                	try {
                		town.setPublic(Boolean.parseBoolean(line));
                	} catch (NumberFormatException nfe) {
                	} catch (Exception e) {
                	}
                line = kvFile.get("active");
                if (line != null)
                	try {
                		town.setActive(Boolean.parseBoolean(line));
                	} catch (NumberFormatException nfe) {
                	} catch (Exception e) {
                	}
			} catch (Exception e) {
				System.out.println("[ccTowns] Loading Error: Exception while reading town file " + town.getName());
				e.printStackTrace();
				return false;
			}

			return true;
		} else
			return false;
	}

	/*
	 * Save keys
	 */
	
	@Override
	public boolean saveWatchedCmdsPlayerList()
	{
		try
		{
			BufferedWriter fout = new BufferedWriter(new FileWriter(rootFolder + dataFolder + FileMgmt.fileSeparator() + "watchedcmdsplayers.txt"));
			for (String str : getWatchedCmdsPlayersKeys())
				fout.write(str + newLine);
			fout.close();
			return true;
		}
		catch (Exception e)
		{
			System.out.println("[ccTowns] Saving Error: Exception while saving watched list file");
			e.printStackTrace();
			return false;
		}
	}
	
	@Override
	public boolean saveWatchedPlayer(String name)
	{
		try
		{
			BufferedWriter fout = new BufferedWriter(new FileWriter(getWatchedFilename(name)));
			for (String cmd : getWatchedCmdsPlayer(name))
				fout.write(cmd + newLine);
			fout.close();
			return true;
		}
		catch (Exception e)
		{
			System.out.println("[ccTowns] Saving Error: Exception while saving watched list file");
			e.printStackTrace();
			return false;
		}
	}

	@Override
	public boolean saveUserList()
	{
		try
		{
			BufferedWriter fout = new BufferedWriter(new FileWriter(rootFolder + dataFolder + FileMgmt.fileSeparator() + "users.txt"));
			for (User user : getUsers())
				fout.write((user.getName()) + newLine);
			fout.close();
			return true;
		}
		catch (Exception e)
		{
			System.out.println("[ccTowns] Saving Error: Exception while saving users list file");
			e.printStackTrace();
			return false;
		}
	}
	
	@Override
	public boolean savePortalList()
	{
		try
		{
			BufferedWriter fout = new BufferedWriter(new FileWriter(rootFolder + dataFolder + FileMgmt.fileSeparator() + "portals.txt"));
			for (Portal portal : getPortals())
				fout.write((portal.getName()) + newLine);
			fout.close();
			return true;
		}
		catch (Exception e)
		{
			System.out.println("[ccTowns] Saving Error: Exception while saving portal list file");
			e.printStackTrace();
			return false;
		}
	}

	@Override
	public boolean saveTownList()
	{
		try
		{
			BufferedWriter fout = new BufferedWriter(new FileWriter(rootFolder + dataFolder + FileMgmt.fileSeparator() + "towns.txt"));
			for (Town town : getTowns())
				fout.write(town.getName() + newLine);
			fout.close();
			return true;
		}
		catch (Exception e)
		{
			System.out.println("[ccTowns] Saving Error: Exception while saving town list file");
			e.printStackTrace();
			return false;
		}
	}
	
	/*
	 * Save individual town objects
	 */

	@Override
	public boolean saveUser(User user)
	{
		BufferedWriter fout;
		String path = getUserFilename(user);
		try
		{
			fout = new BufferedWriter(new FileWriter(path));
		}
		catch (IOException e)
		{
			e.printStackTrace();
			return false;
		}
		try
		{
			
			/*
			 * IDonator object
			 */
			
			// last purchase date
			if (user.hasPurchaseDate())
				fout.write("purchasedate=" + Long.toString(user.getPurchaseDate()) + newLine);
			// current donation package
			if (user.hasDonationPackage())
				fout.write("donationpackage=" + user.getDonationPackage() + newLine);
			// fly violation level
			if (user.hasFlyViolation())
				fout.write("flyviolation=" + Integer.toString(user.getFlyViolation()) + newLine);
			// player allowed fly
			fout.write("canfly=" + Boolean.toString(user.getCanFly()) + newLine);
			// is current (valid) donator
			fout.write("valid=" + Boolean.toString(user.isValid()) + newLine);
			// term length
			if (user.hasTerm())
				fout.write("term=" + Long.toString(user.getTerm()) + newLine);
			
			/*
			 * IFighter object
			 */
			
			// last attempt on pvping
			fout.write("lastpvp=" + Long.toString(user.getLastPvP()) + newLine);
			// pvp champ
			if (user.hasPvPChamp())
				fout.write("pvpchamp=" + user.getPvPChamp() + newLine);
			// pvp won
			fout.write("pvpwon=" + Boolean.toString(user.getPvPWon()) + newLine);
			// pvp lost
			fout.write("pvplost=" + Integer.toString(user.getPvPLost()) + newLine);

			/*
			 * IResident object
			 */
			
			// town plot (chunk)
			if (user.hasChunk())
				fout.write("chunk=" + user.getChunk().getWorld().getName() + ":" + user.getChunk().getX() + ":" + user.getChunk().getZ() + newLine);
			// nag player for placement of pstone
			fout.write("nagpstone=" + Boolean.toString(user.getNagPstone()) + newLine);
			// Last Online
			fout.write("lastonline=" + Long.toString(user.getLastOnline()) + newLine);
			// messages
			fout.write("messages=");
			for (String msg : user.getMessages())
				fout.write(msg + ",");
			fout.write(newLine);
			// rank
			fout.write("rank=" + user.getRank() + newLine);
			// allowed in nether
			fout.write("allowednether=" + user.getAllowedNether() + newLine);
			// town
			if (user.hasTown())
				fout.write("town=" + user.getTown().getName() + newLine);
			// raffle ticket
			if (user.hasRaffleTickets())
			{
				fout.write("raffletickets=");
				for (Integer i : user.getRaffleTickets())
					fout.write(i.toString() + ",");
				fout.write(newLine);
			}

			fout.close();
		}
		catch (Exception e)
		{
			try
			{
				fout.close();
			}
			catch (IOException ioe)
			{
			}
			e.printStackTrace();
			return false;
		}

		return true;
	}

	@Override
	public boolean savePortal(Portal portal)
	{
		BufferedWriter fout;
		String path = getPortalFilename(portal);
		try
		{
			fout = new BufferedWriter(new FileWriter(path));
		}
		catch (IOException e)
		{
			e.printStackTrace();
			return false;
		}
		try
		{
			// town
			if (portal.hasTown())
				fout.write("town=" + portal.getTown().getName() + newLine);
			// send location
			fout.write("world=" + portal.getWLocation().getName() + newLine);
			fout.write("x=" + Double.toString(portal.getXLocation()) + newLine);
			fout.write("y=" + Double.toString(portal.getYLocation()) + newLine);
			fout.write("z=" + Double.toString(portal.getZLocation()) + newLine);
			// enabled
			String enabled = Boolean.toString(portal.isEnabled());
			fout.write("enabled=" + enabled + newLine);
			// is nether
			fout.write("isnether=" + portal.getIsNether() + newLine);
			
			fout.close();
		}
		catch (Exception e)
		{
			try
			{
				fout.close();
			}
			catch (IOException ioe)
			{
			}
			e.printStackTrace();
			return false;
		}

		return true;
	}
	
	@Override
	public boolean saveTown(Town town)
	{
		BufferedWriter fout;
		String path = getTownFilename(town);
		try
		{
			fout = new BufferedWriter(new FileWriter(path));
		}
		catch (IOException e)
		{
			e.printStackTrace();
			return false;
		}
		try 
		{
			// Users
			fout.write("users=" + BukkitUtils.join(town.getUsers(), ",") + newLine);
			// owner
			if (town.hasOwner())
				fout.write("puser=" + town.getOwner().getName() + newLine);
			// vice
			if (town.hasVice())
				fout.write("vice=" + town.getVice().getName() + newLine);
			// chunks
			if (town.hasChunks())
			{
				fout.write("chunks=");
				for (Chunk chu : town.getChunks())
					fout.write(chu.getWorld().getName() + ":" + chu.getX() + ":" + chu.getZ() + ",");
				fout.write(newLine);
			}
			// region
			if (town.hasRegion())
				fout.write("region=" + town.getRegion() + newLine);
			// Assistants
			fout.write("assistants=");
			for (User assistant : town.getAssistants())
				fout.write(assistant.getName() + ",");
			fout.write(newLine);
			// the vault
			if (town.hasVault())
			{
				fout.write("vault-world=" + town.getVault().getWorld().getName());
				fout.write("vault-x=" + Double.toString(town.getVault().getX()));
				fout.write("vault-y=" + Double.toString(town.getVault().getY()));
				fout.write("vault-z=" + Double.toString(town.getVault().getZ()));
				fout.write("vault-yaw=" + Float.toString(town.getVault().getYaw()));
				fout.write("vault-pitch=" + Float.toString(town.getVault().getPitch()));
			}
			// flags
			fout.write("flags=");
			for (String flag : town.getFlags())
				fout.write(flag + ",");
			fout.write(newLine);
			// Town rules
			fout.write("townRules=");
			for (String rule : town.getRules())
			{
				rule.replace(",", " ");
				rule.replace(" ", "_");
				fout.write(rule + ",");
			}
			fout.write(newLine);
			// Plot Price
			fout.write("townPrice=" + Double.toString(town.getTownPrice()) + newLine);
			// Town balance
			fout.write("townBalance=" + Double.toString(town.getBalance()) + newLine);
			// Public
			fout.write("public=" + Boolean.toString(town.isPublic()) + newLine);
			// Active
			fout.write("active=" + Boolean.toString(town.isActive()) + newLine);
			
			fout.close();
			
			DebugMode.log("Saving town: " + town.toString());
		}
		catch (Exception e)
		{
			try
			{
				fout.close();
			}
			catch (IOException ioe)
			{
			}
			e.printStackTrace();
			return false;
		}

		return true;
	}

	@Override
	public void deleteFile(String fileName)
	{
		File file = new File(fileName);
		if (file.exists())
			file.delete();
	}

	@Override
	public void deleteUser(User user)
	{		
		File file = new File(getUserFilename(user));
		if (file.exists())
		{
			try
			{
				FileMgmt.moveFile(file, ("banned"));
			}
			catch (IOException e)
			{
				System.out.println("[ccTowns] Error moving Town txt file.");
			}
		}
	}
	
	@Override
	public void deletePortal(Portal portal)
	{
		File file = new File(getPortalFilename(portal));
		if (file.exists())
		{
			try
			{
				FileMgmt.moveFile(file, "removed");
			}
			catch (IOException e)
			{
				System.out.println("[ccTowns] Error moving portal txt file.");
			}
		}
	}

	@Override
	public void deleteTown(Town town)
	{
		File file = new File(getTownFilename(town));
		if (file.exists())
		{
			try
			{
				FileMgmt.moveFile(file, ("deleted"));
			}
			catch (IOException e)
			{
				System.out.println("[ccTowns] Error moving Town txt file.");
			}
		}
	}
	
	@Override
	public void deleteWatchedCmdsPlayer(String name)
	{
		File file = new File(getWatchedFilename(name));
		if (file.exists())
		{
			FileMgmt.deleteFile(file);
		}
	}
}
