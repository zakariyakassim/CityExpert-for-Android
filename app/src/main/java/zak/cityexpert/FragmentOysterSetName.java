package zak.cityexpert;


import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;


public class FragmentOysterSetName extends Fragment {


    View rootView;
    TextView txtName;
    Button btnNext;
    TextView lblMainTitle;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.fragment_oyster_login_setname, container, false);

        txtName = (TextView) rootView.findViewById(R.id.txtName);
        btnNext = (Button) rootView.findViewById(R.id.btnNext);
        lblMainTitle = (TextView) getActivity().findViewById(R.id.lblMainTitle);
        lblMainTitle.setText("OYSTER NAME");
        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (txtName.getText().length() > 0) {
                    FragmentManager fm = getFragmentManager();
                    FragmentTransaction ft = fm.beginTransaction();

                    FragmentOysterSetPin fragmentOysterSetPin = new FragmentOysterSetPin();

                    Bundle bundle = new Bundle();
                    bundle.putString("NAME", txtName.getText().toString());
                    bundle.putString("USERNAME", getArguments().getString("USERNAME"));
                    bundle.putString("PASSWORD", getArguments().getString("PASSWORD"));
                    fragmentOysterSetPin.setArguments(bundle);
                   // ft.addToBackStack(null);
                    ft.replace(R.id.mainContainer, fragmentOysterSetPin);
                    ft.commit();
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
