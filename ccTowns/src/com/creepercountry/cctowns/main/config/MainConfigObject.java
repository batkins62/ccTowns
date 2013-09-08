package com.creepercountry.cctowns.main.config;

import java.util.List;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.util.Vector;

import com.creepercountry.cctowns.main.CTPlugin;

public final class MainConfigObject
{
	public static String plugin_version, default_portal_world, fly_punish_world;
	public static int config_version, punish_fly;
	public static long config_token, user_inactive, save_config, decrement_fly_violations;
	public static List<String> blacklist_commands, pstone_nag_words, town_notifications;
	public static Vector fly_punish_location, default_portal;
	public static boolean plugin_running, debug, first_run, vault_depend, worldguard_depend,
					nocheatplus_depend, essentials_depend, fly_violation_checks;
	public static double plotcost, raffle_price;
	
    private final CTPlugin plugin;
    public boolean ENABLED = false;
    
    public MainConfigObject(CTPlugin instance)
    {
    	this.plugin = instance;
    	ENABLED = true;
    	load();
    }
    
    public boolean load()
    {
    	// Grab config, copy just the defaults
    	FileConfiguration config = plugin.getConfig();
        config.options().copyDefaults(true);
        
        // Load String variables
        plugin_version = config.getString("version.plugin", "1.1.0");
        default_portal_world = config.getString("default-portal-world", "world");
        fly_punish_world = config.getString("fly-punish-location-world", "world");
        // Load int variables
        config_version = config.getInt("version.config", 1);
        punish_fly = config.getInt("punish-fly", 5);
    	// Load long variables
        config_token = config.getLong("config-token", 0);
        user_inactive = config.getLong("user-inactive", 45);
        save_config = config.getLong("save-config", 3600);
        decrement_fly_violations = config.getLong("decrement-fly-violations", 3600);
        // Load List<String> variables
        blacklist_commands = config.getStringList("watched-players-blacklist");
        pstone_nag_words = config.getStringList("nag-pstone-offender");
        town_notifications = config.getStringList("town-notification");
        // Load Vector variables
        fly_punish_location = config.getVector("fly-punish-location", new Vector(0, 0, 0));
        default_portal = config.getVector("default-portal", new Vector(0, 0, 0));
     	// Load boolean variables
        plugin_running = config.getBoolean("plugin-running", false);
        debug = config.getBoolean("debug", false);
        first_run = config.getBoolean("first-run", true);
        vault_depend = config.getBoolean("dependencies.required.vault", false);
        worldguard_depend = config.getBoolean("dependencies.required.worldguard", false);
        nocheatplus_depend = config.getBoolean("dependencies.optional.nocheatplus", false);
        essentials_depend = config.getBoolean("dependencies.optional.essentials", false);
        fly_violation_checks = config.getBoolean("fly-violation-checks", true);
        // Load double variables
        plotcost = config.getDouble("plots.cost", 100.00);
        raffle_price = config.getDouble(ConfigNode.RAFFLE_PRICE.getPath(), 250.00);
        
    	return true;
    }
}
