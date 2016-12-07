package com.google.android.gms.internal;

import com.googlecode.mp4parser.authoring.tracks.h265.NalUnitTypes;
import java.io.IOException;

public interface zzauf {

    public static final class zza extends zzbut {
        private static volatile zza[] zzbvg;
        public Integer zzbvh;
        public zze[] zzbvi;
        public zzb[] zzbvj;

        public zza() {
            zzMk();
        }

        public static zza[] zzMj() {
            if (zzbvg == null) {
                synchronized (zzbur.zzcsf) {
                    if (zzbvg == null) {
                        zzbvg = new zza[0];
                    }
                }
            }
            return zzbvg;
        }

        public boolean equals(Object obj) {
            if (obj == this) {
                return true;
            }
            if (!(obj instanceof zza)) {
                return false;
            }
            zza com_google_android_gms_internal_zzauf_zza = (zza) obj;
            if (this.zzbvh == null) {
                if (com_google_android_gms_internal_zzauf_zza.zzbvh != null) {
                    return false;
                }
            } else if (!this.zzbvh.equals(com_google_android_gms_internal_zzauf_zza.zzbvh)) {
                return false;
            }
            return !zzbur.equals(this.zzbvi, com_google_android_gms_internal_zzauf_zza.zzbvi) ? false : zzbur.equals(this.zzbvj, com_google_android_gms_internal_zzauf_zza.zzbvj);
        }

        public int hashCode() {
            return (((((this.zzbvh == null ? 0 : this.zzbvh.hashCode()) + ((getClass().getName().hashCode() + 527) * 31)) * 31) + zzbur.hashCode(this.zzbvi)) * 31) + zzbur.hashCode(this.zzbvj);
        }

        public zza zzG(zzbul com_google_android_gms_internal_zzbul) throws IOException {
            while (true) {
                int zzacu = com_google_android_gms_internal_zzbul.zzacu();
                int zzc;
                Object obj;
                switch (zzacu) {
                    case 0:
                        break;
                    case 8:
                        this.zzbvh = Integer.valueOf(com_google_android_gms_internal_zzbul.zzacy());
                        continue;
                    case 18:
                        zzc = zzbuw.zzc(com_google_android_gms_internal_zzbul, 18);
                        zzacu = this.zzbvi == null ? 0 : this.zzbvi.length;
                        obj = new zze[(zzc + zzacu)];
                        if (zzacu != 0) {
                            System.arraycopy(this.zzbvi, 0, obj, 0, zzacu);
                        }
                        while (zzacu < obj.length - 1) {
                            obj[zzacu] = new zze();
                            com_google_android_gms_internal_zzbul.zza(obj[zzacu]);
                            com_google_android_gms_internal_zzbul.zzacu();
                            zzacu++;
                        }
                        obj[zzacu] = new zze();
                        com_google_android_gms_internal_zzbul.zza(obj[zzacu]);
                        this.zzbvi = obj;
                        continue;
                    case NalUnitTypes.NAL_TYPE_RSV_VCL26 /*26*/:
                        zzc = zzbuw.zzc(com_google_android_gms_internal_zzbul, 26);
                        zzacu = this.zzbvj == null ? 0 : this.zzbvj.length;
                        obj = new zzb[(zzc + zzacu)];
                        if (zzacu != 0) {
                            System.arraycopy(this.zzbvj, 0, obj, 0, zzacu);
                        }
                        while (zzacu < obj.length - 1) {
                            obj[zzacu] = new zzb();
                            com_google_android_gms_internal_zzbul.zza(obj[zzacu]);
                            com_google_android_gms_internal_zzbul.zzacu();
                            zzacu++;
                        }
                        obj[zzacu] = new zzb();
                        com_google_android_gms_internal_zzbul.zza(obj[zzacu]);
                        this.zzbvj = obj;
                        continue;
                    default:
                        if (!zzbuw.zzb(com_google_android_gms_internal_zzbul, zzacu)) {
                            break;
                        }
                        continue;
                }
                return this;
            }
        }

        public zza zzMk() {
            this.zzbvh = null;
            this.zzbvi = zze.zzMq();
            this.zzbvj = zzb.zzMl();
            this.zzcsg = -1;
            return this;
        }

