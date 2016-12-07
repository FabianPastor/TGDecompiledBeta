package com.google.android.gms.internal;

import android.support.v4.view.MotionEventCompat;
import com.googlecode.mp4parser.authoring.tracks.h265.NalUnitTypes;
import com.googlecode.mp4parser.boxes.microsoft.XtraBox;
import java.io.IOException;
import org.telegram.messenger.support.widget.helper.ItemTouchHelper.Callback;

public interface zzae {

    public static final class zza extends zzare<zza> {
        public String zzcs;
        public String zzct;
        public Long zzcu;
        public Long zzcv;
        public Long zzcw;
        public Long zzcx;
        public Long zzcy;
        public Long zzcz;
        public Long zzda;
        public Long zzdb;
        public Long zzdc;
        public Long zzdd;
        public String zzde;
        public Long zzdf;
        public Long zzdg;
        public Long zzdh;
        public Long zzdi;
        public Long zzdj;
        public Long zzdk;
        public Long zzdl;
        public Long zzdm;
        public Long zzdn;
        public String zzdo;
        public String zzdp;
        public Long zzdq;
        public Long zzdr;
        public Long zzds;
        public String zzdt;
        public Long zzdu;
        public Long zzdv;
        public Long zzdw;
        public zzb zzdx;
        public Long zzdy;
        public Long zzdz;
        public Long zzea;
        public Long zzeb;
        public Long zzec;
        public Long zzed;
        public String zzee;
        public String zzef;
        public Integer zzeg;
        public Integer zzeh;
        public Long zzei;
        public Long zzej;
        public Long zzek;
        public Long zzel;
        public Long zzem;
        public Integer zzen;
        public zza zzeo;
        public zza[] zzep;
        public zzb zzeq;
        public Long zzer;
        public String zzes;
        public Integer zzet;
        public Boolean zzeu;
        public String zzev;
        public Long zzew;
        public zze zzex;

        public static final class zza extends zzare<zza> {
            private static volatile zza[] zzey;
            public Long zzdf;
            public Long zzdg;
            public Long zzez;
            public Long zzfa;
            public Long zzfb;
            public Long zzfc;
            public Integer zzfd;
            public Long zzfe;
            public Long zzff;
            public Long zzfg;
            public Integer zzfh;
            public Long zzfi;

            public zza() {
                this.zzdf = null;
                this.zzdg = null;
                this.zzez = null;
                this.zzfa = null;
                this.zzfb = null;
                this.zzfc = null;
                this.zzfd = null;
                this.zzfe = null;
                this.zzff = null;
                this.zzfg = null;
                this.zzfh = null;
                this.zzfi = null;
                this.bqE = -1;
            }

            public static zza[] zzy() {
                if (zzey == null) {
                    synchronized (zzari.bqD) {
                        if (zzey == null) {
                            zzey = new zza[0];
                        }
                    }
                }
                return zzey;
            }

            public void zza(zzard com_google_android_gms_internal_zzard) throws IOException {
                if (this.zzdf != null) {
                    com_google_android_gms_internal_zzard.zzb(1, this.zzdf.longValue());
                }
                if (this.zzdg != null) {
                    com_google_android_gms_internal_zzard.zzb(2, this.zzdg.longValue());
                }
                if (this.zzez != null) {
                    com_google_android_gms_internal_zzard.zzb(3, this.zzez.longValue());
                }
                if (this.zzfa != null) {
                    com_google_android_gms_internal_zzard.zzb(4, this.zzfa.longValue());
                }
                if (this.zzfb != null) {
                    com_google_android_gms_internal_zzard.zzb(5, this.zzfb.longValue());
                }
                if (this.zzfc != null) {
                    com_google_android_gms_internal_zzard.zzb(6, this.zzfc.longValue());
                }
                if (this.zzfd != null) {
                    com_google_android_gms_internal_zzard.zzae(7, this.zzfd.intValue());
                }
                if (this.zzfe != null) {
                    com_google_android_gms_internal_zzard.zzb(8, this.zzfe.longValue());
                }
                if (this.zzff != null) {
                    com_google_android_gms_internal_zzard.zzb(9, this.zzff.longValue());
                }
                if (this.zzfg != null) {
                    com_google_android_gms_internal_zzard.zzb(10, this.zzfg.longValue());
                }
                if (this.zzfh != null) {
                    com_google_android_gms_internal_zzard.zzae(11, this.zzfh.intValue());
                }
                if (this.zzfi != null) {
                    com_google_android_gms_internal_zzard.zzb(12, this.zzfi.longValue());
                }
                super.zza(com_google_android_gms_internal_zzard);
            }

            public /* synthetic */ zzark zzb(zzarc com_google_android_gms_internal_zzarc) throws IOException {
                return zzd(com_google_android_gms_internal_zzarc);
            }

            public zza zzd(zzarc com_google_android_gms_internal_zzarc) throws IOException {
                while (true) {
                    int cw = com_google_android_gms_internal_zzarc.cw();
                    switch (cw) {
                        case 0:
                            break;
                        case 8:
                            this.zzdf = Long.valueOf(com_google_android_gms_internal_zzarc.cz());
                            continue;
                        case 16:
                            this.zzdg = Long.valueOf(com_google_android_gms_internal_zzarc.cz());
                            continue;
                        case 24:
                            this.zzez = Long.valueOf(com_google_android_gms_internal_zzarc.cz());
                            continue;
                        case 32:
                            this.zzfa = Long.valueOf(com_google_android_gms_internal_zzarc.cz());
                            continue;
                        case MotionEventCompat.AXIS_GENERIC_9 /*40*/:
                            this.zzfb = Long.valueOf(com_google_android_gms_internal_zzarc.cz());
                            continue;
                        case 48:
                            this.zzfc = Long.valueOf(com_google_android_gms_internal_zzarc.cz());
                            continue;
                        case 56:
                            cw = com_google_android_gms_internal_zzarc.cA();
                            switch (cw) {
                                case 0:
                                case 1:
                                case 2:
                                case 1000:
                                    this.zzfd = Integer.valueOf(cw);
                                    break;
                                default:
                                    continue;
                            }
                        case 64:
                            this.zzfe = Long.valueOf(com_google_android_gms_internal_zzarc.cz());
                            continue;
                        case XtraBox.MP4_XTRA_BT_GUID /*72*/:
                            this.zzff = Long.valueOf(com_google_android_gms_internal_zzarc.cz());
                            continue;
                        case 80:
                            this.zzfg = Long.valueOf(com_google_android_gms_internal_zzarc.cz());
                            continue;
                        case 88:
                            cw = com_google_android_gms_internal_zzarc.cA();
                            switch (cw) {
                                case 0:
                                case 1:
                                case 2:
                                case 1000:
                                    this.zzfh = Integer.valueOf(cw);
                                    break;
                                default:
                                    continue;
                            }
                        case 96:
                            this.zzfi = Long.valueOf(com_google_android_gms_internal_zzarc.cz());
                            continue;
                        default:
                            if (!super.zza(com_google_android_gms_internal_zzarc, cw)) {
                                break;
                            }
                            continue;
                    }
                    return this;
                }
            }

