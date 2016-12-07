package com.google.android.gms.internal;

import android.support.v4.view.MotionEventCompat;
import com.googlecode.mp4parser.authoring.tracks.h265.NalUnitTypes;
import java.io.IOException;

public interface zzvm {

    public static final class zza extends zzark {
        private static volatile zza[] atj;
        public Integer asA;
        public zzf atk;
        public zzf atl;
        public Boolean atm;

        public zza() {
            zzbzc();
        }

        public static zza[] zzbzb() {
            if (atj == null) {
                synchronized (zzari.bqD) {
                    if (atj == null) {
                        atj = new zza[0];
                    }
                }
            }
            return atj;
        }

        public boolean equals(Object obj) {
            if (obj == this) {
                return true;
            }
            if (!(obj instanceof zza)) {
                return false;
            }
            zza com_google_android_gms_internal_zzvm_zza = (zza) obj;
            if (this.asA == null) {
                if (com_google_android_gms_internal_zzvm_zza.asA != null) {
                    return false;
                }
            } else if (!this.asA.equals(com_google_android_gms_internal_zzvm_zza.asA)) {
                return false;
            }
            if (this.atk == null) {
                if (com_google_android_gms_internal_zzvm_zza.atk != null) {
                    return false;
                }
            } else if (!this.atk.equals(com_google_android_gms_internal_zzvm_zza.atk)) {
                return false;
            }
            if (this.atl == null) {
                if (com_google_android_gms_internal_zzvm_zza.atl != null) {
                    return false;
                }
            } else if (!this.atl.equals(com_google_android_gms_internal_zzvm_zza.atl)) {
                return false;
            }
            return this.atm == null ? com_google_android_gms_internal_zzvm_zza.atm == null : this.atm.equals(com_google_android_gms_internal_zzvm_zza.atm);
        }

        public int hashCode() {
            int i = 0;
            int hashCode = ((this.atl == null ? 0 : this.atl.hashCode()) + (((this.atk == null ? 0 : this.atk.hashCode()) + (((this.asA == null ? 0 : this.asA.hashCode()) + ((getClass().getName().hashCode() + 527) * 31)) * 31)) * 31)) * 31;
            if (this.atm != null) {
                i = this.atm.hashCode();
            }
            return hashCode + i;
        }

        public void zza(zzard com_google_android_gms_internal_zzard) throws IOException {
            if (this.asA != null) {
                com_google_android_gms_internal_zzard.zzae(1, this.asA.intValue());
            }
            if (this.atk != null) {
                com_google_android_gms_internal_zzard.zza(2, this.atk);
            }
            if (this.atl != null) {
                com_google_android_gms_internal_zzard.zza(3, this.atl);
            }
            if (this.atm != null) {
                com_google_android_gms_internal_zzard.zzj(4, this.atm.booleanValue());
            }
            super.zza(com_google_android_gms_internal_zzard);
        }

        public zza zzam(zzarc com_google_android_gms_internal_zzarc) throws IOException {
            while (true) {
                int cw = com_google_android_gms_internal_zzarc.cw();
                switch (cw) {
                    case 0:
                        break;
                    case 8:
                        this.asA = Integer.valueOf(com_google_android_gms_internal_zzarc.cA());
                        continue;
                    case 18:
                        if (this.atk == null) {
                            this.atk = new zzf();
                        }
                        com_google_android_gms_internal_zzarc.zza(this.atk);
                        continue;
                    case NalUnitTypes.NAL_TYPE_RSV_VCL26 /*26*/:
                        if (this.atl == null) {
                            this.atl = new zzf();
                        }
                        com_google_android_gms_internal_zzarc.zza(this.atl);
                        continue;
                    case 32:
                        this.atm = Boolean.valueOf(com_google_android_gms_internal_zzarc.cC());
                        continue;
                    default:
                        if (!zzarn.zzb(com_google_android_gms_internal_zzarc, cw)) {
                            break;
                        }
                        continue;
                }
                return this;
            }
        }

        public /* synthetic */ zzark zzb(zzarc com_google_android_gms_internal_zzarc) throws IOException {
            return zzam(com_google_android_gms_internal_zzarc);
        }

        public zza zzbzc() {
            this.asA = null;
            this.atk = null;
            this.atl = null;
            this.atm = null;
            this.bqE = -1;
            return this;
        }

        protected int zzx() {
            int zzx = super.zzx();
            if (this.asA != null) {
                zzx += zzard.zzag(1, this.asA.intValue());
            }
            if (this.atk != null) {
                zzx += zzard.zzc(2, this.atk);
            }
            if (this.atl != null) {
                zzx += zzard.zzc(3, this.atl);
            }
            return this.atm != null ? zzx + zzard.zzk(4, this.atm.booleanValue()) : zzx;
        }
    }

    public static final class zzb extends zzark {
        private static volatile zzb[] atn;
        public zzc[] ato;
        public Long atp;
        public Long atq;
        public Integer count;
        public String name;

        public zzb() {
            zzbze();
        }

        public static zzb[] zzbzd() {
            if (atn == null) {
                synchronized (zzari.bqD) {
                    if (atn == null) {
                        atn = new zzb[0];
                    }
                }
            }
            return atn;
        }

        public boolean equals(Object obj) {
            if (obj == this) {
                return true;
            }
            if (!(obj instanceof zzb)) {
                return false;
            }
            zzb com_google_android_gms_internal_zzvm_zzb = (zzb) obj;
            if (!zzari.equals(this.ato, com_google_android_gms_internal_zzvm_zzb.ato)) {
                return false;
            }
            if (this.name == null) {
                if (com_google_android_gms_internal_zzvm_zzb.name != null) {
                    return false;
                }
            } else if (!this.name.equals(com_google_android_gms_internal_zzvm_zzb.name)) {
                return false;
            }
            if (this.atp == null) {
                if (com_google_android_gms_internal_zzvm_zzb.atp != null) {
                    return false;
                }
            } else if (!this.atp.equals(com_google_android_gms_internal_zzvm_zzb.atp)) {
                return false;
            }
            if (this.atq == null) {
                if (com_google_android_gms_internal_zzvm_zzb.atq != null) {
                    return false;
                }
            } else if (!this.atq.equals(com_google_android_gms_internal_zzvm_zzb.atq)) {
                return false;
            }
            return this.count == null ? com_google_android_gms_internal_zzvm_zzb.count == null : this.count.equals(com_google_android_gms_internal_zzvm_zzb.count);
        }

        public int hashCode() {
            int i = 0;
            int hashCode = ((this.atq == null ? 0 : this.atq.hashCode()) + (((this.atp == null ? 0 : this.atp.hashCode()) + (((this.name == null ? 0 : this.name.hashCode()) + ((((getClass().getName().hashCode() + 527) * 31) + zzari.hashCode(this.ato)) * 31)) * 31)) * 31)) * 31;
            if (this.count != null) {
                i = this.count.hashCode();
            }
            return hashCode + i;
        }

