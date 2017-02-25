package com.google.android.gms.internal;

import com.google.android.gms.common.internal.zzac;

class zzatn {
    final String mAppId;
    final String mName;
    final long zzbrE;
    final long zzbrF;
    final long zzbrG;

    zzatn(String str, String str2, long j, long j2, long j3) {
        boolean z = true;
        zzac.zzdr(str);
        zzac.zzdr(str2);
        zzac.zzax(j >= 0);
        if (j2 < 0) {
            z = false;
        }
        zzac.zzax(z);
        this.mAppId = str;
        this.mName = str2;
        this.zzbrE = j;
        this.zzbrF = j2;
        this.zzbrG = j3;
    }

    zzatn zzLU() {
        return new zzatn(this.mAppId, this.mName, this.zzbrE + 1, this.zzbrF + 1, this.zzbrG);
    }

    zzatn zzap(long j) {
        return new zzatn(this.mAppId, this.mName, this.zzbrE, this.zzbrF, j);
    }
}
