package com.google.android.gms.internal;

import java.io.IOException;

public interface zzvl {

    public static final class zza extends zzark {
        private static volatile zza[] ata;
        public Boolean atb;
        public Boolean atc;
        public String name;

        public zza() {
            zzbyx();
        }

        public static zza[] zzbyw() {
            if (ata == null) {
                synchronized (zzari.bqD) {
                    if (ata == null) {
                        ata = new zza[0];
                    }
                }
            }
            return ata;
        }

        public boolean equals(Object obj) {
            if (obj == this) {
                return true;
            }
            if (!(obj instanceof zza)) {
                return false;
            }
            zza com_google_android_gms_internal_zzvl_zza = (zza) obj;
            if (this.name == null) {
                if (com_google_android_gms_internal_zzvl_zza.name != null) {
                    return false;
                }
            } else if (!this.name.equals(com_google_android_gms_internal_zzvl_zza.name)) {
                return false;
            }
            if (this.atb == null) {
                if (com_google_android_gms_internal_zzvl_zza.atb != null) {
                    return false;
                }
            } else if (!this.atb.equals(com_google_android_gms_internal_zzvl_zza.atb)) {
                return false;
            }
            return this.atc == null ? com_google_android_gms_internal_zzvl_zza.atc == null : this.atc.equals(com_google_android_gms_internal_zzvl_zza.atc);
        }

        public int hashCode() {
            int i = 0;
            int hashCode = ((this.atb == null ? 0 : this.atb.hashCode()) + (((this.name == null ? 0 : this.name.hashCode()) + ((getClass().getName().hashCode() + 527) * 31)) * 31)) * 31;
            if (this.atc != null) {
                i = this.atc.hashCode();
            }
            return hashCode + i;
        }

        public void zza(zzard com_google_android_gms_internal_zzard) throws IOException {
            if (this.name != null) {
                com_google_android_gms_internal_zzard.zzr(1, this.name);
            }
            if (this.atb != null) {
                com_google_android_gms_internal_zzard.zzj(2, this.atb.booleanValue());
            }
            if (this.atc != null) {
                com_google_android_gms_internal_zzard.zzj(3, this.atc.booleanValue());
            }
            super.zza(com_google_android_gms_internal_zzard);
        }

