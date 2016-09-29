package zak.cityexpert;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import java.io.InputStream;
import java.net.URL;


public class FragmentEachPlace extends Fragment {
   // Button btnPlaceFav;
    PlaceDatabase placeDatabase;
   // Button btnBackToMainMenu;
    TextView lblMainTitle;
    TextView lblAddress;
    TextView lblDistance;
    Button btnAddFav;
    String name;
    String image;
    String address;
    String distance;
    ImageView imageViewPlace;
    Bitmap bmp;
    View rootView;
    Utilities utilities;
    String place_type;
   // LatLng latLng;
    Button btnFav;
    Double lat;
    Double lng;
    FragmentEachPlace thisfragment;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_each_place, container, false);

        name = getArguments().getString("NAME");
        image = getArguments().getString("IMAGE");
        distance = getArguments().getString("DISTANCE");
        address = getArguments().getString("ADDRESS");
        place_type = getArguments().getString("TYPE");
        lat = getArguments().getDouble("LAT");
        lng = getArguments().getDouble("LNG");
        thisfragment = this;

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
            CharSequence text = "Not Connection.";
            int duration = Toast.LENGTH_SHORT;

            Toast toast = Toast.makeText(context, text, duration);
            toast.show();
        }

        return rootView;
    }


    public void run() {
        btnAddFav = (Button) rootView.findViewById(R.id.btnAddPlaceToFav);
       // btnPlaceFav = (Button) rootView.findViewById(R.id.btnPlaceFav);
        imageViewPlace = (ImageView) rootView.findViewById(R.id.imageViewPlace);
        placeDatabase = new PlaceDatabase(getActivity());
        lblMainTitle = (TextView) getActivity().findViewById(R.id.lblMainTitle);
        lblAddress = (TextView) rootView.findViewById(R.id.lblAddress);
        lblDistance = (TextView) rootView.findViewById(R.id.lblPlaceDistance);
        lblMainTitle.setText(name);
        lblAddress.setText(address);
        lblDistance.setText(distance + " Miles Away");
        btnFav = (Button) rootView.findViewById(R.id.btnFav);


        btnFav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                FragmentManager fm = getFragmentManager();
                FragmentTransaction ft = fm.beginTransaction();

                FragmentPlaceFavourites fragmentPlaceFavourites = new FragmentPlaceFavourites();
                ft.addToBackStack("placeFavFromEachPlace");
                ft.replace(R.id.mainContainer, fragmentPlaceFavourites);
                ft.commit();

            }
        });

        MainActivity.googleMap.clear();

        // LatLng latLng = new LatLng(Double.parseDouble(thePlaces.get(position).get(LAT)), Double.parseDouble(thePlaces.get(position).get(LNG)));

        MainActivity.googleMap.addMarker(new MarkerOptions()
                .position(new LatLng(lat,lng))
                .title(name)
                .snippet("")
                .icon(BitmapDescriptorFactory.fromResource(R.raw.bustopicon))).showInfoWindow();
        MainActivity.googleMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(lat,lng)));
        MainActivity.googleMap.animateCamera(CameraUpdateFactory.zoomTo(15));

        // getPos = position;

        MainActivity.googleMap.setOnMyLocationChangeListener(new GoogleMap.OnMyLocationChangeListener() {
            @Override
            public void onMyLocationChange(Location location) {

                MainActivity.googleMap.clear();

                // LatLng latLng = new LatLng(Double.parseDouble(thePlaces.get(getPos).get(LAT)), Double.parseDouble(thePlaces.get(getPos).get(LNG)));

                PolylineOptions line =
                        new PolylineOptions().add(new LatLng(lat,lng),
                                new LatLng(MainActivity.googleMap.getMyLocation().getLatitude(),
                                        MainActivity.googleMap.getMyLocation().getLongitude()))
                                .width(5).color(Color.parseColor("#ce193e"));


                MainActivity.googleMap.addPolyline(line);

                MainActivity.googleMap.addMarker(new MarkerOptions()
                        .position(new LatLng(lat,lng))
                        .title(name)
                        .snippet("")
                        .icon(BitmapDescriptorFactory.fromResource(R.raw.bustopicon))).showInfoWindow();
                // googleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));


            }

        });





        if(placeDatabase.ifExist(address)){

            btnAddFav.setText("REMOVE");

            btnAddFav.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    placeDatabase.deleteEach(address);

                    FragmentManager fm = getActivity().getFragmentManager();
                    FragmentTransaction ft = fm.beginTransaction();

                   ft.remove(thisfragment);
                    ft.replace(R.id.mainContainer, thisfragment);
                    ft.commit();

                }
            });

        }else {
            btnAddFav.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    placeDatabase.addPlaceToFav(name, image, address, distance, place_type, lat + "", lng + "");

                    FragmentManager fm = getActivity().getFragmentManager();
                    FragmentTransaction ft = fm.beginTransaction();

                    ft.remove(thisfragment);
                    ft.replace(R.id.mainContainer, thisfragment);
                    ft.commit();
                }
            });
        }


        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                try {
                    InputStream in = new URL(image).openStream();
                    bmp = BitmapFactory.decodeStream(in);
                } catch (Exception e) {
                    // log error
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void result) {
                if (bmp != null)
                    imageViewPlace.setImageBitmap(bmp);
                imageViewPlace.getLayoutParams().height = 600;
                imageViewPlace.getLayoutParams().width = 600;

            }

        }.execute();


    }


    @Override
    public void onDetach() {
        super.onDetach();
    }

}
