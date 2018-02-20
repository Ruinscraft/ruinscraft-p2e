package com.ruinscraft.p2e.plotmap;

import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;

import com.intellectualcrafters.plot.object.Plot;

public class PlayerEnterPlotAbsEvent extends PlayerEvent {

	private static final HandlerList handlers = new HandlerList();
	private final Plot plot;
	
	/**
	 * Called when a player leaves a plot.
	 *
	 * @param player Player that entered the plot
	 * @param plot   Plot that was entered
	 */
	public PlayerEnterPlotAbsEvent(Player player, Plot plot) {
		super(player);
		this.plot = plot;
	}

	public static HandlerList getHandlerList() {
		return handlers;
	}
	
	/**
	 * Get the plot involved.
	 *
	 * @return Plot
	 */
	public Plot getPlot() {
		return this.plot;
	}
	
	public HandlerList getHandlers() {
		return handlers;
	}

}
