package com.ruinscraft.p2e.timedclaims;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerJoinListener implements Listener {

	@EventHandler(priority = EventPriority.NORMAL)
	public void onJoin(PlayerJoinEvent event) {
		
		Player player = event.getPlayer();
		
		TimedClaimsExtension.getYamlHandler().getPlayerYaml(player);
		TimedClaimsExtension.getMenuHandler().setLoginTime(player.getName(), System.currentTimeMillis());
		
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onLeave(PlayerQuitEvent event) {
		
		Player player = event.getPlayer();
		
		FileConfiguration config = TimedClaimsExtension.getYamlHandler().getConfig();
		YamlConfiguration pconfig = TimedClaimsExtension.getYamlHandler().getPlayerYaml(player);
		
		long time = TimedClaimsExtension.getMenuHandler().getLoginTime().get(player.getName());
		long online = System.currentTimeMillis() - time;
		
		for (String s : config.getConfigurationSection("menus.rewards.reward-items").getKeys(false)) {
			
			int timealready = (int) pconfig.get("rewards." + s + ".time-online");
			long newtime = timealready + online;
			pconfig.set("rewards." + s + ".time-online", newtime);
			
		}
		
		TimedClaimsExtension.getYamlHandler().savePlayerYaml(player, pconfig);
		
	}
}