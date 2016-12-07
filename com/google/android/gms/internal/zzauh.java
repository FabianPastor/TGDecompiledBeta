package com.google.android.gms.internal;

import android.support.v4.view.MotionEventCompat;
import com.googlecode.mp4parser.authoring.tracks.h265.NalUnitTypes;
import java.io.IOException;
import org.telegram.messenger.exoplayer2.extractor.ts.TsExtractor;

public interface zzauh {

    public static final class zza extends zzbut {
        private static volatile zza[] zzbvQ;
        public zzf zzbvR;
        public zzf zzbvS;
        public Boolean zzbvT;
        public Integer zzbvh;

        public zza() {
            zzMz();
        }

        public static zza[] zzMy() {
            if (zzbvQ == null) {
                synchronized (zzbur.zzcsf) {
                    if (zzbvQ == null) {
                        zzbvQ = new zza[0];
                    }
                }
            }
            return zzbvQ;
        }

        public boolean equals(Object obj) {
            if (obj == this) {
                return true;
            }
            if (!(obj instanceof zza)) {
                return false;
            }
            zza com_google_android_gms_internal_zzauh_zza = (zza) obj;
            if (this.zzbvh == null) {
                if (com_google_android_gms_internal_zzauh_zza.zzbvh != null) {
                    return false;
                }
            } else if (!this.zzbvh.equals(com_google_android_gms_internal_zzauh_zza.zzbvh)) {
                return false;
            }
            if (this.zzbvR == null) {
                if (com_google_android_gms_internal_zzauh_zza.zzbvR != null) {
                    return false;
                }
            } else if (!this.zzbvR.equals(com_google_android_gms_internal_zzauh_zza.zzbvR)) {
                return false;
            }
            if (this.zzbvS == null) {
                if (com_google_android_gms_internal_zzauh_zza.zzbvS != null) {
                    return false;
                }
            } else if (!this.zzbvS.equals(com_google_android_gms_internal_zzauh_zza.zzbvS)) {
                return false;
            }
            return this.zzbvT == null ? com_google_android_gms_internal_zzauh_zza.zzbvT == null : this.zzbvT.equals(com_google_android_gms_internal_zzauh_zza.zzbvT);
        }

        public int hashCode() {
            int i = 0;
            int hashCode = ((this.zzbvS == null ? 0 : this.zzbvS.hashCode()) + (((this.zzbvR == null ? 0 : this.zzbvR.hashCode()) + (((this.zzbvh == null ? 0 : this.zzbvh.hashCode()) + ((getClass().getName().hashCode() + 527) * 31)) * 31)) * 31)) * 31;
            if (this.zzbvT != null) {
                i = this.zzbvT.hashCode();
            }
            return hashCode + i;
        }

        public zza zzMz() {
            this.zzbvh = null;
            this.zzbvR = null;
            this.zzbvS = null;
            this.zzbvT = null;
            this.zzcsg = -1;
            return this;
        }

