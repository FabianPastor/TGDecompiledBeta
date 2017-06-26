package com.google.android.gms.internal;

import com.google.android.gms.common.internal.zzbo;
import com.google.android.gms.common.util.zze;

final class zzcje {
    private long mStartTime;
    private final zze zzvy;

    public zzcje(zze com_google_android_gms_common_util_zze) {
        zzbo.zzu(com_google_android_gms_common_util_zze);
        this.zzvy = com_google_android_gms_common_util_zze;
    }

    public final void clear() {
        this.mStartTime = 0;
    }

    public final void start() {
        this.mStartTime = this.zzvy.elapsedRealtime();
    }

    public final boolean zzu(long j) {
        return this.mStartTime == 0 || this.zzvy.elapsedRealtime() - this.mStartTime >= j;
    }
}
