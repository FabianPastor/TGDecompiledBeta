package com.google.android.gms.internal;

abstract class zzchi extends zzchh {
    private boolean zzafK;

    zzchi(zzcgk com_google_android_gms_internal_zzcgk) {
        super(com_google_android_gms_internal_zzcgk);
        this.zzboe.zzb(this);
    }

    public final void initialize() {
        if (this.zzafK) {
            throw new IllegalStateException("Can't initialize twice");
        }
        zzjD();
        this.zzboe.zzzd();
        this.zzafK = true;
    }

    final boolean isInitialized() {
        return this.zzafK;
    }

    protected abstract void zzjD();

    protected final void zzkD() {
        if (!isInitialized()) {
            throw new IllegalStateException("Not initialized");
        }
    }
}
