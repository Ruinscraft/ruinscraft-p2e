package com.ruinscraft.p2e.timedclaims;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import com.intellectualcrafters.plot.object.PlotPlayer;
import com.ruinscraft.p2e.P2Util;

public class PlayerJoinListener implements Listener {
	
	@EventHandler(priority = EventPriority.NORMAL)
	public void onJoin(PlayerJoinEvent event) {
		
		Player player = event.getPlayer();
		TimedClaimsExtension.getMenuHandler().setLoginTime(player.getName(), System.currentTimeMillis());
		
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onLeave(PlayerQuitEvent event) {
		
		PlotPlayer player = P2Util.getPlayer(event.getPlayer());
		
		long time = TimedClaimsExtension.getMenuHandler().getLoginTime().get(player.getName());
		long online = System.currentTimeMillis() - time;
		
		int timealready = DataHandler.getTimeOnline(player);
		int newtime = (int) ((timealready + online) % Integer.MAX_VALUE);
		DataHandler.setTimeOnline(player, newtime);
		
	}
	
}