package com.creepercountry.cctowns.util.tasks;

import java.util.List;

import net.sacredlabyrinth.Phaed.PreciousStones.FieldFlag;
import net.sacredlabyrinth.Phaed.PreciousStones.PreciousStones;
import net.sacredlabyrinth.Phaed.PreciousStones.vectors.Field;

import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import com.creepercountry.cctowns.main.CTPlugin;
import com.creepercountry.cctowns.util.BukkitUtils;
import com.creepercountry.cctowns.util.DebugMode;

public class NagPstoneTask extends BukkitRunnable
{
	private PreciousStones pstone;
	private Player plr;
	
	public NagPstoneTask(CTPlugin instance, Player player)
	{
		this.pstone = instance.getPreciousStones();
		this.plr = player;
	}
	
	@Override
	public void run()
	{
		 List<Field> fields = pstone.getForceFieldManager().getOwnersFields(plr, FieldFlag.ALL);
		 
		 if (fields == null)
		 {
			 BukkitUtils.info("DFADSFADSFADSFADSFADSFDSFADFADSfa");
		 }
		 DebugMode.log("found: " + fields.toString());
	}
}
