package com.creepercountry.cctowns.listeners.commands.town;

import org.bukkit.command.CommandSender;

import com.creepercountry.cctowns.hooks.Vault;
import com.creepercountry.cctowns.objects.TownUniverse;
import com.creepercountry.cctowns.util.BukkitUtils;
import com.creepercountry.cctowns.util.Colors;
import com.creepercountry.cctowns.util.DebugMode;
import com.creepercountry.cctowns.util.NotRegisteredException;

public class TownStatusCommand extends BaseCommand
{
    public TownStatusCommand()
    {
        name = "status";
        usage = "<true|false|?> <town>";
        minArgs = 2;
    }

    @Override
    public boolean execute()
    {
    	String status = args.get(0).toLowerCase();
    	String townname = args.get(1).toLowerCase();
    	
    	try
    	{
			town = TownUniverse.getDataSource().getTown(townname);
		}
    	catch (NotRegisteredException e)
    	{
			BukkitUtils.sendMessage(sender, Colors.Red, e.getMessage());
			return true;
		}
    	
    	if (status.equals("true") || status.equals("false"))
		{
    		boolean choice = Boolean.parseBoolean(status);
			town.setActive(choice);
			DebugMode.log(Boolean.toString(choice));
			TownUniverse.getDataSource().saveTown(town);
			
			BukkitUtils.sendMessage(sender, Colors.Green, "Successfully set status of town " + townname + " to " + status);
			
			return true;
		}
    	
    	if (status.equals("?"))
    	{
    		BukkitUtils.sendMessage(sender, Colors.LightBlue, Boolean.toString(town.isActive()));
    		
    		return true;
    	}
		
        return false;
    }
    
    @Override
    public void moreHelp()
    {
        BukkitUtils.sendMessage(sender, Colors.Rose, "Toggle or check the Active status of a town.");
        BukkitUtils.sendMessage(sender, Colors.Rose, "Administratively disable a town.");
        BukkitUtils.sendMessage(sender, Colors.Rose, "This will not effect the public status towns can toggle, however will function the same.");
    }

    @Override
    public boolean permission(CommandSender csender)
    {
    	return Vault.perms.has(csender, "ct.town.status");
    }

    @Override
    public BaseCommand newInstance()
    {
        return new TownStatusCommand();
    }
}