package com.google.android.gms.internal;

import android.support.v4.view.MotionEventCompat;
import com.googlecode.mp4parser.authoring.tracks.h265.NalUnitTypes;
import com.googlecode.mp4parser.boxes.microsoft.XtraBox;
import java.io.IOException;
import org.telegram.messenger.support.widget.helper.ItemTouchHelper.Callback;

public interface zzaf {

    public static final class zza extends zzbun<zza> {
        public String zzaM;
        public String zzaO;
        public String zzaP;
        public String zzaQ;
        public String zzaZ;
        public Long zzbA;
        public Long zzbB;
        public zzb zzbC;
        public Long zzbD;
        public Long zzbE;
        public Long zzbF;
        public Long zzbG;
        public Long zzbH;
        public Long zzbI;
        public Integer zzbJ;
        public Integer zzbK;
        public Long zzbL;
        public Long zzbM;
        public Long zzbN;
        public Long zzbO;
        public Long zzbP;
        public Integer zzbQ;
        public zza zzbR;
        public zza[] zzbS;
        public zzb zzbT;
        public Long zzbU;
        public String zzbV;
        public Integer zzbW;
        public Boolean zzbX;
        public String zzbY;
        public Long zzbZ;
        public String zzba;
        public Long zzbb;
        public Long zzbc;
        public Long zzbd;
        public Long zzbe;
        public Long zzbf;
        public Long zzbg;
        public Long zzbh;
        public Long zzbi;
        public Long zzbj;
        public Long zzbk;
        public String zzbl;
        public Long zzbm;
        public Long zzbn;
        public Long zzbo;
        public Long zzbp;
        public Long zzbq;
        public Long zzbr;
        public Long zzbs;
        public Long zzbt;
        public Long zzbu;
        public String zzbv;
        public Long zzbw;
        public Long zzbx;
        public Long zzby;
        public Long zzbz;
        public zze zzca;

        public static final class zza extends zzbun<zza> {
            private static volatile zza[] zzcb;
            public Long zzbm;
            public Long zzbn;
            public Long zzcc;
            public Long zzcd;
            public Long zzce;
            public Long zzcf;
            public Integer zzcg;
            public Long zzch;
            public Long zzci;
            public Long zzcj;
            public Integer zzck;
            public Long zzcl;

            public zza() {
                this.zzbm = null;
                this.zzbn = null;
                this.zzcc = null;
                this.zzcd = null;
                this.zzce = null;
                this.zzcf = null;
                this.zzcg = null;
                this.zzch = null;
                this.zzci = null;
                this.zzcj = null;
                this.zzck = null;
                this.zzcl = null;
                this.zzcsg = -1;
            }

            public static zza[] zzy() {
                if (zzcb == null) {
                    synchronized (zzbur.zzcsf) {
                        if (zzcb == null) {
                            zzcb = new zza[0];
                        }
                    }
                }
                return zzcb;
            }

            public void zza(zzbum com_google_android_gms_internal_zzbum) throws IOException {
                if (this.zzbm != null) {
                    com_google_android_gms_internal_zzbum.zzb(1, this.zzbm.longValue());
                }
                if (this.zzbn != null) {
                    com_google_android_gms_internal_zzbum.zzb(2, this.zzbn.longValue());
                }
                if (this.zzcc != null) {
                    com_google_android_gms_internal_zzbum.zzb(3, this.zzcc.longValue());
                }
                if (this.zzcd != null) {
                    com_google_android_gms_internal_zzbum.zzb(4, this.zzcd.longValue());
                }
                if (this.zzce != null) {
                    com_google_android_gms_internal_zzbum.zzb(5, this.zzce.longValue());
                }
                if (this.zzcf != null) {
                    com_google_android_gms_internal_zzbum.zzb(6, this.zzcf.longValue());
                }
                if (this.zzcg != null) {
                    com_google_android_gms_internal_zzbum.zzF(7, this.zzcg.intValue());
                }
                if (this.zzch != null) {
                    com_google_android_gms_internal_zzbum.zzb(8, this.zzch.longValue());
                }
                if (this.zzci != null) {
                    com_google_android_gms_internal_zzbum.zzb(9, this.zzci.longValue());
                }
                if (this.zzcj != null) {
                    com_google_android_gms_internal_zzbum.zzb(10, this.zzcj.longValue());
                }
                if (this.zzck != null) {
                    com_google_android_gms_internal_zzbum.zzF(11, this.zzck.intValue());
                }
                if (this.zzcl != null) {
                    com_google_android_gms_internal_zzbum.zzb(12, this.zzcl.longValue());
                }
                super.zza(com_google_android_gms_internal_zzbum);
            }

            public /* synthetic */ zzbut zzb(zzbul com_google_android_gms_internal_zzbul) throws IOException {
                return zzg(com_google_android_gms_internal_zzbul);
            }

            public zza zzg(zzbul com_google_android_gms_internal_zzbul) throws IOException {
                while (true) {
                    int zzacu = com_google_android_gms_internal_zzbul.zzacu();
                    switch (zzacu) {
                        case 0:
                            break;
                        case 8:
                            this.zzbm = Long.valueOf(com_google_android_gms_internal_zzbul.zzacx());
                            continue;
                        case 16:
                            this.zzbn = Long.valueOf(com_google_android_gms_internal_zzbul.zzacx());
                            continue;
                        case 24:
                            this.zzcc = Long.valueOf(com_google_android_gms_internal_zzbul.zzacx());
                            continue;
                        case 32:
                            this.zzcd = Long.valueOf(com_google_android_gms_internal_zzbul.zzacx());
                            continue;
                        case MotionEventCompat.AXIS_GENERIC_9 /*40*/:
                            this.zzce = Long.valueOf(com_google_android_gms_internal_zzbul.zzacx());
                            continue;
                        case 48:
                            this.zzcf = Long.valueOf(com_google_android_gms_internal_zzbul.zzacx());
                            continue;
                        case 56:
                            zzacu = com_google_android_gms_internal_zzbul.zzacy();
                            switch (zzacu) {
                                case 0:
                                case 1:
                                case 2:
                                case 1000:
                                    this.zzcg = Integer.valueOf(zzacu);
                                    break;
                                default:
                                    continue;
                            }
                        case 64:
                            this.zzch = Long.valueOf(com_google_android_gms_internal_zzbul.zzacx());
                            continue;
                        case XtraBox.MP4_XTRA_BT_GUID /*72*/:
                            this.zzci = Long.valueOf(com_google_android_gms_internal_zzbul.zzacx());
                            continue;
                        case 80:
                            this.zzcj = Long.valueOf(com_google_android_gms_internal_zzbul.zzacx());
                            continue;
                        case 88:
                            zzacu = com_google_android_gms_internal_zzbul.zzacy();
                            switch (zzacu) {
                                case 0:
                                case 1:
                                case 2:
                                case 1000:
                                    this.zzck = Integer.valueOf(zzacu);
                                    break;
                                default:
                                    continue;
                            }
                        case 96:
                            this.zzcl = Long.valueOf(com_google_android_gms_internal_zzbul.zzacx());
                            continue;
                        default:
                            if (!super.zza(com_google_android_gms_internal_zzbul, zzacu)) {
                                break;
                            }
                            continue;
                    }
                    return this;
                }
            }

