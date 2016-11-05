package com.udacity.mlndcapstone.numtrans;

import android.graphics.Bitmap;

/**
 * Created by girishpai on 11/4/16.
 */
public class RoiObject {
    int xCord;

    public RoiObject(int xCord,String photoName, Bitmap bmp) {
        this.xCord = xCord;
    }

    public int compareTo(RoiObject roi) {
        if(this.xCord < roi.xCord) {
            return -1;
        }
        else if(this.xCord > roi.xCord) {
            return 1;
        }
        else {
            return 0;
        }
    }
}
