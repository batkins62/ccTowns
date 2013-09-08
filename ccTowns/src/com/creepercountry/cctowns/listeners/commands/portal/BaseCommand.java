package com.creepercountry.cctowns.listeners.commands.portal;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.creepercountry.cctowns.main.CTPlugin;
import com.creepercountry.cctowns.objects.TownUniverse;
import com.creepercountry.cctowns.objects.town.Town;
import com.creepercountry.cctowns.objects.user.User;
import com.creepercountry.cctowns.util.BukkitUtils;
import com.creepercountry.cctowns.util.Colors;
import com.creepercountry.cctowns.util.StopWatch;

/**
* Abstract class representing a command. When run by the command manager (
* {@link CommandExecutor}), it pre-processes all the data into more useful forms.
* Extending classes should adjust required fields in their constructor
*
*/
public abstract class BaseCommand
{
	protected final CTPlugin plugin = CTPlugin.getInstance();
	protected StopWatch sw = plugin.getStopWatch();
	protected CommandSender sender;
	protected User user;
	protected Town town;
	protected boolean isPlayer = false;
	protected List<String> args = new ArrayList<String>();
	protected String usedCommand;
	protected Player player;
	protected TownUniverse universe = plugin.getTownUniverse();
	// Commands below can be set by each individual command
	public String name;
	public int minArgs = -1;
	public int maxArgs = -1;
	public boolean allowConsole = true;
	public boolean commandPassThrough = false;
	public String usage;
	public List<SubCommand> subCommands = new ArrayList<SubCommand>();

	/**
	 * Method called by the command manager in {@link CTPlugin} to run the
	 * command. Arguments are processed into a list for easier manipulating.
	 * Argument lengths, permissions and sender types are all handled.
	 *
	 * @param csender
	 * {@link CommandSender} to send data to
	 * @param preArgs arguments to be processed
	 * @param cmd command being executed
	 * @return true on success, false if there is an error in the checks or if
	 * the extending command returns false
	 */
	public boolean run(CommandSender csender, String[] preArgs, String cmd)
	{
		// Get the current time for StopWatch
		long start = System.nanoTime();
		
		sender = csender;
		usedCommand = cmd;
		// Sort out arguments
		args.clear();
		args.addAll(Arrays.asList(preArgs));
		// Check arg lengths
		if (minArgs > -1 && args.size() < minArgs || maxArgs > -1 && args.size() > maxArgs)
		{
			BukkitUtils.sendMessage(sender, Colors.Red, "Wrong arguments supplied!");
			sendUsage();
			return true;
		}
		// Check if sender should be a player
		if (!allowConsole && !(sender instanceof Player))
		{
			return false;
		}
		if (sender instanceof Player)
		{
			player = (Player) sender;
			isPlayer = true;
		}
		// Check permission
		if (!permission())
		{
			BukkitUtils.sendMessage(sender, Colors.Red, "You do not have permission to do that!");
			return false;
		}
		
		// log to StopWatch
        sw.setLoad("portalBaseCommand", System.nanoTime() - start);
        
		return execute();
	}

	/**
	 * Runs the extending command. Should only be run by the BaseCommand after
	 * all pre-processing is done
	 *
	 * @return true on success, false otherwise
	 */
	public abstract boolean execute();

	/**
	 * Performs the extending command's permission check.
	 *
	 * @return true if the user has permission, false if not
	 */
	public final boolean permission()
	{
		return permission(sender);
	}

	public abstract boolean permission(CommandSender csender);

	public abstract BaseCommand newInstance();

	/**
	 * Sends advanced help to the sender
	 */
	public void moreHelp()
	{
	}

	/**
	 * Displays the help information for this command
	 */
	public void sendUsage()
	{
		BukkitUtils.sendMessage(sender, Colors.Red, "/" + usedCommand + " " + name + " " + usage);
	}
	
	/**
	 * Formats a string with a provided title and padding and centers title.
	 * 
	 * @param title
	 * @param fill
	 * @return
	 */
    protected String cleanTitle(String title, String fill)
    {
        int chatWidthMax = 53; 											// Vanilla client line character max
        int titleWidth = title.length() + 2; 							// Title's character width with 2 spaces padding
        int fillWidth = (int) ((chatWidthMax - titleWidth) / 2D); 		// Fill string calculation for padding either side
        String cleanTitle = "";
        
        for(int i = 0; i < fillWidth; i++)
            cleanTitle += fill;
        cleanTitle += " " + Colors.LightBlue + title + Colors.Blue + " ";
        for(int i = 0; i < fillWidth; i++)
            cleanTitle += fill;
        
        return cleanTitle;
    }
}