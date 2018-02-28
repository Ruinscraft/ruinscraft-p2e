package com.ruinscraft.p2e.timedclaims;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import com.intellectualcrafters.plot.object.PlotPlayer;
import com.intellectualcrafters.plot.util.ByteArrayUtilities;
import com.ruinscraft.p2e.P2Extensions;
import com.ruinscraft.p2e.P2Util;

public class PlayerJoinListener implements Listener {
	
	@EventHandler(priority = EventPriority.NORMAL)
	public void onJoin(PlayerJoinEvent event) {
		
		PlotPlayer player = P2Util.getPlayer(event.getPlayer());
		createPlayerMeta(player);
		
		setMeta(player);
		
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onLeave(PlayerQuitEvent event) {
		
		PlotPlayer player = P2Util.getPlayer(event.getPlayer());
		
		long time = (int) player.getMeta(TimeMeta.CLAIM_TIME);
		long online = System.currentTimeMillis() - time;
		
		int timealready = (int) player.getMeta(TimeMeta.TIME_ONLINE);
		int newtime = (int) ((timealready + online) % Integer.MAX_VALUE);
		player.setPersistentMeta(TimeMeta.TIME_ONLINE, ByteArrayUtilities.integerToBytes(newtime));
		player.setPersistentMeta(TimeMeta.CLAIM_TIME, ByteArrayUtilities.integerToBytes(player.getMeta(TimeMeta.CLAIM_TIME)));
		player.setPersistentMeta(TimeMeta.PLOTS_GIVEN, ByteArrayUtilities.integerToBytes(player.getMeta(TimeMeta.PLOTS_GIVEN)));
		
	}
	
	public void createPlayerMeta(PlotPlayer player) {
		
		if (player.getPersistentMeta(TimeMeta.CLAIM_TIME) == null) {

			player.setPersistentMeta(TimeMeta.TIME_ONLINE, ByteArrayUtilities.integerToBytes(0));
			player.setPersistentMeta(TimeMeta.PLOTS_GIVEN, ByteArrayUtilities.integerToBytes(0));
			
			if (P2Extensions.getInstance().getConfig().getBoolean("first-time-claim")) {
				player.setPersistentMeta(TimeMeta.CLAIM_TIME, ByteArrayUtilities.integerToBytes(0));
			} else {
				player.setPersistentMeta(TimeMeta.CLAIM_TIME, ByteArrayUtilities.integerToBytes((int) System.currentTimeMillis() % Integer.MAX_VALUE));
			}
			
			setMeta(player);
			
		}
		
	}
	
	public void setMeta(PlotPlayer player) {
		player.setMeta(TimeMeta.TIME_ONLINE, ByteArrayUtilities.bytesToInteger(player.getPersistentMeta(TimeMeta.TIME_ONLINE)));
		player.setMeta(TimeMeta.PLOTS_GIVEN, ByteArrayUtilities.bytesToInteger(player.getPersistentMeta(TimeMeta.PLOTS_GIVEN)));
		player.setMeta(TimeMeta.CLAIM_TIME, ByteArrayUtilities.bytesToInteger(player.getPersistentMeta(TimeMeta.CLAIM_TIME)));
	}
	
}