            protected int zzv() {
                int zzv = super.zzv();
                if (this.zzbm != null) {
                    zzv += zzbum.zzf(1, this.zzbm.longValue());
                }
                if (this.zzbn != null) {
                    zzv += zzbum.zzf(2, this.zzbn.longValue());
                }
                if (this.zzcc != null) {
                    zzv += zzbum.zzf(3, this.zzcc.longValue());
                }
                if (this.zzcd != null) {
                    zzv += zzbum.zzf(4, this.zzcd.longValue());
                }
                if (this.zzce != null) {
                    zzv += zzbum.zzf(5, this.zzce.longValue());
                }
                if (this.zzcf != null) {
                    zzv += zzbum.zzf(6, this.zzcf.longValue());
                }
                if (this.zzcg != null) {
                    zzv += zzbum.zzH(7, this.zzcg.intValue());
                }
                if (this.zzch != null) {
                    zzv += zzbum.zzf(8, this.zzch.longValue());
                }
                if (this.zzci != null) {
                    zzv += zzbum.zzf(9, this.zzci.longValue());
                }
                if (this.zzcj != null) {
                    zzv += zzbum.zzf(10, this.zzcj.longValue());
                }
                if (this.zzck != null) {
                    zzv += zzbum.zzH(11, this.zzck.intValue());
                }
                return this.zzcl != null ? zzv + zzbum.zzf(12, this.zzcl.longValue()) : zzv;
            }
        }

        public static final class zzb extends zzbun<zzb> {
            public Long zzbO;
            public Long zzbP;
            public Long zzcm;

            public zzb() {
                this.zzbO = null;
                this.zzbP = null;
                this.zzcm = null;
                this.zzcsg = -1;
            }

            public void zza(zzbum com_google_android_gms_internal_zzbum) throws IOException {
                if (this.zzbO != null) {
                    com_google_android_gms_internal_zzbum.zzb(1, this.zzbO.longValue());
                }
                if (this.zzbP != null) {
                    com_google_android_gms_internal_zzbum.zzb(2, this.zzbP.longValue());
                }
                if (this.zzcm != null) {
                    com_google_android_gms_internal_zzbum.zzb(3, this.zzcm.longValue());
                }
                super.zza(com_google_android_gms_internal_zzbum);
            }

            public /* synthetic */ zzbut zzb(zzbul com_google_android_gms_internal_zzbul) throws IOException {
                return zzh(com_google_android_gms_internal_zzbul);
            }

            public zzb zzh(zzbul com_google_android_gms_internal_zzbul) throws IOException {
                while (true) {
                    int zzacu = com_google_android_gms_internal_zzbul.zzacu();
                    switch (zzacu) {
                        case 0:
                            break;
                        case 8:
                            this.zzbO = Long.valueOf(com_google_android_gms_internal_zzbul.zzacx());
                            continue;
                        case 16:
                            this.zzbP = Long.valueOf(com_google_android_gms_internal_zzbul.zzacx());
                            continue;
                        case 24:
                            this.zzcm = Long.valueOf(com_google_android_gms_internal_zzbul.zzacx());
                            continue;
                        default:
                            if (!super.zza(com_google_android_gms_internal_zzbul, zzacu)) {
                                break;
                            }
                            continue;
                    }
                    return this;
                }
            }

            protected int zzv() {
                int zzv = super.zzv();
                if (this.zzbO != null) {
                    zzv += zzbum.zzf(1, this.zzbO.longValue());
                }
                if (this.zzbP != null) {
                    zzv += zzbum.zzf(2, this.zzbP.longValue());
                }
                return this.zzcm != null ? zzv + zzbum.zzf(3, this.zzcm.longValue()) : zzv;
            }
        }

        public zza() {
            this.zzba = null;
            this.zzaZ = null;
            this.zzbb = null;
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
            this.zzaM = null;
            this.zzbv = null;
            this.zzbw = null;
            this.zzbx = null;
            this.zzby = null;
            this.zzaO = null;
            this.zzbz = null;
            this.zzbA = null;
            this.zzbB = null;
            this.zzbD = null;
            this.zzbE = null;
            this.zzbF = null;
            this.zzbG = null;
            this.zzbH = null;
            this.zzbI = null;
            this.zzaP = null;
            this.zzaQ = null;
            this.zzbJ = null;
            this.zzbK = null;
            this.zzbL = null;
            this.zzbM = null;
            this.zzbN = null;
            this.zzbO = null;
            this.zzbP = null;
            this.zzbQ = null;
            this.zzbS = zza.zzy();
            this.zzbU = null;
            this.zzbV = null;
            this.zzbW = null;
            this.zzbX = null;
            this.zzbY = null;
            this.zzbZ = null;
            this.zzcsg = -1;
        }

        public static zza zzd(byte[] bArr) throws zzbus {
            return (zza) zzbut.zza(new zza(), bArr);
        }

