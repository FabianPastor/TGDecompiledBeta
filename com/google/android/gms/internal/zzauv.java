package com.google.android.gms.internal;

import java.io.IOException;

public interface zzauv {

    public static final class zza extends zzbxn<zza> {
        private static volatile zza[] zzbwO;
        public String name;
        public Boolean zzbwP;
        public Boolean zzbwQ;

        public zza() {
            zzNt();
        }

        public static zza[] zzNs() {
            if (zzbwO == null) {
                synchronized (zzbxr.zzcuI) {
                    if (zzbwO == null) {
                        zzbwO = new zza[0];
                    }
                }
            }
            return zzbwO;
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
            if (this.zzbwP == null) {
                if (com_google_android_gms_internal_zzauv_zza.zzbwP != null) {
                    return false;
                }
            } else if (!this.zzbwP.equals(com_google_android_gms_internal_zzauv_zza.zzbwP)) {
                return false;
            }
            if (this.zzbwQ == null) {
                if (com_google_android_gms_internal_zzauv_zza.zzbwQ != null) {
                    return false;
                }
            } else if (!this.zzbwQ.equals(com_google_android_gms_internal_zzauv_zza.zzbwQ)) {
                return false;
            }
            return (this.zzcuA == null || this.zzcuA.isEmpty()) ? com_google_android_gms_internal_zzauv_zza.zzcuA == null || com_google_android_gms_internal_zzauv_zza.zzcuA.isEmpty() : this.zzcuA.equals(com_google_android_gms_internal_zzauv_zza.zzcuA);
        }

        public int hashCode() {
            int i = 0;
            int hashCode = ((this.zzbwQ == null ? 0 : this.zzbwQ.hashCode()) + (((this.zzbwP == null ? 0 : this.zzbwP.hashCode()) + (((this.name == null ? 0 : this.name.hashCode()) + ((getClass().getName().hashCode() + 527) * 31)) * 31)) * 31)) * 31;
            if (!(this.zzcuA == null || this.zzcuA.isEmpty())) {
                i = this.zzcuA.hashCode();
            }
            return hashCode + i;
        }

