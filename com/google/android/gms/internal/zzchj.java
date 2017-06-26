package com.google.android.gms.internal;

import android.content.Context;
import com.google.android.gms.common.internal.zzbo;

public final class zzchj {
    final Context mContext;

    public zzchj(Context context) {
        zzbo.zzu(context);
        Context applicationContext = context.getApplicationContext();
        zzbo.zzu(applicationContext);
        this.mContext = applicationContext;
    }
}
