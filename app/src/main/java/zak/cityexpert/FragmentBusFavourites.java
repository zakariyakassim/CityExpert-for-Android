package zak.cityexpert;


import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class FragmentBusFavourites extends Fragment {

    private String PLACES = "places";
    //  TextView lblTitle;
    List<String> getPlaces;
    ListView listViewFav;
    ArrayList<HashMap<String, String>> places;
    ListAdapter adapter;
    Utilities utilities;
    TextView lblMainTitle;
    View rootView;
  //  Button btnBackToMainMenu;
    BusDatabase busDatabase;
    Button btnDeleteAll;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_bus_favourites, container, false);

        utilities = new Utilities(getActivity());
        lblMainTitle = (TextView) getActivity().findViewById(R.id.lblMainTitle);
        lblMainTitle.setText("FAVOURITE BUSTOPS");
        if (utilities.isNetworkAvailable()) {

            run();

        } else {
            Context context = rootView.getContext();
            CharSequence text = "Not Connection.";
            int duration = Toast.LENGTH_SHORT;

            Toast toast = Toast.makeText(context, text, duration);
            toast.show();
        }

      /*  btnBackToMainMenu = (Button) getActivity().findViewById(R.id.btnBackToMainMenu);
        btnBackToMainMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                lblMainTitle.setText("");

                FragmentManager fm = getFragmentManager();
                FragmentTransaction ft = fm.beginTransaction();

                FragmentBus fragmentBus = new FragmentBus();

                ft.replace(R.id.mainContainer, fragmentBus);
                ft.commit();
            }
        });*/
        return rootView;
    }

    public void run() {

        listViewFav = (ListView) rootView.findViewById(R.id.favBusListView);
        busDatabase = new BusDatabase(getActivity());
        btnDeleteAll = (Button) rootView.findViewById(R.id.btnDeleteAll);
        ListAdapter adapter = new SimpleAdapter(getActivity(), busDatabase.getAllStops(),
                R.layout.stops_list_layout, new String[]{busDatabase.STOPNAME(), busDatabase.TOWARDS(), busDatabase.DISTANCE()},
                new int[]{R.id.lblBusTop, R.id.lblTowards, R.id.lblDistance});

        listViewFav.setAdapter(adapter);
        Animation animation1 = AnimationUtils.loadAnimation(getActivity(), R.anim.custom);
        listViewFav.startAnimation(animation1);
        listViewFav.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

              //  LatLng latLng = new LatLng(Double.parseDouble(busDatabase.getAllStops().get(position).get(busDatabase.LATITUDE())), Double.parseDouble(busDatabase.getAllStops().get(position).get(busDatabase.LONGITUDE())));

                FragmentManager fm = getActivity().getFragmentManager();
                FragmentTransaction ft = fm.beginTransaction();

                FragmentBusTimes fragmentBusTimes = new FragmentBusTimes();
                Bundle bundle = new Bundle();

                bundle.putString("STOPNAME",busDatabase.getAllStops().get(position).get(busDatabase.STOPNAME()));
                bundle.putString("STOPCODE",busDatabase.getAllStops().get(position).get(busDatabase.STOPCODE()));
                bundle.putString("DISTANCE",busDatabase.getAllStops().get(position).get(busDatabase.DISTANCE()));
                bundle.putString("TOWARDS",busDatabase.getAllStops().get(position).get(busDatabase.TOWARDS()));
                bundle.putDouble("LAT", Double.parseDouble(busDatabase.getAllStops().get(position).get(busDatabase.LATITUDE())));
                bundle.putDouble("LNG", Double.parseDouble(busDatabase.getAllStops().get(position).get(busDatabase.LONGITUDE())));

                fragmentBusTimes.setArguments(bundle);

                ft.addToBackStack("busTimesFromFav");
                ft.replace(R.id.mainContainer, fragmentBusTimes);
                ft.commit();
            }

        });

        btnDeleteAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                busDatabase.deleteAll();
                FragmentManager fm = getFragmentManager();
                FragmentTransaction ft = fm.beginTransaction();

                FragmentBusFavourites fragmentBusFavourites = new FragmentBusFavourites();

                ft.replace(R.id.mainContainer, fragmentBusFavourites);
                ft.commit();
            }
        });


    }


}
