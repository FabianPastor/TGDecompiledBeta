package com.google.android.gms.internal;

import android.support.v4.view.MotionEventCompat;
import java.io.IOException;

public interface zzbyp {

    public static final class zza extends zzbyd<zza> {
        private static volatile zza[] zzcxF;
        public String zzcxG;

        public zza() {
            zzafR();
        }

        public static zza[] zzafQ() {
            if (zzcxF == null) {
                synchronized (zzbyh.zzcwK) {
                    if (zzcxF == null) {
                        zzcxF = new zza[0];
                    }
                }
            }
            return zzcxF;
        }

        public void zza(zzbyc com_google_android_gms_internal_zzbyc) throws IOException {
            if (!(this.zzcxG == null || this.zzcxG.equals(""))) {
                com_google_android_gms_internal_zzbyc.zzq(1, this.zzcxG);
            }
            super.zza(com_google_android_gms_internal_zzbyc);
        }

        public zza zzafR() {
            this.zzcxG = "";
            this.zzcwC = null;
            this.zzcwL = -1;
            return this;
        }

        public /* synthetic */ zzbyj zzb(zzbyb com_google_android_gms_internal_zzbyb) throws IOException {
            return zzbc(com_google_android_gms_internal_zzbyb);
        }

        public zza zzbc(zzbyb com_google_android_gms_internal_zzbyb) throws IOException {
            while (true) {
                int zzaeW = com_google_android_gms_internal_zzbyb.zzaeW();
                switch (zzaeW) {
                    case 0:
                        break;
                    case 10:
                        this.zzcxG = com_google_android_gms_internal_zzbyb.readString();
                        continue;
                    default:
                        if (!super.zza(com_google_android_gms_internal_zzbyb, zzaeW)) {
                            break;
                        }
                        continue;
                }
                return this;
            }
        }

        protected int zzu() {
            int zzu = super.zzu();
            return (this.zzcxG == null || this.zzcxG.equals("")) ? zzu : zzu + zzbyc.zzr(1, this.zzcxG);
        }
    }

    public static final class zzb extends zzbyd<zzb> {
        public String zzcxG;
        public String zzcxH;
        public long zzcxI;
        public String zzcxJ;
        public int zzcxK;
        public int zzcxL;
        public String zzcxM;
        public String zzcxN;
        public String zzcxO;
        public String zzcxP;
        public String zzcxQ;
        public int zzcxR;
        public zza[] zzcxS;

        public zzb() {
            zzafS();
        }

        public static zzb zzal(byte[] bArr) throws zzbyi {
            return (zzb) zzbyj.zza(new zzb(), bArr);
        }

        public void zza(zzbyc com_google_android_gms_internal_zzbyc) throws IOException {
            if (!(this.zzcxG == null || this.zzcxG.equals(""))) {
                com_google_android_gms_internal_zzbyc.zzq(1, this.zzcxG);
            }
            if (!(this.zzcxH == null || this.zzcxH.equals(""))) {
                com_google_android_gms_internal_zzbyc.zzq(2, this.zzcxH);
            }
            if (this.zzcxI != 0) {
                com_google_android_gms_internal_zzbyc.zzb(3, this.zzcxI);
            }
            if (!(this.zzcxJ == null || this.zzcxJ.equals(""))) {
                com_google_android_gms_internal_zzbyc.zzq(4, this.zzcxJ);
            }
            if (this.zzcxK != 0) {
                com_google_android_gms_internal_zzbyc.zzJ(5, this.zzcxK);
            }
            if (this.zzcxL != 0) {
                com_google_android_gms_internal_zzbyc.zzJ(6, this.zzcxL);
            }
            if (!(this.zzcxM == null || this.zzcxM.equals(""))) {
                com_google_android_gms_internal_zzbyc.zzq(7, this.zzcxM);
            }
            if (!(this.zzcxN == null || this.zzcxN.equals(""))) {
                com_google_android_gms_internal_zzbyc.zzq(8, this.zzcxN);
            }
            if (!(this.zzcxO == null || this.zzcxO.equals(""))) {
                com_google_android_gms_internal_zzbyc.zzq(9, this.zzcxO);
            }
            if (!(this.zzcxP == null || this.zzcxP.equals(""))) {
                com_google_android_gms_internal_zzbyc.zzq(10, this.zzcxP);
            }
            if (!(this.zzcxQ == null || this.zzcxQ.equals(""))) {
                com_google_android_gms_internal_zzbyc.zzq(11, this.zzcxQ);
            }
            if (this.zzcxR != 0) {
                com_google_android_gms_internal_zzbyc.zzJ(12, this.zzcxR);
            }
            if (this.zzcxS != null && this.zzcxS.length > 0) {
                for (zzbyj com_google_android_gms_internal_zzbyj : this.zzcxS) {
                    if (com_google_android_gms_internal_zzbyj != null) {
                        com_google_android_gms_internal_zzbyc.zza(13, com_google_android_gms_internal_zzbyj);
                    }
                }
            }
            super.zza(com_google_android_gms_internal_zzbyc);
        }

        public zzb zzafS() {
            this.zzcxG = "";
            this.zzcxH = "";
            this.zzcxI = 0;
            this.zzcxJ = "";
            this.zzcxK = 0;
            this.zzcxL = 0;
            this.zzcxM = "";
            this.zzcxN = "";
            this.zzcxO = "";
            this.zzcxP = "";
            this.zzcxQ = "";
            this.zzcxR = 0;
            this.zzcxS = zza.zzafQ();
            this.zzcwC = null;
            this.zzcwL = -1;
            return this;
        }

