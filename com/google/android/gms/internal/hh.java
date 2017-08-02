package com.google.android.gms.internal;

import android.support.v4.view.MotionEventCompat;
import java.io.IOException;
import java.util.Arrays;

public final class hh extends adj<hh> {
    public byte[] zzbTM;
    public String zzbTN;
    public double zzbTO;
    public float zzbTP;
    public long zzbTQ;
    public int zzbTR;
    public int zzbTS;
    public boolean zzbTT;
    public hf[] zzbTU;
    public hg[] zzbTV;
    public String[] zzbTW;
    public long[] zzbTX;
    public float[] zzbTY;
    public long zzbTZ;

    public hh() {
        this.zzbTM = ads.zzcsI;
        this.zzbTN = "";
        this.zzbTO = 0.0d;
        this.zzbTP = 0.0f;
        this.zzbTQ = 0;
        this.zzbTR = 0;
        this.zzbTS = 0;
        this.zzbTT = false;
        this.zzbTU = hf.zzEa();
        this.zzbTV = hg.zzEb();
        this.zzbTW = ads.EMPTY_STRING_ARRAY;
        this.zzbTX = ads.zzcsD;
        this.zzbTY = ads.zzcsE;
        this.zzbTZ = 0;
        this.zzcso = null;
        this.zzcsx = -1;
    }

