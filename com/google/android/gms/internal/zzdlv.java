package com.google.android.gms.internal;

import android.app.Activity;
import android.os.IBinder;
import android.os.IInterface;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.dynamic.zzk;
import com.google.android.gms.dynamic.zzn;
import com.google.android.gms.dynamic.zzp;
import com.google.android.gms.wallet.fragment.WalletFragmentOptions;

public final class zzdlv extends zzp<zzdld> {
    private static zzdlv zzlfp;

    protected zzdlv() {
        super("com.google.android.gms.wallet.dynamite.WalletDynamiteCreatorImpl");
    }

    public static zzdkw zza(Activity activity, zzk com_google_android_gms_dynamic_zzk, WalletFragmentOptions walletFragmentOptions, zzdkz com_google_android_gms_internal_zzdkz) throws GooglePlayServicesNotAvailableException {
        int isGooglePlayServicesAvailable = GooglePlayServicesUtil.isGooglePlayServicesAvailable(activity);
        if (isGooglePlayServicesAvailable != 0) {
            throw new GooglePlayServicesNotAvailableException(isGooglePlayServicesAvailable);
        }
        try {
            if (zzlfp == null) {
                zzlfp = new zzdlv();
            }
            return ((zzdld) zzlfp.zzde(activity)).zza(zzn.zzz(activity), com_google_android_gms_dynamic_zzk, walletFragmentOptions, com_google_android_gms_internal_zzdkz);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        } catch (Throwable e2) {
            throw new RuntimeException(e2);
        }
    }

    protected final /* synthetic */ Object zze(IBinder iBinder) {
        if (iBinder == null) {
            return null;
        }
        IInterface queryLocalInterface = iBinder.queryLocalInterface("com.google.android.gms.wallet.internal.IWalletDynamiteCreator");
        return queryLocalInterface instanceof zzdld ? (zzdld) queryLocalInterface : new zzdle(iBinder);
    }
}
