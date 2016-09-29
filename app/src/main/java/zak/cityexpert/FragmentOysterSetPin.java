package zak.cityexpert;


import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.LatLng;


public class FragmentOysterSetPin extends Fragment {


    View rootView;
    TextView num1, num2, num3, num4;
    Button btnDone;
    OysterDatabase oysterDatabase;
    TextView lblMainTitle;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.fragment_oyster_login_setpin, container, false);
        oysterDatabase = new OysterDatabase(getActivity());
        lblMainTitle = (TextView) getActivity().findViewById(R.id.lblMainTitle);
        lblMainTitle.setText("OYSTER SECURITY");

        num1 = (TextView) rootView.findViewById(R.id.txtNum1);
        num2 = (TextView) rootView.findViewById(R.id.txtNum2);
        num3 = (TextView) rootView.findViewById(R.id.txtNum3);
        num4 = (TextView) rootView.findViewById(R.id.txtNum4);
        num1.setRawInputType(Configuration.KEYBOARD_QWERTY);
        num2.setRawInputType(Configuration.KEYBOARD_QWERTY);
        num3.setRawInputType(Configuration.KEYBOARD_QWERTY);
        num4.setRawInputType(Configuration.KEYBOARD_QWERTY);

        btnDone = (Button) rootView.findViewById(R.id.btnDone);

        btnDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (num1.getText().length() > 0 || num1.getText() != null) {
                    if (num2.getText().length() > 0 || num2.getText() != null) {
                        if (num3.getText().length() > 0 || num3.getText() != null) {
                            if (num4.getText().length() > 0 || num4.getText() != null) {

                                oysterDatabase.addUser(getArguments().getString("NAME"),
                                        getArguments().getString("USERNAME"),
                                        getArguments().getString("PASSWORD"),
                                        num1.getText().toString() + num2.getText().toString() + num3.getText().toString() + num4.getText().toString());

                                FragmentManager fm = getFragmentManager();
                                FragmentTransaction ft = fm.beginTransaction();
                                FragmentOyster fragmentOyster = new FragmentOyster();
                               // ft.addToBackStack(null);
                                ft.replace(R.id.mainContainer, fragmentOyster);
                                ft.commit();
                            }
                        }
                    }
                }
            }
        });

        num1.addTextChangedListener(new TextWatcher(){

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                num2.requestFocus();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        num2.addTextChangedListener(new TextWatcher(){

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                num3.requestFocus();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        num3.addTextChangedListener(new TextWatcher(){

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                num4.requestFocus();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        num4.addTextChangedListener(new TextWatcher(){

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                InputMethodManager inputMethodManager =(InputMethodManager) getActivity().getSystemService(Activity.INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(rootView.getWindowToken(), 0);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        return rootView;
    }


    @Override
    public void onDetach() {
        super.onDetach();
    }
}
