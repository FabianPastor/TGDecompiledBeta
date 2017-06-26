package com.google.android.gms.internal;

import java.io.IOException;
import java.util.Arrays;

public final class adz extends ada<adz> implements Cloneable {
    private boolean zzctA;
    private byte[] zzctx;
    private String zzcty;
    private byte[][] zzctz;

    public adz() {
        this.zzctx = adj.zzcst;
        this.zzcty = "";
        this.zzctz = adj.zzcss;
        this.zzctA = false;
        this.zzcrZ = null;
        this.zzcsi = -1;
    }

    private adz zzMa() {
        try {
            adz com_google_android_gms_internal_adz = (adz) super.zzLL();
            if (this.zzctz != null && this.zzctz.length > 0) {
                com_google_android_gms_internal_adz.zzctz = (byte[][]) this.zzctz.clone();
            }
            return com_google_android_gms_internal_adz;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError(e);
        }
    }

    public final /* synthetic */ Object clone() throws CloneNotSupportedException {
        return zzMa();
    }

    public final boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof adz)) {
            return false;
        }
        adz com_google_android_gms_internal_adz = (adz) obj;
        if (!Arrays.equals(this.zzctx, com_google_android_gms_internal_adz.zzctx)) {
            return false;
        }
        if (this.zzcty == null) {
            if (com_google_android_gms_internal_adz.zzcty != null) {
                return false;
            }
        } else if (!this.zzcty.equals(com_google_android_gms_internal_adz.zzcty)) {
            return false;
        }
        return !ade.zza(this.zzctz, com_google_android_gms_internal_adz.zzctz) ? false : this.zzctA != com_google_android_gms_internal_adz.zzctA ? false : (this.zzcrZ == null || this.zzcrZ.isEmpty()) ? com_google_android_gms_internal_adz.zzcrZ == null || com_google_android_gms_internal_adz.zzcrZ.isEmpty() : this.zzcrZ.equals(com_google_android_gms_internal_adz.zzcrZ);
    }

    public final int hashCode() {
        int i = 0;
        int hashCode = ((this.zzctA ? 1231 : 1237) + (((((this.zzcty == null ? 0 : this.zzcty.hashCode()) + ((((getClass().getName().hashCode() + 527) * 31) + Arrays.hashCode(this.zzctx)) * 31)) * 31) + ade.zzc(this.zzctz)) * 31)) * 31;
        if (!(this.zzcrZ == null || this.zzcrZ.isEmpty())) {
            i = this.zzcrZ.hashCode();
        }
        return hashCode + i;
    }

    public final /* synthetic */ ada zzLL() throws CloneNotSupportedException {
        return (adz) clone();
    }

    public final /* synthetic */ adg zzLM() throws CloneNotSupportedException {
        return (adz) clone();
    }

    public final /* synthetic */ adg zza(acx com_google_android_gms_internal_acx) throws IOException {
        while (true) {
            int zzLy = com_google_android_gms_internal_acx.zzLy();
            switch (zzLy) {
                case 0:
                    break;
                case 10:
                    this.zzctx = com_google_android_gms_internal_acx.readBytes();
                    continue;
                case 18:
                    int zzb = adj.zzb(com_google_android_gms_internal_acx, 18);
                    zzLy = this.zzctz == null ? 0 : this.zzctz.length;
                    Object obj = new byte[(zzb + zzLy)][];
                    if (zzLy != 0) {
                        System.arraycopy(this.zzctz, 0, obj, 0, zzLy);
                    }
                    while (zzLy < obj.length - 1) {
                        obj[zzLy] = com_google_android_gms_internal_acx.readBytes();
                        com_google_android_gms_internal_acx.zzLy();
                        zzLy++;
                    }
                    obj[zzLy] = com_google_android_gms_internal_acx.readBytes();
                    this.zzctz = obj;
                    continue;
                case 24:
                    this.zzctA = com_google_android_gms_internal_acx.zzLB();
                    continue;
                case 34:
                    this.zzcty = com_google_android_gms_internal_acx.readString();
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
        if (!Arrays.equals(this.zzctx, adj.zzcst)) {
            com_google_android_gms_internal_acy.zzb(1, this.zzctx);
        }
        if (this.zzctz != null && this.zzctz.length > 0) {
            for (byte[] bArr : this.zzctz) {
                if (bArr != null) {
                    com_google_android_gms_internal_acy.zzb(2, bArr);
                }
            }
        }
        if (this.zzctA) {
            com_google_android_gms_internal_acy.zzk(3, this.zzctA);
        }
        if (!(this.zzcty == null || this.zzcty.equals(""))) {
            com_google_android_gms_internal_acy.zzl(4, this.zzcty);
        }
        super.zza(com_google_android_gms_internal_acy);
    }

    protected final int zzn() {
        int i = 0;
        int zzn = super.zzn();
        if (!Arrays.equals(this.zzctx, adj.zzcst)) {
            zzn += acy.zzc(1, this.zzctx);
        }
        if (this.zzctz != null && this.zzctz.length > 0) {
            int i2 = 0;
            int i3 = 0;
            while (i < this.zzctz.length) {
                byte[] bArr = this.zzctz[i];
                if (bArr != null) {
                    i3++;
                    i2 += acy.zzJ(bArr);
                }
                i++;
            }
            zzn = (zzn + i2) + (i3 * 1);
        }
        if (this.zzctA) {
            zzn += acy.zzct(3) + 1;
        }
        return (this.zzcty == null || this.zzcty.equals("")) ? zzn : zzn + acy.zzm(4, this.zzcty);
    }
}
