package zak.cityexpert;

import android.app.Activity;
import android.app.Fragment;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.hardware.SensorListener;
import android.hardware.SensorManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Vibrator;
import android.speech.RecognizerIntent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.SeekBar;
import android.widget.SimpleAdapter;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import org.apache.commons.lang3.text.WordUtils;

import java.io.IOException;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class FragmentAlertMe extends Fragment {

    Utilities utilities;
    ListView listView;
    TextView lblMainTitle;
    Button btnAlertMeSearch;
    SearchView alertMeSearchView;
    ArrayList<HashMap<String, String>> display = new ArrayList<>();
    ListAdapter adapter;
    LatLng latLng;
    SeekBar seekBarDistance;
    TextView lblMetres;
    View rootView;
    MediaPlayer mp;
    int distance = 400;
    Vibrator vibrator;
    private final int REQ_CODE_SPEECH_INPUT = 100;
    Switch switchVibration;
    Switch switchRinger;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.fragment_alert_me, container, false);
        utilities = new Utilities(getActivity());
        if (utilities.isNetworkAvailable()) {
            run();
        } else {

            Context context = rootView.getContext();
            CharSequence text = "Not Connection.";
            int duration = Toast.LENGTH_SHORT;

            Toast toast = Toast.makeText(context, text, duration);
            toast.show();
        }

        return rootView;
    }

    public void run() {

        lblMainTitle = (TextView) getActivity().findViewById(R.id.lblMainTitle);
        seekBarDistance = (SeekBar) rootView.findViewById(R.id.seekBar);

        alertMeSearchView = (SearchView) rootView.findViewById(R.id.alertMeSearchView);

        listView = (ListView) rootView.findViewById(R.id.alertmelistview);
        lblMetres = (TextView) rootView.findViewById(R.id.lblMetres);
        //  alertMeSearchView = (SearchView) rootView.findViewById(R.id.alertMeSearchView);
        lblMainTitle.setText("NOTIFY ME");
        // new AlertMeStops(getActivity(), listView, MainActivity.googleMap, alertMeSearchView).execute();


        switchVibration = (Switch) rootView.findViewById(R.id.switchVibration);
        switchRinger = (Switch) rootView.findViewById(R.id.switchRinger);
        switchVibration.setChecked(true);
        switchRinger.setChecked(true);



        lblMetres.setText(distance + " Metres");

        seekBarDistance.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress,
                                          boolean fromUser) {

                lblMetres.setText(String.valueOf(progress) + " Metres");
                distance = progress;

                if (latLng != null) {

                    MainActivity.googleMap.clear();
                    PolylineOptions line =
                            new PolylineOptions().add(new LatLng(latLng.latitude,
                                            latLng.longitude),
                                    new LatLng(MainActivity.googleMap.getMyLocation().getLatitude(),
                                            MainActivity.googleMap.getMyLocation().getLongitude()))
                                    .width(5).color(Color.parseColor("#ce193e"));

                    CircleOptions circleOptions = new CircleOptions()
                            .center(latLng)
                            .radius(distance).strokeWidth(5).strokeColor(Color.parseColor("#ce193e"));

                    MainActivity.googleMap.addCircle(circleOptions);
                    MainActivity.googleMap.addPolyline(line);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub
            }
        });

        btnAlertMeSearch = (Button) rootView.findViewById(R.id.btnALertMeSearch);
        btnAlertMeSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                promptSpeechInput();

            }
        });

        alertMeSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                if (alertMeSearchView.getQuery() != null) {
                    if (alertMeSearchView.getQuery().length() > 0) {
                        String convertedString =
                                Normalizer
                                        .normalize(alertMeSearchView.getQuery().toString(), Normalizer.Form.NFD)
                                        .replaceAll("[^\\p{ASCII}]", "");
                        search(convertedString);
                    }
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

    }

    private void promptSpeechInput() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT,
                getString(R.string.speech_prompt));
        try {
            startActivityForResult(intent, REQ_CODE_SPEECH_INPUT);
        } catch (ActivityNotFoundException a) {
            Toast.makeText(getActivity(),
                    getString(R.string.speech_not_supported),
                    Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case REQ_CODE_SPEECH_INPUT: {
                if (resultCode == Activity.RESULT_OK && null != data) {

                    ArrayList<String> result = data
                            .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    if(result.size() > 0) {
                        alertMeSearchView.setQuery(result.get(0), true);
                    } else {
                        Toast.makeText(getActivity(),
                                "Try Again",
                                Toast.LENGTH_SHORT).show();
                    }
                }
                break;
            }
        }
    }

    public void search(String input) {
        display.clear();

        try {
            Geocoder geoCoder = new Geocoder(getActivity());
            List<Address> addresses = geoCoder.getFromLocationName(input+" london", 15);

            if(input.length() > 0 && addresses.size() > 0) {
                MainActivity.googleMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(addresses.get(0).getLatitude(), addresses.get(0).getLongitude())));

            final HashMap<String, String> map = new HashMap<>();
            for (int x = 0; x < addresses.size(); x++) {

                map.put("NAME", addresses.get(x).getAddressLine(0) + ", " + addresses.get(x).getAddressLine(1));
                map.put("LAT", Double.toString(addresses.get(x).getLatitude()));
                map.put("LNG", Double.toString(addresses.get(x).getLongitude()));

                display.add(map);
            }
        } else {
                Toast.makeText(getActivity(),
                        WordUtils.capitalize(input) + " not found.",
                        Toast.LENGTH_SHORT).show();
            }

            adapter = new SimpleAdapter(getActivity(), display,
                    R.layout.places_list_layout, new String[]{"NAME"}, new int[]{R.id.lblPlace});

            listView.setAdapter(adapter);

            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                    latLng = new LatLng(Double.parseDouble(display.get(position).get("LAT")), Double.parseDouble(display.get(position).get("LNG")));

                    MainActivity.googleMap.clear();
                    MainActivity.googleMap.addMarker(new MarkerOptions()
                            .position(latLng)
                            .title(display.get(position).get("NAME")));
                    // .snippet("");
                    //  .icon(BitmapDescriptorFactory.fromResource(R.raw.stopmarker)));

                    MainActivity.googleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                    MainActivity.googleMap.animateCamera(CameraUpdateFactory.zoomTo(13));

                    PolylineOptions line =
                            new PolylineOptions().add(new LatLng(latLng.latitude,
                                            latLng.longitude),
                                    new LatLng(MainActivity.googleMap.getMyLocation().getLatitude(),
                                            MainActivity.googleMap.getMyLocation().getLongitude()))
                                    .width(5).color(Color.parseColor("#ce193e"));

                    CircleOptions circleOptions = new CircleOptions()
                            .center(latLng)
                            .radius(distance).strokeWidth(5).strokeColor(Color.parseColor("#ce193e"));


                    //  googleMap.addPolyline(line);
                    MainActivity.googleMap.addCircle(circleOptions);
                    MainActivity.googleMap.addPolyline(line);


                    MainActivity.googleMap.setOnMyLocationChangeListener(new GoogleMap.OnMyLocationChangeListener() {
                        @Override
                        public void onMyLocationChange(Location location) {

                            MainActivity.googleMap.clear();
                            PolylineOptions line =
                                    new PolylineOptions().add(new LatLng(latLng.latitude,
                                                    latLng.longitude),
                                            new LatLng(MainActivity.googleMap.getMyLocation().getLatitude(),
                                                    MainActivity.googleMap.getMyLocation().getLongitude()))
                                            .width(5).color(Color.parseColor("#ce193e"));

                            CircleOptions circleOptions = new CircleOptions()
                                    .center(latLng)
                                    .radius(distance).strokeWidth(5).strokeColor(Color.parseColor("#ce193e"));

                            MainActivity.googleMap.addCircle(circleOptions);
                            MainActivity.googleMap.addPolyline(line);

                            float[] results = new float[1];
                            Location.distanceBetween(latLng.latitude, latLng.longitude, MainActivity.googleMap.getMyLocation().getLatitude(), MainActivity.googleMap.getMyLocation().getLongitude(), results);
                            float distanceInMeters = results[0];
                            boolean isReachedTheCircle = distanceInMeters < distance;

                            if (isReachedTheCircle) {
                                if (switchRinger.isChecked()) {
                                    mp = MediaPlayer.create(getActivity(), R.raw.alarm1);
                                    mp.start();
                                }
                                if (switchVibration.isChecked()) {
                                    vibrator = (Vibrator) getActivity().getSystemService(Activity.VIBRATOR_SERVICE);
                                    vibrator.vibrate(50000);
                                }
                            }else {
                                if(vibrator != null){
                                    vibrator.cancel();
                                }
                                if (mp != null){
                                    mp.stop();
                                }
                            }
                        }
                    });
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
