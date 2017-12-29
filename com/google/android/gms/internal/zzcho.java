package com.google.android.gms.internal;

public final class zzcho {
    private final int mPriority;
    private /* synthetic */ zzchm zzjce;
    private final boolean zzjcf;
    private final boolean zzjcg;

    zzcho(zzchm com_google_android_gms_internal_zzchm, int i, boolean z, boolean z2) {
        this.zzjce = com_google_android_gms_internal_zzchm;
        this.mPriority = i;
        this.zzjcf = z;
        this.zzjcg = z2;
    }

    public final void log(String str) {
        this.zzjce.zza(this.mPriority, this.zzjcf, this.zzjcg, str, null, null, null);
    }

    public final void zzd(String str, Object obj, Object obj2, Object obj3) {
        this.zzjce.zza(this.mPriority, this.zzjcf, this.zzjcg, str, obj, obj2, obj3);
    }

    public final void zze(String str, Object obj, Object obj2) {
        this.zzjce.zza(this.mPriority, this.zzjcf, this.zzjcg, str, obj, obj2, null);
    }

    public final void zzj(String str, Object obj) {
        this.zzjce.zza(this.mPriority, this.zzjcf, this.zzjcg, str, obj, null, null);
    }
}
