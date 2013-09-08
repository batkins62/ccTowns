package com.creepercountry.cctowns.objects.Handlers;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import com.creepercountry.cctowns.main.CTPlugin;
import com.creepercountry.cctowns.objects.MasterObject;
import com.creepercountry.cctowns.objects.TownUniverse;
import com.creepercountry.cctowns.objects.user.User;
import com.creepercountry.cctowns.util.AlreadyRegisteredException;
import com.creepercountry.cctowns.util.NotRegisteredException;
import com.creepercountry.cctowns.util.PvPFinalRound;
import com.creepercountry.cctowns.util.TimeUtils;

public class PvPHandler extends MasterObject
{
	private Player fighter, fightee;
	private User rfightee;
	private int round;
	private int fighterwin, fighterloss;
	TimeUtils time;
	private CTPlugin plugin = CTPlugin.getInstance();
	
	/**
	 * the constructor. STARTS THE MATCH
	 * @param ee the fightee
	 * @param er the fighter
	 */
	public PvPHandler(Player ee, Player er, Integer sessionid)
	{
		setName(sessionid.toString());
		setFighter(er);
		setFightee(ee);
		round = 1;
		
		// set the date and time
		this.time = new TimeUtils(System.currentTimeMillis());
		try { TownUniverse.getDataSource().newPvPHandler(sessionid, this); } catch (AlreadyRegisteredException e) {}
	}
	
	/**
	 * Run when the fightee lost the battle
	 * @throws PvPFinalRound if lost 10 times
	 */
	public void fighteeLost() throws PvPFinalRound
	{
		time.add(Calendar.DAY_OF_MONTH, 7);
		rfightee.setLastPvP(time.getTimeInMillis());
		rfightee.setPvPLost(1);
		rfightee.setPvPChamp(fighter.getName());
		TownUniverse.getDataSource().saveUser(rfightee);
		
		// check if the player lost 10 times
		if (rfightee.getPvPLost() <= 10)
			throw new PvPFinalRound("Challenger has lost 10 matches");
	}
	
	/**
	 * Run when the fightee won the battle
	 */
	public String fighteeWon()
	{
		StringBuffer sb = new StringBuffer();
		Date date = time.getDate();
		DateFormat df = new SimpleDateFormat("MM.dd.yyyy G 'at' HH:mm:ss z", plugin.getLocale());
		sb.append(df.format(date) + "\n");
		
		rfightee.setPvPChamp(fighter.getName());
		rfightee.setPvPWon(true);
		rfightee.setLastPvP(time.getTimeInMillis());
		TownUniverse.getDataSource().saveUser(rfightee);
		return sb.toString();
	}
	
	/**
	 * adds a new round to the counter, at three rounds throw final
	 * @throws PvPFinalRound on final third round
	 */
	public void newRound() throws PvPFinalRound
	{
		this.round++;
		
		if (round == 3)
			throw new PvPFinalRound("Final Round - " + getName());
	}
	
	/**
	 * Set the champion of the round.
	 * 
	 * @param who set to 0 for champion win, or 1 for champion loss
	 */
	public void roundChampion(int who)
	{
		switch (who)
		{
			case 1: fighterloss++;
			case 0: fighterwin++;
		}
	}
	
	/**
	 * @param ee the player whom is challenging the champion.
	 */
	public void setFightee(Player ee)
	{
		this.fightee = ee;
		try { this.rfightee = TownUniverse.getDataSource().getUser(ee.getName()); } catch (NotRegisteredException e) {}
	}
	
	/**
	 * @param  er the player whom is the champion being challenged
	 */
	public void setFighter(Player er)
	{
		this.fighter = er;
	}
	
	/**
	 * @return fighter
	 * @throws NotRegisteredException if player is not online
	 */
	public Player getFighter() throws NotRegisteredException
	{
		if (!Arrays.asList(Bukkit.getOnlinePlayers()).contains(fighter))
			throw new NotRegisteredException("This player is not online.");
		
		return fighter;
	}

	/**
	 * @return fightee
	 * @throws NotRegisteredException if player is not online
	 */
	public Player getFightee() throws NotRegisteredException
	{
		if (!Arrays.asList(Bukkit.getOnlinePlayers()).contains(fightee))
			throw new NotRegisteredException("This player is not online.");
		
		return fightee;
	}
	
	/**
	 * @return timeutils object used
	 */
	public TimeUtils getTime()
	{
		return time;
	}
	
	/**
	 * @return the round we are on
	 */
	public Integer getRound()
	{
		return Integer.valueOf(round);
	}
	
	/**
	 * @return the round winner
	 * @param who set to 0 for champion wins, or 1 for champion losses
	 * RETURNS 0 IF who IS NOT 0 or 1
	 */
	public Integer getRoundWinner(int who)
	{
		Integer num = new Integer(0);
		switch (who)
		{
			case 1: num = new Integer(fighterloss);
			case 0: num = new Integer(fighterwin);
		}
		return num;
	}
}
