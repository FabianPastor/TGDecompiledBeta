package com.google.android.gms.gcm;

import android.os.Bundle;

public class zzc {
    public static final zzc aff = new zzc(0, 30, 3600);
    public static final zzc afg = new zzc(1, 30, 3600);
    private final int afh;
    private final int afi;
    private final int afj;

    private zzc(int i, int i2, int i3) {
        this.afh = i;
        this.afi = i2;
        this.afj = i3;
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof zzc)) {
            return false;
        }
        zzc com_google_android_gms_gcm_zzc = (zzc) obj;
        return com_google_android_gms_gcm_zzc.afh == this.afh && com_google_android_gms_gcm_zzc.afi == this.afi && com_google_android_gms_gcm_zzc.afj == this.afj;
    }

    public int hashCode() {
        return (((((this.afh + 1) ^ 1000003) * 1000003) ^ this.afi) * 1000003) ^ this.afj;
    }

    public String toString() {
        int i = this.afh;
        int i2 = this.afi;
        return "policy=" + i + " initial_backoff=" + i2 + " maximum_backoff=" + this.afj;
    }

    public Bundle zzaj(Bundle bundle) {
        bundle.putInt("retry_policy", this.afh);
        bundle.putInt("initial_backoff_seconds", this.afi);
        bundle.putInt("maximum_backoff_seconds", this.afj);
        return bundle;
    }

    public int zzboc() {
        return this.afh;
    }

    public int zzbod() {
        return this.afi;
    }

    public int zzboe() {
        return this.afj;
    }
}
