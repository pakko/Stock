package com.ml.model;

import org.springframework.data.mongodb.core.index.Indexed;

public class StatsResult {
	private String stats;
	@Indexed(unique = true)
	private long date;
	
	public StatsResult(String stats, long date) {
		super();
		this.stats = stats;
		this.date = date;
	}

	public String getStats() {
		return stats;
	}

	public void setStats(String stats) {
		this.stats = stats;
	}

	public long getDate() {
		return date;
	}

	public void setDate(long date) {
		this.date = date;
	}

	@Override
	public String toString() {
		return "StatsResult [stats=" + stats + ", date=" + date + "]";
	}
	
	
	
	
	
}
