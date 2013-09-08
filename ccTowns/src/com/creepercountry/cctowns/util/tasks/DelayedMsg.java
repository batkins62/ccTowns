package com.creepercountry.cctowns.util.tasks;

import java.util.List;

import org.bukkit.command.CommandSender;
import org.bukkit.scheduler.BukkitRunnable;

import com.creepercountry.cctowns.util.BukkitUtils;

public class DelayedMsg extends BukkitRunnable
{
	private List<CommandSender> senders;
	private String msg;
	
	public DelayedMsg(List<CommandSender> senderz, String message)
	{
		this.senders = senderz;
		this.msg = message;
	}
	
	@Override
	public void run()
	{
		 for (CommandSender sender : senders)
			 BukkitUtils.sendMessage(sender, "", msg);
	}
}
