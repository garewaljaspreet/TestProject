package com.developers.uberanimation;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;


/**
 * Created by navdeepg on 2/11/2016.
 */
public class BeansMain {

    public ArrayList<BeansSnapped> getSnappedPoints() {
        return snappedPoints;
    }

    public void setSnappedPoints(ArrayList<BeansSnapped> snappedPoints) {
        this.snappedPoints = snappedPoints;
    }

    ArrayList<BeansSnapped> snappedPoints;

}