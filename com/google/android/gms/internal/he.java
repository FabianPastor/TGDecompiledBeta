package com.google.android.gms.internal;

import java.io.IOException;

public final class he extends adj<he> {
    public hf[] zzbTH;

    public he() {
        this.zzbTH = hf.zzEa();
        this.zzcsd = null;
        this.zzcsm = -1;
    }

    public static he zzy(byte[] bArr) throws ado {
        return (he) adp.zza(new he(), bArr);
    }

    public final boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof he)) {
            return false;
        }
        he heVar = (he) obj;
        return !adn.equals(this.zzbTH, heVar.zzbTH) ? false : (this.zzcsd == null || this.zzcsd.isEmpty()) ? heVar.zzcsd == null || heVar.zzcsd.isEmpty() : this.zzcsd.equals(heVar.zzcsd);
    }

    public final int hashCode() {
        int hashCode = (((getClass().getName().hashCode() + 527) * 31) + adn.hashCode(this.zzbTH)) * 31;
        int hashCode2 = (this.zzcsd == null || this.zzcsd.isEmpty()) ? 0 : this.zzcsd.hashCode();
        return hashCode2 + hashCode;
    }

    public final /* synthetic */ adp zza(adg com_google_android_gms_internal_adg) throws IOException {
        while (true) {
            int zzLB = com_google_android_gms_internal_adg.zzLB();
            switch (zzLB) {
                case 0:
                    break;
                case 10:
                    int zzb = ads.zzb(com_google_android_gms_internal_adg, 10);
                    zzLB = this.zzbTH == null ? 0 : this.zzbTH.length;
                    Object obj = new hf[(zzb + zzLB)];
                    if (zzLB != 0) {
                        System.arraycopy(this.zzbTH, 0, obj, 0, zzLB);
                    }
                    while (zzLB < obj.length - 1) {
                        obj[zzLB] = new hf();
                        com_google_android_gms_internal_adg.zza(obj[zzLB]);
                        com_google_android_gms_internal_adg.zzLB();
                        zzLB++;
                    }
                    obj[zzLB] = new hf();
                    com_google_android_gms_internal_adg.zza(obj[zzLB]);
                    this.zzbTH = obj;
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
        if (this.zzbTH != null && this.zzbTH.length > 0) {
            for (adp com_google_android_gms_internal_adp : this.zzbTH) {
                if (com_google_android_gms_internal_adp != null) {
                    com_google_android_gms_internal_adh.zza(1, com_google_android_gms_internal_adp);
                }
            }
        }
        super.zza(com_google_android_gms_internal_adh);
    }

    protected final int zzn() {
        int zzn = super.zzn();
        if (this.zzbTH != null && this.zzbTH.length > 0) {
            for (adp com_google_android_gms_internal_adp : this.zzbTH) {
                if (com_google_android_gms_internal_adp != null) {
                    zzn += adh.zzb(1, com_google_android_gms_internal_adp);
                }
            }
        }
        return zzn;
    }
}
