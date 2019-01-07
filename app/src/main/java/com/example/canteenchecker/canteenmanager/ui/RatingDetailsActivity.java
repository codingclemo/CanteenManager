package com.example.canteenchecker.canteenmanager.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Movie;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.canteenchecker.canteenmanager.CanteenManagerApplication;
import com.example.canteenchecker.canteenmanager.R;
import com.example.canteenchecker.canteenmanager.core.Rating;
import com.example.canteenchecker.canteenmanager.proxy.ServiceProxy;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class RatingDetailsActivity extends AppCompatActivity {

	private static final String RATING_ID_KEY = "RatingId";
	private static final String RATING = "Rating";
	private static Rating rating = null;
	private static final String TAG = RatingDetailsActivity.class.toString();

	public static Intent createIntent(Context context, Rating rating) {
		Intent intent = new Intent(context, RatingDetailsActivity.class);
		intent.putExtra(RATING, rating);
		return intent;
	}


	private Button btnDelete;
	private TextView txvRatingId;
	private TextView txvUserName;
	private TextView txvRemark;
	private RatingBar rtbAverageRating;
	private TextView txvDate;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_rating_details);

		rating = (Rating) getIntent().getSerializableExtra(RATING);
		txvRatingId = findViewById(R.id.txvRatingId);
		txvUserName = findViewById(R.id.txvUserName);
		txvRemark = findViewById(R.id.txvRemark);
		rtbAverageRating = findViewById(R.id.rtbAverageRating);
		txvDate = findViewById(R.id.txvDate);
		btnDelete = findViewById(R.id.btnDelete);
		btnDelete.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				deleteRating();
			}
		});

		txvRatingId.setText("#" + rating.getRatingId());
		txvUserName.setText(rating.getUsername());
		txvRemark.setText(rating.getRemark());
		rtbAverageRating.setRating(rating.getRatingPoints());
		String dateString = new SimpleDateFormat(getResources().getString(R.string.format_date)).format(new Date(rating.getTimestamp()));
		txvDate.setText(dateString);
	}

	private void deleteRating() {
		setEnabled(false);

		new AsyncTask<Integer, Void, Boolean>() {
			@Override
			protected Boolean doInBackground(Integer... id) {

				try {
					//return new ServiceProxy().authenticate(strings[0], strings[1]);
					String token = CanteenManagerApplication.getInstance().getAuthenticationToken();
					return Boolean.valueOf(new ServiceProxy().deleteRating(token, id[0]));
				} catch (IOException e) {
					Log.e(TAG, "Deletion failed!", e);
					return null;
				}

			}

			//TODO: test this

			@Override
			protected void onPostExecute(Boolean b) {
				Toast.makeText(RatingDetailsActivity.this, b.booleanValue() ? "Rating deleted" : "Deletion failed", Toast.LENGTH_LONG)
						.show();

				if (!b.booleanValue()) {
					setEnabled(true);
				} else {
					finish();
				}
			}
		}.execute(rating.getRatingId());
	}

	private void setEnabled(boolean enabled) {
		btnDelete.setEnabled(enabled);
	}
}
