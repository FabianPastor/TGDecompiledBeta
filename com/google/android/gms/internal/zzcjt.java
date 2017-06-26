package com.google.android.gms.internal;

import java.io.IOException;

public final class zzcjt extends ada<zzcjt> {
    private static volatile zzcjt[] zzbvq;
    public String key;
    public String value;

    public zzcjt() {
        this.key = null;
        this.value = null;
        this.zzcrZ = null;
        this.zzcsi = -1;
    }

    public static zzcjt[] zzzz() {
        if (zzbvq == null) {
            synchronized (ade.zzcsh) {
                if (zzbvq == null) {
                    zzbvq = new zzcjt[0];
                }
            }
        }
        return zzbvq;
    }

    public final boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof zzcjt)) {
            return false;
        }
        zzcjt com_google_android_gms_internal_zzcjt = (zzcjt) obj;
        if (this.key == null) {
            if (com_google_android_gms_internal_zzcjt.key != null) {
                return false;
            }
        } else if (!this.key.equals(com_google_android_gms_internal_zzcjt.key)) {
            return false;
        }
        if (this.value == null) {
            if (com_google_android_gms_internal_zzcjt.value != null) {
                return false;
            }
        } else if (!this.value.equals(com_google_android_gms_internal_zzcjt.value)) {
            return false;
        }
        return (this.zzcrZ == null || this.zzcrZ.isEmpty()) ? com_google_android_gms_internal_zzcjt.zzcrZ == null || com_google_android_gms_internal_zzcjt.zzcrZ.isEmpty() : this.zzcrZ.equals(com_google_android_gms_internal_zzcjt.zzcrZ);
    }

    public final int hashCode() {
        int i = 0;
        int hashCode = ((this.value == null ? 0 : this.value.hashCode()) + (((this.key == null ? 0 : this.key.hashCode()) + ((getClass().getName().hashCode() + 527) * 31)) * 31)) * 31;
        if (!(this.zzcrZ == null || this.zzcrZ.isEmpty())) {
            i = this.zzcrZ.hashCode();
        }
        return hashCode + i;
    }

    public final /* synthetic */ adg zza(acx com_google_android_gms_internal_acx) throws IOException {
        while (true) {
            int zzLy = com_google_android_gms_internal_acx.zzLy();
            switch (zzLy) {
                case 0:
                    break;
                case 10:
                    this.key = com_google_android_gms_internal_acx.readString();
                    continue;
                case 18:
                    this.value = com_google_android_gms_internal_acx.readString();
                    continue;
                default:
                    if (!super.zza(com_google_android_gms_internal_acx, zzLy)) {
                        break;
                    }
                    continue;
            }
            return this;
        }
    }

    public final void zza(acy com_google_android_gms_internal_acy) throws IOException {
        if (this.key != null) {
            com_google_android_gms_internal_acy.zzl(1, this.key);
        }
        if (this.value != null) {
            com_google_android_gms_internal_acy.zzl(2, this.value);
        }
        super.zza(com_google_android_gms_internal_acy);
    }

    protected final int zzn() {
        int zzn = super.zzn();
        if (this.key != null) {
            zzn += acy.zzm(1, this.key);
        }
        return this.value != null ? zzn + acy.zzm(2, this.value) : zzn;
    }
}