        public void zza(zzard com_google_android_gms_internal_zzard) throws IOException {
            if (this.ato != null && this.ato.length > 0) {
                for (zzark com_google_android_gms_internal_zzark : this.ato) {
                    if (com_google_android_gms_internal_zzark != null) {
                        com_google_android_gms_internal_zzard.zza(1, com_google_android_gms_internal_zzark);
                    }
                }
            }
            if (this.name != null) {
                com_google_android_gms_internal_zzard.zzr(2, this.name);
            }
            if (this.atp != null) {
                com_google_android_gms_internal_zzard.zzb(3, this.atp.longValue());
            }
            if (this.atq != null) {
                com_google_android_gms_internal_zzard.zzb(4, this.atq.longValue());
            }
            if (this.count != null) {
                com_google_android_gms_internal_zzard.zzae(5, this.count.intValue());
            }
            super.zza(com_google_android_gms_internal_zzard);
        }

        public zzb zzan(zzarc com_google_android_gms_internal_zzarc) throws IOException {
            while (true) {
                int cw = com_google_android_gms_internal_zzarc.cw();
                switch (cw) {
                    case 0:
                        break;
                    case 10:
                        int zzc = zzarn.zzc(com_google_android_gms_internal_zzarc, 10);
                        cw = this.ato == null ? 0 : this.ato.length;
                        Object obj = new zzc[(zzc + cw)];
                        if (cw != 0) {
                            System.arraycopy(this.ato, 0, obj, 0, cw);
                        }
                        while (cw < obj.length - 1) {
                            obj[cw] = new zzc();
                            com_google_android_gms_internal_zzarc.zza(obj[cw]);
                            com_google_android_gms_internal_zzarc.cw();
                            cw++;
                        }
                        obj[cw] = new zzc();
                        com_google_android_gms_internal_zzarc.zza(obj[cw]);
                        this.ato = obj;
                        continue;
                    case 18:
                        this.name = com_google_android_gms_internal_zzarc.readString();
                        continue;
                    case 24:
                        this.atp = Long.valueOf(com_google_android_gms_internal_zzarc.cz());
                        continue;
                    case 32:
                        this.atq = Long.valueOf(com_google_android_gms_internal_zzarc.cz());
                        continue;
                    case MotionEventCompat.AXIS_GENERIC_9 /*40*/:
                        this.count = Integer.valueOf(com_google_android_gms_internal_zzarc.cA());
                        continue;
                    default:
                        if (!zzarn.zzb(com_google_android_gms_internal_zzarc, cw)) {
                            break;
                        }
                        continue;
                }
                return this;
            }
        }

        public /* synthetic */ zzark zzb(zzarc com_google_android_gms_internal_zzarc) throws IOException {
            return zzan(com_google_android_gms_internal_zzarc);
        }

        public zzb zzbze() {
            this.ato = zzc.zzbzf();
            this.name = null;
            this.atp = null;
            this.atq = null;
            this.count = null;
            this.bqE = -1;
            return this;
        }

        protected int zzx() {
            int zzx = super.zzx();
            if (this.ato != null && this.ato.length > 0) {
                for (zzark com_google_android_gms_internal_zzark : this.ato) {
                    if (com_google_android_gms_internal_zzark != null) {
                        zzx += zzard.zzc(1, com_google_android_gms_internal_zzark);
                    }
                }
            }
            if (this.name != null) {
                zzx += zzard.zzs(2, this.name);
            }
            if (this.atp != null) {
                zzx += zzard.zzf(3, this.atp.longValue());
            }
            if (this.atq != null) {
                zzx += zzard.zzf(4, this.atq.longValue());
            }
            return this.count != null ? zzx + zzard.zzag(5, this.count.intValue()) : zzx;
        }
    }

    public static final class zzc extends zzark {
        private static volatile zzc[] atr;
        public String Dr;
        public Float asw;
        public Double asx;
        public Long ats;
        public String name;

        public zzc() {
            zzbzg();
        }

        public static zzc[] zzbzf() {
            if (atr == null) {
                synchronized (zzari.bqD) {
                    if (atr == null) {
                        atr = new zzc[0];
                    }
                }
            }
            return atr;
        }

        public boolean equals(Object obj) {
            if (obj == this) {
                return true;
            }
            if (!(obj instanceof zzc)) {
                return false;
            }
            zzc com_google_android_gms_internal_zzvm_zzc = (zzc) obj;
            if (this.name == null) {
                if (com_google_android_gms_internal_zzvm_zzc.name != null) {
                    return false;
                }
            } else if (!this.name.equals(com_google_android_gms_internal_zzvm_zzc.name)) {
                return false;
            }
            if (this.Dr == null) {
                if (com_google_android_gms_internal_zzvm_zzc.Dr != null) {
                    return false;
                }
            } else if (!this.Dr.equals(com_google_android_gms_internal_zzvm_zzc.Dr)) {
                return false;
            }
            if (this.ats == null) {
                if (com_google_android_gms_internal_zzvm_zzc.ats != null) {
                    return false;
                }
            } else if (!this.ats.equals(com_google_android_gms_internal_zzvm_zzc.ats)) {
                return false;
            }
            if (this.asw == null) {
                if (com_google_android_gms_internal_zzvm_zzc.asw != null) {
                    return false;
                }
            } else if (!this.asw.equals(com_google_android_gms_internal_zzvm_zzc.asw)) {
                return false;
            }
            return this.asx == null ? com_google_android_gms_internal_zzvm_zzc.asx == null : this.asx.equals(com_google_android_gms_internal_zzvm_zzc.asx);
        }

        public int hashCode() {
            int i = 0;
            int hashCode = ((this.asw == null ? 0 : this.asw.hashCode()) + (((this.ats == null ? 0 : this.ats.hashCode()) + (((this.Dr == null ? 0 : this.Dr.hashCode()) + (((this.name == null ? 0 : this.name.hashCode()) + ((getClass().getName().hashCode() + 527) * 31)) * 31)) * 31)) * 31)) * 31;
            if (this.asx != null) {
                i = this.asx.hashCode();
            }
            return hashCode + i;
        }

        public void zza(zzard com_google_android_gms_internal_zzard) throws IOException {
            if (this.name != null) {
                com_google_android_gms_internal_zzard.zzr(1, this.name);
            }
            if (this.Dr != null) {
                com_google_android_gms_internal_zzard.zzr(2, this.Dr);
            }
            if (this.ats != null) {
                com_google_android_gms_internal_zzard.zzb(3, this.ats.longValue());
            }
            if (this.asw != null) {
                com_google_android_gms_internal_zzard.zzc(4, this.asw.floatValue());
            }
            if (this.asx != null) {
                com_google_android_gms_internal_zzard.zza(5, this.asx.doubleValue());
            }
            super.zza(com_google_android_gms_internal_zzard);
        }

        public zzc zzao(zzarc com_google_android_gms_internal_zzarc) throws IOException {
            while (true) {
                int cw = com_google_android_gms_internal_zzarc.cw();
                switch (cw) {
                    case 0:
                        break;
                    case 10:
                        this.name = com_google_android_gms_internal_zzarc.readString();
                        continue;
                    case 18:
                        this.Dr = com_google_android_gms_internal_zzarc.readString();
                        continue;
                    case 24:
                        this.ats = Long.valueOf(com_google_android_gms_internal_zzarc.cz());
                        continue;
                    case 37:
                        this.asw = Float.valueOf(com_google_android_gms_internal_zzarc.readFloat());
                        continue;
                    case 41:
                        this.asx = Double.valueOf(com_google_android_gms_internal_zzarc.readDouble());
                        continue;
                    default:
                        if (!zzarn.zzb(com_google_android_gms_internal_zzarc, cw)) {
                            break;
                        }
                        continue;
                }
                return this;
            }
        }

