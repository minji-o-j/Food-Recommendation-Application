package com.esrc.biosignal.service;

import android.os.Parcel;
import android.os.Parcelable;

import com.esrc.biosignal.libs.State;

/**
 * Created by lhw48 on 2016-06-22.
 */
public class StateData extends State implements Parcelable {

    public StateData(State state) {
        super(state);
    }

    public static final Creator<StateData> CREATOR = new Creator<StateData>() {
        @Override
        public StateData createFromParcel(Parcel in) {
            return new StateData(in);
        }

        @Override
        public StateData[] newArray(int size) {
            return new StateData[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeInt(state);
    }

    protected StateData(Parcel in) {
        state = in.readInt();
    }
}
