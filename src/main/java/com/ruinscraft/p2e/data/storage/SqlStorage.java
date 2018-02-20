package com.ruinscraft.p2e.data.storage;

import java.util.UUID;

import com.ruinscraft.p2e.data.RuinscraftPlayer;
import com.ruinscraft.p2e.data.RuinscraftPlayerMetaEntry;

public interface SqlStorage {
	
	void checkTables();
	
	RuinscraftPlayer getPlayer(UUID uuid);
	
	String getLastUsername(long ruinscraftPlayerId);
	
	void loadMeta(RuinscraftPlayer ruinscraftPlayer, String server);
	
	void saveMeta(long ruinscraftPlayerId, String server, String key, RuinscraftPlayerMetaEntry meta);
	
	SqlConnectionProvider getProvider();
	
}
