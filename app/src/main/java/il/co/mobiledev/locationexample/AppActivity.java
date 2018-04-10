package il.co.mobiledev.locationexample;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.util.SparseIntArray;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;


public abstract class AppActivity extends FragmentActivity  {

	public String TAG = getClass().getSimpleName();

	private boolean mIsDestroyed = false;
	private Callback<ArrayList<String>> mVoiceRecognitionCallback;

	private Dialog mProgressDialog;
	private Toast mMessageToast;

	private SparseIntArray mActivityRequestCodes = new SparseIntArray(3); // used to finish child activities

	private HashMap<String, Callback<Boolean>> mRequestPermissionCallbacks = new HashMap<>();

	protected void onCreate(Bundle savedInstanceState) {
		mIsDestroyed = false;
		setVolumeControlStream(AudioManager.STREAM_MUSIC);

		super.onCreate(savedInstanceState);
	}

	protected Context getContext() {
		return this;
	}

	public AppActivity getActivity() {
		return this;
	}

	public AppActivity getAppActivity() {
		return this;
	}

	public void hideKeyboard() {
		View view = getWindow().getCurrentFocus();
		if (view == null)
			return;

		IBinder binder = view.getWindowToken();
		if (binder == null)
			return;

		if (view instanceof EditText)
			((EditText) view).setText(((EditText) view).getText().toString());

		InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(binder, InputMethodManager.HIDE_NOT_ALWAYS);
	}

	public ViewGroup getRootView() {
		return (ViewGroup) ((ViewGroup) findViewById(android.R.id.content)).getChildAt(0);
	}

	@Override
	protected void onDestroy() {

		mIsDestroyed = true;

		// finish child activities before finishing self
		for (int i = 0; i < mActivityRequestCodes.size(); i++) {
			finishActivity(mActivityRequestCodes.valueAt(i));
		}


		super.onDestroy();
	}

	public boolean isDestroyed() {
		return mIsDestroyed;
	}




	@Override
	public void startActivityForResult(Intent intent, int requestCode) {
		mActivityRequestCodes.put(requestCode, requestCode);
		super.startActivityForResult(intent, requestCode);
	}

	public void requestPermission(String permission, Callback<Boolean> callback) {
		// Assume thisActivity is the current activity
		if (ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED) {
			if (callback != null)
				callback.onComplete(true, null);
		} else {
			mRequestPermissionCallbacks.put(permission, callback);
			ActivityCompat.requestPermissions(this, new String[]{permission}, 0);
		}
	}


	@Override
	public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
		for (int i = 0; i < permissions.length; i++) {
			String permission = permissions[i];
			Callback<Boolean> callback = mRequestPermissionCallbacks.remove(permission);
			if (callback != null)
				callback.onComplete(grantResults[i] == PackageManager.PERMISSION_GRANTED, null);
		}

	}


}
