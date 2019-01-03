package com.example.canteenchecker.canteenmanager.core;

public class Canteen {

	private final String id;
	private final String name;
	private final String phoneNumber;
	private final String website;
	private final String setMeal;
	private final float setMealPrice;
	private final float averageRating;
	private final String location;
	private final int averageWaitingTime;

	public Canteen(String id, String name, String phoneNumber, String website, String setMeal, float setMealPrice, float averageRating, String location, int averageWaitingTime) {
		this.id = id;
		this.name = name;
		this.phoneNumber = phoneNumber;
		this.website = website;
		this.setMeal = setMeal;
		this.setMealPrice = setMealPrice;
		this.averageRating = averageRating;
		this.location = location;
		this.averageWaitingTime = averageWaitingTime;
	}

	public String getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public String getWebsite() {
		return website;
	}

	public String getSetMeal() {
		return setMeal;
	}

	public float getSetMealPrice() {
		return setMealPrice;
	}

	public float getAverageRating() {
		return averageRating;
	}

	public String getLocation() {
		return location;
	}

	public int getAverageWaitingTime() {
		return averageWaitingTime;
	}

}
