package com.google.android.gms.internal;

import java.io.IOException;

public interface zzauv {

    public static final class zza extends zzbyd<zza> {
        private static volatile zza[] zzbwN;
        public String name;
        public Boolean zzbwO;
        public Boolean zzbwP;

        public zza() {
            zzNw();
        }

        public static zza[] zzNv() {
            if (zzbwN == null) {
                synchronized (zzbyh.zzcwK) {
                    if (zzbwN == null) {
                        zzbwN = new zza[0];
                    }
                }
            }
            return zzbwN;
        }

        public boolean equals(Object obj) {
            if (obj == this) {
                return true;
            }
            if (!(obj instanceof zza)) {
                return false;
            }
            zza com_google_android_gms_internal_zzauv_zza = (zza) obj;
            if (this.name == null) {
                if (com_google_android_gms_internal_zzauv_zza.name != null) {
                    return false;
                }
            } else if (!this.name.equals(com_google_android_gms_internal_zzauv_zza.name)) {
                return false;
            }
            if (this.zzbwO == null) {
                if (com_google_android_gms_internal_zzauv_zza.zzbwO != null) {
                    return false;
                }
            } else if (!this.zzbwO.equals(com_google_android_gms_internal_zzauv_zza.zzbwO)) {
                return false;
            }
            if (this.zzbwP == null) {
                if (com_google_android_gms_internal_zzauv_zza.zzbwP != null) {
                    return false;
                }
            } else if (!this.zzbwP.equals(com_google_android_gms_internal_zzauv_zza.zzbwP)) {
                return false;
            }
            return (this.zzcwC == null || this.zzcwC.isEmpty()) ? com_google_android_gms_internal_zzauv_zza.zzcwC == null || com_google_android_gms_internal_zzauv_zza.zzcwC.isEmpty() : this.zzcwC.equals(com_google_android_gms_internal_zzauv_zza.zzcwC);
        }

        public int hashCode() {
            int i = 0;
            int hashCode = ((this.zzbwP == null ? 0 : this.zzbwP.hashCode()) + (((this.zzbwO == null ? 0 : this.zzbwO.hashCode()) + (((this.name == null ? 0 : this.name.hashCode()) + ((getClass().getName().hashCode() + 527) * 31)) * 31)) * 31)) * 31;
            if (!(this.zzcwC == null || this.zzcwC.isEmpty())) {
                i = this.zzcwC.hashCode();
            }
            return hashCode + i;
        }

