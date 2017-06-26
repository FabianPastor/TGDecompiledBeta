package com.google.android.gms.internal;

import android.support.v4.view.MotionEventCompat;
import com.googlecode.mp4parser.authoring.tracks.h265.NalUnitTypes;
import java.io.IOException;

public final class adx extends ada<adx> implements Cloneable {
    private String[] zzctr;
    private String[] zzcts;
    private int[] zzctt;
    private long[] zzctu;
    private long[] zzctv;

    public adx() {
        this.zzctr = adj.EMPTY_STRING_ARRAY;
        this.zzcts = adj.EMPTY_STRING_ARRAY;
        this.zzctt = adj.zzcsn;
        this.zzctu = adj.zzcso;
        this.zzctv = adj.zzcso;
        this.zzcrZ = null;
        this.zzcsi = -1;
    }

    private adx zzLY() {
        try {
            adx com_google_android_gms_internal_adx = (adx) super.zzLL();
            if (this.zzctr != null && this.zzctr.length > 0) {
                com_google_android_gms_internal_adx.zzctr = (String[]) this.zzctr.clone();
            }
            if (this.zzcts != null && this.zzcts.length > 0) {
                com_google_android_gms_internal_adx.zzcts = (String[]) this.zzcts.clone();
            }
            if (this.zzctt != null && this.zzctt.length > 0) {
                com_google_android_gms_internal_adx.zzctt = (int[]) this.zzctt.clone();
            }
            if (this.zzctu != null && this.zzctu.length > 0) {
                com_google_android_gms_internal_adx.zzctu = (long[]) this.zzctu.clone();
            }
            if (this.zzctv != null && this.zzctv.length > 0) {
                com_google_android_gms_internal_adx.zzctv = (long[]) this.zzctv.clone();
            }
            return com_google_android_gms_internal_adx;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError(e);
        }
    }

    public final /* synthetic */ Object clone() throws CloneNotSupportedException {
        return zzLY();
    }

