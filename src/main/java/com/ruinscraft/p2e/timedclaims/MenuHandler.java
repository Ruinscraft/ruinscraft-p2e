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

	public MenuHandler(Plugin plugin) {
		Bukkit.getPluginManager().registerEvents(this, plugin);
    }

	public static void addRewardMenu(PlotPlayer player, RewardsMenu menu) { 
		rewardsMenus.put(player.getUUID(), menu); 
	}

	public void openRewardsMenu(PlotPlayer player) { 
		P2Util.getPlayer(player).openInventory(this.getRewardsMenu(player).getInventory());
	}

	public RewardsMenu getRewardsMenu(PlotPlayer player) {
		
		RewardsMenu rewardsMenu = rewardsMenus.get(player.getUUID());

		if (rewardsMenu != null) {
			rewardsMenu.update(player);
			return rewardsMenu;
		}

		return new RewardsMenu(player);
		
	}
	
	public long getTime(PlotPlayer player) {
		
		long logintime = player.getMeta(TimeMeta.CLAIM_TIME);
		long time = System.currentTimeMillis() - logintime;
		
		long claimedsofar = player.getMeta(TimeMeta.TIME_ONLINE);
		long delay = player.getMeta(TimeMeta.PLOTS_GIVEN);
		
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
				
				int plots = player.getMeta(TimeMeta.PLOTS_GIVEN);
				int newplots = plots + 1;
				player.setMeta(TimeMeta.PLOTS_GIVEN, newplots);
				player.setMeta(TimeMeta.TIME_ONLINE, 0);
			
				for (String cmd : config.getStringList(TimedClaimsExtension.getTimedClaims().getName() 
															+ ".menus.reward-items.claim-reward-cmds")) {
					Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), cmd.replace("{player}", player
								.getName()).replace("{uuid}", player.getUUID().toString()));
				}
				
				
				player.sendMessage("{prefix} You have earned 1 new claim for your playtime! &l/p auto &r&7or &l/p claim"
						.replace("{prefix}", P2Util.prefix));
				
				player.setMeta(TimeMeta.CLAIM_TIME, (int) (System.currentTimeMillis() % Integer.MAX_VALUE));
				
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
		
		final PlotPlayer player = P2Util.getPlayer((Player) event.getPlayer());
		Inventory inventory = event.getInventory();

		if (inventory.getName().equals(this.getRewardsMenu(player).getInventory().getName())) {
			
			final RewardsMenu rewardsMenu = this.getRewardsMenu(player);
			runningTasks.add(player.getUUID());
			final P2Extensions instance = P2Extensions.getInstance();

			new BukkitRunnable() {
				
				public void run() {
					if (player.isOnline() && runningTasks.contains(player.getUUID())) {
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