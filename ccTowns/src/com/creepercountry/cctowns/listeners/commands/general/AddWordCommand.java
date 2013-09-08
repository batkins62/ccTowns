package com.creepercountry.cctowns.listeners.commands.general;

import org.bukkit.command.CommandSender;

import com.creepercountry.cctowns.hooks.Vault;
import com.creepercountry.cctowns.util.BukkitUtils;
import com.creepercountry.cctowns.util.Colors;

public class AddWordCommand extends BaseCommand
{
    public AddWordCommand()
    {
        name = "word";
        usage = "<ignore|nag> <word>";
        minArgs = 2;
    }

    @Override
    public boolean execute()
    {
    	
		
        return true;
    }
    
    @Override
    public void moreHelp()
    {
        BukkitUtils.sendMessage(sender, Colors.Gold, "Add a word to the lists of special chat words");
        BukkitUtils.sendMessage(sender, Colors.Rose, "nag: words that will trigger pstone offenders nag.");
        BukkitUtils.sendMessage(sender, Colors.Rose, "ignore: words that wont log for watched players");
    }

    @Override
    public boolean permission(CommandSender csender)
    {
    	return Vault.perms.has(csender, "ct.command.word");
    }

    @Override
    public BaseCommand newInstance()
    {
        return new AddWordCommand();
    }
}