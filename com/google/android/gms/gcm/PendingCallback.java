package com.google.android.gms.gcm;

import android.os.IBinder;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.ReflectedParcelable;

public class PendingCallback implements Parcelable, ReflectedParcelable {
    public static final Creator<PendingCallback> CREATOR = new Creator<PendingCallback>() {
        public /* synthetic */ Object createFromParcel(Parcel parcel) {
            return zzna(parcel);
        }

        public /* synthetic */ Object[] newArray(int i) {
            return zztp(i);
        }

        public PendingCallback zzna(Parcel parcel) {
            return new PendingCallback(parcel);
        }

        public PendingCallback[] zztp(int i) {
            return new PendingCallback[i];
        }
    };
    final IBinder DI;

    public PendingCallback(Parcel parcel) {
        this.DI = parcel.readStrongBinder();
    }

    public int describeContents() {
        return 0;
    }

    public IBinder getIBinder() {
        return this.DI;
    }

    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeStrongBinder(this.DI);
    }
}
