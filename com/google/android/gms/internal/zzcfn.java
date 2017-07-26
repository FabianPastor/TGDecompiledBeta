package com.google.android.gms.internal;

public final class zzcfn {
    private final int mPriority;
    private /* synthetic */ zzcfl zzbqW;
    private final boolean zzbqX;
    private final boolean zzbqY;

    zzcfn(zzcfl com_google_android_gms_internal_zzcfl, int i, boolean z, boolean z2) {
        this.zzbqW = com_google_android_gms_internal_zzcfl;
        this.mPriority = i;
        this.zzbqX = z;
        this.zzbqY = z2;
    }

    public final void log(String str) {
        this.zzbqW.zza(this.mPriority, this.zzbqX, this.zzbqY, str, null, null, null);
    }

    public final void zzd(String str, Object obj, Object obj2, Object obj3) {
        this.zzbqW.zza(this.mPriority, this.zzbqX, this.zzbqY, str, obj, obj2, obj3);
    }

    public final void zze(String str, Object obj, Object obj2) {
        this.zzbqW.zza(this.mPriority, this.zzbqX, this.zzbqY, str, obj, obj2, null);
    }

    public final void zzj(String str, Object obj) {
        this.zzbqW.zza(this.mPriority, this.zzbqX, this.zzbqY, str, obj, null, null);
    }
}
