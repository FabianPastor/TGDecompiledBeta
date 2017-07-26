package com.google.android.gms.internal;

import android.os.Bundle;
import android.os.Parcel;
import android.os.RemoteException;

public abstract class zzcbh extends zzee implements zzcbg {
    public zzcbh() {
        attachInterface(this, "com.google.android.gms.identity.intents.internal.IAddressCallbacks");
    }

    public boolean onTransact(int i, Parcel parcel, Parcel parcel2, int i2) throws RemoteException {
        if (zza(i, parcel, parcel2, i2)) {
            return true;
        }
        if (i != 2) {
            return false;
        }
        zze(parcel.readInt(), (Bundle) zzef.zza(parcel, Bundle.CREATOR));
        parcel2.writeNoException();
        return true;
    }
}
