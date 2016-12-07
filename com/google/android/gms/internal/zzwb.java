package com.google.android.gms.internal;

import java.io.IOException;

public interface zzwb {

    public static final class zza extends zzasa {
        private static volatile zza[] awz;
        public Boolean awA;
        public Boolean awB;
        public String name;

        public zza() {
            zzbzs();
        }

        public static zza[] zzbzr() {
            if (awz == null) {
                synchronized (zzary.btO) {
                    if (awz == null) {
                        awz = new zza[0];
                    }
                }
            }
            return awz;
        }

        public boolean equals(Object obj) {
            if (obj == this) {
                return true;
            }
            if (!(obj instanceof zza)) {
                return false;
            }
            zza com_google_android_gms_internal_zzwb_zza = (zza) obj;
            if (this.name == null) {
                if (com_google_android_gms_internal_zzwb_zza.name != null) {
                    return false;
                }
            } else if (!this.name.equals(com_google_android_gms_internal_zzwb_zza.name)) {
                return false;
            }
            if (this.awA == null) {
                if (com_google_android_gms_internal_zzwb_zza.awA != null) {
                    return false;
                }
            } else if (!this.awA.equals(com_google_android_gms_internal_zzwb_zza.awA)) {
                return false;
            }
            return this.awB == null ? com_google_android_gms_internal_zzwb_zza.awB == null : this.awB.equals(com_google_android_gms_internal_zzwb_zza.awB);
        }

        public int hashCode() {
            int i = 0;
            int hashCode = ((this.awA == null ? 0 : this.awA.hashCode()) + (((this.name == null ? 0 : this.name.hashCode()) + ((getClass().getName().hashCode() + 527) * 31)) * 31)) * 31;
            if (this.awB != null) {
                i = this.awB.hashCode();
            }
            return hashCode + i;
        }

        public void zza(zzart com_google_android_gms_internal_zzart) throws IOException {
            if (this.name != null) {
                com_google_android_gms_internal_zzart.zzq(1, this.name);
            }
            if (this.awA != null) {
                com_google_android_gms_internal_zzart.zzg(2, this.awA.booleanValue());
            }
            if (this.awB != null) {
                com_google_android_gms_internal_zzart.zzg(3, this.awB.booleanValue());
            }
            super.zza(com_google_android_gms_internal_zzart);
        }

