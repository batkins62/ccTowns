package com.creepercountry.cctowns.listeners.commands.general;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;

import com.creepercountry.cctowns.hooks.Vault;
import com.creepercountry.cctowns.main.CTEngine;
import com.creepercountry.cctowns.objects.TownUniverse;
import com.creepercountry.cctowns.objects.user.User;
import com.creepercountry.cctowns.util.BukkitUtils;
import com.creepercountry.cctowns.util.Colors;
import com.creepercountry.cctowns.util.NotRegisteredException;
import com.creepercountry.cctowns.util.TimeUtils;

public class DonationCommand extends BaseCommand
{
    public DonationCommand()
    {
        name = "donation";
        usage = "<new|expire|status> <player> [package] [term]";
        minArgs = 2;
        maxArgs = 4;
    }

	@Override
	public boolean execute()
	{
		if (args.get(0).equalsIgnoreCase("expire"))
		{
			try
			{
				User user = TownUniverse.getDataSource().getUser(args.get(1));		
				user.setValid(false);
				TownUniverse.getDataSource().saveUser(user);
				
				BukkitUtils.sendMessage(sender, Colors.Green, String.format("Successfully expired player %s.", user.getName()));
			}
			catch (NotRegisteredException nre)
			{
				return false;
			}
			
			return true;
		}
		else if (args.get(0).equalsIgnoreCase("status"))
		{
			try
			{
				TimeZone tz = TimeZone.getTimeZone("America/Phoenix");
				User user = TownUniverse.getDataSource().getUser(args.get(1));	
				TimeUtils tu = new TimeUtils(user.getPurchaseDate());
				GregorianCalendar current = new GregorianCalendar();
				current.setTimeZone(tz);
				current.setTimeInMillis(System.currentTimeMillis());
				tu.setTimeZone(tz);
				
				BukkitUtils.sendMessage(sender, Colors.Blue, cleanTitle(user.getName(), "-"));
				BukkitUtils.sendMessage(sender, Colors.LightBlue, String.format(plugin.getLocale(), "Current Date:%s %s", Colors.Blue,
						DateFormat.getDateTimeInstance(DateFormat.LONG, DateFormat.LONG, plugin.getLocale()).format(current.getTime())));
				BukkitUtils.sendMessage(sender, Colors.LightBlue, String.format(plugin.getLocale(), "Current Package:%s %s", Colors.Blue, user.getDonationPackage()));
				BukkitUtils.sendMessage(sender, Colors.LightBlue, String.format(plugin.getLocale(), "Term Length:%s %s days (Expires on: ~%s)", Colors.Blue,
						TimeUnit.DAYS.convert(user.getTerm(), TimeUnit.MILLISECONDS),
						DateFormat.getDateInstance(DateFormat.SHORT, plugin.getLocale())));
				BukkitUtils.sendMessage(sender, Colors.LightBlue, String.format(plugin.getLocale(), "Expiration (full):%s %s", Colors.Blue,
						DateFormat.getDateTimeInstance(DateFormat.LONG, DateFormat.LONG, plugin.getLocale())));
				BukkitUtils.sendMessage(sender, Colors.LightBlue, String.format(plugin.getLocale(), "Current Fly Violation Level:%s %s", Colors.Blue, Integer.toString(user.getFlyViolation())));
			}
			catch (NotRegisteredException nre)
			{
				BukkitUtils.sendMessage(sender, Colors.Red, nre.getMessage());
				return true;
			}
			return true;
		}
		
		if (args.get(0).equalsIgnoreCase("new") && (args.size() < 4))
			return false;
		
		try
		{
			// TODO: yes i know its redundant, this is here to remind how to use this date.
			TimeZone tz = plugin.getTimeZone();
			User user = TownUniverse.getDataSource().getUser(args.get(1));	
			TimeUtils tu = new TimeUtils(System.currentTimeMillis());
			tu.setTimeZone(tz);
			tu.add(args.get(3).toLowerCase());
			
			if (user.isValid())
			{
				long oldPD = user.getPurchaseDate();
				long oldTM = user.getTerm();
				TimeUtils oldTU = new TimeUtils(oldPD);
				oldTU.setTimeZone(tz);
				oldTU.add(Calendar.MILLISECOND, new Long(oldTM).intValue());
				oldTU.add(args.get(3).toLowerCase());
				long term = oldTU.getTimeInMillis() - System.currentTimeMillis();
				
				user.setPurchaseDate(tu.getTimeInMillis());
				user.zeroFlyViolation();
				user.setTerm(term);
				
				BukkitUtils.sendMessage(sender, Colors.Gold, "Player already was a donator.");
			}
			else
			{
				user.setPurchaseDate(tu.getTimeInMillis());
				tu.add(args.get(3).toLowerCase());
				long term = tu.getTimeInMillis() - System.currentTimeMillis();
				user.setTerm(term);
				user.setDonationPackage(args.get(2).toLowerCase());
				user.zeroFlyViolation();
				user.setValid(true);
			}
			
			TownUniverse.getDataSource().saveUser(user);
			if (plugin.isOnline(user.getName()))
				CTEngine.CHECKFLY = true;
			
			BukkitUtils.sendMessage(sender, Colors.Green, String.format("Successfully added player %s to %s bundle.", user.getName(), args.get(2).toLowerCase()));
			BukkitUtils.sendMessage(sender, Colors.Gold, String.format("Use '/cc donation expire %s' to expire the player from donation perks.", user.getName()));
			BukkitUtils.sendMessage(sender, Colors.Gold, String.format(plugin.getLocale(), "Player will have a term of %s days (%s hours)", TimeUnit.DAYS.convert(user.getTerm(), TimeUnit.MILLISECONDS), TimeUnit.HOURS.convert(user.getTerm(), TimeUnit.MILLISECONDS)));
		}
		catch (NotRegisteredException nre)
		{
			BukkitUtils.sendMessage(sender, Colors.Red, nre.getMessage());
			for (OfflinePlayer plr : Bukkit.getOperators())
				try
				{
					User usr = TownUniverse.getDataSource().getUser(plr.getName());
					usr.addMessage("Player donated but was not a registered player and perks were not added.");
					usr.addMessage(nre.getMessage());
					TownUniverse.getDataSource().saveUser(usr);
				}
				catch (NotRegisteredException nre2) { continue; }
				catch (NullPointerException npe) { continue; }
		}
		
		return true;
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
		return new DonationCommand();
	}

}
