package com.creepercountry.cctowns.hooks;

import net.milkbowl.vault.chat.Chat;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import net.milkbowl.vault.permission.Permission;

import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;

import com.creepercountry.cctowns.main.CTPlugin;
import com.creepercountry.cctowns.util.BukkitUtils;
import com.creepercountry.cctowns.util.Colors;

public class Vault implements Hook
{
    /**
     * our vault/hooks instances and variables
     */
    public static Economy econ = null;
    public static Permission perms = null;
    public static Chat chat = null;
    
    private Plugin pl;
	private CTPlugin plugin;

    @Override
    public void onEnable(CTPlugin plugin)
    {
    	this.plugin = plugin;
		this.pl = plugin.getServer().getPluginManager().getPlugin("Vault");
		if (isEnabled())
		{
			setupEconomy();
			setupChat();
			setupPermissions();
		}
    }
    
	@Override
	public void onDisable(CTPlugin plugin)
	{
		this.pl = null;
		Vault.econ = null;
		Vault.perms = null;
		Vault.chat = null;
	}
    
	/**
     * Depend on Vault.jar
     * @return
     */
    private boolean setupEconomy()
    {
    	RegisteredServiceProvider<Economy> rsp = plugin.getServer().getServicesManager().getRegistration(Economy.class);
        econ = rsp.getProvider();
        return econ != null;
    }
    
    /**
     * Setup chat (part of vault) dependancy
     * @return
     */
    private boolean setupChat()
    {
        RegisteredServiceProvider<Chat> rsp = plugin.getServer().getServicesManager().getRegistration(Chat.class);
        chat = rsp.getProvider();
        return chat != null;
    }

    /**
     * Setup permissions (part of vault) dependancy
     * @return
     */
    private boolean setupPermissions()
    {
        RegisteredServiceProvider<Permission> rsp = plugin.getServer().getServicesManager().getRegistration(Permission.class);
        perms = rsp.getProvider();
        return perms != null;
    }

	@Override
	public int getUniqueID()
	{
		return pl.hashCode();
	}

	@Override
	public String getName()
	{
		return pl.getName();
	}

	@Override
	public boolean isEnabled()
	{
		return pl != null;
	}

	@Override
	public Plugin getPlugin()
	{
		return pl;
	}
	
    /**
     * use vault to charge the user.
     * 
     * @param amount
     * @param player
     * @param msg to show what your charging the user for (only shows console when logged)
     * @return true once we successfully charge player
     */
    public boolean chargeuser(double amount, Player player, String msg)
    {
    	// charge the user then log outcome then return true
		EconomyResponse r = econ.withdrawPlayer(player.getName(), amount);
        if(r.transactionSuccess())
        {
            BukkitUtils.sendMessage(player, Colors.Green, String.format("You were charged %s and now have %s", econ.format(r.amount), econ.format(r.balance)));
            BukkitUtils.info(player.getName() + " was charged $" + amount + " for " + msg);
            return true;
        }
        else
        {
        	BukkitUtils.sendMessage(player, Colors.Red, String.format("An error occured: %s", r.errorMessage));
        }
        return false;
    }
    
    /**
     * use vault to pay the user
     * 
     * @param player
     * @param amount
     * @param msg
     */
    public void payuser(Player player, double amount, String msg)
    {
    	// pay the user then log outcome
		EconomyResponse r = econ.depositPlayer(player.getName(), amount);
        if(r.transactionSuccess())
        {
            BukkitUtils.sendMessage(player, Colors.Green, String.format("You were given %s and now have %s for " + msg, econ.format(r.amount), econ.format(r.balance)));
            BukkitUtils.info(player.getName() + " recieved $" + amount + " for " + msg);
        }
        else
        {
        	BukkitUtils.sendMessage(player, Colors.Red, String.format("An error occured: %s", r.errorMessage));
        }
    }
}
