package com.google.android.gms.internal;

abstract class zzauh extends zzaug {
    private boolean zzadP;

    zzauh(zzaue com_google_android_gms_internal_zzaue) {
        super(com_google_android_gms_internal_zzaue);
        this.zzbqc.zzb(this);
    }

    public final void initialize() {
        if (this.zzadP) {
            throw new IllegalStateException("Can't initialize twice");
        }
        zzmS();
        this.zzbqc.zzMK();
        this.zzadP = true;
    }

    boolean isInitialized() {
        return this.zzadP;
    }

    protected abstract void zzmS();

    protected void zzob() {
        if (!isInitialized()) {
            throw new IllegalStateException("Not initialized");
        }
    }
}
