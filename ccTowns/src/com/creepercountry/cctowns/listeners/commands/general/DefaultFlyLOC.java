package com.creepercountry.cctowns.listeners.commands.general;

import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.util.Vector;

import com.creepercountry.cctowns.hooks.Vault;
import com.creepercountry.cctowns.main.config.ConfigNode;
import com.creepercountry.cctowns.util.BukkitUtils;
import com.creepercountry.cctowns.util.Colors;

public class DefaultFlyLOC extends BaseCommand
{
    public DefaultFlyLOC()
    {
        name = "default";
        usage = "";
        minArgs = 0;
        maxArgs = 0;
        allowConsole = false;
    }

    @Override
    public boolean execute()
    {
    	final Location loc = player.getLocation();
    	final String world = loc.getWorld().getName();
    	final Vector vector = loc.toVector();
    	
    	BukkitUtils.sendMessage(player, Colors.Gold, "Setting default location for fly offenders...");
    	plugin.getConfig().set(ConfigNode.FLY_PUNISH_LOCATION.getPath(), vector);
    	plugin.getConfig().set(ConfigNode.PUNISH_FLY_WORLD.getPath(), world);
    	plugin.saveConfig();
    	plugin.getConf().load();
    	BukkitUtils.sendMessage(player, Colors.Green, "Successfully set value.");
    	
		
		return true;
    }
    
    @Override
    public void moreHelp()
    {
        BukkitUtils.sendMessage(sender, Colors.Rose, "default location for fly. use standing in the tp location");
    }

    @Override
    public boolean permission(CommandSender csender)
    {
    	return Vault.perms.has(csender, "ct.command.defaultflyloc");
    }

    @Override
    public BaseCommand newInstance()
    {
        return new DefaultFlyLOC();
    }
}