        public void zza(zzbum com_google_android_gms_internal_zzbum) throws IOException {
            if (this.zzba != null) {
                com_google_android_gms_internal_zzbum.zzq(1, this.zzba);
            }
            if (this.zzaZ != null) {
                com_google_android_gms_internal_zzbum.zzq(2, this.zzaZ);
            }
            if (this.zzbb != null) {
                com_google_android_gms_internal_zzbum.zzb(3, this.zzbb.longValue());
            }
            if (this.zzbc != null) {
                com_google_android_gms_internal_zzbum.zzb(4, this.zzbc.longValue());
            }
            if (this.zzbd != null) {
                com_google_android_gms_internal_zzbum.zzb(5, this.zzbd.longValue());
            }
            if (this.zzbe != null) {
                com_google_android_gms_internal_zzbum.zzb(6, this.zzbe.longValue());
            }
            if (this.zzbf != null) {
                com_google_android_gms_internal_zzbum.zzb(7, this.zzbf.longValue());
            }
            if (this.zzbg != null) {
                com_google_android_gms_internal_zzbum.zzb(8, this.zzbg.longValue());
            }
            if (this.zzbh != null) {
                com_google_android_gms_internal_zzbum.zzb(9, this.zzbh.longValue());
            }
            if (this.zzbi != null) {
                com_google_android_gms_internal_zzbum.zzb(10, this.zzbi.longValue());
            }
            if (this.zzbj != null) {
                com_google_android_gms_internal_zzbum.zzb(11, this.zzbj.longValue());
            }
            if (this.zzbk != null) {
                com_google_android_gms_internal_zzbum.zzb(12, this.zzbk.longValue());
            }
            if (this.zzbl != null) {
                com_google_android_gms_internal_zzbum.zzq(13, this.zzbl);
            }
            if (this.zzbm != null) {
                com_google_android_gms_internal_zzbum.zzb(14, this.zzbm.longValue());
            }
            if (this.zzbn != null) {
                com_google_android_gms_internal_zzbum.zzb(15, this.zzbn.longValue());
            }
            if (this.zzbo != null) {
                com_google_android_gms_internal_zzbum.zzb(16, this.zzbo.longValue());
            }
            if (this.zzbp != null) {
                com_google_android_gms_internal_zzbum.zzb(17, this.zzbp.longValue());
            }
            if (this.zzbq != null) {
                com_google_android_gms_internal_zzbum.zzb(18, this.zzbq.longValue());
            }
            if (this.zzbr != null) {
                com_google_android_gms_internal_zzbum.zzb(19, this.zzbr.longValue());
            }
            if (this.zzbs != null) {
                com_google_android_gms_internal_zzbum.zzb(20, this.zzbs.longValue());
            }
            if (this.zzbU != null) {
                com_google_android_gms_internal_zzbum.zzb(21, this.zzbU.longValue());
            }
            if (this.zzbt != null) {
                com_google_android_gms_internal_zzbum.zzb(22, this.zzbt.longValue());
            }
            if (this.zzbu != null) {
                com_google_android_gms_internal_zzbum.zzb(23, this.zzbu.longValue());
            }
            if (this.zzbV != null) {
                com_google_android_gms_internal_zzbum.zzq(24, this.zzbV);
            }
            if (this.zzbZ != null) {
                com_google_android_gms_internal_zzbum.zzb(25, this.zzbZ.longValue());
            }
            if (this.zzbW != null) {
                com_google_android_gms_internal_zzbum.zzF(26, this.zzbW.intValue());
            }
            if (this.zzaM != null) {
                com_google_android_gms_internal_zzbum.zzq(27, this.zzaM);
            }
            if (this.zzbX != null) {
                com_google_android_gms_internal_zzbum.zzg(28, this.zzbX.booleanValue());
            }
            if (this.zzbv != null) {
                com_google_android_gms_internal_zzbum.zzq(29, this.zzbv);
            }
            if (this.zzbY != null) {
                com_google_android_gms_internal_zzbum.zzq(30, this.zzbY);
            }
            if (this.zzbw != null) {
                com_google_android_gms_internal_zzbum.zzb(31, this.zzbw.longValue());
            }
            if (this.zzbx != null) {
                com_google_android_gms_internal_zzbum.zzb(32, this.zzbx.longValue());
            }
            if (this.zzby != null) {
                com_google_android_gms_internal_zzbum.zzb(33, this.zzby.longValue());
            }
            if (this.zzaO != null) {
                com_google_android_gms_internal_zzbum.zzq(34, this.zzaO);
            }
            if (this.zzbz != null) {
                com_google_android_gms_internal_zzbum.zzb(35, this.zzbz.longValue());
            }
            if (this.zzbA != null) {
                com_google_android_gms_internal_zzbum.zzb(36, this.zzbA.longValue());
            }
            if (this.zzbB != null) {
                com_google_android_gms_internal_zzbum.zzb(37, this.zzbB.longValue());
            }
            if (this.zzbC != null) {
                com_google_android_gms_internal_zzbum.zza(38, this.zzbC);
            }
            if (this.zzbD != null) {
                com_google_android_gms_internal_zzbum.zzb(39, this.zzbD.longValue());
            }
            if (this.zzbE != null) {
                com_google_android_gms_internal_zzbum.zzb(40, this.zzbE.longValue());
            }
            if (this.zzbF != null) {
                com_google_android_gms_internal_zzbum.zzb(41, this.zzbF.longValue());
            }
            if (this.zzbG != null) {
                com_google_android_gms_internal_zzbum.zzb(42, this.zzbG.longValue());
            }
            if (this.zzbS != null && this.zzbS.length > 0) {
                for (zzbut com_google_android_gms_internal_zzbut : this.zzbS) {
                    if (com_google_android_gms_internal_zzbut != null) {
                        com_google_android_gms_internal_zzbum.zza(43, com_google_android_gms_internal_zzbut);
                    }
                }
            }
            if (this.zzbH != null) {
                com_google_android_gms_internal_zzbum.zzb(44, this.zzbH.longValue());
            }
            if (this.zzbI != null) {
                com_google_android_gms_internal_zzbum.zzb(45, this.zzbI.longValue());
            }
            if (this.zzaP != null) {
                com_google_android_gms_internal_zzbum.zzq(46, this.zzaP);
            }
            if (this.zzaQ != null) {
                com_google_android_gms_internal_zzbum.zzq(47, this.zzaQ);
            }
            if (this.zzbJ != null) {
                com_google_android_gms_internal_zzbum.zzF(48, this.zzbJ.intValue());
            }
            if (this.zzbK != null) {
                com_google_android_gms_internal_zzbum.zzF(49, this.zzbK.intValue());
            }
            if (this.zzbR != null) {
                com_google_android_gms_internal_zzbum.zza(50, this.zzbR);
            }
            if (this.zzbL != null) {
                com_google_android_gms_internal_zzbum.zzb(51, this.zzbL.longValue());
            }
            if (this.zzbM != null) {
                com_google_android_gms_internal_zzbum.zzb(52, this.zzbM.longValue());
            }
            if (this.zzbN != null) {
                com_google_android_gms_internal_zzbum.zzb(53, this.zzbN.longValue());
            }
            if (this.zzbO != null) {
                com_google_android_gms_internal_zzbum.zzb(54, this.zzbO.longValue());
            }
            if (this.zzbP != null) {
                com_google_android_gms_internal_zzbum.zzb(55, this.zzbP.longValue());
            }
            if (this.zzbQ != null) {
                com_google_android_gms_internal_zzbum.zzF(56, this.zzbQ.intValue());
            }
            if (this.zzbT != null) {
                com_google_android_gms_internal_zzbum.zza(57, this.zzbT);
            }
            if (this.zzca != null) {
                com_google_android_gms_internal_zzbum.zza(201, this.zzca);
            }
            super.zza(com_google_android_gms_internal_zzbum);
        }

