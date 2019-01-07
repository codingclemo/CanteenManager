package com.example.canteenchecker.canteenmanager.ui;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;


import com.example.canteenchecker.canteenmanager.CanteenManagerApplication;
import com.example.canteenchecker.canteenmanager.R;
import com.example.canteenchecker.canteenmanager.core.Canteen;
import com.example.canteenchecker.canteenmanager.proxy.ServiceProxy;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.text.NumberFormat;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class DetailsFragment extends Fragment {

	private static final String TAG = DetailsFragment.class.toString();
	private static final int DEFAULTMAP_ZOOM_FACTOR = 17;
	private String canteenId;
	private float averageRating;

	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_details, null);
	}

	@Override
	public void onActivityCreated(@Nullable Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		setViews();
		btnSave.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				saveChanges();
			}
		});
		btnWebsite.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				openLink();
			}
		});

		loadMyCanteen();
	}

	private void openLink() {
		String url = EdtWebAddress.getText().toString();
		url = url.replace("http://", "");
		Uri uri = Uri.parse("http://" + url); // missing 'http://' will cause crashed
		Intent intent = new Intent(Intent.ACTION_VIEW, uri);
		startActivity(intent);
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
		Log.d(TAG, "getCanteenFromUI:" + EdtMenuPrice.getText().toString());
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
		return canteen;
	}

	public void loadMyCanteen() {
		new AsyncTask<Void, Void, Canteen>() {
			@Override
			protected Canteen doInBackground(Void... params) {
				try {
					String token = CanteenManagerApplication.getInstance().getAuthenticationToken();
					Canteen myCanteen = new ServiceProxy().getMyCanteen(token);
					CanteenManagerApplication.getInstance().setCanteenId(myCanteen.getId());
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
			updateMap(canteen.getLocation());

		}
	}

	private void updateMap(String location) {
		new AsyncTask<String, Void, LatLng>() {
			@Override
			protected LatLng doInBackground(String... strings) {
				LatLng location = null;
				Geocoder geocoder = new Geocoder(getActivity());
				try {
					List<Address> addresses = geocoder.getFromLocationName(strings[0], 1);
					if(addresses != null && addresses.size() > 0) {
						Address address = addresses.get(0);
						location = new LatLng(address.getLatitude(), address.getLongitude());
					} else {
						Log.w(TAG, "Resolving failed!");
					}
				} catch (IOException e) {
					Log.w(TAG, "Resolving of Adress failed.");
				}
				return location;
			}

			@Override
			protected void onPostExecute(final LatLng latLng) {
				mpfMap.getMapAsync(new OnMapReadyCallback() {
					@Override
					public void onMapReady(GoogleMap googleMap) {
						googleMap.clear();
						if(latLng != null) {
							googleMap.addMarker(new MarkerOptions().position(latLng));
							googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, DEFAULTMAP_ZOOM_FACTOR));
						} else {
							googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(0, 0), 0));
						}
					}
				});
			}
		}.execute(location);
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


	private EditText EdtCanteenName;
	private EditText EdtMenuOfTheDay;
	private EditText EdtMenuPrice;
	private EditText EdtAddress;
	private EditText EdtWebAddress;
	private EditText EdtPhone;
	private TextView txtWaitingTime;
	private SeekBar SkbWaitingTime;
	private ImageButton btnWebsite;
	private Button btnSave;
	private SupportMapFragment mpfMap;

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
		btnSave = getView().findViewById(R.id.btnSaveDetails);
		btnWebsite = getView().findViewById(R.id.btnWebsite);

		FragmentManager fm = getChildFragmentManager();
		mpfMap = (SupportMapFragment) fm.findFragmentById(R.id.mpfMap);
		mpfMap.getMapAsync(new OnMapReadyCallback() {
			@Override
			public void onMapReady(GoogleMap googleMap) {
				UiSettings uiSettings = googleMap.getUiSettings();
				uiSettings.setAllGesturesEnabled(false);
				uiSettings.setZoomControlsEnabled(true);
			}
		});

		EdtAddress.addTextChangedListener(new TextWatcher() {

			private Timer timer = new Timer();


			@Override
			public void beforeTextChanged(CharSequence s, int start, int
					count, int after) {

			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
			                          int count) {
			}

			@Override
			public void afterTextChanged(final Editable s) {
				timer.cancel();
				timer = new Timer();
				int sleep = 350;
				if (s.length() == 1)
					sleep = 1000;
				else if (s.length() <= 3)
					sleep = 700;
				else if (s.length() <= 5)
					sleep = 500;
				timer.schedule(new TimerTask() {
					@Override
					public void run() {
						updateMap(s.toString());
					}
				}, sleep);
			}
		});

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
		btnWebsite.setEnabled(enabled);
		btnSave.setEnabled(enabled);
	}
}
