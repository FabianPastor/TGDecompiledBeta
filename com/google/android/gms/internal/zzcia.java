package com.google.android.gms.internal;

import android.content.SharedPreferences.Editor;
import com.google.android.gms.common.internal.zzbq;

public final class zzcia {
    private final String zzbhb;
    private long zzdrr;
    private boolean zzjdl;
    private /* synthetic */ zzchx zzjdm;
    private final long zzjdn;

    public zzcia(zzchx com_google_android_gms_internal_zzchx, String str, long j) {
        this.zzjdm = com_google_android_gms_internal_zzchx;
        zzbq.zzgm(str);
        this.zzbhb = str;
        this.zzjdn = j;
    }

    public final long get() {
        if (!this.zzjdl) {
            this.zzjdl = true;
            this.zzdrr = this.zzjdm.zzazl().getLong(this.zzbhb, this.zzjdn);
        }
        return this.zzdrr;
    }

    public final void set(long j) {
        Editor edit = this.zzjdm.zzazl().edit();
        edit.putLong(this.zzbhb, j);
        edit.apply();
        this.zzdrr = j;
    }
}
