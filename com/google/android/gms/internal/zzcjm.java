package com.google.android.gms.internal;

import android.content.Context;
import com.google.android.gms.common.internal.zzbq;

public final class zzcjm {
    final Context mContext;

    public zzcjm(Context context) {
        zzbq.checkNotNull(context);
        Context applicationContext = context.getApplicationContext();
        zzbq.checkNotNull(applicationContext);
        this.mContext = applicationContext;
    }
}