            protected int zzx() {
                int zzx = super.zzx();
                if (this.zzdf != null) {
                    zzx += zzard.zzf(1, this.zzdf.longValue());
                }
                if (this.zzdg != null) {
                    zzx += zzard.zzf(2, this.zzdg.longValue());
                }
                if (this.zzez != null) {
                    zzx += zzard.zzf(3, this.zzez.longValue());
                }
                if (this.zzfa != null) {
                    zzx += zzard.zzf(4, this.zzfa.longValue());
                }
                if (this.zzfb != null) {
                    zzx += zzard.zzf(5, this.zzfb.longValue());
                }
                if (this.zzfc != null) {
                    zzx += zzard.zzf(6, this.zzfc.longValue());
                }
                if (this.zzfd != null) {
                    zzx += zzard.zzag(7, this.zzfd.intValue());
                }
                if (this.zzfe != null) {
                    zzx += zzard.zzf(8, this.zzfe.longValue());
                }
                if (this.zzff != null) {
                    zzx += zzard.zzf(9, this.zzff.longValue());
                }
                if (this.zzfg != null) {
                    zzx += zzard.zzf(10, this.zzfg.longValue());
                }
                if (this.zzfh != null) {
                    zzx += zzard.zzag(11, this.zzfh.intValue());
                }
                return this.zzfi != null ? zzx + zzard.zzf(12, this.zzfi.longValue()) : zzx;
            }
        }

        public static final class zzb extends zzare<zzb> {
            public Long zzel;
            public Long zzem;
            public Long zzfj;

            public zzb() {
                this.zzel = null;
                this.zzem = null;
                this.zzfj = null;
                this.bqE = -1;
            }

            public void zza(zzard com_google_android_gms_internal_zzard) throws IOException {
                if (this.zzel != null) {
                    com_google_android_gms_internal_zzard.zzb(1, this.zzel.longValue());
                }
                if (this.zzem != null) {
                    com_google_android_gms_internal_zzard.zzb(2, this.zzem.longValue());
                }
                if (this.zzfj != null) {
                    com_google_android_gms_internal_zzard.zzb(3, this.zzfj.longValue());
                }
                super.zza(com_google_android_gms_internal_zzard);
            }

            public /* synthetic */ zzark zzb(zzarc com_google_android_gms_internal_zzarc) throws IOException {
                return zze(com_google_android_gms_internal_zzarc);
            }

            public zzb zze(zzarc com_google_android_gms_internal_zzarc) throws IOException {
                while (true) {
                    int cw = com_google_android_gms_internal_zzarc.cw();
                    switch (cw) {
                        case 0:
                            break;
                        case 8:
                            this.zzel = Long.valueOf(com_google_android_gms_internal_zzarc.cz());
                            continue;
                        case 16:
                            this.zzem = Long.valueOf(com_google_android_gms_internal_zzarc.cz());
                            continue;
                        case 24:
                            this.zzfj = Long.valueOf(com_google_android_gms_internal_zzarc.cz());
                            continue;
                        default:
                            if (!super.zza(com_google_android_gms_internal_zzarc, cw)) {
                                break;
                            }
                            continue;
                    }
                    return this;
                }
            }

            protected int zzx() {
                int zzx = super.zzx();
                if (this.zzel != null) {
                    zzx += zzard.zzf(1, this.zzel.longValue());
                }
                if (this.zzem != null) {
                    zzx += zzard.zzf(2, this.zzem.longValue());
                }
                return this.zzfj != null ? zzx + zzard.zzf(3, this.zzfj.longValue()) : zzx;
            }
        }

        public zza() {
            this.zzct = null;
            this.zzcs = null;
            this.zzcu = null;
            this.zzcv = null;
            this.zzcw = null;
            this.zzcx = null;
            this.zzcy = null;
            this.zzcz = null;
            this.zzda = null;
            this.zzdb = null;
            this.zzdc = null;
            this.zzdd = null;
            this.zzde = null;
            this.zzdf = null;
            this.zzdg = null;
            this.zzdh = null;
            this.zzdi = null;
            this.zzdj = null;
            this.zzdk = null;
            this.zzdl = null;
            this.zzdm = null;
            this.zzdn = null;
            this.zzdo = null;
            this.zzdp = null;
            this.zzdq = null;
            this.zzdr = null;
            this.zzds = null;
            this.zzdt = null;
            this.zzdu = null;
            this.zzdv = null;
            this.zzdw = null;
            this.zzdy = null;
            this.zzdz = null;
            this.zzea = null;
            this.zzeb = null;
            this.zzec = null;
            this.zzed = null;
            this.zzee = null;
            this.zzef = null;
            this.zzeg = null;
            this.zzeh = null;
            this.zzei = null;
            this.zzej = null;
            this.zzek = null;
            this.zzel = null;
            this.zzem = null;
            this.zzen = null;
            this.zzep = zza.zzy();
            this.zzer = null;
            this.zzes = null;
            this.zzet = null;
            this.zzeu = null;
            this.zzev = null;
            this.zzew = null;
            this.bqE = -1;
        }

        public static zza zzc(byte[] bArr) throws zzarj {
            return (zza) zzark.zza(new zza(), bArr);
        }

