package com.google.android.gms.internal;

import com.googlecode.mp4parser.authoring.tracks.h265.NalUnitTypes;
import java.io.IOException;

public interface zzwa {

    public static final class zza extends zzasa {
        private static volatile zza[] avY;
        public Integer avZ;
        public zze[] awa;
        public zzb[] awb;

        public zza() {
            zzbzi();
        }

        public static zza[] zzbzh() {
            if (avY == null) {
                synchronized (zzary.btO) {
                    if (avY == null) {
                        avY = new zza[0];
                    }
                }
            }
            return avY;
        }

        public boolean equals(Object obj) {
            if (obj == this) {
                return true;
            }
            if (!(obj instanceof zza)) {
                return false;
            }
            zza com_google_android_gms_internal_zzwa_zza = (zza) obj;
            if (this.avZ == null) {
                if (com_google_android_gms_internal_zzwa_zza.avZ != null) {
                    return false;
                }
            } else if (!this.avZ.equals(com_google_android_gms_internal_zzwa_zza.avZ)) {
                return false;
            }
            return !zzary.equals(this.awa, com_google_android_gms_internal_zzwa_zza.awa) ? false : zzary.equals(this.awb, com_google_android_gms_internal_zzwa_zza.awb);
        }

        public int hashCode() {
            return (((((this.avZ == null ? 0 : this.avZ.hashCode()) + ((getClass().getName().hashCode() + 527) * 31)) * 31) + zzary.hashCode(this.awa)) * 31) + zzary.hashCode(this.awb);
        }

        public void zza(zzart com_google_android_gms_internal_zzart) throws IOException {
            int i = 0;
            if (this.avZ != null) {
                com_google_android_gms_internal_zzart.zzaf(1, this.avZ.intValue());
            }
            if (this.awa != null && this.awa.length > 0) {
                for (zzasa com_google_android_gms_internal_zzasa : this.awa) {
                    if (com_google_android_gms_internal_zzasa != null) {
                        com_google_android_gms_internal_zzart.zza(2, com_google_android_gms_internal_zzasa);
                    }
                }
            }
            if (this.awb != null && this.awb.length > 0) {
                while (i < this.awb.length) {
                    zzasa com_google_android_gms_internal_zzasa2 = this.awb[i];
                    if (com_google_android_gms_internal_zzasa2 != null) {
                        com_google_android_gms_internal_zzart.zza(3, com_google_android_gms_internal_zzasa2);
                    }
                    i++;
                }
            }
            super.zza(com_google_android_gms_internal_zzart);
        }

        public zza zzag(zzars com_google_android_gms_internal_zzars) throws IOException {
            while (true) {
                int bU = com_google_android_gms_internal_zzars.bU();
                int zzc;
                Object obj;
                switch (bU) {
                    case 0:
                        break;
                    case 8:
                        this.avZ = Integer.valueOf(com_google_android_gms_internal_zzars.bY());
                        continue;
                    case 18:
                        zzc = zzasd.zzc(com_google_android_gms_internal_zzars, 18);
                        bU = this.awa == null ? 0 : this.awa.length;
                        obj = new zze[(zzc + bU)];
                        if (bU != 0) {
                            System.arraycopy(this.awa, 0, obj, 0, bU);
                        }
                        while (bU < obj.length - 1) {
                            obj[bU] = new zze();
                            com_google_android_gms_internal_zzars.zza(obj[bU]);
                            com_google_android_gms_internal_zzars.bU();
                            bU++;
                        }
                        obj[bU] = new zze();
                        com_google_android_gms_internal_zzars.zza(obj[bU]);
                        this.awa = obj;
                        continue;
                    case NalUnitTypes.NAL_TYPE_RSV_VCL26 /*26*/:
                        zzc = zzasd.zzc(com_google_android_gms_internal_zzars, 26);
                        bU = this.awb == null ? 0 : this.awb.length;
                        obj = new zzb[(zzc + bU)];
                        if (bU != 0) {
                            System.arraycopy(this.awb, 0, obj, 0, bU);
                        }
                        while (bU < obj.length - 1) {
                            obj[bU] = new zzb();
                            com_google_android_gms_internal_zzars.zza(obj[bU]);
                            com_google_android_gms_internal_zzars.bU();
                            bU++;
                        }
                        obj[bU] = new zzb();
                        com_google_android_gms_internal_zzars.zza(obj[bU]);
                        this.awb = obj;
                        continue;
                    default:
                        if (!zzasd.zzb(com_google_android_gms_internal_zzars, bU)) {
                            break;
                        }
                        continue;
                }
                return this;
            }
        }

