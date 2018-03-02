package com.ruinscraft.p2e.plotborder;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Particle;

import com.intellectualcrafters.plot.commands.SubCommand;
import com.ruinscraft.p2e.P2Extension;
import com.ruinscraft.p2e.P2Extensions;


public class PlotBorderExtension implements P2Extension {
	
	public static final Particle PARTICLE = Particle.HEART;
	
	private static PlotBorderExtension plotBorder;
	private static P2Extensions instance = P2Extensions.getInstance();
	
	private BorderCommand command;
	
	private static List<UUID> activePlayers;
	
	@Override
	public boolean enable() {
		
		plotBorder = this;
		
		activePlayers = new ArrayList<UUID>();
		
		command = new BorderCommand();
		
		Bukkit.getScheduler().runTaskTimer(instance, new ParticleRunnable(), 0L, 10L);
		
		return true;
		
	}
	
	@Override
	public boolean disable() {
		plotBorder = null;
		return true;
	}
	
	public String getName() {
		return "Plot-Border";
	}
	
	public SubCommand getP2SubCommand() {
		return command;
	}
	
	public static PlotBorderExtension getPlotBorder() {
		return plotBorder;
	}
	
	public static List<UUID> getActivePlayers() {
		return activePlayers;
	}
	
}
