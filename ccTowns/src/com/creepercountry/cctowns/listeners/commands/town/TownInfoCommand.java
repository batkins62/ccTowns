package com.creepercountry.cctowns.listeners.commands.town;

import org.bukkit.command.CommandSender;

import com.creepercountry.cctowns.hooks.Vault;
import com.creepercountry.cctowns.objects.TownUniverse;
import com.creepercountry.cctowns.objects.user.User;
import com.creepercountry.cctowns.util.BukkitUtils;
import com.creepercountry.cctowns.util.Colors;
import com.creepercountry.cctowns.util.NotRegisteredException;
import com.creepercountry.cctowns.util.StringUtils;

public class TownInfoCommand extends BaseCommand
{
    public TownInfoCommand()
    {
        name = "info";
        usage = "[town]";
        minArgs = 0;
        maxArgs = 1;
    }

    @Override
    public boolean execute()
    {
    	try
    	{
    		if ((args.size() == 0) && isPlayer)
    		{
    			user = TownUniverse.getDataSource().getUser(sender.getName());
    			town = user.getTown();
    		}
    		else if (!isPlayer && (args.size() == 0))
    		{
    			BukkitUtils.sendMessage(sender, Colors.Red, "Specify a town.");
    			return true;
    		}
    		else
    		{
    			town = TownUniverse.getDataSource().getTown(args.get(0).toLowerCase());
    		}
    	}
    	catch (NotRegisteredException n)
    	{
    		BukkitUtils.sendMessage(sender, Colors.Red, n.getMessage());
    		
    		return true;
    	}
    	
		String owner = null;
		try
		{
			owner = town.getOwner().getName();
		}
		catch (Exception e)
		{
			owner = Colors.Red + "(( " + Colors.White + "Cannot locate owner in file" + Colors.Red + " ))";
		}
		BukkitUtils.sendMessage(sender, Colors.Blue, cleanTitle(town.getName() + "\'s info", "="));
		BukkitUtils.sendMessage(sender, Colors.Blue, "| Town Owner: " + Colors.LightBlue + owner);
		BukkitUtils.sendMessage(sender, Colors.Blue, "| Town Assistants:");
		for (User res : town.getAssistants())
			BukkitUtils.sendMessage(sender, Colors.Blue, "|" + "   - " + Colors.LightBlue + res.getName());
		BukkitUtils.sendMessage(sender, Colors.Blue, "| Town Status: " + Colors.Green + ((town.isOpen()) ? "OPEN" : Colors.Red + "CLOSED"));
		BukkitUtils.sendMessage(sender, Colors.Blue, "| Town Members:");
		for (User res : town.getUsers())
			BukkitUtils.sendMessage(sender, Colors.Blue, "|" + "   - " + Colors.LightBlue + res.getName());
		BukkitUtils.sendMessage(sender, Colors.Blue, "| Town Rules:");
		for (String rule : town.getRules())
			BukkitUtils.sendMessage(sender, Colors.Blue, "|" + "   - " + Colors.LightBlue + rule);
		if (Vault.perms.has(sender, "ct.command.vault") && (town.getVault() != null))
			BukkitUtils.sendMessage(sender, Colors.Blue, "| Vault Location: " + Colors.LightBlue + StringUtils.cleanLoc(town.getVault()));
		
        return true;
    }
    
    @Override
    public void moreHelp()
    {
        BukkitUtils.sendMessage(sender, Colors.Rose, "Shows information about the town.");
        BukkitUtils.sendMessage(sender, Colors.Rose, "Specifying a town will show info of that town.");
        BukkitUtils.sendMessage(sender, Colors.Rose, "Not specifying a town will show the town you are a member of");
    }

    @Override
    public boolean permission(CommandSender csender)
    {
    	return Vault.perms.has(csender, "ct.command.info");
    }

    @Override
    public BaseCommand newInstance()
    {
        return new TownInfoCommand();
    }
}