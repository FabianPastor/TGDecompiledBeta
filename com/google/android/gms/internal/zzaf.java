package com.google.android.gms.internal;

import android.support.v4.view.MotionEventCompat;
import com.googlecode.mp4parser.authoring.tracks.h265.NalUnitTypes;
import com.googlecode.mp4parser.boxes.microsoft.XtraBox;
import java.io.IOException;
import org.telegram.messenger.support.widget.helper.ItemTouchHelper.Callback;

public interface zzaf {

    public static final class zza extends zzaru<zza> {
        public String zzcn;
        public String zzcp;
        public String zzcq;
        public String zzcr;
        public String zzda;
        public String zzdb;
        public Long zzdc;
        public Long zzdd;
        public Long zzde;
        public Long zzdf;
        public Long zzdg;
        public Long zzdh;
        public Long zzdi;
        public Long zzdj;
        public Long zzdk;
        public Long zzdl;
        public String zzdm;
        public Long zzdn;
        public Long zzdo;
        public Long zzdp;
        public Long zzdq;
        public Long zzdr;
        public Long zzds;
        public Long zzdt;
        public Long zzdu;
        public Long zzdv;
        public String zzdw;
        public Long zzdx;
        public Long zzdy;
        public Long zzdz;
        public Long zzea;
        public Long zzeb;
        public Long zzec;
        public zzb zzed;
        public Long zzee;
        public Long zzef;
        public Long zzeg;
        public Long zzeh;
        public Long zzei;
        public Long zzej;
        public Integer zzek;
        public Integer zzel;
        public Long zzem;
        public Long zzen;
        public Long zzeo;
        public Long zzep;
        public Long zzeq;
        public Integer zzer;
        public zza zzes;
        public zza[] zzet;
        public zzb zzeu;
        public Long zzev;
        public String zzew;
        public Integer zzex;
        public Boolean zzey;
        public String zzez;
        public Long zzfa;
        public zze zzfb;

        public static final class zza extends zzaru<zza> {
            private static volatile zza[] zzfc;
            public Long zzdn;
            public Long zzdo;
            public Long zzfd;
            public Long zzfe;
            public Long zzff;
            public Long zzfg;
            public Integer zzfh;
            public Long zzfi;
            public Long zzfj;
            public Long zzfk;
            public Integer zzfl;
            public Long zzfm;

            public zza() {
                this.zzdn = null;
                this.zzdo = null;
                this.zzfd = null;
                this.zzfe = null;
                this.zzff = null;
                this.zzfg = null;
                this.zzfh = null;
                this.zzfi = null;
                this.zzfj = null;
                this.zzfk = null;
                this.zzfl = null;
                this.zzfm = null;
                this.btP = -1;
            }

            public static zza[] zzaa() {
                if (zzfc == null) {
                    synchronized (zzary.btO) {
                        if (zzfc == null) {
                            zzfc = new zza[0];
                        }
                    }
                }
                return zzfc;
            }

            public void zza(zzart com_google_android_gms_internal_zzart) throws IOException {
                if (this.zzdn != null) {
                    com_google_android_gms_internal_zzart.zzb(1, this.zzdn.longValue());
                }
                if (this.zzdo != null) {
                    com_google_android_gms_internal_zzart.zzb(2, this.zzdo.longValue());
                }
                if (this.zzfd != null) {
                    com_google_android_gms_internal_zzart.zzb(3, this.zzfd.longValue());
                }
                if (this.zzfe != null) {
                    com_google_android_gms_internal_zzart.zzb(4, this.zzfe.longValue());
                }
                if (this.zzff != null) {
                    com_google_android_gms_internal_zzart.zzb(5, this.zzff.longValue());
                }
                if (this.zzfg != null) {
                    com_google_android_gms_internal_zzart.zzb(6, this.zzfg.longValue());
                }
                if (this.zzfh != null) {
                    com_google_android_gms_internal_zzart.zzaf(7, this.zzfh.intValue());
                }
                if (this.zzfi != null) {
                    com_google_android_gms_internal_zzart.zzb(8, this.zzfi.longValue());
                }
                if (this.zzfj != null) {
                    com_google_android_gms_internal_zzart.zzb(9, this.zzfj.longValue());
                }
                if (this.zzfk != null) {
                    com_google_android_gms_internal_zzart.zzb(10, this.zzfk.longValue());
                }
                if (this.zzfl != null) {
                    com_google_android_gms_internal_zzart.zzaf(11, this.zzfl.intValue());
                }
                if (this.zzfm != null) {
                    com_google_android_gms_internal_zzart.zzb(12, this.zzfm.longValue());
                }
                super.zza(com_google_android_gms_internal_zzart);
            }

            public /* synthetic */ zzasa zzb(zzars com_google_android_gms_internal_zzars) throws IOException {
                return zzg(com_google_android_gms_internal_zzars);
            }

            public zza zzg(zzars com_google_android_gms_internal_zzars) throws IOException {
                while (true) {
                    int bU = com_google_android_gms_internal_zzars.bU();
                    switch (bU) {
                        case 0:
                            break;
                        case 8:
                            this.zzdn = Long.valueOf(com_google_android_gms_internal_zzars.bX());
                            continue;
                        case 16:
                            this.zzdo = Long.valueOf(com_google_android_gms_internal_zzars.bX());
                            continue;
                        case 24:
                            this.zzfd = Long.valueOf(com_google_android_gms_internal_zzars.bX());
                            continue;
                        case 32:
                            this.zzfe = Long.valueOf(com_google_android_gms_internal_zzars.bX());
                            continue;
                        case MotionEventCompat.AXIS_GENERIC_9 /*40*/:
                            this.zzff = Long.valueOf(com_google_android_gms_internal_zzars.bX());
                            continue;
                        case 48:
                            this.zzfg = Long.valueOf(com_google_android_gms_internal_zzars.bX());
                            continue;
                        case 56:
                            bU = com_google_android_gms_internal_zzars.bY();
                            switch (bU) {
                                case 0:
                                case 1:
                                case 2:
                                case 1000:
                                    this.zzfh = Integer.valueOf(bU);
                                    break;
                                default:
                                    continue;
                            }
                        case 64:
                            this.zzfi = Long.valueOf(com_google_android_gms_internal_zzars.bX());
                            continue;
                        case XtraBox.MP4_XTRA_BT_GUID /*72*/:
                            this.zzfj = Long.valueOf(com_google_android_gms_internal_zzars.bX());
                            continue;
                        case 80:
                            this.zzfk = Long.valueOf(com_google_android_gms_internal_zzars.bX());
                            continue;
                        case 88:
                            bU = com_google_android_gms_internal_zzars.bY();
                            switch (bU) {
                                case 0:
                                case 1:
                                case 2:
                                case 1000:
                                    this.zzfl = Integer.valueOf(bU);
                                    break;
                                default:
                                    continue;
                            }
                        case 96:
                            this.zzfm = Long.valueOf(com_google_android_gms_internal_zzars.bX());
                            continue;
                        default:
                            if (!super.zza(com_google_android_gms_internal_zzars, bU)) {
                                break;
                            }
                            continue;
                    }
                    return this;
                }
            }

