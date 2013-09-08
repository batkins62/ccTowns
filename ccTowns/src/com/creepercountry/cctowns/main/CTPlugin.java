package com.creepercountry.cctowns.main;

import java.util.Locale;
import java.util.TimeZone;
import java.util.logging.Logger;

import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import com.creepercountry.cctowns.hooks.DependancyManager;
import com.creepercountry.cctowns.hooks.Essentials;
import com.creepercountry.cctowns.hooks.Hook;
import com.creepercountry.cctowns.hooks.NoCheatPlus;
import com.creepercountry.cctowns.hooks.PreciousStones;
import com.creepercountry.cctowns.hooks.Vault;
import com.creepercountry.cctowns.hooks.WorldGuard;
import com.creepercountry.cctowns.listeners.CTBlockListener;
import com.creepercountry.cctowns.listeners.CTEntityListener;
import com.creepercountry.cctowns.listeners.CTPlayerListener;
import com.creepercountry.cctowns.listeners.CTWeatherListener;
import com.creepercountry.cctowns.listeners.CTWorldListener;
import com.creepercountry.cctowns.listeners.commands.general.GenCommandExecutor;
import com.creepercountry.cctowns.listeners.commands.portal.PortalCommandExecutor;
import com.creepercountry.cctowns.listeners.commands.town.TownCommandExecutor;
import com.creepercountry.cctowns.main.config.MainConfigObject;
import com.creepercountry.cctowns.objects.TownUniverse;
import com.creepercountry.cctowns.objects.Handlers.RaffleHandler;
import com.creepercountry.cctowns.util.BukkitUtils;
import com.creepercountry.cctowns.util.Colors;
import com.creepercountry.cctowns.util.DebugMode;
import com.creepercountry.cctowns.util.NotRegisteredException;
import com.creepercountry.cctowns.util.StopWatch;
import com.creepercountry.cctowns.util.TownException;
import com.creepercountry.cctowns.util.Version;
import com.creepercountry.cctowns.util.WorldGuardObject;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;

public class CTPlugin extends JavaPlugin
{
    /**
     * Our Plugin instance
     */
    private static CTPlugin instance;
    
    /**
     * The StopWatch object
     */
    private StopWatch sw;
    
    /**
     * RaffleHandler object
     */
    private RaffleHandler rh;
    
    /**
     * The engine instance
     */
    private CTEngine engine;
    
    /**
     * TownUniverse variable
     */
	private TownUniverse townUniverse;
	
	/**
	 * DependancyManager object
	 */
	private DependancyManager dm;
	
	/**
	 * WorldGuard Object
	 */
	private WorldGuardObject wg;
	
	/**
	 * The plugins Locale
	 */
	private Locale locale;
	
	/**
	 * The plugins TimeZone
	 */
	private TimeZone timezone;
    
    /**
     * Main Config & config version
     */
    public static int cversion = 2;
    private MainConfigObject config;
    
    /**
     * The listeners
     */
    private CTPlayerListener playerListener;
    private CTWorldListener worldListener;
    private CTBlockListener blockListener;
    private CTWeatherListener weatherListener;
    private CTEntityListener entityListener;
    
    /**
     * The Executor
     */
    private TownCommandExecutor townCommandExecutor;
    private PortalCommandExecutor portalCommandExecutor;
    private GenCommandExecutor genCommandExecutor;
    
    /**
     * Logger instance
     */
    private static final Logger logger = Logger.getLogger("ccTowns");
    
    /* MY WISH LIST
     * 
     * on first run, or when wanted, config will be changed in game only. this will prompt on first run until admin completes.
     * 
     * such as '/cc config start' outputs:
     * Current value: "Language=En(US)"
     * Suggested value: "Language=En(US)"
     * Change the language output of this plugin. Note, this will change the Locale
     * 
     * by answering '/cc config change En(GB)' will output:
     * Current value: "Language=En(GB)"
     * Suggested value: "Language=En(US)"
     * Change the language output of this plugin. Note, this will change the Locale
     * 
     * by answering '/cc config next' will display the next config option.
     * Keep prompting admins on join until they complete this wizard. we know they did by asking the last question:
     * type: /cc config finish <ACCESSCODEPASSWORDWILLBERANDOM>
     * do not allow changing of the config while game is running, we can prevent by using admin password.
     * in the config at top we say:
     * NO CHANGES WILL SAVE WITHOUT CORRECT ADMIN PASSWORD BELOW.
     * this will compare the password saved in memory with supplied whether or not changes accept.
     * on game stop, admin password will show in config.
     * 
     * ---------------->
     * 
     * 
     * Change txt to MySql
     * 
     */
    
