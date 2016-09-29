package zak.cityexpert;


import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;


public class FragmentBusTimes extends Fragment {


    Utilities utilities;
    TextView lblBustopName;
    ListView busTimesList;
    Button btnAddStopToFav;
    TextView lblMainTitle;
    BusDatabase busDatabase;
    Button btnFav;
    View rootView;
    String stopname;
    String stopcode;
    String distance;
    String towards;
    Double lat;
    Double lng;
    FragmentBusTimes thisfragment;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.fragment_bus_times, container, false);
        utilities = new Utilities(getActivity());

        stopname = getArguments().getString("STOPNAME");
        stopcode = getArguments().getString("STOPCODE");
        distance = getArguments().getString("DISTANCE");
        towards = getArguments().getString("TOWARDS");
        lat = getArguments().getDouble("LAT");
        lng = getArguments().getDouble("LNG");

        thisfragment = this;

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
            CharSequence text = "Not Connection.";
            int duration = Toast.LENGTH_SHORT;
            Toast toast = Toast.makeText(context, text, duration);
            toast.show();
        }
        return rootView;

    }


    public void run() {

        busDatabase = new BusDatabase(getActivity());
        busTimesList = (ListView) rootView.findViewById(R.id.busListView);
        lblMainTitle = (TextView) getActivity().findViewById(R.id.lblMainTitle);
        lblBustopName = (TextView) rootView.findViewById(R.id.lblMainTitle);
        btnFav = (Button) rootView.findViewById(R.id.btnBusFavourites);
        btnAddStopToFav = (Button) rootView.findViewById(R.id.btnAddStopToFav);

        if(busDatabase.ifExist(stopcode)){
            btnAddStopToFav.setText("REMOVE");
            btnAddStopToFav.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    busDatabase.deleteEach(stopcode);

                    FragmentManager fm = getActivity().getFragmentManager();
                    FragmentTransaction ft = fm.beginTransaction();
                    ft.remove(thisfragment);
                    ft.replace(R.id.mainContainer, thisfragment);
                    ft.commit();
                }
            });
        }else {
            btnAddStopToFav.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    busDatabase.addStopToFav(stopname, stopcode, lat + "", lng + "", distance, towards);

                    FragmentManager fm = getActivity().getFragmentManager();
                    FragmentTransaction ft = fm.beginTransaction();

                    ft.remove(thisfragment);
                    ft.replace(R.id.mainContainer, thisfragment);

                    ft.commit();
                }
            });
        }


        new BusTimes(getActivity(), busTimesList, stopcode).execute();

        lblMainTitle.setText(stopname.toUpperCase());

        Animation animation1 = AnimationUtils.loadAnimation(getActivity(), R.anim.custom);
        busTimesList.startAnimation(animation1);

        MainActivity.googleMap.clear();


       // LatLng latLng = new LatLng(Double.parseDouble(nearestStops.get(position).get(LATITUDE)), Double.parseDouble(nearestStops.get(position).get(LONGITUDE)));


        MainActivity.googleMap.addMarker(new MarkerOptions()
                .position(new LatLng(lat,lng))
                .title(stopname)
                .snippet("")
                .icon(BitmapDescriptorFactory.fromResource(R.raw.bustopicon))).showInfoWindow();
        MainActivity.googleMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(lat,lng)));
        MainActivity.googleMap.animateCamera(CameraUpdateFactory.zoomTo(15));


        PolylineOptions line =
                new PolylineOptions().add(new LatLng(lat,lng),
                        new LatLng(MainActivity.googleMap.getMyLocation().getLatitude(),
                                MainActivity.googleMap.getMyLocation().getLongitude()))
                        .width(5).color(Color.parseColor("#ce193e"));


        MainActivity.googleMap.addPolyline(line);



        MainActivity.googleMap.setOnMyLocationChangeListener(new GoogleMap.OnMyLocationChangeListener() {
            @Override
            public void onMyLocationChange(Location location) {

                MainActivity.googleMap.clear();

                //  LatLng latLng = new LatLng(Double.parseDouble(nearestStops.get(getPos).get(LATITUDE)), Double.parseDouble(nearestStops.get(getPos).get(LONGITUDE)));


                MainActivity.googleMap.addMarker(new MarkerOptions()
                        .position(new LatLng(lat,lng))
                        .title(stopname)
                        .snippet("")
                        .icon(BitmapDescriptorFactory.fromResource(R.raw.bustopicon))).showInfoWindow();
                // googleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));


                PolylineOptions line =
                        new PolylineOptions().add(new LatLng(lat,lng),
                                new LatLng(MainActivity.googleMap.getMyLocation().getLatitude(),
                                        MainActivity.googleMap.getMyLocation().getLongitude()))
                                .width(5).color(Color.parseColor("#ce193e"));


                MainActivity.googleMap.addPolyline(line);


            }
        });


        btnFav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                lblMainTitle.setText("FAVOURITE BUSTOPS");

                FragmentManager fm = getFragmentManager();
                FragmentTransaction ft = fm.beginTransaction();

                FragmentBusFavourites fragmentBusFavourites = new FragmentBusFavourites();
                ft.addToBackStack("busFavFromBusTimes");
                ft.replace(R.id.mainContainer, fragmentBusFavourites);

                ft.commit();

            }
        });


    }

    @Override
    public void onDetach() {
        super.onDetach();
    }


}
