package com.google.android.gms.measurement.internal;

abstract class zzaa extends zzz {
    private boolean aJ;

    zzaa(zzx com_google_android_gms_measurement_internal_zzx) {
        super(com_google_android_gms_measurement_internal_zzx);
        this.anq.zzb(this);
    }

    public final void initialize() {
        if (this.aJ) {
            throw new IllegalStateException("Can't initialize twice");
        }
        zzym();
        this.anq.zzbxo();
        this.aJ = true;
    }

    boolean isInitialized() {
        return this.aJ;
    }

    protected void zzaax() {
        if (!isInitialized()) {
            throw new IllegalStateException("Not initialized");
        }
    }

    boolean zzbxt() {
        return false;
    }

    protected abstract void zzym();
}
