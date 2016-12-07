package com.google.android.gms.internal;

import android.content.Context;

public class zzsz {
    private static zzsz GQ = new zzsz();
    private zzsy GP = null;

    public static zzsy zzco(Context context) {
        return GQ.zzcn(context);
    }

    public synchronized zzsy zzcn(Context context) {
        if (this.GP == null) {
            if (context.getApplicationContext() != null) {
                context = context.getApplicationContext();
            }
            this.GP = new zzsy(context);
        }
        return this.GP;
    }
}
