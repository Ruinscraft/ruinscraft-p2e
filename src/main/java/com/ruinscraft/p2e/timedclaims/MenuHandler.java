package com.ruinscraft.p2e.timedclaims;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import com.intellectualcrafters.plot.object.PlotPlayer;
import com.intellectualcrafters.plot.util.ByteArrayUtilities;
import com.ruinscraft.p2e.P2Extensions;
import com.ruinscraft.p2e.P2Util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class MenuHandler implements Listener {
	
	private static P2Extensions instance = P2Extensions.getInstance();
	
	private static HashMap<UUID, RewardsMenu> rewardsMenus = new HashMap<UUID, RewardsMenu>();
	private static ArrayList<UUID> runningTasks = new ArrayList<UUID>();
	private HashMap<String, Long> loginTime = new HashMap<String, Long>();

	public MenuHandler(Plugin plugin) {
		Bukkit.getPluginManager().registerEvents(this, plugin);
    }
	
	public HashMap<String, Long> getLoginTime() { 
		return loginTime; 
	}
	
	public void setLoginTime(String player, Long time) {
		loginTime.put(player, time);
	}

	public static void addRewardMenu(PlotPlayer player, RewardsMenu menu) { 
		rewardsMenus.put(player.getUUID(), menu); 
	}

	public void openRewardsMenu(PlotPlayer player) { 
		P2Util.getPlayer(player).openInventory(this.getRewardsMenu(player).getInventory());
	}

	public RewardsMenu getRewardsMenu(PlotPlayer player) {
		
		RewardsMenu rewardsMenu = rewardsMenus.get(player.getUniqueId());

		if (rewardsMenu != null) {
			rewardsMenu.update(player);
			return rewardsMenu;
		}

		return new RewardsMenu(player);
		
	}
	
	public long getTime(PlotPlayer player) {
		
		String playerName = player.getName();
		long logintime = loginTime.get(playerName);
		long time = System.currentTimeMillis() - logintime;
		
		long claimedsofar = ByteArrayUtilities.bytesToInteger(player.getPersistentMeta("time-online"));
		long delay = ByteArrayUtilities.bytesToInteger(player.getPersistentMeta("plots-given"));
		
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
	
	public String getFormattedTime(PlotPlayer player) {
		
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
		
		for (Player bukkitPlayer : Bukkit.getOnlinePlayers()) {
			
			PlotPlayer player = P2Util.getPlayer(bukkitPlayer);
			
			if (getTime(player) == 0) {
				
				i++;
				
				FileConfiguration config = instance.getConfig();
				
				String shortPath = "menus.rewards.reward-items";
				
				for (String s : config.getConfigurationSection(shortPath).getKeys(false)) {
						
					String path = shortPath + "." + s;
						
					int plots = DataHandler.getPlotsGiven(player);
					int newplots = plots + 1;
					DataHandler.setPlotsGiven(player, newplots);
					DataHandler.setTimeOnline(player, 0);
			
					for (String cmd : config.getStringList(path + ".claim-reward-cmds")) {
						Bukkit.getServer()
							.dispatchCommand(Bukkit.getConsoleSender(), cmd.replace("{player}", player
									.getName()).replace("{uuid}", player.getUUID().toString()));
					}
			
				}
				
				
				player.sendMessage("{prefix} You have earned 1 new claim for your playtime! &l/p auto &r&7or &l/p claim"
						.replace("{prefix}", P2Util.prefix));
				
				loginTime.put(player.getName(), System.currentTimeMillis());
				
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

		if (inventory.getName().equals(this.getRewardsMenu(player).getInventory().getName())) {
			
			final RewardsMenu rewardsMenu = this.getRewardsMenu(player);
			runningTasks.add(player.getUniqueId());
			final P2Extensions instance = P2Extensions.getInstance();

			new BukkitRunnable() {
				
				public void run() {
					if (player.isOnline() && runningTasks.contains(player.getUniqueId())) {
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

		if (runningTasks.contains(player.getUniqueId())) {
			runningTasks.remove(player.getUniqueId());
		}
		
	}
}