    public final boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof hh)) {
            return false;
        }
        hh hhVar = (hh) obj;
        if (!Arrays.equals(this.zzbTM, hhVar.zzbTM)) {
            return false;
        }
        if (this.zzbTN == null) {
            if (hhVar.zzbTN != null) {
                return false;
            }
        } else if (!this.zzbTN.equals(hhVar.zzbTN)) {
            return false;
        }
        return Double.doubleToLongBits(this.zzbTO) != Double.doubleToLongBits(hhVar.zzbTO) ? false : Float.floatToIntBits(this.zzbTP) != Float.floatToIntBits(hhVar.zzbTP) ? false : this.zzbTQ != hhVar.zzbTQ ? false : this.zzbTR != hhVar.zzbTR ? false : this.zzbTS != hhVar.zzbTS ? false : this.zzbTT != hhVar.zzbTT ? false : !adn.equals(this.zzbTU, hhVar.zzbTU) ? false : !adn.equals(this.zzbTV, hhVar.zzbTV) ? false : !adn.equals(this.zzbTW, hhVar.zzbTW) ? false : !adn.equals(this.zzbTX, hhVar.zzbTX) ? false : !adn.equals(this.zzbTY, hhVar.zzbTY) ? false : this.zzbTZ != hhVar.zzbTZ ? false : (this.zzcso == null || this.zzcso.isEmpty()) ? hhVar.zzcso == null || hhVar.zzcso.isEmpty() : this.zzcso.equals(hhVar.zzcso);
    }

    public final int hashCode() {
        int i = 0;
        int hashCode = (this.zzbTN == null ? 0 : this.zzbTN.hashCode()) + ((((getClass().getName().hashCode() + 527) * 31) + Arrays.hashCode(this.zzbTM)) * 31);
        long doubleToLongBits = Double.doubleToLongBits(this.zzbTO);
        hashCode = ((((((((((((((this.zzbTT ? 1231 : 1237) + (((((((((((hashCode * 31) + ((int) (doubleToLongBits ^ (doubleToLongBits >>> 32)))) * 31) + Float.floatToIntBits(this.zzbTP)) * 31) + ((int) (this.zzbTQ ^ (this.zzbTQ >>> 32)))) * 31) + this.zzbTR) * 31) + this.zzbTS) * 31)) * 31) + adn.hashCode(this.zzbTU)) * 31) + adn.hashCode(this.zzbTV)) * 31) + adn.hashCode(this.zzbTW)) * 31) + adn.hashCode(this.zzbTX)) * 31) + adn.hashCode(this.zzbTY)) * 31) + ((int) (this.zzbTZ ^ (this.zzbTZ >>> 32)))) * 31;
        if (!(this.zzcso == null || this.zzcso.isEmpty())) {
            i = this.zzcso.hashCode();
        }
        return hashCode + i;
    }

    public final /* synthetic */ adp zza(adg com_google_android_gms_internal_adg) throws IOException {
        while (true) {
            int zzLA = com_google_android_gms_internal_adg.zzLA();
            int zzb;
            Object obj;
            int zzcn;
            switch (zzLA) {
                case 0:
                    break;
                case 10:
                    this.zzbTM = com_google_android_gms_internal_adg.readBytes();
                    continue;
                case 18:
                    this.zzbTN = com_google_android_gms_internal_adg.readString();
                    continue;
                case 25:
                    this.zzbTO = Double.longBitsToDouble(com_google_android_gms_internal_adg.zzLI());
                    continue;
                case 37:
                    this.zzbTP = Float.intBitsToFloat(com_google_android_gms_internal_adg.zzLH());
                    continue;
                case MotionEventCompat.AXIS_GENERIC_9 /*40*/:
                    this.zzbTQ = com_google_android_gms_internal_adg.zzLG();
                    continue;
                case 48:
                    this.zzbTR = com_google_android_gms_internal_adg.zzLF();
                    continue;
                case 56:
                    zzLA = com_google_android_gms_internal_adg.zzLF();
                    this.zzbTS = (-(zzLA & 1)) ^ (zzLA >>> 1);
                    continue;
                case 64:
                    this.zzbTT = com_google_android_gms_internal_adg.zzLD();
                    continue;
                case 74:
                    zzb = ads.zzb(com_google_android_gms_internal_adg, 74);
                    zzLA = this.zzbTU == null ? 0 : this.zzbTU.length;
                    obj = new hf[(zzb + zzLA)];
                    if (zzLA != 0) {
                        System.arraycopy(this.zzbTU, 0, obj, 0, zzLA);
                    }
                    while (zzLA < obj.length - 1) {
                        obj[zzLA] = new hf();
                        com_google_android_gms_internal_adg.zza(obj[zzLA]);
                        com_google_android_gms_internal_adg.zzLA();
                        zzLA++;
                    }
                    obj[zzLA] = new hf();
                    com_google_android_gms_internal_adg.zza(obj[zzLA]);
                    this.zzbTU = obj;
                    continue;
                case 82:
                    zzb = ads.zzb(com_google_android_gms_internal_adg, 82);
                    zzLA = this.zzbTV == null ? 0 : this.zzbTV.length;
                    obj = new hg[(zzb + zzLA)];
                    if (zzLA != 0) {
                        System.arraycopy(this.zzbTV, 0, obj, 0, zzLA);
                    }
                    while (zzLA < obj.length - 1) {
                        obj[zzLA] = new hg();
                        com_google_android_gms_internal_adg.zza(obj[zzLA]);
                        com_google_android_gms_internal_adg.zzLA();
                        zzLA++;
                    }
                    obj[zzLA] = new hg();
                    com_google_android_gms_internal_adg.zza(obj[zzLA]);
                    this.zzbTV = obj;
                    continue;
                case 90:
                    zzb = ads.zzb(com_google_android_gms_internal_adg, 90);
                    zzLA = this.zzbTW == null ? 0 : this.zzbTW.length;
                    obj = new String[(zzb + zzLA)];
                    if (zzLA != 0) {
                        System.arraycopy(this.zzbTW, 0, obj, 0, zzLA);
                    }
                    while (zzLA < obj.length - 1) {
                        obj[zzLA] = com_google_android_gms_internal_adg.readString();
                        com_google_android_gms_internal_adg.zzLA();
                        zzLA++;
                    }
                    obj[zzLA] = com_google_android_gms_internal_adg.readString();
                    this.zzbTW = obj;
                    continue;
                case 96:
                    zzb = ads.zzb(com_google_android_gms_internal_adg, 96);
                    zzLA = this.zzbTX == null ? 0 : this.zzbTX.length;
                    obj = new long[(zzb + zzLA)];
                    if (zzLA != 0) {
                        System.arraycopy(this.zzbTX, 0, obj, 0, zzLA);
                    }
                    while (zzLA < obj.length - 1) {
                        obj[zzLA] = com_google_android_gms_internal_adg.zzLG();
                        com_google_android_gms_internal_adg.zzLA();
                        zzLA++;
                    }
                    obj[zzLA] = com_google_android_gms_internal_adg.zzLG();
                    this.zzbTX = obj;
                    continue;
                case 98:
                    zzcn = com_google_android_gms_internal_adg.zzcn(com_google_android_gms_internal_adg.zzLF());
                    zzb = com_google_android_gms_internal_adg.getPosition();
                    zzLA = 0;
                    while (com_google_android_gms_internal_adg.zzLK() > 0) {
                        com_google_android_gms_internal_adg.zzLG();
                        zzLA++;
                    }
                    com_google_android_gms_internal_adg.zzcp(zzb);
                    zzb = this.zzbTX == null ? 0 : this.zzbTX.length;
                    Object obj2 = new long[(zzLA + zzb)];
                    if (zzb != 0) {
                        System.arraycopy(this.zzbTX, 0, obj2, 0, zzb);
                    }
                    while (zzb < obj2.length) {
                        obj2[zzb] = com_google_android_gms_internal_adg.zzLG();
                        zzb++;
                    }
                    this.zzbTX = obj2;
                    com_google_android_gms_internal_adg.zzco(zzcn);
                    continue;
                case 104:
                    this.zzbTZ = com_google_android_gms_internal_adg.zzLG();
                    continue;
                case 114:
                    zzLA = com_google_android_gms_internal_adg.zzLF();
                    zzb = com_google_android_gms_internal_adg.zzcn(zzLA);
                    zzcn = zzLA / 4;
                    zzLA = this.zzbTY == null ? 0 : this.zzbTY.length;
                    Object obj3 = new float[(zzcn + zzLA)];
                    if (zzLA != 0) {
                        System.arraycopy(this.zzbTY, 0, obj3, 0, zzLA);
                    }
                    while (zzLA < obj3.length) {
                        obj3[zzLA] = Float.intBitsToFloat(com_google_android_gms_internal_adg.zzLH());
                        zzLA++;
                    }
                    this.zzbTY = obj3;
                    com_google_android_gms_internal_adg.zzco(zzb);
                    continue;
                case 117:
                    zzb = ads.zzb(com_google_android_gms_internal_adg, 117);
                    zzLA = this.zzbTY == null ? 0 : this.zzbTY.length;
                    obj = new float[(zzb + zzLA)];
                    if (zzLA != 0) {
                        System.arraycopy(this.zzbTY, 0, obj, 0, zzLA);
                    }
                    while (zzLA < obj.length - 1) {
                        obj[zzLA] = Float.intBitsToFloat(com_google_android_gms_internal_adg.zzLH());
                        com_google_android_gms_internal_adg.zzLA();
                        zzLA++;
                    }
                    obj[zzLA] = Float.intBitsToFloat(com_google_android_gms_internal_adg.zzLH());
                    this.zzbTY = obj;
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
        if (!Arrays.equals(this.zzbTM, ads.zzcsI)) {
            com_google_android_gms_internal_adh.zzb(1, this.zzbTM);
        }
        if (!(this.zzbTN == null || this.zzbTN.equals(""))) {
            com_google_android_gms_internal_adh.zzl(2, this.zzbTN);
        }
        if (Double.doubleToLongBits(this.zzbTO) != Double.doubleToLongBits(0.0d)) {
            com_google_android_gms_internal_adh.zza(3, this.zzbTO);
        }
        if (Float.floatToIntBits(this.zzbTP) != Float.floatToIntBits(0.0f)) {
            com_google_android_gms_internal_adh.zzc(4, this.zzbTP);
        }
        if (this.zzbTQ != 0) {
            com_google_android_gms_internal_adh.zzb(5, this.zzbTQ);
        }
        if (this.zzbTR != 0) {
            com_google_android_gms_internal_adh.zzr(6, this.zzbTR);
        }
        if (this.zzbTS != 0) {
            int i2 = this.zzbTS;
            com_google_android_gms_internal_adh.zzt(7, 0);
            com_google_android_gms_internal_adh.zzcu(adh.zzcw(i2));
        }
        if (this.zzbTT) {
            com_google_android_gms_internal_adh.zzk(8, this.zzbTT);
        }
        if (this.zzbTU != null && this.zzbTU.length > 0) {
            for (adp com_google_android_gms_internal_adp : this.zzbTU) {
                if (com_google_android_gms_internal_adp != null) {
                    com_google_android_gms_internal_adh.zza(9, com_google_android_gms_internal_adp);
                }
            }
        }
        if (this.zzbTV != null && this.zzbTV.length > 0) {
            for (adp com_google_android_gms_internal_adp2 : this.zzbTV) {
                if (com_google_android_gms_internal_adp2 != null) {
                    com_google_android_gms_internal_adh.zza(10, com_google_android_gms_internal_adp2);
                }
            }
        }
        if (this.zzbTW != null && this.zzbTW.length > 0) {
            for (String str : this.zzbTW) {
                if (str != null) {
                    com_google_android_gms_internal_adh.zzl(11, str);
                }
            }
        }
        if (this.zzbTX != null && this.zzbTX.length > 0) {
            for (long zzb : this.zzbTX) {
                com_google_android_gms_internal_adh.zzb(12, zzb);
            }
        }
        if (this.zzbTZ != 0) {
            com_google_android_gms_internal_adh.zzb(13, this.zzbTZ);
        }
        if (this.zzbTY != null && this.zzbTY.length > 0) {
            while (i < this.zzbTY.length) {
                com_google_android_gms_internal_adh.zzc(14, this.zzbTY[i]);
                i++;
            }
        }
        super.zza(com_google_android_gms_internal_adh);
    }

    protected final int zzn() {
        int i;
        int i2 = 0;
        int zzn = super.zzn();
        if (!Arrays.equals(this.zzbTM, ads.zzcsI)) {
            zzn += adh.zzc(1, this.zzbTM);
        }
        if (!(this.zzbTN == null || this.zzbTN.equals(""))) {
            zzn += adh.zzm(2, this.zzbTN);
        }
        if (Double.doubleToLongBits(this.zzbTO) != Double.doubleToLongBits(0.0d)) {
            zzn += adh.zzct(3) + 8;
        }
        if (Float.floatToIntBits(this.zzbTP) != Float.floatToIntBits(0.0f)) {
            zzn += adh.zzct(4) + 4;
        }
        if (this.zzbTQ != 0) {
            zzn += adh.zze(5, this.zzbTQ);
        }
        if (this.zzbTR != 0) {
            zzn += adh.zzs(6, this.zzbTR);
        }
        if (this.zzbTS != 0) {
            zzn += adh.zzcv(adh.zzcw(this.zzbTS)) + adh.zzct(7);
        }
        if (this.zzbTT) {
            zzn += adh.zzct(8) + 1;
        }
        if (this.zzbTU != null && this.zzbTU.length > 0) {
            i = zzn;
            for (adp com_google_android_gms_internal_adp : this.zzbTU) {
                if (com_google_android_gms_internal_adp != null) {
                    i += adh.zzb(9, com_google_android_gms_internal_adp);
                }
            }
            zzn = i;
        }
        if (this.zzbTV != null && this.zzbTV.length > 0) {
            i = zzn;
            for (adp com_google_android_gms_internal_adp2 : this.zzbTV) {
                if (com_google_android_gms_internal_adp2 != null) {
                    i += adh.zzb(10, com_google_android_gms_internal_adp2);
                }
            }
            zzn = i;
        }
        if (this.zzbTW != null && this.zzbTW.length > 0) {
            int i3 = 0;
            int i4 = 0;
            for (String str : this.zzbTW) {
                if (str != null) {
                    i4++;
                    i3 += adh.zzhQ(str);
                }
            }
            zzn = (zzn + i3) + (i4 * 1);
        }
        if (this.zzbTX != null && this.zzbTX.length > 0) {
            i = 0;
            while (i2 < this.zzbTX.length) {
                i += adh.zzaP(this.zzbTX[i2]);
                i2++;
            }
            zzn = (zzn + i) + (this.zzbTX.length * 1);
        }
        if (this.zzbTZ != 0) {
            zzn += adh.zze(13, this.zzbTZ);
        }
        return (this.zzbTY == null || this.zzbTY.length <= 0) ? zzn : (zzn + (this.zzbTY.length * 4)) + (this.zzbTY.length * 1);
    }
}
