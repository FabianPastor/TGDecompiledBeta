package com.google.android.gms.internal;

import com.google.android.gms.common.internal.zzac;
import com.google.android.gms.common.util.zze;

class zzatz {
    private long zzKH;
    private final zze zzuI;

    public zzatz(zze com_google_android_gms_common_util_zze) {
        zzac.zzw(com_google_android_gms_common_util_zze);
        this.zzuI = com_google_android_gms_common_util_zze;
    }

    public void clear() {
        this.zzKH = 0;
    }

    public void start() {
        this.zzKH = this.zzuI.elapsedRealtime();
    }

    public boolean zzz(long j) {
        return this.zzKH == 0 || this.zzuI.elapsedRealtime() - this.zzKH >= j;
    }
}
