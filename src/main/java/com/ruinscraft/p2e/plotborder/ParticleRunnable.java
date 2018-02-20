package com.ruinscraft.p2e.plotborder;

import java.util.List;
import java.util.UUID;

import com.ruinscraft.p2e.P2Extensions;

public class ParticleRunnable implements Runnable {

	public void run() {
		
		final List<UUID> players = PlotBorderExtension.getActivePlayers();
		
		long time = System.currentTimeMillis();
		
		PlotBorderUtil.spawnPoints(players);
		
		long nextTime = System.currentTimeMillis();
		P2Extensions.getInstance().getLogger().info(String.valueOf(nextTime - time));

	}

}

