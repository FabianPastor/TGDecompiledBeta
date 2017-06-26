package com.google.android.gms.internal;

import android.content.SharedPreferences.Editor;
import android.support.annotation.WorkerThread;
import com.google.android.gms.common.internal.zzbo;

public final class zzcfx {
    private final String zzBP;
    private boolean zzaAI;
    private final boolean zzbrD = true;
    private boolean zzbrE;
    private /* synthetic */ zzcfv zzbrF;

    public zzcfx(zzcfv com_google_android_gms_internal_zzcfv, String str, boolean z) {
        this.zzbrF = com_google_android_gms_internal_zzcfv;
        zzbo.zzcF(str);
        this.zzBP = str;
    }

    @WorkerThread
    public final boolean get() {
        if (!this.zzbrE) {
            this.zzbrE = true;
            this.zzaAI = this.zzbrF.zzaix.getBoolean(this.zzBP, this.zzbrD);
        }
        return this.zzaAI;
    }

    @WorkerThread
    public final void set(boolean z) {
        Editor edit = this.zzbrF.zzaix.edit();
        edit.putBoolean(this.zzBP, z);
        edit.apply();
        this.zzaAI = z;
    }
}
