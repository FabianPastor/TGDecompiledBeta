package com.google.android.gms.internal;

import android.os.Bundle;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.RemoteException;
import com.google.android.gms.wallet.FullWalletRequest;
import com.google.android.gms.wallet.IsReadyToPayRequest;

public final class zzdlc extends zzeu implements zzdlb {
    zzdlc(IBinder iBinder) {
        super(iBinder, "com.google.android.gms.wallet.internal.IOwService");
    }

    public final void zza(FullWalletRequest fullWalletRequest, Bundle bundle, zzdlf com_google_android_gms_internal_zzdlf) throws RemoteException {
        Parcel zzbe = zzbe();
        zzew.zza(zzbe, (Parcelable) fullWalletRequest);
        zzew.zza(zzbe, (Parcelable) bundle);
        zzew.zza(zzbe, (IInterface) com_google_android_gms_internal_zzdlf);
        zzc(2, zzbe);
    }

    public final void zza(IsReadyToPayRequest isReadyToPayRequest, Bundle bundle, zzdlf com_google_android_gms_internal_zzdlf) throws RemoteException {
        Parcel zzbe = zzbe();
        zzew.zza(zzbe, (Parcelable) isReadyToPayRequest);
        zzew.zza(zzbe, (Parcelable) bundle);
        zzew.zza(zzbe, (IInterface) com_google_android_gms_internal_zzdlf);
        zzc(14, zzbe);
    }
}
