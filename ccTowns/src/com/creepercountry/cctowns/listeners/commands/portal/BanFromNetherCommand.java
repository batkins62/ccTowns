package com.creepercountry.cctowns.listeners.commands.portal;

import org.bukkit.command.CommandSender;

import com.creepercountry.cctowns.hooks.Vault;
import com.creepercountry.cctowns.objects.TownUniverse;
import com.creepercountry.cctowns.util.BukkitUtils;
import com.creepercountry.cctowns.util.Colors;
import com.creepercountry.cctowns.util.NotRegisteredException;

public class BanFromNetherCommand extends BaseCommand
{
    public BanFromNetherCommand()
    {
        name = "ban";
        usage = "<player>";
        minArgs = 1;
        maxArgs = 1;
    }

    @Override
    public boolean execute()
    {
    	try
    	{
    		user = TownUniverse.getDataSource().getUser(args.get(0));
    		boolean allowed = false;
    		if (user.getAllowedNether() == false)
    			allowed = true;
    		
    		user.setAllowedNether(allowed);
    		TownUniverse.getDataSource().saveUser(user);
    		BukkitUtils.sendMessage(sender, Colors.Gold, "user has been banned from the nether? " + Boolean.toString(allowed));
    		return true;
    	}
    	catch (NotRegisteredException nre)
    	{
    		BukkitUtils.sendMessage(sender, Colors.Red, nre.getMessage());
    		return true;
    	}
    }
    
    @Override
    public void moreHelp()
    {
        BukkitUtils.sendMessage(sender, Colors.Rose, "Bans the player from the nether ");
        BukkitUtils.sendMessage(sender, Colors.Rose, "issue again to unban.");
    }

    @Override
    public boolean permission(CommandSender csender)
    {
    	return Vault.perms.has(csender, "ct.command.ban.nether");
    }

    @Override
    public BaseCommand newInstance()
    {
        return new BanFromNetherCommand();
    }
}