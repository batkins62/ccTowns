package com.creepercountry.cctowns.util.tasks;

import org.bukkit.scheduler.BukkitRunnable;

import com.creepercountry.cctowns.main.CTEngine;
import com.creepercountry.cctowns.objects.TownUniverse;
import com.creepercountry.cctowns.objects.user.User;
import com.creepercountry.cctowns.util.BukkitUtils;
import com.creepercountry.cctowns.util.Colors;
import com.creepercountry.cctowns.util.DebugMode;

public class ViolationDecremationTask extends BukkitRunnable
{
    public ViolationDecremationTask()
    {
    	DebugMode.log("Creating Decrementation Task!");
    }
    
	@Override
	public void run()
	{
		BukkitUtils.info("Running Violation Decremation Sweep...");
		
		int curInt = 0;
		int noVL = 0;
		for (User usr : TownUniverse.getOnlineRegisteredUsers())
		{
			curInt++;
			if (usr.hasFlyViolation())
			{
				usr.subtractFlyViolation(1);
				TownUniverse.getDataSource().saveUser(usr);
				BukkitUtils.info("Player " + usr.getName() + " now has a Fly Violation Level of: " + usr.getFlyViolation());
			}
			else
				noVL++;
		}
		
		if (curInt <= noVL)
		{
			BukkitUtils.warning("========================================");
			BukkitUtils.warning(Colors.Red + "No donators online have a VL! Cancelling Check Task (DECREMENTATION)");
			BukkitUtils.warning("Check Tasks will be enabled next time a donator VL!");
			BukkitUtils.warning("========================================");
			CTEngine.DECREMENTATION = false;
			this.cancel();
		}
	}
}
