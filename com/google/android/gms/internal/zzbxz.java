package com.google.android.gms.internal;

import android.support.v4.view.MotionEventCompat;
import java.io.IOException;
import org.telegram.tgnet.TLRPC;

public interface zzbxz {

    public static final class zza extends zzbxn<zza> {
        private static volatile zza[] zzcvO;
        public String zzcvP;

        public zza() {
            zzafj();
        }

        public static zza[] zzafi() {
            if (zzcvO == null) {
                synchronized (zzbxr.zzcuQ) {
                    if (zzcvO == null) {
                        zzcvO = new zza[0];
                    }
                }
            }
            return zzcvO;
        }

        public void zza(zzbxm com_google_android_gms_internal_zzbxm) throws IOException {
            if (!(this.zzcvP == null || this.zzcvP.equals(""))) {
                com_google_android_gms_internal_zzbxm.zzq(1, this.zzcvP);
            }
            super.zza(com_google_android_gms_internal_zzbxm);
        }

        public zza zzaV(zzbxl com_google_android_gms_internal_zzbxl) throws IOException {
            while (true) {
                int zzaeo = com_google_android_gms_internal_zzbxl.zzaeo();
                switch (zzaeo) {
                    case 0:
                        break;
                    case 10:
                        this.zzcvP = com_google_android_gms_internal_zzbxl.readString();
                        continue;
                    default:
                        if (!super.zza(com_google_android_gms_internal_zzbxl, zzaeo)) {
                            break;
                        }
                        continue;
                }
                return this;
            }
        }

        public zza zzafj() {
            this.zzcvP = "";
            this.zzcuI = null;
            this.zzcuR = -1;
            return this;
        }

        public /* synthetic */ zzbxt zzb(zzbxl com_google_android_gms_internal_zzbxl) throws IOException {
            return zzaV(com_google_android_gms_internal_zzbxl);
        }

        protected int zzu() {
            int zzu = super.zzu();
            return (this.zzcvP == null || this.zzcvP.equals("")) ? zzu : zzu + zzbxm.zzr(1, this.zzcvP);
        }
    }

    public static final class zzb extends zzbxn<zzb> {
        public String zzcvP;
        public String zzcvQ;
        public long zzcvR;
        public String zzcvS;
        public int zzcvT;
        public int zzcvU;
        public String zzcvV;
        public String zzcvW;
        public String zzcvX;
        public String zzcvY;
        public String zzcvZ;
        public int zzcwa;
        public zza[] zzcwb;

        public zzb() {
            zzafk();
        }

        public static zzb zzak(byte[] bArr) throws zzbxs {
            return (zzb) zzbxt.zza(new zzb(), bArr);
        }

        public void zza(zzbxm com_google_android_gms_internal_zzbxm) throws IOException {
            if (!(this.zzcvP == null || this.zzcvP.equals(""))) {
                com_google_android_gms_internal_zzbxm.zzq(1, this.zzcvP);
            }
            if (!(this.zzcvQ == null || this.zzcvQ.equals(""))) {
                com_google_android_gms_internal_zzbxm.zzq(2, this.zzcvQ);
            }
            if (this.zzcvR != 0) {
                com_google_android_gms_internal_zzbxm.zzb(3, this.zzcvR);
            }
            if (!(this.zzcvS == null || this.zzcvS.equals(""))) {
                com_google_android_gms_internal_zzbxm.zzq(4, this.zzcvS);
            }
            if (this.zzcvT != 0) {
                com_google_android_gms_internal_zzbxm.zzJ(5, this.zzcvT);
            }
            if (this.zzcvU != 0) {
                com_google_android_gms_internal_zzbxm.zzJ(6, this.zzcvU);
            }
            if (!(this.zzcvV == null || this.zzcvV.equals(""))) {
                com_google_android_gms_internal_zzbxm.zzq(7, this.zzcvV);
            }
            if (!(this.zzcvW == null || this.zzcvW.equals(""))) {
                com_google_android_gms_internal_zzbxm.zzq(8, this.zzcvW);
            }
            if (!(this.zzcvX == null || this.zzcvX.equals(""))) {
                com_google_android_gms_internal_zzbxm.zzq(9, this.zzcvX);
            }
            if (!(this.zzcvY == null || this.zzcvY.equals(""))) {
                com_google_android_gms_internal_zzbxm.zzq(10, this.zzcvY);
            }
            if (!(this.zzcvZ == null || this.zzcvZ.equals(""))) {
                com_google_android_gms_internal_zzbxm.zzq(11, this.zzcvZ);
            }
            if (this.zzcwa != 0) {
                com_google_android_gms_internal_zzbxm.zzJ(12, this.zzcwa);
            }
            if (this.zzcwb != null && this.zzcwb.length > 0) {
                for (zzbxt com_google_android_gms_internal_zzbxt : this.zzcwb) {
                    if (com_google_android_gms_internal_zzbxt != null) {
                        com_google_android_gms_internal_zzbxm.zza(13, com_google_android_gms_internal_zzbxt);
                    }
                }
            }
            super.zza(com_google_android_gms_internal_zzbxm);
        }

