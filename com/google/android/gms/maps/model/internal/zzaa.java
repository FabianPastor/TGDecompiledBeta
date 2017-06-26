package com.google.android.gms.maps.model.internal;

import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.RemoteException;
import com.google.android.gms.internal.zzee;
import com.google.android.gms.internal.zzef;

public abstract class zzaa extends zzee implements zzz {
    public zzaa() {
        attachInterface(this, "com.google.android.gms.maps.model.internal.ITileProviderDelegate");
    }

    public static zzz zzaj(IBinder iBinder) {
        if (iBinder == null) {
            return null;
        }
        IInterface queryLocalInterface = iBinder.queryLocalInterface("com.google.android.gms.maps.model.internal.ITileProviderDelegate");
        return queryLocalInterface instanceof zzz ? (zzz) queryLocalInterface : new zzab(iBinder);
    }

    public boolean onTransact(int i, Parcel parcel, Parcel parcel2, int i2) throws RemoteException {
        if (zza(i, parcel, parcel2, i2)) {
            return true;
        }
        if (i != 1) {
            return false;
        }
        Parcelable tile = getTile(parcel.readInt(), parcel.readInt(), parcel.readInt());
        parcel2.writeNoException();
        zzef.zzb(parcel2, tile);
        return true;
    }
}
