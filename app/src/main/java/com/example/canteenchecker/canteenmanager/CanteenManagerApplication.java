package com.example.canteenchecker.canteenmanager;

import android.app.Application;

import com.google.firebase.FirebaseApp;
import com.google.firebase.messaging.FirebaseMessaging;

public class CanteenManagerApplication extends Application {

	private static final String FIREBASE_MESSAGING_TOPIC = "canteens";
	private static CanteenManagerApplication instance;
	public static CanteenManagerApplication getInstance() {
		return instance;
	}

	@Override
	public void onCreate() {
		super.onCreate();

		instance = this;

		FirebaseApp.initializeApp(this);
		FirebaseMessaging.getInstance().subscribeToTopic(FIREBASE_MESSAGING_TOPIC);
	}

	private String authenticationToken;
	public synchronized String getAuthenticationToken() {
		return authenticationToken;
	}

	public synchronized void setAuthenticationToken(String authenticationToken) {
		this.authenticationToken = authenticationToken;
	}

	public synchronized boolean isAuthenticated() {
		return getAuthenticationToken() != null;
	}
}