        public void zza(zzbum com_google_android_gms_internal_zzbum) throws IOException {
            int i = 0;
            if (this.zzbvh != null) {
                com_google_android_gms_internal_zzbum.zzF(1, this.zzbvh.intValue());
            }
            if (this.zzbvi != null && this.zzbvi.length > 0) {
                for (zzbut com_google_android_gms_internal_zzbut : this.zzbvi) {
                    if (com_google_android_gms_internal_zzbut != null) {
                        com_google_android_gms_internal_zzbum.zza(2, com_google_android_gms_internal_zzbut);
                    }
                }
            }
            if (this.zzbvj != null && this.zzbvj.length > 0) {
                while (i < this.zzbvj.length) {
                    zzbut com_google_android_gms_internal_zzbut2 = this.zzbvj[i];
                    if (com_google_android_gms_internal_zzbut2 != null) {
                        com_google_android_gms_internal_zzbum.zza(3, com_google_android_gms_internal_zzbut2);
                    }
                    i++;
                }
            }
            super.zza(com_google_android_gms_internal_zzbum);
        }

        public /* synthetic */ zzbut zzb(zzbul com_google_android_gms_internal_zzbul) throws IOException {
            return zzG(com_google_android_gms_internal_zzbul);
        }

        protected int zzv() {
            int i = 0;
            int zzv = super.zzv();
            if (this.zzbvh != null) {
                zzv += zzbum.zzH(1, this.zzbvh.intValue());
            }
            if (this.zzbvi != null && this.zzbvi.length > 0) {
                int i2 = zzv;
                for (zzbut com_google_android_gms_internal_zzbut : this.zzbvi) {
                    if (com_google_android_gms_internal_zzbut != null) {
                        i2 += zzbum.zzc(2, com_google_android_gms_internal_zzbut);
                    }
                }
                zzv = i2;
            }
            if (this.zzbvj != null && this.zzbvj.length > 0) {
                while (i < this.zzbvj.length) {
                    zzbut com_google_android_gms_internal_zzbut2 = this.zzbvj[i];
                    if (com_google_android_gms_internal_zzbut2 != null) {
                        zzv += zzbum.zzc(3, com_google_android_gms_internal_zzbut2);
                    }
                    i++;
                }
            }
            return zzv;
        }
    }

    public static final class zzb extends zzbut {
        private static volatile zzb[] zzbvk;
        public Integer zzbvl;
        public String zzbvm;
        public zzc[] zzbvn;
        public Boolean zzbvo;
        public zzd zzbvp;

        public zzb() {
            zzMm();
        }

        public static zzb[] zzMl() {
            if (zzbvk == null) {
                synchronized (zzbur.zzcsf) {
                    if (zzbvk == null) {
                        zzbvk = new zzb[0];
                    }
                }
            }
            return zzbvk;
        }

        public boolean equals(Object obj) {
            if (obj == this) {
                return true;
            }
            if (!(obj instanceof zzb)) {
                return false;
            }
            zzb com_google_android_gms_internal_zzauf_zzb = (zzb) obj;
            if (this.zzbvl == null) {
                if (com_google_android_gms_internal_zzauf_zzb.zzbvl != null) {
                    return false;
                }
            } else if (!this.zzbvl.equals(com_google_android_gms_internal_zzauf_zzb.zzbvl)) {
                return false;
            }
            if (this.zzbvm == null) {
                if (com_google_android_gms_internal_zzauf_zzb.zzbvm != null) {
                    return false;
                }
            } else if (!this.zzbvm.equals(com_google_android_gms_internal_zzauf_zzb.zzbvm)) {
                return false;
            }
            if (!zzbur.equals(this.zzbvn, com_google_android_gms_internal_zzauf_zzb.zzbvn)) {
                return false;
            }
            if (this.zzbvo == null) {
                if (com_google_android_gms_internal_zzauf_zzb.zzbvo != null) {
                    return false;
                }
            } else if (!this.zzbvo.equals(com_google_android_gms_internal_zzauf_zzb.zzbvo)) {
                return false;
            }
            return this.zzbvp == null ? com_google_android_gms_internal_zzauf_zzb.zzbvp == null : this.zzbvp.equals(com_google_android_gms_internal_zzauf_zzb.zzbvp);
        }

