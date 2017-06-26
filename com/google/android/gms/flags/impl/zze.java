package com.google.android.gms.flags.impl;

import android.content.SharedPreferences;
import java.util.concurrent.Callable;

final class zze implements Callable<Integer> {
    private /* synthetic */ SharedPreferences zzaXK;
    private /* synthetic */ String zzaXL;
    private /* synthetic */ Integer zzaXN;

    zze(SharedPreferences sharedPreferences, String str, Integer num) {
        this.zzaXK = sharedPreferences;
        this.zzaXL = str;
        this.zzaXN = num;
    }

    public final /* synthetic */ Object call() throws Exception {
        return Integer.valueOf(this.zzaXK.getInt(this.zzaXL, this.zzaXN.intValue()));
    }
}
