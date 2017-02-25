package com.google.android.gms.internal;

import com.googlecode.mp4parser.authoring.tracks.h265.NalUnitTypes;
import java.io.IOException;

public interface zzauu {

    public static final class zza extends zzbxn<zza> {
        private static volatile zza[] zzbwn;
        public Integer zzbwo;
        public zze[] zzbwp;
        public zzb[] zzbwq;

        public zza() {
            zzNj();
        }

        public static zza[] zzNi() {
            if (zzbwn == null) {
                synchronized (zzbxr.zzcuI) {
                    if (zzbwn == null) {
                        zzbwn = new zza[0];
                    }
                }
            }
            return zzbwn;
        }

        public boolean equals(Object obj) {
            if (obj == this) {
                return true;
            }
            if (!(obj instanceof zza)) {
                return false;
            }
            zza com_google_android_gms_internal_zzauu_zza = (zza) obj;
            if (this.zzbwo == null) {
                if (com_google_android_gms_internal_zzauu_zza.zzbwo != null) {
                    return false;
                }
            } else if (!this.zzbwo.equals(com_google_android_gms_internal_zzauu_zza.zzbwo)) {
                return false;
            }
            return (zzbxr.equals(this.zzbwp, com_google_android_gms_internal_zzauu_zza.zzbwp) && zzbxr.equals(this.zzbwq, com_google_android_gms_internal_zzauu_zza.zzbwq)) ? (this.zzcuA == null || this.zzcuA.isEmpty()) ? com_google_android_gms_internal_zzauu_zza.zzcuA == null || com_google_android_gms_internal_zzauu_zza.zzcuA.isEmpty() : this.zzcuA.equals(com_google_android_gms_internal_zzauu_zza.zzcuA) : false;
        }

        public int hashCode() {
            int i = 0;
            int hashCode = ((((((this.zzbwo == null ? 0 : this.zzbwo.hashCode()) + ((getClass().getName().hashCode() + 527) * 31)) * 31) + zzbxr.hashCode(this.zzbwp)) * 31) + zzbxr.hashCode(this.zzbwq)) * 31;
            if (!(this.zzcuA == null || this.zzcuA.isEmpty())) {
                i = this.zzcuA.hashCode();
            }
            return hashCode + i;
        }

