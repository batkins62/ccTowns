package com.creepercountry.cctowns.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.flags.DefaultFlag;
import com.sk89q.worldguard.protection.flags.StateFlag;

public class WorldGuardObject
{
	/**
	 * WorldGaurd instance
	 */
	WorldGuardPlugin wg;
	
    /**
     * Flags
     */
    private StateFlag pvp, mobspawning, lighter, firespread, lavafire,
    lightning, chestaccess, pistons, use, vehicleplace, vehicledestroy,
    snowfall, snowmelt, iceform, icemelt, grassgrowth;
    
    /**
     * list of wg flags with costs
     */
    private static final Map<String, Double> flagsdetail = new HashMap<String, Double>();
    
    /**
     * list of wg StateFlags
     */
    private Map<String, StateFlag> StateFlagList = new HashMap<String, StateFlag>();
    
    /**
     * list of all wg flags available
     */
    private static final Set<String> flags = new HashSet<String>();
    
    /**
     * Constructor
     * 
     * @param wg
     */
    public WorldGuardObject(WorldGuardPlugin wg)
    {
    	clear();
    	this.wg = wg;
    	runStateFlags();
    	runFlagSet();
    	runFlagDetailHash();
    	runStateHash();
    }
    
    /**
     * clear the Hash's (useful in reloads where server doesnt shutdown)
     * 
     * clears: flagsdetail and flags
     */
    public void clear()
    {
    	flagsdetail.clear();
    	flags.clear();
    	StateFlagList.clear();
	}

    /**
     * Runs on object create, fills Set with wg flags usable
     * 
     * DO NOT USE THIS ELSEWHERE!!! use StateFlag instead.
     */
	private void runFlagSet()
    {
		flags.add("pvp");
		flags.add("mobspawning");
		flags.add("lighter");
		flags.add("firespread");
		flags.add("lavafire");
		flags.add("lightning");
		flags.add("chestaccess");
		flags.add("pistons");
		flags.add("use");
		flags.add("vehicleplace");
		flags.add("vehicledestroy");
		flags.add("snowfall");
		flags.add("snowmelt");
		flags.add("iceform");
		flags.add("icemelt");
		flags.add("heal");
		flags.add("grassgrowth");
		flags.add("feed");
		flags.add("nocmds");
	}
	
	/**
     * Runs on object create, fills the StateFlagList hash with values usable
     * 
     * DO NOT USE THIS ELSEWHERE!!! use StateFlag instead.
     */
	private void runStateHash()
	{
		StateFlagList.put("pvp", pvp);
		StateFlagList.put("mobspawning", mobspawning);
		StateFlagList.put("lighter", lighter);
		StateFlagList.put("firespread", firespread);
		StateFlagList.put("lavafire", lavafire);
		StateFlagList.put("lightning", lightning);
		StateFlagList.put("chestaccess", chestaccess);
		StateFlagList.put("pistons", pistons);
		StateFlagList.put("use", use);
		StateFlagList.put("vehicleplace", vehicleplace);
		StateFlagList.put("vehicledestroy", vehicledestroy);
		StateFlagList.put("snowfall", snowfall);
		StateFlagList.put("snowmelt", snowmelt);
		StateFlagList.put("iceform", iceform);
		StateFlagList.put("icemelt", icemelt);
		StateFlagList.put("grassgrowth", grassgrowth);
	}

	/**
     * Runs on object create, fills the flagdetail hash with values usable
     * 
     * DO NOT USE THIS ELSEWHERE!!! use StateFlag instead.
     */
	private void runFlagDetailHash()
    {
		flagsdetail.put("pvp", 500.00);
		flagsdetail.put("mobspawning", 50000.00);
		flagsdetail.put("lighter", 1000.00);
		flagsdetail.put("firespread", 3000.00);
		flagsdetail.put("lavafire", 3000.00);
		flagsdetail.put("lightning", 2000.00);
		flagsdetail.put("chestaccess", 15000.00);
		flagsdetail.put("pistons", 2000.00);
		flagsdetail.put("use", 7500.00);
		flagsdetail.put("vehicleplace", 2000.00);
		flagsdetail.put("vehicledestroy", 2000.00);
		flagsdetail.put("snowfall", 3000.00);
		flagsdetail.put("snowmelt", 3000.00);
		flagsdetail.put("iceform", 3000.00);
		flagsdetail.put("icemelt", 3000.00);
		flagsdetail.put("heal", 50000.00);
		flagsdetail.put("grassgrowth", 1000.00);
		flagsdetail.put("feed", 75000.00);
		flagsdetail.put("nocmds", 4000.00);
    }

    /**
     * Runs on object create, fills StateFlags with values usable
     * 
     * DO NOT USE THIS ELSEWHERE!!! use StateFlag instead.
     */
	private void runStateFlags()
    {
        pvp = DefaultFlag.PVP;
        mobspawning = DefaultFlag.MOB_SPAWNING;
        lighter = DefaultFlag.LIGHTER;
        firespread = DefaultFlag.FIRE_SPREAD;
        lavafire = DefaultFlag.LAVA_FIRE;
        lightning = DefaultFlag.LIGHTNING;
        chestaccess = DefaultFlag.CHEST_ACCESS;
        pistons = DefaultFlag.PISTONS;
        use = DefaultFlag.USE;
        vehicleplace = DefaultFlag.PLACE_VEHICLE;
        vehicledestroy = DefaultFlag.DESTROY_VEHICLE;
        snowfall = DefaultFlag.SNOW_FALL;
        snowmelt = DefaultFlag.SNOW_MELT;
        iceform = DefaultFlag.ICE_FORM;
        icemelt = DefaultFlag.ICE_MELT;
        grassgrowth = DefaultFlag.GRASS_SPREAD;
    }
	
	//
	// TODO: make a WorldGuardException then have some of these throw rather return null
	//
    
    /**
     * Get the cost of a flag
     * 
     * @return as Double, 0.00 if flag doesnt exist
     */
    public Double getCost(String flag)
    {
    	if (!flagsdetail.containsKey(flag))
    		return 0.00;
    	
		return flagsdetail.get(flag);
    }
    
    /**
     * Get a list of wg flags available.
     * 
     * @return set of wg flags
     */
    public Set<String> getFlags()
    {
    	return flags;
    }
    
    /**
     * Checks if a flag exists.
     * 
     * @param flag
     * @return true if flag is valid
     */
    public boolean hasFlag(String flag)
    {
    	return getFlag(flag) != null;
    }
    
    /**
     * Get the list of flags as a List not a Set for easier manipulation.
     * 
     * @return List<String>
     */
    public List<String> getFlagsAsList()
    {
    	List<String> out = new ArrayList<String>();
    	out.clear();
    	for (String item : flags)
    	{
    		out.add(item);
    		DebugMode.log(item);
    	}
    	return out;
    }
    
    /**
     * get the flag
     * 
     * @param flag
     * @return the StateFlag, null if flag not found
     */
    public StateFlag getFlag(String flag)
    {
    	if (!StateFlagList.containsKey(flag))
    		return null;
    	
		return StateFlagList.get(flag);
    }
}
