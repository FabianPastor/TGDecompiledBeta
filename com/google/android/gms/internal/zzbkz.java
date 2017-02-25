package com.google.android.gms.internal;

import android.app.Activity;
import android.os.IBinder;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.dynamic.zzc;
import com.google.android.gms.dynamic.zzd;
import com.google.android.gms.dynamic.zzf;
import com.google.android.gms.internal.zzbkv.zza;
import com.google.android.gms.wallet.fragment.WalletFragmentOptions;

public class zzbkz extends zzf<zzbkv> {
    private static zzbkz zzbSx;

    protected zzbkz() {
        super("com.google.android.gms.wallet.dynamite.WalletDynamiteCreatorImpl");
    }

    private static zzbkz zzUa() {
        if (zzbSx == null) {
            zzbSx = new zzbkz();
        }
        return zzbSx;
    }

    public static zzbks zza(Activity activity, zzc com_google_android_gms_dynamic_zzc, WalletFragmentOptions walletFragmentOptions, zzbkt com_google_android_gms_internal_zzbkt) throws GooglePlayServicesNotAvailableException {
        int isGooglePlayServicesAvailable = GooglePlayServicesUtil.isGooglePlayServicesAvailable(activity);
        if (isGooglePlayServicesAvailable != 0) {
            throw new GooglePlayServicesNotAvailableException(isGooglePlayServicesAvailable);
        }
        try {
            return ((zzbkv) zzUa().zzbl(activity)).zza(zzd.zzA(activity), com_google_android_gms_dynamic_zzc, walletFragmentOptions, com_google_android_gms_internal_zzbkt);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        } catch (Throwable e2) {
            throw new RuntimeException(e2);
        }
    }

    protected /* synthetic */ Object zzc(IBinder iBinder) {
        return zzfz(iBinder);
    }

    protected zzbkv zzfz(IBinder iBinder) {
        return zza.zzfw(iBinder);
    }
}
