package il.co.mobiledev.locationexample;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Asaf Pinhassi on 01/12/2016.
 * asaf@MobileDev.co.il
 * Taken from http://stackoverflow.com/questions/18000093/how-to-marshall-and-unmarshall-a-parcelable-to-a-byte-array-with-help-of-parcel
 *
 * With the help of the util class above, you can marshall/unmarshall instances of your class MyClass implements Parcelable like so:

 Unmarshalling (with CREATOR)

 byte[] bytes = …
 MyClass myclass = ParcelableUtil.unmarshall(bytes, MyClass.CREATOR);
 Unmarshalling (without CREATOR)

 byte[] bytes = …
 Parcel parcel = ParcelableUtil.unmarshall(bytes);
 MyClass myclass = new MyClass(parcel); // Or MyClass.CREATOR.createFromParcel(parcel).
 Marshalling

 MyClass myclass = …
 byte[] bytes = ParcelableUtil.marshall(myclass);


 *
 *
 */


public class ParcelableUtil {
	public static byte[] marshall(Parcelable parceable) {
		Parcel parcel = Parcel.obtain();
		parceable.writeToParcel(parcel, 0);
		byte[] bytes = parcel.marshall();
		parcel.recycle();
		return bytes;
	}

	public static Parcel unmarshall(byte[] bytes) {
		Parcel parcel = Parcel.obtain();
		parcel.unmarshall(bytes, 0, bytes.length);
		parcel.setDataPosition(0); // This is extremely important!
		return parcel;
	}

	public static <T> T unmarshall(byte[] bytes, Parcelable.Creator<T> creator) {
		Parcel parcel = unmarshall(bytes);
		T result = creator.createFromParcel(parcel);
		parcel.recycle();
		return result;
	}
}
