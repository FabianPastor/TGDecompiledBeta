package com.google.android.gms.gcm;

import android.os.IBinder;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.ReflectedParcelable;

public class PendingCallback implements Parcelable, ReflectedParcelable {
    public static final Creator<PendingCallback> CREATOR = new Creator<PendingCallback>() {
        public /* synthetic */ Object createFromParcel(Parcel parcel) {
            return zzmx(parcel);
        }

        public /* synthetic */ Object[] newArray(int i) {
            return zzto(i);
        }

        public PendingCallback zzmx(Parcel parcel) {
            return new PendingCallback(parcel);
        }

        public PendingCallback[] zzto(int i) {
            return new PendingCallback[i];
        }
    };
    final IBinder Bz;

    public PendingCallback(Parcel parcel) {
        this.Bz = parcel.readStrongBinder();
    }

    public int describeContents() {
        return 0;
    }

    public IBinder getIBinder() {
        return this.Bz;
    }

    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeStrongBinder(this.Bz);
    }
}
