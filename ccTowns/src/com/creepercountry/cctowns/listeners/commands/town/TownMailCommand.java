package com.creepercountry.cctowns.listeners.commands.town;

import org.bukkit.command.CommandSender;

import com.creepercountry.cctowns.hooks.Vault;
import com.creepercountry.cctowns.objects.TownUniverse;
import com.creepercountry.cctowns.util.BukkitUtils;
import com.creepercountry.cctowns.util.Colors;
import com.creepercountry.cctowns.util.NotRegisteredException;

public class TownMailCommand extends BaseCommand
{
    public TownMailCommand()
    {
        name = "mail";
        usage = "<read|clear> [user]";
        minArgs = 1;
        maxArgs = 2;
    }

    @Override
    public boolean execute()
    {
    	String action = args.get(0).toLowerCase();
    	int num = 1;
    	
    	try
    	{
    		if (action.equals("read"))
        	{
        		if (isPlayer)
        		{
        			user = TownUniverse.getDataSource().getUser(sender.getName());
        			
        			if (args.size() == 2)
        			{
        				if (!Vault.perms.has(sender, "ct.command.mail.others"))
        				{
        					BukkitUtils.sendMessage(sender, Colors.Red, "You do not have permission to read others mail.");
        					return true;
        				}
        				
        				user = TownUniverse.getDataSource().getUser(args.get(1).toLowerCase());
        				
        				if (!user.hasMessages())
        				{
        					BukkitUtils.sendMessage(player, Colors.Red, "This user doesnt have any messages to read");
        					return true;
        				}
        			}
        		}
        		else
        		{
        			if (!(args.size() == 2))
        				return false;
        			
        			user = TownUniverse.getDataSource().getUser(args.get(1).toLowerCase());
        			
        			if (!user.hasMessages())
    				{
    					BukkitUtils.sendMessage(player, Colors.Red, "This user doesnt have any messages to read");
    					return true;
    				}
        		}
        		
        		// do the real work here:
        		cleanTitle("Town Messages", "=");
        		for (String msg : user.getMessages())
        		{
        			BukkitUtils.sendMessage(player, Colors.LightBlue, num + ". " + msg);
        		}
        		
        		return true;
        	}
        	else if (action.equals("clear"))
        	{
        		if (isPlayer)
        		{
        			user = TownUniverse.getDataSource().getUser(sender.getName());
        			
        			if (args.size() == 2)
        			{
        				if (!Vault.perms.has(sender, "ct.command.mail.others"))
        				{
        					BukkitUtils.sendMessage(sender, Colors.Red, "You do not have permission to clear others mail.");
        					return true;
        				}
        				
        				user = TownUniverse.getDataSource().getUser(args.get(1).toLowerCase());
        				
        				if (!user.hasMessages())
        				{
        					BukkitUtils.sendMessage(player, Colors.Red, "This user doesnt have any messages to clear");
        					return true;
        				}
        			}
        		}
        		else
        		{
        			if (!(args.size() == 2))
        				return false;
        			
        			user = TownUniverse.getDataSource().getUser(args.get(1).toLowerCase());
        			
        			if (!user.hasMessages())
    				{
    					BukkitUtils.sendMessage(player, Colors.Red, "This user doesnt have any messages to clear");
    					return true;
    				}
        		}
        		
        		// do the real work here:
        		user.getMessages().clear();
        		TownUniverse.getDataSource().saveUser(user);
        		
        		BukkitUtils.sendMessage(player, Colors.Blue, "cleared all messages!");
        		return true;
        	}
    	}
    	catch (NotRegisteredException n)
    	{
    		BukkitUtils.sendMessage(sender, Colors.Red, n.getMessage());
			return true;
    	}
    	
    	return false;
    }
    
    @Override
    public void moreHelp()
    {
        BukkitUtils.sendMessage(sender, Colors.Rose, "clear or read automated town messages informing you about inportant town information.");
        BukkitUtils.sendMessage(sender, Colors.Rose, "clear will clear ALL messages, not just 1; just as read will read ALL messages.");
    }

    @Override
    public boolean permission(CommandSender csender)
    {
    	return Vault.perms.has(csender, "ct.command.mail");
    }

    @Override
    public BaseCommand newInstance()
    {
        return new TownMailCommand();
    }
}
