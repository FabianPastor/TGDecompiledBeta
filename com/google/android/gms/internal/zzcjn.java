package com.google.android.gms.internal;

import com.googlecode.mp4parser.authoring.tracks.h265.NalUnitTypes;
import java.io.IOException;

public final class zzcjn extends adj<zzcjn> {
    private static volatile zzcjn[] zzbuL;
    public Integer zzbuM;
    public String zzbuN;
    public zzcjo[] zzbuO;
    private Boolean zzbuP;
    public zzcjp zzbuQ;

    public zzcjn() {
        this.zzbuM = null;
        this.zzbuN = null;
        this.zzbuO = zzcjo.zzzw();
        this.zzbuP = null;
        this.zzbuQ = null;
        this.zzcso = null;
        this.zzcsx = -1;
    }

    public static zzcjn[] zzzv() {
        if (zzbuL == null) {
            synchronized (adn.zzcsw) {
                if (zzbuL == null) {
                    zzbuL = new zzcjn[0];
                }
            }
        }
        return zzbuL;
    }

    public final boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof zzcjn)) {
            return false;
        }
        zzcjn com_google_android_gms_internal_zzcjn = (zzcjn) obj;
        if (this.zzbuM == null) {
            if (com_google_android_gms_internal_zzcjn.zzbuM != null) {
                return false;
            }
        } else if (!this.zzbuM.equals(com_google_android_gms_internal_zzcjn.zzbuM)) {
            return false;
        }
        if (this.zzbuN == null) {
            if (com_google_android_gms_internal_zzcjn.zzbuN != null) {
                return false;
            }
        } else if (!this.zzbuN.equals(com_google_android_gms_internal_zzcjn.zzbuN)) {
            return false;
        }
        if (!adn.equals(this.zzbuO, com_google_android_gms_internal_zzcjn.zzbuO)) {
            return false;
        }
        if (this.zzbuP == null) {
            if (com_google_android_gms_internal_zzcjn.zzbuP != null) {
                return false;
            }
        } else if (!this.zzbuP.equals(com_google_android_gms_internal_zzcjn.zzbuP)) {
            return false;
        }
        if (this.zzbuQ == null) {
            if (com_google_android_gms_internal_zzcjn.zzbuQ != null) {
                return false;
            }
        } else if (!this.zzbuQ.equals(com_google_android_gms_internal_zzcjn.zzbuQ)) {
            return false;
        }
        return (this.zzcso == null || this.zzcso.isEmpty()) ? com_google_android_gms_internal_zzcjn.zzcso == null || com_google_android_gms_internal_zzcjn.zzcso.isEmpty() : this.zzcso.equals(com_google_android_gms_internal_zzcjn.zzcso);
    }

    public final int hashCode() {
        int i = 0;
        int hashCode = ((this.zzbuQ == null ? 0 : this.zzbuQ.hashCode()) + (((this.zzbuP == null ? 0 : this.zzbuP.hashCode()) + (((((this.zzbuN == null ? 0 : this.zzbuN.hashCode()) + (((this.zzbuM == null ? 0 : this.zzbuM.hashCode()) + ((getClass().getName().hashCode() + 527) * 31)) * 31)) * 31) + adn.hashCode(this.zzbuO)) * 31)) * 31)) * 31;
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
                    this.zzbuN = com_google_android_gms_internal_adg.readString();
                    continue;
                case NalUnitTypes.NAL_TYPE_RSV_VCL26 /*26*/:
                    int zzb = ads.zzb(com_google_android_gms_internal_adg, 26);
                    zzLA = this.zzbuO == null ? 0 : this.zzbuO.length;
                    Object obj = new zzcjo[(zzb + zzLA)];
                    if (zzLA != 0) {
                        System.arraycopy(this.zzbuO, 0, obj, 0, zzLA);
                    }
                    while (zzLA < obj.length - 1) {
                        obj[zzLA] = new zzcjo();
                        com_google_android_gms_internal_adg.zza(obj[zzLA]);
                        com_google_android_gms_internal_adg.zzLA();
                        zzLA++;
                    }
                    obj[zzLA] = new zzcjo();
                    com_google_android_gms_internal_adg.zza(obj[zzLA]);
                    this.zzbuO = obj;
                    continue;
                case 32:
                    this.zzbuP = Boolean.valueOf(com_google_android_gms_internal_adg.zzLD());
                    continue;
                case 42:
                    if (this.zzbuQ == null) {
                        this.zzbuQ = new zzcjp();
                    }
                    com_google_android_gms_internal_adg.zza(this.zzbuQ);
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
        if (this.zzbuN != null) {
            com_google_android_gms_internal_adh.zzl(2, this.zzbuN);
        }
        if (this.zzbuO != null && this.zzbuO.length > 0) {
            for (adp com_google_android_gms_internal_adp : this.zzbuO) {
                if (com_google_android_gms_internal_adp != null) {
                    com_google_android_gms_internal_adh.zza(3, com_google_android_gms_internal_adp);
                }
            }
        }
        if (this.zzbuP != null) {
            com_google_android_gms_internal_adh.zzk(4, this.zzbuP.booleanValue());
        }
        if (this.zzbuQ != null) {
            com_google_android_gms_internal_adh.zza(5, this.zzbuQ);
        }
        super.zza(com_google_android_gms_internal_adh);
    }

    protected final int zzn() {
        int zzn = super.zzn();
        if (this.zzbuM != null) {
            zzn += adh.zzs(1, this.zzbuM.intValue());
        }
        if (this.zzbuN != null) {
            zzn += adh.zzm(2, this.zzbuN);
        }
        if (this.zzbuO != null && this.zzbuO.length > 0) {
            int i = zzn;
            for (adp com_google_android_gms_internal_adp : this.zzbuO) {
                if (com_google_android_gms_internal_adp != null) {
                    i += adh.zzb(3, com_google_android_gms_internal_adp);
                }
            }
            zzn = i;
        }
        if (this.zzbuP != null) {
            this.zzbuP.booleanValue();
            zzn += adh.zzct(4) + 1;
        }
        return this.zzbuQ != null ? zzn + adh.zzb(5, this.zzbuQ) : zzn;
    }
}
