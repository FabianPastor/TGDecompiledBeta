package com.google.android.gms.internal;

import android.support.v4.view.MotionEventCompat;
import com.googlecode.mp4parser.authoring.tracks.h265.NalUnitTypes;
import java.io.IOException;
import org.telegram.messenger.exoplayer2.extractor.ts.TsExtractor;

public interface zzauw {

    public static final class zza extends zzbyd<zza> {
        private static volatile zza[] zzbwW;
        public zzf zzbwX;
        public zzf zzbwY;
        public Boolean zzbwZ;
        public Integer zzbwn;

        public zza() {
            zzNB();
        }

        public static zza[] zzNA() {
            if (zzbwW == null) {
                synchronized (zzbyh.zzcwK) {
                    if (zzbwW == null) {
                        zzbwW = new zza[0];
                    }
                }
            }
            return zzbwW;
        }

        public boolean equals(Object obj) {
            if (obj == this) {
                return true;
            }
            if (!(obj instanceof zza)) {
                return false;
            }
            zza com_google_android_gms_internal_zzauw_zza = (zza) obj;
            if (this.zzbwn == null) {
                if (com_google_android_gms_internal_zzauw_zza.zzbwn != null) {
                    return false;
                }
            } else if (!this.zzbwn.equals(com_google_android_gms_internal_zzauw_zza.zzbwn)) {
                return false;
            }
            if (this.zzbwX == null) {
                if (com_google_android_gms_internal_zzauw_zza.zzbwX != null) {
                    return false;
                }
            } else if (!this.zzbwX.equals(com_google_android_gms_internal_zzauw_zza.zzbwX)) {
                return false;
            }
            if (this.zzbwY == null) {
                if (com_google_android_gms_internal_zzauw_zza.zzbwY != null) {
                    return false;
                }
            } else if (!this.zzbwY.equals(com_google_android_gms_internal_zzauw_zza.zzbwY)) {
                return false;
            }
            if (this.zzbwZ == null) {
                if (com_google_android_gms_internal_zzauw_zza.zzbwZ != null) {
                    return false;
                }
            } else if (!this.zzbwZ.equals(com_google_android_gms_internal_zzauw_zza.zzbwZ)) {
                return false;
            }
            return (this.zzcwC == null || this.zzcwC.isEmpty()) ? com_google_android_gms_internal_zzauw_zza.zzcwC == null || com_google_android_gms_internal_zzauw_zza.zzcwC.isEmpty() : this.zzcwC.equals(com_google_android_gms_internal_zzauw_zza.zzcwC);
        }

        public int hashCode() {
            int i = 0;
            int hashCode = ((this.zzbwZ == null ? 0 : this.zzbwZ.hashCode()) + (((this.zzbwY == null ? 0 : this.zzbwY.hashCode()) + (((this.zzbwX == null ? 0 : this.zzbwX.hashCode()) + (((this.zzbwn == null ? 0 : this.zzbwn.hashCode()) + ((getClass().getName().hashCode() + 527) * 31)) * 31)) * 31)) * 31)) * 31;
            if (!(this.zzcwC == null || this.zzcwC.isEmpty())) {
                i = this.zzcwC.hashCode();
            }
            return hashCode + i;
        }

        public zza zzNB() {
            this.zzbwn = null;
            this.zzbwX = null;
            this.zzbwY = null;
            this.zzbwZ = null;
            this.zzcwC = null;
            this.zzcwL = -1;
            return this;
        }

