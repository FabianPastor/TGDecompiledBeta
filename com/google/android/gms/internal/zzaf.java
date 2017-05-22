package com.google.android.gms.internal;

import com.googlecode.mp4parser.authoring.tracks.h265.NalUnitTypes;
import com.googlecode.mp4parser.boxes.microsoft.XtraBox;
import java.io.IOException;

public interface zzaf {

    public static final class zza extends zzbyd<zza> {
        public String stackTrace;
        public String zzaS;
        public Long zzaT;
        public String zzaU;
        public String zzaV;
        public Long zzaW;
        public Long zzaX;
        public String zzaY;
        public Long zzaZ;
        public String zzba;

        public zza() {
            this.zzaS = null;
            this.zzaT = null;
            this.stackTrace = null;
            this.zzaU = null;
            this.zzaV = null;
            this.zzaW = null;
            this.zzaX = null;
            this.zzaY = null;
            this.zzaZ = null;
            this.zzba = null;
            this.zzcwL = -1;
        }

        public void zza(zzbyc com_google_android_gms_internal_zzbyc) throws IOException {
            if (this.zzaS != null) {
                com_google_android_gms_internal_zzbyc.zzq(1, this.zzaS);
            }
            if (this.zzaT != null) {
                com_google_android_gms_internal_zzbyc.zzb(2, this.zzaT.longValue());
            }
            if (this.stackTrace != null) {
                com_google_android_gms_internal_zzbyc.zzq(3, this.stackTrace);
            }
            if (this.zzaU != null) {
                com_google_android_gms_internal_zzbyc.zzq(4, this.zzaU);
            }
            if (this.zzaV != null) {
                com_google_android_gms_internal_zzbyc.zzq(5, this.zzaV);
            }
            if (this.zzaW != null) {
                com_google_android_gms_internal_zzbyc.zzb(6, this.zzaW.longValue());
            }
            if (this.zzaX != null) {
                com_google_android_gms_internal_zzbyc.zzb(7, this.zzaX.longValue());
            }
            if (this.zzaY != null) {
                com_google_android_gms_internal_zzbyc.zzq(8, this.zzaY);
            }
            if (this.zzaZ != null) {
                com_google_android_gms_internal_zzbyc.zzb(9, this.zzaZ.longValue());
            }
            if (this.zzba != null) {
                com_google_android_gms_internal_zzbyc.zzq(10, this.zzba);
            }
            super.zza(com_google_android_gms_internal_zzbyc);
        }

        public /* synthetic */ zzbyj zzb(zzbyb com_google_android_gms_internal_zzbyb) throws IOException {
            return zze(com_google_android_gms_internal_zzbyb);
        }

        public zza zze(zzbyb com_google_android_gms_internal_zzbyb) throws IOException {
            while (true) {
                int zzaeW = com_google_android_gms_internal_zzbyb.zzaeW();
                switch (zzaeW) {
                    case 0:
                        break;
                    case 10:
                        this.zzaS = com_google_android_gms_internal_zzbyb.readString();
                        continue;
                    case 16:
                        this.zzaT = Long.valueOf(com_google_android_gms_internal_zzbyb.zzaeZ());
                        continue;
                    case NalUnitTypes.NAL_TYPE_RSV_VCL26 /*26*/:
                        this.stackTrace = com_google_android_gms_internal_zzbyb.readString();
                        continue;
                    case 34:
                        this.zzaU = com_google_android_gms_internal_zzbyb.readString();
                        continue;
                    case 42:
                        this.zzaV = com_google_android_gms_internal_zzbyb.readString();
                        continue;
                    case 48:
                        this.zzaW = Long.valueOf(com_google_android_gms_internal_zzbyb.zzaeZ());
                        continue;
                    case 56:
                        this.zzaX = Long.valueOf(com_google_android_gms_internal_zzbyb.zzaeZ());
                        continue;
                    case 66:
                        this.zzaY = com_google_android_gms_internal_zzbyb.readString();
                        continue;
                    case XtraBox.MP4_XTRA_BT_GUID /*72*/:
                        this.zzaZ = Long.valueOf(com_google_android_gms_internal_zzbyb.zzaeZ());
                        continue;
                    case 82:
                        this.zzba = com_google_android_gms_internal_zzbyb.readString();
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
            if (this.zzaS != null) {
                zzu += zzbyc.zzr(1, this.zzaS);
            }
            if (this.zzaT != null) {
                zzu += zzbyc.zzf(2, this.zzaT.longValue());
            }
            if (this.stackTrace != null) {
                zzu += zzbyc.zzr(3, this.stackTrace);
            }
            if (this.zzaU != null) {
                zzu += zzbyc.zzr(4, this.zzaU);
            }
            if (this.zzaV != null) {
                zzu += zzbyc.zzr(5, this.zzaV);
            }
            if (this.zzaW != null) {
                zzu += zzbyc.zzf(6, this.zzaW.longValue());
            }
            if (this.zzaX != null) {
                zzu += zzbyc.zzf(7, this.zzaX.longValue());
            }
            if (this.zzaY != null) {
                zzu += zzbyc.zzr(8, this.zzaY);
            }
            if (this.zzaZ != null) {
                zzu += zzbyc.zzf(9, this.zzaZ.longValue());
            }
            return this.zzba != null ? zzu + zzbyc.zzr(10, this.zzba) : zzu;
        }
    }
}