        public /* synthetic */ zzark zzb(zzarc com_google_android_gms_internal_zzarc) throws IOException {
            return zzao(com_google_android_gms_internal_zzarc);
        }

        public zzc zzbzg() {
            this.name = null;
            this.Dr = null;
            this.ats = null;
            this.asw = null;
            this.asx = null;
            this.bqE = -1;
            return this;
        }

        protected int zzx() {
            int zzx = super.zzx();
            if (this.name != null) {
                zzx += zzard.zzs(1, this.name);
            }
            if (this.Dr != null) {
                zzx += zzard.zzs(2, this.Dr);
            }
            if (this.ats != null) {
                zzx += zzard.zzf(3, this.ats.longValue());
            }
            if (this.asw != null) {
                zzx += zzard.zzd(4, this.asw.floatValue());
            }
            return this.asx != null ? zzx + zzard.zzb(5, this.asx.doubleValue()) : zzx;
        }
    }

    public static final class zzd extends zzark {
        public zze[] att;

        public zzd() {
            zzbzh();
        }

        public boolean equals(Object obj) {
            if (obj == this) {
                return true;
            }
            if (!(obj instanceof zzd)) {
                return false;
            }
            return zzari.equals(this.att, ((zzd) obj).att);
        }

        public int hashCode() {
            return ((getClass().getName().hashCode() + 527) * 31) + zzari.hashCode(this.att);
        }

        public void zza(zzard com_google_android_gms_internal_zzard) throws IOException {
            if (this.att != null && this.att.length > 0) {
                for (zzark com_google_android_gms_internal_zzark : this.att) {
                    if (com_google_android_gms_internal_zzark != null) {
                        com_google_android_gms_internal_zzard.zza(1, com_google_android_gms_internal_zzark);
                    }
                }
            }
            super.zza(com_google_android_gms_internal_zzard);
        }

        public zzd zzap(zzarc com_google_android_gms_internal_zzarc) throws IOException {
            while (true) {
                int cw = com_google_android_gms_internal_zzarc.cw();
                switch (cw) {
                    case 0:
                        break;
                    case 10:
                        int zzc = zzarn.zzc(com_google_android_gms_internal_zzarc, 10);
                        cw = this.att == null ? 0 : this.att.length;
                        Object obj = new zze[(zzc + cw)];
                        if (cw != 0) {
                            System.arraycopy(this.att, 0, obj, 0, cw);
                        }
                        while (cw < obj.length - 1) {
                            obj[cw] = new zze();
                            com_google_android_gms_internal_zzarc.zza(obj[cw]);
                            com_google_android_gms_internal_zzarc.cw();
                            cw++;
                        }
                        obj[cw] = new zze();
                        com_google_android_gms_internal_zzarc.zza(obj[cw]);
                        this.att = obj;
                        continue;
                    default:
                        if (!zzarn.zzb(com_google_android_gms_internal_zzarc, cw)) {
                            break;
                        }
                        continue;
                }
                return this;
            }
        }

        public /* synthetic */ zzark zzb(zzarc com_google_android_gms_internal_zzarc) throws IOException {
            return zzap(com_google_android_gms_internal_zzarc);
        }

        public zzd zzbzh() {
            this.att = zze.zzbzi();
            this.bqE = -1;
            return this;
        }

        protected int zzx() {
            int zzx = super.zzx();
            if (this.att != null && this.att.length > 0) {
                for (zzark com_google_android_gms_internal_zzark : this.att) {
                    if (com_google_android_gms_internal_zzark != null) {
                        zzx += zzard.zzc(1, com_google_android_gms_internal_zzark);
                    }
                }
            }
            return zzx;
        }
    }

    public static final class zze extends zzark {
        private static volatile zze[] atu;
        public String afY;
        public String anQ;
        public String anR;
        public String anU;
        public String anY;
        public Long atA;
        public Long atB;
        public Long atC;
        public String atD;
        public String atE;
        public String atF;
        public Integer atG;
        public Long atH;
        public Long atI;
        public String atJ;
        public Boolean atK;
        public String atL;
        public Long atM;
        public Integer atN;
        public Boolean atO;
        public zza[] atP;
        public Integer atQ;
        public Integer atR;
        public Integer atS;
        public String atT;
        public Integer atv;
        public zzb[] atw;
        public zzg[] atx;
        public Long aty;
        public Long atz;
        public String zzck;
        public String zzct;

        public zze() {
            zzbzj();
        }

        public static zze[] zzbzi() {
            if (atu == null) {
                synchronized (zzari.bqD) {
                    if (atu == null) {
                        atu = new zze[0];
                    }
                }
            }
            return atu;
        }

