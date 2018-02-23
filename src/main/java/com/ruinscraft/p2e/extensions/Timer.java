package com.ruinscraft.p2e.extensions;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.intellectualcrafters.plot.commands.CommandCategory;
import com.intellectualcrafters.plot.commands.RequiredType;
import com.intellectualcrafters.plot.commands.SubCommand;
import com.intellectualcrafters.plot.config.C;
import com.intellectualcrafters.plot.object.Location;
import com.intellectualcrafters.plot.object.Plot;
import com.intellectualcrafters.plot.object.PlotPlayer;
import com.intellectualcrafters.plot.util.MainUtil;
import com.intellectualcrafters.plot.util.PlotGameMode;
import com.plotsquared.general.commands.CommandDeclaration;
import com.ruinscraft.p2e.P2Extension;

@CommandDeclaration(
		command = "timer",
		description = "Start a timer on the plot you are standing on",
		usage = "/plot timer",
		permission = "plots.timer",
		category = CommandCategory.APPEARANCE,
		requiredType = RequiredType.PLAYER)
public class Timer extends SubCommand implements P2Extension {

	private final Map<Plot, Expire> plotTimers = new ConcurrentHashMap<>();
	
	private class Expire {
		
		private final long expireAt;
		private final Runnable runOnExpire;
		
		public Expire(long expireAt, Runnable runOnExpire) {
			this.expireAt = expireAt;
			this.runOnExpire = runOnExpire;
		}
		
		public long getExpireAt() {
			return expireAt;
		}
		
		public Runnable runOnExpire() {
			return runOnExpire;
		}
		
	}
	
	@Override
	public void enable() {
		
		
	
	}

	@Override
	public void disable() {
		plotTimers.clear();
	}

	@Override
	public String getName() {
		return "timer";
	}

	@Override
	public SubCommand getP2SubCommand() {
		return this;
	}

	@Override
	// TODO: make it where you can set gamemode AND teleport players on expiry
	public boolean onCommand(PlotPlayer plotPlayer, String[] args) {

		final Plot plot = plotPlayer.getCurrentPlot();

		if (!plot.isOwner(plotPlayer.getUUID())) {
			MainUtil.sendMessage(plotPlayer, C.NO_PLOT_PERMS);
			return true;
		}
		
		if (args.length > 0) {
			
			if (args[0].equalsIgnoreCase("cancel")) {
				if (!plotTimers.containsKey(plot)) {
					MainUtil.sendMessage(plotPlayer, "you dont have a plot timer running");
					return false;
				}
				plotTimers.remove(plot);
				MainUtil.sendMessage(plotPlayer, "Your plot timer has been canceled");
			}
			
			// eg 3h1m, 30s, 1m20s
			String timeString = args[0];
			Runnable runWhenExpire = null;
			
			// check options
			if (args.length > 1) {
				
				if (args[1].equalsIgnoreCase("teleport")) {
					Location l = plotPlayer.getLocation();
					runWhenExpire = () -> {
						plot.getPlayersInPlot().forEach(p -> p.teleport(l));
					};
					MainUtil.sendMessage(plotPlayer, "All players in plot will teleport to the location you are currently standing at when the timer finishes");
				}
				
				if (args[1].equalsIgnoreCase("gamemode")) {
					if (args.length < 2) {
						MainUtil.sendMessage(plotPlayer, "please specify a gamemode to set players to when the timer expires");
						return false;
					}
					PlotGameMode gameMode = PlotGameMode.valueOf(args[2]);
					runWhenExpire = () -> {
						plot.getPlayersInPlot().forEach(p -> p.setGameMode(gameMode));
					};
					MainUtil.sendMessage(plotPlayer, "all players will be set to " + gameMode.getName() + " when the timer epires");
				}
				
			}
			
		}

		if (plotTimers.containsKey(plot)) {
			MainUtil.sendMessage(plotPlayer, "You already have a plot timer running on this plot, /plot timer cancel");
			return false;
		}

		return true;

	}

}
