package com.example.canteenchecker.canteenmanager.ui;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.example.canteenchecker.canteenmanager.R;

public class DetailsFragment extends Fragment {



	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		//just change the fragment_dashboard
		//with the fragment you want to inflate
		//like if the class is HomeFragment it should have R.layout.home_fragment
		//if it is DashboardFragment it should have R.layout.fragment_dashboard

		return inflater.inflate(R.layout.fragment_details, null);
	}

	@Override
	public void onActivityCreated(@Nullable Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		setValues();
	}

	SeekBar.OnSeekBarChangeListener seekBarChangeListener = new SeekBar.OnSeekBarChangeListener() {

		@Override
		public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
			// updated continuously as the user slides the thumb
			txtWaitingTime.setText(getResources().getString(R.string.WaitingTime) + " " + progress);
		}

		@Override
		public void onStartTrackingTouch(SeekBar seekBar) {
			// called when the user first touches the SeekBar
		}

		@Override
		public void onStopTrackingTouch(SeekBar seekBar) {
			// called after the user finishes moving the SeekBar
		}
	};


	private String CanteenName;
	private String MenuOfTheDay;
	private Double MenuPrice;
	private String Address;
	private String WebAddress;
	private String Phone;
	//private int WaitingTime;


	EditText EdtCanteenName;
	EditText EdtMenuOfTheDay;
	EditText EdtMenuPrice;
	EditText EdtAddress;
	EditText EdtWebAddress;
	EditText EdtPhone;
	TextView txtWaitingTime;
	SeekBar SkbWaitingTime;

	private void setValues() {
		CanteenName = "Clemteen";
		MenuOfTheDay = "Lasagne al forno";
		MenuPrice = 5.90;
		Address = "Im Kreuzlandl 11, 4020 Linz";
		WebAddress = "www.sammereyer.com/clemens";
		Phone = "246234215";
		//WaitingTime = 5;


		EdtCanteenName = getView().findViewById(R.id.edtCanteenName);
		EdtMenuOfTheDay = getView().findViewById(R.id.edtDailyMenu);
		EdtMenuPrice = getView().findViewById(R.id.edtMenuPrice);
		EdtAddress = getView().findViewById(R.id.edtAddress);
		EdtWebAddress = getView().findViewById(R.id.edtWebsite);
		EdtPhone = getView().findViewById(R.id.edtPhone);
		SkbWaitingTime = getView().findViewById(R.id.skbWaitingTime);
		SkbWaitingTime.setOnSeekBarChangeListener(seekBarChangeListener);
		//SkbWaitingTime.setProgress(5);

		EdtCanteenName.setText(CanteenName);
		EdtMenuOfTheDay.setText(MenuOfTheDay);
		EdtMenuPrice.setText(MenuPrice.toString());
		EdtAddress.setText(Address);
		EdtWebAddress.setText(WebAddress);
		EdtPhone.setText(Phone);

		int WaitingTime = SkbWaitingTime.getProgress();
		txtWaitingTime = getView().findViewById(R.id.txtWaitingTime);
		txtWaitingTime.setText(getResources().getString(R.string.WaitingTime) + " " + WaitingTime);
	}
}
