package com.google.android.gms.internal;

import com.googlecode.mp4parser.authoring.tracks.h265.NalUnitTypes;
import com.googlecode.mp4parser.boxes.microsoft.XtraBox;
import java.io.IOException;

public interface zzae {

    public static final class zza extends zzbun<zza> {
        public String stackTrace;
        public String zzaR;
        public Long zzaS;
        public String zzaT;
        public String zzaU;
        public Long zzaV;
        public Long zzaW;
        public String zzaX;
        public Long zzaY;
        public String zzaZ;

        public zza() {
            this.zzaR = null;
            this.zzaS = null;
            this.stackTrace = null;
            this.zzaT = null;
            this.zzaU = null;
            this.zzaV = null;
            this.zzaW = null;
            this.zzaX = null;
            this.zzaY = null;
            this.zzaZ = null;
            this.zzcsg = -1;
        }

        public void zza(zzbum com_google_android_gms_internal_zzbum) throws IOException {
            if (this.zzaR != null) {
                com_google_android_gms_internal_zzbum.zzq(1, this.zzaR);
            }
            if (this.zzaS != null) {
                com_google_android_gms_internal_zzbum.zzb(2, this.zzaS.longValue());
            }
            if (this.stackTrace != null) {
                com_google_android_gms_internal_zzbum.zzq(3, this.stackTrace);
            }
            if (this.zzaT != null) {
                com_google_android_gms_internal_zzbum.zzq(4, this.zzaT);
            }
            if (this.zzaU != null) {
                com_google_android_gms_internal_zzbum.zzq(5, this.zzaU);
            }
            if (this.zzaV != null) {
                com_google_android_gms_internal_zzbum.zzb(6, this.zzaV.longValue());
            }
            if (this.zzaW != null) {
                com_google_android_gms_internal_zzbum.zzb(7, this.zzaW.longValue());
            }
            if (this.zzaX != null) {
                com_google_android_gms_internal_zzbum.zzq(8, this.zzaX);
            }
            if (this.zzaY != null) {
                com_google_android_gms_internal_zzbum.zzb(9, this.zzaY.longValue());
            }
            if (this.zzaZ != null) {
                com_google_android_gms_internal_zzbum.zzq(10, this.zzaZ);
            }
            super.zza(com_google_android_gms_internal_zzbum);
        }

        public /* synthetic */ zzbut zzb(zzbul com_google_android_gms_internal_zzbul) throws IOException {
            return zze(com_google_android_gms_internal_zzbul);
        }

        public zza zze(zzbul com_google_android_gms_internal_zzbul) throws IOException {
            while (true) {
                int zzacu = com_google_android_gms_internal_zzbul.zzacu();
                switch (zzacu) {
                    case 0:
                        break;
                    case 10:
                        this.zzaR = com_google_android_gms_internal_zzbul.readString();
                        continue;
                    case 16:
                        this.zzaS = Long.valueOf(com_google_android_gms_internal_zzbul.zzacx());
                        continue;
                    case NalUnitTypes.NAL_TYPE_RSV_VCL26 /*26*/:
                        this.stackTrace = com_google_android_gms_internal_zzbul.readString();
                        continue;
                    case 34:
                        this.zzaT = com_google_android_gms_internal_zzbul.readString();
                        continue;
                    case 42:
                        this.zzaU = com_google_android_gms_internal_zzbul.readString();
                        continue;
                    case 48:
                        this.zzaV = Long.valueOf(com_google_android_gms_internal_zzbul.zzacx());
                        continue;
                    case 56:
                        this.zzaW = Long.valueOf(com_google_android_gms_internal_zzbul.zzacx());
                        continue;
                    case 66:
                        this.zzaX = com_google_android_gms_internal_zzbul.readString();
                        continue;
                    case XtraBox.MP4_XTRA_BT_GUID /*72*/:
                        this.zzaY = Long.valueOf(com_google_android_gms_internal_zzbul.zzacx());
                        continue;
                    case 82:
                        this.zzaZ = com_google_android_gms_internal_zzbul.readString();
                        continue;
                    default:
                        if (!super.zza(com_google_android_gms_internal_zzbul, zzacu)) {
                            break;
                        }
                        continue;
                }
                return this;
            }
        }

        protected int zzv() {
            int zzv = super.zzv();
            if (this.zzaR != null) {
                zzv += zzbum.zzr(1, this.zzaR);
            }
            if (this.zzaS != null) {
                zzv += zzbum.zzf(2, this.zzaS.longValue());
            }
            if (this.stackTrace != null) {
                zzv += zzbum.zzr(3, this.stackTrace);
            }
            if (this.zzaT != null) {
                zzv += zzbum.zzr(4, this.zzaT);
            }
            if (this.zzaU != null) {
                zzv += zzbum.zzr(5, this.zzaU);
            }
            if (this.zzaV != null) {
                zzv += zzbum.zzf(6, this.zzaV.longValue());
            }
            if (this.zzaW != null) {
                zzv += zzbum.zzf(7, this.zzaW.longValue());
            }
            if (this.zzaX != null) {
                zzv += zzbum.zzr(8, this.zzaX);
            }
            if (this.zzaY != null) {
                zzv += zzbum.zzf(9, this.zzaY.longValue());
            }
            return this.zzaZ != null ? zzv + zzbum.zzr(10, this.zzaZ) : zzv;
        }
    }
}
