package com.google.android.gms.internal;

import android.content.SharedPreferences.Editor;
import android.support.annotation.WorkerThread;
import com.google.android.gms.common.internal.zzbo;

public final class zzcga {
    private String mValue;
    private final String zzBP;
    private boolean zzbrE;
    private /* synthetic */ zzcfv zzbrF;
    private final String zzbrK = null;

    public zzcga(zzcfv com_google_android_gms_internal_zzcfv, String str, String str2) {
        this.zzbrF = com_google_android_gms_internal_zzcfv;
        zzbo.zzcF(str);
        this.zzBP = str;
    }

    @WorkerThread
    public final void zzef(String str) {
        if (!zzcjk.zzR(str, this.mValue)) {
            Editor edit = this.zzbrF.zzaix.edit();
            edit.putString(this.zzBP, str);
            edit.apply();
            this.mValue = str;
        }
    }

    @WorkerThread
    public final String zzyL() {
        if (!this.zzbrE) {
            this.zzbrE = true;
            this.mValue = this.zzbrF.zzaix.getString(this.zzBP, null);
        }
        return this.mValue;
    }
}
