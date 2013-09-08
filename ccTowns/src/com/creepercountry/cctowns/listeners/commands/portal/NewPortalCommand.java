package com.creepercountry.cctowns.listeners.commands.portal;

import org.bukkit.command.CommandSender;

import com.creepercountry.cctowns.hooks.Vault;
import com.creepercountry.cctowns.objects.TownUniverse;
import com.creepercountry.cctowns.objects.town.Portal;
import com.creepercountry.cctowns.util.AlreadyRegisteredException;
import com.creepercountry.cctowns.util.BukkitUtils;
import com.creepercountry.cctowns.util.Colors;
import com.creepercountry.cctowns.util.NotRegisteredException;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;

public class NewPortalCommand extends BaseCommand
{
    public NewPortalCommand()
    {
        name = "new";
        usage = "<portal> [town]";
        minArgs = 1;
        maxArgs = 2;
        allowConsole = false;
    }

	@Override
	public boolean execute()
	{
		try
		{
			ProtectedRegion pr = plugin.getWorldGuard().getRegionManager(player.getWorld()).getRegion(args.get(0));
			if (pr == null)
			{
				BukkitUtils.sendMessage(player, Colors.Red, "First you need to define a wg region with this exact name.");
				BukkitUtils.sendMessage(player, Colors.Rose, "Remember that the wg region should contain the area around the portal.");
				BukkitUtils.sendMessage(player, Colors.Rose, "Once you have completed this, try this command again.");
				return true;
			}
			
			TownUniverse.getDataSource().newPortal(args.get(0));
			Portal portal = TownUniverse.getDataSource().getPortal(args.get(0));
			
			if (args.size() > 1)
				if (TownUniverse.getDataSource().hasTown(args.get(1)))
					portal.setTown(TownUniverse.getDataSource().getTown(args.get(1)));
				else
					BukkitUtils.sendMessage(player, Colors.Yellow, "No town found by this name, and was NOT pointed to this portal.");
			portal.setEnabled(true);
			portal.setWLocation(player.getLocation().getWorld().getName());
			portal.setXLocation(player.getLocation().getX());
			portal.setYLocation(player.getLocation().getY());
			portal.setZLocation(player.getLocation().getZ());
			
			if (TownUniverse.getDataSource().savePortalList())
				TownUniverse.getDataSource().savePortal(portal);
			else
			{
				BukkitUtils.sendMessage(player, Colors.Red, "SAVE ERROR!!!");
				return false;
			}
			
			BukkitUtils.sendMessage(player, Colors.Green, "Successfully created portal that teleports to this location.");
			BukkitUtils.sendMessage(player, Colors.Green, "Portal Name: " + portal.getName());
			
			return true;
		}
		catch (NotRegisteredException nre) {}
		catch (AlreadyRegisteredException are)
		{
			/*
			 * This will be thrown when the portal was already created, but will overwrite the data that was there.
			 */
			
			ProtectedRegion pr = null;
			pr = plugin.getWorldGuard().getRegionManager(player.getWorld()).getRegion(args.get(0));
			if (pr == null)
			{
				BukkitUtils.sendMessage(player, Colors.Red, "[WARNING] The wg region is undefined, this is required for the portal to work.");
				return true;
			}
			
			Portal portal = null;
			try { portal = TownUniverse.getDataSource().getPortal(args.get(0)); } catch (NotRegisteredException e) {}
			
			if (args.size() > 1)
				if (TownUniverse.getDataSource().hasTown(args.get(1)))
					try { portal.setTown(TownUniverse.getDataSource().getTown(args.get(1))); } catch (NotRegisteredException e) {}
				else
					BukkitUtils.sendMessage(player, Colors.Yellow, "No town found by this name, and was NOT pointed to this portal.");
			portal.setEnabled(true);
			portal.setWLocation(player.getLocation().getWorld().getName());
			portal.setXLocation(player.getLocation().getX());
			portal.setYLocation(player.getLocation().getY());
			portal.setZLocation(player.getLocation().getZ());
			
			TownUniverse.getDataSource().savePortal(portal);
			BukkitUtils.sendMessage(player, Colors.Green, "Successfully recreated portal at this location.");
			
			return true;
		}
		return false;
	}
	
    @Override
    public void moreHelp()
    {
        BukkitUtils.sendMessage(sender, Colors.Rose, "Create a new portal.");
        BukkitUtils.sendMessage(sender, Colors.Rose, "Using this command again will overwrite old data saved.");
        BukkitUtils.sendMessage(sender, Colors.Rose, "ONLY use this command WHERE you want the portal to teleport TO.");
    }

	@Override
	public boolean permission(CommandSender csender)
	{
		
		return Vault.perms.has(csender, "ct.command.portal.create");
	}

	@Override
	public BaseCommand newInstance()
	{
		return new NewPortalCommand();
	}
}
