package com.creepercountry.cctowns.listeners.commands.town;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.creepercountry.cctowns.hooks.Vault;
import com.creepercountry.cctowns.objects.TownUniverse;
import com.creepercountry.cctowns.objects.town.Town;
import com.creepercountry.cctowns.objects.user.User;
import com.creepercountry.cctowns.util.BukkitUtils;
import com.creepercountry.cctowns.util.Colors;
import com.creepercountry.cctowns.util.NotRegisteredException;
import com.creepercountry.cctowns.util.TownException;

public class NewTownCommand extends BaseCommand
{
    public NewTownCommand()
    {
        name = "new";
        usage = "<town> <puser> <region>";
        minArgs = 3;
    }

    @Override
    public boolean execute()
    {
		try
		{
			user = TownUniverse.getDataSource().getUser(args.get(1));
		}
		catch (NotRegisteredException e)
		{
			e.getMessage();
			return true;
		}
    	
    	newTown(sender, args.get(0), user, args.get(2));

        return true;
    }

    @Override
    public boolean permission(CommandSender csender)
    {
    	return Vault.perms.has(csender, "ct.town.create");
    }

    @Override
    public void moreHelp()
    {
        BukkitUtils.sendMessage(sender, Colors.Rose, "Creates a new joinable town.");
        BukkitUtils.sendMessage(sender, Colors.Rose, "Captital letters matter as this is CaSe SeNsItIve!");
        BukkitUtils.sendMessage(sender, Colors.Rose, "Do not abbreviate names.");
    }

    @Override
    public BaseCommand newInstance()
    {
        return new NewTownCommand();
    }
    
    /**
     * Create a new town. Command: /town new [town] *[puser]
     * 
     * @param sender
     * @param name town name
     * @param mayorName
     * @param region
     */
	public void newTown(CommandSender sender, String name, User mayorName, String region)
	{
		try
		{			
			if (TownUniverse.getDataSource().hasTown(name))
				throw new TownException("This town exists already!");

			if (user.hasTown())
				throw new TownException("You already created a town for this user!");
			
			BukkitUtils.sendMessage(sender, Colors.Blue, cleanTitle("New Town Creation", "="));
			
			BukkitUtils.sendMessage(sender, Colors.Blue, "Town Name: " + Colors.LightBlue + name);
			BukkitUtils.sendMessage(sender, Colors.Blue, "Town Owner: " + Colors.LightBlue + mayorName);
			BukkitUtils.sendMessage(sender, Colors.Blue, "Region name: " + Colors.LightBlue + region);

			TownUniverse.getDataSource().newTown(name);
			Town town = TownUniverse.getDataSource().getTown(name);
			town.setOwner(user);
			town.setRegion(region);
			town.addUser(user);

			TownUniverse.getDataSource().saveUser(user);
			TownUniverse.getDataSource().saveTown(town);
			TownUniverse.getDataSource().saveTownList();

			BukkitUtils.sendMessage((Player)sender, Colors.Green, "Succesfully added town!");
		}
		catch (TownException x)
		{
			BukkitUtils.sendMessage(sender, Colors.Red, x.getMessage());
			return;
		}
	}
}
