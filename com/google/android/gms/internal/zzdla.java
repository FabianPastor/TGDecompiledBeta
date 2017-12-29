package com.google.android.gms.internal;

import android.os.Bundle;
import android.os.Parcel;
import android.os.RemoteException;

public abstract class zzdla extends zzev implements zzdkz {
    public zzdla() {
        attachInterface(this, "com.google.android.gms.wallet.fragment.internal.IWalletFragmentStateListener");
    }

    public boolean onTransact(int i, Parcel parcel, Parcel parcel2, int i2) throws RemoteException {
        if (zza(i, parcel, parcel2, i2)) {
            return true;
        }
        if (i != 2) {
            return false;
        }
        zza(parcel.readInt(), parcel.readInt(), (Bundle) zzew.zza(parcel, Bundle.CREATOR));
        parcel2.writeNoException();
        return true;
    }
}
