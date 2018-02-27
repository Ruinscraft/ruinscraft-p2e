package com.ruinscraft.p2e.plotborder;

import org.bukkit.entity.Player;

import com.intellectualcrafters.plot.commands.CommandCategory;
import com.intellectualcrafters.plot.commands.RequiredType;
import com.intellectualcrafters.plot.commands.SubCommand;
import com.intellectualcrafters.plot.object.PlotPlayer;
import com.plotsquared.general.commands.CommandDeclaration;

@CommandDeclaration(
		command = "border", 
		category = CommandCategory.APPEARANCE, 
		usage = "/plot border",
		permission = "plots.border", 
		description = "Visualize your plot border with particles",
		requiredType = RequiredType.PLAYER)

public class BorderCommand extends SubCommand {

	public boolean onCommand(PlotPlayer sender, String[] args) {
		
		if (!(sender instanceof Player)) {
			return false;
		}
		
		Player player = (Player) sender;
		
		if (PlotBorderExtension.getActivePlayers().contains(player.getUniqueId())) {
			PlotBorderExtension.getActivePlayers().remove(player.getUniqueId());
			return false;
		}
		
		PlotBorderExtension.getActivePlayers().add(player.getUniqueId());
		
		return true;
		
	}
	
}
