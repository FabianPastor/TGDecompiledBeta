package com.google.android.gms.internal;

import android.support.v4.view.MotionEventCompat;
import com.googlecode.mp4parser.authoring.tracks.h265.NalUnitTypes;
import java.io.IOException;

public interface zzwc {

    public static final class zza extends zzasa {
        private static volatile zza[] awI;
        public Integer avZ;
        public zzf awJ;
        public zzf awK;
        public Boolean awL;

        public zza() {
            zzbzx();
        }

        public static zza[] zzbzw() {
            if (awI == null) {
                synchronized (zzary.btO) {
                    if (awI == null) {
                        awI = new zza[0];
                    }
                }
            }
            return awI;
        }

        public boolean equals(Object obj) {
            if (obj == this) {
                return true;
            }
            if (!(obj instanceof zza)) {
                return false;
            }
            zza com_google_android_gms_internal_zzwc_zza = (zza) obj;
            if (this.avZ == null) {
                if (com_google_android_gms_internal_zzwc_zza.avZ != null) {
                    return false;
                }
            } else if (!this.avZ.equals(com_google_android_gms_internal_zzwc_zza.avZ)) {
                return false;
            }
            if (this.awJ == null) {
                if (com_google_android_gms_internal_zzwc_zza.awJ != null) {
                    return false;
                }
            } else if (!this.awJ.equals(com_google_android_gms_internal_zzwc_zza.awJ)) {
                return false;
            }
            if (this.awK == null) {
                if (com_google_android_gms_internal_zzwc_zza.awK != null) {
                    return false;
                }
            } else if (!this.awK.equals(com_google_android_gms_internal_zzwc_zza.awK)) {
                return false;
            }
            return this.awL == null ? com_google_android_gms_internal_zzwc_zza.awL == null : this.awL.equals(com_google_android_gms_internal_zzwc_zza.awL);
        }

        public int hashCode() {
            int i = 0;
            int hashCode = ((this.awK == null ? 0 : this.awK.hashCode()) + (((this.awJ == null ? 0 : this.awJ.hashCode()) + (((this.avZ == null ? 0 : this.avZ.hashCode()) + ((getClass().getName().hashCode() + 527) * 31)) * 31)) * 31)) * 31;
            if (this.awL != null) {
                i = this.awL.hashCode();
            }
            return hashCode + i;
        }

        public void zza(zzart com_google_android_gms_internal_zzart) throws IOException {
            if (this.avZ != null) {
                com_google_android_gms_internal_zzart.zzaf(1, this.avZ.intValue());
            }
            if (this.awJ != null) {
                com_google_android_gms_internal_zzart.zza(2, this.awJ);
            }
            if (this.awK != null) {
                com_google_android_gms_internal_zzart.zza(3, this.awK);
            }
            if (this.awL != null) {
                com_google_android_gms_internal_zzart.zzg(4, this.awL.booleanValue());
            }
            super.zza(com_google_android_gms_internal_zzart);
        }

