package com.google.android.gms.internal;

import android.content.SharedPreferences.Editor;
import com.google.android.gms.common.internal.zzbq;

public final class zzcic {
    private String mValue;
    private final String zzbhb;
    private boolean zzjdl;
    private /* synthetic */ zzchx zzjdm;
    private final String zzjdr = null;

    public zzcic(zzchx com_google_android_gms_internal_zzchx, String str, String str2) {
        this.zzjdm = com_google_android_gms_internal_zzchx;
        zzbq.zzgm(str);
        this.zzbhb = str;
    }

    public final String zzazr() {
        if (!this.zzjdl) {
            this.zzjdl = true;
            this.mValue = this.zzjdm.zzazl().getString(this.zzbhb, null);
        }
        return this.mValue;
    }

    public final void zzjq(String str) {
        if (!zzclq.zzas(str, this.mValue)) {
            Editor edit = this.zzjdm.zzazl().edit();
            edit.putString(this.zzbhb, str);
            edit.apply();
            this.mValue = str;
        }
    }
}
