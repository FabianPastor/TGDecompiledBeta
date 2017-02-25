package com.google.android.gms.internal;

import org.telegram.messenger.exoplayer2.DefaultLoadControl;

public class zze implements zzp {
    private int zzn;
    private int zzo;
    private final int zzp;
    private final float zzq;

    public zze() {
        this(DefaultLoadControl.DEFAULT_BUFFER_FOR_PLAYBACK_MS, 1, 1.0f);
    }

    public zze(int i, int i2, float f) {
        this.zzn = i;
        this.zzp = i2;
        this.zzq = f;
    }

    public void zza(zzs com_google_android_gms_internal_zzs) throws zzs {
        this.zzo++;
        this.zzn = (int) (((float) this.zzn) + (((float) this.zzn) * this.zzq));
        if (!zze()) {
            throw com_google_android_gms_internal_zzs;
        }
    }

    public int zzc() {
        return this.zzn;
    }

    public int zzd() {
        return this.zzo;
    }

    protected boolean zze() {
        return this.zzo <= this.zzp;
    }
}
