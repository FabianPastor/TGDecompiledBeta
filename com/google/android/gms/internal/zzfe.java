package com.google.android.gms.internal;

import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;

public abstract class zzfe extends zzee implements zzfd {
    public static zzfd zzc(IBinder iBinder) {
        if (iBinder == null) {
            return null;
        }
        IInterface queryLocalInterface = iBinder.queryLocalInterface("com.google.android.gms.ads.identifier.internal.IAdvertisingIdService");
        return queryLocalInterface instanceof zzfd ? (zzfd) queryLocalInterface : new zzff(iBinder);
    }

    public boolean onTransact(int i, Parcel parcel, Parcel parcel2, int i2) throws RemoteException {
        if (zza(i, parcel, parcel2, i2)) {
            return true;
        }
        String id;
        switch (i) {
            case 1:
                id = getId();
                parcel2.writeNoException();
                parcel2.writeString(id);
                return true;
            case 2:
                boolean zzb = zzb(zzef.zza(parcel));
                parcel2.writeNoException();
                zzef.zza(parcel2, zzb);
                return true;
            case 3:
                id = zzq(parcel.readString());
                parcel2.writeNoException();
                parcel2.writeString(id);
                return true;
            case 4:
                zzc(parcel.readString(), zzef.zza(parcel));
                parcel2.writeNoException();
                return true;
            default:
                return false;
        }
    }
}
