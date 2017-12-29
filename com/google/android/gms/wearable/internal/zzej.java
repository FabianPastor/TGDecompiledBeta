package com.google.android.gms.wearable.internal;

import android.os.Parcel;
import android.os.RemoteException;
import com.google.android.gms.internal.zzev;

public abstract class zzej extends zzev implements zzei {
    public zzej() {
        attachInterface(this, "com.google.android.gms.wearable.internal.IChannelStreamCallbacks");
    }

    public boolean onTransact(int i, Parcel parcel, Parcel parcel2, int i2) throws RemoteException {
        if (zza(i, parcel, parcel2, i2)) {
            return true;
        }
        if (i != 2) {
            return false;
        }
        zzs(parcel.readInt(), parcel.readInt());
        parcel2.writeNoException();
        return true;
    }
}
