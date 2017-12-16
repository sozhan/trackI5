package tmm.tracki5.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;

import tmm.tracki5.R;

public class SchoolStrengthFragment extends Fragment {

private ProgressBar schoolProgressBarCurrToday;
private ProgressBar schoolProgressBarOvrall;
private Handler mHandler = new Handler();
private int mProgressStatus=0;
private ImageView Imageview;
private int ClassAverageToday = 87;
private int ClassAverageOverall = 93;

String date1 = new SimpleDateFormat("dd-MMM-yyyy").format(new Date());

TextView setDate1;


        public SchoolStrengthFragment() {
            // Required empty public constructor
        }

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
        }
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            // Inflate the layout for this fragment
            View v = inflater.inflate(R.layout.school_strength_fragment, container, false);
            schoolProgressBarCurrToday = (ProgressBar)v.findViewById(R.id.progressBar);
            schoolProgressBarOvrall = (ProgressBar)v.findViewById(R.id.progressBar1);
            setDate1 = (TextView)v.findViewById(R.id.TodayDateClass);
            setDate1.setText(date1);
            SetAverage(schoolProgressBarCurrToday,ClassAverageToday);
            SetAverage(schoolProgressBarOvrall, ClassAverageOverall);
            return v;
        }
        public void SetAverage(final ProgressBar P, final int average) {
            final Thread background = new Thread() {
                public void run() {
                    try {
                        while(mProgressStatus <= average)
                        {

                            // Thread will sleep for 5 seconds
                            Thread.sleep(1000/50);
                            mProgressStatus+=1;

                            //update the progress bar
                            mHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    P.setProgress(mProgressStatus);
                                }
                            });
                        }
                    } catch (Exception e) {
                    }
                }
            };
            background.start();
        }

}