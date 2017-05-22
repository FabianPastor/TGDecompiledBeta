package com.google.android.gms.internal;

import com.googlecode.mp4parser.authoring.tracks.h265.NalUnitTypes;
import java.io.IOException;

public interface zzauu {

    public static final class zza extends zzbyd<zza> {
        private static volatile zza[] zzbwm;
        public Integer zzbwn;
        public zze[] zzbwo;
        public zzb[] zzbwp;

        public zza() {
            zzNm();
        }

        public static zza[] zzNl() {
            if (zzbwm == null) {
                synchronized (zzbyh.zzcwK) {
                    if (zzbwm == null) {
                        zzbwm = new zza[0];
                    }
                }
            }
            return zzbwm;
        }

        public boolean equals(Object obj) {
            if (obj == this) {
                return true;
            }
            if (!(obj instanceof zza)) {
                return false;
            }
            zza com_google_android_gms_internal_zzauu_zza = (zza) obj;
            if (this.zzbwn == null) {
                if (com_google_android_gms_internal_zzauu_zza.zzbwn != null) {
                    return false;
                }
            } else if (!this.zzbwn.equals(com_google_android_gms_internal_zzauu_zza.zzbwn)) {
                return false;
            }
            return (zzbyh.equals(this.zzbwo, com_google_android_gms_internal_zzauu_zza.zzbwo) && zzbyh.equals(this.zzbwp, com_google_android_gms_internal_zzauu_zza.zzbwp)) ? (this.zzcwC == null || this.zzcwC.isEmpty()) ? com_google_android_gms_internal_zzauu_zza.zzcwC == null || com_google_android_gms_internal_zzauu_zza.zzcwC.isEmpty() : this.zzcwC.equals(com_google_android_gms_internal_zzauu_zza.zzcwC) : false;
        }

        public int hashCode() {
            int i = 0;
            int hashCode = ((((((this.zzbwn == null ? 0 : this.zzbwn.hashCode()) + ((getClass().getName().hashCode() + 527) * 31)) * 31) + zzbyh.hashCode(this.zzbwo)) * 31) + zzbyh.hashCode(this.zzbwp)) * 31;
            if (!(this.zzcwC == null || this.zzcwC.isEmpty())) {
                i = this.zzcwC.hashCode();
            }
            return hashCode + i;
        }