        public /* synthetic */ zzbut zzb(zzbul com_google_android_gms_internal_zzbul) throws IOException {
            return zzf(com_google_android_gms_internal_zzbul);
        }

        public zza zzf(zzbul com_google_android_gms_internal_zzbul) throws IOException {
            while (true) {
                int zzacu = com_google_android_gms_internal_zzbul.zzacu();
                switch (zzacu) {
                    case 0:
                        break;
                    case 10:
                        this.zzba = com_google_android_gms_internal_zzbul.readString();
                        continue;
                    case 18:
                        this.zzaZ = com_google_android_gms_internal_zzbul.readString();
                        continue;
                    case 24:
                        this.zzbb = Long.valueOf(com_google_android_gms_internal_zzbul.zzacx());
                        continue;
                    case 32:
                        this.zzbc = Long.valueOf(com_google_android_gms_internal_zzbul.zzacx());
                        continue;
                    case MotionEventCompat.AXIS_GENERIC_9 /*40*/:
                        this.zzbd = Long.valueOf(com_google_android_gms_internal_zzbul.zzacx());
                        continue;
                    case 48:
                        this.zzbe = Long.valueOf(com_google_android_gms_internal_zzbul.zzacx());
                        continue;
                    case 56:
                        this.zzbf = Long.valueOf(com_google_android_gms_internal_zzbul.zzacx());
                        continue;
                    case 64:
                        this.zzbg = Long.valueOf(com_google_android_gms_internal_zzbul.zzacx());
                        continue;
                    case XtraBox.MP4_XTRA_BT_GUID /*72*/:
                        this.zzbh = Long.valueOf(com_google_android_gms_internal_zzbul.zzacx());
                        continue;
                    case 80:
                        this.zzbi = Long.valueOf(com_google_android_gms_internal_zzbul.zzacx());
                        continue;
                    case 88:
                        this.zzbj = Long.valueOf(com_google_android_gms_internal_zzbul.zzacx());
                        continue;
                    case 96:
                        this.zzbk = Long.valueOf(com_google_android_gms_internal_zzbul.zzacx());
                        continue;
                    case 106:
                        this.zzbl = com_google_android_gms_internal_zzbul.readString();
                        continue;
                    case 112:
                        this.zzbm = Long.valueOf(com_google_android_gms_internal_zzbul.zzacx());
                        continue;
                    case 120:
                        this.zzbn = Long.valueOf(com_google_android_gms_internal_zzbul.zzacx());
                        continue;
                    case 128:
                        this.zzbo = Long.valueOf(com_google_android_gms_internal_zzbul.zzacx());
                        continue;
                    case 136:
                        this.zzbp = Long.valueOf(com_google_android_gms_internal_zzbul.zzacx());
                        continue;
                    case 144:
                        this.zzbq = Long.valueOf(com_google_android_gms_internal_zzbul.zzacx());
                        continue;
                    case 152:
                        this.zzbr = Long.valueOf(com_google_android_gms_internal_zzbul.zzacx());
                        continue;
                    case 160:
                        this.zzbs = Long.valueOf(com_google_android_gms_internal_zzbul.zzacx());
                        continue;
                    case 168:
                        this.zzbU = Long.valueOf(com_google_android_gms_internal_zzbul.zzacx());
                        continue;
                    case 176:
                        this.zzbt = Long.valueOf(com_google_android_gms_internal_zzbul.zzacx());
                        continue;
                    case 184:
                        this.zzbu = Long.valueOf(com_google_android_gms_internal_zzbul.zzacx());
                        continue;
                    case 194:
                        this.zzbV = com_google_android_gms_internal_zzbul.readString();
                        continue;
                    case Callback.DEFAULT_DRAG_ANIMATION_DURATION /*200*/:
                        this.zzbZ = Long.valueOf(com_google_android_gms_internal_zzbul.zzacx());
                        continue;
                    case 208:
                        zzacu = com_google_android_gms_internal_zzbul.zzacy();
                        switch (zzacu) {
                            case 0:
                            case 1:
                            case 2:
                            case 3:
                            case 4:
                            case 5:
                            case 6:
                                this.zzbW = Integer.valueOf(zzacu);
                                break;
                            default:
                                continue;
                        }
                    case 218:
                        this.zzaM = com_google_android_gms_internal_zzbul.readString();
                        continue;
                    case 224:
                        this.zzbX = Boolean.valueOf(com_google_android_gms_internal_zzbul.zzacA());
                        continue;
                    case 234:
                        this.zzbv = com_google_android_gms_internal_zzbul.readString();
                        continue;
                    case 242:
                        this.zzbY = com_google_android_gms_internal_zzbul.readString();
                        continue;
                    case 248:
                        this.zzbw = Long.valueOf(com_google_android_gms_internal_zzbul.zzacx());
                        continue;
                    case 256:
                        this.zzbx = Long.valueOf(com_google_android_gms_internal_zzbul.zzacx());
                        continue;
                    case 264:
                        this.zzby = Long.valueOf(com_google_android_gms_internal_zzbul.zzacx());
                        continue;
                    case 274:
                        this.zzaO = com_google_android_gms_internal_zzbul.readString();
                        continue;
                    case 280:
                        this.zzbz = Long.valueOf(com_google_android_gms_internal_zzbul.zzacx());
                        continue;
                    case 288:
                        this.zzbA = Long.valueOf(com_google_android_gms_internal_zzbul.zzacx());
                        continue;
                    case 296:
                        this.zzbB = Long.valueOf(com_google_android_gms_internal_zzbul.zzacx());
                        continue;
                    case 306:
                        if (this.zzbC == null) {
                            this.zzbC = new zzb();
                        }
                        com_google_android_gms_internal_zzbul.zza(this.zzbC);
                        continue;
                    case 312:
                        this.zzbD = Long.valueOf(com_google_android_gms_internal_zzbul.zzacx());
                        continue;
                    case 320:
                        this.zzbE = Long.valueOf(com_google_android_gms_internal_zzbul.zzacx());
                        continue;
                    case 328:
                        this.zzbF = Long.valueOf(com_google_android_gms_internal_zzbul.zzacx());
                        continue;
                    case 336:
                        this.zzbG = Long.valueOf(com_google_android_gms_internal_zzbul.zzacx());
                        continue;
                    case 346:
                        int zzc = zzbuw.zzc(com_google_android_gms_internal_zzbul, 346);
                        zzacu = this.zzbS == null ? 0 : this.zzbS.length;
                        Object obj = new zza[(zzc + zzacu)];
                        if (zzacu != 0) {
                            System.arraycopy(this.zzbS, 0, obj, 0, zzacu);
                        }
                        while (zzacu < obj.length - 1) {
                            obj[zzacu] = new zza();
                            com_google_android_gms_internal_zzbul.zza(obj[zzacu]);
                            com_google_android_gms_internal_zzbul.zzacu();
                            zzacu++;
                        }
                        obj[zzacu] = new zza();
                        com_google_android_gms_internal_zzbul.zza(obj[zzacu]);
                        this.zzbS = obj;
                        continue;
                    case 352:
                        this.zzbH = Long.valueOf(com_google_android_gms_internal_zzbul.zzacx());
                        continue;
                    case 360:
                        this.zzbI = Long.valueOf(com_google_android_gms_internal_zzbul.zzacx());
                        continue;
                    case 370:
                        this.zzaP = com_google_android_gms_internal_zzbul.readString();
                        continue;
                    case 378:
                        this.zzaQ = com_google_android_gms_internal_zzbul.readString();
                        continue;
                    case 384:
                        zzacu = com_google_android_gms_internal_zzbul.zzacy();
                        switch (zzacu) {
                            case 0:
                            case 1:
                            case 2:
                            case 1000:
                                this.zzbJ = Integer.valueOf(zzacu);
                                break;
                            default:
                                continue;
                        }
                    case 392:
                        zzacu = com_google_android_gms_internal_zzbul.zzacy();
                        switch (zzacu) {
                            case 0:
                            case 1:
                            case 2:
                            case 1000:
                                this.zzbK = Integer.valueOf(zzacu);
                                break;
                            default:
                                continue;
                        }
                    case 402:
                        if (this.zzbR == null) {
                            this.zzbR = new zza();
                        }
                        com_google_android_gms_internal_zzbul.zza(this.zzbR);
                        continue;
                    case 408:
                        this.zzbL = Long.valueOf(com_google_android_gms_internal_zzbul.zzacx());
                        continue;
                    case 416:
                        this.zzbM = Long.valueOf(com_google_android_gms_internal_zzbul.zzacx());
                        continue;
                    case 424:
                        this.zzbN = Long.valueOf(com_google_android_gms_internal_zzbul.zzacx());
                        continue;
                    case 432:
                        this.zzbO = Long.valueOf(com_google_android_gms_internal_zzbul.zzacx());
                        continue;
                    case 440:
                        this.zzbP = Long.valueOf(com_google_android_gms_internal_zzbul.zzacx());
                        continue;
                    case 448:
                        zzacu = com_google_android_gms_internal_zzbul.zzacy();
                        switch (zzacu) {
                            case 0:
                            case 1:
                            case 2:
                            case 1000:
                                this.zzbQ = Integer.valueOf(zzacu);
                                break;
                            default:
                                continue;
                        }
                    case 458:
                        if (this.zzbT == null) {
                            this.zzbT = new zzb();
                        }
                        com_google_android_gms_internal_zzbul.zza(this.zzbT);
                        continue;
                    case 1610:
                        if (this.zzca == null) {
                            this.zzca = new zze();
                        }
                        com_google_android_gms_internal_zzbul.zza(this.zzca);
                        continue;
                    default:
                        if (!super.zza(com_google_android_gms_internal_zzbul, zzacu)) {
                            break;
                        }
                        continue;
                }
                return this;
            }
        }

