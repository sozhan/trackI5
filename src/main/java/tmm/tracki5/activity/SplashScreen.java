package tmm.tracki5.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ProgressBar;

import tmm.tracki5.R;

/**
 * Created by Arun on 19/02/16.
 */
public class SplashScreen extends Activity {

    private ProgressBar mProgress;
    private int mProgressStatus = 0;

    private Handler mHandler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_screen);

        final ProgressBar mProgress = (ProgressBar) findViewById(R.id.progressBarDummy);
        mProgress.setProgress(0);
        mProgress.setMax(50);

// METHOD 1

        /****** Create Thread that will sleep for 5 seconds *************/
        final Thread background = new Thread() {
            public void run() {

                try {

                    while(mProgressStatus <= 90)
                    {

                        // Thread will sleep for 5 seconds
                        Thread.sleep(1000/2);
                        mProgressStatus+=5;

                        //update the progress bar
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                mProgress.setProgress(mProgressStatus);
                            }
                        });
                    }

                    // After 5 seconds redirect to another intent
                    Intent i = new Intent(getBaseContext(), HomeActivity.class);
                    startActivity(i);

                    //Remove activity
                    finish();
                } catch (Exception e) {

                }
            }
        };
        // start thread
        background.start();
    }
    @Override
    protected void onDestroy() {

        super.onDestroy();

    }
}

