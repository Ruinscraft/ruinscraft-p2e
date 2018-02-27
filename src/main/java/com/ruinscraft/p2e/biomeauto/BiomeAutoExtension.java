package com.ruinscraft.p2e.biomeauto;

import java.text.Collator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.apache.commons.lang.WordUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType.SlotType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.intellectualcrafters.plot.commands.CommandCategory;
import com.intellectualcrafters.plot.commands.RequiredType;
import com.intellectualcrafters.plot.commands.SubCommand;
import com.intellectualcrafters.plot.object.PlotPlayer;
import com.plotsquared.general.commands.CommandDeclaration;
import com.ruinscraft.p2e.P2Extension;
import com.ruinscraft.p2e.P2Extensions;

import us.blockbox.biomefinder.BiomeFinder;
import us.blockbox.biomefinder.BiomeFinder.LocationPreference;
import us.blockbox.biomefinder.CacheManager;

@CommandDeclaration(
		command = "auto", 
		category = CommandCategory.CLAIMING, 
		usage = "/plot auto",
		permission = "plots.auto", 
		description = "Choose a biome to start claiming land in",
		requiredType = RequiredType.NONE)

public class BiomeAutoExtension extends SubCommand implements P2Extension, Listener {
	
	private static Map<Player, Inventory> inventories;
	private static Map<ItemStack, Biome> biomeInv;
	
	private static BiomeAutoExtension biomeAuto;
	private static P2Extensions instance;
	private static CacheManager cacheManager;
	
	@Override
	public boolean enable() {
		
		instance = P2Extensions.getInstance();
		biomeAuto = this;
		
		if (!(Bukkit.getPluginManager().isPluginEnabled("BiomeFinder"))) {
			instance.getLogger().info("BiomeFinder not found, disabling BiomeAutoExtension");
			disable();
			return false;
		}
		
		inventories = new HashMap<Player, Inventory>();
		biomeInv = new HashMap<ItemStack, Biome>();
		cacheManager = BiomeFinder.getPlugin().getCacheManager();
		
		return true;
		
	}
	
	@Override
	public boolean disable() {
		
		inventories.clear();
		biomeAuto = null;
		
		return true;
		
	}
	
	public String getName() {
		return "Biome-Auto";
	}
	
	public SubCommand getP2SubCommand() {
		return this;
	}

