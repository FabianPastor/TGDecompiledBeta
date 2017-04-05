package com.google.android.gms.internal;

import java.io.IOException;

public interface zzauv {

    public static final class zza extends zzbxn<zza> {
        private static volatile zza[] zzbwK;
        public String name;
        public Boolean zzbwL;
        public Boolean zzbwM;

        public zza() {
            zzNu();
        }

        public static zza[] zzNt() {
            if (zzbwK == null) {
                synchronized (zzbxr.zzcuQ) {
                    if (zzbwK == null) {
                        zzbwK = new zza[0];
                    }
                }
            }
            return zzbwK;
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
            if (this.zzbwL == null) {
                if (com_google_android_gms_internal_zzauv_zza.zzbwL != null) {
                    return false;
                }
            } else if (!this.zzbwL.equals(com_google_android_gms_internal_zzauv_zza.zzbwL)) {
                return false;
            }
            if (this.zzbwM == null) {
                if (com_google_android_gms_internal_zzauv_zza.zzbwM != null) {
                    return false;
                }
            } else if (!this.zzbwM.equals(com_google_android_gms_internal_zzauv_zza.zzbwM)) {
                return false;
            }
            return (this.zzcuI == null || this.zzcuI.isEmpty()) ? com_google_android_gms_internal_zzauv_zza.zzcuI == null || com_google_android_gms_internal_zzauv_zza.zzcuI.isEmpty() : this.zzcuI.equals(com_google_android_gms_internal_zzauv_zza.zzcuI);
        }

        public int hashCode() {
            int i = 0;
            int hashCode = ((this.zzbwM == null ? 0 : this.zzbwM.hashCode()) + (((this.zzbwL == null ? 0 : this.zzbwL.hashCode()) + (((this.name == null ? 0 : this.name.hashCode()) + ((getClass().getName().hashCode() + 527) * 31)) * 31)) * 31)) * 31;
            if (!(this.zzcuI == null || this.zzcuI.isEmpty())) {
                i = this.zzcuI.hashCode();
            }
            return hashCode + i;
        }

