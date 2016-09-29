package zak.cityexpert;


import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.HashMap;


public class FragmentBus extends Fragment {

    Utilities utilities;
    TextView lblBustopName;
    ListView busTimesList;
    TextView lblMainTitle;
    Button btnFav;
    Button btnSearch;
    View rootView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.fragment_bus, container, false);
        utilities = new Utilities(getActivity());
        if (utilities.isNetworkAvailable()) {
            if(utilities.getGpsStatus(getActivity())){
                run();
            }else{
                Context context = rootView.getContext();
                CharSequence text = "No GPS Available.";
                int duration = Toast.LENGTH_SHORT;
                Toast toast = Toast.makeText(context, text, duration);
                toast.show();
            }

        } else {
            Context context = rootView.getContext();
            CharSequence text = "No Connection.";
            int duration = Toast.LENGTH_SHORT;
            Toast toast = Toast.makeText(context, text, duration);
            toast.show();
        }
        return rootView;
    }

    public void run() {
        busTimesList = (ListView) rootView.findViewById(R.id.busListView);
        lblMainTitle = (TextView) getActivity().findViewById(R.id.lblMainTitle);
        lblBustopName = (TextView) rootView.findViewById(R.id.lblMainTitle);
        btnSearch = (Button) rootView.findViewById(R.id.btnBusSearch);
        btnFav = (Button) rootView.findViewById(R.id.btnBusFavourites);
        lblMainTitle.setText("NEARBY STOPS");

            LatLng latLng = new LatLng(utilities.getCurrentLocation(getActivity()).getLatitude(), utilities.getCurrentLocation(getActivity()).getLongitude());

            // LatLng latLng = new LatLng(MainActivity.googleMap.getMyLocation().getLatitude(), MainActivity.googleMap.getMyLocation().getLongitude());
            MainActivity.googleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));

            new NearestStops(getActivity(), busTimesList, MainActivity.googleMap, utilities.getCurrentLocation(getActivity()).getLatitude(), utilities.getCurrentLocation(getActivity()).getLongitude()).execute();
            // new BusTops(getActivity(), busTimesList, MainActivity.googleMap).execute();

        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                lblMainTitle.setText("SEARCH FOR BUSTOPS");

                FragmentManager fm = getFragmentManager();
                FragmentTransaction ft = fm.beginTransaction();
                FragmentSearchStops fragmentSearchStops = new FragmentSearchStops();
                ft.addToBackStack("searchStops");
                ft.replace(R.id.mainContainer, fragmentSearchStops);
                ft.commit();

            }
        });

        btnFav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fm = getFragmentManager();
                FragmentTransaction ft = fm.beginTransaction();
                FragmentBusFavourites fragmentBusFavourites = new FragmentBusFavourites();
                ft.addToBackStack("busFav");
                ft.replace(R.id.mainContainer, fragmentBusFavourites);
                ft.commit();
            }
        });

        View.OnKeyListener pressed = new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK) {

                    Context context = rootView.getContext();
                    CharSequence text = "yhyhh";
                    int duration = Toast.LENGTH_SHORT;
                    Toast toast = Toast.makeText(context, text, duration);
                    toast.show();

                    return true;
                }
                return false;
            }
        };
        rootView.setOnKeyListener(pressed);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }
}