    public final boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof adx)) {
            return false;
        }
        adx com_google_android_gms_internal_adx = (adx) obj;
        return !ade.equals(this.zzctr, com_google_android_gms_internal_adx.zzctr) ? false : !ade.equals(this.zzcts, com_google_android_gms_internal_adx.zzcts) ? false : !ade.equals(this.zzctt, com_google_android_gms_internal_adx.zzctt) ? false : !ade.equals(this.zzctu, com_google_android_gms_internal_adx.zzctu) ? false : !ade.equals(this.zzctv, com_google_android_gms_internal_adx.zzctv) ? false : (this.zzcrZ == null || this.zzcrZ.isEmpty()) ? com_google_android_gms_internal_adx.zzcrZ == null || com_google_android_gms_internal_adx.zzcrZ.isEmpty() : this.zzcrZ.equals(com_google_android_gms_internal_adx.zzcrZ);
    }

    public final int hashCode() {
        int hashCode = (((((((((((getClass().getName().hashCode() + 527) * 31) + ade.hashCode(this.zzctr)) * 31) + ade.hashCode(this.zzcts)) * 31) + ade.hashCode(this.zzctt)) * 31) + ade.hashCode(this.zzctu)) * 31) + ade.hashCode(this.zzctv)) * 31;
        int hashCode2 = (this.zzcrZ == null || this.zzcrZ.isEmpty()) ? 0 : this.zzcrZ.hashCode();
        return hashCode2 + hashCode;
    }

    public final /* synthetic */ ada zzLL() throws CloneNotSupportedException {
        return (adx) clone();
    }

    public final /* synthetic */ adg zzLM() throws CloneNotSupportedException {
        return (adx) clone();
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
                case 10:
                    zzb = adj.zzb(com_google_android_gms_internal_acx, 10);
                    zzLy = this.zzctr == null ? 0 : this.zzctr.length;
                    obj = new String[(zzb + zzLy)];
                    if (zzLy != 0) {
                        System.arraycopy(this.zzctr, 0, obj, 0, zzLy);
                    }
                    while (zzLy < obj.length - 1) {
                        obj[zzLy] = com_google_android_gms_internal_acx.readString();
                        com_google_android_gms_internal_acx.zzLy();
                        zzLy++;
                    }
                    obj[zzLy] = com_google_android_gms_internal_acx.readString();
                    this.zzctr = obj;
                    continue;
                case 18:
                    zzb = adj.zzb(com_google_android_gms_internal_acx, 18);
                    zzLy = this.zzcts == null ? 0 : this.zzcts.length;
                    obj = new String[(zzb + zzLy)];
                    if (zzLy != 0) {
                        System.arraycopy(this.zzcts, 0, obj, 0, zzLy);
                    }
                    while (zzLy < obj.length - 1) {
                        obj[zzLy] = com_google_android_gms_internal_acx.readString();
                        com_google_android_gms_internal_acx.zzLy();
                        zzLy++;
                    }
                    obj[zzLy] = com_google_android_gms_internal_acx.readString();
                    this.zzcts = obj;
                    continue;
                case 24:
                    zzb = adj.zzb(com_google_android_gms_internal_acx, 24);
                    zzLy = this.zzctt == null ? 0 : this.zzctt.length;
                    obj = new int[(zzb + zzLy)];
                    if (zzLy != 0) {
                        System.arraycopy(this.zzctt, 0, obj, 0, zzLy);
                    }
                    while (zzLy < obj.length - 1) {
                        obj[zzLy] = com_google_android_gms_internal_acx.zzLA();
                        com_google_android_gms_internal_acx.zzLy();
                        zzLy++;
                    }
                    obj[zzLy] = com_google_android_gms_internal_acx.zzLA();
                    this.zzctt = obj;
                    continue;
                case NalUnitTypes.NAL_TYPE_RSV_VCL26 /*26*/:
                    zzcn = com_google_android_gms_internal_acx.zzcn(com_google_android_gms_internal_acx.zzLD());
                    zzb = com_google_android_gms_internal_acx.getPosition();
                    zzLy = 0;
                    while (com_google_android_gms_internal_acx.zzLI() > 0) {
                        com_google_android_gms_internal_acx.zzLA();
                        zzLy++;
                    }
                    com_google_android_gms_internal_acx.zzcp(zzb);
                    zzb = this.zzctt == null ? 0 : this.zzctt.length;
                    obj2 = new int[(zzLy + zzb)];
                    if (zzb != 0) {
                        System.arraycopy(this.zzctt, 0, obj2, 0, zzb);
                    }
                    while (zzb < obj2.length) {
                        obj2[zzb] = com_google_android_gms_internal_acx.zzLA();
                        zzb++;
                    }
                    this.zzctt = obj2;
                    com_google_android_gms_internal_acx.zzco(zzcn);
                    continue;
                case 32:
                    zzb = adj.zzb(com_google_android_gms_internal_acx, 32);
                    zzLy = this.zzctu == null ? 0 : this.zzctu.length;
                    obj = new long[(zzb + zzLy)];
                    if (zzLy != 0) {
                        System.arraycopy(this.zzctu, 0, obj, 0, zzLy);
                    }
                    while (zzLy < obj.length - 1) {
                        obj[zzLy] = com_google_android_gms_internal_acx.zzLz();
                        com_google_android_gms_internal_acx.zzLy();
                        zzLy++;
                    }
                    obj[zzLy] = com_google_android_gms_internal_acx.zzLz();
                    this.zzctu = obj;
                    continue;
                case 34:
                    zzcn = com_google_android_gms_internal_acx.zzcn(com_google_android_gms_internal_acx.zzLD());
                    zzb = com_google_android_gms_internal_acx.getPosition();
                    zzLy = 0;
                    while (com_google_android_gms_internal_acx.zzLI() > 0) {
                        com_google_android_gms_internal_acx.zzLz();
                        zzLy++;
                    }
                    com_google_android_gms_internal_acx.zzcp(zzb);
                    zzb = this.zzctu == null ? 0 : this.zzctu.length;
                    obj2 = new long[(zzLy + zzb)];
                    if (zzb != 0) {
                        System.arraycopy(this.zzctu, 0, obj2, 0, zzb);
                    }
                    while (zzb < obj2.length) {
                        obj2[zzb] = com_google_android_gms_internal_acx.zzLz();
                        zzb++;
                    }
                    this.zzctu = obj2;
                    com_google_android_gms_internal_acx.zzco(zzcn);
                    continue;
                case MotionEventCompat.AXIS_GENERIC_9 /*40*/:
                    zzb = adj.zzb(com_google_android_gms_internal_acx, 40);
                    zzLy = this.zzctv == null ? 0 : this.zzctv.length;
                    obj = new long[(zzb + zzLy)];
                    if (zzLy != 0) {
                        System.arraycopy(this.zzctv, 0, obj, 0, zzLy);
                    }
                    while (zzLy < obj.length - 1) {
                        obj[zzLy] = com_google_android_gms_internal_acx.zzLz();
                        com_google_android_gms_internal_acx.zzLy();
                        zzLy++;
                    }
                    obj[zzLy] = com_google_android_gms_internal_acx.zzLz();
                    this.zzctv = obj;
                    continue;
                case 42:
                    zzcn = com_google_android_gms_internal_acx.zzcn(com_google_android_gms_internal_acx.zzLD());
                    zzb = com_google_android_gms_internal_acx.getPosition();
                    zzLy = 0;
                    while (com_google_android_gms_internal_acx.zzLI() > 0) {
                        com_google_android_gms_internal_acx.zzLz();
                        zzLy++;
                    }
                    com_google_android_gms_internal_acx.zzcp(zzb);
                    zzb = this.zzctv == null ? 0 : this.zzctv.length;
                    obj2 = new long[(zzLy + zzb)];
                    if (zzb != 0) {
                        System.arraycopy(this.zzctv, 0, obj2, 0, zzb);
                    }
                    while (zzb < obj2.length) {
                        obj2[zzb] = com_google_android_gms_internal_acx.zzLz();
                        zzb++;
                    }
                    this.zzctv = obj2;
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
        if (this.zzctr != null && this.zzctr.length > 0) {
            for (String str : this.zzctr) {
                if (str != null) {
                    com_google_android_gms_internal_acy.zzl(1, str);
                }
            }
        }
        if (this.zzcts != null && this.zzcts.length > 0) {
            for (String str2 : this.zzcts) {
                if (str2 != null) {
                    com_google_android_gms_internal_acy.zzl(2, str2);
                }
            }
        }
        if (this.zzctt != null && this.zzctt.length > 0) {
            for (int zzr : this.zzctt) {
                com_google_android_gms_internal_acy.zzr(3, zzr);
            }
        }
        if (this.zzctu != null && this.zzctu.length > 0) {
            for (long zzb : this.zzctu) {
                com_google_android_gms_internal_acy.zzb(4, zzb);
            }
        }
        if (this.zzctv != null && this.zzctv.length > 0) {
            while (i < this.zzctv.length) {
                com_google_android_gms_internal_acy.zzb(5, this.zzctv[i]);
                i++;
            }
        }
        super.zza(com_google_android_gms_internal_acy);
    }

    protected final int zzn() {
        int i;
        int i2;
        int i3;
        int i4 = 0;
        int zzn = super.zzn();
        if (this.zzctr == null || this.zzctr.length <= 0) {
            i = zzn;
        } else {
            i2 = 0;
            i3 = 0;
            for (String str : this.zzctr) {
                if (str != null) {
                    i3++;
                    i2 += acy.zzhQ(str);
                }
            }
            i = (zzn + i2) + (i3 * 1);
        }
        if (this.zzcts != null && this.zzcts.length > 0) {
            i3 = 0;
            zzn = 0;
            for (String str2 : this.zzcts) {
                if (str2 != null) {
                    zzn++;
                    i3 += acy.zzhQ(str2);
                }
            }
            i = (i + i3) + (zzn * 1);
        }
        if (this.zzctt != null && this.zzctt.length > 0) {
            i3 = 0;
            for (int zzn2 : this.zzctt) {
                i3 += acy.zzcr(zzn2);
            }
            i = (i + i3) + (this.zzctt.length * 1);
        }
        if (this.zzctu != null && this.zzctu.length > 0) {
            i3 = 0;
            for (long zzaP : this.zzctu) {
                i3 += acy.zzaP(zzaP);
            }
            i = (i + i3) + (this.zzctu.length * 1);
        }
        if (this.zzctv == null || this.zzctv.length <= 0) {
            return i;
        }
        i2 = 0;
        while (i4 < this.zzctv.length) {
            i2 += acy.zzaP(this.zzctv[i4]);
            i4++;
        }
        return (i + i2) + (this.zzctv.length * 1);
    }
}