        public boolean equals(Object obj) {
            if (obj == this) {
                return true;
            }
            if (!(obj instanceof zze)) {
                return false;
            }
            zze com_google_android_gms_internal_zzvm_zze = (zze) obj;
            if (this.atv == null) {
                if (com_google_android_gms_internal_zzvm_zze.atv != null) {
                    return false;
                }
            } else if (!this.atv.equals(com_google_android_gms_internal_zzvm_zze.atv)) {
                return false;
            }
            if (!zzari.equals(this.atw, com_google_android_gms_internal_zzvm_zze.atw)) {
                return false;
            }
            if (!zzari.equals(this.atx, com_google_android_gms_internal_zzvm_zze.atx)) {
                return false;
            }
            if (this.aty == null) {
                if (com_google_android_gms_internal_zzvm_zze.aty != null) {
                    return false;
                }
            } else if (!this.aty.equals(com_google_android_gms_internal_zzvm_zze.aty)) {
                return false;
            }
            if (this.atz == null) {
                if (com_google_android_gms_internal_zzvm_zze.atz != null) {
                    return false;
                }
            } else if (!this.atz.equals(com_google_android_gms_internal_zzvm_zze.atz)) {
                return false;
            }
            if (this.atA == null) {
                if (com_google_android_gms_internal_zzvm_zze.atA != null) {
                    return false;
                }
            } else if (!this.atA.equals(com_google_android_gms_internal_zzvm_zze.atA)) {
                return false;
            }
            if (this.atB == null) {
                if (com_google_android_gms_internal_zzvm_zze.atB != null) {
                    return false;
                }
            } else if (!this.atB.equals(com_google_android_gms_internal_zzvm_zze.atB)) {
                return false;
            }
            if (this.atC == null) {
                if (com_google_android_gms_internal_zzvm_zze.atC != null) {
                    return false;
                }
            } else if (!this.atC.equals(com_google_android_gms_internal_zzvm_zze.atC)) {
                return false;
            }
            if (this.atD == null) {
                if (com_google_android_gms_internal_zzvm_zze.atD != null) {
                    return false;
                }
            } else if (!this.atD.equals(com_google_android_gms_internal_zzvm_zze.atD)) {
                return false;
            }
            if (this.zzct == null) {
                if (com_google_android_gms_internal_zzvm_zze.zzct != null) {
                    return false;
                }
            } else if (!this.zzct.equals(com_google_android_gms_internal_zzvm_zze.zzct)) {
                return false;
            }
            if (this.atE == null) {
                if (com_google_android_gms_internal_zzvm_zze.atE != null) {
                    return false;
                }
            } else if (!this.atE.equals(com_google_android_gms_internal_zzvm_zze.atE)) {
                return false;
            }
            if (this.atF == null) {
                if (com_google_android_gms_internal_zzvm_zze.atF != null) {
                    return false;
                }
            } else if (!this.atF.equals(com_google_android_gms_internal_zzvm_zze.atF)) {
                return false;
            }
            if (this.atG == null) {
                if (com_google_android_gms_internal_zzvm_zze.atG != null) {
                    return false;
                }
            } else if (!this.atG.equals(com_google_android_gms_internal_zzvm_zze.atG)) {
                return false;
            }
            if (this.anR == null) {
                if (com_google_android_gms_internal_zzvm_zze.anR != null) {
                    return false;
                }
            } else if (!this.anR.equals(com_google_android_gms_internal_zzvm_zze.anR)) {
                return false;
            }
            if (this.zzck == null) {
                if (com_google_android_gms_internal_zzvm_zze.zzck != null) {
                    return false;
                }
            } else if (!this.zzck.equals(com_google_android_gms_internal_zzvm_zze.zzck)) {
                return false;
            }
            if (this.afY == null) {
                if (com_google_android_gms_internal_zzvm_zze.afY != null) {
                    return false;
                }
            } else if (!this.afY.equals(com_google_android_gms_internal_zzvm_zze.afY)) {
                return false;
            }
            if (this.atH == null) {
                if (com_google_android_gms_internal_zzvm_zze.atH != null) {
                    return false;
                }
            } else if (!this.atH.equals(com_google_android_gms_internal_zzvm_zze.atH)) {
                return false;
            }
            if (this.atI == null) {
                if (com_google_android_gms_internal_zzvm_zze.atI != null) {
                    return false;
                }
            } else if (!this.atI.equals(com_google_android_gms_internal_zzvm_zze.atI)) {
                return false;
            }
            if (this.atJ == null) {
                if (com_google_android_gms_internal_zzvm_zze.atJ != null) {
                    return false;
                }
            } else if (!this.atJ.equals(com_google_android_gms_internal_zzvm_zze.atJ)) {
                return false;
            }
            if (this.atK == null) {
                if (com_google_android_gms_internal_zzvm_zze.atK != null) {
                    return false;
                }
            } else if (!this.atK.equals(com_google_android_gms_internal_zzvm_zze.atK)) {
                return false;
            }
            if (this.atL == null) {
                if (com_google_android_gms_internal_zzvm_zze.atL != null) {
                    return false;
                }
            } else if (!this.atL.equals(com_google_android_gms_internal_zzvm_zze.atL)) {
                return false;
            }
            if (this.atM == null) {
                if (com_google_android_gms_internal_zzvm_zze.atM != null) {
                    return false;
                }
            } else if (!this.atM.equals(com_google_android_gms_internal_zzvm_zze.atM)) {
                return false;
            }
            if (this.atN == null) {
                if (com_google_android_gms_internal_zzvm_zze.atN != null) {
                    return false;
                }
            } else if (!this.atN.equals(com_google_android_gms_internal_zzvm_zze.atN)) {
                return false;
            }
            if (this.anU == null) {
                if (com_google_android_gms_internal_zzvm_zze.anU != null) {
                    return false;
                }
            } else if (!this.anU.equals(com_google_android_gms_internal_zzvm_zze.anU)) {
                return false;
            }
            if (this.anQ == null) {
                if (com_google_android_gms_internal_zzvm_zze.anQ != null) {
                    return false;
                }
            } else if (!this.anQ.equals(com_google_android_gms_internal_zzvm_zze.anQ)) {
                return false;
            }
            if (this.atO == null) {
                if (com_google_android_gms_internal_zzvm_zze.atO != null) {
                    return false;
                }
            } else if (!this.atO.equals(com_google_android_gms_internal_zzvm_zze.atO)) {
                return false;
            }
            if (!zzari.equals(this.atP, com_google_android_gms_internal_zzvm_zze.atP)) {
                return false;
            }
            if (this.anY == null) {
                if (com_google_android_gms_internal_zzvm_zze.anY != null) {
                    return false;
                }
            } else if (!this.anY.equals(com_google_android_gms_internal_zzvm_zze.anY)) {
                return false;
            }
            if (this.atQ == null) {
                if (com_google_android_gms_internal_zzvm_zze.atQ != null) {
                    return false;
                }
            } else if (!this.atQ.equals(com_google_android_gms_internal_zzvm_zze.atQ)) {
                return false;
            }
            if (this.atR == null) {
                if (com_google_android_gms_internal_zzvm_zze.atR != null) {
                    return false;
                }
            } else if (!this.atR.equals(com_google_android_gms_internal_zzvm_zze.atR)) {
                return false;
            }
            if (this.atS == null) {
                if (com_google_android_gms_internal_zzvm_zze.atS != null) {
                    return false;
                }
            } else if (!this.atS.equals(com_google_android_gms_internal_zzvm_zze.atS)) {
                return false;
            }
            return this.atT == null ? com_google_android_gms_internal_zzvm_zze.atT == null : this.atT.equals(com_google_android_gms_internal_zzvm_zze.atT);
        }

        public int hashCode() {
            int i = 0;
            int hashCode = ((this.atS == null ? 0 : this.atS.hashCode()) + (((this.atR == null ? 0 : this.atR.hashCode()) + (((this.atQ == null ? 0 : this.atQ.hashCode()) + (((this.anY == null ? 0 : this.anY.hashCode()) + (((((this.atO == null ? 0 : this.atO.hashCode()) + (((this.anQ == null ? 0 : this.anQ.hashCode()) + (((this.anU == null ? 0 : this.anU.hashCode()) + (((this.atN == null ? 0 : this.atN.hashCode()) + (((this.atM == null ? 0 : this.atM.hashCode()) + (((this.atL == null ? 0 : this.atL.hashCode()) + (((this.atK == null ? 0 : this.atK.hashCode()) + (((this.atJ == null ? 0 : this.atJ.hashCode()) + (((this.atI == null ? 0 : this.atI.hashCode()) + (((this.atH == null ? 0 : this.atH.hashCode()) + (((this.afY == null ? 0 : this.afY.hashCode()) + (((this.zzck == null ? 0 : this.zzck.hashCode()) + (((this.anR == null ? 0 : this.anR.hashCode()) + (((this.atG == null ? 0 : this.atG.hashCode()) + (((this.atF == null ? 0 : this.atF.hashCode()) + (((this.atE == null ? 0 : this.atE.hashCode()) + (((this.zzct == null ? 0 : this.zzct.hashCode()) + (((this.atD == null ? 0 : this.atD.hashCode()) + (((this.atC == null ? 0 : this.atC.hashCode()) + (((this.atB == null ? 0 : this.atB.hashCode()) + (((this.atA == null ? 0 : this.atA.hashCode()) + (((this.atz == null ? 0 : this.atz.hashCode()) + (((this.aty == null ? 0 : this.aty.hashCode()) + (((((((this.atv == null ? 0 : this.atv.hashCode()) + ((getClass().getName().hashCode() + 527) * 31)) * 31) + zzari.hashCode(this.atw)) * 31) + zzari.hashCode(this.atx)) * 31)) * 31)) * 31)) * 31)) * 31)) * 31)) * 31)) * 31)) * 31)) * 31)) * 31)) * 31)) * 31)) * 31)) * 31)) * 31)) * 31)) * 31)) * 31)) * 31)) * 31)) * 31)) * 31)) * 31) + zzari.hashCode(this.atP)) * 31)) * 31)) * 31)) * 31)) * 31;
            if (this.atT != null) {
                i = this.atT.hashCode();
            }
            return hashCode + i;
        }

