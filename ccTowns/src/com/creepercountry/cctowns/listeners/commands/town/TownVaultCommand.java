package com.creepercountry.cctowns.listeners.commands.town;

import org.bukkit.command.CommandSender;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;

import com.creepercountry.cctowns.hooks.Vault;
import com.creepercountry.cctowns.objects.TownUniverse;
import com.creepercountry.cctowns.util.BukkitUtils;
import com.creepercountry.cctowns.util.Colors;
import com.creepercountry.cctowns.util.NotRegisteredException;

public class TownVaultCommand extends BaseCommand
{
    public TownVaultCommand()
    {
        name = "vault";
        usage = "<action> [town]";
        minArgs = 1;
        maxArgs = 2;
        allowConsole = true;
    }

    @Override
    public boolean execute()
    {
    	String action = args.get(0).toLowerCase();
    	
    	try
    	{
	    	if (args.size() == 2)
	    		town = TownUniverse.getDataSource().getTown(args.get(1));
	    	if (args.size() == 1)
	    	{
	    		if (isPlayer)
	    		{
	    			user = TownUniverse.getDataSource().getUser(sender.getName());
	    			town = user.getTown();
	    		}
	    		else
	    		{
	    			BukkitUtils.sendMessage(sender, Colors.Red, "MUST supply a town");
	    			return false;
	    		}
	    	}
    	}
    	catch (NotRegisteredException nre)
    	{
    		BukkitUtils.sendMessage(sender, Colors.Red, nre.getMessage());
    		return true;
    	}
    	
    	if (action.equals("tp"))
    	{
    		if (!isPlayer)
    		{
    			BukkitUtils.sendMessage(sender, Colors.Red, "You MUST be a player to use or set teleport.");
    			return true;
    		}
    		
    		player.teleport(town.getVault(), TeleportCause.COMMAND);
    		BukkitUtils.sendMessage(player, Colors.Green, "Now entering " + town.getName() + "\'s protected vault!");
    		
    		return true;
    	}
    	else if (action.equals("set"))
    	{
    		if (!isPlayer)
    		{
    			BukkitUtils.sendMessage(sender, Colors.Red, "You MUST be a player to use or set teleport.");
    			return true;
    		}
    		
    		town.setVault(player.getLocation());
    		BukkitUtils.sendMessage(player, Colors.Gold, "Setting this exact location as " + town.getName() + "\'s protected vault teleport location.");
    		
    		return true;
    	}
    	
        return false;
    }
    
    @Override
    public void moreHelp()
    {
        BukkitUtils.sendMessage(sender, Colors.Rose, "action can be: tp or set");
    }

    @Override
    public boolean permission(CommandSender csender)
    {
    	return Vault.perms.has(csender, "ct.command.vault");
    }

    @Override
    public BaseCommand newInstance()
    {
        return new TownVaultCommand();
    }
}