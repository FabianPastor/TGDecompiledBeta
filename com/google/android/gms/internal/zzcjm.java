package com.google.android.gms.internal;

import com.googlecode.mp4parser.authoring.tracks.h265.NalUnitTypes;
import java.io.IOException;

public final class zzcjm extends adj<zzcjm> {
    private static volatile zzcjm[] zzbuH;
    public Integer zzbuI;
    public zzcjq[] zzbuJ;
    public zzcjn[] zzbuK;

    public zzcjm() {
        this.zzbuI = null;
        this.zzbuJ = zzcjq.zzzx();
        this.zzbuK = zzcjn.zzzv();
        this.zzcso = null;
        this.zzcsx = -1;
    }

    public static zzcjm[] zzzu() {
        if (zzbuH == null) {
            synchronized (adn.zzcsw) {
                if (zzbuH == null) {
                    zzbuH = new zzcjm[0];
                }
            }
        }
        return zzbuH;
    }

    public final boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof zzcjm)) {
            return false;
        }
        zzcjm com_google_android_gms_internal_zzcjm = (zzcjm) obj;
        if (this.zzbuI == null) {
            if (com_google_android_gms_internal_zzcjm.zzbuI != null) {
                return false;
            }
        } else if (!this.zzbuI.equals(com_google_android_gms_internal_zzcjm.zzbuI)) {
            return false;
        }
        return !adn.equals(this.zzbuJ, com_google_android_gms_internal_zzcjm.zzbuJ) ? false : !adn.equals(this.zzbuK, com_google_android_gms_internal_zzcjm.zzbuK) ? false : (this.zzcso == null || this.zzcso.isEmpty()) ? com_google_android_gms_internal_zzcjm.zzcso == null || com_google_android_gms_internal_zzcjm.zzcso.isEmpty() : this.zzcso.equals(com_google_android_gms_internal_zzcjm.zzcso);
    }

    public final int hashCode() {
        int i = 0;
        int hashCode = ((((((this.zzbuI == null ? 0 : this.zzbuI.hashCode()) + ((getClass().getName().hashCode() + 527) * 31)) * 31) + adn.hashCode(this.zzbuJ)) * 31) + adn.hashCode(this.zzbuK)) * 31;
        if (!(this.zzcso == null || this.zzcso.isEmpty())) {
            i = this.zzcso.hashCode();
        }
        return hashCode + i;
    }

    public final /* synthetic */ adp zza(adg com_google_android_gms_internal_adg) throws IOException {
        while (true) {
            int zzLA = com_google_android_gms_internal_adg.zzLA();
            int zzb;
            Object obj;
            switch (zzLA) {
                case 0:
                    break;
                case 8:
                    this.zzbuI = Integer.valueOf(com_google_android_gms_internal_adg.zzLF());
                    continue;
                case 18:
                    zzb = ads.zzb(com_google_android_gms_internal_adg, 18);
                    zzLA = this.zzbuJ == null ? 0 : this.zzbuJ.length;
                    obj = new zzcjq[(zzb + zzLA)];
                    if (zzLA != 0) {
                        System.arraycopy(this.zzbuJ, 0, obj, 0, zzLA);
                    }
                    while (zzLA < obj.length - 1) {
                        obj[zzLA] = new zzcjq();
                        com_google_android_gms_internal_adg.zza(obj[zzLA]);
                        com_google_android_gms_internal_adg.zzLA();
                        zzLA++;
                    }
                    obj[zzLA] = new zzcjq();
                    com_google_android_gms_internal_adg.zza(obj[zzLA]);
                    this.zzbuJ = obj;
                    continue;
                case NalUnitTypes.NAL_TYPE_RSV_VCL26 /*26*/:
                    zzb = ads.zzb(com_google_android_gms_internal_adg, 26);
                    zzLA = this.zzbuK == null ? 0 : this.zzbuK.length;
                    obj = new zzcjn[(zzb + zzLA)];
                    if (zzLA != 0) {
                        System.arraycopy(this.zzbuK, 0, obj, 0, zzLA);
                    }
                    while (zzLA < obj.length - 1) {
                        obj[zzLA] = new zzcjn();
                        com_google_android_gms_internal_adg.zza(obj[zzLA]);
                        com_google_android_gms_internal_adg.zzLA();
                        zzLA++;
                    }
                    obj[zzLA] = new zzcjn();
                    com_google_android_gms_internal_adg.zza(obj[zzLA]);
                    this.zzbuK = obj;
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
        int i = 0;
        if (this.zzbuI != null) {
            com_google_android_gms_internal_adh.zzr(1, this.zzbuI.intValue());
        }
        if (this.zzbuJ != null && this.zzbuJ.length > 0) {
            for (adp com_google_android_gms_internal_adp : this.zzbuJ) {
                if (com_google_android_gms_internal_adp != null) {
                    com_google_android_gms_internal_adh.zza(2, com_google_android_gms_internal_adp);
                }
            }
        }
        if (this.zzbuK != null && this.zzbuK.length > 0) {
            while (i < this.zzbuK.length) {
                adp com_google_android_gms_internal_adp2 = this.zzbuK[i];
                if (com_google_android_gms_internal_adp2 != null) {
                    com_google_android_gms_internal_adh.zza(3, com_google_android_gms_internal_adp2);
                }
                i++;
            }
        }
        super.zza(com_google_android_gms_internal_adh);
    }

    protected final int zzn() {
        int i = 0;
        int zzn = super.zzn();
        if (this.zzbuI != null) {
            zzn += adh.zzs(1, this.zzbuI.intValue());
        }
        if (this.zzbuJ != null && this.zzbuJ.length > 0) {
            int i2 = zzn;
            for (adp com_google_android_gms_internal_adp : this.zzbuJ) {
                if (com_google_android_gms_internal_adp != null) {
                    i2 += adh.zzb(2, com_google_android_gms_internal_adp);
                }
            }
            zzn = i2;
        }
        if (this.zzbuK != null && this.zzbuK.length > 0) {
            while (i < this.zzbuK.length) {
                adp com_google_android_gms_internal_adp2 = this.zzbuK[i];
                if (com_google_android_gms_internal_adp2 != null) {
                    zzn += adh.zzb(3, com_google_android_gms_internal_adp2);
                }
                i++;
            }
        }
        return zzn;
    }
}
