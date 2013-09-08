package com.creepercountry.cctowns.listeners.commands.general;

import org.bukkit.command.CommandSender;

import com.creepercountry.cctowns.hooks.Vault;
import com.creepercountry.cctowns.main.config.ConfigNode;
import com.creepercountry.cctowns.util.BukkitUtils;
import com.creepercountry.cctowns.util.Colors;

public class ServerCommand extends BaseCommand
{
    public ServerCommand()
    {
        name = "server";
        usage = "<setting|notify> <setting|player>";
        minArgs = 2;
        maxArgs = 2;
    }

    @Override
    public boolean execute()
    {
    	String action = args.get(0).toLowerCase();
    	
    	if (action.equals("setting"))
    	{
    		BukkitUtils.sendMessage(sender, Colors.Red, "Not at this time.");
    		return true;
    	}
    	else if (action.equals("notify"))
    	{
    		plugin.getConfig().set(ConfigNode.TOWN_NOTIFICATIONS.getPath(), args.get(1).toLowerCase());
    		plugin.saveConfig();
    	}
		
        return false;
    }
    
    @Override
    public void moreHelp()
    {
        BukkitUtils.sendMessage(sender, Colors.Rose, "Set various plugin to server options.");
        BukkitUtils.sendMessage(sender, Colors.Rose, "used to administrate actions of the whole plugin.");
    }

    @Override
    public boolean permission(CommandSender csender)
    {
    	return Vault.perms.has(csender, "ct.command.server");
    }

    @Override
    public BaseCommand newInstance()
    {
        return new ServerCommand();
    }
}