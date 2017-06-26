package com.google.android.gms.common.internal;

import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;
import com.google.android.gms.internal.zzee;
import com.google.android.gms.internal.zzef;

public abstract class zzas extends zzee implements zzar {
    public zzas() {
        attachInterface(this, "com.google.android.gms.common.internal.ICertData");
    }

    public static zzar zzI(IBinder iBinder) {
        if (iBinder == null) {
            return null;
        }
        IInterface queryLocalInterface = iBinder.queryLocalInterface("com.google.android.gms.common.internal.ICertData");
        return queryLocalInterface instanceof zzar ? (zzar) queryLocalInterface : new zzat(iBinder);
    }

    public boolean onTransact(int i, Parcel parcel, Parcel parcel2, int i2) throws RemoteException {
        if (zza(i, parcel, parcel2, i2)) {
            return true;
        }
        switch (i) {
            case 1:
                IInterface zzoY = zzoY();
                parcel2.writeNoException();
                zzef.zza(parcel2, zzoY);
                return true;
            case 2:
                int zzoZ = zzoZ();
                parcel2.writeNoException();
                parcel2.writeInt(zzoZ);
                return true;
            default:
                return false;
        }
    }
}
