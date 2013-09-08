package com.creepercountry.cctowns.listeners.commands.general;

import org.bukkit.command.CommandSender;

import com.creepercountry.cctowns.hooks.Vault;
import com.creepercountry.cctowns.util.BukkitUtils;
import com.creepercountry.cctowns.util.Colors;

import java.util.List;

public class HelpCommand extends BaseCommand
{
	public HelpCommand()
	{
		name = "help";
		maxArgs = 1;
		minArgs = 0;
		usage = "<- lists all town commands";
	}

	@Override
	public boolean execute()
	{
		if (args.isEmpty()) // General help
		{
			BukkitUtils.sendMessage(sender, Colors.White, cleanTitle("Town Help", "="));
			BukkitUtils.sendMessage(sender, Colors.White, "Type /town help <command> for more info on that command");
			List<BaseCommand> var = plugin.getGenCommandExecutor().getCommands();
			for (BaseCommand cmd : var.toArray(new BaseCommand[var.size()]))
			{ 
				if (cmd.permission(sender))
				{
					BukkitUtils.sendMessage(sender, Colors.Green, "- " + "/" + usedCommand + " " + cmd.name + Colors.Green + " " + cmd.usage);
				}
			}
		} // Command-specific help
		else {
			List<BaseCommand> var = plugin.getGenCommandExecutor().getCommands();
			for (BaseCommand cmd : var.toArray(new BaseCommand[var.size()])) {
				if (cmd.permission(sender) && cmd.name.equalsIgnoreCase(args.get(0)))
				{
					BukkitUtils.sendMessage(sender, Colors.White, "---------------------- Town - " + cmd.name);
					BukkitUtils.sendMessage(sender, Colors.Green, "- " + "/" + usedCommand + " " + cmd.name + Colors.Green + " " + cmd.usage);
					cmd.sender = sender;
					cmd.moreHelp();
					return true;
				}
			}
			BukkitUtils.sendMessage(sender, Colors.Red, "No command found by that name");
		}
		return true;
	}

	@Override
	public void moreHelp()
	{
		BukkitUtils.sendMessage(sender, Colors.LightBlue, "Shows all ccTown commands");
		BukkitUtils.sendMessage(sender, Colors.Red, "Type " + Colors.Gray + "/town help <command>" + Colors.Red + " for help on that command");
	}

	@Override
	public boolean permission(CommandSender csender)
	{
		return Vault.perms.has(csender, "ct.command.help");
	}

	@Override
	public BaseCommand newInstance()
	{
		return new HelpCommand();
	}

	/**
	 * Formats a string with a provided title and padding and centers title.
	 * 
	 * @param title
	 * @param fill
	 * @return
	 */
	@Override
	protected String cleanTitle(String title, String fill)
	{
		int chatWidthMax = 53;										// Vanilla client line character max
		int titleWidth = title.length() + 2;						// Title's character width with 2 spaces padding
		int fillWidth = (int) ((chatWidthMax - titleWidth) / 2D);	// Fill string calculation for padding either side
		String cleanTitle = "";

		for(int i = 0; i < fillWidth; i++)
			cleanTitle += fill;
		cleanTitle += " " + title + " ";
		for(int i = 0; i < fillWidth; i++)
			cleanTitle += fill;

		return cleanTitle;
	}
}