        public /* synthetic */ zzbyj zzb(zzbyb com_google_android_gms_internal_zzbyb) throws IOException {
            return zzbd(com_google_android_gms_internal_zzbyb);
        }

        public zzb zzbd(zzbyb com_google_android_gms_internal_zzbyb) throws IOException {
            while (true) {
                int zzaeW = com_google_android_gms_internal_zzbyb.zzaeW();
                switch (zzaeW) {
                    case 0:
                        break;
                    case 10:
                        this.zzcxG = com_google_android_gms_internal_zzbyb.readString();
                        continue;
                    case 18:
                        this.zzcxH = com_google_android_gms_internal_zzbyb.readString();
                        continue;
                    case 24:
                        this.zzcxI = com_google_android_gms_internal_zzbyb.zzaeZ();
                        continue;
                    case 34:
                        this.zzcxJ = com_google_android_gms_internal_zzbyb.readString();
                        continue;
                    case MotionEventCompat.AXIS_GENERIC_9 /*40*/:
                        this.zzcxK = com_google_android_gms_internal_zzbyb.zzafa();
                        continue;
                    case 48:
                        this.zzcxL = com_google_android_gms_internal_zzbyb.zzafa();
                        continue;
                    case 58:
                        this.zzcxM = com_google_android_gms_internal_zzbyb.readString();
                        continue;
                    case 66:
                        this.zzcxN = com_google_android_gms_internal_zzbyb.readString();
                        continue;
                    case 74:
                        this.zzcxO = com_google_android_gms_internal_zzbyb.readString();
                        continue;
                    case 82:
                        this.zzcxP = com_google_android_gms_internal_zzbyb.readString();
                        continue;
                    case 90:
                        this.zzcxQ = com_google_android_gms_internal_zzbyb.readString();
                        continue;
                    case 96:
                        zzaeW = com_google_android_gms_internal_zzbyb.zzafa();
                        switch (zzaeW) {
                            case 0:
                            case 1:
                            case 2:
                                this.zzcxR = zzaeW;
                                break;
                            default:
                                continue;
                        }
                    case 106:
                        int zzb = zzbym.zzb(com_google_android_gms_internal_zzbyb, 106);
                        zzaeW = this.zzcxS == null ? 0 : this.zzcxS.length;
                        Object obj = new zza[(zzb + zzaeW)];
                        if (zzaeW != 0) {
                            System.arraycopy(this.zzcxS, 0, obj, 0, zzaeW);
                        }
                        while (zzaeW < obj.length - 1) {
                            obj[zzaeW] = new zza();
                            com_google_android_gms_internal_zzbyb.zza(obj[zzaeW]);
                            com_google_android_gms_internal_zzbyb.zzaeW();
                            zzaeW++;
                        }
                        obj[zzaeW] = new zza();
                        com_google_android_gms_internal_zzbyb.zza(obj[zzaeW]);
                        this.zzcxS = obj;
                        continue;
                    default:
                        if (!super.zza(com_google_android_gms_internal_zzbyb, zzaeW)) {
                            break;
                        }
                        continue;
                }
                return this;
            }
        }

        protected int zzu() {
            int zzu = super.zzu();
            if (!(this.zzcxG == null || this.zzcxG.equals(""))) {
                zzu += zzbyc.zzr(1, this.zzcxG);
            }
            if (!(this.zzcxH == null || this.zzcxH.equals(""))) {
                zzu += zzbyc.zzr(2, this.zzcxH);
            }
            if (this.zzcxI != 0) {
                zzu += zzbyc.zzf(3, this.zzcxI);
            }
            if (!(this.zzcxJ == null || this.zzcxJ.equals(""))) {
                zzu += zzbyc.zzr(4, this.zzcxJ);
            }
            if (this.zzcxK != 0) {
                zzu += zzbyc.zzL(5, this.zzcxK);
            }
            if (this.zzcxL != 0) {
                zzu += zzbyc.zzL(6, this.zzcxL);
            }
            if (!(this.zzcxM == null || this.zzcxM.equals(""))) {
                zzu += zzbyc.zzr(7, this.zzcxM);
            }
            if (!(this.zzcxN == null || this.zzcxN.equals(""))) {
                zzu += zzbyc.zzr(8, this.zzcxN);
            }
            if (!(this.zzcxO == null || this.zzcxO.equals(""))) {
                zzu += zzbyc.zzr(9, this.zzcxO);
            }
            if (!(this.zzcxP == null || this.zzcxP.equals(""))) {
                zzu += zzbyc.zzr(10, this.zzcxP);
            }
            if (!(this.zzcxQ == null || this.zzcxQ.equals(""))) {
                zzu += zzbyc.zzr(11, this.zzcxQ);
            }
            if (this.zzcxR != 0) {
                zzu += zzbyc.zzL(12, this.zzcxR);
            }
            if (this.zzcxS == null || this.zzcxS.length <= 0) {
                return zzu;
            }
            int i = zzu;
            for (zzbyj com_google_android_gms_internal_zzbyj : this.zzcxS) {
                if (com_google_android_gms_internal_zzbyj != null) {
                    i += zzbyc.zzc(13, com_google_android_gms_internal_zzbyj);
                }
            }
            return i;
        }
    }
}
