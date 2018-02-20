package com.ruinscraft.p2e.timedclaims;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import com.ruinscraft.p2e.P2Util;

public class RewardsCmd implements CommandExecutor {

	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args)
	{
		if (sender instanceof Player)
		{
			Player player = (Player) sender;

			if (label.equalsIgnoreCase("rewards") || label.equalsIgnoreCase("nextclaim")) {
				
				TimedClaimsExtension.getMenuHandler().openRewardsMenu(player);
				
			} else {
				
				if (args.length >= 1) {
					
					if (args[0].equalsIgnoreCase("help")) {
						
						player.sendMessage("&8-- &6TimedRewards &7Commands Help &8--");
						player.sendMessage("&e/timedrewards help &8- &7Displays this page.");
						player.sendMessage("&e/rewards &8- &7Opens the rewards menu.");
						
					} else if (args[0].equalsIgnoreCase("open")) {
						
						TimedClaimsExtension.getMenuHandler().openRewardsMenu(player);
						
					} else {
						
						player.sendMessage("{prefix} Invalid arguments, please use: &e{args}&7."
								.replace("{prefix}", P2Util.prefix)
								.replace("{args}", "/timedrewards help"));
						
					}
					
				} else {
					
					player.sendMessage("{prefix} Invalid arguments, please use: &e{args}&7."
							.replace("{prefix}", P2Util.prefix)
							.replace("{args}", "/timedrewards help"));
					
				}
				
			}
			
		} else if (sender instanceof ConsoleCommandSender) {
			
			if (args.length >= 3)
			{
				if (args[0].equalsIgnoreCase("message") || args[0].equalsIgnoreCase("msg"))
				{
					Player player = Bukkit.getPlayer(args[1]);

					if (player != null)
					{
						StringBuilder msg = new StringBuilder();

						for (int i = 2; i < args.length; i++)
						{
							msg.append(args[i] + " ");
						}

						player.sendMessage(msg.toString());
					}
					else
					{
						P2Util.log("[Error] Could not find player: " + args[1]);
					}
				}
			}
			else
			{
				P2Util.log("-- TimedRewards Console Command Help --");
				P2Util.log("tr msg <player> <msg> - Sends a message without prefix to specified player.");
			}
		}

		return false;
	}
}