            protected int zzx() {
                int zzx = super.zzx();
                if (this.zzdn != null) {
                    zzx += zzart.zzf(1, this.zzdn.longValue());
                }
                if (this.zzdo != null) {
                    zzx += zzart.zzf(2, this.zzdo.longValue());
                }
                if (this.zzfd != null) {
                    zzx += zzart.zzf(3, this.zzfd.longValue());
                }
                if (this.zzfe != null) {
                    zzx += zzart.zzf(4, this.zzfe.longValue());
                }
                if (this.zzff != null) {
                    zzx += zzart.zzf(5, this.zzff.longValue());
                }
                if (this.zzfg != null) {
                    zzx += zzart.zzf(6, this.zzfg.longValue());
                }
                if (this.zzfh != null) {
                    zzx += zzart.zzah(7, this.zzfh.intValue());
                }
                if (this.zzfi != null) {
                    zzx += zzart.zzf(8, this.zzfi.longValue());
                }
                if (this.zzfj != null) {
                    zzx += zzart.zzf(9, this.zzfj.longValue());
                }
                if (this.zzfk != null) {
                    zzx += zzart.zzf(10, this.zzfk.longValue());
                }
                if (this.zzfl != null) {
                    zzx += zzart.zzah(11, this.zzfl.intValue());
                }
                return this.zzfm != null ? zzx + zzart.zzf(12, this.zzfm.longValue()) : zzx;
            }
        }

        public static final class zzb extends zzaru<zzb> {
            public Long zzep;
            public Long zzeq;
            public Long zzfn;

            public zzb() {
                this.zzep = null;
                this.zzeq = null;
                this.zzfn = null;
                this.btP = -1;
            }

            public void zza(zzart com_google_android_gms_internal_zzart) throws IOException {
                if (this.zzep != null) {
                    com_google_android_gms_internal_zzart.zzb(1, this.zzep.longValue());
                }
                if (this.zzeq != null) {
                    com_google_android_gms_internal_zzart.zzb(2, this.zzeq.longValue());
                }
                if (this.zzfn != null) {
                    com_google_android_gms_internal_zzart.zzb(3, this.zzfn.longValue());
                }
                super.zza(com_google_android_gms_internal_zzart);
            }

            public /* synthetic */ zzasa zzb(zzars com_google_android_gms_internal_zzars) throws IOException {
                return zzh(com_google_android_gms_internal_zzars);
            }

            public zzb zzh(zzars com_google_android_gms_internal_zzars) throws IOException {
                while (true) {
                    int bU = com_google_android_gms_internal_zzars.bU();
                    switch (bU) {
                        case 0:
                            break;
                        case 8:
                            this.zzep = Long.valueOf(com_google_android_gms_internal_zzars.bX());
                            continue;
                        case 16:
                            this.zzeq = Long.valueOf(com_google_android_gms_internal_zzars.bX());
                            continue;
                        case 24:
                            this.zzfn = Long.valueOf(com_google_android_gms_internal_zzars.bX());
                            continue;
                        default:
                            if (!super.zza(com_google_android_gms_internal_zzars, bU)) {
                                break;
                            }
                            continue;
                    }
                    return this;
                }
            }

            protected int zzx() {
                int zzx = super.zzx();
                if (this.zzep != null) {
                    zzx += zzart.zzf(1, this.zzep.longValue());
                }
                if (this.zzeq != null) {
                    zzx += zzart.zzf(2, this.zzeq.longValue());
                }
                return this.zzfn != null ? zzx + zzart.zzf(3, this.zzfn.longValue()) : zzx;
            }
        }

        public zza() {
            this.zzdb = null;
            this.zzda = null;
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
            this.zzcn = null;
            this.zzdw = null;
            this.zzdx = null;
            this.zzdy = null;
            this.zzdz = null;
            this.zzcp = null;
            this.zzea = null;
            this.zzeb = null;
            this.zzec = null;
            this.zzee = null;
            this.zzef = null;
            this.zzeg = null;
            this.zzeh = null;
            this.zzei = null;
            this.zzej = null;
            this.zzcq = null;
            this.zzcr = null;
            this.zzek = null;
            this.zzel = null;
            this.zzem = null;
            this.zzen = null;
            this.zzeo = null;
            this.zzep = null;
            this.zzeq = null;
            this.zzer = null;
            this.zzet = zza.zzaa();
            this.zzev = null;
            this.zzew = null;
            this.zzex = null;
            this.zzey = null;
            this.zzez = null;
            this.zzfa = null;
            this.btP = -1;
        }

        public static zza zzd(byte[] bArr) throws zzarz {
            return (zza) zzasa.zza(new zza(), bArr);
        }

