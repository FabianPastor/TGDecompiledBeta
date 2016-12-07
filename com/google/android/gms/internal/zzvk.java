package com.google.android.gms.internal;

import com.googlecode.mp4parser.authoring.tracks.h265.NalUnitTypes;
import java.io.IOException;

public interface zzvk {

    public static final class zza extends zzark {
        private static volatile zza[] asz;
        public Integer asA;
        public zze[] asB;
        public zzb[] asC;

        public zza() {
            zzbyn();
        }

        public static zza[] zzbym() {
            if (asz == null) {
                synchronized (zzari.bqD) {
                    if (asz == null) {
                        asz = new zza[0];
                    }
                }
            }
            return asz;
        }

        public boolean equals(Object obj) {
            if (obj == this) {
                return true;
            }
            if (!(obj instanceof zza)) {
                return false;
            }
            zza com_google_android_gms_internal_zzvk_zza = (zza) obj;
            if (this.asA == null) {
                if (com_google_android_gms_internal_zzvk_zza.asA != null) {
                    return false;
                }
            } else if (!this.asA.equals(com_google_android_gms_internal_zzvk_zza.asA)) {
                return false;
            }
            return !zzari.equals(this.asB, com_google_android_gms_internal_zzvk_zza.asB) ? false : zzari.equals(this.asC, com_google_android_gms_internal_zzvk_zza.asC);
        }

        public int hashCode() {
            return (((((this.asA == null ? 0 : this.asA.hashCode()) + ((getClass().getName().hashCode() + 527) * 31)) * 31) + zzari.hashCode(this.asB)) * 31) + zzari.hashCode(this.asC);
        }

        public void zza(zzard com_google_android_gms_internal_zzard) throws IOException {
            int i = 0;
            if (this.asA != null) {
                com_google_android_gms_internal_zzard.zzae(1, this.asA.intValue());
            }
            if (this.asB != null && this.asB.length > 0) {
                for (zzark com_google_android_gms_internal_zzark : this.asB) {
                    if (com_google_android_gms_internal_zzark != null) {
                        com_google_android_gms_internal_zzard.zza(2, com_google_android_gms_internal_zzark);
                    }
                }
            }
            if (this.asC != null && this.asC.length > 0) {
                while (i < this.asC.length) {
                    zzark com_google_android_gms_internal_zzark2 = this.asC[i];
                    if (com_google_android_gms_internal_zzark2 != null) {
                        com_google_android_gms_internal_zzard.zza(3, com_google_android_gms_internal_zzark2);
                    }
                    i++;
                }
            }
            super.zza(com_google_android_gms_internal_zzard);
        }

