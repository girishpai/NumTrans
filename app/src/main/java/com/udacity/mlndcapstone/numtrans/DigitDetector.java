package com.udacity.mlndcapstone.numtrans;

import android.content.Context;
import android.content.res.AssetManager;
import android.util.Log;

/**
 * Created by girishpai on 11/3/16.
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
        // model from beginner tutorial
        //int ret = init(assetManager, "file:///android_asset/beginner-graph.pb");

        // model from expert tutorial
        int ret = init(assetManager, "file:///android_asset/expert-graph.pb");

        return ret >= 0;
    }
}
