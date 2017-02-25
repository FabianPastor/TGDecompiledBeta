package com.google.android.gms.internal;

public class zzs extends Exception {
    private long zzB;
    public final zzj zzai;

    public zzs() {
        this.zzai = null;
    }

    public zzs(zzj com_google_android_gms_internal_zzj) {
        this.zzai = com_google_android_gms_internal_zzj;
    }

    public zzs(Throwable th) {
        super(th);
        this.zzai = null;
    }

    void zza(long j) {
        this.zzB = j;
    }
}