        public int hashCode() {
            int i = 0;
            int hashCode = ((this.zzbvo == null ? 0 : this.zzbvo.hashCode()) + (((((this.zzbvm == null ? 0 : this.zzbvm.hashCode()) + (((this.zzbvl == null ? 0 : this.zzbvl.hashCode()) + ((getClass().getName().hashCode() + 527) * 31)) * 31)) * 31) + zzbur.hashCode(this.zzbvn)) * 31)) * 31;
            if (this.zzbvp != null) {
                i = this.zzbvp.hashCode();
            }
            return hashCode + i;
        }

        public zzb zzH(zzbul com_google_android_gms_internal_zzbul) throws IOException {
            while (true) {
                int zzacu = com_google_android_gms_internal_zzbul.zzacu();
                switch (zzacu) {
                    case 0:
                        break;
                    case 8:
                        this.zzbvl = Integer.valueOf(com_google_android_gms_internal_zzbul.zzacy());
                        continue;
                    case 18:
                        this.zzbvm = com_google_android_gms_internal_zzbul.readString();
                        continue;
                    case NalUnitTypes.NAL_TYPE_RSV_VCL26 /*26*/:
                        int zzc = zzbuw.zzc(com_google_android_gms_internal_zzbul, 26);
                        zzacu = this.zzbvn == null ? 0 : this.zzbvn.length;
                        Object obj = new zzc[(zzc + zzacu)];
                        if (zzacu != 0) {
                            System.arraycopy(this.zzbvn, 0, obj, 0, zzacu);
                        }
                        while (zzacu < obj.length - 1) {
                            obj[zzacu] = new zzc();
                            com_google_android_gms_internal_zzbul.zza(obj[zzacu]);
                            com_google_android_gms_internal_zzbul.zzacu();
                            zzacu++;
                        }
                        obj[zzacu] = new zzc();
                        com_google_android_gms_internal_zzbul.zza(obj[zzacu]);
                        this.zzbvn = obj;
                        continue;
                    case 32:
                        this.zzbvo = Boolean.valueOf(com_google_android_gms_internal_zzbul.zzacA());
                        continue;
                    case 42:
                        if (this.zzbvp == null) {
                            this.zzbvp = new zzd();
                        }
                        com_google_android_gms_internal_zzbul.zza(this.zzbvp);
                        continue;
                    default:
                        if (!zzbuw.zzb(com_google_android_gms_internal_zzbul, zzacu)) {
                            break;
                        }
                        continue;
                }
                return this;
            }
        }

        public zzb zzMm() {
            this.zzbvl = null;
            this.zzbvm = null;
            this.zzbvn = zzc.zzMn();
            this.zzbvo = null;
            this.zzbvp = null;
            this.zzcsg = -1;
            return this;
        }

        public void zza(zzbum com_google_android_gms_internal_zzbum) throws IOException {
            if (this.zzbvl != null) {
                com_google_android_gms_internal_zzbum.zzF(1, this.zzbvl.intValue());
            }
            if (this.zzbvm != null) {
                com_google_android_gms_internal_zzbum.zzq(2, this.zzbvm);
            }
            if (this.zzbvn != null && this.zzbvn.length > 0) {
                for (zzbut com_google_android_gms_internal_zzbut : this.zzbvn) {
                    if (com_google_android_gms_internal_zzbut != null) {
                        com_google_android_gms_internal_zzbum.zza(3, com_google_android_gms_internal_zzbut);
                    }
                }
            }
            if (this.zzbvo != null) {
                com_google_android_gms_internal_zzbum.zzg(4, this.zzbvo.booleanValue());
            }
            if (this.zzbvp != null) {
                com_google_android_gms_internal_zzbum.zza(5, this.zzbvp);
            }
            super.zza(com_google_android_gms_internal_zzbum);
        }

        public /* synthetic */ zzbut zzb(zzbul com_google_android_gms_internal_zzbul) throws IOException {
            return zzH(com_google_android_gms_internal_zzbul);
        }

