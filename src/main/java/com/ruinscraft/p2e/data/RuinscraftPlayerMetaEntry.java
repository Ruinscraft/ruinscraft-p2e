package com.ruinscraft.p2e.data;

public class RuinscraftPlayerMetaEntry {

	private long lastUpdated;
	private Object value;
	
	public RuinscraftPlayerMetaEntry() {

	}
	
	public RuinscraftPlayerMetaEntry(Object value) {
		this.lastUpdated = System.currentTimeMillis();
		this.value = value;
	}

	public long getLastUpdated() {
		return lastUpdated;
	}

	public void setLastUpdated(long lastUpdated) {
		this.lastUpdated = lastUpdated;
	}

	public Object getValue() {
		return value;
	}

	public void setValue(Object value) {
		this.value = value;
	}

}