        public /* synthetic */ zzasa zzb(zzars com_google_android_gms_internal_zzars) throws IOException {
            return zzag(com_google_android_gms_internal_zzars);
        }

        public zza zzbzi() {
            this.avZ = null;
            this.awa = zze.zzbzo();
            this.awb = zzb.zzbzj();
            this.btP = -1;
            return this;
        }

        protected int zzx() {
            int i = 0;
            int zzx = super.zzx();
            if (this.avZ != null) {
                zzx += zzart.zzah(1, this.avZ.intValue());
            }
            if (this.awa != null && this.awa.length > 0) {
                int i2 = zzx;
                for (zzasa com_google_android_gms_internal_zzasa : this.awa) {
                    if (com_google_android_gms_internal_zzasa != null) {
                        i2 += zzart.zzc(2, com_google_android_gms_internal_zzasa);
                    }
                }
                zzx = i2;
            }
            if (this.awb != null && this.awb.length > 0) {
                while (i < this.awb.length) {
                    zzasa com_google_android_gms_internal_zzasa2 = this.awb[i];
                    if (com_google_android_gms_internal_zzasa2 != null) {
                        zzx += zzart.zzc(3, com_google_android_gms_internal_zzasa2);
                    }
                    i++;
                }
            }
            return zzx;
        }
    }

    public static final class zzb extends zzasa {
        private static volatile zzb[] awc;
        public Integer awd;
        public String awe;
        public zzc[] awf;
        public Boolean awg;
        public zzd awh;

        public zzb() {
            zzbzk();
        }

        public static zzb[] zzbzj() {
            if (awc == null) {
                synchronized (zzary.btO) {
                    if (awc == null) {
                        awc = new zzb[0];
                    }
                }
            }
            return awc;
        }

        public boolean equals(Object obj) {
            if (obj == this) {
                return true;
            }
            if (!(obj instanceof zzb)) {
                return false;
            }
            zzb com_google_android_gms_internal_zzwa_zzb = (zzb) obj;
            if (this.awd == null) {
                if (com_google_android_gms_internal_zzwa_zzb.awd != null) {
                    return false;
                }
            } else if (!this.awd.equals(com_google_android_gms_internal_zzwa_zzb.awd)) {
                return false;
            }
            if (this.awe == null) {
                if (com_google_android_gms_internal_zzwa_zzb.awe != null) {
                    return false;
                }
            } else if (!this.awe.equals(com_google_android_gms_internal_zzwa_zzb.awe)) {
                return false;
            }
            if (!zzary.equals(this.awf, com_google_android_gms_internal_zzwa_zzb.awf)) {
                return false;
            }
            if (this.awg == null) {
                if (com_google_android_gms_internal_zzwa_zzb.awg != null) {
                    return false;
                }
            } else if (!this.awg.equals(com_google_android_gms_internal_zzwa_zzb.awg)) {
                return false;
            }
            return this.awh == null ? com_google_android_gms_internal_zzwa_zzb.awh == null : this.awh.equals(com_google_android_gms_internal_zzwa_zzb.awh);
        }

        public int hashCode() {
            int i = 0;
            int hashCode = ((this.awg == null ? 0 : this.awg.hashCode()) + (((((this.awe == null ? 0 : this.awe.hashCode()) + (((this.awd == null ? 0 : this.awd.hashCode()) + ((getClass().getName().hashCode() + 527) * 31)) * 31)) * 31) + zzary.hashCode(this.awf)) * 31)) * 31;
            if (this.awh != null) {
                i = this.awh.hashCode();
            }
            return hashCode + i;
        }

