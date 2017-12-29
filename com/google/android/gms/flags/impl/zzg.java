package com.google.android.gms.flags.impl;

import android.content.SharedPreferences;
import java.util.concurrent.Callable;

final class zzg implements Callable<Long> {
    private /* synthetic */ SharedPreferences zzhiy;
    private /* synthetic */ String zzhiz;
    private /* synthetic */ Long zzhjc;

    zzg(SharedPreferences sharedPreferences, String str, Long l) {
        this.zzhiy = sharedPreferences;
        this.zzhiz = str;
        this.zzhjc = l;
    }

    public final /* synthetic */ Object call() throws Exception {
        return Long.valueOf(this.zzhiy.getLong(this.zzhiz, this.zzhjc.longValue()));
    }
}
