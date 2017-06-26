package com.google.android.gms.internal;

import java.io.IOException;

public final class zzcjs extends ada<zzcjs> {
    public String zzboQ;
    public Long zzbvl;
    private Integer zzbvm;
    public zzcjt[] zzbvn;
    public zzcjr[] zzbvo;
    public zzcjl[] zzbvp;

    public zzcjs() {
        this.zzbvl = null;
        this.zzboQ = null;
        this.zzbvm = null;
        this.zzbvn = zzcjt.zzzz();
        this.zzbvo = zzcjr.zzzy();
        this.zzbvp = zzcjl.zzzu();
        this.zzcrZ = null;
        this.zzcsi = -1;
    }

    public final boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof zzcjs)) {
            return false;
        }
        zzcjs com_google_android_gms_internal_zzcjs = (zzcjs) obj;
        if (this.zzbvl == null) {
            if (com_google_android_gms_internal_zzcjs.zzbvl != null) {
                return false;
            }
        } else if (!this.zzbvl.equals(com_google_android_gms_internal_zzcjs.zzbvl)) {
            return false;
        }
        if (this.zzboQ == null) {
            if (com_google_android_gms_internal_zzcjs.zzboQ != null) {
                return false;
            }
        } else if (!this.zzboQ.equals(com_google_android_gms_internal_zzcjs.zzboQ)) {
            return false;
        }
        if (this.zzbvm == null) {
            if (com_google_android_gms_internal_zzcjs.zzbvm != null) {
                return false;
            }
        } else if (!this.zzbvm.equals(com_google_android_gms_internal_zzcjs.zzbvm)) {
            return false;
        }
        return !ade.equals(this.zzbvn, com_google_android_gms_internal_zzcjs.zzbvn) ? false : !ade.equals(this.zzbvo, com_google_android_gms_internal_zzcjs.zzbvo) ? false : !ade.equals(this.zzbvp, com_google_android_gms_internal_zzcjs.zzbvp) ? false : (this.zzcrZ == null || this.zzcrZ.isEmpty()) ? com_google_android_gms_internal_zzcjs.zzcrZ == null || com_google_android_gms_internal_zzcjs.zzcrZ.isEmpty() : this.zzcrZ.equals(com_google_android_gms_internal_zzcjs.zzcrZ);
    }

    public final int hashCode() {
        int i = 0;
        int hashCode = ((((((((this.zzbvm == null ? 0 : this.zzbvm.hashCode()) + (((this.zzboQ == null ? 0 : this.zzboQ.hashCode()) + (((this.zzbvl == null ? 0 : this.zzbvl.hashCode()) + ((getClass().getName().hashCode() + 527) * 31)) * 31)) * 31)) * 31) + ade.hashCode(this.zzbvn)) * 31) + ade.hashCode(this.zzbvo)) * 31) + ade.hashCode(this.zzbvp)) * 31;
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
                    this.zzbvl = Long.valueOf(com_google_android_gms_internal_acx.zzLE());
                    continue;
                case 18:
                    this.zzboQ = com_google_android_gms_internal_acx.readString();
                    continue;
                case 24:
                    this.zzbvm = Integer.valueOf(com_google_android_gms_internal_acx.zzLD());
                    continue;
                case 34:
                    zzb = adj.zzb(com_google_android_gms_internal_acx, 34);
                    zzLy = this.zzbvn == null ? 0 : this.zzbvn.length;
                    obj = new zzcjt[(zzb + zzLy)];
                    if (zzLy != 0) {
                        System.arraycopy(this.zzbvn, 0, obj, 0, zzLy);
                    }
                    while (zzLy < obj.length - 1) {
                        obj[zzLy] = new zzcjt();
                        com_google_android_gms_internal_acx.zza(obj[zzLy]);
                        com_google_android_gms_internal_acx.zzLy();
                        zzLy++;
                    }
                    obj[zzLy] = new zzcjt();
                    com_google_android_gms_internal_acx.zza(obj[zzLy]);
                    this.zzbvn = obj;
                    continue;
                case 42:
                    zzb = adj.zzb(com_google_android_gms_internal_acx, 42);
                    zzLy = this.zzbvo == null ? 0 : this.zzbvo.length;
                    obj = new zzcjr[(zzb + zzLy)];
                    if (zzLy != 0) {
                        System.arraycopy(this.zzbvo, 0, obj, 0, zzLy);
                    }
                    while (zzLy < obj.length - 1) {
                        obj[zzLy] = new zzcjr();
                        com_google_android_gms_internal_acx.zza(obj[zzLy]);
                        com_google_android_gms_internal_acx.zzLy();
                        zzLy++;
                    }
                    obj[zzLy] = new zzcjr();
                    com_google_android_gms_internal_acx.zza(obj[zzLy]);
                    this.zzbvo = obj;
                    continue;
                case 50:
                    zzb = adj.zzb(com_google_android_gms_internal_acx, 50);
                    zzLy = this.zzbvp == null ? 0 : this.zzbvp.length;
                    obj = new zzcjl[(zzb + zzLy)];
                    if (zzLy != 0) {
                        System.arraycopy(this.zzbvp, 0, obj, 0, zzLy);
                    }
                    while (zzLy < obj.length - 1) {
                        obj[zzLy] = new zzcjl();
                        com_google_android_gms_internal_acx.zza(obj[zzLy]);
                        com_google_android_gms_internal_acx.zzLy();
                        zzLy++;
                    }
                    obj[zzLy] = new zzcjl();
                    com_google_android_gms_internal_acx.zza(obj[zzLy]);
                    this.zzbvp = obj;
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
        if (this.zzbvl != null) {
            com_google_android_gms_internal_acy.zzb(1, this.zzbvl.longValue());
        }
        if (this.zzboQ != null) {
            com_google_android_gms_internal_acy.zzl(2, this.zzboQ);
        }
        if (this.zzbvm != null) {
            com_google_android_gms_internal_acy.zzr(3, this.zzbvm.intValue());
        }
        if (this.zzbvn != null && this.zzbvn.length > 0) {
            for (adg com_google_android_gms_internal_adg : this.zzbvn) {
                if (com_google_android_gms_internal_adg != null) {
                    com_google_android_gms_internal_acy.zza(4, com_google_android_gms_internal_adg);
                }
            }
        }
        if (this.zzbvo != null && this.zzbvo.length > 0) {
            for (adg com_google_android_gms_internal_adg2 : this.zzbvo) {
                if (com_google_android_gms_internal_adg2 != null) {
                    com_google_android_gms_internal_acy.zza(5, com_google_android_gms_internal_adg2);
                }
            }
        }
        if (this.zzbvp != null && this.zzbvp.length > 0) {
            while (i < this.zzbvp.length) {
                adg com_google_android_gms_internal_adg3 = this.zzbvp[i];
                if (com_google_android_gms_internal_adg3 != null) {
                    com_google_android_gms_internal_acy.zza(6, com_google_android_gms_internal_adg3);
                }
                i++;
            }
        }
        super.zza(com_google_android_gms_internal_acy);
    }

    protected final int zzn() {
        int i;
        int i2 = 0;
        int zzn = super.zzn();
        if (this.zzbvl != null) {
            zzn += acy.zze(1, this.zzbvl.longValue());
        }
        if (this.zzboQ != null) {
            zzn += acy.zzm(2, this.zzboQ);
        }
        if (this.zzbvm != null) {
            zzn += acy.zzs(3, this.zzbvm.intValue());
        }
        if (this.zzbvn != null && this.zzbvn.length > 0) {
            i = zzn;
            for (adg com_google_android_gms_internal_adg : this.zzbvn) {
                if (com_google_android_gms_internal_adg != null) {
                    i += acy.zzb(4, com_google_android_gms_internal_adg);
                }
            }
            zzn = i;
        }
        if (this.zzbvo != null && this.zzbvo.length > 0) {
            i = zzn;
            for (adg com_google_android_gms_internal_adg2 : this.zzbvo) {
                if (com_google_android_gms_internal_adg2 != null) {
                    i += acy.zzb(5, com_google_android_gms_internal_adg2);
                }
            }
            zzn = i;
        }
        if (this.zzbvp != null && this.zzbvp.length > 0) {
            while (i2 < this.zzbvp.length) {
                adg com_google_android_gms_internal_adg3 = this.zzbvp[i2];
                if (com_google_android_gms_internal_adg3 != null) {
                    zzn += acy.zzb(6, com_google_android_gms_internal_adg3);
                }
                i2++;
            }
        }
        return zzn;
    }
}
