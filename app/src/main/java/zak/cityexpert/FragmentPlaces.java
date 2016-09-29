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

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.LatLng;

import org.apache.commons.lang3.text.WordUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;


public class FragmentPlaces extends Fragment {

    private String PLACES = "places";
    //  TextView lblTitle;
    List<String> getPlaces;
    ListView listViewPlaces;
    ArrayList<HashMap<String, String>> places;
    ListAdapter adapter;
    Utilities utilities;
    TextView lblMainTitle;
    View rootView;
  //  Button btnBackToMainMenu;
    Button btnFav;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
         rootView = inflater.inflate(R.layout.fragment_places, container, false);

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

    public void run(){
        lblMainTitle = (TextView) getActivity().findViewById(R.id.lblMainTitle);
        //  lblTitle = (TextView) rootView.findViewById(R.id.lblMainTitle);
        getPlaces = Arrays.asList(getResources().getStringArray(R.array.places));
        places = new ArrayList<>();
        // btnBack = (Button) rootView.findViewById(R.id.btnBack);
        listViewPlaces = (ListView) rootView.findViewById(R.id.listViewPlaces);
btnFav = (Button) rootView.findViewById(R.id.btnFav);
        LatLng latLng = new LatLng(utilities.getCurrentLocation(getActivity()).getLatitude(), utilities.getCurrentLocation(getActivity()).getLongitude());
//        LatLng latLng = new LatLng(MainActivity.googleMap.getMyLocation().getLatitude(), MainActivity.googleMap.getMyLocation().getLongitude());
        MainActivity.googleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        MainActivity.googleMap.animateCamera(CameraUpdateFactory.zoomTo(10));
        lblMainTitle.setText("PLACES");
        for (int x = 0; x < getPlaces.size(); x++) {

            HashMap<String, String> map = new HashMap<>();

            map.put(PLACES, WordUtils.capitalize(getPlaces.get(x).replace("_", " ")));

            places.add(map);
        }

        adapter = new SimpleAdapter(getActivity(), places,
                R.layout.places_list_layout, new String[]{PLACES}, new int[]{R.id.lblPlace});

        listViewPlaces.setAdapter(adapter);

        Animation animation1 = AnimationUtils.loadAnimation(getActivity(), R.anim.custom);
        listViewPlaces.startAnimation(animation1);

        listViewPlaces.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {


                lblMainTitle.setText(getPlaces.get(position).replace("_", " ").toUpperCase());

                FragmentManager fm = getFragmentManager();
                FragmentTransaction ft = fm.beginTransaction();

                FragmentPlacesType fragmentPlacesType = new FragmentPlacesType();

                Bundle bundle = new Bundle();
                bundle.putString("TYPE", getPlaces.get(position));
                fragmentPlacesType.setArguments(bundle);
                ft.addToBackStack("placesType");
                ft.replace(R.id.mainContainer, fragmentPlacesType);
                ft.commit();


            }
        });


        btnFav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fm = getActivity().getFragmentManager();
                FragmentTransaction ft = fm.beginTransaction();

                FragmentPlaceFavourites fragmentPlaceFavourites = new FragmentPlaceFavourites();
                ft.addToBackStack("placeFav");
                ft.replace(R.id.mainContainer, fragmentPlaceFavourites);
                ft.commit();
            }
        });


    }



}
