package com.google.android.gms.internal;

import android.content.SharedPreferences.Editor;
import android.support.annotation.WorkerThread;
import com.google.android.gms.common.internal.zzbo;

public final class zzcfy {
    private final String zzBP;
    private long zzaeZ;
    private boolean zzbrE;
    private /* synthetic */ zzcfv zzbrF;
    private final long zzbrG;

    public zzcfy(zzcfv com_google_android_gms_internal_zzcfv, String str, long j) {
        this.zzbrF = com_google_android_gms_internal_zzcfv;
        zzbo.zzcF(str);
        this.zzBP = str;
        this.zzbrG = j;
    }

    @WorkerThread
    public final long get() {
        if (!this.zzbrE) {
            this.zzbrE = true;
            this.zzaeZ = this.zzbrF.zzaix.getLong(this.zzBP, this.zzbrG);
        }
        return this.zzaeZ;
    }

    @WorkerThread
    public final void set(long j) {
        Editor edit = this.zzbrF.zzaix.edit();
        edit.putLong(this.zzBP, j);
        edit.apply();
        this.zzaeZ = j;
    }
}
