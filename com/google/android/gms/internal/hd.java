package com.google.android.gms.internal;

import java.io.IOException;

public final class hd extends ada<hd> {
    public he[] zzbTF;

    public hd() {
        this.zzbTF = he.zzDZ();
        this.zzcrZ = null;
        this.zzcsi = -1;
    }

    public static hd zzy(byte[] bArr) throws adf {
        return (hd) adg.zza(new hd(), bArr);
    }

    public final boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof hd)) {
            return false;
        }
        hd hdVar = (hd) obj;
        return !ade.equals(this.zzbTF, hdVar.zzbTF) ? false : (this.zzcrZ == null || this.zzcrZ.isEmpty()) ? hdVar.zzcrZ == null || hdVar.zzcrZ.isEmpty() : this.zzcrZ.equals(hdVar.zzcrZ);
    }

    public final int hashCode() {
        int hashCode = (((getClass().getName().hashCode() + 527) * 31) + ade.hashCode(this.zzbTF)) * 31;
        int hashCode2 = (this.zzcrZ == null || this.zzcrZ.isEmpty()) ? 0 : this.zzcrZ.hashCode();
        return hashCode2 + hashCode;
    }

    public final /* synthetic */ adg zza(acx com_google_android_gms_internal_acx) throws IOException {
        while (true) {
            int zzLy = com_google_android_gms_internal_acx.zzLy();
            switch (zzLy) {
                case 0:
                    break;
                case 10:
                    int zzb = adj.zzb(com_google_android_gms_internal_acx, 10);
                    zzLy = this.zzbTF == null ? 0 : this.zzbTF.length;
                    Object obj = new he[(zzb + zzLy)];
                    if (zzLy != 0) {
                        System.arraycopy(this.zzbTF, 0, obj, 0, zzLy);
                    }
                    while (zzLy < obj.length - 1) {
                        obj[zzLy] = new he();
                        com_google_android_gms_internal_acx.zza(obj[zzLy]);
                        com_google_android_gms_internal_acx.zzLy();
                        zzLy++;
                    }
                    obj[zzLy] = new he();
                    com_google_android_gms_internal_acx.zza(obj[zzLy]);
                    this.zzbTF = obj;
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
        if (this.zzbTF != null && this.zzbTF.length > 0) {
            for (adg com_google_android_gms_internal_adg : this.zzbTF) {
                if (com_google_android_gms_internal_adg != null) {
                    com_google_android_gms_internal_acy.zza(1, com_google_android_gms_internal_adg);
                }
            }
        }
        super.zza(com_google_android_gms_internal_acy);
    }

    protected final int zzn() {
        int zzn = super.zzn();
        if (this.zzbTF != null && this.zzbTF.length > 0) {
            for (adg com_google_android_gms_internal_adg : this.zzbTF) {
                if (com_google_android_gms_internal_adg != null) {
                    zzn += acy.zzb(1, com_google_android_gms_internal_adg);
                }
            }
        }
        return zzn;
    }
}