        protected int zzv() {
            int zzv = super.zzv();
            if (this.zzbvl != null) {
                zzv += zzbum.zzH(1, this.zzbvl.intValue());
            }
            if (this.zzbvm != null) {
                zzv += zzbum.zzr(2, this.zzbvm);
            }
            if (this.zzbvn != null && this.zzbvn.length > 0) {
                int i = zzv;
                for (zzbut com_google_android_gms_internal_zzbut : this.zzbvn) {
                    if (com_google_android_gms_internal_zzbut != null) {
                        i += zzbum.zzc(3, com_google_android_gms_internal_zzbut);
                    }
                }
                zzv = i;
            }
            if (this.zzbvo != null) {
                zzv += zzbum.zzh(4, this.zzbvo.booleanValue());
            }
            return this.zzbvp != null ? zzv + zzbum.zzc(5, this.zzbvp) : zzv;
        }
    }

    public static final class zzc extends zzbut {
        private static volatile zzc[] zzbvq;
        public zzf zzbvr;
        public zzd zzbvs;
        public Boolean zzbvt;
        public String zzbvu;

        public zzc() {
            zzMo();
        }

        public static zzc[] zzMn() {
            if (zzbvq == null) {
                synchronized (zzbur.zzcsf) {
                    if (zzbvq == null) {
                        zzbvq = new zzc[0];
                    }
                }
            }
            return zzbvq;
        }

        public boolean equals(Object obj) {
            if (obj == this) {
                return true;
            }
            if (!(obj instanceof zzc)) {
                return false;
            }
            zzc com_google_android_gms_internal_zzauf_zzc = (zzc) obj;
            if (this.zzbvr == null) {
                if (com_google_android_gms_internal_zzauf_zzc.zzbvr != null) {
                    return false;
                }
            } else if (!this.zzbvr.equals(com_google_android_gms_internal_zzauf_zzc.zzbvr)) {
                return false;
            }
            if (this.zzbvs == null) {
                if (com_google_android_gms_internal_zzauf_zzc.zzbvs != null) {
                    return false;
                }
            } else if (!this.zzbvs.equals(com_google_android_gms_internal_zzauf_zzc.zzbvs)) {
                return false;
            }
            if (this.zzbvt == null) {
                if (com_google_android_gms_internal_zzauf_zzc.zzbvt != null) {
                    return false;
                }
            } else if (!this.zzbvt.equals(com_google_android_gms_internal_zzauf_zzc.zzbvt)) {
                return false;
            }
            return this.zzbvu == null ? com_google_android_gms_internal_zzauf_zzc.zzbvu == null : this.zzbvu.equals(com_google_android_gms_internal_zzauf_zzc.zzbvu);
        }

        public int hashCode() {
            int i = 0;
            int hashCode = ((this.zzbvt == null ? 0 : this.zzbvt.hashCode()) + (((this.zzbvs == null ? 0 : this.zzbvs.hashCode()) + (((this.zzbvr == null ? 0 : this.zzbvr.hashCode()) + ((getClass().getName().hashCode() + 527) * 31)) * 31)) * 31)) * 31;
            if (this.zzbvu != null) {
                i = this.zzbvu.hashCode();
            }
            return hashCode + i;
        }

        public zzc zzI(zzbul com_google_android_gms_internal_zzbul) throws IOException {
            while (true) {
                int zzacu = com_google_android_gms_internal_zzbul.zzacu();
                switch (zzacu) {
                    case 0:
                        break;
                    case 10:
                        if (this.zzbvr == null) {
                            this.zzbvr = new zzf();
                        }
                        com_google_android_gms_internal_zzbul.zza(this.zzbvr);
                        continue;
                    case 18:
                        if (this.zzbvs == null) {
                            this.zzbvs = new zzd();
                        }
                        com_google_android_gms_internal_zzbul.zza(this.zzbvs);
                        continue;
                    case 24:
                        this.zzbvt = Boolean.valueOf(com_google_android_gms_internal_zzbul.zzacA());
                        continue;
                    case 34:
                        this.zzbvu = com_google_android_gms_internal_zzbul.readString();
                        continue;
                    default:
                        if (!zzbuw.zzb(com_google_android_gms_internal_zzbul, zzacu)) {
                            break;
                        }
                        continue;
                }
                return this;
            }
        }

        public zzc zzMo() {
            this.zzbvr = null;
            this.zzbvs = null;
            this.zzbvt = null;
            this.zzbvu = null;
            this.zzcsg = -1;
            return this;
        }

