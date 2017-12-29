package com.google.android.gms.internal;

import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.RemoteException;
import com.google.android.gms.common.internal.zzan;

public final class zzcxm extends zzeu implements zzcxl {
    zzcxm(IBinder iBinder) {
        super(iBinder, "com.google.android.gms.signin.internal.ISignInService");
    }

    public final void zza(zzan com_google_android_gms_common_internal_zzan, int i, boolean z) throws RemoteException {
        Parcel zzbe = zzbe();
        zzew.zza(zzbe, (IInterface) com_google_android_gms_common_internal_zzan);
        zzbe.writeInt(i);
        zzew.zza(zzbe, z);
        zzb(9, zzbe);
    }

    public final void zza(zzcxo com_google_android_gms_internal_zzcxo, zzcxj com_google_android_gms_internal_zzcxj) throws RemoteException {
        Parcel zzbe = zzbe();
        zzew.zza(zzbe, (Parcelable) com_google_android_gms_internal_zzcxo);
        zzew.zza(zzbe, (IInterface) com_google_android_gms_internal_zzcxj);
        zzb(12, zzbe);
    }

    public final void zzeh(int i) throws RemoteException {
        Parcel zzbe = zzbe();
        zzbe.writeInt(i);
        zzb(7, zzbe);
    }
}