        public void zza(zzart com_google_android_gms_internal_zzart) throws IOException {
            if (this.awd != null) {
                com_google_android_gms_internal_zzart.zzaf(1, this.awd.intValue());
            }
            if (this.awe != null) {
                com_google_android_gms_internal_zzart.zzq(2, this.awe);
            }
            if (this.awf != null && this.awf.length > 0) {
                for (zzasa com_google_android_gms_internal_zzasa : this.awf) {
                    if (com_google_android_gms_internal_zzasa != null) {
                        com_google_android_gms_internal_zzart.zza(3, com_google_android_gms_internal_zzasa);
                    }
                }
            }
            if (this.awg != null) {
                com_google_android_gms_internal_zzart.zzg(4, this.awg.booleanValue());
            }
            if (this.awh != null) {
                com_google_android_gms_internal_zzart.zza(5, this.awh);
            }
            super.zza(com_google_android_gms_internal_zzart);
        }

        public zzb zzah(zzars com_google_android_gms_internal_zzars) throws IOException {
            while (true) {
                int bU = com_google_android_gms_internal_zzars.bU();
                switch (bU) {
                    case 0:
                        break;
                    case 8:
                        this.awd = Integer.valueOf(com_google_android_gms_internal_zzars.bY());
                        continue;
                    case 18:
                        this.awe = com_google_android_gms_internal_zzars.readString();
                        continue;
                    case NalUnitTypes.NAL_TYPE_RSV_VCL26 /*26*/:
                        int zzc = zzasd.zzc(com_google_android_gms_internal_zzars, 26);
                        bU = this.awf == null ? 0 : this.awf.length;
                        Object obj = new zzc[(zzc + bU)];
                        if (bU != 0) {
                            System.arraycopy(this.awf, 0, obj, 0, bU);
                        }
                        while (bU < obj.length - 1) {
                            obj[bU] = new zzc();
                            com_google_android_gms_internal_zzars.zza(obj[bU]);
                            com_google_android_gms_internal_zzars.bU();
                            bU++;
                        }
                        obj[bU] = new zzc();
                        com_google_android_gms_internal_zzars.zza(obj[bU]);
                        this.awf = obj;
                        continue;
                    case 32:
                        this.awg = Boolean.valueOf(com_google_android_gms_internal_zzars.ca());
                        continue;
                    case 42:
                        if (this.awh == null) {
                            this.awh = new zzd();
                        }
                        com_google_android_gms_internal_zzars.zza(this.awh);
                        continue;
                    default:
                        if (!zzasd.zzb(com_google_android_gms_internal_zzars, bU)) {
                            break;
                        }
                        continue;
                }
                return this;
            }
        }

        public /* synthetic */ zzasa zzb(zzars com_google_android_gms_internal_zzars) throws IOException {
            return zzah(com_google_android_gms_internal_zzars);
        }

        public zzb zzbzk() {
            this.awd = null;
            this.awe = null;
            this.awf = zzc.zzbzl();
            this.awg = null;
            this.awh = null;
            this.btP = -1;
            return this;
        }

        protected int zzx() {
            int zzx = super.zzx();
            if (this.awd != null) {
                zzx += zzart.zzah(1, this.awd.intValue());
            }
            if (this.awe != null) {
                zzx += zzart.zzr(2, this.awe);
            }
            if (this.awf != null && this.awf.length > 0) {
                int i = zzx;
                for (zzasa com_google_android_gms_internal_zzasa : this.awf) {
                    if (com_google_android_gms_internal_zzasa != null) {
                        i += zzart.zzc(3, com_google_android_gms_internal_zzasa);
                    }
                }
                zzx = i;
            }
            if (this.awg != null) {
                zzx += zzart.zzh(4, this.awg.booleanValue());
            }
            return this.awh != null ? zzx + zzart.zzc(5, this.awh) : zzx;
        }
    }

    public static final class zzc extends zzasa {
        private static volatile zzc[] awi;
        public zzf awj;
        public zzd awk;
        public Boolean awl;
        public String awm;

        public zzc() {
            zzbzm();
        }

        public static zzc[] zzbzl() {
            if (awi == null) {
                synchronized (zzary.btO) {
                    if (awi == null) {
                        awi = new zzc[0];
                    }
                }
            }
            return awi;
        }

