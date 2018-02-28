package com.ruinscraft.p2e.timedclaims;

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

	public boolean onCommand(PlotPlayer plotPlayer, String[] args) {
		
		if (plotPlayer.isOnline()) {
			TimedClaimsExtension.getMenuHandler().openRewardsMenu(plotPlayer);
		}

		return false;
		
	}
	
}