        public void zza(zzard com_google_android_gms_internal_zzard) throws IOException {
            if (this.zzct != null) {
                com_google_android_gms_internal_zzard.zzr(1, this.zzct);
            }
            if (this.zzcs != null) {
                com_google_android_gms_internal_zzard.zzr(2, this.zzcs);
            }
            if (this.zzcu != null) {
                com_google_android_gms_internal_zzard.zzb(3, this.zzcu.longValue());
            }
            if (this.zzcv != null) {
                com_google_android_gms_internal_zzard.zzb(4, this.zzcv.longValue());
            }
            if (this.zzcw != null) {
                com_google_android_gms_internal_zzard.zzb(5, this.zzcw.longValue());
            }
            if (this.zzcx != null) {
                com_google_android_gms_internal_zzard.zzb(6, this.zzcx.longValue());
            }
            if (this.zzcy != null) {
                com_google_android_gms_internal_zzard.zzb(7, this.zzcy.longValue());
            }
            if (this.zzcz != null) {
                com_google_android_gms_internal_zzard.zzb(8, this.zzcz.longValue());
            }
            if (this.zzda != null) {
                com_google_android_gms_internal_zzard.zzb(9, this.zzda.longValue());
            }
            if (this.zzdb != null) {
                com_google_android_gms_internal_zzard.zzb(10, this.zzdb.longValue());
            }
            if (this.zzdc != null) {
                com_google_android_gms_internal_zzard.zzb(11, this.zzdc.longValue());
            }
            if (this.zzdd != null) {
                com_google_android_gms_internal_zzard.zzb(12, this.zzdd.longValue());
            }
            if (this.zzde != null) {
                com_google_android_gms_internal_zzard.zzr(13, this.zzde);
            }
            if (this.zzdf != null) {
                com_google_android_gms_internal_zzard.zzb(14, this.zzdf.longValue());
            }
            if (this.zzdg != null) {
                com_google_android_gms_internal_zzard.zzb(15, this.zzdg.longValue());
            }
            if (this.zzdh != null) {
                com_google_android_gms_internal_zzard.zzb(16, this.zzdh.longValue());
            }
            if (this.zzdi != null) {
                com_google_android_gms_internal_zzard.zzb(17, this.zzdi.longValue());
            }
            if (this.zzdj != null) {
                com_google_android_gms_internal_zzard.zzb(18, this.zzdj.longValue());
            }
            if (this.zzdk != null) {
                com_google_android_gms_internal_zzard.zzb(19, this.zzdk.longValue());
            }
            if (this.zzdl != null) {
                com_google_android_gms_internal_zzard.zzb(20, this.zzdl.longValue());
            }
            if (this.zzer != null) {
                com_google_android_gms_internal_zzard.zzb(21, this.zzer.longValue());
            }
            if (this.zzdm != null) {
                com_google_android_gms_internal_zzard.zzb(22, this.zzdm.longValue());
            }
            if (this.zzdn != null) {
                com_google_android_gms_internal_zzard.zzb(23, this.zzdn.longValue());
            }
            if (this.zzes != null) {
                com_google_android_gms_internal_zzard.zzr(24, this.zzes);
            }
            if (this.zzew != null) {
                com_google_android_gms_internal_zzard.zzb(25, this.zzew.longValue());
            }
            if (this.zzet != null) {
                com_google_android_gms_internal_zzard.zzae(26, this.zzet.intValue());
            }
            if (this.zzdo != null) {
                com_google_android_gms_internal_zzard.zzr(27, this.zzdo);
            }
            if (this.zzeu != null) {
                com_google_android_gms_internal_zzard.zzj(28, this.zzeu.booleanValue());
            }
            if (this.zzdp != null) {
                com_google_android_gms_internal_zzard.zzr(29, this.zzdp);
            }
            if (this.zzev != null) {
                com_google_android_gms_internal_zzard.zzr(30, this.zzev);
            }
            if (this.zzdq != null) {
                com_google_android_gms_internal_zzard.zzb(31, this.zzdq.longValue());
            }
            if (this.zzdr != null) {
                com_google_android_gms_internal_zzard.zzb(32, this.zzdr.longValue());
            }
            if (this.zzds != null) {
                com_google_android_gms_internal_zzard.zzb(33, this.zzds.longValue());
            }
            if (this.zzdt != null) {
                com_google_android_gms_internal_zzard.zzr(34, this.zzdt);
            }
            if (this.zzdu != null) {
                com_google_android_gms_internal_zzard.zzb(35, this.zzdu.longValue());
            }
            if (this.zzdv != null) {
                com_google_android_gms_internal_zzard.zzb(36, this.zzdv.longValue());
            }
            if (this.zzdw != null) {
                com_google_android_gms_internal_zzard.zzb(37, this.zzdw.longValue());
            }
            if (this.zzdx != null) {
                com_google_android_gms_internal_zzard.zza(38, this.zzdx);
            }
            if (this.zzdy != null) {
                com_google_android_gms_internal_zzard.zzb(39, this.zzdy.longValue());
            }
            if (this.zzdz != null) {
                com_google_android_gms_internal_zzard.zzb(40, this.zzdz.longValue());
            }
            if (this.zzea != null) {
                com_google_android_gms_internal_zzard.zzb(41, this.zzea.longValue());
            }
            if (this.zzeb != null) {
                com_google_android_gms_internal_zzard.zzb(42, this.zzeb.longValue());
            }
            if (this.zzep != null && this.zzep.length > 0) {
                for (zzark com_google_android_gms_internal_zzark : this.zzep) {
                    if (com_google_android_gms_internal_zzark != null) {
                        com_google_android_gms_internal_zzard.zza(43, com_google_android_gms_internal_zzark);
                    }
                }
            }
            if (this.zzec != null) {
                com_google_android_gms_internal_zzard.zzb(44, this.zzec.longValue());
            }
            if (this.zzed != null) {
                com_google_android_gms_internal_zzard.zzb(45, this.zzed.longValue());
            }
            if (this.zzee != null) {
                com_google_android_gms_internal_zzard.zzr(46, this.zzee);
            }
            if (this.zzef != null) {
                com_google_android_gms_internal_zzard.zzr(47, this.zzef);
            }
            if (this.zzeg != null) {
                com_google_android_gms_internal_zzard.zzae(48, this.zzeg.intValue());
            }
            if (this.zzeh != null) {
                com_google_android_gms_internal_zzard.zzae(49, this.zzeh.intValue());
            }
            if (this.zzeo != null) {
                com_google_android_gms_internal_zzard.zza(50, this.zzeo);
            }
            if (this.zzei != null) {
                com_google_android_gms_internal_zzard.zzb(51, this.zzei.longValue());
            }
            if (this.zzej != null) {
                com_google_android_gms_internal_zzard.zzb(52, this.zzej.longValue());
            }
            if (this.zzek != null) {
                com_google_android_gms_internal_zzard.zzb(53, this.zzek.longValue());
            }
            if (this.zzel != null) {
                com_google_android_gms_internal_zzard.zzb(54, this.zzel.longValue());
            }
            if (this.zzem != null) {
                com_google_android_gms_internal_zzard.zzb(55, this.zzem.longValue());
            }
            if (this.zzen != null) {
                com_google_android_gms_internal_zzard.zzae(56, this.zzen.intValue());
            }
            if (this.zzeq != null) {
                com_google_android_gms_internal_zzard.zza(57, this.zzeq);
            }
            if (this.zzex != null) {
                com_google_android_gms_internal_zzard.zza(201, this.zzex);
            }
            super.zza(com_google_android_gms_internal_zzard);
        }

