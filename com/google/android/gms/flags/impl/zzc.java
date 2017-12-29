package com.google.android.gms.flags.impl;

import android.content.SharedPreferences;
import java.util.concurrent.Callable;

final class zzc implements Callable<Boolean> {
    private /* synthetic */ SharedPreferences zzhiy;
    private /* synthetic */ String zzhiz;
    private /* synthetic */ Boolean zzhja;

    zzc(SharedPreferences sharedPreferences, String str, Boolean bool) {
        this.zzhiy = sharedPreferences;
        this.zzhiz = str;
        this.zzhja = bool;
    }

    public final /* synthetic */ Object call() throws Exception {
        return Boolean.valueOf(this.zzhiy.getBoolean(this.zzhiz, this.zzhja.booleanValue()));
    }
}
