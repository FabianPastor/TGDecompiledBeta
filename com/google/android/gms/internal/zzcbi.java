package com.google.android.gms.internal;

import android.os.Bundle;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.RemoteException;
import com.google.android.gms.identity.intents.UserAddressRequest;

public final class zzcbi extends zzed implements zzcbh {
    zzcbi(IBinder iBinder) {
        super(iBinder, "com.google.android.gms.identity.intents.internal.IAddressService");
    }

    public final void zza(zzcbf com_google_android_gms_internal_zzcbf, UserAddressRequest userAddressRequest, Bundle bundle) throws RemoteException {
        Parcel zzZ = zzZ();
        zzef.zza(zzZ, (IInterface) com_google_android_gms_internal_zzcbf);
        zzef.zza(zzZ, (Parcelable) userAddressRequest);
        zzef.zza(zzZ, (Parcelable) bundle);
        zzb(2, zzZ);
    }
}
