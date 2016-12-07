package com.google.android.gms.gcm;

import android.os.Bundle;

public class zzc {
    public static final zzc aho = new zzc(0, 30, 3600);
    public static final zzc ahp = new zzc(1, 30, 3600);
    private final int ahq;
    private final int ahr;
    private final int ahs;

    private zzc(int i, int i2, int i3) {
        this.ahq = i;
        this.ahr = i2;
        this.ahs = i3;
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof zzc)) {
            return false;
        }
        zzc com_google_android_gms_gcm_zzc = (zzc) obj;
        return com_google_android_gms_gcm_zzc.ahq == this.ahq && com_google_android_gms_gcm_zzc.ahr == this.ahr && com_google_android_gms_gcm_zzc.ahs == this.ahs;
    }

    public int hashCode() {
        return (((((this.ahq + 1) ^ 1000003) * 1000003) ^ this.ahr) * 1000003) ^ this.ahs;
    }

    public String toString() {
        int i = this.ahq;
        int i2 = this.ahr;
        return "policy=" + i + " initial_backoff=" + i2 + " maximum_backoff=" + this.ahs;
    }

    public Bundle zzaj(Bundle bundle) {
        bundle.putInt("retry_policy", this.ahq);
        bundle.putInt("initial_backoff_seconds", this.ahr);
        bundle.putInt("maximum_backoff_seconds", this.ahs);
        return bundle;
    }

    public int zzbnv() {
        return this.ahq;
    }

    public int zzbnw() {
        return this.ahr;
    }

    public int zzbnx() {
        return this.ahs;
    }
}
