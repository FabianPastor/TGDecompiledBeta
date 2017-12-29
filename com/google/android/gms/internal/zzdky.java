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

public final class zzdky extends zzeu implements zzdkw {
    zzdky(IBinder iBinder) {
        super(iBinder, "com.google.android.gms.wallet.fragment.internal.IWalletFragmentDelegate");
    }

    public final void initialize(WalletFragmentInitParams walletFragmentInitParams) throws RemoteException {
        Parcel zzbe = zzbe();
        zzew.zza(zzbe, (Parcelable) walletFragmentInitParams);
        zzb(10, zzbe);
    }

    public final void onActivityResult(int i, int i2, Intent intent) throws RemoteException {
        Parcel zzbe = zzbe();
        zzbe.writeInt(i);
        zzbe.writeInt(i2);
        zzew.zza(zzbe, (Parcelable) intent);
        zzb(9, zzbe);
    }

    public final void onCreate(Bundle bundle) throws RemoteException {
        Parcel zzbe = zzbe();
        zzew.zza(zzbe, (Parcelable) bundle);
        zzb(2, zzbe);
    }

    public final IObjectWrapper onCreateView(IObjectWrapper iObjectWrapper, IObjectWrapper iObjectWrapper2, Bundle bundle) throws RemoteException {
        Parcel zzbe = zzbe();
        zzew.zza(zzbe, (IInterface) iObjectWrapper);
        zzew.zza(zzbe, (IInterface) iObjectWrapper2);
        zzew.zza(zzbe, (Parcelable) bundle);
        zzbe = zza(3, zzbe);
        IObjectWrapper zzaq = zza.zzaq(zzbe.readStrongBinder());
        zzbe.recycle();
        return zzaq;
    }

    public final void onPause() throws RemoteException {
        zzb(6, zzbe());
    }

    public final void onResume() throws RemoteException {
        zzb(5, zzbe());
    }

    public final void onSaveInstanceState(Bundle bundle) throws RemoteException {
        Parcel zzbe = zzbe();
        zzew.zza(zzbe, (Parcelable) bundle);
        zzbe = zza(8, zzbe);
        if (zzbe.readInt() != 0) {
            bundle.readFromParcel(zzbe);
        }
        zzbe.recycle();
    }

    public final void onStart() throws RemoteException {
        zzb(4, zzbe());
    }

    public final void onStop() throws RemoteException {
        zzb(7, zzbe());
    }

    public final void setEnabled(boolean z) throws RemoteException {
        Parcel zzbe = zzbe();
        zzew.zza(zzbe, z);
        zzb(12, zzbe);
    }

    public final void updateMaskedWallet(MaskedWallet maskedWallet) throws RemoteException {
        Parcel zzbe = zzbe();
        zzew.zza(zzbe, (Parcelable) maskedWallet);
        zzb(14, zzbe);
    }

    public final void updateMaskedWalletRequest(MaskedWalletRequest maskedWalletRequest) throws RemoteException {
        Parcel zzbe = zzbe();
        zzew.zza(zzbe, (Parcelable) maskedWalletRequest);
        zzb(11, zzbe);
    }

    public final void zza(IObjectWrapper iObjectWrapper, WalletFragmentOptions walletFragmentOptions, Bundle bundle) throws RemoteException {
        Parcel zzbe = zzbe();
        zzew.zza(zzbe, (IInterface) iObjectWrapper);
        zzew.zza(zzbe, (Parcelable) walletFragmentOptions);
        zzew.zza(zzbe, (Parcelable) bundle);
        zzb(1, zzbe);
    }
}
