package com.google.android.gms.internal;

import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.RemoteException;
import com.google.android.gms.dynamic.IObjectWrapper;
import com.google.android.gms.dynamic.zzk;
import com.google.android.gms.wallet.fragment.WalletFragmentOptions;

public final class gh extends zzed implements gg {
    gh(IBinder iBinder) {
        super(iBinder, "com.google.android.gms.wallet.internal.IWalletDynamiteCreator");
    }

    public final fz zza(IObjectWrapper iObjectWrapper, zzk com_google_android_gms_dynamic_zzk, WalletFragmentOptions walletFragmentOptions, gc gcVar) throws RemoteException {
        Parcel zzZ = zzZ();
        zzef.zza(zzZ, (IInterface) iObjectWrapper);
        zzef.zza(zzZ, (IInterface) com_google_android_gms_dynamic_zzk);
        zzef.zza(zzZ, (Parcelable) walletFragmentOptions);
        zzef.zza(zzZ, (IInterface) gcVar);
        zzZ = zza(1, zzZ);
        fz zzal = ga.zzal(zzZ.readStrongBinder());
        zzZ.recycle();
        return zzal;
    }
}
