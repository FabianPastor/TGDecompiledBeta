package com.google.android.gms.internal;

import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.RemoteException;
import com.google.android.gms.dynamic.IObjectWrapper;
import com.google.android.gms.dynamic.IObjectWrapper.zza;
import com.google.android.gms.wallet.MaskedWallet;
import com.google.android.gms.wallet.MaskedWalletRequest;
import com.google.android.gms.wallet.fragment.WalletFragmentInitParams;
import com.google.android.gms.wallet.fragment.WalletFragmentOptions;

public final class gc extends zzed implements ga {
    gc(IBinder iBinder) {
        super(iBinder, "com.google.android.gms.wallet.fragment.internal.IWalletFragmentDelegate");
    }

    public final int getState() throws RemoteException {
        Parcel zza = zza(13, zzZ());
        int readInt = zza.readInt();
        zza.recycle();
        return readInt;
    }

    public final void initialize(WalletFragmentInitParams walletFragmentInitParams) throws RemoteException {
        Parcel zzZ = zzZ();
        zzef.zza(zzZ, (Parcelable) walletFragmentInitParams);
        zzb(10, zzZ);
    }

    public final void onActivityResult(int i, int i2, Intent intent) throws RemoteException {
        Parcel zzZ = zzZ();
        zzZ.writeInt(i);
        zzZ.writeInt(i2);
        zzef.zza(zzZ, (Parcelable) intent);
        zzb(9, zzZ);
    }

    public final void onCreate(Bundle bundle) throws RemoteException {
        Parcel zzZ = zzZ();
        zzef.zza(zzZ, (Parcelable) bundle);
        zzb(2, zzZ);
    }

    public final IObjectWrapper onCreateView(IObjectWrapper iObjectWrapper, IObjectWrapper iObjectWrapper2, Bundle bundle) throws RemoteException {
        Parcel zzZ = zzZ();
        zzef.zza(zzZ, (IInterface) iObjectWrapper);
        zzef.zza(zzZ, (IInterface) iObjectWrapper2);
        zzef.zza(zzZ, (Parcelable) bundle);
        zzZ = zza(3, zzZ);
        IObjectWrapper zzM = zza.zzM(zzZ.readStrongBinder());
        zzZ.recycle();
        return zzM;
    }

    public final void onPause() throws RemoteException {
        zzb(6, zzZ());
    }

    public final void onResume() throws RemoteException {
        zzb(5, zzZ());
    }

    public final void onSaveInstanceState(Bundle bundle) throws RemoteException {
        Parcel zzZ = zzZ();
        zzef.zza(zzZ, (Parcelable) bundle);
        zzZ = zza(8, zzZ);
        if (zzZ.readInt() != 0) {
            bundle.readFromParcel(zzZ);
        }
        zzZ.recycle();
    }

    public final void onStart() throws RemoteException {
        zzb(4, zzZ());
    }

    public final void onStop() throws RemoteException {
        zzb(7, zzZ());
    }

    public final void setEnabled(boolean z) throws RemoteException {
        Parcel zzZ = zzZ();
        zzef.zza(zzZ, z);
        zzb(12, zzZ);
    }

    public final void updateMaskedWallet(MaskedWallet maskedWallet) throws RemoteException {
        Parcel zzZ = zzZ();
        zzef.zza(zzZ, (Parcelable) maskedWallet);
        zzb(14, zzZ);
    }

    public final void updateMaskedWalletRequest(MaskedWalletRequest maskedWalletRequest) throws RemoteException {
        Parcel zzZ = zzZ();
        zzef.zza(zzZ, (Parcelable) maskedWalletRequest);
        zzb(11, zzZ);
    }

    public final void zza(IObjectWrapper iObjectWrapper, WalletFragmentOptions walletFragmentOptions, Bundle bundle) throws RemoteException {
        Parcel zzZ = zzZ();
        zzef.zza(zzZ, (IInterface) iObjectWrapper);
        zzef.zza(zzZ, (Parcelable) walletFragmentOptions);
        zzef.zza(zzZ, (Parcelable) bundle);
        zzb(1, zzZ);
    }
}
