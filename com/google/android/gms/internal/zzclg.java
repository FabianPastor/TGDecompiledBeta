package com.google.android.gms.internal;

import android.os.Bundle;

final class zzclg extends zzcgs {
    private /* synthetic */ zzclf zzjjf;

    zzclg(zzclf com_google_android_gms_internal_zzclf, zzcim com_google_android_gms_internal_zzcim) {
        this.zzjjf = com_google_android_gms_internal_zzclf;
        super(com_google_android_gms_internal_zzcim);
    }

    public final void run() {
        zzcjk com_google_android_gms_internal_zzcjk = this.zzjjf;
        com_google_android_gms_internal_zzcjk.zzve();
        com_google_android_gms_internal_zzcjk.zzawy().zzazj().zzj("Session started, time", Long.valueOf(com_google_android_gms_internal_zzcjk.zzws().elapsedRealtime()));
        com_google_android_gms_internal_zzcjk.zzawz().zzjdg.set(false);
        com_google_android_gms_internal_zzcjk.zzawm().zzc("auto", "_s", new Bundle());
        com_google_android_gms_internal_zzcjk.zzawz().zzjdh.set(com_google_android_gms_internal_zzcjk.zzws().currentTimeMillis());
    }
}
