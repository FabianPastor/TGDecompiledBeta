package com.google.android.gms.internal;

import android.support.v4.view.MotionEventCompat;
import com.googlecode.mp4parser.authoring.tracks.h265.NalUnitTypes;
import java.io.IOException;

public final class zzckb extends adj<zzckb> {
    private static volatile zzckb[] zzbwg;
    public String name;
    public String zzaIF;
    private Float zzbuA;
    public Double zzbuB;
    public Long zzbvA;
    public Long zzbwh;

    public zzckb() {
        this.zzbwh = null;
        this.name = null;
        this.zzaIF = null;
        this.zzbvA = null;
        this.zzbuA = null;
        this.zzbuB = null;
        this.zzcso = null;
        this.zzcsx = -1;
    }

    public static zzckb[] zzzE() {
        if (zzbwg == null) {
            synchronized (adn.zzcsw) {
                if (zzbwg == null) {
                    zzbwg = new zzckb[0];
                }
            }
        }
        return zzbwg;
    }

    public final boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof zzckb)) {
            return false;
        }
        zzckb com_google_android_gms_internal_zzckb = (zzckb) obj;
        if (this.zzbwh == null) {
            if (com_google_android_gms_internal_zzckb.zzbwh != null) {
                return false;
            }
        } else if (!this.zzbwh.equals(com_google_android_gms_internal_zzckb.zzbwh)) {
            return false;
        }
        if (this.name == null) {
            if (com_google_android_gms_internal_zzckb.name != null) {
                return false;
            }
        } else if (!this.name.equals(com_google_android_gms_internal_zzckb.name)) {
            return false;
        }
        if (this.zzaIF == null) {
            if (com_google_android_gms_internal_zzckb.zzaIF != null) {
                return false;
            }
        } else if (!this.zzaIF.equals(com_google_android_gms_internal_zzckb.zzaIF)) {
            return false;
        }
        if (this.zzbvA == null) {
            if (com_google_android_gms_internal_zzckb.zzbvA != null) {
                return false;
            }
        } else if (!this.zzbvA.equals(com_google_android_gms_internal_zzckb.zzbvA)) {
            return false;
        }
        if (this.zzbuA == null) {
            if (com_google_android_gms_internal_zzckb.zzbuA != null) {
                return false;
            }
        } else if (!this.zzbuA.equals(com_google_android_gms_internal_zzckb.zzbuA)) {
            return false;
        }
        if (this.zzbuB == null) {
            if (com_google_android_gms_internal_zzckb.zzbuB != null) {
                return false;
            }
        } else if (!this.zzbuB.equals(com_google_android_gms_internal_zzckb.zzbuB)) {
            return false;
        }
        return (this.zzcso == null || this.zzcso.isEmpty()) ? com_google_android_gms_internal_zzckb.zzcso == null || com_google_android_gms_internal_zzckb.zzcso.isEmpty() : this.zzcso.equals(com_google_android_gms_internal_zzckb.zzcso);
    }

    public final int hashCode() {
        int i = 0;
        int hashCode = ((this.zzbuB == null ? 0 : this.zzbuB.hashCode()) + (((this.zzbuA == null ? 0 : this.zzbuA.hashCode()) + (((this.zzbvA == null ? 0 : this.zzbvA.hashCode()) + (((this.zzaIF == null ? 0 : this.zzaIF.hashCode()) + (((this.name == null ? 0 : this.name.hashCode()) + (((this.zzbwh == null ? 0 : this.zzbwh.hashCode()) + ((getClass().getName().hashCode() + 527) * 31)) * 31)) * 31)) * 31)) * 31)) * 31)) * 31;
        if (!(this.zzcso == null || this.zzcso.isEmpty())) {
            i = this.zzcso.hashCode();
        }
        return hashCode + i;
    }

    public final /* synthetic */ adp zza(adg com_google_android_gms_internal_adg) throws IOException {
        while (true) {
            int zzLA = com_google_android_gms_internal_adg.zzLA();
            switch (zzLA) {
                case 0:
                    break;
                case 8:
                    this.zzbwh = Long.valueOf(com_google_android_gms_internal_adg.zzLG());
                    continue;
                case 18:
                    this.name = com_google_android_gms_internal_adg.readString();
                    continue;
                case NalUnitTypes.NAL_TYPE_RSV_VCL26 /*26*/:
                    this.zzaIF = com_google_android_gms_internal_adg.readString();
                    continue;
                case 32:
                    this.zzbvA = Long.valueOf(com_google_android_gms_internal_adg.zzLG());
                    continue;
                case MotionEventCompat.AXIS_GENERIC_14 /*45*/:
                    this.zzbuA = Float.valueOf(Float.intBitsToFloat(com_google_android_gms_internal_adg.zzLH()));
                    continue;
                case 49:
                    this.zzbuB = Double.valueOf(Double.longBitsToDouble(com_google_android_gms_internal_adg.zzLI()));
                    continue;
                default:
                    if (!super.zza(com_google_android_gms_internal_adg, zzLA)) {
                        break;
                    }
                    continue;
            }
            return this;
        }
    }

    public final void zza(adh com_google_android_gms_internal_adh) throws IOException {
        if (this.zzbwh != null) {
            com_google_android_gms_internal_adh.zzb(1, this.zzbwh.longValue());
        }
        if (this.name != null) {
            com_google_android_gms_internal_adh.zzl(2, this.name);
        }
        if (this.zzaIF != null) {
            com_google_android_gms_internal_adh.zzl(3, this.zzaIF);
        }
        if (this.zzbvA != null) {
            com_google_android_gms_internal_adh.zzb(4, this.zzbvA.longValue());
        }
        if (this.zzbuA != null) {
            com_google_android_gms_internal_adh.zzc(5, this.zzbuA.floatValue());
        }
        if (this.zzbuB != null) {
            com_google_android_gms_internal_adh.zza(6, this.zzbuB.doubleValue());
        }
        super.zza(com_google_android_gms_internal_adh);
    }

    protected final int zzn() {
        int zzn = super.zzn();
        if (this.zzbwh != null) {
            zzn += adh.zze(1, this.zzbwh.longValue());
        }
        if (this.name != null) {
            zzn += adh.zzm(2, this.name);
        }
        if (this.zzaIF != null) {
            zzn += adh.zzm(3, this.zzaIF);
        }
        if (this.zzbvA != null) {
            zzn += adh.zze(4, this.zzbvA.longValue());
        }
        if (this.zzbuA != null) {
            this.zzbuA.floatValue();
            zzn += adh.zzct(5) + 4;
        }
        if (this.zzbuB == null) {
            return zzn;
        }
        this.zzbuB.doubleValue();
        return zzn + (adh.zzct(6) + 8);
    }
}
