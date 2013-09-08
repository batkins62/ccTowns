package com.creepercountry.cctowns.listeners.commands.town;

import org.bukkit.command.CommandSender;

import com.creepercountry.cctowns.hooks.Vault;
import com.creepercountry.cctowns.objects.TownUniverse;
import com.creepercountry.cctowns.objects.user.User;
import com.creepercountry.cctowns.util.BukkitUtils;
import com.creepercountry.cctowns.util.Colors;
import com.creepercountry.cctowns.util.NotRegisteredException;
import com.creepercountry.cctowns.util.TownException;

public class TownPromoteCommand extends BaseCommand
{
    public TownPromoteCommand()
    {
        name = "promote";
        usage = "<user> [town]";
        minArgs = 1;
        maxArgs = 2;
    }

    @Override
    public boolean execute()
    {
    	User respromoted = null;
    	
    	if (args.size() == 1)
    	{
    		try
    		{
    			if (isPlayer)
    			{
    				user = TownUniverse.getDataSource().getUser(sender.getName());
    				respromoted = TownUniverse.getDataSource().getUser(args.get(0));
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
    			respromoted = TownUniverse.getDataSource().getUser(args.get(0));
    			if (respromoted.hasTown())
    				if (!respromoted.getTown().getName().equalsIgnoreCase(args.get(1)))
    				{
    					BukkitUtils.sendMessage(sender, Colors.Red, "User doesnt belong to town");
    				}
    			
    			town = respromoted.getTown();
    			user = town.getOwner();
    			User senderuser = TownUniverse.getDataSource().getUser(sender.getName());
    			
    			if (!Vault.perms.has(sender, "ct.command.promote.others") || !senderuser.getTown().getName().equals(town.getName()))
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
			
			if (respromoted.isAssistant())
				throw new TownException("They are already an assistant!");
			
			if (respromoted.isOwner())
				throw new TownException("They are the puser of this town!");
		}
		catch (TownException x)
		{
			BukkitUtils.sendMessage(sender, Colors.Red, x.getMessage());
			return true;
		}

		try
		{
			town.addAssistant(respromoted);
		}
		catch (TownException e)
		{
			BukkitUtils.sendMessage(sender, Colors.Red, e.getMessage());
		}
		
		TownUniverse.getDataSource().saveTown(town);
		TownUniverse.getDataSource().saveUser(respromoted);
		
        return true;
    }
    
    @Override
    public void moreHelp()
    {
        BukkitUtils.sendMessage(sender, Colors.Rose, "Promote a user from user to assistant.");
        BukkitUtils.sendMessage(sender, Colors.Rose, "users names are CaSe SeNsItIvE and must be full usernames.");
    }

    @Override
    public boolean permission(CommandSender csender)
    {
    	return Vault.perms.has(csender, "ct.command.promote");
    }

    @Override
    public BaseCommand newInstance()
    {
        return new TownPromoteCommand();
    }
}