        public zza zzad(zzarc com_google_android_gms_internal_zzarc) throws IOException {
            while (true) {
                int cw = com_google_android_gms_internal_zzarc.cw();
                int zzc;
                Object obj;
                switch (cw) {
                    case 0:
                        break;
                    case 8:
                        this.asA = Integer.valueOf(com_google_android_gms_internal_zzarc.cA());
                        continue;
                    case 18:
                        zzc = zzarn.zzc(com_google_android_gms_internal_zzarc, 18);
                        cw = this.asB == null ? 0 : this.asB.length;
                        obj = new zze[(zzc + cw)];
                        if (cw != 0) {
                            System.arraycopy(this.asB, 0, obj, 0, cw);
                        }
                        while (cw < obj.length - 1) {
                            obj[cw] = new zze();
                            com_google_android_gms_internal_zzarc.zza(obj[cw]);
                            com_google_android_gms_internal_zzarc.cw();
                            cw++;
                        }
                        obj[cw] = new zze();
                        com_google_android_gms_internal_zzarc.zza(obj[cw]);
                        this.asB = obj;
                        continue;
                    case NalUnitTypes.NAL_TYPE_RSV_VCL26 /*26*/:
                        zzc = zzarn.zzc(com_google_android_gms_internal_zzarc, 26);
                        cw = this.asC == null ? 0 : this.asC.length;
                        obj = new zzb[(zzc + cw)];
                        if (cw != 0) {
                            System.arraycopy(this.asC, 0, obj, 0, cw);
                        }
                        while (cw < obj.length - 1) {
                            obj[cw] = new zzb();
                            com_google_android_gms_internal_zzarc.zza(obj[cw]);
                            com_google_android_gms_internal_zzarc.cw();
                            cw++;
                        }
                        obj[cw] = new zzb();
                        com_google_android_gms_internal_zzarc.zza(obj[cw]);
                        this.asC = obj;
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
            return zzad(com_google_android_gms_internal_zzarc);
        }

        public zza zzbyn() {
            this.asA = null;
            this.asB = zze.zzbyt();
            this.asC = zzb.zzbyo();
            this.bqE = -1;
            return this;
        }

        protected int zzx() {
            int i = 0;
            int zzx = super.zzx();
            if (this.asA != null) {
                zzx += zzard.zzag(1, this.asA.intValue());
            }
            if (this.asB != null && this.asB.length > 0) {
                int i2 = zzx;
                for (zzark com_google_android_gms_internal_zzark : this.asB) {
                    if (com_google_android_gms_internal_zzark != null) {
                        i2 += zzard.zzc(2, com_google_android_gms_internal_zzark);
                    }
                }
                zzx = i2;
            }
            if (this.asC != null && this.asC.length > 0) {
                while (i < this.asC.length) {
                    zzark com_google_android_gms_internal_zzark2 = this.asC[i];
                    if (com_google_android_gms_internal_zzark2 != null) {
                        zzx += zzard.zzc(3, com_google_android_gms_internal_zzark2);
                    }
                    i++;
                }
            }
            return zzx;
        }
    }

    public static final class zzb extends zzark {
        private static volatile zzb[] asD;
        public Integer asE;
        public String asF;
        public zzc[] asG;
        public Boolean asH;
        public zzd asI;

        public zzb() {
            zzbyp();
        }

        public static zzb[] zzbyo() {
            if (asD == null) {
                synchronized (zzari.bqD) {
                    if (asD == null) {
                        asD = new zzb[0];
                    }
                }
            }
            return asD;
        }

        public boolean equals(Object obj) {
            if (obj == this) {
                return true;
            }
            if (!(obj instanceof zzb)) {
                return false;
            }
            zzb com_google_android_gms_internal_zzvk_zzb = (zzb) obj;
            if (this.asE == null) {
                if (com_google_android_gms_internal_zzvk_zzb.asE != null) {
                    return false;
                }
            } else if (!this.asE.equals(com_google_android_gms_internal_zzvk_zzb.asE)) {
                return false;
            }
            if (this.asF == null) {
                if (com_google_android_gms_internal_zzvk_zzb.asF != null) {
                    return false;
                }
            } else if (!this.asF.equals(com_google_android_gms_internal_zzvk_zzb.asF)) {
                return false;
            }
            if (!zzari.equals(this.asG, com_google_android_gms_internal_zzvk_zzb.asG)) {
                return false;
            }
            if (this.asH == null) {
                if (com_google_android_gms_internal_zzvk_zzb.asH != null) {
                    return false;
                }
            } else if (!this.asH.equals(com_google_android_gms_internal_zzvk_zzb.asH)) {
                return false;
            }
            return this.asI == null ? com_google_android_gms_internal_zzvk_zzb.asI == null : this.asI.equals(com_google_android_gms_internal_zzvk_zzb.asI);
        }

        public int hashCode() {
            int i = 0;
            int hashCode = ((this.asH == null ? 0 : this.asH.hashCode()) + (((((this.asF == null ? 0 : this.asF.hashCode()) + (((this.asE == null ? 0 : this.asE.hashCode()) + ((getClass().getName().hashCode() + 527) * 31)) * 31)) * 31) + zzari.hashCode(this.asG)) * 31)) * 31;
            if (this.asI != null) {
                i = this.asI.hashCode();
            }
            return hashCode + i;
        }

        public void zza(zzard com_google_android_gms_internal_zzard) throws IOException {
            if (this.asE != null) {
                com_google_android_gms_internal_zzard.zzae(1, this.asE.intValue());
            }
            if (this.asF != null) {
                com_google_android_gms_internal_zzard.zzr(2, this.asF);
            }
            if (this.asG != null && this.asG.length > 0) {
                for (zzark com_google_android_gms_internal_zzark : this.asG) {
                    if (com_google_android_gms_internal_zzark != null) {
                        com_google_android_gms_internal_zzard.zza(3, com_google_android_gms_internal_zzark);
                    }
                }
            }
            if (this.asH != null) {
                com_google_android_gms_internal_zzard.zzj(4, this.asH.booleanValue());
            }
            if (this.asI != null) {
                com_google_android_gms_internal_zzard.zza(5, this.asI);
            }
            super.zza(com_google_android_gms_internal_zzard);
        }

        public zzb zzae(zzarc com_google_android_gms_internal_zzarc) throws IOException {
            while (true) {
                int cw = com_google_android_gms_internal_zzarc.cw();
                switch (cw) {
                    case 0:
                        break;
                    case 8:
                        this.asE = Integer.valueOf(com_google_android_gms_internal_zzarc.cA());
                        continue;
                    case 18:
                        this.asF = com_google_android_gms_internal_zzarc.readString();
                        continue;
                    case NalUnitTypes.NAL_TYPE_RSV_VCL26 /*26*/:
                        int zzc = zzarn.zzc(com_google_android_gms_internal_zzarc, 26);
                        cw = this.asG == null ? 0 : this.asG.length;
                        Object obj = new zzc[(zzc + cw)];
                        if (cw != 0) {
                            System.arraycopy(this.asG, 0, obj, 0, cw);
                        }
                        while (cw < obj.length - 1) {
                            obj[cw] = new zzc();
                            com_google_android_gms_internal_zzarc.zza(obj[cw]);
                            com_google_android_gms_internal_zzarc.cw();
                            cw++;
                        }
                        obj[cw] = new zzc();
                        com_google_android_gms_internal_zzarc.zza(obj[cw]);
                        this.asG = obj;
                        continue;
                    case 32:
                        this.asH = Boolean.valueOf(com_google_android_gms_internal_zzarc.cC());
                        continue;
                    case 42:
                        if (this.asI == null) {
                            this.asI = new zzd();
                        }
                        com_google_android_gms_internal_zzarc.zza(this.asI);
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
            return zzae(com_google_android_gms_internal_zzarc);
        }

        public zzb zzbyp() {
            this.asE = null;
            this.asF = null;
            this.asG = zzc.zzbyq();
            this.asH = null;
            this.asI = null;
            this.bqE = -1;
            return this;
        }

        protected int zzx() {
            int zzx = super.zzx();
            if (this.asE != null) {
                zzx += zzard.zzag(1, this.asE.intValue());
            }
            if (this.asF != null) {
                zzx += zzard.zzs(2, this.asF);
            }
            if (this.asG != null && this.asG.length > 0) {
                int i = zzx;
                for (zzark com_google_android_gms_internal_zzark : this.asG) {
                    if (com_google_android_gms_internal_zzark != null) {
                        i += zzard.zzc(3, com_google_android_gms_internal_zzark);
                    }
                }
                zzx = i;
            }
            if (this.asH != null) {
                zzx += zzard.zzk(4, this.asH.booleanValue());
            }
            return this.asI != null ? zzx + zzard.zzc(5, this.asI) : zzx;
        }
    }

    public static final class zzc extends zzark {
        private static volatile zzc[] asJ;
        public zzf asK;
        public zzd asL;
        public Boolean asM;
        public String asN;

        public zzc() {
            zzbyr();
        }

        public static zzc[] zzbyq() {
            if (asJ == null) {
                synchronized (zzari.bqD) {
                    if (asJ == null) {
                        asJ = new zzc[0];
                    }
                }
            }
            return asJ;
        }

        public boolean equals(Object obj) {
            if (obj == this) {
                return true;
            }
            if (!(obj instanceof zzc)) {
                return false;
            }
            zzc com_google_android_gms_internal_zzvk_zzc = (zzc) obj;
            if (this.asK == null) {
                if (com_google_android_gms_internal_zzvk_zzc.asK != null) {
                    return false;
                }
            } else if (!this.asK.equals(com_google_android_gms_internal_zzvk_zzc.asK)) {
                return false;
            }
            if (this.asL == null) {
                if (com_google_android_gms_internal_zzvk_zzc.asL != null) {
                    return false;
                }
            } else if (!this.asL.equals(com_google_android_gms_internal_zzvk_zzc.asL)) {
                return false;
            }
            if (this.asM == null) {
                if (com_google_android_gms_internal_zzvk_zzc.asM != null) {
                    return false;
                }
            } else if (!this.asM.equals(com_google_android_gms_internal_zzvk_zzc.asM)) {
                return false;
            }
            return this.asN == null ? com_google_android_gms_internal_zzvk_zzc.asN == null : this.asN.equals(com_google_android_gms_internal_zzvk_zzc.asN);
        }

        public int hashCode() {
            int i = 0;
            int hashCode = ((this.asM == null ? 0 : this.asM.hashCode()) + (((this.asL == null ? 0 : this.asL.hashCode()) + (((this.asK == null ? 0 : this.asK.hashCode()) + ((getClass().getName().hashCode() + 527) * 31)) * 31)) * 31)) * 31;
            if (this.asN != null) {
                i = this.asN.hashCode();
            }
            return hashCode + i;
        }

        public void zza(zzard com_google_android_gms_internal_zzard) throws IOException {
            if (this.asK != null) {
                com_google_android_gms_internal_zzard.zza(1, this.asK);
            }
            if (this.asL != null) {
                com_google_android_gms_internal_zzard.zza(2, this.asL);
            }
            if (this.asM != null) {
                com_google_android_gms_internal_zzard.zzj(3, this.asM.booleanValue());
            }
            if (this.asN != null) {
                com_google_android_gms_internal_zzard.zzr(4, this.asN);
            }
            super.zza(com_google_android_gms_internal_zzard);
        }

        public zzc zzaf(zzarc com_google_android_gms_internal_zzarc) throws IOException {
            while (true) {
                int cw = com_google_android_gms_internal_zzarc.cw();
                switch (cw) {
                    case 0:
                        break;
                    case 10:
                        if (this.asK == null) {
                            this.asK = new zzf();
                        }
                        com_google_android_gms_internal_zzarc.zza(this.asK);
                        continue;
                    case 18:
                        if (this.asL == null) {
                            this.asL = new zzd();
                        }
                        com_google_android_gms_internal_zzarc.zza(this.asL);
                        continue;
                    case 24:
                        this.asM = Boolean.valueOf(com_google_android_gms_internal_zzarc.cC());
                        continue;
                    case 34:
                        this.asN = com_google_android_gms_internal_zzarc.readString();
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
            return zzaf(com_google_android_gms_internal_zzarc);
        }

        public zzc zzbyr() {
            this.asK = null;
            this.asL = null;
            this.asM = null;
            this.asN = null;
            this.bqE = -1;
            return this;
        }

        protected int zzx() {
            int zzx = super.zzx();
            if (this.asK != null) {
                zzx += zzard.zzc(1, this.asK);
            }
            if (this.asL != null) {
                zzx += zzard.zzc(2, this.asL);
            }
            if (this.asM != null) {
                zzx += zzard.zzk(3, this.asM.booleanValue());
            }
            return this.asN != null ? zzx + zzard.zzs(4, this.asN) : zzx;
        }
    }

    public static final class zzd extends zzark {
        public Integer asO;
        public Boolean asP;
        public String asQ;
        public String asR;
        public String asS;

        public zzd() {
            zzbys();
        }

        public boolean equals(Object obj) {
            if (obj == this) {
                return true;
            }
            if (!(obj instanceof zzd)) {
                return false;
            }
            zzd com_google_android_gms_internal_zzvk_zzd = (zzd) obj;
            if (this.asO == null) {
                if (com_google_android_gms_internal_zzvk_zzd.asO != null) {
                    return false;
                }
            } else if (!this.asO.equals(com_google_android_gms_internal_zzvk_zzd.asO)) {
                return false;
            }
            if (this.asP == null) {
                if (com_google_android_gms_internal_zzvk_zzd.asP != null) {
                    return false;
                }
            } else if (!this.asP.equals(com_google_android_gms_internal_zzvk_zzd.asP)) {
                return false;
            }
            if (this.asQ == null) {
                if (com_google_android_gms_internal_zzvk_zzd.asQ != null) {
                    return false;
                }
            } else if (!this.asQ.equals(com_google_android_gms_internal_zzvk_zzd.asQ)) {
                return false;
            }
            if (this.asR == null) {
                if (com_google_android_gms_internal_zzvk_zzd.asR != null) {
                    return false;
                }
            } else if (!this.asR.equals(com_google_android_gms_internal_zzvk_zzd.asR)) {
                return false;
            }
            return this.asS == null ? com_google_android_gms_internal_zzvk_zzd.asS == null : this.asS.equals(com_google_android_gms_internal_zzvk_zzd.asS);
        }

        public int hashCode() {
            int i = 0;
            int hashCode = ((this.asR == null ? 0 : this.asR.hashCode()) + (((this.asQ == null ? 0 : this.asQ.hashCode()) + (((this.asP == null ? 0 : this.asP.hashCode()) + (((this.asO == null ? 0 : this.asO.intValue()) + ((getClass().getName().hashCode() + 527) * 31)) * 31)) * 31)) * 31)) * 31;
            if (this.asS != null) {
                i = this.asS.hashCode();
            }
            return hashCode + i;
        }

        public void zza(zzard com_google_android_gms_internal_zzard) throws IOException {
            if (this.asO != null) {
                com_google_android_gms_internal_zzard.zzae(1, this.asO.intValue());
            }
            if (this.asP != null) {
                com_google_android_gms_internal_zzard.zzj(2, this.asP.booleanValue());
            }
            if (this.asQ != null) {
                com_google_android_gms_internal_zzard.zzr(3, this.asQ);
            }
            if (this.asR != null) {
                com_google_android_gms_internal_zzard.zzr(4, this.asR);
            }
            if (this.asS != null) {
                com_google_android_gms_internal_zzard.zzr(5, this.asS);
            }
            super.zza(com_google_android_gms_internal_zzard);
        }

        public zzd zzag(zzarc com_google_android_gms_internal_zzarc) throws IOException {
            while (true) {
                int cw = com_google_android_gms_internal_zzarc.cw();
                switch (cw) {
                    case 0:
                        break;
                    case 8:
                        cw = com_google_android_gms_internal_zzarc.cA();
                        switch (cw) {
                            case 0:
                            case 1:
                            case 2:
                            case 3:
                            case 4:
                                this.asO = Integer.valueOf(cw);
                                break;
                            default:
                                continue;
                        }
                    case 16:
                        this.asP = Boolean.valueOf(com_google_android_gms_internal_zzarc.cC());
                        continue;
                    case NalUnitTypes.NAL_TYPE_RSV_VCL26 /*26*/:
                        this.asQ = com_google_android_gms_internal_zzarc.readString();
                        continue;
                    case 34:
                        this.asR = com_google_android_gms_internal_zzarc.readString();
                        continue;
                    case 42:
                        this.asS = com_google_android_gms_internal_zzarc.readString();
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
            return zzag(com_google_android_gms_internal_zzarc);
        }

        public zzd zzbys() {
            this.asP = null;
            this.asQ = null;
            this.asR = null;
            this.asS = null;
            this.bqE = -1;
            return this;
        }

        protected int zzx() {
            int zzx = super.zzx();
            if (this.asO != null) {
                zzx += zzard.zzag(1, this.asO.intValue());
            }
            if (this.asP != null) {
                zzx += zzard.zzk(2, this.asP.booleanValue());
            }
            if (this.asQ != null) {
                zzx += zzard.zzs(3, this.asQ);
            }
            if (this.asR != null) {
                zzx += zzard.zzs(4, this.asR);
            }
            return this.asS != null ? zzx + zzard.zzs(5, this.asS) : zzx;
        }
    }

    public static final class zze extends zzark {
        private static volatile zze[] asT;
        public Integer asE;
        public String asU;
        public zzc asV;

        public zze() {
            zzbyu();
        }

        public static zze[] zzbyt() {
            if (asT == null) {
                synchronized (zzari.bqD) {
                    if (asT == null) {
                        asT = new zze[0];
                    }
                }
            }
            return asT;
        }

        public boolean equals(Object obj) {
            if (obj == this) {
                return true;
            }
            if (!(obj instanceof zze)) {
                return false;
            }
            zze com_google_android_gms_internal_zzvk_zze = (zze) obj;
            if (this.asE == null) {
                if (com_google_android_gms_internal_zzvk_zze.asE != null) {
                    return false;
                }
            } else if (!this.asE.equals(com_google_android_gms_internal_zzvk_zze.asE)) {
                return false;
            }
            if (this.asU == null) {
                if (com_google_android_gms_internal_zzvk_zze.asU != null) {
                    return false;
                }
            } else if (!this.asU.equals(com_google_android_gms_internal_zzvk_zze.asU)) {
                return false;
            }
            return this.asV == null ? com_google_android_gms_internal_zzvk_zze.asV == null : this.asV.equals(com_google_android_gms_internal_zzvk_zze.asV);
        }

        public int hashCode() {
            int i = 0;
            int hashCode = ((this.asU == null ? 0 : this.asU.hashCode()) + (((this.asE == null ? 0 : this.asE.hashCode()) + ((getClass().getName().hashCode() + 527) * 31)) * 31)) * 31;
            if (this.asV != null) {
                i = this.asV.hashCode();
            }
            return hashCode + i;
        }

        public void zza(zzard com_google_android_gms_internal_zzard) throws IOException {
            if (this.asE != null) {
                com_google_android_gms_internal_zzard.zzae(1, this.asE.intValue());
            }
            if (this.asU != null) {
                com_google_android_gms_internal_zzard.zzr(2, this.asU);
            }
            if (this.asV != null) {
                com_google_android_gms_internal_zzard.zza(3, this.asV);
            }
            super.zza(com_google_android_gms_internal_zzard);
        }

        public zze zzah(zzarc com_google_android_gms_internal_zzarc) throws IOException {
            while (true) {
                int cw = com_google_android_gms_internal_zzarc.cw();
                switch (cw) {
                    case 0:
                        break;
                    case 8:
                        this.asE = Integer.valueOf(com_google_android_gms_internal_zzarc.cA());
                        continue;
                    case 18:
                        this.asU = com_google_android_gms_internal_zzarc.readString();
                        continue;
                    case NalUnitTypes.NAL_TYPE_RSV_VCL26 /*26*/:
                        if (this.asV == null) {
                            this.asV = new zzc();
                        }
                        com_google_android_gms_internal_zzarc.zza(this.asV);
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
            return zzah(com_google_android_gms_internal_zzarc);
        }

        public zze zzbyu() {
            this.asE = null;
            this.asU = null;
            this.asV = null;
            this.bqE = -1;
            return this;
        }

        protected int zzx() {
            int zzx = super.zzx();
            if (this.asE != null) {
                zzx += zzard.zzag(1, this.asE.intValue());
            }
            if (this.asU != null) {
                zzx += zzard.zzs(2, this.asU);
            }
            return this.asV != null ? zzx + zzard.zzc(3, this.asV) : zzx;
        }
    }

    public static final class zzf extends zzark {
        public Integer asW;
        public String asX;
        public Boolean asY;
        public String[] asZ;

        public zzf() {
            zzbyv();
        }

        public boolean equals(Object obj) {
            if (obj == this) {
                return true;
            }
            if (!(obj instanceof zzf)) {
                return false;
            }
            zzf com_google_android_gms_internal_zzvk_zzf = (zzf) obj;
            if (this.asW == null) {
                if (com_google_android_gms_internal_zzvk_zzf.asW != null) {
                    return false;
                }
            } else if (!this.asW.equals(com_google_android_gms_internal_zzvk_zzf.asW)) {
                return false;
            }
            if (this.asX == null) {
                if (com_google_android_gms_internal_zzvk_zzf.asX != null) {
                    return false;
                }
            } else if (!this.asX.equals(com_google_android_gms_internal_zzvk_zzf.asX)) {
                return false;
            }
            if (this.asY == null) {
                if (com_google_android_gms_internal_zzvk_zzf.asY != null) {
                    return false;
                }
            } else if (!this.asY.equals(com_google_android_gms_internal_zzvk_zzf.asY)) {
                return false;
            }
            return zzari.equals(this.asZ, com_google_android_gms_internal_zzvk_zzf.asZ);
        }

        public int hashCode() {
            int i = 0;
            int hashCode = ((this.asX == null ? 0 : this.asX.hashCode()) + (((this.asW == null ? 0 : this.asW.intValue()) + ((getClass().getName().hashCode() + 527) * 31)) * 31)) * 31;
            if (this.asY != null) {
                i = this.asY.hashCode();
            }
            return ((hashCode + i) * 31) + zzari.hashCode(this.asZ);
        }

        public void zza(zzard com_google_android_gms_internal_zzard) throws IOException {
            if (this.asW != null) {
                com_google_android_gms_internal_zzard.zzae(1, this.asW.intValue());
            }
            if (this.asX != null) {
                com_google_android_gms_internal_zzard.zzr(2, this.asX);
            }
            if (this.asY != null) {
                com_google_android_gms_internal_zzard.zzj(3, this.asY.booleanValue());
            }
            if (this.asZ != null && this.asZ.length > 0) {
                for (String str : this.asZ) {
                    if (str != null) {
                        com_google_android_gms_internal_zzard.zzr(4, str);
                    }
                }
            }
            super.zza(com_google_android_gms_internal_zzard);
        }

        public zzf zzai(zzarc com_google_android_gms_internal_zzarc) throws IOException {
            while (true) {
                int cw = com_google_android_gms_internal_zzarc.cw();
                switch (cw) {
                    case 0:
                        break;
                    case 8:
                        cw = com_google_android_gms_internal_zzarc.cA();
                        switch (cw) {
                            case 0:
                            case 1:
                            case 2:
                            case 3:
                            case 4:
                            case 5:
                            case 6:
                                this.asW = Integer.valueOf(cw);
                                break;
                            default:
                                continue;
                        }
                    case 18:
                        this.asX = com_google_android_gms_internal_zzarc.readString();
                        continue;
                    case 24:
                        this.asY = Boolean.valueOf(com_google_android_gms_internal_zzarc.cC());
                        continue;
                    case 34:
                        int zzc = zzarn.zzc(com_google_android_gms_internal_zzarc, 34);
                        cw = this.asZ == null ? 0 : this.asZ.length;
                        Object obj = new String[(zzc + cw)];
                        if (cw != 0) {
                            System.arraycopy(this.asZ, 0, obj, 0, cw);
                        }
                        while (cw < obj.length - 1) {
                            obj[cw] = com_google_android_gms_internal_zzarc.readString();
                            com_google_android_gms_internal_zzarc.cw();
                            cw++;
                        }
                        obj[cw] = com_google_android_gms_internal_zzarc.readString();
                        this.asZ = obj;
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
            return zzai(com_google_android_gms_internal_zzarc);
        }

        public zzf zzbyv() {
            this.asX = null;
            this.asY = null;
            this.asZ = zzarn.bqK;
            this.bqE = -1;
            return this;
        }

        protected int zzx() {
            int i = 0;
            int zzx = super.zzx();
            if (this.asW != null) {
                zzx += zzard.zzag(1, this.asW.intValue());
            }
            if (this.asX != null) {
                zzx += zzard.zzs(2, this.asX);
            }
            if (this.asY != null) {
                zzx += zzard.zzk(3, this.asY.booleanValue());
            }
            if (this.asZ == null || this.asZ.length <= 0) {
                return zzx;
            }
            int i2 = 0;
            int i3 = 0;
            while (i < this.asZ.length) {
                String str = this.asZ[i];
                if (str != null) {
                    i3++;
                    i2 += zzard.zzuy(str);
                }
                i++;
            }
            return (zzx + i2) + (i3 * 1);
        }
    }
}