        public zzb zzaW(zzbxl com_google_android_gms_internal_zzbxl) throws IOException {
            while (true) {
                int zzaeo = com_google_android_gms_internal_zzbxl.zzaeo();
                switch (zzaeo) {
                    case 0:
                        break;
                    case 10:
                        this.zzcvP = com_google_android_gms_internal_zzbxl.readString();
                        continue;
                    case 18:
                        this.zzcvQ = com_google_android_gms_internal_zzbxl.readString();
                        continue;
                    case 24:
                        this.zzcvR = com_google_android_gms_internal_zzbxl.zzaer();
                        continue;
                    case 34:
                        this.zzcvS = com_google_android_gms_internal_zzbxl.readString();
                        continue;
                    case MotionEventCompat.AXIS_GENERIC_9 /*40*/:
                        this.zzcvT = com_google_android_gms_internal_zzbxl.zzaes();
                        continue;
                    case 48:
                        this.zzcvU = com_google_android_gms_internal_zzbxl.zzaes();
                        continue;
                    case 58:
                        this.zzcvV = com_google_android_gms_internal_zzbxl.readString();
                        continue;
                    case TLRPC.LAYER /*66*/:
                        this.zzcvW = com_google_android_gms_internal_zzbxl.readString();
                        continue;
                    case 74:
                        this.zzcvX = com_google_android_gms_internal_zzbxl.readString();
                        continue;
                    case 82:
                        this.zzcvY = com_google_android_gms_internal_zzbxl.readString();
                        continue;
                    case 90:
                        this.zzcvZ = com_google_android_gms_internal_zzbxl.readString();
                        continue;
                    case 96:
                        zzaeo = com_google_android_gms_internal_zzbxl.zzaes();
                        switch (zzaeo) {
                            case 0:
                            case 1:
                            case 2:
                                this.zzcwa = zzaeo;
                                break;
                            default:
                                continue;
                        }
                    case 106:
                        int zzb = zzbxw.zzb(com_google_android_gms_internal_zzbxl, 106);
                        zzaeo = this.zzcwb == null ? 0 : this.zzcwb.length;
                        Object obj = new zza[(zzb + zzaeo)];
                        if (zzaeo != 0) {
                            System.arraycopy(this.zzcwb, 0, obj, 0, zzaeo);
                        }
                        while (zzaeo < obj.length - 1) {
                            obj[zzaeo] = new zza();
                            com_google_android_gms_internal_zzbxl.zza(obj[zzaeo]);
                            com_google_android_gms_internal_zzbxl.zzaeo();
                            zzaeo++;
                        }
                        obj[zzaeo] = new zza();
                        com_google_android_gms_internal_zzbxl.zza(obj[zzaeo]);
                        this.zzcwb = obj;
                        continue;
                    default:
                        if (!super.zza(com_google_android_gms_internal_zzbxl, zzaeo)) {
                            break;
                        }
                        continue;
                }
                return this;
            }
        }

        public zzb zzafk() {
            this.zzcvP = "";
            this.zzcvQ = "";
            this.zzcvR = 0;
            this.zzcvS = "";
            this.zzcvT = 0;
            this.zzcvU = 0;
            this.zzcvV = "";
            this.zzcvW = "";
            this.zzcvX = "";
            this.zzcvY = "";
            this.zzcvZ = "";
            this.zzcwa = 0;
            this.zzcwb = zza.zzafi();
            this.zzcuI = null;
            this.zzcuR = -1;
            return this;
        }

        public /* synthetic */ zzbxt zzb(zzbxl com_google_android_gms_internal_zzbxl) throws IOException {
            return zzaW(com_google_android_gms_internal_zzbxl);
        }

        protected int zzu() {
            int zzu = super.zzu();
            if (!(this.zzcvP == null || this.zzcvP.equals(""))) {
                zzu += zzbxm.zzr(1, this.zzcvP);
            }
            if (!(this.zzcvQ == null || this.zzcvQ.equals(""))) {
                zzu += zzbxm.zzr(2, this.zzcvQ);
            }
            if (this.zzcvR != 0) {
                zzu += zzbxm.zzf(3, this.zzcvR);
            }
            if (!(this.zzcvS == null || this.zzcvS.equals(""))) {
                zzu += zzbxm.zzr(4, this.zzcvS);
            }
            if (this.zzcvT != 0) {
                zzu += zzbxm.zzL(5, this.zzcvT);
            }
            if (this.zzcvU != 0) {
                zzu += zzbxm.zzL(6, this.zzcvU);
            }
            if (!(this.zzcvV == null || this.zzcvV.equals(""))) {
                zzu += zzbxm.zzr(7, this.zzcvV);
            }
            if (!(this.zzcvW == null || this.zzcvW.equals(""))) {
                zzu += zzbxm.zzr(8, this.zzcvW);
            }
            if (!(this.zzcvX == null || this.zzcvX.equals(""))) {
                zzu += zzbxm.zzr(9, this.zzcvX);
            }
            if (!(this.zzcvY == null || this.zzcvY.equals(""))) {
                zzu += zzbxm.zzr(10, this.zzcvY);
            }
            if (!(this.zzcvZ == null || this.zzcvZ.equals(""))) {
                zzu += zzbxm.zzr(11, this.zzcvZ);
            }
            if (this.zzcwa != 0) {
                zzu += zzbxm.zzL(12, this.zzcwa);
            }
            if (this.zzcwb == null || this.zzcwb.length <= 0) {
                return zzu;
            }
            int i = zzu;
            for (zzbxt com_google_android_gms_internal_zzbxt : this.zzcwb) {
                if (com_google_android_gms_internal_zzbxt != null) {
                    i += zzbxm.zzc(13, com_google_android_gms_internal_zzbxt);
                }
            }
            return i;
        }
    }
}