        public zza zzP(zzbul com_google_android_gms_internal_zzbul) throws IOException {
            while (true) {
                int zzacu = com_google_android_gms_internal_zzbul.zzacu();
                switch (zzacu) {
                    case 0:
                        break;
                    case 8:
                        this.zzbvh = Integer.valueOf(com_google_android_gms_internal_zzbul.zzacy());
                        continue;
                    case 18:
                        if (this.zzbvR == null) {
                            this.zzbvR = new zzf();
                        }
                        com_google_android_gms_internal_zzbul.zza(this.zzbvR);
                        continue;
                    case NalUnitTypes.NAL_TYPE_RSV_VCL26 /*26*/:
                        if (this.zzbvS == null) {
                            this.zzbvS = new zzf();
                        }
                        com_google_android_gms_internal_zzbul.zza(this.zzbvS);
                        continue;
                    case 32:
                        this.zzbvT = Boolean.valueOf(com_google_android_gms_internal_zzbul.zzacA());
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

        public void zza(zzbum com_google_android_gms_internal_zzbum) throws IOException {
            if (this.zzbvh != null) {
                com_google_android_gms_internal_zzbum.zzF(1, this.zzbvh.intValue());
            }
            if (this.zzbvR != null) {
                com_google_android_gms_internal_zzbum.zza(2, this.zzbvR);
            }
            if (this.zzbvS != null) {
                com_google_android_gms_internal_zzbum.zza(3, this.zzbvS);
            }
            if (this.zzbvT != null) {
                com_google_android_gms_internal_zzbum.zzg(4, this.zzbvT.booleanValue());
            }
            super.zza(com_google_android_gms_internal_zzbum);
        }

        public /* synthetic */ zzbut zzb(zzbul com_google_android_gms_internal_zzbul) throws IOException {
            return zzP(com_google_android_gms_internal_zzbul);
        }

        protected int zzv() {
            int zzv = super.zzv();
            if (this.zzbvh != null) {
                zzv += zzbum.zzH(1, this.zzbvh.intValue());
            }
            if (this.zzbvR != null) {
                zzv += zzbum.zzc(2, this.zzbvR);
            }
            if (this.zzbvS != null) {
                zzv += zzbum.zzc(3, this.zzbvS);
            }
            return this.zzbvT != null ? zzv + zzbum.zzh(4, this.zzbvT.booleanValue()) : zzv;
        }
    }

    public static final class zzb extends zzbut {
        private static volatile zzb[] zzbvU;
        public Integer count;
        public String name;
        public zzc[] zzbvV;
        public Long zzbvW;
        public Long zzbvX;

        public zzb() {
            zzMB();
        }

        public static zzb[] zzMA() {
            if (zzbvU == null) {
                synchronized (zzbur.zzcsf) {
                    if (zzbvU == null) {
                        zzbvU = new zzb[0];
                    }
                }
            }
            return zzbvU;
        }

        public boolean equals(Object obj) {
            if (obj == this) {
                return true;
            }
            if (!(obj instanceof zzb)) {
                return false;
            }
            zzb com_google_android_gms_internal_zzauh_zzb = (zzb) obj;
            if (!zzbur.equals(this.zzbvV, com_google_android_gms_internal_zzauh_zzb.zzbvV)) {
                return false;
            }
            if (this.name == null) {
                if (com_google_android_gms_internal_zzauh_zzb.name != null) {
                    return false;
                }
            } else if (!this.name.equals(com_google_android_gms_internal_zzauh_zzb.name)) {
                return false;
            }
            if (this.zzbvW == null) {
                if (com_google_android_gms_internal_zzauh_zzb.zzbvW != null) {
                    return false;
                }
            } else if (!this.zzbvW.equals(com_google_android_gms_internal_zzauh_zzb.zzbvW)) {
                return false;
            }
            if (this.zzbvX == null) {
                if (com_google_android_gms_internal_zzauh_zzb.zzbvX != null) {
                    return false;
                }
            } else if (!this.zzbvX.equals(com_google_android_gms_internal_zzauh_zzb.zzbvX)) {
                return false;
            }
            return this.count == null ? com_google_android_gms_internal_zzauh_zzb.count == null : this.count.equals(com_google_android_gms_internal_zzauh_zzb.count);
        }

        public int hashCode() {
            int i = 0;
            int hashCode = ((this.zzbvX == null ? 0 : this.zzbvX.hashCode()) + (((this.zzbvW == null ? 0 : this.zzbvW.hashCode()) + (((this.name == null ? 0 : this.name.hashCode()) + ((((getClass().getName().hashCode() + 527) * 31) + zzbur.hashCode(this.zzbvV)) * 31)) * 31)) * 31)) * 31;
            if (this.count != null) {
                i = this.count.hashCode();
            }
            return hashCode + i;
        }

        public zzb zzMB() {
            this.zzbvV = zzc.zzMC();
            this.name = null;
            this.zzbvW = null;
            this.zzbvX = null;
            this.count = null;
            this.zzcsg = -1;
            return this;
        }

        public zzb zzQ(zzbul com_google_android_gms_internal_zzbul) throws IOException {
            while (true) {
                int zzacu = com_google_android_gms_internal_zzbul.zzacu();
                switch (zzacu) {
                    case 0:
                        break;
                    case 10:
                        int zzc = zzbuw.zzc(com_google_android_gms_internal_zzbul, 10);
                        zzacu = this.zzbvV == null ? 0 : this.zzbvV.length;
                        Object obj = new zzc[(zzc + zzacu)];
                        if (zzacu != 0) {
                            System.arraycopy(this.zzbvV, 0, obj, 0, zzacu);
                        }
                        while (zzacu < obj.length - 1) {
                            obj[zzacu] = new zzc();
                            com_google_android_gms_internal_zzbul.zza(obj[zzacu]);
                            com_google_android_gms_internal_zzbul.zzacu();
                            zzacu++;
                        }
                        obj[zzacu] = new zzc();
                        com_google_android_gms_internal_zzbul.zza(obj[zzacu]);
                        this.zzbvV = obj;
                        continue;
                    case 18:
                        this.name = com_google_android_gms_internal_zzbul.readString();
                        continue;
                    case 24:
                        this.zzbvW = Long.valueOf(com_google_android_gms_internal_zzbul.zzacx());
                        continue;
                    case 32:
                        this.zzbvX = Long.valueOf(com_google_android_gms_internal_zzbul.zzacx());
                        continue;
                    case MotionEventCompat.AXIS_GENERIC_9 /*40*/:
                        this.count = Integer.valueOf(com_google_android_gms_internal_zzbul.zzacy());
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

        public void zza(zzbum com_google_android_gms_internal_zzbum) throws IOException {
            if (this.zzbvV != null && this.zzbvV.length > 0) {
                for (zzbut com_google_android_gms_internal_zzbut : this.zzbvV) {
                    if (com_google_android_gms_internal_zzbut != null) {
                        com_google_android_gms_internal_zzbum.zza(1, com_google_android_gms_internal_zzbut);
                    }
                }
            }
            if (this.name != null) {
                com_google_android_gms_internal_zzbum.zzq(2, this.name);
            }
            if (this.zzbvW != null) {
                com_google_android_gms_internal_zzbum.zzb(3, this.zzbvW.longValue());
            }
            if (this.zzbvX != null) {
                com_google_android_gms_internal_zzbum.zzb(4, this.zzbvX.longValue());
            }
            if (this.count != null) {
                com_google_android_gms_internal_zzbum.zzF(5, this.count.intValue());
            }
            super.zza(com_google_android_gms_internal_zzbum);
        }

        public /* synthetic */ zzbut zzb(zzbul com_google_android_gms_internal_zzbul) throws IOException {
            return zzQ(com_google_android_gms_internal_zzbul);
        }

        protected int zzv() {
            int zzv = super.zzv();
            if (this.zzbvV != null && this.zzbvV.length > 0) {
                for (zzbut com_google_android_gms_internal_zzbut : this.zzbvV) {
                    if (com_google_android_gms_internal_zzbut != null) {
                        zzv += zzbum.zzc(1, com_google_android_gms_internal_zzbut);
                    }
                }
            }
            if (this.name != null) {
                zzv += zzbum.zzr(2, this.name);
            }
            if (this.zzbvW != null) {
                zzv += zzbum.zzf(3, this.zzbvW.longValue());
            }
            if (this.zzbvX != null) {
                zzv += zzbum.zzf(4, this.zzbvX.longValue());
            }
            return this.count != null ? zzv + zzbum.zzH(5, this.count.intValue()) : zzv;
        }
    }

    public static final class zzc extends zzbut {
        private static volatile zzc[] zzbvY;
        public String name;
        public String zzaFy;
        public Long zzbvZ;
        public Float zzbvb;
        public Double zzbvc;

        public zzc() {
            zzMD();
        }

        public static zzc[] zzMC() {
            if (zzbvY == null) {
                synchronized (zzbur.zzcsf) {
                    if (zzbvY == null) {
                        zzbvY = new zzc[0];
                    }
                }
            }
            return zzbvY;
        }

        public boolean equals(Object obj) {
            if (obj == this) {
                return true;
            }
            if (!(obj instanceof zzc)) {
                return false;
            }
            zzc com_google_android_gms_internal_zzauh_zzc = (zzc) obj;
            if (this.name == null) {
                if (com_google_android_gms_internal_zzauh_zzc.name != null) {
                    return false;
                }
            } else if (!this.name.equals(com_google_android_gms_internal_zzauh_zzc.name)) {
                return false;
            }
            if (this.zzaFy == null) {
                if (com_google_android_gms_internal_zzauh_zzc.zzaFy != null) {
                    return false;
                }
            } else if (!this.zzaFy.equals(com_google_android_gms_internal_zzauh_zzc.zzaFy)) {
                return false;
            }
            if (this.zzbvZ == null) {
                if (com_google_android_gms_internal_zzauh_zzc.zzbvZ != null) {
                    return false;
                }
            } else if (!this.zzbvZ.equals(com_google_android_gms_internal_zzauh_zzc.zzbvZ)) {
                return false;
            }
            if (this.zzbvb == null) {
                if (com_google_android_gms_internal_zzauh_zzc.zzbvb != null) {
                    return false;
                }
            } else if (!this.zzbvb.equals(com_google_android_gms_internal_zzauh_zzc.zzbvb)) {
                return false;
            }
            return this.zzbvc == null ? com_google_android_gms_internal_zzauh_zzc.zzbvc == null : this.zzbvc.equals(com_google_android_gms_internal_zzauh_zzc.zzbvc);
        }

        public int hashCode() {
            int i = 0;
            int hashCode = ((this.zzbvb == null ? 0 : this.zzbvb.hashCode()) + (((this.zzbvZ == null ? 0 : this.zzbvZ.hashCode()) + (((this.zzaFy == null ? 0 : this.zzaFy.hashCode()) + (((this.name == null ? 0 : this.name.hashCode()) + ((getClass().getName().hashCode() + 527) * 31)) * 31)) * 31)) * 31)) * 31;
            if (this.zzbvc != null) {
                i = this.zzbvc.hashCode();
            }
            return hashCode + i;
        }

        public zzc zzMD() {
            this.name = null;
            this.zzaFy = null;
            this.zzbvZ = null;
            this.zzbvb = null;
            this.zzbvc = null;
            this.zzcsg = -1;
            return this;
        }

        public zzc zzR(zzbul com_google_android_gms_internal_zzbul) throws IOException {
            while (true) {
                int zzacu = com_google_android_gms_internal_zzbul.zzacu();
                switch (zzacu) {
                    case 0:
                        break;
                    case 10:
                        this.name = com_google_android_gms_internal_zzbul.readString();
                        continue;
                    case 18:
                        this.zzaFy = com_google_android_gms_internal_zzbul.readString();
                        continue;
                    case 24:
                        this.zzbvZ = Long.valueOf(com_google_android_gms_internal_zzbul.zzacx());
                        continue;
                    case 37:
                        this.zzbvb = Float.valueOf(com_google_android_gms_internal_zzbul.readFloat());
                        continue;
                    case 41:
                        this.zzbvc = Double.valueOf(com_google_android_gms_internal_zzbul.readDouble());
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

        public void zza(zzbum com_google_android_gms_internal_zzbum) throws IOException {
            if (this.name != null) {
                com_google_android_gms_internal_zzbum.zzq(1, this.name);
            }
            if (this.zzaFy != null) {
                com_google_android_gms_internal_zzbum.zzq(2, this.zzaFy);
            }
            if (this.zzbvZ != null) {
                com_google_android_gms_internal_zzbum.zzb(3, this.zzbvZ.longValue());
            }
            if (this.zzbvb != null) {
                com_google_android_gms_internal_zzbum.zzc(4, this.zzbvb.floatValue());
            }
            if (this.zzbvc != null) {
                com_google_android_gms_internal_zzbum.zza(5, this.zzbvc.doubleValue());
            }
            super.zza(com_google_android_gms_internal_zzbum);
        }

        public /* synthetic */ zzbut zzb(zzbul com_google_android_gms_internal_zzbul) throws IOException {
            return zzR(com_google_android_gms_internal_zzbul);
        }

        protected int zzv() {
            int zzv = super.zzv();
            if (this.name != null) {
                zzv += zzbum.zzr(1, this.name);
            }
            if (this.zzaFy != null) {
                zzv += zzbum.zzr(2, this.zzaFy);
            }
            if (this.zzbvZ != null) {
                zzv += zzbum.zzf(3, this.zzbvZ.longValue());
            }
            if (this.zzbvb != null) {
                zzv += zzbum.zzd(4, this.zzbvb.floatValue());
            }
            return this.zzbvc != null ? zzv + zzbum.zzb(5, this.zzbvc.doubleValue()) : zzv;
        }
    }

    public static final class zzd extends zzbut {
        public zze[] zzbwa;

        public zzd() {
            zzME();
        }

        public boolean equals(Object obj) {
            if (obj == this) {
                return true;
            }
            if (!(obj instanceof zzd)) {
                return false;
            }
            return zzbur.equals(this.zzbwa, ((zzd) obj).zzbwa);
        }

        public int hashCode() {
            return ((getClass().getName().hashCode() + 527) * 31) + zzbur.hashCode(this.zzbwa);
        }

        public zzd zzME() {
            this.zzbwa = zze.zzMF();
            this.zzcsg = -1;
            return this;
        }

        public zzd zzS(zzbul com_google_android_gms_internal_zzbul) throws IOException {
            while (true) {
                int zzacu = com_google_android_gms_internal_zzbul.zzacu();
                switch (zzacu) {
                    case 0:
                        break;
                    case 10:
                        int zzc = zzbuw.zzc(com_google_android_gms_internal_zzbul, 10);
                        zzacu = this.zzbwa == null ? 0 : this.zzbwa.length;
                        Object obj = new zze[(zzc + zzacu)];
                        if (zzacu != 0) {
                            System.arraycopy(this.zzbwa, 0, obj, 0, zzacu);
                        }
                        while (zzacu < obj.length - 1) {
                            obj[zzacu] = new zze();
                            com_google_android_gms_internal_zzbul.zza(obj[zzacu]);
                            com_google_android_gms_internal_zzbul.zzacu();
                            zzacu++;
                        }
                        obj[zzacu] = new zze();
                        com_google_android_gms_internal_zzbul.zza(obj[zzacu]);
                        this.zzbwa = obj;
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

        public void zza(zzbum com_google_android_gms_internal_zzbum) throws IOException {
            if (this.zzbwa != null && this.zzbwa.length > 0) {
                for (zzbut com_google_android_gms_internal_zzbut : this.zzbwa) {
                    if (com_google_android_gms_internal_zzbut != null) {
                        com_google_android_gms_internal_zzbum.zza(1, com_google_android_gms_internal_zzbut);
                    }
                }
            }
            super.zza(com_google_android_gms_internal_zzbum);
        }

        public /* synthetic */ zzbut zzb(zzbul com_google_android_gms_internal_zzbul) throws IOException {
            return zzS(com_google_android_gms_internal_zzbul);
        }

        protected int zzv() {
            int zzv = super.zzv();
            if (this.zzbwa != null && this.zzbwa.length > 0) {
                for (zzbut com_google_android_gms_internal_zzbut : this.zzbwa) {
                    if (com_google_android_gms_internal_zzbut != null) {
                        zzv += zzbum.zzc(1, com_google_android_gms_internal_zzbut);
                    }
                }
            }
            return zzv;
        }
    }

    public static final class zze extends zzbut {
        private static volatile zze[] zzbwb;
        public String zzaR;
        public String zzba;
        public String zzbhg;
        public String zzbqf;
        public String zzbqg;
        public String zzbqj;
        public String zzbqn;
        public String zzbwA;
        public Long zzbwB;
        public Integer zzbwc;
        public zzb[] zzbwd;
        public zzg[] zzbwe;
        public Long zzbwf;
        public Long zzbwg;
        public Long zzbwh;
        public Long zzbwi;
        public Long zzbwj;
        public String zzbwk;
        public String zzbwl;
        public String zzbwm;
        public Integer zzbwn;
        public Long zzbwo;
        public Long zzbwp;
        public String zzbwq;
        public Boolean zzbwr;
        public String zzbws;
        public Long zzbwt;
        public Integer zzbwu;
        public Boolean zzbwv;
        public zza[] zzbww;
        public Integer zzbwx;
        public Integer zzbwy;
        public Integer zzbwz;

        public zze() {
            zzMG();
        }

        public static zze[] zzMF() {
            if (zzbwb == null) {
                synchronized (zzbur.zzcsf) {
                    if (zzbwb == null) {
                        zzbwb = new zze[0];
                    }
                }
            }
            return zzbwb;
        }

        public boolean equals(Object obj) {
            if (obj == this) {
                return true;
            }
            if (!(obj instanceof zze)) {
                return false;
            }
            zze com_google_android_gms_internal_zzauh_zze = (zze) obj;
            if (this.zzbwc == null) {
                if (com_google_android_gms_internal_zzauh_zze.zzbwc != null) {
                    return false;
                }
            } else if (!this.zzbwc.equals(com_google_android_gms_internal_zzauh_zze.zzbwc)) {
                return false;
            }
            if (!zzbur.equals(this.zzbwd, com_google_android_gms_internal_zzauh_zze.zzbwd)) {
                return false;
            }
            if (!zzbur.equals(this.zzbwe, com_google_android_gms_internal_zzauh_zze.zzbwe)) {
                return false;
            }
            if (this.zzbwf == null) {
                if (com_google_android_gms_internal_zzauh_zze.zzbwf != null) {
                    return false;
                }
            } else if (!this.zzbwf.equals(com_google_android_gms_internal_zzauh_zze.zzbwf)) {
                return false;
            }
            if (this.zzbwg == null) {
                if (com_google_android_gms_internal_zzauh_zze.zzbwg != null) {
                    return false;
                }
            } else if (!this.zzbwg.equals(com_google_android_gms_internal_zzauh_zze.zzbwg)) {
                return false;
            }
            if (this.zzbwh == null) {
                if (com_google_android_gms_internal_zzauh_zze.zzbwh != null) {
                    return false;
                }
            } else if (!this.zzbwh.equals(com_google_android_gms_internal_zzauh_zze.zzbwh)) {
                return false;
            }
            if (this.zzbwi == null) {
                if (com_google_android_gms_internal_zzauh_zze.zzbwi != null) {
                    return false;
                }
            } else if (!this.zzbwi.equals(com_google_android_gms_internal_zzauh_zze.zzbwi)) {
                return false;
            }
            if (this.zzbwj == null) {
                if (com_google_android_gms_internal_zzauh_zze.zzbwj != null) {
                    return false;
                }
            } else if (!this.zzbwj.equals(com_google_android_gms_internal_zzauh_zze.zzbwj)) {
                return false;
            }
            if (this.zzbwk == null) {
                if (com_google_android_gms_internal_zzauh_zze.zzbwk != null) {
                    return false;
                }
            } else if (!this.zzbwk.equals(com_google_android_gms_internal_zzauh_zze.zzbwk)) {
                return false;
            }
            if (this.zzba == null) {
                if (com_google_android_gms_internal_zzauh_zze.zzba != null) {
                    return false;
                }
            } else if (!this.zzba.equals(com_google_android_gms_internal_zzauh_zze.zzba)) {
                return false;
            }
            if (this.zzbwl == null) {
                if (com_google_android_gms_internal_zzauh_zze.zzbwl != null) {
                    return false;
                }
            } else if (!this.zzbwl.equals(com_google_android_gms_internal_zzauh_zze.zzbwl)) {
                return false;
            }
            if (this.zzbwm == null) {
                if (com_google_android_gms_internal_zzauh_zze.zzbwm != null) {
                    return false;
                }
            } else if (!this.zzbwm.equals(com_google_android_gms_internal_zzauh_zze.zzbwm)) {
                return false;
            }
            if (this.zzbwn == null) {
                if (com_google_android_gms_internal_zzauh_zze.zzbwn != null) {
                    return false;
                }
            } else if (!this.zzbwn.equals(com_google_android_gms_internal_zzauh_zze.zzbwn)) {
                return false;
            }
            if (this.zzbqg == null) {
                if (com_google_android_gms_internal_zzauh_zze.zzbqg != null) {
                    return false;
                }
            } else if (!this.zzbqg.equals(com_google_android_gms_internal_zzauh_zze.zzbqg)) {
                return false;
            }
            if (this.zzaR == null) {
                if (com_google_android_gms_internal_zzauh_zze.zzaR != null) {
                    return false;
                }
            } else if (!this.zzaR.equals(com_google_android_gms_internal_zzauh_zze.zzaR)) {
                return false;
            }
            if (this.zzbhg == null) {
                if (com_google_android_gms_internal_zzauh_zze.zzbhg != null) {
                    return false;
                }
            } else if (!this.zzbhg.equals(com_google_android_gms_internal_zzauh_zze.zzbhg)) {
                return false;
            }
            if (this.zzbwo == null) {
                if (com_google_android_gms_internal_zzauh_zze.zzbwo != null) {
                    return false;
                }
            } else if (!this.zzbwo.equals(com_google_android_gms_internal_zzauh_zze.zzbwo)) {
                return false;
            }
            if (this.zzbwp == null) {
                if (com_google_android_gms_internal_zzauh_zze.zzbwp != null) {
                    return false;
                }
            } else if (!this.zzbwp.equals(com_google_android_gms_internal_zzauh_zze.zzbwp)) {
                return false;
            }
            if (this.zzbwq == null) {
                if (com_google_android_gms_internal_zzauh_zze.zzbwq != null) {
                    return false;
                }
            } else if (!this.zzbwq.equals(com_google_android_gms_internal_zzauh_zze.zzbwq)) {
                return false;
            }
            if (this.zzbwr == null) {
                if (com_google_android_gms_internal_zzauh_zze.zzbwr != null) {
                    return false;
                }
            } else if (!this.zzbwr.equals(com_google_android_gms_internal_zzauh_zze.zzbwr)) {
                return false;
            }
            if (this.zzbws == null) {
                if (com_google_android_gms_internal_zzauh_zze.zzbws != null) {
                    return false;
                }
            } else if (!this.zzbws.equals(com_google_android_gms_internal_zzauh_zze.zzbws)) {
                return false;
            }
            if (this.zzbwt == null) {
                if (com_google_android_gms_internal_zzauh_zze.zzbwt != null) {
                    return false;
                }
            } else if (!this.zzbwt.equals(com_google_android_gms_internal_zzauh_zze.zzbwt)) {
                return false;
            }
            if (this.zzbwu == null) {
                if (com_google_android_gms_internal_zzauh_zze.zzbwu != null) {
                    return false;
                }
            } else if (!this.zzbwu.equals(com_google_android_gms_internal_zzauh_zze.zzbwu)) {
                return false;
            }
            if (this.zzbqj == null) {
                if (com_google_android_gms_internal_zzauh_zze.zzbqj != null) {
                    return false;
                }
            } else if (!this.zzbqj.equals(com_google_android_gms_internal_zzauh_zze.zzbqj)) {
                return false;
            }
            if (this.zzbqf == null) {
                if (com_google_android_gms_internal_zzauh_zze.zzbqf != null) {
                    return false;
                }
            } else if (!this.zzbqf.equals(com_google_android_gms_internal_zzauh_zze.zzbqf)) {
                return false;
            }
            if (this.zzbwv == null) {
                if (com_google_android_gms_internal_zzauh_zze.zzbwv != null) {
                    return false;
                }
            } else if (!this.zzbwv.equals(com_google_android_gms_internal_zzauh_zze.zzbwv)) {
                return false;
            }
            if (!zzbur.equals(this.zzbww, com_google_android_gms_internal_zzauh_zze.zzbww)) {
                return false;
            }
            if (this.zzbqn == null) {
                if (com_google_android_gms_internal_zzauh_zze.zzbqn != null) {
                    return false;
                }
            } else if (!this.zzbqn.equals(com_google_android_gms_internal_zzauh_zze.zzbqn)) {
                return false;
            }
            if (this.zzbwx == null) {
                if (com_google_android_gms_internal_zzauh_zze.zzbwx != null) {
                    return false;
                }
            } else if (!this.zzbwx.equals(com_google_android_gms_internal_zzauh_zze.zzbwx)) {
                return false;
            }
            if (this.zzbwy == null) {
                if (com_google_android_gms_internal_zzauh_zze.zzbwy != null) {
                    return false;
                }
            } else if (!this.zzbwy.equals(com_google_android_gms_internal_zzauh_zze.zzbwy)) {
                return false;
            }
            if (this.zzbwz == null) {
                if (com_google_android_gms_internal_zzauh_zze.zzbwz != null) {
                    return false;
                }
            } else if (!this.zzbwz.equals(com_google_android_gms_internal_zzauh_zze.zzbwz)) {
                return false;
            }
            if (this.zzbwA == null) {
                if (com_google_android_gms_internal_zzauh_zze.zzbwA != null) {
                    return false;
                }
            } else if (!this.zzbwA.equals(com_google_android_gms_internal_zzauh_zze.zzbwA)) {
                return false;
            }
            return this.zzbwB == null ? com_google_android_gms_internal_zzauh_zze.zzbwB == null : this.zzbwB.equals(com_google_android_gms_internal_zzauh_zze.zzbwB);
        }

        public int hashCode() {
            int i = 0;
            int hashCode = ((this.zzbwA == null ? 0 : this.zzbwA.hashCode()) + (((this.zzbwz == null ? 0 : this.zzbwz.hashCode()) + (((this.zzbwy == null ? 0 : this.zzbwy.hashCode()) + (((this.zzbwx == null ? 0 : this.zzbwx.hashCode()) + (((this.zzbqn == null ? 0 : this.zzbqn.hashCode()) + (((((this.zzbwv == null ? 0 : this.zzbwv.hashCode()) + (((this.zzbqf == null ? 0 : this.zzbqf.hashCode()) + (((this.zzbqj == null ? 0 : this.zzbqj.hashCode()) + (((this.zzbwu == null ? 0 : this.zzbwu.hashCode()) + (((this.zzbwt == null ? 0 : this.zzbwt.hashCode()) + (((this.zzbws == null ? 0 : this.zzbws.hashCode()) + (((this.zzbwr == null ? 0 : this.zzbwr.hashCode()) + (((this.zzbwq == null ? 0 : this.zzbwq.hashCode()) + (((this.zzbwp == null ? 0 : this.zzbwp.hashCode()) + (((this.zzbwo == null ? 0 : this.zzbwo.hashCode()) + (((this.zzbhg == null ? 0 : this.zzbhg.hashCode()) + (((this.zzaR == null ? 0 : this.zzaR.hashCode()) + (((this.zzbqg == null ? 0 : this.zzbqg.hashCode()) + (((this.zzbwn == null ? 0 : this.zzbwn.hashCode()) + (((this.zzbwm == null ? 0 : this.zzbwm.hashCode()) + (((this.zzbwl == null ? 0 : this.zzbwl.hashCode()) + (((this.zzba == null ? 0 : this.zzba.hashCode()) + (((this.zzbwk == null ? 0 : this.zzbwk.hashCode()) + (((this.zzbwj == null ? 0 : this.zzbwj.hashCode()) + (((this.zzbwi == null ? 0 : this.zzbwi.hashCode()) + (((this.zzbwh == null ? 0 : this.zzbwh.hashCode()) + (((this.zzbwg == null ? 0 : this.zzbwg.hashCode()) + (((this.zzbwf == null ? 0 : this.zzbwf.hashCode()) + (((((((this.zzbwc == null ? 0 : this.zzbwc.hashCode()) + ((getClass().getName().hashCode() + 527) * 31)) * 31) + zzbur.hashCode(this.zzbwd)) * 31) + zzbur.hashCode(this.zzbwe)) * 31)) * 31)) * 31)) * 31)) * 31)) * 31)) * 31)) * 31)) * 31)) * 31)) * 31)) * 31)) * 31)) * 31)) * 31)) * 31)) * 31)) * 31)) * 31)) * 31)) * 31)) * 31)) * 31)) * 31) + zzbur.hashCode(this.zzbww)) * 31)) * 31)) * 31)) * 31)) * 31)) * 31;
            if (this.zzbwB != null) {
                i = this.zzbwB.hashCode();
            }
            return hashCode + i;
        }

        public zze zzMG() {
            this.zzbwc = null;
            this.zzbwd = zzb.zzMA();
            this.zzbwe = zzg.zzMI();
            this.zzbwf = null;
            this.zzbwg = null;
            this.zzbwh = null;
            this.zzbwi = null;
            this.zzbwj = null;
            this.zzbwk = null;
            this.zzba = null;
            this.zzbwl = null;
            this.zzbwm = null;
            this.zzbwn = null;
            this.zzbqg = null;
            this.zzaR = null;
            this.zzbhg = null;
            this.zzbwo = null;
            this.zzbwp = null;
            this.zzbwq = null;
            this.zzbwr = null;
            this.zzbws = null;
            this.zzbwt = null;
            this.zzbwu = null;
            this.zzbqj = null;
            this.zzbqf = null;
            this.zzbwv = null;
            this.zzbww = zza.zzMy();
            this.zzbqn = null;
            this.zzbwx = null;
            this.zzbwy = null;
            this.zzbwz = null;
            this.zzbwA = null;
            this.zzbwB = null;
            this.zzcsg = -1;
            return this;
        }

        public zze zzT(zzbul com_google_android_gms_internal_zzbul) throws IOException {
            while (true) {
                int zzacu = com_google_android_gms_internal_zzbul.zzacu();
                int zzc;
                Object obj;
                switch (zzacu) {
                    case 0:
                        break;
                    case 8:
                        this.zzbwc = Integer.valueOf(com_google_android_gms_internal_zzbul.zzacy());
                        continue;
                    case 18:
                        zzc = zzbuw.zzc(com_google_android_gms_internal_zzbul, 18);
                        zzacu = this.zzbwd == null ? 0 : this.zzbwd.length;
                        obj = new zzb[(zzc + zzacu)];
                        if (zzacu != 0) {
                            System.arraycopy(this.zzbwd, 0, obj, 0, zzacu);
                        }
                        while (zzacu < obj.length - 1) {
                            obj[zzacu] = new zzb();
                            com_google_android_gms_internal_zzbul.zza(obj[zzacu]);
                            com_google_android_gms_internal_zzbul.zzacu();
                            zzacu++;
                        }
                        obj[zzacu] = new zzb();
                        com_google_android_gms_internal_zzbul.zza(obj[zzacu]);
                        this.zzbwd = obj;
                        continue;
                    case NalUnitTypes.NAL_TYPE_RSV_VCL26 /*26*/:
                        zzc = zzbuw.zzc(com_google_android_gms_internal_zzbul, 26);
                        zzacu = this.zzbwe == null ? 0 : this.zzbwe.length;
                        obj = new zzg[(zzc + zzacu)];
                        if (zzacu != 0) {
                            System.arraycopy(this.zzbwe, 0, obj, 0, zzacu);
                        }
                        while (zzacu < obj.length - 1) {
                            obj[zzacu] = new zzg();
                            com_google_android_gms_internal_zzbul.zza(obj[zzacu]);
                            com_google_android_gms_internal_zzbul.zzacu();
                            zzacu++;
                        }
                        obj[zzacu] = new zzg();
                        com_google_android_gms_internal_zzbul.zza(obj[zzacu]);
                        this.zzbwe = obj;
                        continue;
                    case 32:
                        this.zzbwf = Long.valueOf(com_google_android_gms_internal_zzbul.zzacx());
                        continue;
                    case MotionEventCompat.AXIS_GENERIC_9 /*40*/:
                        this.zzbwg = Long.valueOf(com_google_android_gms_internal_zzbul.zzacx());
                        continue;
                    case 48:
                        this.zzbwh = Long.valueOf(com_google_android_gms_internal_zzbul.zzacx());
                        continue;
                    case 56:
                        this.zzbwj = Long.valueOf(com_google_android_gms_internal_zzbul.zzacx());
                        continue;
                    case 66:
                        this.zzbwk = com_google_android_gms_internal_zzbul.readString();
                        continue;
                    case 74:
                        this.zzba = com_google_android_gms_internal_zzbul.readString();
                        continue;
                    case 82:
                        this.zzbwl = com_google_android_gms_internal_zzbul.readString();
                        continue;
                    case 90:
                        this.zzbwm = com_google_android_gms_internal_zzbul.readString();
                        continue;
                    case 96:
                        this.zzbwn = Integer.valueOf(com_google_android_gms_internal_zzbul.zzacy());
                        continue;
                    case 106:
                        this.zzbqg = com_google_android_gms_internal_zzbul.readString();
                        continue;
                    case 114:
                        this.zzaR = com_google_android_gms_internal_zzbul.readString();
                        continue;
                    case TsExtractor.TS_STREAM_TYPE_HDMV_DTS /*130*/:
                        this.zzbhg = com_google_android_gms_internal_zzbul.readString();
                        continue;
                    case 136:
                        this.zzbwo = Long.valueOf(com_google_android_gms_internal_zzbul.zzacx());
                        continue;
                    case 144:
                        this.zzbwp = Long.valueOf(com_google_android_gms_internal_zzbul.zzacx());
                        continue;
                    case 154:
                        this.zzbwq = com_google_android_gms_internal_zzbul.readString();
                        continue;
                    case 160:
                        this.zzbwr = Boolean.valueOf(com_google_android_gms_internal_zzbul.zzacA());
                        continue;
                    case 170:
                        this.zzbws = com_google_android_gms_internal_zzbul.readString();
                        continue;
                    case 176:
                        this.zzbwt = Long.valueOf(com_google_android_gms_internal_zzbul.zzacx());
                        continue;
                    case 184:
                        this.zzbwu = Integer.valueOf(com_google_android_gms_internal_zzbul.zzacy());
                        continue;
                    case 194:
                        this.zzbqj = com_google_android_gms_internal_zzbul.readString();
                        continue;
                    case 202:
                        this.zzbqf = com_google_android_gms_internal_zzbul.readString();
                        continue;
                    case 208:
                        this.zzbwi = Long.valueOf(com_google_android_gms_internal_zzbul.zzacx());
                        continue;
                    case 224:
                        this.zzbwv = Boolean.valueOf(com_google_android_gms_internal_zzbul.zzacA());
                        continue;
                    case 234:
                        zzc = zzbuw.zzc(com_google_android_gms_internal_zzbul, 234);
                        zzacu = this.zzbww == null ? 0 : this.zzbww.length;
                        obj = new zza[(zzc + zzacu)];
                        if (zzacu != 0) {
                            System.arraycopy(this.zzbww, 0, obj, 0, zzacu);
                        }
                        while (zzacu < obj.length - 1) {
                            obj[zzacu] = new zza();
                            com_google_android_gms_internal_zzbul.zza(obj[zzacu]);
                            com_google_android_gms_internal_zzbul.zzacu();
                            zzacu++;
                        }
                        obj[zzacu] = new zza();
                        com_google_android_gms_internal_zzbul.zza(obj[zzacu]);
                        this.zzbww = obj;
                        continue;
                    case 242:
                        this.zzbqn = com_google_android_gms_internal_zzbul.readString();
                        continue;
                    case 248:
                        this.zzbwx = Integer.valueOf(com_google_android_gms_internal_zzbul.zzacy());
                        continue;
                    case 256:
                        this.zzbwy = Integer.valueOf(com_google_android_gms_internal_zzbul.zzacy());
                        continue;
                    case 264:
                        this.zzbwz = Integer.valueOf(com_google_android_gms_internal_zzbul.zzacy());
                        continue;
                    case 274:
                        this.zzbwA = com_google_android_gms_internal_zzbul.readString();
                        continue;
                    case 280:
                        this.zzbwB = Long.valueOf(com_google_android_gms_internal_zzbul.zzacx());
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

        public void zza(zzbum com_google_android_gms_internal_zzbum) throws IOException {
            int i = 0;
            if (this.zzbwc != null) {
                com_google_android_gms_internal_zzbum.zzF(1, this.zzbwc.intValue());
            }
            if (this.zzbwd != null && this.zzbwd.length > 0) {
                for (zzbut com_google_android_gms_internal_zzbut : this.zzbwd) {
                    if (com_google_android_gms_internal_zzbut != null) {
                        com_google_android_gms_internal_zzbum.zza(2, com_google_android_gms_internal_zzbut);
                    }
                }
            }
            if (this.zzbwe != null && this.zzbwe.length > 0) {
                for (zzbut com_google_android_gms_internal_zzbut2 : this.zzbwe) {
                    if (com_google_android_gms_internal_zzbut2 != null) {
                        com_google_android_gms_internal_zzbum.zza(3, com_google_android_gms_internal_zzbut2);
                    }
                }
            }
            if (this.zzbwf != null) {
                com_google_android_gms_internal_zzbum.zzb(4, this.zzbwf.longValue());
            }
            if (this.zzbwg != null) {
                com_google_android_gms_internal_zzbum.zzb(5, this.zzbwg.longValue());
            }
            if (this.zzbwh != null) {
                com_google_android_gms_internal_zzbum.zzb(6, this.zzbwh.longValue());
            }
            if (this.zzbwj != null) {
                com_google_android_gms_internal_zzbum.zzb(7, this.zzbwj.longValue());
            }
            if (this.zzbwk != null) {
                com_google_android_gms_internal_zzbum.zzq(8, this.zzbwk);
            }
            if (this.zzba != null) {
                com_google_android_gms_internal_zzbum.zzq(9, this.zzba);
            }
            if (this.zzbwl != null) {
                com_google_android_gms_internal_zzbum.zzq(10, this.zzbwl);
            }
            if (this.zzbwm != null) {
                com_google_android_gms_internal_zzbum.zzq(11, this.zzbwm);
            }
            if (this.zzbwn != null) {
                com_google_android_gms_internal_zzbum.zzF(12, this.zzbwn.intValue());
            }
            if (this.zzbqg != null) {
                com_google_android_gms_internal_zzbum.zzq(13, this.zzbqg);
            }
            if (this.zzaR != null) {
                com_google_android_gms_internal_zzbum.zzq(14, this.zzaR);
            }
            if (this.zzbhg != null) {
                com_google_android_gms_internal_zzbum.zzq(16, this.zzbhg);
            }
            if (this.zzbwo != null) {
                com_google_android_gms_internal_zzbum.zzb(17, this.zzbwo.longValue());
            }
            if (this.zzbwp != null) {
                com_google_android_gms_internal_zzbum.zzb(18, this.zzbwp.longValue());
            }
            if (this.zzbwq != null) {
                com_google_android_gms_internal_zzbum.zzq(19, this.zzbwq);
            }
            if (this.zzbwr != null) {
                com_google_android_gms_internal_zzbum.zzg(20, this.zzbwr.booleanValue());
            }
            if (this.zzbws != null) {
                com_google_android_gms_internal_zzbum.zzq(21, this.zzbws);
            }
            if (this.zzbwt != null) {
                com_google_android_gms_internal_zzbum.zzb(22, this.zzbwt.longValue());
            }
            if (this.zzbwu != null) {
                com_google_android_gms_internal_zzbum.zzF(23, this.zzbwu.intValue());
            }
            if (this.zzbqj != null) {
                com_google_android_gms_internal_zzbum.zzq(24, this.zzbqj);
            }
            if (this.zzbqf != null) {
                com_google_android_gms_internal_zzbum.zzq(25, this.zzbqf);
            }
            if (this.zzbwi != null) {
                com_google_android_gms_internal_zzbum.zzb(26, this.zzbwi.longValue());
            }
            if (this.zzbwv != null) {
                com_google_android_gms_internal_zzbum.zzg(28, this.zzbwv.booleanValue());
            }
            if (this.zzbww != null && this.zzbww.length > 0) {
                while (i < this.zzbww.length) {
                    zzbut com_google_android_gms_internal_zzbut3 = this.zzbww[i];
                    if (com_google_android_gms_internal_zzbut3 != null) {
                        com_google_android_gms_internal_zzbum.zza(29, com_google_android_gms_internal_zzbut3);
                    }
                    i++;
                }
            }
            if (this.zzbqn != null) {
                com_google_android_gms_internal_zzbum.zzq(30, this.zzbqn);
            }
            if (this.zzbwx != null) {
                com_google_android_gms_internal_zzbum.zzF(31, this.zzbwx.intValue());
            }
            if (this.zzbwy != null) {
                com_google_android_gms_internal_zzbum.zzF(32, this.zzbwy.intValue());
            }
            if (this.zzbwz != null) {
                com_google_android_gms_internal_zzbum.zzF(33, this.zzbwz.intValue());
            }
            if (this.zzbwA != null) {
                com_google_android_gms_internal_zzbum.zzq(34, this.zzbwA);
            }
            if (this.zzbwB != null) {
                com_google_android_gms_internal_zzbum.zzb(35, this.zzbwB.longValue());
            }
            super.zza(com_google_android_gms_internal_zzbum);
        }

        public /* synthetic */ zzbut zzb(zzbul com_google_android_gms_internal_zzbul) throws IOException {
            return zzT(com_google_android_gms_internal_zzbul);
        }

        protected int zzv() {
            int i;
            int i2 = 0;
            int zzv = super.zzv();
            if (this.zzbwc != null) {
                zzv += zzbum.zzH(1, this.zzbwc.intValue());
            }
            if (this.zzbwd != null && this.zzbwd.length > 0) {
                i = zzv;
                for (zzbut com_google_android_gms_internal_zzbut : this.zzbwd) {
                    if (com_google_android_gms_internal_zzbut != null) {
                        i += zzbum.zzc(2, com_google_android_gms_internal_zzbut);
                    }
                }
                zzv = i;
            }
            if (this.zzbwe != null && this.zzbwe.length > 0) {
                i = zzv;
                for (zzbut com_google_android_gms_internal_zzbut2 : this.zzbwe) {
                    if (com_google_android_gms_internal_zzbut2 != null) {
                        i += zzbum.zzc(3, com_google_android_gms_internal_zzbut2);
                    }
                }
                zzv = i;
            }
            if (this.zzbwf != null) {
                zzv += zzbum.zzf(4, this.zzbwf.longValue());
            }
            if (this.zzbwg != null) {
                zzv += zzbum.zzf(5, this.zzbwg.longValue());
            }
            if (this.zzbwh != null) {
                zzv += zzbum.zzf(6, this.zzbwh.longValue());
            }
            if (this.zzbwj != null) {
                zzv += zzbum.zzf(7, this.zzbwj.longValue());
            }
            if (this.zzbwk != null) {
                zzv += zzbum.zzr(8, this.zzbwk);
            }
            if (this.zzba != null) {
                zzv += zzbum.zzr(9, this.zzba);
            }
            if (this.zzbwl != null) {
                zzv += zzbum.zzr(10, this.zzbwl);
            }
            if (this.zzbwm != null) {
                zzv += zzbum.zzr(11, this.zzbwm);
            }
            if (this.zzbwn != null) {
                zzv += zzbum.zzH(12, this.zzbwn.intValue());
            }
            if (this.zzbqg != null) {
                zzv += zzbum.zzr(13, this.zzbqg);
            }
            if (this.zzaR != null) {
                zzv += zzbum.zzr(14, this.zzaR);
            }
            if (this.zzbhg != null) {
                zzv += zzbum.zzr(16, this.zzbhg);
            }
            if (this.zzbwo != null) {
                zzv += zzbum.zzf(17, this.zzbwo.longValue());
            }
            if (this.zzbwp != null) {
                zzv += zzbum.zzf(18, this.zzbwp.longValue());
            }
            if (this.zzbwq != null) {
                zzv += zzbum.zzr(19, this.zzbwq);
            }
            if (this.zzbwr != null) {
                zzv += zzbum.zzh(20, this.zzbwr.booleanValue());
            }
            if (this.zzbws != null) {
                zzv += zzbum.zzr(21, this.zzbws);
            }
            if (this.zzbwt != null) {
                zzv += zzbum.zzf(22, this.zzbwt.longValue());
            }
            if (this.zzbwu != null) {
                zzv += zzbum.zzH(23, this.zzbwu.intValue());
            }
            if (this.zzbqj != null) {
                zzv += zzbum.zzr(24, this.zzbqj);
            }
            if (this.zzbqf != null) {
                zzv += zzbum.zzr(25, this.zzbqf);
            }
            if (this.zzbwi != null) {
                zzv += zzbum.zzf(26, this.zzbwi.longValue());
            }
            if (this.zzbwv != null) {
                zzv += zzbum.zzh(28, this.zzbwv.booleanValue());
            }
            if (this.zzbww != null && this.zzbww.length > 0) {
                while (i2 < this.zzbww.length) {
                    zzbut com_google_android_gms_internal_zzbut3 = this.zzbww[i2];
                    if (com_google_android_gms_internal_zzbut3 != null) {
                        zzv += zzbum.zzc(29, com_google_android_gms_internal_zzbut3);
                    }
                    i2++;
                }
            }
            if (this.zzbqn != null) {
                zzv += zzbum.zzr(30, this.zzbqn);
            }
            if (this.zzbwx != null) {
                zzv += zzbum.zzH(31, this.zzbwx.intValue());
            }
            if (this.zzbwy != null) {
                zzv += zzbum.zzH(32, this.zzbwy.intValue());
            }
            if (this.zzbwz != null) {
                zzv += zzbum.zzH(33, this.zzbwz.intValue());
            }
            if (this.zzbwA != null) {
                zzv += zzbum.zzr(34, this.zzbwA);
            }
            return this.zzbwB != null ? zzv + zzbum.zzf(35, this.zzbwB.longValue()) : zzv;
        }
    }

    public static final class zzf extends zzbut {
        public long[] zzbwC;
        public long[] zzbwD;

        public zzf() {
            zzMH();
        }

        public boolean equals(Object obj) {
            if (obj == this) {
                return true;
            }
            if (!(obj instanceof zzf)) {
                return false;
            }
            zzf com_google_android_gms_internal_zzauh_zzf = (zzf) obj;
            return !zzbur.equals(this.zzbwC, com_google_android_gms_internal_zzauh_zzf.zzbwC) ? false : zzbur.equals(this.zzbwD, com_google_android_gms_internal_zzauh_zzf.zzbwD);
        }

        public int hashCode() {
            return ((((getClass().getName().hashCode() + 527) * 31) + zzbur.hashCode(this.zzbwC)) * 31) + zzbur.hashCode(this.zzbwD);
        }

        public zzf zzMH() {
            this.zzbwC = zzbuw.zzcsj;
            this.zzbwD = zzbuw.zzcsj;
            this.zzcsg = -1;
            return this;
        }

        public zzf zzU(zzbul com_google_android_gms_internal_zzbul) throws IOException {
            while (true) {
                int zzacu = com_google_android_gms_internal_zzbul.zzacu();
                int zzc;
                Object obj;
                int zzqj;
                Object obj2;
                switch (zzacu) {
                    case 0:
                        break;
                    case 8:
                        zzc = zzbuw.zzc(com_google_android_gms_internal_zzbul, 8);
                        zzacu = this.zzbwC == null ? 0 : this.zzbwC.length;
                        obj = new long[(zzc + zzacu)];
                        if (zzacu != 0) {
                            System.arraycopy(this.zzbwC, 0, obj, 0, zzacu);
                        }
                        while (zzacu < obj.length - 1) {
                            obj[zzacu] = com_google_android_gms_internal_zzbul.zzacw();
                            com_google_android_gms_internal_zzbul.zzacu();
                            zzacu++;
                        }
                        obj[zzacu] = com_google_android_gms_internal_zzbul.zzacw();
                        this.zzbwC = obj;
                        continue;
                    case 10:
                        zzqj = com_google_android_gms_internal_zzbul.zzqj(com_google_android_gms_internal_zzbul.zzacD());
                        zzc = com_google_android_gms_internal_zzbul.getPosition();
                        zzacu = 0;
                        while (com_google_android_gms_internal_zzbul.zzacI() > 0) {
                            com_google_android_gms_internal_zzbul.zzacw();
                            zzacu++;
                        }
                        com_google_android_gms_internal_zzbul.zzql(zzc);
                        zzc = this.zzbwC == null ? 0 : this.zzbwC.length;
                        obj2 = new long[(zzacu + zzc)];
                        if (zzc != 0) {
                            System.arraycopy(this.zzbwC, 0, obj2, 0, zzc);
                        }
                        while (zzc < obj2.length) {
                            obj2[zzc] = com_google_android_gms_internal_zzbul.zzacw();
                            zzc++;
                        }
                        this.zzbwC = obj2;
                        com_google_android_gms_internal_zzbul.zzqk(zzqj);
                        continue;
                    case 16:
                        zzc = zzbuw.zzc(com_google_android_gms_internal_zzbul, 16);
                        zzacu = this.zzbwD == null ? 0 : this.zzbwD.length;
                        obj = new long[(zzc + zzacu)];
                        if (zzacu != 0) {
                            System.arraycopy(this.zzbwD, 0, obj, 0, zzacu);
                        }
                        while (zzacu < obj.length - 1) {
                            obj[zzacu] = com_google_android_gms_internal_zzbul.zzacw();
                            com_google_android_gms_internal_zzbul.zzacu();
                            zzacu++;
                        }
                        obj[zzacu] = com_google_android_gms_internal_zzbul.zzacw();
                        this.zzbwD = obj;
                        continue;
                    case 18:
                        zzqj = com_google_android_gms_internal_zzbul.zzqj(com_google_android_gms_internal_zzbul.zzacD());
                        zzc = com_google_android_gms_internal_zzbul.getPosition();
                        zzacu = 0;
                        while (com_google_android_gms_internal_zzbul.zzacI() > 0) {
                            com_google_android_gms_internal_zzbul.zzacw();
                            zzacu++;
                        }
                        com_google_android_gms_internal_zzbul.zzql(zzc);
                        zzc = this.zzbwD == null ? 0 : this.zzbwD.length;
                        obj2 = new long[(zzacu + zzc)];
                        if (zzc != 0) {
                            System.arraycopy(this.zzbwD, 0, obj2, 0, zzc);
                        }
                        while (zzc < obj2.length) {
                            obj2[zzc] = com_google_android_gms_internal_zzbul.zzacw();
                            zzc++;
                        }
                        this.zzbwD = obj2;
                        com_google_android_gms_internal_zzbul.zzqk(zzqj);
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

        public void zza(zzbum com_google_android_gms_internal_zzbum) throws IOException {
            int i = 0;
            if (this.zzbwC != null && this.zzbwC.length > 0) {
                for (long zza : this.zzbwC) {
                    com_google_android_gms_internal_zzbum.zza(1, zza);
                }
            }
            if (this.zzbwD != null && this.zzbwD.length > 0) {
                while (i < this.zzbwD.length) {
                    com_google_android_gms_internal_zzbum.zza(2, this.zzbwD[i]);
                    i++;
                }
            }
            super.zza(com_google_android_gms_internal_zzbum);
        }

        public /* synthetic */ zzbut zzb(zzbul com_google_android_gms_internal_zzbul) throws IOException {
            return zzU(com_google_android_gms_internal_zzbul);
        }

        protected int zzv() {
            int i;
            int i2;
            int i3 = 0;
            int zzv = super.zzv();
            if (this.zzbwC == null || this.zzbwC.length <= 0) {
                i = zzv;
            } else {
                i2 = 0;
                for (long zzba : this.zzbwC) {
                    i2 += zzbum.zzba(zzba);
                }
                i = (zzv + i2) + (this.zzbwC.length * 1);
            }
            if (this.zzbwD == null || this.zzbwD.length <= 0) {
                return i;
            }
            i2 = 0;
            while (i3 < this.zzbwD.length) {
                i2 += zzbum.zzba(this.zzbwD[i3]);
                i3++;
            }
            return (i + i2) + (this.zzbwD.length * 1);
        }
    }

    public static final class zzg extends zzbut {
        private static volatile zzg[] zzbwE;
        public String name;
        public String zzaFy;
        public Long zzbvZ;
        public Float zzbvb;
        public Double zzbvc;
        public Long zzbwF;

        public zzg() {
            zzMJ();
        }

        public static zzg[] zzMI() {
            if (zzbwE == null) {
                synchronized (zzbur.zzcsf) {
                    if (zzbwE == null) {
                        zzbwE = new zzg[0];
                    }
                }
            }
            return zzbwE;
        }

        public boolean equals(Object obj) {
            if (obj == this) {
                return true;
            }
            if (!(obj instanceof zzg)) {
                return false;
            }
            zzg com_google_android_gms_internal_zzauh_zzg = (zzg) obj;
            if (this.zzbwF == null) {
                if (com_google_android_gms_internal_zzauh_zzg.zzbwF != null) {
                    return false;
                }
            } else if (!this.zzbwF.equals(com_google_android_gms_internal_zzauh_zzg.zzbwF)) {
                return false;
            }
            if (this.name == null) {
                if (com_google_android_gms_internal_zzauh_zzg.name != null) {
                    return false;
                }
            } else if (!this.name.equals(com_google_android_gms_internal_zzauh_zzg.name)) {
                return false;
            }
            if (this.zzaFy == null) {
                if (com_google_android_gms_internal_zzauh_zzg.zzaFy != null) {
                    return false;
                }
            } else if (!this.zzaFy.equals(com_google_android_gms_internal_zzauh_zzg.zzaFy)) {
                return false;
            }
            if (this.zzbvZ == null) {
                if (com_google_android_gms_internal_zzauh_zzg.zzbvZ != null) {
                    return false;
                }
            } else if (!this.zzbvZ.equals(com_google_android_gms_internal_zzauh_zzg.zzbvZ)) {
                return false;
            }
            if (this.zzbvb == null) {
                if (com_google_android_gms_internal_zzauh_zzg.zzbvb != null) {
                    return false;
                }
            } else if (!this.zzbvb.equals(com_google_android_gms_internal_zzauh_zzg.zzbvb)) {
                return false;
            }
            return this.zzbvc == null ? com_google_android_gms_internal_zzauh_zzg.zzbvc == null : this.zzbvc.equals(com_google_android_gms_internal_zzauh_zzg.zzbvc);
        }

        public int hashCode() {
            int i = 0;
            int hashCode = ((this.zzbvb == null ? 0 : this.zzbvb.hashCode()) + (((this.zzbvZ == null ? 0 : this.zzbvZ.hashCode()) + (((this.zzaFy == null ? 0 : this.zzaFy.hashCode()) + (((this.name == null ? 0 : this.name.hashCode()) + (((this.zzbwF == null ? 0 : this.zzbwF.hashCode()) + ((getClass().getName().hashCode() + 527) * 31)) * 31)) * 31)) * 31)) * 31)) * 31;
            if (this.zzbvc != null) {
                i = this.zzbvc.hashCode();
            }
            return hashCode + i;
        }

        public zzg zzMJ() {
            this.zzbwF = null;
            this.name = null;
            this.zzaFy = null;
            this.zzbvZ = null;
            this.zzbvb = null;
            this.zzbvc = null;
            this.zzcsg = -1;
            return this;
        }

        public zzg zzV(zzbul com_google_android_gms_internal_zzbul) throws IOException {
            while (true) {
                int zzacu = com_google_android_gms_internal_zzbul.zzacu();
                switch (zzacu) {
                    case 0:
                        break;
                    case 8:
                        this.zzbwF = Long.valueOf(com_google_android_gms_internal_zzbul.zzacx());
                        continue;
                    case 18:
                        this.name = com_google_android_gms_internal_zzbul.readString();
                        continue;
                    case NalUnitTypes.NAL_TYPE_RSV_VCL26 /*26*/:
                        this.zzaFy = com_google_android_gms_internal_zzbul.readString();
                        continue;
                    case 32:
                        this.zzbvZ = Long.valueOf(com_google_android_gms_internal_zzbul.zzacx());
                        continue;
                    case MotionEventCompat.AXIS_GENERIC_14 /*45*/:
                        this.zzbvb = Float.valueOf(com_google_android_gms_internal_zzbul.readFloat());
                        continue;
                    case 49:
                        this.zzbvc = Double.valueOf(com_google_android_gms_internal_zzbul.readDouble());
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

        public void zza(zzbum com_google_android_gms_internal_zzbum) throws IOException {
            if (this.zzbwF != null) {
                com_google_android_gms_internal_zzbum.zzb(1, this.zzbwF.longValue());
            }
            if (this.name != null) {
                com_google_android_gms_internal_zzbum.zzq(2, this.name);
            }
            if (this.zzaFy != null) {
                com_google_android_gms_internal_zzbum.zzq(3, this.zzaFy);
            }
            if (this.zzbvZ != null) {
                com_google_android_gms_internal_zzbum.zzb(4, this.zzbvZ.longValue());
            }
            if (this.zzbvb != null) {
                com_google_android_gms_internal_zzbum.zzc(5, this.zzbvb.floatValue());
            }
            if (this.zzbvc != null) {
                com_google_android_gms_internal_zzbum.zza(6, this.zzbvc.doubleValue());
            }
            super.zza(com_google_android_gms_internal_zzbum);
        }

        public /* synthetic */ zzbut zzb(zzbul com_google_android_gms_internal_zzbul) throws IOException {
            return zzV(com_google_android_gms_internal_zzbul);
        }

        protected int zzv() {
            int zzv = super.zzv();
            if (this.zzbwF != null) {
                zzv += zzbum.zzf(1, this.zzbwF.longValue());
            }
            if (this.name != null) {
                zzv += zzbum.zzr(2, this.name);
            }
            if (this.zzaFy != null) {
                zzv += zzbum.zzr(3, this.zzaFy);
            }
            if (this.zzbvZ != null) {
                zzv += zzbum.zzf(4, this.zzbvZ.longValue());
            }
            if (this.zzbvb != null) {
                zzv += zzbum.zzd(5, this.zzbvb.floatValue());
            }
            return this.zzbvc != null ? zzv + zzbum.zzb(6, this.zzbvc.doubleValue()) : zzv;
        }
    }
}
