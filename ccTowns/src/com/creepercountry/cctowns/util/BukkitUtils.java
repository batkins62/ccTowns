package com.creepercountry.cctowns.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.milkbowl.vault.permission.Permission;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.material.MaterialData;

import com.creepercountry.cctowns.main.CTInfo;

public class BukkitUtils
{
	/**
	 * Logger static instance
	 */
	private static final Logger log = Logger.getLogger("ccTowns");
	
	/**
	 * Static permissions instance
	 */
	static Permission permission;

	public static String materialName(int type)
	{
		final Material mat = Material.getMaterial(type);
		return mat != null ? mat.toString().replace('_', ' ').toLowerCase() : String.valueOf(type);
	}

	public static String materialName(int type, byte rawData)
	{
		final Material mat = Material.getMaterial(type);
		if (mat != null)
		{
			if ((type == 6 || type == 17 || type == 18) && rawData > 0 || type == 35 || type == 43 || type == 44)
			{
				final MaterialData data = mat.getNewData(rawData);
				if (data != null)
				{
					return data.toString().toLowerCase().replace('_', ' ').replaceAll("[^a-z ]", "");
				}
			}
			return mat.toString().replace('_', ' ').toLowerCase();
		}
		return String.valueOf(type);
	}

	/**
	 * Send a message to a CommandSender (can be a player or console).
	 *
	 * @param player sender to send to
	 * @param msg message to send
	 * @param clr color of message
	 */
	public static void sendMessage(CommandSender player, String clr, String msg)
	{
		if (player != null)
		{
			player.sendMessage(clr + msg);
		}
		// TODO: add in line-length checking, color wrapping etc
	}
	
    /**
     * Returns true if the given Player has the specific permission
     *
     * @param player The Player who is being checked for permission
     * @param type The String of the permission, ex. admin
     * @return True if the given Player has the specific permission
     */
    public static boolean hasPermission(Player player, String type)
    {
        return permission.has(player, "ct." + type);
    }

	/**
	 * Send an info level log message to console
	 * includes: version 
	 *
	 * @param msg
	 */
	public static void info(String msg)
	{
		final StringBuilder out = new StringBuilder();
		out.append("[ccTowns v" + CTInfo.FULL_VERSION.toString() + "] ");
		out.append(msg);
		log.log(Level.INFO, out.toString());
	}

	/**
	 * Send a warn level log message to console
	 * includes: version
	 *
	 * @param msg
	 */
	public static void warning(String msg)
	{
		final StringBuilder out = new StringBuilder();
		out.append("[ccTowns v" + CTInfo.FULL_VERSION.toString() + "] ");
		out.append(msg);
		log.log(Level.WARNING, out.toString());
	}

	/**
	 * Send a warn level stacktrace to console
	 * includes: version
	 *
	 * @param msg
	 * @param ex
	 */
	public static void warning(String msg, Exception ex)
	{
		log.log(Level.WARNING, "[ccTowns v" + CTInfo.FULL_VERSION.toString() + "] " + msg + ":", ex);
	}

	/**
	 * Send a severe level log message to console
	 * includes: version
	 *
	 * @param msg
	 */
	public static void severe(String msg)
	{
		final StringBuilder out = new StringBuilder();
		out.append("[ccTowns v" + CTInfo.FULL_VERSION.toString() + "] ");
		out.append(msg);
		log.log(Level.SEVERE, out.toString());
	}

	/**
	 * Send a severe level stacktrace to console
	 * includes: version
	 *
	 * @param msg
	 * @param ex
	 */
	public static void severe(String msg, Exception ex)
	{
		log.log(Level.SEVERE, "[ccTowns v" + CTInfo.FULL_VERSION.toString() + "] " + msg + ":", ex);
	}
	
	/**
	 * Send a severe level stacktrace to console
	 * includes: version
	 *
	 * @param msg
	 * @param ex
	 */
	public static void severe(String msg, NoSuchFieldError ex)
	{
		log.log(Level.SEVERE, "[ccTowns v" + CTInfo.FULL_VERSION.toString() + "] " + msg + ":", ex);
	}
	
