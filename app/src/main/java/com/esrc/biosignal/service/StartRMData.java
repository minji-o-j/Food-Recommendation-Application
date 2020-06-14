package com.esrc.biosignal.service;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by lhw48 on 2016-06-22.
 */
public class StartRMData implements Parcelable {
    private Integer uniqueId;
    private String address;

    public StartRMData(Integer uniqueId) {
        this.uniqueId = uniqueId;
        address = null;
    }

    public StartRMData(Integer uniqueId, String address) {
        this.uniqueId = uniqueId;
        this.address = address;
    }

    public Integer getUniqueId() {
        return uniqueId;
    }

    public String getAddress() {
        return address;
    }

    public static final Creator<StartRMData> CREATOR = new Creator<StartRMData>() {
        @Override
        public StartRMData createFromParcel(Parcel in) {
            return new StartRMData(in);
        }

        @Override
        public StartRMData[] newArray(int size) {
            return new StartRMData[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeInt(uniqueId);
        out.writeString(address);
    }

    protected StartRMData(Parcel in) {
        uniqueId = in.readInt();
        address = in.readString();
    }
}
