package com.google.android.gms.internal;

import com.googlecode.mp4parser.authoring.tracks.h265.NalUnitTypes;
import java.io.IOException;

public interface zzad {

    public static final class zza extends zzasa {
        public zzb zzck;
        public zzc zzcl;

        public zza() {
            zzw();
        }

        public static zza zzc(byte[] bArr) throws zzarz {
            return (zza) zzasa.zza(new zza(), bArr);
        }

        public zza zza(zzars com_google_android_gms_internal_zzars) throws IOException {
            while (true) {
                int bU = com_google_android_gms_internal_zzars.bU();
                switch (bU) {
                    case 0:
                        break;
                    case 10:
                        if (this.zzck == null) {
                            this.zzck = new zzb();
                        }
                        com_google_android_gms_internal_zzars.zza(this.zzck);
                        continue;
                    case 18:
                        if (this.zzcl == null) {
                            this.zzcl = new zzc();
                        }
                        com_google_android_gms_internal_zzars.zza(this.zzcl);
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

        public void zza(zzart com_google_android_gms_internal_zzart) throws IOException {
            if (this.zzck != null) {
                com_google_android_gms_internal_zzart.zza(1, this.zzck);
            }
            if (this.zzcl != null) {
                com_google_android_gms_internal_zzart.zza(2, this.zzcl);
            }
            super.zza(com_google_android_gms_internal_zzart);
        }

        public /* synthetic */ zzasa zzb(zzars com_google_android_gms_internal_zzars) throws IOException {
            return zza(com_google_android_gms_internal_zzars);
        }

        public zza zzw() {
            this.zzck = null;
            this.zzcl = null;
            this.btP = -1;
            return this;
        }

        protected int zzx() {
            int zzx = super.zzx();
            if (this.zzck != null) {
                zzx += zzart.zzc(1, this.zzck);
            }
            return this.zzcl != null ? zzx + zzart.zzc(2, this.zzcl) : zzx;
        }
    }

    public static final class zzb extends zzasa {
        public Integer zzcm;

        public zzb() {
            zzy();
        }

        public void zza(zzart com_google_android_gms_internal_zzart) throws IOException {
            if (this.zzcm != null) {
                com_google_android_gms_internal_zzart.zzaf(27, this.zzcm.intValue());
            }
            super.zza(com_google_android_gms_internal_zzart);
        }

        public /* synthetic */ zzasa zzb(zzars com_google_android_gms_internal_zzars) throws IOException {
            return zzc(com_google_android_gms_internal_zzars);
        }

        public zzb zzc(zzars com_google_android_gms_internal_zzars) throws IOException {
            while (true) {
                int bU = com_google_android_gms_internal_zzars.bU();
                switch (bU) {
                    case 0:
                        break;
                    case 216:
                        bU = com_google_android_gms_internal_zzars.bY();
                        switch (bU) {
                            case 0:
                            case 1:
                            case 2:
                            case 3:
                            case 4:
                                this.zzcm = Integer.valueOf(bU);
                                break;
                            default:
                                continue;
                        }
                    default:
                        if (!zzasd.zzb(com_google_android_gms_internal_zzars, bU)) {
                            break;
                        }
                        continue;
                }
                return this;
            }
        }

        protected int zzx() {
            int zzx = super.zzx();
            return this.zzcm != null ? zzx + zzart.zzah(27, this.zzcm.intValue()) : zzx;
        }

        public zzb zzy() {
            this.btP = -1;
            return this;
        }
    }

    public static final class zzc extends zzasa {
        public String zzcn;
        public String zzco;
        public String zzcp;
        public String zzcq;
        public String zzcr;

        public zzc() {
            zzz();
        }

        public void zza(zzart com_google_android_gms_internal_zzart) throws IOException {
            if (this.zzcn != null) {
                com_google_android_gms_internal_zzart.zzq(1, this.zzcn);
            }
            if (this.zzco != null) {
                com_google_android_gms_internal_zzart.zzq(2, this.zzco);
            }
            if (this.zzcp != null) {
                com_google_android_gms_internal_zzart.zzq(3, this.zzcp);
            }
            if (this.zzcq != null) {
                com_google_android_gms_internal_zzart.zzq(4, this.zzcq);
            }
            if (this.zzcr != null) {
                com_google_android_gms_internal_zzart.zzq(5, this.zzcr);
            }
            super.zza(com_google_android_gms_internal_zzart);
        }

        public /* synthetic */ zzasa zzb(zzars com_google_android_gms_internal_zzars) throws IOException {
            return zzd(com_google_android_gms_internal_zzars);
        }

        public zzc zzd(zzars com_google_android_gms_internal_zzars) throws IOException {
            while (true) {
                int bU = com_google_android_gms_internal_zzars.bU();
                switch (bU) {
                    case 0:
                        break;
                    case 10:
                        this.zzcn = com_google_android_gms_internal_zzars.readString();
                        continue;
                    case 18:
                        this.zzco = com_google_android_gms_internal_zzars.readString();
                        continue;
                    case NalUnitTypes.NAL_TYPE_RSV_VCL26 /*26*/:
                        this.zzcp = com_google_android_gms_internal_zzars.readString();
                        continue;
                    case 34:
                        this.zzcq = com_google_android_gms_internal_zzars.readString();
                        continue;
                    case 42:
                        this.zzcr = com_google_android_gms_internal_zzars.readString();
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

        protected int zzx() {
            int zzx = super.zzx();
            if (this.zzcn != null) {
                zzx += zzart.zzr(1, this.zzcn);
            }
            if (this.zzco != null) {
                zzx += zzart.zzr(2, this.zzco);
            }
            if (this.zzcp != null) {
                zzx += zzart.zzr(3, this.zzcp);
            }
            if (this.zzcq != null) {
                zzx += zzart.zzr(4, this.zzcq);
            }
            return this.zzcr != null ? zzx + zzart.zzr(5, this.zzcr) : zzx;
        }

        public zzc zzz() {
            this.zzcn = null;
            this.zzco = null;
            this.zzcp = null;
            this.zzcq = null;
            this.zzcr = null;
            this.btP = -1;
            return this;
        }
    }
}
