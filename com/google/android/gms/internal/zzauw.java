package com.google.android.gms.internal;

import android.support.v4.view.MotionEventCompat;
import com.googlecode.mp4parser.authoring.tracks.h265.NalUnitTypes;
import java.io.IOException;
import org.telegram.messenger.exoplayer2.extractor.ts.TsExtractor;

public interface zzauw {

    public static final class zza extends zzbxn<zza> {
        private static volatile zza[] zzbwT;
        public zzf zzbwU;
        public zzf zzbwV;
        public Boolean zzbwW;
        public Integer zzbwk;

        public zza() {
            zzNz();
        }

        public static zza[] zzNy() {
            if (zzbwT == null) {
                synchronized (zzbxr.zzcuQ) {
                    if (zzbwT == null) {
                        zzbwT = new zza[0];
                    }
                }
            }
            return zzbwT;
        }

        public boolean equals(Object obj) {
            if (obj == this) {
                return true;
            }
            if (!(obj instanceof zza)) {
                return false;
            }
            zza com_google_android_gms_internal_zzauw_zza = (zza) obj;
            if (this.zzbwk == null) {
                if (com_google_android_gms_internal_zzauw_zza.zzbwk != null) {
                    return false;
                }
            } else if (!this.zzbwk.equals(com_google_android_gms_internal_zzauw_zza.zzbwk)) {
                return false;
            }
            if (this.zzbwU == null) {
                if (com_google_android_gms_internal_zzauw_zza.zzbwU != null) {
                    return false;
                }
            } else if (!this.zzbwU.equals(com_google_android_gms_internal_zzauw_zza.zzbwU)) {
                return false;
            }
            if (this.zzbwV == null) {
                if (com_google_android_gms_internal_zzauw_zza.zzbwV != null) {
                    return false;
                }
            } else if (!this.zzbwV.equals(com_google_android_gms_internal_zzauw_zza.zzbwV)) {
                return false;
            }
            if (this.zzbwW == null) {
                if (com_google_android_gms_internal_zzauw_zza.zzbwW != null) {
                    return false;
                }
            } else if (!this.zzbwW.equals(com_google_android_gms_internal_zzauw_zza.zzbwW)) {
                return false;
            }
            return (this.zzcuI == null || this.zzcuI.isEmpty()) ? com_google_android_gms_internal_zzauw_zza.zzcuI == null || com_google_android_gms_internal_zzauw_zza.zzcuI.isEmpty() : this.zzcuI.equals(com_google_android_gms_internal_zzauw_zza.zzcuI);
        }

        public int hashCode() {
            int i = 0;
            int hashCode = ((this.zzbwW == null ? 0 : this.zzbwW.hashCode()) + (((this.zzbwV == null ? 0 : this.zzbwV.hashCode()) + (((this.zzbwU == null ? 0 : this.zzbwU.hashCode()) + (((this.zzbwk == null ? 0 : this.zzbwk.hashCode()) + ((getClass().getName().hashCode() + 527) * 31)) * 31)) * 31)) * 31)) * 31;
            if (!(this.zzcuI == null || this.zzcuI.isEmpty())) {
                i = this.zzcuI.hashCode();
            }
            return hashCode + i;
        }

        public zza zzNz() {
            this.zzbwk = null;
            this.zzbwU = null;
            this.zzbwV = null;
            this.zzbwW = null;
            this.zzcuI = null;
            this.zzcuR = -1;
            return this;
        }

