package com.creepercountry.cctowns.listeners.commands.general;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import com.creepercountry.cctowns.hooks.Vault;
import com.creepercountry.cctowns.objects.TownUniverse;
import com.creepercountry.cctowns.objects.Handlers.PvPHandler;
import com.creepercountry.cctowns.util.BukkitUtils;
import com.creepercountry.cctowns.util.Colors;
import com.creepercountry.cctowns.util.DebugMode;
import com.creepercountry.cctowns.util.NotRegisteredException;
import com.creepercountry.cctowns.util.PvPFinalRound;
import com.creepercountry.cctowns.util.tasks.DelayedMsg;

public class PvPCommand extends BaseCommand
{
    public PvPCommand()
    {
        name = "pvp";
        usage = "<start|cancel|won|lost> <params>";
        minArgs = 3;
        maxArgs = 6;
    }

    @Override
    public boolean execute()
    {	
    	boolean playerParam = false, timezoneParam = false, sessionParam = false;
    	boolean dst = false, newSession = false;
    	Player fightee = null, fighter = player;
    	int offset = +0;
    	Integer sessionid = new Integer(0);
    	PvPHandler session = null;
    	
    	// session param
    	for (String arg : args)
    		if (arg.equalsIgnoreCase("Session"))
    			try
    			{
    				sessionid = Integer.parseInt(args.get(args.indexOf(arg.toLowerCase(plugin.getLocale())) + 1));
    				sessionParam = true;
    			}
				catch (IndexOutOfBoundsException ioobe)
				{
					DebugMode.log(ioobe.getLocalizedMessage());
					return false;
				}
		    	catch (NumberFormatException nfe)
				{
		    		DebugMode.log(nfe.getLocalizedMessage());
					return false;
				}
    	
    	// player param
    	for (String arg : args)
    		if (arg.equalsIgnoreCase("Player"))
    			try
    			{
	    			String name = args.get(args.indexOf(arg.toLowerCase(plugin.getLocale())) + 1);
	    			fightee = Bukkit.matchPlayer(name).get(0);
	    			if (fightee.equals(null))
	    			{
	    				BukkitUtils.sendMessage(sender, Colors.Red, String.format("Player %s cannot be found.", name));
	    				return true;
	    			}
	    			playerParam = true;
    			}
    			catch (IndexOutOfBoundsException ioobe)
    			{
    				DebugMode.log(ioobe.getLocalizedMessage());
    				return false;
    			}
    	
    	// timezone param
    	for (String arg : args)
    		if (arg.equalsIgnoreCase("Timezone"))
    			try
    			{
    				offset = Integer.parseInt(args.get(args.indexOf(arg.toLowerCase(plugin.getLocale())) + 1));
    				String path = args.get(args.indexOf(arg.toLowerCase(plugin.getLocale())) + 2);
    				if (!path.equalsIgnoreCase("true") || !path.equalsIgnoreCase("false"))
    					return false;
        			dst = Boolean.parseBoolean(path);
        			timezoneParam = true;
    			}
		    	catch (IndexOutOfBoundsException ioobe)
				{
		    		DebugMode.log(ioobe.getLocalizedMessage());
					return false;
				}
    			catch (NumberFormatException nfe)
    			{
    				DebugMode.log(nfe.getLocalizedMessage());
    				return false;
    			}
    	
    	// grab session
    	if (!sessionParam && !TownUniverse.getDataSource().getPvPHandlers().isEmpty())
    	{
    		try
    		{
	    		for (PvPHandler pvp : TownUniverse.getDataSource().getPvPHandlers())
	    			if (pvp.getFightee().getName().equals(fightee.getName()) && pvp.getFighter().getName().equals(fighter.getName()))
	    				session = pvp;
    		}
    		catch (NotRegisteredException nre)
    		{
    			BukkitUtils.sendMessage(player, Colors.Red, "The challenger must be online when using this command.");
    			return true;
    		}
    	}
    	else if (sessionParam)
    	{
    		if (TownUniverse.getDataSource().hasPvPHandler(sessionid))
    			try { session = TownUniverse.getDataSource().getPvPHandler(sessionid); } catch (NotRegisteredException e) {}
    	}
    	else // no session found, lets create it
    	{
    		Random generator = new Random();
    		sessionid = generator.nextInt(9999);
    		new PvPHandler(fighter, fighter, sessionid);
    		BukkitUtils.sendMessage(fighter, Colors.Gold, String.format(plugin.getLocale(), "Your session id is %s", sessionid.toString()));
    		newSession = true;
    	}
    	
    	// get the command type
    	String command = args.get(0).toLowerCase();
    	if (command.equals("start") && playerParam && timezoneParam)
    	{
    		try
    		{
	    		for (PvPHandler pvp : TownUniverse.getDataSource().getPvPHandlers())
	    			if (pvp.getFightee().getName().equals(fightee.getName()) && !newSession)
	    			{
	    				BukkitUtils.sendMessage(fighter, Colors.Red, String.format(plugin.getLocale(), "Player %s already has a match started", pvp.getFightee().getName()));
	    				BukkitUtils.sendMessage(fighter, Colors.Rose, String.format(plugin.getLocale(), "Use /pvp cancel Player %s", pvp.getFightee().getName()));
	    				return true;
	    			}
    		}
    		catch (NotRegisteredException nre)
    		{
    			BukkitUtils.sendMessage(player, Colors.Red, "Player must be online when using this command");
    			return true;
    		}
    		
    		session.getTime().setTimeZone(offset, dst);
    		
    		PlayerInventory einventory = player.getInventory();
    		PlayerInventory rinventory = player.getInventory();
    		ItemStack sword = new ItemStack(Material.WOOD_SWORD, 1);
    		ItemStack arrows = new ItemStack(Material.ARROW, 5);
    		ItemStack bow = new ItemStack(Material.BOW, 1);
    		
    		einventory.clear();
    		rinventory.clear();
    		fightee.setHealth(fightee.getMaxHealth());
    		fighter.setHealth(fighter.getMaxHealth());
    		fightee.setFoodLevel(20);
    		fighter.setFoodLevel(20);
    		
    		einventory.addItem(sword);
    		einventory.addItem(arrows);
    		einventory.addItem(bow);
    		rinventory.addItem(sword);
    		rinventory.addItem(arrows);
    		rinventory.addItem(bow);
    		
    		BukkitUtils.sendMessage(fightee, Colors.LightBlue, "Prepare for battle by entering the red side room.");
    		fightee.teleport(fighter.getLocation(), TeleportCause.PLUGIN);
    		
    		BukkitUtils.sendMessage(fighter, Colors.Blue, cleanTitle(fightee.getName(), "="));
    		BukkitUtils.sendMessage(fighter, Colors.Blue, "Total XP: " + Colors.LightBlue + fightee.getTotalExperience());
    		BukkitUtils.sendMessage(fighter, Colors.Blue, "Exhaustion: " + Colors.LightBlue  + fightee.getExhaustion());
    		BukkitUtils.sendMessage(fighter, Colors.Blue, "Health: " + Colors.LightBlue  + fightee.getHealth());
    		BukkitUtils.sendMessage(fighter, Colors.Blue, "Saturation: " + Colors.LightBlue  + fightee.getSaturation());
    		BukkitUtils.sendMessage(fighter, Colors.Blue, "Food Level: " + Colors.LightBlue  + fightee.getFoodLevel());
    		BukkitUtils.sendMessage(fighter, Colors.Blue, "Last Played: " + Colors.LightBlue  + DateFormat.getDateInstance(DateFormat.FULL, plugin.getLocale()).format(new Date(fightee.getLastPlayed())));
    		BukkitUtils.sendMessage(fighter, Colors.Blue, "Can Pickup Items: " + Colors.LightBlue + Boolean.toString(fightee.getCanPickupItems()));
    		BukkitUtils.sendMessage(fighter, Colors.Blue, "Ticks Lived: " + Colors.LightBlue + fightee.getTicksLived());
    		BukkitUtils.sendMessage(fighter, Colors.Blue, "Sleep Ticks: " + Colors.LightBlue + fightee.getSleepTicks());
    		BukkitUtils.sendMessage(fighter, Colors.Blue, "Player Time: " + Colors.LightBlue + fightee.getPlayerTime());
    		
    		return true;
    	}
    	else if (command.equals("lost") && playerParam)
    	{	
    		if (newSession)
    		{
    			BukkitUtils.sendMessage(sender, Colors.Red, "Please start the match before using this command.");
    			return true;
    		}
    		
    		List<CommandSender> senders = new ArrayList<CommandSender>();
    		senders.add(fighter);
    		senders.add(fightee);
    		
    		try
    		{
				session.newRound();
				session.roundChampion(0);
			}
    		catch (PvPFinalRound e)
			{
    			for (CommandSender cs : senders)
    				BukkitUtils.sendMessage(cs, Colors.Gold, "FINAL ROUND!!");
			}
    		
    		if (session.getRound().equals(new Integer(3)))
    		{
    			try
    			{
					session.fighteeLost();
				}
    			catch (PvPFinalRound e)
				{
					BukkitUtils.sendMessage(sender, Colors.Red, e.getLocalizedMessage());
				}
    			StringBuffer sb = new StringBuffer();
    			Date date = session.getTime().getDate();
    			DateFormat df = new SimpleDateFormat("MM.dd.yyyy G 'at' HH:mm:ss z", plugin.getLocale());
    			sb.append(df.format(date));
    			BukkitUtils.sendMessage(fightee, Colors.Green, "You have lost. you may try again on: " + sb.toString());
    			return true;
    		}
    		
    		BukkitUtils.sendMessage(fightee, Colors.Gold, String.format("Round %s...", session.getRound().toString()));
    		BukkitUtils.sendMessage(fighter, Colors.Gold, String.format("Round %s...", session.getRound().toString()));

    		new DelayedMsg(senders, Colors.Yellow + "3").runTaskLater(plugin, 40L);
    		new DelayedMsg(senders, Colors.Gold + "2").runTaskLater(plugin, 40L);
    		new DelayedMsg(senders, Colors.Red + "1").runTaskLater(plugin, 40L);
    		new DelayedMsg(senders, Colors.Green + "FIGHT!").runTaskLater(plugin, 40L);
    		
    		return true;
    	}
    	else if (command.equals("won") && playerParam)
    	{
    		if (newSession)
    		{
    			BukkitUtils.sendMessage(sender, Colors.Red, "Please start the match before using this command.");
    			return true;
    		}
    		
    		List<CommandSender> senders = new ArrayList<CommandSender>();
    		senders.add(fighter);
    		senders.add(fightee);
    		
    		if (session.getRound().equals(new Integer(3)))
    		{
    			BukkitUtils.sendMessage(fightee, Colors.Green, "You have successfully beat your challenger!");
    		}
    		
    		try
    		{
				session.newRound();
				session.roundChampion(1);
			}
    		catch (PvPFinalRound e)
    		{
    			for (CommandSender cs : senders)
    				BukkitUtils.sendMessage(cs, Colors.Gold, "FINAL ROUND!!");
			}
    		
    		BukkitUtils.sendMessage(fightee, Colors.Gold, String.format("Round %s...", session.getRound().toString()));
    		BukkitUtils.sendMessage(fighter, Colors.Gold, String.format("Round %s...", session.getRound().toString()));
    		
    		new DelayedMsg(senders, Colors.Yellow + "3").runTaskLater(plugin, 40L);
    		new DelayedMsg(senders, Colors.Gold + "2").runTaskLater(plugin, 40L);
    		new DelayedMsg(senders, Colors.Red + "1").runTaskLater(plugin, 40L);
    		new DelayedMsg(senders, Colors.Green + "FIGHT!").runTaskLater(plugin, 40L);
    		
    		return true;
    	}
    	else if (command.equals("cancel") && playerParam)
    	{
    		if (newSession)
    		{
    			BukkitUtils.sendMessage(sender, Colors.Red, "Please start the match before using this command.");
    			return true;
    		}
    		
    		BukkitUtils.sendMessage(sender, Colors.Gold, "Cancelled on round: " + session.getRound().toString());
    		BukkitUtils.sendMessage(fighter, Colors.Green, "Score: " + session.getRoundWinner(0) + "/" + session.getRoundWinner(1));
    		
    		TownUniverse.getDataSource().removePvPHandler(session);
    	}
    	else
    		return false;
		
		return true;
    }
    
