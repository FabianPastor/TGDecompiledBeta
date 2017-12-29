package com.google.android.gms.internal;

import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.RemoteException;
import com.google.android.gms.dynamic.IObjectWrapper;
import com.google.android.gms.dynamic.zzk;
import com.google.android.gms.wallet.fragment.WalletFragmentOptions;

public final class zzdle extends zzeu implements zzdld {
    zzdle(IBinder iBinder) {
        super(iBinder, "com.google.android.gms.wallet.internal.IWalletDynamiteCreator");
    }

    public final zzdkw zza(IObjectWrapper iObjectWrapper, zzk com_google_android_gms_dynamic_zzk, WalletFragmentOptions walletFragmentOptions, zzdkz com_google_android_gms_internal_zzdkz) throws RemoteException {
        Parcel zzbe = zzbe();
        zzew.zza(zzbe, (IInterface) iObjectWrapper);
        zzew.zza(zzbe, (IInterface) com_google_android_gms_dynamic_zzk);
        zzew.zza(zzbe, (Parcelable) walletFragmentOptions);
        zzew.zza(zzbe, (IInterface) com_google_android_gms_internal_zzdkz);
        zzbe = zza(1, zzbe);
        zzdkw zzbq = zzdkx.zzbq(zzbe.readStrongBinder());
        zzbe.recycle();
        return zzbq;
    }
}