        protected int zzv() {
            int zzv = super.zzv();
            if (this.zzba != null) {
                zzv += zzbum.zzr(1, this.zzba);
            }
            if (this.zzaZ != null) {
                zzv += zzbum.zzr(2, this.zzaZ);
            }
            if (this.zzbb != null) {
                zzv += zzbum.zzf(3, this.zzbb.longValue());
            }
            if (this.zzbc != null) {
                zzv += zzbum.zzf(4, this.zzbc.longValue());
            }
            if (this.zzbd != null) {
                zzv += zzbum.zzf(5, this.zzbd.longValue());
            }
            if (this.zzbe != null) {
                zzv += zzbum.zzf(6, this.zzbe.longValue());
            }
            if (this.zzbf != null) {
                zzv += zzbum.zzf(7, this.zzbf.longValue());
            }
            if (this.zzbg != null) {
                zzv += zzbum.zzf(8, this.zzbg.longValue());
            }
            if (this.zzbh != null) {
                zzv += zzbum.zzf(9, this.zzbh.longValue());
            }
            if (this.zzbi != null) {
                zzv += zzbum.zzf(10, this.zzbi.longValue());
            }
            if (this.zzbj != null) {
                zzv += zzbum.zzf(11, this.zzbj.longValue());
            }
            if (this.zzbk != null) {
                zzv += zzbum.zzf(12, this.zzbk.longValue());
            }
            if (this.zzbl != null) {
                zzv += zzbum.zzr(13, this.zzbl);
            }
            if (this.zzbm != null) {
                zzv += zzbum.zzf(14, this.zzbm.longValue());
            }
            if (this.zzbn != null) {
                zzv += zzbum.zzf(15, this.zzbn.longValue());
            }
            if (this.zzbo != null) {
                zzv += zzbum.zzf(16, this.zzbo.longValue());
            }
            if (this.zzbp != null) {
                zzv += zzbum.zzf(17, this.zzbp.longValue());
            }
            if (this.zzbq != null) {
                zzv += zzbum.zzf(18, this.zzbq.longValue());
            }
            if (this.zzbr != null) {
                zzv += zzbum.zzf(19, this.zzbr.longValue());
            }
            if (this.zzbs != null) {
                zzv += zzbum.zzf(20, this.zzbs.longValue());
            }
            if (this.zzbU != null) {
                zzv += zzbum.zzf(21, this.zzbU.longValue());
            }
            if (this.zzbt != null) {
                zzv += zzbum.zzf(22, this.zzbt.longValue());
            }
            if (this.zzbu != null) {
                zzv += zzbum.zzf(23, this.zzbu.longValue());
            }
            if (this.zzbV != null) {
                zzv += zzbum.zzr(24, this.zzbV);
            }
            if (this.zzbZ != null) {
                zzv += zzbum.zzf(25, this.zzbZ.longValue());
            }
            if (this.zzbW != null) {
                zzv += zzbum.zzH(26, this.zzbW.intValue());
            }
            if (this.zzaM != null) {
                zzv += zzbum.zzr(27, this.zzaM);
            }
            if (this.zzbX != null) {
                zzv += zzbum.zzh(28, this.zzbX.booleanValue());
            }
            if (this.zzbv != null) {
                zzv += zzbum.zzr(29, this.zzbv);
            }
            if (this.zzbY != null) {
                zzv += zzbum.zzr(30, this.zzbY);
            }
            if (this.zzbw != null) {
                zzv += zzbum.zzf(31, this.zzbw.longValue());
            }
            if (this.zzbx != null) {
                zzv += zzbum.zzf(32, this.zzbx.longValue());
            }
            if (this.zzby != null) {
                zzv += zzbum.zzf(33, this.zzby.longValue());
            }
            if (this.zzaO != null) {
                zzv += zzbum.zzr(34, this.zzaO);
            }
            if (this.zzbz != null) {
                zzv += zzbum.zzf(35, this.zzbz.longValue());
            }
            if (this.zzbA != null) {
                zzv += zzbum.zzf(36, this.zzbA.longValue());
            }
            if (this.zzbB != null) {
                zzv += zzbum.zzf(37, this.zzbB.longValue());
            }
            if (this.zzbC != null) {
                zzv += zzbum.zzc(38, this.zzbC);
            }
            if (this.zzbD != null) {
                zzv += zzbum.zzf(39, this.zzbD.longValue());
            }
            if (this.zzbE != null) {
                zzv += zzbum.zzf(40, this.zzbE.longValue());
            }
            if (this.zzbF != null) {
                zzv += zzbum.zzf(41, this.zzbF.longValue());
            }
            if (this.zzbG != null) {
                zzv += zzbum.zzf(42, this.zzbG.longValue());
            }
            if (this.zzbS != null && this.zzbS.length > 0) {
                int i = zzv;
                for (zzbut com_google_android_gms_internal_zzbut : this.zzbS) {
                    if (com_google_android_gms_internal_zzbut != null) {
                        i += zzbum.zzc(43, com_google_android_gms_internal_zzbut);
                    }
                }
                zzv = i;
            }
            if (this.zzbH != null) {
                zzv += zzbum.zzf(44, this.zzbH.longValue());
            }
            if (this.zzbI != null) {
                zzv += zzbum.zzf(45, this.zzbI.longValue());
            }
            if (this.zzaP != null) {
                zzv += zzbum.zzr(46, this.zzaP);
            }
            if (this.zzaQ != null) {
                zzv += zzbum.zzr(47, this.zzaQ);
            }
            if (this.zzbJ != null) {
                zzv += zzbum.zzH(48, this.zzbJ.intValue());
            }
            if (this.zzbK != null) {
                zzv += zzbum.zzH(49, this.zzbK.intValue());
            }
            if (this.zzbR != null) {
                zzv += zzbum.zzc(50, this.zzbR);
            }
            if (this.zzbL != null) {
                zzv += zzbum.zzf(51, this.zzbL.longValue());
            }
            if (this.zzbM != null) {
                zzv += zzbum.zzf(52, this.zzbM.longValue());
            }
            if (this.zzbN != null) {
                zzv += zzbum.zzf(53, this.zzbN.longValue());
            }
            if (this.zzbO != null) {
                zzv += zzbum.zzf(54, this.zzbO.longValue());
            }
            if (this.zzbP != null) {
                zzv += zzbum.zzf(55, this.zzbP.longValue());
            }
            if (this.zzbQ != null) {
                zzv += zzbum.zzH(56, this.zzbQ.intValue());
            }
            if (this.zzbT != null) {
                zzv += zzbum.zzc(57, this.zzbT);
            }
            return this.zzca != null ? zzv + zzbum.zzc(201, this.zzca) : zzv;
        }
    }