        public zza zzG(zzbyb com_google_android_gms_internal_zzbyb) throws IOException {
            while (true) {
                int zzaeW = com_google_android_gms_internal_zzbyb.zzaeW();
                int zzb;
                Object obj;
                switch (zzaeW) {
                    case 0:
                        break;
                    case 8:
                        this.zzbwn = Integer.valueOf(com_google_android_gms_internal_zzbyb.zzafa());
                        continue;
                    case 18:
                        zzb = zzbym.zzb(com_google_android_gms_internal_zzbyb, 18);
                        zzaeW = this.zzbwo == null ? 0 : this.zzbwo.length;
                        obj = new zze[(zzb + zzaeW)];
                        if (zzaeW != 0) {
                            System.arraycopy(this.zzbwo, 0, obj, 0, zzaeW);
                        }
                        while (zzaeW < obj.length - 1) {
                            obj[zzaeW] = new zze();
                            com_google_android_gms_internal_zzbyb.zza(obj[zzaeW]);
                            com_google_android_gms_internal_zzbyb.zzaeW();
                            zzaeW++;
                        }
                        obj[zzaeW] = new zze();
                        com_google_android_gms_internal_zzbyb.zza(obj[zzaeW]);
                        this.zzbwo = obj;
                        continue;
                    case NalUnitTypes.NAL_TYPE_RSV_VCL26 /*26*/:
                        zzb = zzbym.zzb(com_google_android_gms_internal_zzbyb, 26);
                        zzaeW = this.zzbwp == null ? 0 : this.zzbwp.length;
                        obj = new zzb[(zzb + zzaeW)];
                        if (zzaeW != 0) {
                            System.arraycopy(this.zzbwp, 0, obj, 0, zzaeW);
                        }
                        while (zzaeW < obj.length - 1) {
                            obj[zzaeW] = new zzb();
                            com_google_android_gms_internal_zzbyb.zza(obj[zzaeW]);
                            com_google_android_gms_internal_zzbyb.zzaeW();
                            zzaeW++;
                        }
                        obj[zzaeW] = new zzb();
                        com_google_android_gms_internal_zzbyb.zza(obj[zzaeW]);
                        this.zzbwp = obj;
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

        public zza zzNm() {
            this.zzbwn = null;
            this.zzbwo = zze.zzNs();
            this.zzbwp = zzb.zzNn();
            this.zzcwC = null;
            this.zzcwL = -1;
            return this;
        }

        public void zza(zzbyc com_google_android_gms_internal_zzbyc) throws IOException {
            int i = 0;
            if (this.zzbwn != null) {
                com_google_android_gms_internal_zzbyc.zzJ(1, this.zzbwn.intValue());
            }
            if (this.zzbwo != null && this.zzbwo.length > 0) {
                for (zzbyj com_google_android_gms_internal_zzbyj : this.zzbwo) {
                    if (com_google_android_gms_internal_zzbyj != null) {
                        com_google_android_gms_internal_zzbyc.zza(2, com_google_android_gms_internal_zzbyj);
                    }
                }
            }
            if (this.zzbwp != null && this.zzbwp.length > 0) {
                while (i < this.zzbwp.length) {
                    zzbyj com_google_android_gms_internal_zzbyj2 = this.zzbwp[i];
                    if (com_google_android_gms_internal_zzbyj2 != null) {
                        com_google_android_gms_internal_zzbyc.zza(3, com_google_android_gms_internal_zzbyj2);
                    }
                    i++;
                }
            }
            super.zza(com_google_android_gms_internal_zzbyc);
        }

        public /* synthetic */ zzbyj zzb(zzbyb com_google_android_gms_internal_zzbyb) throws IOException {
            return zzG(com_google_android_gms_internal_zzbyb);
        }

        protected int zzu() {
            int i = 0;
            int zzu = super.zzu();
            if (this.zzbwn != null) {
                zzu += zzbyc.zzL(1, this.zzbwn.intValue());
            }
            if (this.zzbwo != null && this.zzbwo.length > 0) {
                int i2 = zzu;
                for (zzbyj com_google_android_gms_internal_zzbyj : this.zzbwo) {
                    if (com_google_android_gms_internal_zzbyj != null) {
                        i2 += zzbyc.zzc(2, com_google_android_gms_internal_zzbyj);
                    }
                }
                zzu = i2;
            }
            if (this.zzbwp != null && this.zzbwp.length > 0) {
                while (i < this.zzbwp.length) {
                    zzbyj com_google_android_gms_internal_zzbyj2 = this.zzbwp[i];
                    if (com_google_android_gms_internal_zzbyj2 != null) {
                        zzu += zzbyc.zzc(3, com_google_android_gms_internal_zzbyj2);
                    }
                    i++;
                }
            }
            return zzu;
        }
    }

    public static final class zzb extends zzbyd<zzb> {
        private static volatile zzb[] zzbwq;
        public Integer zzbwr;
        public String zzbws;
        public zzc[] zzbwt;
        public Boolean zzbwu;
        public zzd zzbwv;

        public zzb() {
            zzNo();
        }

        public static zzb[] zzNn() {
            if (zzbwq == null) {
                synchronized (zzbyh.zzcwK) {
                    if (zzbwq == null) {
                        zzbwq = new zzb[0];
                    }
                }
            }
            return zzbwq;
        }

        public boolean equals(Object obj) {
            if (obj == this) {
                return true;
            }
            if (!(obj instanceof zzb)) {
                return false;
            }
            zzb com_google_android_gms_internal_zzauu_zzb = (zzb) obj;
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
            if (!zzbyh.equals(this.zzbwt, com_google_android_gms_internal_zzauu_zzb.zzbwt)) {
                return false;
            }
            if (this.zzbwu == null) {
                if (com_google_android_gms_internal_zzauu_zzb.zzbwu != null) {
                    return false;
                }
            } else if (!this.zzbwu.equals(com_google_android_gms_internal_zzauu_zzb.zzbwu)) {
                return false;
            }
            if (this.zzbwv == null) {
                if (com_google_android_gms_internal_zzauu_zzb.zzbwv != null) {
                    return false;
                }
            } else if (!this.zzbwv.equals(com_google_android_gms_internal_zzauu_zzb.zzbwv)) {
                return false;
            }
            return (this.zzcwC == null || this.zzcwC.isEmpty()) ? com_google_android_gms_internal_zzauu_zzb.zzcwC == null || com_google_android_gms_internal_zzauu_zzb.zzcwC.isEmpty() : this.zzcwC.equals(com_google_android_gms_internal_zzauu_zzb.zzcwC);
        }

        public int hashCode() {
            int i = 0;
            int hashCode = ((this.zzbwv == null ? 0 : this.zzbwv.hashCode()) + (((this.zzbwu == null ? 0 : this.zzbwu.hashCode()) + (((((this.zzbws == null ? 0 : this.zzbws.hashCode()) + (((this.zzbwr == null ? 0 : this.zzbwr.hashCode()) + ((getClass().getName().hashCode() + 527) * 31)) * 31)) * 31) + zzbyh.hashCode(this.zzbwt)) * 31)) * 31)) * 31;
            if (!(this.zzcwC == null || this.zzcwC.isEmpty())) {
                i = this.zzcwC.hashCode();
            }
            return hashCode + i;
        }

        public zzb zzH(zzbyb com_google_android_gms_internal_zzbyb) throws IOException {
            while (true) {
                int zzaeW = com_google_android_gms_internal_zzbyb.zzaeW();
                switch (zzaeW) {
                    case 0:
                        break;
                    case 8:
                        this.zzbwr = Integer.valueOf(com_google_android_gms_internal_zzbyb.zzafa());
                        continue;
                    case 18:
                        this.zzbws = com_google_android_gms_internal_zzbyb.readString();
                        continue;
                    case NalUnitTypes.NAL_TYPE_RSV_VCL26 /*26*/:
                        int zzb = zzbym.zzb(com_google_android_gms_internal_zzbyb, 26);
                        zzaeW = this.zzbwt == null ? 0 : this.zzbwt.length;
                        Object obj = new zzc[(zzb + zzaeW)];
                        if (zzaeW != 0) {
                            System.arraycopy(this.zzbwt, 0, obj, 0, zzaeW);
                        }
                        while (zzaeW < obj.length - 1) {
                            obj[zzaeW] = new zzc();
                            com_google_android_gms_internal_zzbyb.zza(obj[zzaeW]);
                            com_google_android_gms_internal_zzbyb.zzaeW();
                            zzaeW++;
                        }
                        obj[zzaeW] = new zzc();
                        com_google_android_gms_internal_zzbyb.zza(obj[zzaeW]);
                        this.zzbwt = obj;
                        continue;
                    case 32:
                        this.zzbwu = Boolean.valueOf(com_google_android_gms_internal_zzbyb.zzafc());
                        continue;
                    case 42:
                        if (this.zzbwv == null) {
                            this.zzbwv = new zzd();
                        }
                        com_google_android_gms_internal_zzbyb.zza(this.zzbwv);
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

        public zzb zzNo() {
            this.zzbwr = null;
            this.zzbws = null;
            this.zzbwt = zzc.zzNp();
            this.zzbwu = null;
            this.zzbwv = null;
            this.zzcwC = null;
            this.zzcwL = -1;
            return this;
        }

        public void zza(zzbyc com_google_android_gms_internal_zzbyc) throws IOException {
            if (this.zzbwr != null) {
                com_google_android_gms_internal_zzbyc.zzJ(1, this.zzbwr.intValue());
            }
            if (this.zzbws != null) {
                com_google_android_gms_internal_zzbyc.zzq(2, this.zzbws);
            }
            if (this.zzbwt != null && this.zzbwt.length > 0) {
                for (zzbyj com_google_android_gms_internal_zzbyj : this.zzbwt) {
                    if (com_google_android_gms_internal_zzbyj != null) {
                        com_google_android_gms_internal_zzbyc.zza(3, com_google_android_gms_internal_zzbyj);
                    }
                }
            }
            if (this.zzbwu != null) {
                com_google_android_gms_internal_zzbyc.zzg(4, this.zzbwu.booleanValue());
            }
            if (this.zzbwv != null) {
                com_google_android_gms_internal_zzbyc.zza(5, this.zzbwv);
            }
            super.zza(com_google_android_gms_internal_zzbyc);
        }

        public /* synthetic */ zzbyj zzb(zzbyb com_google_android_gms_internal_zzbyb) throws IOException {
            return zzH(com_google_android_gms_internal_zzbyb);
        }

        protected int zzu() {
            int zzu = super.zzu();
            if (this.zzbwr != null) {
                zzu += zzbyc.zzL(1, this.zzbwr.intValue());
            }
            if (this.zzbws != null) {
                zzu += zzbyc.zzr(2, this.zzbws);
            }
            if (this.zzbwt != null && this.zzbwt.length > 0) {
                int i = zzu;
                for (zzbyj com_google_android_gms_internal_zzbyj : this.zzbwt) {
                    if (com_google_android_gms_internal_zzbyj != null) {
                        i += zzbyc.zzc(3, com_google_android_gms_internal_zzbyj);
                    }
                }
                zzu = i;
            }
            if (this.zzbwu != null) {
                zzu += zzbyc.zzh(4, this.zzbwu.booleanValue());
            }
            return this.zzbwv != null ? zzu + zzbyc.zzc(5, this.zzbwv) : zzu;
        }
    }

    public static final class zzc extends zzbyd<zzc> {
        private static volatile zzc[] zzbww;
        public String zzbwA;
        public zzf zzbwx;
        public zzd zzbwy;
        public Boolean zzbwz;

        public zzc() {
            zzNq();
        }

        public static zzc[] zzNp() {
            if (zzbww == null) {
                synchronized (zzbyh.zzcwK) {
                    if (zzbww == null) {
                        zzbww = new zzc[0];
                    }
                }
            }
            return zzbww;
        }

        public boolean equals(Object obj) {
            if (obj == this) {
                return true;
            }
            if (!(obj instanceof zzc)) {
                return false;
            }
            zzc com_google_android_gms_internal_zzauu_zzc = (zzc) obj;
            if (this.zzbwx == null) {
                if (com_google_android_gms_internal_zzauu_zzc.zzbwx != null) {
                    return false;
                }
            } else if (!this.zzbwx.equals(com_google_android_gms_internal_zzauu_zzc.zzbwx)) {
                return false;
            }
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
            return (this.zzcwC == null || this.zzcwC.isEmpty()) ? com_google_android_gms_internal_zzauu_zzc.zzcwC == null || com_google_android_gms_internal_zzauu_zzc.zzcwC.isEmpty() : this.zzcwC.equals(com_google_android_gms_internal_zzauu_zzc.zzcwC);
        }

        public int hashCode() {
            int i = 0;
            int hashCode = ((this.zzbwA == null ? 0 : this.zzbwA.hashCode()) + (((this.zzbwz == null ? 0 : this.zzbwz.hashCode()) + (((this.zzbwy == null ? 0 : this.zzbwy.hashCode()) + (((this.zzbwx == null ? 0 : this.zzbwx.hashCode()) + ((getClass().getName().hashCode() + 527) * 31)) * 31)) * 31)) * 31)) * 31;
            if (!(this.zzcwC == null || this.zzcwC.isEmpty())) {
                i = this.zzcwC.hashCode();
            }
            return hashCode + i;
        }

        public zzc zzI(zzbyb com_google_android_gms_internal_zzbyb) throws IOException {
            while (true) {
                int zzaeW = com_google_android_gms_internal_zzbyb.zzaeW();
                switch (zzaeW) {
                    case 0:
                        break;
                    case 10:
                        if (this.zzbwx == null) {
                            this.zzbwx = new zzf();
                        }
                        com_google_android_gms_internal_zzbyb.zza(this.zzbwx);
                        continue;
                    case 18:
                        if (this.zzbwy == null) {
                            this.zzbwy = new zzd();
                        }
                        com_google_android_gms_internal_zzbyb.zza(this.zzbwy);
                        continue;
                    case 24:
                        this.zzbwz = Boolean.valueOf(com_google_android_gms_internal_zzbyb.zzafc());
                        continue;
                    case 34:
                        this.zzbwA = com_google_android_gms_internal_zzbyb.readString();
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

        public zzc zzNq() {
            this.zzbwx = null;
            this.zzbwy = null;
            this.zzbwz = null;
            this.zzbwA = null;
            this.zzcwC = null;
            this.zzcwL = -1;
            return this;
        }

        public void zza(zzbyc com_google_android_gms_internal_zzbyc) throws IOException {
            if (this.zzbwx != null) {
                com_google_android_gms_internal_zzbyc.zza(1, this.zzbwx);
            }
            if (this.zzbwy != null) {
                com_google_android_gms_internal_zzbyc.zza(2, this.zzbwy);
            }
            if (this.zzbwz != null) {
                com_google_android_gms_internal_zzbyc.zzg(3, this.zzbwz.booleanValue());
            }
            if (this.zzbwA != null) {
                com_google_android_gms_internal_zzbyc.zzq(4, this.zzbwA);
            }
            super.zza(com_google_android_gms_internal_zzbyc);
        }

        public /* synthetic */ zzbyj zzb(zzbyb com_google_android_gms_internal_zzbyb) throws IOException {
            return zzI(com_google_android_gms_internal_zzbyb);
        }

        protected int zzu() {
            int zzu = super.zzu();
            if (this.zzbwx != null) {
                zzu += zzbyc.zzc(1, this.zzbwx);
            }
            if (this.zzbwy != null) {
                zzu += zzbyc.zzc(2, this.zzbwy);
            }
            if (this.zzbwz != null) {
                zzu += zzbyc.zzh(3, this.zzbwz.booleanValue());
            }
            return this.zzbwA != null ? zzu + zzbyc.zzr(4, this.zzbwA) : zzu;
        }
    }

    public static final class zzd extends zzbyd<zzd> {
        public Integer zzbwB;
        public Boolean zzbwC;
        public String zzbwD;
        public String zzbwE;
        public String zzbwF;

        public zzd() {
            zzNr();
        }

        public boolean equals(Object obj) {
            if (obj == this) {
                return true;
            }
            if (!(obj instanceof zzd)) {
                return false;
            }
            zzd com_google_android_gms_internal_zzauu_zzd = (zzd) obj;
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
            return (this.zzcwC == null || this.zzcwC.isEmpty()) ? com_google_android_gms_internal_zzauu_zzd.zzcwC == null || com_google_android_gms_internal_zzauu_zzd.zzcwC.isEmpty() : this.zzcwC.equals(com_google_android_gms_internal_zzauu_zzd.zzcwC);
        }

        public int hashCode() {
            int i = 0;
            int hashCode = ((this.zzbwF == null ? 0 : this.zzbwF.hashCode()) + (((this.zzbwE == null ? 0 : this.zzbwE.hashCode()) + (((this.zzbwD == null ? 0 : this.zzbwD.hashCode()) + (((this.zzbwC == null ? 0 : this.zzbwC.hashCode()) + (((this.zzbwB == null ? 0 : this.zzbwB.intValue()) + ((getClass().getName().hashCode() + 527) * 31)) * 31)) * 31)) * 31)) * 31)) * 31;
            if (!(this.zzcwC == null || this.zzcwC.isEmpty())) {
                i = this.zzcwC.hashCode();
            }
            return hashCode + i;
        }

        public zzd zzJ(zzbyb com_google_android_gms_internal_zzbyb) throws IOException {
            while (true) {
                int zzaeW = com_google_android_gms_internal_zzbyb.zzaeW();
                switch (zzaeW) {
                    case 0:
                        break;
                    case 8:
                        zzaeW = com_google_android_gms_internal_zzbyb.zzafa();
                        switch (zzaeW) {
                            case 0:
                            case 1:
                            case 2:
                            case 3:
                            case 4:
                                this.zzbwB = Integer.valueOf(zzaeW);
                                break;
                            default:
                                continue;
                        }
                    case 16:
                        this.zzbwC = Boolean.valueOf(com_google_android_gms_internal_zzbyb.zzafc());
                        continue;
                    case NalUnitTypes.NAL_TYPE_RSV_VCL26 /*26*/:
                        this.zzbwD = com_google_android_gms_internal_zzbyb.readString();
                        continue;
                    case 34:
                        this.zzbwE = com_google_android_gms_internal_zzbyb.readString();
                        continue;
                    case 42:
                        this.zzbwF = com_google_android_gms_internal_zzbyb.readString();
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

        public zzd zzNr() {
            this.zzbwC = null;
            this.zzbwD = null;
            this.zzbwE = null;
            this.zzbwF = null;
            this.zzcwC = null;
            this.zzcwL = -1;
            return this;
        }

        public void zza(zzbyc com_google_android_gms_internal_zzbyc) throws IOException {
            if (this.zzbwB != null) {
                com_google_android_gms_internal_zzbyc.zzJ(1, this.zzbwB.intValue());
            }
            if (this.zzbwC != null) {
                com_google_android_gms_internal_zzbyc.zzg(2, this.zzbwC.booleanValue());
            }
            if (this.zzbwD != null) {
                com_google_android_gms_internal_zzbyc.zzq(3, this.zzbwD);
            }
            if (this.zzbwE != null) {
                com_google_android_gms_internal_zzbyc.zzq(4, this.zzbwE);
            }
            if (this.zzbwF != null) {
                com_google_android_gms_internal_zzbyc.zzq(5, this.zzbwF);
            }
            super.zza(com_google_android_gms_internal_zzbyc);
        }

        public /* synthetic */ zzbyj zzb(zzbyb com_google_android_gms_internal_zzbyb) throws IOException {
            return zzJ(com_google_android_gms_internal_zzbyb);
        }

        protected int zzu() {
            int zzu = super.zzu();
            if (this.zzbwB != null) {
                zzu += zzbyc.zzL(1, this.zzbwB.intValue());
            }
            if (this.zzbwC != null) {
                zzu += zzbyc.zzh(2, this.zzbwC.booleanValue());
            }
            if (this.zzbwD != null) {
                zzu += zzbyc.zzr(3, this.zzbwD);
            }
            if (this.zzbwE != null) {
                zzu += zzbyc.zzr(4, this.zzbwE);
            }
            return this.zzbwF != null ? zzu + zzbyc.zzr(5, this.zzbwF) : zzu;
        }
    }

    public static final class zze extends zzbyd<zze> {
        private static volatile zze[] zzbwG;
        public String zzbwH;
        public zzc zzbwI;
        public Integer zzbwr;

        public zze() {
            zzNt();
        }

        public static zze[] zzNs() {
            if (zzbwG == null) {
                synchronized (zzbyh.zzcwK) {
                    if (zzbwG == null) {
                        zzbwG = new zze[0];
                    }
                }
            }
            return zzbwG;
        }

        public boolean equals(Object obj) {
            if (obj == this) {
                return true;
            }
            if (!(obj instanceof zze)) {
                return false;
            }
            zze com_google_android_gms_internal_zzauu_zze = (zze) obj;
            if (this.zzbwr == null) {
                if (com_google_android_gms_internal_zzauu_zze.zzbwr != null) {
                    return false;
                }
            } else if (!this.zzbwr.equals(com_google_android_gms_internal_zzauu_zze.zzbwr)) {
                return false;
            }
            if (this.zzbwH == null) {
                if (com_google_android_gms_internal_zzauu_zze.zzbwH != null) {
                    return false;
                }
            } else if (!this.zzbwH.equals(com_google_android_gms_internal_zzauu_zze.zzbwH)) {
                return false;
            }
            if (this.zzbwI == null) {
                if (com_google_android_gms_internal_zzauu_zze.zzbwI != null) {
                    return false;
                }
            } else if (!this.zzbwI.equals(com_google_android_gms_internal_zzauu_zze.zzbwI)) {
                return false;
            }
            return (this.zzcwC == null || this.zzcwC.isEmpty()) ? com_google_android_gms_internal_zzauu_zze.zzcwC == null || com_google_android_gms_internal_zzauu_zze.zzcwC.isEmpty() : this.zzcwC.equals(com_google_android_gms_internal_zzauu_zze.zzcwC);
        }

        public int hashCode() {
            int i = 0;
            int hashCode = ((this.zzbwI == null ? 0 : this.zzbwI.hashCode()) + (((this.zzbwH == null ? 0 : this.zzbwH.hashCode()) + (((this.zzbwr == null ? 0 : this.zzbwr.hashCode()) + ((getClass().getName().hashCode() + 527) * 31)) * 31)) * 31)) * 31;
            if (!(this.zzcwC == null || this.zzcwC.isEmpty())) {
                i = this.zzcwC.hashCode();
            }
            return hashCode + i;
        }

        public zze zzK(zzbyb com_google_android_gms_internal_zzbyb) throws IOException {
            while (true) {
                int zzaeW = com_google_android_gms_internal_zzbyb.zzaeW();
                switch (zzaeW) {
                    case 0:
                        break;
                    case 8:
                        this.zzbwr = Integer.valueOf(com_google_android_gms_internal_zzbyb.zzafa());
                        continue;
                    case 18:
                        this.zzbwH = com_google_android_gms_internal_zzbyb.readString();
                        continue;
                    case NalUnitTypes.NAL_TYPE_RSV_VCL26 /*26*/:
                        if (this.zzbwI == null) {
                            this.zzbwI = new zzc();
                        }
                        com_google_android_gms_internal_zzbyb.zza(this.zzbwI);
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

        public zze zzNt() {
            this.zzbwr = null;
            this.zzbwH = null;
            this.zzbwI = null;
            this.zzcwC = null;
            this.zzcwL = -1;
            return this;
        }

        public void zza(zzbyc com_google_android_gms_internal_zzbyc) throws IOException {
            if (this.zzbwr != null) {
                com_google_android_gms_internal_zzbyc.zzJ(1, this.zzbwr.intValue());
            }
            if (this.zzbwH != null) {
                com_google_android_gms_internal_zzbyc.zzq(2, this.zzbwH);
            }
            if (this.zzbwI != null) {
                com_google_android_gms_internal_zzbyc.zza(3, this.zzbwI);
            }
            super.zza(com_google_android_gms_internal_zzbyc);
        }

        public /* synthetic */ zzbyj zzb(zzbyb com_google_android_gms_internal_zzbyb) throws IOException {
            return zzK(com_google_android_gms_internal_zzbyb);
        }

        protected int zzu() {
            int zzu = super.zzu();
            if (this.zzbwr != null) {
                zzu += zzbyc.zzL(1, this.zzbwr.intValue());
            }
            if (this.zzbwH != null) {
                zzu += zzbyc.zzr(2, this.zzbwH);
            }
            return this.zzbwI != null ? zzu + zzbyc.zzc(3, this.zzbwI) : zzu;
        }
    }

    public static final class zzf extends zzbyd<zzf> {
        public Integer zzbwJ;
        public String zzbwK;
        public Boolean zzbwL;
        public String[] zzbwM;

        public zzf() {
            zzNu();
        }

        public boolean equals(Object obj) {
            if (obj == this) {
                return true;
            }
            if (!(obj instanceof zzf)) {
                return false;
            }
            zzf com_google_android_gms_internal_zzauu_zzf = (zzf) obj;
            if (this.zzbwJ == null) {
                if (com_google_android_gms_internal_zzauu_zzf.zzbwJ != null) {
                    return false;
                }
            } else if (!this.zzbwJ.equals(com_google_android_gms_internal_zzauu_zzf.zzbwJ)) {
                return false;
            }
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
            return zzbyh.equals(this.zzbwM, com_google_android_gms_internal_zzauu_zzf.zzbwM) ? (this.zzcwC == null || this.zzcwC.isEmpty()) ? com_google_android_gms_internal_zzauu_zzf.zzcwC == null || com_google_android_gms_internal_zzauu_zzf.zzcwC.isEmpty() : this.zzcwC.equals(com_google_android_gms_internal_zzauu_zzf.zzcwC) : false;
        }

        public int hashCode() {
            int i = 0;
            int hashCode = ((((this.zzbwL == null ? 0 : this.zzbwL.hashCode()) + (((this.zzbwK == null ? 0 : this.zzbwK.hashCode()) + (((this.zzbwJ == null ? 0 : this.zzbwJ.intValue()) + ((getClass().getName().hashCode() + 527) * 31)) * 31)) * 31)) * 31) + zzbyh.hashCode(this.zzbwM)) * 31;
            if (!(this.zzcwC == null || this.zzcwC.isEmpty())) {
                i = this.zzcwC.hashCode();
            }
            return hashCode + i;
        }

        public zzf zzL(zzbyb com_google_android_gms_internal_zzbyb) throws IOException {
            while (true) {
                int zzaeW = com_google_android_gms_internal_zzbyb.zzaeW();
                switch (zzaeW) {
                    case 0:
                        break;
                    case 8:
                        zzaeW = com_google_android_gms_internal_zzbyb.zzafa();
                        switch (zzaeW) {
                            case 0:
                            case 1:
                            case 2:
                            case 3:
                            case 4:
                            case 5:
                            case 6:
                                this.zzbwJ = Integer.valueOf(zzaeW);
                                break;
                            default:
                                continue;
                        }
                    case 18:
                        this.zzbwK = com_google_android_gms_internal_zzbyb.readString();
                        continue;
                    case 24:
                        this.zzbwL = Boolean.valueOf(com_google_android_gms_internal_zzbyb.zzafc());
                        continue;
                    case 34:
                        int zzb = zzbym.zzb(com_google_android_gms_internal_zzbyb, 34);
                        zzaeW = this.zzbwM == null ? 0 : this.zzbwM.length;
                        Object obj = new String[(zzb + zzaeW)];
                        if (zzaeW != 0) {
                            System.arraycopy(this.zzbwM, 0, obj, 0, zzaeW);
                        }
                        while (zzaeW < obj.length - 1) {
                            obj[zzaeW] = com_google_android_gms_internal_zzbyb.readString();
                            com_google_android_gms_internal_zzbyb.zzaeW();
                            zzaeW++;
                        }
                        obj[zzaeW] = com_google_android_gms_internal_zzbyb.readString();
                        this.zzbwM = obj;
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

        public zzf zzNu() {
            this.zzbwK = null;
            this.zzbwL = null;
            this.zzbwM = zzbym.EMPTY_STRING_ARRAY;
            this.zzcwC = null;
            this.zzcwL = -1;
            return this;
        }

        public void zza(zzbyc com_google_android_gms_internal_zzbyc) throws IOException {
            if (this.zzbwJ != null) {
                com_google_android_gms_internal_zzbyc.zzJ(1, this.zzbwJ.intValue());
            }
            if (this.zzbwK != null) {
                com_google_android_gms_internal_zzbyc.zzq(2, this.zzbwK);
            }
            if (this.zzbwL != null) {
                com_google_android_gms_internal_zzbyc.zzg(3, this.zzbwL.booleanValue());
            }
            if (this.zzbwM != null && this.zzbwM.length > 0) {
                for (String str : this.zzbwM) {
                    if (str != null) {
                        com_google_android_gms_internal_zzbyc.zzq(4, str);
                    }
                }
            }
            super.zza(com_google_android_gms_internal_zzbyc);
        }

        public /* synthetic */ zzbyj zzb(zzbyb com_google_android_gms_internal_zzbyb) throws IOException {
            return zzL(com_google_android_gms_internal_zzbyb);
        }

        protected int zzu() {
            int i = 0;
            int zzu = super.zzu();
            if (this.zzbwJ != null) {
                zzu += zzbyc.zzL(1, this.zzbwJ.intValue());
            }
            if (this.zzbwK != null) {
                zzu += zzbyc.zzr(2, this.zzbwK);
            }
            if (this.zzbwL != null) {
                zzu += zzbyc.zzh(3, this.zzbwL.booleanValue());
            }
            if (this.zzbwM == null || this.zzbwM.length <= 0) {
                return zzu;
            }
            int i2 = 0;
            int i3 = 0;
            while (i < this.zzbwM.length) {
                String str = this.zzbwM[i];
                if (str != null) {
                    i3++;
                    i2 += zzbyc.zzku(str);
                }
                i++;
            }
            return (zzu + i2) + (i3 * 1);
        }
    }
}
