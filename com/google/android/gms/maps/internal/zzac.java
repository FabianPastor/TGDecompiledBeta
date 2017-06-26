package com.google.android.gms.maps.internal;

import android.os.Parcel;
import android.os.RemoteException;
import com.google.android.gms.internal.zzee;
import com.google.android.gms.maps.model.internal.zzq;

public abstract class zzac extends zzee implements zzab {
    public zzac() {
        attachInterface(this, "com.google.android.gms.maps.internal.IOnInfoWindowClickListener");
    }

    public boolean onTransact(int i, Parcel parcel, Parcel parcel2, int i2) throws RemoteException {
        if (zza(i, parcel, parcel2, i2)) {
            return true;
        }
        if (i != 1) {
            return false;
        }
        zze(zzq.zzaf(parcel.readStrongBinder()));
        parcel2.writeNoException();
        return true;
    }
}
