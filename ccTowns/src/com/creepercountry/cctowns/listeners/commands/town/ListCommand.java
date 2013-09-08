package com.creepercountry.cctowns.listeners.commands.town;

import java.util.ArrayList;

import org.bukkit.command.CommandSender;

import com.creepercountry.cctowns.hooks.Vault;
import com.creepercountry.cctowns.objects.TownUniverse;
import com.creepercountry.cctowns.objects.town.Town;
import com.creepercountry.cctowns.util.BukkitUtils;
import com.creepercountry.cctowns.util.Colors;

public class ListCommand extends BaseCommand
{
    public ListCommand()
    {
        name = "list";
        usage = "";
        minArgs = 0;
        maxArgs = 0;
    }

    @Override
    public boolean execute()
    {
    	BukkitUtils.sendMessage(sender, Colors.Blue, cleanTitle("Town List", "="));
		
        ArrayList<String> formatedList = new ArrayList<String>();
        
        for (Town town1 : TownUniverse.getDataSource().getTowns())
        {
        	String townToken = Colors.LightBlue + town1.getName();
        	townToken += town1.isOpen() ? Colors.White + " (Open)" : Colors.Red + "(Closed)";
        	townToken += Colors.Blue + " [" + town1.getNumResidents() + "]";
        	townToken += Colors.White;
        	formatedList.add(townToken);
        }
        
        if (TownUniverse.getDataSource().getTowns().isEmpty())
        	BukkitUtils.sendMessage(sender, Colors.Red, "There are no towns!");

        for (String line : BukkitUtils.list(formatedList))
        	sender.sendMessage(line);
        
        return true;
    }
    
    @Override
    public void moreHelp()
    {
        BukkitUtils.sendMessage(sender, Colors.Rose, "");
        BukkitUtils.sendMessage(sender, Colors.Rose, "");
    }

    @Override
    public boolean permission(CommandSender csender)
    {
    	return Vault.perms.has(csender, "ct.command.list");
    }

    @Override
    public BaseCommand newInstance()
    {
        return new ListCommand();
    }
}