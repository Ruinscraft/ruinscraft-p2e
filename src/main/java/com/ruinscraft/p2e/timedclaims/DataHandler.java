package com.ruinscraft.p2e.timedclaims;

import com.intellectualcrafters.plot.object.PlotPlayer;
import com.intellectualcrafters.plot.util.ByteArrayUtilities;
import com.ruinscraft.p2e.P2Extensions;

public class DataHandler {
	
	private static P2Extensions instance = P2Extensions.getInstance();

	public static void setPlayerYaml(PlotPlayer player) {
		
		if (player.getPersistentMeta("claim-time") == null) {

			player.setPersistentMeta("time-online", ByteArrayUtilities.integerToBytes(0));
			player.setPersistentMeta("plots-given", ByteArrayUtilities.integerToBytes(0));
			
			if (instance.getConfig().getBoolean("first-time-claim")) {
				player.setPersistentMeta("claim-time", ByteArrayUtilities.integerToBytes(0));
			} else {
				player.setPersistentMeta("claim-time", ByteArrayUtilities.integerToBytes((int) System.currentTimeMillis() % Integer.MAX_VALUE));
			}
			
		}
		
	}
	
	public static Integer getClaimTime(PlotPlayer player) {
		return ByteArrayUtilities.bytesToInteger(player.getPersistentMeta("claim-time"));
	}
	
	public static void setClaimTime(PlotPlayer player, int claimTime) {
		player.setPersistentMeta("claim-time", ByteArrayUtilities.integerToBytes(claimTime));
	}
	
	public static Integer getTimeOnline(PlotPlayer player) {
		return ByteArrayUtilities.bytesToInteger(player.getPersistentMeta("time-online"));
	}
	
	public static void setTimeOnline(PlotPlayer player, int timeOnline) {
		player.setPersistentMeta("time-online", ByteArrayUtilities.integerToBytes(timeOnline));
	}
	
	public static Integer getPlotsGiven(PlotPlayer player) {
		return ByteArrayUtilities.bytesToInteger(player.getPersistentMeta("plots-given"));
	}
	
	public static void setPlotsGiven(PlotPlayer player, int plotsGiven) {
		player.setPersistentMeta("plots-given", ByteArrayUtilities.integerToBytes(plotsGiven));
	}
	
}