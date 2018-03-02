package com.ruinscraft.p2e.plotborder;

import java.util.List;
import java.util.UUID;

public class ParticleRunnable implements Runnable {

	public void run() {
		
		final List<UUID> players = PlotBorderExtension.getActivePlayers();
		
		PlotBorderUtil.spawnPoints(players);

	}

}

