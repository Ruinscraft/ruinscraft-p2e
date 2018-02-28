package com.ruinscraft.p2e.timedclaims;

import org.bukkit.entity.Player;

import com.intellectualcrafters.plot.commands.CommandCategory;
import com.intellectualcrafters.plot.commands.RequiredType;
import com.intellectualcrafters.plot.commands.SubCommand;
import com.intellectualcrafters.plot.object.PlotPlayer;
import com.plotsquared.general.commands.CommandDeclaration;

@CommandDeclaration(
		command = "next", 
		category = CommandCategory.CLAIMING, 
		usage = "/plot next",
		permission = "plots.next", 
		description = "Show when you will earn your next claim",
		requiredType = RequiredType.PLAYER)

public class NextCommand extends SubCommand {

	public boolean onCommand(PlotPlayer sender, String[] args) {
		
		if (sender.isOnline()) {
			
			Player player = (Player) sender;

			TimedClaimsExtension.getMenuHandler().openRewardsMenu(player);
			
		}

		return false;
		
	}
	
}
