package com.udacity.mlndcapstone.numtrans;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import org.opencv.android.OpenCVLoader;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "NumTrans";
    private DigitDetector mDetector = new DigitDetector();

    //Initialize open cv
    static {
        if(OpenCVLoader.initDebug()) {
            Log.d(TAG,"OpenCV Successfully Loaded");
        }
        else {
            Log.d(TAG,"OpenCV Load Not Successfully");
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        boolean ret = mDetector.setup(this);
        if (!ret) {
            Log.i(TAG, "Detector setup failed");
            return;
        }


    }
}
