package com.google.android.gms.internal;

import android.support.v4.view.MotionEventCompat;
import com.googlecode.mp4parser.authoring.tracks.h265.NalUnitTypes;
import java.io.IOException;

public final class aeg extends adj<aeg> implements Cloneable {
    private String[] zzctv;
    private String[] zzctw;
    private int[] zzctx;
    private long[] zzcty;
    private long[] zzctz;

    public aeg() {
        this.zzctv = ads.EMPTY_STRING_ARRAY;
        this.zzctw = ads.EMPTY_STRING_ARRAY;
        this.zzctx = ads.zzcsr;
        this.zzcty = ads.zzcss;
        this.zzctz = ads.zzcss;
        this.zzcsd = null;
        this.zzcsm = -1;
    }

    private aeg zzMb() {
        try {
            aeg com_google_android_gms_internal_aeg = (aeg) super.zzLO();
            if (this.zzctv != null && this.zzctv.length > 0) {
                com_google_android_gms_internal_aeg.zzctv = (String[]) this.zzctv.clone();
            }
            if (this.zzctw != null && this.zzctw.length > 0) {
                com_google_android_gms_internal_aeg.zzctw = (String[]) this.zzctw.clone();
            }
            if (this.zzctx != null && this.zzctx.length > 0) {
                com_google_android_gms_internal_aeg.zzctx = (int[]) this.zzctx.clone();
            }
            if (this.zzcty != null && this.zzcty.length > 0) {
                com_google_android_gms_internal_aeg.zzcty = (long[]) this.zzcty.clone();
            }
            if (this.zzctz != null && this.zzctz.length > 0) {
                com_google_android_gms_internal_aeg.zzctz = (long[]) this.zzctz.clone();
            }
            return com_google_android_gms_internal_aeg;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError(e);
        }
    }

    public final /* synthetic */ Object clone() throws CloneNotSupportedException {
        return zzMb();
    }

