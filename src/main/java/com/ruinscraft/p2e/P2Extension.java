package com.ruinscraft.p2e;

import java.util.logging.Level;

public interface P2Extension {
	
	String getName();
	
	void enable();
	
	void disable();
	
	void runAsync(Runnable runnable);
	
	void runSync(Runnable runnable);
	
	void log(Level level, String message);
	
}
