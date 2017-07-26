package com.google.android.gms.internal;

import java.io.IOException;

public final class zzcjt extends adj<zzcjt> {
    public String zzboQ;
    public Long zzbvl;
    private Integer zzbvm;
    public zzcju[] zzbvn;
    public zzcjs[] zzbvo;
    public zzcjm[] zzbvp;

    public zzcjt() {
        this.zzbvl = null;
        this.zzboQ = null;
        this.zzbvm = null;
        this.zzbvn = zzcju.zzzz();
        this.zzbvo = zzcjs.zzzy();
        this.zzbvp = zzcjm.zzzu();
        this.zzcsd = null;
        this.zzcsm = -1;
    }

    public final boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof zzcjt)) {
            return false;
        }
        zzcjt com_google_android_gms_internal_zzcjt = (zzcjt) obj;
        if (this.zzbvl == null) {
            if (com_google_android_gms_internal_zzcjt.zzbvl != null) {
                return false;
            }
        } else if (!this.zzbvl.equals(com_google_android_gms_internal_zzcjt.zzbvl)) {
            return false;
        }
        if (this.zzboQ == null) {
            if (com_google_android_gms_internal_zzcjt.zzboQ != null) {
                return false;
            }
        } else if (!this.zzboQ.equals(com_google_android_gms_internal_zzcjt.zzboQ)) {
            return false;
        }
        if (this.zzbvm == null) {
            if (com_google_android_gms_internal_zzcjt.zzbvm != null) {
                return false;
            }
        } else if (!this.zzbvm.equals(com_google_android_gms_internal_zzcjt.zzbvm)) {
            return false;
        }
        return !adn.equals(this.zzbvn, com_google_android_gms_internal_zzcjt.zzbvn) ? false : !adn.equals(this.zzbvo, com_google_android_gms_internal_zzcjt.zzbvo) ? false : !adn.equals(this.zzbvp, com_google_android_gms_internal_zzcjt.zzbvp) ? false : (this.zzcsd == null || this.zzcsd.isEmpty()) ? com_google_android_gms_internal_zzcjt.zzcsd == null || com_google_android_gms_internal_zzcjt.zzcsd.isEmpty() : this.zzcsd.equals(com_google_android_gms_internal_zzcjt.zzcsd);
    }

    public final int hashCode() {
        int i = 0;
        int hashCode = ((((((((this.zzbvm == null ? 0 : this.zzbvm.hashCode()) + (((this.zzboQ == null ? 0 : this.zzboQ.hashCode()) + (((this.zzbvl == null ? 0 : this.zzbvl.hashCode()) + ((getClass().getName().hashCode() + 527) * 31)) * 31)) * 31)) * 31) + adn.hashCode(this.zzbvn)) * 31) + adn.hashCode(this.zzbvo)) * 31) + adn.hashCode(this.zzbvp)) * 31;
        if (!(this.zzcsd == null || this.zzcsd.isEmpty())) {
            i = this.zzcsd.hashCode();
        }
        return hashCode + i;
    }

    public final /* synthetic */ adp zza(adg com_google_android_gms_internal_adg) throws IOException {
        while (true) {
            int zzLB = com_google_android_gms_internal_adg.zzLB();
            int zzb;
            Object obj;
            switch (zzLB) {
                case 0:
                    break;
                case 8:
                    this.zzbvl = Long.valueOf(com_google_android_gms_internal_adg.zzLH());
                    continue;
                case 18:
                    this.zzboQ = com_google_android_gms_internal_adg.readString();
                    continue;
                case 24:
                    this.zzbvm = Integer.valueOf(com_google_android_gms_internal_adg.zzLG());
                    continue;
                case 34:
                    zzb = ads.zzb(com_google_android_gms_internal_adg, 34);
                    zzLB = this.zzbvn == null ? 0 : this.zzbvn.length;
                    obj = new zzcju[(zzb + zzLB)];
                    if (zzLB != 0) {
                        System.arraycopy(this.zzbvn, 0, obj, 0, zzLB);
                    }
                    while (zzLB < obj.length - 1) {
                        obj[zzLB] = new zzcju();
                        com_google_android_gms_internal_adg.zza(obj[zzLB]);
                        com_google_android_gms_internal_adg.zzLB();
                        zzLB++;
                    }
                    obj[zzLB] = new zzcju();
                    com_google_android_gms_internal_adg.zza(obj[zzLB]);
                    this.zzbvn = obj;
                    continue;
                case 42:
                    zzb = ads.zzb(com_google_android_gms_internal_adg, 42);
                    zzLB = this.zzbvo == null ? 0 : this.zzbvo.length;
                    obj = new zzcjs[(zzb + zzLB)];
                    if (zzLB != 0) {
                        System.arraycopy(this.zzbvo, 0, obj, 0, zzLB);
                    }
                    while (zzLB < obj.length - 1) {
                        obj[zzLB] = new zzcjs();
                        com_google_android_gms_internal_adg.zza(obj[zzLB]);
                        com_google_android_gms_internal_adg.zzLB();
                        zzLB++;
                    }
                    obj[zzLB] = new zzcjs();
                    com_google_android_gms_internal_adg.zza(obj[zzLB]);
                    this.zzbvo = obj;
                    continue;
                case 50:
                    zzb = ads.zzb(com_google_android_gms_internal_adg, 50);
                    zzLB = this.zzbvp == null ? 0 : this.zzbvp.length;
                    obj = new zzcjm[(zzb + zzLB)];
                    if (zzLB != 0) {
                        System.arraycopy(this.zzbvp, 0, obj, 0, zzLB);
                    }
                    while (zzLB < obj.length - 1) {
                        obj[zzLB] = new zzcjm();
                        com_google_android_gms_internal_adg.zza(obj[zzLB]);
                        com_google_android_gms_internal_adg.zzLB();
                        zzLB++;
                    }
                    obj[zzLB] = new zzcjm();
                    com_google_android_gms_internal_adg.zza(obj[zzLB]);
                    this.zzbvp = obj;
                    continue;
                default:
                    if (!super.zza(com_google_android_gms_internal_adg, zzLB)) {
                        break;
                    }
                    continue;
            }
            return this;
        }
    }

    public final void zza(adh com_google_android_gms_internal_adh) throws IOException {
        int i = 0;
        if (this.zzbvl != null) {
            com_google_android_gms_internal_adh.zzb(1, this.zzbvl.longValue());
        }
        if (this.zzboQ != null) {
            com_google_android_gms_internal_adh.zzl(2, this.zzboQ);
        }
        if (this.zzbvm != null) {
            com_google_android_gms_internal_adh.zzr(3, this.zzbvm.intValue());
        }
        if (this.zzbvn != null && this.zzbvn.length > 0) {
            for (adp com_google_android_gms_internal_adp : this.zzbvn) {
                if (com_google_android_gms_internal_adp != null) {
                    com_google_android_gms_internal_adh.zza(4, com_google_android_gms_internal_adp);
                }
            }
        }
        if (this.zzbvo != null && this.zzbvo.length > 0) {
            for (adp com_google_android_gms_internal_adp2 : this.zzbvo) {
                if (com_google_android_gms_internal_adp2 != null) {
                    com_google_android_gms_internal_adh.zza(5, com_google_android_gms_internal_adp2);
                }
            }
        }
        if (this.zzbvp != null && this.zzbvp.length > 0) {
            while (i < this.zzbvp.length) {
                adp com_google_android_gms_internal_adp3 = this.zzbvp[i];
                if (com_google_android_gms_internal_adp3 != null) {
                    com_google_android_gms_internal_adh.zza(6, com_google_android_gms_internal_adp3);
                }
                i++;
            }
        }
        super.zza(com_google_android_gms_internal_adh);
    }

    protected final int zzn() {
        int i;
        int i2 = 0;
        int zzn = super.zzn();
        if (this.zzbvl != null) {
            zzn += adh.zze(1, this.zzbvl.longValue());
        }
        if (this.zzboQ != null) {
            zzn += adh.zzm(2, this.zzboQ);
        }
        if (this.zzbvm != null) {
            zzn += adh.zzs(3, this.zzbvm.intValue());
        }
        if (this.zzbvn != null && this.zzbvn.length > 0) {
            i = zzn;
            for (adp com_google_android_gms_internal_adp : this.zzbvn) {
                if (com_google_android_gms_internal_adp != null) {
                    i += adh.zzb(4, com_google_android_gms_internal_adp);
                }
            }
            zzn = i;
        }
        if (this.zzbvo != null && this.zzbvo.length > 0) {
            i = zzn;
            for (adp com_google_android_gms_internal_adp2 : this.zzbvo) {
                if (com_google_android_gms_internal_adp2 != null) {
                    i += adh.zzb(5, com_google_android_gms_internal_adp2);
                }
            }
            zzn = i;
        }
        if (this.zzbvp != null && this.zzbvp.length > 0) {
            while (i2 < this.zzbvp.length) {
                adp com_google_android_gms_internal_adp3 = this.zzbvp[i2];
                if (com_google_android_gms_internal_adp3 != null) {
                    zzn += adh.zzb(6, com_google_android_gms_internal_adp3);
                }
                i2++;
            }
        }
        return zzn;
    }
}
