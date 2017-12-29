package com.google.android.gms.internal;

import android.os.IBinder;
import android.os.Parcel;
import android.os.RemoteException;

public final class zzfq extends zzeu implements zzfo {
    zzfq(IBinder iBinder) {
        super(iBinder, "com.google.android.gms.ads.identifier.internal.IAdvertisingIdService");
    }

    public final String getId() throws RemoteException {
        Parcel zza = zza(1, zzbe());
        String readString = zza.readString();
        zza.recycle();
        return readString;
    }

    public final boolean zzb(boolean z) throws RemoteException {
        Parcel zzbe = zzbe();
        zzew.zza(zzbe, true);
        zzbe = zza(2, zzbe);
        boolean zza = zzew.zza(zzbe);
        zzbe.recycle();
        return zza;
    }
}
