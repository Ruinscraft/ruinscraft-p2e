package com.ruinscraft.p2e.data;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentLinkedQueue;

public class RuinscraftPlayerManager {

	private Map<Long, RuinscraftPlayer> ruinscraftPlayers;

	private ConcurrentLinkedQueue<RuinscraftPlayer> playersToPersist;

	public RuinscraftPlayerManager() {
		ruinscraftPlayers = new HashMap<Long, RuinscraftPlayer>();
		playersToPersist = new ConcurrentLinkedQueue<RuinscraftPlayer>();
	}

	public void load(RuinscraftPlayer ruinscraftPlayer) {
		ruinscraftPlayers.putIfAbsent(ruinscraftPlayer.getId(), ruinscraftPlayer);
	}

	public void unload(long id) {
		ruinscraftPlayers.remove(id);
	}

	public RuinscraftPlayer get(long id) {

		return ruinscraftPlayers.get(id);

	}

	public RuinscraftPlayer get(UUID uuid) {

		for (RuinscraftPlayer ruinscraftPlayer : ruinscraftPlayers.values()) {

			if (ruinscraftPlayer.getMojangUuid().equals(uuid)) {
				return ruinscraftPlayer;
			}

		}

		return null;

	}

	public void addToPersist(RuinscraftPlayer ruinscraftPlayer) {
		playersToPersist.add(ruinscraftPlayer);
	}

	public void clearPersist() {
		playersToPersist.clear();
	}

	public ConcurrentLinkedQueue<RuinscraftPlayer> getToPersist() {
		return playersToPersist;
	}

}