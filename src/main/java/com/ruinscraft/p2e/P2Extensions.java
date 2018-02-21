package com.ruinscraft.p2e;

import org.bukkit.plugin.java.JavaPlugin;

public class P2Extensions extends JavaPlugin {
	
	private static P2Extensions p2Extensions;
	
	@Override
	public void onEnable() {
		
		p2Extensions = this;
		
		saveDefaultConfig();
		
	}
	
	@Override
	public void onDisable() {
		p2Extensions = null;
	}
	
	public static P2Extensions getInstance() {
		return p2Extensions;
	}
	
}
