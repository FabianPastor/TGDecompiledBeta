package com.google.android.gms.wearable.internal;

import android.os.Parcel;
import android.os.RemoteException;
import com.google.android.gms.common.data.DataHolder;
import com.google.android.gms.internal.zzev;
import com.google.android.gms.internal.zzew;

public abstract class zzen extends zzev implements zzem {
    public zzen() {
        attachInterface(this, "com.google.android.gms.wearable.internal.IWearableListener");
    }

    public boolean onTransact(int i, Parcel parcel, Parcel parcel2, int i2) throws RemoteException {
        if (zza(i, parcel, parcel2, i2)) {
            return true;
        }
        switch (i) {
            case 1:
                zzas((DataHolder) zzew.zza(parcel, DataHolder.CREATOR));
                break;
            case 2:
                zza((zzfe) zzew.zza(parcel, zzfe.CREATOR));
                break;
            case 3:
                zza((zzfo) zzew.zza(parcel, zzfo.CREATOR));
                break;
            case 4:
                zzb((zzfo) zzew.zza(parcel, zzfo.CREATOR));
                break;
            case 5:
                onConnectedNodes(parcel.createTypedArrayList(zzfo.CREATOR));
                break;
            case 6:
                zza((zzl) zzew.zza(parcel, zzl.CREATOR));
                break;
            case 7:
                zza((zzaw) zzew.zza(parcel, zzaw.CREATOR));
                break;
            case 8:
                zza((zzah) zzew.zza(parcel, zzah.CREATOR));
                break;
            case 9:
                zza((zzi) zzew.zza(parcel, zzi.CREATOR));
                break;
            default:
                return false;
        }
        return true;
    }
}
