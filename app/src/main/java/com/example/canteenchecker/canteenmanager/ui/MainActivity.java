package com.example.canteenchecker.canteenmanager.ui;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;

import com.example.canteenchecker.canteenmanager.CanteenManagerApplication;
import com.example.canteenchecker.canteenmanager.R;
import com.example.canteenchecker.canteenmanager.ReviewsFragment;
import com.example.canteenchecker.canteenmanager.core.Canteen;
import com.example.canteenchecker.canteenmanager.core.ReviewData;
import com.example.canteenchecker.canteenmanager.proxy.ServiceProxy;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {

	private static final String TAG = MainActivity.class.toString();

	private TextView mTextMessage;
	private ActionBar toolbar;

	private static final int LOGIN_FOR_REVIEW_CREATION = 42;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);


		if(!CanteenManagerApplication.getInstance().isAuthenticated()) {
			startActivityForResult(LoginActivity.createIntent(this), LOGIN_FOR_REVIEW_CREATION);
		}

		setContentView(R.layout.activity_main);

		mTextMessage = (TextView) findViewById(R.id.message);
		BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
		navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

		toolbar = getSupportActionBar();

		toolbar.setTitle(R.string.title_details);

		loadFragment(new DetailsFragment());

	}

	private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
			= new BottomNavigationView.OnNavigationItemSelectedListener() {

		@Override
		public boolean onNavigationItemSelected(@NonNull MenuItem item) {
			Fragment fragment;
			switch (item.getItemId()) {
			case R.id.navigation_details:
				toolbar.setTitle(R.string.title_details);
				fragment = new DetailsFragment();
				loadFragment(fragment);
				return true;
			case R.id.navigation_comments:
				toolbar.setTitle(R.string.title_comments);
				fragment = new CommentsFragment();
				loadFragment(fragment);
				return true;
			}
			return false;
		}
	};

	private void loadFragment(Fragment fragment) {
		// load fragment
		FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
		transaction.replace(R.id.frame_container, fragment);
		transaction.addToBackStack(null);
		transaction.commit();
	}



}