	@Override
	public boolean onCommand(PlotPlayer sender, String[] args) {
		
		Player player = Bukkit.getPlayer(sender.getUUID());
		if (!sender.hasPermission("biomefinder.tp")) {
			
			sender.sendMessage("no perm");
			return true;
			
		}
		
		if (!(sender instanceof PlotPlayer)) {
				
			sender.sendMessage("only from player pls thanks");
			return true;
				
		}
		
		final World world = player.getWorld();
		
		if (!BiomeFinder.getPlugin().getCacheManager().hasCache(world)) {
			
			sender.sendMessage("no world index");
			return true;
			
		}
		
		if (args.length < 1) {
			
			Inventory inventory = Bukkit.createInventory(null, 45, "Biome Inventory");	
			inventories.put(player, inventory);
			
			final List<Biome> biomes = new ArrayList<Biome>(cacheManager.getCache(world).keySet());
			
			List<ItemStack> items = new ArrayList<ItemStack>();
			List<ItemStack> vipItems = new ArrayList<ItemStack>();
			
			ItemStack border = new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 15);
			ItemMeta borderMeta = border.getItemMeta();
			borderMeta.setDisplayName(ChatColor.BLACK + " ");
			border.setItemMeta(borderMeta);
			
			inventory.setItem(0, border);
			inventory.setItem(1, border);
			inventory.setItem(2, border);
			inventory.setItem(3, border);
			inventory.setItem(4, border);
			inventory.setItem(5, border);
			inventory.setItem(6, border);
			inventory.setItem(7, border);
			inventory.setItem(8, border);
			inventory.setItem(9, border);
			inventory.setItem(17, border);
			inventory.setItem(18, border);
			inventory.setItem(26, border);
			inventory.setItem(27, border);
			inventory.setItem(35, border);
			inventory.setItem(36, border);
			inventory.setItem(44, border);
			
			for (Biome biome : biomes) {
				
				String biomeName = biome.name();
				ItemStack biomeItem;
				
				switch (biomeName) {
				
					case "BEACHES":
						biomeItem = new ItemStack(Material.BIRCH_FENCE_GATE, 1);
						break;
					case "BIRCH_FOREST":
						biomeItem = new ItemStack(Material.SAPLING, 1, (short) 2);
						break;
					case "BIRCH_FOREST_HILLS":
						continue;
					case "COLD_BEACH":
						continue;
					case "DEEP_OCEAN":
						biomeName = "Ocean";
						biomeItem = new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 11);
						break;
					case "DESERT":
						biomeItem = new ItemStack(Material.SAND, 1);
						break;
					case "DESERT_HILLS":
						continue;
					case "EXTREME_HILLS":
						biomeItem = new ItemStack(Material.GRAVEL, 1);
						break;
					case "EXTREME_HILLS_WITH_TREES":
						continue;
					case "FOREST":
						biomeItem = new ItemStack(Material.SAPLING, 1);
						break;
					case "FOREST_HILLS":
						biomeItem = new ItemStack(Material.SAPLING, 2);
						break;
					case "FROZEN_OCEAN":
						continue;
					case "FROZEN_RIVER":
						continue;
					case "HELL":
						biomeName = "The Nether";
						biomeItem = new ItemStack(Material.NETHERRACK, 1);
						break;
					case "ICE_FLATS":
						biomeItem = new ItemStack(Material.SNOW, 1);
						break;
					case "ICE_MOUNTAINS":
						biomeItem = new ItemStack(Material.ICE, 1);
						break;
					case "JUNGLE":
						biomeItem = new ItemStack(Material.SAPLING, 1, (short) 3);
						break;
					case "JUNGLE_EDGE":
						continue;
					case "JUNGLE_HILLS":
						continue;
					case "MESA":
						biomeItem = new ItemStack(Material.SAND, 1, (short) 1);
						break;
					case "MESA_CLEAR_ROCK":
						continue;
					case "MESA_ROCK":
						continue;
					case "MUSHROOM_ISLAND":
						biomeItem = new ItemStack(Material.MYCEL, 1);
						break;
					// temp	
					case "MUSHROOM_ISLAND_SHORE":
						continue;
					case "MUTATED_BIRCH_FOREST":
						continue;
					case "MUTATED_BIRCH_FOREST_HILLS":
						continue;
					case "MUTATED_DESERT":
						continue;
					case "MUTATED_EXTREME_HILLS":
						biomeName = "Extreme Hills M";
						biomeItem = new ItemStack(Material.GRAVEL, 2);
						break;
					case "MUTATED_EXTREME_HILLS_WITH_TREES":
						continue;
					case "MUTATED_FOREST":
						continue;
					case "MUTATED_ICE_FLATS":
						biomeName = "Ice Spikes";
						biomeItem = new ItemStack(Material.PACKED_ICE, 1);
						break;
					case "MUTATED_JUNGLE":
						continue;
					case "MUTATED_JUNGLE_EDGE":
						continue;
					case "MUTATED_MESA":
						continue;
					case "MUTATED_MESA_CLEAR_ROCK":
						continue;
					case "MUTATED_MESA_ROCK":
						continue;
					case "MUTATED_PLAINS":
						continue;
					case "MUTATED_REDWOOD_TAIGA":
						continue;
					case "MUTATED_REDWOOD_TAIGA_HILLS":
						continue;
					case "MUTATED_ROOFED_FOREST":
						continue;
					case "MUTATED_SAVANNA":
						biomeName = "Savanna M";
						biomeItem = new ItemStack(Material.SAPLING, 2, (short) 4);
						break;
					case "MUTATED_SAVANNA_ROCK":
						continue;
					case "MUTATED_SWAMPLAND":
						continue;
					case "MUTATED_TAIGA":
						continue;
					case "MUTATED_TAIGA_COLD":
						continue;
					case "OCEAN":
						continue;
					case "PLAINS":
						biomeItem = new ItemStack(Material.GRASS, 1);
						break;
					case "REDWOOD_TAIGA":
						biomeItem = new ItemStack(Material.DIRT, 1, (short) 2);
						break;
					case "REDWOOD_TAIGA_HILLS":
						continue;
					case "RIVER":
						continue;
					case "ROOFED_FOREST":
						biomeItem = new ItemStack(Material.SAPLING, 1, (short) 5);
						break;
					case "SAVANNA":
						biomeItem = new ItemStack(Material.SAPLING, 1, (short) 4);
						break;
					case "SAVANNA_ROCK":
						continue;
					case "SKY":
						biomeName = "The End";
						biomeItem = new ItemStack(Material.EYE_OF_ENDER, 1);
						break;
					case "SMALLER_EXTREME_HILLS":
						continue;
					case "STONE_BEACH":
						biomeItem = new ItemStack(Material.STONE, 1);
						break;
					case "SWAMPLAND":
						biomeItem = new ItemStack(Material.WATER_LILY, 1);
						break;
					case "TAIGA_COLD":
						biomeItem = new ItemStack(Material.SAPLING, 2, (short) 1);
						break;
					case "TAIGA":
						biomeItem = new ItemStack(Material.SAPLING, 1, (short) 1);
						break;
					case "TAIGA_COLD_HILLS":
						continue;
					case "TAIGA_HILLS":
						continue;
					case "VOID":
						continue;
					default:
						continue;
				
				}
				
				if (biomeName == biome.name()) {
					biomeName = getFriendlyName(biome);
				}
				
				if (!(player.hasPermission("biomefinder.biome." + biome.name().toLowerCase()))) {

					ItemMeta meta = biomeItem.getItemMeta();
					meta.setDisplayName(ChatColor.GOLD + biomeName);
					meta.setLore(Arrays.asList(ChatColor.RED + "Available for a higher VIP tier"));
					biomeItem.setItemMeta(meta);
 					
					vipItems.add(biomeItem);
					
					continue;
					
				}
				
				ItemMeta meta = biomeItem.getItemMeta();
				meta.setDisplayName(ChatColor.GOLD + biomeName);
				biomeItem.setItemMeta(meta);
				
				items.add(biomeItem);
				biomeInv.put(biomeItem, biome);
				
			}
			