        public void zza(zzard com_google_android_gms_internal_zzard) throws IOException {
            int i = 0;
            if (this.atv != null) {
                com_google_android_gms_internal_zzard.zzae(1, this.atv.intValue());
            }
            if (this.atw != null && this.atw.length > 0) {
                for (zzark com_google_android_gms_internal_zzark : this.atw) {
                    if (com_google_android_gms_internal_zzark != null) {
                        com_google_android_gms_internal_zzard.zza(2, com_google_android_gms_internal_zzark);
                    }
                }
            }
            if (this.atx != null && this.atx.length > 0) {
                for (zzark com_google_android_gms_internal_zzark2 : this.atx) {
                    if (com_google_android_gms_internal_zzark2 != null) {
                        com_google_android_gms_internal_zzard.zza(3, com_google_android_gms_internal_zzark2);
                    }
                }
            }
            if (this.aty != null) {
                com_google_android_gms_internal_zzard.zzb(4, this.aty.longValue());
            }
            if (this.atz != null) {
                com_google_android_gms_internal_zzard.zzb(5, this.atz.longValue());
            }
            if (this.atA != null) {
                com_google_android_gms_internal_zzard.zzb(6, this.atA.longValue());
            }
            if (this.atC != null) {
                com_google_android_gms_internal_zzard.zzb(7, this.atC.longValue());
            }
            if (this.atD != null) {
                com_google_android_gms_internal_zzard.zzr(8, this.atD);
            }
            if (this.zzct != null) {
                com_google_android_gms_internal_zzard.zzr(9, this.zzct);
            }
            if (this.atE != null) {
                com_google_android_gms_internal_zzard.zzr(10, this.atE);
            }
            if (this.atF != null) {
                com_google_android_gms_internal_zzard.zzr(11, this.atF);
            }
            if (this.atG != null) {
                com_google_android_gms_internal_zzard.zzae(12, this.atG.intValue());
            }
            if (this.anR != null) {
                com_google_android_gms_internal_zzard.zzr(13, this.anR);
            }
            if (this.zzck != null) {
                com_google_android_gms_internal_zzard.zzr(14, this.zzck);
            }
            if (this.afY != null) {
                com_google_android_gms_internal_zzard.zzr(16, this.afY);
            }
            if (this.atH != null) {
                com_google_android_gms_internal_zzard.zzb(17, this.atH.longValue());
            }
            if (this.atI != null) {
                com_google_android_gms_internal_zzard.zzb(18, this.atI.longValue());
            }
            if (this.atJ != null) {
                com_google_android_gms_internal_zzard.zzr(19, this.atJ);
            }
            if (this.atK != null) {
                com_google_android_gms_internal_zzard.zzj(20, this.atK.booleanValue());
            }
            if (this.atL != null) {
                com_google_android_gms_internal_zzard.zzr(21, this.atL);
            }
            if (this.atM != null) {
                com_google_android_gms_internal_zzard.zzb(22, this.atM.longValue());
            }
            if (this.atN != null) {
                com_google_android_gms_internal_zzard.zzae(23, this.atN.intValue());
            }
            if (this.anU != null) {
                com_google_android_gms_internal_zzard.zzr(24, this.anU);
            }
            if (this.anQ != null) {
                com_google_android_gms_internal_zzard.zzr(25, this.anQ);
            }
            if (this.atB != null) {
                com_google_android_gms_internal_zzard.zzb(26, this.atB.longValue());
            }
            if (this.atO != null) {
                com_google_android_gms_internal_zzard.zzj(28, this.atO.booleanValue());
            }
            if (this.atP != null && this.atP.length > 0) {
                while (i < this.atP.length) {
                    zzark com_google_android_gms_internal_zzark3 = this.atP[i];
                    if (com_google_android_gms_internal_zzark3 != null) {
                        com_google_android_gms_internal_zzard.zza(29, com_google_android_gms_internal_zzark3);
                    }
                    i++;
                }
            }
            if (this.anY != null) {
                com_google_android_gms_internal_zzard.zzr(30, this.anY);
            }
            if (this.atQ != null) {
                com_google_android_gms_internal_zzard.zzae(31, this.atQ.intValue());
            }
            if (this.atR != null) {
                com_google_android_gms_internal_zzard.zzae(32, this.atR.intValue());
            }
            if (this.atS != null) {
                com_google_android_gms_internal_zzard.zzae(33, this.atS.intValue());
            }
            if (this.atT != null) {
                com_google_android_gms_internal_zzard.zzr(34, this.atT);
            }
            super.zza(com_google_android_gms_internal_zzard);
        }

