package com.ruinscraft.p2e.plotmap;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;

import com.intellectualcrafters.plot.PS;
import com.intellectualcrafters.plot.commands.SubCommand;
import com.intellectualcrafters.plot.object.Location;
import com.intellectualcrafters.plot.object.Plot;
import com.intellectualcrafters.plot.object.PlotArea;
import com.intellectualcrafters.plot.object.PlotId;
import com.intellectualcrafters.plot.object.PlotPlayer;
import com.ruinscraft.p2e.P2Extension;
import com.ruinscraft.p2e.P2Extensions;
import com.ruinscraft.p2e.P2Util;

import net.md_5.bungee.api.ChatColor;

public class PlotMapExtension implements Listener, P2Extension {

	private static PlotMapExtension plotMap;
	private static P2Extensions instance = P2Extensions.getInstance();

	public static PlotMapExtension getPlotMap() {
		return plotMap;
	}
	
	// default 
	public static int radius = 3;

	private PlotId id;
	
	public Map<String, Boolean> userMap = new HashMap<String, Boolean>();
	public Map<String, Boolean> taskMap = new HashMap<String, Boolean>();
	private Map<Player, Scoreboard> scoreboards = new HashMap<Player, Scoreboard>();
	private LinkedHashMap<String, ChatColor> users = new LinkedHashMap<String, ChatColor>();
	
	private MapCommand command;

	@Override
	public boolean enable() {
		
		plotMap = this;
		
		// gets subcommand
		command = new MapCommand();

		// register login / plot events
		instance.getServer().getPluginManager().registerEvents(this, instance);
		
		return true;

	}

	@Override
	public boolean disable() {

		plotMap = null;
		
		return true;

	}
	
	public String getName() {
		return "plot-map";
	}
	
	public SubCommand getP2SubCommand() {
		return command;
	}

