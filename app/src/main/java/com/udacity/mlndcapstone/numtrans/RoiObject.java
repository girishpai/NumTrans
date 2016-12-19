package com.udacity.mlndcapstone.numtrans;

import android.graphics.Bitmap;

/**
 * Created by girishpai on 11/4/16.
 * This class stores the X-coordinate of all the rectangular boxes which holds the digits
 * This class implements the Comparable interface, which makes it possible to use Java's
 * Collections.sort.
 */
public class RoiObject implements Comparable<RoiObject> {
    int xCord;
    Bitmap bmp;

    public RoiObject(int xCord,Bitmap bmp) {
        this.xCord = xCord;
        this.bmp = bmp;
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
