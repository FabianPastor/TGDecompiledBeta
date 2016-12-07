package com.google.android.gms.internal;

import com.googlecode.mp4parser.authoring.tracks.h265.NalUnitTypes;
import com.googlecode.mp4parser.boxes.microsoft.XtraBox;
import java.io.IOException;

public interface zzad {

    public static final class zza extends zzare<zza> {
        public String stackTrace;
        public String zzck;
        public Long zzcl;
        public String zzcm;
        public String zzcn;
        public Long zzco;
        public Long zzcp;
        public String zzcq;
        public Long zzcr;
        public String zzcs;

        public zza() {
            this.zzck = null;
            this.zzcl = null;
            this.stackTrace = null;
            this.zzcm = null;
            this.zzcn = null;
            this.zzco = null;
            this.zzcp = null;
            this.zzcq = null;
            this.zzcr = null;
            this.zzcs = null;
            this.bqE = -1;
        }

        public zza zza(zzarc com_google_android_gms_internal_zzarc) throws IOException {
            while (true) {
                int cw = com_google_android_gms_internal_zzarc.cw();
                switch (cw) {
                    case 0:
                        break;
                    case 10:
                        this.zzck = com_google_android_gms_internal_zzarc.readString();
                        continue;
                    case 16:
                        this.zzcl = Long.valueOf(com_google_android_gms_internal_zzarc.cz());
                        continue;
                    case NalUnitTypes.NAL_TYPE_RSV_VCL26 /*26*/:
                        this.stackTrace = com_google_android_gms_internal_zzarc.readString();
                        continue;
                    case 34:
                        this.zzcm = com_google_android_gms_internal_zzarc.readString();
                        continue;
                    case 42:
                        this.zzcn = com_google_android_gms_internal_zzarc.readString();
                        continue;
                    case 48:
                        this.zzco = Long.valueOf(com_google_android_gms_internal_zzarc.cz());
                        continue;
                    case 56:
                        this.zzcp = Long.valueOf(com_google_android_gms_internal_zzarc.cz());
                        continue;
                    case 66:
                        this.zzcq = com_google_android_gms_internal_zzarc.readString();
                        continue;
                    case XtraBox.MP4_XTRA_BT_GUID /*72*/:
                        this.zzcr = Long.valueOf(com_google_android_gms_internal_zzarc.cz());
                        continue;
                    case 82:
                        this.zzcs = com_google_android_gms_internal_zzarc.readString();
                        continue;
                    default:
                        if (!super.zza(com_google_android_gms_internal_zzarc, cw)) {
                            break;
                        }
                        continue;
                }
                return this;
            }
        }

        public void zza(zzard com_google_android_gms_internal_zzard) throws IOException {
            if (this.zzck != null) {
                com_google_android_gms_internal_zzard.zzr(1, this.zzck);
            }
            if (this.zzcl != null) {
                com_google_android_gms_internal_zzard.zzb(2, this.zzcl.longValue());
            }
            if (this.stackTrace != null) {
                com_google_android_gms_internal_zzard.zzr(3, this.stackTrace);
            }
            if (this.zzcm != null) {
                com_google_android_gms_internal_zzard.zzr(4, this.zzcm);
            }
            if (this.zzcn != null) {
                com_google_android_gms_internal_zzard.zzr(5, this.zzcn);
            }
            if (this.zzco != null) {
                com_google_android_gms_internal_zzard.zzb(6, this.zzco.longValue());
            }
            if (this.zzcp != null) {
                com_google_android_gms_internal_zzard.zzb(7, this.zzcp.longValue());
            }
            if (this.zzcq != null) {
                com_google_android_gms_internal_zzard.zzr(8, this.zzcq);
            }
            if (this.zzcr != null) {
                com_google_android_gms_internal_zzard.zzb(9, this.zzcr.longValue());
            }
            if (this.zzcs != null) {
                com_google_android_gms_internal_zzard.zzr(10, this.zzcs);
            }
            super.zza(com_google_android_gms_internal_zzard);
        }

        public /* synthetic */ zzark zzb(zzarc com_google_android_gms_internal_zzarc) throws IOException {
            return zza(com_google_android_gms_internal_zzarc);
        }

        protected int zzx() {
            int zzx = super.zzx();
            if (this.zzck != null) {
                zzx += zzard.zzs(1, this.zzck);
            }
            if (this.zzcl != null) {
                zzx += zzard.zzf(2, this.zzcl.longValue());
            }
            if (this.stackTrace != null) {
                zzx += zzard.zzs(3, this.stackTrace);
            }
            if (this.zzcm != null) {
                zzx += zzard.zzs(4, this.zzcm);
            }
            if (this.zzcn != null) {
                zzx += zzard.zzs(5, this.zzcn);
            }
            if (this.zzco != null) {
                zzx += zzard.zzf(6, this.zzco.longValue());
            }
            if (this.zzcp != null) {
                zzx += zzard.zzf(7, this.zzcp.longValue());
            }
            if (this.zzcq != null) {
                zzx += zzard.zzs(8, this.zzcq);
            }
            if (this.zzcr != null) {
                zzx += zzard.zzf(9, this.zzcr.longValue());
            }
            return this.zzcs != null ? zzx + zzard.zzs(10, this.zzcs) : zzx;
        }
    }
}
