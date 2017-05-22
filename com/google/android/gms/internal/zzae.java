package com.google.android.gms.internal;

import com.googlecode.mp4parser.authoring.tracks.h265.NalUnitTypes;
import java.io.IOException;

public interface zzae {

    public static final class zza extends zzbyd<zza> {
        public zzb zzaK;
        public zzc zzaL;

        public zza() {
            this.zzcwL = -1;
        }

        public static zza zzc(byte[] bArr) throws zzbyi {
            return (zza) zzbyj.zza(new zza(), bArr);
        }

        public zza zza(zzbyb com_google_android_gms_internal_zzbyb) throws IOException {
            while (true) {
                int zzaeW = com_google_android_gms_internal_zzbyb.zzaeW();
                switch (zzaeW) {
                    case 0:
                        break;
                    case 10:
                        if (this.zzaK == null) {
                            this.zzaK = new zzb();
                        }
                        com_google_android_gms_internal_zzbyb.zza(this.zzaK);
                        continue;
                    case 18:
                        if (this.zzaL == null) {
                            this.zzaL = new zzc();
                        }
                        com_google_android_gms_internal_zzbyb.zza(this.zzaL);
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
            if (this.zzaK != null) {
                com_google_android_gms_internal_zzbyc.zza(1, this.zzaK);
            }
            if (this.zzaL != null) {
                com_google_android_gms_internal_zzbyc.zza(2, this.zzaL);
            }
            super.zza(com_google_android_gms_internal_zzbyc);
        }

        public /* synthetic */ zzbyj zzb(zzbyb com_google_android_gms_internal_zzbyb) throws IOException {
            return zza(com_google_android_gms_internal_zzbyb);
        }

        protected int zzu() {
            int zzu = super.zzu();
            if (this.zzaK != null) {
                zzu += zzbyc.zzc(1, this.zzaK);
            }
            return this.zzaL != null ? zzu + zzbyc.zzc(2, this.zzaL) : zzu;
        }
    }

    public static final class zzb extends zzbyd<zzb> {
        public Integer zzaM;

        public zzb() {
            this.zzaM = null;
            this.zzcwL = -1;
        }

        public void zza(zzbyc com_google_android_gms_internal_zzbyc) throws IOException {
            if (this.zzaM != null) {
                com_google_android_gms_internal_zzbyc.zzJ(27, this.zzaM.intValue());
            }
            super.zza(com_google_android_gms_internal_zzbyc);
        }

        public /* synthetic */ zzbyj zzb(zzbyb com_google_android_gms_internal_zzbyb) throws IOException {
            return zzc(com_google_android_gms_internal_zzbyb);
        }

        public zzb zzc(zzbyb com_google_android_gms_internal_zzbyb) throws IOException {
            while (true) {
                int zzaeW = com_google_android_gms_internal_zzbyb.zzaeW();
                switch (zzaeW) {
                    case 0:
                        break;
                    case 216:
                        zzaeW = com_google_android_gms_internal_zzbyb.zzafa();
                        switch (zzaeW) {
                            case 0:
                            case 1:
                            case 2:
                            case 3:
                            case 4:
                                this.zzaM = Integer.valueOf(zzaeW);
                                break;
                            default:
                                continue;
                        }
                    default:
                        if (!super.zza(com_google_android_gms_internal_zzbyb, zzaeW)) {
                            break;
                        }
                        continue;
                }
                return this;
            }
        }

        protected int zzu() {
            int zzu = super.zzu();
            return this.zzaM != null ? zzu + zzbyc.zzL(27, this.zzaM.intValue()) : zzu;
        }
    }

    public static final class zzc extends zzbyd<zzc> {
        public String zzaN;
        public String zzaO;
        public String zzaP;
        public String zzaQ;
        public String zzaR;

        public zzc() {
            this.zzaN = null;
            this.zzaO = null;
            this.zzaP = null;
            this.zzaQ = null;
            this.zzaR = null;
            this.zzcwL = -1;
        }

        public void zza(zzbyc com_google_android_gms_internal_zzbyc) throws IOException {
            if (this.zzaN != null) {
                com_google_android_gms_internal_zzbyc.zzq(1, this.zzaN);
            }
            if (this.zzaO != null) {
                com_google_android_gms_internal_zzbyc.zzq(2, this.zzaO);
            }
            if (this.zzaP != null) {
                com_google_android_gms_internal_zzbyc.zzq(3, this.zzaP);
            }
            if (this.zzaQ != null) {
                com_google_android_gms_internal_zzbyc.zzq(4, this.zzaQ);
            }
            if (this.zzaR != null) {
                com_google_android_gms_internal_zzbyc.zzq(5, this.zzaR);
            }
            super.zza(com_google_android_gms_internal_zzbyc);
        }

        public /* synthetic */ zzbyj zzb(zzbyb com_google_android_gms_internal_zzbyb) throws IOException {
            return zzd(com_google_android_gms_internal_zzbyb);
        }

        public zzc zzd(zzbyb com_google_android_gms_internal_zzbyb) throws IOException {
            while (true) {
                int zzaeW = com_google_android_gms_internal_zzbyb.zzaeW();
                switch (zzaeW) {
                    case 0:
                        break;
                    case 10:
                        this.zzaN = com_google_android_gms_internal_zzbyb.readString();
                        continue;
                    case 18:
                        this.zzaO = com_google_android_gms_internal_zzbyb.readString();
                        continue;
                    case NalUnitTypes.NAL_TYPE_RSV_VCL26 /*26*/:
                        this.zzaP = com_google_android_gms_internal_zzbyb.readString();
                        continue;
                    case 34:
                        this.zzaQ = com_google_android_gms_internal_zzbyb.readString();
                        continue;
                    case 42:
                        this.zzaR = com_google_android_gms_internal_zzbyb.readString();
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

        protected int zzu() {
            int zzu = super.zzu();
            if (this.zzaN != null) {
                zzu += zzbyc.zzr(1, this.zzaN);
            }
            if (this.zzaO != null) {
                zzu += zzbyc.zzr(2, this.zzaO);
            }
            if (this.zzaP != null) {
                zzu += zzbyc.zzr(3, this.zzaP);
            }
            if (this.zzaQ != null) {
                zzu += zzbyc.zzr(4, this.zzaQ);
            }
            return this.zzaR != null ? zzu + zzbyc.zzr(5, this.zzaR) : zzu;
        }
    }
}
