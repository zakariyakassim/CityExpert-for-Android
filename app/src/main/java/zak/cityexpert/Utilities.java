package zak.cityexpert;


import android.app.Activity;
import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.opencsv.CSVReader;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Utilities {


    LocationManager locationManager;
    boolean isNetworkEnabled = false, isGPSEnabled = false;
    Location location;
    double latitude, longitude;

    Activity activity;
    public Utilities(Activity activity){

        this.activity = activity;

    }


    public ArrayList<HashMap<String, String>> busTop(){

        ArrayList<HashMap<String, String>> busTop = new ArrayList<HashMap<String, String>>();

        InputStream inputStream = activity.getResources().openRawResource(R.raw.busstops);

        String next[] = {};
        List<String[]> list = new ArrayList<>();

        try {
            CSVReader reader = new CSVReader(new InputStreamReader(inputStream));

            while (true) {

                next = reader.readNext();
                if (next != null) {
                    list.add(next);

                } else {
                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        String STOPNAME = "StopName";
        String STOPCODE = "StopCode";
        String EASTERN = "Eastern";
        String NORTHERN = "Northern";
        String removeUnwantedCharacters;
        for (int x = 1; x < list.size(); x++) {
            removeUnwantedCharacters = list.get(x)[3];
            removeUnwantedCharacters = removeUnwantedCharacters.replace("#", "");
            removeUnwantedCharacters = removeUnwantedCharacters.replace("<>", "");
            removeUnwantedCharacters = removeUnwantedCharacters.replace("<T>", "");
            removeUnwantedCharacters = removeUnwantedCharacters.replace(">T<", "");
            removeUnwantedCharacters = removeUnwantedCharacters.replace("<R>", "");
            removeUnwantedCharacters = removeUnwantedCharacters.replace(">R<", "");
            if (removeUnwantedCharacters.isEmpty()) {
                //Do nothing.
            } else {

                HashMap<String, String> map = new HashMap<String, String>();
                map.put(STOPNAME,removeUnwantedCharacters);
                map.put(STOPCODE,list.get(x)[1]);
                map.put(EASTERN,list.get(x)[4]);
                map.put(NORTHERN, list.get(x)[5]);

                busTop.add(map);


            }
        }

      //  Collections.sort(bustops);

        return busTop;
    }


    private boolean isGooglePlayServicesAvailable() {
        int status = GooglePlayServicesUtil.isGooglePlayServicesAvailable(activity);
        if (ConnectionResult.SUCCESS == status) {
            return true;
        } else {
            GooglePlayServicesUtil.getErrorDialog(status, activity, 0).show();
            return false;
        }
    }

    public boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) activity.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }




    public Location getCurrentLocation(Context context) {
        try {

            locationManager = (LocationManager) context
                    .getSystemService(context.LOCATION_SERVICE);

            // getting GPS status
            isGPSEnabled = locationManager
                    .isProviderEnabled(LocationManager.GPS_PROVIDER);
            System.out.println("gps band chhe" + isGPSEnabled);
            // getting network status
            isNetworkEnabled = locationManager
                    .isProviderEnabled(LocationManager.NETWORK_PROVIDER);

            if (!isGPSEnabled && !isNetworkEnabled) {
                // no network provider is enabled
//                Common.showGPSDisabledAlert("Please enable your location or connect to cellular network.", context);
            } else {
                if (isNetworkEnabled) {
                    Log.d("Network", "Network");
                    if (locationManager != null) {

                        //  if (ContextCompat.checkSelfPermission(context,Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED ) {

                        location = locationManager
                                .getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                        //     }

                        if (location != null) {
                            latitude = location.getLatitude();
                            longitude = location.getLongitude();
                        }
                    }
                }
                // if GPS Enabled get lat/long using GPS Services
                if (isGPSEnabled) {
                    if (location == null) {
                        Log.d("GPS Enabled", "GPS Enabled");
                        if (locationManager != null) {
                            location = locationManager
                                    .getLastKnownLocation(LocationManager.GPS_PROVIDER);
                            if (location != null) {
                                latitude = location.getLatitude();
                                longitude = location.getLongitude();
                            }
                        }
                    }
                }
            }



        } catch (Exception e) {
            e.printStackTrace();
        }


        return location;
    }


    public boolean getGpsStatus(Context context) {
        locationManager = (LocationManager) context
                .getSystemService(context.LOCATION_SERVICE);
        isGPSEnabled = locationManager
                .isProviderEnabled(LocationManager.GPS_PROVIDER);
        System.out.println("gps band chhe" + isGPSEnabled);
        return isGPSEnabled;
    }






}
