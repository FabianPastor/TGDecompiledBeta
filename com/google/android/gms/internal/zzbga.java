package com.google.android.gms.internal;

import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;

public final class zzbga extends zzed implements zzbfz {
    zzbga(IBinder iBinder) {
        super(iBinder, "com.google.android.gms.common.internal.service.ICommonService");
    }

    public final void zza(zzbfx com_google_android_gms_internal_zzbfx) throws RemoteException {
        Parcel zzZ = zzZ();
        zzef.zza(zzZ, (IInterface) com_google_android_gms_internal_zzbfx);
        zzc(1, zzZ);
    }
}
