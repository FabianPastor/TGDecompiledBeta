package com.google.android.gms.internal;

import android.support.v4.view.MotionEventCompat;
import com.googlecode.mp4parser.authoring.tracks.h265.NalUnitTypes;
import java.io.IOException;

public final class zzcka extends ada<zzcka> {
    private static volatile zzcka[] zzbwg;
    public String name;
    public String zzaIF;
    private Float zzbuA;
    public Double zzbuB;
    public Long zzbvA;
    public Long zzbwh;

    public zzcka() {
        this.zzbwh = null;
        this.name = null;
        this.zzaIF = null;
        this.zzbvA = null;
        this.zzbuA = null;
        this.zzbuB = null;
        this.zzcrZ = null;
        this.zzcsi = -1;
    }

    public static zzcka[] zzzE() {
        if (zzbwg == null) {
            synchronized (ade.zzcsh) {
                if (zzbwg == null) {
                    zzbwg = new zzcka[0];
                }
            }
        }
        return zzbwg;
    }

    public final boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof zzcka)) {
            return false;
        }
        zzcka com_google_android_gms_internal_zzcka = (zzcka) obj;
        if (this.zzbwh == null) {
            if (com_google_android_gms_internal_zzcka.zzbwh != null) {
                return false;
            }
        } else if (!this.zzbwh.equals(com_google_android_gms_internal_zzcka.zzbwh)) {
            return false;
        }
        if (this.name == null) {
            if (com_google_android_gms_internal_zzcka.name != null) {
                return false;
            }
        } else if (!this.name.equals(com_google_android_gms_internal_zzcka.name)) {
            return false;
        }
        if (this.zzaIF == null) {
            if (com_google_android_gms_internal_zzcka.zzaIF != null) {
                return false;
            }
        } else if (!this.zzaIF.equals(com_google_android_gms_internal_zzcka.zzaIF)) {
            return false;
        }
        if (this.zzbvA == null) {
            if (com_google_android_gms_internal_zzcka.zzbvA != null) {
                return false;
            }
        } else if (!this.zzbvA.equals(com_google_android_gms_internal_zzcka.zzbvA)) {
            return false;
        }
        if (this.zzbuA == null) {
            if (com_google_android_gms_internal_zzcka.zzbuA != null) {
                return false;
            }
        } else if (!this.zzbuA.equals(com_google_android_gms_internal_zzcka.zzbuA)) {
            return false;
        }
        if (this.zzbuB == null) {
            if (com_google_android_gms_internal_zzcka.zzbuB != null) {
                return false;
            }
        } else if (!this.zzbuB.equals(com_google_android_gms_internal_zzcka.zzbuB)) {
            return false;
        }
        return (this.zzcrZ == null || this.zzcrZ.isEmpty()) ? com_google_android_gms_internal_zzcka.zzcrZ == null || com_google_android_gms_internal_zzcka.zzcrZ.isEmpty() : this.zzcrZ.equals(com_google_android_gms_internal_zzcka.zzcrZ);
    }

    public final int hashCode() {
        int i = 0;
        int hashCode = ((this.zzbuB == null ? 0 : this.zzbuB.hashCode()) + (((this.zzbuA == null ? 0 : this.zzbuA.hashCode()) + (((this.zzbvA == null ? 0 : this.zzbvA.hashCode()) + (((this.zzaIF == null ? 0 : this.zzaIF.hashCode()) + (((this.name == null ? 0 : this.name.hashCode()) + (((this.zzbwh == null ? 0 : this.zzbwh.hashCode()) + ((getClass().getName().hashCode() + 527) * 31)) * 31)) * 31)) * 31)) * 31)) * 31)) * 31;
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
                case 8:
                    this.zzbwh = Long.valueOf(com_google_android_gms_internal_acx.zzLE());
                    continue;
                case 18:
                    this.name = com_google_android_gms_internal_acx.readString();
                    continue;
                case NalUnitTypes.NAL_TYPE_RSV_VCL26 /*26*/:
                    this.zzaIF = com_google_android_gms_internal_acx.readString();
                    continue;
                case 32:
                    this.zzbvA = Long.valueOf(com_google_android_gms_internal_acx.zzLE());
                    continue;
                case MotionEventCompat.AXIS_GENERIC_14 /*45*/:
                    this.zzbuA = Float.valueOf(Float.intBitsToFloat(com_google_android_gms_internal_acx.zzLF()));
                    continue;
                case 49:
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
        if (this.zzbwh != null) {
            com_google_android_gms_internal_acy.zzb(1, this.zzbwh.longValue());
        }
        if (this.name != null) {
            com_google_android_gms_internal_acy.zzl(2, this.name);
        }
        if (this.zzaIF != null) {
            com_google_android_gms_internal_acy.zzl(3, this.zzaIF);
        }
        if (this.zzbvA != null) {
            com_google_android_gms_internal_acy.zzb(4, this.zzbvA.longValue());
        }
        if (this.zzbuA != null) {
            com_google_android_gms_internal_acy.zzc(5, this.zzbuA.floatValue());
        }
        if (this.zzbuB != null) {
            com_google_android_gms_internal_acy.zza(6, this.zzbuB.doubleValue());
        }
        super.zza(com_google_android_gms_internal_acy);
    }

    protected final int zzn() {
        int zzn = super.zzn();
        if (this.zzbwh != null) {
            zzn += acy.zze(1, this.zzbwh.longValue());
        }
        if (this.name != null) {
            zzn += acy.zzm(2, this.name);
        }
        if (this.zzaIF != null) {
            zzn += acy.zzm(3, this.zzaIF);
        }
        if (this.zzbvA != null) {
            zzn += acy.zze(4, this.zzbvA.longValue());
        }
        if (this.zzbuA != null) {
            this.zzbuA.floatValue();
            zzn += acy.zzct(5) + 4;
        }
        if (this.zzbuB == null) {
            return zzn;
        }
        this.zzbuB.doubleValue();
        return zzn + (acy.zzct(6) + 8);
    }
}