        public zza zzam(zzars com_google_android_gms_internal_zzars) throws IOException {
            while (true) {
                int bU = com_google_android_gms_internal_zzars.bU();
                switch (bU) {
                    case 0:
                        break;
                    case 10:
                        this.name = com_google_android_gms_internal_zzars.readString();
                        continue;
                    case 16:
                        this.awA = Boolean.valueOf(com_google_android_gms_internal_zzars.ca());
                        continue;
                    case 24:
                        this.awB = Boolean.valueOf(com_google_android_gms_internal_zzars.ca());
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
            return zzam(com_google_android_gms_internal_zzars);
        }

        public zza zzbzs() {
            this.name = null;
            this.awA = null;
            this.awB = null;
            this.btP = -1;
            return this;
        }

        protected int zzx() {
            int zzx = super.zzx();
            if (this.name != null) {
                zzx += zzart.zzr(1, this.name);
            }
            if (this.awA != null) {
                zzx += zzart.zzh(2, this.awA.booleanValue());
            }
            return this.awB != null ? zzx + zzart.zzh(3, this.awB.booleanValue()) : zzx;
        }
    }

    public static final class zzb extends zzasa {
        public String aqZ;
        public Long awC;
        public Integer awD;
        public zzc[] awE;
        public zza[] awF;
        public com.google.android.gms.internal.zzwa.zza[] awG;

        public zzb() {
            zzbzt();
        }

        public boolean equals(Object obj) {
            if (obj == this) {
                return true;
            }
            if (!(obj instanceof zzb)) {
                return false;
            }
            zzb com_google_android_gms_internal_zzwb_zzb = (zzb) obj;
            if (this.awC == null) {
                if (com_google_android_gms_internal_zzwb_zzb.awC != null) {
                    return false;
                }
            } else if (!this.awC.equals(com_google_android_gms_internal_zzwb_zzb.awC)) {
                return false;
            }
            if (this.aqZ == null) {
                if (com_google_android_gms_internal_zzwb_zzb.aqZ != null) {
                    return false;
                }
            } else if (!this.aqZ.equals(com_google_android_gms_internal_zzwb_zzb.aqZ)) {
                return false;
            }
            if (this.awD == null) {
                if (com_google_android_gms_internal_zzwb_zzb.awD != null) {
                    return false;
                }
            } else if (!this.awD.equals(com_google_android_gms_internal_zzwb_zzb.awD)) {
                return false;
            }
            return !zzary.equals(this.awE, com_google_android_gms_internal_zzwb_zzb.awE) ? false : !zzary.equals(this.awF, com_google_android_gms_internal_zzwb_zzb.awF) ? false : zzary.equals(this.awG, com_google_android_gms_internal_zzwb_zzb.awG);
        }

        public int hashCode() {
            int i = 0;
            int hashCode = ((this.aqZ == null ? 0 : this.aqZ.hashCode()) + (((this.awC == null ? 0 : this.awC.hashCode()) + ((getClass().getName().hashCode() + 527) * 31)) * 31)) * 31;
            if (this.awD != null) {
                i = this.awD.hashCode();
            }
            return ((((((hashCode + i) * 31) + zzary.hashCode(this.awE)) * 31) + zzary.hashCode(this.awF)) * 31) + zzary.hashCode(this.awG);
        }

        public void zza(zzart com_google_android_gms_internal_zzart) throws IOException {
            int i = 0;
            if (this.awC != null) {
                com_google_android_gms_internal_zzart.zzb(1, this.awC.longValue());
            }
            if (this.aqZ != null) {
                com_google_android_gms_internal_zzart.zzq(2, this.aqZ);
            }
            if (this.awD != null) {
                com_google_android_gms_internal_zzart.zzaf(3, this.awD.intValue());
            }
            if (this.awE != null && this.awE.length > 0) {
                for (zzasa com_google_android_gms_internal_zzasa : this.awE) {
                    if (com_google_android_gms_internal_zzasa != null) {
                        com_google_android_gms_internal_zzart.zza(4, com_google_android_gms_internal_zzasa);
                    }
                }
            }
            if (this.awF != null && this.awF.length > 0) {
                for (zzasa com_google_android_gms_internal_zzasa2 : this.awF) {
                    if (com_google_android_gms_internal_zzasa2 != null) {
                        com_google_android_gms_internal_zzart.zza(5, com_google_android_gms_internal_zzasa2);
                    }
                }
            }
            if (this.awG != null && this.awG.length > 0) {
                while (i < this.awG.length) {
                    zzasa com_google_android_gms_internal_zzasa3 = this.awG[i];
                    if (com_google_android_gms_internal_zzasa3 != null) {
                        com_google_android_gms_internal_zzart.zza(6, com_google_android_gms_internal_zzasa3);
                    }
                    i++;
                }
            }
            super.zza(com_google_android_gms_internal_zzart);
        }

        public zzb zzan(zzars com_google_android_gms_internal_zzars) throws IOException {
            while (true) {
                int bU = com_google_android_gms_internal_zzars.bU();
                int zzc;
                Object obj;
                switch (bU) {
                    case 0:
                        break;
                    case 8:
                        this.awC = Long.valueOf(com_google_android_gms_internal_zzars.bX());
                        continue;
                    case 18:
                        this.aqZ = com_google_android_gms_internal_zzars.readString();
                        continue;
                    case 24:
                        this.awD = Integer.valueOf(com_google_android_gms_internal_zzars.bY());
                        continue;
                    case 34:
                        zzc = zzasd.zzc(com_google_android_gms_internal_zzars, 34);
                        bU = this.awE == null ? 0 : this.awE.length;
                        obj = new zzc[(zzc + bU)];
                        if (bU != 0) {
                            System.arraycopy(this.awE, 0, obj, 0, bU);
                        }
                        while (bU < obj.length - 1) {
                            obj[bU] = new zzc();
                            com_google_android_gms_internal_zzars.zza(obj[bU]);
                            com_google_android_gms_internal_zzars.bU();
                            bU++;
                        }
                        obj[bU] = new zzc();
                        com_google_android_gms_internal_zzars.zza(obj[bU]);
                        this.awE = obj;
                        continue;
                    case 42:
                        zzc = zzasd.zzc(com_google_android_gms_internal_zzars, 42);
                        bU = this.awF == null ? 0 : this.awF.length;
                        obj = new zza[(zzc + bU)];
                        if (bU != 0) {
                            System.arraycopy(this.awF, 0, obj, 0, bU);
                        }
                        while (bU < obj.length - 1) {
                            obj[bU] = new zza();
                            com_google_android_gms_internal_zzars.zza(obj[bU]);
                            com_google_android_gms_internal_zzars.bU();
                            bU++;
                        }
                        obj[bU] = new zza();
                        com_google_android_gms_internal_zzars.zza(obj[bU]);
                        this.awF = obj;
                        continue;
                    case 50:
                        zzc = zzasd.zzc(com_google_android_gms_internal_zzars, 50);
                        bU = this.awG == null ? 0 : this.awG.length;
                        obj = new com.google.android.gms.internal.zzwa.zza[(zzc + bU)];
                        if (bU != 0) {
                            System.arraycopy(this.awG, 0, obj, 0, bU);
                        }
                        while (bU < obj.length - 1) {
                            obj[bU] = new com.google.android.gms.internal.zzwa.zza();
                            com_google_android_gms_internal_zzars.zza(obj[bU]);
                            com_google_android_gms_internal_zzars.bU();
                            bU++;
                        }
                        obj[bU] = new com.google.android.gms.internal.zzwa.zza();
                        com_google_android_gms_internal_zzars.zza(obj[bU]);
                        this.awG = obj;
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
            return zzan(com_google_android_gms_internal_zzars);
        }

        public zzb zzbzt() {
            this.awC = null;
            this.aqZ = null;
            this.awD = null;
            this.awE = zzc.zzbzu();
            this.awF = zza.zzbzr();
            this.awG = com.google.android.gms.internal.zzwa.zza.zzbzh();
            this.btP = -1;
            return this;
        }

        protected int zzx() {
            int i;
            int i2 = 0;
            int zzx = super.zzx();
            if (this.awC != null) {
                zzx += zzart.zzf(1, this.awC.longValue());
            }
            if (this.aqZ != null) {
                zzx += zzart.zzr(2, this.aqZ);
            }
            if (this.awD != null) {
                zzx += zzart.zzah(3, this.awD.intValue());
            }
            if (this.awE != null && this.awE.length > 0) {
                i = zzx;
                for (zzasa com_google_android_gms_internal_zzasa : this.awE) {
                    if (com_google_android_gms_internal_zzasa != null) {
                        i += zzart.zzc(4, com_google_android_gms_internal_zzasa);
                    }
                }
                zzx = i;
            }
            if (this.awF != null && this.awF.length > 0) {
                i = zzx;
                for (zzasa com_google_android_gms_internal_zzasa2 : this.awF) {
                    if (com_google_android_gms_internal_zzasa2 != null) {
                        i += zzart.zzc(5, com_google_android_gms_internal_zzasa2);
                    }
                }
                zzx = i;
            }
            if (this.awG != null && this.awG.length > 0) {
                while (i2 < this.awG.length) {
                    zzasa com_google_android_gms_internal_zzasa3 = this.awG[i2];
                    if (com_google_android_gms_internal_zzasa3 != null) {
                        zzx += zzart.zzc(6, com_google_android_gms_internal_zzasa3);
                    }
                    i2++;
                }
            }
            return zzx;
        }
    }

    public static final class zzc extends zzasa {
        private static volatile zzc[] awH;
        public String value;
        public String zzcb;

        public zzc() {
            zzbzv();
        }

        public static zzc[] zzbzu() {
            if (awH == null) {
                synchronized (zzary.btO) {
                    if (awH == null) {
                        awH = new zzc[0];
                    }
                }
            }
            return awH;
        }

        public boolean equals(Object obj) {
            if (obj == this) {
                return true;
            }
            if (!(obj instanceof zzc)) {
                return false;
            }
            zzc com_google_android_gms_internal_zzwb_zzc = (zzc) obj;
            if (this.zzcb == null) {
                if (com_google_android_gms_internal_zzwb_zzc.zzcb != null) {
                    return false;
                }
            } else if (!this.zzcb.equals(com_google_android_gms_internal_zzwb_zzc.zzcb)) {
                return false;
            }
            return this.value == null ? com_google_android_gms_internal_zzwb_zzc.value == null : this.value.equals(com_google_android_gms_internal_zzwb_zzc.value);
        }

        public int hashCode() {
            int i = 0;
            int hashCode = ((this.zzcb == null ? 0 : this.zzcb.hashCode()) + ((getClass().getName().hashCode() + 527) * 31)) * 31;
            if (this.value != null) {
                i = this.value.hashCode();
            }
            return hashCode + i;
        }

        public void zza(zzart com_google_android_gms_internal_zzart) throws IOException {
            if (this.zzcb != null) {
                com_google_android_gms_internal_zzart.zzq(1, this.zzcb);
            }
            if (this.value != null) {
                com_google_android_gms_internal_zzart.zzq(2, this.value);
            }
            super.zza(com_google_android_gms_internal_zzart);
        }

        public zzc zzao(zzars com_google_android_gms_internal_zzars) throws IOException {
            while (true) {
                int bU = com_google_android_gms_internal_zzars.bU();
                switch (bU) {
                    case 0:
                        break;
                    case 10:
                        this.zzcb = com_google_android_gms_internal_zzars.readString();
                        continue;
                    case 18:
                        this.value = com_google_android_gms_internal_zzars.readString();
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
            return zzao(com_google_android_gms_internal_zzars);
        }

        public zzc zzbzv() {
            this.zzcb = null;
            this.value = null;
            this.btP = -1;
            return this;
        }

        protected int zzx() {
            int zzx = super.zzx();
            if (this.zzcb != null) {
                zzx += zzart.zzr(1, this.zzcb);
            }
            return this.value != null ? zzx + zzart.zzr(2, this.value) : zzx;
        }
    }
}
