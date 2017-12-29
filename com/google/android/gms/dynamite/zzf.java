package com.google.android.gms.dynamite;

import android.content.Context;
import com.google.android.gms.dynamite.DynamiteModule.zzc;
import com.google.android.gms.dynamite.DynamiteModule.zzd;

final class zzf implements zzd {
    zzf() {
    }

    public final zzj zza(Context context, String str, zzi com_google_android_gms_dynamite_zzi) throws zzc {
        zzj com_google_android_gms_dynamite_zzj = new zzj();
        com_google_android_gms_dynamite_zzj.zzgxg = com_google_android_gms_dynamite_zzi.zzab(context, str);
        com_google_android_gms_dynamite_zzj.zzgxh = com_google_android_gms_dynamite_zzi.zzc(context, str, true);
        if (com_google_android_gms_dynamite_zzj.zzgxg == 0 && com_google_android_gms_dynamite_zzj.zzgxh == 0) {
            com_google_android_gms_dynamite_zzj.zzgxi = 0;
        } else if (com_google_android_gms_dynamite_zzj.zzgxh >= com_google_android_gms_dynamite_zzj.zzgxg) {
            com_google_android_gms_dynamite_zzj.zzgxi = 1;
        } else {
            com_google_android_gms_dynamite_zzj.zzgxi = -1;
        }
        return com_google_android_gms_dynamite_zzj;
    }
}
