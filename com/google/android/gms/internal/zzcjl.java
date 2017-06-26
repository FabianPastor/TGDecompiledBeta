package com.google.android.gms.internal;

import com.googlecode.mp4parser.authoring.tracks.h265.NalUnitTypes;
import java.io.IOException;

public final class zzcjl extends ada<zzcjl> {
    private static volatile zzcjl[] zzbuH;
    public Integer zzbuI;
    public zzcjp[] zzbuJ;
    public zzcjm[] zzbuK;

    public zzcjl() {
        this.zzbuI = null;
        this.zzbuJ = zzcjp.zzzx();
        this.zzbuK = zzcjm.zzzv();
        this.zzcrZ = null;
        this.zzcsi = -1;
    }

    public static zzcjl[] zzzu() {
        if (zzbuH == null) {
            synchronized (ade.zzcsh) {
                if (zzbuH == null) {
                    zzbuH = new zzcjl[0];
                }
            }
        }
        return zzbuH;
    }

    public final boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof zzcjl)) {
            return false;
        }
        zzcjl com_google_android_gms_internal_zzcjl = (zzcjl) obj;
        if (this.zzbuI == null) {
            if (com_google_android_gms_internal_zzcjl.zzbuI != null) {
                return false;
            }
        } else if (!this.zzbuI.equals(com_google_android_gms_internal_zzcjl.zzbuI)) {
            return false;
        }
        return !ade.equals(this.zzbuJ, com_google_android_gms_internal_zzcjl.zzbuJ) ? false : !ade.equals(this.zzbuK, com_google_android_gms_internal_zzcjl.zzbuK) ? false : (this.zzcrZ == null || this.zzcrZ.isEmpty()) ? com_google_android_gms_internal_zzcjl.zzcrZ == null || com_google_android_gms_internal_zzcjl.zzcrZ.isEmpty() : this.zzcrZ.equals(com_google_android_gms_internal_zzcjl.zzcrZ);
    }

    public final int hashCode() {
        int i = 0;
        int hashCode = ((((((this.zzbuI == null ? 0 : this.zzbuI.hashCode()) + ((getClass().getName().hashCode() + 527) * 31)) * 31) + ade.hashCode(this.zzbuJ)) * 31) + ade.hashCode(this.zzbuK)) * 31;
        if (!(this.zzcrZ == null || this.zzcrZ.isEmpty())) {
            i = this.zzcrZ.hashCode();
        }
        return hashCode + i;
    }

    public final /* synthetic */ adg zza(acx com_google_android_gms_internal_acx) throws IOException {
        while (true) {
            int zzLy = com_google_android_gms_internal_acx.zzLy();
            int zzb;
            Object obj;
            switch (zzLy) {
                case 0:
                    break;
                case 8:
                    this.zzbuI = Integer.valueOf(com_google_android_gms_internal_acx.zzLD());
                    continue;
                case 18:
                    zzb = adj.zzb(com_google_android_gms_internal_acx, 18);
                    zzLy = this.zzbuJ == null ? 0 : this.zzbuJ.length;
                    obj = new zzcjp[(zzb + zzLy)];
                    if (zzLy != 0) {
                        System.arraycopy(this.zzbuJ, 0, obj, 0, zzLy);
                    }
                    while (zzLy < obj.length - 1) {
                        obj[zzLy] = new zzcjp();
                        com_google_android_gms_internal_acx.zza(obj[zzLy]);
                        com_google_android_gms_internal_acx.zzLy();
                        zzLy++;
                    }
                    obj[zzLy] = new zzcjp();
                    com_google_android_gms_internal_acx.zza(obj[zzLy]);
                    this.zzbuJ = obj;
                    continue;
                case NalUnitTypes.NAL_TYPE_RSV_VCL26 /*26*/:
                    zzb = adj.zzb(com_google_android_gms_internal_acx, 26);
                    zzLy = this.zzbuK == null ? 0 : this.zzbuK.length;
                    obj = new zzcjm[(zzb + zzLy)];
                    if (zzLy != 0) {
                        System.arraycopy(this.zzbuK, 0, obj, 0, zzLy);
                    }
                    while (zzLy < obj.length - 1) {
                        obj[zzLy] = new zzcjm();
                        com_google_android_gms_internal_acx.zza(obj[zzLy]);
                        com_google_android_gms_internal_acx.zzLy();
                        zzLy++;
                    }
                    obj[zzLy] = new zzcjm();
                    com_google_android_gms_internal_acx.zza(obj[zzLy]);
                    this.zzbuK = obj;
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
        int i = 0;
        if (this.zzbuI != null) {
            com_google_android_gms_internal_acy.zzr(1, this.zzbuI.intValue());
        }
        if (this.zzbuJ != null && this.zzbuJ.length > 0) {
            for (adg com_google_android_gms_internal_adg : this.zzbuJ) {
                if (com_google_android_gms_internal_adg != null) {
                    com_google_android_gms_internal_acy.zza(2, com_google_android_gms_internal_adg);
                }
            }
        }
        if (this.zzbuK != null && this.zzbuK.length > 0) {
            while (i < this.zzbuK.length) {
                adg com_google_android_gms_internal_adg2 = this.zzbuK[i];
                if (com_google_android_gms_internal_adg2 != null) {
                    com_google_android_gms_internal_acy.zza(3, com_google_android_gms_internal_adg2);
                }
                i++;
            }
        }
        super.zza(com_google_android_gms_internal_acy);
    }

    protected final int zzn() {
        int i = 0;
        int zzn = super.zzn();
        if (this.zzbuI != null) {
            zzn += acy.zzs(1, this.zzbuI.intValue());
        }
        if (this.zzbuJ != null && this.zzbuJ.length > 0) {
            int i2 = zzn;
            for (adg com_google_android_gms_internal_adg : this.zzbuJ) {
                if (com_google_android_gms_internal_adg != null) {
                    i2 += acy.zzb(2, com_google_android_gms_internal_adg);
                }
            }
            zzn = i2;
        }
        if (this.zzbuK != null && this.zzbuK.length > 0) {
            while (i < this.zzbuK.length) {
                adg com_google_android_gms_internal_adg2 = this.zzbuK[i];
                if (com_google_android_gms_internal_adg2 != null) {
                    zzn += acy.zzb(3, com_google_android_gms_internal_adg2);
                }
                i++;
            }
        }
        return zzn;
    }
}