        public zza zzM(zzbxl com_google_android_gms_internal_zzbxl) throws IOException {
            while (true) {
                int zzaeo = com_google_android_gms_internal_zzbxl.zzaeo();
                switch (zzaeo) {
                    case 0:
                        break;
                    case 10:
                        this.name = com_google_android_gms_internal_zzbxl.readString();
                        continue;
                    case 16:
                        this.zzbwL = Boolean.valueOf(com_google_android_gms_internal_zzbxl.zzaeu());
                        continue;
                    case 24:
                        this.zzbwM = Boolean.valueOf(com_google_android_gms_internal_zzbxl.zzaeu());
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

        public zza zzNu() {
            this.name = null;
            this.zzbwL = null;
            this.zzbwM = null;
            this.zzcuI = null;
            this.zzcuR = -1;
            return this;
        }

        public void zza(zzbxm com_google_android_gms_internal_zzbxm) throws IOException {
            if (this.name != null) {
                com_google_android_gms_internal_zzbxm.zzq(1, this.name);
            }
            if (this.zzbwL != null) {
                com_google_android_gms_internal_zzbxm.zzg(2, this.zzbwL.booleanValue());
            }
            if (this.zzbwM != null) {
                com_google_android_gms_internal_zzbxm.zzg(3, this.zzbwM.booleanValue());
            }
            super.zza(com_google_android_gms_internal_zzbxm);
        }

        public /* synthetic */ zzbxt zzb(zzbxl com_google_android_gms_internal_zzbxl) throws IOException {
            return zzM(com_google_android_gms_internal_zzbxl);
        }

        protected int zzu() {
            int zzu = super.zzu();
            if (this.name != null) {
                zzu += zzbxm.zzr(1, this.name);
            }
            if (this.zzbwL != null) {
                zzu += zzbxm.zzh(2, this.zzbwL.booleanValue());
            }
            return this.zzbwM != null ? zzu + zzbxm.zzh(3, this.zzbwM.booleanValue()) : zzu;
        }
    }

    public static final class zzb extends zzbxn<zzb> {
        public String zzbqL;
        public Long zzbwN;
        public Integer zzbwO;
        public zzc[] zzbwP;
        public zza[] zzbwQ;
        public com.google.android.gms.internal.zzauu.zza[] zzbwR;

        public zzb() {
            zzNv();
        }

        public boolean equals(Object obj) {
            if (obj == this) {
                return true;
            }
            if (!(obj instanceof zzb)) {
                return false;
            }
            zzb com_google_android_gms_internal_zzauv_zzb = (zzb) obj;
            if (this.zzbwN == null) {
                if (com_google_android_gms_internal_zzauv_zzb.zzbwN != null) {
                    return false;
                }
            } else if (!this.zzbwN.equals(com_google_android_gms_internal_zzauv_zzb.zzbwN)) {
                return false;
            }
            if (this.zzbqL == null) {
                if (com_google_android_gms_internal_zzauv_zzb.zzbqL != null) {
                    return false;
                }
            } else if (!this.zzbqL.equals(com_google_android_gms_internal_zzauv_zzb.zzbqL)) {
                return false;
            }
            if (this.zzbwO == null) {
                if (com_google_android_gms_internal_zzauv_zzb.zzbwO != null) {
                    return false;
                }
            } else if (!this.zzbwO.equals(com_google_android_gms_internal_zzauv_zzb.zzbwO)) {
                return false;
            }
            return (zzbxr.equals(this.zzbwP, com_google_android_gms_internal_zzauv_zzb.zzbwP) && zzbxr.equals(this.zzbwQ, com_google_android_gms_internal_zzauv_zzb.zzbwQ) && zzbxr.equals(this.zzbwR, com_google_android_gms_internal_zzauv_zzb.zzbwR)) ? (this.zzcuI == null || this.zzcuI.isEmpty()) ? com_google_android_gms_internal_zzauv_zzb.zzcuI == null || com_google_android_gms_internal_zzauv_zzb.zzcuI.isEmpty() : this.zzcuI.equals(com_google_android_gms_internal_zzauv_zzb.zzcuI) : false;
        }

        public int hashCode() {
            int i = 0;
            int hashCode = ((((((((this.zzbwO == null ? 0 : this.zzbwO.hashCode()) + (((this.zzbqL == null ? 0 : this.zzbqL.hashCode()) + (((this.zzbwN == null ? 0 : this.zzbwN.hashCode()) + ((getClass().getName().hashCode() + 527) * 31)) * 31)) * 31)) * 31) + zzbxr.hashCode(this.zzbwP)) * 31) + zzbxr.hashCode(this.zzbwQ)) * 31) + zzbxr.hashCode(this.zzbwR)) * 31;
            if (!(this.zzcuI == null || this.zzcuI.isEmpty())) {
                i = this.zzcuI.hashCode();
            }
            return hashCode + i;
        }

        public zzb zzN(zzbxl com_google_android_gms_internal_zzbxl) throws IOException {
            while (true) {
                int zzaeo = com_google_android_gms_internal_zzbxl.zzaeo();
                int zzb;
                Object obj;
                switch (zzaeo) {
                    case 0:
                        break;
                    case 8:
                        this.zzbwN = Long.valueOf(com_google_android_gms_internal_zzbxl.zzaer());
                        continue;
                    case 18:
                        this.zzbqL = com_google_android_gms_internal_zzbxl.readString();
                        continue;
                    case 24:
                        this.zzbwO = Integer.valueOf(com_google_android_gms_internal_zzbxl.zzaes());
                        continue;
                    case 34:
                        zzb = zzbxw.zzb(com_google_android_gms_internal_zzbxl, 34);
                        zzaeo = this.zzbwP == null ? 0 : this.zzbwP.length;
                        obj = new zzc[(zzb + zzaeo)];
                        if (zzaeo != 0) {
                            System.arraycopy(this.zzbwP, 0, obj, 0, zzaeo);
                        }
                        while (zzaeo < obj.length - 1) {
                            obj[zzaeo] = new zzc();
                            com_google_android_gms_internal_zzbxl.zza(obj[zzaeo]);
                            com_google_android_gms_internal_zzbxl.zzaeo();
                            zzaeo++;
                        }
                        obj[zzaeo] = new zzc();
                        com_google_android_gms_internal_zzbxl.zza(obj[zzaeo]);
                        this.zzbwP = obj;
                        continue;
                    case 42:
                        zzb = zzbxw.zzb(com_google_android_gms_internal_zzbxl, 42);
                        zzaeo = this.zzbwQ == null ? 0 : this.zzbwQ.length;
                        obj = new zza[(zzb + zzaeo)];
                        if (zzaeo != 0) {
                            System.arraycopy(this.zzbwQ, 0, obj, 0, zzaeo);
                        }
                        while (zzaeo < obj.length - 1) {
                            obj[zzaeo] = new zza();
                            com_google_android_gms_internal_zzbxl.zza(obj[zzaeo]);
                            com_google_android_gms_internal_zzbxl.zzaeo();
                            zzaeo++;
                        }
                        obj[zzaeo] = new zza();
                        com_google_android_gms_internal_zzbxl.zza(obj[zzaeo]);
                        this.zzbwQ = obj;
                        continue;
                    case 50:
                        zzb = zzbxw.zzb(com_google_android_gms_internal_zzbxl, 50);
                        zzaeo = this.zzbwR == null ? 0 : this.zzbwR.length;
                        obj = new com.google.android.gms.internal.zzauu.zza[(zzb + zzaeo)];
                        if (zzaeo != 0) {
                            System.arraycopy(this.zzbwR, 0, obj, 0, zzaeo);
                        }
                        while (zzaeo < obj.length - 1) {
                            obj[zzaeo] = new com.google.android.gms.internal.zzauu.zza();
                            com_google_android_gms_internal_zzbxl.zza(obj[zzaeo]);
                            com_google_android_gms_internal_zzbxl.zzaeo();
                            zzaeo++;
                        }
                        obj[zzaeo] = new com.google.android.gms.internal.zzauu.zza();
                        com_google_android_gms_internal_zzbxl.zza(obj[zzaeo]);
                        this.zzbwR = obj;
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

        public zzb zzNv() {
            this.zzbwN = null;
            this.zzbqL = null;
            this.zzbwO = null;
            this.zzbwP = zzc.zzNw();
            this.zzbwQ = zza.zzNt();
            this.zzbwR = com.google.android.gms.internal.zzauu.zza.zzNj();
            this.zzcuI = null;
            this.zzcuR = -1;
            return this;
        }

        public void zza(zzbxm com_google_android_gms_internal_zzbxm) throws IOException {
            int i = 0;
            if (this.zzbwN != null) {
                com_google_android_gms_internal_zzbxm.zzb(1, this.zzbwN.longValue());
            }
            if (this.zzbqL != null) {
                com_google_android_gms_internal_zzbxm.zzq(2, this.zzbqL);
            }
            if (this.zzbwO != null) {
                com_google_android_gms_internal_zzbxm.zzJ(3, this.zzbwO.intValue());
            }
            if (this.zzbwP != null && this.zzbwP.length > 0) {
                for (zzbxt com_google_android_gms_internal_zzbxt : this.zzbwP) {
                    if (com_google_android_gms_internal_zzbxt != null) {
                        com_google_android_gms_internal_zzbxm.zza(4, com_google_android_gms_internal_zzbxt);
                    }
                }
            }
            if (this.zzbwQ != null && this.zzbwQ.length > 0) {
                for (zzbxt com_google_android_gms_internal_zzbxt2 : this.zzbwQ) {
                    if (com_google_android_gms_internal_zzbxt2 != null) {
                        com_google_android_gms_internal_zzbxm.zza(5, com_google_android_gms_internal_zzbxt2);
                    }
                }
            }
            if (this.zzbwR != null && this.zzbwR.length > 0) {
                while (i < this.zzbwR.length) {
                    zzbxt com_google_android_gms_internal_zzbxt3 = this.zzbwR[i];
                    if (com_google_android_gms_internal_zzbxt3 != null) {
                        com_google_android_gms_internal_zzbxm.zza(6, com_google_android_gms_internal_zzbxt3);
                    }
                    i++;
                }
            }
            super.zza(com_google_android_gms_internal_zzbxm);
        }

        public /* synthetic */ zzbxt zzb(zzbxl com_google_android_gms_internal_zzbxl) throws IOException {
            return zzN(com_google_android_gms_internal_zzbxl);
        }

        protected int zzu() {
            int i;
            int i2 = 0;
            int zzu = super.zzu();
            if (this.zzbwN != null) {
                zzu += zzbxm.zzf(1, this.zzbwN.longValue());
            }
            if (this.zzbqL != null) {
                zzu += zzbxm.zzr(2, this.zzbqL);
            }
            if (this.zzbwO != null) {
                zzu += zzbxm.zzL(3, this.zzbwO.intValue());
            }
            if (this.zzbwP != null && this.zzbwP.length > 0) {
                i = zzu;
                for (zzbxt com_google_android_gms_internal_zzbxt : this.zzbwP) {
                    if (com_google_android_gms_internal_zzbxt != null) {
                        i += zzbxm.zzc(4, com_google_android_gms_internal_zzbxt);
                    }
                }
                zzu = i;
            }
            if (this.zzbwQ != null && this.zzbwQ.length > 0) {
                i = zzu;
                for (zzbxt com_google_android_gms_internal_zzbxt2 : this.zzbwQ) {
                    if (com_google_android_gms_internal_zzbxt2 != null) {
                        i += zzbxm.zzc(5, com_google_android_gms_internal_zzbxt2);
                    }
                }
                zzu = i;
            }
            if (this.zzbwR != null && this.zzbwR.length > 0) {
                while (i2 < this.zzbwR.length) {
                    zzbxt com_google_android_gms_internal_zzbxt3 = this.zzbwR[i2];
                    if (com_google_android_gms_internal_zzbxt3 != null) {
                        zzu += zzbxm.zzc(6, com_google_android_gms_internal_zzbxt3);
                    }
                    i2++;
                }
            }
            return zzu;
        }
    }

    public static final class zzc extends zzbxn<zzc> {
        private static volatile zzc[] zzbwS;
        public String value;
        public String zzaB;

        public zzc() {
            zzNx();
        }

        public static zzc[] zzNw() {
            if (zzbwS == null) {
                synchronized (zzbxr.zzcuQ) {
                    if (zzbwS == null) {
                        zzbwS = new zzc[0];
                    }
                }
            }
            return zzbwS;
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
            return (this.zzcuI == null || this.zzcuI.isEmpty()) ? com_google_android_gms_internal_zzauv_zzc.zzcuI == null || com_google_android_gms_internal_zzauv_zzc.zzcuI.isEmpty() : this.zzcuI.equals(com_google_android_gms_internal_zzauv_zzc.zzcuI);
        }

        public int hashCode() {
            int i = 0;
            int hashCode = ((this.value == null ? 0 : this.value.hashCode()) + (((this.zzaB == null ? 0 : this.zzaB.hashCode()) + ((getClass().getName().hashCode() + 527) * 31)) * 31)) * 31;
            if (!(this.zzcuI == null || this.zzcuI.isEmpty())) {
                i = this.zzcuI.hashCode();
            }
            return hashCode + i;
        }

        public zzc zzNx() {
            this.zzaB = null;
            this.value = null;
            this.zzcuI = null;
            this.zzcuR = -1;
            return this;
        }

        public zzc zzO(zzbxl com_google_android_gms_internal_zzbxl) throws IOException {
            while (true) {
                int zzaeo = com_google_android_gms_internal_zzbxl.zzaeo();
                switch (zzaeo) {
                    case 0:
                        break;
                    case 10:
                        this.zzaB = com_google_android_gms_internal_zzbxl.readString();
                        continue;
                    case 18:
                        this.value = com_google_android_gms_internal_zzbxl.readString();
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
            if (this.zzaB != null) {
                com_google_android_gms_internal_zzbxm.zzq(1, this.zzaB);
            }
            if (this.value != null) {
                com_google_android_gms_internal_zzbxm.zzq(2, this.value);
            }
            super.zza(com_google_android_gms_internal_zzbxm);
        }

        public /* synthetic */ zzbxt zzb(zzbxl com_google_android_gms_internal_zzbxl) throws IOException {
            return zzO(com_google_android_gms_internal_zzbxl);
        }

        protected int zzu() {
            int zzu = super.zzu();
            if (this.zzaB != null) {
                zzu += zzbxm.zzr(1, this.zzaB);
            }
            return this.value != null ? zzu + zzbxm.zzr(2, this.value) : zzu;
        }
    }
}
