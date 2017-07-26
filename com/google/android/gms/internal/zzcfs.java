package com.google.android.gms.internal;

import android.support.annotation.WorkerThread;
import com.google.android.gms.common.internal.zzbo;
import java.util.List;
import java.util.Map;

@WorkerThread
final class zzcfs implements Runnable {
    private final String mPackageName;
    private final int zzLe;
    private final Throwable zzaaS;
    private final zzcfr zzbra;
    private final byte[] zzbrb;
    private final Map<String, List<String>> zzbrc;

    private zzcfs(String str, zzcfr com_google_android_gms_internal_zzcfr, int i, Throwable th, byte[] bArr, Map<String, List<String>> map) {
        zzbo.zzu(com_google_android_gms_internal_zzcfr);
        this.zzbra = com_google_android_gms_internal_zzcfr;
        this.zzLe = i;
        this.zzaaS = th;
        this.zzbrb = bArr;
        this.mPackageName = str;
        this.zzbrc = map;
    }

    public final void run() {
        this.zzbra.zza(this.mPackageName, this.zzLe, this.zzaaS, this.zzbrb, this.zzbrc);
    }
}
