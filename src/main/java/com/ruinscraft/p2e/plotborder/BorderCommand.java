package com.ruinscraft.p2e.plotborder;

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

	public boolean onCommand(PlotPlayer plotPlayer, String[] args) {
		
		if (!(plotPlayer instanceof PlotPlayer)) {
			return false;
		}
		
		if (PlotBorderExtension.getActivePlayers().contains(plotPlayer.getUUID())) {
			PlotBorderExtension.getActivePlayers().remove(plotPlayer.getUUID());
			return false;
		}
		
		PlotBorderExtension.getActivePlayers().add(plotPlayer.getUUID());
		
		return true;
		
	}
	
}