        public void zza(zzart com_google_android_gms_internal_zzart) throws IOException {
            if (this.zzdb != null) {
                com_google_android_gms_internal_zzart.zzq(1, this.zzdb);
            }
            if (this.zzda != null) {
                com_google_android_gms_internal_zzart.zzq(2, this.zzda);
            }
            if (this.zzdc != null) {
                com_google_android_gms_internal_zzart.zzb(3, this.zzdc.longValue());
            }
            if (this.zzdd != null) {
                com_google_android_gms_internal_zzart.zzb(4, this.zzdd.longValue());
            }
            if (this.zzde != null) {
                com_google_android_gms_internal_zzart.zzb(5, this.zzde.longValue());
            }
            if (this.zzdf != null) {
                com_google_android_gms_internal_zzart.zzb(6, this.zzdf.longValue());
            }
            if (this.zzdg != null) {
                com_google_android_gms_internal_zzart.zzb(7, this.zzdg.longValue());
            }
            if (this.zzdh != null) {
                com_google_android_gms_internal_zzart.zzb(8, this.zzdh.longValue());
            }
            if (this.zzdi != null) {
                com_google_android_gms_internal_zzart.zzb(9, this.zzdi.longValue());
            }
            if (this.zzdj != null) {
                com_google_android_gms_internal_zzart.zzb(10, this.zzdj.longValue());
            }
            if (this.zzdk != null) {
                com_google_android_gms_internal_zzart.zzb(11, this.zzdk.longValue());
            }
            if (this.zzdl != null) {
                com_google_android_gms_internal_zzart.zzb(12, this.zzdl.longValue());
            }
            if (this.zzdm != null) {
                com_google_android_gms_internal_zzart.zzq(13, this.zzdm);
            }
            if (this.zzdn != null) {
                com_google_android_gms_internal_zzart.zzb(14, this.zzdn.longValue());
            }
            if (this.zzdo != null) {
                com_google_android_gms_internal_zzart.zzb(15, this.zzdo.longValue());
            }
            if (this.zzdp != null) {
                com_google_android_gms_internal_zzart.zzb(16, this.zzdp.longValue());
            }
            if (this.zzdq != null) {
                com_google_android_gms_internal_zzart.zzb(17, this.zzdq.longValue());
            }
            if (this.zzdr != null) {
                com_google_android_gms_internal_zzart.zzb(18, this.zzdr.longValue());
            }
            if (this.zzds != null) {
                com_google_android_gms_internal_zzart.zzb(19, this.zzds.longValue());
            }
            if (this.zzdt != null) {
                com_google_android_gms_internal_zzart.zzb(20, this.zzdt.longValue());
            }
            if (this.zzev != null) {
                com_google_android_gms_internal_zzart.zzb(21, this.zzev.longValue());
            }
            if (this.zzdu != null) {
                com_google_android_gms_internal_zzart.zzb(22, this.zzdu.longValue());
            }
            if (this.zzdv != null) {
                com_google_android_gms_internal_zzart.zzb(23, this.zzdv.longValue());
            }
            if (this.zzew != null) {
                com_google_android_gms_internal_zzart.zzq(24, this.zzew);
            }
            if (this.zzfa != null) {
                com_google_android_gms_internal_zzart.zzb(25, this.zzfa.longValue());
            }
            if (this.zzex != null) {
                com_google_android_gms_internal_zzart.zzaf(26, this.zzex.intValue());
            }
            if (this.zzcn != null) {
                com_google_android_gms_internal_zzart.zzq(27, this.zzcn);
            }
            if (this.zzey != null) {
                com_google_android_gms_internal_zzart.zzg(28, this.zzey.booleanValue());
            }
            if (this.zzdw != null) {
                com_google_android_gms_internal_zzart.zzq(29, this.zzdw);
            }
            if (this.zzez != null) {
                com_google_android_gms_internal_zzart.zzq(30, this.zzez);
            }
            if (this.zzdx != null) {
                com_google_android_gms_internal_zzart.zzb(31, this.zzdx.longValue());
            }
            if (this.zzdy != null) {
                com_google_android_gms_internal_zzart.zzb(32, this.zzdy.longValue());
            }
            if (this.zzdz != null) {
                com_google_android_gms_internal_zzart.zzb(33, this.zzdz.longValue());
            }
            if (this.zzcp != null) {
                com_google_android_gms_internal_zzart.zzq(34, this.zzcp);
            }
            if (this.zzea != null) {
                com_google_android_gms_internal_zzart.zzb(35, this.zzea.longValue());
            }
            if (this.zzeb != null) {
                com_google_android_gms_internal_zzart.zzb(36, this.zzeb.longValue());
            }
            if (this.zzec != null) {
                com_google_android_gms_internal_zzart.zzb(37, this.zzec.longValue());
            }
            if (this.zzed != null) {
                com_google_android_gms_internal_zzart.zza(38, this.zzed);
            }
            if (this.zzee != null) {
                com_google_android_gms_internal_zzart.zzb(39, this.zzee.longValue());
            }
            if (this.zzef != null) {
                com_google_android_gms_internal_zzart.zzb(40, this.zzef.longValue());
            }
            if (this.zzeg != null) {
                com_google_android_gms_internal_zzart.zzb(41, this.zzeg.longValue());
            }
            if (this.zzeh != null) {
                com_google_android_gms_internal_zzart.zzb(42, this.zzeh.longValue());
            }
            if (this.zzet != null && this.zzet.length > 0) {
                for (zzasa com_google_android_gms_internal_zzasa : this.zzet) {
                    if (com_google_android_gms_internal_zzasa != null) {
                        com_google_android_gms_internal_zzart.zza(43, com_google_android_gms_internal_zzasa);
                    }
                }
            }
            if (this.zzei != null) {
                com_google_android_gms_internal_zzart.zzb(44, this.zzei.longValue());
            }
            if (this.zzej != null) {
                com_google_android_gms_internal_zzart.zzb(45, this.zzej.longValue());
            }
            if (this.zzcq != null) {
                com_google_android_gms_internal_zzart.zzq(46, this.zzcq);
            }
            if (this.zzcr != null) {
                com_google_android_gms_internal_zzart.zzq(47, this.zzcr);
            }
            if (this.zzek != null) {
                com_google_android_gms_internal_zzart.zzaf(48, this.zzek.intValue());
            }
            if (this.zzel != null) {
                com_google_android_gms_internal_zzart.zzaf(49, this.zzel.intValue());
            }
            if (this.zzes != null) {
                com_google_android_gms_internal_zzart.zza(50, this.zzes);
            }
            if (this.zzem != null) {
                com_google_android_gms_internal_zzart.zzb(51, this.zzem.longValue());
            }
            if (this.zzen != null) {
                com_google_android_gms_internal_zzart.zzb(52, this.zzen.longValue());
            }
            if (this.zzeo != null) {
                com_google_android_gms_internal_zzart.zzb(53, this.zzeo.longValue());
            }
            if (this.zzep != null) {
                com_google_android_gms_internal_zzart.zzb(54, this.zzep.longValue());
            }
            if (this.zzeq != null) {
                com_google_android_gms_internal_zzart.zzb(55, this.zzeq.longValue());
            }
            if (this.zzer != null) {
                com_google_android_gms_internal_zzart.zzaf(56, this.zzer.intValue());
            }
            if (this.zzeu != null) {
                com_google_android_gms_internal_zzart.zza(57, this.zzeu);
            }
            if (this.zzfb != null) {
                com_google_android_gms_internal_zzart.zza(201, this.zzfb);
            }
            super.zza(com_google_android_gms_internal_zzart);
        }

