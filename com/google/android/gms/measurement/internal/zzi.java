package com.google.android.gms.measurement.internal;

import com.google.android.gms.common.internal.zzac;

class zzi {
    final long aot;
    final long aou;
    final long aov;
    final String mName;
    final String zzcpe;

    zzi(String str, String str2, long j, long j2, long j3) {
        boolean z = true;
        zzac.zzhz(str);
        zzac.zzhz(str2);
        zzac.zzbs(j >= 0);
        if (j2 < 0) {
            z = false;
        }
        zzac.zzbs(z);
        this.zzcpe = str;
        this.mName = str2;
        this.aot = j;
        this.aou = j2;
        this.aov = j3;
    }

    zzi zzbm(long j) {
        return new zzi(this.zzcpe, this.mName, this.aot, this.aou, j);
    }

    zzi zzbvy() {
        return new zzi(this.zzcpe, this.mName, this.aot + 1, this.aou + 1, this.aov);
    }
}
