package com.example.canteenchecker.canteenmanager.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.TextView;

import com.example.canteenchecker.canteenmanager.R;

public class MainActivity extends AppCompatActivity {

	private static final String TAG = MainActivity.class.toString();
	private TextView mTextMessage;
	private ActionBar toolbar;


	public static Intent createIntent(Context context) {
		return new Intent(context, MainActivity.class);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

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
			case R.id.navigation_ratings:
				toolbar.setTitle(R.string.title_ratings);
				fragment = new RatingsFragment();
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
