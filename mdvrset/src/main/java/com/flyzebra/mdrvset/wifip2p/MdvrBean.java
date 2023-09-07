package com.flyzebra.mdrvset.wifip2p;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import com.flyzebra.utils.ByteUtil;

public class MdvrBean implements Parcelable {
    public String model = "MD201";
    public int width = 1280;
    public int height = 720;
    public String deviceAddress = "";
    public String deviceName = "";
    public String deviceIP = "";
    private long tid = 0;

    public MdvrBean() {

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

    protected MdvrBean(Parcel in) {
        deviceAddress = in.readString();
        deviceName = in.readString();
        deviceIP = in.readString();
        tid = in.readLong();
        model = in.readString();
        width = in.readInt();
        height = in.readInt();
    }

    public static final Creator<MdvrBean> CREATOR = new Creator<MdvrBean>() {
        @Override
        public MdvrBean createFromParcel(Parcel in) {
            return new MdvrBean(in);
        }

        @Override
        public MdvrBean[] newArray(int size) {
            return new MdvrBean[size];
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
        dest.writeString(deviceIP);
        dest.writeLong(tid);
        dest.writeString(model);
        dest.writeInt(width);
        dest.writeInt(height);
    }


    @NonNull
    @Override
    public String toString() {
        return "{" + "deviceAddress=" + deviceAddress +
                ", deviceName='" + deviceName + '\'' +
                ", deviceIP='" + deviceIP + '\'' + '}';
    }
}
