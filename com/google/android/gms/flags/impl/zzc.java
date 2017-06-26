package com.google.android.gms.flags.impl;

import android.content.SharedPreferences;
import java.util.concurrent.Callable;

final class zzc implements Callable<Boolean> {
    private /* synthetic */ SharedPreferences zzaXK;
    private /* synthetic */ String zzaXL;
    private /* synthetic */ Boolean zzaXM;

    zzc(SharedPreferences sharedPreferences, String str, Boolean bool) {
        this.zzaXK = sharedPreferences;
        this.zzaXL = str;
        this.zzaXM = bool;
    }

    public final /* synthetic */ Object call() throws Exception {
        return Boolean.valueOf(this.zzaXK.getBoolean(this.zzaXL, this.zzaXM.booleanValue()));
    }
}
