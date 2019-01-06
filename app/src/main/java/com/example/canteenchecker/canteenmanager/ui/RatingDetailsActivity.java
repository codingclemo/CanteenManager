package com.example.canteenchecker.canteenmanager.ui;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.canteenchecker.canteenmanager.R;

public class RatingDetailsActivity extends AppCompatActivity {

	private static final String RATING_ID_KEY = "RatingId";

	public static Intent createIntent(Context context, int ratingId) {
		Intent intent = new Intent(context, RatingDetailsActivity.class);
		intent.putExtra(RATING_ID_KEY, ratingId);
		return intent;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_rating_details);
	}
}