        public /* synthetic */ zzasa zzb(zzars com_google_android_gms_internal_zzars) throws IOException {
            return zzf(com_google_android_gms_internal_zzars);
        }

        public zza zzf(zzars com_google_android_gms_internal_zzars) throws IOException {
            while (true) {
                int bU = com_google_android_gms_internal_zzars.bU();
                switch (bU) {
                    case 0:
                        break;
                    case 10:
                        this.zzdb = com_google_android_gms_internal_zzars.readString();
                        continue;
                    case 18:
                        this.zzda = com_google_android_gms_internal_zzars.readString();
                        continue;
                    case 24:
                        this.zzdc = Long.valueOf(com_google_android_gms_internal_zzars.bX());
                        continue;
                    case 32:
                        this.zzdd = Long.valueOf(com_google_android_gms_internal_zzars.bX());
                        continue;
                    case MotionEventCompat.AXIS_GENERIC_9 /*40*/:
                        this.zzde = Long.valueOf(com_google_android_gms_internal_zzars.bX());
                        continue;
                    case 48:
                        this.zzdf = Long.valueOf(com_google_android_gms_internal_zzars.bX());
                        continue;
                    case 56:
                        this.zzdg = Long.valueOf(com_google_android_gms_internal_zzars.bX());
                        continue;
                    case 64:
                        this.zzdh = Long.valueOf(com_google_android_gms_internal_zzars.bX());
                        continue;
                    case XtraBox.MP4_XTRA_BT_GUID /*72*/:
                        this.zzdi = Long.valueOf(com_google_android_gms_internal_zzars.bX());
                        continue;
                    case 80:
                        this.zzdj = Long.valueOf(com_google_android_gms_internal_zzars.bX());
                        continue;
                    case 88:
                        this.zzdk = Long.valueOf(com_google_android_gms_internal_zzars.bX());
                        continue;
                    case 96:
                        this.zzdl = Long.valueOf(com_google_android_gms_internal_zzars.bX());
                        continue;
                    case 106:
                        this.zzdm = com_google_android_gms_internal_zzars.readString();
                        continue;
                    case 112:
                        this.zzdn = Long.valueOf(com_google_android_gms_internal_zzars.bX());
                        continue;
                    case 120:
                        this.zzdo = Long.valueOf(com_google_android_gms_internal_zzars.bX());
                        continue;
                    case 128:
                        this.zzdp = Long.valueOf(com_google_android_gms_internal_zzars.bX());
                        continue;
                    case 136:
                        this.zzdq = Long.valueOf(com_google_android_gms_internal_zzars.bX());
                        continue;
                    case 144:
                        this.zzdr = Long.valueOf(com_google_android_gms_internal_zzars.bX());
                        continue;
                    case 152:
                        this.zzds = Long.valueOf(com_google_android_gms_internal_zzars.bX());
                        continue;
                    case 160:
                        this.zzdt = Long.valueOf(com_google_android_gms_internal_zzars.bX());
                        continue;
                    case 168:
                        this.zzev = Long.valueOf(com_google_android_gms_internal_zzars.bX());
                        continue;
                    case 176:
                        this.zzdu = Long.valueOf(com_google_android_gms_internal_zzars.bX());
                        continue;
                    case 184:
                        this.zzdv = Long.valueOf(com_google_android_gms_internal_zzars.bX());
                        continue;
                    case 194:
                        this.zzew = com_google_android_gms_internal_zzars.readString();
                        continue;
                    case Callback.DEFAULT_DRAG_ANIMATION_DURATION /*200*/:
                        this.zzfa = Long.valueOf(com_google_android_gms_internal_zzars.bX());
                        continue;
                    case 208:
                        bU = com_google_android_gms_internal_zzars.bY();
                        switch (bU) {
                            case 0:
                            case 1:
                            case 2:
                            case 3:
                            case 4:
                            case 5:
                            case 6:
                                this.zzex = Integer.valueOf(bU);
                                break;
                            default:
                                continue;
                        }
                    case 218:
                        this.zzcn = com_google_android_gms_internal_zzars.readString();
                        continue;
                    case 224:
                        this.zzey = Boolean.valueOf(com_google_android_gms_internal_zzars.ca());
                        continue;
                    case 234:
                        this.zzdw = com_google_android_gms_internal_zzars.readString();
                        continue;
                    case 242:
                        this.zzez = com_google_android_gms_internal_zzars.readString();
                        continue;
                    case 248:
                        this.zzdx = Long.valueOf(com_google_android_gms_internal_zzars.bX());
                        continue;
                    case 256:
                        this.zzdy = Long.valueOf(com_google_android_gms_internal_zzars.bX());
                        continue;
                    case 264:
                        this.zzdz = Long.valueOf(com_google_android_gms_internal_zzars.bX());
                        continue;
                    case 274:
                        this.zzcp = com_google_android_gms_internal_zzars.readString();
                        continue;
                    case 280:
                        this.zzea = Long.valueOf(com_google_android_gms_internal_zzars.bX());
                        continue;
                    case 288:
                        this.zzeb = Long.valueOf(com_google_android_gms_internal_zzars.bX());
                        continue;
                    case 296:
                        this.zzec = Long.valueOf(com_google_android_gms_internal_zzars.bX());
                        continue;
                    case 306:
                        if (this.zzed == null) {
                            this.zzed = new zzb();
                        }
                        com_google_android_gms_internal_zzars.zza(this.zzed);
                        continue;
                    case 312:
                        this.zzee = Long.valueOf(com_google_android_gms_internal_zzars.bX());
                        continue;
                    case 320:
                        this.zzef = Long.valueOf(com_google_android_gms_internal_zzars.bX());
                        continue;
                    case 328:
                        this.zzeg = Long.valueOf(com_google_android_gms_internal_zzars.bX());
                        continue;
                    case 336:
                        this.zzeh = Long.valueOf(com_google_android_gms_internal_zzars.bX());
                        continue;
                    case 346:
                        int zzc = zzasd.zzc(com_google_android_gms_internal_zzars, 346);
                        bU = this.zzet == null ? 0 : this.zzet.length;
                        Object obj = new zza[(zzc + bU)];
                        if (bU != 0) {
                            System.arraycopy(this.zzet, 0, obj, 0, bU);
                        }
                        while (bU < obj.length - 1) {
                            obj[bU] = new zza();
                            com_google_android_gms_internal_zzars.zza(obj[bU]);
                            com_google_android_gms_internal_zzars.bU();
                            bU++;
                        }
                        obj[bU] = new zza();
                        com_google_android_gms_internal_zzars.zza(obj[bU]);
                        this.zzet = obj;
                        continue;
                    case 352:
                        this.zzei = Long.valueOf(com_google_android_gms_internal_zzars.bX());
                        continue;
                    case 360:
                        this.zzej = Long.valueOf(com_google_android_gms_internal_zzars.bX());
                        continue;
                    case 370:
                        this.zzcq = com_google_android_gms_internal_zzars.readString();
                        continue;
                    case 378:
                        this.zzcr = com_google_android_gms_internal_zzars.readString();
                        continue;
                    case 384:
                        bU = com_google_android_gms_internal_zzars.bY();
                        switch (bU) {
                            case 0:
                            case 1:
                            case 2:
                            case 1000:
                                this.zzek = Integer.valueOf(bU);
                                break;
                            default:
                                continue;
                        }
                    case 392:
                        bU = com_google_android_gms_internal_zzars.bY();
                        switch (bU) {
                            case 0:
                            case 1:
                            case 2:
                            case 1000:
                                this.zzel = Integer.valueOf(bU);
                                break;
                            default:
                                continue;
                        }
                    case 402:
                        if (this.zzes == null) {
                            this.zzes = new zza();
                        }
                        com_google_android_gms_internal_zzars.zza(this.zzes);
                        continue;
                    case 408:
                        this.zzem = Long.valueOf(com_google_android_gms_internal_zzars.bX());
                        continue;
                    case 416:
                        this.zzen = Long.valueOf(com_google_android_gms_internal_zzars.bX());
                        continue;
                    case 424:
                        this.zzeo = Long.valueOf(com_google_android_gms_internal_zzars.bX());
                        continue;
                    case 432:
                        this.zzep = Long.valueOf(com_google_android_gms_internal_zzars.bX());
                        continue;
                    case 440:
                        this.zzeq = Long.valueOf(com_google_android_gms_internal_zzars.bX());
                        continue;
                    case 448:
                        bU = com_google_android_gms_internal_zzars.bY();
                        switch (bU) {
                            case 0:
                            case 1:
                            case 2:
                            case 1000:
                                this.zzer = Integer.valueOf(bU);
                                break;
                            default:
                                continue;
                        }
                    case 458:
                        if (this.zzeu == null) {
                            this.zzeu = new zzb();
                        }
                        com_google_android_gms_internal_zzars.zza(this.zzeu);
                        continue;
                    case 1610:
                        if (this.zzfb == null) {
                            this.zzfb = new zze();
                        }
                        com_google_android_gms_internal_zzars.zza(this.zzfb);
                        continue;
                    default:
                        if (!super.zza(com_google_android_gms_internal_zzars, bU)) {
                            break;
                        }
                        continue;
                }
                return this;
            }
        }

