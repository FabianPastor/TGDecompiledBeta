package com.google.android.gms.internal;

import com.google.android.gms.common.internal.zzbq;
import java.util.List;
import java.util.Map;

final class zzcht implements Runnable {
    private final String mPackageName;
    private final int zzcbc;
    private final Throwable zzdfl;
    private final zzchs zzjch;
    private final byte[] zzjci;
    private final Map<String, List<String>> zzjcj;

    private zzcht(String str, zzchs com_google_android_gms_internal_zzchs, int i, Throwable th, byte[] bArr, Map<String, List<String>> map) {
        zzbq.checkNotNull(com_google_android_gms_internal_zzchs);
        this.zzjch = com_google_android_gms_internal_zzchs;
        this.zzcbc = i;
        this.zzdfl = th;
        this.zzjci = bArr;
        this.mPackageName = str;
        this.zzjcj = map;
    }

    public final void run() {
        this.zzjch.zza(this.mPackageName, this.zzcbc, this.zzdfl, this.zzjci, this.zzjcj);
    }
}
