package com.google.android.gms.internal;

import com.googlecode.mp4parser.authoring.tracks.h265.NalUnitTypes;
import java.io.IOException;

public interface zzauu {

    public static final class zza extends zzbxn<zza> {
        private static volatile zza[] zzbwj;
        public Integer zzbwk;
        public zze[] zzbwl;
        public zzb[] zzbwm;

        public zza() {
            zzNk();
        }

        public static zza[] zzNj() {
            if (zzbwj == null) {
                synchronized (zzbxr.zzcuQ) {
                    if (zzbwj == null) {
                        zzbwj = new zza[0];
                    }
                }
            }
            return zzbwj;
        }

        public boolean equals(Object obj) {
            if (obj == this) {
                return true;
            }
            if (!(obj instanceof zza)) {
                return false;
            }
            zza com_google_android_gms_internal_zzauu_zza = (zza) obj;
            if (this.zzbwk == null) {
                if (com_google_android_gms_internal_zzauu_zza.zzbwk != null) {
                    return false;
                }
            } else if (!this.zzbwk.equals(com_google_android_gms_internal_zzauu_zza.zzbwk)) {
                return false;
            }
            return (zzbxr.equals(this.zzbwl, com_google_android_gms_internal_zzauu_zza.zzbwl) && zzbxr.equals(this.zzbwm, com_google_android_gms_internal_zzauu_zza.zzbwm)) ? (this.zzcuI == null || this.zzcuI.isEmpty()) ? com_google_android_gms_internal_zzauu_zza.zzcuI == null || com_google_android_gms_internal_zzauu_zza.zzcuI.isEmpty() : this.zzcuI.equals(com_google_android_gms_internal_zzauu_zza.zzcuI) : false;
        }

        public int hashCode() {
            int i = 0;
            int hashCode = ((((((this.zzbwk == null ? 0 : this.zzbwk.hashCode()) + ((getClass().getName().hashCode() + 527) * 31)) * 31) + zzbxr.hashCode(this.zzbwl)) * 31) + zzbxr.hashCode(this.zzbwm)) * 31;
            if (!(this.zzcuI == null || this.zzcuI.isEmpty())) {
                i = this.zzcuI.hashCode();
            }
            return hashCode + i;
        }

