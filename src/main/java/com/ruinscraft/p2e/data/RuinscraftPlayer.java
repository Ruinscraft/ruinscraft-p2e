package com.ruinscraft.p2e.data;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class RuinscraftPlayer {

	private long id;
	private UUID mojangUuid;
	private Map<String, Map<String, RuinscraftPlayerMetaEntry>> meta;

	public RuinscraftPlayer() {
		meta = new ConcurrentHashMap<String, Map<String, RuinscraftPlayerMetaEntry>>();
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public UUID getMojangUuid() {
		return mojangUuid;
	}

	public void setMojangUuid(UUID mojangUuid) {
		this.mojangUuid = mojangUuid;
	}

	public Object getMetaEntry(String server, String key, Object defaultValue) {
		
		if (!meta.containsKey(server)) {
			return defaultValue;
		}
		
		if (!meta.get(server).containsKey(key)) {
			return defaultValue;
		}
		
		if (meta.get(server).get(key) == null) {
			return defaultValue;
		}
		
		return meta.get(server).get(key).getValue();
		
	}
	
	public String getMetaEntryString(String server, String key, String defaultValue) {
		return getMetaEntry(server, key, defaultValue).toString();
	}
	
	public Boolean getMetaEntryBoolean(String server, String key, Boolean defaultValue) {
		
		if (getMetaEntryString(server, key, Boolean.toString(defaultValue)).equalsIgnoreCase("true")) {
			return true;
		}
		
		else if (getMetaEntryString(server, key, Boolean.toString(defaultValue)).equalsIgnoreCase("1")) {
			return true;
		}
		
		else if (getMetaEntryString(server, key, Boolean.toString(defaultValue)).equalsIgnoreCase("false")) {
			return false;
		}
		
		else if (getMetaEntryString(server, key, Boolean.toString(defaultValue)).equalsIgnoreCase("0")) {
			return false;
		} 
		
		else {
			return defaultValue;
		}
		
	}
	
	public Character getMetaEntryCharacter(String server, String key, Character defaultValue) {
		return getMetaEntryString(server, key, defaultValue.toString()).charAt(0);
	}
	
	public Long getMetaEntryLong(String server, String key, Long defaultValue) {
		return Long.parseLong(getMetaEntryString(server, key, Long.toString(defaultValue)));
	}

	public void setMetaAndSave(String server, String key, RuinscraftPlayerMetaEntry rpme, RuinscraftPlayerManager rpm) {
		
		setMeta(server, key, rpme);
		
		rpm.addToPersist(this);
		
	}
	
	public void setMeta(String server, String key, RuinscraftPlayerMetaEntry rpme) {
		
		if (!meta.containsKey(server)) {
			
			meta.put(server, new HashMap<String, RuinscraftPlayerMetaEntry>());
			
		}
		
		meta.get(server).put(key, rpme);
		
	}
	
	public Map<String, Map<String, RuinscraftPlayerMetaEntry>> getMetaMap() {
		return meta;
	}
	
}
