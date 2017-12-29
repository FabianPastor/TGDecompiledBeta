package com.google.android.gms.internal;

abstract class zzcjl extends zzcjk {
    private boolean zzdtb;

    zzcjl(zzcim com_google_android_gms_internal_zzcim) {
        super(com_google_android_gms_internal_zzcim);
        this.zziwf.zzb(this);
    }

    public final void initialize() {
        if (this.zzdtb) {
            throw new IllegalStateException("Can't initialize twice");
        } else if (!zzaxz()) {
            this.zziwf.zzbak();
            this.zzdtb = true;
        }
    }

    final boolean isInitialized() {
        return this.zzdtb;
    }

    protected abstract boolean zzaxz();

    protected void zzayy() {
    }

    public final void zzazw() {
        if (this.zzdtb) {
            throw new IllegalStateException("Can't initialize twice");
        }
        zzayy();
        this.zziwf.zzbak();
        this.zzdtb = true;
    }

    protected final void zzxf() {
        if (!isInitialized()) {
            throw new IllegalStateException("Not initialized");
        }
    }
}
