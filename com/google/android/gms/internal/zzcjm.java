package com.google.android.gms.internal;

import com.googlecode.mp4parser.authoring.tracks.h265.NalUnitTypes;
import java.io.IOException;

public final class zzcjm extends ada<zzcjm> {
    private static volatile zzcjm[] zzbuL;
    public Integer zzbuM;
    public String zzbuN;
    public zzcjn[] zzbuO;
    private Boolean zzbuP;
    public zzcjo zzbuQ;

    public zzcjm() {
        this.zzbuM = null;
        this.zzbuN = null;
        this.zzbuO = zzcjn.zzzw();
        this.zzbuP = null;
        this.zzbuQ = null;
        this.zzcrZ = null;
        this.zzcsi = -1;
    }

    public static zzcjm[] zzzv() {
        if (zzbuL == null) {
            synchronized (ade.zzcsh) {
                if (zzbuL == null) {
                    zzbuL = new zzcjm[0];
                }
            }
        }
        return zzbuL;
    }

    public final boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof zzcjm)) {
            return false;
        }
        zzcjm com_google_android_gms_internal_zzcjm = (zzcjm) obj;
        if (this.zzbuM == null) {
            if (com_google_android_gms_internal_zzcjm.zzbuM != null) {
                return false;
            }
        } else if (!this.zzbuM.equals(com_google_android_gms_internal_zzcjm.zzbuM)) {
            return false;
        }
        if (this.zzbuN == null) {
            if (com_google_android_gms_internal_zzcjm.zzbuN != null) {
                return false;
            }
        } else if (!this.zzbuN.equals(com_google_android_gms_internal_zzcjm.zzbuN)) {
            return false;
        }
        if (!ade.equals(this.zzbuO, com_google_android_gms_internal_zzcjm.zzbuO)) {
            return false;
        }
        if (this.zzbuP == null) {
            if (com_google_android_gms_internal_zzcjm.zzbuP != null) {
                return false;
            }
        } else if (!this.zzbuP.equals(com_google_android_gms_internal_zzcjm.zzbuP)) {
            return false;
        }
        if (this.zzbuQ == null) {
            if (com_google_android_gms_internal_zzcjm.zzbuQ != null) {
                return false;
            }
        } else if (!this.zzbuQ.equals(com_google_android_gms_internal_zzcjm.zzbuQ)) {
            return false;
        }
        return (this.zzcrZ == null || this.zzcrZ.isEmpty()) ? com_google_android_gms_internal_zzcjm.zzcrZ == null || com_google_android_gms_internal_zzcjm.zzcrZ.isEmpty() : this.zzcrZ.equals(com_google_android_gms_internal_zzcjm.zzcrZ);
    }

    public final int hashCode() {
        int i = 0;
        int hashCode = ((this.zzbuQ == null ? 0 : this.zzbuQ.hashCode()) + (((this.zzbuP == null ? 0 : this.zzbuP.hashCode()) + (((((this.zzbuN == null ? 0 : this.zzbuN.hashCode()) + (((this.zzbuM == null ? 0 : this.zzbuM.hashCode()) + ((getClass().getName().hashCode() + 527) * 31)) * 31)) * 31) + ade.hashCode(this.zzbuO)) * 31)) * 31)) * 31;
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
                    this.zzbuN = com_google_android_gms_internal_acx.readString();
                    continue;
                case NalUnitTypes.NAL_TYPE_RSV_VCL26 /*26*/:
                    int zzb = adj.zzb(com_google_android_gms_internal_acx, 26);
                    zzLy = this.zzbuO == null ? 0 : this.zzbuO.length;
                    Object obj = new zzcjn[(zzb + zzLy)];
                    if (zzLy != 0) {
                        System.arraycopy(this.zzbuO, 0, obj, 0, zzLy);
                    }
                    while (zzLy < obj.length - 1) {
                        obj[zzLy] = new zzcjn();
                        com_google_android_gms_internal_acx.zza(obj[zzLy]);
                        com_google_android_gms_internal_acx.zzLy();
                        zzLy++;
                    }
                    obj[zzLy] = new zzcjn();
                    com_google_android_gms_internal_acx.zza(obj[zzLy]);
                    this.zzbuO = obj;
                    continue;
                case 32:
                    this.zzbuP = Boolean.valueOf(com_google_android_gms_internal_acx.zzLB());
                    continue;
                case 42:
                    if (this.zzbuQ == null) {
                        this.zzbuQ = new zzcjo();
                    }
                    com_google_android_gms_internal_acx.zza(this.zzbuQ);
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
        if (this.zzbuN != null) {
            com_google_android_gms_internal_acy.zzl(2, this.zzbuN);
        }
        if (this.zzbuO != null && this.zzbuO.length > 0) {
            for (adg com_google_android_gms_internal_adg : this.zzbuO) {
                if (com_google_android_gms_internal_adg != null) {
                    com_google_android_gms_internal_acy.zza(3, com_google_android_gms_internal_adg);
                }
            }
        }
        if (this.zzbuP != null) {
            com_google_android_gms_internal_acy.zzk(4, this.zzbuP.booleanValue());
        }
        if (this.zzbuQ != null) {
            com_google_android_gms_internal_acy.zza(5, this.zzbuQ);
        }
        super.zza(com_google_android_gms_internal_acy);
    }

    protected final int zzn() {
        int zzn = super.zzn();
        if (this.zzbuM != null) {
            zzn += acy.zzs(1, this.zzbuM.intValue());
        }
        if (this.zzbuN != null) {
            zzn += acy.zzm(2, this.zzbuN);
        }
        if (this.zzbuO != null && this.zzbuO.length > 0) {
            int i = zzn;
            for (adg com_google_android_gms_internal_adg : this.zzbuO) {
                if (com_google_android_gms_internal_adg != null) {
                    i += acy.zzb(3, com_google_android_gms_internal_adg);
                }
            }
            zzn = i;
        }
        if (this.zzbuP != null) {
            this.zzbuP.booleanValue();
            zzn += acy.zzct(4) + 1;
        }
        return this.zzbuQ != null ? zzn + acy.zzb(5, this.zzbuQ) : zzn;
    }
}
