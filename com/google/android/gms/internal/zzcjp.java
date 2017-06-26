package com.google.android.gms.internal;

import com.googlecode.mp4parser.authoring.tracks.h265.NalUnitTypes;
import java.io.IOException;

public final class zzcjp extends ada<zzcjp> {
    private static volatile zzcjp[] zzbvb;
    public Integer zzbuM;
    public String zzbvc;
    public zzcjn zzbvd;

    public zzcjp() {
        this.zzbuM = null;
        this.zzbvc = null;
        this.zzbvd = null;
        this.zzcrZ = null;
        this.zzcsi = -1;
    }

    public static zzcjp[] zzzx() {
        if (zzbvb == null) {
            synchronized (ade.zzcsh) {
                if (zzbvb == null) {
                    zzbvb = new zzcjp[0];
                }
            }
        }
        return zzbvb;
    }

    public final boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof zzcjp)) {
            return false;
        }
        zzcjp com_google_android_gms_internal_zzcjp = (zzcjp) obj;
        if (this.zzbuM == null) {
            if (com_google_android_gms_internal_zzcjp.zzbuM != null) {
                return false;
            }
        } else if (!this.zzbuM.equals(com_google_android_gms_internal_zzcjp.zzbuM)) {
            return false;
        }
        if (this.zzbvc == null) {
            if (com_google_android_gms_internal_zzcjp.zzbvc != null) {
                return false;
            }
        } else if (!this.zzbvc.equals(com_google_android_gms_internal_zzcjp.zzbvc)) {
            return false;
        }
        if (this.zzbvd == null) {
            if (com_google_android_gms_internal_zzcjp.zzbvd != null) {
                return false;
            }
        } else if (!this.zzbvd.equals(com_google_android_gms_internal_zzcjp.zzbvd)) {
            return false;
        }
        return (this.zzcrZ == null || this.zzcrZ.isEmpty()) ? com_google_android_gms_internal_zzcjp.zzcrZ == null || com_google_android_gms_internal_zzcjp.zzcrZ.isEmpty() : this.zzcrZ.equals(com_google_android_gms_internal_zzcjp.zzcrZ);
    }

    public final int hashCode() {
        int i = 0;
        int hashCode = ((this.zzbvd == null ? 0 : this.zzbvd.hashCode()) + (((this.zzbvc == null ? 0 : this.zzbvc.hashCode()) + (((this.zzbuM == null ? 0 : this.zzbuM.hashCode()) + ((getClass().getName().hashCode() + 527) * 31)) * 31)) * 31)) * 31;
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
                    this.zzbuM = Integer.valueOf(com_google_android_gms_internal_acx.zzLD());
                    continue;
                case 18:
                    this.zzbvc = com_google_android_gms_internal_acx.readString();
                    continue;
                case NalUnitTypes.NAL_TYPE_RSV_VCL26 /*26*/:
                    if (this.zzbvd == null) {
                        this.zzbvd = new zzcjn();
                    }
                    com_google_android_gms_internal_acx.zza(this.zzbvd);
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
        if (this.zzbuM != null) {
            com_google_android_gms_internal_acy.zzr(1, this.zzbuM.intValue());
        }
        if (this.zzbvc != null) {
            com_google_android_gms_internal_acy.zzl(2, this.zzbvc);
        }
        if (this.zzbvd != null) {
            com_google_android_gms_internal_acy.zza(3, this.zzbvd);
        }
        super.zza(com_google_android_gms_internal_acy);
    }

    protected final int zzn() {
        int zzn = super.zzn();
        if (this.zzbuM != null) {
            zzn += acy.zzs(1, this.zzbuM.intValue());
        }
        if (this.zzbvc != null) {
            zzn += acy.zzm(2, this.zzbvc);
        }
        return this.zzbvd != null ? zzn + acy.zzb(3, this.zzbvd) : zzn;
    }
}
