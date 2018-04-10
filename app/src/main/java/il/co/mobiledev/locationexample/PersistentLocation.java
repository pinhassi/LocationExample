package il.co.mobiledev.locationexample;

import android.content.Context;
import android.location.Location;


/**
 * Created by Asaf Pinhassi on 01/12/2016.
 * asaf@MobileDev.co.il
 */
public class PersistentLocation extends PersistentObject<PersistentLocation> {

	private transient Location mLastLocation;
	private byte[] mLastLocationBytes; // since Location is Parcelable and not Serializable, this field is used to save the Location data


	@Override
	public void copy(PersistentLocation object) {
		if (object == null)
			return;
		mLastLocationBytes = object.mLastLocationBytes;
		mLastLocation = ParcelableUtil.unmarshall(mLastLocationBytes, Location.CREATOR);
	}

	@Override
	public synchronized boolean save(Context context) {
		mLastLocationBytes = mLastLocation == null ? null : ParcelableUtil.marshall(mLastLocation);
		return super.save(context);

	}

	public Location getLastLocation() {
		return mLastLocation;
	}

	public void setLastLocation(Location lastLocation) {
		mLastLocation = lastLocation;
	}
}
