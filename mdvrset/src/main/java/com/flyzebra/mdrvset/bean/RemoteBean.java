package com.flyzebra.mdrvset.bean;

import android.os.Parcel;
import android.os.Parcelable;

public class RemoteBean implements Parcelable {
    public long mdvrId = 1;
    public String stid = "8K6H0EZ94E";
    public String model = "MD201";
    public int width = 1280;
    public int height = 720;
    public String expirytime;
    public long remaintime;
    public String ip;

    protected RemoteBean(Parcel in) {
        mdvrId = in.readLong();
        stid = in.readString();
        model = in.readString();
        width = in.readInt();
        height = in.readInt();
        expirytime = in.readString();
        remaintime = in.readLong();
        ip = in.readString();
    }

    public static final Creator<RemoteBean> CREATOR = new Creator<RemoteBean>() {
        @Override
        public RemoteBean createFromParcel(Parcel in) {
            return new RemoteBean(in);
        }

        @Override
        public RemoteBean[] newArray(int size) {
            return new RemoteBean[size];
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
