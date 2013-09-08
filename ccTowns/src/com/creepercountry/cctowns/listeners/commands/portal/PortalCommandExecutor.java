package com.creepercountry.cctowns.listeners.commands.portal;

import java.util.List;

import org.apache.commons.lang.ArrayUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import com.creepercountry.cctowns.main.CTPlugin;
import com.creepercountry.cctowns.util.StopWatch;

import java.util.ArrayList;

public class PortalCommandExecutor implements CommandExecutor
{
	private StopWatch sw = CTPlugin.getInstance().getStopWatch();
	private List<BaseCommand> commands = new ArrayList<BaseCommand>();

	public PortalCommandExecutor()
	{
		// Get the current time for StopWatch
		long start = System.nanoTime();
		
		// Register commands
		commands.add(new HelpCommand());
		commands.add(new NewPortalCommand());
		commands.add(new RemovePortalCommand());
		commands.add(new DefaultPortalCommand());
		commands.add(new BanFromNetherCommand());
		
		// log to StopWatch
        sw.setLoad("portalCommandExecutor", System.nanoTime() - start);
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
			        sw.setLoad("portalOnCommand", System.nanoTime() - start);
			        
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
        sw.setLoad("portalOnCommand", System.nanoTime() - start);
        
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
