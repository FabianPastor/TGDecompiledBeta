package com.google.android.gms.internal;

import android.support.v4.view.MotionEventCompat;
import com.googlecode.mp4parser.authoring.tracks.h265.NalUnitTypes;
import java.io.IOException;
import org.telegram.messenger.exoplayer2.extractor.ts.TsExtractor;

public interface zzauw {

    public static final class zza extends zzbxn<zza> {
        private static volatile zza[] zzbwX;
        public zzf zzbwY;
        public zzf zzbwZ;
        public Integer zzbwo;
        public Boolean zzbxa;

        public zza() {
            zzNy();
        }

        public static zza[] zzNx() {
            if (zzbwX == null) {
                synchronized (zzbxr.zzcuI) {
                    if (zzbwX == null) {
                        zzbwX = new zza[0];
                    }
                }
            }
            return zzbwX;
        }

        public boolean equals(Object obj) {
            if (obj == this) {
                return true;
            }
            if (!(obj instanceof zza)) {
                return false;
            }
            zza com_google_android_gms_internal_zzauw_zza = (zza) obj;
            if (this.zzbwo == null) {
                if (com_google_android_gms_internal_zzauw_zza.zzbwo != null) {
                    return false;
                }
            } else if (!this.zzbwo.equals(com_google_android_gms_internal_zzauw_zza.zzbwo)) {
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
            if (this.zzbxa == null) {
                if (com_google_android_gms_internal_zzauw_zza.zzbxa != null) {
                    return false;
                }
            } else if (!this.zzbxa.equals(com_google_android_gms_internal_zzauw_zza.zzbxa)) {
                return false;
            }
            return (this.zzcuA == null || this.zzcuA.isEmpty()) ? com_google_android_gms_internal_zzauw_zza.zzcuA == null || com_google_android_gms_internal_zzauw_zza.zzcuA.isEmpty() : this.zzcuA.equals(com_google_android_gms_internal_zzauw_zza.zzcuA);
        }

        public int hashCode() {
            int i = 0;
            int hashCode = ((this.zzbxa == null ? 0 : this.zzbxa.hashCode()) + (((this.zzbwZ == null ? 0 : this.zzbwZ.hashCode()) + (((this.zzbwY == null ? 0 : this.zzbwY.hashCode()) + (((this.zzbwo == null ? 0 : this.zzbwo.hashCode()) + ((getClass().getName().hashCode() + 527) * 31)) * 31)) * 31)) * 31)) * 31;
            if (!(this.zzcuA == null || this.zzcuA.isEmpty())) {
                i = this.zzcuA.hashCode();
            }
            return hashCode + i;
        }

        public zza zzNy() {
            this.zzbwo = null;
            this.zzbwY = null;
            this.zzbwZ = null;
            this.zzbxa = null;
            this.zzcuA = null;
            this.zzcuJ = -1;
            return this;
        }

        public zza zzP(zzbxl com_google_android_gms_internal_zzbxl) throws IOException {
            while (true) {
                int zzaen = com_google_android_gms_internal_zzbxl.zzaen();
                switch (zzaen) {
                    case 0:
                        break;
                    case 8:
                        this.zzbwo = Integer.valueOf(com_google_android_gms_internal_zzbxl.zzaer());
                        continue;
                    case 18:
                        if (this.zzbwY == null) {
                            this.zzbwY = new zzf();
                        }
                        com_google_android_gms_internal_zzbxl.zza(this.zzbwY);
                        continue;
                    case NalUnitTypes.NAL_TYPE_RSV_VCL26 /*26*/:
                        if (this.zzbwZ == null) {
                            this.zzbwZ = new zzf();
                        }
                        com_google_android_gms_internal_zzbxl.zza(this.zzbwZ);
                        continue;
                    case 32:
                        this.zzbxa = Boolean.valueOf(com_google_android_gms_internal_zzbxl.zzaet());
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

        public void zza(zzbxm com_google_android_gms_internal_zzbxm) throws IOException {
            if (this.zzbwo != null) {
                com_google_android_gms_internal_zzbxm.zzJ(1, this.zzbwo.intValue());
            }
            if (this.zzbwY != null) {
                com_google_android_gms_internal_zzbxm.zza(2, this.zzbwY);
            }
            if (this.zzbwZ != null) {
                com_google_android_gms_internal_zzbxm.zza(3, this.zzbwZ);
            }
            if (this.zzbxa != null) {
                com_google_android_gms_internal_zzbxm.zzg(4, this.zzbxa.booleanValue());
            }
            super.zza(com_google_android_gms_internal_zzbxm);
        }

        public /* synthetic */ zzbxt zzb(zzbxl com_google_android_gms_internal_zzbxl) throws IOException {
            return zzP(com_google_android_gms_internal_zzbxl);
        }

        protected int zzu() {
            int zzu = super.zzu();
            if (this.zzbwo != null) {
                zzu += zzbxm.zzL(1, this.zzbwo.intValue());
            }
            if (this.zzbwY != null) {
                zzu += zzbxm.zzc(2, this.zzbwY);
            }
            if (this.zzbwZ != null) {
                zzu += zzbxm.zzc(3, this.zzbwZ);
            }
            return this.zzbxa != null ? zzu + zzbxm.zzh(4, this.zzbxa.booleanValue()) : zzu;
        }
    }

    public static final class zzb extends zzbxn<zzb> {
        private static volatile zzb[] zzbxb;
        public Integer count;
        public String name;
        public zzc[] zzbxc;
        public Long zzbxd;
        public Long zzbxe;

        public zzb() {
            zzNA();
        }

        public static zzb[] zzNz() {
            if (zzbxb == null) {
                synchronized (zzbxr.zzcuI) {
                    if (zzbxb == null) {
                        zzbxb = new zzb[0];
                    }
                }
            }
            return zzbxb;
        }

        public boolean equals(Object obj) {
            if (obj == this) {
                return true;
            }
            if (!(obj instanceof zzb)) {
                return false;
            }
            zzb com_google_android_gms_internal_zzauw_zzb = (zzb) obj;
            if (!zzbxr.equals(this.zzbxc, com_google_android_gms_internal_zzauw_zzb.zzbxc)) {
                return false;
            }
            if (this.name == null) {
                if (com_google_android_gms_internal_zzauw_zzb.name != null) {
                    return false;
                }
            } else if (!this.name.equals(com_google_android_gms_internal_zzauw_zzb.name)) {
                return false;
            }
            if (this.zzbxd == null) {
                if (com_google_android_gms_internal_zzauw_zzb.zzbxd != null) {
                    return false;
                }
            } else if (!this.zzbxd.equals(com_google_android_gms_internal_zzauw_zzb.zzbxd)) {
                return false;
            }
            if (this.zzbxe == null) {
                if (com_google_android_gms_internal_zzauw_zzb.zzbxe != null) {
                    return false;
                }
            } else if (!this.zzbxe.equals(com_google_android_gms_internal_zzauw_zzb.zzbxe)) {
                return false;
            }
            if (this.count == null) {
                if (com_google_android_gms_internal_zzauw_zzb.count != null) {
                    return false;
                }
            } else if (!this.count.equals(com_google_android_gms_internal_zzauw_zzb.count)) {
                return false;
            }
            return (this.zzcuA == null || this.zzcuA.isEmpty()) ? com_google_android_gms_internal_zzauw_zzb.zzcuA == null || com_google_android_gms_internal_zzauw_zzb.zzcuA.isEmpty() : this.zzcuA.equals(com_google_android_gms_internal_zzauw_zzb.zzcuA);
        }

        public int hashCode() {
            int i = 0;
            int hashCode = ((this.count == null ? 0 : this.count.hashCode()) + (((this.zzbxe == null ? 0 : this.zzbxe.hashCode()) + (((this.zzbxd == null ? 0 : this.zzbxd.hashCode()) + (((this.name == null ? 0 : this.name.hashCode()) + ((((getClass().getName().hashCode() + 527) * 31) + zzbxr.hashCode(this.zzbxc)) * 31)) * 31)) * 31)) * 31)) * 31;
            if (!(this.zzcuA == null || this.zzcuA.isEmpty())) {
                i = this.zzcuA.hashCode();
            }
            return hashCode + i;
        }

        public zzb zzNA() {
            this.zzbxc = zzc.zzNB();
            this.name = null;
            this.zzbxd = null;
            this.zzbxe = null;
            this.count = null;
            this.zzcuA = null;
            this.zzcuJ = -1;
            return this;
        }

        public zzb zzQ(zzbxl com_google_android_gms_internal_zzbxl) throws IOException {
            while (true) {
                int zzaen = com_google_android_gms_internal_zzbxl.zzaen();
                switch (zzaen) {
                    case 0:
                        break;
                    case 10:
                        int zzb = zzbxw.zzb(com_google_android_gms_internal_zzbxl, 10);
                        zzaen = this.zzbxc == null ? 0 : this.zzbxc.length;
                        Object obj = new zzc[(zzb + zzaen)];
                        if (zzaen != 0) {
                            System.arraycopy(this.zzbxc, 0, obj, 0, zzaen);
                        }
                        while (zzaen < obj.length - 1) {
                            obj[zzaen] = new zzc();
                            com_google_android_gms_internal_zzbxl.zza(obj[zzaen]);
                            com_google_android_gms_internal_zzbxl.zzaen();
                            zzaen++;
                        }
                        obj[zzaen] = new zzc();
                        com_google_android_gms_internal_zzbxl.zza(obj[zzaen]);
                        this.zzbxc = obj;
                        continue;
                    case 18:
                        this.name = com_google_android_gms_internal_zzbxl.readString();
                        continue;
                    case 24:
                        this.zzbxd = Long.valueOf(com_google_android_gms_internal_zzbxl.zzaeq());
                        continue;
                    case 32:
                        this.zzbxe = Long.valueOf(com_google_android_gms_internal_zzbxl.zzaeq());
                        continue;
                    case MotionEventCompat.AXIS_GENERIC_9 /*40*/:
                        this.count = Integer.valueOf(com_google_android_gms_internal_zzbxl.zzaer());
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

        public void zza(zzbxm com_google_android_gms_internal_zzbxm) throws IOException {
            if (this.zzbxc != null && this.zzbxc.length > 0) {
                for (zzbxt com_google_android_gms_internal_zzbxt : this.zzbxc) {
                    if (com_google_android_gms_internal_zzbxt != null) {
                        com_google_android_gms_internal_zzbxm.zza(1, com_google_android_gms_internal_zzbxt);
                    }
                }
            }
            if (this.name != null) {
                com_google_android_gms_internal_zzbxm.zzq(2, this.name);
            }
            if (this.zzbxd != null) {
                com_google_android_gms_internal_zzbxm.zzb(3, this.zzbxd.longValue());
            }
            if (this.zzbxe != null) {
                com_google_android_gms_internal_zzbxm.zzb(4, this.zzbxe.longValue());
            }
            if (this.count != null) {
                com_google_android_gms_internal_zzbxm.zzJ(5, this.count.intValue());
            }
            super.zza(com_google_android_gms_internal_zzbxm);
        }

        public /* synthetic */ zzbxt zzb(zzbxl com_google_android_gms_internal_zzbxl) throws IOException {
            return zzQ(com_google_android_gms_internal_zzbxl);
        }

        protected int zzu() {
            int zzu = super.zzu();
            if (this.zzbxc != null && this.zzbxc.length > 0) {
                for (zzbxt com_google_android_gms_internal_zzbxt : this.zzbxc) {
                    if (com_google_android_gms_internal_zzbxt != null) {
                        zzu += zzbxm.zzc(1, com_google_android_gms_internal_zzbxt);
                    }
                }
            }
            if (this.name != null) {
                zzu += zzbxm.zzr(2, this.name);
            }
            if (this.zzbxd != null) {
                zzu += zzbxm.zzf(3, this.zzbxd.longValue());
            }
            if (this.zzbxe != null) {
                zzu += zzbxm.zzf(4, this.zzbxe.longValue());
            }
            return this.count != null ? zzu + zzbxm.zzL(5, this.count.intValue()) : zzu;
        }
    }

    public static final class zzc extends zzbxn<zzc> {
        private static volatile zzc[] zzbxf;
        public String name;
        public String zzaGV;
        public Float zzbwi;
        public Double zzbwj;
        public Long zzbxg;

        public zzc() {
            zzNC();
        }

        public static zzc[] zzNB() {
            if (zzbxf == null) {
                synchronized (zzbxr.zzcuI) {
                    if (zzbxf == null) {
                        zzbxf = new zzc[0];
                    }
                }
            }
            return zzbxf;
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
            if (this.zzbxg == null) {
                if (com_google_android_gms_internal_zzauw_zzc.zzbxg != null) {
                    return false;
                }
            } else if (!this.zzbxg.equals(com_google_android_gms_internal_zzauw_zzc.zzbxg)) {
                return false;
            }
            if (this.zzbwi == null) {
                if (com_google_android_gms_internal_zzauw_zzc.zzbwi != null) {
                    return false;
                }
            } else if (!this.zzbwi.equals(com_google_android_gms_internal_zzauw_zzc.zzbwi)) {
                return false;
            }
            if (this.zzbwj == null) {
                if (com_google_android_gms_internal_zzauw_zzc.zzbwj != null) {
                    return false;
                }
            } else if (!this.zzbwj.equals(com_google_android_gms_internal_zzauw_zzc.zzbwj)) {
                return false;
            }
            return (this.zzcuA == null || this.zzcuA.isEmpty()) ? com_google_android_gms_internal_zzauw_zzc.zzcuA == null || com_google_android_gms_internal_zzauw_zzc.zzcuA.isEmpty() : this.zzcuA.equals(com_google_android_gms_internal_zzauw_zzc.zzcuA);
        }

        public int hashCode() {
            int i = 0;
            int hashCode = ((this.zzbwj == null ? 0 : this.zzbwj.hashCode()) + (((this.zzbwi == null ? 0 : this.zzbwi.hashCode()) + (((this.zzbxg == null ? 0 : this.zzbxg.hashCode()) + (((this.zzaGV == null ? 0 : this.zzaGV.hashCode()) + (((this.name == null ? 0 : this.name.hashCode()) + ((getClass().getName().hashCode() + 527) * 31)) * 31)) * 31)) * 31)) * 31)) * 31;
            if (!(this.zzcuA == null || this.zzcuA.isEmpty())) {
                i = this.zzcuA.hashCode();
            }
            return hashCode + i;
        }

        public zzc zzNC() {
            this.name = null;
            this.zzaGV = null;
            this.zzbxg = null;
            this.zzbwi = null;
            this.zzbwj = null;
            this.zzcuA = null;
            this.zzcuJ = -1;
            return this;
        }

        public zzc zzR(zzbxl com_google_android_gms_internal_zzbxl) throws IOException {
            while (true) {
                int zzaen = com_google_android_gms_internal_zzbxl.zzaen();
                switch (zzaen) {
                    case 0:
                        break;
                    case 10:
                        this.name = com_google_android_gms_internal_zzbxl.readString();
                        continue;
                    case 18:
                        this.zzaGV = com_google_android_gms_internal_zzbxl.readString();
                        continue;
                    case 24:
                        this.zzbxg = Long.valueOf(com_google_android_gms_internal_zzbxl.zzaeq());
                        continue;
                    case 37:
                        this.zzbwi = Float.valueOf(com_google_android_gms_internal_zzbxl.readFloat());
                        continue;
                    case 41:
                        this.zzbwj = Double.valueOf(com_google_android_gms_internal_zzbxl.readDouble());
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

        public void zza(zzbxm com_google_android_gms_internal_zzbxm) throws IOException {
            if (this.name != null) {
                com_google_android_gms_internal_zzbxm.zzq(1, this.name);
            }
            if (this.zzaGV != null) {
                com_google_android_gms_internal_zzbxm.zzq(2, this.zzaGV);
            }
            if (this.zzbxg != null) {
                com_google_android_gms_internal_zzbxm.zzb(3, this.zzbxg.longValue());
            }
            if (this.zzbwi != null) {
                com_google_android_gms_internal_zzbxm.zzc(4, this.zzbwi.floatValue());
            }
            if (this.zzbwj != null) {
                com_google_android_gms_internal_zzbxm.zza(5, this.zzbwj.doubleValue());
            }
            super.zza(com_google_android_gms_internal_zzbxm);
        }

        public /* synthetic */ zzbxt zzb(zzbxl com_google_android_gms_internal_zzbxl) throws IOException {
            return zzR(com_google_android_gms_internal_zzbxl);
        }

        protected int zzu() {
            int zzu = super.zzu();
            if (this.name != null) {
                zzu += zzbxm.zzr(1, this.name);
            }
            if (this.zzaGV != null) {
                zzu += zzbxm.zzr(2, this.zzaGV);
            }
            if (this.zzbxg != null) {
                zzu += zzbxm.zzf(3, this.zzbxg.longValue());
            }
            if (this.zzbwi != null) {
                zzu += zzbxm.zzd(4, this.zzbwi.floatValue());
            }
            return this.zzbwj != null ? zzu + zzbxm.zzb(5, this.zzbwj.doubleValue()) : zzu;
        }
    }

    public static final class zzd extends zzbxn<zzd> {
        public zze[] zzbxh;

        public zzd() {
            zzND();
        }

        public boolean equals(Object obj) {
            if (obj == this) {
                return true;
            }
            if (!(obj instanceof zzd)) {
                return false;
            }
            zzd com_google_android_gms_internal_zzauw_zzd = (zzd) obj;
            return zzbxr.equals(this.zzbxh, com_google_android_gms_internal_zzauw_zzd.zzbxh) ? (this.zzcuA == null || this.zzcuA.isEmpty()) ? com_google_android_gms_internal_zzauw_zzd.zzcuA == null || com_google_android_gms_internal_zzauw_zzd.zzcuA.isEmpty() : this.zzcuA.equals(com_google_android_gms_internal_zzauw_zzd.zzcuA) : false;
        }

        public int hashCode() {
            int hashCode = (((getClass().getName().hashCode() + 527) * 31) + zzbxr.hashCode(this.zzbxh)) * 31;
            int hashCode2 = (this.zzcuA == null || this.zzcuA.isEmpty()) ? 0 : this.zzcuA.hashCode();
            return hashCode2 + hashCode;
        }

        public zzd zzND() {
            this.zzbxh = zze.zzNE();
            this.zzcuA = null;
            this.zzcuJ = -1;
            return this;
        }

        public zzd zzS(zzbxl com_google_android_gms_internal_zzbxl) throws IOException {
            while (true) {
                int zzaen = com_google_android_gms_internal_zzbxl.zzaen();
                switch (zzaen) {
                    case 0:
                        break;
                    case 10:
                        int zzb = zzbxw.zzb(com_google_android_gms_internal_zzbxl, 10);
                        zzaen = this.zzbxh == null ? 0 : this.zzbxh.length;
                        Object obj = new zze[(zzb + zzaen)];
                        if (zzaen != 0) {
                            System.arraycopy(this.zzbxh, 0, obj, 0, zzaen);
                        }
                        while (zzaen < obj.length - 1) {
                            obj[zzaen] = new zze();
                            com_google_android_gms_internal_zzbxl.zza(obj[zzaen]);
                            com_google_android_gms_internal_zzbxl.zzaen();
                            zzaen++;
                        }
                        obj[zzaen] = new zze();
                        com_google_android_gms_internal_zzbxl.zza(obj[zzaen]);
                        this.zzbxh = obj;
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

        public void zza(zzbxm com_google_android_gms_internal_zzbxm) throws IOException {
            if (this.zzbxh != null && this.zzbxh.length > 0) {
                for (zzbxt com_google_android_gms_internal_zzbxt : this.zzbxh) {
                    if (com_google_android_gms_internal_zzbxt != null) {
                        com_google_android_gms_internal_zzbxm.zza(1, com_google_android_gms_internal_zzbxt);
                    }
                }
            }
            super.zza(com_google_android_gms_internal_zzbxm);
        }

        public /* synthetic */ zzbxt zzb(zzbxl com_google_android_gms_internal_zzbxl) throws IOException {
            return zzS(com_google_android_gms_internal_zzbxl);
        }

        protected int zzu() {
            int zzu = super.zzu();
            if (this.zzbxh != null && this.zzbxh.length > 0) {
                for (zzbxt com_google_android_gms_internal_zzbxt : this.zzbxh) {
                    if (com_google_android_gms_internal_zzbxt != null) {
                        zzu += zzbxm.zzc(1, com_google_android_gms_internal_zzbxt);
                    }
                }
            }
            return zzu;
        }
    }

    public static final class zze extends zzbxn<zze> {
        private static volatile zze[] zzbxi;
        public String zzaS;
        public String zzbb;
        public String zzbhM;
        public String zzbqP;
        public String zzbqQ;
        public String zzbqT;
        public String zzbqX;
        public Long zzbxA;
        public Integer zzbxB;
        public Boolean zzbxC;
        public zza[] zzbxD;
        public Integer zzbxE;
        public Integer zzbxF;
        public Integer zzbxG;
        public String zzbxH;
        public Long zzbxI;
        public Long zzbxJ;
        public Integer zzbxj;
        public zzb[] zzbxk;
        public zzg[] zzbxl;
        public Long zzbxm;
        public Long zzbxn;
        public Long zzbxo;
        public Long zzbxp;
        public Long zzbxq;
        public String zzbxr;
        public String zzbxs;
        public String zzbxt;
        public Integer zzbxu;
        public Long zzbxv;
        public Long zzbxw;
        public String zzbxx;
        public Boolean zzbxy;
        public String zzbxz;

        public zze() {
            zzNF();
        }

        public static zze[] zzNE() {
            if (zzbxi == null) {
                synchronized (zzbxr.zzcuI) {
                    if (zzbxi == null) {
                        zzbxi = new zze[0];
                    }
                }
            }
            return zzbxi;
        }

        public boolean equals(Object obj) {
            if (obj == this) {
                return true;
            }
            if (!(obj instanceof zze)) {
                return false;
            }
            zze com_google_android_gms_internal_zzauw_zze = (zze) obj;
            if (this.zzbxj == null) {
                if (com_google_android_gms_internal_zzauw_zze.zzbxj != null) {
                    return false;
                }
            } else if (!this.zzbxj.equals(com_google_android_gms_internal_zzauw_zze.zzbxj)) {
                return false;
            }
            if (!zzbxr.equals(this.zzbxk, com_google_android_gms_internal_zzauw_zze.zzbxk) || !zzbxr.equals(this.zzbxl, com_google_android_gms_internal_zzauw_zze.zzbxl)) {
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
            if (this.zzbxr == null) {
                if (com_google_android_gms_internal_zzauw_zze.zzbxr != null) {
                    return false;
                }
            } else if (!this.zzbxr.equals(com_google_android_gms_internal_zzauw_zze.zzbxr)) {
                return false;
            }
            if (this.zzbb == null) {
                if (com_google_android_gms_internal_zzauw_zze.zzbb != null) {
                    return false;
                }
            } else if (!this.zzbb.equals(com_google_android_gms_internal_zzauw_zze.zzbb)) {
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
            if (this.zzbxu == null) {
                if (com_google_android_gms_internal_zzauw_zze.zzbxu != null) {
                    return false;
                }
            } else if (!this.zzbxu.equals(com_google_android_gms_internal_zzauw_zze.zzbxu)) {
                return false;
            }
            if (this.zzbqQ == null) {
                if (com_google_android_gms_internal_zzauw_zze.zzbqQ != null) {
                    return false;
                }
            } else if (!this.zzbqQ.equals(com_google_android_gms_internal_zzauw_zze.zzbqQ)) {
                return false;
            }
            if (this.zzaS == null) {
                if (com_google_android_gms_internal_zzauw_zze.zzaS != null) {
                    return false;
                }
            } else if (!this.zzaS.equals(com_google_android_gms_internal_zzauw_zze.zzaS)) {
                return false;
            }
            if (this.zzbhM == null) {
                if (com_google_android_gms_internal_zzauw_zze.zzbhM != null) {
                    return false;
                }
            } else if (!this.zzbhM.equals(com_google_android_gms_internal_zzauw_zze.zzbhM)) {
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
            if (this.zzbxB == null) {
                if (com_google_android_gms_internal_zzauw_zze.zzbxB != null) {
                    return false;
                }
            } else if (!this.zzbxB.equals(com_google_android_gms_internal_zzauw_zze.zzbxB)) {
                return false;
            }
            if (this.zzbqT == null) {
                if (com_google_android_gms_internal_zzauw_zze.zzbqT != null) {
                    return false;
                }
            } else if (!this.zzbqT.equals(com_google_android_gms_internal_zzauw_zze.zzbqT)) {
                return false;
            }
            if (this.zzbqP == null) {
                if (com_google_android_gms_internal_zzauw_zze.zzbqP != null) {
                    return false;
                }
            } else if (!this.zzbqP.equals(com_google_android_gms_internal_zzauw_zze.zzbqP)) {
                return false;
            }
            if (this.zzbxC == null) {
                if (com_google_android_gms_internal_zzauw_zze.zzbxC != null) {
                    return false;
                }
            } else if (!this.zzbxC.equals(com_google_android_gms_internal_zzauw_zze.zzbxC)) {
                return false;
            }
            if (!zzbxr.equals(this.zzbxD, com_google_android_gms_internal_zzauw_zze.zzbxD)) {
                return false;
            }
            if (this.zzbqX == null) {
                if (com_google_android_gms_internal_zzauw_zze.zzbqX != null) {
                    return false;
                }
            } else if (!this.zzbqX.equals(com_google_android_gms_internal_zzauw_zze.zzbqX)) {
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
            if (this.zzbxJ == null) {
                if (com_google_android_gms_internal_zzauw_zze.zzbxJ != null) {
                    return false;
                }
            } else if (!this.zzbxJ.equals(com_google_android_gms_internal_zzauw_zze.zzbxJ)) {
                return false;
            }
            return (this.zzcuA == null || this.zzcuA.isEmpty()) ? com_google_android_gms_internal_zzauw_zze.zzcuA == null || com_google_android_gms_internal_zzauw_zze.zzcuA.isEmpty() : this.zzcuA.equals(com_google_android_gms_internal_zzauw_zze.zzcuA);
        }

        public int hashCode() {
            int i = 0;
            int hashCode = ((this.zzbxJ == null ? 0 : this.zzbxJ.hashCode()) + (((this.zzbxI == null ? 0 : this.zzbxI.hashCode()) + (((this.zzbxH == null ? 0 : this.zzbxH.hashCode()) + (((this.zzbxG == null ? 0 : this.zzbxG.hashCode()) + (((this.zzbxF == null ? 0 : this.zzbxF.hashCode()) + (((this.zzbxE == null ? 0 : this.zzbxE.hashCode()) + (((this.zzbqX == null ? 0 : this.zzbqX.hashCode()) + (((((this.zzbxC == null ? 0 : this.zzbxC.hashCode()) + (((this.zzbqP == null ? 0 : this.zzbqP.hashCode()) + (((this.zzbqT == null ? 0 : this.zzbqT.hashCode()) + (((this.zzbxB == null ? 0 : this.zzbxB.hashCode()) + (((this.zzbxA == null ? 0 : this.zzbxA.hashCode()) + (((this.zzbxz == null ? 0 : this.zzbxz.hashCode()) + (((this.zzbxy == null ? 0 : this.zzbxy.hashCode()) + (((this.zzbxx == null ? 0 : this.zzbxx.hashCode()) + (((this.zzbxw == null ? 0 : this.zzbxw.hashCode()) + (((this.zzbxv == null ? 0 : this.zzbxv.hashCode()) + (((this.zzbhM == null ? 0 : this.zzbhM.hashCode()) + (((this.zzaS == null ? 0 : this.zzaS.hashCode()) + (((this.zzbqQ == null ? 0 : this.zzbqQ.hashCode()) + (((this.zzbxu == null ? 0 : this.zzbxu.hashCode()) + (((this.zzbxt == null ? 0 : this.zzbxt.hashCode()) + (((this.zzbxs == null ? 0 : this.zzbxs.hashCode()) + (((this.zzbb == null ? 0 : this.zzbb.hashCode()) + (((this.zzbxr == null ? 0 : this.zzbxr.hashCode()) + (((this.zzbxq == null ? 0 : this.zzbxq.hashCode()) + (((this.zzbxp == null ? 0 : this.zzbxp.hashCode()) + (((this.zzbxo == null ? 0 : this.zzbxo.hashCode()) + (((this.zzbxn == null ? 0 : this.zzbxn.hashCode()) + (((this.zzbxm == null ? 0 : this.zzbxm.hashCode()) + (((((((this.zzbxj == null ? 0 : this.zzbxj.hashCode()) + ((getClass().getName().hashCode() + 527) * 31)) * 31) + zzbxr.hashCode(this.zzbxk)) * 31) + zzbxr.hashCode(this.zzbxl)) * 31)) * 31)) * 31)) * 31)) * 31)) * 31)) * 31)) * 31)) * 31)) * 31)) * 31)) * 31)) * 31)) * 31)) * 31)) * 31)) * 31)) * 31)) * 31)) * 31)) * 31)) * 31)) * 31)) * 31) + zzbxr.hashCode(this.zzbxD)) * 31)) * 31)) * 31)) * 31)) * 31)) * 31)) * 31)) * 31;
            if (!(this.zzcuA == null || this.zzcuA.isEmpty())) {
                i = this.zzcuA.hashCode();
            }
            return hashCode + i;
        }

        public zze zzNF() {
            this.zzbxj = null;
            this.zzbxk = zzb.zzNz();
            this.zzbxl = zzg.zzNH();
            this.zzbxm = null;
            this.zzbxn = null;
            this.zzbxo = null;
            this.zzbxp = null;
            this.zzbxq = null;
            this.zzbxr = null;
            this.zzbb = null;
            this.zzbxs = null;
            this.zzbxt = null;
            this.zzbxu = null;
            this.zzbqQ = null;
            this.zzaS = null;
            this.zzbhM = null;
            this.zzbxv = null;
            this.zzbxw = null;
            this.zzbxx = null;
            this.zzbxy = null;
            this.zzbxz = null;
            this.zzbxA = null;
            this.zzbxB = null;
            this.zzbqT = null;
            this.zzbqP = null;
            this.zzbxC = null;
            this.zzbxD = zza.zzNx();
            this.zzbqX = null;
            this.zzbxE = null;
            this.zzbxF = null;
            this.zzbxG = null;
            this.zzbxH = null;
            this.zzbxI = null;
            this.zzbxJ = null;
            this.zzcuA = null;
            this.zzcuJ = -1;
            return this;
        }

        public zze zzT(zzbxl com_google_android_gms_internal_zzbxl) throws IOException {
            while (true) {
                int zzaen = com_google_android_gms_internal_zzbxl.zzaen();
                int zzb;
                Object obj;
                switch (zzaen) {
                    case 0:
                        break;
                    case 8:
                        this.zzbxj = Integer.valueOf(com_google_android_gms_internal_zzbxl.zzaer());
                        continue;
                    case 18:
                        zzb = zzbxw.zzb(com_google_android_gms_internal_zzbxl, 18);
                        zzaen = this.zzbxk == null ? 0 : this.zzbxk.length;
                        obj = new zzb[(zzb + zzaen)];
                        if (zzaen != 0) {
                            System.arraycopy(this.zzbxk, 0, obj, 0, zzaen);
                        }
                        while (zzaen < obj.length - 1) {
                            obj[zzaen] = new zzb();
                            com_google_android_gms_internal_zzbxl.zza(obj[zzaen]);
                            com_google_android_gms_internal_zzbxl.zzaen();
                            zzaen++;
                        }
                        obj[zzaen] = new zzb();
                        com_google_android_gms_internal_zzbxl.zza(obj[zzaen]);
                        this.zzbxk = obj;
                        continue;
                    case NalUnitTypes.NAL_TYPE_RSV_VCL26 /*26*/:
                        zzb = zzbxw.zzb(com_google_android_gms_internal_zzbxl, 26);
                        zzaen = this.zzbxl == null ? 0 : this.zzbxl.length;
                        obj = new zzg[(zzb + zzaen)];
                        if (zzaen != 0) {
                            System.arraycopy(this.zzbxl, 0, obj, 0, zzaen);
                        }
                        while (zzaen < obj.length - 1) {
                            obj[zzaen] = new zzg();
                            com_google_android_gms_internal_zzbxl.zza(obj[zzaen]);
                            com_google_android_gms_internal_zzbxl.zzaen();
                            zzaen++;
                        }
                        obj[zzaen] = new zzg();
                        com_google_android_gms_internal_zzbxl.zza(obj[zzaen]);
                        this.zzbxl = obj;
                        continue;
                    case 32:
                        this.zzbxm = Long.valueOf(com_google_android_gms_internal_zzbxl.zzaeq());
                        continue;
                    case MotionEventCompat.AXIS_GENERIC_9 /*40*/:
                        this.zzbxn = Long.valueOf(com_google_android_gms_internal_zzbxl.zzaeq());
                        continue;
                    case 48:
                        this.zzbxo = Long.valueOf(com_google_android_gms_internal_zzbxl.zzaeq());
                        continue;
                    case 56:
                        this.zzbxq = Long.valueOf(com_google_android_gms_internal_zzbxl.zzaeq());
                        continue;
                    case 66:
                        this.zzbxr = com_google_android_gms_internal_zzbxl.readString();
                        continue;
                    case 74:
                        this.zzbb = com_google_android_gms_internal_zzbxl.readString();
                        continue;
                    case 82:
                        this.zzbxs = com_google_android_gms_internal_zzbxl.readString();
                        continue;
                    case 90:
                        this.zzbxt = com_google_android_gms_internal_zzbxl.readString();
                        continue;
                    case 96:
                        this.zzbxu = Integer.valueOf(com_google_android_gms_internal_zzbxl.zzaer());
                        continue;
                    case 106:
                        this.zzbqQ = com_google_android_gms_internal_zzbxl.readString();
                        continue;
                    case 114:
                        this.zzaS = com_google_android_gms_internal_zzbxl.readString();
                        continue;
                    case TsExtractor.TS_STREAM_TYPE_HDMV_DTS /*130*/:
                        this.zzbhM = com_google_android_gms_internal_zzbxl.readString();
                        continue;
                    case 136:
                        this.zzbxv = Long.valueOf(com_google_android_gms_internal_zzbxl.zzaeq());
                        continue;
                    case 144:
                        this.zzbxw = Long.valueOf(com_google_android_gms_internal_zzbxl.zzaeq());
                        continue;
                    case 154:
                        this.zzbxx = com_google_android_gms_internal_zzbxl.readString();
                        continue;
                    case 160:
                        this.zzbxy = Boolean.valueOf(com_google_android_gms_internal_zzbxl.zzaet());
                        continue;
                    case 170:
                        this.zzbxz = com_google_android_gms_internal_zzbxl.readString();
                        continue;
                    case 176:
                        this.zzbxA = Long.valueOf(com_google_android_gms_internal_zzbxl.zzaeq());
                        continue;
                    case 184:
                        this.zzbxB = Integer.valueOf(com_google_android_gms_internal_zzbxl.zzaer());
                        continue;
                    case 194:
                        this.zzbqT = com_google_android_gms_internal_zzbxl.readString();
                        continue;
                    case 202:
                        this.zzbqP = com_google_android_gms_internal_zzbxl.readString();
                        continue;
                    case 208:
                        this.zzbxp = Long.valueOf(com_google_android_gms_internal_zzbxl.zzaeq());
                        continue;
                    case 224:
                        this.zzbxC = Boolean.valueOf(com_google_android_gms_internal_zzbxl.zzaet());
                        continue;
                    case 234:
                        zzb = zzbxw.zzb(com_google_android_gms_internal_zzbxl, 234);
                        zzaen = this.zzbxD == null ? 0 : this.zzbxD.length;
                        obj = new zza[(zzb + zzaen)];
                        if (zzaen != 0) {
                            System.arraycopy(this.zzbxD, 0, obj, 0, zzaen);
                        }
                        while (zzaen < obj.length - 1) {
                            obj[zzaen] = new zza();
                            com_google_android_gms_internal_zzbxl.zza(obj[zzaen]);
                            com_google_android_gms_internal_zzbxl.zzaen();
                            zzaen++;
                        }
                        obj[zzaen] = new zza();
                        com_google_android_gms_internal_zzbxl.zza(obj[zzaen]);
                        this.zzbxD = obj;
                        continue;
                    case 242:
                        this.zzbqX = com_google_android_gms_internal_zzbxl.readString();
                        continue;
                    case 248:
                        this.zzbxE = Integer.valueOf(com_google_android_gms_internal_zzbxl.zzaer());
                        continue;
                    case 256:
                        this.zzbxF = Integer.valueOf(com_google_android_gms_internal_zzbxl.zzaer());
                        continue;
                    case 264:
                        this.zzbxG = Integer.valueOf(com_google_android_gms_internal_zzbxl.zzaer());
                        continue;
                    case 274:
                        this.zzbxH = com_google_android_gms_internal_zzbxl.readString();
                        continue;
                    case 280:
                        this.zzbxI = Long.valueOf(com_google_android_gms_internal_zzbxl.zzaeq());
                        continue;
                    case 288:
                        this.zzbxJ = Long.valueOf(com_google_android_gms_internal_zzbxl.zzaeq());
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

        public void zza(zzbxm com_google_android_gms_internal_zzbxm) throws IOException {
            int i = 0;
            if (this.zzbxj != null) {
                com_google_android_gms_internal_zzbxm.zzJ(1, this.zzbxj.intValue());
            }
            if (this.zzbxk != null && this.zzbxk.length > 0) {
                for (zzbxt com_google_android_gms_internal_zzbxt : this.zzbxk) {
                    if (com_google_android_gms_internal_zzbxt != null) {
                        com_google_android_gms_internal_zzbxm.zza(2, com_google_android_gms_internal_zzbxt);
                    }
                }
            }
            if (this.zzbxl != null && this.zzbxl.length > 0) {
                for (zzbxt com_google_android_gms_internal_zzbxt2 : this.zzbxl) {
                    if (com_google_android_gms_internal_zzbxt2 != null) {
                        com_google_android_gms_internal_zzbxm.zza(3, com_google_android_gms_internal_zzbxt2);
                    }
                }
            }
            if (this.zzbxm != null) {
                com_google_android_gms_internal_zzbxm.zzb(4, this.zzbxm.longValue());
            }
            if (this.zzbxn != null) {
                com_google_android_gms_internal_zzbxm.zzb(5, this.zzbxn.longValue());
            }
            if (this.zzbxo != null) {
                com_google_android_gms_internal_zzbxm.zzb(6, this.zzbxo.longValue());
            }
            if (this.zzbxq != null) {
                com_google_android_gms_internal_zzbxm.zzb(7, this.zzbxq.longValue());
            }
            if (this.zzbxr != null) {
                com_google_android_gms_internal_zzbxm.zzq(8, this.zzbxr);
            }
            if (this.zzbb != null) {
                com_google_android_gms_internal_zzbxm.zzq(9, this.zzbb);
            }
            if (this.zzbxs != null) {
                com_google_android_gms_internal_zzbxm.zzq(10, this.zzbxs);
            }
            if (this.zzbxt != null) {
                com_google_android_gms_internal_zzbxm.zzq(11, this.zzbxt);
            }
            if (this.zzbxu != null) {
                com_google_android_gms_internal_zzbxm.zzJ(12, this.zzbxu.intValue());
            }
            if (this.zzbqQ != null) {
                com_google_android_gms_internal_zzbxm.zzq(13, this.zzbqQ);
            }
            if (this.zzaS != null) {
                com_google_android_gms_internal_zzbxm.zzq(14, this.zzaS);
            }
            if (this.zzbhM != null) {
                com_google_android_gms_internal_zzbxm.zzq(16, this.zzbhM);
            }
            if (this.zzbxv != null) {
                com_google_android_gms_internal_zzbxm.zzb(17, this.zzbxv.longValue());
            }
            if (this.zzbxw != null) {
                com_google_android_gms_internal_zzbxm.zzb(18, this.zzbxw.longValue());
            }
            if (this.zzbxx != null) {
                com_google_android_gms_internal_zzbxm.zzq(19, this.zzbxx);
            }
            if (this.zzbxy != null) {
                com_google_android_gms_internal_zzbxm.zzg(20, this.zzbxy.booleanValue());
            }
            if (this.zzbxz != null) {
                com_google_android_gms_internal_zzbxm.zzq(21, this.zzbxz);
            }
            if (this.zzbxA != null) {
                com_google_android_gms_internal_zzbxm.zzb(22, this.zzbxA.longValue());
            }
            if (this.zzbxB != null) {
                com_google_android_gms_internal_zzbxm.zzJ(23, this.zzbxB.intValue());
            }
            if (this.zzbqT != null) {
                com_google_android_gms_internal_zzbxm.zzq(24, this.zzbqT);
            }
            if (this.zzbqP != null) {
                com_google_android_gms_internal_zzbxm.zzq(25, this.zzbqP);
            }
            if (this.zzbxp != null) {
                com_google_android_gms_internal_zzbxm.zzb(26, this.zzbxp.longValue());
            }
            if (this.zzbxC != null) {
                com_google_android_gms_internal_zzbxm.zzg(28, this.zzbxC.booleanValue());
            }
            if (this.zzbxD != null && this.zzbxD.length > 0) {
                while (i < this.zzbxD.length) {
                    zzbxt com_google_android_gms_internal_zzbxt3 = this.zzbxD[i];
                    if (com_google_android_gms_internal_zzbxt3 != null) {
                        com_google_android_gms_internal_zzbxm.zza(29, com_google_android_gms_internal_zzbxt3);
                    }
                    i++;
                }
            }
            if (this.zzbqX != null) {
                com_google_android_gms_internal_zzbxm.zzq(30, this.zzbqX);
            }
            if (this.zzbxE != null) {
                com_google_android_gms_internal_zzbxm.zzJ(31, this.zzbxE.intValue());
            }
            if (this.zzbxF != null) {
                com_google_android_gms_internal_zzbxm.zzJ(32, this.zzbxF.intValue());
            }
            if (this.zzbxG != null) {
                com_google_android_gms_internal_zzbxm.zzJ(33, this.zzbxG.intValue());
            }
            if (this.zzbxH != null) {
                com_google_android_gms_internal_zzbxm.zzq(34, this.zzbxH);
            }
            if (this.zzbxI != null) {
                com_google_android_gms_internal_zzbxm.zzb(35, this.zzbxI.longValue());
            }
            if (this.zzbxJ != null) {
                com_google_android_gms_internal_zzbxm.zzb(36, this.zzbxJ.longValue());
            }
            super.zza(com_google_android_gms_internal_zzbxm);
        }

        public /* synthetic */ zzbxt zzb(zzbxl com_google_android_gms_internal_zzbxl) throws IOException {
            return zzT(com_google_android_gms_internal_zzbxl);
        }

        protected int zzu() {
            int i;
            int i2 = 0;
            int zzu = super.zzu();
            if (this.zzbxj != null) {
                zzu += zzbxm.zzL(1, this.zzbxj.intValue());
            }
            if (this.zzbxk != null && this.zzbxk.length > 0) {
                i = zzu;
                for (zzbxt com_google_android_gms_internal_zzbxt : this.zzbxk) {
                    if (com_google_android_gms_internal_zzbxt != null) {
                        i += zzbxm.zzc(2, com_google_android_gms_internal_zzbxt);
                    }
                }
                zzu = i;
            }
            if (this.zzbxl != null && this.zzbxl.length > 0) {
                i = zzu;
                for (zzbxt com_google_android_gms_internal_zzbxt2 : this.zzbxl) {
                    if (com_google_android_gms_internal_zzbxt2 != null) {
                        i += zzbxm.zzc(3, com_google_android_gms_internal_zzbxt2);
                    }
                }
                zzu = i;
            }
            if (this.zzbxm != null) {
                zzu += zzbxm.zzf(4, this.zzbxm.longValue());
            }
            if (this.zzbxn != null) {
                zzu += zzbxm.zzf(5, this.zzbxn.longValue());
            }
            if (this.zzbxo != null) {
                zzu += zzbxm.zzf(6, this.zzbxo.longValue());
            }
            if (this.zzbxq != null) {
                zzu += zzbxm.zzf(7, this.zzbxq.longValue());
            }
            if (this.zzbxr != null) {
                zzu += zzbxm.zzr(8, this.zzbxr);
            }
            if (this.zzbb != null) {
                zzu += zzbxm.zzr(9, this.zzbb);
            }
            if (this.zzbxs != null) {
                zzu += zzbxm.zzr(10, this.zzbxs);
            }
            if (this.zzbxt != null) {
                zzu += zzbxm.zzr(11, this.zzbxt);
            }
            if (this.zzbxu != null) {
                zzu += zzbxm.zzL(12, this.zzbxu.intValue());
            }
            if (this.zzbqQ != null) {
                zzu += zzbxm.zzr(13, this.zzbqQ);
            }
            if (this.zzaS != null) {
                zzu += zzbxm.zzr(14, this.zzaS);
            }
            if (this.zzbhM != null) {
                zzu += zzbxm.zzr(16, this.zzbhM);
            }
            if (this.zzbxv != null) {
                zzu += zzbxm.zzf(17, this.zzbxv.longValue());
            }
            if (this.zzbxw != null) {
                zzu += zzbxm.zzf(18, this.zzbxw.longValue());
            }
            if (this.zzbxx != null) {
                zzu += zzbxm.zzr(19, this.zzbxx);
            }
            if (this.zzbxy != null) {
                zzu += zzbxm.zzh(20, this.zzbxy.booleanValue());
            }
            if (this.zzbxz != null) {
                zzu += zzbxm.zzr(21, this.zzbxz);
            }
            if (this.zzbxA != null) {
                zzu += zzbxm.zzf(22, this.zzbxA.longValue());
            }
            if (this.zzbxB != null) {
                zzu += zzbxm.zzL(23, this.zzbxB.intValue());
            }
            if (this.zzbqT != null) {
                zzu += zzbxm.zzr(24, this.zzbqT);
            }
            if (this.zzbqP != null) {
                zzu += zzbxm.zzr(25, this.zzbqP);
            }
            if (this.zzbxp != null) {
                zzu += zzbxm.zzf(26, this.zzbxp.longValue());
            }
            if (this.zzbxC != null) {
                zzu += zzbxm.zzh(28, this.zzbxC.booleanValue());
            }
            if (this.zzbxD != null && this.zzbxD.length > 0) {
                while (i2 < this.zzbxD.length) {
                    zzbxt com_google_android_gms_internal_zzbxt3 = this.zzbxD[i2];
                    if (com_google_android_gms_internal_zzbxt3 != null) {
                        zzu += zzbxm.zzc(29, com_google_android_gms_internal_zzbxt3);
                    }
                    i2++;
                }
            }
            if (this.zzbqX != null) {
                zzu += zzbxm.zzr(30, this.zzbqX);
            }
            if (this.zzbxE != null) {
                zzu += zzbxm.zzL(31, this.zzbxE.intValue());
            }
            if (this.zzbxF != null) {
                zzu += zzbxm.zzL(32, this.zzbxF.intValue());
            }
            if (this.zzbxG != null) {
                zzu += zzbxm.zzL(33, this.zzbxG.intValue());
            }
            if (this.zzbxH != null) {
                zzu += zzbxm.zzr(34, this.zzbxH);
            }
            if (this.zzbxI != null) {
                zzu += zzbxm.zzf(35, this.zzbxI.longValue());
            }
            return this.zzbxJ != null ? zzu + zzbxm.zzf(36, this.zzbxJ.longValue()) : zzu;
        }
    }

    public static final class zzf extends zzbxn<zzf> {
        public long[] zzbxK;
        public long[] zzbxL;

        public zzf() {
            zzNG();
        }

        public boolean equals(Object obj) {
            if (obj == this) {
                return true;
            }
            if (!(obj instanceof zzf)) {
                return false;
            }
            zzf com_google_android_gms_internal_zzauw_zzf = (zzf) obj;
            return (zzbxr.equals(this.zzbxK, com_google_android_gms_internal_zzauw_zzf.zzbxK) && zzbxr.equals(this.zzbxL, com_google_android_gms_internal_zzauw_zzf.zzbxL)) ? (this.zzcuA == null || this.zzcuA.isEmpty()) ? com_google_android_gms_internal_zzauw_zzf.zzcuA == null || com_google_android_gms_internal_zzauw_zzf.zzcuA.isEmpty() : this.zzcuA.equals(com_google_android_gms_internal_zzauw_zzf.zzcuA) : false;
        }

        public int hashCode() {
            int hashCode = (((((getClass().getName().hashCode() + 527) * 31) + zzbxr.hashCode(this.zzbxK)) * 31) + zzbxr.hashCode(this.zzbxL)) * 31;
            int hashCode2 = (this.zzcuA == null || this.zzcuA.isEmpty()) ? 0 : this.zzcuA.hashCode();
            return hashCode2 + hashCode;
        }

        public zzf zzNG() {
            this.zzbxK = zzbxw.zzcuP;
            this.zzbxL = zzbxw.zzcuP;
            this.zzcuA = null;
            this.zzcuJ = -1;
            return this;
        }

        public zzf zzU(zzbxl com_google_android_gms_internal_zzbxl) throws IOException {
            while (true) {
                int zzaen = com_google_android_gms_internal_zzbxl.zzaen();
                int zzb;
                Object obj;
                int zzqZ;
                Object obj2;
                switch (zzaen) {
                    case 0:
                        break;
                    case 8:
                        zzb = zzbxw.zzb(com_google_android_gms_internal_zzbxl, 8);
                        zzaen = this.zzbxK == null ? 0 : this.zzbxK.length;
                        obj = new long[(zzb + zzaen)];
                        if (zzaen != 0) {
                            System.arraycopy(this.zzbxK, 0, obj, 0, zzaen);
                        }
                        while (zzaen < obj.length - 1) {
                            obj[zzaen] = com_google_android_gms_internal_zzbxl.zzaep();
                            com_google_android_gms_internal_zzbxl.zzaen();
                            zzaen++;
                        }
                        obj[zzaen] = com_google_android_gms_internal_zzbxl.zzaep();
                        this.zzbxK = obj;
                        continue;
                    case 10:
                        zzqZ = com_google_android_gms_internal_zzbxl.zzqZ(com_google_android_gms_internal_zzbxl.zzaew());
                        zzb = com_google_android_gms_internal_zzbxl.getPosition();
                        zzaen = 0;
                        while (com_google_android_gms_internal_zzbxl.zzaeB() > 0) {
                            com_google_android_gms_internal_zzbxl.zzaep();
                            zzaen++;
                        }
                        com_google_android_gms_internal_zzbxl.zzrb(zzb);
                        zzb = this.zzbxK == null ? 0 : this.zzbxK.length;
                        obj2 = new long[(zzaen + zzb)];
                        if (zzb != 0) {
                            System.arraycopy(this.zzbxK, 0, obj2, 0, zzb);
                        }
                        while (zzb < obj2.length) {
                            obj2[zzb] = com_google_android_gms_internal_zzbxl.zzaep();
                            zzb++;
                        }
                        this.zzbxK = obj2;
                        com_google_android_gms_internal_zzbxl.zzra(zzqZ);
                        continue;
                    case 16:
                        zzb = zzbxw.zzb(com_google_android_gms_internal_zzbxl, 16);
                        zzaen = this.zzbxL == null ? 0 : this.zzbxL.length;
                        obj = new long[(zzb + zzaen)];
                        if (zzaen != 0) {
                            System.arraycopy(this.zzbxL, 0, obj, 0, zzaen);
                        }
                        while (zzaen < obj.length - 1) {
                            obj[zzaen] = com_google_android_gms_internal_zzbxl.zzaep();
                            com_google_android_gms_internal_zzbxl.zzaen();
                            zzaen++;
                        }
                        obj[zzaen] = com_google_android_gms_internal_zzbxl.zzaep();
                        this.zzbxL = obj;
                        continue;
                    case 18:
                        zzqZ = com_google_android_gms_internal_zzbxl.zzqZ(com_google_android_gms_internal_zzbxl.zzaew());
                        zzb = com_google_android_gms_internal_zzbxl.getPosition();
                        zzaen = 0;
                        while (com_google_android_gms_internal_zzbxl.zzaeB() > 0) {
                            com_google_android_gms_internal_zzbxl.zzaep();
                            zzaen++;
                        }
                        com_google_android_gms_internal_zzbxl.zzrb(zzb);
                        zzb = this.zzbxL == null ? 0 : this.zzbxL.length;
                        obj2 = new long[(zzaen + zzb)];
                        if (zzb != 0) {
                            System.arraycopy(this.zzbxL, 0, obj2, 0, zzb);
                        }
                        while (zzb < obj2.length) {
                            obj2[zzb] = com_google_android_gms_internal_zzbxl.zzaep();
                            zzb++;
                        }
                        this.zzbxL = obj2;
                        com_google_android_gms_internal_zzbxl.zzra(zzqZ);
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

        public void zza(zzbxm com_google_android_gms_internal_zzbxm) throws IOException {
            int i = 0;
            if (this.zzbxK != null && this.zzbxK.length > 0) {
                for (long zza : this.zzbxK) {
                    com_google_android_gms_internal_zzbxm.zza(1, zza);
                }
            }
            if (this.zzbxL != null && this.zzbxL.length > 0) {
                while (i < this.zzbxL.length) {
                    com_google_android_gms_internal_zzbxm.zza(2, this.zzbxL[i]);
                    i++;
                }
            }
            super.zza(com_google_android_gms_internal_zzbxm);
        }

        public /* synthetic */ zzbxt zzb(zzbxl com_google_android_gms_internal_zzbxl) throws IOException {
            return zzU(com_google_android_gms_internal_zzbxl);
        }

        protected int zzu() {
            int i;
            int i2;
            int i3 = 0;
            int zzu = super.zzu();
            if (this.zzbxK == null || this.zzbxK.length <= 0) {
                i = zzu;
            } else {
                i2 = 0;
                for (long zzbe : this.zzbxK) {
                    i2 += zzbxm.zzbe(zzbe);
                }
                i = (zzu + i2) + (this.zzbxK.length * 1);
            }
            if (this.zzbxL == null || this.zzbxL.length <= 0) {
                return i;
            }
            i2 = 0;
            while (i3 < this.zzbxL.length) {
                i2 += zzbxm.zzbe(this.zzbxL[i3]);
                i3++;
            }
            return (i + i2) + (this.zzbxL.length * 1);
        }
    }

    public static final class zzg extends zzbxn<zzg> {
        private static volatile zzg[] zzbxM;
        public String name;
        public String zzaGV;
        public Float zzbwi;
        public Double zzbwj;
        public Long zzbxN;
        public Long zzbxg;

        public zzg() {
            zzNI();
        }

        public static zzg[] zzNH() {
            if (zzbxM == null) {
                synchronized (zzbxr.zzcuI) {
                    if (zzbxM == null) {
                        zzbxM = new zzg[0];
                    }
                }
            }
            return zzbxM;
        }

        public boolean equals(Object obj) {
            if (obj == this) {
                return true;
            }
            if (!(obj instanceof zzg)) {
                return false;
            }
            zzg com_google_android_gms_internal_zzauw_zzg = (zzg) obj;
            if (this.zzbxN == null) {
                if (com_google_android_gms_internal_zzauw_zzg.zzbxN != null) {
                    return false;
                }
            } else if (!this.zzbxN.equals(com_google_android_gms_internal_zzauw_zzg.zzbxN)) {
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
            if (this.zzbxg == null) {
                if (com_google_android_gms_internal_zzauw_zzg.zzbxg != null) {
                    return false;
                }
            } else if (!this.zzbxg.equals(com_google_android_gms_internal_zzauw_zzg.zzbxg)) {
                return false;
            }
            if (this.zzbwi == null) {
                if (com_google_android_gms_internal_zzauw_zzg.zzbwi != null) {
                    return false;
                }
            } else if (!this.zzbwi.equals(com_google_android_gms_internal_zzauw_zzg.zzbwi)) {
                return false;
            }
            if (this.zzbwj == null) {
                if (com_google_android_gms_internal_zzauw_zzg.zzbwj != null) {
                    return false;
                }
            } else if (!this.zzbwj.equals(com_google_android_gms_internal_zzauw_zzg.zzbwj)) {
                return false;
            }
            return (this.zzcuA == null || this.zzcuA.isEmpty()) ? com_google_android_gms_internal_zzauw_zzg.zzcuA == null || com_google_android_gms_internal_zzauw_zzg.zzcuA.isEmpty() : this.zzcuA.equals(com_google_android_gms_internal_zzauw_zzg.zzcuA);
        }

        public int hashCode() {
            int i = 0;
            int hashCode = ((this.zzbwj == null ? 0 : this.zzbwj.hashCode()) + (((this.zzbwi == null ? 0 : this.zzbwi.hashCode()) + (((this.zzbxg == null ? 0 : this.zzbxg.hashCode()) + (((this.zzaGV == null ? 0 : this.zzaGV.hashCode()) + (((this.name == null ? 0 : this.name.hashCode()) + (((this.zzbxN == null ? 0 : this.zzbxN.hashCode()) + ((getClass().getName().hashCode() + 527) * 31)) * 31)) * 31)) * 31)) * 31)) * 31)) * 31;
            if (!(this.zzcuA == null || this.zzcuA.isEmpty())) {
                i = this.zzcuA.hashCode();
            }
            return hashCode + i;
        }

        public zzg zzNI() {
            this.zzbxN = null;
            this.name = null;
            this.zzaGV = null;
            this.zzbxg = null;
            this.zzbwi = null;
            this.zzbwj = null;
            this.zzcuA = null;
            this.zzcuJ = -1;
            return this;
        }

        public zzg zzV(zzbxl com_google_android_gms_internal_zzbxl) throws IOException {
            while (true) {
                int zzaen = com_google_android_gms_internal_zzbxl.zzaen();
                switch (zzaen) {
                    case 0:
                        break;
                    case 8:
                        this.zzbxN = Long.valueOf(com_google_android_gms_internal_zzbxl.zzaeq());
                        continue;
                    case 18:
                        this.name = com_google_android_gms_internal_zzbxl.readString();
                        continue;
                    case NalUnitTypes.NAL_TYPE_RSV_VCL26 /*26*/:
                        this.zzaGV = com_google_android_gms_internal_zzbxl.readString();
                        continue;
                    case 32:
                        this.zzbxg = Long.valueOf(com_google_android_gms_internal_zzbxl.zzaeq());
                        continue;
                    case MotionEventCompat.AXIS_GENERIC_14 /*45*/:
                        this.zzbwi = Float.valueOf(com_google_android_gms_internal_zzbxl.readFloat());
                        continue;
                    case 49:
                        this.zzbwj = Double.valueOf(com_google_android_gms_internal_zzbxl.readDouble());
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

        public void zza(zzbxm com_google_android_gms_internal_zzbxm) throws IOException {
            if (this.zzbxN != null) {
                com_google_android_gms_internal_zzbxm.zzb(1, this.zzbxN.longValue());
            }
            if (this.name != null) {
                com_google_android_gms_internal_zzbxm.zzq(2, this.name);
            }
            if (this.zzaGV != null) {
                com_google_android_gms_internal_zzbxm.zzq(3, this.zzaGV);
            }
            if (this.zzbxg != null) {
                com_google_android_gms_internal_zzbxm.zzb(4, this.zzbxg.longValue());
            }
            if (this.zzbwi != null) {
                com_google_android_gms_internal_zzbxm.zzc(5, this.zzbwi.floatValue());
            }
            if (this.zzbwj != null) {
                com_google_android_gms_internal_zzbxm.zza(6, this.zzbwj.doubleValue());
            }
            super.zza(com_google_android_gms_internal_zzbxm);
        }

        public /* synthetic */ zzbxt zzb(zzbxl com_google_android_gms_internal_zzbxl) throws IOException {
            return zzV(com_google_android_gms_internal_zzbxl);
        }

        protected int zzu() {
            int zzu = super.zzu();
            if (this.zzbxN != null) {
                zzu += zzbxm.zzf(1, this.zzbxN.longValue());
            }
            if (this.name != null) {
                zzu += zzbxm.zzr(2, this.name);
            }
            if (this.zzaGV != null) {
                zzu += zzbxm.zzr(3, this.zzaGV);
            }
            if (this.zzbxg != null) {
                zzu += zzbxm.zzf(4, this.zzbxg.longValue());
            }
            if (this.zzbwi != null) {
                zzu += zzbxm.zzd(5, this.zzbwi.floatValue());
            }
            return this.zzbwj != null ? zzu + zzbxm.zzb(6, this.zzbwj.doubleValue()) : zzu;
        }
    }
}
