package com.google.android.gms.internal;

import android.os.Parcel;
import android.os.RemoteException;
import com.google.android.gms.common.api.Status;

public abstract class zzbdo extends zzee implements zzbdn {
    public zzbdo() {
        attachInterface(this, "com.google.android.gms.common.api.internal.IStatusCallback");
    }

    public boolean onTransact(int i, Parcel parcel, Parcel parcel2, int i2) throws RemoteException {
        if (zza(i, parcel, parcel2, i2)) {
            return true;
        }
        if (i != 1) {
            return false;
        }
        zzu((Status) zzef.zza(parcel, Status.CREATOR));
        return true;
    }
}
