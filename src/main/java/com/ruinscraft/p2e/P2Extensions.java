package com.ruinscraft.p2e;

import java.util.HashSet;
import java.util.Set;

import org.bukkit.plugin.java.JavaPlugin;

import com.ruinscraft.p2e.extensions.Timer;

public class P2Extensions extends JavaPlugin {

	private final Set<P2Extension> extensions = new HashSet<>();
	
	@Override
	public void onEnable() {
		
		registerExtension(new Timer());
		
		for (P2Extension extension : extensions) {
			extension.enable();
		}
		
	}
	
	@Override
	public void onDisable() {
		for (P2Extension extension : extensions) {
			extension.disable();
		}
		extensions.clear();
	}
	
	public void registerExtension(P2Extension extension) {
		extensions.add(extension);
		extension.enable();
	}
	
	public void unregisterExtension(P2Extension extension) {
		extensions.remove(extension);
		extension.disable();
	}
	
	public P2Extension getExtension(String name) {
		for (P2Extension extension : extensions) {
			if (extension.getName().equals(name)) {
				return extension;
			}
		}
		return null;
	}
	
}
