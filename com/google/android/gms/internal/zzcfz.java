package com.google.android.gms.internal;

import android.content.SharedPreferences.Editor;
import android.support.annotation.WorkerThread;
import com.google.android.gms.common.internal.zzbo;

public final class zzcfz {
    private final String zzBN;
    private long zzaeZ;
    private boolean zzbrE;
    private /* synthetic */ zzcfw zzbrF;
    private final long zzbrG;

    public zzcfz(zzcfw com_google_android_gms_internal_zzcfw, String str, long j) {
        this.zzbrF = com_google_android_gms_internal_zzcfw;
        zzbo.zzcF(str);
        this.zzBN = str;
        this.zzbrG = j;
    }

    @WorkerThread
    public final long get() {
        if (!this.zzbrE) {
            this.zzbrE = true;
            this.zzaeZ = this.zzbrF.zzaix.getLong(this.zzBN, this.zzbrG);
        }
        return this.zzaeZ;
    }

    @WorkerThread
    public final void set(long j) {
        Editor edit = this.zzbrF.zzaix.edit();
        edit.putLong(this.zzBN, j);
        edit.apply();
        this.zzaeZ = j;
    }
}
