package com.ruinscraft.p2e;

import org.bukkit.plugin.java.JavaPlugin;

import com.ruinscraft.p2e.data.DataBukkitExtension;
import com.ruinscraft.p2e.plotborder.PlotBorderExtension;
import com.ruinscraft.p2e.timedclaims.TimedClaimsExtension;

public class P2Extensions extends JavaPlugin {
	
	// data-bukkit +
	// countdown
	// plotsay
	// giveplot
	// plot-map +
	// plot-border +
	// biome-finder
	// timed-claims +
	
	/*/
	 * TODO
	 * create config & standardize
	 * move playerdata made by timed-claims to data-bukkit
	 */
	
	private static P2Extensions p2Extensions;
	
	@Override
	public void onEnable() {
		
		p2Extensions = this;
		
		saveDefaultConfig();
		
		if (isEnabled("data-bukkit")) {
			DataBukkitExtension.getDataBukkit().enable();
			P2Util.log("Data-Bukkit enabled");
		}
		
		if (isEnabled("plot-border")) {
			PlotBorderExtension.getPlotBorder().enable();
			P2Util.log("Plot-Border enabled");
		}
		
		if (isEnabled("timed-claims")) {
			TimedClaimsExtension.getTimedClaims().enable();
			P2Util.log("Timed-Claims enabled");
		}
		
	}
	
	@Override
	public void onDisable() {
		p2Extensions = null;
	}
	
	public static P2Extensions getInstance() {
		return p2Extensions;
	}
	
	public static boolean isEnabled(String path) {
		return p2Extensions.getConfig().getBoolean(path + ".isEnabled");
	}
	
}