    @Override
    public void moreHelp()
    {
    	BukkitUtils.sendMessage(sender, Colors.Blue, "PvP Handler for ranking, with commands for champions to use to better track matches.");
    	BukkitUtils.sendMessage(sender, Colors.LightBlue, "when usage is specified start the timezone param must be used.");
    	BukkitUtils.sendMessage(sender, Colors.LightBlue, "session id must be specified to access another champions session.");
    	BukkitUtils.sendMessage(sender, Colors.LightBlue, "when you start a match players inventory will be cleared and items will be given.");
    	BukkitUtils.sendMessage(sender, Colors.LightBlue, "the Won and Lost commands refers to the fightee not yourself the champion.");
    	BukkitUtils.sendMessage(sender, Colors.LightBlue, "You should use the start command when standing in the arena, player will be tp'd to you.");
    	BukkitUtils.sendMessage(sender, Colors.LightBlue, "Use the lost and won commands when you are ready to begin fighting again.");
    	BukkitUtils.sendMessage(sender, "", "");
        BukkitUtils.sendMessage(sender, Colors.Rose, "Param: Player <player>");
        BukkitUtils.sendMessage(sender, Colors.Red, "THE PLAYER PARAM IS REQUIRED FOR ALL USAGES.");
        BukkitUtils.sendMessage(sender, Colors.Rose, "Param: Session <id>");
        BukkitUtils.sendMessage(sender, Colors.Rose, "id can be a former session id, or a specified id.");
        BukkitUtils.sendMessage(sender, Colors.Rose, "Param: Timezone [offset] [dst]");
        BukkitUtils.sendMessage(sender, Colors.Rose, "offset is from UTC, either + or -, such as: -7 is UTC-7, -7 is the offset.");
        BukkitUtils.sendMessage(sender, Colors.Rose, "dst is either true or false.");
    }

    @Override
    public boolean permission(CommandSender csender)
    {
    	return Vault.perms.has(csender, "ct.command.pvp");
    }

    @Override
    public BaseCommand newInstance()
    {
        return new PvPCommand();
    }
}