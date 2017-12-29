package com.google.android.gms.internal;

import android.content.Context;

public final class zzbhf {
    private static zzbhf zzgfh = new zzbhf();
    private zzbhe zzgfg = null;

    private final synchronized zzbhe zzda(Context context) {
        if (this.zzgfg == null) {
            if (context.getApplicationContext() != null) {
                context = context.getApplicationContext();
            }
            this.zzgfg = new zzbhe(context);
        }
        return this.zzgfg;
    }

    public static zzbhe zzdb(Context context) {
        return zzgfh.zzda(context);
    }
}
