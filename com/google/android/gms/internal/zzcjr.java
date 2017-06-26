package com.google.android.gms.internal;

import java.io.IOException;

public final class zzcjr extends ada<zzcjr> {
    private static volatile zzcjr[] zzbvi;
    public String name;
    public Boolean zzbvj;
    public Boolean zzbvk;

    public zzcjr() {
        this.name = null;
        this.zzbvj = null;
        this.zzbvk = null;
        this.zzcrZ = null;
        this.zzcsi = -1;
    }

    public static zzcjr[] zzzy() {
        if (zzbvi == null) {
            synchronized (ade.zzcsh) {
                if (zzbvi == null) {
                    zzbvi = new zzcjr[0];
                }
            }
        }
        return zzbvi;
    }

    public final boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof zzcjr)) {
            return false;
        }
        zzcjr com_google_android_gms_internal_zzcjr = (zzcjr) obj;
        if (this.name == null) {
            if (com_google_android_gms_internal_zzcjr.name != null) {
                return false;
            }
        } else if (!this.name.equals(com_google_android_gms_internal_zzcjr.name)) {
            return false;
        }
        if (this.zzbvj == null) {
            if (com_google_android_gms_internal_zzcjr.zzbvj != null) {
                return false;
            }
        } else if (!this.zzbvj.equals(com_google_android_gms_internal_zzcjr.zzbvj)) {
            return false;
        }
        if (this.zzbvk == null) {
            if (com_google_android_gms_internal_zzcjr.zzbvk != null) {
                return false;
            }
        } else if (!this.zzbvk.equals(com_google_android_gms_internal_zzcjr.zzbvk)) {
            return false;
        }
        return (this.zzcrZ == null || this.zzcrZ.isEmpty()) ? com_google_android_gms_internal_zzcjr.zzcrZ == null || com_google_android_gms_internal_zzcjr.zzcrZ.isEmpty() : this.zzcrZ.equals(com_google_android_gms_internal_zzcjr.zzcrZ);
    }

    public final int hashCode() {
        int i = 0;
        int hashCode = ((this.zzbvk == null ? 0 : this.zzbvk.hashCode()) + (((this.zzbvj == null ? 0 : this.zzbvj.hashCode()) + (((this.name == null ? 0 : this.name.hashCode()) + ((getClass().getName().hashCode() + 527) * 31)) * 31)) * 31)) * 31;
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
                    this.name = com_google_android_gms_internal_acx.readString();
                    continue;
                case 16:
                    this.zzbvj = Boolean.valueOf(com_google_android_gms_internal_acx.zzLB());
                    continue;
                case 24:
                    this.zzbvk = Boolean.valueOf(com_google_android_gms_internal_acx.zzLB());
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
        if (this.name != null) {
            com_google_android_gms_internal_acy.zzl(1, this.name);
        }
        if (this.zzbvj != null) {
            com_google_android_gms_internal_acy.zzk(2, this.zzbvj.booleanValue());
        }
        if (this.zzbvk != null) {
            com_google_android_gms_internal_acy.zzk(3, this.zzbvk.booleanValue());
        }
        super.zza(com_google_android_gms_internal_acy);
    }

    protected final int zzn() {
        int zzn = super.zzn();
        if (this.name != null) {
            zzn += acy.zzm(1, this.name);
        }
        if (this.zzbvj != null) {
            this.zzbvj.booleanValue();
            zzn += acy.zzct(2) + 1;
        }
        if (this.zzbvk == null) {
            return zzn;
        }
        this.zzbvk.booleanValue();
        return zzn + (acy.zzct(3) + 1);
    }
}
