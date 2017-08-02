package com.google.android.gms.internal;

import java.io.IOException;

public final class zzcjr extends adj<zzcjr> {
    public Integer zzbve;
    public String zzbvf;
    public Boolean zzbvg;
    public String[] zzbvh;

    public zzcjr() {
        this.zzbve = null;
        this.zzbvf = null;
        this.zzbvg = null;
        this.zzbvh = ads.EMPTY_STRING_ARRAY;
        this.zzcso = null;
        this.zzcsx = -1;
    }

    public final boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof zzcjr)) {
            return false;
        }
        zzcjr com_google_android_gms_internal_zzcjr = (zzcjr) obj;
        if (this.zzbve == null) {
            if (com_google_android_gms_internal_zzcjr.zzbve != null) {
                return false;
            }
        } else if (!this.zzbve.equals(com_google_android_gms_internal_zzcjr.zzbve)) {
            return false;
        }
        if (this.zzbvf == null) {
            if (com_google_android_gms_internal_zzcjr.zzbvf != null) {
                return false;
            }
        } else if (!this.zzbvf.equals(com_google_android_gms_internal_zzcjr.zzbvf)) {
            return false;
        }
        if (this.zzbvg == null) {
            if (com_google_android_gms_internal_zzcjr.zzbvg != null) {
                return false;
            }
        } else if (!this.zzbvg.equals(com_google_android_gms_internal_zzcjr.zzbvg)) {
            return false;
        }
        return !adn.equals(this.zzbvh, com_google_android_gms_internal_zzcjr.zzbvh) ? false : (this.zzcso == null || this.zzcso.isEmpty()) ? com_google_android_gms_internal_zzcjr.zzcso == null || com_google_android_gms_internal_zzcjr.zzcso.isEmpty() : this.zzcso.equals(com_google_android_gms_internal_zzcjr.zzcso);
    }

    public final int hashCode() {
        int i = 0;
        int hashCode = ((((this.zzbvg == null ? 0 : this.zzbvg.hashCode()) + (((this.zzbvf == null ? 0 : this.zzbvf.hashCode()) + (((this.zzbve == null ? 0 : this.zzbve.intValue()) + ((getClass().getName().hashCode() + 527) * 31)) * 31)) * 31)) * 31) + adn.hashCode(this.zzbvh)) * 31;
        if (!(this.zzcso == null || this.zzcso.isEmpty())) {
            i = this.zzcso.hashCode();
        }
        return hashCode + i;
    }

    public final /* synthetic */ adp zza(adg com_google_android_gms_internal_adg) throws IOException {
        while (true) {
            int zzLA = com_google_android_gms_internal_adg.zzLA();
            int position;
            switch (zzLA) {
                case 0:
                    break;
                case 8:
                    position = com_google_android_gms_internal_adg.getPosition();
                    int zzLF = com_google_android_gms_internal_adg.zzLF();
                    switch (zzLF) {
                        case 0:
                        case 1:
                        case 2:
                        case 3:
                        case 4:
                        case 5:
                        case 6:
                            this.zzbve = Integer.valueOf(zzLF);
                            break;
                        default:
                            com_google_android_gms_internal_adg.zzcp(position);
                            zza(com_google_android_gms_internal_adg, zzLA);
                            continue;
                    }
                case 18:
                    this.zzbvf = com_google_android_gms_internal_adg.readString();
                    continue;
                case 24:
                    this.zzbvg = Boolean.valueOf(com_google_android_gms_internal_adg.zzLD());
                    continue;
                case 34:
                    position = ads.zzb(com_google_android_gms_internal_adg, 34);
                    zzLA = this.zzbvh == null ? 0 : this.zzbvh.length;
                    Object obj = new String[(position + zzLA)];
                    if (zzLA != 0) {
                        System.arraycopy(this.zzbvh, 0, obj, 0, zzLA);
                    }
                    while (zzLA < obj.length - 1) {
                        obj[zzLA] = com_google_android_gms_internal_adg.readString();
                        com_google_android_gms_internal_adg.zzLA();
                        zzLA++;
                    }
                    obj[zzLA] = com_google_android_gms_internal_adg.readString();
                    this.zzbvh = obj;
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
        if (this.zzbve != null) {
            com_google_android_gms_internal_adh.zzr(1, this.zzbve.intValue());
        }
        if (this.zzbvf != null) {
            com_google_android_gms_internal_adh.zzl(2, this.zzbvf);
        }
        if (this.zzbvg != null) {
            com_google_android_gms_internal_adh.zzk(3, this.zzbvg.booleanValue());
        }
        if (this.zzbvh != null && this.zzbvh.length > 0) {
            for (String str : this.zzbvh) {
                if (str != null) {
                    com_google_android_gms_internal_adh.zzl(4, str);
                }
            }
        }
        super.zza(com_google_android_gms_internal_adh);
    }

    protected final int zzn() {
        int i = 0;
        int zzn = super.zzn();
        if (this.zzbve != null) {
            zzn += adh.zzs(1, this.zzbve.intValue());
        }
        if (this.zzbvf != null) {
            zzn += adh.zzm(2, this.zzbvf);
        }
        if (this.zzbvg != null) {
            this.zzbvg.booleanValue();
            zzn += adh.zzct(3) + 1;
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
                i2 += adh.zzhQ(str);
            }
            i++;
        }
        return (zzn + i2) + (i3 * 1);
    }
}