			items = sortAlphabetically(items);
			
			for (ItemStack item : items) {
				inventory.addItem(item);
			}
			
			vipItems = sortAlphabetically(vipItems);
			
			for (ItemStack item : vipItems) {
				inventory.addItem(item);
			}
			
			String biomeName = "The Nether";
			ItemStack biomeItem = new ItemStack(Material.NETHERRACK, 1);
			ItemMeta nethermeta = biomeItem.getItemMeta();
			nethermeta.setDisplayName(ChatColor.DARK_RED + biomeName);
			nethermeta.setLore(Arrays.asList(ChatColor.GRAY + "Available soon!", 
							ChatColor.DARK_GRAY + "May be limited to", ChatColor.DARK_GRAY + "certain VIP tiers"));
			biomeItem.setItemMeta(nethermeta);
			inventory.addItem(biomeItem);
			
			String netherName = "The End";
			ItemStack netherItem = new ItemStack(Material.EYE_OF_ENDER, 1);
			ItemMeta netherrmeta = netherItem.getItemMeta();
			netherrmeta.setDisplayName(ChatColor.DARK_PURPLE + netherName);
			netherrmeta.setLore(Arrays.asList(ChatColor.GRAY + "Available soon!", 
							ChatColor.DARK_GRAY + "May be limited to", ChatColor.DARK_GRAY + "certain VIP tiers"));
			netherItem.setItemMeta(netherrmeta);
			inventory.addItem(netherItem);
			
			player.openInventory(inventory);
			 
			return true;
			
		}
		
		final Biome biome = BiomeFinder.parseBiome(args[0]);
		
		if (biome == null) {
			
			sender.sendMessage("unspecified biome");
			
		} else {
			
			if (!(player.hasPermission("biomefinder.biome." + biome.name().toLowerCase()))) {
				
				sender.sendMessage("no perm");
				return false;
				
			}
			
			if (BiomeFinder.tpToBiome(player, biome, LocationPreference.ANY)) {
				sender.sendMessage("good teleport");
				String title = ("");
				String subtitle = (ChatColor.GOLD + "Type /p claim to start building");
				player.sendTitle(title, subtitle, 20, 120, 20);
			}
			
		}
		
		return false;
		
	}
	
	public String getFriendlyName(Biome biome){
		return WordUtils.capitalizeFully(Pattern.compile("_").matcher(biome.name()).replaceAll(" "));
	}
	
	public List<ItemStack> sortAlphabetically(List<ItemStack> items) {
		
		List<String> names = new ArrayList<String>(items.size()); 
		for (ItemStack item : items) {
			String name = item.getItemMeta().getDisplayName();
			names.add(name);
		}
		
		Collections.sort(names, Collator.getInstance());
		List<ItemStack> newItems = new ArrayList<ItemStack>(items.size());
		
		for (String name : names) {
			for (ItemStack item : items) {
				if (item.getItemMeta().getDisplayName() == name) {
					newItems.add(item);
					break;
				}
			}
		}
		
		return newItems;
		
	}
	
	public static BiomeAutoExtension getBiomeAuto() {
		return biomeAuto;
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onInventoryClick(InventoryClickEvent event) {
		
		Player player = (Player) event.getWhoClicked();
		
		if (!(inventories.containsKey(player))) {
			return;
		}
		
		Inventory invFromHash = inventories.get(player);
		
		if (!(invFromHash.getTitle() == event.getInventory().getTitle())) {
			return;
		}
		
		event.setCancelled(true);

		if (event.getClick().isShiftClick()) {
			return;
		}
		
		// click out of inventory easily
		if (!(event.getSlotType() == SlotType.CONTAINER)) {
			player.closeInventory();
			return;
		}
		
		// do nothing if the item is not a reaction
		if (event.getCurrentItem().getType() == Material.BARRIER ||
				  event.getCurrentItem().getType() == Material.AIR ||
				  		event.getCurrentItem().getType() == Material.STAINED_GLASS_PANE) {
			return;
		}
		
		ItemStack item = event.getCurrentItem();
		
		if (!(biomeInv.containsKey(item))) {
			return;
		}
		
		Biome biome = biomeInv.get(item);
		BiomeFinder.tpToBiome(player, biome);
		if (BiomeFinder.tpToBiome(player, biome, LocationPreference.ANY)) {
			player.sendMessage("good teleport");
			String title = ("");
			String subtitle = (ChatColor.GOLD + "Type /p claim to start building");
			player.sendTitle(title, subtitle, 20, 120, 20);
		}
		
	}
	
}
