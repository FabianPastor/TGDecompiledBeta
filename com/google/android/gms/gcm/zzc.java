package com.google.android.gms.gcm;

import android.os.Bundle;

public class zzc {
    public static final zzc zzbgm = new zzc(0, 30, 3600);
    public static final zzc zzbgn = new zzc(1, 30, 3600);
    private final int zzbgo;
    private final int zzbgp;
    private final int zzbgq;

    private zzc(int i, int i2, int i3) {
        this.zzbgo = i;
        this.zzbgp = i2;
        this.zzbgq = i3;
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof zzc)) {
            return false;
        }
        zzc com_google_android_gms_gcm_zzc = (zzc) obj;
        return com_google_android_gms_gcm_zzc.zzbgo == this.zzbgo && com_google_android_gms_gcm_zzc.zzbgp == this.zzbgp && com_google_android_gms_gcm_zzc.zzbgq == this.zzbgq;
    }

    public int hashCode() {
        return (((((this.zzbgo + 1) ^ 1000003) * 1000003) ^ this.zzbgp) * 1000003) ^ this.zzbgq;
    }

    public String toString() {
        int i = this.zzbgo;
        int i2 = this.zzbgp;
        return "policy=" + i + " initial_backoff=" + i2 + " maximum_backoff=" + this.zzbgq;
    }

    public int zzGg() {
        return this.zzbgo;
    }

    public int zzGh() {
        return this.zzbgp;
    }

    public int zzGi() {
        return this.zzbgq;
    }

    public Bundle zzK(Bundle bundle) {
        bundle.putInt("retry_policy", this.zzbgo);
        bundle.putInt("initial_backoff_seconds", this.zzbgp);
        bundle.putInt("maximum_backoff_seconds", this.zzbgq);
        return bundle;
    }
}
