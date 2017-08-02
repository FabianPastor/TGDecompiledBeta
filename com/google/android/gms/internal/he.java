package com.google.android.gms.internal;

import java.io.IOException;

public final class he extends adj<he> {
    public hf[] zzbTH;

    public he() {
        this.zzbTH = hf.zzEa();
        this.zzcso = null;
        this.zzcsx = -1;
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
        return !adn.equals(this.zzbTH, heVar.zzbTH) ? false : (this.zzcso == null || this.zzcso.isEmpty()) ? heVar.zzcso == null || heVar.zzcso.isEmpty() : this.zzcso.equals(heVar.zzcso);
    }

    public final int hashCode() {
        int hashCode = (((getClass().getName().hashCode() + 527) * 31) + adn.hashCode(this.zzbTH)) * 31;
        int hashCode2 = (this.zzcso == null || this.zzcso.isEmpty()) ? 0 : this.zzcso.hashCode();
        return hashCode2 + hashCode;
    }

    public final /* synthetic */ adp zza(adg com_google_android_gms_internal_adg) throws IOException {
        while (true) {
            int zzLA = com_google_android_gms_internal_adg.zzLA();
            switch (zzLA) {
                case 0:
                    break;
                case 10:
                    int zzb = ads.zzb(com_google_android_gms_internal_adg, 10);
                    zzLA = this.zzbTH == null ? 0 : this.zzbTH.length;
                    Object obj = new hf[(zzb + zzLA)];
                    if (zzLA != 0) {
                        System.arraycopy(this.zzbTH, 0, obj, 0, zzLA);
                    }
                    while (zzLA < obj.length - 1) {
                        obj[zzLA] = new hf();
                        com_google_android_gms_internal_adg.zza(obj[zzLA]);
                        com_google_android_gms_internal_adg.zzLA();
                        zzLA++;
                    }
                    obj[zzLA] = new hf();
                    com_google_android_gms_internal_adg.zza(obj[zzLA]);
                    this.zzbTH = obj;
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
