package com.ruinscraft.p2e;

import java.util.UUID;

public interface MetaStorage {

	void saveMeta(UUID player, String key, byte[] value);
	
	byte[] getMeta(UUID player, String key);
	
}
