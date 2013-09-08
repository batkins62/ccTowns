package com.creepercountry.cctowns.listeners.commands.town;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.creepercountry.cctowns.hooks.Vault;
import com.creepercountry.cctowns.main.config.MainConfigObject;
import com.creepercountry.cctowns.objects.TownUniverse;
import com.creepercountry.cctowns.objects.user.User;
import com.creepercountry.cctowns.util.BukkitUtils;
import com.creepercountry.cctowns.util.Colors;
import com.creepercountry.cctowns.util.NotRegisteredException;

public class TownQuitCommand extends BaseCommand
{
    public TownQuitCommand()
    {
        name = "quit";
        usage = "[player]";
        minArgs = 0;
        maxArgs = 1;
    }

    @Override
    public boolean execute()
    {
    	try
    	{
    		if (isPlayer)
    		{
    			user = TownUniverse.getDataSource().getUser(sender.getName());
    			
    			if ((args.size() > 0) && Vault.perms.has(sender, "ct.command.quit.others"))
    				user = TownUniverse.getDataSource().getUser(args.get(0).toLowerCase());
    		}
    		else
    		{
    			if (args.size() > 0)
    				user = TownUniverse.getDataSource().getUser(args.get(0).toLowerCase());
    		}
    			
    		if (user.isOwner())
    		{
    			BukkitUtils.sendMessage(sender, Colors.Red, "You cannot leave your own town");
    			return true;
    		}
    		
    		if (!user.hasTown())
    		{
    			BukkitUtils.sendMessage(sender, Colors.Red, "You arent a member of any town");
    			return true;
    		}

    		town = user.getTown();
    		town.removeUser(user);
    	
    		TownUniverse.getDataSource().saveUser(user);
    		TownUniverse.getDataSource().saveTown(town);
    		
    		// send a message to the town assistants
    		for (User assistant : town.getAssistants())
    		{
    			assistant.addMessage(user.getName() + " has quit your town.");
    			TownUniverse.getDataSource().saveUser(assistant);
    		}
    		
    		// send a message to the town owner
    		User owner = town.getOwner();
    		owner.addMessage(user.getName() + " has quit your town.");
    		TownUniverse.getDataSource().saveUser(owner);
    		
    		// msg online server mods and admins
    		List<String> exclude = new ArrayList<String>();
    		for (String name : MainConfigObject.town_notifications)
    		{
    			name = name.toLowerCase();
    			
    			if (name.startsWith("!"))
    				exclude.add(name);
    			
    			for (User assistant : town.getAssistants())
    				if (!exclude.contains(assistant.getName().toLowerCase()))
    					exclude.add(assistant.getName().toLowerCase());
    			
    			if (name.equalsIgnoreCase("OP"))
    			{
    				for (OfflinePlayer plr: Bukkit.getOperators())
    					if (!exclude.contains(plr.getName().toLowerCase()))
    					{
    						if (plugin.isOnline(plr.getName().toLowerCase()))
    		    			{
    		    				BukkitUtils.sendMessage(player, Colors.Blue, user.getName() + " has quit town " + town.getName());
    		    			}
    		    			else
    		    			{
    		    				User staff = TownUniverse.getDataSource().getUser(plr.getName().toLowerCase());
    		    				staff.addMessage(user.getName() + " has quit town " + town.getName());
    		    				TownUniverse.getDataSource().saveUser(staff);
    		    			}
    					}
    			}
    			else if (name.equalsIgnoreCase("ONLINE"))
    			{
    				for (Player plr : Bukkit.getOnlinePlayers())
    					if (!exclude.contains(plr.getName().toLowerCase()))
    						BukkitUtils.sendMessage(player, Colors.Blue, user.getName() + " has quit town " + town.getName());
    			}
    			else // regular player
    			{
    				if (!exclude.contains(name))
    				{
    					if (plugin.isOnline(name))
    		    		{
    		    			BukkitUtils.sendMessage(player, Colors.Blue, user.getName() + " has quit town " + town.getName());
    		    		}
    		    		else
    		    		{
    		    			User staff = TownUniverse.getDataSource().getUser(name);
    		    			staff.addMessage(user.getName() + " has quit town " + town.getName());
    		    			TownUniverse.getDataSource().saveUser(staff);
    		    		}
    				}
    			}
    		}
    		exclude.clear();
    		
    		// output the user with confirmation
    		BukkitUtils.sendMessage(sender, Colors.LightBlue, "You have quit " + town.getName());
    	}
    	catch (NotRegisteredException n)
    	{
    		BukkitUtils.sendMessage(sender, Colors.Red, n.getMessage());
    		return true;
    	}
		
        return true;
    }
    
    @Override
    public void moreHelp()
    {
        BukkitUtils.sendMessage(sender, Colors.Rose, "Quit the current town you have membership to");
        BukkitUtils.sendMessage(sender, Colors.Rose, "specifying a player will force a quit to a town.");
    }

    @Override
    public boolean permission(CommandSender csender)
    {
    	return Vault.perms.has(csender, "ct.command.quit");
    }

    @Override
    public BaseCommand newInstance()
    {
        return new TownQuitCommand();
    }
}