        public void zza(zzbum com_google_android_gms_internal_zzbum) throws IOException {
            if (this.zzbvr != null) {
                com_google_android_gms_internal_zzbum.zza(1, this.zzbvr);
            }
            if (this.zzbvs != null) {
                com_google_android_gms_internal_zzbum.zza(2, this.zzbvs);
            }
            if (this.zzbvt != null) {
                com_google_android_gms_internal_zzbum.zzg(3, this.zzbvt.booleanValue());
            }
            if (this.zzbvu != null) {
                com_google_android_gms_internal_zzbum.zzq(4, this.zzbvu);
            }
            super.zza(com_google_android_gms_internal_zzbum);
        }

        public /* synthetic */ zzbut zzb(zzbul com_google_android_gms_internal_zzbul) throws IOException {
            return zzI(com_google_android_gms_internal_zzbul);
        }

        protected int zzv() {
            int zzv = super.zzv();
            if (this.zzbvr != null) {
                zzv += zzbum.zzc(1, this.zzbvr);
            }
            if (this.zzbvs != null) {
                zzv += zzbum.zzc(2, this.zzbvs);
            }
            if (this.zzbvt != null) {
                zzv += zzbum.zzh(3, this.zzbvt.booleanValue());
            }
            return this.zzbvu != null ? zzv + zzbum.zzr(4, this.zzbvu) : zzv;
        }
    }

    public static final class zzd extends zzbut {
        public Integer zzbvv;
        public Boolean zzbvw;
        public String zzbvx;
        public String zzbvy;
        public String zzbvz;

        public zzd() {
            zzMp();
        }

        public boolean equals(Object obj) {
            if (obj == this) {
                return true;
            }
            if (!(obj instanceof zzd)) {
                return false;
            }
            zzd com_google_android_gms_internal_zzauf_zzd = (zzd) obj;
            if (this.zzbvv == null) {
                if (com_google_android_gms_internal_zzauf_zzd.zzbvv != null) {
                    return false;
                }
            } else if (!this.zzbvv.equals(com_google_android_gms_internal_zzauf_zzd.zzbvv)) {
                return false;
            }
            if (this.zzbvw == null) {
                if (com_google_android_gms_internal_zzauf_zzd.zzbvw != null) {
                    return false;
                }
            } else if (!this.zzbvw.equals(com_google_android_gms_internal_zzauf_zzd.zzbvw)) {
                return false;
            }
            if (this.zzbvx == null) {
                if (com_google_android_gms_internal_zzauf_zzd.zzbvx != null) {
                    return false;
                }
            } else if (!this.zzbvx.equals(com_google_android_gms_internal_zzauf_zzd.zzbvx)) {
                return false;
            }
            if (this.zzbvy == null) {
                if (com_google_android_gms_internal_zzauf_zzd.zzbvy != null) {
                    return false;
                }
            } else if (!this.zzbvy.equals(com_google_android_gms_internal_zzauf_zzd.zzbvy)) {
                return false;
            }
            return this.zzbvz == null ? com_google_android_gms_internal_zzauf_zzd.zzbvz == null : this.zzbvz.equals(com_google_android_gms_internal_zzauf_zzd.zzbvz);
        }

        public int hashCode() {
            int i = 0;
            int hashCode = ((this.zzbvy == null ? 0 : this.zzbvy.hashCode()) + (((this.zzbvx == null ? 0 : this.zzbvx.hashCode()) + (((this.zzbvw == null ? 0 : this.zzbvw.hashCode()) + (((this.zzbvv == null ? 0 : this.zzbvv.intValue()) + ((getClass().getName().hashCode() + 527) * 31)) * 31)) * 31)) * 31)) * 31;
            if (this.zzbvz != null) {
                i = this.zzbvz.hashCode();
            }
            return hashCode + i;
        }

