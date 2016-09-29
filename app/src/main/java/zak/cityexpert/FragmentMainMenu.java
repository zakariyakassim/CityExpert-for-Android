package zak.cityexpert;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.LatLng;

public class FragmentMainMenu extends Fragment {

    View rootView;
    Button btnMainMenuBus, btnMainMenuPlaces, btnOyster, btnNotifyMe;
    RelativeLayout relativeLayout;
    Utilities utilities;
    Button btnBackToMainMenu;
    TextView lblMainTitle;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.fragment_main_menu, container, false);

        btnMainMenuBus = (Button) rootView.findViewById(R.id.btnMainMenuBus);
        btnMainMenuPlaces = (Button) rootView.findViewById(R.id.btnMainMenuPlaces);
        btnOyster = (Button) rootView.findViewById(R.id.btnOyster);
        btnNotifyMe = (Button) rootView.findViewById(R.id.btnNotifyMe);
        lblMainTitle = (TextView) getActivity().findViewById(R.id.lblMainTitle);
        utilities = new Utilities(getActivity());
        MainActivity.googleMap.clear();
        MainActivity.googleMap.setOnMyLocationChangeListener(null);
        lblMainTitle.setText(R.string.app_name);


        if (utilities.getGpsStatus(getActivity())) {
            LatLng latLng = new LatLng(utilities.getCurrentLocation(getActivity()).getLatitude(), utilities.getCurrentLocation(getActivity()).getLongitude());
          //  LatLng latLng = new LatLng(51.507351, -0.127758);
            MainActivity.googleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
            MainActivity.googleMap.animateCamera(CameraUpdateFactory.zoomTo(15));

        } else {
            Context context = rootView.getContext();
            CharSequence text = "No GPS Available.";
            int duration = Toast.LENGTH_SHORT;

            Toast toast = Toast.makeText(context, text, duration);
            toast.show();
        }
        //  Display display = getActivity().getWindowManager().getDefaultDisplay();
        //  int screen_width = display.getWidth();
        //  int screen_height = display.getHeight();


        relativeLayout = (RelativeLayout) rootView.findViewById(R.id.main_layout_relative);
        //  relativeLayout.setPadding(100, 100, 100, 100);


        btnBackToMainMenu = (Button) getActivity().findViewById(R.id.btnBackToMainMenu);
        // btnBackToMainMenu.setText("EXIT");
        btnBackToMainMenu.setText("");
        btnBackToMainMenu.setVisibility(View.INVISIBLE);
        btnBackToMainMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (FragmentBusTimer.timer != null) {
                    FragmentBusTimer.timer.cancel();
                }

                FragmentManager fm = getFragmentManager();
                FragmentTransaction ft = fm.beginTransaction();

                FragmentMainMenu fragmentMainMenu = new FragmentMainMenu();

                ft.replace(R.id.mainContainer, fragmentMainMenu);
                ft.commit();


            }
        });


        btnMainMenuBus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if (utilities.isNetworkAvailable()) {
                    btnBackToMainMenu.setVisibility(View.VISIBLE);
                    if (utilities.getGpsStatus(getActivity())) {
                        //  btnBackToMainMenu.setText("BACK");
                        FragmentManager fm = getFragmentManager();
                        FragmentTransaction ft = fm.beginTransaction();

                        FragmentBus fragmentBus = new FragmentBus();
                        ft.addToBackStack("bus");
                        ft.replace(R.id.mainContainer, fragmentBus);
                        ft.commit();
                    } else {


                        FragmentManager fm = getFragmentManager();
                        FragmentTransaction ft = fm.beginTransaction();

                        FragmentSearchStops fragmentSearchStops = new FragmentSearchStops();
                        ft.addToBackStack("searchStops");
                        ft.replace(R.id.mainContainer, fragmentSearchStops);
                        ft.commit();

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

            }
        });

        btnMainMenuPlaces.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (utilities.isNetworkAvailable()) {
                    if (utilities.getGpsStatus(getActivity())) {
                        btnBackToMainMenu.setVisibility(View.VISIBLE);
                        // btnBackToMainMenu.setText("BACK");
                        FragmentManager fm = getFragmentManager();
                        FragmentTransaction ft = fm.beginTransaction();

                        FragmentPlaces fragmentPlaces = new FragmentPlaces();
                        ft.addToBackStack("places");
                        ft.replace(R.id.mainContainer, fragmentPlaces);
                        ft.commit();
                    } else {
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


            }
        });

        btnOyster.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OysterDatabase oysterDatabase = new OysterDatabase(getActivity());
                if (utilities.isNetworkAvailable()) {

                    if (oysterDatabase.dataIsAvailable()) {
                        FragmentManager fm = getFragmentManager();
                        FragmentTransaction ft = fm.beginTransaction();

                        FragmentOyster fragmentOyster = new FragmentOyster();
                        ft.addToBackStack(null);
                        ft.replace(R.id.mainContainer, fragmentOyster);
                        ft.commit();
                    } else {
                        FragmentManager fm = getFragmentManager();
                        FragmentTransaction ft = fm.beginTransaction();

                        FragmentOysterLogin fragmentOysterLogin = new FragmentOysterLogin();
                        ft.addToBackStack(null);
                        ft.replace(R.id.mainContainer, fragmentOysterLogin);
                        ft.commit();
                    }

                    btnBackToMainMenu.setVisibility(View.VISIBLE);

                  /*  Context context = rootView.getContext();
                    CharSequence text = "Working on it ;)";
                    int duration = Toast.LENGTH_SHORT;

                    Toast toast = Toast.makeText(context, text, duration);
                    toast.show(); */

                } else {


                    Context context = rootView.getContext();
                    CharSequence text = "Not Connection.";
                    int duration = Toast.LENGTH_SHORT;

                    Toast toast = Toast.makeText(context, text, duration);
                    toast.show();
                }


            }
        });

        btnNotifyMe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (utilities.isNetworkAvailable()) {
                    if (utilities.getGpsStatus(getActivity())) {
                        btnBackToMainMenu.setVisibility(View.VISIBLE);
                        // btnBackToMainMenu.setText("BACK");
                        FragmentManager fm = getFragmentManager();
                        FragmentTransaction ft = fm.beginTransaction();

                        FragmentAlertMe fragmentAlertMe = new FragmentAlertMe();
                        ft.addToBackStack("alertMe");
                        ft.replace(R.id.mainContainer, fragmentAlertMe);
                        ft.commit();
                    } else {
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


            }
        });


        return rootView;
    }


    @Override
    public void onDetach() {

        super.onDetach();
    }


}
