package com.ruinscraft.p2e.timedclaims;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import com.ruinscraft.p2e.P2Extensions;
import com.ruinscraft.p2e.P2Util;

import java.io.File;
import java.io.IOException;

public class YamlHandler {
	
	private static P2Extensions instance = P2Extensions.getInstance();
	private FileConfiguration config;
	private String pdfile;

	public YamlHandler(Plugin plugin) {
		
		this.config = plugin.getConfig();
		this.pdfile = plugin.getDataFolder() + File.separator + "playerdata";
		
	}

	public void reload() {
		
		instance.reloadConfig();
		this.config = instance.getConfig();
		
	}

	public YamlConfiguration getPlayerYaml(Player player) {
		
		File file = new File(pdfile, player.getUniqueId().toString() + ".yml");
		YamlConfiguration pconfig = YamlConfiguration.loadConfiguration(file);
		String path = "menus.rewards.reward-items";

		if (!file.exists()) {
			
			pconfig.set("name", player.getName());
			pconfig.set("uuid", player.getUniqueId().toString());

			for (String s : config.getConfigurationSection(path).getKeys(false)) {
				
				pconfig.set("rewards." + s + ".time-online", 0); 
				pconfig.set("rewards." + s + ".plots-given", 0);
				
				if (config.getBoolean("first-time-claim")) {
					pconfig.set("rewards." + s + ".claim-time", 0); 	
				} else {
					pconfig.set("rewards." + s + ".claim-time", System.currentTimeMillis());
				}
				
			}

			try {
				pconfig.save(file);
			} catch (IOException e) {
				P2Util.log("[Error] An IOException occurred when saving " + P2Util.getNameEnding(player.getName()) + " config " + "file:");
				e.printStackTrace();
			}
			
		}

		return pconfig;
		
	}

	public void savePlayerYaml(Player player, YamlConfiguration pconfig) {
		
		File file = new File(pdfile, player.getUniqueId() + ".yml");

		if (pconfig == null) pconfig = getPlayerYaml(player);

		try {
			pconfig.save(file);
		} catch (IOException e) {
			P2Util.log("An error occurred when saving " + P2Util.getNameEnding(player.getName()) + " config " + "file:");
			e.printStackTrace();
		}
		
	}

	public FileConfiguration getConfig() { return config; }
	
}