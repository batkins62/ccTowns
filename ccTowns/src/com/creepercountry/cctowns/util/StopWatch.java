package com.creepercountry.cctowns.util;

import java.util.HashMap;

public class StopWatch
{
	/**
	 * Identifier of this stop watch.
	 * Handy when we have output from multiple stop watches
	 * and need to distinguish between them in log or console output.
	 */
	private final String id;
	
	/**
	 * The average amount of time this plugin is taking for its tasks
	 */
	private long averageload;
	
	/**
	 * The Highest load time peak.
	 */
	private long highestload;
	
	/**
	 * The following is a map that holds the task(Key) with the loadtime(value)
	 */
	private HashMap<String, Long> loads = new HashMap<String, Long>();
	
	/**
	 * Construct a new stop watch with the given id.
	 * Does not start any task.
	 *
	 * @param id identifier for this stop watch.
	 * Handy when we have output from multiple stop watches
	 * and need to distinguish between them.
	 */
	public StopWatch(String id)
	{
		this.id = id;
	}
	
	/**
	 * Creates a new key and value for loads.
	 */
	public boolean newLoad(String task, long value)
	{
		if (loads.containsKey(task))
			return false;
		
		loads.put(task, value);
		setAverage(value);
		if (highestload < value)
		{
			this.highestload = value;
			DebugMode.log("[WARNING] [StopWatch] [Peak] " + task + " -> cur." + loads.get(task).toString() + "/avg." + averageload);
			return true;
		}
		DebugMode.log("[INFO] [StopWatch] [Chirp] " + task + " -> cur." + loads.get(task).toString() + "/avg." + averageload);
		return true;
	}
	
	/**
	 * set the average
	 */
	public void setAverage(long time)
	{
		this.averageload = ((time + averageload) / 2);
	}
	
	public void setLoadNoChirp(String task, long time, boolean setaverage)
	{
		if (!loads.containsKey(task))
			if (newLoad(task, time))
				return;
		
		loads.put(task, ((loads.get(task) + time) / 2));
		if (setaverage)
			setAverage(time);
		
		if (highestload < time)
		{
			this.highestload = time;
			DebugMode.log("[WARNING] [StopWatch] [Peak] " + task + " -> cur." + loads.get(task).toString() + "/avg." + averageload);
			return;
		}
	}
	
	/**
	 * Log a load time to a task. if task is not found, it will be created as a key in the map. WILL NOT DISPLAY CHIRPS
	 */
	public void setLoadNoChirp(String task, long time)
	{
		if (!loads.containsKey(task))
			if (newLoad(task, time))
				return;
		
		loads.put(task, ((loads.get(task) + time) / 2));
		setAverage(time);
		
		if (highestload < time)
		{
			this.highestload = time;
			DebugMode.log("[WARNING] [StopWatch] [Peak] " + task + " -> cur." + loads.get(task).toString() + "/avg." + averageload);
			return;
		}
	}
	
	/**
	 * Log a load time to a task. if task is not found, it will be created as a key in the map.
	 */
	public void setLoad(String task, long time)
	{
		if (!loads.containsKey(task))
			if (newLoad(task, time))
				return;
		
		loads.put(task, Long.valueOf((loads.get(task) + time) / 2));
		setAverage(time);
		
		if (highestload < time)
		{
			this.highestload = time;
			DebugMode.log("[WARNING] [StopWatch] [Peak] " + task + " -> cur." + loads.get(task).toString() + "/avg." + averageload);
			return;
		}
		
		DebugMode.log("[INFO] [StopWatch] [Chirp] " + task + " -> cur." + loads.get(task).toString() + "/avg." + averageload);
	}
	
	/**
	 * get the load time of just one task, not them all.
	 * 
	 * @param task
	 * @return
	 */
	public String getLoad(String task)
	{
		if (!loads.containsKey(task))
			return "Could not find task " + task;
		
		return "cur." + loads.get(task).toString().concat("/avg." + Long.toString(averageload));
	}
	
	/**
	 * Output the current data collected.
	 * 
	 * @return the output
	 */
	public String output()
	{
		// create the object
		StringBuilder sb = new StringBuilder();
		
		// make the string
		sb.append(String.format("Outputing StopWatch Latency Stats for %s", id) + "\n");
		sb.append("===============================\n");
		sb.append("Current latency stats per load:\n");
		if (loads.isEmpty())
			sb.append("-> No Loads Found.\n");
		for (String key : this.loads.keySet())
			sb.append(String.format("-> " + key + ": %s", loads.get(key)) + "\n");
		sb.append("===============================\n");
		sb.append("Current Global Average: " + averageload + "\n");
		sb.append("highest Peak Value: " + highestload + "\n");
		
		// output as string
		return sb.toString();
	}
}
