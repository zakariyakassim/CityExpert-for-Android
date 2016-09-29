package zak.cityexpert;


import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;


public class FragmentPlacesType extends Fragment {


    ListView listViewPlacesType;
    ArrayList<HashMap<String, String>> places;
   // Button btnBackToMainMenu;
    Utilities utilities = new Utilities(getActivity());
    TextView lblMainTitle;
    View rootView;
    String type;
   // Button btnFav;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_places_type, container, false);


        utilities = new Utilities(getActivity());

        type = getArguments().getString("TYPE");

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
        listViewPlacesType = (ListView) rootView.findViewById(R.id.listViewPlacesType);
       // btnFav = (Button) rootView.findViewById(R.id.btnFav);

        new GetPlaces(getActivity(), listViewPlacesType, type, MainActivity.googleMap, utilities.getCurrentLocation(getActivity()).getLatitude(), utilities.getCurrentLocation(getActivity()).getLongitude()).execute();


      /*  btnFav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fm = getActivity().getFragmentManager();
                FragmentTransaction ft = fm.beginTransaction();

                FragmentPlaceFavourites fragmentPlaceFavourites = new FragmentPlaceFavourites(type);
ft.addToBackStack(null);
                ft.replace(R.id.mainContainer, fragmentPlaceFavourites);
                ft.commit();
            }
        }); */

    }

}