	public boolean updateMap(Player realPlayer) {

		PlotPlayer player = PlotPlayer.get(realPlayer.getName());
		Scoreboard scoreboard;

		// get scoreboard assigned with player
		if (scoreboards.containsKey(realPlayer)) {
			scoreboard = scoreboards.get(realPlayer);
		} else {
			player.sendMessage(ChatColor.DARK_GRAY + "[" + ChatColor.GOLD + "P2" + ChatColor.DARK_GRAY + "]"
					+ ChatColor.GRAY + " Error while attempting to update claim map");
			return false;
		}

		// if EntryMessageTask is not being run, define that
		if (!(taskMap.containsKey(realPlayer.getName()))) {
			taskMap.put(realPlayer.getName(), false);
		}
		
		if (userMap.containsKey(realPlayer.getName())) {

			// remove the sidebar objective if the player does not have the map enabled
			// (after EntryMessageTask, etc.)
			if (userMap.get(realPlayer.getName()) == false) {

				if (!(scoreboard.getObjective(realPlayer.getName()) == null)) {

					scoreboard.getObjective(realPlayer.getName()).unregister();

				}

				return false;

			}

		}

		Location loc = player.getLocation();
				
		// get plot ID of the current plot
		if (!(player.getCurrentPlot() == null)) {

			PlotArea area = player.getPlotAreaAbs();
			id = area.getPlotAbs(player.getLocation()).getId();

		} else {

			// if player is on a road
			loc = player.getLocation().add(1, 0, 1);

			// go the other way if you are still on a road
			if (loc.getPlot() == null) {
				loc = player.getLocation().add(-2, 0, -2);
			}
			
			PlotArea area = player.getPlotAreaAbs();
			id = area.getPlotAbs(loc).getId();

		}

		// coords of the plot
		int z = id.y;
		int x = id.x;
		
		// get color codes for each user plot
		users.clear();
		users = setColors(id, loc, player);
		
		// lists of the "scores" for the plot map
		List<String> scorenames = new ArrayList<String>();
		
		PlotArea area = PS.get().getPlotAreaManager().getPlotArea(player.getLocation());
		int radius = player.getMeta("plot-map-radius");
		
		// map sizing
		for (z = (id.y - radius); z <= (id.y + radius); z++) {

			StringBuilder stringBuilder = new StringBuilder();

			String entry = " ";

			// map sizing
			for (x = (id.x - radius); x <= (id.x + radius); x++) {

				Plot plot = Plot.fromString(area, (x + ";" + z));

				if (plot.hasOwner()) {
					
					// only goes through this once; claims should have only one owner
					String owner = Bukkit.getOfflinePlayer((UUID) plot.getOwners().toArray()[0]).getName();

					// your claim
					if (owner == player.getName()) {
						
						if (z == (id.y) && x == (id.x)) {
							stringBuilder.append(ChatColor.DARK_GREEN + "█");
							continue;
						}
						
						stringBuilder.append(ChatColor.DARK_GREEN + "▓");
						
						continue;

					}
					
					if (owner == "null" || owner == null) {
						owner = "???";
					}
					
					// claims you are in
					if (z == (id.y) && x == (id.x)) {

						stringBuilder.append(users.get(owner) + "█");
						continue;

					}

					// for other claims
					stringBuilder.append(users.get(owner) + "▓");
					
					continue;

				} else {

					//claims you are in 
					if (z == (id.y) && x == (id.x)) {
						stringBuilder.append(ChatColor.RED + "█");
						continue;
					}
					stringBuilder.append(ChatColor.RED + "▒");
					continue;

				}

			}

			// required to differentiate between each score entry, must be unique
			if (z == (id.y - 5)) {
				stringBuilder.append(ChatColor.ITALIC + "   ");
			}
			if (z == (id.y - 4)) {
				stringBuilder.append(ChatColor.DARK_RED + "   ");
			}
			if (z == (id.y - 3)) {
				stringBuilder.append(ChatColor.RED + "   ");
			}
			if (z == (id.y - 2)) {
				stringBuilder.append(ChatColor.GOLD + "   ");
			}
			if (z == (id.y - 1)) {
				stringBuilder.append(ChatColor.YELLOW + "   ");
			}
			if (z == (id.y)) {
				stringBuilder.append(ChatColor.GREEN + " ");
				double direction = realPlayer.getEyeLocation().getYaw() / 90f;
				stringBuilder.append(ChatColor.GOLD + " " + ChatColor.BOLD + getLocationCharacter(direction));
			}
			if (z == (id.y + 1)) {
				stringBuilder.append(ChatColor.DARK_GREEN + "   ");
			}
			if (z == (id.y + 2)) {
				stringBuilder.append(ChatColor.BLUE + "   ");
			}
			if (z == (id.y + 3)) {
				stringBuilder.append(ChatColor.DARK_PURPLE + "   ");
			}
			if (z == (id.y + 4)) {
				stringBuilder.append(ChatColor.RESET + "   ");
			}
			if (z == (id.y + 5)) {
				stringBuilder.append(ChatColor.BLACK + "   ");
			}

			// add score entry to objective
			String score = entry + stringBuilder;
			scorenames.add(score);

		}
		
		// removes sidebar objective if it exists, in order to update with a new objective
		if (!(scoreboard.getObjective(realPlayer.getName()) == null)) {
			scoreboard.getObjective(realPlayer.getName()).unregister();
		}
		
		// objective which will contain scores displayed in the map
		Objective objective = scoreboard.registerNewObjective(realPlayer.getName(), player.getUUID() + " plotmap");

		objective.setDisplayName(ChatColor.GOLD + "" + ChatColor.BOLD + "Claim Map");
		objective.setDisplaySlot(DisplaySlot.SIDEBAR);

		realPlayer.setScoreboard(scoreboard);

		// each score entry needs a different score to be in order
		// decreases for each added entry
		int scoreInt = 10;

		// claim map
		for (String scorename : scorenames) {
			
			if (scorename.length() > 39) {
				scorename = scorename.substring(0, 39);
			}
			Score score = objective.getScore(scorename);
			scoreInt--;
			score.setScore(scoreInt);

		}

		// shows this if no claims nearby
		if (users.isEmpty()) {

			users.clear();
			Score close = objective.getScore(ChatColor.GOLD + "Disable: /p map");
			scoreInt--;
			close.setScore(scoreInt);
			return false;

		}

		// lists nearby claims
		String plots = (ChatColor.GOLD + "Nearby claims:");
		Score close = objective.getScore(plots);
		scoreInt--;
		close.setScore(scoreInt);

		for (String owner : users.keySet()) {

			if (scoreInt == -3) {
				break;
			}
			
			Score ownerscore = objective.getScore(users.get(owner) + "▓ " + ChatColor.RESET 
												+ "" + owner);
			
			if (realPlayer.getName() == owner) {
				ownerscore = objective.getScore(users.get(owner) + "▓ " + ChatColor.RESET 
												+ "" + ChatColor.ITALIC + "Yourself");
			}
			
			scoreInt--;
			ownerscore.setScore(scoreInt);

		}

		// if EntryMessageTask is being run, show the entry thing
		if (taskMap.get(realPlayer.getName()) == true) {

			// if plot does not have an owner, does not show the entry
			if (player.getCurrentPlot().hasOwner() == false) {
				return false;
			}
			
			String entermsg = (ChatColor.GOLD + "Entering claim:");
			String owner = "";

			for (UUID uuid : player.getCurrentPlot().getOwners()) {

				owner = (ChatColor.RESET + "" + ChatColor.UNDERLINE + Bukkit.getOfflinePlayer(uuid).getName());
				
				if (Bukkit.getOfflinePlayer(uuid).getName() == null) {
					owner = (ChatColor.UNDERLINE + "???");
				}

				if (realPlayer.getName() == Bukkit.getOfflinePlayer(uuid).getName()) {
					owner = (ChatColor.UNDERLINE + "" + ChatColor.ITALIC + "Yourself");
				}

			}
			

			Score sentermsg = scoreboard.getObjective(realPlayer.getName()).getScore(entermsg);
			Score sowner = scoreboard.getObjective(realPlayer.getName()).getScore(owner);

			scoreInt--;
			sentermsg.setScore(scoreInt);
			scoreInt--;
			sowner.setScore(scoreInt);

		}

		return false;

	}
	