    public static final class zzb extends zzbun<zzb> {
        public Long zzcn;
        public Integer zzco;
        public Boolean zzcp;
        public int[] zzcq;
        public Long zzcr;

        public zzb() {
            this.zzcn = null;
            this.zzco = null;
            this.zzcp = null;
            this.zzcq = zzbuw.zzcsi;
            this.zzcr = null;
            this.zzcsg = -1;
        }

        public void zza(zzbum com_google_android_gms_internal_zzbum) throws IOException {
            if (this.zzcn != null) {
                com_google_android_gms_internal_zzbum.zzb(1, this.zzcn.longValue());
            }
            if (this.zzco != null) {
                com_google_android_gms_internal_zzbum.zzF(2, this.zzco.intValue());
            }
            if (this.zzcp != null) {
                com_google_android_gms_internal_zzbum.zzg(3, this.zzcp.booleanValue());
            }
            if (this.zzcq != null && this.zzcq.length > 0) {
                for (int zzF : this.zzcq) {
                    com_google_android_gms_internal_zzbum.zzF(4, zzF);
                }
            }
            if (this.zzcr != null) {
                com_google_android_gms_internal_zzbum.zza(5, this.zzcr.longValue());
            }
            super.zza(com_google_android_gms_internal_zzbum);
        }

        public /* synthetic */ zzbut zzb(zzbul com_google_android_gms_internal_zzbul) throws IOException {
            return zzi(com_google_android_gms_internal_zzbul);
        }

