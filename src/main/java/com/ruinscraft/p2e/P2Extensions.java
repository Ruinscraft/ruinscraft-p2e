package com.ruinscraft.p2e;

import java.util.ArrayList;
import java.util.Collection;

import org.bukkit.plugin.java.JavaPlugin;

import com.ruinscraft.p2e.biomeauto.BiomeAutoExtension;
import com.ruinscraft.p2e.data.DataBukkitExtension;
import com.ruinscraft.p2e.plotborder.PlotBorderExtension;
import com.ruinscraft.p2e.plotmap.PlotMapExtension;
import com.ruinscraft.p2e.timedclaims.TimedClaimsExtension;

public class P2Extensions extends JavaPlugin {
	
	private static P2Extensions p2Extensions;
	
	private Collection<P2Extension> extensions;
	
	@Override
	public void onEnable() {
		
		p2Extensions = this;
		extensions = new ArrayList<>();
		
		saveDefaultConfig();
		
		registerExtension(new DataBukkitExtension());
		registerExtension(new PlotMapExtension());
		registerExtension(new PlotBorderExtension());
		registerExtension(new BiomeAutoExtension());
		registerExtension(new TimedClaimsExtension());
		
	}
	
	@Override
	public void onDisable() {
		
		for (P2Extension extension : extensions) {
			extension.disable();
		}
		extensions.clear();
		p2Extensions = null;
		
	}
	
	public void registerExtension(P2Extension extension) {
		
		if (isEnabled(extension.getName())) {
			if (extension.enable()) {
				extensions.add(extension);
				getLogger().info(extension.getName() + " enabled!"); 
			} else {
				getLogger().info(extension.getName() + " disabled!"); 
			}
		}
		
	}
	
	public void unregisterExtension(P2Extension extension) {
		
		if (extension.disable()) {
			extensions.remove(extension);
			getLogger().info(extension.getName() + " enabled!"); 
		}
		
	}
	
	public static boolean isEnabled(String path) {
		return p2Extensions.getConfig().getBoolean(path.toLowerCase() + ".isEnabled");
	}
	
	public static P2Extensions getInstance() {
		return p2Extensions;
	}
	
}
