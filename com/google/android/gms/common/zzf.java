package com.google.android.gms.common;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.util.Log;
import com.google.android.gms.common.internal.zzaa;
import com.google.android.gms.internal.zzsz;

public class zzf {
    private static zzf xh;
    private final Context mContext;

    private zzf(Context context) {
        this.mContext = context.getApplicationContext();
    }

    public static zzf zzbv(Context context) {
        zzaa.zzy(context);
        synchronized (zzf.class) {
            if (xh == null) {
                zzd.zzbo(context);
                xh = new zzf(context);
            }
        }
        return xh;
    }

    zza zza(PackageInfo packageInfo, zza... com_google_android_gms_common_zzd_zzaArr) {
        int i = 0;
        if (packageInfo.signatures == null) {
            return null;
        }
        if (packageInfo.signatures.length != 1) {
            Log.w("GoogleSignatureVerifier", "Package has more than one signature.");
            return null;
        }
        zzb com_google_android_gms_common_zzd_zzb = new zzb(packageInfo.signatures[0].toByteArray());
        while (i < com_google_android_gms_common_zzd_zzaArr.length) {
            if (com_google_android_gms_common_zzd_zzaArr[i].equals(com_google_android_gms_common_zzd_zzb)) {
                return com_google_android_gms_common_zzd_zzaArr[i];
            }
            i++;
        }
        return null;
    }

    public boolean zza(PackageInfo packageInfo, boolean z) {
        if (!(packageInfo == null || packageInfo.signatures == null)) {
            zza zza;
            if (z) {
                zza = zza(packageInfo, zzd.xa);
            } else {
                zza = zza(packageInfo, zzd.xa[0]);
            }
            if (zza != null) {
                return true;
            }
        }
        return false;
    }

    public boolean zza(PackageManager packageManager, int i) {
        String[] packagesForUid = zzsz.zzco(this.mContext).getPackagesForUid(i);
        if (packagesForUid == null || packagesForUid.length == 0) {
            return false;
        }
        for (String zzb : packagesForUid) {
            if (zzb(packageManager, zzb)) {
                return true;
            }
        }
        return false;
    }

    public boolean zza(PackageManager packageManager, PackageInfo packageInfo) {
        if (packageInfo == null) {
            return false;
        }
        if (zze.zzbr(this.mContext)) {
            return zzb(packageInfo, true);
        }
        boolean zzb = zzb(packageInfo, false);
        if (zzb || !zzb(packageInfo, true)) {
            return zzb;
        }
        Log.w("GoogleSignatureVerifier", "Test-keys aren't accepted on this build.");
        return zzb;
    }

    boolean zzb(PackageInfo packageInfo, boolean z) {
        boolean z2 = false;
        if (packageInfo.signatures.length != 1) {
            Log.w("GoogleSignatureVerifier", "Package has more than one signature.");
        } else {
            zza com_google_android_gms_common_zzd_zzb = new zzb(packageInfo.signatures[0].toByteArray());
            String str = packageInfo.packageName;
            z2 = z ? zzd.zzb(str, com_google_android_gms_common_zzd_zzb) : zzd.zza(str, com_google_android_gms_common_zzd_zzb);
            if (!z2) {
            }
        }
        return z2;
    }

    public boolean zzb(PackageManager packageManager, PackageInfo packageInfo) {
        if (packageInfo == null) {
            return false;
        }
        if (zza(packageInfo, false)) {
            return true;
        }
        if (!zza(packageInfo, true)) {
            return false;
        }
        if (zze.zzbr(this.mContext)) {
            return true;
        }
        Log.w("GoogleSignatureVerifier", "Test-keys aren't accepted on this build.");
        return false;
    }

    public boolean zzb(PackageManager packageManager, String str) {
        try {
            return zza(packageManager, zzsz.zzco(this.mContext).getPackageInfo(str, 64));
        } catch (NameNotFoundException e) {
            return false;
        }
    }
}