        public zza zzaj(zzarc com_google_android_gms_internal_zzarc) throws IOException {
            while (true) {
                int cw = com_google_android_gms_internal_zzarc.cw();
                switch (cw) {
                    case 0:
                        break;
                    case 10:
                        this.name = com_google_android_gms_internal_zzarc.readString();
                        continue;
                    case 16:
                        this.atb = Boolean.valueOf(com_google_android_gms_internal_zzarc.cC());
                        continue;
                    case 24:
                        this.atc = Boolean.valueOf(com_google_android_gms_internal_zzarc.cC());
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
            return zzaj(com_google_android_gms_internal_zzarc);
        }

        public zza zzbyx() {
            this.name = null;
            this.atb = null;
            this.atc = null;
            this.bqE = -1;
            return this;
        }

        protected int zzx() {
            int zzx = super.zzx();
            if (this.name != null) {
                zzx += zzard.zzs(1, this.name);
            }
            if (this.atb != null) {
                zzx += zzard.zzk(2, this.atb.booleanValue());
            }
            return this.atc != null ? zzx + zzard.zzk(3, this.atc.booleanValue()) : zzx;
        }
    }

    public static final class zzb extends zzark {
        public String anQ;
        public Long atd;
        public Integer ate;
        public zzc[] atf;
        public zza[] atg;
        public com.google.android.gms.internal.zzvk.zza[] ath;

        public zzb() {
            zzbyy();
        }

        public boolean equals(Object obj) {
            if (obj == this) {
                return true;
            }
            if (!(obj instanceof zzb)) {
                return false;
            }
            zzb com_google_android_gms_internal_zzvl_zzb = (zzb) obj;
            if (this.atd == null) {
                if (com_google_android_gms_internal_zzvl_zzb.atd != null) {
                    return false;
                }
            } else if (!this.atd.equals(com_google_android_gms_internal_zzvl_zzb.atd)) {
                return false;
            }
            if (this.anQ == null) {
                if (com_google_android_gms_internal_zzvl_zzb.anQ != null) {
                    return false;
                }
            } else if (!this.anQ.equals(com_google_android_gms_internal_zzvl_zzb.anQ)) {
                return false;
            }
            if (this.ate == null) {
                if (com_google_android_gms_internal_zzvl_zzb.ate != null) {
                    return false;
                }
            } else if (!this.ate.equals(com_google_android_gms_internal_zzvl_zzb.ate)) {
                return false;
            }
            return !zzari.equals(this.atf, com_google_android_gms_internal_zzvl_zzb.atf) ? false : !zzari.equals(this.atg, com_google_android_gms_internal_zzvl_zzb.atg) ? false : zzari.equals(this.ath, com_google_android_gms_internal_zzvl_zzb.ath);
        }

        public int hashCode() {
            int i = 0;
            int hashCode = ((this.anQ == null ? 0 : this.anQ.hashCode()) + (((this.atd == null ? 0 : this.atd.hashCode()) + ((getClass().getName().hashCode() + 527) * 31)) * 31)) * 31;
            if (this.ate != null) {
                i = this.ate.hashCode();
            }
            return ((((((hashCode + i) * 31) + zzari.hashCode(this.atf)) * 31) + zzari.hashCode(this.atg)) * 31) + zzari.hashCode(this.ath);
        }

        public void zza(zzard com_google_android_gms_internal_zzard) throws IOException {
            int i = 0;
            if (this.atd != null) {
                com_google_android_gms_internal_zzard.zzb(1, this.atd.longValue());
            }
            if (this.anQ != null) {
                com_google_android_gms_internal_zzard.zzr(2, this.anQ);
            }
            if (this.ate != null) {
                com_google_android_gms_internal_zzard.zzae(3, this.ate.intValue());
            }
            if (this.atf != null && this.atf.length > 0) {
                for (zzark com_google_android_gms_internal_zzark : this.atf) {
                    if (com_google_android_gms_internal_zzark != null) {
                        com_google_android_gms_internal_zzard.zza(4, com_google_android_gms_internal_zzark);
                    }
                }
            }
            if (this.atg != null && this.atg.length > 0) {
                for (zzark com_google_android_gms_internal_zzark2 : this.atg) {
                    if (com_google_android_gms_internal_zzark2 != null) {
                        com_google_android_gms_internal_zzard.zza(5, com_google_android_gms_internal_zzark2);
                    }
                }
            }
            if (this.ath != null && this.ath.length > 0) {
                while (i < this.ath.length) {
                    zzark com_google_android_gms_internal_zzark3 = this.ath[i];
                    if (com_google_android_gms_internal_zzark3 != null) {
                        com_google_android_gms_internal_zzard.zza(6, com_google_android_gms_internal_zzark3);
                    }
                    i++;
                }
            }
            super.zza(com_google_android_gms_internal_zzard);
        }

        public zzb zzak(zzarc com_google_android_gms_internal_zzarc) throws IOException {
            while (true) {
                int cw = com_google_android_gms_internal_zzarc.cw();
                int zzc;
                Object obj;
                switch (cw) {
                    case 0:
                        break;
                    case 8:
                        this.atd = Long.valueOf(com_google_android_gms_internal_zzarc.cz());
                        continue;
                    case 18:
                        this.anQ = com_google_android_gms_internal_zzarc.readString();
                        continue;
                    case 24:
                        this.ate = Integer.valueOf(com_google_android_gms_internal_zzarc.cA());
                        continue;
                    case 34:
                        zzc = zzarn.zzc(com_google_android_gms_internal_zzarc, 34);
                        cw = this.atf == null ? 0 : this.atf.length;
                        obj = new zzc[(zzc + cw)];
                        if (cw != 0) {
                            System.arraycopy(this.atf, 0, obj, 0, cw);
                        }
                        while (cw < obj.length - 1) {
                            obj[cw] = new zzc();
                            com_google_android_gms_internal_zzarc.zza(obj[cw]);
                            com_google_android_gms_internal_zzarc.cw();
                            cw++;
                        }
                        obj[cw] = new zzc();
                        com_google_android_gms_internal_zzarc.zza(obj[cw]);
                        this.atf = obj;
                        continue;
                    case 42:
                        zzc = zzarn.zzc(com_google_android_gms_internal_zzarc, 42);
                        cw = this.atg == null ? 0 : this.atg.length;
                        obj = new zza[(zzc + cw)];
                        if (cw != 0) {
                            System.arraycopy(this.atg, 0, obj, 0, cw);
                        }
                        while (cw < obj.length - 1) {
                            obj[cw] = new zza();
                            com_google_android_gms_internal_zzarc.zza(obj[cw]);
                            com_google_android_gms_internal_zzarc.cw();
                            cw++;
                        }
                        obj[cw] = new zza();
                        com_google_android_gms_internal_zzarc.zza(obj[cw]);
                        this.atg = obj;
                        continue;
                    case 50:
                        zzc = zzarn.zzc(com_google_android_gms_internal_zzarc, 50);
                        cw = this.ath == null ? 0 : this.ath.length;
                        obj = new com.google.android.gms.internal.zzvk.zza[(zzc + cw)];
                        if (cw != 0) {
                            System.arraycopy(this.ath, 0, obj, 0, cw);
                        }
                        while (cw < obj.length - 1) {
                            obj[cw] = new com.google.android.gms.internal.zzvk.zza();
                            com_google_android_gms_internal_zzarc.zza(obj[cw]);
                            com_google_android_gms_internal_zzarc.cw();
                            cw++;
                        }
                        obj[cw] = new com.google.android.gms.internal.zzvk.zza();
                        com_google_android_gms_internal_zzarc.zza(obj[cw]);
                        this.ath = obj;
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
            return zzak(com_google_android_gms_internal_zzarc);
        }

        public zzb zzbyy() {
            this.atd = null;
            this.anQ = null;
            this.ate = null;
            this.atf = zzc.zzbyz();
            this.atg = zza.zzbyw();
            this.ath = com.google.android.gms.internal.zzvk.zza.zzbym();
            this.bqE = -1;
            return this;
        }

        protected int zzx() {
            int i;
            int i2 = 0;
            int zzx = super.zzx();
            if (this.atd != null) {
                zzx += zzard.zzf(1, this.atd.longValue());
            }
            if (this.anQ != null) {
                zzx += zzard.zzs(2, this.anQ);
            }
            if (this.ate != null) {
                zzx += zzard.zzag(3, this.ate.intValue());
            }
            if (this.atf != null && this.atf.length > 0) {
                i = zzx;
                for (zzark com_google_android_gms_internal_zzark : this.atf) {
                    if (com_google_android_gms_internal_zzark != null) {
                        i += zzard.zzc(4, com_google_android_gms_internal_zzark);
                    }
                }
                zzx = i;
            }
            if (this.atg != null && this.atg.length > 0) {
                i = zzx;
                for (zzark com_google_android_gms_internal_zzark2 : this.atg) {
                    if (com_google_android_gms_internal_zzark2 != null) {
                        i += zzard.zzc(5, com_google_android_gms_internal_zzark2);
                    }
                }
                zzx = i;
            }
            if (this.ath != null && this.ath.length > 0) {
                while (i2 < this.ath.length) {
                    zzark com_google_android_gms_internal_zzark3 = this.ath[i2];
                    if (com_google_android_gms_internal_zzark3 != null) {
                        zzx += zzard.zzc(6, com_google_android_gms_internal_zzark3);
                    }
                    i2++;
                }
            }
            return zzx;
        }
    }

    public static final class zzc extends zzark {
        private static volatile zzc[] ati;
        public String value;
        public String zzcb;

        public zzc() {
            zzbza();
        }

        public static zzc[] zzbyz() {
            if (ati == null) {
                synchronized (zzari.bqD) {
                    if (ati == null) {
                        ati = new zzc[0];
                    }
                }
            }
            return ati;
        }

        public boolean equals(Object obj) {
            if (obj == this) {
                return true;
            }
            if (!(obj instanceof zzc)) {
                return false;
            }
            zzc com_google_android_gms_internal_zzvl_zzc = (zzc) obj;
            if (this.zzcb == null) {
                if (com_google_android_gms_internal_zzvl_zzc.zzcb != null) {
                    return false;
                }
            } else if (!this.zzcb.equals(com_google_android_gms_internal_zzvl_zzc.zzcb)) {
                return false;
            }
            return this.value == null ? com_google_android_gms_internal_zzvl_zzc.value == null : this.value.equals(com_google_android_gms_internal_zzvl_zzc.value);
        }

        public int hashCode() {
            int i = 0;
            int hashCode = ((this.zzcb == null ? 0 : this.zzcb.hashCode()) + ((getClass().getName().hashCode() + 527) * 31)) * 31;
            if (this.value != null) {
                i = this.value.hashCode();
            }
            return hashCode + i;
        }

        public void zza(zzard com_google_android_gms_internal_zzard) throws IOException {
            if (this.zzcb != null) {
                com_google_android_gms_internal_zzard.zzr(1, this.zzcb);
            }
            if (this.value != null) {
                com_google_android_gms_internal_zzard.zzr(2, this.value);
            }
            super.zza(com_google_android_gms_internal_zzard);
        }

        public zzc zzal(zzarc com_google_android_gms_internal_zzarc) throws IOException {
            while (true) {
                int cw = com_google_android_gms_internal_zzarc.cw();
                switch (cw) {
                    case 0:
                        break;
                    case 10:
                        this.zzcb = com_google_android_gms_internal_zzarc.readString();
                        continue;
                    case 18:
                        this.value = com_google_android_gms_internal_zzarc.readString();
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
            return zzal(com_google_android_gms_internal_zzarc);
        }

        public zzc zzbza() {
            this.zzcb = null;
            this.value = null;
            this.bqE = -1;
            return this;
        }

        protected int zzx() {
            int zzx = super.zzx();
            if (this.zzcb != null) {
                zzx += zzard.zzs(1, this.zzcb);
            }
            return this.value != null ? zzx + zzard.zzs(2, this.value) : zzx;
        }
    }
}
