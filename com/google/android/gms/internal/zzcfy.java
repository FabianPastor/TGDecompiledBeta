package com.google.android.gms.internal;

import android.content.SharedPreferences.Editor;
import android.support.annotation.WorkerThread;
import com.google.android.gms.common.internal.zzbo;

public final class zzcfy {
    private final String zzBN;
    private boolean zzaAI;
    private final boolean zzbrD = true;
    private boolean zzbrE;
    private /* synthetic */ zzcfw zzbrF;

    public zzcfy(zzcfw com_google_android_gms_internal_zzcfw, String str, boolean z) {
        this.zzbrF = com_google_android_gms_internal_zzcfw;
        zzbo.zzcF(str);
        this.zzBN = str;
    }

    @WorkerThread
    public final boolean get() {
        if (!this.zzbrE) {
            this.zzbrE = true;
            this.zzaAI = this.zzbrF.zzaix.getBoolean(this.zzBN, this.zzbrD);
        }
        return this.zzaAI;
    }

    @WorkerThread
    public final void set(boolean z) {
        Editor edit = this.zzbrF.zzaix.edit();
        edit.putBoolean(this.zzBN, z);
        edit.apply();
        this.zzaAI = z;
    }
}
