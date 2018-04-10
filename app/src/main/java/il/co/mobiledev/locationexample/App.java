package il.co.mobiledev.locationexample;


import android.app.Application;
import android.content.Context;
import android.os.Handler;


public class App extends Application {

	private final String TAG = getClass().getSimpleName();
	private static App mInstance;

	private Context mContext;
	private Handler mHandler;

	@Override
	public void onCreate() {
		mInstance = this;
		mContext = getApplicationContext();
		mHandler = new Handler();
		super.onCreate();

	}

	public static App getInstance(){
		return mInstance;
	}

	public Handler getHandler() {
		return mHandler;
	}

	public Context getContext() {
		return mContext;
	}
}
