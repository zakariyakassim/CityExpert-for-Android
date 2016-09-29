package zak.cityexpert;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.opencsv.CSVReader;

import org.apache.commons.lang3.text.WordUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import uk.me.jstott.jcoord.OSRef;

public class BusTops extends AsyncTask<String, Void, Boolean> {


    private static final String DESTINATION = "Destination";
    private static final String ROUTENAME = "routeName";
    private static final String ESTIMATEDWAIT = "estimatedWait";
    private static final String SCHEDULEDTIME = "scheduledTime";

    ArrayList<HashMap<String, String>> busTimes = new ArrayList<HashMap<String, String>>();


    String STOPNAME = "StopName";
    String STOPCODE = "StopCode";
    String EASTERN = "Eastern";
    String NORTHERN = "Northern";
    ArrayList<HashMap<String, String>> busTop = new ArrayList<HashMap<String, String>>();

    ArrayList<HashMap<String, String>> searchResults = new ArrayList<>();
    private ProgressDialog dialog;
    private Activity activity;
    private Context context;
    private ListView listView;

    GoogleMap googleMap;
    TextView lblStopName;
    String getSearchString;

    public BusTops(Activity activity, ListView listView, GoogleMap googleMap, String getSearchString) {
        this.activity = activity;
        context = activity;
        this.listView = listView;
        this.getSearchString = getSearchString;
        this.googleMap = googleMap;
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

        Collections.sort(busTop, new Comparator<HashMap<String, String>>() {
            @Override
            public int compare(HashMap<String, String> o1,
                               HashMap<String, String> o2) {
                return o1.get(STOPNAME).compareTo(o2.get(STOPNAME));
            }
        });

        searchResults.clear();
        for (int x = 0; x < busTop.size(); x++){

            if(busTop.get(x).get(STOPNAME).toLowerCase().contains(getSearchString)){

                HashMap<String, String> map = new HashMap<String, String>();
                map.put(STOPNAME, busTop.get(x).get(STOPNAME));
                map.put(STOPCODE, busTop.get(x).get(STOPCODE));
                map.put(EASTERN, busTop.get(x).get(EASTERN));
                map.put(NORTHERN, busTop.get(x).get(NORTHERN));

                searchResults.add(map);
            }

        }

        Collections.sort(searchResults, new Comparator<HashMap<String, String>>() {
            @Override
            public int compare(HashMap<String, String> o1,
                               HashMap<String, String> o2) {
                return o1.get(STOPNAME).compareTo(o2.get(STOPNAME));
            }
        });
       // listView.setOnItemClickListener(null);

        lblStopName = (TextView) activity.findViewById(R.id.lblMainTitle);
        ListAdapter adapter = new SimpleAdapter(activity, searchResults,
                R.layout.stops_list_notowards_layout, new String[]{STOPNAME},
                new int[]{R.id.lblBusTop});

        listView.setAdapter(adapter);
        Animation animation1 = AnimationUtils.loadAnimation(activity, R.anim.custom);
        listView.startAnimation(animation1);

        listView.setClickable(true);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
                //  url = "http://countdown.tfl.gov.uk/stopBoard/" + bustops.get(position).getStopCode();
                //   busTimes.clear();

                new BusTimes(activity, listView, searchResults.get(position).get(STOPCODE)).execute();

                lblStopName.setText(searchResults.get(position).get(STOPNAME));


                //  new GetBusTimes(getActivity()).execute();
                // lblBustopName.setText(bustops.get(which).getStopName());
                Animation animation1 = AnimationUtils.loadAnimation(activity, R.anim.custom);
                listView.startAnimation(animation1);


                uk.me.jstott.jcoord.LatLng covert = new OSRef(Double.parseDouble(searchResults.get(position).get(EASTERN)), Double.parseDouble(searchResults.get(position).get(NORTHERN))).toLatLng();
                covert.toWGS84();

                LatLng latLng = new LatLng(covert.getLat(), covert.getLng());
                googleMap.addMarker(new MarkerOptions()
                        .position(latLng)
                        .title(searchResults.get(position).get(STOPNAME))
                        .snippet("")
                        .icon(BitmapDescriptorFactory.fromResource(R.raw.bustopicon)));

                googleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                googleMap.animateCamera(CameraUpdateFactory.zoomTo(15));


            }
        });


    }


    @Override
    protected Boolean doInBackground(String... params) {

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
                map.put(STOPNAME, WordUtils.capitalize(removeUnwantedCharacters.toLowerCase()));
                map.put(STOPCODE, list.get(x)[1]);
                map.put(EASTERN, list.get(x)[4]);
                map.put(NORTHERN, list.get(x)[5]);

                busTop.add(map);

            }
        }

        return false;
    }


}
