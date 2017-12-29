package com.google.android.gms.common.internal;

import android.os.IBinder;
import android.os.Parcel;
import android.os.RemoteException;
import com.google.android.gms.dynamic.IObjectWrapper;
import com.google.android.gms.dynamic.IObjectWrapper.zza;
import com.google.android.gms.internal.zzeu;

public final class zzav extends zzeu implements zzat {
    zzav(IBinder iBinder) {
        super(iBinder, "com.google.android.gms.common.internal.ICertData");
    }

    public final IObjectWrapper zzaga() throws RemoteException {
        Parcel zza = zza(1, zzbe());
        IObjectWrapper zzaq = zza.zzaq(zza.readStrongBinder());
        zza.recycle();
        return zzaq;
    }

    public final int zzagb() throws RemoteException {
        Parcel zza = zza(2, zzbe());
        int readInt = zza.readInt();
        zza.recycle();
        return readInt;
    }
}