        public boolean equals(Object obj) {
            if (obj == this) {
                return true;
            }
            if (!(obj instanceof zzc)) {
                return false;
            }
            zzc com_google_android_gms_internal_zzwa_zzc = (zzc) obj;
            if (this.awj == null) {
                if (com_google_android_gms_internal_zzwa_zzc.awj != null) {
                    return false;
                }
            } else if (!this.awj.equals(com_google_android_gms_internal_zzwa_zzc.awj)) {
                return false;
            }
            if (this.awk == null) {
                if (com_google_android_gms_internal_zzwa_zzc.awk != null) {
                    return false;
                }
            } else if (!this.awk.equals(com_google_android_gms_internal_zzwa_zzc.awk)) {
                return false;
            }
            if (this.awl == null) {
                if (com_google_android_gms_internal_zzwa_zzc.awl != null) {
                    return false;
                }
            } else if (!this.awl.equals(com_google_android_gms_internal_zzwa_zzc.awl)) {
                return false;
            }
            return this.awm == null ? com_google_android_gms_internal_zzwa_zzc.awm == null : this.awm.equals(com_google_android_gms_internal_zzwa_zzc.awm);
        }

        public int hashCode() {
            int i = 0;
            int hashCode = ((this.awl == null ? 0 : this.awl.hashCode()) + (((this.awk == null ? 0 : this.awk.hashCode()) + (((this.awj == null ? 0 : this.awj.hashCode()) + ((getClass().getName().hashCode() + 527) * 31)) * 31)) * 31)) * 31;
            if (this.awm != null) {
                i = this.awm.hashCode();
            }
            return hashCode + i;
        }

        public void zza(zzart com_google_android_gms_internal_zzart) throws IOException {
            if (this.awj != null) {
                com_google_android_gms_internal_zzart.zza(1, this.awj);
            }
            if (this.awk != null) {
                com_google_android_gms_internal_zzart.zza(2, this.awk);
            }
            if (this.awl != null) {
                com_google_android_gms_internal_zzart.zzg(3, this.awl.booleanValue());
            }
            if (this.awm != null) {
                com_google_android_gms_internal_zzart.zzq(4, this.awm);
            }
            super.zza(com_google_android_gms_internal_zzart);
        }

        public zzc zzai(zzars com_google_android_gms_internal_zzars) throws IOException {
            while (true) {
                int bU = com_google_android_gms_internal_zzars.bU();
                switch (bU) {
                    case 0:
                        break;
                    case 10:
                        if (this.awj == null) {
                            this.awj = new zzf();
                        }
                        com_google_android_gms_internal_zzars.zza(this.awj);
                        continue;
                    case 18:
                        if (this.awk == null) {
                            this.awk = new zzd();
                        }
                        com_google_android_gms_internal_zzars.zza(this.awk);
                        continue;
                    case 24:
                        this.awl = Boolean.valueOf(com_google_android_gms_internal_zzars.ca());
                        continue;
                    case 34:
                        this.awm = com_google_android_gms_internal_zzars.readString();
                        continue;
                    default:
                        if (!zzasd.zzb(com_google_android_gms_internal_zzars, bU)) {
                            break;
                        }
                        continue;
                }
                return this;
            }
        }

        public /* synthetic */ zzasa zzb(zzars com_google_android_gms_internal_zzars) throws IOException {
            return zzai(com_google_android_gms_internal_zzars);
        }

        public zzc zzbzm() {
            this.awj = null;
            this.awk = null;
            this.awl = null;
            this.awm = null;
            this.btP = -1;
            return this;
        }

        protected int zzx() {
            int zzx = super.zzx();
            if (this.awj != null) {
                zzx += zzart.zzc(1, this.awj);
            }
            if (this.awk != null) {
                zzx += zzart.zzc(2, this.awk);
            }
            if (this.awl != null) {
                zzx += zzart.zzh(3, this.awl.booleanValue());
            }
            return this.awm != null ? zzx + zzart.zzr(4, this.awm) : zzx;
        }
    }

    public static final class zzd extends zzasa {
        public Integer awn;
        public Boolean awo;
        public String awp;
        public String awq;
        public String awr;

        public zzd() {
            zzbzn();
        }