	@Override
    public void onEnable()
    {
		// Get the current time for StopWatch
		long start = System.nanoTime();
		
		// load our config. start the instance. start the StopWatch
		instance = this;
		sw = new StopWatch(this.getDescription().getName());
		if (!load())
			return;
		
		// Are we debuging?
		if (getConfig().getBoolean("debug", false))
		{
			System.out.println("[ccTowns] WE ARE DEBUGING ACCORDING TO THE CONFIG!");
			System.out.println("[ccTowns] DEBUG GO, DEBUG GO, DEBUG GO");
			System.out.println("[ccTowns] DEBUG GO, DEBUG GO, DEBUG GO");
			System.out.println("[ccTowns] DEBUG GO, DEBUG GO, DEBUG GO");
			System.out.println("[ccTowns] DEBUG GO, DEBUG GO, DEBUG GO");
			DebugMode.go();
			
			// Are we in dev mode
			if (getConfig().getBoolean("debug-dev", false))
			{
				System.out.println("[ccTowns] WE ARE DEV MODE ACCORDING TO THE CONFIG!");
				System.out.println("[ccTowns] DEVMODE GO, DEVMODE GO, DEVMODE GO");
				System.out.println("[ccTowns] DEVMODE GO, DEVMODE GO, DEVMODE GO");
				System.out.println("[ccTowns] DEVMODE GO, DEVMODE GO, DEVMODE GO");
				System.out.println("[ccTowns] DEVMODE GO, DEVMODE GO, DEVMODE GO");
				DebugMode.setDEVMODE(true);
			}
		}
		
		// create the locale & TimeZone
		locale = new Locale("en", "US");
		timezone = TimeZone.getTimeZone("America/Phoenix");
		
		// load the towns
		townUniverse = new TownUniverse(this);
		if (townUniverse.loadSettings())
			logger.info("loaded settings");
		
		// hook into depends
		pluginHooks();

		// register the listeners & executors
        try
        {
            registerEvents();
            registerCommands();
        }
        catch (NoSuchFieldError e)
        {
        	BukkitUtils.severe("NoSuchFieldError", e);
        }
        
		// Load and updates
		new CTUpdater(this).update();

		// set version, get version, and display
		CTInfo.setVersion(this.getDescription().getVersion());
		Version version = CTInfo.FULL_VERSION;
		getConfig().set("plugin.pluginversion", version.toString());
		System.out.println("At version: " + version.toString());

		// Start the engine
		CTEngine.ENABLED = true;
		
		// Re login anyone online. (In case of plugin reloading)
		for (Player player : getServer().getOnlinePlayers())
		{
			try
			{
				getTownUniverse().onLogin(player);
			}
			catch (TownException x)
			{
				BukkitUtils.sendMessage(player, Colors.Red, x.getMessage());
			}
		}
		
		// output to StopWatch
		sw.setLoadNoChirp("onEnable", System.nanoTime() - start, false);
    }
     
	@Override
    public void onDisable()
    {
		// Get the current time for StopWatch
		long start = System.nanoTime();
				
		// Disable the plugin
		CTEngine.ENABLED = false;
		
		// logout anyone online. (In case of plugin reloading)
		for (Player player : getServer().getOnlinePlayers())
		{
			getTownUniverse().onLogout(player);
		}
		
		// unregister hooks
        for (Hook hook : dm.getRegistered())
        {
        	hook.onDisable(this);
        	dm.unregisterHook(hook.getName());
        }
		
		// Save our storage files
		TownUniverse.getDataSource().saveAll();
		
		// Stop Debug
		DebugMode.stop();
		
		// cancel ALL tasks we created
        getServer().getScheduler().cancelTasks(this);
        
        // nullify the universe, releasing all of its memory
        townUniverse = null;
        
        // unregister our events
        HandlerList.unregisterAll(this);
        
        // Finalize the StopWatch then output data
        sw.setLoad("onDisable", System.nanoTime() - start);
        BukkitUtils.info(sw.output());
    }
	
    /**
     * Register all of the events used
     */
    private void registerEvents()
    {
    	// Get the current time for StopWatch
    	long start = System.nanoTime();
    			
        // Shared Objects
        playerListener = new CTPlayerListener(this);
        worldListener = new CTWorldListener(this);
        blockListener = new CTBlockListener(this);
        weatherListener = new CTWeatherListener(this);
        entityListener = new CTEntityListener(this);
        
        // register event listeners
        PluginManager pm = getServer().getPluginManager();
        pm.registerEvents(playerListener, this);
        pm.registerEvents(worldListener, this);
        pm.registerEvents(blockListener, this);
        pm.registerEvents(weatherListener, this);
        pm.registerEvents(entityListener, this);
        
        // debug what we registered
        //TODO: better this
        //for (RegisteredListener listener : HandlerList.getRegisteredListeners(this))
        //	DebugMode.log(listener.toString());
        
        // log to StopWatch
        sw.setLoadNoChirp("registerEvents", (System.nanoTime() - start));
    }
    
