package com.google.android.gms.internal;

import android.support.annotation.WorkerThread;
import com.google.android.gms.common.internal.zzbo;
import java.util.List;
import java.util.Map;

@WorkerThread
final class zzcfr implements Runnable {
    private final String mPackageName;
    private final int zzLg;
    private final Throwable zzaaS;
    private final zzcfq zzbra;
    private final byte[] zzbrb;
    private final Map<String, List<String>> zzbrc;

    private zzcfr(String str, zzcfq com_google_android_gms_internal_zzcfq, int i, Throwable th, byte[] bArr, Map<String, List<String>> map) {
        zzbo.zzu(com_google_android_gms_internal_zzcfq);
        this.zzbra = com_google_android_gms_internal_zzcfq;
        this.zzLg = i;
        this.zzaaS = th;
        this.zzbrb = bArr;
        this.mPackageName = str;
        this.zzbrc = map;
    }

    public final void run() {
        this.zzbra.zza(this.mPackageName, this.zzLg, this.zzaaS, this.zzbrb, this.zzbrc);
    }
}