        public zza zzP(zzbyb com_google_android_gms_internal_zzbyb) throws IOException {
            while (true) {
                int zzaeW = com_google_android_gms_internal_zzbyb.zzaeW();
                switch (zzaeW) {
                    case 0:
                        break;
                    case 8:
                        this.zzbwn = Integer.valueOf(com_google_android_gms_internal_zzbyb.zzafa());
                        continue;
                    case 18:
                        if (this.zzbwX == null) {
                            this.zzbwX = new zzf();
                        }
                        com_google_android_gms_internal_zzbyb.zza(this.zzbwX);
                        continue;
                    case NalUnitTypes.NAL_TYPE_RSV_VCL26 /*26*/:
                        if (this.zzbwY == null) {
                            this.zzbwY = new zzf();
                        }
                        com_google_android_gms_internal_zzbyb.zza(this.zzbwY);
                        continue;
                    case 32:
                        this.zzbwZ = Boolean.valueOf(com_google_android_gms_internal_zzbyb.zzafc());
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

        public void zza(zzbyc com_google_android_gms_internal_zzbyc) throws IOException {
            if (this.zzbwn != null) {
                com_google_android_gms_internal_zzbyc.zzJ(1, this.zzbwn.intValue());
            }
            if (this.zzbwX != null) {
                com_google_android_gms_internal_zzbyc.zza(2, this.zzbwX);
            }
            if (this.zzbwY != null) {
                com_google_android_gms_internal_zzbyc.zza(3, this.zzbwY);
            }
            if (this.zzbwZ != null) {
                com_google_android_gms_internal_zzbyc.zzg(4, this.zzbwZ.booleanValue());
            }
            super.zza(com_google_android_gms_internal_zzbyc);
        }

        public /* synthetic */ zzbyj zzb(zzbyb com_google_android_gms_internal_zzbyb) throws IOException {
            return zzP(com_google_android_gms_internal_zzbyb);
        }

        protected int zzu() {
            int zzu = super.zzu();
            if (this.zzbwn != null) {
                zzu += zzbyc.zzL(1, this.zzbwn.intValue());
            }
            if (this.zzbwX != null) {
                zzu += zzbyc.zzc(2, this.zzbwX);
            }
            if (this.zzbwY != null) {
                zzu += zzbyc.zzc(3, this.zzbwY);
            }
            return this.zzbwZ != null ? zzu + zzbyc.zzh(4, this.zzbwZ.booleanValue()) : zzu;
        }
    }

    public static final class zzb extends zzbyd<zzb> {
        private static volatile zzb[] zzbxa;
        public Integer count;
        public String name;
        public zzc[] zzbxb;
        public Long zzbxc;
        public Long zzbxd;

        public zzb() {
            zzND();
        }

        public static zzb[] zzNC() {
            if (zzbxa == null) {
                synchronized (zzbyh.zzcwK) {
                    if (zzbxa == null) {
                        zzbxa = new zzb[0];
                    }
                }
            }
            return zzbxa;
        }

        public boolean equals(Object obj) {
            if (obj == this) {
                return true;
            }
            if (!(obj instanceof zzb)) {
                return false;
            }
            zzb com_google_android_gms_internal_zzauw_zzb = (zzb) obj;
            if (!zzbyh.equals(this.zzbxb, com_google_android_gms_internal_zzauw_zzb.zzbxb)) {
                return false;
            }
            if (this.name == null) {
                if (com_google_android_gms_internal_zzauw_zzb.name != null) {
                    return false;
                }
            } else if (!this.name.equals(com_google_android_gms_internal_zzauw_zzb.name)) {
                return false;
            }
            if (this.zzbxc == null) {
                if (com_google_android_gms_internal_zzauw_zzb.zzbxc != null) {
                    return false;
                }
            } else if (!this.zzbxc.equals(com_google_android_gms_internal_zzauw_zzb.zzbxc)) {
                return false;
            }
            if (this.zzbxd == null) {
                if (com_google_android_gms_internal_zzauw_zzb.zzbxd != null) {
                    return false;
                }
            } else if (!this.zzbxd.equals(com_google_android_gms_internal_zzauw_zzb.zzbxd)) {
                return false;
            }
            if (this.count == null) {
                if (com_google_android_gms_internal_zzauw_zzb.count != null) {
                    return false;
                }
            } else if (!this.count.equals(com_google_android_gms_internal_zzauw_zzb.count)) {
                return false;
            }
            return (this.zzcwC == null || this.zzcwC.isEmpty()) ? com_google_android_gms_internal_zzauw_zzb.zzcwC == null || com_google_android_gms_internal_zzauw_zzb.zzcwC.isEmpty() : this.zzcwC.equals(com_google_android_gms_internal_zzauw_zzb.zzcwC);
        }

        public int hashCode() {
            int i = 0;
            int hashCode = ((this.count == null ? 0 : this.count.hashCode()) + (((this.zzbxd == null ? 0 : this.zzbxd.hashCode()) + (((this.zzbxc == null ? 0 : this.zzbxc.hashCode()) + (((this.name == null ? 0 : this.name.hashCode()) + ((((getClass().getName().hashCode() + 527) * 31) + zzbyh.hashCode(this.zzbxb)) * 31)) * 31)) * 31)) * 31)) * 31;
            if (!(this.zzcwC == null || this.zzcwC.isEmpty())) {
                i = this.zzcwC.hashCode();
            }
            return hashCode + i;
        }

        public zzb zzND() {
            this.zzbxb = zzc.zzNE();
            this.name = null;
            this.zzbxc = null;
            this.zzbxd = null;
            this.count = null;
            this.zzcwC = null;
            this.zzcwL = -1;
            return this;
        }

        public zzb zzQ(zzbyb com_google_android_gms_internal_zzbyb) throws IOException {
            while (true) {
                int zzaeW = com_google_android_gms_internal_zzbyb.zzaeW();
                switch (zzaeW) {
                    case 0:
                        break;
                    case 10:
                        int zzb = zzbym.zzb(com_google_android_gms_internal_zzbyb, 10);
                        zzaeW = this.zzbxb == null ? 0 : this.zzbxb.length;
                        Object obj = new zzc[(zzb + zzaeW)];
                        if (zzaeW != 0) {
                            System.arraycopy(this.zzbxb, 0, obj, 0, zzaeW);
                        }
                        while (zzaeW < obj.length - 1) {
                            obj[zzaeW] = new zzc();
                            com_google_android_gms_internal_zzbyb.zza(obj[zzaeW]);
                            com_google_android_gms_internal_zzbyb.zzaeW();
                            zzaeW++;
                        }
                        obj[zzaeW] = new zzc();
                        com_google_android_gms_internal_zzbyb.zza(obj[zzaeW]);
                        this.zzbxb = obj;
                        continue;
                    case 18:
                        this.name = com_google_android_gms_internal_zzbyb.readString();
                        continue;
                    case 24:
                        this.zzbxc = Long.valueOf(com_google_android_gms_internal_zzbyb.zzaeZ());
                        continue;
                    case 32:
                        this.zzbxd = Long.valueOf(com_google_android_gms_internal_zzbyb.zzaeZ());
                        continue;
                    case MotionEventCompat.AXIS_GENERIC_9 /*40*/:
                        this.count = Integer.valueOf(com_google_android_gms_internal_zzbyb.zzafa());
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

        public void zza(zzbyc com_google_android_gms_internal_zzbyc) throws IOException {
            if (this.zzbxb != null && this.zzbxb.length > 0) {
                for (zzbyj com_google_android_gms_internal_zzbyj : this.zzbxb) {
                    if (com_google_android_gms_internal_zzbyj != null) {
                        com_google_android_gms_internal_zzbyc.zza(1, com_google_android_gms_internal_zzbyj);
                    }
                }
            }
            if (this.name != null) {
                com_google_android_gms_internal_zzbyc.zzq(2, this.name);
            }
            if (this.zzbxc != null) {
                com_google_android_gms_internal_zzbyc.zzb(3, this.zzbxc.longValue());
            }
            if (this.zzbxd != null) {
                com_google_android_gms_internal_zzbyc.zzb(4, this.zzbxd.longValue());
            }
            if (this.count != null) {
                com_google_android_gms_internal_zzbyc.zzJ(5, this.count.intValue());
            }
            super.zza(com_google_android_gms_internal_zzbyc);
        }

        public /* synthetic */ zzbyj zzb(zzbyb com_google_android_gms_internal_zzbyb) throws IOException {
            return zzQ(com_google_android_gms_internal_zzbyb);
        }

        protected int zzu() {
            int zzu = super.zzu();
            if (this.zzbxb != null && this.zzbxb.length > 0) {
                for (zzbyj com_google_android_gms_internal_zzbyj : this.zzbxb) {
                    if (com_google_android_gms_internal_zzbyj != null) {
                        zzu += zzbyc.zzc(1, com_google_android_gms_internal_zzbyj);
                    }
                }
            }
            if (this.name != null) {
                zzu += zzbyc.zzr(2, this.name);
            }
            if (this.zzbxc != null) {
                zzu += zzbyc.zzf(3, this.zzbxc.longValue());
            }
            if (this.zzbxd != null) {
                zzu += zzbyc.zzf(4, this.zzbxd.longValue());
            }
            return this.count != null ? zzu + zzbyc.zzL(5, this.count.intValue()) : zzu;
        }
    }

    public static final class zzc extends zzbyd<zzc> {
        private static volatile zzc[] zzbxe;
        public String name;
        public String zzaGV;
        public Float zzbwh;
        public Double zzbwi;
        public Long zzbxf;

        public zzc() {
            zzNF();
        }

        public static zzc[] zzNE() {
            if (zzbxe == null) {
                synchronized (zzbyh.zzcwK) {
                    if (zzbxe == null) {
                        zzbxe = new zzc[0];
                    }
                }
            }
            return zzbxe;
        }

        public boolean equals(Object obj) {
            if (obj == this) {
                return true;
            }
            if (!(obj instanceof zzc)) {
                return false;
            }
            zzc com_google_android_gms_internal_zzauw_zzc = (zzc) obj;
            if (this.name == null) {
                if (com_google_android_gms_internal_zzauw_zzc.name != null) {
                    return false;
                }
            } else if (!this.name.equals(com_google_android_gms_internal_zzauw_zzc.name)) {
                return false;
            }
            if (this.zzaGV == null) {
                if (com_google_android_gms_internal_zzauw_zzc.zzaGV != null) {
                    return false;
                }
            } else if (!this.zzaGV.equals(com_google_android_gms_internal_zzauw_zzc.zzaGV)) {
                return false;
            }
            if (this.zzbxf == null) {
                if (com_google_android_gms_internal_zzauw_zzc.zzbxf != null) {
                    return false;
                }
            } else if (!this.zzbxf.equals(com_google_android_gms_internal_zzauw_zzc.zzbxf)) {
                return false;
            }
            if (this.zzbwh == null) {
                if (com_google_android_gms_internal_zzauw_zzc.zzbwh != null) {
                    return false;
                }
            } else if (!this.zzbwh.equals(com_google_android_gms_internal_zzauw_zzc.zzbwh)) {
                return false;
            }
            if (this.zzbwi == null) {
                if (com_google_android_gms_internal_zzauw_zzc.zzbwi != null) {
                    return false;
                }
            } else if (!this.zzbwi.equals(com_google_android_gms_internal_zzauw_zzc.zzbwi)) {
                return false;
            }
            return (this.zzcwC == null || this.zzcwC.isEmpty()) ? com_google_android_gms_internal_zzauw_zzc.zzcwC == null || com_google_android_gms_internal_zzauw_zzc.zzcwC.isEmpty() : this.zzcwC.equals(com_google_android_gms_internal_zzauw_zzc.zzcwC);
        }

        public int hashCode() {
            int i = 0;
            int hashCode = ((this.zzbwi == null ? 0 : this.zzbwi.hashCode()) + (((this.zzbwh == null ? 0 : this.zzbwh.hashCode()) + (((this.zzbxf == null ? 0 : this.zzbxf.hashCode()) + (((this.zzaGV == null ? 0 : this.zzaGV.hashCode()) + (((this.name == null ? 0 : this.name.hashCode()) + ((getClass().getName().hashCode() + 527) * 31)) * 31)) * 31)) * 31)) * 31)) * 31;
            if (!(this.zzcwC == null || this.zzcwC.isEmpty())) {
                i = this.zzcwC.hashCode();
            }
            return hashCode + i;
        }

        public zzc zzNF() {
            this.name = null;
            this.zzaGV = null;
            this.zzbxf = null;
            this.zzbwh = null;
            this.zzbwi = null;
            this.zzcwC = null;
            this.zzcwL = -1;
            return this;
        }

        public zzc zzR(zzbyb com_google_android_gms_internal_zzbyb) throws IOException {
            while (true) {
                int zzaeW = com_google_android_gms_internal_zzbyb.zzaeW();
                switch (zzaeW) {
                    case 0:
                        break;
                    case 10:
                        this.name = com_google_android_gms_internal_zzbyb.readString();
                        continue;
                    case 18:
                        this.zzaGV = com_google_android_gms_internal_zzbyb.readString();
                        continue;
                    case 24:
                        this.zzbxf = Long.valueOf(com_google_android_gms_internal_zzbyb.zzaeZ());
                        continue;
                    case 37:
                        this.zzbwh = Float.valueOf(com_google_android_gms_internal_zzbyb.readFloat());
                        continue;
                    case 41:
                        this.zzbwi = Double.valueOf(com_google_android_gms_internal_zzbyb.readDouble());
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

        public void zza(zzbyc com_google_android_gms_internal_zzbyc) throws IOException {
            if (this.name != null) {
                com_google_android_gms_internal_zzbyc.zzq(1, this.name);
            }
            if (this.zzaGV != null) {
                com_google_android_gms_internal_zzbyc.zzq(2, this.zzaGV);
            }
            if (this.zzbxf != null) {
                com_google_android_gms_internal_zzbyc.zzb(3, this.zzbxf.longValue());
            }
            if (this.zzbwh != null) {
                com_google_android_gms_internal_zzbyc.zzc(4, this.zzbwh.floatValue());
            }
            if (this.zzbwi != null) {
                com_google_android_gms_internal_zzbyc.zza(5, this.zzbwi.doubleValue());
            }
            super.zza(com_google_android_gms_internal_zzbyc);
        }

        public /* synthetic */ zzbyj zzb(zzbyb com_google_android_gms_internal_zzbyb) throws IOException {
            return zzR(com_google_android_gms_internal_zzbyb);
        }

        protected int zzu() {
            int zzu = super.zzu();
            if (this.name != null) {
                zzu += zzbyc.zzr(1, this.name);
            }
            if (this.zzaGV != null) {
                zzu += zzbyc.zzr(2, this.zzaGV);
            }
            if (this.zzbxf != null) {
                zzu += zzbyc.zzf(3, this.zzbxf.longValue());
            }
            if (this.zzbwh != null) {
                zzu += zzbyc.zzd(4, this.zzbwh.floatValue());
            }
            return this.zzbwi != null ? zzu + zzbyc.zzb(5, this.zzbwi.doubleValue()) : zzu;
        }
    }

    public static final class zzd extends zzbyd<zzd> {
        public zze[] zzbxg;

        public zzd() {
            zzNG();
        }

        public boolean equals(Object obj) {
            if (obj == this) {
                return true;
            }
            if (!(obj instanceof zzd)) {
                return false;
            }
            zzd com_google_android_gms_internal_zzauw_zzd = (zzd) obj;
            return zzbyh.equals(this.zzbxg, com_google_android_gms_internal_zzauw_zzd.zzbxg) ? (this.zzcwC == null || this.zzcwC.isEmpty()) ? com_google_android_gms_internal_zzauw_zzd.zzcwC == null || com_google_android_gms_internal_zzauw_zzd.zzcwC.isEmpty() : this.zzcwC.equals(com_google_android_gms_internal_zzauw_zzd.zzcwC) : false;
        }

        public int hashCode() {
            int hashCode = (((getClass().getName().hashCode() + 527) * 31) + zzbyh.hashCode(this.zzbxg)) * 31;
            int hashCode2 = (this.zzcwC == null || this.zzcwC.isEmpty()) ? 0 : this.zzcwC.hashCode();
            return hashCode2 + hashCode;
        }

        public zzd zzNG() {
            this.zzbxg = zze.zzNH();
            this.zzcwC = null;
            this.zzcwL = -1;
            return this;
        }

        public zzd zzS(zzbyb com_google_android_gms_internal_zzbyb) throws IOException {
            while (true) {
                int zzaeW = com_google_android_gms_internal_zzbyb.zzaeW();
                switch (zzaeW) {
                    case 0:
                        break;
                    case 10:
                        int zzb = zzbym.zzb(com_google_android_gms_internal_zzbyb, 10);
                        zzaeW = this.zzbxg == null ? 0 : this.zzbxg.length;
                        Object obj = new zze[(zzb + zzaeW)];
                        if (zzaeW != 0) {
                            System.arraycopy(this.zzbxg, 0, obj, 0, zzaeW);
                        }
                        while (zzaeW < obj.length - 1) {
                            obj[zzaeW] = new zze();
                            com_google_android_gms_internal_zzbyb.zza(obj[zzaeW]);
                            com_google_android_gms_internal_zzbyb.zzaeW();
                            zzaeW++;
                        }
                        obj[zzaeW] = new zze();
                        com_google_android_gms_internal_zzbyb.zza(obj[zzaeW]);
                        this.zzbxg = obj;
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

        public void zza(zzbyc com_google_android_gms_internal_zzbyc) throws IOException {
            if (this.zzbxg != null && this.zzbxg.length > 0) {
                for (zzbyj com_google_android_gms_internal_zzbyj : this.zzbxg) {
                    if (com_google_android_gms_internal_zzbyj != null) {
                        com_google_android_gms_internal_zzbyc.zza(1, com_google_android_gms_internal_zzbyj);
                    }
                }
            }
            super.zza(com_google_android_gms_internal_zzbyc);
        }

        public /* synthetic */ zzbyj zzb(zzbyb com_google_android_gms_internal_zzbyb) throws IOException {
            return zzS(com_google_android_gms_internal_zzbyb);
        }

        protected int zzu() {
            int zzu = super.zzu();
            if (this.zzbxg != null && this.zzbxg.length > 0) {
                for (zzbyj com_google_android_gms_internal_zzbyj : this.zzbxg) {
                    if (com_google_android_gms_internal_zzbyj != null) {
                        zzu += zzbyc.zzc(1, com_google_android_gms_internal_zzbyj);
                    }
                }
            }
            return zzu;
        }
    }

    public static final class zze extends zzbyd<zze> {
        private static volatile zze[] zzbxh;
        public String zzaS;
        public String zzbb;
        public String zzbhN;
        public String zzbqK;
        public String zzbqL;
        public String zzbqO;
        public String zzbqS;
        public Integer zzbxA;
        public Boolean zzbxB;
        public zza[] zzbxC;
        public Integer zzbxD;
        public Integer zzbxE;
        public Integer zzbxF;
        public String zzbxG;
        public Long zzbxH;
        public Long zzbxI;
        public Integer zzbxi;
        public zzb[] zzbxj;
        public zzg[] zzbxk;
        public Long zzbxl;
        public Long zzbxm;
        public Long zzbxn;
        public Long zzbxo;
        public Long zzbxp;
        public String zzbxq;
        public String zzbxr;
        public String zzbxs;
        public Integer zzbxt;
        public Long zzbxu;
        public Long zzbxv;
        public String zzbxw;
        public Boolean zzbxx;
        public String zzbxy;
        public Long zzbxz;

        public zze() {
            zzNI();
        }

        public static zze[] zzNH() {
            if (zzbxh == null) {
                synchronized (zzbyh.zzcwK) {
                    if (zzbxh == null) {
                        zzbxh = new zze[0];
                    }
                }
            }
            return zzbxh;
        }

        public boolean equals(Object obj) {
            if (obj == this) {
                return true;
            }
            if (!(obj instanceof zze)) {
                return false;
            }
            zze com_google_android_gms_internal_zzauw_zze = (zze) obj;
            if (this.zzbxi == null) {
                if (com_google_android_gms_internal_zzauw_zze.zzbxi != null) {
                    return false;
                }
            } else if (!this.zzbxi.equals(com_google_android_gms_internal_zzauw_zze.zzbxi)) {
                return false;
            }
            if (!zzbyh.equals(this.zzbxj, com_google_android_gms_internal_zzauw_zze.zzbxj) || !zzbyh.equals(this.zzbxk, com_google_android_gms_internal_zzauw_zze.zzbxk)) {
                return false;
            }
            if (this.zzbxl == null) {
                if (com_google_android_gms_internal_zzauw_zze.zzbxl != null) {
                    return false;
                }
            } else if (!this.zzbxl.equals(com_google_android_gms_internal_zzauw_zze.zzbxl)) {
                return false;
            }
            if (this.zzbxm == null) {
                if (com_google_android_gms_internal_zzauw_zze.zzbxm != null) {
                    return false;
                }
            } else if (!this.zzbxm.equals(com_google_android_gms_internal_zzauw_zze.zzbxm)) {
                return false;
            }
            if (this.zzbxn == null) {
                if (com_google_android_gms_internal_zzauw_zze.zzbxn != null) {
                    return false;
                }
            } else if (!this.zzbxn.equals(com_google_android_gms_internal_zzauw_zze.zzbxn)) {
                return false;
            }
            if (this.zzbxo == null) {
                if (com_google_android_gms_internal_zzauw_zze.zzbxo != null) {
                    return false;
                }
            } else if (!this.zzbxo.equals(com_google_android_gms_internal_zzauw_zze.zzbxo)) {
                return false;
            }
            if (this.zzbxp == null) {
                if (com_google_android_gms_internal_zzauw_zze.zzbxp != null) {
                    return false;
                }
            } else if (!this.zzbxp.equals(com_google_android_gms_internal_zzauw_zze.zzbxp)) {
                return false;
            }
            if (this.zzbxq == null) {
                if (com_google_android_gms_internal_zzauw_zze.zzbxq != null) {
                    return false;
                }
            } else if (!this.zzbxq.equals(com_google_android_gms_internal_zzauw_zze.zzbxq)) {
                return false;
            }
            if (this.zzbb == null) {
                if (com_google_android_gms_internal_zzauw_zze.zzbb != null) {
                    return false;
                }
            } else if (!this.zzbb.equals(com_google_android_gms_internal_zzauw_zze.zzbb)) {
                return false;
            }
            if (this.zzbxr == null) {
                if (com_google_android_gms_internal_zzauw_zze.zzbxr != null) {
                    return false;
                }
            } else if (!this.zzbxr.equals(com_google_android_gms_internal_zzauw_zze.zzbxr)) {
                return false;
            }
            if (this.zzbxs == null) {
                if (com_google_android_gms_internal_zzauw_zze.zzbxs != null) {
                    return false;
                }
            } else if (!this.zzbxs.equals(com_google_android_gms_internal_zzauw_zze.zzbxs)) {
                return false;
            }
            if (this.zzbxt == null) {
                if (com_google_android_gms_internal_zzauw_zze.zzbxt != null) {
                    return false;
                }
            } else if (!this.zzbxt.equals(com_google_android_gms_internal_zzauw_zze.zzbxt)) {
                return false;
            }
            if (this.zzbqL == null) {
                if (com_google_android_gms_internal_zzauw_zze.zzbqL != null) {
                    return false;
                }
            } else if (!this.zzbqL.equals(com_google_android_gms_internal_zzauw_zze.zzbqL)) {
                return false;
            }
            if (this.zzaS == null) {
                if (com_google_android_gms_internal_zzauw_zze.zzaS != null) {
                    return false;
                }
            } else if (!this.zzaS.equals(com_google_android_gms_internal_zzauw_zze.zzaS)) {
                return false;
            }
            if (this.zzbhN == null) {
                if (com_google_android_gms_internal_zzauw_zze.zzbhN != null) {
                    return false;
                }
            } else if (!this.zzbhN.equals(com_google_android_gms_internal_zzauw_zze.zzbhN)) {
                return false;
            }
            if (this.zzbxu == null) {
                if (com_google_android_gms_internal_zzauw_zze.zzbxu != null) {
                    return false;
                }
            } else if (!this.zzbxu.equals(com_google_android_gms_internal_zzauw_zze.zzbxu)) {
                return false;
            }
            if (this.zzbxv == null) {
                if (com_google_android_gms_internal_zzauw_zze.zzbxv != null) {
                    return false;
                }
            } else if (!this.zzbxv.equals(com_google_android_gms_internal_zzauw_zze.zzbxv)) {
                return false;
            }
            if (this.zzbxw == null) {
                if (com_google_android_gms_internal_zzauw_zze.zzbxw != null) {
                    return false;
                }
            } else if (!this.zzbxw.equals(com_google_android_gms_internal_zzauw_zze.zzbxw)) {
                return false;
            }
            if (this.zzbxx == null) {
                if (com_google_android_gms_internal_zzauw_zze.zzbxx != null) {
                    return false;
                }
            } else if (!this.zzbxx.equals(com_google_android_gms_internal_zzauw_zze.zzbxx)) {
                return false;
            }
            if (this.zzbxy == null) {
                if (com_google_android_gms_internal_zzauw_zze.zzbxy != null) {
                    return false;
                }
            } else if (!this.zzbxy.equals(com_google_android_gms_internal_zzauw_zze.zzbxy)) {
                return false;
            }
            if (this.zzbxz == null) {
                if (com_google_android_gms_internal_zzauw_zze.zzbxz != null) {
                    return false;
                }
            } else if (!this.zzbxz.equals(com_google_android_gms_internal_zzauw_zze.zzbxz)) {
                return false;
            }
            if (this.zzbxA == null) {
                if (com_google_android_gms_internal_zzauw_zze.zzbxA != null) {
                    return false;
                }
            } else if (!this.zzbxA.equals(com_google_android_gms_internal_zzauw_zze.zzbxA)) {
                return false;
            }
            if (this.zzbqO == null) {
                if (com_google_android_gms_internal_zzauw_zze.zzbqO != null) {
                    return false;
                }
            } else if (!this.zzbqO.equals(com_google_android_gms_internal_zzauw_zze.zzbqO)) {
                return false;
            }
            if (this.zzbqK == null) {
                if (com_google_android_gms_internal_zzauw_zze.zzbqK != null) {
                    return false;
                }
            } else if (!this.zzbqK.equals(com_google_android_gms_internal_zzauw_zze.zzbqK)) {
                return false;
            }
            if (this.zzbxB == null) {
                if (com_google_android_gms_internal_zzauw_zze.zzbxB != null) {
                    return false;
                }
            } else if (!this.zzbxB.equals(com_google_android_gms_internal_zzauw_zze.zzbxB)) {
                return false;
            }
            if (!zzbyh.equals(this.zzbxC, com_google_android_gms_internal_zzauw_zze.zzbxC)) {
                return false;
            }
            if (this.zzbqS == null) {
                if (com_google_android_gms_internal_zzauw_zze.zzbqS != null) {
                    return false;
                }
            } else if (!this.zzbqS.equals(com_google_android_gms_internal_zzauw_zze.zzbqS)) {
                return false;
            }
            if (this.zzbxD == null) {
                if (com_google_android_gms_internal_zzauw_zze.zzbxD != null) {
                    return false;
                }
            } else if (!this.zzbxD.equals(com_google_android_gms_internal_zzauw_zze.zzbxD)) {
                return false;
            }
            if (this.zzbxE == null) {
                if (com_google_android_gms_internal_zzauw_zze.zzbxE != null) {
                    return false;
                }
            } else if (!this.zzbxE.equals(com_google_android_gms_internal_zzauw_zze.zzbxE)) {
                return false;
            }
            if (this.zzbxF == null) {
                if (com_google_android_gms_internal_zzauw_zze.zzbxF != null) {
                    return false;
                }
            } else if (!this.zzbxF.equals(com_google_android_gms_internal_zzauw_zze.zzbxF)) {
                return false;
            }
            if (this.zzbxG == null) {
                if (com_google_android_gms_internal_zzauw_zze.zzbxG != null) {
                    return false;
                }
            } else if (!this.zzbxG.equals(com_google_android_gms_internal_zzauw_zze.zzbxG)) {
                return false;
            }
            if (this.zzbxH == null) {
                if (com_google_android_gms_internal_zzauw_zze.zzbxH != null) {
                    return false;
                }
            } else if (!this.zzbxH.equals(com_google_android_gms_internal_zzauw_zze.zzbxH)) {
                return false;
            }
            if (this.zzbxI == null) {
                if (com_google_android_gms_internal_zzauw_zze.zzbxI != null) {
                    return false;
                }
            } else if (!this.zzbxI.equals(com_google_android_gms_internal_zzauw_zze.zzbxI)) {
                return false;
            }
            return (this.zzcwC == null || this.zzcwC.isEmpty()) ? com_google_android_gms_internal_zzauw_zze.zzcwC == null || com_google_android_gms_internal_zzauw_zze.zzcwC.isEmpty() : this.zzcwC.equals(com_google_android_gms_internal_zzauw_zze.zzcwC);
        }

        public int hashCode() {
            int i = 0;
            int hashCode = ((this.zzbxI == null ? 0 : this.zzbxI.hashCode()) + (((this.zzbxH == null ? 0 : this.zzbxH.hashCode()) + (((this.zzbxG == null ? 0 : this.zzbxG.hashCode()) + (((this.zzbxF == null ? 0 : this.zzbxF.hashCode()) + (((this.zzbxE == null ? 0 : this.zzbxE.hashCode()) + (((this.zzbxD == null ? 0 : this.zzbxD.hashCode()) + (((this.zzbqS == null ? 0 : this.zzbqS.hashCode()) + (((((this.zzbxB == null ? 0 : this.zzbxB.hashCode()) + (((this.zzbqK == null ? 0 : this.zzbqK.hashCode()) + (((this.zzbqO == null ? 0 : this.zzbqO.hashCode()) + (((this.zzbxA == null ? 0 : this.zzbxA.hashCode()) + (((this.zzbxz == null ? 0 : this.zzbxz.hashCode()) + (((this.zzbxy == null ? 0 : this.zzbxy.hashCode()) + (((this.zzbxx == null ? 0 : this.zzbxx.hashCode()) + (((this.zzbxw == null ? 0 : this.zzbxw.hashCode()) + (((this.zzbxv == null ? 0 : this.zzbxv.hashCode()) + (((this.zzbxu == null ? 0 : this.zzbxu.hashCode()) + (((this.zzbhN == null ? 0 : this.zzbhN.hashCode()) + (((this.zzaS == null ? 0 : this.zzaS.hashCode()) + (((this.zzbqL == null ? 0 : this.zzbqL.hashCode()) + (((this.zzbxt == null ? 0 : this.zzbxt.hashCode()) + (((this.zzbxs == null ? 0 : this.zzbxs.hashCode()) + (((this.zzbxr == null ? 0 : this.zzbxr.hashCode()) + (((this.zzbb == null ? 0 : this.zzbb.hashCode()) + (((this.zzbxq == null ? 0 : this.zzbxq.hashCode()) + (((this.zzbxp == null ? 0 : this.zzbxp.hashCode()) + (((this.zzbxo == null ? 0 : this.zzbxo.hashCode()) + (((this.zzbxn == null ? 0 : this.zzbxn.hashCode()) + (((this.zzbxm == null ? 0 : this.zzbxm.hashCode()) + (((this.zzbxl == null ? 0 : this.zzbxl.hashCode()) + (((((((this.zzbxi == null ? 0 : this.zzbxi.hashCode()) + ((getClass().getName().hashCode() + 527) * 31)) * 31) + zzbyh.hashCode(this.zzbxj)) * 31) + zzbyh.hashCode(this.zzbxk)) * 31)) * 31)) * 31)) * 31)) * 31)) * 31)) * 31)) * 31)) * 31)) * 31)) * 31)) * 31)) * 31)) * 31)) * 31)) * 31)) * 31)) * 31)) * 31)) * 31)) * 31)) * 31)) * 31)) * 31) + zzbyh.hashCode(this.zzbxC)) * 31)) * 31)) * 31)) * 31)) * 31)) * 31)) * 31)) * 31;
            if (!(this.zzcwC == null || this.zzcwC.isEmpty())) {
                i = this.zzcwC.hashCode();
            }
            return hashCode + i;
        }

        public zze zzNI() {
            this.zzbxi = null;
            this.zzbxj = zzb.zzNC();
            this.zzbxk = zzg.zzNK();
            this.zzbxl = null;
            this.zzbxm = null;
            this.zzbxn = null;
            this.zzbxo = null;
            this.zzbxp = null;
            this.zzbxq = null;
            this.zzbb = null;
            this.zzbxr = null;
            this.zzbxs = null;
            this.zzbxt = null;
            this.zzbqL = null;
            this.zzaS = null;
            this.zzbhN = null;
            this.zzbxu = null;
            this.zzbxv = null;
            this.zzbxw = null;
            this.zzbxx = null;
            this.zzbxy = null;
            this.zzbxz = null;
            this.zzbxA = null;
            this.zzbqO = null;
            this.zzbqK = null;
            this.zzbxB = null;
            this.zzbxC = zza.zzNA();
            this.zzbqS = null;
            this.zzbxD = null;
            this.zzbxE = null;
            this.zzbxF = null;
            this.zzbxG = null;
            this.zzbxH = null;
            this.zzbxI = null;
            this.zzcwC = null;
            this.zzcwL = -1;
            return this;
        }

        public zze zzT(zzbyb com_google_android_gms_internal_zzbyb) throws IOException {
            while (true) {
                int zzaeW = com_google_android_gms_internal_zzbyb.zzaeW();
                int zzb;
                Object obj;
                switch (zzaeW) {
                    case 0:
                        break;
                    case 8:
                        this.zzbxi = Integer.valueOf(com_google_android_gms_internal_zzbyb.zzafa());
                        continue;
                    case 18:
                        zzb = zzbym.zzb(com_google_android_gms_internal_zzbyb, 18);
                        zzaeW = this.zzbxj == null ? 0 : this.zzbxj.length;
                        obj = new zzb[(zzb + zzaeW)];
                        if (zzaeW != 0) {
                            System.arraycopy(this.zzbxj, 0, obj, 0, zzaeW);
                        }
                        while (zzaeW < obj.length - 1) {
                            obj[zzaeW] = new zzb();
                            com_google_android_gms_internal_zzbyb.zza(obj[zzaeW]);
                            com_google_android_gms_internal_zzbyb.zzaeW();
                            zzaeW++;
                        }
                        obj[zzaeW] = new zzb();
                        com_google_android_gms_internal_zzbyb.zza(obj[zzaeW]);
                        this.zzbxj = obj;
                        continue;
                    case NalUnitTypes.NAL_TYPE_RSV_VCL26 /*26*/:
                        zzb = zzbym.zzb(com_google_android_gms_internal_zzbyb, 26);
                        zzaeW = this.zzbxk == null ? 0 : this.zzbxk.length;
                        obj = new zzg[(zzb + zzaeW)];
                        if (zzaeW != 0) {
                            System.arraycopy(this.zzbxk, 0, obj, 0, zzaeW);
                        }
                        while (zzaeW < obj.length - 1) {
                            obj[zzaeW] = new zzg();
                            com_google_android_gms_internal_zzbyb.zza(obj[zzaeW]);
                            com_google_android_gms_internal_zzbyb.zzaeW();
                            zzaeW++;
                        }
                        obj[zzaeW] = new zzg();
                        com_google_android_gms_internal_zzbyb.zza(obj[zzaeW]);
                        this.zzbxk = obj;
                        continue;
                    case 32:
                        this.zzbxl = Long.valueOf(com_google_android_gms_internal_zzbyb.zzaeZ());
                        continue;
                    case MotionEventCompat.AXIS_GENERIC_9 /*40*/:
                        this.zzbxm = Long.valueOf(com_google_android_gms_internal_zzbyb.zzaeZ());
                        continue;
                    case 48:
                        this.zzbxn = Long.valueOf(com_google_android_gms_internal_zzbyb.zzaeZ());
                        continue;
                    case 56:
                        this.zzbxp = Long.valueOf(com_google_android_gms_internal_zzbyb.zzaeZ());
                        continue;
                    case 66:
                        this.zzbxq = com_google_android_gms_internal_zzbyb.readString();
                        continue;
                    case 74:
                        this.zzbb = com_google_android_gms_internal_zzbyb.readString();
                        continue;
                    case 82:
                        this.zzbxr = com_google_android_gms_internal_zzbyb.readString();
                        continue;
                    case 90:
                        this.zzbxs = com_google_android_gms_internal_zzbyb.readString();
                        continue;
                    case 96:
                        this.zzbxt = Integer.valueOf(com_google_android_gms_internal_zzbyb.zzafa());
                        continue;
                    case 106:
                        this.zzbqL = com_google_android_gms_internal_zzbyb.readString();
                        continue;
                    case 114:
                        this.zzaS = com_google_android_gms_internal_zzbyb.readString();
                        continue;
                    case TsExtractor.TS_STREAM_TYPE_HDMV_DTS /*130*/:
                        this.zzbhN = com_google_android_gms_internal_zzbyb.readString();
                        continue;
                    case 136:
                        this.zzbxu = Long.valueOf(com_google_android_gms_internal_zzbyb.zzaeZ());
                        continue;
                    case 144:
                        this.zzbxv = Long.valueOf(com_google_android_gms_internal_zzbyb.zzaeZ());
                        continue;
                    case 154:
                        this.zzbxw = com_google_android_gms_internal_zzbyb.readString();
                        continue;
                    case 160:
                        this.zzbxx = Boolean.valueOf(com_google_android_gms_internal_zzbyb.zzafc());
                        continue;
                    case 170:
                        this.zzbxy = com_google_android_gms_internal_zzbyb.readString();
                        continue;
                    case 176:
                        this.zzbxz = Long.valueOf(com_google_android_gms_internal_zzbyb.zzaeZ());
                        continue;
                    case 184:
                        this.zzbxA = Integer.valueOf(com_google_android_gms_internal_zzbyb.zzafa());
                        continue;
                    case 194:
                        this.zzbqO = com_google_android_gms_internal_zzbyb.readString();
                        continue;
                    case 202:
                        this.zzbqK = com_google_android_gms_internal_zzbyb.readString();
                        continue;
                    case 208:
                        this.zzbxo = Long.valueOf(com_google_android_gms_internal_zzbyb.zzaeZ());
                        continue;
                    case 224:
                        this.zzbxB = Boolean.valueOf(com_google_android_gms_internal_zzbyb.zzafc());
                        continue;
                    case 234:
                        zzb = zzbym.zzb(com_google_android_gms_internal_zzbyb, 234);
                        zzaeW = this.zzbxC == null ? 0 : this.zzbxC.length;
                        obj = new zza[(zzb + zzaeW)];
                        if (zzaeW != 0) {
                            System.arraycopy(this.zzbxC, 0, obj, 0, zzaeW);
                        }
                        while (zzaeW < obj.length - 1) {
                            obj[zzaeW] = new zza();
                            com_google_android_gms_internal_zzbyb.zza(obj[zzaeW]);
                            com_google_android_gms_internal_zzbyb.zzaeW();
                            zzaeW++;
                        }
                        obj[zzaeW] = new zza();
                        com_google_android_gms_internal_zzbyb.zza(obj[zzaeW]);
                        this.zzbxC = obj;
                        continue;
                    case 242:
                        this.zzbqS = com_google_android_gms_internal_zzbyb.readString();
                        continue;
                    case 248:
                        this.zzbxD = Integer.valueOf(com_google_android_gms_internal_zzbyb.zzafa());
                        continue;
                    case 256:
                        this.zzbxE = Integer.valueOf(com_google_android_gms_internal_zzbyb.zzafa());
                        continue;
                    case 264:
                        this.zzbxF = Integer.valueOf(com_google_android_gms_internal_zzbyb.zzafa());
                        continue;
                    case 274:
                        this.zzbxG = com_google_android_gms_internal_zzbyb.readString();
                        continue;
                    case 280:
                        this.zzbxH = Long.valueOf(com_google_android_gms_internal_zzbyb.zzaeZ());
                        continue;
                    case 288:
                        this.zzbxI = Long.valueOf(com_google_android_gms_internal_zzbyb.zzaeZ());
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

        public void zza(zzbyc com_google_android_gms_internal_zzbyc) throws IOException {
            int i = 0;
            if (this.zzbxi != null) {
                com_google_android_gms_internal_zzbyc.zzJ(1, this.zzbxi.intValue());
            }
            if (this.zzbxj != null && this.zzbxj.length > 0) {
                for (zzbyj com_google_android_gms_internal_zzbyj : this.zzbxj) {
                    if (com_google_android_gms_internal_zzbyj != null) {
                        com_google_android_gms_internal_zzbyc.zza(2, com_google_android_gms_internal_zzbyj);
                    }
                }
            }
            if (this.zzbxk != null && this.zzbxk.length > 0) {
                for (zzbyj com_google_android_gms_internal_zzbyj2 : this.zzbxk) {
                    if (com_google_android_gms_internal_zzbyj2 != null) {
                        com_google_android_gms_internal_zzbyc.zza(3, com_google_android_gms_internal_zzbyj2);
                    }
                }
            }
            if (this.zzbxl != null) {
                com_google_android_gms_internal_zzbyc.zzb(4, this.zzbxl.longValue());
            }
            if (this.zzbxm != null) {
                com_google_android_gms_internal_zzbyc.zzb(5, this.zzbxm.longValue());
            }
            if (this.zzbxn != null) {
                com_google_android_gms_internal_zzbyc.zzb(6, this.zzbxn.longValue());
            }
            if (this.zzbxp != null) {
                com_google_android_gms_internal_zzbyc.zzb(7, this.zzbxp.longValue());
            }
            if (this.zzbxq != null) {
                com_google_android_gms_internal_zzbyc.zzq(8, this.zzbxq);
            }
            if (this.zzbb != null) {
                com_google_android_gms_internal_zzbyc.zzq(9, this.zzbb);
            }
            if (this.zzbxr != null) {
                com_google_android_gms_internal_zzbyc.zzq(10, this.zzbxr);
            }
            if (this.zzbxs != null) {
                com_google_android_gms_internal_zzbyc.zzq(11, this.zzbxs);
            }
            if (this.zzbxt != null) {
                com_google_android_gms_internal_zzbyc.zzJ(12, this.zzbxt.intValue());
            }
            if (this.zzbqL != null) {
                com_google_android_gms_internal_zzbyc.zzq(13, this.zzbqL);
            }
            if (this.zzaS != null) {
                com_google_android_gms_internal_zzbyc.zzq(14, this.zzaS);
            }
            if (this.zzbhN != null) {
                com_google_android_gms_internal_zzbyc.zzq(16, this.zzbhN);
            }
            if (this.zzbxu != null) {
                com_google_android_gms_internal_zzbyc.zzb(17, this.zzbxu.longValue());
            }
            if (this.zzbxv != null) {
                com_google_android_gms_internal_zzbyc.zzb(18, this.zzbxv.longValue());
            }
            if (this.zzbxw != null) {
                com_google_android_gms_internal_zzbyc.zzq(19, this.zzbxw);
            }
            if (this.zzbxx != null) {
                com_google_android_gms_internal_zzbyc.zzg(20, this.zzbxx.booleanValue());
            }
            if (this.zzbxy != null) {
                com_google_android_gms_internal_zzbyc.zzq(21, this.zzbxy);
            }
            if (this.zzbxz != null) {
                com_google_android_gms_internal_zzbyc.zzb(22, this.zzbxz.longValue());
            }
            if (this.zzbxA != null) {
                com_google_android_gms_internal_zzbyc.zzJ(23, this.zzbxA.intValue());
            }
            if (this.zzbqO != null) {
                com_google_android_gms_internal_zzbyc.zzq(24, this.zzbqO);
            }
            if (this.zzbqK != null) {
                com_google_android_gms_internal_zzbyc.zzq(25, this.zzbqK);
            }
            if (this.zzbxo != null) {
                com_google_android_gms_internal_zzbyc.zzb(26, this.zzbxo.longValue());
            }
            if (this.zzbxB != null) {
                com_google_android_gms_internal_zzbyc.zzg(28, this.zzbxB.booleanValue());
            }
            if (this.zzbxC != null && this.zzbxC.length > 0) {
                while (i < this.zzbxC.length) {
                    zzbyj com_google_android_gms_internal_zzbyj3 = this.zzbxC[i];
                    if (com_google_android_gms_internal_zzbyj3 != null) {
                        com_google_android_gms_internal_zzbyc.zza(29, com_google_android_gms_internal_zzbyj3);
                    }
                    i++;
                }
            }
            if (this.zzbqS != null) {
                com_google_android_gms_internal_zzbyc.zzq(30, this.zzbqS);
            }
            if (this.zzbxD != null) {
                com_google_android_gms_internal_zzbyc.zzJ(31, this.zzbxD.intValue());
            }
            if (this.zzbxE != null) {
                com_google_android_gms_internal_zzbyc.zzJ(32, this.zzbxE.intValue());
            }
            if (this.zzbxF != null) {
                com_google_android_gms_internal_zzbyc.zzJ(33, this.zzbxF.intValue());
            }
            if (this.zzbxG != null) {
                com_google_android_gms_internal_zzbyc.zzq(34, this.zzbxG);
            }
            if (this.zzbxH != null) {
                com_google_android_gms_internal_zzbyc.zzb(35, this.zzbxH.longValue());
            }
            if (this.zzbxI != null) {
                com_google_android_gms_internal_zzbyc.zzb(36, this.zzbxI.longValue());
            }
            super.zza(com_google_android_gms_internal_zzbyc);
        }

        public /* synthetic */ zzbyj zzb(zzbyb com_google_android_gms_internal_zzbyb) throws IOException {
            return zzT(com_google_android_gms_internal_zzbyb);
        }

        protected int zzu() {
            int i;
            int i2 = 0;
            int zzu = super.zzu();
            if (this.zzbxi != null) {
                zzu += zzbyc.zzL(1, this.zzbxi.intValue());
            }
            if (this.zzbxj != null && this.zzbxj.length > 0) {
                i = zzu;
                for (zzbyj com_google_android_gms_internal_zzbyj : this.zzbxj) {
                    if (com_google_android_gms_internal_zzbyj != null) {
                        i += zzbyc.zzc(2, com_google_android_gms_internal_zzbyj);
                    }
                }
                zzu = i;
            }
            if (this.zzbxk != null && this.zzbxk.length > 0) {
                i = zzu;
                for (zzbyj com_google_android_gms_internal_zzbyj2 : this.zzbxk) {
                    if (com_google_android_gms_internal_zzbyj2 != null) {
                        i += zzbyc.zzc(3, com_google_android_gms_internal_zzbyj2);
                    }
                }
                zzu = i;
            }
            if (this.zzbxl != null) {
                zzu += zzbyc.zzf(4, this.zzbxl.longValue());
            }
            if (this.zzbxm != null) {
                zzu += zzbyc.zzf(5, this.zzbxm.longValue());
            }
            if (this.zzbxn != null) {
                zzu += zzbyc.zzf(6, this.zzbxn.longValue());
            }
            if (this.zzbxp != null) {
                zzu += zzbyc.zzf(7, this.zzbxp.longValue());
            }
            if (this.zzbxq != null) {
                zzu += zzbyc.zzr(8, this.zzbxq);
            }
            if (this.zzbb != null) {
                zzu += zzbyc.zzr(9, this.zzbb);
            }
            if (this.zzbxr != null) {
                zzu += zzbyc.zzr(10, this.zzbxr);
            }
            if (this.zzbxs != null) {
                zzu += zzbyc.zzr(11, this.zzbxs);
            }
            if (this.zzbxt != null) {
                zzu += zzbyc.zzL(12, this.zzbxt.intValue());
            }
            if (this.zzbqL != null) {
                zzu += zzbyc.zzr(13, this.zzbqL);
            }
            if (this.zzaS != null) {
                zzu += zzbyc.zzr(14, this.zzaS);
            }
            if (this.zzbhN != null) {
                zzu += zzbyc.zzr(16, this.zzbhN);
            }
            if (this.zzbxu != null) {
                zzu += zzbyc.zzf(17, this.zzbxu.longValue());
            }
            if (this.zzbxv != null) {
                zzu += zzbyc.zzf(18, this.zzbxv.longValue());
            }
            if (this.zzbxw != null) {
                zzu += zzbyc.zzr(19, this.zzbxw);
            }
            if (this.zzbxx != null) {
                zzu += zzbyc.zzh(20, this.zzbxx.booleanValue());
            }
            if (this.zzbxy != null) {
                zzu += zzbyc.zzr(21, this.zzbxy);
            }
            if (this.zzbxz != null) {
                zzu += zzbyc.zzf(22, this.zzbxz.longValue());
            }
            if (this.zzbxA != null) {
                zzu += zzbyc.zzL(23, this.zzbxA.intValue());
            }
            if (this.zzbqO != null) {
                zzu += zzbyc.zzr(24, this.zzbqO);
            }
            if (this.zzbqK != null) {
                zzu += zzbyc.zzr(25, this.zzbqK);
            }
            if (this.zzbxo != null) {
                zzu += zzbyc.zzf(26, this.zzbxo.longValue());
            }
            if (this.zzbxB != null) {
                zzu += zzbyc.zzh(28, this.zzbxB.booleanValue());
            }
            if (this.zzbxC != null && this.zzbxC.length > 0) {
                while (i2 < this.zzbxC.length) {
                    zzbyj com_google_android_gms_internal_zzbyj3 = this.zzbxC[i2];
                    if (com_google_android_gms_internal_zzbyj3 != null) {
                        zzu += zzbyc.zzc(29, com_google_android_gms_internal_zzbyj3);
                    }
                    i2++;
                }
            }
            if (this.zzbqS != null) {
                zzu += zzbyc.zzr(30, this.zzbqS);
            }
            if (this.zzbxD != null) {
                zzu += zzbyc.zzL(31, this.zzbxD.intValue());
            }
            if (this.zzbxE != null) {
                zzu += zzbyc.zzL(32, this.zzbxE.intValue());
            }
            if (this.zzbxF != null) {
                zzu += zzbyc.zzL(33, this.zzbxF.intValue());
            }
            if (this.zzbxG != null) {
                zzu += zzbyc.zzr(34, this.zzbxG);
            }
            if (this.zzbxH != null) {
                zzu += zzbyc.zzf(35, this.zzbxH.longValue());
            }
            return this.zzbxI != null ? zzu + zzbyc.zzf(36, this.zzbxI.longValue()) : zzu;
        }
    }

    public static final class zzf extends zzbyd<zzf> {
        public long[] zzbxJ;
        public long[] zzbxK;

        public zzf() {
            zzNJ();
        }

        public boolean equals(Object obj) {
            if (obj == this) {
                return true;
            }
            if (!(obj instanceof zzf)) {
                return false;
            }
            zzf com_google_android_gms_internal_zzauw_zzf = (zzf) obj;
            return (zzbyh.equals(this.zzbxJ, com_google_android_gms_internal_zzauw_zzf.zzbxJ) && zzbyh.equals(this.zzbxK, com_google_android_gms_internal_zzauw_zzf.zzbxK)) ? (this.zzcwC == null || this.zzcwC.isEmpty()) ? com_google_android_gms_internal_zzauw_zzf.zzcwC == null || com_google_android_gms_internal_zzauw_zzf.zzcwC.isEmpty() : this.zzcwC.equals(com_google_android_gms_internal_zzauw_zzf.zzcwC) : false;
        }

        public int hashCode() {
            int hashCode = (((((getClass().getName().hashCode() + 527) * 31) + zzbyh.hashCode(this.zzbxJ)) * 31) + zzbyh.hashCode(this.zzbxK)) * 31;
            int hashCode2 = (this.zzcwC == null || this.zzcwC.isEmpty()) ? 0 : this.zzcwC.hashCode();
            return hashCode2 + hashCode;
        }

        public zzf zzNJ() {
            this.zzbxJ = zzbym.zzcwR;
            this.zzbxK = zzbym.zzcwR;
            this.zzcwC = null;
            this.zzcwL = -1;
            return this;
        }

        public zzf zzU(zzbyb com_google_android_gms_internal_zzbyb) throws IOException {
            while (true) {
                int zzaeW = com_google_android_gms_internal_zzbyb.zzaeW();
                int zzb;
                Object obj;
                int zzrf;
                Object obj2;
                switch (zzaeW) {
                    case 0:
                        break;
                    case 8:
                        zzb = zzbym.zzb(com_google_android_gms_internal_zzbyb, 8);
                        zzaeW = this.zzbxJ == null ? 0 : this.zzbxJ.length;
                        obj = new long[(zzb + zzaeW)];
                        if (zzaeW != 0) {
                            System.arraycopy(this.zzbxJ, 0, obj, 0, zzaeW);
                        }
                        while (zzaeW < obj.length - 1) {
                            obj[zzaeW] = com_google_android_gms_internal_zzbyb.zzaeY();
                            com_google_android_gms_internal_zzbyb.zzaeW();
                            zzaeW++;
                        }
                        obj[zzaeW] = com_google_android_gms_internal_zzbyb.zzaeY();
                        this.zzbxJ = obj;
                        continue;
                    case 10:
                        zzrf = com_google_android_gms_internal_zzbyb.zzrf(com_google_android_gms_internal_zzbyb.zzaff());
                        zzb = com_google_android_gms_internal_zzbyb.getPosition();
                        zzaeW = 0;
                        while (com_google_android_gms_internal_zzbyb.zzafk() > 0) {
                            com_google_android_gms_internal_zzbyb.zzaeY();
                            zzaeW++;
                        }
                        com_google_android_gms_internal_zzbyb.zzrh(zzb);
                        zzb = this.zzbxJ == null ? 0 : this.zzbxJ.length;
                        obj2 = new long[(zzaeW + zzb)];
                        if (zzb != 0) {
                            System.arraycopy(this.zzbxJ, 0, obj2, 0, zzb);
                        }
                        while (zzb < obj2.length) {
                            obj2[zzb] = com_google_android_gms_internal_zzbyb.zzaeY();
                            zzb++;
                        }
                        this.zzbxJ = obj2;
                        com_google_android_gms_internal_zzbyb.zzrg(zzrf);
                        continue;
                    case 16:
                        zzb = zzbym.zzb(com_google_android_gms_internal_zzbyb, 16);
                        zzaeW = this.zzbxK == null ? 0 : this.zzbxK.length;
                        obj = new long[(zzb + zzaeW)];
                        if (zzaeW != 0) {
                            System.arraycopy(this.zzbxK, 0, obj, 0, zzaeW);
                        }
                        while (zzaeW < obj.length - 1) {
                            obj[zzaeW] = com_google_android_gms_internal_zzbyb.zzaeY();
                            com_google_android_gms_internal_zzbyb.zzaeW();
                            zzaeW++;
                        }
                        obj[zzaeW] = com_google_android_gms_internal_zzbyb.zzaeY();
                        this.zzbxK = obj;
                        continue;
                    case 18:
                        zzrf = com_google_android_gms_internal_zzbyb.zzrf(com_google_android_gms_internal_zzbyb.zzaff());
                        zzb = com_google_android_gms_internal_zzbyb.getPosition();
                        zzaeW = 0;
                        while (com_google_android_gms_internal_zzbyb.zzafk() > 0) {
                            com_google_android_gms_internal_zzbyb.zzaeY();
                            zzaeW++;
                        }
                        com_google_android_gms_internal_zzbyb.zzrh(zzb);
                        zzb = this.zzbxK == null ? 0 : this.zzbxK.length;
                        obj2 = new long[(zzaeW + zzb)];
                        if (zzb != 0) {
                            System.arraycopy(this.zzbxK, 0, obj2, 0, zzb);
                        }
                        while (zzb < obj2.length) {
                            obj2[zzb] = com_google_android_gms_internal_zzbyb.zzaeY();
                            zzb++;
                        }
                        this.zzbxK = obj2;
                        com_google_android_gms_internal_zzbyb.zzrg(zzrf);
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

        public void zza(zzbyc com_google_android_gms_internal_zzbyc) throws IOException {
            int i = 0;
            if (this.zzbxJ != null && this.zzbxJ.length > 0) {
                for (long zza : this.zzbxJ) {
                    com_google_android_gms_internal_zzbyc.zza(1, zza);
                }
            }
            if (this.zzbxK != null && this.zzbxK.length > 0) {
                while (i < this.zzbxK.length) {
                    com_google_android_gms_internal_zzbyc.zza(2, this.zzbxK[i]);
                    i++;
                }
            }
            super.zza(com_google_android_gms_internal_zzbyc);
        }

        public /* synthetic */ zzbyj zzb(zzbyb com_google_android_gms_internal_zzbyb) throws IOException {
            return zzU(com_google_android_gms_internal_zzbyb);
        }

        protected int zzu() {
            int i;
            int i2;
            int i3 = 0;
            int zzu = super.zzu();
            if (this.zzbxJ == null || this.zzbxJ.length <= 0) {
                i = zzu;
            } else {
                i2 = 0;
                for (long zzbp : this.zzbxJ) {
                    i2 += zzbyc.zzbp(zzbp);
                }
                i = (zzu + i2) + (this.zzbxJ.length * 1);
            }
            if (this.zzbxK == null || this.zzbxK.length <= 0) {
                return i;
            }
            i2 = 0;
            while (i3 < this.zzbxK.length) {
                i2 += zzbyc.zzbp(this.zzbxK[i3]);
                i3++;
            }
            return (i + i2) + (this.zzbxK.length * 1);
        }
    }

    public static final class zzg extends zzbyd<zzg> {
        private static volatile zzg[] zzbxL;
        public String name;
        public String zzaGV;
        public Float zzbwh;
        public Double zzbwi;
        public Long zzbxM;
        public Long zzbxf;

        public zzg() {
            zzNL();
        }

        public static zzg[] zzNK() {
            if (zzbxL == null) {
                synchronized (zzbyh.zzcwK) {
                    if (zzbxL == null) {
                        zzbxL = new zzg[0];
                    }
                }
            }
            return zzbxL;
        }

        public boolean equals(Object obj) {
            if (obj == this) {
                return true;
            }
            if (!(obj instanceof zzg)) {
                return false;
            }
            zzg com_google_android_gms_internal_zzauw_zzg = (zzg) obj;
            if (this.zzbxM == null) {
                if (com_google_android_gms_internal_zzauw_zzg.zzbxM != null) {
                    return false;
                }
            } else if (!this.zzbxM.equals(com_google_android_gms_internal_zzauw_zzg.zzbxM)) {
                return false;
            }
            if (this.name == null) {
                if (com_google_android_gms_internal_zzauw_zzg.name != null) {
                    return false;
                }
            } else if (!this.name.equals(com_google_android_gms_internal_zzauw_zzg.name)) {
                return false;
            }
            if (this.zzaGV == null) {
                if (com_google_android_gms_internal_zzauw_zzg.zzaGV != null) {
                    return false;
                }
            } else if (!this.zzaGV.equals(com_google_android_gms_internal_zzauw_zzg.zzaGV)) {
                return false;
            }
            if (this.zzbxf == null) {
                if (com_google_android_gms_internal_zzauw_zzg.zzbxf != null) {
                    return false;
                }
            } else if (!this.zzbxf.equals(com_google_android_gms_internal_zzauw_zzg.zzbxf)) {
                return false;
            }
            if (this.zzbwh == null) {
                if (com_google_android_gms_internal_zzauw_zzg.zzbwh != null) {
                    return false;
                }
            } else if (!this.zzbwh.equals(com_google_android_gms_internal_zzauw_zzg.zzbwh)) {
                return false;
            }
            if (this.zzbwi == null) {
                if (com_google_android_gms_internal_zzauw_zzg.zzbwi != null) {
                    return false;
                }
            } else if (!this.zzbwi.equals(com_google_android_gms_internal_zzauw_zzg.zzbwi)) {
                return false;
            }
            return (this.zzcwC == null || this.zzcwC.isEmpty()) ? com_google_android_gms_internal_zzauw_zzg.zzcwC == null || com_google_android_gms_internal_zzauw_zzg.zzcwC.isEmpty() : this.zzcwC.equals(com_google_android_gms_internal_zzauw_zzg.zzcwC);
        }

        public int hashCode() {
            int i = 0;
            int hashCode = ((this.zzbwi == null ? 0 : this.zzbwi.hashCode()) + (((this.zzbwh == null ? 0 : this.zzbwh.hashCode()) + (((this.zzbxf == null ? 0 : this.zzbxf.hashCode()) + (((this.zzaGV == null ? 0 : this.zzaGV.hashCode()) + (((this.name == null ? 0 : this.name.hashCode()) + (((this.zzbxM == null ? 0 : this.zzbxM.hashCode()) + ((getClass().getName().hashCode() + 527) * 31)) * 31)) * 31)) * 31)) * 31)) * 31)) * 31;
            if (!(this.zzcwC == null || this.zzcwC.isEmpty())) {
                i = this.zzcwC.hashCode();
            }
            return hashCode + i;
        }

        public zzg zzNL() {
            this.zzbxM = null;
            this.name = null;
            this.zzaGV = null;
            this.zzbxf = null;
            this.zzbwh = null;
            this.zzbwi = null;
            this.zzcwC = null;
            this.zzcwL = -1;
            return this;
        }

        public zzg zzV(zzbyb com_google_android_gms_internal_zzbyb) throws IOException {
            while (true) {
                int zzaeW = com_google_android_gms_internal_zzbyb.zzaeW();
                switch (zzaeW) {
                    case 0:
                        break;
                    case 8:
                        this.zzbxM = Long.valueOf(com_google_android_gms_internal_zzbyb.zzaeZ());
                        continue;
                    case 18:
                        this.name = com_google_android_gms_internal_zzbyb.readString();
                        continue;
                    case NalUnitTypes.NAL_TYPE_RSV_VCL26 /*26*/:
                        this.zzaGV = com_google_android_gms_internal_zzbyb.readString();
                        continue;
                    case 32:
                        this.zzbxf = Long.valueOf(com_google_android_gms_internal_zzbyb.zzaeZ());
                        continue;
                    case MotionEventCompat.AXIS_GENERIC_14 /*45*/:
                        this.zzbwh = Float.valueOf(com_google_android_gms_internal_zzbyb.readFloat());
                        continue;
                    case 49:
                        this.zzbwi = Double.valueOf(com_google_android_gms_internal_zzbyb.readDouble());
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

        public void zza(zzbyc com_google_android_gms_internal_zzbyc) throws IOException {
            if (this.zzbxM != null) {
                com_google_android_gms_internal_zzbyc.zzb(1, this.zzbxM.longValue());
            }
            if (this.name != null) {
                com_google_android_gms_internal_zzbyc.zzq(2, this.name);
            }
            if (this.zzaGV != null) {
                com_google_android_gms_internal_zzbyc.zzq(3, this.zzaGV);
            }
            if (this.zzbxf != null) {
                com_google_android_gms_internal_zzbyc.zzb(4, this.zzbxf.longValue());
            }
            if (this.zzbwh != null) {
                com_google_android_gms_internal_zzbyc.zzc(5, this.zzbwh.floatValue());
            }
            if (this.zzbwi != null) {
                com_google_android_gms_internal_zzbyc.zza(6, this.zzbwi.doubleValue());
            }
            super.zza(com_google_android_gms_internal_zzbyc);
        }

        public /* synthetic */ zzbyj zzb(zzbyb com_google_android_gms_internal_zzbyb) throws IOException {
            return zzV(com_google_android_gms_internal_zzbyb);
        }

        protected int zzu() {
            int zzu = super.zzu();
            if (this.zzbxM != null) {
                zzu += zzbyc.zzf(1, this.zzbxM.longValue());
            }
            if (this.name != null) {
                zzu += zzbyc.zzr(2, this.name);
            }
            if (this.zzaGV != null) {
                zzu += zzbyc.zzr(3, this.zzaGV);
            }
            if (this.zzbxf != null) {
                zzu += zzbyc.zzf(4, this.zzbxf.longValue());
            }
            if (this.zzbwh != null) {
                zzu += zzbyc.zzd(5, this.zzbwh.floatValue());
            }
            return this.zzbwi != null ? zzu + zzbyc.zzb(6, this.zzbwi.doubleValue()) : zzu;
        }
    }
}
