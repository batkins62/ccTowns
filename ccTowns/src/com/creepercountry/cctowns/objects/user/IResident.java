package com.creepercountry.cctowns.objects.user;

import java.util.List;

import org.bukkit.Chunk;


import com.creepercountry.cctowns.objects.town.Town;
import com.creepercountry.cctowns.util.AlreadyRegisteredException;
import com.creepercountry.cctowns.util.EmptyTownException;
import com.creepercountry.cctowns.util.NotRegisteredException;
import com.creepercountry.cctowns.util.TownException;

public interface IResident
{  
	public Chunk getChunk();
    
	public boolean hasChunk();
    
	public void setChunk(Chunk chu);
    
	public void setNagPstone(boolean nag);
    
	public boolean getNagPstone();
    
	public void setLastOnline(long lastOnline);
    
	public long getLastOnline();
    
	public List<String> getMessages();
    
	public boolean hasMessage(String msg);
    
	public int msgQuantity();
    
	public boolean hasMessages();
    
	public void addMessage(String msg);
    
	public void setRank(String rank);
    
	public void removeMessage(String msg) throws TownException;
    
	public String getRank();
    
	public boolean isOwner();
    
	public boolean isVice();
    
	public boolean isAssistant();
    
	public boolean hasTown();
    
	public void clear() throws EmptyTownException;
    
	public void setAllowedNether(boolean can);
    
	public boolean getAllowedNether();
    
	public Town getTown() throws NotRegisteredException;
    
	public void setTown(Town town) throws AlreadyRegisteredException;
	
	public void addRaffleTicket(Integer num);
	
	public void removeRaffleTicket(Integer num);
	
	public List<Integer> getRaffleTickets();
	
	public boolean hasRaffleTickets();
	
	public boolean hasRaffleTicket(Integer num);
}
