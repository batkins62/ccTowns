package com.creepercountry.cctowns.listeners.commands.town;

import org.bukkit.command.CommandSender;

import com.creepercountry.cctowns.hooks.Vault;
import com.creepercountry.cctowns.objects.TownUniverse;
import com.creepercountry.cctowns.util.BukkitUtils;
import com.creepercountry.cctowns.util.Colors;
import com.creepercountry.cctowns.util.DebugMode;
import com.creepercountry.cctowns.util.TownException;

public class TownRuleCommand extends BaseCommand
{
    public TownRuleCommand()
    {
        name = "rule";
        usage = "<town> <rule>";
        minArgs = 2;
    }

    @Override
    public boolean execute()
    {
    	try
    	{
    		town = TownUniverse.getDataSource().getTown(args.get(0).toLowerCase());
    	}
    	catch (Exception e)
    	{
    		BukkitUtils.sendMessage(sender, Colors.LightBlue, e.getMessage());
    		return true;
    	}
    	
    	try
    	{
    		String line = args.get(1);
    		for (int i = 2; i < args.size(); i++)
                line += " " + args.get(i);

    		DebugMode.log(line);
    		town.addRule(line);
    		BukkitUtils.sendMessage(sender, Colors.LightBlue, "Added rule: " + line);
    		TownUniverse.getDataSource().saveTown(town);
    		return true;
    	}
    	catch (TownException t)
    	{
    		BukkitUtils.sendMessage(sender, Colors.LightBlue, t.getMessage());
    		return false;
    	}
    }
    
    @Override
    public void moreHelp()
    {
    	// TODO: finish
        BukkitUtils.sendMessage(sender, Colors.Rose, "");
        BukkitUtils.sendMessage(sender, Colors.Rose, "");
    }

    @Override
    public boolean permission(CommandSender csender)
    {
    	return Vault.perms.has(csender, "ct.command.rule");
    }

    @Override
    public BaseCommand newInstance()
    {
        return new TownRuleCommand();
    }
}