	// colors for each nearby plot (needs some work)
	public LinkedHashMap<String, ChatColor> setColors(PlotId id, Location loc, 
			PlotPlayer player) {
		
		int z = id.y;
		int x = id.x;
		
		List<String> owners = new ArrayList<String>();
		int radius = player.getMeta("plot-map-radius");
		
		for (z = (id.y - radius); z <= (id.y + radius); z++) {
			
			for (x = (id.x - radius); x <= (id.x + radius); x++) {
				
				Plot plot = Plot.fromString(PS.get().getPlotAreaManager().getPlotArea(loc),
						(x + ";" + z));
				
				if (!(plot.hasOwner())) { continue; } 
				
				for (UUID owneruuid : plot.getOwners()) {
					
					String owner = Bukkit.getOfflinePlayer(owneruuid).getName();
					
					if (owner == "null" || owner == null) {
						owner = "???";
					}
					
					if (!owners.contains(owner)) {
						owners.add(owner);
					}
					
					break;
					
				}
				
			}
			
		}
		
		for (String owner : owners) {
			if (!users.containsKey(owner)) {
				users.put(owner, ChatColor.GREEN);
			}
		}
		
		List<String> removethese = new ArrayList<String>();
		
		for (String owner : users.keySet()) {
			
			if (!owners.contains(owner)) {
				removethese.add(owner);
				continue;
			}
			
			if (!(users.get(owner) == ChatColor.GREEN)) {
				continue;
			}
			
			if (owner == player.getName()) {
				users.put(owner, ChatColor.DARK_GREEN);
				continue;
			}
			
			if (!users.containsValue(ChatColor.AQUA)) {
				users.put(owner, ChatColor.AQUA);
				continue;
			} else if (!users.containsValue(ChatColor.LIGHT_PURPLE)) {
				users.put(owner, ChatColor.LIGHT_PURPLE);
				continue;
			} else if (!users.containsValue(ChatColor.BLUE)) {
				users.put(owner, ChatColor.BLUE);
				continue;
			} else if (!users.containsValue(ChatColor.DARK_AQUA)) {
				users.put(owner, ChatColor.DARK_AQUA);
				continue;
			} else if (!users.containsValue(ChatColor.DARK_PURPLE)) {
				users.put(owner, ChatColor.DARK_PURPLE);
				continue;
			} else if (!users.containsValue(ChatColor.GRAY)) {
				users.put(owner, ChatColor.GRAY);
				continue;
			} else if (!users.containsValue(ChatColor.DARK_BLUE)) {
				users.put(owner, ChatColor.DARK_BLUE);
				continue;
			} else if (!users.containsValue(ChatColor.DARK_GRAY)) {
				users.put(owner, ChatColor.DARK_GRAY);
				continue;
			} else if (!users.containsValue(ChatColor.YELLOW)) {
				users.put(owner, ChatColor.YELLOW);
				continue;
			} else if (!users.containsValue(ChatColor.WHITE)) {
				users.put(owner, ChatColor.WHITE);
				continue;
			} else {
				users.put(owner, ChatColor.GREEN);
			}
			
		}
		
		for (String owner : removethese) {
			users.remove(owner, users.get(owner));
		}
		
		return users;
		
	}
	
