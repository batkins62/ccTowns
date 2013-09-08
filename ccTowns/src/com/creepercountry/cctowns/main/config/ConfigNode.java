package com.creepercountry.cctowns.main.config;

public enum ConfigNode
{
	PLUGIN_VERSION ("version.plugin"),
	DEFAULT_PORTAL_WORLD ("default-portal-world"),
	PUNISH_FLY_WORLD ("fly-punish-location-world"),
	CONFIG_VERSION ("version.config"),
	PUNISH_FLY ("punish-fly"),
	CONFIG_TOKEN ("config-token"),
	RESIDENT_INACTIVE ("user-inactive"),
	SAVE_CONFIG ("save-config"),
	DECREMENT_FLY_VIOLATIONS ("decrement-fly-violations"),
	BLACKLIST_VIOLATION ("watched-players-blacklist"),
	PSTONE_NAG_TRIGGER_WORDS ("nag-pstone-offender"),
	FLY_PUNISH_LOCATION ("fly-punish-location"),
	DEFAULT_PORTAL ("default-portal"),
	PLUGIN_RUNNING ("plugin-running"),
	DEBUG ("debug"),
	FIRST_RUN ("first-run"),
	VAULT_DEPEND ("dependencies.required.vault"),
	WORLDGUARD_DEPEND ("dependencies.required.worldguard"),
	NOCHEATPLUS_DEPEND ("dependencies.optional.nocheatplus"),
	ESSENTIALS_DEPEND ("dependencies.optional.essentials"),
	FLY_VIOLATION_CHECKS ("fly-violation-checks"),
	PLOT_COST ("plot.cost"),
	TOWN_NOTIFICATIONS ("town-notifications"),
	RAFFLE_PRICE ("raffle-price");
	
	private final String path;
	ConfigNode(String path)
	{
		this.path = path;
	}
	
	public String getPath()
	{
		return path;
	}
}
