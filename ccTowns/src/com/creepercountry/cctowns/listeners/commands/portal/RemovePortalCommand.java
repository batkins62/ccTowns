package com.creepercountry.cctowns.listeners.commands.portal;

import org.bukkit.command.CommandSender;

import com.creepercountry.cctowns.hooks.Vault;
import com.creepercountry.cctowns.objects.TownUniverse;
import com.creepercountry.cctowns.util.BukkitUtils;
import com.creepercountry.cctowns.util.Colors;
import com.creepercountry.cctowns.util.NotRegisteredException;
import com.creepercountry.cctowns.util.TownException;

public class RemovePortalCommand extends BaseCommand
{
    public RemovePortalCommand()
    {
        name = "remove";
        usage = "<portal>";
        minArgs = 1;
        maxArgs = 1;
    }

	@Override
	public boolean execute()
	{
		try 
		{
			TownUniverse.getDataSource().removePortal(TownUniverse.getDataSource().getPortal(args.get(0)));
			BukkitUtils.sendMessage(sender, Colors.Green, "Successfully removed portal!");
			return true;
		}
		catch (NotRegisteredException nre)
		{
			BukkitUtils.sendMessage(sender, Colors.Red, nre.getMessage());
			return true;
		}
		catch (TownException te)
		{
			BukkitUtils.sendMessage(sender, Colors.Red, te.getMessage());
			return true;
		}
	}
	
    @Override
    public void moreHelp()
    {
        BukkitUtils.sendMessage(sender, Colors.Rose, "Delete a portal.");
    }

	@Override
	public boolean permission(CommandSender csender)
	{
		return Vault.perms.has(csender, "ct.command.portal.delete");
	}

	@Override
	public BaseCommand newInstance()
	{
		return new RemovePortalCommand();
	}
}