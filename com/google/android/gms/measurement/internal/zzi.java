package com.google.android.gms.measurement.internal;

import com.google.android.gms.common.internal.zzaa;

class zzi {
    final long arD;
    final long arE;
    final long arF;
    final String mName;
    final String zzctj;

    zzi(String str, String str2, long j, long j2, long j3) {
        boolean z = true;
        zzaa.zzib(str);
        zzaa.zzib(str2);
        zzaa.zzbt(j >= 0);
        if (j2 < 0) {
            z = false;
        }
        zzaa.zzbt(z);
        this.zzctj = str;
        this.mName = str2;
        this.arD = j;
        this.arE = j2;
        this.arF = j3;
    }

    zzi zzbl(long j) {
        return new zzi(this.zzctj, this.mName, this.arD, this.arE, j);
    }

    zzi zzbwv() {
        return new zzi(this.zzctj, this.mName, this.arD + 1, this.arE + 1, this.arF);
    }
}