        public zza zzG(zzbxl com_google_android_gms_internal_zzbxl) throws IOException {
            while (true) {
                int zzaen = com_google_android_gms_internal_zzbxl.zzaen();
                int zzb;
                Object obj;
                switch (zzaen) {
                    case 0:
                        break;
                    case 8:
                        this.zzbwo = Integer.valueOf(com_google_android_gms_internal_zzbxl.zzaer());
                        continue;
                    case 18:
                        zzb = zzbxw.zzb(com_google_android_gms_internal_zzbxl, 18);
                        zzaen = this.zzbwp == null ? 0 : this.zzbwp.length;
                        obj = new zze[(zzb + zzaen)];
                        if (zzaen != 0) {
                            System.arraycopy(this.zzbwp, 0, obj, 0, zzaen);
                        }
                        while (zzaen < obj.length - 1) {
                            obj[zzaen] = new zze();
                            com_google_android_gms_internal_zzbxl.zza(obj[zzaen]);
                            com_google_android_gms_internal_zzbxl.zzaen();
                            zzaen++;
                        }
                        obj[zzaen] = new zze();
                        com_google_android_gms_internal_zzbxl.zza(obj[zzaen]);
                        this.zzbwp = obj;
                        continue;
                    case NalUnitTypes.NAL_TYPE_RSV_VCL26 /*26*/:
                        zzb = zzbxw.zzb(com_google_android_gms_internal_zzbxl, 26);
                        zzaen = this.zzbwq == null ? 0 : this.zzbwq.length;
                        obj = new zzb[(zzb + zzaen)];
                        if (zzaen != 0) {
                            System.arraycopy(this.zzbwq, 0, obj, 0, zzaen);
                        }
                        while (zzaen < obj.length - 1) {
                            obj[zzaen] = new zzb();
                            com_google_android_gms_internal_zzbxl.zza(obj[zzaen]);
                            com_google_android_gms_internal_zzbxl.zzaen();
                            zzaen++;
                        }
                        obj[zzaen] = new zzb();
                        com_google_android_gms_internal_zzbxl.zza(obj[zzaen]);
                        this.zzbwq = obj;
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

        public zza zzNj() {
            this.zzbwo = null;
            this.zzbwp = zze.zzNp();
            this.zzbwq = zzb.zzNk();
            this.zzcuA = null;
            this.zzcuJ = -1;
            return this;
        }

        public void zza(zzbxm com_google_android_gms_internal_zzbxm) throws IOException {
            int i = 0;
            if (this.zzbwo != null) {
                com_google_android_gms_internal_zzbxm.zzJ(1, this.zzbwo.intValue());
            }
            if (this.zzbwp != null && this.zzbwp.length > 0) {
                for (zzbxt com_google_android_gms_internal_zzbxt : this.zzbwp) {
                    if (com_google_android_gms_internal_zzbxt != null) {
                        com_google_android_gms_internal_zzbxm.zza(2, com_google_android_gms_internal_zzbxt);
                    }
                }
            }
            if (this.zzbwq != null && this.zzbwq.length > 0) {
                while (i < this.zzbwq.length) {
                    zzbxt com_google_android_gms_internal_zzbxt2 = this.zzbwq[i];
                    if (com_google_android_gms_internal_zzbxt2 != null) {
                        com_google_android_gms_internal_zzbxm.zza(3, com_google_android_gms_internal_zzbxt2);
                    }
                    i++;
                }
            }
            super.zza(com_google_android_gms_internal_zzbxm);
        }

        public /* synthetic */ zzbxt zzb(zzbxl com_google_android_gms_internal_zzbxl) throws IOException {
            return zzG(com_google_android_gms_internal_zzbxl);
        }

        protected int zzu() {
            int i = 0;
            int zzu = super.zzu();
            if (this.zzbwo != null) {
                zzu += zzbxm.zzL(1, this.zzbwo.intValue());
            }
            if (this.zzbwp != null && this.zzbwp.length > 0) {
                int i2 = zzu;
                for (zzbxt com_google_android_gms_internal_zzbxt : this.zzbwp) {
                    if (com_google_android_gms_internal_zzbxt != null) {
                        i2 += zzbxm.zzc(2, com_google_android_gms_internal_zzbxt);
                    }
                }
                zzu = i2;
            }
            if (this.zzbwq != null && this.zzbwq.length > 0) {
                while (i < this.zzbwq.length) {
                    zzbxt com_google_android_gms_internal_zzbxt2 = this.zzbwq[i];
                    if (com_google_android_gms_internal_zzbxt2 != null) {
                        zzu += zzbxm.zzc(3, com_google_android_gms_internal_zzbxt2);
                    }
                    i++;
                }
            }
            return zzu;
        }
    }

    public static final class zzb extends zzbxn<zzb> {
        private static volatile zzb[] zzbwr;
        public Integer zzbws;
        public String zzbwt;
        public zzc[] zzbwu;
        public Boolean zzbwv;
        public zzd zzbww;

        public zzb() {
            zzNl();
        }

        public static zzb[] zzNk() {
            if (zzbwr == null) {
                synchronized (zzbxr.zzcuI) {
                    if (zzbwr == null) {
                        zzbwr = new zzb[0];
                    }
                }
            }
            return zzbwr;
        }

        public boolean equals(Object obj) {
            if (obj == this) {
                return true;
            }
            if (!(obj instanceof zzb)) {
                return false;
            }
            zzb com_google_android_gms_internal_zzauu_zzb = (zzb) obj;
            if (this.zzbws == null) {
                if (com_google_android_gms_internal_zzauu_zzb.zzbws != null) {
                    return false;
                }
            } else if (!this.zzbws.equals(com_google_android_gms_internal_zzauu_zzb.zzbws)) {
                return false;
            }
            if (this.zzbwt == null) {
                if (com_google_android_gms_internal_zzauu_zzb.zzbwt != null) {
                    return false;
                }
            } else if (!this.zzbwt.equals(com_google_android_gms_internal_zzauu_zzb.zzbwt)) {
                return false;
            }
            if (!zzbxr.equals(this.zzbwu, com_google_android_gms_internal_zzauu_zzb.zzbwu)) {
                return false;
            }
            if (this.zzbwv == null) {
                if (com_google_android_gms_internal_zzauu_zzb.zzbwv != null) {
                    return false;
                }
            } else if (!this.zzbwv.equals(com_google_android_gms_internal_zzauu_zzb.zzbwv)) {
                return false;
            }
            if (this.zzbww == null) {
                if (com_google_android_gms_internal_zzauu_zzb.zzbww != null) {
                    return false;
                }
            } else if (!this.zzbww.equals(com_google_android_gms_internal_zzauu_zzb.zzbww)) {
                return false;
            }
            return (this.zzcuA == null || this.zzcuA.isEmpty()) ? com_google_android_gms_internal_zzauu_zzb.zzcuA == null || com_google_android_gms_internal_zzauu_zzb.zzcuA.isEmpty() : this.zzcuA.equals(com_google_android_gms_internal_zzauu_zzb.zzcuA);
        }

        public int hashCode() {
            int i = 0;
            int hashCode = ((this.zzbww == null ? 0 : this.zzbww.hashCode()) + (((this.zzbwv == null ? 0 : this.zzbwv.hashCode()) + (((((this.zzbwt == null ? 0 : this.zzbwt.hashCode()) + (((this.zzbws == null ? 0 : this.zzbws.hashCode()) + ((getClass().getName().hashCode() + 527) * 31)) * 31)) * 31) + zzbxr.hashCode(this.zzbwu)) * 31)) * 31)) * 31;
            if (!(this.zzcuA == null || this.zzcuA.isEmpty())) {
                i = this.zzcuA.hashCode();
            }
            return hashCode + i;
        }

        public zzb zzH(zzbxl com_google_android_gms_internal_zzbxl) throws IOException {
            while (true) {
                int zzaen = com_google_android_gms_internal_zzbxl.zzaen();
                switch (zzaen) {
                    case 0:
                        break;
                    case 8:
                        this.zzbws = Integer.valueOf(com_google_android_gms_internal_zzbxl.zzaer());
                        continue;
                    case 18:
                        this.zzbwt = com_google_android_gms_internal_zzbxl.readString();
                        continue;
                    case NalUnitTypes.NAL_TYPE_RSV_VCL26 /*26*/:
                        int zzb = zzbxw.zzb(com_google_android_gms_internal_zzbxl, 26);
                        zzaen = this.zzbwu == null ? 0 : this.zzbwu.length;
                        Object obj = new zzc[(zzb + zzaen)];
                        if (zzaen != 0) {
                            System.arraycopy(this.zzbwu, 0, obj, 0, zzaen);
                        }
                        while (zzaen < obj.length - 1) {
                            obj[zzaen] = new zzc();
                            com_google_android_gms_internal_zzbxl.zza(obj[zzaen]);
                            com_google_android_gms_internal_zzbxl.zzaen();
                            zzaen++;
                        }
                        obj[zzaen] = new zzc();
                        com_google_android_gms_internal_zzbxl.zza(obj[zzaen]);
                        this.zzbwu = obj;
                        continue;
                    case 32:
                        this.zzbwv = Boolean.valueOf(com_google_android_gms_internal_zzbxl.zzaet());
                        continue;
                    case 42:
                        if (this.zzbww == null) {
                            this.zzbww = new zzd();
                        }
                        com_google_android_gms_internal_zzbxl.zza(this.zzbww);
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

        public zzb zzNl() {
            this.zzbws = null;
            this.zzbwt = null;
            this.zzbwu = zzc.zzNm();
            this.zzbwv = null;
            this.zzbww = null;
            this.zzcuA = null;
            this.zzcuJ = -1;
            return this;
        }

        public void zza(zzbxm com_google_android_gms_internal_zzbxm) throws IOException {
            if (this.zzbws != null) {
                com_google_android_gms_internal_zzbxm.zzJ(1, this.zzbws.intValue());
            }
            if (this.zzbwt != null) {
                com_google_android_gms_internal_zzbxm.zzq(2, this.zzbwt);
            }
            if (this.zzbwu != null && this.zzbwu.length > 0) {
                for (zzbxt com_google_android_gms_internal_zzbxt : this.zzbwu) {
                    if (com_google_android_gms_internal_zzbxt != null) {
                        com_google_android_gms_internal_zzbxm.zza(3, com_google_android_gms_internal_zzbxt);
                    }
                }
            }
            if (this.zzbwv != null) {
                com_google_android_gms_internal_zzbxm.zzg(4, this.zzbwv.booleanValue());
            }
            if (this.zzbww != null) {
                com_google_android_gms_internal_zzbxm.zza(5, this.zzbww);
            }
            super.zza(com_google_android_gms_internal_zzbxm);
        }

        public /* synthetic */ zzbxt zzb(zzbxl com_google_android_gms_internal_zzbxl) throws IOException {
            return zzH(com_google_android_gms_internal_zzbxl);
        }

        protected int zzu() {
            int zzu = super.zzu();
            if (this.zzbws != null) {
                zzu += zzbxm.zzL(1, this.zzbws.intValue());
            }
            if (this.zzbwt != null) {
                zzu += zzbxm.zzr(2, this.zzbwt);
            }
            if (this.zzbwu != null && this.zzbwu.length > 0) {
                int i = zzu;
                for (zzbxt com_google_android_gms_internal_zzbxt : this.zzbwu) {
                    if (com_google_android_gms_internal_zzbxt != null) {
                        i += zzbxm.zzc(3, com_google_android_gms_internal_zzbxt);
                    }
                }
                zzu = i;
            }
            if (this.zzbwv != null) {
                zzu += zzbxm.zzh(4, this.zzbwv.booleanValue());
            }
            return this.zzbww != null ? zzu + zzbxm.zzc(5, this.zzbww) : zzu;
        }
    }

    public static final class zzc extends zzbxn<zzc> {
        private static volatile zzc[] zzbwx;
        public Boolean zzbwA;
        public String zzbwB;
        public zzf zzbwy;
        public zzd zzbwz;

        public zzc() {
            zzNn();
        }

        public static zzc[] zzNm() {
            if (zzbwx == null) {
                synchronized (zzbxr.zzcuI) {
                    if (zzbwx == null) {
                        zzbwx = new zzc[0];
                    }
                }
            }
            return zzbwx;
        }

        public boolean equals(Object obj) {
            if (obj == this) {
                return true;
            }
            if (!(obj instanceof zzc)) {
                return false;
            }
            zzc com_google_android_gms_internal_zzauu_zzc = (zzc) obj;
            if (this.zzbwy == null) {
                if (com_google_android_gms_internal_zzauu_zzc.zzbwy != null) {
                    return false;
                }
            } else if (!this.zzbwy.equals(com_google_android_gms_internal_zzauu_zzc.zzbwy)) {
                return false;
            }
            if (this.zzbwz == null) {
                if (com_google_android_gms_internal_zzauu_zzc.zzbwz != null) {
                    return false;
                }
            } else if (!this.zzbwz.equals(com_google_android_gms_internal_zzauu_zzc.zzbwz)) {
                return false;
            }
            if (this.zzbwA == null) {
                if (com_google_android_gms_internal_zzauu_zzc.zzbwA != null) {
                    return false;
                }
            } else if (!this.zzbwA.equals(com_google_android_gms_internal_zzauu_zzc.zzbwA)) {
                return false;
            }
            if (this.zzbwB == null) {
                if (com_google_android_gms_internal_zzauu_zzc.zzbwB != null) {
                    return false;
                }
            } else if (!this.zzbwB.equals(com_google_android_gms_internal_zzauu_zzc.zzbwB)) {
                return false;
            }
            return (this.zzcuA == null || this.zzcuA.isEmpty()) ? com_google_android_gms_internal_zzauu_zzc.zzcuA == null || com_google_android_gms_internal_zzauu_zzc.zzcuA.isEmpty() : this.zzcuA.equals(com_google_android_gms_internal_zzauu_zzc.zzcuA);
        }

        public int hashCode() {
            int i = 0;
            int hashCode = ((this.zzbwB == null ? 0 : this.zzbwB.hashCode()) + (((this.zzbwA == null ? 0 : this.zzbwA.hashCode()) + (((this.zzbwz == null ? 0 : this.zzbwz.hashCode()) + (((this.zzbwy == null ? 0 : this.zzbwy.hashCode()) + ((getClass().getName().hashCode() + 527) * 31)) * 31)) * 31)) * 31)) * 31;
            if (!(this.zzcuA == null || this.zzcuA.isEmpty())) {
                i = this.zzcuA.hashCode();
            }
            return hashCode + i;
        }

        public zzc zzI(zzbxl com_google_android_gms_internal_zzbxl) throws IOException {
            while (true) {
                int zzaen = com_google_android_gms_internal_zzbxl.zzaen();
                switch (zzaen) {
                    case 0:
                        break;
                    case 10:
                        if (this.zzbwy == null) {
                            this.zzbwy = new zzf();
                        }
                        com_google_android_gms_internal_zzbxl.zza(this.zzbwy);
                        continue;
                    case 18:
                        if (this.zzbwz == null) {
                            this.zzbwz = new zzd();
                        }
                        com_google_android_gms_internal_zzbxl.zza(this.zzbwz);
                        continue;
                    case 24:
                        this.zzbwA = Boolean.valueOf(com_google_android_gms_internal_zzbxl.zzaet());
                        continue;
                    case 34:
                        this.zzbwB = com_google_android_gms_internal_zzbxl.readString();
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

        public zzc zzNn() {
            this.zzbwy = null;
            this.zzbwz = null;
            this.zzbwA = null;
            this.zzbwB = null;
            this.zzcuA = null;
            this.zzcuJ = -1;
            return this;
        }

        public void zza(zzbxm com_google_android_gms_internal_zzbxm) throws IOException {
            if (this.zzbwy != null) {
                com_google_android_gms_internal_zzbxm.zza(1, this.zzbwy);
            }
            if (this.zzbwz != null) {
                com_google_android_gms_internal_zzbxm.zza(2, this.zzbwz);
            }
            if (this.zzbwA != null) {
                com_google_android_gms_internal_zzbxm.zzg(3, this.zzbwA.booleanValue());
            }
            if (this.zzbwB != null) {
                com_google_android_gms_internal_zzbxm.zzq(4, this.zzbwB);
            }
            super.zza(com_google_android_gms_internal_zzbxm);
        }

        public /* synthetic */ zzbxt zzb(zzbxl com_google_android_gms_internal_zzbxl) throws IOException {
            return zzI(com_google_android_gms_internal_zzbxl);
        }

        protected int zzu() {
            int zzu = super.zzu();
            if (this.zzbwy != null) {
                zzu += zzbxm.zzc(1, this.zzbwy);
            }
            if (this.zzbwz != null) {
                zzu += zzbxm.zzc(2, this.zzbwz);
            }
            if (this.zzbwA != null) {
                zzu += zzbxm.zzh(3, this.zzbwA.booleanValue());
            }
            return this.zzbwB != null ? zzu + zzbxm.zzr(4, this.zzbwB) : zzu;
        }
    }

    public static final class zzd extends zzbxn<zzd> {
        public Integer zzbwC;
        public Boolean zzbwD;
        public String zzbwE;
        public String zzbwF;
        public String zzbwG;

        public zzd() {
            zzNo();
        }

        public boolean equals(Object obj) {
            if (obj == this) {
                return true;
            }
            if (!(obj instanceof zzd)) {
                return false;
            }
            zzd com_google_android_gms_internal_zzauu_zzd = (zzd) obj;
            if (this.zzbwC == null) {
                if (com_google_android_gms_internal_zzauu_zzd.zzbwC != null) {
                    return false;
                }
            } else if (!this.zzbwC.equals(com_google_android_gms_internal_zzauu_zzd.zzbwC)) {
                return false;
            }
            if (this.zzbwD == null) {
                if (com_google_android_gms_internal_zzauu_zzd.zzbwD != null) {
                    return false;
                }
            } else if (!this.zzbwD.equals(com_google_android_gms_internal_zzauu_zzd.zzbwD)) {
                return false;
            }
            if (this.zzbwE == null) {
                if (com_google_android_gms_internal_zzauu_zzd.zzbwE != null) {
                    return false;
                }
            } else if (!this.zzbwE.equals(com_google_android_gms_internal_zzauu_zzd.zzbwE)) {
                return false;
            }
            if (this.zzbwF == null) {
                if (com_google_android_gms_internal_zzauu_zzd.zzbwF != null) {
                    return false;
                }
            } else if (!this.zzbwF.equals(com_google_android_gms_internal_zzauu_zzd.zzbwF)) {
                return false;
            }
            if (this.zzbwG == null) {
                if (com_google_android_gms_internal_zzauu_zzd.zzbwG != null) {
                    return false;
                }
            } else if (!this.zzbwG.equals(com_google_android_gms_internal_zzauu_zzd.zzbwG)) {
                return false;
            }
            return (this.zzcuA == null || this.zzcuA.isEmpty()) ? com_google_android_gms_internal_zzauu_zzd.zzcuA == null || com_google_android_gms_internal_zzauu_zzd.zzcuA.isEmpty() : this.zzcuA.equals(com_google_android_gms_internal_zzauu_zzd.zzcuA);
        }

        public int hashCode() {
            int i = 0;
            int hashCode = ((this.zzbwG == null ? 0 : this.zzbwG.hashCode()) + (((this.zzbwF == null ? 0 : this.zzbwF.hashCode()) + (((this.zzbwE == null ? 0 : this.zzbwE.hashCode()) + (((this.zzbwD == null ? 0 : this.zzbwD.hashCode()) + (((this.zzbwC == null ? 0 : this.zzbwC.intValue()) + ((getClass().getName().hashCode() + 527) * 31)) * 31)) * 31)) * 31)) * 31)) * 31;
            if (!(this.zzcuA == null || this.zzcuA.isEmpty())) {
                i = this.zzcuA.hashCode();
            }
            return hashCode + i;
        }

        public zzd zzJ(zzbxl com_google_android_gms_internal_zzbxl) throws IOException {
            while (true) {
                int zzaen = com_google_android_gms_internal_zzbxl.zzaen();
                switch (zzaen) {
                    case 0:
                        break;
                    case 8:
                        zzaen = com_google_android_gms_internal_zzbxl.zzaer();
                        switch (zzaen) {
                            case 0:
                            case 1:
                            case 2:
                            case 3:
                            case 4:
                                this.zzbwC = Integer.valueOf(zzaen);
                                break;
                            default:
                                continue;
                        }
                    case 16:
                        this.zzbwD = Boolean.valueOf(com_google_android_gms_internal_zzbxl.zzaet());
                        continue;
                    case NalUnitTypes.NAL_TYPE_RSV_VCL26 /*26*/:
                        this.zzbwE = com_google_android_gms_internal_zzbxl.readString();
                        continue;
                    case 34:
                        this.zzbwF = com_google_android_gms_internal_zzbxl.readString();
                        continue;
                    case 42:
                        this.zzbwG = com_google_android_gms_internal_zzbxl.readString();
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

        public zzd zzNo() {
            this.zzbwD = null;
            this.zzbwE = null;
            this.zzbwF = null;
            this.zzbwG = null;
            this.zzcuA = null;
            this.zzcuJ = -1;
            return this;
        }

        public void zza(zzbxm com_google_android_gms_internal_zzbxm) throws IOException {
            if (this.zzbwC != null) {
                com_google_android_gms_internal_zzbxm.zzJ(1, this.zzbwC.intValue());
            }
            if (this.zzbwD != null) {
                com_google_android_gms_internal_zzbxm.zzg(2, this.zzbwD.booleanValue());
            }
            if (this.zzbwE != null) {
                com_google_android_gms_internal_zzbxm.zzq(3, this.zzbwE);
            }
            if (this.zzbwF != null) {
                com_google_android_gms_internal_zzbxm.zzq(4, this.zzbwF);
            }
            if (this.zzbwG != null) {
                com_google_android_gms_internal_zzbxm.zzq(5, this.zzbwG);
            }
            super.zza(com_google_android_gms_internal_zzbxm);
        }

        public /* synthetic */ zzbxt zzb(zzbxl com_google_android_gms_internal_zzbxl) throws IOException {
            return zzJ(com_google_android_gms_internal_zzbxl);
        }

        protected int zzu() {
            int zzu = super.zzu();
            if (this.zzbwC != null) {
                zzu += zzbxm.zzL(1, this.zzbwC.intValue());
            }
            if (this.zzbwD != null) {
                zzu += zzbxm.zzh(2, this.zzbwD.booleanValue());
            }
            if (this.zzbwE != null) {
                zzu += zzbxm.zzr(3, this.zzbwE);
            }
            if (this.zzbwF != null) {
                zzu += zzbxm.zzr(4, this.zzbwF);
            }
            return this.zzbwG != null ? zzu + zzbxm.zzr(5, this.zzbwG) : zzu;
        }
    }

    public static final class zze extends zzbxn<zze> {
        private static volatile zze[] zzbwH;
        public String zzbwI;
        public zzc zzbwJ;
        public Integer zzbws;

        public zze() {
            zzNq();
        }

        public static zze[] zzNp() {
            if (zzbwH == null) {
                synchronized (zzbxr.zzcuI) {
                    if (zzbwH == null) {
                        zzbwH = new zze[0];
                    }
                }
            }
            return zzbwH;
        }

        public boolean equals(Object obj) {
            if (obj == this) {
                return true;
            }
            if (!(obj instanceof zze)) {
                return false;
            }
            zze com_google_android_gms_internal_zzauu_zze = (zze) obj;
            if (this.zzbws == null) {
                if (com_google_android_gms_internal_zzauu_zze.zzbws != null) {
                    return false;
                }
            } else if (!this.zzbws.equals(com_google_android_gms_internal_zzauu_zze.zzbws)) {
                return false;
            }
            if (this.zzbwI == null) {
                if (com_google_android_gms_internal_zzauu_zze.zzbwI != null) {
                    return false;
                }
            } else if (!this.zzbwI.equals(com_google_android_gms_internal_zzauu_zze.zzbwI)) {
                return false;
            }
            if (this.zzbwJ == null) {
                if (com_google_android_gms_internal_zzauu_zze.zzbwJ != null) {
                    return false;
                }
            } else if (!this.zzbwJ.equals(com_google_android_gms_internal_zzauu_zze.zzbwJ)) {
                return false;
            }
            return (this.zzcuA == null || this.zzcuA.isEmpty()) ? com_google_android_gms_internal_zzauu_zze.zzcuA == null || com_google_android_gms_internal_zzauu_zze.zzcuA.isEmpty() : this.zzcuA.equals(com_google_android_gms_internal_zzauu_zze.zzcuA);
        }

        public int hashCode() {
            int i = 0;
            int hashCode = ((this.zzbwJ == null ? 0 : this.zzbwJ.hashCode()) + (((this.zzbwI == null ? 0 : this.zzbwI.hashCode()) + (((this.zzbws == null ? 0 : this.zzbws.hashCode()) + ((getClass().getName().hashCode() + 527) * 31)) * 31)) * 31)) * 31;
            if (!(this.zzcuA == null || this.zzcuA.isEmpty())) {
                i = this.zzcuA.hashCode();
            }
            return hashCode + i;
        }

        public zze zzK(zzbxl com_google_android_gms_internal_zzbxl) throws IOException {
            while (true) {
                int zzaen = com_google_android_gms_internal_zzbxl.zzaen();
                switch (zzaen) {
                    case 0:
                        break;
                    case 8:
                        this.zzbws = Integer.valueOf(com_google_android_gms_internal_zzbxl.zzaer());
                        continue;
                    case 18:
                        this.zzbwI = com_google_android_gms_internal_zzbxl.readString();
                        continue;
                    case NalUnitTypes.NAL_TYPE_RSV_VCL26 /*26*/:
                        if (this.zzbwJ == null) {
                            this.zzbwJ = new zzc();
                        }
                        com_google_android_gms_internal_zzbxl.zza(this.zzbwJ);
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

        public zze zzNq() {
            this.zzbws = null;
            this.zzbwI = null;
            this.zzbwJ = null;
            this.zzcuA = null;
            this.zzcuJ = -1;
            return this;
        }

        public void zza(zzbxm com_google_android_gms_internal_zzbxm) throws IOException {
            if (this.zzbws != null) {
                com_google_android_gms_internal_zzbxm.zzJ(1, this.zzbws.intValue());
            }
            if (this.zzbwI != null) {
                com_google_android_gms_internal_zzbxm.zzq(2, this.zzbwI);
            }
            if (this.zzbwJ != null) {
                com_google_android_gms_internal_zzbxm.zza(3, this.zzbwJ);
            }
            super.zza(com_google_android_gms_internal_zzbxm);
        }

        public /* synthetic */ zzbxt zzb(zzbxl com_google_android_gms_internal_zzbxl) throws IOException {
            return zzK(com_google_android_gms_internal_zzbxl);
        }

        protected int zzu() {
            int zzu = super.zzu();
            if (this.zzbws != null) {
                zzu += zzbxm.zzL(1, this.zzbws.intValue());
            }
            if (this.zzbwI != null) {
                zzu += zzbxm.zzr(2, this.zzbwI);
            }
            return this.zzbwJ != null ? zzu + zzbxm.zzc(3, this.zzbwJ) : zzu;
        }
    }

    public static final class zzf extends zzbxn<zzf> {
        public Integer zzbwK;
        public String zzbwL;
        public Boolean zzbwM;
        public String[] zzbwN;

        public zzf() {
            zzNr();
        }

        public boolean equals(Object obj) {
            if (obj == this) {
                return true;
            }
            if (!(obj instanceof zzf)) {
                return false;
            }
            zzf com_google_android_gms_internal_zzauu_zzf = (zzf) obj;
            if (this.zzbwK == null) {
                if (com_google_android_gms_internal_zzauu_zzf.zzbwK != null) {
                    return false;
                }
            } else if (!this.zzbwK.equals(com_google_android_gms_internal_zzauu_zzf.zzbwK)) {
                return false;
            }
            if (this.zzbwL == null) {
                if (com_google_android_gms_internal_zzauu_zzf.zzbwL != null) {
                    return false;
                }
            } else if (!this.zzbwL.equals(com_google_android_gms_internal_zzauu_zzf.zzbwL)) {
                return false;
            }
            if (this.zzbwM == null) {
                if (com_google_android_gms_internal_zzauu_zzf.zzbwM != null) {
                    return false;
                }
            } else if (!this.zzbwM.equals(com_google_android_gms_internal_zzauu_zzf.zzbwM)) {
                return false;
            }
            return zzbxr.equals(this.zzbwN, com_google_android_gms_internal_zzauu_zzf.zzbwN) ? (this.zzcuA == null || this.zzcuA.isEmpty()) ? com_google_android_gms_internal_zzauu_zzf.zzcuA == null || com_google_android_gms_internal_zzauu_zzf.zzcuA.isEmpty() : this.zzcuA.equals(com_google_android_gms_internal_zzauu_zzf.zzcuA) : false;
        }

        public int hashCode() {
            int i = 0;
            int hashCode = ((((this.zzbwM == null ? 0 : this.zzbwM.hashCode()) + (((this.zzbwL == null ? 0 : this.zzbwL.hashCode()) + (((this.zzbwK == null ? 0 : this.zzbwK.intValue()) + ((getClass().getName().hashCode() + 527) * 31)) * 31)) * 31)) * 31) + zzbxr.hashCode(this.zzbwN)) * 31;
            if (!(this.zzcuA == null || this.zzcuA.isEmpty())) {
                i = this.zzcuA.hashCode();
            }
            return hashCode + i;
        }

        public zzf zzL(zzbxl com_google_android_gms_internal_zzbxl) throws IOException {
            while (true) {
                int zzaen = com_google_android_gms_internal_zzbxl.zzaen();
                switch (zzaen) {
                    case 0:
                        break;
                    case 8:
                        zzaen = com_google_android_gms_internal_zzbxl.zzaer();
                        switch (zzaen) {
                            case 0:
                            case 1:
                            case 2:
                            case 3:
                            case 4:
                            case 5:
                            case 6:
                                this.zzbwK = Integer.valueOf(zzaen);
                                break;
                            default:
                                continue;
                        }
                    case 18:
                        this.zzbwL = com_google_android_gms_internal_zzbxl.readString();
                        continue;
                    case 24:
                        this.zzbwM = Boolean.valueOf(com_google_android_gms_internal_zzbxl.zzaet());
                        continue;
                    case 34:
                        int zzb = zzbxw.zzb(com_google_android_gms_internal_zzbxl, 34);
                        zzaen = this.zzbwN == null ? 0 : this.zzbwN.length;
                        Object obj = new String[(zzb + zzaen)];
                        if (zzaen != 0) {
                            System.arraycopy(this.zzbwN, 0, obj, 0, zzaen);
                        }
                        while (zzaen < obj.length - 1) {
                            obj[zzaen] = com_google_android_gms_internal_zzbxl.readString();
                            com_google_android_gms_internal_zzbxl.zzaen();
                            zzaen++;
                        }
                        obj[zzaen] = com_google_android_gms_internal_zzbxl.readString();
                        this.zzbwN = obj;
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

        public zzf zzNr() {
            this.zzbwL = null;
            this.zzbwM = null;
            this.zzbwN = zzbxw.zzcuT;
            this.zzcuA = null;
            this.zzcuJ = -1;
            return this;
        }

        public void zza(zzbxm com_google_android_gms_internal_zzbxm) throws IOException {
            if (this.zzbwK != null) {
                com_google_android_gms_internal_zzbxm.zzJ(1, this.zzbwK.intValue());
            }
            if (this.zzbwL != null) {
                com_google_android_gms_internal_zzbxm.zzq(2, this.zzbwL);
            }
            if (this.zzbwM != null) {
                com_google_android_gms_internal_zzbxm.zzg(3, this.zzbwM.booleanValue());
            }
            if (this.zzbwN != null && this.zzbwN.length > 0) {
                for (String str : this.zzbwN) {
                    if (str != null) {
                        com_google_android_gms_internal_zzbxm.zzq(4, str);
                    }
                }
            }
            super.zza(com_google_android_gms_internal_zzbxm);
        }

        public /* synthetic */ zzbxt zzb(zzbxl com_google_android_gms_internal_zzbxl) throws IOException {
            return zzL(com_google_android_gms_internal_zzbxl);
        }

        protected int zzu() {
            int i = 0;
            int zzu = super.zzu();
            if (this.zzbwK != null) {
                zzu += zzbxm.zzL(1, this.zzbwK.intValue());
            }
            if (this.zzbwL != null) {
                zzu += zzbxm.zzr(2, this.zzbwL);
            }
            if (this.zzbwM != null) {
                zzu += zzbxm.zzh(3, this.zzbwM.booleanValue());
            }
            if (this.zzbwN == null || this.zzbwN.length <= 0) {
                return zzu;
            }
            int i2 = 0;
            int i3 = 0;
            while (i < this.zzbwN.length) {
                String str = this.zzbwN[i];
                if (str != null) {
                    i3++;
                    i2 += zzbxm.zzkb(str);
                }
                i++;
            }
            return (zzu + i2) + (i3 * 1);
        }
    }
}