        public zza zzG(zzbxl com_google_android_gms_internal_zzbxl) throws IOException {
            while (true) {
                int zzaeo = com_google_android_gms_internal_zzbxl.zzaeo();
                int zzb;
                Object obj;
                switch (zzaeo) {
                    case 0:
                        break;
                    case 8:
                        this.zzbwk = Integer.valueOf(com_google_android_gms_internal_zzbxl.zzaes());
                        continue;
                    case 18:
                        zzb = zzbxw.zzb(com_google_android_gms_internal_zzbxl, 18);
                        zzaeo = this.zzbwl == null ? 0 : this.zzbwl.length;
                        obj = new zze[(zzb + zzaeo)];
                        if (zzaeo != 0) {
                            System.arraycopy(this.zzbwl, 0, obj, 0, zzaeo);
                        }
                        while (zzaeo < obj.length - 1) {
                            obj[zzaeo] = new zze();
                            com_google_android_gms_internal_zzbxl.zza(obj[zzaeo]);
                            com_google_android_gms_internal_zzbxl.zzaeo();
                            zzaeo++;
                        }
                        obj[zzaeo] = new zze();
                        com_google_android_gms_internal_zzbxl.zza(obj[zzaeo]);
                        this.zzbwl = obj;
                        continue;
                    case NalUnitTypes.NAL_TYPE_RSV_VCL26 /*26*/:
                        zzb = zzbxw.zzb(com_google_android_gms_internal_zzbxl, 26);
                        zzaeo = this.zzbwm == null ? 0 : this.zzbwm.length;
                        obj = new zzb[(zzb + zzaeo)];
                        if (zzaeo != 0) {
                            System.arraycopy(this.zzbwm, 0, obj, 0, zzaeo);
                        }
                        while (zzaeo < obj.length - 1) {
                            obj[zzaeo] = new zzb();
                            com_google_android_gms_internal_zzbxl.zza(obj[zzaeo]);
                            com_google_android_gms_internal_zzbxl.zzaeo();
                            zzaeo++;
                        }
                        obj[zzaeo] = new zzb();
                        com_google_android_gms_internal_zzbxl.zza(obj[zzaeo]);
                        this.zzbwm = obj;
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

        public zza zzNk() {
            this.zzbwk = null;
            this.zzbwl = zze.zzNq();
            this.zzbwm = zzb.zzNl();
            this.zzcuI = null;
            this.zzcuR = -1;
            return this;
        }

        public void zza(zzbxm com_google_android_gms_internal_zzbxm) throws IOException {
            int i = 0;
            if (this.zzbwk != null) {
                com_google_android_gms_internal_zzbxm.zzJ(1, this.zzbwk.intValue());
            }
            if (this.zzbwl != null && this.zzbwl.length > 0) {
                for (zzbxt com_google_android_gms_internal_zzbxt : this.zzbwl) {
                    if (com_google_android_gms_internal_zzbxt != null) {
                        com_google_android_gms_internal_zzbxm.zza(2, com_google_android_gms_internal_zzbxt);
                    }
                }
            }
            if (this.zzbwm != null && this.zzbwm.length > 0) {
                while (i < this.zzbwm.length) {
                    zzbxt com_google_android_gms_internal_zzbxt2 = this.zzbwm[i];
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
            if (this.zzbwk != null) {
                zzu += zzbxm.zzL(1, this.zzbwk.intValue());
            }
            if (this.zzbwl != null && this.zzbwl.length > 0) {
                int i2 = zzu;
                for (zzbxt com_google_android_gms_internal_zzbxt : this.zzbwl) {
                    if (com_google_android_gms_internal_zzbxt != null) {
                        i2 += zzbxm.zzc(2, com_google_android_gms_internal_zzbxt);
                    }
                }
                zzu = i2;
            }
            if (this.zzbwm != null && this.zzbwm.length > 0) {
                while (i < this.zzbwm.length) {
                    zzbxt com_google_android_gms_internal_zzbxt2 = this.zzbwm[i];
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
        private static volatile zzb[] zzbwn;
        public Integer zzbwo;
        public String zzbwp;
        public zzc[] zzbwq;
        public Boolean zzbwr;
        public zzd zzbws;

        public zzb() {
            zzNm();
        }

        public static zzb[] zzNl() {
            if (zzbwn == null) {
                synchronized (zzbxr.zzcuQ) {
                    if (zzbwn == null) {
                        zzbwn = new zzb[0];
                    }
                }
            }
            return zzbwn;
        }

        public boolean equals(Object obj) {
            if (obj == this) {
                return true;
            }
            if (!(obj instanceof zzb)) {
                return false;
            }
            zzb com_google_android_gms_internal_zzauu_zzb = (zzb) obj;
            if (this.zzbwo == null) {
                if (com_google_android_gms_internal_zzauu_zzb.zzbwo != null) {
                    return false;
                }
            } else if (!this.zzbwo.equals(com_google_android_gms_internal_zzauu_zzb.zzbwo)) {
                return false;
            }
            if (this.zzbwp == null) {
                if (com_google_android_gms_internal_zzauu_zzb.zzbwp != null) {
                    return false;
                }
            } else if (!this.zzbwp.equals(com_google_android_gms_internal_zzauu_zzb.zzbwp)) {
                return false;
            }
            if (!zzbxr.equals(this.zzbwq, com_google_android_gms_internal_zzauu_zzb.zzbwq)) {
                return false;
            }
            if (this.zzbwr == null) {
                if (com_google_android_gms_internal_zzauu_zzb.zzbwr != null) {
                    return false;
                }
            } else if (!this.zzbwr.equals(com_google_android_gms_internal_zzauu_zzb.zzbwr)) {
                return false;
            }
            if (this.zzbws == null) {
                if (com_google_android_gms_internal_zzauu_zzb.zzbws != null) {
                    return false;
                }
            } else if (!this.zzbws.equals(com_google_android_gms_internal_zzauu_zzb.zzbws)) {
                return false;
            }
            return (this.zzcuI == null || this.zzcuI.isEmpty()) ? com_google_android_gms_internal_zzauu_zzb.zzcuI == null || com_google_android_gms_internal_zzauu_zzb.zzcuI.isEmpty() : this.zzcuI.equals(com_google_android_gms_internal_zzauu_zzb.zzcuI);
        }

        public int hashCode() {
            int i = 0;
            int hashCode = ((this.zzbws == null ? 0 : this.zzbws.hashCode()) + (((this.zzbwr == null ? 0 : this.zzbwr.hashCode()) + (((((this.zzbwp == null ? 0 : this.zzbwp.hashCode()) + (((this.zzbwo == null ? 0 : this.zzbwo.hashCode()) + ((getClass().getName().hashCode() + 527) * 31)) * 31)) * 31) + zzbxr.hashCode(this.zzbwq)) * 31)) * 31)) * 31;
            if (!(this.zzcuI == null || this.zzcuI.isEmpty())) {
                i = this.zzcuI.hashCode();
            }
            return hashCode + i;
        }

        public zzb zzH(zzbxl com_google_android_gms_internal_zzbxl) throws IOException {
            while (true) {
                int zzaeo = com_google_android_gms_internal_zzbxl.zzaeo();
                switch (zzaeo) {
                    case 0:
                        break;
                    case 8:
                        this.zzbwo = Integer.valueOf(com_google_android_gms_internal_zzbxl.zzaes());
                        continue;
                    case 18:
                        this.zzbwp = com_google_android_gms_internal_zzbxl.readString();
                        continue;
                    case NalUnitTypes.NAL_TYPE_RSV_VCL26 /*26*/:
                        int zzb = zzbxw.zzb(com_google_android_gms_internal_zzbxl, 26);
                        zzaeo = this.zzbwq == null ? 0 : this.zzbwq.length;
                        Object obj = new zzc[(zzb + zzaeo)];
                        if (zzaeo != 0) {
                            System.arraycopy(this.zzbwq, 0, obj, 0, zzaeo);
                        }
                        while (zzaeo < obj.length - 1) {
                            obj[zzaeo] = new zzc();
                            com_google_android_gms_internal_zzbxl.zza(obj[zzaeo]);
                            com_google_android_gms_internal_zzbxl.zzaeo();
                            zzaeo++;
                        }
                        obj[zzaeo] = new zzc();
                        com_google_android_gms_internal_zzbxl.zza(obj[zzaeo]);
                        this.zzbwq = obj;
                        continue;
                    case 32:
                        this.zzbwr = Boolean.valueOf(com_google_android_gms_internal_zzbxl.zzaeu());
                        continue;
                    case 42:
                        if (this.zzbws == null) {
                            this.zzbws = new zzd();
                        }
                        com_google_android_gms_internal_zzbxl.zza(this.zzbws);
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

        public zzb zzNm() {
            this.zzbwo = null;
            this.zzbwp = null;
            this.zzbwq = zzc.zzNn();
            this.zzbwr = null;
            this.zzbws = null;
            this.zzcuI = null;
            this.zzcuR = -1;
            return this;
        }

        public void zza(zzbxm com_google_android_gms_internal_zzbxm) throws IOException {
            if (this.zzbwo != null) {
                com_google_android_gms_internal_zzbxm.zzJ(1, this.zzbwo.intValue());
            }
            if (this.zzbwp != null) {
                com_google_android_gms_internal_zzbxm.zzq(2, this.zzbwp);
            }
            if (this.zzbwq != null && this.zzbwq.length > 0) {
                for (zzbxt com_google_android_gms_internal_zzbxt : this.zzbwq) {
                    if (com_google_android_gms_internal_zzbxt != null) {
                        com_google_android_gms_internal_zzbxm.zza(3, com_google_android_gms_internal_zzbxt);
                    }
                }
            }
            if (this.zzbwr != null) {
                com_google_android_gms_internal_zzbxm.zzg(4, this.zzbwr.booleanValue());
            }
            if (this.zzbws != null) {
                com_google_android_gms_internal_zzbxm.zza(5, this.zzbws);
            }
            super.zza(com_google_android_gms_internal_zzbxm);
        }

        public /* synthetic */ zzbxt zzb(zzbxl com_google_android_gms_internal_zzbxl) throws IOException {
            return zzH(com_google_android_gms_internal_zzbxl);
        }

        protected int zzu() {
            int zzu = super.zzu();
            if (this.zzbwo != null) {
                zzu += zzbxm.zzL(1, this.zzbwo.intValue());
            }
            if (this.zzbwp != null) {
                zzu += zzbxm.zzr(2, this.zzbwp);
            }
            if (this.zzbwq != null && this.zzbwq.length > 0) {
                int i = zzu;
                for (zzbxt com_google_android_gms_internal_zzbxt : this.zzbwq) {
                    if (com_google_android_gms_internal_zzbxt != null) {
                        i += zzbxm.zzc(3, com_google_android_gms_internal_zzbxt);
                    }
                }
                zzu = i;
            }
            if (this.zzbwr != null) {
                zzu += zzbxm.zzh(4, this.zzbwr.booleanValue());
            }
            return this.zzbws != null ? zzu + zzbxm.zzc(5, this.zzbws) : zzu;
        }
    }

    public static final class zzc extends zzbxn<zzc> {
        private static volatile zzc[] zzbwt;
        public zzf zzbwu;
        public zzd zzbwv;
        public Boolean zzbww;
        public String zzbwx;

        public zzc() {
            zzNo();
        }

        public static zzc[] zzNn() {
            if (zzbwt == null) {
                synchronized (zzbxr.zzcuQ) {
                    if (zzbwt == null) {
                        zzbwt = new zzc[0];
                    }
                }
            }
            return zzbwt;
        }

        public boolean equals(Object obj) {
            if (obj == this) {
                return true;
            }
            if (!(obj instanceof zzc)) {
                return false;
            }
            zzc com_google_android_gms_internal_zzauu_zzc = (zzc) obj;
            if (this.zzbwu == null) {
                if (com_google_android_gms_internal_zzauu_zzc.zzbwu != null) {
                    return false;
                }
            } else if (!this.zzbwu.equals(com_google_android_gms_internal_zzauu_zzc.zzbwu)) {
                return false;
            }
            if (this.zzbwv == null) {
                if (com_google_android_gms_internal_zzauu_zzc.zzbwv != null) {
                    return false;
                }
            } else if (!this.zzbwv.equals(com_google_android_gms_internal_zzauu_zzc.zzbwv)) {
                return false;
            }
            if (this.zzbww == null) {
                if (com_google_android_gms_internal_zzauu_zzc.zzbww != null) {
                    return false;
                }
            } else if (!this.zzbww.equals(com_google_android_gms_internal_zzauu_zzc.zzbww)) {
                return false;
            }
            if (this.zzbwx == null) {
                if (com_google_android_gms_internal_zzauu_zzc.zzbwx != null) {
                    return false;
                }
            } else if (!this.zzbwx.equals(com_google_android_gms_internal_zzauu_zzc.zzbwx)) {
                return false;
            }
            return (this.zzcuI == null || this.zzcuI.isEmpty()) ? com_google_android_gms_internal_zzauu_zzc.zzcuI == null || com_google_android_gms_internal_zzauu_zzc.zzcuI.isEmpty() : this.zzcuI.equals(com_google_android_gms_internal_zzauu_zzc.zzcuI);
        }

        public int hashCode() {
            int i = 0;
            int hashCode = ((this.zzbwx == null ? 0 : this.zzbwx.hashCode()) + (((this.zzbww == null ? 0 : this.zzbww.hashCode()) + (((this.zzbwv == null ? 0 : this.zzbwv.hashCode()) + (((this.zzbwu == null ? 0 : this.zzbwu.hashCode()) + ((getClass().getName().hashCode() + 527) * 31)) * 31)) * 31)) * 31)) * 31;
            if (!(this.zzcuI == null || this.zzcuI.isEmpty())) {
                i = this.zzcuI.hashCode();
            }
            return hashCode + i;
        }

        public zzc zzI(zzbxl com_google_android_gms_internal_zzbxl) throws IOException {
            while (true) {
                int zzaeo = com_google_android_gms_internal_zzbxl.zzaeo();
                switch (zzaeo) {
                    case 0:
                        break;
                    case 10:
                        if (this.zzbwu == null) {
                            this.zzbwu = new zzf();
                        }
                        com_google_android_gms_internal_zzbxl.zza(this.zzbwu);
                        continue;
                    case 18:
                        if (this.zzbwv == null) {
                            this.zzbwv = new zzd();
                        }
                        com_google_android_gms_internal_zzbxl.zza(this.zzbwv);
                        continue;
                    case 24:
                        this.zzbww = Boolean.valueOf(com_google_android_gms_internal_zzbxl.zzaeu());
                        continue;
                    case 34:
                        this.zzbwx = com_google_android_gms_internal_zzbxl.readString();
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

        public zzc zzNo() {
            this.zzbwu = null;
            this.zzbwv = null;
            this.zzbww = null;
            this.zzbwx = null;
            this.zzcuI = null;
            this.zzcuR = -1;
            return this;
        }

        public void zza(zzbxm com_google_android_gms_internal_zzbxm) throws IOException {
            if (this.zzbwu != null) {
                com_google_android_gms_internal_zzbxm.zza(1, this.zzbwu);
            }
            if (this.zzbwv != null) {
                com_google_android_gms_internal_zzbxm.zza(2, this.zzbwv);
            }
            if (this.zzbww != null) {
                com_google_android_gms_internal_zzbxm.zzg(3, this.zzbww.booleanValue());
            }
            if (this.zzbwx != null) {
                com_google_android_gms_internal_zzbxm.zzq(4, this.zzbwx);
            }
            super.zza(com_google_android_gms_internal_zzbxm);
        }

        public /* synthetic */ zzbxt zzb(zzbxl com_google_android_gms_internal_zzbxl) throws IOException {
            return zzI(com_google_android_gms_internal_zzbxl);
        }

        protected int zzu() {
            int zzu = super.zzu();
            if (this.zzbwu != null) {
                zzu += zzbxm.zzc(1, this.zzbwu);
            }
            if (this.zzbwv != null) {
                zzu += zzbxm.zzc(2, this.zzbwv);
            }
            if (this.zzbww != null) {
                zzu += zzbxm.zzh(3, this.zzbww.booleanValue());
            }
            return this.zzbwx != null ? zzu + zzbxm.zzr(4, this.zzbwx) : zzu;
        }
    }

    public static final class zzd extends zzbxn<zzd> {
        public String zzbwA;
        public String zzbwB;
        public String zzbwC;
        public Integer zzbwy;
        public Boolean zzbwz;

        public zzd() {
            zzNp();
        }

        public boolean equals(Object obj) {
            if (obj == this) {
                return true;
            }
            if (!(obj instanceof zzd)) {
                return false;
            }
            zzd com_google_android_gms_internal_zzauu_zzd = (zzd) obj;
            if (this.zzbwy == null) {
                if (com_google_android_gms_internal_zzauu_zzd.zzbwy != null) {
                    return false;
                }
            } else if (!this.zzbwy.equals(com_google_android_gms_internal_zzauu_zzd.zzbwy)) {
                return false;
            }
            if (this.zzbwz == null) {
                if (com_google_android_gms_internal_zzauu_zzd.zzbwz != null) {
                    return false;
                }
            } else if (!this.zzbwz.equals(com_google_android_gms_internal_zzauu_zzd.zzbwz)) {
                return false;
            }
            if (this.zzbwA == null) {
                if (com_google_android_gms_internal_zzauu_zzd.zzbwA != null) {
                    return false;
                }
            } else if (!this.zzbwA.equals(com_google_android_gms_internal_zzauu_zzd.zzbwA)) {
                return false;
            }
            if (this.zzbwB == null) {
                if (com_google_android_gms_internal_zzauu_zzd.zzbwB != null) {
                    return false;
                }
            } else if (!this.zzbwB.equals(com_google_android_gms_internal_zzauu_zzd.zzbwB)) {
                return false;
            }
            if (this.zzbwC == null) {
                if (com_google_android_gms_internal_zzauu_zzd.zzbwC != null) {
                    return false;
                }
            } else if (!this.zzbwC.equals(com_google_android_gms_internal_zzauu_zzd.zzbwC)) {
                return false;
            }
            return (this.zzcuI == null || this.zzcuI.isEmpty()) ? com_google_android_gms_internal_zzauu_zzd.zzcuI == null || com_google_android_gms_internal_zzauu_zzd.zzcuI.isEmpty() : this.zzcuI.equals(com_google_android_gms_internal_zzauu_zzd.zzcuI);
        }

        public int hashCode() {
            int i = 0;
            int hashCode = ((this.zzbwC == null ? 0 : this.zzbwC.hashCode()) + (((this.zzbwB == null ? 0 : this.zzbwB.hashCode()) + (((this.zzbwA == null ? 0 : this.zzbwA.hashCode()) + (((this.zzbwz == null ? 0 : this.zzbwz.hashCode()) + (((this.zzbwy == null ? 0 : this.zzbwy.intValue()) + ((getClass().getName().hashCode() + 527) * 31)) * 31)) * 31)) * 31)) * 31)) * 31;
            if (!(this.zzcuI == null || this.zzcuI.isEmpty())) {
                i = this.zzcuI.hashCode();
            }
            return hashCode + i;
        }

        public zzd zzJ(zzbxl com_google_android_gms_internal_zzbxl) throws IOException {
            while (true) {
                int zzaeo = com_google_android_gms_internal_zzbxl.zzaeo();
                switch (zzaeo) {
                    case 0:
                        break;
                    case 8:
                        zzaeo = com_google_android_gms_internal_zzbxl.zzaes();
                        switch (zzaeo) {
                            case 0:
                            case 1:
                            case 2:
                            case 3:
                            case 4:
                                this.zzbwy = Integer.valueOf(zzaeo);
                                break;
                            default:
                                continue;
                        }
                    case 16:
                        this.zzbwz = Boolean.valueOf(com_google_android_gms_internal_zzbxl.zzaeu());
                        continue;
                    case NalUnitTypes.NAL_TYPE_RSV_VCL26 /*26*/:
                        this.zzbwA = com_google_android_gms_internal_zzbxl.readString();
                        continue;
                    case 34:
                        this.zzbwB = com_google_android_gms_internal_zzbxl.readString();
                        continue;
                    case 42:
                        this.zzbwC = com_google_android_gms_internal_zzbxl.readString();
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

        public zzd zzNp() {
            this.zzbwz = null;
            this.zzbwA = null;
            this.zzbwB = null;
            this.zzbwC = null;
            this.zzcuI = null;
            this.zzcuR = -1;
            return this;
        }

        public void zza(zzbxm com_google_android_gms_internal_zzbxm) throws IOException {
            if (this.zzbwy != null) {
                com_google_android_gms_internal_zzbxm.zzJ(1, this.zzbwy.intValue());
            }
            if (this.zzbwz != null) {
                com_google_android_gms_internal_zzbxm.zzg(2, this.zzbwz.booleanValue());
            }
            if (this.zzbwA != null) {
                com_google_android_gms_internal_zzbxm.zzq(3, this.zzbwA);
            }
            if (this.zzbwB != null) {
                com_google_android_gms_internal_zzbxm.zzq(4, this.zzbwB);
            }
            if (this.zzbwC != null) {
                com_google_android_gms_internal_zzbxm.zzq(5, this.zzbwC);
            }
            super.zza(com_google_android_gms_internal_zzbxm);
        }

        public /* synthetic */ zzbxt zzb(zzbxl com_google_android_gms_internal_zzbxl) throws IOException {
            return zzJ(com_google_android_gms_internal_zzbxl);
        }

        protected int zzu() {
            int zzu = super.zzu();
            if (this.zzbwy != null) {
                zzu += zzbxm.zzL(1, this.zzbwy.intValue());
            }
            if (this.zzbwz != null) {
                zzu += zzbxm.zzh(2, this.zzbwz.booleanValue());
            }
            if (this.zzbwA != null) {
                zzu += zzbxm.zzr(3, this.zzbwA);
            }
            if (this.zzbwB != null) {
                zzu += zzbxm.zzr(4, this.zzbwB);
            }
            return this.zzbwC != null ? zzu + zzbxm.zzr(5, this.zzbwC) : zzu;
        }
    }

    public static final class zze extends zzbxn<zze> {
        private static volatile zze[] zzbwD;
        public String zzbwE;
        public zzc zzbwF;
        public Integer zzbwo;

        public zze() {
            zzNr();
        }

        public static zze[] zzNq() {
            if (zzbwD == null) {
                synchronized (zzbxr.zzcuQ) {
                    if (zzbwD == null) {
                        zzbwD = new zze[0];
                    }
                }
            }
            return zzbwD;
        }

        public boolean equals(Object obj) {
            if (obj == this) {
                return true;
            }
            if (!(obj instanceof zze)) {
                return false;
            }
            zze com_google_android_gms_internal_zzauu_zze = (zze) obj;
            if (this.zzbwo == null) {
                if (com_google_android_gms_internal_zzauu_zze.zzbwo != null) {
                    return false;
                }
            } else if (!this.zzbwo.equals(com_google_android_gms_internal_zzauu_zze.zzbwo)) {
                return false;
            }
            if (this.zzbwE == null) {
                if (com_google_android_gms_internal_zzauu_zze.zzbwE != null) {
                    return false;
                }
            } else if (!this.zzbwE.equals(com_google_android_gms_internal_zzauu_zze.zzbwE)) {
                return false;
            }
            if (this.zzbwF == null) {
                if (com_google_android_gms_internal_zzauu_zze.zzbwF != null) {
                    return false;
                }
            } else if (!this.zzbwF.equals(com_google_android_gms_internal_zzauu_zze.zzbwF)) {
                return false;
            }
            return (this.zzcuI == null || this.zzcuI.isEmpty()) ? com_google_android_gms_internal_zzauu_zze.zzcuI == null || com_google_android_gms_internal_zzauu_zze.zzcuI.isEmpty() : this.zzcuI.equals(com_google_android_gms_internal_zzauu_zze.zzcuI);
        }

        public int hashCode() {
            int i = 0;
            int hashCode = ((this.zzbwF == null ? 0 : this.zzbwF.hashCode()) + (((this.zzbwE == null ? 0 : this.zzbwE.hashCode()) + (((this.zzbwo == null ? 0 : this.zzbwo.hashCode()) + ((getClass().getName().hashCode() + 527) * 31)) * 31)) * 31)) * 31;
            if (!(this.zzcuI == null || this.zzcuI.isEmpty())) {
                i = this.zzcuI.hashCode();
            }
            return hashCode + i;
        }

        public zze zzK(zzbxl com_google_android_gms_internal_zzbxl) throws IOException {
            while (true) {
                int zzaeo = com_google_android_gms_internal_zzbxl.zzaeo();
                switch (zzaeo) {
                    case 0:
                        break;
                    case 8:
                        this.zzbwo = Integer.valueOf(com_google_android_gms_internal_zzbxl.zzaes());
                        continue;
                    case 18:
                        this.zzbwE = com_google_android_gms_internal_zzbxl.readString();
                        continue;
                    case NalUnitTypes.NAL_TYPE_RSV_VCL26 /*26*/:
                        if (this.zzbwF == null) {
                            this.zzbwF = new zzc();
                        }
                        com_google_android_gms_internal_zzbxl.zza(this.zzbwF);
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

        public zze zzNr() {
            this.zzbwo = null;
            this.zzbwE = null;
            this.zzbwF = null;
            this.zzcuI = null;
            this.zzcuR = -1;
            return this;
        }

        public void zza(zzbxm com_google_android_gms_internal_zzbxm) throws IOException {
            if (this.zzbwo != null) {
                com_google_android_gms_internal_zzbxm.zzJ(1, this.zzbwo.intValue());
            }
            if (this.zzbwE != null) {
                com_google_android_gms_internal_zzbxm.zzq(2, this.zzbwE);
            }
            if (this.zzbwF != null) {
                com_google_android_gms_internal_zzbxm.zza(3, this.zzbwF);
            }
            super.zza(com_google_android_gms_internal_zzbxm);
        }

        public /* synthetic */ zzbxt zzb(zzbxl com_google_android_gms_internal_zzbxl) throws IOException {
            return zzK(com_google_android_gms_internal_zzbxl);
        }

        protected int zzu() {
            int zzu = super.zzu();
            if (this.zzbwo != null) {
                zzu += zzbxm.zzL(1, this.zzbwo.intValue());
            }
            if (this.zzbwE != null) {
                zzu += zzbxm.zzr(2, this.zzbwE);
            }
            return this.zzbwF != null ? zzu + zzbxm.zzc(3, this.zzbwF) : zzu;
        }
    }

    public static final class zzf extends zzbxn<zzf> {
        public Integer zzbwG;
        public String zzbwH;
        public Boolean zzbwI;
        public String[] zzbwJ;

        public zzf() {
            zzNs();
        }

        public boolean equals(Object obj) {
            if (obj == this) {
                return true;
            }
            if (!(obj instanceof zzf)) {
                return false;
            }
            zzf com_google_android_gms_internal_zzauu_zzf = (zzf) obj;
            if (this.zzbwG == null) {
                if (com_google_android_gms_internal_zzauu_zzf.zzbwG != null) {
                    return false;
                }
            } else if (!this.zzbwG.equals(com_google_android_gms_internal_zzauu_zzf.zzbwG)) {
                return false;
            }
            if (this.zzbwH == null) {
                if (com_google_android_gms_internal_zzauu_zzf.zzbwH != null) {
                    return false;
                }
            } else if (!this.zzbwH.equals(com_google_android_gms_internal_zzauu_zzf.zzbwH)) {
                return false;
            }
            if (this.zzbwI == null) {
                if (com_google_android_gms_internal_zzauu_zzf.zzbwI != null) {
                    return false;
                }
            } else if (!this.zzbwI.equals(com_google_android_gms_internal_zzauu_zzf.zzbwI)) {
                return false;
            }
            return zzbxr.equals(this.zzbwJ, com_google_android_gms_internal_zzauu_zzf.zzbwJ) ? (this.zzcuI == null || this.zzcuI.isEmpty()) ? com_google_android_gms_internal_zzauu_zzf.zzcuI == null || com_google_android_gms_internal_zzauu_zzf.zzcuI.isEmpty() : this.zzcuI.equals(com_google_android_gms_internal_zzauu_zzf.zzcuI) : false;
        }

        public int hashCode() {
            int i = 0;
            int hashCode = ((((this.zzbwI == null ? 0 : this.zzbwI.hashCode()) + (((this.zzbwH == null ? 0 : this.zzbwH.hashCode()) + (((this.zzbwG == null ? 0 : this.zzbwG.intValue()) + ((getClass().getName().hashCode() + 527) * 31)) * 31)) * 31)) * 31) + zzbxr.hashCode(this.zzbwJ)) * 31;
            if (!(this.zzcuI == null || this.zzcuI.isEmpty())) {
                i = this.zzcuI.hashCode();
            }
            return hashCode + i;
        }

        public zzf zzL(zzbxl com_google_android_gms_internal_zzbxl) throws IOException {
            while (true) {
                int zzaeo = com_google_android_gms_internal_zzbxl.zzaeo();
                switch (zzaeo) {
                    case 0:
                        break;
                    case 8:
                        zzaeo = com_google_android_gms_internal_zzbxl.zzaes();
                        switch (zzaeo) {
                            case 0:
                            case 1:
                            case 2:
                            case 3:
                            case 4:
                            case 5:
                            case 6:
                                this.zzbwG = Integer.valueOf(zzaeo);
                                break;
                            default:
                                continue;
                        }
                    case 18:
                        this.zzbwH = com_google_android_gms_internal_zzbxl.readString();
                        continue;
                    case 24:
                        this.zzbwI = Boolean.valueOf(com_google_android_gms_internal_zzbxl.zzaeu());
                        continue;
                    case 34:
                        int zzb = zzbxw.zzb(com_google_android_gms_internal_zzbxl, 34);
                        zzaeo = this.zzbwJ == null ? 0 : this.zzbwJ.length;
                        Object obj = new String[(zzb + zzaeo)];
                        if (zzaeo != 0) {
                            System.arraycopy(this.zzbwJ, 0, obj, 0, zzaeo);
                        }
                        while (zzaeo < obj.length - 1) {
                            obj[zzaeo] = com_google_android_gms_internal_zzbxl.readString();
                            com_google_android_gms_internal_zzbxl.zzaeo();
                            zzaeo++;
                        }
                        obj[zzaeo] = com_google_android_gms_internal_zzbxl.readString();
                        this.zzbwJ = obj;
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

        public zzf zzNs() {
            this.zzbwH = null;
            this.zzbwI = null;
            this.zzbwJ = zzbxw.zzcvb;
            this.zzcuI = null;
            this.zzcuR = -1;
            return this;
        }

        public void zza(zzbxm com_google_android_gms_internal_zzbxm) throws IOException {
            if (this.zzbwG != null) {
                com_google_android_gms_internal_zzbxm.zzJ(1, this.zzbwG.intValue());
            }
            if (this.zzbwH != null) {
                com_google_android_gms_internal_zzbxm.zzq(2, this.zzbwH);
            }
            if (this.zzbwI != null) {
                com_google_android_gms_internal_zzbxm.zzg(3, this.zzbwI.booleanValue());
            }
            if (this.zzbwJ != null && this.zzbwJ.length > 0) {
                for (String str : this.zzbwJ) {
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
            if (this.zzbwG != null) {
                zzu += zzbxm.zzL(1, this.zzbwG.intValue());
            }
            if (this.zzbwH != null) {
                zzu += zzbxm.zzr(2, this.zzbwH);
            }
            if (this.zzbwI != null) {
                zzu += zzbxm.zzh(3, this.zzbwI.booleanValue());
            }
            if (this.zzbwJ == null || this.zzbwJ.length <= 0) {
                return zzu;
            }
            int i2 = 0;
            int i3 = 0;
            while (i < this.zzbwJ.length) {
                String str = this.zzbwJ[i];
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
