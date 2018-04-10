package il.co.mobiledev.locationexample;

import android.location.Location;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class MainActivity extends AppActivity implements MyLocation.Listener{

    private TextView mLocationTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // start location updater
        MyLocation.getInstance().getLocation(getActivity(), true);

        setContentView(R.layout.activity_main);
        mLocationTextView = findViewById(R.id.location);
    }

    @Override
    protected void onResume() {
        super.onResume();

        MyLocation.getInstance().addListener(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MyLocation.getInstance().removeListener(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        MyLocation.getInstance().reset(); // stops location updater
    }

    @Override
    public void onLocationReceived(Location location) {
        mLocationTextView.setText(location.getLatitude() + "," + location.getLongitude());
    }
}
