package com.ruinscraft.p2e.plotmap;

import org.bukkit.Bukkit;

import com.intellectualcrafters.plot.commands.CommandCategory;
import com.intellectualcrafters.plot.commands.RequiredType;
import com.intellectualcrafters.plot.commands.SubCommand;
import com.intellectualcrafters.plot.object.PlotPlayer;
import com.plotsquared.general.commands.CommandDeclaration;

import net.md_5.bungee.api.ChatColor;

@CommandDeclaration(
		command = "map", 
		category = CommandCategory.CLAIMING, 
		usage = "/plot map [0-4|update]",
		permission = "plots.map", 
		description = "A sidebar map of the plots in your area",
		requiredType = RequiredType.NONE)
public class MapCommand extends SubCommand {
	
	private static PlotMapExtension plotMap = PlotMapExtension.getPlotMap();

	@Override
	public boolean onCommand(final PlotPlayer player, final String[] args) {

		String prefix = (ChatColor.DARK_GRAY + "[" + ChatColor.GOLD + "P2" 
							+ ChatColor.DARK_GRAY + "]" + ChatColor.GRAY + " ");
		
		if (!(plotMap.getUserMap().containsKey(player.getName()))) {
			plotMap.getUserMap().put(player.getName(), false);
		}
		
		if (args.length > 0) {
			
			if (args[0].equalsIgnoreCase("update")) {
				
				if (plotMap.getUserMap().get(player.getName()) == false) {
					plotMap.getUserMap().put(player.getName(), true);
					player.sendMessage(prefix + "Claim map was disabled, it has been enabled");
				}
				
				plotMap.updateMap(Bukkit.getPlayer(player.getUUID()));
				player.sendMessage(prefix + "Claim map updated");
				
				return false;
				
			}
			
			int arg = 3;
			
			try {
				arg = Integer.parseInt(args[0].toString());
			} catch (NumberFormatException e) {
				player.sendMessage(prefix + "Provided value not an integer");
			}
			
			if (arg < 0 || arg > 4) {
				player.sendMessage(prefix + "Provided value not valid (0-4 allowed)");
				arg = 3;
			}
			
			player.sendMessage(prefix + "Radius set to " + String.valueOf(arg));
			PlotMapExtension.radius = arg;
			
			if (plotMap.getUserMap().get(player.getName())) {
				
				plotMap.updateMap(Bukkit.getPlayer(player.getUUID()));
				return false;
				
			}
			
			plotMap.getUserMap().put(player.getName(), false);
			
		} else {
			
			PlotMapExtension.radius = 3;
			
			// random notifs
			double random = (Math.random() * 10);
			if (random > 3 && random < 3.4) {
				
				player.sendMessage(prefix + "Tip: Set the radius of your map with /p map [0-4]");
				player.sendMessage(prefix + "Your radius is currently set to 3");
				
			}
			if (random > 3.4 && random < 3.7) {
				
				player.sendMessage(prefix + "Tip: You can update the map on demand with /p map update");
				
			}
			
		}
		
		// enables/disables the map
		if (plotMap.getUserMap().get(player.getName()) == false) {

			plotMap.getUserMap().put(player.getName(), true);
			plotMap.updateMap(Bukkit.getPlayer(player.getUUID()));
			player.sendMessage(prefix + "Claim map enabled");
			
			return false;

		}
		
		if (plotMap.getUserMap().get(player.getName()) == true) {

			plotMap.getUserMap().put(player.getName(), false);
			plotMap.updateMap(Bukkit.getPlayer(player.getUUID()));
			player.sendMessage(prefix + "Claim map disabled");
			
			return false;

		} 

		return false;

	}

}
