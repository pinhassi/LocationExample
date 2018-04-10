package il.co.mobiledev.locationexample;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;



public class MyLocation {

	private String TAG = getClass().getSimpleName();

	private static MyLocation mInstance;

	private int LOCATION_SEARCH_TIMEOUT = 240000;

	private Timer mTimer;
	private LocationManager mLocationManager;


	private boolean mContinuesUpdates;

	private DecimalFormat mDecimalFormat; // used for the formatted Longitude and Latitude

	private ArrayList<Listener> mListeners = new ArrayList<>();
	private float mListenerDistanceThreshold;

	PersistentLocation mPersistentLocation;

	/**
	 * Class constructor
	 */
	private MyLocation() {
		mLocationManager = (LocationManager) App.getInstance().getContext().getSystemService(Context.LOCATION_SERVICE);
		setDecimalAccuracy(10);
		updateLastKnownLocation();
	}


	public static MyLocation getInstance() {
		if (mInstance == null)
			mInstance = new MyLocation();
		return mInstance;
	}

	/**
	 * Cancel listeners and clears Updated Location
	 */
	public void reset() {

		try {
			mLocationManager.removeUpdates(locationListenerGps);
			mLocationManager.removeUpdates(locationListenerNetwork);
		} catch (SecurityException se) {
			Log.e(TAG, "Reset GPS SecurityException error", se);
		} catch (Exception e) {
			Log.e(TAG, "Reset GPS Exception error", e);
		}
		if (mTimer != null) {
			mTimer.cancel();
			mTimer = null;
		}
		setLastKnownLocation(null);

	}

	private LocationListener locationListenerGps = new LocationListener() {
		public void onLocationChanged(Location location) {
			if (mTimer != null) {
				mTimer.cancel();
				mTimer = null;
			}
			setLastKnownLocation(location);
			if (!mContinuesUpdates) {
				try {
					mLocationManager.removeUpdates(locationListenerGps);
					mLocationManager.removeUpdates(locationListenerNetwork);
				} catch (SecurityException se) {
					Log.e(TAG, "locationListenerGps SecurityException error", se);
				} catch (Exception e) {
					Log.e(TAG, "locationListenerGps Exception error", e);
				}
			}
		}

		public void onProviderDisabled(String provider) {
		}

		public void onProviderEnabled(String provider) {
		}

		public void onStatusChanged(String provider, int status, Bundle extras) {
		}
	};

	private LocationListener locationListenerNetwork = new LocationListener() {
		public void onLocationChanged(Location location) {
			if (mTimer != null) {
				mTimer.cancel();
				mTimer = null;
			}
			setLastKnownLocation(location);
			if (!mContinuesUpdates) {
				try {
					mLocationManager.removeUpdates(locationListenerNetwork);
					mLocationManager.removeUpdates(locationListenerGps);
				} catch (SecurityException se) {
					Log.e(TAG, "locationListenerNetwork SecurityException error", se);
				} catch (Exception e) {
					Log.e(TAG, "locationListenerNetwork Exception error", e);
				}
			}
		}

		public void onProviderDisabled(String provider) {
		}

		public void onProviderEnabled(String provider) {
		}

		public void onStatusChanged(String provider, int status, Bundle extras) {
		}
	};

	public void getLocation(final AppActivity appActivity, final boolean continuesUpdates) {
		appActivity.requestPermission(android.Manifest.permission.ACCESS_FINE_LOCATION, new Callback<Boolean>() {
			@Override
			public void onComplete(Boolean data, Throwable error) {
				if (!data) {
					getLocation(appActivity, continuesUpdates);
				} else {
					appActivity.requestPermission(android.Manifest.permission.ACCESS_COARSE_LOCATION, new Callback<Boolean>() {
						@Override
						public void onComplete(Boolean data, Throwable error) {
							if (!data) {
								getLocation(appActivity, continuesUpdates);
							} else {
								getLocation(continuesUpdates);
							}
						}
					});
				}
			}
		});

	}

	private void getLocation(boolean continuesUpdates) {
		mContinuesUpdates = continuesUpdates;

		// register listeners so it will start once the location service is on (both might throw java.lang.IllegalArgumentException)
		try {
			mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListenerGps);
		} catch (SecurityException se) {
			Log.e(TAG, "getLocation locationListenerGps SecurityException error", se);
		} catch (Exception e) {
			Log.e(TAG, "getLocation locationListenerGps Exception error", e);
		}

