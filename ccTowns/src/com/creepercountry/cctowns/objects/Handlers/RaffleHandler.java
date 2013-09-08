package com.creepercountry.cctowns.objects.Handlers;

import java.util.Collection;
import java.util.HashMap;
import java.util.Random;

import com.creepercountry.cctowns.main.config.MainConfigObject;
import com.creepercountry.cctowns.objects.MasterObject;
import com.creepercountry.cctowns.objects.TownUniverse;
import com.creepercountry.cctowns.objects.user.User;
import com.creepercountry.cctowns.util.TwoObjectMap;

public class RaffleHandler extends MasterObject
{
	private Random random;
	private HashMap<Integer, User> tickets;
	
	public RaffleHandler()
	{
		setName("Raffle-" + this.hashCode());
		random = new Random();
		tickets = new HashMap<Integer, User>();
	}
	
	public RaffleHandler(long seed)
	{
		setName("Raffle-" + this.hashCode());
		random = new Random(seed);
		tickets = new HashMap<Integer, User>();
	}
	
	public String newTicket(User user)
	{
		int next = getNext();
		tickets.put(Integer.valueOf(next), user);
		TownUniverse.getDataSource().saveUser(user);
		return "Ticket Number: " + next;
	}
	
	public TwoObjectMap pickWinner()
	{
		double cur = Integer.valueOf(tickets.size()).doubleValue() * MainConfigObject.raffle_price;
		User usr = tickets.get(Integer.valueOf(random.nextInt(tickets.size())));
		TwoObjectMap out = new TwoObjectMap(usr, Double.valueOf(cur));
		tickets.clear();
		return out;
	}
	
	private int getNext()
	{
		int last = 0;
		for (Integer i : tickets.keySet())
			if (i.intValue() > last)
				last = i.intValue();
		return last;
	}
	
	public User getTicket(Integer num)
	{
		return tickets.get(num);
	}
	
	public Collection<User> getTicketHolders()
	{
		return tickets.values();
	}
	
	public int getTotalTickets()
	{
		return tickets.size();
	}
}
