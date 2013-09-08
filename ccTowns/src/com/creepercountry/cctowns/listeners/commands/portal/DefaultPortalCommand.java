package com.creepercountry.cctowns.listeners.commands.portal;

import org.bukkit.Location;
import org.bukkit.command.CommandSender;

import com.creepercountry.cctowns.hooks.Vault;
import com.creepercountry.cctowns.main.config.ConfigNode;
import com.creepercountry.cctowns.util.BukkitUtils;
import com.creepercountry.cctowns.util.Colors;

public class DefaultPortalCommand extends BaseCommand
{
    public DefaultPortalCommand()
    {
        name = "default";
        usage = "";
        allowConsole = false;
        maxArgs = 0;
    }

    @Override
    public boolean execute()
    {
	    BukkitUtils.sendMessage(player, Colors.Yellow, "Creating warp default warp point...");
	    Location loc = player.getLocation();
		
	    plugin.getConfig().set(ConfigNode.DEFAULT_PORTAL.getPath(), loc.toVector());
	    plugin.getConfig().set(ConfigNode.DEFAULT_PORTAL_WORLD.getPath(), loc.getWorld().getName());
	    plugin.saveConfig();
    	
		return true;
    }
    
    @Override
    public void moreHelp()
    {
        BukkitUtils.sendMessage(sender, Colors.Rose, "Creates a default warp spot, aka a 'hub' spot for players to warp to when no portal is found.");
        BukkitUtils.sendMessage(sender, Colors.Rose, "stand where you want the warp spot to send to while issuing this command.");
    }

    @Override
    public boolean permission(CommandSender csender)
    {
    	return Vault.perms.has(csender, "ct.command.portal");
    }

    @Override
    public BaseCommand newInstance()
    {
        return new DefaultPortalCommand();
    }
}
