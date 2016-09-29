package zak.cityexpert;


import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;


public class FragmentOysterBalance extends Fragment {

    View rootView;
    TextView lblMainTitle;
    Utilities utilities;

    TextView lblName, lblEncryptedUsername, lblEncryptedPassword, lblUsername, lblPassword;

    OysterDatabase oysterDatabase;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.fragment_oyster_balance, container, false);

        oysterDatabase = new OysterDatabase(getActivity());
        lblName = (TextView) rootView.findViewById(R.id.lblName);
        lblEncryptedUsername = (TextView) rootView.findViewById(R.id.lblEncryptedUsername);
        lblEncryptedPassword = (TextView) rootView.findViewById(R.id.lblEncryptedPassword);
        lblUsername = (TextView) rootView.findViewById(R.id.lblUsername);
        lblPassword = (TextView) rootView.findViewById(R.id.lblPassword);

        lblName.setText("Name: "+getArguments().getString("NAME"));
        lblEncryptedUsername.setText("Encrypted Username: "+getArguments().getString("ENCRYPTED_USERNAME"));
        lblEncryptedPassword.setText("Encrypted Password: "+getArguments().getString("ENCRYPTED_PASSWORD"));
        try {
            lblUsername.setText("Decrypted Username: "+AESCrypt.decrypt(getArguments().getString("ENCRYPTED_USERNAME")));
            lblPassword.setText("Decrypted Password: "+AESCrypt.decrypt(getArguments().getString("ENCRYPTED_PASSWORD")));
        } catch (Exception e) {
            e.printStackTrace();
        }

        return rootView;
    }


}
