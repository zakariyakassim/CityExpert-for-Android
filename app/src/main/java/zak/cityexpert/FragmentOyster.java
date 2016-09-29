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


public class FragmentOyster extends Fragment {


    View rootView;
    TextView lblMainTitle;
    Utilities utilities;


    Button btnAdduser;


    OysterDatabase oysterDatabase;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.fragment_oyster_users, container, false);
        btnAdduser = (Button) rootView.findViewById(R.id.btnAddUser);
        oysterDatabase = new OysterDatabase(getActivity());
        ListView oysterUsersListView = (ListView) rootView.findViewById(R.id.oysterUsersListView);

        ListAdapter adapter = new SimpleAdapter(getActivity(), oysterDatabase.getAll(),
                R.layout.oyster_name_list_item, new String[]{"NAME"},
                new int[]{R.id.lblName});

        oysterUsersListView.setAdapter(adapter);
        oysterUsersListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {


                FragmentManager fm = getFragmentManager();
                FragmentTransaction ft = fm.beginTransaction();
                FragmentOysterPin fragmentOysterPin = new FragmentOysterPin();

                Bundle bundle = new Bundle();

                bundle.putString("NAME", oysterDatabase.getAll().get(position).get("NAME"));
                bundle.putString("ENCRYPTED_USERNAME", oysterDatabase.getAll().get(position).get("USERNAME"));
                bundle.putString("ENCRYPTED_PASSWORD", oysterDatabase.getAll().get(position).get("PASSWORD"));
                bundle.putString("PIN", oysterDatabase.getAll().get(position).get("PIN"));


                fragmentOysterPin.setArguments(bundle);


              //  ft.addToBackStack(null);
                ft.replace(R.id.mainContainer, fragmentOysterPin);
                ft.commit();


            }
        });

        btnAdduser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fm = getFragmentManager();
                FragmentTransaction ft = fm.beginTransaction();

                FragmentOysterLogin fragmentOysterLogin = new FragmentOysterLogin();
              //  ft.addToBackStack(null);
                ft.replace(R.id.mainContainer, fragmentOysterLogin);
                ft.commit();
            }
        });


        return rootView;
    }


}
