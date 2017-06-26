package com.google.android.gms.internal;

import android.support.v4.view.MotionEventCompat;
import java.io.IOException;
import java.util.Arrays;

public final class hg extends ada<hg> {
    public byte[] zzbTK;
    public String zzbTL;
    public double zzbTM;
    public float zzbTN;
    public long zzbTO;
    public int zzbTP;
    public int zzbTQ;
    public boolean zzbTR;
    public he[] zzbTS;
    public hf[] zzbTT;
    public String[] zzbTU;
    public long[] zzbTV;
    public float[] zzbTW;
    public long zzbTX;

    public hg() {
        this.zzbTK = adj.zzcst;
        this.zzbTL = "";
        this.zzbTM = 0.0d;
        this.zzbTN = 0.0f;
        this.zzbTO = 0;
        this.zzbTP = 0;
        this.zzbTQ = 0;
        this.zzbTR = false;
        this.zzbTS = he.zzDZ();
        this.zzbTT = hf.zzEa();
        this.zzbTU = adj.EMPTY_STRING_ARRAY;
        this.zzbTV = adj.zzcso;
        this.zzbTW = adj.zzcsp;
        this.zzbTX = 0;
        this.zzcrZ = null;
        this.zzcsi = -1;
    }

    public final boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof hg)) {
            return false;
        }
        hg hgVar = (hg) obj;
        if (!Arrays.equals(this.zzbTK, hgVar.zzbTK)) {
            return false;
        }
        if (this.zzbTL == null) {
            if (hgVar.zzbTL != null) {
                return false;
            }
        } else if (!this.zzbTL.equals(hgVar.zzbTL)) {
            return false;
        }
        return Double.doubleToLongBits(this.zzbTM) != Double.doubleToLongBits(hgVar.zzbTM) ? false : Float.floatToIntBits(this.zzbTN) != Float.floatToIntBits(hgVar.zzbTN) ? false : this.zzbTO != hgVar.zzbTO ? false : this.zzbTP != hgVar.zzbTP ? false : this.zzbTQ != hgVar.zzbTQ ? false : this.zzbTR != hgVar.zzbTR ? false : !ade.equals(this.zzbTS, hgVar.zzbTS) ? false : !ade.equals(this.zzbTT, hgVar.zzbTT) ? false : !ade.equals(this.zzbTU, hgVar.zzbTU) ? false : !ade.equals(this.zzbTV, hgVar.zzbTV) ? false : !ade.equals(this.zzbTW, hgVar.zzbTW) ? false : this.zzbTX != hgVar.zzbTX ? false : (this.zzcrZ == null || this.zzcrZ.isEmpty()) ? hgVar.zzcrZ == null || hgVar.zzcrZ.isEmpty() : this.zzcrZ.equals(hgVar.zzcrZ);
    }

    public final int hashCode() {
        int i = 0;
        int hashCode = (this.zzbTL == null ? 0 : this.zzbTL.hashCode()) + ((((getClass().getName().hashCode() + 527) * 31) + Arrays.hashCode(this.zzbTK)) * 31);
        long doubleToLongBits = Double.doubleToLongBits(this.zzbTM);
        hashCode = ((((((((((((((this.zzbTR ? 1231 : 1237) + (((((((((((hashCode * 31) + ((int) (doubleToLongBits ^ (doubleToLongBits >>> 32)))) * 31) + Float.floatToIntBits(this.zzbTN)) * 31) + ((int) (this.zzbTO ^ (this.zzbTO >>> 32)))) * 31) + this.zzbTP) * 31) + this.zzbTQ) * 31)) * 31) + ade.hashCode(this.zzbTS)) * 31) + ade.hashCode(this.zzbTT)) * 31) + ade.hashCode(this.zzbTU)) * 31) + ade.hashCode(this.zzbTV)) * 31) + ade.hashCode(this.zzbTW)) * 31) + ((int) (this.zzbTX ^ (this.zzbTX >>> 32)))) * 31;
        if (!(this.zzcrZ == null || this.zzcrZ.isEmpty())) {
            i = this.zzcrZ.hashCode();
        }
        return hashCode + i;
    }

    public final /* synthetic */ adg zza(acx com_google_android_gms_internal_acx) throws IOException {
        while (true) {
            int zzLy = com_google_android_gms_internal_acx.zzLy();
            int zzb;
            Object obj;
            int zzcn;
            switch (zzLy) {
                case 0:
                    break;
                case 10:
                    this.zzbTK = com_google_android_gms_internal_acx.readBytes();
                    continue;
                case 18:
                    this.zzbTL = com_google_android_gms_internal_acx.readString();
                    continue;
                case 25:
                    this.zzbTM = Double.longBitsToDouble(com_google_android_gms_internal_acx.zzLG());
                    continue;
                case 37:
                    this.zzbTN = Float.intBitsToFloat(com_google_android_gms_internal_acx.zzLF());
                    continue;
                case MotionEventCompat.AXIS_GENERIC_9 /*40*/:
                    this.zzbTO = com_google_android_gms_internal_acx.zzLE();
                    continue;
                case 48:
                    this.zzbTP = com_google_android_gms_internal_acx.zzLD();
                    continue;
                case 56:
                    zzLy = com_google_android_gms_internal_acx.zzLD();
                    this.zzbTQ = (-(zzLy & 1)) ^ (zzLy >>> 1);
                    continue;
                case 64:
                    this.zzbTR = com_google_android_gms_internal_acx.zzLB();
                    continue;
                case 74:
                    zzb = adj.zzb(com_google_android_gms_internal_acx, 74);
                    zzLy = this.zzbTS == null ? 0 : this.zzbTS.length;
                    obj = new he[(zzb + zzLy)];
                    if (zzLy != 0) {
                        System.arraycopy(this.zzbTS, 0, obj, 0, zzLy);
                    }
                    while (zzLy < obj.length - 1) {
                        obj[zzLy] = new he();
                        com_google_android_gms_internal_acx.zza(obj[zzLy]);
                        com_google_android_gms_internal_acx.zzLy();
                        zzLy++;
                    }
                    obj[zzLy] = new he();
                    com_google_android_gms_internal_acx.zza(obj[zzLy]);
                    this.zzbTS = obj;
                    continue;
                case 82:
                    zzb = adj.zzb(com_google_android_gms_internal_acx, 82);
                    zzLy = this.zzbTT == null ? 0 : this.zzbTT.length;
                    obj = new hf[(zzb + zzLy)];
                    if (zzLy != 0) {
                        System.arraycopy(this.zzbTT, 0, obj, 0, zzLy);
                    }
                    while (zzLy < obj.length - 1) {
                        obj[zzLy] = new hf();
                        com_google_android_gms_internal_acx.zza(obj[zzLy]);
                        com_google_android_gms_internal_acx.zzLy();
                        zzLy++;
                    }
                    obj[zzLy] = new hf();
                    com_google_android_gms_internal_acx.zza(obj[zzLy]);
                    this.zzbTT = obj;
                    continue;
                case 90:
                    zzb = adj.zzb(com_google_android_gms_internal_acx, 90);
                    zzLy = this.zzbTU == null ? 0 : this.zzbTU.length;
                    obj = new String[(zzb + zzLy)];
                    if (zzLy != 0) {
                        System.arraycopy(this.zzbTU, 0, obj, 0, zzLy);
                    }
                    while (zzLy < obj.length - 1) {
                        obj[zzLy] = com_google_android_gms_internal_acx.readString();
                        com_google_android_gms_internal_acx.zzLy();
                        zzLy++;
                    }
                    obj[zzLy] = com_google_android_gms_internal_acx.readString();
                    this.zzbTU = obj;
                    continue;
                case 96:
                    zzb = adj.zzb(com_google_android_gms_internal_acx, 96);
                    zzLy = this.zzbTV == null ? 0 : this.zzbTV.length;
                    obj = new long[(zzb + zzLy)];
                    if (zzLy != 0) {
                        System.arraycopy(this.zzbTV, 0, obj, 0, zzLy);
                    }
                    while (zzLy < obj.length - 1) {
                        obj[zzLy] = com_google_android_gms_internal_acx.zzLE();
                        com_google_android_gms_internal_acx.zzLy();
                        zzLy++;
                    }
                    obj[zzLy] = com_google_android_gms_internal_acx.zzLE();
                    this.zzbTV = obj;
                    continue;
                case 98:
                    zzcn = com_google_android_gms_internal_acx.zzcn(com_google_android_gms_internal_acx.zzLD());
                    zzb = com_google_android_gms_internal_acx.getPosition();
                    zzLy = 0;
                    while (com_google_android_gms_internal_acx.zzLI() > 0) {
                        com_google_android_gms_internal_acx.zzLE();
                        zzLy++;
                    }
                    com_google_android_gms_internal_acx.zzcp(zzb);
                    zzb = this.zzbTV == null ? 0 : this.zzbTV.length;
                    Object obj2 = new long[(zzLy + zzb)];
                    if (zzb != 0) {
                        System.arraycopy(this.zzbTV, 0, obj2, 0, zzb);
                    }
                    while (zzb < obj2.length) {
                        obj2[zzb] = com_google_android_gms_internal_acx.zzLE();
                        zzb++;
                    }
                    this.zzbTV = obj2;
                    com_google_android_gms_internal_acx.zzco(zzcn);
                    continue;
                case 104:
                    this.zzbTX = com_google_android_gms_internal_acx.zzLE();
                    continue;
                case 114:
                    zzLy = com_google_android_gms_internal_acx.zzLD();
                    zzb = com_google_android_gms_internal_acx.zzcn(zzLy);
                    zzcn = zzLy / 4;
                    zzLy = this.zzbTW == null ? 0 : this.zzbTW.length;
                    Object obj3 = new float[(zzcn + zzLy)];
                    if (zzLy != 0) {
                        System.arraycopy(this.zzbTW, 0, obj3, 0, zzLy);
                    }
                    while (zzLy < obj3.length) {
                        obj3[zzLy] = Float.intBitsToFloat(com_google_android_gms_internal_acx.zzLF());
                        zzLy++;
                    }
                    this.zzbTW = obj3;
                    com_google_android_gms_internal_acx.zzco(zzb);
                    continue;
                case 117:
                    zzb = adj.zzb(com_google_android_gms_internal_acx, 117);
                    zzLy = this.zzbTW == null ? 0 : this.zzbTW.length;
                    obj = new float[(zzb + zzLy)];
                    if (zzLy != 0) {
                        System.arraycopy(this.zzbTW, 0, obj, 0, zzLy);
                    }
                    while (zzLy < obj.length - 1) {
                        obj[zzLy] = Float.intBitsToFloat(com_google_android_gms_internal_acx.zzLF());
                        com_google_android_gms_internal_acx.zzLy();
                        zzLy++;
                    }
                    obj[zzLy] = Float.intBitsToFloat(com_google_android_gms_internal_acx.zzLF());
                    this.zzbTW = obj;
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
        if (!Arrays.equals(this.zzbTK, adj.zzcst)) {
            com_google_android_gms_internal_acy.zzb(1, this.zzbTK);
        }
        if (!(this.zzbTL == null || this.zzbTL.equals(""))) {
            com_google_android_gms_internal_acy.zzl(2, this.zzbTL);
        }
        if (Double.doubleToLongBits(this.zzbTM) != Double.doubleToLongBits(0.0d)) {
            com_google_android_gms_internal_acy.zza(3, this.zzbTM);
        }
        if (Float.floatToIntBits(this.zzbTN) != Float.floatToIntBits(0.0f)) {
            com_google_android_gms_internal_acy.zzc(4, this.zzbTN);
        }
        if (this.zzbTO != 0) {
            com_google_android_gms_internal_acy.zzb(5, this.zzbTO);
        }
        if (this.zzbTP != 0) {
            com_google_android_gms_internal_acy.zzr(6, this.zzbTP);
        }
        if (this.zzbTQ != 0) {
            int i2 = this.zzbTQ;
            com_google_android_gms_internal_acy.zzt(7, 0);
            com_google_android_gms_internal_acy.zzcu(acy.zzcw(i2));
        }
        if (this.zzbTR) {
            com_google_android_gms_internal_acy.zzk(8, this.zzbTR);
        }
        if (this.zzbTS != null && this.zzbTS.length > 0) {
            for (adg com_google_android_gms_internal_adg : this.zzbTS) {
                if (com_google_android_gms_internal_adg != null) {
                    com_google_android_gms_internal_acy.zza(9, com_google_android_gms_internal_adg);
                }
            }
        }
        if (this.zzbTT != null && this.zzbTT.length > 0) {
            for (adg com_google_android_gms_internal_adg2 : this.zzbTT) {
                if (com_google_android_gms_internal_adg2 != null) {
                    com_google_android_gms_internal_acy.zza(10, com_google_android_gms_internal_adg2);
                }
            }
        }
        if (this.zzbTU != null && this.zzbTU.length > 0) {
            for (String str : this.zzbTU) {
                if (str != null) {
                    com_google_android_gms_internal_acy.zzl(11, str);
                }
            }
        }
        if (this.zzbTV != null && this.zzbTV.length > 0) {
            for (long zzb : this.zzbTV) {
                com_google_android_gms_internal_acy.zzb(12, zzb);
            }
        }
        if (this.zzbTX != 0) {
            com_google_android_gms_internal_acy.zzb(13, this.zzbTX);
        }
        if (this.zzbTW != null && this.zzbTW.length > 0) {
            while (i < this.zzbTW.length) {
                com_google_android_gms_internal_acy.zzc(14, this.zzbTW[i]);
                i++;
            }
        }
        super.zza(com_google_android_gms_internal_acy);
    }

    protected final int zzn() {
        int i;
        int i2 = 0;
        int zzn = super.zzn();
        if (!Arrays.equals(this.zzbTK, adj.zzcst)) {
            zzn += acy.zzc(1, this.zzbTK);
        }
        if (!(this.zzbTL == null || this.zzbTL.equals(""))) {
            zzn += acy.zzm(2, this.zzbTL);
        }
        if (Double.doubleToLongBits(this.zzbTM) != Double.doubleToLongBits(0.0d)) {
            zzn += acy.zzct(3) + 8;
        }
        if (Float.floatToIntBits(this.zzbTN) != Float.floatToIntBits(0.0f)) {
            zzn += acy.zzct(4) + 4;
        }
        if (this.zzbTO != 0) {
            zzn += acy.zze(5, this.zzbTO);
        }
        if (this.zzbTP != 0) {
            zzn += acy.zzs(6, this.zzbTP);
        }
        if (this.zzbTQ != 0) {
            zzn += acy.zzcv(acy.zzcw(this.zzbTQ)) + acy.zzct(7);
        }
        if (this.zzbTR) {
            zzn += acy.zzct(8) + 1;
        }
        if (this.zzbTS != null && this.zzbTS.length > 0) {
            i = zzn;
            for (adg com_google_android_gms_internal_adg : this.zzbTS) {
                if (com_google_android_gms_internal_adg != null) {
                    i += acy.zzb(9, com_google_android_gms_internal_adg);
                }
            }
            zzn = i;
        }
        if (this.zzbTT != null && this.zzbTT.length > 0) {
            i = zzn;
            for (adg com_google_android_gms_internal_adg2 : this.zzbTT) {
                if (com_google_android_gms_internal_adg2 != null) {
                    i += acy.zzb(10, com_google_android_gms_internal_adg2);
                }
            }
            zzn = i;
        }
        if (this.zzbTU != null && this.zzbTU.length > 0) {
            int i3 = 0;
            int i4 = 0;
            for (String str : this.zzbTU) {
                if (str != null) {
                    i4++;
                    i3 += acy.zzhQ(str);
                }
            }
            zzn = (zzn + i3) + (i4 * 1);
        }
        if (this.zzbTV != null && this.zzbTV.length > 0) {
            i = 0;
            while (i2 < this.zzbTV.length) {
                i += acy.zzaP(this.zzbTV[i2]);
                i2++;
            }
            zzn = (zzn + i) + (this.zzbTV.length * 1);
        }
        if (this.zzbTX != 0) {
            zzn += acy.zze(13, this.zzbTX);
        }
        return (this.zzbTW == null || this.zzbTW.length <= 0) ? zzn : (zzn + (this.zzbTW.length * 4)) + (this.zzbTW.length * 1);
    }
}
