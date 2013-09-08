package com.creepercountry.cctowns.listeners.commands.general;

import org.bukkit.command.CommandSender;

import com.creepercountry.cctowns.hooks.Vault;
import com.creepercountry.cctowns.main.CTEngine;
import com.creepercountry.cctowns.util.BukkitUtils;
import com.creepercountry.cctowns.util.Colors;
import com.creepercountry.cctowns.util.DebugMode;

public class DebugModeCommand extends BaseCommand
{
    public DebugModeCommand()
    {
        name = "debug";
        usage = "[status|dev]";
        minArgs = 0;
        maxArgs = 1;
    }

    @Override
    public boolean execute()
    {
    	// if DebugMode is not enabled, enable, and vice versa.
		if (DebugMode.instance != null)
		{
			DebugMode.stop();
			DebugMode.setDEVMODE(false);
			sender.sendMessage("Debug off");
		}
		else
		{
			DebugMode.go();
			sender.sendMessage("Debug on");
		}
		
		if (args.size() > 0)
		{
	    	// if we are just checking status, handle and return.
	    	if (args.get(0).equalsIgnoreCase("status"))
	    	{
	    		if (DebugMode.instance != null)
	    		{
	    			sender.sendMessage("Debug on");
	    		}
	    		else
	    		{
	    			sender.sendMessage("Debug off");
	    		}
	    	}
	    	else if (args.get(0).equalsIgnoreCase("dev"))
	    	{
	    		if (DebugMode.instance != null)
	    		{
	    			sender.sendMessage("Entering dev mode. to exit, please disable debug");
	    			DebugMode.setDEVMODE(true);
	    			plugin.getConfig().set("debug", true);
	    			plugin.getConfig().set("debug-dev", true);
	    			CTEngine.CHECKFLY = true;
	    			CTEngine.DECREMENTATION = true;
	    			// TODO: improve
	    			plugin.saveConfig();
	    		}
	    		else
	    		{
	    			sender.sendMessage("Debug needs to be enabled first");
	    		}
	    	}
		}
		
        return true;
    }
    
    @Override
    public void moreHelp()
    {
        BukkitUtils.sendMessage(sender, Colors.Rose, "Toggle DebugMode");
        BukkitUtils.sendMessage(sender, Colors.Rose, "used for development or error purposes only!");
    }

    @Override
    public boolean permission(CommandSender csender)
    {
    	return Vault.perms.has(csender, "ct.command.debug");
    }

    @Override
    public BaseCommand newInstance()
    {
        return new DebugModeCommand();
    }
}