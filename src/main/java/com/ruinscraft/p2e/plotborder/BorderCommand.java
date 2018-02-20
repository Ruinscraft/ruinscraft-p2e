package com.ruinscraft.p2e.plotborder;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class BorderCommand implements CommandExecutor {

	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		
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
