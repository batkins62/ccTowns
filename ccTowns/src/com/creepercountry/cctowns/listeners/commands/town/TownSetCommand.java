package com.creepercountry.cctowns.listeners.commands.town;

import org.bukkit.command.CommandSender;

import com.creepercountry.cctowns.hooks.Vault;
import com.creepercountry.cctowns.objects.TownUniverse;
import com.creepercountry.cctowns.util.BukkitUtils;
import com.creepercountry.cctowns.util.Colors;
import com.creepercountry.cctowns.util.NotRegisteredException;

public class TownSetCommand extends BaseCommand
{
    public TownSetCommand()
    {
        name = "set";
        usage = "<setting> <option> [town]";
        minArgs = 2;
        maxArgs = 3;
        allowConsole = false;
    }

    @Override
    public boolean execute()
    {
    	/*
    	 *  get the user & town then confirm permission checks
    	 */
    	try
    	{
    		user = TownUniverse.getDataSource().getUser(sender.getName());
    	}
    	catch (NotRegisteredException n)
    	{
    		BukkitUtils.severe(n.getMessage());
    		return false;
    	}
    	
    	// if supplied other town name then use that instead, else use users town
    	try
    	{
    		if (args.size() == 3)
    		{
    			town = TownUniverse.getDataSource().getTown(args.get(2).toLowerCase());
    		}
    		else
    		{
    			town = user.getTown();
    		}
    	}
    	catch (NotRegisteredException n)
    	{
    		BukkitUtils.sendMessage(sender, Colors.Red, n.getMessage());
    		return true;
    	}
    	
    	// check if player can set that setting in that town
    	if (args.size() == 3)
    		if (!town.isStaff(user) || !Vault.perms.has(sender, "ct.command.set.others"))
    			return false;
    	
		String setting = args.get(1).toLowerCase();
		String option = args.get(2).toLowerCase();
		
		/*
		 * PUBLIC SETTING
		 */
		if (setting.equals("public"))
		{
			if (!(option.equals("true") || option.equals("false")))
			{
				return false;
			}
			
			town.setPublic(Boolean.parseBoolean(setting));
			TownUniverse.getDataSource().saveTown(town);
			BukkitUtils.sendMessage(sender, Colors.Blue, "Changed public setting to " + setting);
		}
			
		return true;
    }
    
    @Override
    public void moreHelp()
    {
        BukkitUtils.sendMessage(sender, Colors.Rose, "Set a setting for the town.");
        BukkitUtils.sendMessage(sender, Colors.Rose, "option an be either true or false.");
    }

    @Override
    public boolean permission(CommandSender csender)
    {
    	return Vault.perms.has(csender, "ct.command.set");
    }

    @Override
    public BaseCommand newInstance()
    {
        return new TownSetCommand();
    }
}