        public /* synthetic */ zzark zzb(zzarc com_google_android_gms_internal_zzarc) throws IOException {
            return zzc(com_google_android_gms_internal_zzarc);
        }

        public zza zzc(zzarc com_google_android_gms_internal_zzarc) throws IOException {
            while (true) {
                int cw = com_google_android_gms_internal_zzarc.cw();
                switch (cw) {
                    case 0:
                        break;
                    case 10:
                        this.zzct = com_google_android_gms_internal_zzarc.readString();
                        continue;
                    case 18:
                        this.zzcs = com_google_android_gms_internal_zzarc.readString();
                        continue;
                    case 24:
                        this.zzcu = Long.valueOf(com_google_android_gms_internal_zzarc.cz());
                        continue;
                    case 32:
                        this.zzcv = Long.valueOf(com_google_android_gms_internal_zzarc.cz());
                        continue;
                    case MotionEventCompat.AXIS_GENERIC_9 /*40*/:
                        this.zzcw = Long.valueOf(com_google_android_gms_internal_zzarc.cz());
                        continue;
                    case 48:
                        this.zzcx = Long.valueOf(com_google_android_gms_internal_zzarc.cz());
                        continue;
                    case 56:
                        this.zzcy = Long.valueOf(com_google_android_gms_internal_zzarc.cz());
                        continue;
                    case 64:
                        this.zzcz = Long.valueOf(com_google_android_gms_internal_zzarc.cz());
                        continue;
                    case XtraBox.MP4_XTRA_BT_GUID /*72*/:
                        this.zzda = Long.valueOf(com_google_android_gms_internal_zzarc.cz());
                        continue;
                    case 80:
                        this.zzdb = Long.valueOf(com_google_android_gms_internal_zzarc.cz());
                        continue;
                    case 88:
                        this.zzdc = Long.valueOf(com_google_android_gms_internal_zzarc.cz());
                        continue;
                    case 96:
                        this.zzdd = Long.valueOf(com_google_android_gms_internal_zzarc.cz());
                        continue;
                    case 106:
                        this.zzde = com_google_android_gms_internal_zzarc.readString();
                        continue;
                    case 112:
                        this.zzdf = Long.valueOf(com_google_android_gms_internal_zzarc.cz());
                        continue;
                    case 120:
                        this.zzdg = Long.valueOf(com_google_android_gms_internal_zzarc.cz());
                        continue;
                    case 128:
                        this.zzdh = Long.valueOf(com_google_android_gms_internal_zzarc.cz());
                        continue;
                    case 136:
                        this.zzdi = Long.valueOf(com_google_android_gms_internal_zzarc.cz());
                        continue;
                    case 144:
                        this.zzdj = Long.valueOf(com_google_android_gms_internal_zzarc.cz());
                        continue;
                    case 152:
                        this.zzdk = Long.valueOf(com_google_android_gms_internal_zzarc.cz());
                        continue;
                    case 160:
                        this.zzdl = Long.valueOf(com_google_android_gms_internal_zzarc.cz());
                        continue;
                    case 168:
                        this.zzer = Long.valueOf(com_google_android_gms_internal_zzarc.cz());
                        continue;
                    case 176:
                        this.zzdm = Long.valueOf(com_google_android_gms_internal_zzarc.cz());
                        continue;
                    case 184:
                        this.zzdn = Long.valueOf(com_google_android_gms_internal_zzarc.cz());
                        continue;
                    case 194:
                        this.zzes = com_google_android_gms_internal_zzarc.readString();
                        continue;
                    case Callback.DEFAULT_DRAG_ANIMATION_DURATION /*200*/:
                        this.zzew = Long.valueOf(com_google_android_gms_internal_zzarc.cz());
                        continue;
                    case 208:
                        cw = com_google_android_gms_internal_zzarc.cA();
                        switch (cw) {
                            case 0:
                            case 1:
                            case 2:
                            case 3:
                            case 4:
                            case 5:
                            case 6:
                                this.zzet = Integer.valueOf(cw);
                                break;
                            default:
                                continue;
                        }
                    case 218:
                        this.zzdo = com_google_android_gms_internal_zzarc.readString();
                        continue;
                    case 224:
                        this.zzeu = Boolean.valueOf(com_google_android_gms_internal_zzarc.cC());
                        continue;
                    case 234:
                        this.zzdp = com_google_android_gms_internal_zzarc.readString();
                        continue;
                    case 242:
                        this.zzev = com_google_android_gms_internal_zzarc.readString();
                        continue;
                    case 248:
                        this.zzdq = Long.valueOf(com_google_android_gms_internal_zzarc.cz());
                        continue;
                    case 256:
                        this.zzdr = Long.valueOf(com_google_android_gms_internal_zzarc.cz());
                        continue;
                    case 264:
                        this.zzds = Long.valueOf(com_google_android_gms_internal_zzarc.cz());
                        continue;
                    case 274:
                        this.zzdt = com_google_android_gms_internal_zzarc.readString();
                        continue;
                    case 280:
                        this.zzdu = Long.valueOf(com_google_android_gms_internal_zzarc.cz());
                        continue;
                    case 288:
                        this.zzdv = Long.valueOf(com_google_android_gms_internal_zzarc.cz());
                        continue;
                    case 296:
                        this.zzdw = Long.valueOf(com_google_android_gms_internal_zzarc.cz());
                        continue;
                    case 306:
                        if (this.zzdx == null) {
                            this.zzdx = new zzb();
                        }
                        com_google_android_gms_internal_zzarc.zza(this.zzdx);
                        continue;
                    case 312:
                        this.zzdy = Long.valueOf(com_google_android_gms_internal_zzarc.cz());
                        continue;
                    case 320:
                        this.zzdz = Long.valueOf(com_google_android_gms_internal_zzarc.cz());
                        continue;
                    case 328:
                        this.zzea = Long.valueOf(com_google_android_gms_internal_zzarc.cz());
                        continue;
                    case 336:
                        this.zzeb = Long.valueOf(com_google_android_gms_internal_zzarc.cz());
                        continue;
                    case 346:
                        int zzc = zzarn.zzc(com_google_android_gms_internal_zzarc, 346);
                        cw = this.zzep == null ? 0 : this.zzep.length;
                        Object obj = new zza[(zzc + cw)];
                        if (cw != 0) {
                            System.arraycopy(this.zzep, 0, obj, 0, cw);
                        }
                        while (cw < obj.length - 1) {
                            obj[cw] = new zza();
                            com_google_android_gms_internal_zzarc.zza(obj[cw]);
                            com_google_android_gms_internal_zzarc.cw();
                            cw++;
                        }
                        obj[cw] = new zza();
                        com_google_android_gms_internal_zzarc.zza(obj[cw]);
                        this.zzep = obj;
                        continue;
                    case 352:
                        this.zzec = Long.valueOf(com_google_android_gms_internal_zzarc.cz());
                        continue;
                    case 360:
                        this.zzed = Long.valueOf(com_google_android_gms_internal_zzarc.cz());
                        continue;
                    case 370:
                        this.zzee = com_google_android_gms_internal_zzarc.readString();
                        continue;
                    case 378:
                        this.zzef = com_google_android_gms_internal_zzarc.readString();
                        continue;
                    case 384:
                        cw = com_google_android_gms_internal_zzarc.cA();
                        switch (cw) {
                            case 0:
                            case 1:
                            case 2:
                            case 1000:
                                this.zzeg = Integer.valueOf(cw);
                                break;
                            default:
                                continue;
                        }
                    case 392:
                        cw = com_google_android_gms_internal_zzarc.cA();
                        switch (cw) {
                            case 0:
                            case 1:
                            case 2:
                            case 1000:
                                this.zzeh = Integer.valueOf(cw);
                                break;
                            default:
                                continue;
                        }
                    case 402:
                        if (this.zzeo == null) {
                            this.zzeo = new zza();
                        }
                        com_google_android_gms_internal_zzarc.zza(this.zzeo);
                        continue;
                    case 408:
                        this.zzei = Long.valueOf(com_google_android_gms_internal_zzarc.cz());
                        continue;
                    case 416:
                        this.zzej = Long.valueOf(com_google_android_gms_internal_zzarc.cz());
                        continue;
                    case 424:
                        this.zzek = Long.valueOf(com_google_android_gms_internal_zzarc.cz());
                        continue;
                    case 432:
                        this.zzel = Long.valueOf(com_google_android_gms_internal_zzarc.cz());
                        continue;
                    case 440:
                        this.zzem = Long.valueOf(com_google_android_gms_internal_zzarc.cz());
                        continue;
                    case 448:
                        cw = com_google_android_gms_internal_zzarc.cA();
                        switch (cw) {
                            case 0:
                            case 1:
                            case 2:
                            case 1000:
                                this.zzen = Integer.valueOf(cw);
                                break;
                            default:
                                continue;
                        }
                    case 458:
                        if (this.zzeq == null) {
                            this.zzeq = new zzb();
                        }
                        com_google_android_gms_internal_zzarc.zza(this.zzeq);
                        continue;
                    case 1610:
                        if (this.zzex == null) {
                            this.zzex = new zze();
                        }
                        com_google_android_gms_internal_zzarc.zza(this.zzex);
                        continue;
                    default:
                        if (!super.zza(com_google_android_gms_internal_zzarc, cw)) {
                            break;
                        }
                        continue;
                }
                return this;
            }
        }