    /**
     * Join an array into a String, where the array values are delimited by spaces.
     *
     * @param arr
     * @return
     */
    public static String join(String[] arr)
    {
        return join(arr, 0);
    }

    /**
     * Join an array into a String, where the array values are delimited by spaces, starting at the given offset.
     *
     * @param arr
     * @param offset
     * @return
     */
    public static String join(String[] arr, int offset)
    {
        return join(arr, offset, " ");
    }

    /**
     * Join an array into a String, where the array values are delimited by the given string, starting at the given offset.
     *
     * @param arr
     * @param offset
     * @param delim
     * @return
     */
    public static String join(String[] arr, int offset, String delim)
    {
        String str = "";

        if (arr == null || arr.length == 0)
        {
            return str;
        }

        for (int i = offset; i < arr.length; i++)
        {
            str += arr[i] + delim;
        }

        return str.trim();
    }
	
	/**
	 * Log a FATAL error, this will provide features to continue.
	 * 
	 * @param msg
	 * @param disable
	 * @param log
	 */
	public static void fatal(String msg, boolean disable, boolean log)
	{
		// TODO: finish...
	}

	/**
	 * Returns the friendly bridgeName of an entity
	 *
	 * @param entity
	 * @return
	 */
	public static String getEntityName(Entity entity)
	{
		if (entity instanceof Player)
		{
			return ((Player) entity).getName();
		}
		if (entity instanceof TNTPrimed)
		{
			return "TNT";
		}
		if (entity.getClass().getSimpleName().startsWith("Craft"))
		{
			return entity.getClass().getSimpleName().substring(5);
		}
		return "Unknown";
	}

	/**
	 * Returns the distance between two Locations
	 *
	 * @param from
	 * @param to
	 * @return
	 */
	public static double distance(Location from, Location to)
	{
		return Math.sqrt(Math.pow(from.getX() - to.getX(), 2) + Math.pow(from.getY() - to.getY(), 2) + Math.pow(from.getZ() - to.getZ(), 2));
	}

	@SuppressWarnings("rawtypes")
	public static String join(List arr, String separator)
	{
		if (arr == null || arr.size() == 0)
			return "";
		String out = arr.get(0).toString();
		for (int i = 1; i < arr.size(); i++)
			out += separator + arr.get(i);
		return out;
	}

	public static final int lineLength = 54;
	public static List<String> listArr(String[] args)
	{
		return list(Arrays.asList(args));
	}

	public static List<String> listArr(String[] args, String prefix)
	{
		return list(Arrays.asList(args), prefix);
	}


	public static List<String> list(List<String> args)
	{
		return list(args, "");
	}	

	public static List<String> list(List<String> args, String prefix)
	{
		if (args.size() > 0)
		{
			String line = "";
			for (int i = 0; i < args.size() - 1; i++)
				line += args.get(i) + ", ";
			line += args.get(args.size() - 1).toString();

			return color(prefix + line);
		}

		return new ArrayList<String>();
	}
	
	public static List<String> wordWrap(String[] tokens)
	{
		List<String> out = new ArrayList<String>();
		out.add("");

		for (String s : tokens) {
			if (stripColour(out.get(out.size() - 1)).length() + stripColour(s).length() + 1 > lineLength)
				out.add("");
			out.set(out.size() - 1, out.get(out.size() - 1) + s + " ");
		}

		return out;
	}
	
	public static String stripColour(String s)
	{
		String out = "";
		for (int i = 0; i < s.length(); i++)
		{
			String c = s.substring(i, i + 1);
			if (c.equals("§"))
				i += 1;
			else
				out += c;
		}
		return out;
	}
	
	public static List<String> color(String line)
	{
		List<String> out = wordWrap(line.split(" "));

		String c = "f";
		for (int i = 0; i < out.size(); i++)
		{
			if (!out.get(i).startsWith("§") && !c.equalsIgnoreCase("f"))
				out.set(i, "§" + c + out.get(i));

			for (int index = 0; index < lineLength; index++)
				try
				{
					if (out.get(i).substring(index, index + 1).equalsIgnoreCase("\u00A7"))
						c = out.get(i).substring(index + 1, index + 2);
				}
				catch (Exception e)
				{
				}
		}

		return out;
	}
}