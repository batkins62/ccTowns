package com.creepercountry.cctowns.listeners.commands.town;

import org.bukkit.command.CommandSender;

import com.creepercountry.cctowns.hooks.Vault;
import com.creepercountry.cctowns.objects.TownUniverse;
import com.creepercountry.cctowns.objects.town.Town;
import com.creepercountry.cctowns.util.BukkitUtils;
import com.creepercountry.cctowns.util.Colors;
import com.creepercountry.cctowns.util.TownException;

public class DeleteTownCommand extends BaseCommand
{
    public DeleteTownCommand()
    {
        name = "delete";
        usage = "<town>";
        minArgs = 1;
    }

	@Override
	public boolean execute()
	{
		try
		{
			Town town = TownUniverse.getDataSource().getTown(args.get(0));

			BukkitUtils.sendMessage(sender, Colors.Blue, "Town had " + town.getNumResidents() + " that needed to be removed.");
			
			TownUniverse.getDataSource().removeTown(town);
			
			BukkitUtils.sendMessage(sender, Colors.Green, "Town " + town.getName() + " was successfully removed!");
		}
		catch (TownException x)
		{
			BukkitUtils.sendMessage(sender, Colors.Red, x.getMessage());
		}
		
		return true;
	}
	
    @Override
    public void moreHelp()
    {
        BukkitUtils.sendMessage(sender, Colors.Rose, "Deletes a town.");
        BukkitUtils.sendMessage(sender, Colors.Rose, "This will remove all of the town, but store the file");
        BukkitUtils.sendMessage(sender, Colors.Rose, "inside the data folder, for archiving purposes");
    }

	@Override
	public boolean permission(CommandSender csender)
	{
		
		return Vault.perms.has(csender, "ct.town.delete");
	}

	@Override
	public BaseCommand newInstance()
	{
		return new DeleteTownCommand();
	}
}
