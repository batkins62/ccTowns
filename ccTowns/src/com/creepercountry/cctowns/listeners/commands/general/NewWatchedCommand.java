package com.creepercountry.cctowns.listeners.commands.general;

import java.util.List;

import org.bukkit.command.CommandSender;

import com.creepercountry.cctowns.hooks.Vault;
import com.creepercountry.cctowns.objects.TownUniverse;
import com.creepercountry.cctowns.objects.user.User;
import com.creepercountry.cctowns.util.AlreadyRegisteredException;
import com.creepercountry.cctowns.util.BukkitUtils;
import com.creepercountry.cctowns.util.Colors;
import com.creepercountry.cctowns.util.NotRegisteredException;

public class NewWatchedCommand extends BaseCommand
{
    public NewWatchedCommand()
    {
        name = "watched";
        usage = "<player> <start|stop|read>";
        minArgs = 2;
        maxArgs = 2;
    }

    @Override
    public boolean execute()
    {
    	try
    	{
	    	User res = TownUniverse.getDataSource().getUser(args.get(0));
	    	String action = args.get(1).toLowerCase();
	    	
	    	if (action.equals("start"))
	    	{
	    		TownUniverse.getDataSource().newWatchedCmdsPlayer(res.getName());
	    		BukkitUtils.sendMessage(sender, Colors.Gold, "Starting logging of " + res.getName() + "\'s commands.");
	    		BukkitUtils.sendMessage(sender, Colors.LightBlue, "use: '/cc watched stop' to stop from logging");
	    		BukkitUtils.sendMessage(sender, Colors.LightBlue, "use: '/cc watched read' to read the logs");
	    		TownUniverse.getDataSource().saveWatchedPlayer(res.getName());
	    		TownUniverse.getDataSource().saveWatchedCmdsPlayerList();
	    		return true;
	    	}
	    	else if (action.equals("stop"))
	    	{
	    		TownUniverse.getDataSource().removeWatchedList(res.getName());
	    		TownUniverse.getDataSource().saveWatchedCmdsPlayerList();
	    		BukkitUtils.sendMessage(sender, Colors.Red, "Removed.");
	    		return true;
	    	}
	    	else if (action.equals("read"))
	    	{
	    		List<String> cmds = TownUniverse.getDataSource().getWatchedCmdsPlayer(res.getName());
	    		for (String cmd : cmds)
	    			BukkitUtils.sendMessage(sender, Colors.LightGray, cmd);
	    		return true;
	    	}
    	}
    	catch (NotRegisteredException nre)
    	{
    		BukkitUtils.sendMessage(sender, Colors.Red, nre.getMessage());
    		return true;
    	}
    	catch (AlreadyRegisteredException are)
    	{
    		BukkitUtils.sendMessage(sender, Colors.Red, are.getMessage());
    		return true;
		}
		
        return false;
    }
    
    @Override
    public void moreHelp()
    {
        BukkitUtils.sendMessage(sender, Colors.Rose, "Watch a player's commands, record every issued.");
        BukkitUtils.sendMessage(sender, Colors.Rose, "stoping will delete data.");
    }

    @Override
    public boolean permission(CommandSender csender)
    {
    	return Vault.perms.has(csender, "ct.command.watched");
    }

    @Override
    public BaseCommand newInstance()
    {
        return new NewWatchedCommand();
    }
}