	// future use for direction
	public String getLocationCharacter(Double direction) {
		
		if ((direction > 135 && direction <= 180) || (direction < -135 && direction >= -180)) {
			return "^";
		} else if (direction >= 45 && direction < 135) {
			return "<";
		} else if (direction >= -45 && direction < 45) {
			return "v";
		} else if (direction >= -135 && direction < -45) {
			return ">";
		} else {
			return "?";
		}
		
	}
	
	// users with map enabled
	public Map<String, Boolean> getUserMap() {
		return userMap;
	}
	
	// users with EntryMessageTask ongoing
	public Map<String, Boolean> getTaskMap() {
		return taskMap;
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerJoinEvent(PlayerJoinEvent join) {

		// map enabled for first-time players
		if (!(join.getPlayer().hasPlayedBefore())) {
			userMap.put(join.getPlayer().getName(), true);
			Scoreboard scoreboard = Bukkit.getServer().getScoreboardManager().getNewScoreboard();
			scoreboards.put(join.getPlayer(), scoreboard);
		}

		// puts scoreboard in scoreboards map, if not there already
		if (!(scoreboards.containsKey(join.getPlayer()))) {
			Scoreboard scoreboard = Bukkit.getServer().getScoreboardManager().getNewScoreboard();
			scoreboards.put(join.getPlayer(), scoreboard);
		}

	}
	
	@EventHandler
	public void onPlayerLeaveEvent(PlayerQuitEvent event) {
		
		OfflinePlayer player = event.getPlayer();
		
		taskMap.remove(player.getName());
		userMap.remove(player.getName());
		scoreboards.remove(player);
		
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerPlotEntry(PlayerEnterPlotAbsEvent enter) {

		// entry message if the plot has an owner
		if (enter.getToPlot().hasOwner()) {

			List<String> owners = new ArrayList<String>();

			for (UUID uuid : enter.getToPlot().getOwners()) {
				
				Runnable entryMessageTask = new EntryMessageTask(enter.getPlayer());
				
				if (uuid == null) {
					owners.add("???");
					instance.getServer().getScheduler().runTaskAsynchronously(instance, entryMessageTask);
					break;
				}
				
				if (!(enter.getFromPlot() == null)) {
					if (enter.getFromPlot().hasOwner()) {
						if (uuid == enter.getFromPlot().getOwners().toArray()[0]) { 
							break;
						}
					}
				}

				if (enter.getPlayer().getName() == Bukkit.getOfflinePlayer(uuid).getName()) {

					owners.add("Your claim");
					instance.getServer().getScheduler().runTaskAsynchronously(instance, entryMessageTask);

				} else {

					owners.add(Bukkit.getOfflinePlayer(uuid).getName());
					instance.getServer().getScheduler().runTaskAsynchronously(instance, entryMessageTask);

				}
				
				break;

			}

		}

		// updates the map if the user has the map enabled
		if (userMap.containsKey(enter.getPlayer().getName())) {

			if (userMap.get(enter.getPlayer().getName()) == true) {

				this.updateMap(enter.getPlayer());
				return;

			}

		} else {

			return;

		}

	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerTurn(PlayerMoveEvent event) {
		
		PlotPlayer plotPlayer = PlotPlayer.wrap(event.getPlayer());
		Plot oldPlot = plotPlayer.getPlotAreaAbs().getPlotAbs(P2Util.getLocation(event.getFrom()));
		Plot newPlot = plotPlayer.getPlotAreaAbs().getPlotAbs(P2Util.getLocation(event.getTo()));
		
		if (oldPlot == null && newPlot == null) {
			return;
		} else if (oldPlot != null && newPlot == null) {
			return;
		} else if ((oldPlot == null && newPlot != null) || !(oldPlot.getId().equals(newPlot.getId()))) {
			Bukkit.getServer().getPluginManager().callEvent(new PlayerEnterPlotAbsEvent(event.getPlayer(), newPlot, oldPlot));
			return;
		}
		
		// updateMap(event.getPlayer()); something like that
		
	}

}
