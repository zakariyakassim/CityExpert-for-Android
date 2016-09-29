package zak.cityexpert;


import android.app.Activity;
import android.app.Fragment;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Vibrator;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Switch;
import android.widget.TextView;

import java.io.IOException;
import java.util.concurrent.TimeUnit;


public class FragmentBusTimer extends Fragment {

    Button btnNearbyStops;
    TextView lblTime;
    int minutes = 0;
    TextView lblNumber;
    int number = 0;
    static CountDownTimer timer;
    Utilities utilities;
    MediaPlayer mp;
    Button btnStop;
    //  NumberPicker numberPicker;
    TextView lblMainTitle;
    View rootView;
    int count = 0;
    // Button btnBackToMainMenu;
    // int key = 0;

    // Button btn1, btn2, btn3, btn4, btn5, btn6, btn7, btn8, btn9, btn10, btn11, btn12, btn13, btn14, btn15;

    Button[] btn = new Button[15];
    Switch switchVibration;
    Switch switchRinger;



  /*  public FragmentBusTimer(int minutes) {
        this.minutes = minutes;
    } */


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.fragment_bus_timer, container, false);
        utilities = new Utilities(getActivity());
        minutes = getArguments().getInt("MINUTES");
        run();

        return rootView;

    }


    public void run() {

        btn[0] = (Button) rootView.findViewById(R.id.btn1);
        btn[1] = (Button) rootView.findViewById(R.id.btn2);
        btn[2] = (Button) rootView.findViewById(R.id.btn3);
        btn[3] = (Button) rootView.findViewById(R.id.btn4);
        btn[4] = (Button) rootView.findViewById(R.id.btn5);
        btn[5] = (Button) rootView.findViewById(R.id.btn6);
        btn[6] = (Button) rootView.findViewById(R.id.btn7);
        btn[7] = (Button) rootView.findViewById(R.id.btn8);
        btn[8] = (Button) rootView.findViewById(R.id.btn9);
        btn[9] = (Button) rootView.findViewById(R.id.btn10);
        btn[10] = (Button) rootView.findViewById(R.id.btn11);
        btn[11] = (Button) rootView.findViewById(R.id.btn12);
        btn[12] = (Button) rootView.findViewById(R.id.btn13);
        btn[13] = (Button) rootView.findViewById(R.id.btn14);
        btn[14] = (Button) rootView.findViewById(R.id.btn15);


        lblNumber = (TextView) rootView.findViewById(R.id.lblNumber);
        lblTime = (TextView) getActivity().findViewById(R.id.lblMainTitle);
        btnStop = (Button) rootView.findViewById(R.id.btnStop);
        lblMainTitle = (TextView) getActivity().findViewById(R.id.lblMainTitle);
        btnStop.setVisibility(View.GONE);
        mp = MediaPlayer.create(getActivity(), R.raw.alarm1);
        lblNumber.setText("Remind me in 0 minutes.");

        switchVibration = (Switch) rootView.findViewById(R.id.switchVibration);
        switchRinger = (Switch) rootView.findViewById(R.id.switchRinger);
        switchVibration.setChecked(true);
        switchRinger.setChecked(true);



        timer = new CountDownTimer((minutes * 1000) * 60, 1000) {
            public void onTick(long millisUntilFinished) {
                Integer onTickSecs = (int) (long) TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished);

                minutes = (int) (long) TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished);

                if (onTickSecs == number * 60) {
                    lblTime.setText("Done");
                    number = number - 1;

                    if (switchRinger.isChecked()) {
                        mp.start();
                        btnStop.setVisibility(View.VISIBLE);
                    }

                    if (switchVibration.isChecked()) {
                        ((Vibrator) getActivity().getSystemService(Activity.VIBRATOR_SERVICE)).vibrate(5000);
                    }


                    if (number != 0) {
                        btn[number - 1].setBackgroundColor(Color.parseColor("#c9526a"));
                    } else {
                        btn[number].setBackgroundColor(Color.parseColor("#A6A6A6"));
                    }
                    lblNumber.setText("Remind me in " + number + " minute.");
                } else {
                    lblTime.setText("" + String.format("%d min, %d sec",
                            TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished),
                            TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) -
                                    TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished))) + "");
                }
                for (int x = 0; x < btn.length; x++) {
                    if (Integer.parseInt(btn[x].getText().toString()) > onTickSecs / 60) {
                        btn[x].setEnabled(false);
                        btn[x].setBackgroundColor(Color.parseColor("#A6A6A6"));
                    }
                }

            }

            public void onFinish() {
                lblTime.setText("DUE");
            }
        }.start();

        for (int x = 0; x < btn.length; x++) {

            btn[x].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Button b = (Button) v;
                    int key = Integer.parseInt(b.getText().toString());
                    number = key;
                    lblNumber.setText("Remind me in " + key + " minute.");

                    for (int i = 0; i < minutes; i++) {
                        if (i < btn.length) {
                            btn[i].setBackgroundColor(Color.parseColor("#ce193e"));
                        }
                    }
                    v.setBackgroundColor(Color.parseColor("#c9526a"));
                }
            });

            if (x > minutes) {
                btn[x].setEnabled(false);
                btn[x].setBackgroundColor(Color.parseColor("#A6A6A6"));
            }
        }

        btnStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
    mp.stop();
                try {
                    mp.prepare();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                btnStop.setVisibility(View.INVISIBLE);
            }
        });
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }
}
