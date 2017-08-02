package com.google.android.gms.internal;

import com.googlecode.mp4parser.authoring.tracks.h265.NalUnitTypes;
import java.io.IOException;

public final class zzcjv extends adj<zzcjv> {
    private static volatile zzcjv[] zzbvr;
    public Integer zzbuI;
    public zzcka zzbvs;
    public zzcka zzbvt;
    public Boolean zzbvu;

    public zzcjv() {
        this.zzbuI = null;
        this.zzbvs = null;
        this.zzbvt = null;
        this.zzbvu = null;
        this.zzcso = null;
        this.zzcsx = -1;
    }

    public static zzcjv[] zzzA() {
        if (zzbvr == null) {
            synchronized (adn.zzcsw) {
                if (zzbvr == null) {
                    zzbvr = new zzcjv[0];
                }
            }
        }
        return zzbvr;
    }

    public final boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof zzcjv)) {
            return false;
        }
        zzcjv com_google_android_gms_internal_zzcjv = (zzcjv) obj;
        if (this.zzbuI == null) {
            if (com_google_android_gms_internal_zzcjv.zzbuI != null) {
                return false;
            }
        } else if (!this.zzbuI.equals(com_google_android_gms_internal_zzcjv.zzbuI)) {
            return false;
        }
        if (this.zzbvs == null) {
            if (com_google_android_gms_internal_zzcjv.zzbvs != null) {
                return false;
            }
        } else if (!this.zzbvs.equals(com_google_android_gms_internal_zzcjv.zzbvs)) {
            return false;
        }
        if (this.zzbvt == null) {
            if (com_google_android_gms_internal_zzcjv.zzbvt != null) {
                return false;
            }
        } else if (!this.zzbvt.equals(com_google_android_gms_internal_zzcjv.zzbvt)) {
            return false;
        }
        if (this.zzbvu == null) {
            if (com_google_android_gms_internal_zzcjv.zzbvu != null) {
                return false;
            }
        } else if (!this.zzbvu.equals(com_google_android_gms_internal_zzcjv.zzbvu)) {
            return false;
        }
        return (this.zzcso == null || this.zzcso.isEmpty()) ? com_google_android_gms_internal_zzcjv.zzcso == null || com_google_android_gms_internal_zzcjv.zzcso.isEmpty() : this.zzcso.equals(com_google_android_gms_internal_zzcjv.zzcso);
    }

    public final int hashCode() {
        int i = 0;
        int hashCode = ((this.zzbvu == null ? 0 : this.zzbvu.hashCode()) + (((this.zzbvt == null ? 0 : this.zzbvt.hashCode()) + (((this.zzbvs == null ? 0 : this.zzbvs.hashCode()) + (((this.zzbuI == null ? 0 : this.zzbuI.hashCode()) + ((getClass().getName().hashCode() + 527) * 31)) * 31)) * 31)) * 31)) * 31;
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
                    this.zzbuI = Integer.valueOf(com_google_android_gms_internal_adg.zzLF());
                    continue;
                case 18:
                    if (this.zzbvs == null) {
                        this.zzbvs = new zzcka();
                    }
                    com_google_android_gms_internal_adg.zza(this.zzbvs);
                    continue;
                case NalUnitTypes.NAL_TYPE_RSV_VCL26 /*26*/:
                    if (this.zzbvt == null) {
                        this.zzbvt = new zzcka();
                    }
                    com_google_android_gms_internal_adg.zza(this.zzbvt);
                    continue;
                case 32:
                    this.zzbvu = Boolean.valueOf(com_google_android_gms_internal_adg.zzLD());
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
        if (this.zzbuI != null) {
            com_google_android_gms_internal_adh.zzr(1, this.zzbuI.intValue());
        }
        if (this.zzbvs != null) {
            com_google_android_gms_internal_adh.zza(2, this.zzbvs);
        }
        if (this.zzbvt != null) {
            com_google_android_gms_internal_adh.zza(3, this.zzbvt);
        }
        if (this.zzbvu != null) {
            com_google_android_gms_internal_adh.zzk(4, this.zzbvu.booleanValue());
        }
        super.zza(com_google_android_gms_internal_adh);
    }

    protected final int zzn() {
        int zzn = super.zzn();
        if (this.zzbuI != null) {
            zzn += adh.zzs(1, this.zzbuI.intValue());
        }
        if (this.zzbvs != null) {
            zzn += adh.zzb(2, this.zzbvs);
        }
        if (this.zzbvt != null) {
            zzn += adh.zzb(3, this.zzbvt);
        }
        if (this.zzbvu == null) {
            return zzn;
        }
        this.zzbvu.booleanValue();
        return zzn + (adh.zzct(4) + 1);
    }
}
