package com.creepercountry.cctowns.listeners.commands.town;

import org.bukkit.command.CommandSender;

import com.creepercountry.cctowns.hooks.Vault;
import com.creepercountry.cctowns.objects.TownUniverse;
import com.creepercountry.cctowns.objects.town.Town;
import com.creepercountry.cctowns.objects.user.User;
import com.creepercountry.cctowns.util.AlreadyRegisteredException;
import com.creepercountry.cctowns.util.BukkitUtils;
import com.creepercountry.cctowns.util.Colors;
import com.creepercountry.cctowns.util.DebugMode;
import com.creepercountry.cctowns.util.NotRegisteredException;

public class TownJoinCommand extends BaseCommand
{
    public TownJoinCommand()
    {
        name = "join";
        usage = "<town> [player]";
        minArgs = 1;
        maxArgs = 2;
    }

    @Override
    public boolean execute()
    {    	
		try
		{
			String townname = null;
			DebugMode.log("1");
	    	try
	    	{
	    		if (isPlayer && (args.size() >= 1))
	    		{
	    			DebugMode.log("2");
	    			user = TownUniverse.getDataSource().getUser(sender.getName());
	    			townname = args.get(0).toLowerCase();
	    		}
	    		else if ((isPlayer && (args.size() > 1)) && Vault.perms.has(sender, "ct.command.join.others"))
	    		{
	    			DebugMode.log("3");
	    			user = TownUniverse.getDataSource().getUser(args.get(1).toLowerCase());
	    		}
	    		else if (!isPlayer && (args.size() > 1))
	    		{
	    			DebugMode.log("4");
	    			if (args.size() >= 2)
	    			{
	    				DebugMode.log("5");
	    				user = TownUniverse.getDataSource().getUser(args.get(1).toLowerCase());
	    				townname = args.get(0).toLowerCase();
	    			}
	    			else
	    			{
	    				DebugMode.log("6");
	    				return false;
	    			}
	    		}
	    		else
	    		{
	    			DebugMode.log("7");
	    			return false;
	    		}
	    	}
	    	catch (NotRegisteredException n)
	    	{
	    		DebugMode.log("8");
	    		BukkitUtils.sendMessage(sender, Colors.Red, n.getMessage());
	    		
	    		return true;
	    	}
	    	catch (Exception i)
	    	{
	    		DebugMode.go();
	    		BukkitUtils.sendMessage(sender, Colors.Red, "Error, Alert an admin as there was a problem processing your request");
	    		BukkitUtils.severe("ERROR in TownJoinCommand", i);
	    		return false;
	    	}
	    	
	    	DebugMode.log("9");
	    	if (townname.isEmpty())
	    		return false;

			town = TownUniverse.getDataSource().getTown(townname);
			DebugMode.log("10");
			if (user.hasTown())
				if (user.getTown().equals(townname))
					throw new Exception("You are trying to join a town you are already in.");

			// Check if user is currently in a town.
			if (user.hasTown())
				throw new Exception("sorry, but at this time, you can only join 1 town");
			DebugMode.log("11");
			if (isPlayer)
			{
				DebugMode.log("12");
				// Check if town is town is free to join.
				if (!town.isOpen())
					throw new Exception(String.format("this town is not open", town.getName()));
				
				if (!town.isActive())
					throw new Exception(String.format("this town is not active", town.getName()));
			}

			// Check if player is already in selected town
			// Then add player to town.
			townAddUser(town, user);

			// User was added successfully.
			BukkitUtils.sendMessage(sender, Colors.LightBlue, "You have joined " + town.getName());
		}
		catch (Exception e)
		{
			BukkitUtils.sendMessage(sender, Colors.Red, e.getMessage());
			return true;
		}
		
		return true;
    }
    
    @Override
    public void moreHelp()
    {
        BukkitUtils.sendMessage(sender, Colors.Rose, "Joins a joinable town, set default status to user.");
        if (!isPlayer || Vault.perms.has(sender, "ct.command.join.others"));
        	BukkitUtils.sendMessage(sender, Colors.Rose, "Specifying a player forces them to join a town.");
    }

    @Override
    public boolean permission(CommandSender csender)
    {
    	return Vault.perms.has(csender, "ct.command.join");
    }

    @Override
    public BaseCommand newInstance()
    {
        return new TownJoinCommand();
    }
    
    /**
     * Add a user to a town
     * 
     * @param town
     * @param user
     */
    public static void townAddUser(Town town, User user)
    {
        try
        {
			town.addUser(user);
		}
        catch (AlreadyRegisteredException e)
		{
			e.printStackTrace();
		}
        TownUniverse.getDataSource().saveUser(user);
        TownUniverse.getDataSource().saveTown(town);
    }
}