        public zza zzM(zzbyb com_google_android_gms_internal_zzbyb) throws IOException {
            while (true) {
                int zzaeW = com_google_android_gms_internal_zzbyb.zzaeW();
                switch (zzaeW) {
                    case 0:
                        break;
                    case 10:
                        this.name = com_google_android_gms_internal_zzbyb.readString();
                        continue;
                    case 16:
                        this.zzbwO = Boolean.valueOf(com_google_android_gms_internal_zzbyb.zzafc());
                        continue;
                    case 24:
                        this.zzbwP = Boolean.valueOf(com_google_android_gms_internal_zzbyb.zzafc());
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

        public zza zzNw() {
            this.name = null;
            this.zzbwO = null;
            this.zzbwP = null;
            this.zzcwC = null;
            this.zzcwL = -1;
            return this;
        }

        public void zza(zzbyc com_google_android_gms_internal_zzbyc) throws IOException {
            if (this.name != null) {
                com_google_android_gms_internal_zzbyc.zzq(1, this.name);
            }
            if (this.zzbwO != null) {
                com_google_android_gms_internal_zzbyc.zzg(2, this.zzbwO.booleanValue());
            }
            if (this.zzbwP != null) {
                com_google_android_gms_internal_zzbyc.zzg(3, this.zzbwP.booleanValue());
            }
            super.zza(com_google_android_gms_internal_zzbyc);
        }

        public /* synthetic */ zzbyj zzb(zzbyb com_google_android_gms_internal_zzbyb) throws IOException {
            return zzM(com_google_android_gms_internal_zzbyb);
        }

        protected int zzu() {
            int zzu = super.zzu();
            if (this.name != null) {
                zzu += zzbyc.zzr(1, this.name);
            }
            if (this.zzbwO != null) {
                zzu += zzbyc.zzh(2, this.zzbwO.booleanValue());
            }
            return this.zzbwP != null ? zzu + zzbyc.zzh(3, this.zzbwP.booleanValue()) : zzu;
        }
    }

    public static final class zzb extends zzbyd<zzb> {
        public String zzbqK;
        public Long zzbwQ;
        public Integer zzbwR;
        public zzc[] zzbwS;
        public zza[] zzbwT;
        public com.google.android.gms.internal.zzauu.zza[] zzbwU;

        public zzb() {
            zzNx();
        }

        public boolean equals(Object obj) {
            if (obj == this) {
                return true;
            }
            if (!(obj instanceof zzb)) {
                return false;
            }
            zzb com_google_android_gms_internal_zzauv_zzb = (zzb) obj;
            if (this.zzbwQ == null) {
                if (com_google_android_gms_internal_zzauv_zzb.zzbwQ != null) {
                    return false;
                }
            } else if (!this.zzbwQ.equals(com_google_android_gms_internal_zzauv_zzb.zzbwQ)) {
                return false;
            }
            if (this.zzbqK == null) {
                if (com_google_android_gms_internal_zzauv_zzb.zzbqK != null) {
                    return false;
                }
            } else if (!this.zzbqK.equals(com_google_android_gms_internal_zzauv_zzb.zzbqK)) {
                return false;
            }
            if (this.zzbwR == null) {
                if (com_google_android_gms_internal_zzauv_zzb.zzbwR != null) {
                    return false;
                }
            } else if (!this.zzbwR.equals(com_google_android_gms_internal_zzauv_zzb.zzbwR)) {
                return false;
            }
            return (zzbyh.equals(this.zzbwS, com_google_android_gms_internal_zzauv_zzb.zzbwS) && zzbyh.equals(this.zzbwT, com_google_android_gms_internal_zzauv_zzb.zzbwT) && zzbyh.equals(this.zzbwU, com_google_android_gms_internal_zzauv_zzb.zzbwU)) ? (this.zzcwC == null || this.zzcwC.isEmpty()) ? com_google_android_gms_internal_zzauv_zzb.zzcwC == null || com_google_android_gms_internal_zzauv_zzb.zzcwC.isEmpty() : this.zzcwC.equals(com_google_android_gms_internal_zzauv_zzb.zzcwC) : false;
        }

        public int hashCode() {
            int i = 0;
            int hashCode = ((((((((this.zzbwR == null ? 0 : this.zzbwR.hashCode()) + (((this.zzbqK == null ? 0 : this.zzbqK.hashCode()) + (((this.zzbwQ == null ? 0 : this.zzbwQ.hashCode()) + ((getClass().getName().hashCode() + 527) * 31)) * 31)) * 31)) * 31) + zzbyh.hashCode(this.zzbwS)) * 31) + zzbyh.hashCode(this.zzbwT)) * 31) + zzbyh.hashCode(this.zzbwU)) * 31;
            if (!(this.zzcwC == null || this.zzcwC.isEmpty())) {
                i = this.zzcwC.hashCode();
            }
            return hashCode + i;
        }

        public zzb zzN(zzbyb com_google_android_gms_internal_zzbyb) throws IOException {
            while (true) {
                int zzaeW = com_google_android_gms_internal_zzbyb.zzaeW();
                int zzb;
                Object obj;
                switch (zzaeW) {
                    case 0:
                        break;
                    case 8:
                        this.zzbwQ = Long.valueOf(com_google_android_gms_internal_zzbyb.zzaeZ());
                        continue;
                    case 18:
                        this.zzbqK = com_google_android_gms_internal_zzbyb.readString();
                        continue;
                    case 24:
                        this.zzbwR = Integer.valueOf(com_google_android_gms_internal_zzbyb.zzafa());
                        continue;
                    case 34:
                        zzb = zzbym.zzb(com_google_android_gms_internal_zzbyb, 34);
                        zzaeW = this.zzbwS == null ? 0 : this.zzbwS.length;
                        obj = new zzc[(zzb + zzaeW)];
                        if (zzaeW != 0) {
                            System.arraycopy(this.zzbwS, 0, obj, 0, zzaeW);
                        }
                        while (zzaeW < obj.length - 1) {
                            obj[zzaeW] = new zzc();
                            com_google_android_gms_internal_zzbyb.zza(obj[zzaeW]);
                            com_google_android_gms_internal_zzbyb.zzaeW();
                            zzaeW++;
                        }
                        obj[zzaeW] = new zzc();
                        com_google_android_gms_internal_zzbyb.zza(obj[zzaeW]);
                        this.zzbwS = obj;
                        continue;
                    case 42:
                        zzb = zzbym.zzb(com_google_android_gms_internal_zzbyb, 42);
                        zzaeW = this.zzbwT == null ? 0 : this.zzbwT.length;
                        obj = new zza[(zzb + zzaeW)];
                        if (zzaeW != 0) {
                            System.arraycopy(this.zzbwT, 0, obj, 0, zzaeW);
                        }
                        while (zzaeW < obj.length - 1) {
                            obj[zzaeW] = new zza();
                            com_google_android_gms_internal_zzbyb.zza(obj[zzaeW]);
                            com_google_android_gms_internal_zzbyb.zzaeW();
                            zzaeW++;
                        }
                        obj[zzaeW] = new zza();
                        com_google_android_gms_internal_zzbyb.zza(obj[zzaeW]);
                        this.zzbwT = obj;
                        continue;
                    case 50:
                        zzb = zzbym.zzb(com_google_android_gms_internal_zzbyb, 50);
                        zzaeW = this.zzbwU == null ? 0 : this.zzbwU.length;
                        obj = new com.google.android.gms.internal.zzauu.zza[(zzb + zzaeW)];
                        if (zzaeW != 0) {
                            System.arraycopy(this.zzbwU, 0, obj, 0, zzaeW);
                        }
                        while (zzaeW < obj.length - 1) {
                            obj[zzaeW] = new com.google.android.gms.internal.zzauu.zza();
                            com_google_android_gms_internal_zzbyb.zza(obj[zzaeW]);
                            com_google_android_gms_internal_zzbyb.zzaeW();
                            zzaeW++;
                        }
                        obj[zzaeW] = new com.google.android.gms.internal.zzauu.zza();
                        com_google_android_gms_internal_zzbyb.zza(obj[zzaeW]);
                        this.zzbwU = obj;
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

        public zzb zzNx() {
            this.zzbwQ = null;
            this.zzbqK = null;
            this.zzbwR = null;
            this.zzbwS = zzc.zzNy();
            this.zzbwT = zza.zzNv();
            this.zzbwU = com.google.android.gms.internal.zzauu.zza.zzNl();
            this.zzcwC = null;
            this.zzcwL = -1;
            return this;
        }

        public void zza(zzbyc com_google_android_gms_internal_zzbyc) throws IOException {
            int i = 0;
            if (this.zzbwQ != null) {
                com_google_android_gms_internal_zzbyc.zzb(1, this.zzbwQ.longValue());
            }
            if (this.zzbqK != null) {
                com_google_android_gms_internal_zzbyc.zzq(2, this.zzbqK);
            }
            if (this.zzbwR != null) {
                com_google_android_gms_internal_zzbyc.zzJ(3, this.zzbwR.intValue());
            }
            if (this.zzbwS != null && this.zzbwS.length > 0) {
                for (zzbyj com_google_android_gms_internal_zzbyj : this.zzbwS) {
                    if (com_google_android_gms_internal_zzbyj != null) {
                        com_google_android_gms_internal_zzbyc.zza(4, com_google_android_gms_internal_zzbyj);
                    }
                }
            }
            if (this.zzbwT != null && this.zzbwT.length > 0) {
                for (zzbyj com_google_android_gms_internal_zzbyj2 : this.zzbwT) {
                    if (com_google_android_gms_internal_zzbyj2 != null) {
                        com_google_android_gms_internal_zzbyc.zza(5, com_google_android_gms_internal_zzbyj2);
                    }
                }
            }
            if (this.zzbwU != null && this.zzbwU.length > 0) {
                while (i < this.zzbwU.length) {
                    zzbyj com_google_android_gms_internal_zzbyj3 = this.zzbwU[i];
                    if (com_google_android_gms_internal_zzbyj3 != null) {
                        com_google_android_gms_internal_zzbyc.zza(6, com_google_android_gms_internal_zzbyj3);
                    }
                    i++;
                }
            }
            super.zza(com_google_android_gms_internal_zzbyc);
        }

        public /* synthetic */ zzbyj zzb(zzbyb com_google_android_gms_internal_zzbyb) throws IOException {
            return zzN(com_google_android_gms_internal_zzbyb);
        }

        protected int zzu() {
            int i;
            int i2 = 0;
            int zzu = super.zzu();
            if (this.zzbwQ != null) {
                zzu += zzbyc.zzf(1, this.zzbwQ.longValue());
            }
            if (this.zzbqK != null) {
                zzu += zzbyc.zzr(2, this.zzbqK);
            }
            if (this.zzbwR != null) {
                zzu += zzbyc.zzL(3, this.zzbwR.intValue());
            }
            if (this.zzbwS != null && this.zzbwS.length > 0) {
                i = zzu;
                for (zzbyj com_google_android_gms_internal_zzbyj : this.zzbwS) {
                    if (com_google_android_gms_internal_zzbyj != null) {
                        i += zzbyc.zzc(4, com_google_android_gms_internal_zzbyj);
                    }
                }
                zzu = i;
            }
            if (this.zzbwT != null && this.zzbwT.length > 0) {
                i = zzu;
                for (zzbyj com_google_android_gms_internal_zzbyj2 : this.zzbwT) {
                    if (com_google_android_gms_internal_zzbyj2 != null) {
                        i += zzbyc.zzc(5, com_google_android_gms_internal_zzbyj2);
                    }
                }
                zzu = i;
            }
            if (this.zzbwU != null && this.zzbwU.length > 0) {
                while (i2 < this.zzbwU.length) {
                    zzbyj com_google_android_gms_internal_zzbyj3 = this.zzbwU[i2];
                    if (com_google_android_gms_internal_zzbyj3 != null) {
                        zzu += zzbyc.zzc(6, com_google_android_gms_internal_zzbyj3);
                    }
                    i2++;
                }
            }
            return zzu;
        }
    }

    public static final class zzc extends zzbyd<zzc> {
        private static volatile zzc[] zzbwV;
        public String value;
        public String zzaB;

        public zzc() {
            zzNz();
        }

        public static zzc[] zzNy() {
            if (zzbwV == null) {
                synchronized (zzbyh.zzcwK) {
                    if (zzbwV == null) {
                        zzbwV = new zzc[0];
                    }
                }
            }
            return zzbwV;
        }

        public boolean equals(Object obj) {
            if (obj == this) {
                return true;
            }
            if (!(obj instanceof zzc)) {
                return false;
            }
            zzc com_google_android_gms_internal_zzauv_zzc = (zzc) obj;
            if (this.zzaB == null) {
                if (com_google_android_gms_internal_zzauv_zzc.zzaB != null) {
                    return false;
                }
            } else if (!this.zzaB.equals(com_google_android_gms_internal_zzauv_zzc.zzaB)) {
                return false;
            }
            if (this.value == null) {
                if (com_google_android_gms_internal_zzauv_zzc.value != null) {
                    return false;
                }
            } else if (!this.value.equals(com_google_android_gms_internal_zzauv_zzc.value)) {
                return false;
            }
            return (this.zzcwC == null || this.zzcwC.isEmpty()) ? com_google_android_gms_internal_zzauv_zzc.zzcwC == null || com_google_android_gms_internal_zzauv_zzc.zzcwC.isEmpty() : this.zzcwC.equals(com_google_android_gms_internal_zzauv_zzc.zzcwC);
        }

        public int hashCode() {
            int i = 0;
            int hashCode = ((this.value == null ? 0 : this.value.hashCode()) + (((this.zzaB == null ? 0 : this.zzaB.hashCode()) + ((getClass().getName().hashCode() + 527) * 31)) * 31)) * 31;
            if (!(this.zzcwC == null || this.zzcwC.isEmpty())) {
                i = this.zzcwC.hashCode();
            }
            return hashCode + i;
        }

        public zzc zzNz() {
            this.zzaB = null;
            this.value = null;
            this.zzcwC = null;
            this.zzcwL = -1;
            return this;
        }

        public zzc zzO(zzbyb com_google_android_gms_internal_zzbyb) throws IOException {
            while (true) {
                int zzaeW = com_google_android_gms_internal_zzbyb.zzaeW();
                switch (zzaeW) {
                    case 0:
                        break;
                    case 10:
                        this.zzaB = com_google_android_gms_internal_zzbyb.readString();
                        continue;
                    case 18:
                        this.value = com_google_android_gms_internal_zzbyb.readString();
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

        public void zza(zzbyc com_google_android_gms_internal_zzbyc) throws IOException {
            if (this.zzaB != null) {
                com_google_android_gms_internal_zzbyc.zzq(1, this.zzaB);
            }
            if (this.value != null) {
                com_google_android_gms_internal_zzbyc.zzq(2, this.value);
            }
            super.zza(com_google_android_gms_internal_zzbyc);
        }

        public /* synthetic */ zzbyj zzb(zzbyb com_google_android_gms_internal_zzbyb) throws IOException {
            return zzO(com_google_android_gms_internal_zzbyb);
        }

        protected int zzu() {
            int zzu = super.zzu();
            if (this.zzaB != null) {
                zzu += zzbyc.zzr(1, this.zzaB);
            }
            return this.value != null ? zzu + zzbyc.zzr(2, this.value) : zzu;
        }
    }
}
