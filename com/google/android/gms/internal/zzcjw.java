package com.google.android.gms.internal;

import java.io.IOException;

public final class zzcjw extends ada<zzcjw> {
    private static volatile zzcjw[] zzbvz;
    public String name;
    public String zzaIF;
    private Float zzbuA;
    public Double zzbuB;
    public Long zzbvA;

    public zzcjw() {
        this.name = null;
        this.zzaIF = null;
        this.zzbvA = null;
        this.zzbuA = null;
        this.zzbuB = null;
        this.zzcrZ = null;
        this.zzcsi = -1;
    }

    public static zzcjw[] zzzC() {
        if (zzbvz == null) {
            synchronized (ade.zzcsh) {
                if (zzbvz == null) {
                    zzbvz = new zzcjw[0];
                }
            }
        }
        return zzbvz;
    }

    public final boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof zzcjw)) {
            return false;
        }
        zzcjw com_google_android_gms_internal_zzcjw = (zzcjw) obj;
        if (this.name == null) {
            if (com_google_android_gms_internal_zzcjw.name != null) {
                return false;
            }
        } else if (!this.name.equals(com_google_android_gms_internal_zzcjw.name)) {
            return false;
        }
        if (this.zzaIF == null) {
            if (com_google_android_gms_internal_zzcjw.zzaIF != null) {
                return false;
            }
        } else if (!this.zzaIF.equals(com_google_android_gms_internal_zzcjw.zzaIF)) {
            return false;
        }
        if (this.zzbvA == null) {
            if (com_google_android_gms_internal_zzcjw.zzbvA != null) {
                return false;
            }
        } else if (!this.zzbvA.equals(com_google_android_gms_internal_zzcjw.zzbvA)) {
            return false;
        }
        if (this.zzbuA == null) {
            if (com_google_android_gms_internal_zzcjw.zzbuA != null) {
                return false;
            }
        } else if (!this.zzbuA.equals(com_google_android_gms_internal_zzcjw.zzbuA)) {
            return false;
        }
        if (this.zzbuB == null) {
            if (com_google_android_gms_internal_zzcjw.zzbuB != null) {
                return false;
            }
        } else if (!this.zzbuB.equals(com_google_android_gms_internal_zzcjw.zzbuB)) {
            return false;
        }
        return (this.zzcrZ == null || this.zzcrZ.isEmpty()) ? com_google_android_gms_internal_zzcjw.zzcrZ == null || com_google_android_gms_internal_zzcjw.zzcrZ.isEmpty() : this.zzcrZ.equals(com_google_android_gms_internal_zzcjw.zzcrZ);
    }

    public final int hashCode() {
        int i = 0;
        int hashCode = ((this.zzbuB == null ? 0 : this.zzbuB.hashCode()) + (((this.zzbuA == null ? 0 : this.zzbuA.hashCode()) + (((this.zzbvA == null ? 0 : this.zzbvA.hashCode()) + (((this.zzaIF == null ? 0 : this.zzaIF.hashCode()) + (((this.name == null ? 0 : this.name.hashCode()) + ((getClass().getName().hashCode() + 527) * 31)) * 31)) * 31)) * 31)) * 31)) * 31;
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
                case 18:
                    this.zzaIF = com_google_android_gms_internal_acx.readString();
                    continue;
                case 24:
                    this.zzbvA = Long.valueOf(com_google_android_gms_internal_acx.zzLE());
                    continue;
                case 37:
                    this.zzbuA = Float.valueOf(Float.intBitsToFloat(com_google_android_gms_internal_acx.zzLF()));
                    continue;
                case 41:
                    this.zzbuB = Double.valueOf(Double.longBitsToDouble(com_google_android_gms_internal_acx.zzLG()));
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
        if (this.zzaIF != null) {
            com_google_android_gms_internal_acy.zzl(2, this.zzaIF);
        }
        if (this.zzbvA != null) {
            com_google_android_gms_internal_acy.zzb(3, this.zzbvA.longValue());
        }
        if (this.zzbuA != null) {
            com_google_android_gms_internal_acy.zzc(4, this.zzbuA.floatValue());
        }
        if (this.zzbuB != null) {
            com_google_android_gms_internal_acy.zza(5, this.zzbuB.doubleValue());
        }
        super.zza(com_google_android_gms_internal_acy);
    }

    protected final int zzn() {
        int zzn = super.zzn();
        if (this.name != null) {
            zzn += acy.zzm(1, this.name);
        }
        if (this.zzaIF != null) {
            zzn += acy.zzm(2, this.zzaIF);
        }
        if (this.zzbvA != null) {
            zzn += acy.zze(3, this.zzbvA.longValue());
        }
        if (this.zzbuA != null) {
            this.zzbuA.floatValue();
            zzn += acy.zzct(4) + 4;
        }
        if (this.zzbuB == null) {
            return zzn;
        }
        this.zzbuB.doubleValue();
        return zzn + (acy.zzct(5) + 8);
    }
}