        public zze zzaq(zzarc com_google_android_gms_internal_zzarc) throws IOException {
            while (true) {
                int cw = com_google_android_gms_internal_zzarc.cw();
                int zzc;
                Object obj;
                switch (cw) {
                    case 0:
                        break;
                    case 8:
                        this.atv = Integer.valueOf(com_google_android_gms_internal_zzarc.cA());
                        continue;
                    case 18:
                        zzc = zzarn.zzc(com_google_android_gms_internal_zzarc, 18);
                        cw = this.atw == null ? 0 : this.atw.length;
                        obj = new zzb[(zzc + cw)];
                        if (cw != 0) {
                            System.arraycopy(this.atw, 0, obj, 0, cw);
                        }
                        while (cw < obj.length - 1) {
                            obj[cw] = new zzb();
                            com_google_android_gms_internal_zzarc.zza(obj[cw]);
                            com_google_android_gms_internal_zzarc.cw();
                            cw++;
                        }
                        obj[cw] = new zzb();
                        com_google_android_gms_internal_zzarc.zza(obj[cw]);
                        this.atw = obj;
                        continue;
                    case NalUnitTypes.NAL_TYPE_RSV_VCL26 /*26*/:
                        zzc = zzarn.zzc(com_google_android_gms_internal_zzarc, 26);
                        cw = this.atx == null ? 0 : this.atx.length;
                        obj = new zzg[(zzc + cw)];
                        if (cw != 0) {
                            System.arraycopy(this.atx, 0, obj, 0, cw);
                        }
                        while (cw < obj.length - 1) {
                            obj[cw] = new zzg();
                            com_google_android_gms_internal_zzarc.zza(obj[cw]);
                            com_google_android_gms_internal_zzarc.cw();
                            cw++;
                        }
                        obj[cw] = new zzg();
                        com_google_android_gms_internal_zzarc.zza(obj[cw]);
                        this.atx = obj;
                        continue;
                    case 32:
                        this.aty = Long.valueOf(com_google_android_gms_internal_zzarc.cz());
                        continue;
                    case MotionEventCompat.AXIS_GENERIC_9 /*40*/:
                        this.atz = Long.valueOf(com_google_android_gms_internal_zzarc.cz());
                        continue;
                    case 48:
                        this.atA = Long.valueOf(com_google_android_gms_internal_zzarc.cz());
                        continue;
                    case 56:
                        this.atC = Long.valueOf(com_google_android_gms_internal_zzarc.cz());
                        continue;
                    case 66:
                        this.atD = com_google_android_gms_internal_zzarc.readString();
                        continue;
                    case 74:
                        this.zzct = com_google_android_gms_internal_zzarc.readString();
                        continue;
                    case 82:
                        this.atE = com_google_android_gms_internal_zzarc.readString();
                        continue;
                    case 90:
                        this.atF = com_google_android_gms_internal_zzarc.readString();
                        continue;
                    case 96:
                        this.atG = Integer.valueOf(com_google_android_gms_internal_zzarc.cA());
                        continue;
                    case 106:
                        this.anR = com_google_android_gms_internal_zzarc.readString();
                        continue;
                    case 114:
                        this.zzck = com_google_android_gms_internal_zzarc.readString();
                        continue;
                    case 130:
                        this.afY = com_google_android_gms_internal_zzarc.readString();
                        continue;
                    case 136:
                        this.atH = Long.valueOf(com_google_android_gms_internal_zzarc.cz());
                        continue;
                    case 144:
                        this.atI = Long.valueOf(com_google_android_gms_internal_zzarc.cz());
                        continue;
                    case 154:
                        this.atJ = com_google_android_gms_internal_zzarc.readString();
                        continue;
                    case 160:
                        this.atK = Boolean.valueOf(com_google_android_gms_internal_zzarc.cC());
                        continue;
                    case 170:
                        this.atL = com_google_android_gms_internal_zzarc.readString();
                        continue;
                    case 176:
                        this.atM = Long.valueOf(com_google_android_gms_internal_zzarc.cz());
                        continue;
                    case 184:
                        this.atN = Integer.valueOf(com_google_android_gms_internal_zzarc.cA());
                        continue;
                    case 194:
                        this.anU = com_google_android_gms_internal_zzarc.readString();
                        continue;
                    case 202:
                        this.anQ = com_google_android_gms_internal_zzarc.readString();
                        continue;
                    case 208:
                        this.atB = Long.valueOf(com_google_android_gms_internal_zzarc.cz());
                        continue;
                    case 224:
                        this.atO = Boolean.valueOf(com_google_android_gms_internal_zzarc.cC());
                        continue;
                    case 234:
                        zzc = zzarn.zzc(com_google_android_gms_internal_zzarc, 234);
                        cw = this.atP == null ? 0 : this.atP.length;
                        obj = new zza[(zzc + cw)];
                        if (cw != 0) {
                            System.arraycopy(this.atP, 0, obj, 0, cw);
                        }
                        while (cw < obj.length - 1) {
                            obj[cw] = new zza();
                            com_google_android_gms_internal_zzarc.zza(obj[cw]);
                            com_google_android_gms_internal_zzarc.cw();
                            cw++;
                        }
                        obj[cw] = new zza();
                        com_google_android_gms_internal_zzarc.zza(obj[cw]);
                        this.atP = obj;
                        continue;
                    case 242:
                        this.anY = com_google_android_gms_internal_zzarc.readString();
                        continue;
                    case 248:
                        this.atQ = Integer.valueOf(com_google_android_gms_internal_zzarc.cA());
                        continue;
                    case 256:
                        this.atR = Integer.valueOf(com_google_android_gms_internal_zzarc.cA());
                        continue;
                    case 264:
                        this.atS = Integer.valueOf(com_google_android_gms_internal_zzarc.cA());
                        continue;
                    case 274:
                        this.atT = com_google_android_gms_internal_zzarc.readString();
                        continue;
                    default:
                        if (!zzarn.zzb(com_google_android_gms_internal_zzarc, cw)) {
                            break;
                        }
                        continue;
                }
                return this;
            }
        }

        public /* synthetic */ zzark zzb(zzarc com_google_android_gms_internal_zzarc) throws IOException {
            return zzaq(com_google_android_gms_internal_zzarc);
        }

        public zze zzbzj() {
            this.atv = null;
            this.atw = zzb.zzbzd();
            this.atx = zzg.zzbzl();
            this.aty = null;
            this.atz = null;
            this.atA = null;
            this.atB = null;
            this.atC = null;
            this.atD = null;
            this.zzct = null;
            this.atE = null;
            this.atF = null;
            this.atG = null;
            this.anR = null;
            this.zzck = null;
            this.afY = null;
            this.atH = null;
            this.atI = null;
            this.atJ = null;
            this.atK = null;
            this.atL = null;
            this.atM = null;
            this.atN = null;
            this.anU = null;
            this.anQ = null;
            this.atO = null;
            this.atP = zza.zzbzb();
            this.anY = null;
            this.atQ = null;
            this.atR = null;
            this.atS = null;
            this.atT = null;
            this.bqE = -1;
            return this;
        }

