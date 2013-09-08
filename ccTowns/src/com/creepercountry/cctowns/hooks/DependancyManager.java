package com.creepercountry.cctowns.hooks;

import java.util.Collection;
import java.util.HashMap;

import com.creepercountry.cctowns.util.NotRegisteredException;

public class DependancyManager
{
	private final HashMap<String, Hook> hooks = new HashMap<String, Hook>();
	
	public DependancyManager()
	{
		this.hooks.clear();
	}
	
	/**
	 * Register an initialized hook object.
	 *
	 * @param name
	 * Name of the hook
	 * @param hook
	 * Hook object
	 */
	public void registerHook(String name, Hook hook)
	{
		this.hooks.put(name, hook);
	}

	/**
	 * unregister a hook
	 *
	 * @param name
	 * Hook name to unregister
	 * @return the unregistered hook or null if no hook by the given name was registered
	 */
	public Hook unregisterHook(String name)
	{
		final Hook ret = this.hooks.get(name);
		this.hooks.remove(name);
		return ret;
	}
	
	/**
	 * gets a registered hook by name
	 * 
	 * @param name
	 * @return hook
	 * @throws NotRegisteredException if not registered
	 */
	public Hook getHook(String name) throws NotRegisteredException
	{
		if (hooks.containsKey(name))
			return hooks.get(name);
		else
			throw new NotRegisteredException("Hook not registered: " + name);
	}
	
	/**
	 * Get registered hooks!
	 * @return 
	 */
	public Collection<Hook> getRegistered()
	{
		return this.hooks.values();
	}
}
