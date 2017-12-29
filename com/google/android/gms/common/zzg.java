package com.google.android.gms.common;

import android.content.Context;
import android.util.Log;
import com.google.android.gms.common.internal.zzba;
import com.google.android.gms.common.internal.zzbb;
import com.google.android.gms.common.internal.zzbq;
import com.google.android.gms.dynamic.zzn;
import com.google.android.gms.dynamite.DynamiteModule;

final class zzg {
    private static zzba zzfky;
    private static final Object zzfkz = new Object();
    private static Context zzfla;

    static boolean zza(String str, zzh com_google_android_gms_common_zzh) {
        return zza(str, com_google_android_gms_common_zzh, false);
    }

    private static boolean zza(String str, zzh com_google_android_gms_common_zzh, boolean z) {
        boolean z2 = false;
        if (zzafz()) {
            zzbq.checkNotNull(zzfla);
            try {
                z2 = zzfky.zza(new zzn(str, com_google_android_gms_common_zzh, z), zzn.zzz(zzfla.getPackageManager()));
            } catch (Throwable e) {
                Log.e("GoogleCertificates", "Failed to get Google certificates from remote", e);
            }
        }
        return z2;
    }

    private static boolean zzafz() {
        boolean z = true;
        if (zzfky == null) {
            zzbq.checkNotNull(zzfla);
            synchronized (zzfkz) {
                if (zzfky == null) {
                    try {
                        zzfky = zzbb.zzan(DynamiteModule.zza(zzfla, DynamiteModule.zzgwz, "com.google.android.gms.googlecertificates").zzhb("com.google.android.gms.common.GoogleCertificatesImpl"));
                    } catch (Throwable e) {
                        Log.e("GoogleCertificates", "Failed to load com.google.android.gms.googlecertificates", e);
                        z = false;
                    }
                }
            }
        }
        return z;
    }

    static boolean zzb(String str, zzh com_google_android_gms_common_zzh) {
        return zza(str, com_google_android_gms_common_zzh, true);
    }

    static synchronized void zzcg(Context context) {
        synchronized (zzg.class) {
            if (zzfla != null) {
                Log.w("GoogleCertificates", "GoogleCertificates has been initialized already");
            } else if (context != null) {
                zzfla = context.getApplicationContext();
            }
        }
    }
}