        public zza zzM(zzbxl com_google_android_gms_internal_zzbxl) throws IOException {
            while (true) {
                int zzaen = com_google_android_gms_internal_zzbxl.zzaen();
                switch (zzaen) {
                    case 0:
                        break;
                    case 10:
                        this.name = com_google_android_gms_internal_zzbxl.readString();
                        continue;
                    case 16:
                        this.zzbwP = Boolean.valueOf(com_google_android_gms_internal_zzbxl.zzaet());
                        continue;
                    case 24:
                        this.zzbwQ = Boolean.valueOf(com_google_android_gms_internal_zzbxl.zzaet());
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

        public zza zzNt() {
            this.name = null;
            this.zzbwP = null;
            this.zzbwQ = null;
            this.zzcuA = null;
            this.zzcuJ = -1;
            return this;
        }

        public void zza(zzbxm com_google_android_gms_internal_zzbxm) throws IOException {
            if (this.name != null) {
                com_google_android_gms_internal_zzbxm.zzq(1, this.name);
            }
            if (this.zzbwP != null) {
                com_google_android_gms_internal_zzbxm.zzg(2, this.zzbwP.booleanValue());
            }
            if (this.zzbwQ != null) {
                com_google_android_gms_internal_zzbxm.zzg(3, this.zzbwQ.booleanValue());
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
            if (this.zzbwP != null) {
                zzu += zzbxm.zzh(2, this.zzbwP.booleanValue());
            }
            return this.zzbwQ != null ? zzu + zzbxm.zzh(3, this.zzbwQ.booleanValue()) : zzu;
        }
    }

    public static final class zzb extends zzbxn<zzb> {
        public String zzbqP;
        public Long zzbwR;
        public Integer zzbwS;
        public zzc[] zzbwT;
        public zza[] zzbwU;
        public com.google.android.gms.internal.zzauu.zza[] zzbwV;

        public zzb() {
            zzNu();
        }

        public boolean equals(Object obj) {
            if (obj == this) {
                return true;
            }
            if (!(obj instanceof zzb)) {
                return false;
            }
            zzb com_google_android_gms_internal_zzauv_zzb = (zzb) obj;
            if (this.zzbwR == null) {
                if (com_google_android_gms_internal_zzauv_zzb.zzbwR != null) {
                    return false;
                }
            } else if (!this.zzbwR.equals(com_google_android_gms_internal_zzauv_zzb.zzbwR)) {
                return false;
            }
            if (this.zzbqP == null) {
                if (com_google_android_gms_internal_zzauv_zzb.zzbqP != null) {
                    return false;
                }
            } else if (!this.zzbqP.equals(com_google_android_gms_internal_zzauv_zzb.zzbqP)) {
                return false;
            }
            if (this.zzbwS == null) {
                if (com_google_android_gms_internal_zzauv_zzb.zzbwS != null) {
                    return false;
                }
            } else if (!this.zzbwS.equals(com_google_android_gms_internal_zzauv_zzb.zzbwS)) {
                return false;
            }
            return (zzbxr.equals(this.zzbwT, com_google_android_gms_internal_zzauv_zzb.zzbwT) && zzbxr.equals(this.zzbwU, com_google_android_gms_internal_zzauv_zzb.zzbwU) && zzbxr.equals(this.zzbwV, com_google_android_gms_internal_zzauv_zzb.zzbwV)) ? (this.zzcuA == null || this.zzcuA.isEmpty()) ? com_google_android_gms_internal_zzauv_zzb.zzcuA == null || com_google_android_gms_internal_zzauv_zzb.zzcuA.isEmpty() : this.zzcuA.equals(com_google_android_gms_internal_zzauv_zzb.zzcuA) : false;
        }

        public int hashCode() {
            int i = 0;
            int hashCode = ((((((((this.zzbwS == null ? 0 : this.zzbwS.hashCode()) + (((this.zzbqP == null ? 0 : this.zzbqP.hashCode()) + (((this.zzbwR == null ? 0 : this.zzbwR.hashCode()) + ((getClass().getName().hashCode() + 527) * 31)) * 31)) * 31)) * 31) + zzbxr.hashCode(this.zzbwT)) * 31) + zzbxr.hashCode(this.zzbwU)) * 31) + zzbxr.hashCode(this.zzbwV)) * 31;
            if (!(this.zzcuA == null || this.zzcuA.isEmpty())) {
                i = this.zzcuA.hashCode();
            }
            return hashCode + i;
        }

        public zzb zzN(zzbxl com_google_android_gms_internal_zzbxl) throws IOException {
            while (true) {
                int zzaen = com_google_android_gms_internal_zzbxl.zzaen();
                int zzb;
                Object obj;
                switch (zzaen) {
                    case 0:
                        break;
                    case 8:
                        this.zzbwR = Long.valueOf(com_google_android_gms_internal_zzbxl.zzaeq());
                        continue;
                    case 18:
                        this.zzbqP = com_google_android_gms_internal_zzbxl.readString();
                        continue;
                    case 24:
                        this.zzbwS = Integer.valueOf(com_google_android_gms_internal_zzbxl.zzaer());
                        continue;
                    case 34:
                        zzb = zzbxw.zzb(com_google_android_gms_internal_zzbxl, 34);
                        zzaen = this.zzbwT == null ? 0 : this.zzbwT.length;
                        obj = new zzc[(zzb + zzaen)];
                        if (zzaen != 0) {
                            System.arraycopy(this.zzbwT, 0, obj, 0, zzaen);
                        }
                        while (zzaen < obj.length - 1) {
                            obj[zzaen] = new zzc();
                            com_google_android_gms_internal_zzbxl.zza(obj[zzaen]);
                            com_google_android_gms_internal_zzbxl.zzaen();
                            zzaen++;
                        }
                        obj[zzaen] = new zzc();
                        com_google_android_gms_internal_zzbxl.zza(obj[zzaen]);
                        this.zzbwT = obj;
                        continue;
                    case 42:
                        zzb = zzbxw.zzb(com_google_android_gms_internal_zzbxl, 42);
                        zzaen = this.zzbwU == null ? 0 : this.zzbwU.length;
                        obj = new zza[(zzb + zzaen)];
                        if (zzaen != 0) {
                            System.arraycopy(this.zzbwU, 0, obj, 0, zzaen);
                        }
                        while (zzaen < obj.length - 1) {
                            obj[zzaen] = new zza();
                            com_google_android_gms_internal_zzbxl.zza(obj[zzaen]);
                            com_google_android_gms_internal_zzbxl.zzaen();
                            zzaen++;
                        }
                        obj[zzaen] = new zza();
                        com_google_android_gms_internal_zzbxl.zza(obj[zzaen]);
                        this.zzbwU = obj;
                        continue;
                    case 50:
                        zzb = zzbxw.zzb(com_google_android_gms_internal_zzbxl, 50);
                        zzaen = this.zzbwV == null ? 0 : this.zzbwV.length;
                        obj = new com.google.android.gms.internal.zzauu.zza[(zzb + zzaen)];
                        if (zzaen != 0) {
                            System.arraycopy(this.zzbwV, 0, obj, 0, zzaen);
                        }
                        while (zzaen < obj.length - 1) {
                            obj[zzaen] = new com.google.android.gms.internal.zzauu.zza();
                            com_google_android_gms_internal_zzbxl.zza(obj[zzaen]);
                            com_google_android_gms_internal_zzbxl.zzaen();
                            zzaen++;
                        }
                        obj[zzaen] = new com.google.android.gms.internal.zzauu.zza();
                        com_google_android_gms_internal_zzbxl.zza(obj[zzaen]);
                        this.zzbwV = obj;
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

        public zzb zzNu() {
            this.zzbwR = null;
            this.zzbqP = null;
            this.zzbwS = null;
            this.zzbwT = zzc.zzNv();
            this.zzbwU = zza.zzNs();
            this.zzbwV = com.google.android.gms.internal.zzauu.zza.zzNi();
            this.zzcuA = null;
            this.zzcuJ = -1;
            return this;
        }

        public void zza(zzbxm com_google_android_gms_internal_zzbxm) throws IOException {
            int i = 0;
            if (this.zzbwR != null) {
                com_google_android_gms_internal_zzbxm.zzb(1, this.zzbwR.longValue());
            }
            if (this.zzbqP != null) {
                com_google_android_gms_internal_zzbxm.zzq(2, this.zzbqP);
            }
            if (this.zzbwS != null) {
                com_google_android_gms_internal_zzbxm.zzJ(3, this.zzbwS.intValue());
            }
            if (this.zzbwT != null && this.zzbwT.length > 0) {
                for (zzbxt com_google_android_gms_internal_zzbxt : this.zzbwT) {
                    if (com_google_android_gms_internal_zzbxt != null) {
                        com_google_android_gms_internal_zzbxm.zza(4, com_google_android_gms_internal_zzbxt);
                    }
                }
            }
            if (this.zzbwU != null && this.zzbwU.length > 0) {
                for (zzbxt com_google_android_gms_internal_zzbxt2 : this.zzbwU) {
                    if (com_google_android_gms_internal_zzbxt2 != null) {
                        com_google_android_gms_internal_zzbxm.zza(5, com_google_android_gms_internal_zzbxt2);
                    }
                }
            }
            if (this.zzbwV != null && this.zzbwV.length > 0) {
                while (i < this.zzbwV.length) {
                    zzbxt com_google_android_gms_internal_zzbxt3 = this.zzbwV[i];
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
            if (this.zzbwR != null) {
                zzu += zzbxm.zzf(1, this.zzbwR.longValue());
            }
            if (this.zzbqP != null) {
                zzu += zzbxm.zzr(2, this.zzbqP);
            }
            if (this.zzbwS != null) {
                zzu += zzbxm.zzL(3, this.zzbwS.intValue());
            }
            if (this.zzbwT != null && this.zzbwT.length > 0) {
                i = zzu;
                for (zzbxt com_google_android_gms_internal_zzbxt : this.zzbwT) {
                    if (com_google_android_gms_internal_zzbxt != null) {
                        i += zzbxm.zzc(4, com_google_android_gms_internal_zzbxt);
                    }
                }
                zzu = i;
            }
            if (this.zzbwU != null && this.zzbwU.length > 0) {
                i = zzu;
                for (zzbxt com_google_android_gms_internal_zzbxt2 : this.zzbwU) {
                    if (com_google_android_gms_internal_zzbxt2 != null) {
                        i += zzbxm.zzc(5, com_google_android_gms_internal_zzbxt2);
                    }
                }
                zzu = i;
            }
            if (this.zzbwV != null && this.zzbwV.length > 0) {
                while (i2 < this.zzbwV.length) {
                    zzbxt com_google_android_gms_internal_zzbxt3 = this.zzbwV[i2];
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
        private static volatile zzc[] zzbwW;
        public String value;
        public String zzaB;

        public zzc() {
            zzNw();
        }

        public static zzc[] zzNv() {
            if (zzbwW == null) {
                synchronized (zzbxr.zzcuI) {
                    if (zzbwW == null) {
                        zzbwW = new zzc[0];
                    }
                }
            }
            return zzbwW;
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
            return (this.zzcuA == null || this.zzcuA.isEmpty()) ? com_google_android_gms_internal_zzauv_zzc.zzcuA == null || com_google_android_gms_internal_zzauv_zzc.zzcuA.isEmpty() : this.zzcuA.equals(com_google_android_gms_internal_zzauv_zzc.zzcuA);
        }

        public int hashCode() {
            int i = 0;
            int hashCode = ((this.value == null ? 0 : this.value.hashCode()) + (((this.zzaB == null ? 0 : this.zzaB.hashCode()) + ((getClass().getName().hashCode() + 527) * 31)) * 31)) * 31;
            if (!(this.zzcuA == null || this.zzcuA.isEmpty())) {
                i = this.zzcuA.hashCode();
            }
            return hashCode + i;
        }

        public zzc zzNw() {
            this.zzaB = null;
            this.value = null;
            this.zzcuA = null;
            this.zzcuJ = -1;
            return this;
        }

        public zzc zzO(zzbxl com_google_android_gms_internal_zzbxl) throws IOException {
            while (true) {
                int zzaen = com_google_android_gms_internal_zzbxl.zzaen();
                switch (zzaen) {
                    case 0:
                        break;
                    case 10:
                        this.zzaB = com_google_android_gms_internal_zzbxl.readString();
                        continue;
                    case 18:
                        this.value = com_google_android_gms_internal_zzbxl.readString();
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
