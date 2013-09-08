package com.creepercountry.cctowns.objects.user;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Chunk;

import com.creepercountry.cctowns.objects.town.EconomyObject;
import com.creepercountry.cctowns.objects.town.Town;
import com.creepercountry.cctowns.util.AlreadyRegisteredException;
import com.creepercountry.cctowns.util.EmptyTownException;
import com.creepercountry.cctowns.util.NotRegisteredException;
import com.creepercountry.cctowns.util.TownException;

public class User extends EconomyObject implements IResident, IDonator, IFighter
{
	private List<String> messages = new ArrayList<String>();
	private List<String> ip = new ArrayList<String>();
	private List<Integer> tickets = new ArrayList<Integer>();
	private Town town;
	private long lastOnline;
	private Chunk chunk;
	private String rank;
	private boolean canNether, nagPstone;
	
	private int flyviolation;
	private boolean canFly, valid;
	private String dpackage;
	private long pdate, term;
	
	private String pvpchampion;
	private int pvplost;
	private boolean pvpwon;
	private long pvplast;

    public User(String name)
    {
    	setName(name);
    	setRank("");
    	setAllowedNether(true);
    	setNagPstone(false);
    	setCanFly(false);
    	setValid(false);
    	setPvPWon(false);
    }
    
    @Override
    public Chunk getChunk()
    {
    	return chunk;
    }
    
    @Override
    public boolean hasChunk()
    {
    	return chunk != null;
    }
    
    @Override
    public void setChunk(Chunk chu)
    {
    	this.chunk = chu;
    }
    
    @Override
    public void setNagPstone(boolean nag)
    {
    	this.nagPstone = nag;
    }
    
    @Override
    public boolean getNagPstone()
    {
    	return this.nagPstone;
    }

    @Override
    public void setLastOnline(long lastOnline)
    {
    	this.lastOnline = lastOnline;
    }
    
    @Override
    public long getLastOnline()
    {
    	return lastOnline;
    }
    
    @Override
	public List<String> getMessages()
	{
		return messages;
	}
    
    @Override
	public boolean hasMessage(String msg)
	{
		return messages.contains(msg);
	}
	
    @Override
	public int msgQuantity()
	{
		return messages.size();
	}
	
    @Override
	public boolean hasMessages()
	{
		return !messages.isEmpty();
	}
	
    @Override
	public void addMessage(String msg)
	{
		messages.add(msg);
	}
	
    @Override
	public void setRank(String rank)
	{
		if (rank.matches(" "))
			rank = "";

		this.rank = rank;
	}
	
    @Override
	public void removeMessage(String msg) throws TownException
	{
		if (!hasMessage(msg))
			throw new TownException("No messages found!");
		else
			messages.remove(msg);
	}

    @Override
	public String getRank()
	{
		return rank;
	}
    
    @Override
	public boolean isOwner()
	{
		return hasTown() ? town.isOwner(this) : false;
	}
	
    @Override
	public boolean isVice()
	{
		return hasTown() ? town.isVice(this) : false;
	}
    
    @Override
	public boolean isAssistant()
	{
		return hasTown() ? town.isAssistant(this) : false;
	}

    @Override
	public boolean hasTown()
	{
		return !(town == null);
	}

    @Override
	public void clear() throws EmptyTownException
	{
		if (hasTown())
			try
			{
				town.removeUser(this);
			}
			catch (NotRegisteredException e)
			{
			}
		messages.clear();
		ip.clear();
		tickets.clear();
	}
	
    @Override
	public void setAllowedNether(boolean can)
	{
		canNether = can;
	}
	
    @Override
	public boolean getAllowedNether()
	{
		return canNether;
	}
	
    @Override
	public Town getTown() throws NotRegisteredException
	{
		if (hasTown())
			return town;
		
		else
			throw new NotRegisteredException("Resident doesn't belong to any town");
	}
	
    @Override
	public void setTown(Town town) throws AlreadyRegisteredException
	{
		if (town == null)
		{
			this.town = null;
			return;
		}
		
		if (this.town == town)
			return;
		
		if (hasTown())
			throw new AlreadyRegisteredException("Already Registered Town!");
		
		this.town = town;
	}
    
	@Override
	public void setLastPvP(long epoch)
	{
		if (new Long(epoch).equals(0L))
			epoch = System.currentTimeMillis();

		this.pvplast = epoch;
	}

	@Override
	public boolean hasPvPChamp()
	{
		return (pvpchampion != null);
	}

	@Override
	public long getLastPvP()
	{
		return pvplast;
	}

	@Override
	public void setPvPChamp(String name)
	{
		this.pvpchampion = name;
	}

	@Override
	public String getPvPChamp()
	{
		return pvpchampion;
	}

	@Override
	public void setPvPLost(int amt)
	{
		pvplost = pvplost + amt;
	}

	@Override
	public int getPvPLost()
	{
		return pvplost;
	}

	@Override
	public void setPvPWon(boolean won)
	{
		this.pvpwon = won;
	}

	@Override
	public boolean getPvPWon()
	{
		return pvpwon;
	}
	
	@Override
    public boolean getCanFly()
    {
    	return canFly;
    }

    @Override
    public void setCanFly(boolean fly)
    {
    	this.canFly = fly;
    }
    
    @Override
    public long getPurchaseDate()
    {
    	return pdate;
    }
    
    @Override
    public String getDonationPackage()
    {
    	return dpackage;
    }
    
    @Override
	public void addFlyViolation(int amount)
	{
		flyviolation = flyviolation + amount;
	}
	
    @Override
	public int getFlyViolation()
	{
		return flyviolation;
	}
	
    @Override
	public void setFlyViolation(int amount)
	{
		flyviolation = amount;
	}
    
    @Override
	public void subtractFlyViolation(int amount)
	{
		flyviolation = flyviolation - amount;
	}
	
    @Override
	public void zeroFlyViolation()
	{
		flyviolation = 0;
	}
	
    @Override
	public boolean hasFlyViolation()
	{
		return flyviolation > 0;
	}
	
    @Override
	public void setPurchaseDate(long expire)
	{
		this.pdate = expire;
	}
    
    @Override
	public void setDonationPackage(String bundle)
	{
		this.dpackage = bundle;
	}

	@Override
	public void setValid(boolean val)
	{
		this.valid = val;
	}

	@Override
	public boolean isValid()
	{
		return valid;
	}
	
	@Override
	public void setTerm(long milli)
	{
		this.term = milli;
	}
	
	@Override
	public long getTerm()
	{
		return term;
	}
	
	@Override
	public boolean equals(Object obj)
	{
		User res = (User) obj;
		return res.getName().equals(this.getName()) ? true : false;
	}

	@Override
	public boolean hasPurchaseDate()
	{
		return pdate != 0;
	}

	@Override
	public boolean hasDonationPackage()
	{
		return dpackage != null;
	}

	@Override
	public boolean hasTerm()
	{
		return term != 0;
	}
	
	@Override
	public void addRaffleTicket(Integer num)
	{
		if (!tickets.contains(num))
			this.tickets.add(num);
	}
	
	@Override
	public void removeRaffleTicket(Integer num)
	{
		if (tickets.contains(num))
			this.tickets.remove(num);
	}
	
	@Override
	public List<Integer> getRaffleTickets()
	{
		return this.tickets;
	}
	
	@Override
	public boolean hasRaffleTickets()
	{
		return !tickets.isEmpty();
	}
	
	@Override
	public boolean hasRaffleTicket(Integer num)
	{
		return this.tickets.contains(num);
	}
}
