package com.example.canteenchecker.canteenmanager;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.canteenchecker.canteenmanager.core.ReviewData;
import com.example.canteenchecker.canteenmanager.proxy.ServiceProxy;
import com.example.canteenchecker.canteenmanager.service.MyFirebaseMessagingService;
import com.example.canteenchecker.canteenmanager.ui.LoginActivity;

import java.io.IOException;
import java.text.NumberFormat;

public class ReviewsFragment extends Fragment {

	private static final String TAG = ReviewsFragment.class.toString();
	private static final String CANTEEN_ID_KEY = "canteenId";
	private static final int LOGIN_FOR_REIVEW_CREATION = 42;

	private final BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			String canteenId = getCanteenId();
			if(canteenId != null && canteenId.equals(MyFirebaseMessagingService.canteenChangedId(intent))) {
				updateReviews();
			}
		}
	};

	public static Fragment create(String canteenId) {
		ReviewsFragment reviewsFragment = new ReviewsFragment();
		Bundle arguments = new Bundle();
		arguments.putString(CANTEEN_ID_KEY, canteenId);
		reviewsFragment.setArguments(arguments);
		return reviewsFragment;
	}


	private TextView txvAverageRating;
	private RatingBar rtbAverageRating;
	private TextView txvTotalRatings;
	private View viwRatingOne;
	private View viwRatingTwo;
	private View viwRatingThree;
	private View viwRatingFour;
	private View viwRatingFive;

	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_reviews, container, false);
		txvAverageRating = view.findViewById(R.id.txvAverageRating);
		rtbAverageRating = view.findViewById(R.id.rtbAverageRating);
		txvTotalRatings = view.findViewById(R.id.txvTotalRatings);
		viwRatingOne = view.findViewById(R.id.viwRatingsOne);
		viwRatingTwo = view.findViewById(R.id.viwRatingsTwo);
		viwRatingThree = view.findViewById(R.id.viwRatingsThree);
		viwRatingFour = view.findViewById(R.id.viwRatingsFour);
		viwRatingFive = view.findViewById(R.id.viwRatingsFive);

		updateReviews();

		LocalBroadcastManager.getInstance(getActivity()).registerReceiver(broadcastReceiver, MyFirebaseMessagingService.canteenChangedIntentFilter());
		return view;
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();

		LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(broadcastReceiver);
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if(requestCode == LOGIN_FOR_REIVEW_CREATION && resultCode == Activity.RESULT_OK) {
			//createReview();
		}
	}

	private String getCanteenId() {
		return getArguments().getString(CANTEEN_ID_KEY);
	}

	private void updateReviews() {
		new AsyncTask<String, Void, ReviewData>() {
			@Override
			protected ReviewData doInBackground(String... strings) {
				try {
					return new ServiceProxy().getReviewsDataForCanteen(strings[0]);
				} catch(IOException e) {
					Log.e(TAG, "");
					return null;
				}
			}

			@Override
			protected void onPostExecute(ReviewData reviewData) {
				if(reviewData != null) {
					txvAverageRating.setText(NumberFormat.getNumberInstance().format(reviewData.getAverageRating()));
					txvTotalRatings.setText(NumberFormat.getNumberInstance().format(reviewData.getTotalRatings()));
					rtbAverageRating.setRating(reviewData.getAverageRating());
					setWeight(viwRatingOne, reviewData.getRatingsOne(), reviewData.getTotalRatingsOfMostCommonGrade());
					setWeight(viwRatingTwo, reviewData.getRatingsTwo(), reviewData.getTotalRatingsOfMostCommonGrade());
					setWeight(viwRatingThree, reviewData.getRatingsThree(), reviewData.getTotalRatingsOfMostCommonGrade());
					setWeight(viwRatingFour, reviewData.getRatingsFour(), reviewData.getTotalRatingsOfMostCommonGrade());
					setWeight(viwRatingFive, reviewData.getRatingsFive(), reviewData.getTotalRatingsOfMostCommonGrade());
				} else  {
					txvAverageRating.setText(null);
					txvTotalRatings.setText(null);
					rtbAverageRating.setRating(0);
					setWeight(viwRatingOne, 0, 1);
					setWeight(viwRatingTwo, 0, 1);
					setWeight(viwRatingThree, 0, 1);
					setWeight(viwRatingFour, 0, 1);
					setWeight(viwRatingFive, 0, 1);
				}
			}

			private void setWeight(View view, int value, int maximum) {
				ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
				float weight = ((float) value) / maximum;
				view.setLayoutParams(new LinearLayout.LayoutParams(layoutParams.width, layoutParams.height, weight));
			}
		}.execute(getCanteenId());
	}
/*
	private void createReview() {
		if(CanteenManagerApplication.getInstance().isAuthenticated()) {
			final View view = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_add_review, null);
			new AlertDialog.Builder(getActivity())
					.setTitle(R.string.dlgAddReview_title)
					.setView(view)
					.setPositiveButton(R.string.dlgAddReview_positiveButton, new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							dialog.dismiss();
							new AsyncTask<Object, Void, String>() {
								@Override
								protected String doInBackground(Object... objects) {
									try {
										return new ServiceProxy().createReview(
												(String) objects[0],
												(String) objects[1],
												(int) objects[2],
												(String) objects[3]
										);
									} catch (IOException e) {
										return null;
									}
								}

								@Override
								protected void onPostExecute(String s) {
									Toast.makeText(getActivity(), s != null ? getString(R.string.msg_ReviewCreated) : getString(R.string.msg_ReviewNotCreated), Toast.LENGTH_SHORT).show();
								}
							}.execute(
									CanteenManagerApplication.getInstance().getAuthenticationToken(),
									getCanteenId(),
									Math.round(((RatingBar)view.findViewById(R.id.rtbRating)).getRating()),
									((EditText)view.findViewById(R.id.edtRemark)).getText().toString()
							);
						}
					})
					.create()
					.show();
		} else {
			startActivityForResult(LoginActivity.createIntent(getActivity()), LOGIN_FOR_REIVEW_CREATION);
		}
	}
*/
}