        public zzb zzi(zzbul com_google_android_gms_internal_zzbul) throws IOException {
            while (true) {
                int zzacu = com_google_android_gms_internal_zzbul.zzacu();
                int zzc;
                switch (zzacu) {
                    case 0:
                        break;
                    case 8:
                        this.zzcn = Long.valueOf(com_google_android_gms_internal_zzbul.zzacx());
                        continue;
                    case 16:
                        this.zzco = Integer.valueOf(com_google_android_gms_internal_zzbul.zzacy());
                        continue;
                    case 24:
                        this.zzcp = Boolean.valueOf(com_google_android_gms_internal_zzbul.zzacA());
                        continue;
                    case 32:
                        zzc = zzbuw.zzc(com_google_android_gms_internal_zzbul, 32);
                        zzacu = this.zzcq == null ? 0 : this.zzcq.length;
                        Object obj = new int[(zzc + zzacu)];
                        if (zzacu != 0) {
                            System.arraycopy(this.zzcq, 0, obj, 0, zzacu);
                        }
                        while (zzacu < obj.length - 1) {
                            obj[zzacu] = com_google_android_gms_internal_zzbul.zzacy();
                            com_google_android_gms_internal_zzbul.zzacu();
                            zzacu++;
                        }
                        obj[zzacu] = com_google_android_gms_internal_zzbul.zzacy();
                        this.zzcq = obj;
                        continue;
                    case 34:
                        int zzqj = com_google_android_gms_internal_zzbul.zzqj(com_google_android_gms_internal_zzbul.zzacD());
                        zzc = com_google_android_gms_internal_zzbul.getPosition();
                        zzacu = 0;
                        while (com_google_android_gms_internal_zzbul.zzacI() > 0) {
                            com_google_android_gms_internal_zzbul.zzacy();
                            zzacu++;
                        }
                        com_google_android_gms_internal_zzbul.zzql(zzc);
                        zzc = this.zzcq == null ? 0 : this.zzcq.length;
                        Object obj2 = new int[(zzacu + zzc)];
                        if (zzc != 0) {
                            System.arraycopy(this.zzcq, 0, obj2, 0, zzc);
                        }
                        while (zzc < obj2.length) {
                            obj2[zzc] = com_google_android_gms_internal_zzbul.zzacy();
                            zzc++;
                        }
                        this.zzcq = obj2;
                        com_google_android_gms_internal_zzbul.zzqk(zzqj);
                        continue;
                    case MotionEventCompat.AXIS_GENERIC_9 /*40*/:
                        this.zzcr = Long.valueOf(com_google_android_gms_internal_zzbul.zzacw());
                        continue;
                    default:
                        if (!super.zza(com_google_android_gms_internal_zzbul, zzacu)) {
                            break;
                        }
                        continue;
                }
                return this;
            }
        }

        protected int zzv() {
            int i = 0;
            int zzv = super.zzv();
            if (this.zzcn != null) {
                zzv += zzbum.zzf(1, this.zzcn.longValue());
            }
            if (this.zzco != null) {
                zzv += zzbum.zzH(2, this.zzco.intValue());
            }
            if (this.zzcp != null) {
                zzv += zzbum.zzh(3, this.zzcp.booleanValue());
            }
            if (this.zzcq != null && this.zzcq.length > 0) {
                int i2 = 0;
                while (i < this.zzcq.length) {
                    i2 += zzbum.zzqp(this.zzcq[i]);
                    i++;
                }
                zzv = (zzv + i2) + (this.zzcq.length * 1);
            }
            return this.zzcr != null ? zzv + zzbum.zze(5, this.zzcr.longValue()) : zzv;
        }
    }

    public static final class zzc extends zzbun<zzc> {
        public byte[] zzcs;
        public byte[] zzct;

        public zzc() {
            this.zzcs = null;
            this.zzct = null;
            this.zzcsg = -1;
        }

        public void zza(zzbum com_google_android_gms_internal_zzbum) throws IOException {
            if (this.zzcs != null) {
                com_google_android_gms_internal_zzbum.zzb(1, this.zzcs);
            }
            if (this.zzct != null) {
                com_google_android_gms_internal_zzbum.zzb(2, this.zzct);
            }
            super.zza(com_google_android_gms_internal_zzbum);
        }

        public /* synthetic */ zzbut zzb(zzbul com_google_android_gms_internal_zzbul) throws IOException {
            return zzj(com_google_android_gms_internal_zzbul);
        }

        public zzc zzj(zzbul com_google_android_gms_internal_zzbul) throws IOException {
            while (true) {
                int zzacu = com_google_android_gms_internal_zzbul.zzacu();
                switch (zzacu) {
                    case 0:
                        break;
                    case 10:
                        this.zzcs = com_google_android_gms_internal_zzbul.readBytes();
                        continue;
                    case 18:
                        this.zzct = com_google_android_gms_internal_zzbul.readBytes();
                        continue;
                    default:
                        if (!super.zza(com_google_android_gms_internal_zzbul, zzacu)) {
                            break;
                        }
                        continue;
                }
                return this;
            }
        }

        protected int zzv() {
            int zzv = super.zzv();
            if (this.zzcs != null) {
                zzv += zzbum.zzc(1, this.zzcs);
            }
            return this.zzct != null ? zzv + zzbum.zzc(2, this.zzct) : zzv;
        }
    }

    public static final class zzd extends zzbun<zzd> {
        public byte[] data;
        public byte[] zzcu;
        public byte[] zzcv;
        public byte[] zzcw;

        public zzd() {
            this.data = null;
            this.zzcu = null;
            this.zzcv = null;
            this.zzcw = null;
            this.zzcsg = -1;
        }

        public static zzd zze(byte[] bArr) throws zzbus {
            return (zzd) zzbut.zza(new zzd(), bArr);
        }

        public void zza(zzbum com_google_android_gms_internal_zzbum) throws IOException {
            if (this.data != null) {
                com_google_android_gms_internal_zzbum.zzb(1, this.data);
            }
            if (this.zzcu != null) {
                com_google_android_gms_internal_zzbum.zzb(2, this.zzcu);
            }
            if (this.zzcv != null) {
                com_google_android_gms_internal_zzbum.zzb(3, this.zzcv);
            }
            if (this.zzcw != null) {
                com_google_android_gms_internal_zzbum.zzb(4, this.zzcw);
            }
            super.zza(com_google_android_gms_internal_zzbum);
        }

        public /* synthetic */ zzbut zzb(zzbul com_google_android_gms_internal_zzbul) throws IOException {
            return zzk(com_google_android_gms_internal_zzbul);
        }

        public zzd zzk(zzbul com_google_android_gms_internal_zzbul) throws IOException {
            while (true) {
                int zzacu = com_google_android_gms_internal_zzbul.zzacu();
                switch (zzacu) {
                    case 0:
                        break;
                    case 10:
                        this.data = com_google_android_gms_internal_zzbul.readBytes();
                        continue;
                    case 18:
                        this.zzcu = com_google_android_gms_internal_zzbul.readBytes();
                        continue;
                    case NalUnitTypes.NAL_TYPE_RSV_VCL26 /*26*/:
                        this.zzcv = com_google_android_gms_internal_zzbul.readBytes();
                        continue;
                    case 34:
                        this.zzcw = com_google_android_gms_internal_zzbul.readBytes();
                        continue;
                    default:
                        if (!super.zza(com_google_android_gms_internal_zzbul, zzacu)) {
                            break;
                        }
                        continue;
                }
                return this;
            }
        }

