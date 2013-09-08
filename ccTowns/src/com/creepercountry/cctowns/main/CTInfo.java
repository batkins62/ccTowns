package com.creepercountry.cctowns.main;

import com.creepercountry.cctowns.util.Version;



public class CTInfo
{
	/**
	 * ccTowns Full Version
	 */
    public static Version FULL_VERSION;

    /**
     * set plugin version
     * 
     * @param version
     */
    public static void setVersion(String version)
    {
        String implementationVersion = CTPlugin.class.getPackage().getImplementationVersion();
        FULL_VERSION = new Version(version + " " + implementationVersion);
    }
}