        protected int zzx() {
            int zzx = super.zzx();
            if (this.zzct != null) {
                zzx += zzard.zzs(1, this.zzct);
            }
            if (this.zzcs != null) {
                zzx += zzard.zzs(2, this.zzcs);
            }
            if (this.zzcu != null) {
                zzx += zzard.zzf(3, this.zzcu.longValue());
            }
            if (this.zzcv != null) {
                zzx += zzard.zzf(4, this.zzcv.longValue());
            }
            if (this.zzcw != null) {
                zzx += zzard.zzf(5, this.zzcw.longValue());
            }
            if (this.zzcx != null) {
                zzx += zzard.zzf(6, this.zzcx.longValue());
            }
            if (this.zzcy != null) {
                zzx += zzard.zzf(7, this.zzcy.longValue());
            }
            if (this.zzcz != null) {
                zzx += zzard.zzf(8, this.zzcz.longValue());
            }
            if (this.zzda != null) {
                zzx += zzard.zzf(9, this.zzda.longValue());
            }
            if (this.zzdb != null) {
                zzx += zzard.zzf(10, this.zzdb.longValue());
            }
            if (this.zzdc != null) {
                zzx += zzard.zzf(11, this.zzdc.longValue());
            }
            if (this.zzdd != null) {
                zzx += zzard.zzf(12, this.zzdd.longValue());
            }
            if (this.zzde != null) {
                zzx += zzard.zzs(13, this.zzde);
            }
            if (this.zzdf != null) {
                zzx += zzard.zzf(14, this.zzdf.longValue());
            }
            if (this.zzdg != null) {
                zzx += zzard.zzf(15, this.zzdg.longValue());
            }
            if (this.zzdh != null) {
                zzx += zzard.zzf(16, this.zzdh.longValue());
            }
            if (this.zzdi != null) {
                zzx += zzard.zzf(17, this.zzdi.longValue());
            }
            if (this.zzdj != null) {
                zzx += zzard.zzf(18, this.zzdj.longValue());
            }
            if (this.zzdk != null) {
                zzx += zzard.zzf(19, this.zzdk.longValue());
            }
            if (this.zzdl != null) {
                zzx += zzard.zzf(20, this.zzdl.longValue());
            }
            if (this.zzer != null) {
                zzx += zzard.zzf(21, this.zzer.longValue());
            }
            if (this.zzdm != null) {
                zzx += zzard.zzf(22, this.zzdm.longValue());
            }
            if (this.zzdn != null) {
                zzx += zzard.zzf(23, this.zzdn.longValue());
            }
            if (this.zzes != null) {
                zzx += zzard.zzs(24, this.zzes);
            }
            if (this.zzew != null) {
                zzx += zzard.zzf(25, this.zzew.longValue());
            }
            if (this.zzet != null) {
                zzx += zzard.zzag(26, this.zzet.intValue());
            }
            if (this.zzdo != null) {
                zzx += zzard.zzs(27, this.zzdo);
            }
            if (this.zzeu != null) {
                zzx += zzard.zzk(28, this.zzeu.booleanValue());
            }
            if (this.zzdp != null) {
                zzx += zzard.zzs(29, this.zzdp);
            }
            if (this.zzev != null) {
                zzx += zzard.zzs(30, this.zzev);
            }
            if (this.zzdq != null) {
                zzx += zzard.zzf(31, this.zzdq.longValue());
            }
            if (this.zzdr != null) {
                zzx += zzard.zzf(32, this.zzdr.longValue());
            }
            if (this.zzds != null) {
                zzx += zzard.zzf(33, this.zzds.longValue());
            }
            if (this.zzdt != null) {
                zzx += zzard.zzs(34, this.zzdt);
            }
            if (this.zzdu != null) {
                zzx += zzard.zzf(35, this.zzdu.longValue());
            }
            if (this.zzdv != null) {
                zzx += zzard.zzf(36, this.zzdv.longValue());
            }
            if (this.zzdw != null) {
                zzx += zzard.zzf(37, this.zzdw.longValue());
            }
            if (this.zzdx != null) {
                zzx += zzard.zzc(38, this.zzdx);
            }
            if (this.zzdy != null) {
                zzx += zzard.zzf(39, this.zzdy.longValue());
            }
            if (this.zzdz != null) {
                zzx += zzard.zzf(40, this.zzdz.longValue());
            }
            if (this.zzea != null) {
                zzx += zzard.zzf(41, this.zzea.longValue());
            }
            if (this.zzeb != null) {
                zzx += zzard.zzf(42, this.zzeb.longValue());
            }
            if (this.zzep != null && this.zzep.length > 0) {
                int i = zzx;
                for (zzark com_google_android_gms_internal_zzark : this.zzep) {
                    if (com_google_android_gms_internal_zzark != null) {
                        i += zzard.zzc(43, com_google_android_gms_internal_zzark);
                    }
                }
                zzx = i;
            }
            if (this.zzec != null) {
                zzx += zzard.zzf(44, this.zzec.longValue());
            }
            if (this.zzed != null) {
                zzx += zzard.zzf(45, this.zzed.longValue());
            }
            if (this.zzee != null) {
                zzx += zzard.zzs(46, this.zzee);
            }
            if (this.zzef != null) {
                zzx += zzard.zzs(47, this.zzef);
            }
            if (this.zzeg != null) {
                zzx += zzard.zzag(48, this.zzeg.intValue());
            }
            if (this.zzeh != null) {
                zzx += zzard.zzag(49, this.zzeh.intValue());
            }
            if (this.zzeo != null) {
                zzx += zzard.zzc(50, this.zzeo);
            }
            if (this.zzei != null) {
                zzx += zzard.zzf(51, this.zzei.longValue());
            }
            if (this.zzej != null) {
                zzx += zzard.zzf(52, this.zzej.longValue());
            }
            if (this.zzek != null) {
                zzx += zzard.zzf(53, this.zzek.longValue());
            }
            if (this.zzel != null) {
                zzx += zzard.zzf(54, this.zzel.longValue());
            }
            if (this.zzem != null) {
                zzx += zzard.zzf(55, this.zzem.longValue());
            }
            if (this.zzen != null) {
                zzx += zzard.zzag(56, this.zzen.intValue());
            }
            if (this.zzeq != null) {
                zzx += zzard.zzc(57, this.zzeq);
            }
            return this.zzex != null ? zzx + zzard.zzc(201, this.zzex) : zzx;
        }
    }

