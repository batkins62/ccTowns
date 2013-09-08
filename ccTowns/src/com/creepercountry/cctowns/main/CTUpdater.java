package com.creepercountry.cctowns.main;

import java.io.File;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginDescriptionFile;

import com.creepercountry.cctowns.objects.TownUniverse;
import com.creepercountry.cctowns.objects.user.User;
import com.creepercountry.cctowns.storage.FileMgmt;
import com.creepercountry.cctowns.storage.KeyValueFile;
import com.creepercountry.cctowns.util.BukkitUtils;

public class CTUpdater
{
	private CTPlugin plugin;
	private PluginDescriptionFile info;
	
	protected String rootFolder = "";
	protected String dataFolder = FileMgmt.fileSeparator() + "data";
	
	public CTUpdater(CTPlugin instance)
	{
		this.plugin = instance;
		this.info = instance.getDescription();
	}
	
	// VERSION 1.1.2 to 1.1.3
	public void update()
	{
		String current = info.getVersion();
		String old = plugin.getConfig().getString("plugin.pluginversion");
		if (!current.equals(old))
		{
			cleanRun();
			
			if (old.equals("1.1.2"))
			{
				BukkitUtils.info("Updating to 1.1.3");
				for (User usr : TownUniverse.getDataSource().getUsers())
				{
					String dpackage, term, lastonline;
					String path = rootFolder + dataFolder + FileMgmt.fileSeparator() + "users" + FileMgmt.fileSeparator() + usr.getName() + ".txt";;
					File fileUser = new File(path);
					
					if (fileUser.exists() && fileUser.isFile())
					{
						try
						{
							KeyValueFile kvFile = new KeyValueFile(path);
							
							dpackage = kvFile.get("donationPackage");
							term = kvFile.get("donationExpire");
							lastonline = kvFile.get("lastOnline");
							
							if (dpackage != null)
							{
								usr.setDonationPackage(dpackage);
								BukkitUtils.info("formating donation package for user " + usr.getName());
							}
							if (term != null)
							{
								usr.setTerm(Long.parseLong(term));
								BukkitUtils.info("formating donation term for user " + usr.getName());
							}
							if (lastonline != null)
							{
								usr.setLastOnline(Long.parseLong(lastonline));
								BukkitUtils.info("formating last online for user " + usr.getName());
							}
							TownUniverse.getDataSource().saveUser(usr);
						}
						catch (Exception e)
						{
							System.out.println("[ccTowns] Loading Error: Exception while reading user file " + usr.getName());
						}
				}
				
				old = "1.1.3";
			}
			
			// this is how it would continue, (after we update to 1.1.2, we update to the next version, and so on)
			if (old.equals("1.1.3"))
			{
				
			}
			
			CTEngine.ENABLED = true;
			
			}
		}
	}
	
	public void cleanRun()
	{
		for (Player plr : Bukkit.getOnlinePlayers())
			plr.kickPlayer("Updating the server, please join back in a few.");
		
		CTEngine.ENABLED = false;
	}
}