        public zza zzap(zzars com_google_android_gms_internal_zzars) throws IOException {
            while (true) {
                int bU = com_google_android_gms_internal_zzars.bU();
                switch (bU) {
                    case 0:
                        break;
                    case 8:
                        this.avZ = Integer.valueOf(com_google_android_gms_internal_zzars.bY());
                        continue;
                    case 18:
                        if (this.awJ == null) {
                            this.awJ = new zzf();
                        }
                        com_google_android_gms_internal_zzars.zza(this.awJ);
                        continue;
                    case NalUnitTypes.NAL_TYPE_RSV_VCL26 /*26*/:
                        if (this.awK == null) {
                            this.awK = new zzf();
                        }
                        com_google_android_gms_internal_zzars.zza(this.awK);
                        continue;
                    case 32:
                        this.awL = Boolean.valueOf(com_google_android_gms_internal_zzars.ca());
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
            return zzap(com_google_android_gms_internal_zzars);
        }

        public zza zzbzx() {
            this.avZ = null;
            this.awJ = null;
            this.awK = null;
            this.awL = null;
            this.btP = -1;
            return this;
        }

        protected int zzx() {
            int zzx = super.zzx();
            if (this.avZ != null) {
                zzx += zzart.zzah(1, this.avZ.intValue());
            }
            if (this.awJ != null) {
                zzx += zzart.zzc(2, this.awJ);
            }
            if (this.awK != null) {
                zzx += zzart.zzc(3, this.awK);
            }
            return this.awL != null ? zzx + zzart.zzh(4, this.awL.booleanValue()) : zzx;
        }
    }

    public static final class zzb extends zzasa {
        private static volatile zzb[] awM;
        public zzc[] awN;
        public Long awO;
        public Long awP;
        public Integer count;
        public String name;

        public zzb() {
            zzbzz();
        }

        public static zzb[] zzbzy() {
            if (awM == null) {
                synchronized (zzary.btO) {
                    if (awM == null) {
                        awM = new zzb[0];
                    }
                }
            }
            return awM;
        }

        public boolean equals(Object obj) {
            if (obj == this) {
                return true;
            }
            if (!(obj instanceof zzb)) {
                return false;
            }
            zzb com_google_android_gms_internal_zzwc_zzb = (zzb) obj;
            if (!zzary.equals(this.awN, com_google_android_gms_internal_zzwc_zzb.awN)) {
                return false;
            }
            if (this.name == null) {
                if (com_google_android_gms_internal_zzwc_zzb.name != null) {
                    return false;
                }
            } else if (!this.name.equals(com_google_android_gms_internal_zzwc_zzb.name)) {
                return false;
            }
            if (this.awO == null) {
                if (com_google_android_gms_internal_zzwc_zzb.awO != null) {
                    return false;
                }
            } else if (!this.awO.equals(com_google_android_gms_internal_zzwc_zzb.awO)) {
                return false;
            }
            if (this.awP == null) {
                if (com_google_android_gms_internal_zzwc_zzb.awP != null) {
                    return false;
                }
            } else if (!this.awP.equals(com_google_android_gms_internal_zzwc_zzb.awP)) {
                return false;
            }
            return this.count == null ? com_google_android_gms_internal_zzwc_zzb.count == null : this.count.equals(com_google_android_gms_internal_zzwc_zzb.count);
        }

        public int hashCode() {
            int i = 0;
            int hashCode = ((this.awP == null ? 0 : this.awP.hashCode()) + (((this.awO == null ? 0 : this.awO.hashCode()) + (((this.name == null ? 0 : this.name.hashCode()) + ((((getClass().getName().hashCode() + 527) * 31) + zzary.hashCode(this.awN)) * 31)) * 31)) * 31)) * 31;
            if (this.count != null) {
                i = this.count.hashCode();
            }
            return hashCode + i;
        }

        public void zza(zzart com_google_android_gms_internal_zzart) throws IOException {
            if (this.awN != null && this.awN.length > 0) {
                for (zzasa com_google_android_gms_internal_zzasa : this.awN) {
                    if (com_google_android_gms_internal_zzasa != null) {
                        com_google_android_gms_internal_zzart.zza(1, com_google_android_gms_internal_zzasa);
                    }
                }
            }
            if (this.name != null) {
                com_google_android_gms_internal_zzart.zzq(2, this.name);
            }
            if (this.awO != null) {
                com_google_android_gms_internal_zzart.zzb(3, this.awO.longValue());
            }
            if (this.awP != null) {
                com_google_android_gms_internal_zzart.zzb(4, this.awP.longValue());
            }
            if (this.count != null) {
                com_google_android_gms_internal_zzart.zzaf(5, this.count.intValue());
            }
            super.zza(com_google_android_gms_internal_zzart);
        }

        public zzb zzaq(zzars com_google_android_gms_internal_zzars) throws IOException {
            while (true) {
                int bU = com_google_android_gms_internal_zzars.bU();
                switch (bU) {
                    case 0:
                        break;
                    case 10:
                        int zzc = zzasd.zzc(com_google_android_gms_internal_zzars, 10);
                        bU = this.awN == null ? 0 : this.awN.length;
                        Object obj = new zzc[(zzc + bU)];
                        if (bU != 0) {
                            System.arraycopy(this.awN, 0, obj, 0, bU);
                        }
                        while (bU < obj.length - 1) {
                            obj[bU] = new zzc();
                            com_google_android_gms_internal_zzars.zza(obj[bU]);
                            com_google_android_gms_internal_zzars.bU();
                            bU++;
                        }
                        obj[bU] = new zzc();
                        com_google_android_gms_internal_zzars.zza(obj[bU]);
                        this.awN = obj;
                        continue;
                    case 18:
                        this.name = com_google_android_gms_internal_zzars.readString();
                        continue;
                    case 24:
                        this.awO = Long.valueOf(com_google_android_gms_internal_zzars.bX());
                        continue;
                    case 32:
                        this.awP = Long.valueOf(com_google_android_gms_internal_zzars.bX());
                        continue;
                    case MotionEventCompat.AXIS_GENERIC_9 /*40*/:
                        this.count = Integer.valueOf(com_google_android_gms_internal_zzars.bY());
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
            return zzaq(com_google_android_gms_internal_zzars);
        }

        public zzb zzbzz() {
            this.awN = zzc.zzcaa();
            this.name = null;
            this.awO = null;
            this.awP = null;
            this.count = null;
            this.btP = -1;
            return this;
        }

        protected int zzx() {
            int zzx = super.zzx();
            if (this.awN != null && this.awN.length > 0) {
                for (zzasa com_google_android_gms_internal_zzasa : this.awN) {
                    if (com_google_android_gms_internal_zzasa != null) {
                        zzx += zzart.zzc(1, com_google_android_gms_internal_zzasa);
                    }
                }
            }
            if (this.name != null) {
                zzx += zzart.zzr(2, this.name);
            }
            if (this.awO != null) {
                zzx += zzart.zzf(3, this.awO.longValue());
            }
            if (this.awP != null) {
                zzx += zzart.zzf(4, this.awP.longValue());
            }
            return this.count != null ? zzx + zzart.zzah(5, this.count.intValue()) : zzx;
        }
    }

    public static final class zzc extends zzasa {
        private static volatile zzc[] awQ;
        public String Fe;
        public Float avV;
        public Double avW;
        public Long awR;
        public String name;

        public zzc() {
            zzcab();
        }

        public static zzc[] zzcaa() {
            if (awQ == null) {
                synchronized (zzary.btO) {
                    if (awQ == null) {
                        awQ = new zzc[0];
                    }
                }
            }
            return awQ;
        }

        public boolean equals(Object obj) {
            if (obj == this) {
                return true;
            }
            if (!(obj instanceof zzc)) {
                return false;
            }
            zzc com_google_android_gms_internal_zzwc_zzc = (zzc) obj;
            if (this.name == null) {
                if (com_google_android_gms_internal_zzwc_zzc.name != null) {
                    return false;
                }
            } else if (!this.name.equals(com_google_android_gms_internal_zzwc_zzc.name)) {
                return false;
            }
            if (this.Fe == null) {
                if (com_google_android_gms_internal_zzwc_zzc.Fe != null) {
                    return false;
                }
            } else if (!this.Fe.equals(com_google_android_gms_internal_zzwc_zzc.Fe)) {
                return false;
            }
            if (this.awR == null) {
                if (com_google_android_gms_internal_zzwc_zzc.awR != null) {
                    return false;
                }
            } else if (!this.awR.equals(com_google_android_gms_internal_zzwc_zzc.awR)) {
                return false;
            }
            if (this.avV == null) {
                if (com_google_android_gms_internal_zzwc_zzc.avV != null) {
                    return false;
                }
            } else if (!this.avV.equals(com_google_android_gms_internal_zzwc_zzc.avV)) {
                return false;
            }
            return this.avW == null ? com_google_android_gms_internal_zzwc_zzc.avW == null : this.avW.equals(com_google_android_gms_internal_zzwc_zzc.avW);
        }

        public int hashCode() {
            int i = 0;
            int hashCode = ((this.avV == null ? 0 : this.avV.hashCode()) + (((this.awR == null ? 0 : this.awR.hashCode()) + (((this.Fe == null ? 0 : this.Fe.hashCode()) + (((this.name == null ? 0 : this.name.hashCode()) + ((getClass().getName().hashCode() + 527) * 31)) * 31)) * 31)) * 31)) * 31;
            if (this.avW != null) {
                i = this.avW.hashCode();
            }
            return hashCode + i;
        }

        public void zza(zzart com_google_android_gms_internal_zzart) throws IOException {
            if (this.name != null) {
                com_google_android_gms_internal_zzart.zzq(1, this.name);
            }
            if (this.Fe != null) {
                com_google_android_gms_internal_zzart.zzq(2, this.Fe);
            }
            if (this.awR != null) {
                com_google_android_gms_internal_zzart.zzb(3, this.awR.longValue());
            }
            if (this.avV != null) {
                com_google_android_gms_internal_zzart.zzc(4, this.avV.floatValue());
            }
            if (this.avW != null) {
                com_google_android_gms_internal_zzart.zza(5, this.avW.doubleValue());
            }
            super.zza(com_google_android_gms_internal_zzart);
        }

        public zzc zzar(zzars com_google_android_gms_internal_zzars) throws IOException {
            while (true) {
                int bU = com_google_android_gms_internal_zzars.bU();
                switch (bU) {
                    case 0:
                        break;
                    case 10:
                        this.name = com_google_android_gms_internal_zzars.readString();
                        continue;
                    case 18:
                        this.Fe = com_google_android_gms_internal_zzars.readString();
                        continue;
                    case 24:
                        this.awR = Long.valueOf(com_google_android_gms_internal_zzars.bX());
                        continue;
                    case 37:
                        this.avV = Float.valueOf(com_google_android_gms_internal_zzars.readFloat());
                        continue;
                    case 41:
                        this.avW = Double.valueOf(com_google_android_gms_internal_zzars.readDouble());
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
            return zzar(com_google_android_gms_internal_zzars);
        }

        public zzc zzcab() {
            this.name = null;
            this.Fe = null;
            this.awR = null;
            this.avV = null;
            this.avW = null;
            this.btP = -1;
            return this;
        }

        protected int zzx() {
            int zzx = super.zzx();
            if (this.name != null) {
                zzx += zzart.zzr(1, this.name);
            }
            if (this.Fe != null) {
                zzx += zzart.zzr(2, this.Fe);
            }
            if (this.awR != null) {
                zzx += zzart.zzf(3, this.awR.longValue());
            }
            if (this.avV != null) {
                zzx += zzart.zzd(4, this.avV.floatValue());
            }
            return this.avW != null ? zzx + zzart.zzb(5, this.avW.doubleValue()) : zzx;
        }
    }

    public static final class zzd extends zzasa {
        public zze[] awS;

        public zzd() {
            zzcac();
        }

        public boolean equals(Object obj) {
            if (obj == this) {
                return true;
            }
            if (!(obj instanceof zzd)) {
                return false;
            }
            return zzary.equals(this.awS, ((zzd) obj).awS);
        }

        public int hashCode() {
            return ((getClass().getName().hashCode() + 527) * 31) + zzary.hashCode(this.awS);
        }

        public void zza(zzart com_google_android_gms_internal_zzart) throws IOException {
            if (this.awS != null && this.awS.length > 0) {
                for (zzasa com_google_android_gms_internal_zzasa : this.awS) {
                    if (com_google_android_gms_internal_zzasa != null) {
                        com_google_android_gms_internal_zzart.zza(1, com_google_android_gms_internal_zzasa);
                    }
                }
            }
            super.zza(com_google_android_gms_internal_zzart);
        }

        public zzd zzas(zzars com_google_android_gms_internal_zzars) throws IOException {
            while (true) {
                int bU = com_google_android_gms_internal_zzars.bU();
                switch (bU) {
                    case 0:
                        break;
                    case 10:
                        int zzc = zzasd.zzc(com_google_android_gms_internal_zzars, 10);
                        bU = this.awS == null ? 0 : this.awS.length;
                        Object obj = new zze[(zzc + bU)];
                        if (bU != 0) {
                            System.arraycopy(this.awS, 0, obj, 0, bU);
                        }
                        while (bU < obj.length - 1) {
                            obj[bU] = new zze();
                            com_google_android_gms_internal_zzars.zza(obj[bU]);
                            com_google_android_gms_internal_zzars.bU();
                            bU++;
                        }
                        obj[bU] = new zze();
                        com_google_android_gms_internal_zzars.zza(obj[bU]);
                        this.awS = obj;
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
            return zzas(com_google_android_gms_internal_zzars);
        }

        public zzd zzcac() {
            this.awS = zze.zzcad();
            this.btP = -1;
            return this;
        }

        protected int zzx() {
            int zzx = super.zzx();
            if (this.awS != null && this.awS.length > 0) {
                for (zzasa com_google_android_gms_internal_zzasa : this.awS) {
                    if (com_google_android_gms_internal_zzasa != null) {
                        zzx += zzart.zzc(1, com_google_android_gms_internal_zzasa);
                    }
                }
            }
            return zzx;
        }
    }

    public static final class zze extends zzasa {
        private static volatile zze[] awT;
        public String aii;
        public String aqZ;
        public String ara;
        public String ard;
        public String arh;
        public Integer awU;
        public zzb[] awV;
        public zzg[] awW;
        public Long awX;
        public Long awY;
        public Long awZ;
        public Long axa;
        public Long axb;
        public String axc;
        public String axd;
        public String axe;
        public Integer axf;
        public Long axg;
        public Long axh;
        public String axi;
        public Boolean axj;
        public String axk;
        public Long axl;
        public Integer axm;
        public Boolean axn;
        public zza[] axo;
        public Integer axp;
        public Integer axq;
        public Integer axr;
        public String axs;
        public Long axt;
        public String zzcs;
        public String zzdb;

        public zze() {
            zzcae();
        }

        public static zze[] zzcad() {
            if (awT == null) {
                synchronized (zzary.btO) {
                    if (awT == null) {
                        awT = new zze[0];
                    }
                }
            }
            return awT;
        }

        public boolean equals(Object obj) {
            if (obj == this) {
                return true;
            }
            if (!(obj instanceof zze)) {
                return false;
            }
            zze com_google_android_gms_internal_zzwc_zze = (zze) obj;
            if (this.awU == null) {
                if (com_google_android_gms_internal_zzwc_zze.awU != null) {
                    return false;
                }
            } else if (!this.awU.equals(com_google_android_gms_internal_zzwc_zze.awU)) {
                return false;
            }
            if (!zzary.equals(this.awV, com_google_android_gms_internal_zzwc_zze.awV)) {
                return false;
            }
            if (!zzary.equals(this.awW, com_google_android_gms_internal_zzwc_zze.awW)) {
                return false;
            }
            if (this.awX == null) {
                if (com_google_android_gms_internal_zzwc_zze.awX != null) {
                    return false;
                }
            } else if (!this.awX.equals(com_google_android_gms_internal_zzwc_zze.awX)) {
                return false;
            }
            if (this.awY == null) {
                if (com_google_android_gms_internal_zzwc_zze.awY != null) {
                    return false;
                }
            } else if (!this.awY.equals(com_google_android_gms_internal_zzwc_zze.awY)) {
                return false;
            }
            if (this.awZ == null) {
                if (com_google_android_gms_internal_zzwc_zze.awZ != null) {
                    return false;
                }
            } else if (!this.awZ.equals(com_google_android_gms_internal_zzwc_zze.awZ)) {
                return false;
            }
            if (this.axa == null) {
                if (com_google_android_gms_internal_zzwc_zze.axa != null) {
                    return false;
                }
            } else if (!this.axa.equals(com_google_android_gms_internal_zzwc_zze.axa)) {
                return false;
            }
            if (this.axb == null) {
                if (com_google_android_gms_internal_zzwc_zze.axb != null) {
                    return false;
                }
            } else if (!this.axb.equals(com_google_android_gms_internal_zzwc_zze.axb)) {
                return false;
            }
            if (this.axc == null) {
                if (com_google_android_gms_internal_zzwc_zze.axc != null) {
                    return false;
                }
            } else if (!this.axc.equals(com_google_android_gms_internal_zzwc_zze.axc)) {
                return false;
            }
            if (this.zzdb == null) {
                if (com_google_android_gms_internal_zzwc_zze.zzdb != null) {
                    return false;
                }
            } else if (!this.zzdb.equals(com_google_android_gms_internal_zzwc_zze.zzdb)) {
                return false;
            }
            if (this.axd == null) {
                if (com_google_android_gms_internal_zzwc_zze.axd != null) {
                    return false;
                }
            } else if (!this.axd.equals(com_google_android_gms_internal_zzwc_zze.axd)) {
                return false;
            }
            if (this.axe == null) {
                if (com_google_android_gms_internal_zzwc_zze.axe != null) {
                    return false;
                }
            } else if (!this.axe.equals(com_google_android_gms_internal_zzwc_zze.axe)) {
                return false;
            }
            if (this.axf == null) {
                if (com_google_android_gms_internal_zzwc_zze.axf != null) {
                    return false;
                }
            } else if (!this.axf.equals(com_google_android_gms_internal_zzwc_zze.axf)) {
                return false;
            }
            if (this.ara == null) {
                if (com_google_android_gms_internal_zzwc_zze.ara != null) {
                    return false;
                }
            } else if (!this.ara.equals(com_google_android_gms_internal_zzwc_zze.ara)) {
                return false;
            }
            if (this.zzcs == null) {
                if (com_google_android_gms_internal_zzwc_zze.zzcs != null) {
                    return false;
                }
            } else if (!this.zzcs.equals(com_google_android_gms_internal_zzwc_zze.zzcs)) {
                return false;
            }
            if (this.aii == null) {
                if (com_google_android_gms_internal_zzwc_zze.aii != null) {
                    return false;
                }
            } else if (!this.aii.equals(com_google_android_gms_internal_zzwc_zze.aii)) {
                return false;
            }
            if (this.axg == null) {
                if (com_google_android_gms_internal_zzwc_zze.axg != null) {
                    return false;
                }
            } else if (!this.axg.equals(com_google_android_gms_internal_zzwc_zze.axg)) {
                return false;
            }
            if (this.axh == null) {
                if (com_google_android_gms_internal_zzwc_zze.axh != null) {
                    return false;
                }
            } else if (!this.axh.equals(com_google_android_gms_internal_zzwc_zze.axh)) {
                return false;
            }
            if (this.axi == null) {
                if (com_google_android_gms_internal_zzwc_zze.axi != null) {
                    return false;
                }
            } else if (!this.axi.equals(com_google_android_gms_internal_zzwc_zze.axi)) {
                return false;
            }
            if (this.axj == null) {
                if (com_google_android_gms_internal_zzwc_zze.axj != null) {
                    return false;
                }
            } else if (!this.axj.equals(com_google_android_gms_internal_zzwc_zze.axj)) {
                return false;
            }
            if (this.axk == null) {
                if (com_google_android_gms_internal_zzwc_zze.axk != null) {
                    return false;
                }
            } else if (!this.axk.equals(com_google_android_gms_internal_zzwc_zze.axk)) {
                return false;
            }
            if (this.axl == null) {
                if (com_google_android_gms_internal_zzwc_zze.axl != null) {
                    return false;
                }
            } else if (!this.axl.equals(com_google_android_gms_internal_zzwc_zze.axl)) {
                return false;
            }
            if (this.axm == null) {
                if (com_google_android_gms_internal_zzwc_zze.axm != null) {
                    return false;
                }
            } else if (!this.axm.equals(com_google_android_gms_internal_zzwc_zze.axm)) {
                return false;
            }
            if (this.ard == null) {
                if (com_google_android_gms_internal_zzwc_zze.ard != null) {
                    return false;
                }
            } else if (!this.ard.equals(com_google_android_gms_internal_zzwc_zze.ard)) {
                return false;
            }
            if (this.aqZ == null) {
                if (com_google_android_gms_internal_zzwc_zze.aqZ != null) {
                    return false;
                }
            } else if (!this.aqZ.equals(com_google_android_gms_internal_zzwc_zze.aqZ)) {
                return false;
            }
            if (this.axn == null) {
                if (com_google_android_gms_internal_zzwc_zze.axn != null) {
                    return false;
                }
            } else if (!this.axn.equals(com_google_android_gms_internal_zzwc_zze.axn)) {
                return false;
            }
            if (!zzary.equals(this.axo, com_google_android_gms_internal_zzwc_zze.axo)) {
                return false;
            }
            if (this.arh == null) {
                if (com_google_android_gms_internal_zzwc_zze.arh != null) {
                    return false;
                }
            } else if (!this.arh.equals(com_google_android_gms_internal_zzwc_zze.arh)) {
                return false;
            }
            if (this.axp == null) {
                if (com_google_android_gms_internal_zzwc_zze.axp != null) {
                    return false;
                }
            } else if (!this.axp.equals(com_google_android_gms_internal_zzwc_zze.axp)) {
                return false;
            }
            if (this.axq == null) {
                if (com_google_android_gms_internal_zzwc_zze.axq != null) {
                    return false;
                }
            } else if (!this.axq.equals(com_google_android_gms_internal_zzwc_zze.axq)) {
                return false;
            }
            if (this.axr == null) {
                if (com_google_android_gms_internal_zzwc_zze.axr != null) {
                    return false;
                }
            } else if (!this.axr.equals(com_google_android_gms_internal_zzwc_zze.axr)) {
                return false;
            }
            if (this.axs == null) {
                if (com_google_android_gms_internal_zzwc_zze.axs != null) {
                    return false;
                }
            } else if (!this.axs.equals(com_google_android_gms_internal_zzwc_zze.axs)) {
                return false;
            }
            return this.axt == null ? com_google_android_gms_internal_zzwc_zze.axt == null : this.axt.equals(com_google_android_gms_internal_zzwc_zze.axt);
        }

        public int hashCode() {
            int i = 0;
            int hashCode = ((this.axs == null ? 0 : this.axs.hashCode()) + (((this.axr == null ? 0 : this.axr.hashCode()) + (((this.axq == null ? 0 : this.axq.hashCode()) + (((this.axp == null ? 0 : this.axp.hashCode()) + (((this.arh == null ? 0 : this.arh.hashCode()) + (((((this.axn == null ? 0 : this.axn.hashCode()) + (((this.aqZ == null ? 0 : this.aqZ.hashCode()) + (((this.ard == null ? 0 : this.ard.hashCode()) + (((this.axm == null ? 0 : this.axm.hashCode()) + (((this.axl == null ? 0 : this.axl.hashCode()) + (((this.axk == null ? 0 : this.axk.hashCode()) + (((this.axj == null ? 0 : this.axj.hashCode()) + (((this.axi == null ? 0 : this.axi.hashCode()) + (((this.axh == null ? 0 : this.axh.hashCode()) + (((this.axg == null ? 0 : this.axg.hashCode()) + (((this.aii == null ? 0 : this.aii.hashCode()) + (((this.zzcs == null ? 0 : this.zzcs.hashCode()) + (((this.ara == null ? 0 : this.ara.hashCode()) + (((this.axf == null ? 0 : this.axf.hashCode()) + (((this.axe == null ? 0 : this.axe.hashCode()) + (((this.axd == null ? 0 : this.axd.hashCode()) + (((this.zzdb == null ? 0 : this.zzdb.hashCode()) + (((this.axc == null ? 0 : this.axc.hashCode()) + (((this.axb == null ? 0 : this.axb.hashCode()) + (((this.axa == null ? 0 : this.axa.hashCode()) + (((this.awZ == null ? 0 : this.awZ.hashCode()) + (((this.awY == null ? 0 : this.awY.hashCode()) + (((this.awX == null ? 0 : this.awX.hashCode()) + (((((((this.awU == null ? 0 : this.awU.hashCode()) + ((getClass().getName().hashCode() + 527) * 31)) * 31) + zzary.hashCode(this.awV)) * 31) + zzary.hashCode(this.awW)) * 31)) * 31)) * 31)) * 31)) * 31)) * 31)) * 31)) * 31)) * 31)) * 31)) * 31)) * 31)) * 31)) * 31)) * 31)) * 31)) * 31)) * 31)) * 31)) * 31)) * 31)) * 31)) * 31)) * 31) + zzary.hashCode(this.axo)) * 31)) * 31)) * 31)) * 31)) * 31)) * 31;
            if (this.axt != null) {
                i = this.axt.hashCode();
            }
            return hashCode + i;
        }

        public void zza(zzart com_google_android_gms_internal_zzart) throws IOException {
            int i = 0;
            if (this.awU != null) {
                com_google_android_gms_internal_zzart.zzaf(1, this.awU.intValue());
            }
            if (this.awV != null && this.awV.length > 0) {
                for (zzasa com_google_android_gms_internal_zzasa : this.awV) {
                    if (com_google_android_gms_internal_zzasa != null) {
                        com_google_android_gms_internal_zzart.zza(2, com_google_android_gms_internal_zzasa);
                    }
                }
            }
            if (this.awW != null && this.awW.length > 0) {
                for (zzasa com_google_android_gms_internal_zzasa2 : this.awW) {
                    if (com_google_android_gms_internal_zzasa2 != null) {
                        com_google_android_gms_internal_zzart.zza(3, com_google_android_gms_internal_zzasa2);
                    }
                }
            }
            if (this.awX != null) {
                com_google_android_gms_internal_zzart.zzb(4, this.awX.longValue());
            }
            if (this.awY != null) {
                com_google_android_gms_internal_zzart.zzb(5, this.awY.longValue());
            }
            if (this.awZ != null) {
                com_google_android_gms_internal_zzart.zzb(6, this.awZ.longValue());
            }
            if (this.axb != null) {
                com_google_android_gms_internal_zzart.zzb(7, this.axb.longValue());
            }
            if (this.axc != null) {
                com_google_android_gms_internal_zzart.zzq(8, this.axc);
            }
            if (this.zzdb != null) {
                com_google_android_gms_internal_zzart.zzq(9, this.zzdb);
            }
            if (this.axd != null) {
                com_google_android_gms_internal_zzart.zzq(10, this.axd);
            }
            if (this.axe != null) {
                com_google_android_gms_internal_zzart.zzq(11, this.axe);
            }
            if (this.axf != null) {
                com_google_android_gms_internal_zzart.zzaf(12, this.axf.intValue());
            }
            if (this.ara != null) {
                com_google_android_gms_internal_zzart.zzq(13, this.ara);
            }
            if (this.zzcs != null) {
                com_google_android_gms_internal_zzart.zzq(14, this.zzcs);
            }
            if (this.aii != null) {
                com_google_android_gms_internal_zzart.zzq(16, this.aii);
            }
            if (this.axg != null) {
                com_google_android_gms_internal_zzart.zzb(17, this.axg.longValue());
            }
            if (this.axh != null) {
                com_google_android_gms_internal_zzart.zzb(18, this.axh.longValue());
            }
            if (this.axi != null) {
                com_google_android_gms_internal_zzart.zzq(19, this.axi);
            }
            if (this.axj != null) {
                com_google_android_gms_internal_zzart.zzg(20, this.axj.booleanValue());
            }
            if (this.axk != null) {
                com_google_android_gms_internal_zzart.zzq(21, this.axk);
            }
            if (this.axl != null) {
                com_google_android_gms_internal_zzart.zzb(22, this.axl.longValue());
            }
            if (this.axm != null) {
                com_google_android_gms_internal_zzart.zzaf(23, this.axm.intValue());
            }
            if (this.ard != null) {
                com_google_android_gms_internal_zzart.zzq(24, this.ard);
            }
            if (this.aqZ != null) {
                com_google_android_gms_internal_zzart.zzq(25, this.aqZ);
            }
            if (this.axa != null) {
                com_google_android_gms_internal_zzart.zzb(26, this.axa.longValue());
            }
            if (this.axn != null) {
                com_google_android_gms_internal_zzart.zzg(28, this.axn.booleanValue());
            }
            if (this.axo != null && this.axo.length > 0) {
                while (i < this.axo.length) {
                    zzasa com_google_android_gms_internal_zzasa3 = this.axo[i];
                    if (com_google_android_gms_internal_zzasa3 != null) {
                        com_google_android_gms_internal_zzart.zza(29, com_google_android_gms_internal_zzasa3);
                    }
                    i++;
                }
            }
            if (this.arh != null) {
                com_google_android_gms_internal_zzart.zzq(30, this.arh);
            }
            if (this.axp != null) {
                com_google_android_gms_internal_zzart.zzaf(31, this.axp.intValue());
            }
            if (this.axq != null) {
                com_google_android_gms_internal_zzart.zzaf(32, this.axq.intValue());
            }
            if (this.axr != null) {
                com_google_android_gms_internal_zzart.zzaf(33, this.axr.intValue());
            }
            if (this.axs != null) {
                com_google_android_gms_internal_zzart.zzq(34, this.axs);
            }
            if (this.axt != null) {
                com_google_android_gms_internal_zzart.zzb(35, this.axt.longValue());
            }
            super.zza(com_google_android_gms_internal_zzart);
        }

        public zze zzat(zzars com_google_android_gms_internal_zzars) throws IOException {
            while (true) {
                int bU = com_google_android_gms_internal_zzars.bU();
                int zzc;
                Object obj;
                switch (bU) {
                    case 0:
                        break;
                    case 8:
                        this.awU = Integer.valueOf(com_google_android_gms_internal_zzars.bY());
                        continue;
                    case 18:
                        zzc = zzasd.zzc(com_google_android_gms_internal_zzars, 18);
                        bU = this.awV == null ? 0 : this.awV.length;
                        obj = new zzb[(zzc + bU)];
                        if (bU != 0) {
                            System.arraycopy(this.awV, 0, obj, 0, bU);
                        }
                        while (bU < obj.length - 1) {
                            obj[bU] = new zzb();
                            com_google_android_gms_internal_zzars.zza(obj[bU]);
                            com_google_android_gms_internal_zzars.bU();
                            bU++;
                        }
                        obj[bU] = new zzb();
                        com_google_android_gms_internal_zzars.zza(obj[bU]);
                        this.awV = obj;
                        continue;
                    case NalUnitTypes.NAL_TYPE_RSV_VCL26 /*26*/:
                        zzc = zzasd.zzc(com_google_android_gms_internal_zzars, 26);
                        bU = this.awW == null ? 0 : this.awW.length;
                        obj = new zzg[(zzc + bU)];
                        if (bU != 0) {
                            System.arraycopy(this.awW, 0, obj, 0, bU);
                        }
                        while (bU < obj.length - 1) {
                            obj[bU] = new zzg();
                            com_google_android_gms_internal_zzars.zza(obj[bU]);
                            com_google_android_gms_internal_zzars.bU();
                            bU++;
                        }
                        obj[bU] = new zzg();
                        com_google_android_gms_internal_zzars.zza(obj[bU]);
                        this.awW = obj;
                        continue;
                    case 32:
                        this.awX = Long.valueOf(com_google_android_gms_internal_zzars.bX());
                        continue;
                    case MotionEventCompat.AXIS_GENERIC_9 /*40*/:
                        this.awY = Long.valueOf(com_google_android_gms_internal_zzars.bX());
                        continue;
                    case 48:
                        this.awZ = Long.valueOf(com_google_android_gms_internal_zzars.bX());
                        continue;
                    case 56:
                        this.axb = Long.valueOf(com_google_android_gms_internal_zzars.bX());
                        continue;
                    case 66:
                        this.axc = com_google_android_gms_internal_zzars.readString();
                        continue;
                    case 74:
                        this.zzdb = com_google_android_gms_internal_zzars.readString();
                        continue;
                    case 82:
                        this.axd = com_google_android_gms_internal_zzars.readString();
                        continue;
                    case 90:
                        this.axe = com_google_android_gms_internal_zzars.readString();
                        continue;
                    case 96:
                        this.axf = Integer.valueOf(com_google_android_gms_internal_zzars.bY());
                        continue;
                    case 106:
                        this.ara = com_google_android_gms_internal_zzars.readString();
                        continue;
                    case 114:
                        this.zzcs = com_google_android_gms_internal_zzars.readString();
                        continue;
                    case 130:
                        this.aii = com_google_android_gms_internal_zzars.readString();
                        continue;
                    case 136:
                        this.axg = Long.valueOf(com_google_android_gms_internal_zzars.bX());
                        continue;
                    case 144:
                        this.axh = Long.valueOf(com_google_android_gms_internal_zzars.bX());
                        continue;
                    case 154:
                        this.axi = com_google_android_gms_internal_zzars.readString();
                        continue;
                    case 160:
                        this.axj = Boolean.valueOf(com_google_android_gms_internal_zzars.ca());
                        continue;
                    case 170:
                        this.axk = com_google_android_gms_internal_zzars.readString();
                        continue;
                    case 176:
                        this.axl = Long.valueOf(com_google_android_gms_internal_zzars.bX());
                        continue;
                    case 184:
                        this.axm = Integer.valueOf(com_google_android_gms_internal_zzars.bY());
                        continue;
                    case 194:
                        this.ard = com_google_android_gms_internal_zzars.readString();
                        continue;
                    case 202:
                        this.aqZ = com_google_android_gms_internal_zzars.readString();
                        continue;
                    case 208:
                        this.axa = Long.valueOf(com_google_android_gms_internal_zzars.bX());
                        continue;
                    case 224:
                        this.axn = Boolean.valueOf(com_google_android_gms_internal_zzars.ca());
                        continue;
                    case 234:
                        zzc = zzasd.zzc(com_google_android_gms_internal_zzars, 234);
                        bU = this.axo == null ? 0 : this.axo.length;
                        obj = new zza[(zzc + bU)];
                        if (bU != 0) {
                            System.arraycopy(this.axo, 0, obj, 0, bU);
                        }
                        while (bU < obj.length - 1) {
                            obj[bU] = new zza();
                            com_google_android_gms_internal_zzars.zza(obj[bU]);
                            com_google_android_gms_internal_zzars.bU();
                            bU++;
                        }
                        obj[bU] = new zza();
                        com_google_android_gms_internal_zzars.zza(obj[bU]);
                        this.axo = obj;
                        continue;
                    case 242:
                        this.arh = com_google_android_gms_internal_zzars.readString();
                        continue;
                    case 248:
                        this.axp = Integer.valueOf(com_google_android_gms_internal_zzars.bY());
                        continue;
                    case 256:
                        this.axq = Integer.valueOf(com_google_android_gms_internal_zzars.bY());
                        continue;
                    case 264:
                        this.axr = Integer.valueOf(com_google_android_gms_internal_zzars.bY());
                        continue;
                    case 274:
                        this.axs = com_google_android_gms_internal_zzars.readString();
                        continue;
                    case 280:
                        this.axt = Long.valueOf(com_google_android_gms_internal_zzars.bX());
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
            return zzat(com_google_android_gms_internal_zzars);
        }

        public zze zzcae() {
            this.awU = null;
            this.awV = zzb.zzbzy();
            this.awW = zzg.zzcag();
            this.awX = null;
            this.awY = null;
            this.awZ = null;
            this.axa = null;
            this.axb = null;
            this.axc = null;
            this.zzdb = null;
            this.axd = null;
            this.axe = null;
            this.axf = null;
            this.ara = null;
            this.zzcs = null;
            this.aii = null;
            this.axg = null;
            this.axh = null;
            this.axi = null;
            this.axj = null;
            this.axk = null;
            this.axl = null;
            this.axm = null;
            this.ard = null;
            this.aqZ = null;
            this.axn = null;
            this.axo = zza.zzbzw();
            this.arh = null;
            this.axp = null;
            this.axq = null;
            this.axr = null;
            this.axs = null;
            this.axt = null;
            this.btP = -1;
            return this;
        }

        protected int zzx() {
            int i;
            int i2 = 0;
            int zzx = super.zzx();
            if (this.awU != null) {
                zzx += zzart.zzah(1, this.awU.intValue());
            }
            if (this.awV != null && this.awV.length > 0) {
                i = zzx;
                for (zzasa com_google_android_gms_internal_zzasa : this.awV) {
                    if (com_google_android_gms_internal_zzasa != null) {
                        i += zzart.zzc(2, com_google_android_gms_internal_zzasa);
                    }
                }
                zzx = i;
            }
            if (this.awW != null && this.awW.length > 0) {
                i = zzx;
                for (zzasa com_google_android_gms_internal_zzasa2 : this.awW) {
                    if (com_google_android_gms_internal_zzasa2 != null) {
                        i += zzart.zzc(3, com_google_android_gms_internal_zzasa2);
                    }
                }
                zzx = i;
            }
            if (this.awX != null) {
                zzx += zzart.zzf(4, this.awX.longValue());
            }
            if (this.awY != null) {
                zzx += zzart.zzf(5, this.awY.longValue());
            }
            if (this.awZ != null) {
                zzx += zzart.zzf(6, this.awZ.longValue());
            }
            if (this.axb != null) {
                zzx += zzart.zzf(7, this.axb.longValue());
            }
            if (this.axc != null) {
                zzx += zzart.zzr(8, this.axc);
            }
            if (this.zzdb != null) {
                zzx += zzart.zzr(9, this.zzdb);
            }
            if (this.axd != null) {
                zzx += zzart.zzr(10, this.axd);
            }
            if (this.axe != null) {
                zzx += zzart.zzr(11, this.axe);
            }
            if (this.axf != null) {
                zzx += zzart.zzah(12, this.axf.intValue());
            }
            if (this.ara != null) {
                zzx += zzart.zzr(13, this.ara);
            }
            if (this.zzcs != null) {
                zzx += zzart.zzr(14, this.zzcs);
            }
            if (this.aii != null) {
                zzx += zzart.zzr(16, this.aii);
            }
            if (this.axg != null) {
                zzx += zzart.zzf(17, this.axg.longValue());
            }
            if (this.axh != null) {
                zzx += zzart.zzf(18, this.axh.longValue());
            }
            if (this.axi != null) {
                zzx += zzart.zzr(19, this.axi);
            }
            if (this.axj != null) {
                zzx += zzart.zzh(20, this.axj.booleanValue());
            }
            if (this.axk != null) {
                zzx += zzart.zzr(21, this.axk);
            }
            if (this.axl != null) {
                zzx += zzart.zzf(22, this.axl.longValue());
            }
            if (this.axm != null) {
                zzx += zzart.zzah(23, this.axm.intValue());
            }
            if (this.ard != null) {
                zzx += zzart.zzr(24, this.ard);
            }
            if (this.aqZ != null) {
                zzx += zzart.zzr(25, this.aqZ);
            }
            if (this.axa != null) {
                zzx += zzart.zzf(26, this.axa.longValue());
            }
            if (this.axn != null) {
                zzx += zzart.zzh(28, this.axn.booleanValue());
            }
            if (this.axo != null && this.axo.length > 0) {
                while (i2 < this.axo.length) {
                    zzasa com_google_android_gms_internal_zzasa3 = this.axo[i2];
                    if (com_google_android_gms_internal_zzasa3 != null) {
                        zzx += zzart.zzc(29, com_google_android_gms_internal_zzasa3);
                    }
                    i2++;
                }
            }
            if (this.arh != null) {
                zzx += zzart.zzr(30, this.arh);
            }
            if (this.axp != null) {
                zzx += zzart.zzah(31, this.axp.intValue());
            }
            if (this.axq != null) {
                zzx += zzart.zzah(32, this.axq.intValue());
            }
            if (this.axr != null) {
                zzx += zzart.zzah(33, this.axr.intValue());
            }
            if (this.axs != null) {
                zzx += zzart.zzr(34, this.axs);
            }
            return this.axt != null ? zzx + zzart.zzf(35, this.axt.longValue()) : zzx;
        }
    }

    public static final class zzf extends zzasa {
        public long[] axu;
        public long[] axv;

        public zzf() {
            zzcaf();
        }

        public boolean equals(Object obj) {
            if (obj == this) {
                return true;
            }
            if (!(obj instanceof zzf)) {
                return false;
            }
            zzf com_google_android_gms_internal_zzwc_zzf = (zzf) obj;
            return !zzary.equals(this.axu, com_google_android_gms_internal_zzwc_zzf.axu) ? false : zzary.equals(this.axv, com_google_android_gms_internal_zzwc_zzf.axv);
        }

        public int hashCode() {
            return ((((getClass().getName().hashCode() + 527) * 31) + zzary.hashCode(this.axu)) * 31) + zzary.hashCode(this.axv);
        }

        public void zza(zzart com_google_android_gms_internal_zzart) throws IOException {
            int i = 0;
            if (this.axu != null && this.axu.length > 0) {
                for (long zza : this.axu) {
                    com_google_android_gms_internal_zzart.zza(1, zza);
                }
            }
            if (this.axv != null && this.axv.length > 0) {
                while (i < this.axv.length) {
                    com_google_android_gms_internal_zzart.zza(2, this.axv[i]);
                    i++;
                }
            }
            super.zza(com_google_android_gms_internal_zzart);
        }

        public zzf zzau(zzars com_google_android_gms_internal_zzars) throws IOException {
            while (true) {
                int bU = com_google_android_gms_internal_zzars.bU();
                int zzc;
                Object obj;
                int zzagt;
                Object obj2;
                switch (bU) {
                    case 0:
                        break;
                    case 8:
                        zzc = zzasd.zzc(com_google_android_gms_internal_zzars, 8);
                        bU = this.axu == null ? 0 : this.axu.length;
                        obj = new long[(zzc + bU)];
                        if (bU != 0) {
                            System.arraycopy(this.axu, 0, obj, 0, bU);
                        }
                        while (bU < obj.length - 1) {
                            obj[bU] = com_google_android_gms_internal_zzars.bW();
                            com_google_android_gms_internal_zzars.bU();
                            bU++;
                        }
                        obj[bU] = com_google_android_gms_internal_zzars.bW();
                        this.axu = obj;
                        continue;
                    case 10:
                        zzagt = com_google_android_gms_internal_zzars.zzagt(com_google_android_gms_internal_zzars.cd());
                        zzc = com_google_android_gms_internal_zzars.getPosition();
                        bU = 0;
                        while (com_google_android_gms_internal_zzars.ci() > 0) {
                            com_google_android_gms_internal_zzars.bW();
                            bU++;
                        }
                        com_google_android_gms_internal_zzars.zzagv(zzc);
                        zzc = this.axu == null ? 0 : this.axu.length;
                        obj2 = new long[(bU + zzc)];
                        if (zzc != 0) {
                            System.arraycopy(this.axu, 0, obj2, 0, zzc);
                        }
                        while (zzc < obj2.length) {
                            obj2[zzc] = com_google_android_gms_internal_zzars.bW();
                            zzc++;
                        }
                        this.axu = obj2;
                        com_google_android_gms_internal_zzars.zzagu(zzagt);
                        continue;
                    case 16:
                        zzc = zzasd.zzc(com_google_android_gms_internal_zzars, 16);
                        bU = this.axv == null ? 0 : this.axv.length;
                        obj = new long[(zzc + bU)];
                        if (bU != 0) {
                            System.arraycopy(this.axv, 0, obj, 0, bU);
                        }
                        while (bU < obj.length - 1) {
                            obj[bU] = com_google_android_gms_internal_zzars.bW();
                            com_google_android_gms_internal_zzars.bU();
                            bU++;
                        }
                        obj[bU] = com_google_android_gms_internal_zzars.bW();
                        this.axv = obj;
                        continue;
                    case 18:
                        zzagt = com_google_android_gms_internal_zzars.zzagt(com_google_android_gms_internal_zzars.cd());
                        zzc = com_google_android_gms_internal_zzars.getPosition();
                        bU = 0;
                        while (com_google_android_gms_internal_zzars.ci() > 0) {
                            com_google_android_gms_internal_zzars.bW();
                            bU++;
                        }
                        com_google_android_gms_internal_zzars.zzagv(zzc);
                        zzc = this.axv == null ? 0 : this.axv.length;
                        obj2 = new long[(bU + zzc)];
                        if (zzc != 0) {
                            System.arraycopy(this.axv, 0, obj2, 0, zzc);
                        }
                        while (zzc < obj2.length) {
                            obj2[zzc] = com_google_android_gms_internal_zzars.bW();
                            zzc++;
                        }
                        this.axv = obj2;
                        com_google_android_gms_internal_zzars.zzagu(zzagt);
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
            return zzau(com_google_android_gms_internal_zzars);
        }

        public zzf zzcaf() {
            this.axu = zzasd.btS;
            this.axv = zzasd.btS;
            this.btP = -1;
            return this;
        }

        protected int zzx() {
            int i;
            int i2;
            int i3 = 0;
            int zzx = super.zzx();
            if (this.axu == null || this.axu.length <= 0) {
                i = zzx;
            } else {
                i2 = 0;
                for (long zzcy : this.axu) {
                    i2 += zzart.zzcy(zzcy);
                }
                i = (zzx + i2) + (this.axu.length * 1);
            }
            if (this.axv == null || this.axv.length <= 0) {
                return i;
            }
            i2 = 0;
            while (i3 < this.axv.length) {
                i2 += zzart.zzcy(this.axv[i3]);
                i3++;
            }
            return (i + i2) + (this.axv.length * 1);
        }
    }

    public static final class zzg extends zzasa {
        private static volatile zzg[] axw;
        public String Fe;
        public Float avV;
        public Double avW;
        public Long awR;
        public Long axx;
        public String name;

        public zzg() {
            zzcah();
        }

        public static zzg[] zzcag() {
            if (axw == null) {
                synchronized (zzary.btO) {
                    if (axw == null) {
                        axw = new zzg[0];
                    }
                }
            }
            return axw;
        }

        public boolean equals(Object obj) {
            if (obj == this) {
                return true;
            }
            if (!(obj instanceof zzg)) {
                return false;
            }
            zzg com_google_android_gms_internal_zzwc_zzg = (zzg) obj;
            if (this.axx == null) {
                if (com_google_android_gms_internal_zzwc_zzg.axx != null) {
                    return false;
                }
            } else if (!this.axx.equals(com_google_android_gms_internal_zzwc_zzg.axx)) {
                return false;
            }
            if (this.name == null) {
                if (com_google_android_gms_internal_zzwc_zzg.name != null) {
                    return false;
                }
            } else if (!this.name.equals(com_google_android_gms_internal_zzwc_zzg.name)) {
                return false;
            }
            if (this.Fe == null) {
                if (com_google_android_gms_internal_zzwc_zzg.Fe != null) {
                    return false;
                }
            } else if (!this.Fe.equals(com_google_android_gms_internal_zzwc_zzg.Fe)) {
                return false;
            }
            if (this.awR == null) {
                if (com_google_android_gms_internal_zzwc_zzg.awR != null) {
                    return false;
                }
            } else if (!this.awR.equals(com_google_android_gms_internal_zzwc_zzg.awR)) {
                return false;
            }
            if (this.avV == null) {
                if (com_google_android_gms_internal_zzwc_zzg.avV != null) {
                    return false;
                }
            } else if (!this.avV.equals(com_google_android_gms_internal_zzwc_zzg.avV)) {
                return false;
            }
            return this.avW == null ? com_google_android_gms_internal_zzwc_zzg.avW == null : this.avW.equals(com_google_android_gms_internal_zzwc_zzg.avW);
        }

        public int hashCode() {
            int i = 0;
            int hashCode = ((this.avV == null ? 0 : this.avV.hashCode()) + (((this.awR == null ? 0 : this.awR.hashCode()) + (((this.Fe == null ? 0 : this.Fe.hashCode()) + (((this.name == null ? 0 : this.name.hashCode()) + (((this.axx == null ? 0 : this.axx.hashCode()) + ((getClass().getName().hashCode() + 527) * 31)) * 31)) * 31)) * 31)) * 31)) * 31;
            if (this.avW != null) {
                i = this.avW.hashCode();
            }
            return hashCode + i;
        }

        public void zza(zzart com_google_android_gms_internal_zzart) throws IOException {
            if (this.axx != null) {
                com_google_android_gms_internal_zzart.zzb(1, this.axx.longValue());
            }
            if (this.name != null) {
                com_google_android_gms_internal_zzart.zzq(2, this.name);
            }
            if (this.Fe != null) {
                com_google_android_gms_internal_zzart.zzq(3, this.Fe);
            }
            if (this.awR != null) {
                com_google_android_gms_internal_zzart.zzb(4, this.awR.longValue());
            }
            if (this.avV != null) {
                com_google_android_gms_internal_zzart.zzc(5, this.avV.floatValue());
            }
            if (this.avW != null) {
                com_google_android_gms_internal_zzart.zza(6, this.avW.doubleValue());
            }
            super.zza(com_google_android_gms_internal_zzart);
        }

        public zzg zzav(zzars com_google_android_gms_internal_zzars) throws IOException {
            while (true) {
                int bU = com_google_android_gms_internal_zzars.bU();
                switch (bU) {
                    case 0:
                        break;
                    case 8:
                        this.axx = Long.valueOf(com_google_android_gms_internal_zzars.bX());
                        continue;
                    case 18:
                        this.name = com_google_android_gms_internal_zzars.readString();
                        continue;
                    case NalUnitTypes.NAL_TYPE_RSV_VCL26 /*26*/:
                        this.Fe = com_google_android_gms_internal_zzars.readString();
                        continue;
                    case 32:
                        this.awR = Long.valueOf(com_google_android_gms_internal_zzars.bX());
                        continue;
                    case MotionEventCompat.AXIS_GENERIC_14 /*45*/:
                        this.avV = Float.valueOf(com_google_android_gms_internal_zzars.readFloat());
                        continue;
                    case 49:
                        this.avW = Double.valueOf(com_google_android_gms_internal_zzars.readDouble());
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
            return zzav(com_google_android_gms_internal_zzars);
        }

        public zzg zzcah() {
            this.axx = null;
            this.name = null;
            this.Fe = null;
            this.awR = null;
            this.avV = null;
            this.avW = null;
            this.btP = -1;
            return this;
        }

        protected int zzx() {
            int zzx = super.zzx();
            if (this.axx != null) {
                zzx += zzart.zzf(1, this.axx.longValue());
            }
            if (this.name != null) {
                zzx += zzart.zzr(2, this.name);
            }
            if (this.Fe != null) {
                zzx += zzart.zzr(3, this.Fe);
            }
            if (this.awR != null) {
                zzx += zzart.zzf(4, this.awR.longValue());
            }
            if (this.avV != null) {
                zzx += zzart.zzd(5, this.avV.floatValue());
            }
            return this.avW != null ? zzx + zzart.zzb(6, this.avW.doubleValue()) : zzx;
        }
    }
}
