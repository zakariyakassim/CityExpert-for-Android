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


public class FragmentPlaceFavourites extends Fragment {


    ListView listViewFav;


    Utilities utilities;
    TextView lblMainTitle;
    View rootView;
  //  Button btnBackToMainMenu;
    PlaceDatabase placeDatabase;
    Button btnDeleteAll;
    FragmentPlaceFavourites thisfragment;

   /* String name = "";
    String image = "";
    String address = "";
    String distance = "";
    String place_type = "";
    LatLng ltlng;




    public FragmentPlaceFavourites(String name, String image, String address, String distance, String place_type, LatLng latLng) {
        this.name = name;
        this.image = image;
        this.address = address;
        this.distance = distance;
        this.place_type = place_type;
        this.ltlng = latLng;
    } */


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_place_favourites, container, false);

        utilities = new Utilities(getActivity());
        lblMainTitle = (TextView) getActivity().findViewById(R.id.lblMainTitle);
        lblMainTitle.setText("FAVOURITE PLACES");
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

     /*   btnBackToMainMenu = (Button) getActivity().findViewById(R.id.btnBackToMainMenu);
        btnBackToMainMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                lblMainTitle.setText("PLACES");

                FragmentManager fm = getFragmentManager();
                FragmentTransaction ft = fm.beginTransaction();

                FragmentPlaces fragmentPlaces = new FragmentPlaces();

                ft.replace(R.id.mainContainer, fragmentPlaces);
                ft.commit();

            }
        }); */
        return rootView;
    }

    public void run() {

        listViewFav = (ListView) rootView.findViewById(R.id.favPlaceListView);
        placeDatabase = new PlaceDatabase(getActivity());
        btnDeleteAll = (Button) rootView.findViewById(R.id.btnDeleteAll);

        ListAdapter adapter = new SimpleAdapter(getActivity(), placeDatabase.getAllPlaces(),
                R.layout.placetype_list_layout, new String[]{placeDatabase.PLACENAME(), placeDatabase.ADDRESS(), placeDatabase.DISTANCE()},
                new int[]{R.id.lblName, R.id.lblVicinity, R.id.lblDistance});

        listViewFav.setAdapter(adapter);
        Animation animation1 = AnimationUtils.loadAnimation(getActivity(), R.anim.custom);
        listViewFav.startAnimation(animation1);
        listViewFav.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

              //  LatLng latLng = new LatLng(Double.parseDouble(placeDatabase.getAllPlaces().get(position).get(placeDatabase.LAT())), Double.parseDouble(placeDatabase.getAllPlaces().get(position).get(placeDatabase.LNG())));

                FragmentManager fm = getActivity().getFragmentManager();
                FragmentTransaction ft = fm.beginTransaction();

                FragmentEachPlace fragmentEachPlace = new FragmentEachPlace();


                Bundle bundle = new Bundle();

                bundle.putString("NAME",placeDatabase.getAllPlaces().get(position).get(placeDatabase.PLACENAME()));
                bundle.putString("IMAGE",placeDatabase.getAllPlaces().get(position).get(placeDatabase.IMAGE()));
                bundle.putString("ADDRESS",placeDatabase.getAllPlaces().get(position).get(placeDatabase.ADDRESS()));
                bundle.putString("DISTANCE",placeDatabase.getAllPlaces().get(position).get(placeDatabase.DISTANCE()));
                bundle.putString("TYPE",placeDatabase.getAllPlaces().get(position).get(placeDatabase.TYPE()));
                bundle.putDouble("LAT",Double.parseDouble(placeDatabase.getAllPlaces().get(position).get(placeDatabase.LAT())));
                bundle.putDouble("LNG",Double.parseDouble(placeDatabase.getAllPlaces().get(position).get(placeDatabase.LNG())));

                fragmentEachPlace.setArguments(bundle);




                ft.addToBackStack("eachPlaceFromFav");
                ft.replace(R.id.mainContainer, fragmentEachPlace);
                ft.commit();
            }

        });

        btnDeleteAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                placeDatabase.deleteAll();
                FragmentManager fm = getFragmentManager();
                FragmentTransaction ft = fm.beginTransaction();

               FragmentPlaceFavourites fragmentPlaceFavourites = new FragmentPlaceFavourites();
                ft.replace(R.id.mainContainer, fragmentPlaceFavourites);
                ft.commit();
            }
        });


    }


}