    public static final class zzb extends zzare<zzb> {
        public Long zzfk;
        public Integer zzfl;
        public Boolean zzfm;
        public int[] zzfn;
        public Long zzfo;

        public zzb() {
            this.zzfk = null;
            this.zzfl = null;
            this.zzfm = null;
            this.zzfn = zzarn.bqF;
            this.zzfo = null;
            this.bqE = -1;
        }

        public void zza(zzard com_google_android_gms_internal_zzard) throws IOException {
            if (this.zzfk != null) {
                com_google_android_gms_internal_zzard.zzb(1, this.zzfk.longValue());
            }
            if (this.zzfl != null) {
                com_google_android_gms_internal_zzard.zzae(2, this.zzfl.intValue());
            }
            if (this.zzfm != null) {
                com_google_android_gms_internal_zzard.zzj(3, this.zzfm.booleanValue());
            }
            if (this.zzfn != null && this.zzfn.length > 0) {
                for (int zzae : this.zzfn) {
                    com_google_android_gms_internal_zzard.zzae(4, zzae);
                }
            }
            if (this.zzfo != null) {
                com_google_android_gms_internal_zzard.zza(5, this.zzfo.longValue());
            }
            super.zza(com_google_android_gms_internal_zzard);
        }

        public /* synthetic */ zzark zzb(zzarc com_google_android_gms_internal_zzarc) throws IOException {
            return zzf(com_google_android_gms_internal_zzarc);
        }

