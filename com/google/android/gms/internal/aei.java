package com.google.android.gms.internal;

import java.io.IOException;
import java.util.Arrays;

public final class aei extends adj<aei> implements Cloneable {
    private byte[] zzctB;
    private String zzctC;
    private byte[][] zzctD;
    private boolean zzctE;

    public aei() {
        this.zzctB = ads.zzcsx;
        this.zzctC = "";
        this.zzctD = ads.zzcsw;
        this.zzctE = false;
        this.zzcsd = null;
        this.zzcsm = -1;
    }

    private aei zzMd() {
        try {
            aei com_google_android_gms_internal_aei = (aei) super.zzLO();
            if (this.zzctD != null && this.zzctD.length > 0) {
                com_google_android_gms_internal_aei.zzctD = (byte[][]) this.zzctD.clone();
            }
            return com_google_android_gms_internal_aei;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError(e);
        }
    }

    public final /* synthetic */ Object clone() throws CloneNotSupportedException {
        return zzMd();
    }

    public final boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof aei)) {
            return false;
        }
        aei com_google_android_gms_internal_aei = (aei) obj;
        if (!Arrays.equals(this.zzctB, com_google_android_gms_internal_aei.zzctB)) {
            return false;
        }
        if (this.zzctC == null) {
            if (com_google_android_gms_internal_aei.zzctC != null) {
                return false;
            }
        } else if (!this.zzctC.equals(com_google_android_gms_internal_aei.zzctC)) {
            return false;
        }
        return !adn.zza(this.zzctD, com_google_android_gms_internal_aei.zzctD) ? false : this.zzctE != com_google_android_gms_internal_aei.zzctE ? false : (this.zzcsd == null || this.zzcsd.isEmpty()) ? com_google_android_gms_internal_aei.zzcsd == null || com_google_android_gms_internal_aei.zzcsd.isEmpty() : this.zzcsd.equals(com_google_android_gms_internal_aei.zzcsd);
    }

    public final int hashCode() {
        int i = 0;
        int hashCode = ((this.zzctE ? 1231 : 1237) + (((((this.zzctC == null ? 0 : this.zzctC.hashCode()) + ((((getClass().getName().hashCode() + 527) * 31) + Arrays.hashCode(this.zzctB)) * 31)) * 31) + adn.zzc(this.zzctD)) * 31)) * 31;
        if (!(this.zzcsd == null || this.zzcsd.isEmpty())) {
            i = this.zzcsd.hashCode();
        }
        return hashCode + i;
    }

    public final /* synthetic */ adj zzLO() throws CloneNotSupportedException {
        return (aei) clone();
    }

    public final /* synthetic */ adp zzLP() throws CloneNotSupportedException {
        return (aei) clone();
    }

    public final /* synthetic */ adp zza(adg com_google_android_gms_internal_adg) throws IOException {
        while (true) {
            int zzLB = com_google_android_gms_internal_adg.zzLB();
            switch (zzLB) {
                case 0:
                    break;
                case 10:
                    this.zzctB = com_google_android_gms_internal_adg.readBytes();
                    continue;
                case 18:
                    int zzb = ads.zzb(com_google_android_gms_internal_adg, 18);
                    zzLB = this.zzctD == null ? 0 : this.zzctD.length;
                    Object obj = new byte[(zzb + zzLB)][];
                    if (zzLB != 0) {
                        System.arraycopy(this.zzctD, 0, obj, 0, zzLB);
                    }
                    while (zzLB < obj.length - 1) {
                        obj[zzLB] = com_google_android_gms_internal_adg.readBytes();
                        com_google_android_gms_internal_adg.zzLB();
                        zzLB++;
                    }
                    obj[zzLB] = com_google_android_gms_internal_adg.readBytes();
                    this.zzctD = obj;
                    continue;
                case 24:
                    this.zzctE = com_google_android_gms_internal_adg.zzLE();
                    continue;
                case 34:
                    this.zzctC = com_google_android_gms_internal_adg.readString();
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
        if (!Arrays.equals(this.zzctB, ads.zzcsx)) {
            com_google_android_gms_internal_adh.zzb(1, this.zzctB);
        }
        if (this.zzctD != null && this.zzctD.length > 0) {
            for (byte[] bArr : this.zzctD) {
                if (bArr != null) {
                    com_google_android_gms_internal_adh.zzb(2, bArr);
                }
            }
        }
        if (this.zzctE) {
            com_google_android_gms_internal_adh.zzk(3, this.zzctE);
        }
        if (!(this.zzctC == null || this.zzctC.equals(""))) {
            com_google_android_gms_internal_adh.zzl(4, this.zzctC);
        }
        super.zza(com_google_android_gms_internal_adh);
    }

    protected final int zzn() {
        int i = 0;
        int zzn = super.zzn();
        if (!Arrays.equals(this.zzctB, ads.zzcsx)) {
            zzn += adh.zzc(1, this.zzctB);
        }
        if (this.zzctD != null && this.zzctD.length > 0) {
            int i2 = 0;
            int i3 = 0;
            while (i < this.zzctD.length) {
                byte[] bArr = this.zzctD[i];
                if (bArr != null) {
                    i3++;
                    i2 += adh.zzJ(bArr);
                }
                i++;
            }
            zzn = (zzn + i2) + (i3 * 1);
        }
        if (this.zzctE) {
            zzn += adh.zzct(3) + 1;
        }
        return (this.zzctC == null || this.zzctC.equals("")) ? zzn : zzn + adh.zzm(4, this.zzctC);
    }
}
