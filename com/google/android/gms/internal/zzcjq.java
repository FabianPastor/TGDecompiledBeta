package com.google.android.gms.internal;

import com.googlecode.mp4parser.authoring.tracks.h265.NalUnitTypes;
import java.io.IOException;

public final class zzcjq extends adj<zzcjq> {
    private static volatile zzcjq[] zzbvb;
    public Integer zzbuM;
    public String zzbvc;
    public zzcjo zzbvd;

    public zzcjq() {
        this.zzbuM = null;
        this.zzbvc = null;
        this.zzbvd = null;
        this.zzcso = null;
        this.zzcsx = -1;
    }

    public static zzcjq[] zzzx() {
        if (zzbvb == null) {
            synchronized (adn.zzcsw) {
                if (zzbvb == null) {
                    zzbvb = new zzcjq[0];
                }
            }
        }
        return zzbvb;
    }

    public final boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof zzcjq)) {
            return false;
        }
        zzcjq com_google_android_gms_internal_zzcjq = (zzcjq) obj;
        if (this.zzbuM == null) {
            if (com_google_android_gms_internal_zzcjq.zzbuM != null) {
                return false;
            }
        } else if (!this.zzbuM.equals(com_google_android_gms_internal_zzcjq.zzbuM)) {
            return false;
        }
        if (this.zzbvc == null) {
            if (com_google_android_gms_internal_zzcjq.zzbvc != null) {
                return false;
            }
        } else if (!this.zzbvc.equals(com_google_android_gms_internal_zzcjq.zzbvc)) {
            return false;
        }
        if (this.zzbvd == null) {
            if (com_google_android_gms_internal_zzcjq.zzbvd != null) {
                return false;
            }
        } else if (!this.zzbvd.equals(com_google_android_gms_internal_zzcjq.zzbvd)) {
            return false;
        }
        return (this.zzcso == null || this.zzcso.isEmpty()) ? com_google_android_gms_internal_zzcjq.zzcso == null || com_google_android_gms_internal_zzcjq.zzcso.isEmpty() : this.zzcso.equals(com_google_android_gms_internal_zzcjq.zzcso);
    }

    public final int hashCode() {
        int i = 0;
        int hashCode = ((this.zzbvd == null ? 0 : this.zzbvd.hashCode()) + (((this.zzbvc == null ? 0 : this.zzbvc.hashCode()) + (((this.zzbuM == null ? 0 : this.zzbuM.hashCode()) + ((getClass().getName().hashCode() + 527) * 31)) * 31)) * 31)) * 31;
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
                    this.zzbuM = Integer.valueOf(com_google_android_gms_internal_adg.zzLF());
                    continue;
                case 18:
                    this.zzbvc = com_google_android_gms_internal_adg.readString();
                    continue;
                case NalUnitTypes.NAL_TYPE_RSV_VCL26 /*26*/:
                    if (this.zzbvd == null) {
                        this.zzbvd = new zzcjo();
                    }
                    com_google_android_gms_internal_adg.zza(this.zzbvd);
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
        if (this.zzbuM != null) {
            com_google_android_gms_internal_adh.zzr(1, this.zzbuM.intValue());
        }
        if (this.zzbvc != null) {
            com_google_android_gms_internal_adh.zzl(2, this.zzbvc);
        }
        if (this.zzbvd != null) {
            com_google_android_gms_internal_adh.zza(3, this.zzbvd);
        }
        super.zza(com_google_android_gms_internal_adh);
    }

    protected final int zzn() {
        int zzn = super.zzn();
        if (this.zzbuM != null) {
            zzn += adh.zzs(1, this.zzbuM.intValue());
        }
        if (this.zzbvc != null) {
            zzn += adh.zzm(2, this.zzbvc);
        }
        return this.zzbvd != null ? zzn + adh.zzb(3, this.zzbvd) : zzn;
    }
}
