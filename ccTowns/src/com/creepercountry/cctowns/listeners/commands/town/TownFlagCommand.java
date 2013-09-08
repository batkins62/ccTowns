package com.creepercountry.cctowns.listeners.commands.town;

import java.text.DecimalFormat;
import java.util.List;
import org.bukkit.World;
import org.bukkit.command.CommandSender;

import com.creepercountry.cctowns.hooks.Vault;
import com.creepercountry.cctowns.objects.TownUniverse;
import com.creepercountry.cctowns.util.BukkitUtils;
import com.creepercountry.cctowns.util.Colors;
import com.creepercountry.cctowns.util.DebugMode;
import com.creepercountry.cctowns.util.NotRegisteredException;
import com.creepercountry.cctowns.util.TownException;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.flags.InvalidFlagFormat;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;

public class TownFlagCommand extends BaseCommand
{
    public TownFlagCommand()
    {
        name = "flag";
        usage = "<action> [flag] [town]";
        minArgs = 1;
        maxArgs = 3;
        allowConsole = false;
    }

    @Override
    public boolean execute()
    {
    	String action = args.get(0).toLowerCase();
    	
    	try
    	{
    		user = TownUniverse.getDataSource().getUser(sender.getName());
    	}
    	catch (NotRegisteredException n)
    	{
    		BukkitUtils.severe(n.getMessage());
    		return false;
    	}
    	
    	if (args.size() == 3)
    	{
    		try
    		{
    			town = TownUniverse.getDataSource().getTown(args.get(2).toLowerCase());
    		}
    		catch (NotRegisteredException n)
    		{
    			BukkitUtils.sendMessage(sender, Colors.Red, n.getMessage());
    			return true;
    		}
    	}
    	else
    	{
    		try
    		{
				town = user.getTown();
			}
    		catch (NotRegisteredException n)
			{
    			BukkitUtils.sendMessage(sender, Colors.Red, n.getMessage());
    			return true;
			}
    	}
    	
    	if (action.equals("buy"))
    	{
    		String flag = args.get(1).toLowerCase();
    		
    		// lets not allow just anyone to buy flags for a town
    		if (!town.isStaff(user) && !Vault.perms.has(sender, "ct.command.flag.others"))
    		{
    			BukkitUtils.sendMessage(sender, Colors.Red, "You do not have permissions to do that!");
    			BukkitUtils.warning("Denied " + sender.getName() + ", needs: ct.command.flag.others");
    			return true;
    		}
    		
    		if (town.hasFlag(flag))
    		{
    			BukkitUtils.sendMessage(sender, Colors.Red, "You already have that flag");
    			return true;
    		}
    		
    		// TODO: later remove this after you finish the toggle for it
    		if (flag.equals("heal") || flag.equals("feed") || flag.equals("nocmds"))
    		{
    			BukkitUtils.sendMessage(sender, Colors.Red, "sorry these flags cant be purchased yet.");
    			return true;
    		}
    		
    		if (plugin.getWorldGuardObject().hasFlag(flag))
    		{
    			if (town.getBalance() >= plugin.getWorldGuardObject().getCost(flag))
    			{
    				try
    				{
						town.addFlag(flag);
					}
    				catch (TownException t)
					{
    					BukkitUtils.sendMessage(sender, Colors.Red, t.getMessage());
    	    			return true;
					}
					double bal = town.getBalance() - plugin.getWorldGuardObject().getCost(flag);
					town.setBalance(bal);
					TownUniverse.getDataSource().saveTown(town);
					// TODO: record a total of all money spent so we know what money is lost
					
					BukkitUtils.sendMessage(sender, Colors.Green, "You have purchased the " + flag + " flag!");
					BukkitUtils.sendMessage(sender, Colors.LightBlue, "to use flag: /town flag toggle " + flag);
					BukkitUtils.sendMessage(sender, Colors.LightBlue, "Your now town has: " + Colors.Green + new DecimalFormat("$0.00").format(town.getBalance()));
					return true;
				}
				else
				{
					BukkitUtils.sendMessage(sender, Colors.Red, "Your town doesnt appear to have enough money!");
					BukkitUtils.sendMessage(sender, Colors.LightBlue, "Your town has: " + Colors.Green + new DecimalFormat("$0.00").format(town.getBalance()));
					BukkitUtils.sendMessage(sender, Colors.LightBlue, "Your town needs: " + Colors.Green + new DecimalFormat("$0.00").format(plugin.getWorldGuardObject().getCost(flag)));
					BukkitUtils.sendMessage(sender, Colors.LightBlue, "/town money deposit <amount>");
					return true;
				}
    		}
    		else
    		{
    			BukkitUtils.sendMessage(sender, Colors.Red, "check the flag you specified. perhaps you mispelled?");
    			BukkitUtils.sendMessage(sender, Colors.Red, "You typed: " + flag + ", however there is no flag by that name available.");
    			return true;
    		}
    	}
    	else if (action.equals("toggle"))
    	{
    		String flag = args.get(1).toLowerCase();
    		
    		// lets not allow just anyone to buy flags for a town
    		if (!Vault.perms.has(sender, "ct.command.flag.others") || !town.isStaff(user))
    		{
    			BukkitUtils.sendMessage(sender, Colors.Red, "You do not have permissions to do that!");
    			BukkitUtils.warning("Denied " + sender.getName() + ", needs: ct.command.flag.others");
    			return true;
    		}
    		
    		
    		if (!town.hasFlag(flag))
    		{
    			BukkitUtils.sendMessage(sender, Colors.Red, "You need to purchase this flag first!");
    			BukkitUtils.sendMessage(sender, Colors.LightBlue, "/town flag buy <flag>");
    			return true;
    		}
	
    		World world = player.getLocation().getWorld();
    		RegionManager regionManager = null;
			regionManager = plugin.getWorldGuard().getRegionManager(world);
    		ProtectedRegion region;
    		
    		try
    		{
    			region = regionManager.getRegionExact(town.getRegion());
    		}
    		catch (Exception e)
    		{
    			BukkitUtils.sendMessage(sender, Colors.Red, e.getMessage());
    			return true;
    		}
    		
    		ApplicableRegionSet set = regionManager.getApplicableRegions(region);

    		String state = null;
    		
    		if (set.allows(plugin.getWorldGuardObject().getFlag(flag)))
    			state = "deny";
    		else if (!set.allows(plugin.getWorldGuardObject().getFlag(flag)))
    			state = "allow";
    		
    		if (state == null)
    		{
    			BukkitUtils.severe("No such State esists");
    			BukkitUtils.sendMessage(sender, Colors.Red, "Please inform an administrator as their has been a problem");
    			return true;
    		}
    		else
    			DebugMode.log("State: " + state);
    		
    		try
    		{
    			region.setFlag(plugin.getWorldGuardObject().getFlag(flag), plugin.getWorldGuardObject().getFlag(flag).parseInput(plugin.getWorldGuard(), sender, state));
    			BukkitUtils.sendMessage(sender, Colors.Blue, "Changed flag " + plugin.getWorldGuardObject().getFlag(flag).getName() + " to " + state);
    		}
    		catch (InvalidFlagFormat i)
    		{
    			BukkitUtils.sendMessage(sender, Colors.Red, i.getMessage());
    		}
    		catch (Exception e)
    		{
    			BukkitUtils.severe(e.getMessage());
    		}
    		
    		//Save the Region
    		try
    		{
    			regionManager.save();
    		}
    		catch (Exception exp)
    		{
    		}
    		
    		return true;
    	}
    	else if (action.equals("list"))
    	{		
    		// TODO: paginate for easier viewing
    		List<String> flags = plugin.getWorldGuardObject().getFlagsAsList();
    		
    		for (String flag : flags)
    			BukkitUtils.sendMessage(sender, Colors.LightBlue, flag);

    		return true;
    	}
    	
    	return false;
    }

	@Override
    public void moreHelp()
    {
		//TODO: finish
        BukkitUtils.sendMessage(sender, Colors.Rose, "");
        BukkitUtils.sendMessage(sender, Colors.Rose, "");
    }

    @Override
    public boolean permission(CommandSender csender)
    {
    	return Vault.perms.has(csender, "ct.command.flag");
    }

    @Override
    public BaseCommand newInstance()
    {
        return new TownFlagCommand();
    }
}