package com.udacity.mlndcapstone.numtrans;

import android.content.Context;
import android.content.res.AssetManager;
import android.util.Log;

/**
 * Created by girishpai on 11/3/16.
 * This class integrates with the graph created by the trainer model.
 * The idea and the Native code to integrate is completely borrowed from
 * https://github.com/miyosuda/TensorFlowAndroidMNIST/tree/master/jni-build/jni and
 * https://github.com/tensorflow/tensorflow/tree/master/tensorflow/examples/android
 * This is to save development time as this project was mainly focussed on Deep Learning and
 * Computer Vision for Android.
 */
public class DigitDetector {
    static {
        System.loadLibrary("tensorflow_mnist");
    }

    private native int init(AssetManager assetManager, String model);

    /**
     * draw pixels
     */
    public native int detectDigit(int[] pixels);

    public boolean setup(Context context) {
        AssetManager assetManager = context.getAssets();

        if(assetManager == null) {
            Log.d("digitDetect","AssetManager Null");
        }

        // model from expert tutorial
        int ret = init(assetManager, "file:///android_asset/NumTrans_Graph.pb");

        return ret >= 0;
    }
}
