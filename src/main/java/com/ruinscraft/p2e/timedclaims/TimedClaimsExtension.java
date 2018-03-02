package com.ruinscraft.p2e.timedclaims;

import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;
import org.bukkit.scheduler.BukkitRunnable;

import com.ruinscraft.p2e.P2Extension;
import com.ruinscraft.p2e.P2Extensions;

public class TimedClaimsExtension implements P2Extension {
	
	private static TimedClaimsExtension timedClaimsExtension;
	private static P2Extensions instance;
	private static MenuHandler menuHandler;
	
	private NextCommand command;

	@Override
	public boolean enable() {
		
		instance = P2Extensions.getInstance();
		timedClaimsExtension = this;
		menuHandler = new MenuHandler();

		command = new NextCommand();

		PluginManager pm = Bukkit.getPluginManager();
		pm.registerEvents(menuHandler, instance);
		pm.registerEvents(new PlayerJoinListener(), instance);
		
		new BukkitRunnable() {
			
			public void run() {
				
				menuHandler.updateUsers();
				
			}
			
		}.runTaskTimer(instance, 0L, 1200L);
		
		return true;
		
	}

	@Override
	public boolean disable() {
		
		timedClaimsExtension = null;
		
		return true;
		
	}
	
	@Override
	public String getName() {
		return "Timed-Claims";
	}
	
	@Override
	public NextCommand getP2SubCommand() {
		return command;
	}

	public static TimedClaimsExtension getTimedClaims() { 
		return timedClaimsExtension;
	}

	public static MenuHandler getMenuHandler() { 
		return menuHandler;
	}
	
}