        public zzb zzf(zzarc com_google_android_gms_internal_zzarc) throws IOException {
            while (true) {
                int cw = com_google_android_gms_internal_zzarc.cw();
                int zzc;
                switch (cw) {
                    case 0:
                        break;
                    case 8:
                        this.zzfk = Long.valueOf(com_google_android_gms_internal_zzarc.cz());
                        continue;
                    case 16:
                        this.zzfl = Integer.valueOf(com_google_android_gms_internal_zzarc.cA());
                        continue;
                    case 24:
                        this.zzfm = Boolean.valueOf(com_google_android_gms_internal_zzarc.cC());
                        continue;
                    case 32:
                        zzc = zzarn.zzc(com_google_android_gms_internal_zzarc, 32);
                        cw = this.zzfn == null ? 0 : this.zzfn.length;
                        Object obj = new int[(zzc + cw)];
                        if (cw != 0) {
                            System.arraycopy(this.zzfn, 0, obj, 0, cw);
                        }
                        while (cw < obj.length - 1) {
                            obj[cw] = com_google_android_gms_internal_zzarc.cA();
                            com_google_android_gms_internal_zzarc.cw();
                            cw++;
                        }
                        obj[cw] = com_google_android_gms_internal_zzarc.cA();
                        this.zzfn = obj;
                        continue;
                    case 34:
                        int zzahc = com_google_android_gms_internal_zzarc.zzahc(com_google_android_gms_internal_zzarc.cF());
                        zzc = com_google_android_gms_internal_zzarc.getPosition();
                        cw = 0;
                        while (com_google_android_gms_internal_zzarc.cK() > 0) {
                            com_google_android_gms_internal_zzarc.cA();
                            cw++;
                        }
                        com_google_android_gms_internal_zzarc.zzahe(zzc);
                        zzc = this.zzfn == null ? 0 : this.zzfn.length;
                        Object obj2 = new int[(cw + zzc)];
                        if (zzc != 0) {
                            System.arraycopy(this.zzfn, 0, obj2, 0, zzc);
                        }
                        while (zzc < obj2.length) {
                            obj2[zzc] = com_google_android_gms_internal_zzarc.cA();
                            zzc++;
                        }
                        this.zzfn = obj2;
                        com_google_android_gms_internal_zzarc.zzahd(zzahc);
                        continue;
                    case MotionEventCompat.AXIS_GENERIC_9 /*40*/:
                        this.zzfo = Long.valueOf(com_google_android_gms_internal_zzarc.cy());
                        continue;
                    default:
                        if (!super.zza(com_google_android_gms_internal_zzarc, cw)) {
                            break;
                        }
                        continue;
                }
                return this;
            }
        }

        protected int zzx() {
            int i = 0;
            int zzx = super.zzx();
            if (this.zzfk != null) {
                zzx += zzard.zzf(1, this.zzfk.longValue());
            }
            if (this.zzfl != null) {
                zzx += zzard.zzag(2, this.zzfl.intValue());
            }
            if (this.zzfm != null) {
                zzx += zzard.zzk(3, this.zzfm.booleanValue());
            }
            if (this.zzfn != null && this.zzfn.length > 0) {
                int i2 = 0;
                while (i < this.zzfn.length) {
                    i2 += zzard.zzahi(this.zzfn[i]);
                    i++;
                }
                zzx = (zzx + i2) + (this.zzfn.length * 1);
            }
            return this.zzfo != null ? zzx + zzard.zze(5, this.zzfo.longValue()) : zzx;
        }
    }

    public static final class zzc extends zzare<zzc> {
        public byte[] zzfp;
        public byte[] zzfq;

        public zzc() {
            this.zzfp = null;
            this.zzfq = null;
            this.bqE = -1;
        }

        public void zza(zzard com_google_android_gms_internal_zzard) throws IOException {
            if (this.zzfp != null) {
                com_google_android_gms_internal_zzard.zza(1, this.zzfp);
            }
            if (this.zzfq != null) {
                com_google_android_gms_internal_zzard.zza(2, this.zzfq);
            }
            super.zza(com_google_android_gms_internal_zzard);
        }

        public /* synthetic */ zzark zzb(zzarc com_google_android_gms_internal_zzarc) throws IOException {
            return zzg(com_google_android_gms_internal_zzarc);
        }

        public zzc zzg(zzarc com_google_android_gms_internal_zzarc) throws IOException {
            while (true) {
                int cw = com_google_android_gms_internal_zzarc.cw();
                switch (cw) {
                    case 0:
                        break;
                    case 10:
                        this.zzfp = com_google_android_gms_internal_zzarc.readBytes();
                        continue;
                    case 18:
                        this.zzfq = com_google_android_gms_internal_zzarc.readBytes();
                        continue;
                    default:
                        if (!super.zza(com_google_android_gms_internal_zzarc, cw)) {
                            break;
                        }
                        continue;
                }
                return this;
            }
        }

        protected int zzx() {
            int zzx = super.zzx();
            if (this.zzfp != null) {
                zzx += zzard.zzb(1, this.zzfp);
            }
            return this.zzfq != null ? zzx + zzard.zzb(2, this.zzfq) : zzx;
        }
    }

    public static final class zzd extends zzare<zzd> {
        public byte[] data;
        public byte[] zzfr;
        public byte[] zzfs;
        public byte[] zzft;

        public zzd() {
            this.data = null;
            this.zzfr = null;
            this.zzfs = null;
            this.zzft = null;
            this.bqE = -1;
        }

        public static zzd zzd(byte[] bArr) throws zzarj {
            return (zzd) zzark.zza(new zzd(), bArr);
        }

        public void zza(zzard com_google_android_gms_internal_zzard) throws IOException {
            if (this.data != null) {
                com_google_android_gms_internal_zzard.zza(1, this.data);
            }
            if (this.zzfr != null) {
                com_google_android_gms_internal_zzard.zza(2, this.zzfr);
            }
            if (this.zzfs != null) {
                com_google_android_gms_internal_zzard.zza(3, this.zzfs);
            }
            if (this.zzft != null) {
                com_google_android_gms_internal_zzard.zza(4, this.zzft);
            }
            super.zza(com_google_android_gms_internal_zzard);
        }

        public /* synthetic */ zzark zzb(zzarc com_google_android_gms_internal_zzarc) throws IOException {
            return zzh(com_google_android_gms_internal_zzarc);
        }

        public zzd zzh(zzarc com_google_android_gms_internal_zzarc) throws IOException {
            while (true) {
                int cw = com_google_android_gms_internal_zzarc.cw();
                switch (cw) {
                    case 0:
                        break;
                    case 10:
                        this.data = com_google_android_gms_internal_zzarc.readBytes();
                        continue;
                    case 18:
                        this.zzfr = com_google_android_gms_internal_zzarc.readBytes();
                        continue;
                    case NalUnitTypes.NAL_TYPE_RSV_VCL26 /*26*/:
                        this.zzfs = com_google_android_gms_internal_zzarc.readBytes();
                        continue;
                    case 34:
                        this.zzft = com_google_android_gms_internal_zzarc.readBytes();
                        continue;
                    default:
                        if (!super.zza(com_google_android_gms_internal_zzarc, cw)) {
                            break;
                        }
                        continue;
                }
                return this;
            }
        }

