package com.google.android.gms.internal;

import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.RemoteException;
import com.google.android.gms.common.internal.zzal;

public final class zzctt extends zzed implements zzcts {
    zzctt(IBinder iBinder) {
        super(iBinder, "com.google.android.gms.signin.internal.ISignInService");
    }

    public final void zza(zzal com_google_android_gms_common_internal_zzal, int i, boolean z) throws RemoteException {
        Parcel zzZ = zzZ();
        zzef.zza(zzZ, (IInterface) com_google_android_gms_common_internal_zzal);
        zzZ.writeInt(i);
        zzef.zza(zzZ, z);
        zzb(9, zzZ);
    }

    public final void zza(zzctv com_google_android_gms_internal_zzctv, zzctq com_google_android_gms_internal_zzctq) throws RemoteException {
        Parcel zzZ = zzZ();
        zzef.zza(zzZ, (Parcelable) com_google_android_gms_internal_zzctv);
        zzef.zza(zzZ, (IInterface) com_google_android_gms_internal_zzctq);
        zzb(12, zzZ);
    }

    public final void zzbv(int i) throws RemoteException {
        Parcel zzZ = zzZ();
        zzZ.writeInt(i);
        zzb(7, zzZ);
    }
}
