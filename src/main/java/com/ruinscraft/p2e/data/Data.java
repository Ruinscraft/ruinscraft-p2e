package com.ruinscraft.p2e.data;

import com.ruinscraft.p2e.data.storage.SqlStorage;

public interface Data {

	SqlStorage getSqlStorage();
	
	RuinscraftPlayerManager getRuinscraftPlayerManager();
	
}
