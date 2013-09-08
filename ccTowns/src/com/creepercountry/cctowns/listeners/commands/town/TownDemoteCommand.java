package com.creepercountry.cctowns.listeners.commands.town;

import org.bukkit.command.CommandSender;

import com.creepercountry.cctowns.hooks.Vault;
import com.creepercountry.cctowns.objects.TownUniverse;
import com.creepercountry.cctowns.objects.user.User;
import com.creepercountry.cctowns.util.BukkitUtils;
import com.creepercountry.cctowns.util.Colors;
import com.creepercountry.cctowns.util.NotRegisteredException;
import com.creepercountry.cctowns.util.TownException;

public class TownDemoteCommand extends BaseCommand
{
    public TownDemoteCommand()
    {
        name = "demote";
        usage = "<user> [town]";
        minArgs = 1;
        maxArgs = 2;
    }

    @Override
    public boolean execute()
    {
    	User resdemoted = null;
    	
    	if (args.size() == 1)
    	{
    		try
    		{
    			if (isPlayer)
    			{
    				user = TownUniverse.getDataSource().getUser(sender.getName());
    				resdemoted = TownUniverse.getDataSource().getUser(args.get(0));
    				town = user.getTown();
    			}
    			else
    				return false;
    		}
    		catch (NotRegisteredException n)
    		{
    			BukkitUtils.sendMessage(sender, Colors.Red, n.getMessage());
    			return true;
    		}
    	}
		
    	if (args.size() == 2)
    	{
    		try
    		{
    			resdemoted = TownUniverse.getDataSource().getUser(args.get(0));
    			if (resdemoted.hasTown())
    				if (!resdemoted.getTown().getName().equalsIgnoreCase(args.get(1)))
    				{
    					BukkitUtils.sendMessage(sender, Colors.Red, "User doesnt belong to town");
    				}
    			
    			town = resdemoted.getTown();
    			user = town.getOwner();
    			User senderuser = TownUniverse.getDataSource().getUser(sender.getName());
    			
    			if (!Vault.perms.has(sender, "ct.command.demote.others") || !senderuser.getTown().getName().equals(town.getName()))
    			{
    				BukkitUtils.sendMessage(sender, Colors.Red, "You cannot promote others from a town that isnt yours!");
    				return true;
    			}
    		}
    		catch (NotRegisteredException n)
    		{
    			BukkitUtils.sendMessage(sender, Colors.Red, n.getMessage());
    			return true;
    		}
    	}
    	
    	try
		{
			if (!user.isOwner())
				throw new TownException("You are not the puser of this town!");
			
			if (resdemoted.isOwner())
				throw new TownException("They are the puser of this town!");
		}
		catch (TownException x)
		{
			BukkitUtils.sendMessage(sender, Colors.Red, x.getMessage());
			return true;
		}

		town.removeAssistant(resdemoted);
		
		TownUniverse.getDataSource().saveTown(town);
		TownUniverse.getDataSource().saveUser(resdemoted);
		
        return true;
    }
    
    @Override
    public void moreHelp()
    {
        BukkitUtils.sendMessage(sender, Colors.Rose, "demote a user from assistant to user.");
        BukkitUtils.sendMessage(sender, Colors.Rose, "users names are CaSe SeNsItIvE and must be full usernames.");
    }

    @Override
    public boolean permission(CommandSender csender)
    {
    	return Vault.perms.has(csender, "ct.command.demote");
    }

    @Override
    public BaseCommand newInstance()
    {
        return new TownDemoteCommand();
    }
}