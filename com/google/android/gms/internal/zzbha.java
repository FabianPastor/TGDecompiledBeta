package com.google.android.gms.internal;

import android.content.Context;

public final class zzbha {
    private static zzbha zzaKk = new zzbha();
    private zzbgz zzaKj = null;

    private final synchronized zzbgz zzaO(Context context) {
        if (this.zzaKj == null) {
            if (context.getApplicationContext() != null) {
                context = context.getApplicationContext();
            }
            this.zzaKj = new zzbgz(context);
        }
        return this.zzaKj;
    }

    public static zzbgz zzaP(Context context) {
        return zzaKk.zzaO(context);
    }
}
