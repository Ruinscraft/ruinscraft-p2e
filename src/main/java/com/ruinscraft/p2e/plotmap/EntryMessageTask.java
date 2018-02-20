package com.ruinscraft.p2e.plotmap;

import org.bukkit.entity.Player;

public class EntryMessageTask implements Runnable {

	private final Player enterer;
	public boolean removeMap = false;
	private static PlotMapExtension plotMap = PlotMapExtension.getPlotMap();

	public EntryMessageTask(Player enterer) {
		this.enterer = enterer;
	}

	@Override
	public void run() {

		// defines if the user did not have the map enabled before
		if ((!plotMap.getUserMap().containsKey(enterer.getName())) 
				|| plotMap.getUserMap().get(enterer.getName()) == false) {
			PlotMapExtension.radius = 3;
			removeMap = true;
			plotMap.getUserMap().put(enterer.getName(), true);
			
		}
		
		// defines if EntryMessageTask is being run, and updates the map
		plotMap.taskMap.put(enterer.getName(), true);
		plotMap.updateMap(enterer);

		// sleep for 4 1/2 seconds
		try {
			Thread.sleep(4500);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		// defines EntryMessageTask as ended
		plotMap.getTaskMap().put(enterer.getName(), false);
		
		// if the user didn't have the map enabled before, remove it
		if (removeMap == true) {
			
			plotMap.getUserMap().put(enterer.getName(), false);
			
		}
		
		// update map
		plotMap.updateMap(enterer);

	}

}
