package com.ruinscraft.p2e;

import com.intellectualcrafters.plot.commands.SubCommand;

public interface P2Extension {
	
	boolean enable();
	
	boolean disable();
	
	String getName();
	
	SubCommand getP2SubCommand();
	
}
