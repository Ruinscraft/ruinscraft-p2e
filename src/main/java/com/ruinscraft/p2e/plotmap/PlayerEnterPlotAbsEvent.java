package com.ruinscraft.p2e.plotmap;

import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;

import com.intellectualcrafters.plot.object.Plot;

public class PlayerEnterPlotAbsEvent extends PlayerEvent {

	private static final HandlerList handlers = new HandlerList();
	private final Plot toPlot;
	private final Plot fromPlot;
	
	/**
	 * Called when a player leaves a plot.
	 *
	 * @param player Player that entered the plot
	 * @param plot   Plot that was entered
	 */
	public PlayerEnterPlotAbsEvent(Player player, Plot toPlot, Plot fromPlot) {
		super(player);
		this.toPlot = toPlot;
		this.fromPlot = fromPlot;
	}

	public static HandlerList getHandlerList() {
		return handlers;
	}
	
	/**
	 * Get the plot involved.
	 *
	 * @return Plot
	 */
	public Plot getToPlot() {
		return this.toPlot;
	}
	
	public Plot getFromPlot() {
		return this.fromPlot;
	}
	
	public HandlerList getHandlers() {
		return handlers;
	}

}