        public zzd zzJ(zzbul com_google_android_gms_internal_zzbul) throws IOException {
            while (true) {
                int zzacu = com_google_android_gms_internal_zzbul.zzacu();
                switch (zzacu) {
                    case 0:
                        break;
                    case 8:
                        zzacu = com_google_android_gms_internal_zzbul.zzacy();
                        switch (zzacu) {
                            case 0:
                            case 1:
                            case 2:
                            case 3:
                            case 4:
                                this.zzbvv = Integer.valueOf(zzacu);
                                break;
                            default:
                                continue;
                        }
                    case 16:
                        this.zzbvw = Boolean.valueOf(com_google_android_gms_internal_zzbul.zzacA());
                        continue;
                    case NalUnitTypes.NAL_TYPE_RSV_VCL26 /*26*/:
                        this.zzbvx = com_google_android_gms_internal_zzbul.readString();
                        continue;
                    case 34:
                        this.zzbvy = com_google_android_gms_internal_zzbul.readString();
                        continue;
                    case 42:
                        this.zzbvz = com_google_android_gms_internal_zzbul.readString();
                        continue;
                    default:
                        if (!zzbuw.zzb(com_google_android_gms_internal_zzbul, zzacu)) {
                            break;
                        }
                        continue;
                }
                return this;
            }
        }

        public zzd zzMp() {
            this.zzbvw = null;
            this.zzbvx = null;
            this.zzbvy = null;
            this.zzbvz = null;
            this.zzcsg = -1;
            return this;
        }

        public void zza(zzbum com_google_android_gms_internal_zzbum) throws IOException {
            if (this.zzbvv != null) {
                com_google_android_gms_internal_zzbum.zzF(1, this.zzbvv.intValue());
            }
            if (this.zzbvw != null) {
                com_google_android_gms_internal_zzbum.zzg(2, this.zzbvw.booleanValue());
            }
            if (this.zzbvx != null) {
                com_google_android_gms_internal_zzbum.zzq(3, this.zzbvx);
            }
            if (this.zzbvy != null) {
                com_google_android_gms_internal_zzbum.zzq(4, this.zzbvy);
            }
            if (this.zzbvz != null) {
                com_google_android_gms_internal_zzbum.zzq(5, this.zzbvz);
            }
            super.zza(com_google_android_gms_internal_zzbum);
        }

        public /* synthetic */ zzbut zzb(zzbul com_google_android_gms_internal_zzbul) throws IOException {
            return zzJ(com_google_android_gms_internal_zzbul);
        }

        protected int zzv() {
            int zzv = super.zzv();
            if (this.zzbvv != null) {
                zzv += zzbum.zzH(1, this.zzbvv.intValue());
            }
            if (this.zzbvw != null) {
                zzv += zzbum.zzh(2, this.zzbvw.booleanValue());
            }
            if (this.zzbvx != null) {
                zzv += zzbum.zzr(3, this.zzbvx);
            }
            if (this.zzbvy != null) {
                zzv += zzbum.zzr(4, this.zzbvy);
            }
            return this.zzbvz != null ? zzv + zzbum.zzr(5, this.zzbvz) : zzv;
        }
    }

    public static final class zze extends zzbut {
        private static volatile zze[] zzbvA;
        public String zzbvB;
        public zzc zzbvC;
        public Integer zzbvl;

        public zze() {
            zzMr();
        }

        public static zze[] zzMq() {
            if (zzbvA == null) {
                synchronized (zzbur.zzcsf) {
                    if (zzbvA == null) {
                        zzbvA = new zze[0];
                    }
                }
            }
            return zzbvA;
        }

        public boolean equals(Object obj) {
            if (obj == this) {
                return true;
            }
            if (!(obj instanceof zze)) {
                return false;
            }
            zze com_google_android_gms_internal_zzauf_zze = (zze) obj;
            if (this.zzbvl == null) {
                if (com_google_android_gms_internal_zzauf_zze.zzbvl != null) {
                    return false;
                }
            } else if (!this.zzbvl.equals(com_google_android_gms_internal_zzauf_zze.zzbvl)) {
                return false;
            }
            if (this.zzbvB == null) {
                if (com_google_android_gms_internal_zzauf_zze.zzbvB != null) {
                    return false;
                }
            } else if (!this.zzbvB.equals(com_google_android_gms_internal_zzauf_zze.zzbvB)) {
                return false;
            }
            return this.zzbvC == null ? com_google_android_gms_internal_zzauf_zze.zzbvC == null : this.zzbvC.equals(com_google_android_gms_internal_zzauf_zze.zzbvC);
        }

