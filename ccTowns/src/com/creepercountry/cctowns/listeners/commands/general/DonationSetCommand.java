package com.creepercountry.cctowns.listeners.commands.general;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.concurrent.TimeUnit;
import java.util.regex.PatternSyntaxException;

import org.bukkit.command.CommandSender;

import com.creepercountry.cctowns.hooks.Vault;
import com.creepercountry.cctowns.objects.TownUniverse;
import com.creepercountry.cctowns.objects.user.User;
import com.creepercountry.cctowns.util.BukkitUtils;
import com.creepercountry.cctowns.util.Colors;
import com.creepercountry.cctowns.util.NotRegisteredException;

public class DonationSetCommand extends BaseCommand
{
    public DonationSetCommand()
    {
        name = "donationset";
        usage = "<player> <package> <term(in days)> <purchase date(MM-DD-YYYY)> <flyviolation>";
        minArgs = 5;
        allowConsole = true;
    }

	@Override
	public boolean execute()
	{
		try
		{
			User user = TownUniverse.getDataSource().getUser(args.get(0));
			String dpackage = args.get(1).toLowerCase();
			long term = TimeUnit.MILLISECONDS.convert(Long.parseLong(args.get(2)), TimeUnit.DAYS);
			int flyviolation= Integer.parseInt(args.get(4));
			String[] rpd = args.get(3).split("-");
			Calendar cal = GregorianCalendar.getInstance(plugin.getLocale());
			cal.set(Calendar.MONTH, Integer.parseInt(rpd[0]) + 1);
			cal.set(Calendar.DATE, Integer.parseInt(rpd[1]));
			cal.set(Calendar.YEAR, Integer.parseInt(rpd[2]));
			long pdate = cal.getTimeInMillis();
			
			user.setDonationPackage(dpackage);
			user.setTerm(term);
			user.setFlyViolation(flyviolation);
			user.setPurchaseDate(pdate);
			TownUniverse.getDataSource().saveUser(user);
			
			BukkitUtils.sendMessage(sender, Colors.Blue, "Heres how the file looks:");
			BukkitUtils.sendMessage(sender, Colors.LightBlue, "Donation Package: " + user.getDonationPackage());
			BukkitUtils.sendMessage(sender, Colors.LightBlue, "Term: " + Long.toString(user.getTerm()) + " (Days: " + TimeUnit.DAYS.convert(user.getTerm(), TimeUnit.MILLISECONDS));
			BukkitUtils.sendMessage(sender, Colors.LightBlue, "Purchase Date: " + DateFormat.getDateInstance(DateFormat.LONG, plugin.getLocale()).format(cal.getTime()));
			BukkitUtils.sendMessage(sender, Colors.LightBlue, "Fly Violation: " + Integer.toString(user.getFlyViolation()));
			BukkitUtils.sendMessage(sender, Colors.Green, "Success writing to file!");
		}
		catch (NotRegisteredException nre)
		{
			BukkitUtils.sendMessage(sender, Colors.Red, nre.getMessage());
			return true;
		}
		catch (NumberFormatException nfe)
		{
			BukkitUtils.sendMessage(sender, Colors.Red, nfe.getMessage());
			return true;
		}
		catch (IndexOutOfBoundsException ioob)
		{
			BukkitUtils.sendMessage(sender, Colors.Red, ioob.getMessage());
			BukkitUtils.severe(ioob.getMessage());
			return true;
		}
		catch (PatternSyntaxException pse)
		{
			BukkitUtils.sendMessage(sender, Colors.Red, pse.getMessage());
			BukkitUtils.severe(pse.getMessage());
			return true;
		}
		catch (Exception e)
		{
			BukkitUtils.sendMessage(sender, Colors.Red, "Alert Server Administration. Outputing severe exception to console.");
			BukkitUtils.severe("com.creepercountry.cctowns.listeners.commands.general.DonationSetCommand threw an Exception: ", e);
			return true;
		}
		
		return true;
	}
	
    @Override
    public void moreHelp()
    {
        BukkitUtils.sendMessage(sender, Colors.Rose, "used for conversion from past version, fixes flaws with donator files.");
    }

	@Override
	public boolean permission(CommandSender csender)
	{
		
		return Vault.perms.has(csender, "ct.command.donation");
	}

	@Override
	public BaseCommand newInstance()
	{
		return new DonationSetCommand();
	}

}