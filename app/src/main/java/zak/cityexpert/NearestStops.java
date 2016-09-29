package zak.cityexpert;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.Context;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

public class NearestStops extends AsyncTask<String, Void, Boolean> {

    Button btnBack;
    Utilities utilities;
    TextView lblMainTitle;

    String STOPNAME = "StopName";
    String STOPCODE = "StopCode";
    String LONGITUDE = "Logitude";
    String LATITUDE = "Latitude";
    String DISTANCE = "Distance";
    String TOWARDS = "Towards";

    ArrayList<HashMap<String, String>> nearestStops = new ArrayList<HashMap<String, String>>();


    private ProgressDialog dialog;

    private Activity activity;

    private Context context;

    private ListView listView;

    double lat = 51.499883;
    double lng = 0.119551;

    double swLat;
    double swLng;
    double neLat;
    double neLng;

    GoogleMap googleMap;

    TextView lblStopName;
    int getPos;
    JSONArray stops;

    public NearestStops(Activity activity, ListView listView, GoogleMap googleMap, double lat, double lng) {
        this.activity = activity;
        context = activity;
        this.listView = listView;
        this.lat = lat;
        this.lng = lng;
        this.googleMap = googleMap;
        swLat = lat - 0.01;
        swLng = lng - 0.01;
        neLat = lat + 0.01;
        neLng = lng + 0.01;

        btnBack = (Button) activity.findViewById(R.id.btnBackToMainMenu);

        utilities = new Utilities(activity);
        lblMainTitle = (TextView) activity.findViewById(R.id.lblMainTitle);
        dialog = new ProgressDialog(context);
    }

    @Override
    protected void onPreExecute() {
        this.dialog.setMessage("Getting data from TFL");
        //this.dialog.show();
    }

    @Override
    protected void onPostExecute(Boolean result) {
        if (dialog.isShowing()) {
            dialog.dismiss();
        }

        if (stops == null) {

            CharSequence text = "TFL Server is down.";
            int duration = Toast.LENGTH_SHORT;

            Toast toast = Toast.makeText(context, text, duration);
            toast.show();
        } else {

            googleMap.clear();
            for (int i = 0; i < nearestStops.size(); i++) {
                LatLng latLng = new LatLng(Double.parseDouble(nearestStops.get(i).get(LATITUDE)), Double.parseDouble(nearestStops.get(i).get(LONGITUDE)));


                googleMap.addMarker(new MarkerOptions()
                        .position(latLng)
                        .title(nearestStops.get(i).get(STOPNAME))
                        .snippet("")
                        .icon(BitmapDescriptorFactory.fromResource(R.raw.bustopicon)));


            }


            Collections.sort(nearestStops, new Comparator<HashMap<String, String>>() {
                @Override
                public int compare(HashMap<String, String> o1,
                                   HashMap<String, String> o2) {
                    return Double.compare(Double.parseDouble(o1.get(DISTANCE)), Double.parseDouble(o2.get(DISTANCE)));
                }
            });


            lblStopName = (TextView) activity.findViewById(R.id.lblMainTitle);
            ListAdapter adapter = new SimpleAdapter(activity, nearestStops,
                    R.layout.stops_list_layout, new String[]{STOPNAME, TOWARDS, DISTANCE},
                    new int[]{R.id.lblBusTop, R.id.lblTowards, R.id.lblDistance});

            listView.setAdapter(adapter);
            Animation animation1 = AnimationUtils.loadAnimation(activity, R.anim.custom);
            listView.startAnimation(animation1);
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                    LatLng latLng = new LatLng(Double.parseDouble(nearestStops.get(position).get(LATITUDE)), Double.parseDouble(nearestStops.get(position).get(LONGITUDE)));


                    FragmentManager fm = activity.getFragmentManager();
                    FragmentTransaction ft = fm.beginTransaction();

                    FragmentBusTimes fragmentBusTimes = new FragmentBusTimes();
                    Bundle bundle = new Bundle();

                    bundle.putString("STOPNAME",nearestStops.get(position).get(STOPNAME));
                    bundle.putString("STOPCODE",nearestStops.get(position).get(STOPCODE));
                    bundle.putString("DISTANCE",nearestStops.get(position).get(DISTANCE));
                    bundle.putString("TOWARDS",nearestStops.get(position).get(TOWARDS));
                    bundle.putDouble("LAT", Double.parseDouble(nearestStops.get(position).get(LATITUDE)));
                    bundle.putDouble("LNG", Double.parseDouble(nearestStops.get(position).get(LONGITUDE)));

                    fragmentBusTimes.setArguments(bundle);

                    ft.addToBackStack("busTimes");
                    ft.replace(R.id.mainContainer, fragmentBusTimes);
                    ft.commit();


                }


            });


        }
    }


    @Override
    protected Boolean doInBackground(String... params) {


        JSONParser getBusTimesFromServer = new JSONParser();

        JSONObject getBusTimes = getBusTimesFromServer.getJSONFromUrl("http://countdown.tfl.gov.uk/markers/swLat/" + swLat + "/swLng/" + swLng + "/neLat/" + neLat + "/neLng/" + neLng + "/");
if(getBusTimes != null) {
    try {
        stops = getBusTimes.getJSONArray("markers");

    } catch (JSONException e) {
        e.printStackTrace();
    }
}

        if (stops != null) {
            for (int i = 0; i < stops.length(); i++) {
                try {
                    JSONObject c = stops.getJSONObject(i);


                    String getStopName = c.getString("name");
                    String getStopCode = c.getString("id");
                    String getLat = c.getString("lat");
                    String getLong = c.getString("lng");
                    String getTowards = c.getString("towards");

                    //  JSONArray routes = c.getJSONArray("routes");

                    //   JSONObject g = new JSONObject();


                    HashMap<String, String> map = new HashMap<String, String>();
                    Location current = new Location("current");
                    current.setLatitude(lat);
                    current.setLongitude(lng);
                    Location stoploc = new Location("stop");
                    stoploc.setLatitude(Double.parseDouble(getLat));
                    stoploc.setLongitude(Double.parseDouble(getLong));

                    //  double s = current.distanceTo(stoploc);

                    // double d = ConvertMetersToMiles(s);

                    String distance = Float.toString(current.distanceTo(stoploc));
                    distance = Double.toString(round(ConvertMetersToMiles(Double.parseDouble(distance)), 2));

                    //  String distance = Math.round(ConvertMetersToMiles(s)) + " Miles";


                    map.put(STOPNAME, getStopName);
                    map.put(STOPCODE, getStopCode);
                    map.put(LATITUDE, getLat);
                    map.put(LONGITUDE, getLong);
                    map.put(DISTANCE, distance);
                    map.put(TOWARDS, getTowards);
                    //  map.put("B1", new JSONObject(routes.getString(1)).getString("name")+"Z");

                    nearestStops.add(map);


                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }

        return false;
    }

    public double ConvertMetersToMiles(double meters) {
        return (meters / 1609.344);
    }

    private static double round(double value, int precision) {
        int scale = (int) Math.pow(10, precision);
        return (double) Math.round(value * scale) / scale;
    }
}
