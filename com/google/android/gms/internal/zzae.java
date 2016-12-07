package com.google.android.gms.internal;

import com.googlecode.mp4parser.authoring.tracks.h265.NalUnitTypes;
import com.googlecode.mp4parser.boxes.microsoft.XtraBox;
import java.io.IOException;

public interface zzae {

    public static final class zza extends zzaru<zza> {
        public String stackTrace;
        public String zzcs;
        public Long zzct;
        public String zzcu;
        public String zzcv;
        public Long zzcw;
        public Long zzcx;
        public String zzcy;
        public Long zzcz;
        public String zzda;

        public zza() {
            this.zzcs = null;
            this.zzct = null;
            this.stackTrace = null;
            this.zzcu = null;
            this.zzcv = null;
            this.zzcw = null;
            this.zzcx = null;
            this.zzcy = null;
            this.zzcz = null;
            this.zzda = null;
            this.btP = -1;
        }

        public void zza(zzart com_google_android_gms_internal_zzart) throws IOException {
            if (this.zzcs != null) {
                com_google_android_gms_internal_zzart.zzq(1, this.zzcs);
            }
            if (this.zzct != null) {
                com_google_android_gms_internal_zzart.zzb(2, this.zzct.longValue());
            }
            if (this.stackTrace != null) {
                com_google_android_gms_internal_zzart.zzq(3, this.stackTrace);
            }
            if (this.zzcu != null) {
                com_google_android_gms_internal_zzart.zzq(4, this.zzcu);
            }
            if (this.zzcv != null) {
                com_google_android_gms_internal_zzart.zzq(5, this.zzcv);
            }
            if (this.zzcw != null) {
                com_google_android_gms_internal_zzart.zzb(6, this.zzcw.longValue());
            }
            if (this.zzcx != null) {
                com_google_android_gms_internal_zzart.zzb(7, this.zzcx.longValue());
            }
            if (this.zzcy != null) {
                com_google_android_gms_internal_zzart.zzq(8, this.zzcy);
            }
            if (this.zzcz != null) {
                com_google_android_gms_internal_zzart.zzb(9, this.zzcz.longValue());
            }
            if (this.zzda != null) {
                com_google_android_gms_internal_zzart.zzq(10, this.zzda);
            }
            super.zza(com_google_android_gms_internal_zzart);
        }

        public /* synthetic */ zzasa zzb(zzars com_google_android_gms_internal_zzars) throws IOException {
            return zze(com_google_android_gms_internal_zzars);
        }

        public zza zze(zzars com_google_android_gms_internal_zzars) throws IOException {
            while (true) {
                int bU = com_google_android_gms_internal_zzars.bU();
                switch (bU) {
                    case 0:
                        break;
                    case 10:
                        this.zzcs = com_google_android_gms_internal_zzars.readString();
                        continue;
                    case 16:
                        this.zzct = Long.valueOf(com_google_android_gms_internal_zzars.bX());
                        continue;
                    case NalUnitTypes.NAL_TYPE_RSV_VCL26 /*26*/:
                        this.stackTrace = com_google_android_gms_internal_zzars.readString();
                        continue;
                    case 34:
                        this.zzcu = com_google_android_gms_internal_zzars.readString();
                        continue;
                    case 42:
                        this.zzcv = com_google_android_gms_internal_zzars.readString();
                        continue;
                    case 48:
                        this.zzcw = Long.valueOf(com_google_android_gms_internal_zzars.bX());
                        continue;
                    case 56:
                        this.zzcx = Long.valueOf(com_google_android_gms_internal_zzars.bX());
                        continue;
                    case 66:
                        this.zzcy = com_google_android_gms_internal_zzars.readString();
                        continue;
                    case XtraBox.MP4_XTRA_BT_GUID /*72*/:
                        this.zzcz = Long.valueOf(com_google_android_gms_internal_zzars.bX());
                        continue;
                    case 82:
                        this.zzda = com_google_android_gms_internal_zzars.readString();
                        continue;
                    default:
                        if (!super.zza(com_google_android_gms_internal_zzars, bU)) {
                            break;
                        }
                        continue;
                }
                return this;
            }
        }

        protected int zzx() {
            int zzx = super.zzx();
            if (this.zzcs != null) {
                zzx += zzart.zzr(1, this.zzcs);
            }
            if (this.zzct != null) {
                zzx += zzart.zzf(2, this.zzct.longValue());
            }
            if (this.stackTrace != null) {
                zzx += zzart.zzr(3, this.stackTrace);
            }
            if (this.zzcu != null) {
                zzx += zzart.zzr(4, this.zzcu);
            }
            if (this.zzcv != null) {
                zzx += zzart.zzr(5, this.zzcv);
            }
            if (this.zzcw != null) {
                zzx += zzart.zzf(6, this.zzcw.longValue());
            }
            if (this.zzcx != null) {
                zzx += zzart.zzf(7, this.zzcx.longValue());
            }
            if (this.zzcy != null) {
                zzx += zzart.zzr(8, this.zzcy);
            }
            if (this.zzcz != null) {
                zzx += zzart.zzf(9, this.zzcz.longValue());
            }
            return this.zzda != null ? zzx + zzart.zzr(10, this.zzda) : zzx;
        }
    }
}
