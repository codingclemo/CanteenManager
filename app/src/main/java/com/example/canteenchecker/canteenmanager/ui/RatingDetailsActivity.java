package com.example.canteenchecker.canteenmanager.ui;

import android.content.Context;
import android.content.Intent;
import android.graphics.Movie;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.RatingBar;
import android.widget.TextView;

import com.example.canteenchecker.canteenmanager.R;
import com.example.canteenchecker.canteenmanager.core.Rating;

import java.text.SimpleDateFormat;
import java.util.Date;

public class RatingDetailsActivity extends AppCompatActivity {

	private static final String RATING_ID_KEY = "RatingId";
	private static final String RATING = "Rating";
	private static Rating rating = null;

	/*
	public static Intent createIntent(Context context, int ratingId) {
		Intent intent = new Intent(context, RatingDetailsActivity.class);
		intent.putExtra(RATING_ID_KEY, ratingId);
		return intent;
	}
*/
	public static Intent createIntent(Context context, Rating rating) {
		Intent intent = new Intent(context, RatingDetailsActivity.class);
		intent.putExtra(RATING, rating);
		return intent;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);


		setContentView(R.layout.activity_rating_details);


		rating = (Rating) getIntent().getSerializableExtra(RATING);

		final TextView txvUserName = findViewById(R.id.txvUserName);
		final TextView txvRemark = findViewById(R.id.txvRemark);
		final RatingBar rtbAverageRating = findViewById(R.id.rtbAverageRating);
		final TextView txvDate = findViewById(R.id.txvDate);

		txvUserName.setText(rating.getUsername());
		txvRemark.setText(rating.getRemark());
		rtbAverageRating.setRating(rating.getRatingPoints());
		String dateString = new SimpleDateFormat(getResources().getString(R.string.format_date)).format(new Date(rating.getTimestamp()));
		txvDate.setText(dateString);
	}
}
