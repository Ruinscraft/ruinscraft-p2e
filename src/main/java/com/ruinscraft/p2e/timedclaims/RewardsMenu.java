package com.ruinscraft.p2e.timedclaims;

import com.intellectualcrafters.plot.PS;
import com.intellectualcrafters.plot.object.PlotArea;
import com.intellectualcrafters.plot.object.PlotPlayer;
import com.ruinscraft.p2e.P2Util;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;
import java.util.concurrent.TimeUnit;

public class RewardsMenu {
	
	private Inventory inventory;
	private int size;
	private String title;

	private HashMap<String, ItemStack> rewardItems = new HashMap<String, ItemStack>();

	@SuppressWarnings("deprecation")
	public RewardsMenu(Player player) {
		
		FileConfiguration config = TimedClaimsExtension.getYamlHandler().getConfig();
		String path = "menus.rewards.reward-items";

		this.size = config.getInt("menus.rewards.size");
		this.title = P2Util.colour(config.getString("menus.rewards.title"));
		this.inventory = Bukkit.createInventory(null, size, title);
		
		try {
			
			for (String s : config.getConfigurationSection(path).getKeys(false)) {
				
				String ipath = path + "." + s;
				String fullId = config.getString(ipath + ".id");
				int amount = config.getInt(ipath + ".amount");

				ItemStack itemStack;

				if (fullId.contains(":")) {
					String[] parts = fullId.split(":");
					itemStack = new ItemStack(Integer.parseInt(parts[0]), amount, (short) Integer.parseInt(parts[1]));
				} else {
					itemStack = new ItemStack(Integer.parseInt(fullId), amount);
				}

				ItemMeta itemMeta = itemStack.getItemMeta();
				itemMeta.setDisplayName(P2Util.colour(config.getString(ipath + ".name")));

				ArrayList<String> lore = new ArrayList<String>();

				for (String line : config.getStringList(ipath + ".lore")) {
					
					String time = TimedClaimsExtension.getMenuHandler().getFormattedTime(player);
					long result = TimedClaimsExtension.getMenuHandler().getTime(player);

					lore.add(P2Util.colour(line.replace("{time}", time)
							.replace("{days}", Long.toString(TimeUnit.MILLISECONDS.toDays(result)))
							.replace("{hours}", Long.toString(TimeUnit.MILLISECONDS.toHours(result) - TimeUnit.DAYS.toHours(TimeUnit.MILLISECONDS
									.toDays(result))))
							.replace("{minutes}", Long.toString(TimeUnit.MILLISECONDS.toMinutes(result) - TimeUnit.HOURS
									.toMinutes(TimeUnit.MILLISECONDS.toHours(result))))
							.replace("{seconds}", Long.toString(TimeUnit.MILLISECONDS.toSeconds(result) - TimeUnit.MINUTES
									.toSeconds(TimeUnit.MILLISECONDS.toMinutes(result))))));
				}

				itemMeta.setLore(lore);
				itemStack.setItemMeta(itemMeta);
				inventory.setItem(config.getInt(ipath + ".slot"), itemStack);

				rewardItems.put(s, itemStack);
			}

			int slot = 0;

			for (ItemStack itemStack : inventory) {
				
				if (itemStack == null) {
					
					String fullId = config.getString("menus.rewards.other-items");

					if (fullId.contains(":")) {
						String[] parts = fullId.split(":");
						itemStack = new ItemStack(Integer.parseInt(parts[0]), 1, (short) Integer.parseInt(parts[1]));
					} else {
						itemStack = new ItemStack(Integer.parseInt(fullId), 1);
					}

					if (itemStack.getItemMeta() != null) { // Air
						ItemMeta itemMeta = itemStack.getItemMeta();
						itemMeta.setDisplayName(" ");
						itemStack.setItemMeta(itemMeta);
					}

					inventory.setItem(slot, itemStack);
					
				}

				slot++;
				
			}
			
		} catch (NullPointerException e) {
			P2Util.log("[Error] A NullPointerException occurred when creating the Rewards Menu for " + player.getName() + ":");
			e.printStackTrace();
		} catch (Exception e) {
			P2Util.log("[Error] An unknown exception occurred when creating the Rewards Menu for " + player.getName() + ":");
			e.printStackTrace();
		}

		MenuHandler.addRewardMenu(player, this);
		
	}

	public void update(Player player) {
		
		FileConfiguration config = TimedClaimsExtension.getYamlHandler().getConfig();
		String path = "menus.rewards.reward-items";

		for (Map.Entry<String, ItemStack> entry : rewardItems.entrySet()) {
			
			String key = entry.getKey();
			ItemStack itemStack = entry.getValue();
			ItemMeta itemMeta = itemStack.getItemMeta();

			String ipath = path + "." + key;

			ArrayList<String> lore = new ArrayList<String>();
			
			long result = TimedClaimsExtension.getMenuHandler().getTime(player);
			String time = TimedClaimsExtension.getMenuHandler().getFormattedTime(player);
			
			YamlConfiguration pconfig = TimedClaimsExtension.getYamlHandler().getPlayerYaml(player);
			String shortPath = "menus.rewards.reward-items";
			
			PlotPlayer user = PlotPlayer.wrap(player);
			PlotArea area = PS.get().getPlotAreaAbs(user.getLocation());
			int allowed = user.getAllowedPlots();
			int i = area.getPlotsAbs(user).size();
			
			int unclaimed = allowed - i;
			
			int delay = 0;
			for (String s : config.getConfigurationSection(shortPath).getKeys(false)) {
				delay = pconfig.getInt("rewards." + s + ".plots-given");
			}

			for (String line : config.getStringList(ipath + ".lore")) {

				lore.add(P2Util.colour(line.replace("{time}", time)
						.replace("{plotssofar}", Integer.toString(delay))
						.replace("{all}", Integer.toString(allowed))
						.replace("{unclaimed}", Integer.toString(unclaimed))
						.replace("{days}", Long.toString(TimeUnit.MILLISECONDS.toDays(result)))
						.replace("{hours}", Long.toString(TimeUnit.MILLISECONDS.toHours(result) - TimeUnit.DAYS.toHours(TimeUnit.MILLISECONDS
								.toDays(result))))
						.replace("{minutes}", Long.toString(TimeUnit.MILLISECONDS.toMinutes(result) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS
								.toHours(result))))
						.replace("{seconds}", Long.toString(TimeUnit.MILLISECONDS.toSeconds(result) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS
								.toMinutes(result))))));
			}

			itemMeta.setLore(lore);
			itemStack.setItemMeta(itemMeta);
			this.inventory.setItem(config.getInt(ipath + ".slot"), itemStack);
			
		}
		
	}

    public Inventory getInventory() {
        return inventory;
    }
    
}