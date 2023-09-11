package com.flyzebra.mdrvset.bean;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import com.flyzebra.utils.ByteUtil;

public class WifiP2PBean implements Parcelable {
    public String deviceAddress = "";
    public String deviceName = "";
    public String deviceIp = "";
    private long tid = 0;

    public WifiP2PBean() {

    }

    public long getTid() {
        if (tid == 0) {
            int index = deviceName.indexOf("_");
            tid = ByteUtil.sysIdToInt64(deviceName.substring(index + 1));
            return tid;
        } else {
            return tid;
        }
    }

    protected WifiP2PBean(Parcel in) {
        deviceAddress = in.readString();
        deviceName = in.readString();
        deviceIp = in.readString();
        tid = in.readLong();
    }

    public static final Creator<WifiP2PBean> CREATOR = new Creator<WifiP2PBean>() {
        @Override
        public WifiP2PBean createFromParcel(Parcel in) {
            return new WifiP2PBean(in);
        }

        @Override
        public WifiP2PBean[] newArray(int size) {
            return new WifiP2PBean[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(deviceAddress);
        dest.writeString(deviceName);
        dest.writeString(deviceIp);
        dest.writeLong(tid);
    }


    @NonNull
    @Override
    public String toString() {
        return "{" + "deviceAddress=" + deviceAddress +
                ", deviceName='" + deviceName + '\'' +
                ", deviceIP='" + deviceIp + '\'' + '}';
    }
}
