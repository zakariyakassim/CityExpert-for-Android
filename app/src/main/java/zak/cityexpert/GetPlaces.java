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
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
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

public class GetPlaces extends AsyncTask<String, Void, Boolean> {


    String API_KEY = "AIzaSyBVz1dDxXIQij0wGw45AfIOiA2B8nZByn8";

    private static final String NAME = "name";
    private static final String VICINITY = "vicinity";
    private static final String LAT = "51.4999490";
    private static final String LNG = "0.1193750";
    private static final String IMAGE = "image";
    private static final String DISTANCE = "distance";

    //  private static final String SCHEDULEDTIME = "scheduledTime";

    ArrayList<HashMap<String, String>> thePlaces = new ArrayList<>();


    private ProgressDialog dialog;

    private Activity activity;

    private Context context;

    private ListView listView;

    private String placetype;

    private GoogleMap googleMap;
    double lat;
    double lng;

    String state;

    LatLng latLng;

    JSONArray places;
    int getPos;


    public GetPlaces(Activity activity, ListView listView, String placetype, GoogleMap googleMap, double lat, double lng) {
        this.activity = activity;
        context = activity;
        this.listView = listView;
        this.placetype = placetype;
        this.googleMap = googleMap;
        latLng = new LatLng(lat, lng);
        this.lat = lat;
        this.lng = lng;

        dialog = new ProgressDialog(context);
    }

    @Override
    protected void onPreExecute() {
        //  this.dialog.setMessage("Getting data from TFL");
        //this.dialog.show();
    }