		try {
			mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListenerNetwork);
		} catch (SecurityException se) {
			Log.e(TAG, "getLocation locationListenerNetwork SecurityException error", se);
		} catch (Exception e) {
			Log.e(TAG, "getLocation locationListenerNetwork Exception error", e);
		}

		//startTimeOutTimer();

		return;
	}

	public void setLocationSearchTimeout(int timeout) {
		LOCATION_SEARCH_TIMEOUT = timeout;
		startTimeOutTimer();
	}


	public boolean isGpsProviderEnabled() {
		boolean gpsEnabled = false;
		//exceptions will be thrown if provider is not permitted.
		try {
			gpsEnabled = mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
		} catch (Exception ignore) {
		}
		return gpsEnabled;
	}

	public boolean isNetworkProviderEnabled() {
		boolean networkEnabled = false;

		//exceptions will be thrown if provider is not permitted.
		try {
			networkEnabled = mLocationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
		} catch (Exception ignore) {
		}

		return networkEnabled;

	}


	public boolean locationServicesEnabled() {
		return isGpsProviderEnabled() || isNetworkProviderEnabled();

	}


	private void startTimeOutTimer() {
		if (mTimer != null) {
			mTimer.cancel();
			mTimer = null;
		}

		if (LOCATION_SEARCH_TIMEOUT > 0) {
			mTimer = new Timer();
			mTimer.schedule(new GetLastLocationTimerTask(), LOCATION_SEARCH_TIMEOUT);
		}
	}


	/**
	 * Cancel listeners and get last known location
	 */
	private class GetLastLocationTimerTask extends TimerTask {
		@Override
		public void run() {
			updateLastKnownLocation();
			mTimer = null;
		}
	}

	/**
	 * Sets UpdatedLocation as last known location
	 *
	 * @return UpdatedLocation != null
	 */
	private boolean updateLastKnownLocation() {
		boolean isLocationSet = false;
		Location net_loc = null, gps_loc = null;

		try {
			if (isGpsProviderEnabled()) // in case GPS is enabled but no location found
				mLocationManager.removeUpdates(locationListenerGps);

			gps_loc = mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
			net_loc = mLocationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

		} catch (SecurityException se) {
			Log.e(TAG, "GetLastLocationTimerTask TimerTask run() SecurityException error", se);
		} catch (Exception e) {
			Log.e(TAG, "GetLastLocationTimerTask TimerTask run() Exception error", e);
		}

		//if there are both values use the latest one
		if (gps_loc != null && net_loc != null) {
			if (gps_loc.getTime() > net_loc.getTime())
				setLastKnownLocation(gps_loc);
			else
				setLastKnownLocation(net_loc);
			isLocationSet = true;
		}

		if (gps_loc != null) {
			setLastKnownLocation(gps_loc);
			isLocationSet = true;
		}
		if (net_loc != null) {
			setLastKnownLocation(net_loc);
			isLocationSet = true;
		}

		if (!isLocationSet)
			setLastKnownLocation(null);
		return isLocationSet;
	}


	/**
	 * sets location results accuracy
	 *
	 * @param n number of places after the point. negative value or 0 means not set.
	 */
	public void setDecimalAccuracy(int n) {
		// configuring decimal format class member 
		StringBuilder sb = new StringBuilder("0.");
		for (int i = 0; i < n; i++)
			sb.append("0");
		mDecimalFormat = new DecimalFormat(sb.toString());
	}

	public String getFormattedLongitude() {
		return getFormattedLongitude(getLastLocation());
	}

	public String getFormattedLatitude() {
		return getFormattedLatitude(getLastLocation());
	}

	public String getFormattedLongitude(Location location) {
		if (location == null)
			return null;
		return mDecimalFormat.format(location.getLongitude());
	}

	public String getFormattedLatitude(Location location) {
		if (location == null)
			return null;
		return mDecimalFormat.format(location.getLatitude());
	}


	/**
	 * Sets the location stored in MyLocation
	 *
	 * @param location the Location to set
	 */
	public synchronized void setLastKnownLocation(final Location location) {
		// check for outdated data
		Location lastLocation = getLastLocation();
		if (lastLocation != null && location != null && lastLocation.getTime() > location.getTime())
			return;

		float distanceFromOldLocation = location == null ? 0 : lastLocation == null ? Float.MAX_VALUE : location.distanceTo(lastLocation);
		//Log.d(TAG, "distanceFromOldLocation: " + distanceFromOldLocation);
		if (distanceFromOldLocation > mListenerDistanceThreshold) {
			mPersistentLocation.setLastLocation(location);
			mPersistentLocation.save();
			if (mListeners.size() > 0) {
				App.getInstance().getHandler().post(new Runnable() {
					@Override
					public void run() {
						for (Listener listener : mListeners)
							listener.onLocationReceived(location);
					}
				});
			}
		}
	}


	/**
	 * Gets the location stored in MyLocation
	 *
	 * @return the Location
	 */
	public synchronized Location getLastLocation() {
		if (mPersistentLocation == null) {
			mPersistentLocation = new PersistentLocation();
			mPersistentLocation.load();
		}

		return mPersistentLocation.getLastLocation();
	}


	/**
	 * Waits for location to be updated
	 *
	 * @param timeout How many second to wait until timeout
	 * @return the Location
	 */
	public Location getLastLocation(int timeout) {
		Location location = null;
		for (int i = 0; i < timeout; i++) {
			location = getLastLocation();
			if (location == null || location.getLatitude() == 0 || location.getLongitude() == 0) {
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			} else {
				break;
			}
		}

		return location;
	}

	/**
	 * Waits for location to be updated
	 *
	 * @param timeout How many second to wait until timeout
	 * @return the Location
	 */
	public void getLastLocationAsync(final int timeout, final Listener listener) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				final Location finalLocation = getLastLocation(timeout);
				if (listener != null) {
					App.getInstance().getHandler().post(new Runnable() {
						@Override
						public void run() {
							listener.onLocationReceived(finalLocation);
						}
					});
				}
			}
		}).start();
	}

	public void addListener(Listener listener) {
		mListeners.remove(listener);
		mListeners.add(listener);
	}

	public void removeListener(Listener listener) {
		mListeners.remove(listener);
	}

	/**
	 * Prevents updates when location is no significantly changes
	 * @param threshold the approximate distance in meters
	 */
	public void setDistanceThreshold(float threshold) {
		mListenerDistanceThreshold = threshold;
	}

	public interface Listener {
		void onLocationReceived(Location location);
	}


}