        protected int zzx() {
            int zzx = super.zzx();
            if (this.zzdb != null) {
                zzx += zzart.zzr(1, this.zzdb);
            }
            if (this.zzda != null) {
                zzx += zzart.zzr(2, this.zzda);
            }
            if (this.zzdc != null) {
                zzx += zzart.zzf(3, this.zzdc.longValue());
            }
            if (this.zzdd != null) {
                zzx += zzart.zzf(4, this.zzdd.longValue());
            }
            if (this.zzde != null) {
                zzx += zzart.zzf(5, this.zzde.longValue());
            }
            if (this.zzdf != null) {
                zzx += zzart.zzf(6, this.zzdf.longValue());
            }
            if (this.zzdg != null) {
                zzx += zzart.zzf(7, this.zzdg.longValue());
            }
            if (this.zzdh != null) {
                zzx += zzart.zzf(8, this.zzdh.longValue());
            }
            if (this.zzdi != null) {
                zzx += zzart.zzf(9, this.zzdi.longValue());
            }
            if (this.zzdj != null) {
                zzx += zzart.zzf(10, this.zzdj.longValue());
            }
            if (this.zzdk != null) {
                zzx += zzart.zzf(11, this.zzdk.longValue());
            }
            if (this.zzdl != null) {
                zzx += zzart.zzf(12, this.zzdl.longValue());
            }
            if (this.zzdm != null) {
                zzx += zzart.zzr(13, this.zzdm);
            }
            if (this.zzdn != null) {
                zzx += zzart.zzf(14, this.zzdn.longValue());
            }
            if (this.zzdo != null) {
                zzx += zzart.zzf(15, this.zzdo.longValue());
            }
            if (this.zzdp != null) {
                zzx += zzart.zzf(16, this.zzdp.longValue());
            }
            if (this.zzdq != null) {
                zzx += zzart.zzf(17, this.zzdq.longValue());
            }
            if (this.zzdr != null) {
                zzx += zzart.zzf(18, this.zzdr.longValue());
            }
            if (this.zzds != null) {
                zzx += zzart.zzf(19, this.zzds.longValue());
            }
            if (this.zzdt != null) {
                zzx += zzart.zzf(20, this.zzdt.longValue());
            }
            if (this.zzev != null) {
                zzx += zzart.zzf(21, this.zzev.longValue());
            }
            if (this.zzdu != null) {
                zzx += zzart.zzf(22, this.zzdu.longValue());
            }
            if (this.zzdv != null) {
                zzx += zzart.zzf(23, this.zzdv.longValue());
            }
            if (this.zzew != null) {
                zzx += zzart.zzr(24, this.zzew);
            }
            if (this.zzfa != null) {
                zzx += zzart.zzf(25, this.zzfa.longValue());
            }
            if (this.zzex != null) {
                zzx += zzart.zzah(26, this.zzex.intValue());
            }
            if (this.zzcn != null) {
                zzx += zzart.zzr(27, this.zzcn);
            }
            if (this.zzey != null) {
                zzx += zzart.zzh(28, this.zzey.booleanValue());
            }
            if (this.zzdw != null) {
                zzx += zzart.zzr(29, this.zzdw);
            }
            if (this.zzez != null) {
                zzx += zzart.zzr(30, this.zzez);
            }
            if (this.zzdx != null) {
                zzx += zzart.zzf(31, this.zzdx.longValue());
            }
            if (this.zzdy != null) {
                zzx += zzart.zzf(32, this.zzdy.longValue());
            }
            if (this.zzdz != null) {
                zzx += zzart.zzf(33, this.zzdz.longValue());
            }
            if (this.zzcp != null) {
                zzx += zzart.zzr(34, this.zzcp);
            }
            if (this.zzea != null) {
                zzx += zzart.zzf(35, this.zzea.longValue());
            }
            if (this.zzeb != null) {
                zzx += zzart.zzf(36, this.zzeb.longValue());
            }
            if (this.zzec != null) {
                zzx += zzart.zzf(37, this.zzec.longValue());
            }
            if (this.zzed != null) {
                zzx += zzart.zzc(38, this.zzed);
            }
            if (this.zzee != null) {
                zzx += zzart.zzf(39, this.zzee.longValue());
            }
            if (this.zzef != null) {
                zzx += zzart.zzf(40, this.zzef.longValue());
            }
            if (this.zzeg != null) {
                zzx += zzart.zzf(41, this.zzeg.longValue());
            }
            if (this.zzeh != null) {
                zzx += zzart.zzf(42, this.zzeh.longValue());
            }
            if (this.zzet != null && this.zzet.length > 0) {
                int i = zzx;
                for (zzasa com_google_android_gms_internal_zzasa : this.zzet) {
                    if (com_google_android_gms_internal_zzasa != null) {
                        i += zzart.zzc(43, com_google_android_gms_internal_zzasa);
                    }
                }
                zzx = i;
            }
            if (this.zzei != null) {
                zzx += zzart.zzf(44, this.zzei.longValue());
            }
            if (this.zzej != null) {
                zzx += zzart.zzf(45, this.zzej.longValue());
            }
            if (this.zzcq != null) {
                zzx += zzart.zzr(46, this.zzcq);
            }
            if (this.zzcr != null) {
                zzx += zzart.zzr(47, this.zzcr);
            }
            if (this.zzek != null) {
                zzx += zzart.zzah(48, this.zzek.intValue());
            }
            if (this.zzel != null) {
                zzx += zzart.zzah(49, this.zzel.intValue());
            }
            if (this.zzes != null) {
                zzx += zzart.zzc(50, this.zzes);
            }
            if (this.zzem != null) {
                zzx += zzart.zzf(51, this.zzem.longValue());
            }
            if (this.zzen != null) {
                zzx += zzart.zzf(52, this.zzen.longValue());
            }
            if (this.zzeo != null) {
                zzx += zzart.zzf(53, this.zzeo.longValue());
            }
            if (this.zzep != null) {
                zzx += zzart.zzf(54, this.zzep.longValue());
            }
            if (this.zzeq != null) {
                zzx += zzart.zzf(55, this.zzeq.longValue());
            }
            if (this.zzer != null) {
                zzx += zzart.zzah(56, this.zzer.intValue());
            }
            if (this.zzeu != null) {
                zzx += zzart.zzc(57, this.zzeu);
            }
            return this.zzfb != null ? zzx + zzart.zzc(201, this.zzfb) : zzx;
        }
    }

