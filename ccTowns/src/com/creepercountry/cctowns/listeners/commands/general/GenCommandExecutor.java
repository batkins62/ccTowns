package com.creepercountry.cctowns.listeners.commands.general;

import java.util.List;

import org.apache.commons.lang.ArrayUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import com.creepercountry.cctowns.main.CTPlugin;
import com.creepercountry.cctowns.util.StopWatch;

import java.util.ArrayList;

public class GenCommandExecutor implements CommandExecutor
{
	private StopWatch sw = CTPlugin.getInstance().getStopWatch();
	private List<BaseCommand> commands = new ArrayList<BaseCommand>();

	public GenCommandExecutor()
	{
		// Get the current time for StopWatch
		long start = System.nanoTime();
		
		// Register commands
		commands.add(new HelpCommand());
		commands.add(new NewWatchedCommand());
		commands.add(new ServerCommand());
		commands.add(new ReloadCommand());
		commands.add(new DonationCommand());
		commands.add(new DebugModeCommand());
		commands.add(new StopWatchCommand());
		commands.add(new DefaultFlyLOC());
		commands.add(new PvPCommand());
		commands.add(new DonationSetCommand());
		commands.add(new TermCommand());
		commands.add(new RaffleCommand());
		
		// log to StopWatch
        sw.setLoad("GenCommandExecutor", System.nanoTime() - start);
	}

	/**
	 * Command manager
	 *
	 * @param sender - {@link CommandSender}
	 * @param command - {@link Command}
	 * @param label command name
	 * @param args arguments
	 */
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args)
	{
		// Get the current time for StopWatch
		long start = System.nanoTime();
		
		// If no arg provided for command, set to help by default
		if (args.length == 0)
		{
			args = new String[]{"help"};
		}

		// Loop through commands to find match. Supports sub-commands
		BaseCommand townCmd;
		BaseCommand[] guardCmdArray = commands.toArray(new BaseCommand[commands.size()]);
		int index = 0;
		String[] tempArgs = args;

		while (index < guardCmdArray.length && tempArgs.length > 0)
		{
			townCmd = guardCmdArray[index];
			if(tempArgs[0].equalsIgnoreCase(townCmd.name))
			{
				if(townCmd.subCommands.size() > 0 && tempArgs.length > 1)
				{
					guardCmdArray = townCmd.subCommands.toArray(new BaseCommand[townCmd.subCommands.size()]);
					index = 0;
					tempArgs = (String[]) ArrayUtils.remove(tempArgs, 0);
				}
				else
				{
					tempArgs = (String[]) ArrayUtils.remove(tempArgs, 0);
					
					// log to StopWatch
			        sw.setLoad("genOnCommand", System.nanoTime() - start);
			        
					return townCmd.newInstance().run(sender, tempArgs, label);
				}
			}
			else
			{
				index++;
			}
		}

		new HelpCommand().run(sender, args, label);
		
		// log to StopWatch
        sw.setLoad("genOnCommand", System.nanoTime() - start);
        
		return true;
	}

	/**
	 * @return the commands
	 */
	public List<BaseCommand> getCommands()
	{
		return commands;
	}
}