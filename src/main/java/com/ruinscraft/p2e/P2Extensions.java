package com.ruinscraft.p2e;

import java.util.ArrayList;
import java.util.Collection;

import org.bukkit.plugin.java.JavaPlugin;

import com.ruinscraft.p2e.biomeauto.BiomeAutoExtension;
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
	
	private Collection<Extension> extensions;
	
	@Override
	public void onEnable() {
		
		p2Extensions = this;
		extensions = new ArrayList<>();
		
		saveDefaultConfig();
		
		if (isEnabled("data-bukkit")) {
			if (DataBukkitExtension.getDataBukkit().enable()) {
				P2Util.log("Data-Bukkit enabled");
				extensions.add(DataBukkitExtension.getDataBukkit());
			}
		}
		
		if (isEnabled("plot-border")) {
			if (PlotBorderExtension.getPlotBorder().enable()) {
				P2Util.log("Plot-Border enabled");
				extensions.add(PlotBorderExtension.getPlotBorder());
			}
		}
		
		if (isEnabled("timed-claims")) {
			if (TimedClaimsExtension.getTimedClaims().enable()) {
				P2Util.log("Timed-Claims enabled");
				extensions.add(TimedClaimsExtension.getTimedClaims());
			}
		}
		
		if (isEnabled("biome-auto")) {
			if (BiomeAutoExtension.getBiomeAuto().enable()) {
				P2Util.log("Biome-Auto enabled");
				extensions.add(BiomeAutoExtension.getBiomeAuto());
			}
		}
		
	}
	
	@Override
	public void onDisable() {
		p2Extensions = null;
		for (Extension extension : extensions) {
			extension.disable();
		}
	}
	
	public static P2Extensions getInstance() {
		return p2Extensions;
	}
	
	public static boolean isEnabled(String path) {
		return p2Extensions.getConfig().getBoolean(path + ".isEnabled");
	}
	
}