        public int hashCode() {
            int i = 0;
            int hashCode = ((this.zzbvB == null ? 0 : this.zzbvB.hashCode()) + (((this.zzbvl == null ? 0 : this.zzbvl.hashCode()) + ((getClass().getName().hashCode() + 527) * 31)) * 31)) * 31;
            if (this.zzbvC != null) {
                i = this.zzbvC.hashCode();
            }
            return hashCode + i;
        }

        public zze zzK(zzbul com_google_android_gms_internal_zzbul) throws IOException {
            while (true) {
                int zzacu = com_google_android_gms_internal_zzbul.zzacu();
                switch (zzacu) {
                    case 0:
                        break;
                    case 8:
                        this.zzbvl = Integer.valueOf(com_google_android_gms_internal_zzbul.zzacy());
                        continue;
                    case 18:
                        this.zzbvB = com_google_android_gms_internal_zzbul.readString();
                        continue;
                    case NalUnitTypes.NAL_TYPE_RSV_VCL26 /*26*/:
                        if (this.zzbvC == null) {
                            this.zzbvC = new zzc();
                        }
                        com_google_android_gms_internal_zzbul.zza(this.zzbvC);
                        continue;
                    default:
                        if (!zzbuw.zzb(com_google_android_gms_internal_zzbul, zzacu)) {
                            break;
                        }
                        continue;
                }
                return this;
            }
        }

        public zze zzMr() {
            this.zzbvl = null;
            this.zzbvB = null;
            this.zzbvC = null;
            this.zzcsg = -1;
            return this;
        }

        public void zza(zzbum com_google_android_gms_internal_zzbum) throws IOException {
            if (this.zzbvl != null) {
                com_google_android_gms_internal_zzbum.zzF(1, this.zzbvl.intValue());
            }
            if (this.zzbvB != null) {
                com_google_android_gms_internal_zzbum.zzq(2, this.zzbvB);
            }
            if (this.zzbvC != null) {
                com_google_android_gms_internal_zzbum.zza(3, this.zzbvC);
            }
            super.zza(com_google_android_gms_internal_zzbum);
        }

        public /* synthetic */ zzbut zzb(zzbul com_google_android_gms_internal_zzbul) throws IOException {
            return zzK(com_google_android_gms_internal_zzbul);
        }

        protected int zzv() {
            int zzv = super.zzv();
            if (this.zzbvl != null) {
                zzv += zzbum.zzH(1, this.zzbvl.intValue());
            }
            if (this.zzbvB != null) {
                zzv += zzbum.zzr(2, this.zzbvB);
            }
            return this.zzbvC != null ? zzv + zzbum.zzc(3, this.zzbvC) : zzv;
        }
    }

    public static final class zzf extends zzbut {
        public Integer zzbvD;
        public String zzbvE;
        public Boolean zzbvF;
        public String[] zzbvG;

        public zzf() {
            zzMs();
        }

        public boolean equals(Object obj) {
            if (obj == this) {
                return true;
            }
            if (!(obj instanceof zzf)) {
                return false;
            }
            zzf com_google_android_gms_internal_zzauf_zzf = (zzf) obj;
            if (this.zzbvD == null) {
                if (com_google_android_gms_internal_zzauf_zzf.zzbvD != null) {
                    return false;
                }
            } else if (!this.zzbvD.equals(com_google_android_gms_internal_zzauf_zzf.zzbvD)) {
                return false;
            }
            if (this.zzbvE == null) {
                if (com_google_android_gms_internal_zzauf_zzf.zzbvE != null) {
                    return false;
                }
            } else if (!this.zzbvE.equals(com_google_android_gms_internal_zzauf_zzf.zzbvE)) {
                return false;
            }
            if (this.zzbvF == null) {
                if (com_google_android_gms_internal_zzauf_zzf.zzbvF != null) {
                    return false;
                }
            } else if (!this.zzbvF.equals(com_google_android_gms_internal_zzauf_zzf.zzbvF)) {
                return false;
            }
            return zzbur.equals(this.zzbvG, com_google_android_gms_internal_zzauf_zzf.zzbvG);
        }

