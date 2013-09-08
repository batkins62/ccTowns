package com.creepercountry.cctowns.listeners;

import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.block.Sign;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.SignChangeEvent;

import com.creepercountry.cctowns.hooks.Vault;
import com.creepercountry.cctowns.main.CTPlugin;
import com.creepercountry.cctowns.objects.TownUniverse;
import com.creepercountry.cctowns.objects.town.Town;
import com.creepercountry.cctowns.objects.user.User;
import com.creepercountry.cctowns.util.BukkitUtils;
import com.creepercountry.cctowns.util.Colors;
import com.creepercountry.cctowns.util.NotRegisteredException;

public class CTBlockListener implements Listener
{	
	/**
	 * Town Universe
	 */
	private final TownUniverse universe;
	
	public CTBlockListener(CTPlugin instance)
	{
		this.universe = instance.getTownUniverse();
	}
	
	@EventHandler // EventPriority.NORMAL by default
	public void onSignChangeEvent(SignChangeEvent event)
	{
		if (event.getLine(0).equalsIgnoreCase("[plot]"))
		{
			universe.onPlotSignEvent(event);
		}
		
		if (event.getLine(0).equalsIgnoreCase(""))
		{
			
		}
	}
	
	@EventHandler // EventPriority.NORMAL by default
	public void onBlockBreakEvent(BlockBreakEvent event)
	{
		if (event.getBlock().getState().getType().equals(Material.SIGN))
		{
			Sign sign = (Sign) event.getBlock();
			if (!sign.getLine(0).equalsIgnoreCase(Colors.Blue + "[FOR SALE]") || sign.getLine(0).equalsIgnoreCase(Colors.Red + "[SOLD]"))
				return;
			
			// check for perms
			if (!Vault.perms.has(event.getPlayer(), "ct.plot.destroy"))
			{
				BukkitUtils.sendMessage(event.getPlayer(), Colors.Red, "You do not have permission to break plots!");
				event.setCancelled(true);
				event.setExpToDrop(0);
			}
			
			Town town = null;
			Chunk chunk = null;
			User user = null;
			User breaker = null;
			try { breaker = TownUniverse.getDataSource().getUser(event.getPlayer().getName()); } catch (NotRegisteredException nre) { return; }
			
			for (Town t : TownUniverse.getDataSource().getTowns())
			{
				for (Chunk c : town.getChunks())
				{
					if (c.equals(sign.getChunk()))
					{
						chunk = c;
						town = t;
						
						for (User r : t.getUsers())
						{
							if (r.getChunk().equals(c))
							{
								user = r;
							}
						}
					}
				}
			}
			
			if ((town == null) || (chunk == null) || (user == null) || (breaker == null))
				return;
		}
	}
}
