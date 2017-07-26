package com.google.android.gms.internal;

import android.os.Bundle;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.RemoteException;
import com.google.android.gms.wallet.FullWalletRequest;
import com.google.android.gms.wallet.IsReadyToPayRequest;
import com.google.android.gms.wallet.MaskedWalletRequest;
import com.google.android.gms.wallet.NotifyTransactionStatusRequest;

public final class gg extends zzed implements gf {
    gg(IBinder iBinder) {
        super(iBinder, "com.google.android.gms.wallet.internal.IOwService");
    }

    public final void zza(Bundle bundle, gj gjVar) throws RemoteException {
        Parcel zzZ = zzZ();
        zzef.zza(zzZ, (Parcelable) bundle);
        zzef.zza(zzZ, (IInterface) gjVar);
        zzc(5, zzZ);
    }

    public final void zza(FullWalletRequest fullWalletRequest, Bundle bundle, gj gjVar) throws RemoteException {
        Parcel zzZ = zzZ();
        zzef.zza(zzZ, (Parcelable) fullWalletRequest);
        zzef.zza(zzZ, (Parcelable) bundle);
        zzef.zza(zzZ, (IInterface) gjVar);
        zzc(2, zzZ);
    }

    public final void zza(IsReadyToPayRequest isReadyToPayRequest, Bundle bundle, gj gjVar) throws RemoteException {
        Parcel zzZ = zzZ();
        zzef.zza(zzZ, (Parcelable) isReadyToPayRequest);
        zzef.zza(zzZ, (Parcelable) bundle);
        zzef.zza(zzZ, (IInterface) gjVar);
        zzc(14, zzZ);
    }

    public final void zza(MaskedWalletRequest maskedWalletRequest, Bundle bundle, gj gjVar) throws RemoteException {
        Parcel zzZ = zzZ();
        zzef.zza(zzZ, (Parcelable) maskedWalletRequest);
        zzef.zza(zzZ, (Parcelable) bundle);
        zzef.zza(zzZ, (IInterface) gjVar);
        zzc(1, zzZ);
    }

    public final void zza(NotifyTransactionStatusRequest notifyTransactionStatusRequest, Bundle bundle) throws RemoteException {
        Parcel zzZ = zzZ();
        zzef.zza(zzZ, (Parcelable) notifyTransactionStatusRequest);
        zzef.zza(zzZ, (Parcelable) bundle);
        zzc(4, zzZ);
    }

    public final void zza(String str, String str2, Bundle bundle, gj gjVar) throws RemoteException {
        Parcel zzZ = zzZ();
        zzZ.writeString(str);
        zzZ.writeString(str2);
        zzef.zza(zzZ, (Parcelable) bundle);
        zzef.zza(zzZ, (IInterface) gjVar);
        zzc(3, zzZ);
    }

    public final void zzb(Bundle bundle, gj gjVar) throws RemoteException {
        Parcel zzZ = zzZ();
        zzef.zza(zzZ, (Parcelable) bundle);
        zzef.zza(zzZ, (IInterface) gjVar);
        zzc(11, zzZ);
    }
}