        public boolean equals(Object obj) {
            if (obj == this) {
                return true;
            }
            if (!(obj instanceof zzd)) {
                return false;
            }
            zzd com_google_android_gms_internal_zzwa_zzd = (zzd) obj;
            if (this.awn == null) {
                if (com_google_android_gms_internal_zzwa_zzd.awn != null) {
                    return false;
                }
            } else if (!this.awn.equals(com_google_android_gms_internal_zzwa_zzd.awn)) {
                return false;
            }
            if (this.awo == null) {
                if (com_google_android_gms_internal_zzwa_zzd.awo != null) {
                    return false;
                }
            } else if (!this.awo.equals(com_google_android_gms_internal_zzwa_zzd.awo)) {
                return false;
            }
            if (this.awp == null) {
                if (com_google_android_gms_internal_zzwa_zzd.awp != null) {
                    return false;
                }
            } else if (!this.awp.equals(com_google_android_gms_internal_zzwa_zzd.awp)) {
                return false;
            }
            if (this.awq == null) {
                if (com_google_android_gms_internal_zzwa_zzd.awq != null) {
                    return false;
                }
            } else if (!this.awq.equals(com_google_android_gms_internal_zzwa_zzd.awq)) {
                return false;
            }
            return this.awr == null ? com_google_android_gms_internal_zzwa_zzd.awr == null : this.awr.equals(com_google_android_gms_internal_zzwa_zzd.awr);
        }

        public int hashCode() {
            int i = 0;
            int hashCode = ((this.awq == null ? 0 : this.awq.hashCode()) + (((this.awp == null ? 0 : this.awp.hashCode()) + (((this.awo == null ? 0 : this.awo.hashCode()) + (((this.awn == null ? 0 : this.awn.intValue()) + ((getClass().getName().hashCode() + 527) * 31)) * 31)) * 31)) * 31)) * 31;
            if (this.awr != null) {
                i = this.awr.hashCode();
            }
            return hashCode + i;
        }

        public void zza(zzart com_google_android_gms_internal_zzart) throws IOException {
            if (this.awn != null) {
                com_google_android_gms_internal_zzart.zzaf(1, this.awn.intValue());
            }
            if (this.awo != null) {
                com_google_android_gms_internal_zzart.zzg(2, this.awo.booleanValue());
            }
            if (this.awp != null) {
                com_google_android_gms_internal_zzart.zzq(3, this.awp);
            }
            if (this.awq != null) {
                com_google_android_gms_internal_zzart.zzq(4, this.awq);
            }
            if (this.awr != null) {
                com_google_android_gms_internal_zzart.zzq(5, this.awr);
            }
            super.zza(com_google_android_gms_internal_zzart);
        }

        public zzd zzaj(zzars com_google_android_gms_internal_zzars) throws IOException {
            while (true) {
                int bU = com_google_android_gms_internal_zzars.bU();
                switch (bU) {
                    case 0:
                        break;
                    case 8:
                        bU = com_google_android_gms_internal_zzars.bY();
                        switch (bU) {
                            case 0:
                            case 1:
                            case 2:
                            case 3:
                            case 4:
                                this.awn = Integer.valueOf(bU);
                                break;
                            default:
                                continue;
                        }
                    case 16:
                        this.awo = Boolean.valueOf(com_google_android_gms_internal_zzars.ca());
                        continue;
                    case NalUnitTypes.NAL_TYPE_RSV_VCL26 /*26*/:
                        this.awp = com_google_android_gms_internal_zzars.readString();
                        continue;
                    case 34:
                        this.awq = com_google_android_gms_internal_zzars.readString();
                        continue;
                    case 42:
                        this.awr = com_google_android_gms_internal_zzars.readString();
                        continue;
                    default:
                        if (!zzasd.zzb(com_google_android_gms_internal_zzars, bU)) {
                            break;
                        }
                        continue;
                }
                return this;
            }
        }

        public /* synthetic */ zzasa zzb(zzars com_google_android_gms_internal_zzars) throws IOException {
            return zzaj(com_google_android_gms_internal_zzars);
        }

        public zzd zzbzn() {
            this.awo = null;
            this.awp = null;
            this.awq = null;
            this.awr = null;
            this.btP = -1;
            return this;
        }

        protected int zzx() {
            int zzx = super.zzx();
            if (this.awn != null) {
                zzx += zzart.zzah(1, this.awn.intValue());
            }
            if (this.awo != null) {
                zzx += zzart.zzh(2, this.awo.booleanValue());
            }
            if (this.awp != null) {
                zzx += zzart.zzr(3, this.awp);
            }
            if (this.awq != null) {
                zzx += zzart.zzr(4, this.awq);
            }
            return this.awr != null ? zzx + zzart.zzr(5, this.awr) : zzx;
        }
    }

    public static final class zze extends zzasa {
        private static volatile zze[] aws;
        public Integer awd;
        public String awt;
        public zzc awu;

        public zze() {
            zzbzp();
        }

