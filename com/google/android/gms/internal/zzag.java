package com.google.android.gms.internal;

import android.support.v4.view.MotionEventCompat;
import com.googlecode.mp4parser.authoring.tracks.h265.NalUnitTypes;
import com.googlecode.mp4parser.boxes.microsoft.XtraBox;
import java.io.IOException;
import org.telegram.messenger.support.widget.helper.ItemTouchHelper.Callback;

public interface zzag {

    public static final class zza extends zzbxn<zza> {
        public String zzaN;
        public String zzaP;
        public String zzaQ;
        public String zzaR;
        public Long zzbA;
        public Long zzbB;
        public Long zzbC;
        public zzb zzbD;
        public Long zzbE;
        public Long zzbF;
        public Long zzbG;
        public Long zzbH;
        public Long zzbI;
        public Long zzbJ;
        public Integer zzbK;
        public Integer zzbL;
        public Long zzbM;
        public Long zzbN;
        public Long zzbO;
        public Long zzbP;
        public Long zzbQ;
        public Integer zzbR;
        public zza zzbS;
        public zza[] zzbT;
        public zzb zzbU;
        public Long zzbV;
        public String zzbW;
        public Integer zzbX;
        public Boolean zzbY;
        public String zzbZ;
        public String zzba;
        public String zzbb;
        public Long zzbc;
        public Long zzbd;
        public Long zzbe;
        public Long zzbf;
        public Long zzbg;
        public Long zzbh;
        public Long zzbi;
        public Long zzbj;
        public Long zzbk;
        public Long zzbl;
        public String zzbm;
        public Long zzbn;
        public Long zzbo;
        public Long zzbp;
        public Long zzbq;
        public Long zzbr;
        public Long zzbs;
        public Long zzbt;
        public Long zzbu;
        public Long zzbv;
        public String zzbw;
        public Long zzbx;
        public Long zzby;
        public Long zzbz;
        public Long zzca;
        public zze zzcb;

        public static final class zza extends zzbxn<zza> {
            private static volatile zza[] zzcc;
            public Long zzbn;
            public Long zzbo;
            public Long zzcd;
            public Long zzce;
            public Long zzcf;
            public Long zzcg;
            public Integer zzch;
            public Long zzci;
            public Long zzcj;
            public Long zzck;
            public Integer zzcl;
            public Long zzcm;

            public zza() {
                this.zzbn = null;
                this.zzbo = null;
                this.zzcd = null;
                this.zzce = null;
                this.zzcf = null;
                this.zzcg = null;
                this.zzch = null;
                this.zzci = null;
                this.zzcj = null;
                this.zzck = null;
                this.zzcl = null;
                this.zzcm = null;
                this.zzcuR = -1;
            }

            public static zza[] zzv() {
                if (zzcc == null) {
                    synchronized (zzbxr.zzcuQ) {
                        if (zzcc == null) {
                            zzcc = new zza[0];
                        }
                    }
                }
                return zzcc;
            }

            public void zza(zzbxm com_google_android_gms_internal_zzbxm) throws IOException {
                if (this.zzbn != null) {
                    com_google_android_gms_internal_zzbxm.zzb(1, this.zzbn.longValue());
                }
                if (this.zzbo != null) {
                    com_google_android_gms_internal_zzbxm.zzb(2, this.zzbo.longValue());
                }
                if (this.zzcd != null) {
                    com_google_android_gms_internal_zzbxm.zzb(3, this.zzcd.longValue());
                }
                if (this.zzce != null) {
                    com_google_android_gms_internal_zzbxm.zzb(4, this.zzce.longValue());
                }
                if (this.zzcf != null) {
                    com_google_android_gms_internal_zzbxm.zzb(5, this.zzcf.longValue());
                }
                if (this.zzcg != null) {
                    com_google_android_gms_internal_zzbxm.zzb(6, this.zzcg.longValue());
                }
                if (this.zzch != null) {
                    com_google_android_gms_internal_zzbxm.zzJ(7, this.zzch.intValue());
                }
                if (this.zzci != null) {
                    com_google_android_gms_internal_zzbxm.zzb(8, this.zzci.longValue());
                }
                if (this.zzcj != null) {
                    com_google_android_gms_internal_zzbxm.zzb(9, this.zzcj.longValue());
                }
                if (this.zzck != null) {
                    com_google_android_gms_internal_zzbxm.zzb(10, this.zzck.longValue());
                }
                if (this.zzcl != null) {
                    com_google_android_gms_internal_zzbxm.zzJ(11, this.zzcl.intValue());
                }
                if (this.zzcm != null) {
                    com_google_android_gms_internal_zzbxm.zzb(12, this.zzcm.longValue());
                }
                super.zza(com_google_android_gms_internal_zzbxm);
            }

            public /* synthetic */ zzbxt zzb(zzbxl com_google_android_gms_internal_zzbxl) throws IOException {
                return zzg(com_google_android_gms_internal_zzbxl);
            }