    public static final class zzb extends zzaru<zzb> {
        public Long zzfo;
        public Integer zzfp;
        public Boolean zzfq;
        public int[] zzfr;
        public Long zzfs;

        public zzb() {
            this.zzfo = null;
            this.zzfp = null;
            this.zzfq = null;
            this.zzfr = zzasd.btR;
            this.zzfs = null;
            this.btP = -1;
        }

        public void zza(zzart com_google_android_gms_internal_zzart) throws IOException {
            if (this.zzfo != null) {
                com_google_android_gms_internal_zzart.zzb(1, this.zzfo.longValue());
            }
            if (this.zzfp != null) {
                com_google_android_gms_internal_zzart.zzaf(2, this.zzfp.intValue());
            }
            if (this.zzfq != null) {
                com_google_android_gms_internal_zzart.zzg(3, this.zzfq.booleanValue());
            }
            if (this.zzfr != null && this.zzfr.length > 0) {
                for (int zzaf : this.zzfr) {
                    com_google_android_gms_internal_zzart.zzaf(4, zzaf);
                }
            }
            if (this.zzfs != null) {
                com_google_android_gms_internal_zzart.zza(5, this.zzfs.longValue());
            }
            super.zza(com_google_android_gms_internal_zzart);
        }

        public /* synthetic */ zzasa zzb(zzars com_google_android_gms_internal_zzars) throws IOException {
            return zzi(com_google_android_gms_internal_zzars);
        }