    @Override
    protected void onPostExecute(Boolean result) {
        if (dialog.isShowing()) {
            dialog.dismiss();
        }

        if (places == null) {

            CharSequence text = "Server is down.";
            int duration = Toast.LENGTH_SHORT;

            Toast toast = Toast.makeText(context, text, duration);
            toast.show();
        } else {

            googleMap.clear();
            for (int i = 0; i < thePlaces.size(); i++) {
                LatLng latLng = new LatLng(Double.parseDouble(thePlaces.get(i).get(LAT)), Double.parseDouble(thePlaces.get(i).get(LNG)));


                googleMap.addMarker(new MarkerOptions()
                        .position(latLng)
                        .title(thePlaces.get(i).get(NAME))
                        .snippet("")
                        .icon(BitmapDescriptorFactory.fromResource(R.raw.bustopicon)));
            }

            Collections.sort(thePlaces, new Comparator<HashMap<String, String>>() {
                @Override
                public int compare(HashMap<String, String> o1,
                                   HashMap<String, String> o2) {
                    return Double.compare(Double.parseDouble(o1.get(DISTANCE)), Double.parseDouble(o2.get(DISTANCE)));
                }
            });

            ListAdapter adapter = new SimpleAdapter(activity, thePlaces,
                    R.layout.placetype_list_layout, new String[]{NAME, VICINITY, DISTANCE}, new int[]{R.id.lblName, R.id.lblVicinity, R.id.lblDistance});

            //  if(listView != null)
            listView.setAdapter(adapter);
            Animation animation1 = AnimationUtils.loadAnimation(activity, R.anim.custom);
            listView.startAnimation(animation1);
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                    //LatLng latLng = new LatLng(Double.parseDouble(thePlaces.get(position).get(LAT)), Double.parseDouble(thePlaces.get(position).get(LNG)));

                    FragmentManager fm = activity.getFragmentManager();
                    FragmentTransaction ft = fm.beginTransaction();

                    FragmentEachPlace fragmentEachPlace = new FragmentEachPlace();
                    Bundle bundle = new Bundle();

                    bundle.putString("NAME",thePlaces.get(position).get(NAME));
                    bundle.putString("IMAGE",thePlaces.get(position).get(IMAGE));
                    bundle.putString("ADDRESS",thePlaces.get(position).get(VICINITY));
                    bundle.putString("DISTANCE",thePlaces.get(position).get(DISTANCE));
                    bundle.putString("TYPE",placetype);
                    bundle.putDouble("LAT", Double.parseDouble(thePlaces.get(position).get(LAT)));
                    bundle.putDouble("LNG",Double.parseDouble(thePlaces.get(position).get(LNG)));

                    fragmentEachPlace.setArguments(bundle);



                    ft.addToBackStack("eachPlace");
                    ft.replace(R.id.mainContainer, fragmentEachPlace);
                    ft.commit();

                /*    googleMap.clear();

                    LatLng latLng = new LatLng(Double.parseDouble(thePlaces.get(position).get(LAT)), Double.parseDouble(thePlaces.get(position).get(LNG)));

                    googleMap.addMarker(new MarkerOptions()
                            .position(latLng)
                            .title(thePlaces.get(position).get(NAME))
                            .snippet("")
                            .icon(BitmapDescriptorFactory.fromResource(R.raw.bustopicon))).showInfoWindow();
                    googleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                    googleMap.animateCamera(CameraUpdateFactory.zoomTo(15));

                    getPos = position;

                    googleMap.setOnMyLocationChangeListener(new GoogleMap.OnMyLocationChangeListener() {
                        @Override
                        public void onMyLocationChange(Location location) {

                            googleMap.clear();

                            LatLng latLng = new LatLng(Double.parseDouble(thePlaces.get(getPos).get(LAT)), Double.parseDouble(thePlaces.get(getPos).get(LNG)));

                            PolylineOptions line =
                                    new PolylineOptions().add(new LatLng(latLng.latitude,
                                                    latLng.longitude),
                                            new LatLng(googleMap.getMyLocation().getLatitude(),
                                                    googleMap.getMyLocation().getLongitude()))
                                            .width(5).color(Color.parseColor("#ce193e"));


                            googleMap.addPolyline(line);

                            googleMap.addMarker(new MarkerOptions()
                                    .position(latLng)
                                    .title(thePlaces.get(getPos).get(NAME))
                                    .snippet("")
                                    .icon(BitmapDescriptorFactory.fromResource(R.raw.bustopicon))).showInfoWindow();
                           // googleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));

                        }
                    }); */

                }
            });
        }
    }


    @Override
    protected Boolean doInBackground(String... params) {

        JSONParser getPlacesFromServer = new JSONParser();


        JSONObject getPlaces = getPlacesFromServer.getJSONFromUrl("https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=" + lat + "," + lng + "&radius=10000&types=" + placetype + "&key=" + API_KEY);
        // JSONObject getPlaces = getPlacesFromServer.getJSONFromUrl("https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=51.4999490,0.1193750&radius=500&types="+placetype+"&key=AIzaSyC8aJYaI9djrUoEMaMIa3sRF36buKPSV0M");
        if(getPlaces != null) {
            try {
                places = getPlaces.getJSONArray("results");
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        if(places != null) {
            for (int i = 0; i < places.length(); i++) {
                try {
                    JSONObject c = places.getJSONObject(i);
                    String name = c.getString(NAME);
                    String vicinity = c.getString(VICINITY);
                    // String rating = c.getString(RATING);

                    JSONObject jsonLocation = c.getJSONObject("geometry").getJSONObject("location");
                    String getLat = jsonLocation.getString("lat");
                    String getLng = jsonLocation.getString("lng");

                    HashMap<String, String> map = new HashMap<>();

                    Location current = new Location("current");
                    current.setLatitude(lat);
                    current.setLongitude(lng);
                    Location stoploc = new Location("stop");
                    stoploc.setLatitude(Double.parseDouble(getLat));
                    stoploc.setLongitude(Double.parseDouble(getLng));

                    String distance = Float.toString(current.distanceTo(stoploc));
                    distance = Double.toString(round(ConvertMetersToMiles(Double.parseDouble(distance)), 2));


                    JSONArray imagearray = c.getJSONArray("photos");


                    JSONObject imageobject = imagearray.getJSONObject(0);


                    String image = "https://maps.googleapis.com/maps/api/place/photo?maxwidth=400&photoreference=" + imageobject.getString("photo_reference") + "&key=" + API_KEY;

                    // String image = imageobject.getString("photo_reference");


                    map.put(NAME, name);
                    map.put(VICINITY, vicinity);
                    map.put(LAT, getLat);
                    map.put(LNG, getLng);
                    map.put(DISTANCE, distance);
                    map.put(IMAGE, image);

                    thePlaces.add(map);

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
