package com.google.android.gms.common;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.util.Log;
import com.google.android.gms.common.internal.zzbq;
import com.google.android.gms.internal.zzbhf;

public class zzq {
    private static zzq zzflp;
    private final Context mContext;

    private zzq(Context context) {
        this.mContext = context.getApplicationContext();
    }

    static zzh zza(PackageInfo packageInfo, zzh... com_google_android_gms_common_zzhArr) {
        int i = 0;
        if (packageInfo.signatures == null) {
            return null;
        }
        if (packageInfo.signatures.length != 1) {
            Log.w("GoogleSignatureVerifier", "Package has more than one signature.");
            return null;
        }
        zzi com_google_android_gms_common_zzi = new zzi(packageInfo.signatures[0].toByteArray());
        while (i < com_google_android_gms_common_zzhArr.length) {
            if (com_google_android_gms_common_zzhArr[i].equals(com_google_android_gms_common_zzi)) {
                return com_google_android_gms_common_zzhArr[i];
            }
            i++;
        }
        return null;
    }

    private static boolean zza(PackageInfo packageInfo, boolean z) {
        if (!(packageInfo == null || packageInfo.signatures == null)) {
            zzh zza;
            if (z) {
                zza = zza(packageInfo, zzk.zzflf);
            } else {
                zza = zza(packageInfo, zzk.zzflf[0]);
            }
            if (zza != null) {
                return true;
            }
        }
        return false;
    }

    private static boolean zzb(PackageInfo packageInfo, boolean z) {
        boolean z2 = false;
        if (packageInfo.signatures.length != 1) {
            Log.w("GoogleSignatureVerifier", "Package has more than one signature.");
        } else {
            zzh com_google_android_gms_common_zzi = new zzi(packageInfo.signatures[0].toByteArray());
            String str = packageInfo.packageName;
            z2 = z ? zzg.zzb(str, com_google_android_gms_common_zzi) : zzg.zza(str, com_google_android_gms_common_zzi);
            if (!z2) {
                Log.d("GoogleSignatureVerifier", "Cert not in list. atk=" + z);
            }
        }
        return z2;
    }

    public static zzq zzci(Context context) {
        zzbq.checkNotNull(context);
        synchronized (zzq.class) {
            if (zzflp == null) {
                zzg.zzcg(context);
                zzflp = new zzq(context);
            }
        }
        return zzflp;
    }

    private final boolean zzfy(String str) {
        try {
            PackageInfo packageInfo = zzbhf.zzdb(this.mContext).getPackageInfo(str, 64);
            if (packageInfo == null) {
                return false;
            }
            if (zzp.zzch(this.mContext)) {
                return zzb(packageInfo, true);
            }
            boolean zzb = zzb(packageInfo, false);
            if (!zzb && zzb(packageInfo, true)) {
                Log.w("GoogleSignatureVerifier", "Test-keys aren't accepted on this build.");
            }
            return zzb;
        } catch (NameNotFoundException e) {
            return false;
        }
    }

    public final boolean zza(PackageInfo packageInfo) {
        if (packageInfo == null) {
            return false;
        }
        if (zza(packageInfo, false)) {
            return true;
        }
        if (!zza(packageInfo, true)) {
            return false;
        }
        if (zzp.zzch(this.mContext)) {
            return true;
        }
        Log.w("GoogleSignatureVerifier", "Test-keys aren't accepted on this build.");
        return false;
    }

    public final boolean zzbq(int i) {
        String[] packagesForUid = zzbhf.zzdb(this.mContext).getPackagesForUid(i);
        if (packagesForUid == null || packagesForUid.length == 0) {
            return false;
        }
        for (String zzfy : packagesForUid) {
            if (zzfy(zzfy)) {
                return true;
            }
        }
        return false;
    }
}
