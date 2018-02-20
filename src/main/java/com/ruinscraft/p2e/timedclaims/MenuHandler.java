package com.ruinscraft.p2e.timedclaims;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import com.ruinscraft.p2e.P2Extensions;
import com.ruinscraft.p2e.P2Util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class MenuHandler implements Listener {
	
	private static HashMap<UUID, RewardsMenu> rewardsMenus = new HashMap<UUID, RewardsMenu>();
	private static ArrayList<UUID> runningTasks = new ArrayList<UUID>();
	private HashMap<String, Long> loginTime = new HashMap<String, Long>();

	public MenuHandler(Plugin plugin) {
		Bukkit.getPluginManager().registerEvents(this, plugin);
    }
	
	public HashMap<String, Long> getLoginTime() { return loginTime; }
	
	public void setLoginTime(String player, Long time) {
		loginTime.put(player, time);
	}

	public static void addRewardMenu(Player player, RewardsMenu menu) { rewardsMenus.put(player.getUniqueId(), menu); }

	public void openRewardsMenu(Player player) { player.openInventory(this.getRewardsMenu(player).getInventory()); }

	public RewardsMenu getRewardsMenu(Player player) {
		
		RewardsMenu rewardsMenu = rewardsMenus.get(player.getUniqueId());

		if (rewardsMenu != null) {
			rewardsMenu.update(player);
			return rewardsMenu;
		}

		return new RewardsMenu(player);
		
	}
	
	public long getTime(Player player) {
		
		String playerName = player.getName();
		long logintime = loginTime.get(playerName);
		long time = System.currentTimeMillis() - logintime;
		
		YamlConfiguration pconfig = TimedClaimsExtension.getYamlHandler().getPlayerYaml(player);
		FileConfiguration config = TimedClaimsExtension.getYamlHandler().getConfig();
		String shortPath = "menus.rewards.reward-items";
		
		long claimedsofar = 0;
		long delay = 0;
		
		for (String s : config.getConfigurationSection(shortPath).getKeys(false)) {
			
			claimedsofar = pconfig.getLong("rewards." + s + ".time-online");
			delay = pconfig.getInt("rewards." + s + ".plots-given");
			
		}
		
		long result = (1180000L + (delay * 1200000L)) - (claimedsofar + time);
		
		if (player.hasPermission("timedrewards.delay.fifteen")) {
			result = (1180000L + (delay * 900000L)) - (claimedsofar + time);
		}
		if (player.hasPermission("timedrewards.delay.ten")) {
			result = (1180000L + (delay * 600000L)) - (claimedsofar + time);
		}
		if (player.hasPermission("timedrewards.delay.five")) {
			result = (1180000L + (delay * 300000L)) - (claimedsofar + time);
		}
		
		if (result <= 0) {
			return 0;
		}

		return result;
	}
	
	public String getFormattedTime(Player player) {
		
		long result = getTime(player);

		if (result <= 0) {
			return ("Processing...");
		} else 
			return TimeUnit.MILLISECONDS.toDays(result) + "d " + String.format("%02d:%02d:%02d", TimeUnit.MILLISECONDS.toHours(result) - TimeUnit.DAYS
					.toHours(TimeUnit.MILLISECONDS.toDays(result)), TimeUnit.MILLISECONDS.toMinutes(result) - TimeUnit.HOURS
					.toMinutes(TimeUnit.MILLISECONDS.toHours(result)), TimeUnit.MILLISECONDS.toSeconds(result) - TimeUnit.MINUTES
					.toSeconds(TimeUnit.MILLISECONDS.toMinutes(result)));
	}
	
	public void updateUsers() {
		
		int i = 0;
		
		for (Player player : Bukkit.getOnlinePlayers()) {
			
			if (getTime(player) == 0) {
				
				i++;
				
				FileConfiguration config = TimedClaimsExtension.getYamlHandler().getConfig();
				YamlConfiguration pconfig = TimedClaimsExtension.getYamlHandler().getPlayerYaml(player);
				
				String shortPath = "menus.rewards.reward-items";
				
				for (String s : config.getConfigurationSection(shortPath).getKeys(false)) {
						
					String path = shortPath + "." + s;
						
					int plots = pconfig.getInt("rewards." + s + ".plots-given");
					int newplots = plots + 1;
					pconfig.set("rewards." + s + ".plots-given", newplots);
					
					pconfig.set("rewards." + s + ".time-online", 0);
			
					for (String cmd : config.getStringList(path + ".claim-reward-cmds")) {
						Bukkit.getServer()
							.dispatchCommand(Bukkit.getConsoleSender(), cmd.replace("{player}", player
									.getName()).replace("{uuid}", player.getUniqueId().toString()));
					}
			
				}
				
				
				player.sendMessage("{prefix} You have earned 1 new claim for your playtime! &l/p auto &r&7or &l/p claim"
						.replace("{prefix}", P2Util.prefix));
				
				loginTime.put(player.getName(), System.currentTimeMillis());
				
				TimedClaimsExtension.getYamlHandler().savePlayerYaml(player, pconfig);
				
			}
			
		}
		
		if (i > 0) {
			
			if (i == 1) {
				P2Util.log("Successfully updated all plot counts! " + Integer.toString(i) + " user given rewards");	
			} else {
				P2Util.log("Successfully updated all plot counts! " + Integer.toString(i) + " users given rewards");	
			}
			
		}
		
	}

	@EventHandler 
	public void onOpen(InventoryOpenEvent event) {
		
		final Player player = (Player) event.getPlayer();
		Inventory inventory = event.getInventory();

		if (inventory.getName().equals(this.getRewardsMenu(player).getInventory().getName()))
		{
			final RewardsMenu rewardsMenu = this.getRewardsMenu(player);
			runningTasks.add(player.getUniqueId());
			final P2Extensions instance = P2Extensions.getInstance();

			new BukkitRunnable()
			{
				public void run()
				{
					if (player.isOnline() && runningTasks.contains(player.getUniqueId()))
					{
						rewardsMenu.update(player);
					}
					else cancel();
				}
			}.runTaskTimerAsynchronously(instance, 0L, 20L);
		}
	}

	@EventHandler 
	public void onClose(InventoryCloseEvent event) {
		
		Player player = (Player) event.getPlayer();

		if (runningTasks.contains(player.getUniqueId()))
		{
			runningTasks.remove(player.getUniqueId());
		}
	}
}