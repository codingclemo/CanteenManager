package com.example.canteenchecker.canteenmanager.ui;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.canteenchecker.canteenmanager.CanteenManagerApplication;
import com.example.canteenchecker.canteenmanager.R;
import com.example.canteenchecker.canteenmanager.core.Canteen;
import com.example.canteenchecker.canteenmanager.proxy.ServiceProxy;

import java.io.IOException;
import java.text.NumberFormat;

public class DetailsFragment extends Fragment {

	private static final String TAG = DetailsFragment.class.toString();
	private String canteenId;
	private float averageRating;

	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_details, null);
	}

	@Override
	public void onActivityCreated(@Nullable Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		btnSave = getView().findViewById(R.id.btnSaveDetails);
		btnSave.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				saveChanges();
			}
		});
		setViews();
		loadMyCanteen();
	}

	private void saveChanges() {
		setUIEnabled(false);
		Canteen updatedCanteen = getCanteenFromUI();
		new AsyncTask<Object, Void, Boolean>() {
			@Override
			protected Boolean doInBackground(Object... objects) {
				Boolean result = new Boolean("True");
				try {
					result = new ServiceProxy().updateCanteen(
							(String) objects[0],
							(Canteen) objects[1]);
					return result;
				} catch (IOException e) {
					Log.d(TAG, "doInBackground: " + e);
					return result;
				}
			}

			@Override
			protected void onPostExecute(Boolean updateSuccessful) {
				//Toast.makeText(getActivity(), s != null ? getString(R.string.msg_ReviewCreated) : getString(R.string
						//.msg_ReviewNotCreated), Toast.LENGTH_SHORT).show();
				setUIEnabled(true);
				loadMyCanteen();
				Toast.makeText(getActivity(), updateSuccessful.booleanValue() ? "Canteen updated":"Update failed", Toast.LENGTH_SHORT).show();
			}
		}.execute(
				CanteenManagerApplication.getInstance().getAuthenticationToken(),
				updatedCanteen
				);

	}

	private Canteen getCanteenFromUI() {
		Canteen canteen = new Canteen(
				canteenId,
				EdtCanteenName.getText().toString(),
				EdtPhone.getText().toString(),
				EdtWebAddress.getText().toString(),
				EdtMenuOfTheDay.getText().toString(),
				Float.parseFloat(EdtMenuPrice.getText().toString()),
				averageRating,
				EdtAddress.getText().toString(),
				SkbWaitingTime.getProgress()
		);
		Log.d(TAG, "getCanteenFromUI:" + EdtMenuPrice.getText().toString());
		return canteen; //Float.parseFloat(EdtMenuPrice.getText().toString()),

	}

	public void loadMyCanteen() {
		new AsyncTask<Void, Void, Canteen>() {
			@Override
			protected Canteen doInBackground(Void... params) {
				try {
					String token = CanteenManagerApplication.getInstance().getAuthenticationToken();
					Canteen myCanteen = new ServiceProxy().getMyCanteen(token);
					return myCanteen;
				} catch (IOException e) {
					Log.e(TAG, "");
					return null;
				}
			}

			@Override
			protected void onPostExecute(Canteen canteen) {
				updateValues(canteen);
			}
		}.execute();
	}

	private void updateValues(Canteen canteen) {
		if (canteen != null) {
			canteenId = canteen.getId();
			averageRating = canteen.getAverageRating();
			EdtCanteenName.setText(canteen.getName());
			EdtMenuOfTheDay.setText(canteen.getSetMeal());
			EdtMenuPrice.setText(NumberFormat.getNumberInstance().format(canteen.getSetMealPrice()));
			EdtAddress.setText(canteen.getLocation());
			EdtWebAddress.setText(canteen.getWebsite());
			EdtPhone.setText(canteen.getPhoneNumber());
			int waitingTime = canteen.getAverageWaitingTime();
			SkbWaitingTime.setProgress(waitingTime);
			txtWaitingTime.setText(getResources().getString(R.string.WaitingTime) + " " + waitingTime);
		}
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


	EditText EdtCanteenName;
	EditText EdtMenuOfTheDay;
	EditText EdtMenuPrice;
	EditText EdtAddress;
	EditText EdtWebAddress;
	EditText EdtPhone;
	TextView txtWaitingTime;
	SeekBar SkbWaitingTime;
	Button btnSave;

	private void setViews() {
		EdtCanteenName = getView().findViewById(R.id.edtCanteenName);
		EdtMenuOfTheDay = getView().findViewById(R.id.edtDailyMenu);
		EdtMenuPrice = getView().findViewById(R.id.edtMenuPrice);
		EdtAddress = getView().findViewById(R.id.edtAddress);
		EdtWebAddress = getView().findViewById(R.id.edtWebsite);
		EdtPhone = getView().findViewById(R.id.edtPhone);
		SkbWaitingTime = getView().findViewById(R.id.skbWaitingTime);
		SkbWaitingTime.setOnSeekBarChangeListener(seekBarChangeListener);
		txtWaitingTime = getView().findViewById(R.id.txtWaitingTime);
	}

	private void setUIEnabled(boolean enabled) {
		EdtCanteenName.setEnabled(enabled);
		EdtMenuOfTheDay.setEnabled(enabled);
		EdtMenuPrice.setEnabled(enabled);
		EdtAddress.setEnabled(enabled);
		EdtWebAddress.setEnabled(enabled);
		EdtPhone.setEnabled(enabled);
		SkbWaitingTime.setEnabled(enabled);
		SkbWaitingTime.setEnabled(enabled);
	}
}
