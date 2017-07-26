package com.google.android.gms.internal;

abstract class zzchj extends zzchi {
    private boolean zzafK;

    zzchj(zzcgl com_google_android_gms_internal_zzcgl) {
        super(com_google_android_gms_internal_zzcgl);
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
