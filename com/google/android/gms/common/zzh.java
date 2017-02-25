package com.google.android.gms.common;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.util.Log;
import com.google.android.gms.common.internal.zzac;
import com.google.android.gms.internal.zzadf;
import com.google.android.gms.internal.zzadg;

public class zzh {
    private static zzh zzayD;
    private final Context mContext;
    private final zzadf zzayE = zzadg.zzbi(this.mContext);

    private zzh(Context context) {
        this.mContext = context.getApplicationContext();
    }

    public static zzh zzaN(Context context) {
        zzac.zzw(context);
        synchronized (zzh.class) {
            if (zzayD == null) {
                zzf.zzaG(context);
                zzayD = new zzh(context);
            }
        }
        return zzayD;
    }

    zza zza(PackageInfo packageInfo, zza... com_google_android_gms_common_zzf_zzaArr) {
        int i = 0;
        if (packageInfo.signatures == null) {
            return null;
        }
        if (packageInfo.signatures.length != 1) {
            Log.w("GoogleSignatureVerifier", "Package has more than one signature.");
            return null;
        }
        zzb com_google_android_gms_common_zzf_zzb = new zzb(packageInfo.signatures[0].toByteArray());
        while (i < com_google_android_gms_common_zzf_zzaArr.length) {
            if (com_google_android_gms_common_zzf_zzaArr[i].equals(com_google_android_gms_common_zzf_zzb)) {
                return com_google_android_gms_common_zzf_zzaArr[i];
            }
            i++;
        }
        return null;
    }

    public boolean zza(PackageInfo packageInfo) {
        if (packageInfo == null) {
            return false;
        }
        if (zzg.zzaJ(this.mContext)) {
            return zzb(packageInfo, true);
        }
        boolean zzb = zzb(packageInfo, false);
        if (zzb || !zzb(packageInfo, true)) {
            return zzb;
        }
        Log.w("GoogleSignatureVerifier", "Test-keys aren't accepted on this build.");
        return zzb;
    }

    public boolean zza(PackageInfo packageInfo, boolean z) {
        if (!(packageInfo == null || packageInfo.signatures == null)) {
            zza zza;
            if (z) {
                zza = zza(packageInfo, zzd.zzayw);
            } else {
                zza = zza(packageInfo, zzd.zzayw[0]);
            }
            if (zza != null) {
                return true;
            }
        }
        return false;
    }

    @Deprecated
    public boolean zza(PackageManager packageManager, int i) {
        return zzcx(i);
    }

    @Deprecated
    public boolean zza(PackageManager packageManager, PackageInfo packageInfo) {
        return zzb(packageInfo);
    }

    public boolean zzb(PackageInfo packageInfo) {
        if (packageInfo == null) {
            return false;
        }
        if (zza(packageInfo, false)) {
            return true;
        }
        if (!zza(packageInfo, true)) {
            return false;
        }
        if (zzg.zzaJ(this.mContext)) {
            return true;
        }
        Log.w("GoogleSignatureVerifier", "Test-keys aren't accepted on this build.");
        return false;
    }

    boolean zzb(PackageInfo packageInfo, boolean z) {
        boolean z2 = false;
        if (packageInfo.signatures.length != 1) {
            Log.w("GoogleSignatureVerifier", "Package has more than one signature.");
        } else {
            zza com_google_android_gms_common_zzf_zzb = new zzb(packageInfo.signatures[0].toByteArray());
            String str = packageInfo.packageName;
            z2 = z ? zzf.zzb(str, com_google_android_gms_common_zzf_zzb) : zzf.zza(str, com_google_android_gms_common_zzf_zzb);
            if (!z2) {
                Log.d("GoogleSignatureVerifier", "Cert not in list. atk=" + z);
            }
        }
        return z2;
    }

    public boolean zzcx(int i) {
        String[] packagesForUid = this.zzayE.getPackagesForUid(i);
        if (packagesForUid == null || packagesForUid.length == 0) {
            return false;
        }
        for (String zzdd : packagesForUid) {
            if (zzdd(zzdd)) {
                return true;
            }
        }
        return false;
    }

    public boolean zzdd(String str) {
        try {
            return zza(this.zzayE.getPackageInfo(str, 64));
        } catch (NameNotFoundException e) {
            return false;
        }
    }
}
