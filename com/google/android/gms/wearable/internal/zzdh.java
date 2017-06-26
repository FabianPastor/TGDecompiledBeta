package com.google.android.gms.wearable.internal;

import android.os.Parcel;
import android.os.RemoteException;
import com.google.android.gms.internal.zzee;

public abstract class zzdh extends zzee implements zzdg {
    public zzdh() {
        attachInterface(this, "com.google.android.gms.wearable.internal.IChannelStreamCallbacks");
    }

    public boolean onTransact(int i, Parcel parcel, Parcel parcel2, int i2) throws RemoteException {
        if (zza(i, parcel, parcel2, i2)) {
            return true;
        }
        if (i != 2) {
            return false;
        }
        zzm(parcel.readInt(), parcel.readInt());
        parcel2.writeNoException();
        return true;
    }
}
