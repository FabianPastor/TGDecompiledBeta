package com.google.android.gms.flags.impl;

import android.content.SharedPreferences;
import java.util.concurrent.Callable;

final class zzi implements Callable<String> {
    private /* synthetic */ SharedPreferences zzaXK;
    private /* synthetic */ String zzaXL;
    private /* synthetic */ String zzaXP;

    zzi(SharedPreferences sharedPreferences, String str, String str2) {
        this.zzaXK = sharedPreferences;
        this.zzaXL = str;
        this.zzaXP = str2;
    }

    public final /* synthetic */ Object call() throws Exception {
        return this.zzaXK.getString(this.zzaXL, this.zzaXP);
    }
}
