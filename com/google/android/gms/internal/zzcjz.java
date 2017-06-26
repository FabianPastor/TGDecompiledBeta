package com.google.android.gms.internal;

import java.io.IOException;

public final class zzcjz extends ada<zzcjz> {
    public long[] zzbwe;
    public long[] zzbwf;

    public zzcjz() {
        this.zzbwe = adj.zzcso;
        this.zzbwf = adj.zzcso;
        this.zzcrZ = null;
        this.zzcsi = -1;
    }

    public final boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof zzcjz)) {
            return false;
        }
        zzcjz com_google_android_gms_internal_zzcjz = (zzcjz) obj;
        return !ade.equals(this.zzbwe, com_google_android_gms_internal_zzcjz.zzbwe) ? false : !ade.equals(this.zzbwf, com_google_android_gms_internal_zzcjz.zzbwf) ? false : (this.zzcrZ == null || this.zzcrZ.isEmpty()) ? com_google_android_gms_internal_zzcjz.zzcrZ == null || com_google_android_gms_internal_zzcjz.zzcrZ.isEmpty() : this.zzcrZ.equals(com_google_android_gms_internal_zzcjz.zzcrZ);
    }

    public final int hashCode() {
        int hashCode = (((((getClass().getName().hashCode() + 527) * 31) + ade.hashCode(this.zzbwe)) * 31) + ade.hashCode(this.zzbwf)) * 31;
        int hashCode2 = (this.zzcrZ == null || this.zzcrZ.isEmpty()) ? 0 : this.zzcrZ.hashCode();
        return hashCode2 + hashCode;
    }

    public final /* synthetic */ adg zza(acx com_google_android_gms_internal_acx) throws IOException {
        while (true) {
            int zzLy = com_google_android_gms_internal_acx.zzLy();
            int zzb;
            Object obj;
            int zzcn;
            Object obj2;
            switch (zzLy) {
                case 0:
                    break;
                case 8:
                    zzb = adj.zzb(com_google_android_gms_internal_acx, 8);
                    zzLy = this.zzbwe == null ? 0 : this.zzbwe.length;
                    obj = new long[(zzb + zzLy)];
                    if (zzLy != 0) {
                        System.arraycopy(this.zzbwe, 0, obj, 0, zzLy);
                    }
                    while (zzLy < obj.length - 1) {
                        obj[zzLy] = com_google_android_gms_internal_acx.zzLE();
                        com_google_android_gms_internal_acx.zzLy();
                        zzLy++;
                    }
                    obj[zzLy] = com_google_android_gms_internal_acx.zzLE();
                    this.zzbwe = obj;
                    continue;
                case 10:
                    zzcn = com_google_android_gms_internal_acx.zzcn(com_google_android_gms_internal_acx.zzLD());
                    zzb = com_google_android_gms_internal_acx.getPosition();
                    zzLy = 0;
                    while (com_google_android_gms_internal_acx.zzLI() > 0) {
                        com_google_android_gms_internal_acx.zzLE();
                        zzLy++;
                    }
                    com_google_android_gms_internal_acx.zzcp(zzb);
                    zzb = this.zzbwe == null ? 0 : this.zzbwe.length;
                    obj2 = new long[(zzLy + zzb)];
                    if (zzb != 0) {
                        System.arraycopy(this.zzbwe, 0, obj2, 0, zzb);
                    }
                    while (zzb < obj2.length) {
                        obj2[zzb] = com_google_android_gms_internal_acx.zzLE();
                        zzb++;
                    }
                    this.zzbwe = obj2;
                    com_google_android_gms_internal_acx.zzco(zzcn);
                    continue;
                case 16:
                    zzb = adj.zzb(com_google_android_gms_internal_acx, 16);
                    zzLy = this.zzbwf == null ? 0 : this.zzbwf.length;
                    obj = new long[(zzb + zzLy)];
                    if (zzLy != 0) {
                        System.arraycopy(this.zzbwf, 0, obj, 0, zzLy);
                    }
                    while (zzLy < obj.length - 1) {
                        obj[zzLy] = com_google_android_gms_internal_acx.zzLE();
                        com_google_android_gms_internal_acx.zzLy();
                        zzLy++;
                    }
                    obj[zzLy] = com_google_android_gms_internal_acx.zzLE();
                    this.zzbwf = obj;
                    continue;
                case 18:
                    zzcn = com_google_android_gms_internal_acx.zzcn(com_google_android_gms_internal_acx.zzLD());
                    zzb = com_google_android_gms_internal_acx.getPosition();
                    zzLy = 0;
                    while (com_google_android_gms_internal_acx.zzLI() > 0) {
                        com_google_android_gms_internal_acx.zzLE();
                        zzLy++;
                    }
                    com_google_android_gms_internal_acx.zzcp(zzb);
                    zzb = this.zzbwf == null ? 0 : this.zzbwf.length;
                    obj2 = new long[(zzLy + zzb)];
                    if (zzb != 0) {
                        System.arraycopy(this.zzbwf, 0, obj2, 0, zzb);
                    }
                    while (zzb < obj2.length) {
                        obj2[zzb] = com_google_android_gms_internal_acx.zzLE();
                        zzb++;
                    }
                    this.zzbwf = obj2;
                    com_google_android_gms_internal_acx.zzco(zzcn);
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
        if (this.zzbwe != null && this.zzbwe.length > 0) {
            for (long zza : this.zzbwe) {
                com_google_android_gms_internal_acy.zza(1, zza);
            }
        }
        if (this.zzbwf != null && this.zzbwf.length > 0) {
            while (i < this.zzbwf.length) {
                com_google_android_gms_internal_acy.zza(2, this.zzbwf[i]);
                i++;
            }
        }
        super.zza(com_google_android_gms_internal_acy);
    }

    protected final int zzn() {
        int i;
        int i2;
        int i3 = 0;
        int zzn = super.zzn();
        if (this.zzbwe == null || this.zzbwe.length <= 0) {
            i = zzn;
        } else {
            i2 = 0;
            for (long zzaP : this.zzbwe) {
                i2 += acy.zzaP(zzaP);
            }
            i = (zzn + i2) + (this.zzbwe.length * 1);
        }
        if (this.zzbwf == null || this.zzbwf.length <= 0) {
            return i;
        }
        i2 = 0;
        while (i3 < this.zzbwf.length) {
            i2 += acy.zzaP(this.zzbwf[i3]);
            i3++;
        }
        return (i + i2) + (this.zzbwf.length * 1);
    }
}