    /**
     * Register all of the commands used, point them to executor
     * 
     */
    private void registerCommands()
    {
    	// Get the current time for StopWatch
    	long start = System.nanoTime();
    	
    	// Shared Ojects
    	townCommandExecutor = new TownCommandExecutor();
    	portalCommandExecutor = new PortalCommandExecutor();
    	genCommandExecutor = new GenCommandExecutor();
        
        // point commands to executor        
        getCommand("town").setExecutor(townCommandExecutor);
        getCommand("portal").setExecutor(portalCommandExecutor);
        getCommand("cc").setExecutor(genCommandExecutor);
        
        // debug what we loaded
        //TODO: make this output what we registered
        
        // log to StopWatch
        sw.setLoadNoChirp("registerCommands", (System.nanoTime() - start));
    }

	/**
     * Check for required plugins to be loaded
     */
    private void pluginHooks()
    {
    	// Get the current time for StopWatch
    	long start = System.nanoTime();
    	
    	// Register the dependency
        dm = new DependancyManager();
        PluginManager pm = getServer().getPluginManager();
        if (pm.isPluginEnabled("NoCheatPlus"))
        	dm.registerHook("NoCheatPlus", new NoCheatPlus());
        if (pm.isPluginEnabled("Essentials"))
        	dm.registerHook("Essentials", new Essentials());
        dm.registerHook("WorldGuard", new WorldGuard());
        dm.registerHook("Vault", new Vault());
        dm.registerHook("PreciousStones", new PreciousStones());
        
        // Enable the dependencies
        for (Hook hook : dm.getRegistered())
        	hook.onEnable(this);
        
        // WorldGuardObject
        wg = new WorldGuardObject(this.getWorldGuard());
        
        // RaffleHandler
        rh = new RaffleHandler();
        
        // log to StopWatch
        sw.setLoadNoChirp("pluginHooks", (System.nanoTime() - start));
    }
    
    /**
     *  load the config
     */
    public boolean load()
    {
    	config = new MainConfigObject(this);
    	
    	return true;
    }
    
    /**
     * checks if a player is online
     * 
     * @param playerName (string - use player.getName() if necessary)
     * @return false if player isnt online
     */
	public boolean isOnline(String playerName)
	{
		for (Player player : getServer().getOnlinePlayers())
			if (player.getName().equalsIgnoreCase(playerName))
				return true;

		return false;
	}
    
	/**
	 * @return TownUniverse
	 */
    public TownUniverse getTownUniverse()
    {
    	return townUniverse;
    }
    
    /**
     * @return the worldguardobject
     */
    public WorldGuardObject getWorldGuardObject()
    {
    	return wg;
    }
    
    /**
     * @return the config
     */
    public MainConfigObject getConf()
    {
        return config;
    }
    
    /**
     * @return if we are using permissions
     */
    public boolean isPermissions()
    {
        return Vault.perms != null;
    }
    
    /**
     * @return the current plugin instance
     */
    public static CTPlugin getInstance()
    {
        return instance;
    }
    
    /**
     * @return the engine instance
     */
    public CTEngine getEngine()
    {
    	return engine;
    }

	/**
	 * @return the town command executor
	 */
	public TownCommandExecutor getTownCommandExecutor()
	{
		return townCommandExecutor;
	}
	
	/**
	 * @return the General command executor
	 */
	public GenCommandExecutor getGenCommandExecutor()
	{
		return genCommandExecutor;
	}
	
	/**
	 * @return the StopWatch object
	 */
	public StopWatch getStopWatch()
	{
		return sw;
	}
	
	/**
	 * @return the DependancyManager object
	 */
	public DependancyManager getDependancyManager()
	{
		return dm;
	}
	
	/**
	 * @return the Vault dependancy
	 */
	public Vault getVault()
	{
		try { return (Vault)dm.getHook("Vault").getPlugin(); } catch (NotRegisteredException e) { return null; }
	}
	
	/**
	 * @return the WorldGuard dependancy
	 */
	public WorldGuardPlugin getWorldGuard()
	{
		try { return (WorldGuardPlugin)dm.getHook("WorldGuard").getPlugin(); } catch (NotRegisteredException e) { return null; }
	}
	
	/**
	 * @return the PreciousStones dependancy
	 */
	public net.sacredlabyrinth.Phaed.PreciousStones.PreciousStones getPreciousStones()
	{
		try { return (net.sacredlabyrinth.Phaed.PreciousStones.PreciousStones)dm.getHook("PreciousStones").getPlugin(); } catch (NotRegisteredException e) { return null; }
	}
	
	/**
	 * @return the main command executor
	 */
	public PortalCommandExecutor getPortalCommandExecutor()
	{
		return portalCommandExecutor;
	}
	
	/**
	 * @return the RaffleHandler object
	 */
	public RaffleHandler getRaffleHandler()
	{
		return rh;
	}
	
	/**
	 * @return the Locale
	 */
	public Locale getLocale()
	{
		return locale;
	}
	
	/**
	 * @return the timezone
	 */
	public TimeZone getTimeZone()
	{
		return timezone;
	}
}
