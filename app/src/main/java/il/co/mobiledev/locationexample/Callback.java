package il.co.mobiledev.locationexample;

public interface Callback<T> {
	void onComplete(T data, Throwable error);
}