    public final boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof aeg)) {
            return false;
        }
        aeg com_google_android_gms_internal_aeg = (aeg) obj;
        return !adn.equals(this.zzctv, com_google_android_gms_internal_aeg.zzctv) ? false : !adn.equals(this.zzctw, com_google_android_gms_internal_aeg.zzctw) ? false : !adn.equals(this.zzctx, com_google_android_gms_internal_aeg.zzctx) ? false : !adn.equals(this.zzcty, com_google_android_gms_internal_aeg.zzcty) ? false : !adn.equals(this.zzctz, com_google_android_gms_internal_aeg.zzctz) ? false : (this.zzcsd == null || this.zzcsd.isEmpty()) ? com_google_android_gms_internal_aeg.zzcsd == null || com_google_android_gms_internal_aeg.zzcsd.isEmpty() : this.zzcsd.equals(com_google_android_gms_internal_aeg.zzcsd);
    }

    public final int hashCode() {
        int hashCode = (((((((((((getClass().getName().hashCode() + 527) * 31) + adn.hashCode(this.zzctv)) * 31) + adn.hashCode(this.zzctw)) * 31) + adn.hashCode(this.zzctx)) * 31) + adn.hashCode(this.zzcty)) * 31) + adn.hashCode(this.zzctz)) * 31;
        int hashCode2 = (this.zzcsd == null || this.zzcsd.isEmpty()) ? 0 : this.zzcsd.hashCode();
        return hashCode2 + hashCode;
    }

    public final /* synthetic */ adj zzLO() throws CloneNotSupportedException {
        return (aeg) clone();
    }

    public final /* synthetic */ adp zzLP() throws CloneNotSupportedException {
        return (aeg) clone();
    }

    public final /* synthetic */ adp zza(adg com_google_android_gms_internal_adg) throws IOException {
        while (true) {
            int zzLB = com_google_android_gms_internal_adg.zzLB();
            int zzb;
            Object obj;
            int zzcn;
            Object obj2;
            switch (zzLB) {
                case 0:
                    break;
                case 10:
                    zzb = ads.zzb(com_google_android_gms_internal_adg, 10);
                    zzLB = this.zzctv == null ? 0 : this.zzctv.length;
                    obj = new String[(zzb + zzLB)];
                    if (zzLB != 0) {
                        System.arraycopy(this.zzctv, 0, obj, 0, zzLB);
                    }
                    while (zzLB < obj.length - 1) {
                        obj[zzLB] = com_google_android_gms_internal_adg.readString();
                        com_google_android_gms_internal_adg.zzLB();
                        zzLB++;
                    }
                    obj[zzLB] = com_google_android_gms_internal_adg.readString();
                    this.zzctv = obj;
                    continue;
                case 18:
                    zzb = ads.zzb(com_google_android_gms_internal_adg, 18);
                    zzLB = this.zzctw == null ? 0 : this.zzctw.length;
                    obj = new String[(zzb + zzLB)];
                    if (zzLB != 0) {
                        System.arraycopy(this.zzctw, 0, obj, 0, zzLB);
                    }
                    while (zzLB < obj.length - 1) {
                        obj[zzLB] = com_google_android_gms_internal_adg.readString();
                        com_google_android_gms_internal_adg.zzLB();
                        zzLB++;
                    }
                    obj[zzLB] = com_google_android_gms_internal_adg.readString();
                    this.zzctw = obj;
                    continue;
                case 24:
                    zzb = ads.zzb(com_google_android_gms_internal_adg, 24);
                    zzLB = this.zzctx == null ? 0 : this.zzctx.length;
                    obj = new int[(zzb + zzLB)];
                    if (zzLB != 0) {
                        System.arraycopy(this.zzctx, 0, obj, 0, zzLB);
                    }
                    while (zzLB < obj.length - 1) {
                        obj[zzLB] = com_google_android_gms_internal_adg.zzLD();
                        com_google_android_gms_internal_adg.zzLB();
                        zzLB++;
                    }
                    obj[zzLB] = com_google_android_gms_internal_adg.zzLD();
                    this.zzctx = obj;
                    continue;
                case NalUnitTypes.NAL_TYPE_RSV_VCL26 /*26*/:
                    zzcn = com_google_android_gms_internal_adg.zzcn(com_google_android_gms_internal_adg.zzLG());
                    zzb = com_google_android_gms_internal_adg.getPosition();
                    zzLB = 0;
                    while (com_google_android_gms_internal_adg.zzLL() > 0) {
                        com_google_android_gms_internal_adg.zzLD();
                        zzLB++;
                    }
                    com_google_android_gms_internal_adg.zzcp(zzb);
                    zzb = this.zzctx == null ? 0 : this.zzctx.length;
                    obj2 = new int[(zzLB + zzb)];
                    if (zzb != 0) {
                        System.arraycopy(this.zzctx, 0, obj2, 0, zzb);
                    }
                    while (zzb < obj2.length) {
                        obj2[zzb] = com_google_android_gms_internal_adg.zzLD();
                        zzb++;
                    }
                    this.zzctx = obj2;
                    com_google_android_gms_internal_adg.zzco(zzcn);
                    continue;
                case 32:
                    zzb = ads.zzb(com_google_android_gms_internal_adg, 32);
                    zzLB = this.zzcty == null ? 0 : this.zzcty.length;
                    obj = new long[(zzb + zzLB)];
                    if (zzLB != 0) {
                        System.arraycopy(this.zzcty, 0, obj, 0, zzLB);
                    }
                    while (zzLB < obj.length - 1) {
                        obj[zzLB] = com_google_android_gms_internal_adg.zzLC();
                        com_google_android_gms_internal_adg.zzLB();
                        zzLB++;
                    }
                    obj[zzLB] = com_google_android_gms_internal_adg.zzLC();
                    this.zzcty = obj;
                    continue;
                case 34:
                    zzcn = com_google_android_gms_internal_adg.zzcn(com_google_android_gms_internal_adg.zzLG());
                    zzb = com_google_android_gms_internal_adg.getPosition();
                    zzLB = 0;
                    while (com_google_android_gms_internal_adg.zzLL() > 0) {
                        com_google_android_gms_internal_adg.zzLC();
                        zzLB++;
                    }
                    com_google_android_gms_internal_adg.zzcp(zzb);
                    zzb = this.zzcty == null ? 0 : this.zzcty.length;
                    obj2 = new long[(zzLB + zzb)];
                    if (zzb != 0) {
                        System.arraycopy(this.zzcty, 0, obj2, 0, zzb);
                    }
                    while (zzb < obj2.length) {
                        obj2[zzb] = com_google_android_gms_internal_adg.zzLC();
                        zzb++;
                    }
                    this.zzcty = obj2;
                    com_google_android_gms_internal_adg.zzco(zzcn);
                    continue;
                case MotionEventCompat.AXIS_GENERIC_9 /*40*/:
                    zzb = ads.zzb(com_google_android_gms_internal_adg, 40);
                    zzLB = this.zzctz == null ? 0 : this.zzctz.length;
                    obj = new long[(zzb + zzLB)];
                    if (zzLB != 0) {
                        System.arraycopy(this.zzctz, 0, obj, 0, zzLB);
                    }
                    while (zzLB < obj.length - 1) {
                        obj[zzLB] = com_google_android_gms_internal_adg.zzLC();
                        com_google_android_gms_internal_adg.zzLB();
                        zzLB++;
                    }
                    obj[zzLB] = com_google_android_gms_internal_adg.zzLC();
                    this.zzctz = obj;
                    continue;
                case 42:
                    zzcn = com_google_android_gms_internal_adg.zzcn(com_google_android_gms_internal_adg.zzLG());
                    zzb = com_google_android_gms_internal_adg.getPosition();
                    zzLB = 0;
                    while (com_google_android_gms_internal_adg.zzLL() > 0) {
                        com_google_android_gms_internal_adg.zzLC();
                        zzLB++;
                    }
                    com_google_android_gms_internal_adg.zzcp(zzb);
                    zzb = this.zzctz == null ? 0 : this.zzctz.length;
                    obj2 = new long[(zzLB + zzb)];
                    if (zzb != 0) {
                        System.arraycopy(this.zzctz, 0, obj2, 0, zzb);
                    }
                    while (zzb < obj2.length) {
                        obj2[zzb] = com_google_android_gms_internal_adg.zzLC();
                        zzb++;
                    }
                    this.zzctz = obj2;
                    com_google_android_gms_internal_adg.zzco(zzcn);
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
        int i = 0;
        if (this.zzctv != null && this.zzctv.length > 0) {
            for (String str : this.zzctv) {
                if (str != null) {
                    com_google_android_gms_internal_adh.zzl(1, str);
                }
            }
        }
        if (this.zzctw != null && this.zzctw.length > 0) {
            for (String str2 : this.zzctw) {
                if (str2 != null) {
                    com_google_android_gms_internal_adh.zzl(2, str2);
                }
            }
        }
        if (this.zzctx != null && this.zzctx.length > 0) {
            for (int zzr : this.zzctx) {
                com_google_android_gms_internal_adh.zzr(3, zzr);
            }
        }
        if (this.zzcty != null && this.zzcty.length > 0) {
            for (long zzb : this.zzcty) {
                com_google_android_gms_internal_adh.zzb(4, zzb);
            }
        }
        if (this.zzctz != null && this.zzctz.length > 0) {
            while (i < this.zzctz.length) {
                com_google_android_gms_internal_adh.zzb(5, this.zzctz[i]);
                i++;
            }
        }
        super.zza(com_google_android_gms_internal_adh);
    }

    protected final int zzn() {
        int i;
        int i2;
        int i3;
        int i4 = 0;
        int zzn = super.zzn();
        if (this.zzctv == null || this.zzctv.length <= 0) {
            i = zzn;
        } else {
            i2 = 0;
            i3 = 0;
            for (String str : this.zzctv) {
                if (str != null) {
                    i3++;
                    i2 += adh.zzhS(str);
                }
            }
            i = (zzn + i2) + (i3 * 1);
        }
        if (this.zzctw != null && this.zzctw.length > 0) {
            i3 = 0;
            zzn = 0;
            for (String str2 : this.zzctw) {
                if (str2 != null) {
                    zzn++;
                    i3 += adh.zzhS(str2);
                }
            }
            i = (i + i3) + (zzn * 1);
        }
        if (this.zzctx != null && this.zzctx.length > 0) {
            i3 = 0;
            for (int zzn2 : this.zzctx) {
                i3 += adh.zzcr(zzn2);
            }
            i = (i + i3) + (this.zzctx.length * 1);
        }
        if (this.zzcty != null && this.zzcty.length > 0) {
            i3 = 0;
            for (long zzaP : this.zzcty) {
                i3 += adh.zzaP(zzaP);
            }
            i = (i + i3) + (this.zzcty.length * 1);
        }
        if (this.zzctz == null || this.zzctz.length <= 0) {
            return i;
        }
        i2 = 0;
        while (i4 < this.zzctz.length) {
            i2 += adh.zzaP(this.zzctz[i4]);
            i4++;
        }
        return (i + i2) + (this.zzctz.length * 1);
    }
}
