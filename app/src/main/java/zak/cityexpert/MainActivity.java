package zak.cityexpert;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.Display;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;

public class MainActivity extends AppCompatActivity implements LocationListener, OnMapReadyCallback {


    Utilities utilities;
    static GoogleMap googleMap;
    double latitude = 0;
    double longitude = 0;
    Location location;
    static List<String[]> bustops;
    TextView lblLoc;
    Context context;
    SupportMapFragment supportMapFragment;
    TextView lblMainTitle;

    static ViewPager viewPager;

    static String text = "text";

    ListView busTimesList;

    RelativeLayout splash;
    FragmentManager fm;
    Thread timer;
    LatLng latLng;


    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // googleMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(51.484573, -0.174687)));
        // googleMap.animateCamera(CameraUpdateFactory.zoomTo(10));

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        busTimesList = (ListView) findViewById(R.id.busListView);
        //  Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //   setSupportActionBar(toolbar);
        context = this;
        //lblLoc = (TextView) findViewById(R.id.lblLoc);

        utilities = new Utilities(this);

        lblMainTitle = (TextView) findViewById(R.id.lblMainTitle);

        Button btnBackToHere = (Button) findViewById(R.id.btnBackToMainMenu);


        if (utilities.getGpsStatus(this)) {
            latLng = new LatLng(utilities.getCurrentLocation(this).getLatitude(), utilities.getCurrentLocation(this).getLongitude());
        } else {
            latLng = new LatLng(51.507351, -0.127758);
        }


        btnBackToHere.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                lblMainTitle.setText("");

                FragmentManager fm = getFragmentManager();
                FragmentTransaction ft = fm.beginTransaction();

                FragmentMainMenu fragmentMainMenu = new FragmentMainMenu();

                ft.replace(R.id.mainContainer, fragmentMainMenu);
                ft.commit();
            }
        });

        Display display = getWindowManager().getDefaultDisplay();
       // int screen_width = display.getWidth();
        int screen_height = display.getHeight();

        SupportMapFragment map_fragment = (SupportMapFragment) (getSupportFragmentManager()
                .findFragmentById(R.id.map));
        ViewGroup.LayoutParams map_fragment_params = map_fragment.getView().getLayoutParams();

        map_fragment_params.height = screen_height / 3;
        map_fragment.getView().setLayoutParams(map_fragment_params);


        supportMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        googleMap = supportMapFragment.getMap();
        googleMap.setMyLocationEnabled(true);


        supportMapFragment.getMapAsync(this);
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        String bestProvider = locationManager.getBestProvider(criteria, true);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return;
        }
        location = locationManager.getLastKnownLocation(bestProvider);
        if (location != null) {
            onLocationChanged(location);
        }
        locationManager.requestLocationUpdates(bestProvider, 20000, 0, this);


        //  latitude = location.getLatitude();
        //  longitude = location.getLongitude();


        fm = getFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();

        FragmentMainMenu fragmentMainMenu = new FragmentMainMenu();
        ft.addToBackStack(null);


        ft.replace(R.id.mainContainer, fragmentMainMenu);
        ft.commit();


        googleMap.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
            @Override
            public void onMapLoaded() {
                // LatLng latLng = new LatLng(51.4998649, 0.1194298);
                // LatLng latLng = new LatLng(utilities.getCurrentLocation(this).getLatitude(), utilities.getCurrentLocation(this).getLongitude());
                googleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                googleMap.animateCamera(CameraUpdateFactory.zoomTo(12));
            }
        });


        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            if (FragmentBusTimer.timer != null) {
                FragmentBusTimer.timer.cancel();
            }
            if (fm.getBackStackEntryCount() == 1) {
                System.exit(0);
            } else {
                getFragmentManager().popBackStack();
            }
            System.out.println("yhhhhhh " + fm.getBackStackEntryCount());
            return true;
        }

        return super.onKeyDown(keyCode, event);
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    static String getUrlSource(String url) throws IOException {
        URL yahoo = new URL(url);
        URLConnection yc = yahoo.openConnection();
        StringBuilder a;
        try (BufferedReader in = new BufferedReader(new InputStreamReader(
                yc.getInputStream(), "UTF-8"))) {
            String inputLine;
            a = new StringBuilder();
            while ((inputLine = in.readLine()) != null) {
                a.append(inputLine);
            }
        }

        return a.toString();
    }

    @Override
    public void onLocationChanged(Location location) {
      /*  latitude = location.getLatitude();
        longitude = location.getLongitude();
        LatLng latLng = new LatLng(latitude, longitude);
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        googleMap.animateCamera(CameraUpdateFactory.zoomTo(12)); */


    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

      /*  latitude = location.getLatitude();
        longitude = location.getLongitude();
        LatLng latLng = new LatLng(latitude, longitude);
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        googleMap.animateCamera(CameraUpdateFactory.zoomTo(12)); */

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Main Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app deep link URI is correct.
                Uri.parse("android-app://zak.cityexpert/http/host/path")
        );
        AppIndex.AppIndexApi.start(client, viewAction);
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Main Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app deep link URI is correct.
                Uri.parse("android-app://zak.cityexpert/http/host/path")
        );
        AppIndex.AppIndexApi.end(client, viewAction);
        client.disconnect();
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        if (utilities.isGPSEnabled) {
            LatLng latLng = new LatLng(utilities.getCurrentLocation(this).getLatitude(), utilities.getCurrentLocation(this).getLongitude());
            googleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
            googleMap.animateCamera(CameraUpdateFactory.zoomTo(12));
        }

        // new NearestStops(this, busTimesList, googleMap, googleMap.getMyLocation().getLatitude(), googleMap.getMyLocation().getLongitude()).execute();
        // lblBustopName.setText("PICK A STOP");

    }
}