        protected int zzx() {
            int i;
            int i2 = 0;
            int zzx = super.zzx();
            if (this.atv != null) {
                zzx += zzard.zzag(1, this.atv.intValue());
            }
            if (this.atw != null && this.atw.length > 0) {
                i = zzx;
                for (zzark com_google_android_gms_internal_zzark : this.atw) {
                    if (com_google_android_gms_internal_zzark != null) {
                        i += zzard.zzc(2, com_google_android_gms_internal_zzark);
                    }
                }
                zzx = i;
            }
            if (this.atx != null && this.atx.length > 0) {
                i = zzx;
                for (zzark com_google_android_gms_internal_zzark2 : this.atx) {
                    if (com_google_android_gms_internal_zzark2 != null) {
                        i += zzard.zzc(3, com_google_android_gms_internal_zzark2);
                    }
                }
                zzx = i;
            }
            if (this.aty != null) {
                zzx += zzard.zzf(4, this.aty.longValue());
            }
            if (this.atz != null) {
                zzx += zzard.zzf(5, this.atz.longValue());
            }
            if (this.atA != null) {
                zzx += zzard.zzf(6, this.atA.longValue());
            }
            if (this.atC != null) {
                zzx += zzard.zzf(7, this.atC.longValue());
            }
            if (this.atD != null) {
                zzx += zzard.zzs(8, this.atD);
            }
            if (this.zzct != null) {
                zzx += zzard.zzs(9, this.zzct);
            }
            if (this.atE != null) {
                zzx += zzard.zzs(10, this.atE);
            }
            if (this.atF != null) {
                zzx += zzard.zzs(11, this.atF);
            }
            if (this.atG != null) {
                zzx += zzard.zzag(12, this.atG.intValue());
            }
            if (this.anR != null) {
                zzx += zzard.zzs(13, this.anR);
            }
            if (this.zzck != null) {
                zzx += zzard.zzs(14, this.zzck);
            }
            if (this.afY != null) {
                zzx += zzard.zzs(16, this.afY);
            }
            if (this.atH != null) {
                zzx += zzard.zzf(17, this.atH.longValue());
            }
            if (this.atI != null) {
                zzx += zzard.zzf(18, this.atI.longValue());
            }
            if (this.atJ != null) {
                zzx += zzard.zzs(19, this.atJ);
            }
            if (this.atK != null) {
                zzx += zzard.zzk(20, this.atK.booleanValue());
            }
            if (this.atL != null) {
                zzx += zzard.zzs(21, this.atL);
            }
            if (this.atM != null) {
                zzx += zzard.zzf(22, this.atM.longValue());
            }
            if (this.atN != null) {
                zzx += zzard.zzag(23, this.atN.intValue());
            }
            if (this.anU != null) {
                zzx += zzard.zzs(24, this.anU);
            }
            if (this.anQ != null) {
                zzx += zzard.zzs(25, this.anQ);
            }
            if (this.atB != null) {
                zzx += zzard.zzf(26, this.atB.longValue());
            }
            if (this.atO != null) {
                zzx += zzard.zzk(28, this.atO.booleanValue());
            }
            if (this.atP != null && this.atP.length > 0) {
                while (i2 < this.atP.length) {
                    zzark com_google_android_gms_internal_zzark3 = this.atP[i2];
                    if (com_google_android_gms_internal_zzark3 != null) {
                        zzx += zzard.zzc(29, com_google_android_gms_internal_zzark3);
                    }
                    i2++;
                }
            }
            if (this.anY != null) {
                zzx += zzard.zzs(30, this.anY);
            }
            if (this.atQ != null) {
                zzx += zzard.zzag(31, this.atQ.intValue());
            }
            if (this.atR != null) {
                zzx += zzard.zzag(32, this.atR.intValue());
            }
            if (this.atS != null) {
                zzx += zzard.zzag(33, this.atS.intValue());
            }
            return this.atT != null ? zzx + zzard.zzs(34, this.atT) : zzx;
        }
    }

    public static final class zzf extends zzark {
        public long[] atU;
        public long[] atV;

        public zzf() {
            zzbzk();
        }

        public boolean equals(Object obj) {
            if (obj == this) {
                return true;
            }
            if (!(obj instanceof zzf)) {
                return false;
            }
            zzf com_google_android_gms_internal_zzvm_zzf = (zzf) obj;
            return !zzari.equals(this.atU, com_google_android_gms_internal_zzvm_zzf.atU) ? false : zzari.equals(this.atV, com_google_android_gms_internal_zzvm_zzf.atV);
        }

        public int hashCode() {
            return ((((getClass().getName().hashCode() + 527) * 31) + zzari.hashCode(this.atU)) * 31) + zzari.hashCode(this.atV);
        }

        public void zza(zzard com_google_android_gms_internal_zzard) throws IOException {
            int i = 0;
            if (this.atU != null && this.atU.length > 0) {
                for (long zza : this.atU) {
                    com_google_android_gms_internal_zzard.zza(1, zza);
                }
            }
            if (this.atV != null && this.atV.length > 0) {
                while (i < this.atV.length) {
                    com_google_android_gms_internal_zzard.zza(2, this.atV[i]);
                    i++;
                }
            }
            super.zza(com_google_android_gms_internal_zzard);
        }

        public zzf zzar(zzarc com_google_android_gms_internal_zzarc) throws IOException {
            while (true) {
                int cw = com_google_android_gms_internal_zzarc.cw();
                int zzc;
                Object obj;
                int zzahc;
                Object obj2;
                switch (cw) {
                    case 0:
                        break;
                    case 8:
                        zzc = zzarn.zzc(com_google_android_gms_internal_zzarc, 8);
                        cw = this.atU == null ? 0 : this.atU.length;
                        obj = new long[(zzc + cw)];
                        if (cw != 0) {
                            System.arraycopy(this.atU, 0, obj, 0, cw);
                        }
                        while (cw < obj.length - 1) {
                            obj[cw] = com_google_android_gms_internal_zzarc.cy();
                            com_google_android_gms_internal_zzarc.cw();
                            cw++;
                        }
                        obj[cw] = com_google_android_gms_internal_zzarc.cy();
                        this.atU = obj;
                        continue;
                    case 10:
                        zzahc = com_google_android_gms_internal_zzarc.zzahc(com_google_android_gms_internal_zzarc.cF());
                        zzc = com_google_android_gms_internal_zzarc.getPosition();
                        cw = 0;
                        while (com_google_android_gms_internal_zzarc.cK() > 0) {
                            com_google_android_gms_internal_zzarc.cy();
                            cw++;
                        }
                        com_google_android_gms_internal_zzarc.zzahe(zzc);
                        zzc = this.atU == null ? 0 : this.atU.length;
                        obj2 = new long[(cw + zzc)];
                        if (zzc != 0) {
                            System.arraycopy(this.atU, 0, obj2, 0, zzc);
                        }
                        while (zzc < obj2.length) {
                            obj2[zzc] = com_google_android_gms_internal_zzarc.cy();
                            zzc++;
                        }
                        this.atU = obj2;
                        com_google_android_gms_internal_zzarc.zzahd(zzahc);
                        continue;
                    case 16:
                        zzc = zzarn.zzc(com_google_android_gms_internal_zzarc, 16);
                        cw = this.atV == null ? 0 : this.atV.length;
                        obj = new long[(zzc + cw)];
                        if (cw != 0) {
                            System.arraycopy(this.atV, 0, obj, 0, cw);
                        }
                        while (cw < obj.length - 1) {
                            obj[cw] = com_google_android_gms_internal_zzarc.cy();
                            com_google_android_gms_internal_zzarc.cw();
                            cw++;
                        }
                        obj[cw] = com_google_android_gms_internal_zzarc.cy();
                        this.atV = obj;
                        continue;
                    case 18:
                        zzahc = com_google_android_gms_internal_zzarc.zzahc(com_google_android_gms_internal_zzarc.cF());
                        zzc = com_google_android_gms_internal_zzarc.getPosition();
                        cw = 0;
                        while (com_google_android_gms_internal_zzarc.cK() > 0) {
                            com_google_android_gms_internal_zzarc.cy();
                            cw++;
                        }
                        com_google_android_gms_internal_zzarc.zzahe(zzc);
                        zzc = this.atV == null ? 0 : this.atV.length;
                        obj2 = new long[(cw + zzc)];
                        if (zzc != 0) {
                            System.arraycopy(this.atV, 0, obj2, 0, zzc);
                        }
                        while (zzc < obj2.length) {
                            obj2[zzc] = com_google_android_gms_internal_zzarc.cy();
                            zzc++;
                        }
                        this.atV = obj2;
                        com_google_android_gms_internal_zzarc.zzahd(zzahc);
                        continue;
                    default:
                        if (!zzarn.zzb(com_google_android_gms_internal_zzarc, cw)) {
                            break;
                        }
                        continue;
                }
                return this;
            }
        }