        protected int zzx() {
            int zzx = super.zzx();
            if (this.data != null) {
                zzx += zzard.zzb(1, this.data);
            }
            if (this.zzfr != null) {
                zzx += zzard.zzb(2, this.zzfr);
            }
            if (this.zzfs != null) {
                zzx += zzard.zzb(3, this.zzfs);
            }
            return this.zzft != null ? zzx + zzard.zzb(4, this.zzft) : zzx;
        }
    }

    public static final class zze extends zzare<zze> {
        public Long zzfk;
        public String zzfu;
        public byte[] zzfv;

        public zze() {
            this.zzfk = null;
            this.zzfu = null;
            this.zzfv = null;
            this.bqE = -1;
        }

        public void zza(zzard com_google_android_gms_internal_zzard) throws IOException {
            if (this.zzfk != null) {
                com_google_android_gms_internal_zzard.zzb(1, this.zzfk.longValue());
            }
            if (this.zzfu != null) {
                com_google_android_gms_internal_zzard.zzr(3, this.zzfu);
            }
            if (this.zzfv != null) {
                com_google_android_gms_internal_zzard.zza(4, this.zzfv);
            }
            super.zza(com_google_android_gms_internal_zzard);
        }

        public /* synthetic */ zzark zzb(zzarc com_google_android_gms_internal_zzarc) throws IOException {
            return zzi(com_google_android_gms_internal_zzarc);
        }

        public zze zzi(zzarc com_google_android_gms_internal_zzarc) throws IOException {
            while (true) {
                int cw = com_google_android_gms_internal_zzarc.cw();
                switch (cw) {
                    case 0:
                        break;
                    case 8:
                        this.zzfk = Long.valueOf(com_google_android_gms_internal_zzarc.cz());
                        continue;
                    case NalUnitTypes.NAL_TYPE_RSV_VCL26 /*26*/:
                        this.zzfu = com_google_android_gms_internal_zzarc.readString();
                        continue;
                    case 34:
                        this.zzfv = com_google_android_gms_internal_zzarc.readBytes();
                        continue;
                    default:
                        if (!super.zza(com_google_android_gms_internal_zzarc, cw)) {
                            break;
                        }
                        continue;
                }
                return this;
            }
        }

        protected int zzx() {
            int zzx = super.zzx();
            if (this.zzfk != null) {
                zzx += zzard.zzf(1, this.zzfk.longValue());
            }
            if (this.zzfu != null) {
                zzx += zzard.zzs(3, this.zzfu);
            }
            return this.zzfv != null ? zzx + zzard.zzb(4, this.zzfv) : zzx;
        }
    }

    public static final class zzf extends zzare<zzf> {
        public byte[] zzfr;
        public byte[][] zzfw;
        public Integer zzfx;
        public Integer zzfy;

        public zzf() {
            this.zzfw = zzarn.bqL;
            this.zzfr = null;
            this.zzfx = null;
            this.zzfy = null;
            this.bqE = -1;
        }

        public void zza(zzard com_google_android_gms_internal_zzard) throws IOException {
            if (this.zzfw != null && this.zzfw.length > 0) {
                for (byte[] bArr : this.zzfw) {
                    if (bArr != null) {
                        com_google_android_gms_internal_zzard.zza(1, bArr);
                    }
                }
            }
            if (this.zzfr != null) {
                com_google_android_gms_internal_zzard.zza(2, this.zzfr);
            }
            if (this.zzfx != null) {
                com_google_android_gms_internal_zzard.zzae(3, this.zzfx.intValue());
            }
            if (this.zzfy != null) {
                com_google_android_gms_internal_zzard.zzae(4, this.zzfy.intValue());
            }
            super.zza(com_google_android_gms_internal_zzard);
        }

        public /* synthetic */ zzark zzb(zzarc com_google_android_gms_internal_zzarc) throws IOException {
            return zzj(com_google_android_gms_internal_zzarc);
        }

        public zzf zzj(zzarc com_google_android_gms_internal_zzarc) throws IOException {
            while (true) {
                int cw = com_google_android_gms_internal_zzarc.cw();
                switch (cw) {
                    case 0:
                        break;
                    case 10:
                        int zzc = zzarn.zzc(com_google_android_gms_internal_zzarc, 10);
                        cw = this.zzfw == null ? 0 : this.zzfw.length;
                        Object obj = new byte[(zzc + cw)][];
                        if (cw != 0) {
                            System.arraycopy(this.zzfw, 0, obj, 0, cw);
                        }
                        while (cw < obj.length - 1) {
                            obj[cw] = com_google_android_gms_internal_zzarc.readBytes();
                            com_google_android_gms_internal_zzarc.cw();
                            cw++;
                        }
                        obj[cw] = com_google_android_gms_internal_zzarc.readBytes();
                        this.zzfw = obj;
                        continue;
                    case 18:
                        this.zzfr = com_google_android_gms_internal_zzarc.readBytes();
                        continue;
                    case 24:
                        cw = com_google_android_gms_internal_zzarc.cA();
                        switch (cw) {
                            case 0:
                            case 1:
                                this.zzfx = Integer.valueOf(cw);
                                break;
                            default:
                                continue;
                        }
                    case 32:
                        cw = com_google_android_gms_internal_zzarc.cA();
                        switch (cw) {
                            case 0:
                            case 1:
                                this.zzfy = Integer.valueOf(cw);
                                break;
                            default:
                                continue;
                        }
                    default:
                        if (!super.zza(com_google_android_gms_internal_zzarc, cw)) {
                            break;
                        }
                        continue;
                }
                return this;
            }
        }

        protected int zzx() {
            int i = 0;
            int zzx = super.zzx();
            if (this.zzfw == null || this.zzfw.length <= 0) {
                i = zzx;
            } else {
                int i2 = 0;
                int i3 = 0;
                while (i < this.zzfw.length) {
                    byte[] bArr = this.zzfw[i];
                    if (bArr != null) {
                        i3++;
                        i2 += zzard.zzbg(bArr);
                    }
                    i++;
                }
                i = (zzx + i2) + (i3 * 1);
            }
            if (this.zzfr != null) {
                i += zzard.zzb(2, this.zzfr);
            }
            if (this.zzfx != null) {
                i += zzard.zzag(3, this.zzfx.intValue());
            }
            return this.zzfy != null ? i + zzard.zzag(4, this.zzfy.intValue()) : i;
        }
    }
}
