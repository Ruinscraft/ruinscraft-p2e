package com.ruinscraft.p2e.timedclaims;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import com.google.common.base.Optional;
import com.intellectualcrafters.plot.object.PlotPlayer;
import com.ruinscraft.p2e.P2Extensions;
import com.ruinscraft.p2e.P2Util;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.Arrays;

public class DataHandler {
	
	private static P2Extensions instance = P2Extensions.getInstance();
	private FileConfiguration config;
	private String pdfile;

	public DataHandler(Plugin plugin) {
		
		this.config = plugin.getConfig();
		this.pdfile = plugin.getDataFolder() + File.separator + "playerdata";
		
	}

	public void reload() {
		
		instance.reloadConfig();
		this.config = instance.getConfig();
		
	}
	
	// all a mess rn

	public YamlConfiguration getPlayerYaml(PlotPlayer player) {
		
		if (player.getMeta("claimTime") == null) {

			player.setPersistentMeta("time-online", new byte[0]);
			player.setPersistentMeta("plots-given", new byte[0]);
			
			if (config.getBoolean("first-time-claim")) {
				player.setPersistentMeta("claim-time", new byte[0]);
			} else {
				player.setPersistentMeta("claim-time", new byte[]); // System.currentTimeMillis()
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