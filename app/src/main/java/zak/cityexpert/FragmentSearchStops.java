package zak.cityexpert;


import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Locale;


public class FragmentSearchStops extends Fragment {


    ListView busSearchListView;
    Utilities utilities;
    TextView lblMainTitle;
    View rootView;
   // Button btnBackToMainMenu;
    Button btnBustopSearch;
    SearchView searchViewBustopSearch;
    Button btnAllStops;
    Button btnFav;
    private final int REQ_CODE_SPEECH_INPUT = 100;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_search_stops, container, false);

        utilities = new Utilities(getActivity());
        lblMainTitle = (TextView) getActivity().findViewById(R.id.lblMainTitle);
        lblMainTitle.setText("SEARCH FOR BUSTOPS");
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
        }); */


        return rootView;
    }

    public void run() {

        busSearchListView = (ListView) rootView.findViewById(R.id.busSearchListView);
        btnBustopSearch = (Button) rootView.findViewById(R.id.btnBustopSearch);
        searchViewBustopSearch = (SearchView) rootView.findViewById(R.id.searchViewBustopSearch);
        btnAllStops = (Button) rootView.findViewById(R.id.btnAllStops);
        btnFav = (Button) rootView.findViewById(R.id.btnFavourites);

     //   new BusTops(getActivity(), busSearchListView, MainActivity.googleMap, "").execute();


        btnFav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {



                FragmentManager fm = getFragmentManager();
                FragmentTransaction ft = fm.beginTransaction();

                FragmentBusFavourites fragmentBusFavourites = new FragmentBusFavourites();
                ft.addToBackStack("busFavFromSearch");
                ft.replace(R.id.mainContainer, fragmentBusFavourites);
                ft.commit();

            }
        });




        btnBustopSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                promptSpeechInput();



              /*  if (searchViewBustopSearch.getQuery() != null) {
                    if (searchViewBustopSearch.getQuery().length() > 0) {
                        String convertedString =
                                Normalizer
                                        .normalize(searchViewBustopSearch.getQuery().toString(), Normalizer.Form.NFD)
                                        .replaceAll("[^\\p{ASCII}]", "");
                        search(convertedString.toLowerCase());
                        searchViewBustopSearch.clearFocus();
                    }
                }*/
            }
        });



        searchViewBustopSearch.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if (searchViewBustopSearch.getQuery() != null) {
                    if (searchViewBustopSearch.getQuery().length() > 0) {
                        String convertedString =
                                Normalizer
                                        .normalize(searchViewBustopSearch.getQuery().toString(), Normalizer.Form.NFD)
                                        .replaceAll("[^\\p{ASCII}]", "");
                        search(convertedString.toLowerCase());

                    }
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        btnAllStops.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new BusTops(getActivity(), busSearchListView, MainActivity.googleMap, "").execute();
            }
        });

    }


    private void promptSpeechInput() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT,
                getString(R.string.speech_prompt));

        try {
            startActivityForResult(intent, REQ_CODE_SPEECH_INPUT);
        } catch (ActivityNotFoundException a) {
            Toast.makeText(getActivity(),
                    getString(R.string.speech_not_supported),
                    Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case REQ_CODE_SPEECH_INPUT: {
                if (resultCode == Activity.RESULT_OK && null != data) {

                    ArrayList<String> result = data
                            .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    searchViewBustopSearch.setQuery(result.get(0), true);
                }
                break;
            }

        }
    }


    public void search(String input) {

            new BusTops(getActivity(), busSearchListView, MainActivity.googleMap, input).execute();

    }

}
