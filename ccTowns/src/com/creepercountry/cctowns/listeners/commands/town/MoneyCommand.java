package com.creepercountry.cctowns.listeners.commands.town;

import java.text.DecimalFormat;

import net.milkbowl.vault.economy.EconomyResponse;

import org.bukkit.command.CommandSender;

import com.creepercountry.cctowns.hooks.Vault;
import com.creepercountry.cctowns.objects.TownUniverse;
import com.creepercountry.cctowns.util.BukkitUtils;
import com.creepercountry.cctowns.util.Colors;
import com.creepercountry.cctowns.util.NotRegisteredException;

public class MoneyCommand extends BaseCommand
{
    public MoneyCommand()
    {
        name = "money";
        usage = "<deposit|balance> [amount|(town] [amount])";
        minArgs = 1;
        maxArgs = 3;
    }
    
    @Override
    public boolean execute()
    {
    	String action = args.get(0).toLowerCase();
    	
    	if (isPlayer)
    	{
    		/*
    		 * Get the player as a user
    		 */
    		try
    		{
    			user = TownUniverse.getDataSource().getUser(sender.getName());
    		}
    		catch (NotRegisteredException n)
    		{
    			BukkitUtils.sendMessage(sender, Colors.Red, n.getMessage());
    			return true;
    		}
    		
    		/*
    		 * process command
    		 */
    		if ((args.size() == 1) && action.equals("balance"))
    		{
    			try
    			{
    				town = user.getTown();
    			}
    			catch (NotRegisteredException n)
    			{
    				BukkitUtils.sendMessage(sender, Colors.Red, "You need to be a member of a town to give money to it!");
    				return true;
    			}
    			
    			BukkitUtils.sendMessage(sender, Colors.Green, new DecimalFormat("$0.00").format(town.getBalance()));
    			return true;
    		}
    		else if ((args.size() == 3) && action.equals("deposit"))
    		{
    			if (!Vault.perms.has(sender, "ct.command.deposit.others"))
				{
					BukkitUtils.sendMessage(sender, Colors.Red, "You do not have permission to do that.");
					return true;
				}
    			
				if (!args.get(2).matches("\\d{1,5}\\.\\d{0,2}"))
				{
					BukkitUtils.sendMessage(sender, Colors.Red, args.get(2) + "should be formated as a number only");
					BukkitUtils.sendMessage(sender, Colors.Red, "Format: #####.##");
					BukkitUtils.sendMessage(sender, Colors.Red, "Number cannot be larger than 99999.99");
					return true;
				}
				
				try
    			{
    				town = TownUniverse.getDataSource().getTown(args.get(1).toLowerCase());
    			}
    			catch (NotRegisteredException n)
    			{
    				BukkitUtils.sendMessage(sender, Colors.Red, "no town by that name!");
    				return true;
    			}
				
				try
				{
					EconomyResponse er = Vault.econ.withdrawPlayer(user.getName(), Double.parseDouble(args.get(2)));
					if (!er.transactionSuccess())
					{
						BukkitUtils.sendMessage(sender, Colors.Red, er.errorMessage);
						return true;
					}
					BukkitUtils.sendMessage(sender, Colors.Gold, String.format("You now have %s in your holdings!", er.amount));
					
					double newbal = town.getBalance() + Double.parseDouble(args.get(2));
					town.setBalance(newbal);
					BukkitUtils.sendMessage(sender, Colors.Green, "You have successfully deposited: " + new DecimalFormat("$0.00").format(newbal));
					return true;
				}
				catch (NumberFormatException f)
				{
					BukkitUtils.severe(f.getMessage());
					return false;
				}
    		}
    		else if (args.size() == 2)
    		{
    			if (action.equals("deposit"))
    			{
    				if (!args.get(1).matches("\\d{1,5}\\.\\d{0,2}"))
    				{
    					BukkitUtils.sendMessage(sender, Colors.Red, args.get(1) + "should be formated as a number only");
    					BukkitUtils.sendMessage(sender, Colors.Red, "Format: #####.##");
    					BukkitUtils.sendMessage(sender, Colors.Red, "Number cannot be larger than 99999.99");
    					return true;
    				}
    				
    				try
        			{
        				town = user.getTown();
        			}
        			catch (NotRegisteredException n)
        			{
        				BukkitUtils.sendMessage(sender, Colors.Red, "You need to be a member of a town to give money to it!");
        				return true;
        			}
    				
    				try
    				{
    					EconomyResponse er = Vault.econ.withdrawPlayer(user.getName(), Double.parseDouble(args.get(1)));
    					if (!er.transactionSuccess())
    					{
    						BukkitUtils.sendMessage(sender, Colors.Red, er.errorMessage);
    						return true;
    					}
    					BukkitUtils.sendMessage(sender, Colors.Gold, String.format("You now have %s in your holdings!", er.amount));
    					
    					double newbal = town.getBalance() + Double.parseDouble(args.get(1));
    					town.setBalance(newbal);
    					BukkitUtils.sendMessage(sender, Colors.Green, "Town now has: " + new DecimalFormat("$0.00").format(newbal));
    					return true;
    				}
    				catch (NumberFormatException f)
    				{
    					BukkitUtils.severe(f.getMessage());
    					return false;
    				}
    			}
    			else if (action.equals("balance"))
    			{
    				if (!Vault.perms.has(sender, "ct.command.balance.others"))
    				{
    					BukkitUtils.sendMessage(sender, Colors.Red, "You do not have permission to do that.");
    					return true;
    				}
    				
    				try
    				{
    					town = TownUniverse.getDataSource().getTown(args.get(1).toLowerCase());
    				}
    				catch (NotRegisteredException n)
    				{
    					BukkitUtils.sendMessage(sender, Colors.Red, "No town found by that name!");
    				}
    				
    				BukkitUtils.sendMessage(sender, Colors.Green, new DecimalFormat("$0.00").format(town.getBalance()));
    				return true;
    			}
    		}
    		else
    			return false;
    	}
    	else // NOT player
    	{
    		if (args.size() == 2)
    		{
    			try
				{
					town = TownUniverse.getDataSource().getTown(args.get(1).toLowerCase());
				}
				catch (NotRegisteredException n)
				{
					BukkitUtils.sendMessage(sender, Colors.Red, "No town found by that name!");
				}
				
				BukkitUtils.sendMessage(sender, Colors.Green, new DecimalFormat("$0.00").format(town.getBalance()));
				return true;
    		}
    		else if ((args.size() == 3))
    		{
    			if (!args.get(2).matches("\\d{1,5}\\.\\d{1,2}"))
				{
					BukkitUtils.sendMessage(sender, Colors.Red, args.get(2) + "should be formated as a number only");
					BukkitUtils.sendMessage(sender, Colors.Red, "Format: #####.##");
					BukkitUtils.sendMessage(sender, Colors.Red, "Number cannot be larger than 99999.99");
					return true;
				}
				
				try
    			{
    				town = TownUniverse.getDataSource().getTown(args.get(1).toLowerCase());
    			}
    			catch (NotRegisteredException n)
    			{
    				BukkitUtils.sendMessage(sender, Colors.Red, "no town by that name!");
    				return true;
    			}
				
				try
				{
					double newbal = town.getBalance() + Double.parseDouble(args.get(2));
					town.setBalance(newbal);
					BukkitUtils.sendMessage(sender, Colors.Green, "You have successfully deposited: " + new DecimalFormat("$0.00").format(newbal));
					return true;
				}
				catch (NumberFormatException f)
				{
					BukkitUtils.severe(f.getMessage());
					return false;
				}
    		}
    		else
    			return false;
    	}
		return false;
    }
    
    @Override
    public void moreHelp()
    {
        BukkitUtils.sendMessage(sender, Colors.Rose, "Deposit money into a town bank, or view the balance of the account");
        BukkitUtils.sendMessage(sender, Colors.Rose, "if you are depositing money: number cannot be larger than 99999.99");
    }

    @Override
    public boolean permission(CommandSender csender)
    {
    	return Vault.perms.has(csender, "ct.command.money");
    }

    @Override
    public BaseCommand newInstance()
    {
        return new MoneyCommand();
    }
}