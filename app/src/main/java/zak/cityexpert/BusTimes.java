package zak.cityexpert;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.format.Time;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

public class BusTimes extends AsyncTask<String, Void, Boolean> {


    private static final String DESTINATION = "Destination";
    private static final String ROUTENAME = "routeName";
    private static final String ESTIMATEDWAIT = "estimatedWait";
    private static final String SCHEDULEDTIME = "scheduledTime";

    ArrayList<HashMap<String, String>> busTimes = new ArrayList<HashMap<String, String>>();


    private ProgressDialog dialog;

    private Activity activity;

    private Context context;

    private ListView listView;

    private String stopCode;

    JSONArray arrivals;

    public BusTimes(Activity activity, ListView listView, String stopCode) {
        this.activity = activity;
        context = activity;
        this.listView = listView;
        this.stopCode = stopCode;
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
        if (arrivals == null) {

            CharSequence text = "TFL Server is down.";
            int duration = Toast.LENGTH_SHORT;

            Toast toast = Toast.makeText(context, text, duration);
            toast.show();
        } else {

        ListAdapter adapter = new SimpleAdapter(activity, busTimes,
                R.layout.bustimes_list_item, new String[]{DESTINATION, ROUTENAME, ESTIMATEDWAIT,
                SCHEDULEDTIME}, new int[]{R.id.txtToward,
                R.id.txtBus, R.id.txtMinute, R.id.txtEstTime});

        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {


                String estWait = busTimes.get(position).get(ESTIMATEDWAIT);

                if (estWait.equals("due")) {

                    //do nothing

                } else {
                    String[] output = estWait.split("\\s+");

                   // int minutes = Integer.parseInt(output[0] + "000") * 60;
                    int minutes = Integer.parseInt(output[0]);

                    FragmentManager fm = activity.getFragmentManager();
                    FragmentTransaction ft = fm.beginTransaction();


                    FragmentBusTimer fragmentBusTimer = new FragmentBusTimer();
                    ft.addToBackStack("busTimer");
                    Bundle bundle = new Bundle();

                    bundle.putInt("MINUTES", minutes);

                    fragmentBusTimer.setArguments(bundle);


                    ft.replace(R.id.mainContainer, fragmentBusTimer);

                    ft.commit();
                }
            }
        });
    }
    }



    @Override
    protected Boolean doInBackground(String... params) {
        JSONParser getBusTimesFromServer = new JSONParser();
        JSONObject getBusTimes = getBusTimesFromServer
                .getJSONFromUrl("http://countdown.tfl.gov.uk/stopBoard/" + stopCode);
        if(getBusTimes != null) {
            try {
                arrivals = getBusTimes.getJSONArray("arrivals");
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        if(arrivals != null) {
            for (int i = 0; i < arrivals.length(); i++) {
                try {
                    JSONObject c = arrivals.getJSONObject(i);
                    String Destination = c.getString("destination");
                    String RouteName = c.getString("routeName");
                    String EstimatedWait = c.getString("estimatedWait");
                    String ScheduledTime = c.getString("scheduledTime");

                    ScheduledTime = getScheduledtime(ScheduledTime, 1);

                    HashMap<String, String> map = new HashMap<String, String>();


                    map.put(DESTINATION, Destination);
                    map.put(ROUTENAME, RouteName);
                    map.put(ESTIMATEDWAIT, EstimatedWait);
                    map.put(SCHEDULEDTIME, ScheduledTime);
                    busTimes.add(map);

                } catch (JSONException e) {
                    e.printStackTrace();

                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }

        }

        return false;
    }

   public String getScheduledtime(String time, int hoursToAdd) throws ParseException {
       Calendar calendar = Calendar.getInstance();
       DateFormat formatter = new SimpleDateFormat("hh:mm");
       Date date = formatter.parse(time);
       calendar.setTime(date);
       calendar.add(Calendar.HOUR, 1);
       return calendar.getTime().getHours()+":"+calendar.getTime().getMinutes();
   }
}
