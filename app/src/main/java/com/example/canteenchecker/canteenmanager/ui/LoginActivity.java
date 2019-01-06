package com.example.canteenchecker.canteenmanager.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.canteenchecker.canteenmanager.CanteenManagerApplication;
import com.example.canteenchecker.canteenmanager.R;
import com.example.canteenchecker.canteenmanager.proxy.ServiceProxy;

import java.io.IOException;

public class LoginActivity extends AppCompatActivity {
	private static final String TAG = LoginActivity.class.toString();

	private EditText edtUserName;
	private EditText edtPassWord;
	private Button btnLogIn;

	private static final int LOGIN_FOR_REVIEW_CREATION = 42;


	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);



		if(CanteenManagerApplication.getInstance().isAuthenticated()) {
			startActivityForResult(MainActivity.createIntent(this), LOGIN_FOR_REVIEW_CREATION);
		} else {
			setContentView(R.layout.activity_login);

			edtUserName = findViewById(R.id.edtUserName);
			edtPassWord = findViewById(R.id.edtPassWord);
			btnLogIn = findViewById(R.id.btnLogIn);

			btnLogIn.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					login();
				}
			});
		}
	}

	private void login() {

		setUIEnabled(false);

		new AsyncTask<String, Void, String>() {
			@Override
			protected String doInBackground(String... strings) {

				try {
					return new ServiceProxy().authenticate(strings[0], strings[1]);
				} catch (IOException e) {
					Log.e(TAG, "Login failed!", e);
					return null;
				}

			}

			@Override
			protected void onPostExecute(String s) {
				if(s != null) {
					//authentication finished with success ...
					CanteenManagerApplication.getInstance().setAuthenticationToken(s);
					setResult(Activity.RESULT_OK);
					//finish();
					Intent intent = new Intent(LoginActivity.this, MainActivity.class);
					startActivity(intent);

				} else {
					// or not ...
					edtPassWord.setText(null);
					setUIEnabled(true);
					Toast.makeText(LoginActivity.this, R.string.msg_LoginNotSucessfull, Toast.LENGTH_LONG).show();
				}

			}
		}.execute(edtUserName.getText().toString(), edtPassWord.getText().toString());

	}

	private void setUIEnabled(boolean enabled) {
		edtUserName.setEnabled(enabled);
		edtPassWord.setEnabled(enabled);
		btnLogIn.setEnabled(enabled);
	}

}
