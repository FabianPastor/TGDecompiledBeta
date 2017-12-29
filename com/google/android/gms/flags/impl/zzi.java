package com.google.android.gms.flags.impl;

import android.content.SharedPreferences;
import java.util.concurrent.Callable;

final class zzi implements Callable<String> {
    private /* synthetic */ SharedPreferences zzhiy;
    private /* synthetic */ String zzhiz;
    private /* synthetic */ String zzhjd;

    zzi(SharedPreferences sharedPreferences, String str, String str2) {
        this.zzhiy = sharedPreferences;
        this.zzhiz = str;
        this.zzhjd = str2;
    }

    public final /* synthetic */ Object call() throws Exception {
        return this.zzhiy.getString(this.zzhiz, this.zzhjd);
    }
}
