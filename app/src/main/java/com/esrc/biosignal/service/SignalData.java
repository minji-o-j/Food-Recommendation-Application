package com.esrc.biosignal.service;

import android.os.Parcel;
import android.os.Parcelable;

import com.esrc.biosignal.libs.Signal;

/**
 * Created by lhw48 on 2016-06-22.
 */
public class SignalData extends Signal implements Parcelable {

    public SignalData(Signal signal) {
        super(signal);
    }

    public static final Parcelable.Creator<SignalData> CREATOR = new Parcelable.Creator<SignalData>() {
        @Override
        public SignalData createFromParcel(Parcel in) {
            return new SignalData(in);
        }

        @Override
        public SignalData[] newArray(int size) {
            return new SignalData[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeDouble(value);
    }

    protected SignalData(Parcel in) {
        value = in.readDouble();
    }
}