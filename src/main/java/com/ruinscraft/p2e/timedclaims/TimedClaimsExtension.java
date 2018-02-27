package com.ruinscraft.p2e.timedclaims;

import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;
import org.bukkit.scheduler.BukkitRunnable;

import com.ruinscraft.p2e.P2Extension;
import com.ruinscraft.p2e.P2Extensions;
import com.ruinscraft.p2e.P2Util;

public class TimedClaimsExtension implements P2Extension {
	
	private static TimedClaimsExtension timedClaimsExtension;
	private static P2Extensions instance;
	private static DataHandler dataHandler;
	private static MenuHandler menuHandler;

	@Override
	public boolean enable() {
		
		P2Util.log("Plugin enabled (version: " + instance.getDescription().getVersion() + ") by ImABradley.");
		timedClaimsExtension = this;

		P2Util.loadResource("config.yml");
		P2Util.loadResource("messages.yml");

		dataHandler = new DataHandler(instance);
		menuHandler = new MenuHandler(instance);

		instance.getCommand("nextclaim").setExecutor(new RewardsCmd());

		PluginManager pm = Bukkit.getPluginManager();
		pm.registerEvents(new PlayerJoinListener(), instance);
		
		new BukkitRunnable() {
			
			public void run() {
				
				menuHandler.updateUsers();
				
			}
			
		}.runTaskTimerAsynchronously(instance, 0L, 1200L);
		
		return true;
		
	}

	@Override
	public boolean disable() {
		
		P2Util.log("Plugin disabled (version: " + instance.getDescription().getVersion() + ") by ImABradley.");
		
		timedClaimsExtension = null;
		
		return true;
		
	}

	public static void reloadPlugin() {
		
		P2Util.log("Reloading plugin..");

		menuHandler = new MenuHandler(instance);
		yamlHandler.reload();

		P2Util.log("Plugin successfully reloaded.");
		
	}

	public static TimedClaimsExtension getTimedClaims() { return timedClaimsExtension; }

	public static MenuHandler getMenuHandler() { return menuHandler; }

	public static YamlHandler getYamlHandler() { return yamlHandler; }
	
}