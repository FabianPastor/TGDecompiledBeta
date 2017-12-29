package com.google.android.gms.common.internal;

import android.content.Context;
import android.content.ServiceConnection;

public abstract class zzag {
    private static final Object zzgai = new Object();
    private static zzag zzgaj;

    public static zzag zzco(Context context) {
        synchronized (zzgai) {
            if (zzgaj == null) {
                zzgaj = new zzai(context.getApplicationContext());
            }
        }
        return zzgaj;
    }

    public final void zza(String str, String str2, int i, ServiceConnection serviceConnection, String str3) {
        zzb(new zzah(str, str2, i), serviceConnection, str3);
    }

    protected abstract boolean zza(zzah com_google_android_gms_common_internal_zzah, ServiceConnection serviceConnection, String str);

    protected abstract void zzb(zzah com_google_android_gms_common_internal_zzah, ServiceConnection serviceConnection, String str);
}
