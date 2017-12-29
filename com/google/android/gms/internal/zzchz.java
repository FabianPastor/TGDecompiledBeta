package com.google.android.gms.internal;

import android.content.SharedPreferences.Editor;
import com.google.android.gms.common.internal.zzbq;

public final class zzchz {
    private final String zzbhb;
    private boolean zzfmd;
    private final boolean zzjdk = true;
    private boolean zzjdl;
    private /* synthetic */ zzchx zzjdm;

    public zzchz(zzchx com_google_android_gms_internal_zzchx, String str, boolean z) {
        this.zzjdm = com_google_android_gms_internal_zzchx;
        zzbq.zzgm(str);
        this.zzbhb = str;
    }

    public final boolean get() {
        if (!this.zzjdl) {
            this.zzjdl = true;
            this.zzfmd = this.zzjdm.zzazl().getBoolean(this.zzbhb, this.zzjdk);
        }
        return this.zzfmd;
    }

    public final void set(boolean z) {
        Editor edit = this.zzjdm.zzazl().edit();
        edit.putBoolean(this.zzbhb, z);
        edit.apply();
        this.zzfmd = z;
    }
}
