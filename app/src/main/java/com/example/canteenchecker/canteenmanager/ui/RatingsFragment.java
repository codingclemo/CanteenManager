package com.example.canteenchecker.canteenmanager.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import com.example.canteenchecker.canteenmanager.CanteenManagerApplication;
import com.example.canteenchecker.canteenmanager.R;
import com.example.canteenchecker.canteenmanager.core.Canteen;
import com.example.canteenchecker.canteenmanager.core.Rating;
import com.example.canteenchecker.canteenmanager.proxy.ServiceProxy;
import com.example.canteenchecker.canteenmanager.service.MyFirebaseMessagingService;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

public class RatingsFragment extends Fragment {

	private static final String TAG = RatingsFragment.class.toString();
	private final RatingsAdapter ratingsAdapter = new RatingsAdapter();
	private SwipeRefreshLayout srlRatings;
	private final BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			updateRatings();
		}
	};

	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_ratings, null);
	}


	@Override
	public void onActivityCreated(@Nullable Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		RecyclerView rcvRatings = getView().findViewById(R.id.rcvCanteenReviews);
		final FragmentActivity thisActivity = getActivity();

		rcvRatings.setLayoutManager(new LinearLayoutManager(thisActivity));
		rcvRatings.setAdapter(ratingsAdapter);

		srlRatings = getView().findViewById(R.id.srlCanteenReviews);
		srlRatings.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
			@Override
			public void onRefresh() {
				updateRatings();
			}
		});

		LocalBroadcastManager.getInstance(thisActivity).registerReceiver(broadcastReceiver, MyFirebaseMessagingService
				.canteenChangedIntentFilter());

		insertNestedFragment();
		updateRatings();
	}

	private void insertNestedFragment() {
		Fragment childFragment = new RatingStatisticsFragment();
		FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
		transaction.replace(R.id.child_fragment_reviews, childFragment).commit();
	}

	@Override
	public void onResume() {
		super.onResume();
		updateRatings();
	}

	private void updateRatings() {
		srlRatings.setRefreshing(true);

		new AsyncTask<Void, Void, Canteen>() {
			// läuft asyncron (kein Zugriff auf UI-Thread)
			@Override
			protected Canteen doInBackground(Void... params) {
				try {
					String token = CanteenManagerApplication.getInstance().getAuthenticationToken();
					Log.v(TAG, String.format("Downloading ratings"));
					Canteen canteen = new ServiceProxy().getRatings(token);
					return canteen;
				} catch (IOException e) {
					Log.e(TAG, String.format("Downloading of ratings failed", e));
					return null;
				}
			}

			// läuft nach dem die Daten geholt wurden asyncron und werden hier in den UI-Thread geschrieben!
			@Override
			protected void onPostExecute(Canteen canteen) {
				Collection<Rating> ratings = canteen.getRatings();
				ratingsAdapter.displayRatings(ratings);
				srlRatings.setRefreshing(false);
			}
		}.execute();
	}

	private static class RatingsAdapter extends RecyclerView.Adapter<RatingsAdapter.ViewHolder> {
		static class ViewHolder extends RecyclerView.ViewHolder {
			private final TextView txvUserName = itemView.findViewById(R.id.txvUserName);
			private final TextView txvRemark = itemView.findViewById(R.id.txvRemark);
			private final RatingBar rtbAverageRating = itemView.findViewById(R.id.rtbAverageRating);
			private final TextView txvDate = itemView.findViewById(R.id.txvDate);

			public ViewHolder(View itemView) {
				super(itemView);
			}
		}

		@Override
		public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
			View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_rating_item, parent, false);
			return new ViewHolder(view);
		}

		@Override
		public void onBindViewHolder(final RatingsAdapter.ViewHolder holder, int position) {
			final Rating r = ratingList.get(position);

			holder.txvUserName.setText(r.getUsername());
			holder.txvRemark.setText(r.getRemark());
			holder.rtbAverageRating.setRating(r.getRatingPoints());

			String dateString = new SimpleDateFormat("MM/dd/yyyy").format(new Date(r.getTimestamp()));
			holder.txvDate.setText(dateString);
			holder.itemView.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					Context context = holder.itemView.getContext();
					context.startActivity(RatingDetailsActivity.createIntent(context, r));
				}
			});
		}

		private final List<Rating> ratingList = new ArrayList<>();

		void displayRatings(Collection<Rating> ratings) {
			ratingList.clear();
			if(ratings != null) {
				ratingList.addAll(ratings);
			}
			notifyDataSetChanged();
		}

		@Override
		public int getItemCount() {
			return ratingList.size();
		}
	}
}
