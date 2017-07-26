package com.google.android.gms.internal;

import com.google.android.gms.common.internal.zzbo;

final class zzcev {
    final String mAppId;
    final String mName;
    final long zzbpG;
    final long zzbpH;
    final long zzbpI;

    zzcev(String str, String str2, long j, long j2, long j3) {
        boolean z = true;
        zzbo.zzcF(str);
        zzbo.zzcF(str2);
        zzbo.zzaf(j >= 0);
        if (j2 < 0) {
            z = false;
        }
        zzbo.zzaf(z);
        this.mAppId = str;
        this.mName = str2;
        this.zzbpG = j;
        this.zzbpH = j2;
        this.zzbpI = j3;
    }

    final zzcev zzab(long j) {
        return new zzcev(this.mAppId, this.mName, this.zzbpG, this.zzbpH, j);
    }

    final zzcev zzys() {
        return new zzcev(this.mAppId, this.mName, this.zzbpG + 1, this.zzbpH + 1, this.zzbpI);
    }
}
