package com.google.android.gms.internal;

import android.support.v4.view.MotionEventCompat;
import java.io.IOException;

public interface zzbxz {

    public static final class zza extends zzbxn<zza> {
        private static volatile zza[] zzcvG;
        public String zzcvH;

        public zza() {
            zzafi();
        }

        public static zza[] zzafh() {
            if (zzcvG == null) {
                synchronized (zzbxr.zzcuI) {
                    if (zzcvG == null) {
                        zzcvG = new zza[0];
                    }
                }
            }
            return zzcvG;
        }

        public void zza(zzbxm com_google_android_gms_internal_zzbxm) throws IOException {
            if (!(this.zzcvH == null || this.zzcvH.equals(""))) {
                com_google_android_gms_internal_zzbxm.zzq(1, this.zzcvH);
            }
            super.zza(com_google_android_gms_internal_zzbxm);
        }

        public zza zzaV(zzbxl com_google_android_gms_internal_zzbxl) throws IOException {
            while (true) {
                int zzaen = com_google_android_gms_internal_zzbxl.zzaen();
                switch (zzaen) {
                    case 0:
                        break;
                    case 10:
                        this.zzcvH = com_google_android_gms_internal_zzbxl.readString();
                        continue;
                    default:
                        if (!super.zza(com_google_android_gms_internal_zzbxl, zzaen)) {
                            break;
                        }
                        continue;
                }
                return this;
            }
        }

        public zza zzafi() {
            this.zzcvH = "";
            this.zzcuA = null;
            this.zzcuJ = -1;
            return this;
        }

        public /* synthetic */ zzbxt zzb(zzbxl com_google_android_gms_internal_zzbxl) throws IOException {
            return zzaV(com_google_android_gms_internal_zzbxl);
        }

        protected int zzu() {
            int zzu = super.zzu();
            return (this.zzcvH == null || this.zzcvH.equals("")) ? zzu : zzu + zzbxm.zzr(1, this.zzcvH);
        }
    }

    public static final class zzb extends zzbxn<zzb> {
        public String zzcvH;
        public String zzcvI;
        public long zzcvJ;
        public String zzcvK;
        public int zzcvL;
        public int zzcvM;
        public String zzcvN;
        public String zzcvO;
        public String zzcvP;
        public String zzcvQ;
        public String zzcvR;
        public int zzcvS;
        public zza[] zzcvT;

        public zzb() {
            zzafj();
        }

        public static zzb zzak(byte[] bArr) throws zzbxs {
            return (zzb) zzbxt.zza(new zzb(), bArr);
        }

        public void zza(zzbxm com_google_android_gms_internal_zzbxm) throws IOException {
            if (!(this.zzcvH == null || this.zzcvH.equals(""))) {
                com_google_android_gms_internal_zzbxm.zzq(1, this.zzcvH);
            }
            if (!(this.zzcvI == null || this.zzcvI.equals(""))) {
                com_google_android_gms_internal_zzbxm.zzq(2, this.zzcvI);
            }
            if (this.zzcvJ != 0) {
                com_google_android_gms_internal_zzbxm.zzb(3, this.zzcvJ);
            }
            if (!(this.zzcvK == null || this.zzcvK.equals(""))) {
                com_google_android_gms_internal_zzbxm.zzq(4, this.zzcvK);
            }
            if (this.zzcvL != 0) {
                com_google_android_gms_internal_zzbxm.zzJ(5, this.zzcvL);
            }
            if (this.zzcvM != 0) {
                com_google_android_gms_internal_zzbxm.zzJ(6, this.zzcvM);
            }
            if (!(this.zzcvN == null || this.zzcvN.equals(""))) {
                com_google_android_gms_internal_zzbxm.zzq(7, this.zzcvN);
            }
            if (!(this.zzcvO == null || this.zzcvO.equals(""))) {
                com_google_android_gms_internal_zzbxm.zzq(8, this.zzcvO);
            }
            if (!(this.zzcvP == null || this.zzcvP.equals(""))) {
                com_google_android_gms_internal_zzbxm.zzq(9, this.zzcvP);
            }
            if (!(this.zzcvQ == null || this.zzcvQ.equals(""))) {
                com_google_android_gms_internal_zzbxm.zzq(10, this.zzcvQ);
            }
            if (!(this.zzcvR == null || this.zzcvR.equals(""))) {
                com_google_android_gms_internal_zzbxm.zzq(11, this.zzcvR);
            }
            if (this.zzcvS != 0) {
                com_google_android_gms_internal_zzbxm.zzJ(12, this.zzcvS);
            }
            if (this.zzcvT != null && this.zzcvT.length > 0) {
                for (zzbxt com_google_android_gms_internal_zzbxt : this.zzcvT) {
                    if (com_google_android_gms_internal_zzbxt != null) {
                        com_google_android_gms_internal_zzbxm.zza(13, com_google_android_gms_internal_zzbxt);
                    }
                }
            }
            super.zza(com_google_android_gms_internal_zzbxm);
        }

