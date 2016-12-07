package com.google.android.gms.internal;

import com.googlecode.mp4parser.authoring.tracks.h265.NalUnitTypes;
import java.io.IOException;

public interface zzad {

    public static final class zza extends zzbut {
        public zzb zzaJ;
        public zzc zzaK;

        public zza() {
            zzu();
        }

        public static zza zzc(byte[] bArr) throws zzbus {
            return (zza) zzbut.zza(new zza(), bArr);
        }

        public zza zza(zzbul com_google_android_gms_internal_zzbul) throws IOException {
            while (true) {
                int zzacu = com_google_android_gms_internal_zzbul.zzacu();
                switch (zzacu) {
                    case 0:
                        break;
                    case 10:
                        if (this.zzaJ == null) {
                            this.zzaJ = new zzb();
                        }
                        com_google_android_gms_internal_zzbul.zza(this.zzaJ);
                        continue;
                    case 18:
                        if (this.zzaK == null) {
                            this.zzaK = new zzc();
                        }
                        com_google_android_gms_internal_zzbul.zza(this.zzaK);
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
            if (this.zzaJ != null) {
                com_google_android_gms_internal_zzbum.zza(1, this.zzaJ);
            }
            if (this.zzaK != null) {
                com_google_android_gms_internal_zzbum.zza(2, this.zzaK);
            }
            super.zza(com_google_android_gms_internal_zzbum);
        }

        public /* synthetic */ zzbut zzb(zzbul com_google_android_gms_internal_zzbul) throws IOException {
            return zza(com_google_android_gms_internal_zzbul);
        }

        public zza zzu() {
            this.zzaJ = null;
            this.zzaK = null;
            this.zzcsg = -1;
            return this;
        }

        protected int zzv() {
            int zzv = super.zzv();
            if (this.zzaJ != null) {
                zzv += zzbum.zzc(1, this.zzaJ);
            }
            return this.zzaK != null ? zzv + zzbum.zzc(2, this.zzaK) : zzv;
        }
    }

    public static final class zzb extends zzbut {
        public Integer zzaL;

        public zzb() {
            zzw();
        }

        public void zza(zzbum com_google_android_gms_internal_zzbum) throws IOException {
            if (this.zzaL != null) {
                com_google_android_gms_internal_zzbum.zzF(27, this.zzaL.intValue());
            }
            super.zza(com_google_android_gms_internal_zzbum);
        }

        public /* synthetic */ zzbut zzb(zzbul com_google_android_gms_internal_zzbul) throws IOException {
            return zzc(com_google_android_gms_internal_zzbul);
        }

        public zzb zzc(zzbul com_google_android_gms_internal_zzbul) throws IOException {
            while (true) {
                int zzacu = com_google_android_gms_internal_zzbul.zzacu();
                switch (zzacu) {
                    case 0:
                        break;
                    case 216:
                        zzacu = com_google_android_gms_internal_zzbul.zzacy();
                        switch (zzacu) {
                            case 0:
                            case 1:
                            case 2:
                            case 3:
                            case 4:
                                this.zzaL = Integer.valueOf(zzacu);
                                break;
                            default:
                                continue;
                        }
                    default:
                        if (!zzbuw.zzb(com_google_android_gms_internal_zzbul, zzacu)) {
                            break;
                        }
                        continue;
                }
                return this;
            }
        }

        protected int zzv() {
            int zzv = super.zzv();
            return this.zzaL != null ? zzv + zzbum.zzH(27, this.zzaL.intValue()) : zzv;
        }

        public zzb zzw() {
            this.zzcsg = -1;
            return this;
        }
    }

    public static final class zzc extends zzbut {
        public String zzaM;
        public String zzaN;
        public String zzaO;
        public String zzaP;
        public String zzaQ;

        public zzc() {
            zzx();
        }

        public void zza(zzbum com_google_android_gms_internal_zzbum) throws IOException {
            if (this.zzaM != null) {
                com_google_android_gms_internal_zzbum.zzq(1, this.zzaM);
            }
            if (this.zzaN != null) {
                com_google_android_gms_internal_zzbum.zzq(2, this.zzaN);
            }
            if (this.zzaO != null) {
                com_google_android_gms_internal_zzbum.zzq(3, this.zzaO);
            }
            if (this.zzaP != null) {
                com_google_android_gms_internal_zzbum.zzq(4, this.zzaP);
            }
            if (this.zzaQ != null) {
                com_google_android_gms_internal_zzbum.zzq(5, this.zzaQ);
            }
            super.zza(com_google_android_gms_internal_zzbum);
        }

        public /* synthetic */ zzbut zzb(zzbul com_google_android_gms_internal_zzbul) throws IOException {
            return zzd(com_google_android_gms_internal_zzbul);
        }

        public zzc zzd(zzbul com_google_android_gms_internal_zzbul) throws IOException {
            while (true) {
                int zzacu = com_google_android_gms_internal_zzbul.zzacu();
                switch (zzacu) {
                    case 0:
                        break;
                    case 10:
                        this.zzaM = com_google_android_gms_internal_zzbul.readString();
                        continue;
                    case 18:
                        this.zzaN = com_google_android_gms_internal_zzbul.readString();
                        continue;
                    case NalUnitTypes.NAL_TYPE_RSV_VCL26 /*26*/:
                        this.zzaO = com_google_android_gms_internal_zzbul.readString();
                        continue;
                    case 34:
                        this.zzaP = com_google_android_gms_internal_zzbul.readString();
                        continue;
                    case 42:
                        this.zzaQ = com_google_android_gms_internal_zzbul.readString();
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

        protected int zzv() {
            int zzv = super.zzv();
            if (this.zzaM != null) {
                zzv += zzbum.zzr(1, this.zzaM);
            }
            if (this.zzaN != null) {
                zzv += zzbum.zzr(2, this.zzaN);
            }
            if (this.zzaO != null) {
                zzv += zzbum.zzr(3, this.zzaO);
            }
            if (this.zzaP != null) {
                zzv += zzbum.zzr(4, this.zzaP);
            }
            return this.zzaQ != null ? zzv + zzbum.zzr(5, this.zzaQ) : zzv;
        }

        public zzc zzx() {
            this.zzaM = null;
            this.zzaN = null;
            this.zzaO = null;
            this.zzaP = null;
            this.zzaQ = null;
            this.zzcsg = -1;
            return this;
        }
    }
}
