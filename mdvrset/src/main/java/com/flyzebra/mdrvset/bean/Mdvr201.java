package com.flyzebra.mdrvset.bean;

import android.os.Parcel;
import android.os.Parcelable;

public class Mdvr201 implements Parcelable {
    public long mdvrId = 1;
    public String stid = "8K6H0EZ94E";
    public String model = "MD201";
    public int width = 1280;
    public int height = 720;
    public String expirytime;
    public long remaintime;
    public String ip;

    protected Mdvr201(Parcel in) {
        mdvrId = in.readLong();
        stid = in.readString();
        model = in.readString();
        width = in.readInt();
        height = in.readInt();
        expirytime = in.readString();
        remaintime = in.readLong();
        ip = in.readString();
    }

    public static final Creator<Mdvr201> CREATOR = new Creator<Mdvr201>() {
        @Override
        public Mdvr201 createFromParcel(Parcel in) {
            return new Mdvr201(in);
        }

        @Override
        public Mdvr201[] newArray(int size) {
            return new Mdvr201[size];
        }
    };

    @Override
    public String toString() {
        return "Phone{" +
                "phoneId=" + mdvrId +
                ", sTid='" + stid + '\'' +
                ", model='" + model + '\'' +
                ", width=" + width +
                ", height=" + height +
                ", expirytime='" + expirytime + '\'' +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(mdvrId);
        dest.writeString(stid);
        dest.writeString(model);
        dest.writeInt(width);
        dest.writeInt(height);
        dest.writeString(expirytime);
        dest.writeLong(remaintime);
        dest.writeString(ip);
    }
}