        public zzb zzaW(zzbxl com_google_android_gms_internal_zzbxl) throws IOException {
            while (true) {
                int zzaen = com_google_android_gms_internal_zzbxl.zzaen();
                switch (zzaen) {
                    case 0:
                        break;
                    case 10:
                        this.zzcvH = com_google_android_gms_internal_zzbxl.readString();
                        continue;
                    case 18:
                        this.zzcvI = com_google_android_gms_internal_zzbxl.readString();
                        continue;
                    case 24:
                        this.zzcvJ = com_google_android_gms_internal_zzbxl.zzaeq();
                        continue;
                    case 34:
                        this.zzcvK = com_google_android_gms_internal_zzbxl.readString();
                        continue;
                    case MotionEventCompat.AXIS_GENERIC_9 /*40*/:
                        this.zzcvL = com_google_android_gms_internal_zzbxl.zzaer();
                        continue;
                    case 48:
                        this.zzcvM = com_google_android_gms_internal_zzbxl.zzaer();
                        continue;
                    case 58:
                        this.zzcvN = com_google_android_gms_internal_zzbxl.readString();
                        continue;
                    case 66:
                        this.zzcvO = com_google_android_gms_internal_zzbxl.readString();
                        continue;
                    case 74:
                        this.zzcvP = com_google_android_gms_internal_zzbxl.readString();
                        continue;
                    case 82:
                        this.zzcvQ = com_google_android_gms_internal_zzbxl.readString();
                        continue;
                    case 90:
                        this.zzcvR = com_google_android_gms_internal_zzbxl.readString();
                        continue;
                    case 96:
                        zzaen = com_google_android_gms_internal_zzbxl.zzaer();
                        switch (zzaen) {
                            case 0:
                            case 1:
                            case 2:
                                this.zzcvS = zzaen;
                                break;
                            default:
                                continue;
                        }
                    case 106:
                        int zzb = zzbxw.zzb(com_google_android_gms_internal_zzbxl, 106);
                        zzaen = this.zzcvT == null ? 0 : this.zzcvT.length;
                        Object obj = new zza[(zzb + zzaen)];
                        if (zzaen != 0) {
                            System.arraycopy(this.zzcvT, 0, obj, 0, zzaen);
                        }
                        while (zzaen < obj.length - 1) {
                            obj[zzaen] = new zza();
                            com_google_android_gms_internal_zzbxl.zza(obj[zzaen]);
                            com_google_android_gms_internal_zzbxl.zzaen();
                            zzaen++;
                        }
                        obj[zzaen] = new zza();
                        com_google_android_gms_internal_zzbxl.zza(obj[zzaen]);
                        this.zzcvT = obj;
                        continue;
                    default:
                        if (!super.zza(com_google_android_gms_internal_zzbxl, zzaen)) {
                            break;
                        }
                        continue;
                }
                return this;
            }
        }

        public zzb zzafj() {
            this.zzcvH = "";
            this.zzcvI = "";
            this.zzcvJ = 0;
            this.zzcvK = "";
            this.zzcvL = 0;
            this.zzcvM = 0;
            this.zzcvN = "";
            this.zzcvO = "";
            this.zzcvP = "";
            this.zzcvQ = "";
            this.zzcvR = "";
            this.zzcvS = 0;
            this.zzcvT = zza.zzafh();
            this.zzcuA = null;
            this.zzcuJ = -1;
            return this;
        }

        public /* synthetic */ zzbxt zzb(zzbxl com_google_android_gms_internal_zzbxl) throws IOException {
            return zzaW(com_google_android_gms_internal_zzbxl);
        }

        protected int zzu() {
            int zzu = super.zzu();
            if (!(this.zzcvH == null || this.zzcvH.equals(""))) {
                zzu += zzbxm.zzr(1, this.zzcvH);
            }
            if (!(this.zzcvI == null || this.zzcvI.equals(""))) {
                zzu += zzbxm.zzr(2, this.zzcvI);
            }
            if (this.zzcvJ != 0) {
                zzu += zzbxm.zzf(3, this.zzcvJ);
            }
            if (!(this.zzcvK == null || this.zzcvK.equals(""))) {
                zzu += zzbxm.zzr(4, this.zzcvK);
            }
            if (this.zzcvL != 0) {
                zzu += zzbxm.zzL(5, this.zzcvL);
            }
            if (this.zzcvM != 0) {
                zzu += zzbxm.zzL(6, this.zzcvM);
            }
            if (!(this.zzcvN == null || this.zzcvN.equals(""))) {
                zzu += zzbxm.zzr(7, this.zzcvN);
            }
            if (!(this.zzcvO == null || this.zzcvO.equals(""))) {
                zzu += zzbxm.zzr(8, this.zzcvO);
            }
            if (!(this.zzcvP == null || this.zzcvP.equals(""))) {
                zzu += zzbxm.zzr(9, this.zzcvP);
            }
            if (!(this.zzcvQ == null || this.zzcvQ.equals(""))) {
                zzu += zzbxm.zzr(10, this.zzcvQ);
            }
            if (!(this.zzcvR == null || this.zzcvR.equals(""))) {
                zzu += zzbxm.zzr(11, this.zzcvR);
            }
            if (this.zzcvS != 0) {
                zzu += zzbxm.zzL(12, this.zzcvS);
            }
            if (this.zzcvT == null || this.zzcvT.length <= 0) {
                return zzu;
            }
            int i = zzu;
            for (zzbxt com_google_android_gms_internal_zzbxt : this.zzcvT) {
                if (com_google_android_gms_internal_zzbxt != null) {
                    i += zzbxm.zzc(13, com_google_android_gms_internal_zzbxt);
                }
            }
            return i;
        }
    }
}
