package com.google.android.gms.internal;

import android.support.v4.view.MotionEventCompat;
import com.googlecode.mp4parser.authoring.tracks.h265.NalUnitTypes;
import java.io.IOException;

public final class aeg extends adj<aeg> implements Cloneable {
    private String[] zzctG;
    private String[] zzctH;
    private int[] zzctI;
    private long[] zzctJ;
    private long[] zzctK;

    public aeg() {
        this.zzctG = ads.EMPTY_STRING_ARRAY;
        this.zzctH = ads.EMPTY_STRING_ARRAY;
        this.zzctI = ads.zzcsC;
        this.zzctJ = ads.zzcsD;
        this.zzctK = ads.zzcsD;
        this.zzcso = null;
        this.zzcsx = -1;
    }

    private aeg zzMa() {
        try {
            aeg com_google_android_gms_internal_aeg = (aeg) super.zzLN();
            if (this.zzctG != null && this.zzctG.length > 0) {
                com_google_android_gms_internal_aeg.zzctG = (String[]) this.zzctG.clone();
            }
            if (this.zzctH != null && this.zzctH.length > 0) {
                com_google_android_gms_internal_aeg.zzctH = (String[]) this.zzctH.clone();
            }
            if (this.zzctI != null && this.zzctI.length > 0) {
                com_google_android_gms_internal_aeg.zzctI = (int[]) this.zzctI.clone();
            }
            if (this.zzctJ != null && this.zzctJ.length > 0) {
                com_google_android_gms_internal_aeg.zzctJ = (long[]) this.zzctJ.clone();
            }
            if (this.zzctK != null && this.zzctK.length > 0) {
                com_google_android_gms_internal_aeg.zzctK = (long[]) this.zzctK.clone();
            }
            return com_google_android_gms_internal_aeg;
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
        if (!(obj instanceof aeg)) {
            return false;
        }
        aeg com_google_android_gms_internal_aeg = (aeg) obj;
        return !adn.equals(this.zzctG, com_google_android_gms_internal_aeg.zzctG) ? false : !adn.equals(this.zzctH, com_google_android_gms_internal_aeg.zzctH) ? false : !adn.equals(this.zzctI, com_google_android_gms_internal_aeg.zzctI) ? false : !adn.equals(this.zzctJ, com_google_android_gms_internal_aeg.zzctJ) ? false : !adn.equals(this.zzctK, com_google_android_gms_internal_aeg.zzctK) ? false : (this.zzcso == null || this.zzcso.isEmpty()) ? com_google_android_gms_internal_aeg.zzcso == null || com_google_android_gms_internal_aeg.zzcso.isEmpty() : this.zzcso.equals(com_google_android_gms_internal_aeg.zzcso);
    }

    public final int hashCode() {
        int hashCode = (((((((((((getClass().getName().hashCode() + 527) * 31) + adn.hashCode(this.zzctG)) * 31) + adn.hashCode(this.zzctH)) * 31) + adn.hashCode(this.zzctI)) * 31) + adn.hashCode(this.zzctJ)) * 31) + adn.hashCode(this.zzctK)) * 31;
        int hashCode2 = (this.zzcso == null || this.zzcso.isEmpty()) ? 0 : this.zzcso.hashCode();
        return hashCode2 + hashCode;
    }

    public final /* synthetic */ adj zzLN() throws CloneNotSupportedException {
        return (aeg) clone();
    }

    public final /* synthetic */ adp zzLO() throws CloneNotSupportedException {
        return (aeg) clone();
    }

    public final /* synthetic */ adp zza(adg com_google_android_gms_internal_adg) throws IOException {
        while (true) {
            int zzLA = com_google_android_gms_internal_adg.zzLA();
            int zzb;
            Object obj;
            int zzcn;
            Object obj2;
            switch (zzLA) {
                case 0:
                    break;
                case 10:
                    zzb = ads.zzb(com_google_android_gms_internal_adg, 10);
                    zzLA = this.zzctG == null ? 0 : this.zzctG.length;
                    obj = new String[(zzb + zzLA)];
                    if (zzLA != 0) {
                        System.arraycopy(this.zzctG, 0, obj, 0, zzLA);
                    }
                    while (zzLA < obj.length - 1) {
                        obj[zzLA] = com_google_android_gms_internal_adg.readString();
                        com_google_android_gms_internal_adg.zzLA();
                        zzLA++;
                    }
                    obj[zzLA] = com_google_android_gms_internal_adg.readString();
                    this.zzctG = obj;
                    continue;
                case 18:
                    zzb = ads.zzb(com_google_android_gms_internal_adg, 18);
                    zzLA = this.zzctH == null ? 0 : this.zzctH.length;
                    obj = new String[(zzb + zzLA)];
                    if (zzLA != 0) {
                        System.arraycopy(this.zzctH, 0, obj, 0, zzLA);
                    }
                    while (zzLA < obj.length - 1) {
                        obj[zzLA] = com_google_android_gms_internal_adg.readString();
                        com_google_android_gms_internal_adg.zzLA();
                        zzLA++;
                    }
                    obj[zzLA] = com_google_android_gms_internal_adg.readString();
                    this.zzctH = obj;
                    continue;
                case 24:
                    zzb = ads.zzb(com_google_android_gms_internal_adg, 24);
                    zzLA = this.zzctI == null ? 0 : this.zzctI.length;
                    obj = new int[(zzb + zzLA)];
                    if (zzLA != 0) {
                        System.arraycopy(this.zzctI, 0, obj, 0, zzLA);
                    }
                    while (zzLA < obj.length - 1) {
                        obj[zzLA] = com_google_android_gms_internal_adg.zzLC();
                        com_google_android_gms_internal_adg.zzLA();
                        zzLA++;
                    }
                    obj[zzLA] = com_google_android_gms_internal_adg.zzLC();
                    this.zzctI = obj;
                    continue;
                case NalUnitTypes.NAL_TYPE_RSV_VCL26 /*26*/:
                    zzcn = com_google_android_gms_internal_adg.zzcn(com_google_android_gms_internal_adg.zzLF());
                    zzb = com_google_android_gms_internal_adg.getPosition();
                    zzLA = 0;
                    while (com_google_android_gms_internal_adg.zzLK() > 0) {
                        com_google_android_gms_internal_adg.zzLC();
                        zzLA++;
                    }
                    com_google_android_gms_internal_adg.zzcp(zzb);
                    zzb = this.zzctI == null ? 0 : this.zzctI.length;
                    obj2 = new int[(zzLA + zzb)];
                    if (zzb != 0) {
                        System.arraycopy(this.zzctI, 0, obj2, 0, zzb);
                    }
                    while (zzb < obj2.length) {
                        obj2[zzb] = com_google_android_gms_internal_adg.zzLC();
                        zzb++;
                    }
                    this.zzctI = obj2;
                    com_google_android_gms_internal_adg.zzco(zzcn);
                    continue;
                case 32:
                    zzb = ads.zzb(com_google_android_gms_internal_adg, 32);
                    zzLA = this.zzctJ == null ? 0 : this.zzctJ.length;
                    obj = new long[(zzb + zzLA)];
                    if (zzLA != 0) {
                        System.arraycopy(this.zzctJ, 0, obj, 0, zzLA);
                    }
                    while (zzLA < obj.length - 1) {
                        obj[zzLA] = com_google_android_gms_internal_adg.zzLB();
                        com_google_android_gms_internal_adg.zzLA();
                        zzLA++;
                    }
                    obj[zzLA] = com_google_android_gms_internal_adg.zzLB();
                    this.zzctJ = obj;
                    continue;
                case 34:
                    zzcn = com_google_android_gms_internal_adg.zzcn(com_google_android_gms_internal_adg.zzLF());
                    zzb = com_google_android_gms_internal_adg.getPosition();
                    zzLA = 0;
                    while (com_google_android_gms_internal_adg.zzLK() > 0) {
                        com_google_android_gms_internal_adg.zzLB();
                        zzLA++;
                    }
                    com_google_android_gms_internal_adg.zzcp(zzb);
                    zzb = this.zzctJ == null ? 0 : this.zzctJ.length;
                    obj2 = new long[(zzLA + zzb)];
                    if (zzb != 0) {
                        System.arraycopy(this.zzctJ, 0, obj2, 0, zzb);
                    }
                    while (zzb < obj2.length) {
                        obj2[zzb] = com_google_android_gms_internal_adg.zzLB();
                        zzb++;
                    }
                    this.zzctJ = obj2;
                    com_google_android_gms_internal_adg.zzco(zzcn);
                    continue;
                case MotionEventCompat.AXIS_GENERIC_9 /*40*/:
                    zzb = ads.zzb(com_google_android_gms_internal_adg, 40);
                    zzLA = this.zzctK == null ? 0 : this.zzctK.length;
                    obj = new long[(zzb + zzLA)];
                    if (zzLA != 0) {
                        System.arraycopy(this.zzctK, 0, obj, 0, zzLA);
                    }
                    while (zzLA < obj.length - 1) {
                        obj[zzLA] = com_google_android_gms_internal_adg.zzLB();
                        com_google_android_gms_internal_adg.zzLA();
                        zzLA++;
                    }
                    obj[zzLA] = com_google_android_gms_internal_adg.zzLB();
                    this.zzctK = obj;
                    continue;
                case 42:
                    zzcn = com_google_android_gms_internal_adg.zzcn(com_google_android_gms_internal_adg.zzLF());
                    zzb = com_google_android_gms_internal_adg.getPosition();
                    zzLA = 0;
                    while (com_google_android_gms_internal_adg.zzLK() > 0) {
                        com_google_android_gms_internal_adg.zzLB();
                        zzLA++;
                    }
                    com_google_android_gms_internal_adg.zzcp(zzb);
                    zzb = this.zzctK == null ? 0 : this.zzctK.length;
                    obj2 = new long[(zzLA + zzb)];
                    if (zzb != 0) {
                        System.arraycopy(this.zzctK, 0, obj2, 0, zzb);
                    }
                    while (zzb < obj2.length) {
                        obj2[zzb] = com_google_android_gms_internal_adg.zzLB();
                        zzb++;
                    }
                    this.zzctK = obj2;
                    com_google_android_gms_internal_adg.zzco(zzcn);
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
        int i = 0;
        if (this.zzctG != null && this.zzctG.length > 0) {
            for (String str : this.zzctG) {
                if (str != null) {
                    com_google_android_gms_internal_adh.zzl(1, str);
                }
            }
        }
        if (this.zzctH != null && this.zzctH.length > 0) {
            for (String str2 : this.zzctH) {
                if (str2 != null) {
                    com_google_android_gms_internal_adh.zzl(2, str2);
                }
            }
        }
        if (this.zzctI != null && this.zzctI.length > 0) {
            for (int zzr : this.zzctI) {
                com_google_android_gms_internal_adh.zzr(3, zzr);
            }
        }
        if (this.zzctJ != null && this.zzctJ.length > 0) {
            for (long zzb : this.zzctJ) {
                com_google_android_gms_internal_adh.zzb(4, zzb);
            }
        }
        if (this.zzctK != null && this.zzctK.length > 0) {
            while (i < this.zzctK.length) {
                com_google_android_gms_internal_adh.zzb(5, this.zzctK[i]);
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
        if (this.zzctG == null || this.zzctG.length <= 0) {
            i = zzn;
        } else {
            i2 = 0;
            i3 = 0;
            for (String str : this.zzctG) {
                if (str != null) {
                    i3++;
                    i2 += adh.zzhQ(str);
                }
            }
            i = (zzn + i2) + (i3 * 1);
        }
        if (this.zzctH != null && this.zzctH.length > 0) {
            i3 = 0;
            zzn = 0;
            for (String str2 : this.zzctH) {
                if (str2 != null) {
                    zzn++;
                    i3 += adh.zzhQ(str2);
                }
            }
            i = (i + i3) + (zzn * 1);
        }
        if (this.zzctI != null && this.zzctI.length > 0) {
            i3 = 0;
            for (int zzn2 : this.zzctI) {
                i3 += adh.zzcr(zzn2);
            }
            i = (i + i3) + (this.zzctI.length * 1);
        }
        if (this.zzctJ != null && this.zzctJ.length > 0) {
            i3 = 0;
            for (long zzaP : this.zzctJ) {
                i3 += adh.zzaP(zzaP);
            }
            i = (i + i3) + (this.zzctJ.length * 1);
        }
        if (this.zzctK == null || this.zzctK.length <= 0) {
            return i;
        }
        i2 = 0;
        while (i4 < this.zzctK.length) {
            i2 += adh.zzaP(this.zzctK[i4]);
            i4++;
        }
        return (i + i2) + (this.zzctK.length * 1);
    }
}