        public zzb zzi(zzars com_google_android_gms_internal_zzars) throws IOException {
            while (true) {
                int bU = com_google_android_gms_internal_zzars.bU();
                int zzc;
                switch (bU) {
                    case 0:
                        break;
                    case 8:
                        this.zzfo = Long.valueOf(com_google_android_gms_internal_zzars.bX());
                        continue;
                    case 16:
                        this.zzfp = Integer.valueOf(com_google_android_gms_internal_zzars.bY());
                        continue;
                    case 24:
                        this.zzfq = Boolean.valueOf(com_google_android_gms_internal_zzars.ca());
                        continue;
                    case 32:
                        zzc = zzasd.zzc(com_google_android_gms_internal_zzars, 32);
                        bU = this.zzfr == null ? 0 : this.zzfr.length;
                        Object obj = new int[(zzc + bU)];
                        if (bU != 0) {
                            System.arraycopy(this.zzfr, 0, obj, 0, bU);
                        }
                        while (bU < obj.length - 1) {
                            obj[bU] = com_google_android_gms_internal_zzars.bY();
                            com_google_android_gms_internal_zzars.bU();
                            bU++;
                        }
                        obj[bU] = com_google_android_gms_internal_zzars.bY();
                        this.zzfr = obj;
                        continue;
                    case 34:
                        int zzagt = com_google_android_gms_internal_zzars.zzagt(com_google_android_gms_internal_zzars.cd());
                        zzc = com_google_android_gms_internal_zzars.getPosition();
                        bU = 0;
                        while (com_google_android_gms_internal_zzars.ci() > 0) {
                            com_google_android_gms_internal_zzars.bY();
                            bU++;
                        }
                        com_google_android_gms_internal_zzars.zzagv(zzc);
                        zzc = this.zzfr == null ? 0 : this.zzfr.length;
                        Object obj2 = new int[(bU + zzc)];
                        if (zzc != 0) {
                            System.arraycopy(this.zzfr, 0, obj2, 0, zzc);
                        }
                        while (zzc < obj2.length) {
                            obj2[zzc] = com_google_android_gms_internal_zzars.bY();
                            zzc++;
                        }
                        this.zzfr = obj2;
                        com_google_android_gms_internal_zzars.zzagu(zzagt);
                        continue;
                    case MotionEventCompat.AXIS_GENERIC_9 /*40*/:
                        this.zzfs = Long.valueOf(com_google_android_gms_internal_zzars.bW());
                        continue;
                    default:
                        if (!super.zza(com_google_android_gms_internal_zzars, bU)) {
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
            if (this.zzfo != null) {
                zzx += zzart.zzf(1, this.zzfo.longValue());
            }
            if (this.zzfp != null) {
                zzx += zzart.zzah(2, this.zzfp.intValue());
            }
            if (this.zzfq != null) {
                zzx += zzart.zzh(3, this.zzfq.booleanValue());
            }
            if (this.zzfr != null && this.zzfr.length > 0) {
                int i2 = 0;
                while (i < this.zzfr.length) {
                    i2 += zzart.zzagz(this.zzfr[i]);
                    i++;
                }
                zzx = (zzx + i2) + (this.zzfr.length * 1);
            }
            return this.zzfs != null ? zzx + zzart.zze(5, this.zzfs.longValue()) : zzx;
        }
    }

    public static final class zzc extends zzaru<zzc> {
        public byte[] zzft;
        public byte[] zzfu;

        public zzc() {
            this.zzft = null;
            this.zzfu = null;
            this.btP = -1;
        }

        public void zza(zzart com_google_android_gms_internal_zzart) throws IOException {
            if (this.zzft != null) {
                com_google_android_gms_internal_zzart.zzb(1, this.zzft);
            }
            if (this.zzfu != null) {
                com_google_android_gms_internal_zzart.zzb(2, this.zzfu);
            }
            super.zza(com_google_android_gms_internal_zzart);
        }

        public /* synthetic */ zzasa zzb(zzars com_google_android_gms_internal_zzars) throws IOException {
            return zzj(com_google_android_gms_internal_zzars);
        }

        public zzc zzj(zzars com_google_android_gms_internal_zzars) throws IOException {
            while (true) {
                int bU = com_google_android_gms_internal_zzars.bU();
                switch (bU) {
                    case 0:
                        break;
                    case 10:
                        this.zzft = com_google_android_gms_internal_zzars.readBytes();
                        continue;
                    case 18:
                        this.zzfu = com_google_android_gms_internal_zzars.readBytes();
                        continue;
                    default:
                        if (!super.zza(com_google_android_gms_internal_zzars, bU)) {
                            break;
                        }
                        continue;
                }
                return this;
            }
        }

        protected int zzx() {
            int zzx = super.zzx();
            if (this.zzft != null) {
                zzx += zzart.zzc(1, this.zzft);
            }
            return this.zzfu != null ? zzx + zzart.zzc(2, this.zzfu) : zzx;
        }
    }

    public static final class zzd extends zzaru<zzd> {
        public byte[] data;
        public byte[] zzfv;
        public byte[] zzfw;
        public byte[] zzfx;

        public zzd() {
            this.data = null;
            this.zzfv = null;
            this.zzfw = null;
            this.zzfx = null;
            this.btP = -1;
        }

        public static zzd zze(byte[] bArr) throws zzarz {
            return (zzd) zzasa.zza(new zzd(), bArr);
        }

        public void zza(zzart com_google_android_gms_internal_zzart) throws IOException {
            if (this.data != null) {
                com_google_android_gms_internal_zzart.zzb(1, this.data);
            }
            if (this.zzfv != null) {
                com_google_android_gms_internal_zzart.zzb(2, this.zzfv);
            }
            if (this.zzfw != null) {
                com_google_android_gms_internal_zzart.zzb(3, this.zzfw);
            }
            if (this.zzfx != null) {
                com_google_android_gms_internal_zzart.zzb(4, this.zzfx);
            }
            super.zza(com_google_android_gms_internal_zzart);
        }

        public /* synthetic */ zzasa zzb(zzars com_google_android_gms_internal_zzars) throws IOException {
            return zzk(com_google_android_gms_internal_zzars);
        }

        public zzd zzk(zzars com_google_android_gms_internal_zzars) throws IOException {
            while (true) {
                int bU = com_google_android_gms_internal_zzars.bU();
                switch (bU) {
                    case 0:
                        break;
                    case 10:
                        this.data = com_google_android_gms_internal_zzars.readBytes();
                        continue;
                    case 18:
                        this.zzfv = com_google_android_gms_internal_zzars.readBytes();
                        continue;
                    case NalUnitTypes.NAL_TYPE_RSV_VCL26 /*26*/:
                        this.zzfw = com_google_android_gms_internal_zzars.readBytes();
                        continue;
                    case 34:
                        this.zzfx = com_google_android_gms_internal_zzars.readBytes();
                        continue;
                    default:
                        if (!super.zza(com_google_android_gms_internal_zzars, bU)) {
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
                zzx += zzart.zzc(1, this.data);
            }
            if (this.zzfv != null) {
                zzx += zzart.zzc(2, this.zzfv);
            }
            if (this.zzfw != null) {
                zzx += zzart.zzc(3, this.zzfw);
            }
            return this.zzfx != null ? zzx + zzart.zzc(4, this.zzfx) : zzx;
        }
    }

    public static final class zze extends zzaru<zze> {
        public Long zzfo;
        public String zzfy;
        public byte[] zzfz;

        public zze() {
            this.zzfo = null;
            this.zzfy = null;
            this.zzfz = null;
            this.btP = -1;
        }

        public void zza(zzart com_google_android_gms_internal_zzart) throws IOException {
            if (this.zzfo != null) {
                com_google_android_gms_internal_zzart.zzb(1, this.zzfo.longValue());
            }
            if (this.zzfy != null) {
                com_google_android_gms_internal_zzart.zzq(3, this.zzfy);
            }
            if (this.zzfz != null) {
                com_google_android_gms_internal_zzart.zzb(4, this.zzfz);
            }
            super.zza(com_google_android_gms_internal_zzart);
        }

        public /* synthetic */ zzasa zzb(zzars com_google_android_gms_internal_zzars) throws IOException {
            return zzl(com_google_android_gms_internal_zzars);
        }

        public zze zzl(zzars com_google_android_gms_internal_zzars) throws IOException {
            while (true) {
                int bU = com_google_android_gms_internal_zzars.bU();
                switch (bU) {
                    case 0:
                        break;
                    case 8:
                        this.zzfo = Long.valueOf(com_google_android_gms_internal_zzars.bX());
                        continue;
                    case NalUnitTypes.NAL_TYPE_RSV_VCL26 /*26*/:
                        this.zzfy = com_google_android_gms_internal_zzars.readString();
                        continue;
                    case 34:
                        this.zzfz = com_google_android_gms_internal_zzars.readBytes();
                        continue;
                    default:
                        if (!super.zza(com_google_android_gms_internal_zzars, bU)) {
                            break;
                        }
                        continue;
                }
                return this;
            }
        }

        protected int zzx() {
            int zzx = super.zzx();
            if (this.zzfo != null) {
                zzx += zzart.zzf(1, this.zzfo.longValue());
            }
            if (this.zzfy != null) {
                zzx += zzart.zzr(3, this.zzfy);
            }
            return this.zzfz != null ? zzx + zzart.zzc(4, this.zzfz) : zzx;
        }
    }

    public static final class zzf extends zzaru<zzf> {
        public byte[] zzfv;
        public byte[][] zzga;
        public Integer zzgb;
        public Integer zzgc;

        public zzf() {
            this.zzga = zzasd.btX;
            this.zzfv = null;
            this.zzgb = null;
            this.zzgc = null;
            this.btP = -1;
        }

        public void zza(zzart com_google_android_gms_internal_zzart) throws IOException {
            if (this.zzga != null && this.zzga.length > 0) {
                for (byte[] bArr : this.zzga) {
                    if (bArr != null) {
                        com_google_android_gms_internal_zzart.zzb(1, bArr);
                    }
                }
            }
            if (this.zzfv != null) {
                com_google_android_gms_internal_zzart.zzb(2, this.zzfv);
            }
            if (this.zzgb != null) {
                com_google_android_gms_internal_zzart.zzaf(3, this.zzgb.intValue());
            }
            if (this.zzgc != null) {
                com_google_android_gms_internal_zzart.zzaf(4, this.zzgc.intValue());
            }
            super.zza(com_google_android_gms_internal_zzart);
        }

        public /* synthetic */ zzasa zzb(zzars com_google_android_gms_internal_zzars) throws IOException {
            return zzm(com_google_android_gms_internal_zzars);
        }

        public zzf zzm(zzars com_google_android_gms_internal_zzars) throws IOException {
            while (true) {
                int bU = com_google_android_gms_internal_zzars.bU();
                switch (bU) {
                    case 0:
                        break;
                    case 10:
                        int zzc = zzasd.zzc(com_google_android_gms_internal_zzars, 10);
                        bU = this.zzga == null ? 0 : this.zzga.length;
                        Object obj = new byte[(zzc + bU)][];
                        if (bU != 0) {
                            System.arraycopy(this.zzga, 0, obj, 0, bU);
                        }
                        while (bU < obj.length - 1) {
                            obj[bU] = com_google_android_gms_internal_zzars.readBytes();
                            com_google_android_gms_internal_zzars.bU();
                            bU++;
                        }
                        obj[bU] = com_google_android_gms_internal_zzars.readBytes();
                        this.zzga = obj;
                        continue;
                    case 18:
                        this.zzfv = com_google_android_gms_internal_zzars.readBytes();
                        continue;
                    case 24:
                        bU = com_google_android_gms_internal_zzars.bY();
                        switch (bU) {
                            case 0:
                            case 1:
                                this.zzgb = Integer.valueOf(bU);
                                break;
                            default:
                                continue;
                        }
                    case 32:
                        bU = com_google_android_gms_internal_zzars.bY();
                        switch (bU) {
                            case 0:
                            case 1:
                                this.zzgc = Integer.valueOf(bU);
                                break;
                            default:
                                continue;
                        }
                    default:
                        if (!super.zza(com_google_android_gms_internal_zzars, bU)) {
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
            if (this.zzga == null || this.zzga.length <= 0) {
                i = zzx;
            } else {
                int i2 = 0;
                int i3 = 0;
                while (i < this.zzga.length) {
                    byte[] bArr = this.zzga[i];
                    if (bArr != null) {
                        i3++;
                        i2 += zzart.zzbg(bArr);
                    }
                    i++;
                }
                i = (zzx + i2) + (i3 * 1);
            }
            if (this.zzfv != null) {
                i += zzart.zzc(2, this.zzfv);
            }
            if (this.zzgb != null) {
                i += zzart.zzah(3, this.zzgb.intValue());
            }
            return this.zzgc != null ? i + zzart.zzah(4, this.zzgc.intValue()) : i;
        }
    }
}