        public static zze[] zzbzo() {
            if (aws == null) {
                synchronized (zzary.btO) {
                    if (aws == null) {
                        aws = new zze[0];
                    }
                }
            }
            return aws;
        }

        public boolean equals(Object obj) {
            if (obj == this) {
                return true;
            }
            if (!(obj instanceof zze)) {
                return false;
            }
            zze com_google_android_gms_internal_zzwa_zze = (zze) obj;
            if (this.awd == null) {
                if (com_google_android_gms_internal_zzwa_zze.awd != null) {
                    return false;
                }
            } else if (!this.awd.equals(com_google_android_gms_internal_zzwa_zze.awd)) {
                return false;
            }
            if (this.awt == null) {
                if (com_google_android_gms_internal_zzwa_zze.awt != null) {
                    return false;
                }
            } else if (!this.awt.equals(com_google_android_gms_internal_zzwa_zze.awt)) {
                return false;
            }
            return this.awu == null ? com_google_android_gms_internal_zzwa_zze.awu == null : this.awu.equals(com_google_android_gms_internal_zzwa_zze.awu);
        }

        public int hashCode() {
            int i = 0;
            int hashCode = ((this.awt == null ? 0 : this.awt.hashCode()) + (((this.awd == null ? 0 : this.awd.hashCode()) + ((getClass().getName().hashCode() + 527) * 31)) * 31)) * 31;
            if (this.awu != null) {
                i = this.awu.hashCode();
            }
            return hashCode + i;
        }

        public void zza(zzart com_google_android_gms_internal_zzart) throws IOException {
            if (this.awd != null) {
                com_google_android_gms_internal_zzart.zzaf(1, this.awd.intValue());
            }
            if (this.awt != null) {
                com_google_android_gms_internal_zzart.zzq(2, this.awt);
            }
            if (this.awu != null) {
                com_google_android_gms_internal_zzart.zza(3, this.awu);
            }
            super.zza(com_google_android_gms_internal_zzart);
        }

        public zze zzak(zzars com_google_android_gms_internal_zzars) throws IOException {
            while (true) {
                int bU = com_google_android_gms_internal_zzars.bU();
                switch (bU) {
                    case 0:
                        break;
                    case 8:
                        this.awd = Integer.valueOf(com_google_android_gms_internal_zzars.bY());
                        continue;
                    case 18:
                        this.awt = com_google_android_gms_internal_zzars.readString();
                        continue;
                    case NalUnitTypes.NAL_TYPE_RSV_VCL26 /*26*/:
                        if (this.awu == null) {
                            this.awu = new zzc();
                        }
                        com_google_android_gms_internal_zzars.zza(this.awu);
                        continue;
                    default:
                        if (!zzasd.zzb(com_google_android_gms_internal_zzars, bU)) {
                            break;
                        }
                        continue;
                }
                return this;
            }
        }

        public /* synthetic */ zzasa zzb(zzars com_google_android_gms_internal_zzars) throws IOException {
            return zzak(com_google_android_gms_internal_zzars);
        }

        public zze zzbzp() {
            this.awd = null;
            this.awt = null;
            this.awu = null;
            this.btP = -1;
            return this;
        }

        protected int zzx() {
            int zzx = super.zzx();
            if (this.awd != null) {
                zzx += zzart.zzah(1, this.awd.intValue());
            }
            if (this.awt != null) {
                zzx += zzart.zzr(2, this.awt);
            }
            return this.awu != null ? zzx + zzart.zzc(3, this.awu) : zzx;
        }
    }

    public static final class zzf extends zzasa {
        public Integer awv;
        public String aww;
        public Boolean awx;
        public String[] awy;

        public zzf() {
            zzbzq();
        }

        public boolean equals(Object obj) {
            if (obj == this) {
                return true;
            }
            if (!(obj instanceof zzf)) {
                return false;
            }
            zzf com_google_android_gms_internal_zzwa_zzf = (zzf) obj;
            if (this.awv == null) {
                if (com_google_android_gms_internal_zzwa_zzf.awv != null) {
                    return false;
                }
            } else if (!this.awv.equals(com_google_android_gms_internal_zzwa_zzf.awv)) {
                return false;
            }
            if (this.aww == null) {
                if (com_google_android_gms_internal_zzwa_zzf.aww != null) {
                    return false;
                }
            } else if (!this.aww.equals(com_google_android_gms_internal_zzwa_zzf.aww)) {
                return false;
            }
            if (this.awx == null) {
                if (com_google_android_gms_internal_zzwa_zzf.awx != null) {
                    return false;
                }
            } else if (!this.awx.equals(com_google_android_gms_internal_zzwa_zzf.awx)) {
                return false;
            }
            return zzary.equals(this.awy, com_google_android_gms_internal_zzwa_zzf.awy);
        }