            public zza zzg(zzbxl com_google_android_gms_internal_zzbxl) throws IOException {
                while (true) {
                    int zzaeo = com_google_android_gms_internal_zzbxl.zzaeo();
                    switch (zzaeo) {
                        case 0:
                            break;
                        case 8:
                            this.zzbn = Long.valueOf(com_google_android_gms_internal_zzbxl.zzaer());
                            continue;
                        case 16:
                            this.zzbo = Long.valueOf(com_google_android_gms_internal_zzbxl.zzaer());
                            continue;
                        case 24:
                            this.zzcd = Long.valueOf(com_google_android_gms_internal_zzbxl.zzaer());
                            continue;
                        case 32:
                            this.zzce = Long.valueOf(com_google_android_gms_internal_zzbxl.zzaer());
                            continue;
                        case MotionEventCompat.AXIS_GENERIC_9 /*40*/:
                            this.zzcf = Long.valueOf(com_google_android_gms_internal_zzbxl.zzaer());
                            continue;
                        case 48:
                            this.zzcg = Long.valueOf(com_google_android_gms_internal_zzbxl.zzaer());
                            continue;
                        case 56:
                            zzaeo = com_google_android_gms_internal_zzbxl.zzaes();
                            switch (zzaeo) {
                                case 0:
                                case 1:
                                case 2:
                                case 1000:
                                    this.zzch = Integer.valueOf(zzaeo);
                                    break;
                                default:
                                    continue;
                            }
                        case 64:
                            this.zzci = Long.valueOf(com_google_android_gms_internal_zzbxl.zzaer());
                            continue;
                        case XtraBox.MP4_XTRA_BT_GUID /*72*/:
                            this.zzcj = Long.valueOf(com_google_android_gms_internal_zzbxl.zzaer());
                            continue;
                        case 80:
                            this.zzck = Long.valueOf(com_google_android_gms_internal_zzbxl.zzaer());
                            continue;
                        case 88:
                            zzaeo = com_google_android_gms_internal_zzbxl.zzaes();
                            switch (zzaeo) {
                                case 0:
                                case 1:
                                case 2:
                                case 1000:
                                    this.zzcl = Integer.valueOf(zzaeo);
                                    break;
                                default:
                                    continue;
                            }
                        case 96:
                            this.zzcm = Long.valueOf(com_google_android_gms_internal_zzbxl.zzaer());
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

            protected int zzu() {
                int zzu = super.zzu();
                if (this.zzbn != null) {
                    zzu += zzbxm.zzf(1, this.zzbn.longValue());
                }
                if (this.zzbo != null) {
                    zzu += zzbxm.zzf(2, this.zzbo.longValue());
                }
                if (this.zzcd != null) {
                    zzu += zzbxm.zzf(3, this.zzcd.longValue());
                }
                if (this.zzce != null) {
                    zzu += zzbxm.zzf(4, this.zzce.longValue());
                }
                if (this.zzcf != null) {
                    zzu += zzbxm.zzf(5, this.zzcf.longValue());
                }
                if (this.zzcg != null) {
                    zzu += zzbxm.zzf(6, this.zzcg.longValue());
                }
                if (this.zzch != null) {
                    zzu += zzbxm.zzL(7, this.zzch.intValue());
                }
                if (this.zzci != null) {
                    zzu += zzbxm.zzf(8, this.zzci.longValue());
                }
                if (this.zzcj != null) {
                    zzu += zzbxm.zzf(9, this.zzcj.longValue());
                }
                if (this.zzck != null) {
                    zzu += zzbxm.zzf(10, this.zzck.longValue());
                }
                if (this.zzcl != null) {
                    zzu += zzbxm.zzL(11, this.zzcl.intValue());
                }
                return this.zzcm != null ? zzu + zzbxm.zzf(12, this.zzcm.longValue()) : zzu;
            }
        }

        public static final class zzb extends zzbxn<zzb> {
            public Long zzbP;
            public Long zzbQ;
            public Long zzcn;

            public zzb() {
                this.zzbP = null;
                this.zzbQ = null;
                this.zzcn = null;
                this.zzcuR = -1;
            }

            public void zza(zzbxm com_google_android_gms_internal_zzbxm) throws IOException {
                if (this.zzbP != null) {
                    com_google_android_gms_internal_zzbxm.zzb(1, this.zzbP.longValue());
                }
                if (this.zzbQ != null) {
                    com_google_android_gms_internal_zzbxm.zzb(2, this.zzbQ.longValue());
                }
                if (this.zzcn != null) {
                    com_google_android_gms_internal_zzbxm.zzb(3, this.zzcn.longValue());
                }
                super.zza(com_google_android_gms_internal_zzbxm);
            }

            public /* synthetic */ zzbxt zzb(zzbxl com_google_android_gms_internal_zzbxl) throws IOException {
                return zzh(com_google_android_gms_internal_zzbxl);
            }

            public zzb zzh(zzbxl com_google_android_gms_internal_zzbxl) throws IOException {
                while (true) {
                    int zzaeo = com_google_android_gms_internal_zzbxl.zzaeo();
                    switch (zzaeo) {
                        case 0:
                            break;
                        case 8:
                            this.zzbP = Long.valueOf(com_google_android_gms_internal_zzbxl.zzaer());
                            continue;
                        case 16:
                            this.zzbQ = Long.valueOf(com_google_android_gms_internal_zzbxl.zzaer());
                            continue;
                        case 24:
                            this.zzcn = Long.valueOf(com_google_android_gms_internal_zzbxl.zzaer());
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

            protected int zzu() {
                int zzu = super.zzu();
                if (this.zzbP != null) {
                    zzu += zzbxm.zzf(1, this.zzbP.longValue());
                }
                if (this.zzbQ != null) {
                    zzu += zzbxm.zzf(2, this.zzbQ.longValue());
                }
                return this.zzcn != null ? zzu + zzbxm.zzf(3, this.zzcn.longValue()) : zzu;
            }
        }

        public zza() {
            this.zzbb = null;
            this.zzba = null;
            this.zzbc = null;
            this.zzbd = null;
            this.zzbe = null;
            this.zzbf = null;
            this.zzbg = null;
            this.zzbh = null;
            this.zzbi = null;
            this.zzbj = null;
            this.zzbk = null;
            this.zzbl = null;
            this.zzbm = null;
            this.zzbn = null;
            this.zzbo = null;
            this.zzbp = null;
            this.zzbq = null;
            this.zzbr = null;
            this.zzbs = null;
            this.zzbt = null;
            this.zzbu = null;
            this.zzbv = null;
            this.zzaN = null;
            this.zzbw = null;
            this.zzbx = null;
            this.zzby = null;
            this.zzbz = null;
            this.zzaP = null;
            this.zzbA = null;
            this.zzbB = null;
            this.zzbC = null;
            this.zzbE = null;
            this.zzbF = null;
            this.zzbG = null;
            this.zzbH = null;
            this.zzbI = null;
            this.zzbJ = null;
            this.zzaQ = null;
            this.zzaR = null;
            this.zzbK = null;
            this.zzbL = null;
            this.zzbM = null;
            this.zzbN = null;
            this.zzbO = null;
            this.zzbP = null;
            this.zzbQ = null;
            this.zzbR = null;
            this.zzbT = zza.zzv();
            this.zzbV = null;
            this.zzbW = null;
            this.zzbX = null;
            this.zzbY = null;
            this.zzbZ = null;
            this.zzca = null;
            this.zzcuR = -1;
        }

        public static zza zzd(byte[] bArr) throws zzbxs {
            return (zza) zzbxt.zza(new zza(), bArr);
        }

        public void zza(zzbxm com_google_android_gms_internal_zzbxm) throws IOException {
            if (this.zzbb != null) {
                com_google_android_gms_internal_zzbxm.zzq(1, this.zzbb);
            }
            if (this.zzba != null) {
                com_google_android_gms_internal_zzbxm.zzq(2, this.zzba);
            }
            if (this.zzbc != null) {
                com_google_android_gms_internal_zzbxm.zzb(3, this.zzbc.longValue());
            }
            if (this.zzbd != null) {
                com_google_android_gms_internal_zzbxm.zzb(4, this.zzbd.longValue());
            }
            if (this.zzbe != null) {
                com_google_android_gms_internal_zzbxm.zzb(5, this.zzbe.longValue());
            }
            if (this.zzbf != null) {
                com_google_android_gms_internal_zzbxm.zzb(6, this.zzbf.longValue());
            }
            if (this.zzbg != null) {
                com_google_android_gms_internal_zzbxm.zzb(7, this.zzbg.longValue());
            }
            if (this.zzbh != null) {
                com_google_android_gms_internal_zzbxm.zzb(8, this.zzbh.longValue());
            }
            if (this.zzbi != null) {
                com_google_android_gms_internal_zzbxm.zzb(9, this.zzbi.longValue());
            }
            if (this.zzbj != null) {
                com_google_android_gms_internal_zzbxm.zzb(10, this.zzbj.longValue());
            }
            if (this.zzbk != null) {
                com_google_android_gms_internal_zzbxm.zzb(11, this.zzbk.longValue());
            }
            if (this.zzbl != null) {
                com_google_android_gms_internal_zzbxm.zzb(12, this.zzbl.longValue());
            }
            if (this.zzbm != null) {
                com_google_android_gms_internal_zzbxm.zzq(13, this.zzbm);
            }
            if (this.zzbn != null) {
                com_google_android_gms_internal_zzbxm.zzb(14, this.zzbn.longValue());
            }
            if (this.zzbo != null) {
                com_google_android_gms_internal_zzbxm.zzb(15, this.zzbo.longValue());
            }
            if (this.zzbp != null) {
                com_google_android_gms_internal_zzbxm.zzb(16, this.zzbp.longValue());
            }
            if (this.zzbq != null) {
                com_google_android_gms_internal_zzbxm.zzb(17, this.zzbq.longValue());
            }
            if (this.zzbr != null) {
                com_google_android_gms_internal_zzbxm.zzb(18, this.zzbr.longValue());
            }
            if (this.zzbs != null) {
                com_google_android_gms_internal_zzbxm.zzb(19, this.zzbs.longValue());
            }
            if (this.zzbt != null) {
                com_google_android_gms_internal_zzbxm.zzb(20, this.zzbt.longValue());
            }
            if (this.zzbV != null) {
                com_google_android_gms_internal_zzbxm.zzb(21, this.zzbV.longValue());
            }
            if (this.zzbu != null) {
                com_google_android_gms_internal_zzbxm.zzb(22, this.zzbu.longValue());
            }
            if (this.zzbv != null) {
                com_google_android_gms_internal_zzbxm.zzb(23, this.zzbv.longValue());
            }
            if (this.zzbW != null) {
                com_google_android_gms_internal_zzbxm.zzq(24, this.zzbW);
            }
            if (this.zzca != null) {
                com_google_android_gms_internal_zzbxm.zzb(25, this.zzca.longValue());
            }
            if (this.zzbX != null) {
                com_google_android_gms_internal_zzbxm.zzJ(26, this.zzbX.intValue());
            }
            if (this.zzaN != null) {
                com_google_android_gms_internal_zzbxm.zzq(27, this.zzaN);
            }
            if (this.zzbY != null) {
                com_google_android_gms_internal_zzbxm.zzg(28, this.zzbY.booleanValue());
            }
            if (this.zzbw != null) {
                com_google_android_gms_internal_zzbxm.zzq(29, this.zzbw);
            }
            if (this.zzbZ != null) {
                com_google_android_gms_internal_zzbxm.zzq(30, this.zzbZ);
            }
            if (this.zzbx != null) {
                com_google_android_gms_internal_zzbxm.zzb(31, this.zzbx.longValue());
            }
            if (this.zzby != null) {
                com_google_android_gms_internal_zzbxm.zzb(32, this.zzby.longValue());
            }
            if (this.zzbz != null) {
                com_google_android_gms_internal_zzbxm.zzb(33, this.zzbz.longValue());
            }
            if (this.zzaP != null) {
                com_google_android_gms_internal_zzbxm.zzq(34, this.zzaP);
            }
            if (this.zzbA != null) {
                com_google_android_gms_internal_zzbxm.zzb(35, this.zzbA.longValue());
            }
            if (this.zzbB != null) {
                com_google_android_gms_internal_zzbxm.zzb(36, this.zzbB.longValue());
            }
            if (this.zzbC != null) {
                com_google_android_gms_internal_zzbxm.zzb(37, this.zzbC.longValue());
            }
            if (this.zzbD != null) {
                com_google_android_gms_internal_zzbxm.zza(38, this.zzbD);
            }
            if (this.zzbE != null) {
                com_google_android_gms_internal_zzbxm.zzb(39, this.zzbE.longValue());
            }
            if (this.zzbF != null) {
                com_google_android_gms_internal_zzbxm.zzb(40, this.zzbF.longValue());
            }
            if (this.zzbG != null) {
                com_google_android_gms_internal_zzbxm.zzb(41, this.zzbG.longValue());
            }
            if (this.zzbH != null) {
                com_google_android_gms_internal_zzbxm.zzb(42, this.zzbH.longValue());
            }
            if (this.zzbT != null && this.zzbT.length > 0) {
                for (zzbxt com_google_android_gms_internal_zzbxt : this.zzbT) {
                    if (com_google_android_gms_internal_zzbxt != null) {
                        com_google_android_gms_internal_zzbxm.zza(43, com_google_android_gms_internal_zzbxt);
                    }
                }
            }
            if (this.zzbI != null) {
                com_google_android_gms_internal_zzbxm.zzb(44, this.zzbI.longValue());
            }
            if (this.zzbJ != null) {
                com_google_android_gms_internal_zzbxm.zzb(45, this.zzbJ.longValue());
            }
            if (this.zzaQ != null) {
                com_google_android_gms_internal_zzbxm.zzq(46, this.zzaQ);
            }
            if (this.zzaR != null) {
                com_google_android_gms_internal_zzbxm.zzq(47, this.zzaR);
            }
            if (this.zzbK != null) {
                com_google_android_gms_internal_zzbxm.zzJ(48, this.zzbK.intValue());
            }
            if (this.zzbL != null) {
                com_google_android_gms_internal_zzbxm.zzJ(49, this.zzbL.intValue());
            }
            if (this.zzbS != null) {
                com_google_android_gms_internal_zzbxm.zza(50, this.zzbS);
            }
            if (this.zzbM != null) {
                com_google_android_gms_internal_zzbxm.zzb(51, this.zzbM.longValue());
            }
            if (this.zzbN != null) {
                com_google_android_gms_internal_zzbxm.zzb(52, this.zzbN.longValue());
            }
            if (this.zzbO != null) {
                com_google_android_gms_internal_zzbxm.zzb(53, this.zzbO.longValue());
            }
            if (this.zzbP != null) {
                com_google_android_gms_internal_zzbxm.zzb(54, this.zzbP.longValue());
            }
            if (this.zzbQ != null) {
                com_google_android_gms_internal_zzbxm.zzb(55, this.zzbQ.longValue());
            }
            if (this.zzbR != null) {
                com_google_android_gms_internal_zzbxm.zzJ(56, this.zzbR.intValue());
            }
            if (this.zzbU != null) {
                com_google_android_gms_internal_zzbxm.zza(57, this.zzbU);
            }
            if (this.zzcb != null) {
                com_google_android_gms_internal_zzbxm.zza(201, this.zzcb);
            }
            super.zza(com_google_android_gms_internal_zzbxm);
        }

        public /* synthetic */ zzbxt zzb(zzbxl com_google_android_gms_internal_zzbxl) throws IOException {
            return zzf(com_google_android_gms_internal_zzbxl);
        }

        public zza zzf(zzbxl com_google_android_gms_internal_zzbxl) throws IOException {
            while (true) {
                int zzaeo = com_google_android_gms_internal_zzbxl.zzaeo();
                switch (zzaeo) {
                    case 0:
                        break;
                    case 10:
                        this.zzbb = com_google_android_gms_internal_zzbxl.readString();
                        continue;
                    case 18:
                        this.zzba = com_google_android_gms_internal_zzbxl.readString();
                        continue;
                    case 24:
                        this.zzbc = Long.valueOf(com_google_android_gms_internal_zzbxl.zzaer());
                        continue;
                    case 32:
                        this.zzbd = Long.valueOf(com_google_android_gms_internal_zzbxl.zzaer());
                        continue;
                    case MotionEventCompat.AXIS_GENERIC_9 /*40*/:
                        this.zzbe = Long.valueOf(com_google_android_gms_internal_zzbxl.zzaer());
                        continue;
                    case 48:
                        this.zzbf = Long.valueOf(com_google_android_gms_internal_zzbxl.zzaer());
                        continue;
                    case 56:
                        this.zzbg = Long.valueOf(com_google_android_gms_internal_zzbxl.zzaer());
                        continue;
                    case 64:
                        this.zzbh = Long.valueOf(com_google_android_gms_internal_zzbxl.zzaer());
                        continue;
                    case XtraBox.MP4_XTRA_BT_GUID /*72*/:
                        this.zzbi = Long.valueOf(com_google_android_gms_internal_zzbxl.zzaer());
                        continue;
                    case 80:
                        this.zzbj = Long.valueOf(com_google_android_gms_internal_zzbxl.zzaer());
                        continue;
                    case 88:
                        this.zzbk = Long.valueOf(com_google_android_gms_internal_zzbxl.zzaer());
                        continue;
                    case 96:
                        this.zzbl = Long.valueOf(com_google_android_gms_internal_zzbxl.zzaer());
                        continue;
                    case 106:
                        this.zzbm = com_google_android_gms_internal_zzbxl.readString();
                        continue;
                    case 112:
                        this.zzbn = Long.valueOf(com_google_android_gms_internal_zzbxl.zzaer());
                        continue;
                    case 120:
                        this.zzbo = Long.valueOf(com_google_android_gms_internal_zzbxl.zzaer());
                        continue;
                    case 128:
                        this.zzbp = Long.valueOf(com_google_android_gms_internal_zzbxl.zzaer());
                        continue;
                    case 136:
                        this.zzbq = Long.valueOf(com_google_android_gms_internal_zzbxl.zzaer());
                        continue;
                    case 144:
                        this.zzbr = Long.valueOf(com_google_android_gms_internal_zzbxl.zzaer());
                        continue;
                    case 152:
                        this.zzbs = Long.valueOf(com_google_android_gms_internal_zzbxl.zzaer());
                        continue;
                    case 160:
                        this.zzbt = Long.valueOf(com_google_android_gms_internal_zzbxl.zzaer());
                        continue;
                    case 168:
                        this.zzbV = Long.valueOf(com_google_android_gms_internal_zzbxl.zzaer());
                        continue;
                    case 176:
                        this.zzbu = Long.valueOf(com_google_android_gms_internal_zzbxl.zzaer());
                        continue;
                    case 184:
                        this.zzbv = Long.valueOf(com_google_android_gms_internal_zzbxl.zzaer());
                        continue;
                    case 194:
                        this.zzbW = com_google_android_gms_internal_zzbxl.readString();
                        continue;
                    case Callback.DEFAULT_DRAG_ANIMATION_DURATION /*200*/:
                        this.zzca = Long.valueOf(com_google_android_gms_internal_zzbxl.zzaer());
                        continue;
                    case 208:
                        zzaeo = com_google_android_gms_internal_zzbxl.zzaes();
                        switch (zzaeo) {
                            case 0:
                            case 1:
                            case 2:
                            case 3:
                            case 4:
                            case 5:
                            case 6:
                                this.zzbX = Integer.valueOf(zzaeo);
                                break;
                            default:
                                continue;
                        }
                    case 218:
                        this.zzaN = com_google_android_gms_internal_zzbxl.readString();
                        continue;
                    case 224:
                        this.zzbY = Boolean.valueOf(com_google_android_gms_internal_zzbxl.zzaeu());
                        continue;
                    case 234:
                        this.zzbw = com_google_android_gms_internal_zzbxl.readString();
                        continue;
                    case 242:
                        this.zzbZ = com_google_android_gms_internal_zzbxl.readString();
                        continue;
                    case 248:
                        this.zzbx = Long.valueOf(com_google_android_gms_internal_zzbxl.zzaer());
                        continue;
                    case 256:
                        this.zzby = Long.valueOf(com_google_android_gms_internal_zzbxl.zzaer());
                        continue;
                    case 264:
                        this.zzbz = Long.valueOf(com_google_android_gms_internal_zzbxl.zzaer());
                        continue;
                    case 274:
                        this.zzaP = com_google_android_gms_internal_zzbxl.readString();
                        continue;
                    case 280:
                        this.zzbA = Long.valueOf(com_google_android_gms_internal_zzbxl.zzaer());
                        continue;
                    case 288:
                        this.zzbB = Long.valueOf(com_google_android_gms_internal_zzbxl.zzaer());
                        continue;
                    case 296:
                        this.zzbC = Long.valueOf(com_google_android_gms_internal_zzbxl.zzaer());
                        continue;
                    case 306:
                        if (this.zzbD == null) {
                            this.zzbD = new zzb();
                        }
                        com_google_android_gms_internal_zzbxl.zza(this.zzbD);
                        continue;
                    case 312:
                        this.zzbE = Long.valueOf(com_google_android_gms_internal_zzbxl.zzaer());
                        continue;
                    case 320:
                        this.zzbF = Long.valueOf(com_google_android_gms_internal_zzbxl.zzaer());
                        continue;
                    case 328:
                        this.zzbG = Long.valueOf(com_google_android_gms_internal_zzbxl.zzaer());
                        continue;
                    case 336:
                        this.zzbH = Long.valueOf(com_google_android_gms_internal_zzbxl.zzaer());
                        continue;
                    case 346:
                        int zzb = zzbxw.zzb(com_google_android_gms_internal_zzbxl, 346);
                        zzaeo = this.zzbT == null ? 0 : this.zzbT.length;
                        Object obj = new zza[(zzb + zzaeo)];
                        if (zzaeo != 0) {
                            System.arraycopy(this.zzbT, 0, obj, 0, zzaeo);
                        }
                        while (zzaeo < obj.length - 1) {
                            obj[zzaeo] = new zza();
                            com_google_android_gms_internal_zzbxl.zza(obj[zzaeo]);
                            com_google_android_gms_internal_zzbxl.zzaeo();
                            zzaeo++;
                        }
                        obj[zzaeo] = new zza();
                        com_google_android_gms_internal_zzbxl.zza(obj[zzaeo]);
                        this.zzbT = obj;
                        continue;
                    case 352:
                        this.zzbI = Long.valueOf(com_google_android_gms_internal_zzbxl.zzaer());
                        continue;
                    case 360:
                        this.zzbJ = Long.valueOf(com_google_android_gms_internal_zzbxl.zzaer());
                        continue;
                    case 370:
                        this.zzaQ = com_google_android_gms_internal_zzbxl.readString();
                        continue;
                    case 378:
                        this.zzaR = com_google_android_gms_internal_zzbxl.readString();
                        continue;
                    case 384:
                        zzaeo = com_google_android_gms_internal_zzbxl.zzaes();
                        switch (zzaeo) {
                            case 0:
                            case 1:
                            case 2:
                            case 1000:
                                this.zzbK = Integer.valueOf(zzaeo);
                                break;
                            default:
                                continue;
                        }
                    case 392:
                        zzaeo = com_google_android_gms_internal_zzbxl.zzaes();
                        switch (zzaeo) {
                            case 0:
                            case 1:
                            case 2:
                            case 1000:
                                this.zzbL = Integer.valueOf(zzaeo);
                                break;
                            default:
                                continue;
                        }
                    case 402:
                        if (this.zzbS == null) {
                            this.zzbS = new zza();
                        }
                        com_google_android_gms_internal_zzbxl.zza(this.zzbS);
                        continue;
                    case 408:
                        this.zzbM = Long.valueOf(com_google_android_gms_internal_zzbxl.zzaer());
                        continue;
                    case 416:
                        this.zzbN = Long.valueOf(com_google_android_gms_internal_zzbxl.zzaer());
                        continue;
                    case 424:
                        this.zzbO = Long.valueOf(com_google_android_gms_internal_zzbxl.zzaer());
                        continue;
                    case 432:
                        this.zzbP = Long.valueOf(com_google_android_gms_internal_zzbxl.zzaer());
                        continue;
                    case 440:
                        this.zzbQ = Long.valueOf(com_google_android_gms_internal_zzbxl.zzaer());
                        continue;
                    case 448:
                        zzaeo = com_google_android_gms_internal_zzbxl.zzaes();
                        switch (zzaeo) {
                            case 0:
                            case 1:
                            case 2:
                            case 1000:
                                this.zzbR = Integer.valueOf(zzaeo);
                                break;
                            default:
                                continue;
                        }
                    case 458:
                        if (this.zzbU == null) {
                            this.zzbU = new zzb();
                        }
                        com_google_android_gms_internal_zzbxl.zza(this.zzbU);
                        continue;
                    case 1610:
                        if (this.zzcb == null) {
                            this.zzcb = new zze();
                        }
                        com_google_android_gms_internal_zzbxl.zza(this.zzcb);
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

        protected int zzu() {
            int zzu = super.zzu();
            if (this.zzbb != null) {
                zzu += zzbxm.zzr(1, this.zzbb);
            }
            if (this.zzba != null) {
                zzu += zzbxm.zzr(2, this.zzba);
            }
            if (this.zzbc != null) {
                zzu += zzbxm.zzf(3, this.zzbc.longValue());
            }
            if (this.zzbd != null) {
                zzu += zzbxm.zzf(4, this.zzbd.longValue());
            }
            if (this.zzbe != null) {
                zzu += zzbxm.zzf(5, this.zzbe.longValue());
            }
            if (this.zzbf != null) {
                zzu += zzbxm.zzf(6, this.zzbf.longValue());
            }
            if (this.zzbg != null) {
                zzu += zzbxm.zzf(7, this.zzbg.longValue());
            }
            if (this.zzbh != null) {
                zzu += zzbxm.zzf(8, this.zzbh.longValue());
            }
            if (this.zzbi != null) {
                zzu += zzbxm.zzf(9, this.zzbi.longValue());
            }
            if (this.zzbj != null) {
                zzu += zzbxm.zzf(10, this.zzbj.longValue());
            }
            if (this.zzbk != null) {
                zzu += zzbxm.zzf(11, this.zzbk.longValue());
            }
            if (this.zzbl != null) {
                zzu += zzbxm.zzf(12, this.zzbl.longValue());
            }
            if (this.zzbm != null) {
                zzu += zzbxm.zzr(13, this.zzbm);
            }
            if (this.zzbn != null) {
                zzu += zzbxm.zzf(14, this.zzbn.longValue());
            }
            if (this.zzbo != null) {
                zzu += zzbxm.zzf(15, this.zzbo.longValue());
            }
            if (this.zzbp != null) {
                zzu += zzbxm.zzf(16, this.zzbp.longValue());
            }
            if (this.zzbq != null) {
                zzu += zzbxm.zzf(17, this.zzbq.longValue());
            }
            if (this.zzbr != null) {
                zzu += zzbxm.zzf(18, this.zzbr.longValue());
            }
            if (this.zzbs != null) {
                zzu += zzbxm.zzf(19, this.zzbs.longValue());
            }
            if (this.zzbt != null) {
                zzu += zzbxm.zzf(20, this.zzbt.longValue());
            }
            if (this.zzbV != null) {
                zzu += zzbxm.zzf(21, this.zzbV.longValue());
            }
            if (this.zzbu != null) {
                zzu += zzbxm.zzf(22, this.zzbu.longValue());
            }
            if (this.zzbv != null) {
                zzu += zzbxm.zzf(23, this.zzbv.longValue());
            }
            if (this.zzbW != null) {
                zzu += zzbxm.zzr(24, this.zzbW);
            }
            if (this.zzca != null) {
                zzu += zzbxm.zzf(25, this.zzca.longValue());
            }
            if (this.zzbX != null) {
                zzu += zzbxm.zzL(26, this.zzbX.intValue());
            }
            if (this.zzaN != null) {
                zzu += zzbxm.zzr(27, this.zzaN);
            }
            if (this.zzbY != null) {
                zzu += zzbxm.zzh(28, this.zzbY.booleanValue());
            }
            if (this.zzbw != null) {
                zzu += zzbxm.zzr(29, this.zzbw);
            }
            if (this.zzbZ != null) {
                zzu += zzbxm.zzr(30, this.zzbZ);
            }
            if (this.zzbx != null) {
                zzu += zzbxm.zzf(31, this.zzbx.longValue());
            }
            if (this.zzby != null) {
                zzu += zzbxm.zzf(32, this.zzby.longValue());
            }
            if (this.zzbz != null) {
                zzu += zzbxm.zzf(33, this.zzbz.longValue());
            }
            if (this.zzaP != null) {
                zzu += zzbxm.zzr(34, this.zzaP);
            }
            if (this.zzbA != null) {
                zzu += zzbxm.zzf(35, this.zzbA.longValue());
            }
            if (this.zzbB != null) {
                zzu += zzbxm.zzf(36, this.zzbB.longValue());
            }
            if (this.zzbC != null) {
                zzu += zzbxm.zzf(37, this.zzbC.longValue());
            }
            if (this.zzbD != null) {
                zzu += zzbxm.zzc(38, this.zzbD);
            }
            if (this.zzbE != null) {
                zzu += zzbxm.zzf(39, this.zzbE.longValue());
            }
            if (this.zzbF != null) {
                zzu += zzbxm.zzf(40, this.zzbF.longValue());
            }
            if (this.zzbG != null) {
                zzu += zzbxm.zzf(41, this.zzbG.longValue());
            }
            if (this.zzbH != null) {
                zzu += zzbxm.zzf(42, this.zzbH.longValue());
            }
            if (this.zzbT != null && this.zzbT.length > 0) {
                int i = zzu;
                for (zzbxt com_google_android_gms_internal_zzbxt : this.zzbT) {
                    if (com_google_android_gms_internal_zzbxt != null) {
                        i += zzbxm.zzc(43, com_google_android_gms_internal_zzbxt);
                    }
                }
                zzu = i;
            }
            if (this.zzbI != null) {
                zzu += zzbxm.zzf(44, this.zzbI.longValue());
            }
            if (this.zzbJ != null) {
                zzu += zzbxm.zzf(45, this.zzbJ.longValue());
            }
            if (this.zzaQ != null) {
                zzu += zzbxm.zzr(46, this.zzaQ);
            }
            if (this.zzaR != null) {
                zzu += zzbxm.zzr(47, this.zzaR);
            }
            if (this.zzbK != null) {
                zzu += zzbxm.zzL(48, this.zzbK.intValue());
            }
            if (this.zzbL != null) {
                zzu += zzbxm.zzL(49, this.zzbL.intValue());
            }
            if (this.zzbS != null) {
                zzu += zzbxm.zzc(50, this.zzbS);
            }
            if (this.zzbM != null) {
                zzu += zzbxm.zzf(51, this.zzbM.longValue());
            }
            if (this.zzbN != null) {
                zzu += zzbxm.zzf(52, this.zzbN.longValue());
            }
            if (this.zzbO != null) {
                zzu += zzbxm.zzf(53, this.zzbO.longValue());
            }
            if (this.zzbP != null) {
                zzu += zzbxm.zzf(54, this.zzbP.longValue());
            }
            if (this.zzbQ != null) {
                zzu += zzbxm.zzf(55, this.zzbQ.longValue());
            }
            if (this.zzbR != null) {
                zzu += zzbxm.zzL(56, this.zzbR.intValue());
            }
            if (this.zzbU != null) {
                zzu += zzbxm.zzc(57, this.zzbU);
            }
            return this.zzcb != null ? zzu + zzbxm.zzc(201, this.zzcb) : zzu;
        }
    }

    public static final class zzb extends zzbxn<zzb> {
        public Long zzco;
        public Integer zzcp;
        public Boolean zzcq;
        public int[] zzcr;
        public Long zzcs;

        public zzb() {
            this.zzco = null;
            this.zzcp = null;
            this.zzcq = null;
            this.zzcr = zzbxw.zzcuW;
            this.zzcs = null;
            this.zzcuR = -1;
        }

        public void zza(zzbxm com_google_android_gms_internal_zzbxm) throws IOException {
            if (this.zzco != null) {
                com_google_android_gms_internal_zzbxm.zzb(1, this.zzco.longValue());
            }
            if (this.zzcp != null) {
                com_google_android_gms_internal_zzbxm.zzJ(2, this.zzcp.intValue());
            }
            if (this.zzcq != null) {
                com_google_android_gms_internal_zzbxm.zzg(3, this.zzcq.booleanValue());
            }
            if (this.zzcr != null && this.zzcr.length > 0) {
                for (int zzJ : this.zzcr) {
                    com_google_android_gms_internal_zzbxm.zzJ(4, zzJ);
                }
            }
            if (this.zzcs != null) {
                com_google_android_gms_internal_zzbxm.zza(5, this.zzcs.longValue());
            }
            super.zza(com_google_android_gms_internal_zzbxm);
        }

        public /* synthetic */ zzbxt zzb(zzbxl com_google_android_gms_internal_zzbxl) throws IOException {
            return zzi(com_google_android_gms_internal_zzbxl);
        }

        public zzb zzi(zzbxl com_google_android_gms_internal_zzbxl) throws IOException {
            while (true) {
                int zzaeo = com_google_android_gms_internal_zzbxl.zzaeo();
                int zzb;
                switch (zzaeo) {
                    case 0:
                        break;
                    case 8:
                        this.zzco = Long.valueOf(com_google_android_gms_internal_zzbxl.zzaer());
                        continue;
                    case 16:
                        this.zzcp = Integer.valueOf(com_google_android_gms_internal_zzbxl.zzaes());
                        continue;
                    case 24:
                        this.zzcq = Boolean.valueOf(com_google_android_gms_internal_zzbxl.zzaeu());
                        continue;
                    case 32:
                        zzb = zzbxw.zzb(com_google_android_gms_internal_zzbxl, 32);
                        zzaeo = this.zzcr == null ? 0 : this.zzcr.length;
                        Object obj = new int[(zzb + zzaeo)];
                        if (zzaeo != 0) {
                            System.arraycopy(this.zzcr, 0, obj, 0, zzaeo);
                        }
                        while (zzaeo < obj.length - 1) {
                            obj[zzaeo] = com_google_android_gms_internal_zzbxl.zzaes();
                            com_google_android_gms_internal_zzbxl.zzaeo();
                            zzaeo++;
                        }
                        obj[zzaeo] = com_google_android_gms_internal_zzbxl.zzaes();
                        this.zzcr = obj;
                        continue;
                    case 34:
                        int zzra = com_google_android_gms_internal_zzbxl.zzra(com_google_android_gms_internal_zzbxl.zzaex());
                        zzb = com_google_android_gms_internal_zzbxl.getPosition();
                        zzaeo = 0;
                        while (com_google_android_gms_internal_zzbxl.zzaeC() > 0) {
                            com_google_android_gms_internal_zzbxl.zzaes();
                            zzaeo++;
                        }
                        com_google_android_gms_internal_zzbxl.zzrc(zzb);
                        zzb = this.zzcr == null ? 0 : this.zzcr.length;
                        Object obj2 = new int[(zzaeo + zzb)];
                        if (zzb != 0) {
                            System.arraycopy(this.zzcr, 0, obj2, 0, zzb);
                        }
                        while (zzb < obj2.length) {
                            obj2[zzb] = com_google_android_gms_internal_zzbxl.zzaes();
                            zzb++;
                        }
                        this.zzcr = obj2;
                        com_google_android_gms_internal_zzbxl.zzrb(zzra);
                        continue;
                    case MotionEventCompat.AXIS_GENERIC_9 /*40*/:
                        this.zzcs = Long.valueOf(com_google_android_gms_internal_zzbxl.zzaeq());
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

        protected int zzu() {
            int i = 0;
            int zzu = super.zzu();
            if (this.zzco != null) {
                zzu += zzbxm.zzf(1, this.zzco.longValue());
            }
            if (this.zzcp != null) {
                zzu += zzbxm.zzL(2, this.zzcp.intValue());
            }
            if (this.zzcq != null) {
                zzu += zzbxm.zzh(3, this.zzcq.booleanValue());
            }
            if (this.zzcr != null && this.zzcr.length > 0) {
                int i2 = 0;
                while (i < this.zzcr.length) {
                    i2 += zzbxm.zzrg(this.zzcr[i]);
                    i++;
                }
                zzu = (zzu + i2) + (this.zzcr.length * 1);
            }
            return this.zzcs != null ? zzu + zzbxm.zze(5, this.zzcs.longValue()) : zzu;
        }
    }

    public static final class zzc extends zzbxn<zzc> {
        public byte[] zzct;
        public byte[] zzcu;

        public zzc() {
            this.zzct = null;
            this.zzcu = null;
            this.zzcuR = -1;
        }

        public void zza(zzbxm com_google_android_gms_internal_zzbxm) throws IOException {
            if (this.zzct != null) {
                com_google_android_gms_internal_zzbxm.zzb(1, this.zzct);
            }
            if (this.zzcu != null) {
                com_google_android_gms_internal_zzbxm.zzb(2, this.zzcu);
            }
            super.zza(com_google_android_gms_internal_zzbxm);
        }

        public /* synthetic */ zzbxt zzb(zzbxl com_google_android_gms_internal_zzbxl) throws IOException {
            return zzj(com_google_android_gms_internal_zzbxl);
        }

        public zzc zzj(zzbxl com_google_android_gms_internal_zzbxl) throws IOException {
            while (true) {
                int zzaeo = com_google_android_gms_internal_zzbxl.zzaeo();
                switch (zzaeo) {
                    case 0:
                        break;
                    case 10:
                        this.zzct = com_google_android_gms_internal_zzbxl.readBytes();
                        continue;
                    case 18:
                        this.zzcu = com_google_android_gms_internal_zzbxl.readBytes();
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

        protected int zzu() {
            int zzu = super.zzu();
            if (this.zzct != null) {
                zzu += zzbxm.zzc(1, this.zzct);
            }
            return this.zzcu != null ? zzu + zzbxm.zzc(2, this.zzcu) : zzu;
        }
    }

    public static final class zzd extends zzbxn<zzd> {
        public byte[] data;
        public byte[] zzcv;
        public byte[] zzcw;
        public byte[] zzcx;

        public zzd() {
            this.data = null;
            this.zzcv = null;
            this.zzcw = null;
            this.zzcx = null;
            this.zzcuR = -1;
        }

        public static zzd zze(byte[] bArr) throws zzbxs {
            return (zzd) zzbxt.zza(new zzd(), bArr);
        }

        public void zza(zzbxm com_google_android_gms_internal_zzbxm) throws IOException {
            if (this.data != null) {
                com_google_android_gms_internal_zzbxm.zzb(1, this.data);
            }
            if (this.zzcv != null) {
                com_google_android_gms_internal_zzbxm.zzb(2, this.zzcv);
            }
            if (this.zzcw != null) {
                com_google_android_gms_internal_zzbxm.zzb(3, this.zzcw);
            }
            if (this.zzcx != null) {
                com_google_android_gms_internal_zzbxm.zzb(4, this.zzcx);
            }
            super.zza(com_google_android_gms_internal_zzbxm);
        }

        public /* synthetic */ zzbxt zzb(zzbxl com_google_android_gms_internal_zzbxl) throws IOException {
            return zzk(com_google_android_gms_internal_zzbxl);
        }

        public zzd zzk(zzbxl com_google_android_gms_internal_zzbxl) throws IOException {
            while (true) {
                int zzaeo = com_google_android_gms_internal_zzbxl.zzaeo();
                switch (zzaeo) {
                    case 0:
                        break;
                    case 10:
                        this.data = com_google_android_gms_internal_zzbxl.readBytes();
                        continue;
                    case 18:
                        this.zzcv = com_google_android_gms_internal_zzbxl.readBytes();
                        continue;
                    case NalUnitTypes.NAL_TYPE_RSV_VCL26 /*26*/:
                        this.zzcw = com_google_android_gms_internal_zzbxl.readBytes();
                        continue;
                    case 34:
                        this.zzcx = com_google_android_gms_internal_zzbxl.readBytes();
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

        protected int zzu() {
            int zzu = super.zzu();
            if (this.data != null) {
                zzu += zzbxm.zzc(1, this.data);
            }
            if (this.zzcv != null) {
                zzu += zzbxm.zzc(2, this.zzcv);
            }
            if (this.zzcw != null) {
                zzu += zzbxm.zzc(3, this.zzcw);
            }
            return this.zzcx != null ? zzu + zzbxm.zzc(4, this.zzcx) : zzu;
        }
    }

    public static final class zze extends zzbxn<zze> {
        public Long zzco;
        public String zzcy;
        public byte[] zzcz;

        public zze() {
            this.zzco = null;
            this.zzcy = null;
            this.zzcz = null;
            this.zzcuR = -1;
        }

        public void zza(zzbxm com_google_android_gms_internal_zzbxm) throws IOException {
            if (this.zzco != null) {
                com_google_android_gms_internal_zzbxm.zzb(1, this.zzco.longValue());
            }
            if (this.zzcy != null) {
                com_google_android_gms_internal_zzbxm.zzq(3, this.zzcy);
            }
            if (this.zzcz != null) {
                com_google_android_gms_internal_zzbxm.zzb(4, this.zzcz);
            }
            super.zza(com_google_android_gms_internal_zzbxm);
        }

        public /* synthetic */ zzbxt zzb(zzbxl com_google_android_gms_internal_zzbxl) throws IOException {
            return zzl(com_google_android_gms_internal_zzbxl);
        }

        public zze zzl(zzbxl com_google_android_gms_internal_zzbxl) throws IOException {
            while (true) {
                int zzaeo = com_google_android_gms_internal_zzbxl.zzaeo();
                switch (zzaeo) {
                    case 0:
                        break;
                    case 8:
                        this.zzco = Long.valueOf(com_google_android_gms_internal_zzbxl.zzaer());
                        continue;
                    case NalUnitTypes.NAL_TYPE_RSV_VCL26 /*26*/:
                        this.zzcy = com_google_android_gms_internal_zzbxl.readString();
                        continue;
                    case 34:
                        this.zzcz = com_google_android_gms_internal_zzbxl.readBytes();
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

        protected int zzu() {
            int zzu = super.zzu();
            if (this.zzco != null) {
                zzu += zzbxm.zzf(1, this.zzco.longValue());
            }
            if (this.zzcy != null) {
                zzu += zzbxm.zzr(3, this.zzcy);
            }
            return this.zzcz != null ? zzu + zzbxm.zzc(4, this.zzcz) : zzu;
        }
    }

    public static final class zzf extends zzbxn<zzf> {
        public byte[][] zzcA;
        public Integer zzcB;
        public Integer zzcC;
        public byte[] zzcv;

        public zzf() {
            this.zzcA = zzbxw.zzcvc;
            this.zzcv = null;
            this.zzcB = null;
            this.zzcC = null;
            this.zzcuR = -1;
        }

        public void zza(zzbxm com_google_android_gms_internal_zzbxm) throws IOException {
            if (this.zzcA != null && this.zzcA.length > 0) {
                for (byte[] bArr : this.zzcA) {
                    if (bArr != null) {
                        com_google_android_gms_internal_zzbxm.zzb(1, bArr);
                    }
                }
            }
            if (this.zzcv != null) {
                com_google_android_gms_internal_zzbxm.zzb(2, this.zzcv);
            }
            if (this.zzcB != null) {
                com_google_android_gms_internal_zzbxm.zzJ(3, this.zzcB.intValue());
            }
            if (this.zzcC != null) {
                com_google_android_gms_internal_zzbxm.zzJ(4, this.zzcC.intValue());
            }
            super.zza(com_google_android_gms_internal_zzbxm);
        }

        public /* synthetic */ zzbxt zzb(zzbxl com_google_android_gms_internal_zzbxl) throws IOException {
            return zzm(com_google_android_gms_internal_zzbxl);
        }

        public zzf zzm(zzbxl com_google_android_gms_internal_zzbxl) throws IOException {
            while (true) {
                int zzaeo = com_google_android_gms_internal_zzbxl.zzaeo();
                switch (zzaeo) {
                    case 0:
                        break;
                    case 10:
                        int zzb = zzbxw.zzb(com_google_android_gms_internal_zzbxl, 10);
                        zzaeo = this.zzcA == null ? 0 : this.zzcA.length;
                        Object obj = new byte[(zzb + zzaeo)][];
                        if (zzaeo != 0) {
                            System.arraycopy(this.zzcA, 0, obj, 0, zzaeo);
                        }
                        while (zzaeo < obj.length - 1) {
                            obj[zzaeo] = com_google_android_gms_internal_zzbxl.readBytes();
                            com_google_android_gms_internal_zzbxl.zzaeo();
                            zzaeo++;
                        }
                        obj[zzaeo] = com_google_android_gms_internal_zzbxl.readBytes();
                        this.zzcA = obj;
                        continue;
                    case 18:
                        this.zzcv = com_google_android_gms_internal_zzbxl.readBytes();
                        continue;
                    case 24:
                        zzaeo = com_google_android_gms_internal_zzbxl.zzaes();
                        switch (zzaeo) {
                            case 0:
                            case 1:
                                this.zzcB = Integer.valueOf(zzaeo);
                                break;
                            default:
                                continue;
                        }
                    case 32:
                        zzaeo = com_google_android_gms_internal_zzbxl.zzaes();
                        switch (zzaeo) {
                            case 0:
                            case 1:
                                this.zzcC = Integer.valueOf(zzaeo);
                                break;
                            default:
                                continue;
                        }
                    default:
                        if (!super.zza(com_google_android_gms_internal_zzbxl, zzaeo)) {
                            break;
                        }
                        continue;
                }
                return this;
            }
        }

        protected int zzu() {
            int i = 0;
            int zzu = super.zzu();
            if (this.zzcA == null || this.zzcA.length <= 0) {
                i = zzu;
            } else {
                int i2 = 0;
                int i3 = 0;
                while (i < this.zzcA.length) {
                    byte[] bArr = this.zzcA[i];
                    if (bArr != null) {
                        i3++;
                        i2 += zzbxm.zzai(bArr);
                    }
                    i++;
                }
                i = (zzu + i2) + (i3 * 1);
            }
            if (this.zzcv != null) {
                i += zzbxm.zzc(2, this.zzcv);
            }
            if (this.zzcB != null) {
                i += zzbxm.zzL(3, this.zzcB.intValue());
            }
            return this.zzcC != null ? i + zzbxm.zzL(4, this.zzcC.intValue()) : i;
        }
    }
}
