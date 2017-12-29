package com.google.android.gms.flags.impl;

import android.content.SharedPreferences;
import java.util.concurrent.Callable;

final class zze implements Callable<Integer> {
    private /* synthetic */ SharedPreferences zzhiy;
    private /* synthetic */ String zzhiz;
    private /* synthetic */ Integer zzhjb;

    zze(SharedPreferences sharedPreferences, String str, Integer num) {
        this.zzhiy = sharedPreferences;
        this.zzhiz = str;
        this.zzhjb = num;
    }

    public final /* synthetic */ Object call() throws Exception {
        return Integer.valueOf(this.zzhiy.getInt(this.zzhiz, this.zzhjb.intValue()));
    }
}