        public int hashCode() {
            int i = 0;
            int hashCode = ((this.aww == null ? 0 : this.aww.hashCode()) + (((this.awv == null ? 0 : this.awv.intValue()) + ((getClass().getName().hashCode() + 527) * 31)) * 31)) * 31;
            if (this.awx != null) {
                i = this.awx.hashCode();
            }
            return ((hashCode + i) * 31) + zzary.hashCode(this.awy);
        }

        public void zza(zzart com_google_android_gms_internal_zzart) throws IOException {
            if (this.awv != null) {
                com_google_android_gms_internal_zzart.zzaf(1, this.awv.intValue());
            }
            if (this.aww != null) {
                com_google_android_gms_internal_zzart.zzq(2, this.aww);
            }
            if (this.awx != null) {
                com_google_android_gms_internal_zzart.zzg(3, this.awx.booleanValue());
            }
            if (this.awy != null && this.awy.length > 0) {
                for (String str : this.awy) {
                    if (str != null) {
                        com_google_android_gms_internal_zzart.zzq(4, str);
                    }
                }
            }
            super.zza(com_google_android_gms_internal_zzart);
        }

        public zzf zzal(zzars com_google_android_gms_internal_zzars) throws IOException {
            while (true) {
                int bU = com_google_android_gms_internal_zzars.bU();
                switch (bU) {
                    case 0:
                        break;
                    case 8:
                        bU = com_google_android_gms_internal_zzars.bY();
                        switch (bU) {
                            case 0:
                            case 1:
                            case 2:
                            case 3:
                            case 4:
                            case 5:
                            case 6:
                                this.awv = Integer.valueOf(bU);
                                break;
                            default:
                                continue;
                        }
                    case 18:
                        this.aww = com_google_android_gms_internal_zzars.readString();
                        continue;
                    case 24:
                        this.awx = Boolean.valueOf(com_google_android_gms_internal_zzars.ca());
                        continue;
                    case 34:
                        int zzc = zzasd.zzc(com_google_android_gms_internal_zzars, 34);
                        bU = this.awy == null ? 0 : this.awy.length;
                        Object obj = new String[(zzc + bU)];
                        if (bU != 0) {
                            System.arraycopy(this.awy, 0, obj, 0, bU);
                        }
                        while (bU < obj.length - 1) {
                            obj[bU] = com_google_android_gms_internal_zzars.readString();
                            com_google_android_gms_internal_zzars.bU();
                            bU++;
                        }
                        obj[bU] = com_google_android_gms_internal_zzars.readString();
                        this.awy = obj;
                        continue;
                    default:
                        if (!zzasd.zzb(com_google_android_gms_internal_zzars, bU)) {
                            break;
                        }
                        continue;
                }
                return this;
            }
        }

        public /* synthetic */ zzasa zzb(zzars com_google_android_gms_internal_zzars) throws IOException {
            return zzal(com_google_android_gms_internal_zzars);
        }

        public zzf zzbzq() {
            this.aww = null;
            this.awx = null;
            this.awy = zzasd.btW;
            this.btP = -1;
            return this;
        }

        protected int zzx() {
            int i = 0;
            int zzx = super.zzx();
            if (this.awv != null) {
                zzx += zzart.zzah(1, this.awv.intValue());
            }
            if (this.aww != null) {
                zzx += zzart.zzr(2, this.aww);
            }
            if (this.awx != null) {
                zzx += zzart.zzh(3, this.awx.booleanValue());
            }
            if (this.awy == null || this.awy.length <= 0) {
                return zzx;
            }
            int i2 = 0;
            int i3 = 0;
            while (i < this.awy.length) {
                String str = this.awy[i];
                if (str != null) {
                    i3++;
                    i2 += zzart.zzuy(str);
                }
                i++;
            }
            return (zzx + i2) + (i3 * 1);
        }
    }
}
