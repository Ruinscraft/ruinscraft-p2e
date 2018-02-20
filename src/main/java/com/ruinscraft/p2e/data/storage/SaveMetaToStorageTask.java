package com.ruinscraft.p2e.data.storage;

import com.ruinscraft.p2e.data.RuinscraftPlayer;
import com.ruinscraft.p2e.data.RuinscraftPlayerManager;

public class SaveMetaToStorageTask implements Runnable {

	private final RuinscraftPlayerManager ruinscraftPlayerManager;
	private final SqlStorage sqlStorage;
	private volatile boolean cancel;

	public SaveMetaToStorageTask(RuinscraftPlayerManager ruinscraftPlayerManager, SqlStorage sqlStorage) {
		this.ruinscraftPlayerManager = ruinscraftPlayerManager;
		this.sqlStorage = sqlStorage;
		cancel = false;
	}

	public void run() {
		
		if (cancel) {
			return;
		}
		
		for (RuinscraftPlayer ruinscraftPlayer : ruinscraftPlayerManager.getToPersist()) {
			
			for (String server : ruinscraftPlayer.getMetaMap().keySet()) {

				for (String key : ruinscraftPlayer.getMetaMap().get(server).keySet()) {

					sqlStorage.saveMeta(ruinscraftPlayer.getId(), server, key, ruinscraftPlayer.getMetaMap().get(server).get(key));

				}

			}

		}
		
		ruinscraftPlayerManager.clearPersist();

	}
	
	public void cancel() {
		cancel = true;
	}

}