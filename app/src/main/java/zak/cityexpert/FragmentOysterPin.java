package zak.cityexpert;


import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.StringRes;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.TextView;


public class FragmentOysterPin extends Fragment {


    View rootView;
    TextView num1, num2, num3, num4;

    OysterDatabase oysterDatabase;
    TextView lblMainTitle;

    String pin = "";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.fragment_oyster_login_pin, container, false);
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




        num1.addTextChangedListener(new TextWatcher(){

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(num1.getText().length() > 0) {
                    pin = pin + num1.getText();
                    num2.requestFocus();
                }
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
                if(num1.getText().length() > 0) {
                    pin = pin + num2.getText();
                    num3.requestFocus();
                }
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
                if(num1.getText().length() > 0) {
                    pin = pin + num3.getText();
                    num4.requestFocus();
                }
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

                if(num1.getText().length() > 0) {

                    pin = pin + num4.getText();
                    System.out.println(pin);
                    System.out.println(getArguments().getString("PIN"));


                    if (pin.equals(getArguments().getString("PIN"))) {

                        FragmentManager fm = getFragmentManager();
                        FragmentTransaction ft = fm.beginTransaction();
                        FragmentOysterBalance fragmentOysterBalance = new FragmentOysterBalance();

                        Bundle bundle = new Bundle();

                        bundle.putString("NAME", getArguments().getString("NAME"));
                        bundle.putString("ENCRYPTED_USERNAME", getArguments().getString("ENCRYPTED_USERNAME"));
                        bundle.putString("ENCRYPTED_PASSWORD", getArguments().getString("ENCRYPTED_PASSWORD"));
                        bundle.putString("PIN", getArguments().getString("PIN"));


                        fragmentOysterBalance.setArguments(bundle);


                     //   ft.addToBackStack(null);
                        ft.replace(R.id.mainContainer, fragmentOysterBalance);
                        ft.commit();

                        InputMethodManager inputMethodManager = (InputMethodManager) getActivity().getSystemService(Activity.INPUT_METHOD_SERVICE);
                        inputMethodManager.hideSoftInputFromWindow(rootView.getWindowToken(), 0);
                    } else {
                         num1.setText("");
                         num2.setText("");
                         num3.setText("");
                         num4.setText("");
                        num1.requestFocus();
                        pin = "";
                    }

                }

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
