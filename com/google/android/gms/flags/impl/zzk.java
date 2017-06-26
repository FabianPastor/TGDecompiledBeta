package com.google.android.gms.flags.impl;

import android.content.Context;
import android.content.SharedPreferences;
import java.util.concurrent.Callable;

final class zzk implements Callable<SharedPreferences> {
    private /* synthetic */ Context zztH;

    zzk(Context context) {
        this.zztH = context;
    }

    public final /* synthetic */ Object call() throws Exception {
        return this.zztH.getSharedPreferences("google_sdk_flags", 0);
    }
}
