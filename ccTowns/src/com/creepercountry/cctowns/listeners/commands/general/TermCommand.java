package com.creepercountry.cctowns.listeners.commands.general;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;

import org.bukkit.command.CommandSender;

import com.creepercountry.cctowns.hooks.Vault;
import com.creepercountry.cctowns.objects.TownUniverse;
import com.creepercountry.cctowns.util.BukkitUtils;
import com.creepercountry.cctowns.util.Colors;
import com.creepercountry.cctowns.util.NotRegisteredException;

public class TermCommand extends BaseCommand
{
    public TermCommand()
    {
        name = "term";
        usage = "<set|add|subtract|current> [<player> <month#> <dayofmonth> <year>]";
        minArgs = 1;
        maxArgs = 5;
    }

	@Override
	public boolean execute()
	{
		try
		{
			Calendar cur = GregorianCalendar.getInstance(plugin.getTimeZone(), plugin.getLocale());
			String action = args.get(0).toLowerCase();
			boolean lenApprop = args.size() >= maxArgs;
			
			if (lenApprop)
			{
				try
				{
					user = TownUniverse.getDataSource().getUser(args.get(1));
				}
				catch (NotRegisteredException nre)
				{
					BukkitUtils.sendMessage(sender, Colors.Red, nre.getMessage());
					return true;
				}
			}
			
			if (action.equals("current"))
			{
				StringBuffer sb = new StringBuffer();
				sb.append("Current Time: ");
				sb.append(DateFormat.getDateTimeInstance(DateFormat.LONG, DateFormat.LONG, plugin.getLocale()).format(cur.getTime()));
				sb.append("\nTimeZone set to: " + cur.getTimeZone().getDisplayName());
				sb.append(" (" + cur.getTimeZone().getOffset(System.currentTimeMillis()) + ")");
				BukkitUtils.sendMessage(sender, ((isPlayer) ? Colors.LightBlue : Colors.White), sb.toString());
			}
			else if (action.equals("set") && lenApprop)
			{
				Calendar ucal = GregorianCalendar.getInstance(plugin.getTimeZone(), plugin.getLocale());
				ucal.set(Calendar.MONTH, Integer.parseInt(args.get(2)) + 1);
				ucal.set(Calendar.DAY_OF_MONTH, Integer.parseInt(args.get(3)));
				ucal.set(Calendar.YEAR, Integer.parseInt(args.get(4)));
				user.setTerm(ucal.getTimeInMillis() - cur.getTimeInMillis());
				DateFormat df = DateFormat.getDateInstance(DateFormat.LONG, plugin.getLocale());
				ucal.setTimeInMillis(System.currentTimeMillis());
				ucal.add(Calendar.MILLISECOND, Long.valueOf(user.getTerm()).intValue());
				BukkitUtils.sendMessage(sender, Colors.LightBlue, df.format(ucal.getTime()));
			}
			else if (action.equals("subtract") && lenApprop)
			{
				BukkitUtils.sendMessage(sender, Colors.Red, "Feature not supported yet :(");
			}
			else if (action.equals("add") && lenApprop)
			{
				BukkitUtils.sendMessage(sender, Colors.Red, "Feature not supported yet :(");
			}
			else
				return false;
			
			if (lenApprop)
				TownUniverse.getDataSource().saveUser(user);
			
			return true;
		}
		catch (NumberFormatException nfe)
		{
			BukkitUtils.severe(nfe.getMessage());
			return false;
		}
		catch (Exception ex)
		{
			BukkitUtils.severe("com.creepercountry.cctowns.listeners.commands.general.TermCommand", ex);
			return false;
		}
	}
	
    @Override
    public void moreHelp()
    {
        BukkitUtils.sendMessage(sender, Colors.Rose, "");
    }

	@Override
	public boolean permission(CommandSender csender)
	{
		
		return Vault.perms.has(csender, "ct.command.donation");
	}

	@Override
	public BaseCommand newInstance()
	{
		return new TermCommand();
	}

}