        public /* synthetic */ zzark zzb(zzarc com_google_android_gms_internal_zzarc) throws IOException {
            return zzar(com_google_android_gms_internal_zzarc);
        }

        public zzf zzbzk() {
            this.atU = zzarn.bqG;
            this.atV = zzarn.bqG;
            this.bqE = -1;
            return this;
        }

        protected int zzx() {
            int i;
            int i2;
            int i3 = 0;
            int zzx = super.zzx();
            if (this.atU == null || this.atU.length <= 0) {
                i = zzx;
            } else {
                i2 = 0;
                for (long zzda : this.atU) {
                    i2 += zzard.zzda(zzda);
                }
                i = (zzx + i2) + (this.atU.length * 1);
            }
            if (this.atV == null || this.atV.length <= 0) {
                return i;
            }
            i2 = 0;
            while (i3 < this.atV.length) {
                i2 += zzard.zzda(this.atV[i3]);
                i3++;
            }
            return (i + i2) + (this.atV.length * 1);
        }
    }

    public static final class zzg extends zzark {
        private static volatile zzg[] atW;
        public String Dr;
        public Float asw;
        public Double asx;
        public Long atX;
        public Long ats;
        public String name;

        public zzg() {
            zzbzm();
        }

        public static zzg[] zzbzl() {
            if (atW == null) {
                synchronized (zzari.bqD) {
                    if (atW == null) {
                        atW = new zzg[0];
                    }
                }
            }
            return atW;
        }

        public boolean equals(Object obj) {
            if (obj == this) {
                return true;
            }
            if (!(obj instanceof zzg)) {
                return false;
            }
            zzg com_google_android_gms_internal_zzvm_zzg = (zzg) obj;
            if (this.atX == null) {
                if (com_google_android_gms_internal_zzvm_zzg.atX != null) {
                    return false;
                }
            } else if (!this.atX.equals(com_google_android_gms_internal_zzvm_zzg.atX)) {
                return false;
            }
            if (this.name == null) {
                if (com_google_android_gms_internal_zzvm_zzg.name != null) {
                    return false;
                }
            } else if (!this.name.equals(com_google_android_gms_internal_zzvm_zzg.name)) {
                return false;
            }
            if (this.Dr == null) {
                if (com_google_android_gms_internal_zzvm_zzg.Dr != null) {
                    return false;
                }
            } else if (!this.Dr.equals(com_google_android_gms_internal_zzvm_zzg.Dr)) {
                return false;
            }
            if (this.ats == null) {
                if (com_google_android_gms_internal_zzvm_zzg.ats != null) {
                    return false;
                }
            } else if (!this.ats.equals(com_google_android_gms_internal_zzvm_zzg.ats)) {
                return false;
            }
            if (this.asw == null) {
                if (com_google_android_gms_internal_zzvm_zzg.asw != null) {
                    return false;
                }
            } else if (!this.asw.equals(com_google_android_gms_internal_zzvm_zzg.asw)) {
                return false;
            }
            return this.asx == null ? com_google_android_gms_internal_zzvm_zzg.asx == null : this.asx.equals(com_google_android_gms_internal_zzvm_zzg.asx);
        }

        public int hashCode() {
            int i = 0;
            int hashCode = ((this.asw == null ? 0 : this.asw.hashCode()) + (((this.ats == null ? 0 : this.ats.hashCode()) + (((this.Dr == null ? 0 : this.Dr.hashCode()) + (((this.name == null ? 0 : this.name.hashCode()) + (((this.atX == null ? 0 : this.atX.hashCode()) + ((getClass().getName().hashCode() + 527) * 31)) * 31)) * 31)) * 31)) * 31)) * 31;
            if (this.asx != null) {
                i = this.asx.hashCode();
            }
            return hashCode + i;
        }

        public void zza(zzard com_google_android_gms_internal_zzard) throws IOException {
            if (this.atX != null) {
                com_google_android_gms_internal_zzard.zzb(1, this.atX.longValue());
            }
            if (this.name != null) {
                com_google_android_gms_internal_zzard.zzr(2, this.name);
            }
            if (this.Dr != null) {
                com_google_android_gms_internal_zzard.zzr(3, this.Dr);
            }
            if (this.ats != null) {
                com_google_android_gms_internal_zzard.zzb(4, this.ats.longValue());
            }
            if (this.asw != null) {
                com_google_android_gms_internal_zzard.zzc(5, this.asw.floatValue());
            }
            if (this.asx != null) {
                com_google_android_gms_internal_zzard.zza(6, this.asx.doubleValue());
            }
            super.zza(com_google_android_gms_internal_zzard);
        }

        public zzg zzas(zzarc com_google_android_gms_internal_zzarc) throws IOException {
            while (true) {
                int cw = com_google_android_gms_internal_zzarc.cw();
                switch (cw) {
                    case 0:
                        break;
                    case 8:
                        this.atX = Long.valueOf(com_google_android_gms_internal_zzarc.cz());
                        continue;
                    case 18:
                        this.name = com_google_android_gms_internal_zzarc.readString();
                        continue;
                    case NalUnitTypes.NAL_TYPE_RSV_VCL26 /*26*/:
                        this.Dr = com_google_android_gms_internal_zzarc.readString();
                        continue;
                    case 32:
                        this.ats = Long.valueOf(com_google_android_gms_internal_zzarc.cz());
                        continue;
                    case MotionEventCompat.AXIS_GENERIC_14 /*45*/:
                        this.asw = Float.valueOf(com_google_android_gms_internal_zzarc.readFloat());
                        continue;
                    case 49:
                        this.asx = Double.valueOf(com_google_android_gms_internal_zzarc.readDouble());
                        continue;
                    default:
                        if (!zzarn.zzb(com_google_android_gms_internal_zzarc, cw)) {
                            break;
                        }
                        continue;
                }
                return this;
            }
        }

        public /* synthetic */ zzark zzb(zzarc com_google_android_gms_internal_zzarc) throws IOException {
            return zzas(com_google_android_gms_internal_zzarc);
        }

        public zzg zzbzm() {
            this.atX = null;
            this.name = null;
            this.Dr = null;
            this.ats = null;
            this.asw = null;
            this.asx = null;
            this.bqE = -1;
            return this;
        }

        protected int zzx() {
            int zzx = super.zzx();
            if (this.atX != null) {
                zzx += zzard.zzf(1, this.atX.longValue());
            }
            if (this.name != null) {
                zzx += zzard.zzs(2, this.name);
            }
            if (this.Dr != null) {
                zzx += zzard.zzs(3, this.Dr);
            }
            if (this.ats != null) {
                zzx += zzard.zzf(4, this.ats.longValue());
            }
            if (this.asw != null) {
                zzx += zzard.zzd(5, this.asw.floatValue());
            }
            return this.asx != null ? zzx + zzard.zzb(6, this.asx.doubleValue()) : zzx;
        }
    }
}
