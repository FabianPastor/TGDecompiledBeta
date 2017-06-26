package com.google.android.gms.internal;

import com.googlecode.mp4parser.authoring.tracks.h265.NalUnitTypes;
import java.io.IOException;

public final class zzcju extends ada<zzcju> {
    private static volatile zzcju[] zzbvr;
    public Integer zzbuI;
    public zzcjz zzbvs;
    public zzcjz zzbvt;
    public Boolean zzbvu;

    public zzcju() {
        this.zzbuI = null;
        this.zzbvs = null;
        this.zzbvt = null;
        this.zzbvu = null;
        this.zzcrZ = null;
        this.zzcsi = -1;
    }

    public static zzcju[] zzzA() {
        if (zzbvr == null) {
            synchronized (ade.zzcsh) {
                if (zzbvr == null) {
                    zzbvr = new zzcju[0];
                }
            }
        }
        return zzbvr;
    }

    public final boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof zzcju)) {
            return false;
        }
        zzcju com_google_android_gms_internal_zzcju = (zzcju) obj;
        if (this.zzbuI == null) {
            if (com_google_android_gms_internal_zzcju.zzbuI != null) {
                return false;
            }
        } else if (!this.zzbuI.equals(com_google_android_gms_internal_zzcju.zzbuI)) {
            return false;
        }
        if (this.zzbvs == null) {
            if (com_google_android_gms_internal_zzcju.zzbvs != null) {
                return false;
            }
        } else if (!this.zzbvs.equals(com_google_android_gms_internal_zzcju.zzbvs)) {
            return false;
        }
        if (this.zzbvt == null) {
            if (com_google_android_gms_internal_zzcju.zzbvt != null) {
                return false;
            }
        } else if (!this.zzbvt.equals(com_google_android_gms_internal_zzcju.zzbvt)) {
            return false;
        }
        if (this.zzbvu == null) {
            if (com_google_android_gms_internal_zzcju.zzbvu != null) {
                return false;
            }
        } else if (!this.zzbvu.equals(com_google_android_gms_internal_zzcju.zzbvu)) {
            return false;
        }
        return (this.zzcrZ == null || this.zzcrZ.isEmpty()) ? com_google_android_gms_internal_zzcju.zzcrZ == null || com_google_android_gms_internal_zzcju.zzcrZ.isEmpty() : this.zzcrZ.equals(com_google_android_gms_internal_zzcju.zzcrZ);
    }

    public final int hashCode() {
        int i = 0;
        int hashCode = ((this.zzbvu == null ? 0 : this.zzbvu.hashCode()) + (((this.zzbvt == null ? 0 : this.zzbvt.hashCode()) + (((this.zzbvs == null ? 0 : this.zzbvs.hashCode()) + (((this.zzbuI == null ? 0 : this.zzbuI.hashCode()) + ((getClass().getName().hashCode() + 527) * 31)) * 31)) * 31)) * 31)) * 31;
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
                    this.zzbuI = Integer.valueOf(com_google_android_gms_internal_acx.zzLD());
                    continue;
                case 18:
                    if (this.zzbvs == null) {
                        this.zzbvs = new zzcjz();
                    }
                    com_google_android_gms_internal_acx.zza(this.zzbvs);
                    continue;
                case NalUnitTypes.NAL_TYPE_RSV_VCL26 /*26*/:
                    if (this.zzbvt == null) {
                        this.zzbvt = new zzcjz();
                    }
                    com_google_android_gms_internal_acx.zza(this.zzbvt);
                    continue;
                case 32:
                    this.zzbvu = Boolean.valueOf(com_google_android_gms_internal_acx.zzLB());
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
        if (this.zzbuI != null) {
            com_google_android_gms_internal_acy.zzr(1, this.zzbuI.intValue());
        }
        if (this.zzbvs != null) {
            com_google_android_gms_internal_acy.zza(2, this.zzbvs);
        }
        if (this.zzbvt != null) {
            com_google_android_gms_internal_acy.zza(3, this.zzbvt);
        }
        if (this.zzbvu != null) {
            com_google_android_gms_internal_acy.zzk(4, this.zzbvu.booleanValue());
        }
        super.zza(com_google_android_gms_internal_acy);
    }

    protected final int zzn() {
        int zzn = super.zzn();
        if (this.zzbuI != null) {
            zzn += acy.zzs(1, this.zzbuI.intValue());
        }
        if (this.zzbvs != null) {
            zzn += acy.zzb(2, this.zzbvs);
        }
        if (this.zzbvt != null) {
            zzn += acy.zzb(3, this.zzbvt);
        }
        if (this.zzbvu == null) {
            return zzn;
        }
        this.zzbvu.booleanValue();
        return zzn + (acy.zzct(4) + 1);
    }
}
