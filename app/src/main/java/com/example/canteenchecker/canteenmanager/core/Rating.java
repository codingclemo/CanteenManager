package com.example.canteenchecker.canteenmanager.core;


public class Rating {

	private final int ratingId;
	private final String username;
	private final String remark;
	private final int ratingPoints;
	private final long timestamp;

	public Rating(int ratingId, int ratingPoints, String username, long timestamp, String remark) {
		this.ratingId = ratingId;
		this.ratingPoints = ratingPoints;
		this.username = username;
		this.timestamp = timestamp;
		this.remark = remark;
	}

	public int getRatingId() {
		return ratingId;
	}

	public String getUsername() {
		return username;
	}

	public String getRemark() {
		return remark;
	}

	public int getRatingPoints() {
		return ratingPoints;
	}

	public long getTimestamp() {
		return timestamp;
	}


}
