package com.google.android.gms.internal;

import java.io.IOException;

public final class he extends ada<he> {
    private static volatile he[] zzbTG;
    public String name;
    public hf zzbTH;

    public he() {
        this.name = "";
        this.zzbTH = null;
        this.zzcrZ = null;
        this.zzcsi = -1;
    }

    public static he[] zzDZ() {
        if (zzbTG == null) {
            synchronized (ade.zzcsh) {
                if (zzbTG == null) {
                    zzbTG = new he[0];
                }
            }
        }
        return zzbTG;
    }

    public final boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof he)) {
            return false;
        }
        he heVar = (he) obj;
        if (this.name == null) {
            if (heVar.name != null) {
                return false;
            }
        } else if (!this.name.equals(heVar.name)) {
            return false;
        }
        if (this.zzbTH == null) {
            if (heVar.zzbTH != null) {
                return false;
            }
        } else if (!this.zzbTH.equals(heVar.zzbTH)) {
            return false;
        }
        return (this.zzcrZ == null || this.zzcrZ.isEmpty()) ? heVar.zzcrZ == null || heVar.zzcrZ.isEmpty() : this.zzcrZ.equals(heVar.zzcrZ);
    }

    public final int hashCode() {
        int i = 0;
        int hashCode = ((this.zzbTH == null ? 0 : this.zzbTH.hashCode()) + (((this.name == null ? 0 : this.name.hashCode()) + ((getClass().getName().hashCode() + 527) * 31)) * 31)) * 31;
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
                case 10:
                    this.name = com_google_android_gms_internal_acx.readString();
                    continue;
                case 18:
                    if (this.zzbTH == null) {
                        this.zzbTH = new hf();
                    }
                    com_google_android_gms_internal_acx.zza(this.zzbTH);
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
        com_google_android_gms_internal_acy.zzl(1, this.name);
        if (this.zzbTH != null) {
            com_google_android_gms_internal_acy.zza(2, this.zzbTH);
        }
        super.zza(com_google_android_gms_internal_acy);
    }

    protected final int zzn() {
        int zzn = super.zzn() + acy.zzm(1, this.name);
        return this.zzbTH != null ? zzn + acy.zzb(2, this.zzbTH) : zzn;
    }
}