        public int hashCode() {
            int i = 0;
            int hashCode = ((this.zzbvE == null ? 0 : this.zzbvE.hashCode()) + (((this.zzbvD == null ? 0 : this.zzbvD.intValue()) + ((getClass().getName().hashCode() + 527) * 31)) * 31)) * 31;
            if (this.zzbvF != null) {
                i = this.zzbvF.hashCode();
            }
            return ((hashCode + i) * 31) + zzbur.hashCode(this.zzbvG);
        }

        public zzf zzL(zzbul com_google_android_gms_internal_zzbul) throws IOException {
            while (true) {
                int zzacu = com_google_android_gms_internal_zzbul.zzacu();
                switch (zzacu) {
                    case 0:
                        break;
                    case 8:
                        zzacu = com_google_android_gms_internal_zzbul.zzacy();
                        switch (zzacu) {
                            case 0:
                            case 1:
                            case 2:
                            case 3:
                            case 4:
                            case 5:
                            case 6:
                                this.zzbvD = Integer.valueOf(zzacu);
                                break;
                            default:
                                continue;
                        }
                    case 18:
                        this.zzbvE = com_google_android_gms_internal_zzbul.readString();
                        continue;
                    case 24:
                        this.zzbvF = Boolean.valueOf(com_google_android_gms_internal_zzbul.zzacA());
                        continue;
                    case 34:
                        int zzc = zzbuw.zzc(com_google_android_gms_internal_zzbul, 34);
                        zzacu = this.zzbvG == null ? 0 : this.zzbvG.length;
                        Object obj = new String[(zzc + zzacu)];
                        if (zzacu != 0) {
                            System.arraycopy(this.zzbvG, 0, obj, 0, zzacu);
                        }
                        while (zzacu < obj.length - 1) {
                            obj[zzacu] = com_google_android_gms_internal_zzbul.readString();
                            com_google_android_gms_internal_zzbul.zzacu();
                            zzacu++;
                        }
                        obj[zzacu] = com_google_android_gms_internal_zzbul.readString();
                        this.zzbvG = obj;
                        continue;
                    default:
                        if (!zzbuw.zzb(com_google_android_gms_internal_zzbul, zzacu)) {
                            break;
                        }
                        continue;
                }
                return this;
            }
        }

        public zzf zzMs() {
            this.zzbvE = null;
            this.zzbvF = null;
            this.zzbvG = zzbuw.zzcsn;
            this.zzcsg = -1;
            return this;
        }

        public void zza(zzbum com_google_android_gms_internal_zzbum) throws IOException {
            if (this.zzbvD != null) {
                com_google_android_gms_internal_zzbum.zzF(1, this.zzbvD.intValue());
            }
            if (this.zzbvE != null) {
                com_google_android_gms_internal_zzbum.zzq(2, this.zzbvE);
            }
            if (this.zzbvF != null) {
                com_google_android_gms_internal_zzbum.zzg(3, this.zzbvF.booleanValue());
            }
            if (this.zzbvG != null && this.zzbvG.length > 0) {
                for (String str : this.zzbvG) {
                    if (str != null) {
                        com_google_android_gms_internal_zzbum.zzq(4, str);
                    }
                }
            }
            super.zza(com_google_android_gms_internal_zzbum);
        }

        public /* synthetic */ zzbut zzb(zzbul com_google_android_gms_internal_zzbul) throws IOException {
            return zzL(com_google_android_gms_internal_zzbul);
        }

        protected int zzv() {
            int i = 0;
            int zzv = super.zzv();
            if (this.zzbvD != null) {
                zzv += zzbum.zzH(1, this.zzbvD.intValue());
            }
            if (this.zzbvE != null) {
                zzv += zzbum.zzr(2, this.zzbvE);
            }
            if (this.zzbvF != null) {
                zzv += zzbum.zzh(3, this.zzbvF.booleanValue());
            }
            if (this.zzbvG == null || this.zzbvG.length <= 0) {
                return zzv;
            }
            int i2 = 0;
            int i3 = 0;
            while (i < this.zzbvG.length) {
                String str = this.zzbvG[i];
                if (str != null) {
                    i3++;
                    i2 += zzbum.zzkc(str);
                }
                i++;
            }
            return (zzv + i2) + (i3 * 1);
        }
    }
}
