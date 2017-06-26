package com.google.android.gms.internal;

import java.io.IOException;

public final class zzcjq extends ada<zzcjq> {
    public Integer zzbve;
    public String zzbvf;
    public Boolean zzbvg;
    public String[] zzbvh;

    public zzcjq() {
        this.zzbve = null;
        this.zzbvf = null;
        this.zzbvg = null;
        this.zzbvh = adj.EMPTY_STRING_ARRAY;
        this.zzcrZ = null;
        this.zzcsi = -1;
    }

    public final boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof zzcjq)) {
            return false;
        }
        zzcjq com_google_android_gms_internal_zzcjq = (zzcjq) obj;
        if (this.zzbve == null) {
            if (com_google_android_gms_internal_zzcjq.zzbve != null) {
                return false;
            }
        } else if (!this.zzbve.equals(com_google_android_gms_internal_zzcjq.zzbve)) {
            return false;
        }
        if (this.zzbvf == null) {
            if (com_google_android_gms_internal_zzcjq.zzbvf != null) {
                return false;
            }
        } else if (!this.zzbvf.equals(com_google_android_gms_internal_zzcjq.zzbvf)) {
            return false;
        }
        if (this.zzbvg == null) {
            if (com_google_android_gms_internal_zzcjq.zzbvg != null) {
                return false;
            }
        } else if (!this.zzbvg.equals(com_google_android_gms_internal_zzcjq.zzbvg)) {
            return false;
        }
        return !ade.equals(this.zzbvh, com_google_android_gms_internal_zzcjq.zzbvh) ? false : (this.zzcrZ == null || this.zzcrZ.isEmpty()) ? com_google_android_gms_internal_zzcjq.zzcrZ == null || com_google_android_gms_internal_zzcjq.zzcrZ.isEmpty() : this.zzcrZ.equals(com_google_android_gms_internal_zzcjq.zzcrZ);
    }

    public final int hashCode() {
        int i = 0;
        int hashCode = ((((this.zzbvg == null ? 0 : this.zzbvg.hashCode()) + (((this.zzbvf == null ? 0 : this.zzbvf.hashCode()) + (((this.zzbve == null ? 0 : this.zzbve.intValue()) + ((getClass().getName().hashCode() + 527) * 31)) * 31)) * 31)) * 31) + ade.hashCode(this.zzbvh)) * 31;
        if (!(this.zzcrZ == null || this.zzcrZ.isEmpty())) {
            i = this.zzcrZ.hashCode();
        }
        return hashCode + i;
    }

    public final /* synthetic */ adg zza(acx com_google_android_gms_internal_acx) throws IOException {
        while (true) {
            int zzLy = com_google_android_gms_internal_acx.zzLy();
            int position;
            switch (zzLy) {
                case 0:
                    break;
                case 8:
                    position = com_google_android_gms_internal_acx.getPosition();
                    int zzLD = com_google_android_gms_internal_acx.zzLD();
                    switch (zzLD) {
                        case 0:
                        case 1:
                        case 2:
                        case 3:
                        case 4:
                        case 5:
                        case 6:
                            this.zzbve = Integer.valueOf(zzLD);
                            break;
                        default:
                            com_google_android_gms_internal_acx.zzcp(position);
                            zza(com_google_android_gms_internal_acx, zzLy);
                            continue;
                    }
                case 18:
                    this.zzbvf = com_google_android_gms_internal_acx.readString();
                    continue;
                case 24:
                    this.zzbvg = Boolean.valueOf(com_google_android_gms_internal_acx.zzLB());
                    continue;
                case 34:
                    position = adj.zzb(com_google_android_gms_internal_acx, 34);
                    zzLy = this.zzbvh == null ? 0 : this.zzbvh.length;
                    Object obj = new String[(position + zzLy)];
                    if (zzLy != 0) {
                        System.arraycopy(this.zzbvh, 0, obj, 0, zzLy);
                    }
                    while (zzLy < obj.length - 1) {
                        obj[zzLy] = com_google_android_gms_internal_acx.readString();
                        com_google_android_gms_internal_acx.zzLy();
                        zzLy++;
                    }
                    obj[zzLy] = com_google_android_gms_internal_acx.readString();
                    this.zzbvh = obj;
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
        if (this.zzbve != null) {
            com_google_android_gms_internal_acy.zzr(1, this.zzbve.intValue());
        }
        if (this.zzbvf != null) {
            com_google_android_gms_internal_acy.zzl(2, this.zzbvf);
        }
        if (this.zzbvg != null) {
            com_google_android_gms_internal_acy.zzk(3, this.zzbvg.booleanValue());
        }
        if (this.zzbvh != null && this.zzbvh.length > 0) {
            for (String str : this.zzbvh) {
                if (str != null) {
                    com_google_android_gms_internal_acy.zzl(4, str);
                }
            }
        }
        super.zza(com_google_android_gms_internal_acy);
    }

    protected final int zzn() {
        int i = 0;
        int zzn = super.zzn();
        if (this.zzbve != null) {
            zzn += acy.zzs(1, this.zzbve.intValue());
        }
        if (this.zzbvf != null) {
            zzn += acy.zzm(2, this.zzbvf);
        }
        if (this.zzbvg != null) {
            this.zzbvg.booleanValue();
            zzn += acy.zzct(3) + 1;
        }
        if (this.zzbvh == null || this.zzbvh.length <= 0) {
            return zzn;
        }
        int i2 = 0;
        int i3 = 0;
        while (i < this.zzbvh.length) {
            String str = this.zzbvh[i];
            if (str != null) {
                i3++;
                i2 += acy.zzhQ(str);
            }
            i++;
        }
        return (zzn + i2) + (i3 * 1);
    }
}
