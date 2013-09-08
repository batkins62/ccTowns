package com.creepercountry.cctowns.util;

import java.util.logging.Logger;

public class DebugMode
{
    private final Logger logger;
    public static DebugMode instance = null;
    private static boolean DEVMODE = false;
    
    /**
     * Constructor
     */
    public DebugMode()
    {
        this.logger = Logger.getLogger("ccTowns");
    }

    public static void go()
    {
    	//TODO: Throws null error
        //BukkitUtils.warning("Debug enabled. Disable via /towns debug off");
        DebugMode.instance = new DebugMode();
    }

    public static void log(String message)
    {
        if (DebugMode.instance != null)
        {
        	DebugMode.instance.logger.info("[CT DEBUG] " + message);
        }
    }

    public static void stop()
    {
    	BukkitUtils.info("Debug disabled. Enable via /towns debug on");
    	DebugMode.instance = null;
    }
    
    public static void setDEVMODE(boolean active)
    {
    	DEVMODE = active;
    }
    
    public static boolean getDEVMODE()
    {
    	return DEVMODE;
    }
}