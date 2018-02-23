package com.ruinscraft.p2e.extensions;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.intellectualcrafters.plot.commands.SubCommand;
import com.intellectualcrafters.plot.config.C;
import com.intellectualcrafters.plot.object.Plot;
import com.intellectualcrafters.plot.object.PlotPlayer;
import com.intellectualcrafters.plot.util.MainUtil;
import com.ruinscraft.p2e.P2Extension;

public class Timer extends SubCommand implements P2Extension {
	
	private final Map<Plot, Long> plotTimers = new ConcurrentHashMap<>();
	
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
	public boolean onCommand(PlotPlayer plotPlayer, String[] args) {
		
		Plot plot = plotPlayer.getCurrentPlot();
		
		if (!plot.isOwner(plotPlayer.getUUID())) {
			MainUtil.sendMessage(plotPlayer, C.NO_PLOT_PERMS);
			return true;
		}
		
		
		
		return true;
		
	}
	
}