        public zza zzP(zzbxl com_google_android_gms_internal_zzbxl) throws IOException {
            while (true) {
                int zzaeo = com_google_android_gms_internal_zzbxl.zzaeo();
                switch (zzaeo) {
                    case 0:
                        break;
                    case 8:
                        this.zzbwk = Integer.valueOf(com_google_android_gms_internal_zzbxl.zzaes());
                        continue;
                    case 18:
                        if (this.zzbwU == null) {
                            this.zzbwU = new zzf();
                        }
                        com_google_android_gms_internal_zzbxl.zza(this.zzbwU);
                        continue;
                    case NalUnitTypes.NAL_TYPE_RSV_VCL26 /*26*/:
                        if (this.zzbwV == null) {
                            this.zzbwV = new zzf();
                        }
                        com_google_android_gms_internal_zzbxl.zza(this.zzbwV);
                        continue;
                    case 32:
                        this.zzbwW = Boolean.valueOf(com_google_android_gms_internal_zzbxl.zzaeu());
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

        public void zza(zzbxm com_google_android_gms_internal_zzbxm) throws IOException {
            if (this.zzbwk != null) {
                com_google_android_gms_internal_zzbxm.zzJ(1, this.zzbwk.intValue());
            }
            if (this.zzbwU != null) {
                com_google_android_gms_internal_zzbxm.zza(2, this.zzbwU);
            }
            if (this.zzbwV != null) {
                com_google_android_gms_internal_zzbxm.zza(3, this.zzbwV);
            }
            if (this.zzbwW != null) {
                com_google_android_gms_internal_zzbxm.zzg(4, this.zzbwW.booleanValue());
            }
            super.zza(com_google_android_gms_internal_zzbxm);
        }

        public /* synthetic */ zzbxt zzb(zzbxl com_google_android_gms_internal_zzbxl) throws IOException {
            return zzP(com_google_android_gms_internal_zzbxl);
        }

        protected int zzu() {
            int zzu = super.zzu();
            if (this.zzbwk != null) {
                zzu += zzbxm.zzL(1, this.zzbwk.intValue());
            }
            if (this.zzbwU != null) {
                zzu += zzbxm.zzc(2, this.zzbwU);
            }
            if (this.zzbwV != null) {
                zzu += zzbxm.zzc(3, this.zzbwV);
            }
            return this.zzbwW != null ? zzu + zzbxm.zzh(4, this.zzbwW.booleanValue()) : zzu;
        }
    }

    public static final class zzb extends zzbxn<zzb> {
        private static volatile zzb[] zzbwX;
        public Integer count;
        public String name;
        public zzc[] zzbwY;
        public Long zzbwZ;
        public Long zzbxa;

        public zzb() {
            zzNB();
        }

        public static zzb[] zzNA() {
            if (zzbwX == null) {
                synchronized (zzbxr.zzcuQ) {
                    if (zzbwX == null) {
                        zzbwX = new zzb[0];
                    }
                }
            }
            return zzbwX;
        }

        public boolean equals(Object obj) {
            if (obj == this) {
                return true;
            }
            if (!(obj instanceof zzb)) {
                return false;
            }
            zzb com_google_android_gms_internal_zzauw_zzb = (zzb) obj;
            if (!zzbxr.equals(this.zzbwY, com_google_android_gms_internal_zzauw_zzb.zzbwY)) {
                return false;
            }
            if (this.name == null) {
                if (com_google_android_gms_internal_zzauw_zzb.name != null) {
                    return false;
                }
            } else if (!this.name.equals(com_google_android_gms_internal_zzauw_zzb.name)) {
                return false;
            }
            if (this.zzbwZ == null) {
                if (com_google_android_gms_internal_zzauw_zzb.zzbwZ != null) {
                    return false;
                }
            } else if (!this.zzbwZ.equals(com_google_android_gms_internal_zzauw_zzb.zzbwZ)) {
                return false;
            }
            if (this.zzbxa == null) {
                if (com_google_android_gms_internal_zzauw_zzb.zzbxa != null) {
                    return false;
                }
            } else if (!this.zzbxa.equals(com_google_android_gms_internal_zzauw_zzb.zzbxa)) {
                return false;
            }
            if (this.count == null) {
                if (com_google_android_gms_internal_zzauw_zzb.count != null) {
                    return false;
                }
            } else if (!this.count.equals(com_google_android_gms_internal_zzauw_zzb.count)) {
                return false;
            }
            return (this.zzcuI == null || this.zzcuI.isEmpty()) ? com_google_android_gms_internal_zzauw_zzb.zzcuI == null || com_google_android_gms_internal_zzauw_zzb.zzcuI.isEmpty() : this.zzcuI.equals(com_google_android_gms_internal_zzauw_zzb.zzcuI);
        }

        public int hashCode() {
            int i = 0;
            int hashCode = ((this.count == null ? 0 : this.count.hashCode()) + (((this.zzbxa == null ? 0 : this.zzbxa.hashCode()) + (((this.zzbwZ == null ? 0 : this.zzbwZ.hashCode()) + (((this.name == null ? 0 : this.name.hashCode()) + ((((getClass().getName().hashCode() + 527) * 31) + zzbxr.hashCode(this.zzbwY)) * 31)) * 31)) * 31)) * 31)) * 31;
            if (!(this.zzcuI == null || this.zzcuI.isEmpty())) {
                i = this.zzcuI.hashCode();
            }
            return hashCode + i;
        }

        public zzb zzNB() {
            this.zzbwY = zzc.zzNC();
            this.name = null;
            this.zzbwZ = null;
            this.zzbxa = null;
            this.count = null;
            this.zzcuI = null;
            this.zzcuR = -1;
            return this;
        }

        public zzb zzQ(zzbxl com_google_android_gms_internal_zzbxl) throws IOException {
            while (true) {
                int zzaeo = com_google_android_gms_internal_zzbxl.zzaeo();
                switch (zzaeo) {
                    case 0:
                        break;
                    case 10:
                        int zzb = zzbxw.zzb(com_google_android_gms_internal_zzbxl, 10);
                        zzaeo = this.zzbwY == null ? 0 : this.zzbwY.length;
                        Object obj = new zzc[(zzb + zzaeo)];
                        if (zzaeo != 0) {
                            System.arraycopy(this.zzbwY, 0, obj, 0, zzaeo);
                        }
                        while (zzaeo < obj.length - 1) {
                            obj[zzaeo] = new zzc();
                            com_google_android_gms_internal_zzbxl.zza(obj[zzaeo]);
                            com_google_android_gms_internal_zzbxl.zzaeo();
                            zzaeo++;
                        }
                        obj[zzaeo] = new zzc();
                        com_google_android_gms_internal_zzbxl.zza(obj[zzaeo]);
                        this.zzbwY = obj;
                        continue;
                    case 18:
                        this.name = com_google_android_gms_internal_zzbxl.readString();
                        continue;
                    case 24:
                        this.zzbwZ = Long.valueOf(com_google_android_gms_internal_zzbxl.zzaer());
                        continue;
                    case 32:
                        this.zzbxa = Long.valueOf(com_google_android_gms_internal_zzbxl.zzaer());
                        continue;
                    case MotionEventCompat.AXIS_GENERIC_9 /*40*/:
                        this.count = Integer.valueOf(com_google_android_gms_internal_zzbxl.zzaes());
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

        public void zza(zzbxm com_google_android_gms_internal_zzbxm) throws IOException {
            if (this.zzbwY != null && this.zzbwY.length > 0) {
                for (zzbxt com_google_android_gms_internal_zzbxt : this.zzbwY) {
                    if (com_google_android_gms_internal_zzbxt != null) {
                        com_google_android_gms_internal_zzbxm.zza(1, com_google_android_gms_internal_zzbxt);
                    }
                }
            }
            if (this.name != null) {
                com_google_android_gms_internal_zzbxm.zzq(2, this.name);
            }
            if (this.zzbwZ != null) {
                com_google_android_gms_internal_zzbxm.zzb(3, this.zzbwZ.longValue());
            }
            if (this.zzbxa != null) {
                com_google_android_gms_internal_zzbxm.zzb(4, this.zzbxa.longValue());
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
            if (this.zzbwY != null && this.zzbwY.length > 0) {
                for (zzbxt com_google_android_gms_internal_zzbxt : this.zzbwY) {
                    if (com_google_android_gms_internal_zzbxt != null) {
                        zzu += zzbxm.zzc(1, com_google_android_gms_internal_zzbxt);
                    }
                }
            }
            if (this.name != null) {
                zzu += zzbxm.zzr(2, this.name);
            }
            if (this.zzbwZ != null) {
                zzu += zzbxm.zzf(3, this.zzbwZ.longValue());
            }
            if (this.zzbxa != null) {
                zzu += zzbxm.zzf(4, this.zzbxa.longValue());
            }
            return this.count != null ? zzu + zzbxm.zzL(5, this.count.intValue()) : zzu;
        }
    }

    public static final class zzc extends zzbxn<zzc> {
        private static volatile zzc[] zzbxb;
        public String name;
        public String zzaGV;
        public Float zzbwe;
        public Double zzbwf;
        public Long zzbxc;

        public zzc() {
            zzND();
        }

        public static zzc[] zzNC() {
            if (zzbxb == null) {
                synchronized (zzbxr.zzcuQ) {
                    if (zzbxb == null) {
                        zzbxb = new zzc[0];
                    }
                }
            }
            return zzbxb;
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
            if (this.zzbxc == null) {
                if (com_google_android_gms_internal_zzauw_zzc.zzbxc != null) {
                    return false;
                }
            } else if (!this.zzbxc.equals(com_google_android_gms_internal_zzauw_zzc.zzbxc)) {
                return false;
            }
            if (this.zzbwe == null) {
                if (com_google_android_gms_internal_zzauw_zzc.zzbwe != null) {
                    return false;
                }
            } else if (!this.zzbwe.equals(com_google_android_gms_internal_zzauw_zzc.zzbwe)) {
                return false;
            }
            if (this.zzbwf == null) {
                if (com_google_android_gms_internal_zzauw_zzc.zzbwf != null) {
                    return false;
                }
            } else if (!this.zzbwf.equals(com_google_android_gms_internal_zzauw_zzc.zzbwf)) {
                return false;
            }
            return (this.zzcuI == null || this.zzcuI.isEmpty()) ? com_google_android_gms_internal_zzauw_zzc.zzcuI == null || com_google_android_gms_internal_zzauw_zzc.zzcuI.isEmpty() : this.zzcuI.equals(com_google_android_gms_internal_zzauw_zzc.zzcuI);
        }

        public int hashCode() {
            int i = 0;
            int hashCode = ((this.zzbwf == null ? 0 : this.zzbwf.hashCode()) + (((this.zzbwe == null ? 0 : this.zzbwe.hashCode()) + (((this.zzbxc == null ? 0 : this.zzbxc.hashCode()) + (((this.zzaGV == null ? 0 : this.zzaGV.hashCode()) + (((this.name == null ? 0 : this.name.hashCode()) + ((getClass().getName().hashCode() + 527) * 31)) * 31)) * 31)) * 31)) * 31)) * 31;
            if (!(this.zzcuI == null || this.zzcuI.isEmpty())) {
                i = this.zzcuI.hashCode();
            }
            return hashCode + i;
        }

        public zzc zzND() {
            this.name = null;
            this.zzaGV = null;
            this.zzbxc = null;
            this.zzbwe = null;
            this.zzbwf = null;
            this.zzcuI = null;
            this.zzcuR = -1;
            return this;
        }

        public zzc zzR(zzbxl com_google_android_gms_internal_zzbxl) throws IOException {
            while (true) {
                int zzaeo = com_google_android_gms_internal_zzbxl.zzaeo();
                switch (zzaeo) {
                    case 0:
                        break;
                    case 10:
                        this.name = com_google_android_gms_internal_zzbxl.readString();
                        continue;
                    case 18:
                        this.zzaGV = com_google_android_gms_internal_zzbxl.readString();
                        continue;
                    case 24:
                        this.zzbxc = Long.valueOf(com_google_android_gms_internal_zzbxl.zzaer());
                        continue;
                    case 37:
                        this.zzbwe = Float.valueOf(com_google_android_gms_internal_zzbxl.readFloat());
                        continue;
                    case 41:
                        this.zzbwf = Double.valueOf(com_google_android_gms_internal_zzbxl.readDouble());
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

        public void zza(zzbxm com_google_android_gms_internal_zzbxm) throws IOException {
            if (this.name != null) {
                com_google_android_gms_internal_zzbxm.zzq(1, this.name);
            }
            if (this.zzaGV != null) {
                com_google_android_gms_internal_zzbxm.zzq(2, this.zzaGV);
            }
            if (this.zzbxc != null) {
                com_google_android_gms_internal_zzbxm.zzb(3, this.zzbxc.longValue());
            }
            if (this.zzbwe != null) {
                com_google_android_gms_internal_zzbxm.zzc(4, this.zzbwe.floatValue());
            }
            if (this.zzbwf != null) {
                com_google_android_gms_internal_zzbxm.zza(5, this.zzbwf.doubleValue());
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
            if (this.zzbxc != null) {
                zzu += zzbxm.zzf(3, this.zzbxc.longValue());
            }
            if (this.zzbwe != null) {
                zzu += zzbxm.zzd(4, this.zzbwe.floatValue());
            }
            return this.zzbwf != null ? zzu + zzbxm.zzb(5, this.zzbwf.doubleValue()) : zzu;
        }
    }

    public static final class zzd extends zzbxn<zzd> {
        public zze[] zzbxd;

        public zzd() {
            zzNE();
        }

        public boolean equals(Object obj) {
            if (obj == this) {
                return true;
            }
            if (!(obj instanceof zzd)) {
                return false;
            }
            zzd com_google_android_gms_internal_zzauw_zzd = (zzd) obj;
            return zzbxr.equals(this.zzbxd, com_google_android_gms_internal_zzauw_zzd.zzbxd) ? (this.zzcuI == null || this.zzcuI.isEmpty()) ? com_google_android_gms_internal_zzauw_zzd.zzcuI == null || com_google_android_gms_internal_zzauw_zzd.zzcuI.isEmpty() : this.zzcuI.equals(com_google_android_gms_internal_zzauw_zzd.zzcuI) : false;
        }

        public int hashCode() {
            int hashCode = (((getClass().getName().hashCode() + 527) * 31) + zzbxr.hashCode(this.zzbxd)) * 31;
            int hashCode2 = (this.zzcuI == null || this.zzcuI.isEmpty()) ? 0 : this.zzcuI.hashCode();
            return hashCode2 + hashCode;
        }

        public zzd zzNE() {
            this.zzbxd = zze.zzNF();
            this.zzcuI = null;
            this.zzcuR = -1;
            return this;
        }

        public zzd zzS(zzbxl com_google_android_gms_internal_zzbxl) throws IOException {
            while (true) {
                int zzaeo = com_google_android_gms_internal_zzbxl.zzaeo();
                switch (zzaeo) {
                    case 0:
                        break;
                    case 10:
                        int zzb = zzbxw.zzb(com_google_android_gms_internal_zzbxl, 10);
                        zzaeo = this.zzbxd == null ? 0 : this.zzbxd.length;
                        Object obj = new zze[(zzb + zzaeo)];
                        if (zzaeo != 0) {
                            System.arraycopy(this.zzbxd, 0, obj, 0, zzaeo);
                        }
                        while (zzaeo < obj.length - 1) {
                            obj[zzaeo] = new zze();
                            com_google_android_gms_internal_zzbxl.zza(obj[zzaeo]);
                            com_google_android_gms_internal_zzbxl.zzaeo();
                            zzaeo++;
                        }
                        obj[zzaeo] = new zze();
                        com_google_android_gms_internal_zzbxl.zza(obj[zzaeo]);
                        this.zzbxd = obj;
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

        public void zza(zzbxm com_google_android_gms_internal_zzbxm) throws IOException {
            if (this.zzbxd != null && this.zzbxd.length > 0) {
                for (zzbxt com_google_android_gms_internal_zzbxt : this.zzbxd) {
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
            if (this.zzbxd != null && this.zzbxd.length > 0) {
                for (zzbxt com_google_android_gms_internal_zzbxt : this.zzbxd) {
                    if (com_google_android_gms_internal_zzbxt != null) {
                        zzu += zzbxm.zzc(1, com_google_android_gms_internal_zzbxt);
                    }
                }
            }
            return zzu;
        }
    }

    public static final class zze extends zzbxn<zze> {
        private static volatile zze[] zzbxe;
        public String zzaS;
        public String zzbb;
        public String zzbhN;
        public String zzbqL;
        public String zzbqM;
        public String zzbqP;
        public String zzbqT;
        public Integer zzbxA;
        public Integer zzbxB;
        public Integer zzbxC;
        public String zzbxD;
        public Long zzbxE;
        public Long zzbxF;
        public Integer zzbxf;
        public zzb[] zzbxg;
        public zzg[] zzbxh;
        public Long zzbxi;
        public Long zzbxj;
        public Long zzbxk;
        public Long zzbxl;
        public Long zzbxm;
        public String zzbxn;
        public String zzbxo;
        public String zzbxp;
        public Integer zzbxq;
        public Long zzbxr;
        public Long zzbxs;
        public String zzbxt;
        public Boolean zzbxu;
        public String zzbxv;
        public Long zzbxw;
        public Integer zzbxx;
        public Boolean zzbxy;
        public zza[] zzbxz;

        public zze() {
            zzNG();
        }

        public static zze[] zzNF() {
            if (zzbxe == null) {
                synchronized (zzbxr.zzcuQ) {
                    if (zzbxe == null) {
                        zzbxe = new zze[0];
                    }
                }
            }
            return zzbxe;
        }

        public boolean equals(Object obj) {
            if (obj == this) {
                return true;
            }
            if (!(obj instanceof zze)) {
                return false;
            }
            zze com_google_android_gms_internal_zzauw_zze = (zze) obj;
            if (this.zzbxf == null) {
                if (com_google_android_gms_internal_zzauw_zze.zzbxf != null) {
                    return false;
                }
            } else if (!this.zzbxf.equals(com_google_android_gms_internal_zzauw_zze.zzbxf)) {
                return false;
            }
            if (!zzbxr.equals(this.zzbxg, com_google_android_gms_internal_zzauw_zze.zzbxg) || !zzbxr.equals(this.zzbxh, com_google_android_gms_internal_zzauw_zze.zzbxh)) {
                return false;
            }
            if (this.zzbxi == null) {
                if (com_google_android_gms_internal_zzauw_zze.zzbxi != null) {
                    return false;
                }
            } else if (!this.zzbxi.equals(com_google_android_gms_internal_zzauw_zze.zzbxi)) {
                return false;
            }
            if (this.zzbxj == null) {
                if (com_google_android_gms_internal_zzauw_zze.zzbxj != null) {
                    return false;
                }
            } else if (!this.zzbxj.equals(com_google_android_gms_internal_zzauw_zze.zzbxj)) {
                return false;
            }
            if (this.zzbxk == null) {
                if (com_google_android_gms_internal_zzauw_zze.zzbxk != null) {
                    return false;
                }
            } else if (!this.zzbxk.equals(com_google_android_gms_internal_zzauw_zze.zzbxk)) {
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
            if (this.zzbb == null) {
                if (com_google_android_gms_internal_zzauw_zze.zzbb != null) {
                    return false;
                }
            } else if (!this.zzbb.equals(com_google_android_gms_internal_zzauw_zze.zzbb)) {
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
            if (this.zzbqM == null) {
                if (com_google_android_gms_internal_zzauw_zze.zzbqM != null) {
                    return false;
                }
            } else if (!this.zzbqM.equals(com_google_android_gms_internal_zzauw_zze.zzbqM)) {
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
            if (this.zzbqP == null) {
                if (com_google_android_gms_internal_zzauw_zze.zzbqP != null) {
                    return false;
                }
            } else if (!this.zzbqP.equals(com_google_android_gms_internal_zzauw_zze.zzbqP)) {
                return false;
            }
            if (this.zzbqL == null) {
                if (com_google_android_gms_internal_zzauw_zze.zzbqL != null) {
                    return false;
                }
            } else if (!this.zzbqL.equals(com_google_android_gms_internal_zzauw_zze.zzbqL)) {
                return false;
            }
            if (this.zzbxy == null) {
                if (com_google_android_gms_internal_zzauw_zze.zzbxy != null) {
                    return false;
                }
            } else if (!this.zzbxy.equals(com_google_android_gms_internal_zzauw_zze.zzbxy)) {
                return false;
            }
            if (!zzbxr.equals(this.zzbxz, com_google_android_gms_internal_zzauw_zze.zzbxz)) {
                return false;
            }
            if (this.zzbqT == null) {
                if (com_google_android_gms_internal_zzauw_zze.zzbqT != null) {
                    return false;
                }
            } else if (!this.zzbqT.equals(com_google_android_gms_internal_zzauw_zze.zzbqT)) {
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
            if (this.zzbxC == null) {
                if (com_google_android_gms_internal_zzauw_zze.zzbxC != null) {
                    return false;
                }
            } else if (!this.zzbxC.equals(com_google_android_gms_internal_zzauw_zze.zzbxC)) {
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
            return (this.zzcuI == null || this.zzcuI.isEmpty()) ? com_google_android_gms_internal_zzauw_zze.zzcuI == null || com_google_android_gms_internal_zzauw_zze.zzcuI.isEmpty() : this.zzcuI.equals(com_google_android_gms_internal_zzauw_zze.zzcuI);
        }

        public int hashCode() {
            int i = 0;
            int hashCode = ((this.zzbxF == null ? 0 : this.zzbxF.hashCode()) + (((this.zzbxE == null ? 0 : this.zzbxE.hashCode()) + (((this.zzbxD == null ? 0 : this.zzbxD.hashCode()) + (((this.zzbxC == null ? 0 : this.zzbxC.hashCode()) + (((this.zzbxB == null ? 0 : this.zzbxB.hashCode()) + (((this.zzbxA == null ? 0 : this.zzbxA.hashCode()) + (((this.zzbqT == null ? 0 : this.zzbqT.hashCode()) + (((((this.zzbxy == null ? 0 : this.zzbxy.hashCode()) + (((this.zzbqL == null ? 0 : this.zzbqL.hashCode()) + (((this.zzbqP == null ? 0 : this.zzbqP.hashCode()) + (((this.zzbxx == null ? 0 : this.zzbxx.hashCode()) + (((this.zzbxw == null ? 0 : this.zzbxw.hashCode()) + (((this.zzbxv == null ? 0 : this.zzbxv.hashCode()) + (((this.zzbxu == null ? 0 : this.zzbxu.hashCode()) + (((this.zzbxt == null ? 0 : this.zzbxt.hashCode()) + (((this.zzbxs == null ? 0 : this.zzbxs.hashCode()) + (((this.zzbxr == null ? 0 : this.zzbxr.hashCode()) + (((this.zzbhN == null ? 0 : this.zzbhN.hashCode()) + (((this.zzaS == null ? 0 : this.zzaS.hashCode()) + (((this.zzbqM == null ? 0 : this.zzbqM.hashCode()) + (((this.zzbxq == null ? 0 : this.zzbxq.hashCode()) + (((this.zzbxp == null ? 0 : this.zzbxp.hashCode()) + (((this.zzbxo == null ? 0 : this.zzbxo.hashCode()) + (((this.zzbb == null ? 0 : this.zzbb.hashCode()) + (((this.zzbxn == null ? 0 : this.zzbxn.hashCode()) + (((this.zzbxm == null ? 0 : this.zzbxm.hashCode()) + (((this.zzbxl == null ? 0 : this.zzbxl.hashCode()) + (((this.zzbxk == null ? 0 : this.zzbxk.hashCode()) + (((this.zzbxj == null ? 0 : this.zzbxj.hashCode()) + (((this.zzbxi == null ? 0 : this.zzbxi.hashCode()) + (((((((this.zzbxf == null ? 0 : this.zzbxf.hashCode()) + ((getClass().getName().hashCode() + 527) * 31)) * 31) + zzbxr.hashCode(this.zzbxg)) * 31) + zzbxr.hashCode(this.zzbxh)) * 31)) * 31)) * 31)) * 31)) * 31)) * 31)) * 31)) * 31)) * 31)) * 31)) * 31)) * 31)) * 31)) * 31)) * 31)) * 31)) * 31)) * 31)) * 31)) * 31)) * 31)) * 31)) * 31)) * 31) + zzbxr.hashCode(this.zzbxz)) * 31)) * 31)) * 31)) * 31)) * 31)) * 31)) * 31)) * 31;
            if (!(this.zzcuI == null || this.zzcuI.isEmpty())) {
                i = this.zzcuI.hashCode();
            }
            return hashCode + i;
        }

        public zze zzNG() {
            this.zzbxf = null;
            this.zzbxg = zzb.zzNA();
            this.zzbxh = zzg.zzNI();
            this.zzbxi = null;
            this.zzbxj = null;
            this.zzbxk = null;
            this.zzbxl = null;
            this.zzbxm = null;
            this.zzbxn = null;
            this.zzbb = null;
            this.zzbxo = null;
            this.zzbxp = null;
            this.zzbxq = null;
            this.zzbqM = null;
            this.zzaS = null;
            this.zzbhN = null;
            this.zzbxr = null;
            this.zzbxs = null;
            this.zzbxt = null;
            this.zzbxu = null;
            this.zzbxv = null;
            this.zzbxw = null;
            this.zzbxx = null;
            this.zzbqP = null;
            this.zzbqL = null;
            this.zzbxy = null;
            this.zzbxz = zza.zzNy();
            this.zzbqT = null;
            this.zzbxA = null;
            this.zzbxB = null;
            this.zzbxC = null;
            this.zzbxD = null;
            this.zzbxE = null;
            this.zzbxF = null;
            this.zzcuI = null;
            this.zzcuR = -1;
            return this;
        }

        public zze zzT(zzbxl com_google_android_gms_internal_zzbxl) throws IOException {
            while (true) {
                int zzaeo = com_google_android_gms_internal_zzbxl.zzaeo();
                int zzb;
                Object obj;
                switch (zzaeo) {
                    case 0:
                        break;
                    case 8:
                        this.zzbxf = Integer.valueOf(com_google_android_gms_internal_zzbxl.zzaes());
                        continue;
                    case 18:
                        zzb = zzbxw.zzb(com_google_android_gms_internal_zzbxl, 18);
                        zzaeo = this.zzbxg == null ? 0 : this.zzbxg.length;
                        obj = new zzb[(zzb + zzaeo)];
                        if (zzaeo != 0) {
                            System.arraycopy(this.zzbxg, 0, obj, 0, zzaeo);
                        }
                        while (zzaeo < obj.length - 1) {
                            obj[zzaeo] = new zzb();
                            com_google_android_gms_internal_zzbxl.zza(obj[zzaeo]);
                            com_google_android_gms_internal_zzbxl.zzaeo();
                            zzaeo++;
                        }
                        obj[zzaeo] = new zzb();
                        com_google_android_gms_internal_zzbxl.zza(obj[zzaeo]);
                        this.zzbxg = obj;
                        continue;
                    case NalUnitTypes.NAL_TYPE_RSV_VCL26 /*26*/:
                        zzb = zzbxw.zzb(com_google_android_gms_internal_zzbxl, 26);
                        zzaeo = this.zzbxh == null ? 0 : this.zzbxh.length;
                        obj = new zzg[(zzb + zzaeo)];
                        if (zzaeo != 0) {
                            System.arraycopy(this.zzbxh, 0, obj, 0, zzaeo);
                        }
                        while (zzaeo < obj.length - 1) {
                            obj[zzaeo] = new zzg();
                            com_google_android_gms_internal_zzbxl.zza(obj[zzaeo]);
                            com_google_android_gms_internal_zzbxl.zzaeo();
                            zzaeo++;
                        }
                        obj[zzaeo] = new zzg();
                        com_google_android_gms_internal_zzbxl.zza(obj[zzaeo]);
                        this.zzbxh = obj;
                        continue;
                    case 32:
                        this.zzbxi = Long.valueOf(com_google_android_gms_internal_zzbxl.zzaer());
                        continue;
                    case MotionEventCompat.AXIS_GENERIC_9 /*40*/:
                        this.zzbxj = Long.valueOf(com_google_android_gms_internal_zzbxl.zzaer());
                        continue;
                    case 48:
                        this.zzbxk = Long.valueOf(com_google_android_gms_internal_zzbxl.zzaer());
                        continue;
                    case 56:
                        this.zzbxm = Long.valueOf(com_google_android_gms_internal_zzbxl.zzaer());
                        continue;
                    case 66:
                        this.zzbxn = com_google_android_gms_internal_zzbxl.readString();
                        continue;
                    case 74:
                        this.zzbb = com_google_android_gms_internal_zzbxl.readString();
                        continue;
                    case 82:
                        this.zzbxo = com_google_android_gms_internal_zzbxl.readString();
                        continue;
                    case 90:
                        this.zzbxp = com_google_android_gms_internal_zzbxl.readString();
                        continue;
                    case 96:
                        this.zzbxq = Integer.valueOf(com_google_android_gms_internal_zzbxl.zzaes());
                        continue;
                    case 106:
                        this.zzbqM = com_google_android_gms_internal_zzbxl.readString();
                        continue;
                    case 114:
                        this.zzaS = com_google_android_gms_internal_zzbxl.readString();
                        continue;
                    case TsExtractor.TS_STREAM_TYPE_HDMV_DTS /*130*/:
                        this.zzbhN = com_google_android_gms_internal_zzbxl.readString();
                        continue;
                    case 136:
                        this.zzbxr = Long.valueOf(com_google_android_gms_internal_zzbxl.zzaer());
                        continue;
                    case 144:
                        this.zzbxs = Long.valueOf(com_google_android_gms_internal_zzbxl.zzaer());
                        continue;
                    case 154:
                        this.zzbxt = com_google_android_gms_internal_zzbxl.readString();
                        continue;
                    case 160:
                        this.zzbxu = Boolean.valueOf(com_google_android_gms_internal_zzbxl.zzaeu());
                        continue;
                    case 170:
                        this.zzbxv = com_google_android_gms_internal_zzbxl.readString();
                        continue;
                    case 176:
                        this.zzbxw = Long.valueOf(com_google_android_gms_internal_zzbxl.zzaer());
                        continue;
                    case 184:
                        this.zzbxx = Integer.valueOf(com_google_android_gms_internal_zzbxl.zzaes());
                        continue;
                    case 194:
                        this.zzbqP = com_google_android_gms_internal_zzbxl.readString();
                        continue;
                    case 202:
                        this.zzbqL = com_google_android_gms_internal_zzbxl.readString();
                        continue;
                    case 208:
                        this.zzbxl = Long.valueOf(com_google_android_gms_internal_zzbxl.zzaer());
                        continue;
                    case 224:
                        this.zzbxy = Boolean.valueOf(com_google_android_gms_internal_zzbxl.zzaeu());
                        continue;
                    case 234:
                        zzb = zzbxw.zzb(com_google_android_gms_internal_zzbxl, 234);
                        zzaeo = this.zzbxz == null ? 0 : this.zzbxz.length;
                        obj = new zza[(zzb + zzaeo)];
                        if (zzaeo != 0) {
                            System.arraycopy(this.zzbxz, 0, obj, 0, zzaeo);
                        }
                        while (zzaeo < obj.length - 1) {
                            obj[zzaeo] = new zza();
                            com_google_android_gms_internal_zzbxl.zza(obj[zzaeo]);
                            com_google_android_gms_internal_zzbxl.zzaeo();
                            zzaeo++;
                        }
                        obj[zzaeo] = new zza();
                        com_google_android_gms_internal_zzbxl.zza(obj[zzaeo]);
                        this.zzbxz = obj;
                        continue;
                    case 242:
                        this.zzbqT = com_google_android_gms_internal_zzbxl.readString();
                        continue;
                    case 248:
                        this.zzbxA = Integer.valueOf(com_google_android_gms_internal_zzbxl.zzaes());
                        continue;
                    case 256:
                        this.zzbxB = Integer.valueOf(com_google_android_gms_internal_zzbxl.zzaes());
                        continue;
                    case 264:
                        this.zzbxC = Integer.valueOf(com_google_android_gms_internal_zzbxl.zzaes());
                        continue;
                    case 274:
                        this.zzbxD = com_google_android_gms_internal_zzbxl.readString();
                        continue;
                    case 280:
                        this.zzbxE = Long.valueOf(com_google_android_gms_internal_zzbxl.zzaer());
                        continue;
                    case 288:
                        this.zzbxF = Long.valueOf(com_google_android_gms_internal_zzbxl.zzaer());
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

        public void zza(zzbxm com_google_android_gms_internal_zzbxm) throws IOException {
            int i = 0;
            if (this.zzbxf != null) {
                com_google_android_gms_internal_zzbxm.zzJ(1, this.zzbxf.intValue());
            }
            if (this.zzbxg != null && this.zzbxg.length > 0) {
                for (zzbxt com_google_android_gms_internal_zzbxt : this.zzbxg) {
                    if (com_google_android_gms_internal_zzbxt != null) {
                        com_google_android_gms_internal_zzbxm.zza(2, com_google_android_gms_internal_zzbxt);
                    }
                }
            }
            if (this.zzbxh != null && this.zzbxh.length > 0) {
                for (zzbxt com_google_android_gms_internal_zzbxt2 : this.zzbxh) {
                    if (com_google_android_gms_internal_zzbxt2 != null) {
                        com_google_android_gms_internal_zzbxm.zza(3, com_google_android_gms_internal_zzbxt2);
                    }
                }
            }
            if (this.zzbxi != null) {
                com_google_android_gms_internal_zzbxm.zzb(4, this.zzbxi.longValue());
            }
            if (this.zzbxj != null) {
                com_google_android_gms_internal_zzbxm.zzb(5, this.zzbxj.longValue());
            }
            if (this.zzbxk != null) {
                com_google_android_gms_internal_zzbxm.zzb(6, this.zzbxk.longValue());
            }
            if (this.zzbxm != null) {
                com_google_android_gms_internal_zzbxm.zzb(7, this.zzbxm.longValue());
            }
            if (this.zzbxn != null) {
                com_google_android_gms_internal_zzbxm.zzq(8, this.zzbxn);
            }
            if (this.zzbb != null) {
                com_google_android_gms_internal_zzbxm.zzq(9, this.zzbb);
            }
            if (this.zzbxo != null) {
                com_google_android_gms_internal_zzbxm.zzq(10, this.zzbxo);
            }
            if (this.zzbxp != null) {
                com_google_android_gms_internal_zzbxm.zzq(11, this.zzbxp);
            }
            if (this.zzbxq != null) {
                com_google_android_gms_internal_zzbxm.zzJ(12, this.zzbxq.intValue());
            }
            if (this.zzbqM != null) {
                com_google_android_gms_internal_zzbxm.zzq(13, this.zzbqM);
            }
            if (this.zzaS != null) {
                com_google_android_gms_internal_zzbxm.zzq(14, this.zzaS);
            }
            if (this.zzbhN != null) {
                com_google_android_gms_internal_zzbxm.zzq(16, this.zzbhN);
            }
            if (this.zzbxr != null) {
                com_google_android_gms_internal_zzbxm.zzb(17, this.zzbxr.longValue());
            }
            if (this.zzbxs != null) {
                com_google_android_gms_internal_zzbxm.zzb(18, this.zzbxs.longValue());
            }
            if (this.zzbxt != null) {
                com_google_android_gms_internal_zzbxm.zzq(19, this.zzbxt);
            }
            if (this.zzbxu != null) {
                com_google_android_gms_internal_zzbxm.zzg(20, this.zzbxu.booleanValue());
            }
            if (this.zzbxv != null) {
                com_google_android_gms_internal_zzbxm.zzq(21, this.zzbxv);
            }
            if (this.zzbxw != null) {
                com_google_android_gms_internal_zzbxm.zzb(22, this.zzbxw.longValue());
            }
            if (this.zzbxx != null) {
                com_google_android_gms_internal_zzbxm.zzJ(23, this.zzbxx.intValue());
            }
            if (this.zzbqP != null) {
                com_google_android_gms_internal_zzbxm.zzq(24, this.zzbqP);
            }
            if (this.zzbqL != null) {
                com_google_android_gms_internal_zzbxm.zzq(25, this.zzbqL);
            }
            if (this.zzbxl != null) {
                com_google_android_gms_internal_zzbxm.zzb(26, this.zzbxl.longValue());
            }
            if (this.zzbxy != null) {
                com_google_android_gms_internal_zzbxm.zzg(28, this.zzbxy.booleanValue());
            }
            if (this.zzbxz != null && this.zzbxz.length > 0) {
                while (i < this.zzbxz.length) {
                    zzbxt com_google_android_gms_internal_zzbxt3 = this.zzbxz[i];
                    if (com_google_android_gms_internal_zzbxt3 != null) {
                        com_google_android_gms_internal_zzbxm.zza(29, com_google_android_gms_internal_zzbxt3);
                    }
                    i++;
                }
            }
            if (this.zzbqT != null) {
                com_google_android_gms_internal_zzbxm.zzq(30, this.zzbqT);
            }
            if (this.zzbxA != null) {
                com_google_android_gms_internal_zzbxm.zzJ(31, this.zzbxA.intValue());
            }
            if (this.zzbxB != null) {
                com_google_android_gms_internal_zzbxm.zzJ(32, this.zzbxB.intValue());
            }
            if (this.zzbxC != null) {
                com_google_android_gms_internal_zzbxm.zzJ(33, this.zzbxC.intValue());
            }
            if (this.zzbxD != null) {
                com_google_android_gms_internal_zzbxm.zzq(34, this.zzbxD);
            }
            if (this.zzbxE != null) {
                com_google_android_gms_internal_zzbxm.zzb(35, this.zzbxE.longValue());
            }
            if (this.zzbxF != null) {
                com_google_android_gms_internal_zzbxm.zzb(36, this.zzbxF.longValue());
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
            if (this.zzbxf != null) {
                zzu += zzbxm.zzL(1, this.zzbxf.intValue());
            }
            if (this.zzbxg != null && this.zzbxg.length > 0) {
                i = zzu;
                for (zzbxt com_google_android_gms_internal_zzbxt : this.zzbxg) {
                    if (com_google_android_gms_internal_zzbxt != null) {
                        i += zzbxm.zzc(2, com_google_android_gms_internal_zzbxt);
                    }
                }
                zzu = i;
            }
            if (this.zzbxh != null && this.zzbxh.length > 0) {
                i = zzu;
                for (zzbxt com_google_android_gms_internal_zzbxt2 : this.zzbxh) {
                    if (com_google_android_gms_internal_zzbxt2 != null) {
                        i += zzbxm.zzc(3, com_google_android_gms_internal_zzbxt2);
                    }
                }
                zzu = i;
            }
            if (this.zzbxi != null) {
                zzu += zzbxm.zzf(4, this.zzbxi.longValue());
            }
            if (this.zzbxj != null) {
                zzu += zzbxm.zzf(5, this.zzbxj.longValue());
            }
            if (this.zzbxk != null) {
                zzu += zzbxm.zzf(6, this.zzbxk.longValue());
            }
            if (this.zzbxm != null) {
                zzu += zzbxm.zzf(7, this.zzbxm.longValue());
            }
            if (this.zzbxn != null) {
                zzu += zzbxm.zzr(8, this.zzbxn);
            }
            if (this.zzbb != null) {
                zzu += zzbxm.zzr(9, this.zzbb);
            }
            if (this.zzbxo != null) {
                zzu += zzbxm.zzr(10, this.zzbxo);
            }
            if (this.zzbxp != null) {
                zzu += zzbxm.zzr(11, this.zzbxp);
            }
            if (this.zzbxq != null) {
                zzu += zzbxm.zzL(12, this.zzbxq.intValue());
            }
            if (this.zzbqM != null) {
                zzu += zzbxm.zzr(13, this.zzbqM);
            }
            if (this.zzaS != null) {
                zzu += zzbxm.zzr(14, this.zzaS);
            }
            if (this.zzbhN != null) {
                zzu += zzbxm.zzr(16, this.zzbhN);
            }
            if (this.zzbxr != null) {
                zzu += zzbxm.zzf(17, this.zzbxr.longValue());
            }
            if (this.zzbxs != null) {
                zzu += zzbxm.zzf(18, this.zzbxs.longValue());
            }
            if (this.zzbxt != null) {
                zzu += zzbxm.zzr(19, this.zzbxt);
            }
            if (this.zzbxu != null) {
                zzu += zzbxm.zzh(20, this.zzbxu.booleanValue());
            }
            if (this.zzbxv != null) {
                zzu += zzbxm.zzr(21, this.zzbxv);
            }
            if (this.zzbxw != null) {
                zzu += zzbxm.zzf(22, this.zzbxw.longValue());
            }
            if (this.zzbxx != null) {
                zzu += zzbxm.zzL(23, this.zzbxx.intValue());
            }
            if (this.zzbqP != null) {
                zzu += zzbxm.zzr(24, this.zzbqP);
            }
            if (this.zzbqL != null) {
                zzu += zzbxm.zzr(25, this.zzbqL);
            }
            if (this.zzbxl != null) {
                zzu += zzbxm.zzf(26, this.zzbxl.longValue());
            }
            if (this.zzbxy != null) {
                zzu += zzbxm.zzh(28, this.zzbxy.booleanValue());
            }
            if (this.zzbxz != null && this.zzbxz.length > 0) {
                while (i2 < this.zzbxz.length) {
                    zzbxt com_google_android_gms_internal_zzbxt3 = this.zzbxz[i2];
                    if (com_google_android_gms_internal_zzbxt3 != null) {
                        zzu += zzbxm.zzc(29, com_google_android_gms_internal_zzbxt3);
                    }
                    i2++;
                }
            }
            if (this.zzbqT != null) {
                zzu += zzbxm.zzr(30, this.zzbqT);
            }
            if (this.zzbxA != null) {
                zzu += zzbxm.zzL(31, this.zzbxA.intValue());
            }
            if (this.zzbxB != null) {
                zzu += zzbxm.zzL(32, this.zzbxB.intValue());
            }
            if (this.zzbxC != null) {
                zzu += zzbxm.zzL(33, this.zzbxC.intValue());
            }
            if (this.zzbxD != null) {
                zzu += zzbxm.zzr(34, this.zzbxD);
            }
            if (this.zzbxE != null) {
                zzu += zzbxm.zzf(35, this.zzbxE.longValue());
            }
            return this.zzbxF != null ? zzu + zzbxm.zzf(36, this.zzbxF.longValue()) : zzu;
        }
    }

    public static final class zzf extends zzbxn<zzf> {
        public long[] zzbxG;
        public long[] zzbxH;

        public zzf() {
            zzNH();
        }

        public boolean equals(Object obj) {
            if (obj == this) {
                return true;
            }
            if (!(obj instanceof zzf)) {
                return false;
            }
            zzf com_google_android_gms_internal_zzauw_zzf = (zzf) obj;
            return (zzbxr.equals(this.zzbxG, com_google_android_gms_internal_zzauw_zzf.zzbxG) && zzbxr.equals(this.zzbxH, com_google_android_gms_internal_zzauw_zzf.zzbxH)) ? (this.zzcuI == null || this.zzcuI.isEmpty()) ? com_google_android_gms_internal_zzauw_zzf.zzcuI == null || com_google_android_gms_internal_zzauw_zzf.zzcuI.isEmpty() : this.zzcuI.equals(com_google_android_gms_internal_zzauw_zzf.zzcuI) : false;
        }

        public int hashCode() {
            int hashCode = (((((getClass().getName().hashCode() + 527) * 31) + zzbxr.hashCode(this.zzbxG)) * 31) + zzbxr.hashCode(this.zzbxH)) * 31;
            int hashCode2 = (this.zzcuI == null || this.zzcuI.isEmpty()) ? 0 : this.zzcuI.hashCode();
            return hashCode2 + hashCode;
        }

        public zzf zzNH() {
            this.zzbxG = zzbxw.zzcuX;
            this.zzbxH = zzbxw.zzcuX;
            this.zzcuI = null;
            this.zzcuR = -1;
            return this;
        }

        public zzf zzU(zzbxl com_google_android_gms_internal_zzbxl) throws IOException {
            while (true) {
                int zzaeo = com_google_android_gms_internal_zzbxl.zzaeo();
                int zzb;
                Object obj;
                int zzra;
                Object obj2;
                switch (zzaeo) {
                    case 0:
                        break;
                    case 8:
                        zzb = zzbxw.zzb(com_google_android_gms_internal_zzbxl, 8);
                        zzaeo = this.zzbxG == null ? 0 : this.zzbxG.length;
                        obj = new long[(zzb + zzaeo)];
                        if (zzaeo != 0) {
                            System.arraycopy(this.zzbxG, 0, obj, 0, zzaeo);
                        }
                        while (zzaeo < obj.length - 1) {
                            obj[zzaeo] = com_google_android_gms_internal_zzbxl.zzaeq();
                            com_google_android_gms_internal_zzbxl.zzaeo();
                            zzaeo++;
                        }
                        obj[zzaeo] = com_google_android_gms_internal_zzbxl.zzaeq();
                        this.zzbxG = obj;
                        continue;
                    case 10:
                        zzra = com_google_android_gms_internal_zzbxl.zzra(com_google_android_gms_internal_zzbxl.zzaex());
                        zzb = com_google_android_gms_internal_zzbxl.getPosition();
                        zzaeo = 0;
                        while (com_google_android_gms_internal_zzbxl.zzaeC() > 0) {
                            com_google_android_gms_internal_zzbxl.zzaeq();
                            zzaeo++;
                        }
                        com_google_android_gms_internal_zzbxl.zzrc(zzb);
                        zzb = this.zzbxG == null ? 0 : this.zzbxG.length;
                        obj2 = new long[(zzaeo + zzb)];
                        if (zzb != 0) {
                            System.arraycopy(this.zzbxG, 0, obj2, 0, zzb);
                        }
                        while (zzb < obj2.length) {
                            obj2[zzb] = com_google_android_gms_internal_zzbxl.zzaeq();
                            zzb++;
                        }
                        this.zzbxG = obj2;
                        com_google_android_gms_internal_zzbxl.zzrb(zzra);
                        continue;
                    case 16:
                        zzb = zzbxw.zzb(com_google_android_gms_internal_zzbxl, 16);
                        zzaeo = this.zzbxH == null ? 0 : this.zzbxH.length;
                        obj = new long[(zzb + zzaeo)];
                        if (zzaeo != 0) {
                            System.arraycopy(this.zzbxH, 0, obj, 0, zzaeo);
                        }
                        while (zzaeo < obj.length - 1) {
                            obj[zzaeo] = com_google_android_gms_internal_zzbxl.zzaeq();
                            com_google_android_gms_internal_zzbxl.zzaeo();
                            zzaeo++;
                        }
                        obj[zzaeo] = com_google_android_gms_internal_zzbxl.zzaeq();
                        this.zzbxH = obj;
                        continue;
                    case 18:
                        zzra = com_google_android_gms_internal_zzbxl.zzra(com_google_android_gms_internal_zzbxl.zzaex());
                        zzb = com_google_android_gms_internal_zzbxl.getPosition();
                        zzaeo = 0;
                        while (com_google_android_gms_internal_zzbxl.zzaeC() > 0) {
                            com_google_android_gms_internal_zzbxl.zzaeq();
                            zzaeo++;
                        }
                        com_google_android_gms_internal_zzbxl.zzrc(zzb);
                        zzb = this.zzbxH == null ? 0 : this.zzbxH.length;
                        obj2 = new long[(zzaeo + zzb)];
                        if (zzb != 0) {
                            System.arraycopy(this.zzbxH, 0, obj2, 0, zzb);
                        }
                        while (zzb < obj2.length) {
                            obj2[zzb] = com_google_android_gms_internal_zzbxl.zzaeq();
                            zzb++;
                        }
                        this.zzbxH = obj2;
                        com_google_android_gms_internal_zzbxl.zzrb(zzra);
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

        public void zza(zzbxm com_google_android_gms_internal_zzbxm) throws IOException {
            int i = 0;
            if (this.zzbxG != null && this.zzbxG.length > 0) {
                for (long zza : this.zzbxG) {
                    com_google_android_gms_internal_zzbxm.zza(1, zza);
                }
            }
            if (this.zzbxH != null && this.zzbxH.length > 0) {
                while (i < this.zzbxH.length) {
                    com_google_android_gms_internal_zzbxm.zza(2, this.zzbxH[i]);
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
            if (this.zzbxG == null || this.zzbxG.length <= 0) {
                i = zzu;
            } else {
                i2 = 0;
                for (long zzbe : this.zzbxG) {
                    i2 += zzbxm.zzbe(zzbe);
                }
                i = (zzu + i2) + (this.zzbxG.length * 1);
            }
            if (this.zzbxH == null || this.zzbxH.length <= 0) {
                return i;
            }
            i2 = 0;
            while (i3 < this.zzbxH.length) {
                i2 += zzbxm.zzbe(this.zzbxH[i3]);
                i3++;
            }
            return (i + i2) + (this.zzbxH.length * 1);
        }
    }

    public static final class zzg extends zzbxn<zzg> {
        private static volatile zzg[] zzbxI;
        public String name;
        public String zzaGV;
        public Float zzbwe;
        public Double zzbwf;
        public Long zzbxJ;
        public Long zzbxc;

        public zzg() {
            zzNJ();
        }

        public static zzg[] zzNI() {
            if (zzbxI == null) {
                synchronized (zzbxr.zzcuQ) {
                    if (zzbxI == null) {
                        zzbxI = new zzg[0];
                    }
                }
            }
            return zzbxI;
        }

        public boolean equals(Object obj) {
            if (obj == this) {
                return true;
            }
            if (!(obj instanceof zzg)) {
                return false;
            }
            zzg com_google_android_gms_internal_zzauw_zzg = (zzg) obj;
            if (this.zzbxJ == null) {
                if (com_google_android_gms_internal_zzauw_zzg.zzbxJ != null) {
                    return false;
                }
            } else if (!this.zzbxJ.equals(com_google_android_gms_internal_zzauw_zzg.zzbxJ)) {
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
            if (this.zzbxc == null) {
                if (com_google_android_gms_internal_zzauw_zzg.zzbxc != null) {
                    return false;
                }
            } else if (!this.zzbxc.equals(com_google_android_gms_internal_zzauw_zzg.zzbxc)) {
                return false;
            }
            if (this.zzbwe == null) {
                if (com_google_android_gms_internal_zzauw_zzg.zzbwe != null) {
                    return false;
                }
            } else if (!this.zzbwe.equals(com_google_android_gms_internal_zzauw_zzg.zzbwe)) {
                return false;
            }
            if (this.zzbwf == null) {
                if (com_google_android_gms_internal_zzauw_zzg.zzbwf != null) {
                    return false;
                }
            } else if (!this.zzbwf.equals(com_google_android_gms_internal_zzauw_zzg.zzbwf)) {
                return false;
            }
            return (this.zzcuI == null || this.zzcuI.isEmpty()) ? com_google_android_gms_internal_zzauw_zzg.zzcuI == null || com_google_android_gms_internal_zzauw_zzg.zzcuI.isEmpty() : this.zzcuI.equals(com_google_android_gms_internal_zzauw_zzg.zzcuI);
        }

        public int hashCode() {
            int i = 0;
            int hashCode = ((this.zzbwf == null ? 0 : this.zzbwf.hashCode()) + (((this.zzbwe == null ? 0 : this.zzbwe.hashCode()) + (((this.zzbxc == null ? 0 : this.zzbxc.hashCode()) + (((this.zzaGV == null ? 0 : this.zzaGV.hashCode()) + (((this.name == null ? 0 : this.name.hashCode()) + (((this.zzbxJ == null ? 0 : this.zzbxJ.hashCode()) + ((getClass().getName().hashCode() + 527) * 31)) * 31)) * 31)) * 31)) * 31)) * 31)) * 31;
            if (!(this.zzcuI == null || this.zzcuI.isEmpty())) {
                i = this.zzcuI.hashCode();
            }
            return hashCode + i;
        }

        public zzg zzNJ() {
            this.zzbxJ = null;
            this.name = null;
            this.zzaGV = null;
            this.zzbxc = null;
            this.zzbwe = null;
            this.zzbwf = null;
            this.zzcuI = null;
            this.zzcuR = -1;
            return this;
        }

        public zzg zzV(zzbxl com_google_android_gms_internal_zzbxl) throws IOException {
            while (true) {
                int zzaeo = com_google_android_gms_internal_zzbxl.zzaeo();
                switch (zzaeo) {
                    case 0:
                        break;
                    case 8:
                        this.zzbxJ = Long.valueOf(com_google_android_gms_internal_zzbxl.zzaer());
                        continue;
                    case 18:
                        this.name = com_google_android_gms_internal_zzbxl.readString();
                        continue;
                    case NalUnitTypes.NAL_TYPE_RSV_VCL26 /*26*/:
                        this.zzaGV = com_google_android_gms_internal_zzbxl.readString();
                        continue;
                    case 32:
                        this.zzbxc = Long.valueOf(com_google_android_gms_internal_zzbxl.zzaer());
                        continue;
                    case MotionEventCompat.AXIS_GENERIC_14 /*45*/:
                        this.zzbwe = Float.valueOf(com_google_android_gms_internal_zzbxl.readFloat());
                        continue;
                    case 49:
                        this.zzbwf = Double.valueOf(com_google_android_gms_internal_zzbxl.readDouble());
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

        public void zza(zzbxm com_google_android_gms_internal_zzbxm) throws IOException {
            if (this.zzbxJ != null) {
                com_google_android_gms_internal_zzbxm.zzb(1, this.zzbxJ.longValue());
            }
            if (this.name != null) {
                com_google_android_gms_internal_zzbxm.zzq(2, this.name);
            }
            if (this.zzaGV != null) {
                com_google_android_gms_internal_zzbxm.zzq(3, this.zzaGV);
            }
            if (this.zzbxc != null) {
                com_google_android_gms_internal_zzbxm.zzb(4, this.zzbxc.longValue());
            }
            if (this.zzbwe != null) {
                com_google_android_gms_internal_zzbxm.zzc(5, this.zzbwe.floatValue());
            }
            if (this.zzbwf != null) {
                com_google_android_gms_internal_zzbxm.zza(6, this.zzbwf.doubleValue());
            }
            super.zza(com_google_android_gms_internal_zzbxm);
        }

        public /* synthetic */ zzbxt zzb(zzbxl com_google_android_gms_internal_zzbxl) throws IOException {
            return zzV(com_google_android_gms_internal_zzbxl);
        }

        protected int zzu() {
            int zzu = super.zzu();
            if (this.zzbxJ != null) {
                zzu += zzbxm.zzf(1, this.zzbxJ.longValue());
            }
            if (this.name != null) {
                zzu += zzbxm.zzr(2, this.name);
            }
            if (this.zzaGV != null) {
                zzu += zzbxm.zzr(3, this.zzaGV);
            }
            if (this.zzbxc != null) {
                zzu += zzbxm.zzf(4, this.zzbxc.longValue());
            }
            if (this.zzbwe != null) {
                zzu += zzbxm.zzd(5, this.zzbwe.floatValue());
            }
            return this.zzbwf != null ? zzu + zzbxm.zzb(6, this.zzbwf.doubleValue()) : zzu;
        }
    }
}
