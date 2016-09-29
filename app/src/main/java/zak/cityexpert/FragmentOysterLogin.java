package zak.cityexpert;


import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;


public class FragmentOysterLogin extends Fragment {


    View rootView;
    TextView lblMainTitle;
    Utilities utilities;

    TextView txtUsername;
    TextView txtPassword;
    Button btnLogin;

    TextView lblMessage;

    OysterDatabase oysterDatabase;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        oysterDatabase = new OysterDatabase(getActivity());
        utilities = new Utilities(getActivity());
        lblMainTitle = (TextView) getActivity().findViewById(R.id.lblMainTitle);


        rootView = inflater.inflate(R.layout.fragment_oyster_login, container, false);
        txtUsername = (TextView) rootView.findViewById(R.id.txtName);
        txtPassword = (TextView) rootView.findViewById(R.id.txtNum1);
        btnLogin = (Button) rootView.findViewById(R.id.btnNext);
        lblMessage = (TextView) rootView.findViewById(R.id.lblMessage);

        lblMainTitle.setText("OYSTER LOGIN");


        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (txtUsername.getText().length() > 0 && txtPassword.getText().length() > 0) {
                    try {
                        FragmentManager fm = getFragmentManager();
                        FragmentTransaction ft = fm.beginTransaction();

                        FragmentOysterSetName fragmentOysterSetName = new FragmentOysterSetName();

                        Bundle bundle = new Bundle();
                        bundle.putString("USERNAME", AESCrypt.encrypt(txtUsername.getText().toString()));
                        bundle.putString("PASSWORD", AESCrypt.encrypt(txtPassword.getText().toString()));
                        fragmentOysterSetName.setArguments(bundle);
                      //  ft.addToBackStack(null);
                        ft.replace(R.id.mainContainer, fragmentOysterSetName);
                        ft.commit();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });


        return rootView;
    }






}