        protected int zzv() {
            int zzv = super.zzv();
            if (this.data != null) {
                zzv += zzbum.zzc(1, this.data);
            }
            if (this.zzcu != null) {
                zzv += zzbum.zzc(2, this.zzcu);
            }
            if (this.zzcv != null) {
                zzv += zzbum.zzc(3, this.zzcv);
            }
            return this.zzcw != null ? zzv + zzbum.zzc(4, this.zzcw) : zzv;
        }
    }

    public static final class zze extends zzbun<zze> {
        public Long zzcn;
        public String zzcx;
        public byte[] zzcy;

        public zze() {
            this.zzcn = null;
            this.zzcx = null;
            this.zzcy = null;
            this.zzcsg = -1;
        }

        public void zza(zzbum com_google_android_gms_internal_zzbum) throws IOException {
            if (this.zzcn != null) {
                com_google_android_gms_internal_zzbum.zzb(1, this.zzcn.longValue());
            }
            if (this.zzcx != null) {
                com_google_android_gms_internal_zzbum.zzq(3, this.zzcx);
            }
            if (this.zzcy != null) {
                com_google_android_gms_internal_zzbum.zzb(4, this.zzcy);
            }
            super.zza(com_google_android_gms_internal_zzbum);
        }

        public /* synthetic */ zzbut zzb(zzbul com_google_android_gms_internal_zzbul) throws IOException {
            return zzl(com_google_android_gms_internal_zzbul);
        }

        public zze zzl(zzbul com_google_android_gms_internal_zzbul) throws IOException {
            while (true) {
                int zzacu = com_google_android_gms_internal_zzbul.zzacu();
                switch (zzacu) {
                    case 0:
                        break;
                    case 8:
                        this.zzcn = Long.valueOf(com_google_android_gms_internal_zzbul.zzacx());
                        continue;
                    case NalUnitTypes.NAL_TYPE_RSV_VCL26 /*26*/:
                        this.zzcx = com_google_android_gms_internal_zzbul.readString();
                        continue;
                    case 34:
                        this.zzcy = com_google_android_gms_internal_zzbul.readBytes();
                        continue;
                    default:
                        if (!super.zza(com_google_android_gms_internal_zzbul, zzacu)) {
                            break;
                        }
                        continue;
                }
                return this;
            }
        }

        protected int zzv() {
            int zzv = super.zzv();
            if (this.zzcn != null) {
                zzv += zzbum.zzf(1, this.zzcn.longValue());
            }
            if (this.zzcx != null) {
                zzv += zzbum.zzr(3, this.zzcx);
            }
            return this.zzcy != null ? zzv + zzbum.zzc(4, this.zzcy) : zzv;
        }
    }

    public static final class zzf extends zzbun<zzf> {
        public Integer zzcA;
        public Integer zzcB;
        public byte[] zzcu;
        public byte[][] zzcz;

        public zzf() {
            this.zzcz = zzbuw.zzcso;
            this.zzcu = null;
            this.zzcA = null;
            this.zzcB = null;
            this.zzcsg = -1;
        }

        public void zza(zzbum com_google_android_gms_internal_zzbum) throws IOException {
            if (this.zzcz != null && this.zzcz.length > 0) {
                for (byte[] bArr : this.zzcz) {
                    if (bArr != null) {
                        com_google_android_gms_internal_zzbum.zzb(1, bArr);
                    }
                }
            }
            if (this.zzcu != null) {
                com_google_android_gms_internal_zzbum.zzb(2, this.zzcu);
            }
            if (this.zzcA != null) {
                com_google_android_gms_internal_zzbum.zzF(3, this.zzcA.intValue());
            }
            if (this.zzcB != null) {
                com_google_android_gms_internal_zzbum.zzF(4, this.zzcB.intValue());
            }
            super.zza(com_google_android_gms_internal_zzbum);
        }

        public /* synthetic */ zzbut zzb(zzbul com_google_android_gms_internal_zzbul) throws IOException {
            return zzm(com_google_android_gms_internal_zzbul);
        }

        public zzf zzm(zzbul com_google_android_gms_internal_zzbul) throws IOException {
            while (true) {
                int zzacu = com_google_android_gms_internal_zzbul.zzacu();
                switch (zzacu) {
                    case 0:
                        break;
                    case 10:
                        int zzc = zzbuw.zzc(com_google_android_gms_internal_zzbul, 10);
                        zzacu = this.zzcz == null ? 0 : this.zzcz.length;
                        Object obj = new byte[(zzc + zzacu)][];
                        if (zzacu != 0) {
                            System.arraycopy(this.zzcz, 0, obj, 0, zzacu);
                        }
                        while (zzacu < obj.length - 1) {
                            obj[zzacu] = com_google_android_gms_internal_zzbul.readBytes();
                            com_google_android_gms_internal_zzbul.zzacu();
                            zzacu++;
                        }
                        obj[zzacu] = com_google_android_gms_internal_zzbul.readBytes();
                        this.zzcz = obj;
                        continue;
                    case 18:
                        this.zzcu = com_google_android_gms_internal_zzbul.readBytes();
                        continue;
                    case 24:
                        zzacu = com_google_android_gms_internal_zzbul.zzacy();
                        switch (zzacu) {
                            case 0:
                            case 1:
                                this.zzcA = Integer.valueOf(zzacu);
                                break;
                            default:
                                continue;
                        }
                    case 32:
                        zzacu = com_google_android_gms_internal_zzbul.zzacy();
                        switch (zzacu) {
                            case 0:
                            case 1:
                                this.zzcB = Integer.valueOf(zzacu);
                                break;
                            default:
                                continue;
                        }
                    default:
                        if (!super.zza(com_google_android_gms_internal_zzbul, zzacu)) {
                            break;
                        }
                        continue;
                }
                return this;
            }
        }

        protected int zzv() {
            int i = 0;
            int zzv = super.zzv();
            if (this.zzcz == null || this.zzcz.length <= 0) {
                i = zzv;
            } else {
                int i2 = 0;
                int i3 = 0;
                while (i < this.zzcz.length) {
                    byte[] bArr = this.zzcz[i];
                    if (bArr != null) {
                        i3++;
                        i2 += zzbum.zzag(bArr);
                    }
                    i++;
                }
                i = (zzv + i2) + (i3 * 1);
            }
            if (this.zzcu != null) {
                i += zzbum.zzc(2, this.zzcu);
            }
            if (this.zzcA != null) {
                i += zzbum.zzH(3, this.zzcA.intValue());
            }
            return this.zzcB != null ? i + zzbum.zzH(4, this.zzcB.intValue()) : i;
        }
    }
}
