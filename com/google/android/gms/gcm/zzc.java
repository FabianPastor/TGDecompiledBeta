package com.google.android.gms.gcm;

import android.os.Bundle;

public class zzc {
    public static final zzc zzbgR = new zzc(0, 30, 3600);
    public static final zzc zzbgS = new zzc(1, 30, 3600);
    private final int zzbgT;
    private final int zzbgU;
    private final int zzbgV;

    private zzc(int i, int i2, int i3) {
        this.zzbgT = i;
        this.zzbgU = i2;
        this.zzbgV = i3;
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof zzc)) {
            return false;
        }
        zzc com_google_android_gms_gcm_zzc = (zzc) obj;
        return com_google_android_gms_gcm_zzc.zzbgT == this.zzbgT && com_google_android_gms_gcm_zzc.zzbgU == this.zzbgU && com_google_android_gms_gcm_zzc.zzbgV == this.zzbgV;
    }

    public int hashCode() {
        return (((((this.zzbgT + 1) ^ 1000003) * 1000003) ^ this.zzbgU) * 1000003) ^ this.zzbgV;
    }

    public String toString() {
        int i = this.zzbgT;
        int i2 = this.zzbgU;
        return "policy=" + i + " initial_backoff=" + i2 + " maximum_backoff=" + this.zzbgV;
    }

    public int zzGT() {
        return this.zzbgT;
    }

    public int zzGU() {
        return this.zzbgU;
    }

    public int zzGV() {
        return this.zzbgV;
    }

    public Bundle zzK(Bundle bundle) {
        bundle.putInt("retry_policy", this.zzbgT);
        bundle.putInt("initial_backoff_seconds", this.zzbgU);
        bundle.putInt("maximum_backoff_seconds", this.zzbgV);
        return bundle;
    }
}
