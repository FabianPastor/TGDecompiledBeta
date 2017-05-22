package com.google.android.gms.internal;

import android.support.v4.view.MotionEventCompat;
import com.googlecode.mp4parser.authoring.tracks.h265.NalUnitTypes;
import java.io.IOException;
import java.util.Arrays;
import org.telegram.messenger.exoplayer2.extractor.ts.TsExtractor;

public interface zzbyo {

    public static final class zza extends zzbyd<zza> implements Cloneable {
        public String[] zzcwZ;
        public String[] zzcxa;
        public int[] zzcxb;
        public long[] zzcxc;
        public long[] zzcxd;

        public zza() {
            zzafD();
        }

        public /* synthetic */ Object clone() throws CloneNotSupportedException {
            return zzafE();
        }

        public boolean equals(Object obj) {
            if (obj == this) {
                return true;
            }
            if (!(obj instanceof zza)) {
                return false;
            }
            zza com_google_android_gms_internal_zzbyo_zza = (zza) obj;
            return (zzbyh.equals(this.zzcwZ, com_google_android_gms_internal_zzbyo_zza.zzcwZ) && zzbyh.equals(this.zzcxa, com_google_android_gms_internal_zzbyo_zza.zzcxa) && zzbyh.equals(this.zzcxb, com_google_android_gms_internal_zzbyo_zza.zzcxb) && zzbyh.equals(this.zzcxc, com_google_android_gms_internal_zzbyo_zza.zzcxc) && zzbyh.equals(this.zzcxd, com_google_android_gms_internal_zzbyo_zza.zzcxd)) ? (this.zzcwC == null || this.zzcwC.isEmpty()) ? com_google_android_gms_internal_zzbyo_zza.zzcwC == null || com_google_android_gms_internal_zzbyo_zza.zzcwC.isEmpty() : this.zzcwC.equals(com_google_android_gms_internal_zzbyo_zza.zzcwC) : false;
        }

        public int hashCode() {
            int hashCode = (((((((((((getClass().getName().hashCode() + 527) * 31) + zzbyh.hashCode(this.zzcwZ)) * 31) + zzbyh.hashCode(this.zzcxa)) * 31) + zzbyh.hashCode(this.zzcxb)) * 31) + zzbyh.hashCode(this.zzcxc)) * 31) + zzbyh.hashCode(this.zzcxd)) * 31;
            int hashCode2 = (this.zzcwC == null || this.zzcwC.isEmpty()) ? 0 : this.zzcwC.hashCode();
            return hashCode2 + hashCode;
        }

        public void zza(zzbyc com_google_android_gms_internal_zzbyc) throws IOException {
            int i = 0;
            if (this.zzcwZ != null && this.zzcwZ.length > 0) {
                for (String str : this.zzcwZ) {
                    if (str != null) {
                        com_google_android_gms_internal_zzbyc.zzq(1, str);
                    }
                }
            }
            if (this.zzcxa != null && this.zzcxa.length > 0) {
                for (String str2 : this.zzcxa) {
                    if (str2 != null) {
                        com_google_android_gms_internal_zzbyc.zzq(2, str2);
                    }
                }
            }
            if (this.zzcxb != null && this.zzcxb.length > 0) {
                for (int zzJ : this.zzcxb) {
                    com_google_android_gms_internal_zzbyc.zzJ(3, zzJ);
                }
            }
            if (this.zzcxc != null && this.zzcxc.length > 0) {
                for (long zzb : this.zzcxc) {
                    com_google_android_gms_internal_zzbyc.zzb(4, zzb);
                }
            }
            if (this.zzcxd != null && this.zzcxd.length > 0) {
                while (i < this.zzcxd.length) {
                    com_google_android_gms_internal_zzbyc.zzb(5, this.zzcxd[i]);
                    i++;
                }
            }
            super.zza(com_google_android_gms_internal_zzbyc);
        }

        public zza zzaW(zzbyb com_google_android_gms_internal_zzbyb) throws IOException {
            while (true) {
                int zzaeW = com_google_android_gms_internal_zzbyb.zzaeW();
                int zzb;
                Object obj;
                int zzrf;
                Object obj2;
                switch (zzaeW) {
                    case 0:
                        break;
                    case 10:
                        zzb = zzbym.zzb(com_google_android_gms_internal_zzbyb, 10);
                        zzaeW = this.zzcwZ == null ? 0 : this.zzcwZ.length;
                        obj = new String[(zzb + zzaeW)];
                        if (zzaeW != 0) {
                            System.arraycopy(this.zzcwZ, 0, obj, 0, zzaeW);
                        }
                        while (zzaeW < obj.length - 1) {
                            obj[zzaeW] = com_google_android_gms_internal_zzbyb.readString();
                            com_google_android_gms_internal_zzbyb.zzaeW();
                            zzaeW++;
                        }
                        obj[zzaeW] = com_google_android_gms_internal_zzbyb.readString();
                        this.zzcwZ = obj;
                        continue;
                    case 18:
                        zzb = zzbym.zzb(com_google_android_gms_internal_zzbyb, 18);
                        zzaeW = this.zzcxa == null ? 0 : this.zzcxa.length;
                        obj = new String[(zzb + zzaeW)];
                        if (zzaeW != 0) {
                            System.arraycopy(this.zzcxa, 0, obj, 0, zzaeW);
                        }
                        while (zzaeW < obj.length - 1) {
                            obj[zzaeW] = com_google_android_gms_internal_zzbyb.readString();
                            com_google_android_gms_internal_zzbyb.zzaeW();
                            zzaeW++;
                        }
                        obj[zzaeW] = com_google_android_gms_internal_zzbyb.readString();
                        this.zzcxa = obj;
                        continue;
                    case 24:
                        zzb = zzbym.zzb(com_google_android_gms_internal_zzbyb, 24);
                        zzaeW = this.zzcxb == null ? 0 : this.zzcxb.length;
                        obj = new int[(zzb + zzaeW)];
                        if (zzaeW != 0) {
                            System.arraycopy(this.zzcxb, 0, obj, 0, zzaeW);
                        }
                        while (zzaeW < obj.length - 1) {
                            obj[zzaeW] = com_google_android_gms_internal_zzbyb.zzafa();
                            com_google_android_gms_internal_zzbyb.zzaeW();
                            zzaeW++;
                        }
                        obj[zzaeW] = com_google_android_gms_internal_zzbyb.zzafa();
                        this.zzcxb = obj;
                        continue;
                    case NalUnitTypes.NAL_TYPE_RSV_VCL26 /*26*/:
                        zzrf = com_google_android_gms_internal_zzbyb.zzrf(com_google_android_gms_internal_zzbyb.zzaff());
                        zzb = com_google_android_gms_internal_zzbyb.getPosition();
                        zzaeW = 0;
                        while (com_google_android_gms_internal_zzbyb.zzafk() > 0) {
                            com_google_android_gms_internal_zzbyb.zzafa();
                            zzaeW++;
                        }
                        com_google_android_gms_internal_zzbyb.zzrh(zzb);
                        zzb = this.zzcxb == null ? 0 : this.zzcxb.length;
                        obj2 = new int[(zzaeW + zzb)];
                        if (zzb != 0) {
                            System.arraycopy(this.zzcxb, 0, obj2, 0, zzb);
                        }
                        while (zzb < obj2.length) {
                            obj2[zzb] = com_google_android_gms_internal_zzbyb.zzafa();
                            zzb++;
                        }
                        this.zzcxb = obj2;
                        com_google_android_gms_internal_zzbyb.zzrg(zzrf);
                        continue;
                    case 32:
                        zzb = zzbym.zzb(com_google_android_gms_internal_zzbyb, 32);
                        zzaeW = this.zzcxc == null ? 0 : this.zzcxc.length;
                        obj = new long[(zzb + zzaeW)];
                        if (zzaeW != 0) {
                            System.arraycopy(this.zzcxc, 0, obj, 0, zzaeW);
                        }
                        while (zzaeW < obj.length - 1) {
                            obj[zzaeW] = com_google_android_gms_internal_zzbyb.zzaeZ();
                            com_google_android_gms_internal_zzbyb.zzaeW();
                            zzaeW++;
                        }
                        obj[zzaeW] = com_google_android_gms_internal_zzbyb.zzaeZ();
                        this.zzcxc = obj;
                        continue;
                    case 34:
                        zzrf = com_google_android_gms_internal_zzbyb.zzrf(com_google_android_gms_internal_zzbyb.zzaff());
                        zzb = com_google_android_gms_internal_zzbyb.getPosition();
                        zzaeW = 0;
                        while (com_google_android_gms_internal_zzbyb.zzafk() > 0) {
                            com_google_android_gms_internal_zzbyb.zzaeZ();
                            zzaeW++;
                        }
                        com_google_android_gms_internal_zzbyb.zzrh(zzb);
                        zzb = this.zzcxc == null ? 0 : this.zzcxc.length;
                        obj2 = new long[(zzaeW + zzb)];
                        if (zzb != 0) {
                            System.arraycopy(this.zzcxc, 0, obj2, 0, zzb);
                        }
                        while (zzb < obj2.length) {
                            obj2[zzb] = com_google_android_gms_internal_zzbyb.zzaeZ();
                            zzb++;
                        }
                        this.zzcxc = obj2;
                        com_google_android_gms_internal_zzbyb.zzrg(zzrf);
                        continue;
                    case MotionEventCompat.AXIS_GENERIC_9 /*40*/:
                        zzb = zzbym.zzb(com_google_android_gms_internal_zzbyb, 40);
                        zzaeW = this.zzcxd == null ? 0 : this.zzcxd.length;
                        obj = new long[(zzb + zzaeW)];
                        if (zzaeW != 0) {
                            System.arraycopy(this.zzcxd, 0, obj, 0, zzaeW);
                        }
                        while (zzaeW < obj.length - 1) {
                            obj[zzaeW] = com_google_android_gms_internal_zzbyb.zzaeZ();
                            com_google_android_gms_internal_zzbyb.zzaeW();
                            zzaeW++;
                        }
                        obj[zzaeW] = com_google_android_gms_internal_zzbyb.zzaeZ();
                        this.zzcxd = obj;
                        continue;
                    case 42:
                        zzrf = com_google_android_gms_internal_zzbyb.zzrf(com_google_android_gms_internal_zzbyb.zzaff());
                        zzb = com_google_android_gms_internal_zzbyb.getPosition();
                        zzaeW = 0;
                        while (com_google_android_gms_internal_zzbyb.zzafk() > 0) {
                            com_google_android_gms_internal_zzbyb.zzaeZ();
                            zzaeW++;
                        }
                        com_google_android_gms_internal_zzbyb.zzrh(zzb);
                        zzb = this.zzcxd == null ? 0 : this.zzcxd.length;
                        obj2 = new long[(zzaeW + zzb)];
                        if (zzb != 0) {
                            System.arraycopy(this.zzcxd, 0, obj2, 0, zzb);
                        }
                        while (zzb < obj2.length) {
                            obj2[zzb] = com_google_android_gms_internal_zzbyb.zzaeZ();
                            zzb++;
                        }
                        this.zzcxd = obj2;
                        com_google_android_gms_internal_zzbyb.zzrg(zzrf);
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

        public zza zzafD() {
            this.zzcwZ = zzbym.EMPTY_STRING_ARRAY;
            this.zzcxa = zzbym.EMPTY_STRING_ARRAY;
            this.zzcxb = zzbym.zzcwQ;
            this.zzcxc = zzbym.zzcwR;
            this.zzcxd = zzbym.zzcwR;
            this.zzcwC = null;
            this.zzcwL = -1;
            return this;
        }

        public zza zzafE() {
            try {
                zza com_google_android_gms_internal_zzbyo_zza = (zza) super.zzafp();
                if (this.zzcwZ != null && this.zzcwZ.length > 0) {
                    com_google_android_gms_internal_zzbyo_zza.zzcwZ = (String[]) this.zzcwZ.clone();
                }
                if (this.zzcxa != null && this.zzcxa.length > 0) {
                    com_google_android_gms_internal_zzbyo_zza.zzcxa = (String[]) this.zzcxa.clone();
                }
                if (this.zzcxb != null && this.zzcxb.length > 0) {
                    com_google_android_gms_internal_zzbyo_zza.zzcxb = (int[]) this.zzcxb.clone();
                }
                if (this.zzcxc != null && this.zzcxc.length > 0) {
                    com_google_android_gms_internal_zzbyo_zza.zzcxc = (long[]) this.zzcxc.clone();
                }
                if (this.zzcxd != null && this.zzcxd.length > 0) {
                    com_google_android_gms_internal_zzbyo_zza.zzcxd = (long[]) this.zzcxd.clone();
                }
                return com_google_android_gms_internal_zzbyo_zza;
            } catch (CloneNotSupportedException e) {
                throw new AssertionError(e);
            }
        }

        public /* synthetic */ zzbyd zzafp() throws CloneNotSupportedException {
            return (zza) clone();
        }

        public /* synthetic */ zzbyj zzafq() throws CloneNotSupportedException {
            return (zza) clone();
        }

        public /* synthetic */ zzbyj zzb(zzbyb com_google_android_gms_internal_zzbyb) throws IOException {
            return zzaW(com_google_android_gms_internal_zzbyb);
        }

        protected int zzu() {
            int i;
            int i2;
            int i3;
            int i4 = 0;
            int zzu = super.zzu();
            if (this.zzcwZ == null || this.zzcwZ.length <= 0) {
                i = zzu;
            } else {
                i2 = 0;
                i3 = 0;
                for (String str : this.zzcwZ) {
                    if (str != null) {
                        i3++;
                        i2 += zzbyc.zzku(str);
                    }
                }
                i = (zzu + i2) + (i3 * 1);
            }
            if (this.zzcxa != null && this.zzcxa.length > 0) {
                i3 = 0;
                zzu = 0;
                for (String str2 : this.zzcxa) {
                    if (str2 != null) {
                        zzu++;
                        i3 += zzbyc.zzku(str2);
                    }
                }
                i = (i + i3) + (zzu * 1);
            }
            if (this.zzcxb != null && this.zzcxb.length > 0) {
                i3 = 0;
                for (int zzu2 : this.zzcxb) {
                    i3 += zzbyc.zzrl(zzu2);
                }
                i = (i + i3) + (this.zzcxb.length * 1);
            }
            if (this.zzcxc != null && this.zzcxc.length > 0) {
                i3 = 0;
                for (long zzbq : this.zzcxc) {
                    i3 += zzbyc.zzbq(zzbq);
                }
                i = (i + i3) + (this.zzcxc.length * 1);
            }
            if (this.zzcxd == null || this.zzcxd.length <= 0) {
                return i;
            }
            i2 = 0;
            while (i4 < this.zzcxd.length) {
                i2 += zzbyc.zzbq(this.zzcxd[i4]);
                i4++;
            }
            return (i + i2) + (this.zzcxd.length * 1);
        }
    }

    public static final class zzb extends zzbyd<zzb> implements Cloneable {
        public String version;
        public int zzbqV;
        public String zzcxe;

        public zzb() {
            zzafF();
        }

        public /* synthetic */ Object clone() throws CloneNotSupportedException {
            return zzafG();
        }

        public boolean equals(Object obj) {
            if (obj == this) {
                return true;
            }
            if (!(obj instanceof zzb)) {
                return false;
            }
            zzb com_google_android_gms_internal_zzbyo_zzb = (zzb) obj;
            if (this.zzbqV != com_google_android_gms_internal_zzbyo_zzb.zzbqV) {
                return false;
            }
            if (this.zzcxe == null) {
                if (com_google_android_gms_internal_zzbyo_zzb.zzcxe != null) {
                    return false;
                }
            } else if (!this.zzcxe.equals(com_google_android_gms_internal_zzbyo_zzb.zzcxe)) {
                return false;
            }
            if (this.version == null) {
                if (com_google_android_gms_internal_zzbyo_zzb.version != null) {
                    return false;
                }
            } else if (!this.version.equals(com_google_android_gms_internal_zzbyo_zzb.version)) {
                return false;
            }
            return (this.zzcwC == null || this.zzcwC.isEmpty()) ? com_google_android_gms_internal_zzbyo_zzb.zzcwC == null || com_google_android_gms_internal_zzbyo_zzb.zzcwC.isEmpty() : this.zzcwC.equals(com_google_android_gms_internal_zzbyo_zzb.zzcwC);
        }

        public int hashCode() {
            int i = 0;
            int hashCode = ((this.version == null ? 0 : this.version.hashCode()) + (((this.zzcxe == null ? 0 : this.zzcxe.hashCode()) + ((((getClass().getName().hashCode() + 527) * 31) + this.zzbqV) * 31)) * 31)) * 31;
            if (!(this.zzcwC == null || this.zzcwC.isEmpty())) {
                i = this.zzcwC.hashCode();
            }
            return hashCode + i;
        }

        public void zza(zzbyc com_google_android_gms_internal_zzbyc) throws IOException {
            if (this.zzbqV != 0) {
                com_google_android_gms_internal_zzbyc.zzJ(1, this.zzbqV);
            }
            if (!(this.zzcxe == null || this.zzcxe.equals(""))) {
                com_google_android_gms_internal_zzbyc.zzq(2, this.zzcxe);
            }
            if (!(this.version == null || this.version.equals(""))) {
                com_google_android_gms_internal_zzbyc.zzq(3, this.version);
            }
            super.zza(com_google_android_gms_internal_zzbyc);
        }

        public zzb zzaX(zzbyb com_google_android_gms_internal_zzbyb) throws IOException {
            while (true) {
                int zzaeW = com_google_android_gms_internal_zzbyb.zzaeW();
                switch (zzaeW) {
                    case 0:
                        break;
                    case 8:
                        this.zzbqV = com_google_android_gms_internal_zzbyb.zzafa();
                        continue;
                    case 18:
                        this.zzcxe = com_google_android_gms_internal_zzbyb.readString();
                        continue;
                    case NalUnitTypes.NAL_TYPE_RSV_VCL26 /*26*/:
                        this.version = com_google_android_gms_internal_zzbyb.readString();
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

        public zzb zzafF() {
            this.zzbqV = 0;
            this.zzcxe = "";
            this.version = "";
            this.zzcwC = null;
            this.zzcwL = -1;
            return this;
        }

        public zzb zzafG() {
            try {
                return (zzb) super.zzafp();
            } catch (CloneNotSupportedException e) {
                throw new AssertionError(e);
            }
        }

        public /* synthetic */ zzbyd zzafp() throws CloneNotSupportedException {
            return (zzb) clone();
        }

        public /* synthetic */ zzbyj zzafq() throws CloneNotSupportedException {
            return (zzb) clone();
        }

        public /* synthetic */ zzbyj zzb(zzbyb com_google_android_gms_internal_zzbyb) throws IOException {
            return zzaX(com_google_android_gms_internal_zzbyb);
        }

        protected int zzu() {
            int zzu = super.zzu();
            if (this.zzbqV != 0) {
                zzu += zzbyc.zzL(1, this.zzbqV);
            }
            if (!(this.zzcxe == null || this.zzcxe.equals(""))) {
                zzu += zzbyc.zzr(2, this.zzcxe);
            }
            return (this.version == null || this.version.equals("")) ? zzu : zzu + zzbyc.zzr(3, this.version);
        }
    }

    public static final class zzc extends zzbyd<zzc> implements Cloneable {
        public byte[] zzcxf;
        public String zzcxg;
        public byte[][] zzcxh;
        public boolean zzcxi;

        public zzc() {
            zzafH();
        }

        public /* synthetic */ Object clone() throws CloneNotSupportedException {
            return zzafI();
        }

        public boolean equals(Object obj) {
            if (obj == this) {
                return true;
            }
            if (!(obj instanceof zzc)) {
                return false;
            }
            zzc com_google_android_gms_internal_zzbyo_zzc = (zzc) obj;
            if (!Arrays.equals(this.zzcxf, com_google_android_gms_internal_zzbyo_zzc.zzcxf)) {
                return false;
            }
            if (this.zzcxg == null) {
                if (com_google_android_gms_internal_zzbyo_zzc.zzcxg != null) {
                    return false;
                }
            } else if (!this.zzcxg.equals(com_google_android_gms_internal_zzbyo_zzc.zzcxg)) {
                return false;
            }
            return (zzbyh.zza(this.zzcxh, com_google_android_gms_internal_zzbyo_zzc.zzcxh) && this.zzcxi == com_google_android_gms_internal_zzbyo_zzc.zzcxi) ? (this.zzcwC == null || this.zzcwC.isEmpty()) ? com_google_android_gms_internal_zzbyo_zzc.zzcwC == null || com_google_android_gms_internal_zzbyo_zzc.zzcwC.isEmpty() : this.zzcwC.equals(com_google_android_gms_internal_zzbyo_zzc.zzcwC) : false;
        }

        public int hashCode() {
            int i = 0;
            int hashCode = ((this.zzcxi ? 1231 : 1237) + (((((this.zzcxg == null ? 0 : this.zzcxg.hashCode()) + ((((getClass().getName().hashCode() + 527) * 31) + Arrays.hashCode(this.zzcxf)) * 31)) * 31) + zzbyh.zzb(this.zzcxh)) * 31)) * 31;
            if (!(this.zzcwC == null || this.zzcwC.isEmpty())) {
                i = this.zzcwC.hashCode();
            }
            return hashCode + i;
        }

        public void zza(zzbyc com_google_android_gms_internal_zzbyc) throws IOException {
            if (!Arrays.equals(this.zzcxf, zzbym.zzcwW)) {
                com_google_android_gms_internal_zzbyc.zzb(1, this.zzcxf);
            }
            if (this.zzcxh != null && this.zzcxh.length > 0) {
                for (byte[] bArr : this.zzcxh) {
                    if (bArr != null) {
                        com_google_android_gms_internal_zzbyc.zzb(2, bArr);
                    }
                }
            }
            if (this.zzcxi) {
                com_google_android_gms_internal_zzbyc.zzg(3, this.zzcxi);
            }
            if (!(this.zzcxg == null || this.zzcxg.equals(""))) {
                com_google_android_gms_internal_zzbyc.zzq(4, this.zzcxg);
            }
            super.zza(com_google_android_gms_internal_zzbyc);
        }

        public zzc zzaY(zzbyb com_google_android_gms_internal_zzbyb) throws IOException {
            while (true) {
                int zzaeW = com_google_android_gms_internal_zzbyb.zzaeW();
                switch (zzaeW) {
                    case 0:
                        break;
                    case 10:
                        this.zzcxf = com_google_android_gms_internal_zzbyb.readBytes();
                        continue;
                    case 18:
                        int zzb = zzbym.zzb(com_google_android_gms_internal_zzbyb, 18);
                        zzaeW = this.zzcxh == null ? 0 : this.zzcxh.length;
                        Object obj = new byte[(zzb + zzaeW)][];
                        if (zzaeW != 0) {
                            System.arraycopy(this.zzcxh, 0, obj, 0, zzaeW);
                        }
                        while (zzaeW < obj.length - 1) {
                            obj[zzaeW] = com_google_android_gms_internal_zzbyb.readBytes();
                            com_google_android_gms_internal_zzbyb.zzaeW();
                            zzaeW++;
                        }
                        obj[zzaeW] = com_google_android_gms_internal_zzbyb.readBytes();
                        this.zzcxh = obj;
                        continue;
                    case 24:
                        this.zzcxi = com_google_android_gms_internal_zzbyb.zzafc();
                        continue;
                    case 34:
                        this.zzcxg = com_google_android_gms_internal_zzbyb.readString();
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

        public zzc zzafH() {
            this.zzcxf = zzbym.zzcwW;
            this.zzcxg = "";
            this.zzcxh = zzbym.zzcwV;
            this.zzcxi = false;
            this.zzcwC = null;
            this.zzcwL = -1;
            return this;
        }

        public zzc zzafI() {
            try {
                zzc com_google_android_gms_internal_zzbyo_zzc = (zzc) super.zzafp();
                if (this.zzcxh != null && this.zzcxh.length > 0) {
                    com_google_android_gms_internal_zzbyo_zzc.zzcxh = (byte[][]) this.zzcxh.clone();
                }
                return com_google_android_gms_internal_zzbyo_zzc;
            } catch (CloneNotSupportedException e) {
                throw new AssertionError(e);
            }
        }

        public /* synthetic */ zzbyd zzafp() throws CloneNotSupportedException {
            return (zzc) clone();
        }

        public /* synthetic */ zzbyj zzafq() throws CloneNotSupportedException {
            return (zzc) clone();
        }

        public /* synthetic */ zzbyj zzb(zzbyb com_google_android_gms_internal_zzbyb) throws IOException {
            return zzaY(com_google_android_gms_internal_zzbyb);
        }

        protected int zzu() {
            int i = 0;
            int zzu = super.zzu();
            if (!Arrays.equals(this.zzcxf, zzbym.zzcwW)) {
                zzu += zzbyc.zzc(1, this.zzcxf);
            }
            if (this.zzcxh != null && this.zzcxh.length > 0) {
                int i2 = 0;
                int i3 = 0;
                while (i < this.zzcxh.length) {
                    byte[] bArr = this.zzcxh[i];
                    if (bArr != null) {
                        i3++;
                        i2 += zzbyc.zzaj(bArr);
                    }
                    i++;
                }
                zzu = (zzu + i2) + (i3 * 1);
            }
            if (this.zzcxi) {
                zzu += zzbyc.zzh(3, this.zzcxi);
            }
            return (this.zzcxg == null || this.zzcxg.equals("")) ? zzu : zzu + zzbyc.zzr(4, this.zzcxg);
        }
    }

    public static final class zzd extends zzbyd<zzd> implements Cloneable {
        public String tag;
        public boolean zzced;
        public zzf zzcnt;
        public int[] zzcxA;
        public long zzcxB;
        public long zzcxj;
        public long zzcxk;
        public long zzcxl;
        public int zzcxm;
        public zze[] zzcxn;
        public byte[] zzcxo;
        public zzb zzcxp;
        public byte[] zzcxq;
        public String zzcxr;
        public String zzcxs;
        public zza zzcxt;
        public String zzcxu;
        public long zzcxv;
        public zzc zzcxw;
        public byte[] zzcxx;
        public String zzcxy;
        public int zzcxz;
        public int zzri;

        public zzd() {
            zzafJ();
        }

        public /* synthetic */ Object clone() throws CloneNotSupportedException {
            return zzafK();
        }

        public boolean equals(Object obj) {
            if (obj == this) {
                return true;
            }
            if (!(obj instanceof zzd)) {
                return false;
            }
            zzd com_google_android_gms_internal_zzbyo_zzd = (zzd) obj;
            if (this.zzcxj != com_google_android_gms_internal_zzbyo_zzd.zzcxj || this.zzcxk != com_google_android_gms_internal_zzbyo_zzd.zzcxk || this.zzcxl != com_google_android_gms_internal_zzbyo_zzd.zzcxl) {
                return false;
            }
            if (this.tag == null) {
                if (com_google_android_gms_internal_zzbyo_zzd.tag != null) {
                    return false;
                }
            } else if (!this.tag.equals(com_google_android_gms_internal_zzbyo_zzd.tag)) {
                return false;
            }
            if (this.zzcxm != com_google_android_gms_internal_zzbyo_zzd.zzcxm || this.zzri != com_google_android_gms_internal_zzbyo_zzd.zzri || this.zzced != com_google_android_gms_internal_zzbyo_zzd.zzced || !zzbyh.equals(this.zzcxn, com_google_android_gms_internal_zzbyo_zzd.zzcxn) || !Arrays.equals(this.zzcxo, com_google_android_gms_internal_zzbyo_zzd.zzcxo)) {
                return false;
            }
            if (this.zzcxp == null) {
                if (com_google_android_gms_internal_zzbyo_zzd.zzcxp != null) {
                    return false;
                }
            } else if (!this.zzcxp.equals(com_google_android_gms_internal_zzbyo_zzd.zzcxp)) {
                return false;
            }
            if (!Arrays.equals(this.zzcxq, com_google_android_gms_internal_zzbyo_zzd.zzcxq)) {
                return false;
            }
            if (this.zzcxr == null) {
                if (com_google_android_gms_internal_zzbyo_zzd.zzcxr != null) {
                    return false;
                }
            } else if (!this.zzcxr.equals(com_google_android_gms_internal_zzbyo_zzd.zzcxr)) {
                return false;
            }
            if (this.zzcxs == null) {
                if (com_google_android_gms_internal_zzbyo_zzd.zzcxs != null) {
                    return false;
                }
            } else if (!this.zzcxs.equals(com_google_android_gms_internal_zzbyo_zzd.zzcxs)) {
                return false;
            }
            if (this.zzcxt == null) {
                if (com_google_android_gms_internal_zzbyo_zzd.zzcxt != null) {
                    return false;
                }
            } else if (!this.zzcxt.equals(com_google_android_gms_internal_zzbyo_zzd.zzcxt)) {
                return false;
            }
            if (this.zzcxu == null) {
                if (com_google_android_gms_internal_zzbyo_zzd.zzcxu != null) {
                    return false;
                }
            } else if (!this.zzcxu.equals(com_google_android_gms_internal_zzbyo_zzd.zzcxu)) {
                return false;
            }
            if (this.zzcxv != com_google_android_gms_internal_zzbyo_zzd.zzcxv) {
                return false;
            }
            if (this.zzcxw == null) {
                if (com_google_android_gms_internal_zzbyo_zzd.zzcxw != null) {
                    return false;
                }
            } else if (!this.zzcxw.equals(com_google_android_gms_internal_zzbyo_zzd.zzcxw)) {
                return false;
            }
            if (!Arrays.equals(this.zzcxx, com_google_android_gms_internal_zzbyo_zzd.zzcxx)) {
                return false;
            }
            if (this.zzcxy == null) {
                if (com_google_android_gms_internal_zzbyo_zzd.zzcxy != null) {
                    return false;
                }
            } else if (!this.zzcxy.equals(com_google_android_gms_internal_zzbyo_zzd.zzcxy)) {
                return false;
            }
            if (this.zzcxz != com_google_android_gms_internal_zzbyo_zzd.zzcxz || !zzbyh.equals(this.zzcxA, com_google_android_gms_internal_zzbyo_zzd.zzcxA) || this.zzcxB != com_google_android_gms_internal_zzbyo_zzd.zzcxB) {
                return false;
            }
            if (this.zzcnt == null) {
                if (com_google_android_gms_internal_zzbyo_zzd.zzcnt != null) {
                    return false;
                }
            } else if (!this.zzcnt.equals(com_google_android_gms_internal_zzbyo_zzd.zzcnt)) {
                return false;
            }
            return (this.zzcwC == null || this.zzcwC.isEmpty()) ? com_google_android_gms_internal_zzbyo_zzd.zzcwC == null || com_google_android_gms_internal_zzbyo_zzd.zzcwC.isEmpty() : this.zzcwC.equals(com_google_android_gms_internal_zzbyo_zzd.zzcwC);
        }

        public int hashCode() {
            int i = 0;
            int hashCode = ((this.zzcnt == null ? 0 : this.zzcnt.hashCode()) + (((((((((this.zzcxy == null ? 0 : this.zzcxy.hashCode()) + (((((this.zzcxw == null ? 0 : this.zzcxw.hashCode()) + (((((this.zzcxu == null ? 0 : this.zzcxu.hashCode()) + (((this.zzcxt == null ? 0 : this.zzcxt.hashCode()) + (((this.zzcxs == null ? 0 : this.zzcxs.hashCode()) + (((this.zzcxr == null ? 0 : this.zzcxr.hashCode()) + (((((this.zzcxp == null ? 0 : this.zzcxp.hashCode()) + (((((((this.zzced ? 1231 : 1237) + (((((((this.tag == null ? 0 : this.tag.hashCode()) + ((((((((getClass().getName().hashCode() + 527) * 31) + ((int) (this.zzcxj ^ (this.zzcxj >>> 32)))) * 31) + ((int) (this.zzcxk ^ (this.zzcxk >>> 32)))) * 31) + ((int) (this.zzcxl ^ (this.zzcxl >>> 32)))) * 31)) * 31) + this.zzcxm) * 31) + this.zzri) * 31)) * 31) + zzbyh.hashCode(this.zzcxn)) * 31) + Arrays.hashCode(this.zzcxo)) * 31)) * 31) + Arrays.hashCode(this.zzcxq)) * 31)) * 31)) * 31)) * 31)) * 31) + ((int) (this.zzcxv ^ (this.zzcxv >>> 32)))) * 31)) * 31) + Arrays.hashCode(this.zzcxx)) * 31)) * 31) + this.zzcxz) * 31) + zzbyh.hashCode(this.zzcxA)) * 31) + ((int) (this.zzcxB ^ (this.zzcxB >>> 32)))) * 31)) * 31;
            if (!(this.zzcwC == null || this.zzcwC.isEmpty())) {
                i = this.zzcwC.hashCode();
            }
            return hashCode + i;
        }

        public void zza(zzbyc com_google_android_gms_internal_zzbyc) throws IOException {
            int i = 0;
            if (this.zzcxj != 0) {
                com_google_android_gms_internal_zzbyc.zzb(1, this.zzcxj);
            }
            if (!(this.tag == null || this.tag.equals(""))) {
                com_google_android_gms_internal_zzbyc.zzq(2, this.tag);
            }
            if (this.zzcxn != null && this.zzcxn.length > 0) {
                for (zzbyj com_google_android_gms_internal_zzbyj : this.zzcxn) {
                    if (com_google_android_gms_internal_zzbyj != null) {
                        com_google_android_gms_internal_zzbyc.zza(3, com_google_android_gms_internal_zzbyj);
                    }
                }
            }
            if (!Arrays.equals(this.zzcxo, zzbym.zzcwW)) {
                com_google_android_gms_internal_zzbyc.zzb(4, this.zzcxo);
            }
            if (!Arrays.equals(this.zzcxq, zzbym.zzcwW)) {
                com_google_android_gms_internal_zzbyc.zzb(6, this.zzcxq);
            }
            if (this.zzcxt != null) {
                com_google_android_gms_internal_zzbyc.zza(7, this.zzcxt);
            }
            if (!(this.zzcxr == null || this.zzcxr.equals(""))) {
                com_google_android_gms_internal_zzbyc.zzq(8, this.zzcxr);
            }
            if (this.zzcxp != null) {
                com_google_android_gms_internal_zzbyc.zza(9, this.zzcxp);
            }
            if (this.zzced) {
                com_google_android_gms_internal_zzbyc.zzg(10, this.zzced);
            }
            if (this.zzcxm != 0) {
                com_google_android_gms_internal_zzbyc.zzJ(11, this.zzcxm);
            }
            if (this.zzri != 0) {
                com_google_android_gms_internal_zzbyc.zzJ(12, this.zzri);
            }
            if (!(this.zzcxs == null || this.zzcxs.equals(""))) {
                com_google_android_gms_internal_zzbyc.zzq(13, this.zzcxs);
            }
            if (!(this.zzcxu == null || this.zzcxu.equals(""))) {
                com_google_android_gms_internal_zzbyc.zzq(14, this.zzcxu);
            }
            if (this.zzcxv != 180000) {
                com_google_android_gms_internal_zzbyc.zzd(15, this.zzcxv);
            }
            if (this.zzcxw != null) {
                com_google_android_gms_internal_zzbyc.zza(16, this.zzcxw);
            }
            if (this.zzcxk != 0) {
                com_google_android_gms_internal_zzbyc.zzb(17, this.zzcxk);
            }
            if (!Arrays.equals(this.zzcxx, zzbym.zzcwW)) {
                com_google_android_gms_internal_zzbyc.zzb(18, this.zzcxx);
            }
            if (this.zzcxz != 0) {
                com_google_android_gms_internal_zzbyc.zzJ(19, this.zzcxz);
            }
            if (this.zzcxA != null && this.zzcxA.length > 0) {
                while (i < this.zzcxA.length) {
                    com_google_android_gms_internal_zzbyc.zzJ(20, this.zzcxA[i]);
                    i++;
                }
            }
            if (this.zzcxl != 0) {
                com_google_android_gms_internal_zzbyc.zzb(21, this.zzcxl);
            }
            if (this.zzcxB != 0) {
                com_google_android_gms_internal_zzbyc.zzb(22, this.zzcxB);
            }
            if (this.zzcnt != null) {
                com_google_android_gms_internal_zzbyc.zza(23, this.zzcnt);
            }
            if (!(this.zzcxy == null || this.zzcxy.equals(""))) {
                com_google_android_gms_internal_zzbyc.zzq(24, this.zzcxy);
            }
            super.zza(com_google_android_gms_internal_zzbyc);
        }

        public zzd zzaZ(zzbyb com_google_android_gms_internal_zzbyb) throws IOException {
            while (true) {
                int zzaeW = com_google_android_gms_internal_zzbyb.zzaeW();
                int zzb;
                Object obj;
                switch (zzaeW) {
                    case 0:
                        break;
                    case 8:
                        this.zzcxj = com_google_android_gms_internal_zzbyb.zzaeZ();
                        continue;
                    case 18:
                        this.tag = com_google_android_gms_internal_zzbyb.readString();
                        continue;
                    case NalUnitTypes.NAL_TYPE_RSV_VCL26 /*26*/:
                        zzb = zzbym.zzb(com_google_android_gms_internal_zzbyb, 26);
                        zzaeW = this.zzcxn == null ? 0 : this.zzcxn.length;
                        obj = new zze[(zzb + zzaeW)];
                        if (zzaeW != 0) {
                            System.arraycopy(this.zzcxn, 0, obj, 0, zzaeW);
                        }
                        while (zzaeW < obj.length - 1) {
                            obj[zzaeW] = new zze();
                            com_google_android_gms_internal_zzbyb.zza(obj[zzaeW]);
                            com_google_android_gms_internal_zzbyb.zzaeW();
                            zzaeW++;
                        }
                        obj[zzaeW] = new zze();
                        com_google_android_gms_internal_zzbyb.zza(obj[zzaeW]);
                        this.zzcxn = obj;
                        continue;
                    case 34:
                        this.zzcxo = com_google_android_gms_internal_zzbyb.readBytes();
                        continue;
                    case 50:
                        this.zzcxq = com_google_android_gms_internal_zzbyb.readBytes();
                        continue;
                    case 58:
                        if (this.zzcxt == null) {
                            this.zzcxt = new zza();
                        }
                        com_google_android_gms_internal_zzbyb.zza(this.zzcxt);
                        continue;
                    case 66:
                        this.zzcxr = com_google_android_gms_internal_zzbyb.readString();
                        continue;
                    case 74:
                        if (this.zzcxp == null) {
                            this.zzcxp = new zzb();
                        }
                        com_google_android_gms_internal_zzbyb.zza(this.zzcxp);
                        continue;
                    case 80:
                        this.zzced = com_google_android_gms_internal_zzbyb.zzafc();
                        continue;
                    case 88:
                        this.zzcxm = com_google_android_gms_internal_zzbyb.zzafa();
                        continue;
                    case 96:
                        this.zzri = com_google_android_gms_internal_zzbyb.zzafa();
                        continue;
                    case 106:
                        this.zzcxs = com_google_android_gms_internal_zzbyb.readString();
                        continue;
                    case 114:
                        this.zzcxu = com_google_android_gms_internal_zzbyb.readString();
                        continue;
                    case 120:
                        this.zzcxv = com_google_android_gms_internal_zzbyb.zzafe();
                        continue;
                    case TsExtractor.TS_STREAM_TYPE_HDMV_DTS /*130*/:
                        if (this.zzcxw == null) {
                            this.zzcxw = new zzc();
                        }
                        com_google_android_gms_internal_zzbyb.zza(this.zzcxw);
                        continue;
                    case 136:
                        this.zzcxk = com_google_android_gms_internal_zzbyb.zzaeZ();
                        continue;
                    case 146:
                        this.zzcxx = com_google_android_gms_internal_zzbyb.readBytes();
                        continue;
                    case 152:
                        zzaeW = com_google_android_gms_internal_zzbyb.zzafa();
                        switch (zzaeW) {
                            case 0:
                            case 1:
                            case 2:
                                this.zzcxz = zzaeW;
                                break;
                            default:
                                continue;
                        }
                    case 160:
                        zzb = zzbym.zzb(com_google_android_gms_internal_zzbyb, 160);
                        zzaeW = this.zzcxA == null ? 0 : this.zzcxA.length;
                        obj = new int[(zzb + zzaeW)];
                        if (zzaeW != 0) {
                            System.arraycopy(this.zzcxA, 0, obj, 0, zzaeW);
                        }
                        while (zzaeW < obj.length - 1) {
                            obj[zzaeW] = com_google_android_gms_internal_zzbyb.zzafa();
                            com_google_android_gms_internal_zzbyb.zzaeW();
                            zzaeW++;
                        }
                        obj[zzaeW] = com_google_android_gms_internal_zzbyb.zzafa();
                        this.zzcxA = obj;
                        continue;
                    case 162:
                        int zzrf = com_google_android_gms_internal_zzbyb.zzrf(com_google_android_gms_internal_zzbyb.zzaff());
                        zzb = com_google_android_gms_internal_zzbyb.getPosition();
                        zzaeW = 0;
                        while (com_google_android_gms_internal_zzbyb.zzafk() > 0) {
                            com_google_android_gms_internal_zzbyb.zzafa();
                            zzaeW++;
                        }
                        com_google_android_gms_internal_zzbyb.zzrh(zzb);
                        zzb = this.zzcxA == null ? 0 : this.zzcxA.length;
                        Object obj2 = new int[(zzaeW + zzb)];
                        if (zzb != 0) {
                            System.arraycopy(this.zzcxA, 0, obj2, 0, zzb);
                        }
                        while (zzb < obj2.length) {
                            obj2[zzb] = com_google_android_gms_internal_zzbyb.zzafa();
                            zzb++;
                        }
                        this.zzcxA = obj2;
                        com_google_android_gms_internal_zzbyb.zzrg(zzrf);
                        continue;
                    case 168:
                        this.zzcxl = com_google_android_gms_internal_zzbyb.zzaeZ();
                        continue;
                    case 176:
                        this.zzcxB = com_google_android_gms_internal_zzbyb.zzaeZ();
                        continue;
                    case 186:
                        if (this.zzcnt == null) {
                            this.zzcnt = new zzf();
                        }
                        com_google_android_gms_internal_zzbyb.zza(this.zzcnt);
                        continue;
                    case 194:
                        this.zzcxy = com_google_android_gms_internal_zzbyb.readString();
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

        public zzd zzafJ() {
            this.zzcxj = 0;
            this.zzcxk = 0;
            this.zzcxl = 0;
            this.tag = "";
            this.zzcxm = 0;
            this.zzri = 0;
            this.zzced = false;
            this.zzcxn = zze.zzafL();
            this.zzcxo = zzbym.zzcwW;
            this.zzcxp = null;
            this.zzcxq = zzbym.zzcwW;
            this.zzcxr = "";
            this.zzcxs = "";
            this.zzcxt = null;
            this.zzcxu = "";
            this.zzcxv = 180000;
            this.zzcxw = null;
            this.zzcxx = zzbym.zzcwW;
            this.zzcxy = "";
            this.zzcxz = 0;
            this.zzcxA = zzbym.zzcwQ;
            this.zzcxB = 0;
            this.zzcnt = null;
            this.zzcwC = null;
            this.zzcwL = -1;
            return this;
        }

        public zzd zzafK() {
            try {
                zzd com_google_android_gms_internal_zzbyo_zzd = (zzd) super.zzafp();
                if (this.zzcxn != null && this.zzcxn.length > 0) {
                    com_google_android_gms_internal_zzbyo_zzd.zzcxn = new zze[this.zzcxn.length];
                    for (int i = 0; i < this.zzcxn.length; i++) {
                        if (this.zzcxn[i] != null) {
                            com_google_android_gms_internal_zzbyo_zzd.zzcxn[i] = (zze) this.zzcxn[i].clone();
                        }
                    }
                }
                if (this.zzcxp != null) {
                    com_google_android_gms_internal_zzbyo_zzd.zzcxp = (zzb) this.zzcxp.clone();
                }
                if (this.zzcxt != null) {
                    com_google_android_gms_internal_zzbyo_zzd.zzcxt = (zza) this.zzcxt.clone();
                }
                if (this.zzcxw != null) {
                    com_google_android_gms_internal_zzbyo_zzd.zzcxw = (zzc) this.zzcxw.clone();
                }
                if (this.zzcxA != null && this.zzcxA.length > 0) {
                    com_google_android_gms_internal_zzbyo_zzd.zzcxA = (int[]) this.zzcxA.clone();
                }
                if (this.zzcnt != null) {
                    com_google_android_gms_internal_zzbyo_zzd.zzcnt = (zzf) this.zzcnt.clone();
                }
                return com_google_android_gms_internal_zzbyo_zzd;
            } catch (CloneNotSupportedException e) {
                throw new AssertionError(e);
            }
        }

        public /* synthetic */ zzbyd zzafp() throws CloneNotSupportedException {
            return (zzd) clone();
        }

        public /* synthetic */ zzbyj zzafq() throws CloneNotSupportedException {
            return (zzd) clone();
        }

        public /* synthetic */ zzbyj zzb(zzbyb com_google_android_gms_internal_zzbyb) throws IOException {
            return zzaZ(com_google_android_gms_internal_zzbyb);
        }

        protected int zzu() {
            int i;
            int i2 = 0;
            int zzu = super.zzu();
            if (this.zzcxj != 0) {
                zzu += zzbyc.zzf(1, this.zzcxj);
            }
            if (!(this.tag == null || this.tag.equals(""))) {
                zzu += zzbyc.zzr(2, this.tag);
            }
            if (this.zzcxn != null && this.zzcxn.length > 0) {
                i = zzu;
                for (zzbyj com_google_android_gms_internal_zzbyj : this.zzcxn) {
                    if (com_google_android_gms_internal_zzbyj != null) {
                        i += zzbyc.zzc(3, com_google_android_gms_internal_zzbyj);
                    }
                }
                zzu = i;
            }
            if (!Arrays.equals(this.zzcxo, zzbym.zzcwW)) {
                zzu += zzbyc.zzc(4, this.zzcxo);
            }
            if (!Arrays.equals(this.zzcxq, zzbym.zzcwW)) {
                zzu += zzbyc.zzc(6, this.zzcxq);
            }
            if (this.zzcxt != null) {
                zzu += zzbyc.zzc(7, this.zzcxt);
            }
            if (!(this.zzcxr == null || this.zzcxr.equals(""))) {
                zzu += zzbyc.zzr(8, this.zzcxr);
            }
            if (this.zzcxp != null) {
                zzu += zzbyc.zzc(9, this.zzcxp);
            }
            if (this.zzced) {
                zzu += zzbyc.zzh(10, this.zzced);
            }
            if (this.zzcxm != 0) {
                zzu += zzbyc.zzL(11, this.zzcxm);
            }
            if (this.zzri != 0) {
                zzu += zzbyc.zzL(12, this.zzri);
            }
            if (!(this.zzcxs == null || this.zzcxs.equals(""))) {
                zzu += zzbyc.zzr(13, this.zzcxs);
            }
            if (!(this.zzcxu == null || this.zzcxu.equals(""))) {
                zzu += zzbyc.zzr(14, this.zzcxu);
            }
            if (this.zzcxv != 180000) {
                zzu += zzbyc.zzh(15, this.zzcxv);
            }
            if (this.zzcxw != null) {
                zzu += zzbyc.zzc(16, this.zzcxw);
            }
            if (this.zzcxk != 0) {
                zzu += zzbyc.zzf(17, this.zzcxk);
            }
            if (!Arrays.equals(this.zzcxx, zzbym.zzcwW)) {
                zzu += zzbyc.zzc(18, this.zzcxx);
            }
            if (this.zzcxz != 0) {
                zzu += zzbyc.zzL(19, this.zzcxz);
            }
            if (this.zzcxA != null && this.zzcxA.length > 0) {
                i = 0;
                while (i2 < this.zzcxA.length) {
                    i += zzbyc.zzrl(this.zzcxA[i2]);
                    i2++;
                }
                zzu = (zzu + i) + (this.zzcxA.length * 2);
            }
            if (this.zzcxl != 0) {
                zzu += zzbyc.zzf(21, this.zzcxl);
            }
            if (this.zzcxB != 0) {
                zzu += zzbyc.zzf(22, this.zzcxB);
            }
            if (this.zzcnt != null) {
                zzu += zzbyc.zzc(23, this.zzcnt);
            }
            return (this.zzcxy == null || this.zzcxy.equals("")) ? zzu : zzu + zzbyc.zzr(24, this.zzcxy);
        }
    }

    public static final class zze extends zzbyd<zze> implements Cloneable {
        private static volatile zze[] zzcxC;
        public String value;
        public String zzaB;

        public zze() {
            zzafM();
        }

        public static zze[] zzafL() {
            if (zzcxC == null) {
                synchronized (zzbyh.zzcwK) {
                    if (zzcxC == null) {
                        zzcxC = new zze[0];
                    }
                }
            }
            return zzcxC;
        }

        public /* synthetic */ Object clone() throws CloneNotSupportedException {
            return zzafN();
        }

        public boolean equals(Object obj) {
            if (obj == this) {
                return true;
            }
            if (!(obj instanceof zze)) {
                return false;
            }
            zze com_google_android_gms_internal_zzbyo_zze = (zze) obj;
            if (this.zzaB == null) {
                if (com_google_android_gms_internal_zzbyo_zze.zzaB != null) {
                    return false;
                }
            } else if (!this.zzaB.equals(com_google_android_gms_internal_zzbyo_zze.zzaB)) {
                return false;
            }
            if (this.value == null) {
                if (com_google_android_gms_internal_zzbyo_zze.value != null) {
                    return false;
                }
            } else if (!this.value.equals(com_google_android_gms_internal_zzbyo_zze.value)) {
                return false;
            }
            return (this.zzcwC == null || this.zzcwC.isEmpty()) ? com_google_android_gms_internal_zzbyo_zze.zzcwC == null || com_google_android_gms_internal_zzbyo_zze.zzcwC.isEmpty() : this.zzcwC.equals(com_google_android_gms_internal_zzbyo_zze.zzcwC);
        }

        public int hashCode() {
            int i = 0;
            int hashCode = ((this.value == null ? 0 : this.value.hashCode()) + (((this.zzaB == null ? 0 : this.zzaB.hashCode()) + ((getClass().getName().hashCode() + 527) * 31)) * 31)) * 31;
            if (!(this.zzcwC == null || this.zzcwC.isEmpty())) {
                i = this.zzcwC.hashCode();
            }
            return hashCode + i;
        }

        public void zza(zzbyc com_google_android_gms_internal_zzbyc) throws IOException {
            if (!(this.zzaB == null || this.zzaB.equals(""))) {
                com_google_android_gms_internal_zzbyc.zzq(1, this.zzaB);
            }
            if (!(this.value == null || this.value.equals(""))) {
                com_google_android_gms_internal_zzbyc.zzq(2, this.value);
            }
            super.zza(com_google_android_gms_internal_zzbyc);
        }

        public zze zzafM() {
            this.zzaB = "";
            this.value = "";
            this.zzcwC = null;
            this.zzcwL = -1;
            return this;
        }

        public zze zzafN() {
            try {
                return (zze) super.zzafp();
            } catch (CloneNotSupportedException e) {
                throw new AssertionError(e);
            }
        }

        public /* synthetic */ zzbyd zzafp() throws CloneNotSupportedException {
            return (zze) clone();
        }

        public /* synthetic */ zzbyj zzafq() throws CloneNotSupportedException {
            return (zze) clone();
        }

        public /* synthetic */ zzbyj zzb(zzbyb com_google_android_gms_internal_zzbyb) throws IOException {
            return zzba(com_google_android_gms_internal_zzbyb);
        }

        public zze zzba(zzbyb com_google_android_gms_internal_zzbyb) throws IOException {
            while (true) {
                int zzaeW = com_google_android_gms_internal_zzbyb.zzaeW();
                switch (zzaeW) {
                    case 0:
                        break;
                    case 10:
                        this.zzaB = com_google_android_gms_internal_zzbyb.readString();
                        continue;
                    case 18:
                        this.value = com_google_android_gms_internal_zzbyb.readString();
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
            if (!(this.zzaB == null || this.zzaB.equals(""))) {
                zzu += zzbyc.zzr(1, this.zzaB);
            }
            return (this.value == null || this.value.equals("")) ? zzu : zzu + zzbyc.zzr(2, this.value);
        }
    }

    public static final class zzf extends zzbyd<zzf> implements Cloneable {
        public int zzcxD;
        public int zzcxE;

        public zzf() {
            zzafO();
        }

        public /* synthetic */ Object clone() throws CloneNotSupportedException {
            return zzafP();
        }

        public boolean equals(Object obj) {
            if (obj == this) {
                return true;
            }
            if (!(obj instanceof zzf)) {
                return false;
            }
            zzf com_google_android_gms_internal_zzbyo_zzf = (zzf) obj;
            return (this.zzcxD == com_google_android_gms_internal_zzbyo_zzf.zzcxD && this.zzcxE == com_google_android_gms_internal_zzbyo_zzf.zzcxE) ? (this.zzcwC == null || this.zzcwC.isEmpty()) ? com_google_android_gms_internal_zzbyo_zzf.zzcwC == null || com_google_android_gms_internal_zzbyo_zzf.zzcwC.isEmpty() : this.zzcwC.equals(com_google_android_gms_internal_zzbyo_zzf.zzcwC) : false;
        }

        public int hashCode() {
            int hashCode = (((((getClass().getName().hashCode() + 527) * 31) + this.zzcxD) * 31) + this.zzcxE) * 31;
            int hashCode2 = (this.zzcwC == null || this.zzcwC.isEmpty()) ? 0 : this.zzcwC.hashCode();
            return hashCode2 + hashCode;
        }

        public void zza(zzbyc com_google_android_gms_internal_zzbyc) throws IOException {
            if (this.zzcxD != -1) {
                com_google_android_gms_internal_zzbyc.zzJ(1, this.zzcxD);
            }
            if (this.zzcxE != 0) {
                com_google_android_gms_internal_zzbyc.zzJ(2, this.zzcxE);
            }
            super.zza(com_google_android_gms_internal_zzbyc);
        }

        public zzf zzafO() {
            this.zzcxD = -1;
            this.zzcxE = 0;
            this.zzcwC = null;
            this.zzcwL = -1;
            return this;
        }

        public zzf zzafP() {
            try {
                return (zzf) super.zzafp();
            } catch (CloneNotSupportedException e) {
                throw new AssertionError(e);
            }
        }

        public /* synthetic */ zzbyd zzafp() throws CloneNotSupportedException {
            return (zzf) clone();
        }

        public /* synthetic */ zzbyj zzafq() throws CloneNotSupportedException {
            return (zzf) clone();
        }

        public /* synthetic */ zzbyj zzb(zzbyb com_google_android_gms_internal_zzbyb) throws IOException {
            return zzbb(com_google_android_gms_internal_zzbyb);
        }

        public zzf zzbb(zzbyb com_google_android_gms_internal_zzbyb) throws IOException {
            while (true) {
                int zzaeW = com_google_android_gms_internal_zzbyb.zzaeW();
                switch (zzaeW) {
                    case 0:
                        break;
                    case 8:
                        zzaeW = com_google_android_gms_internal_zzbyb.zzafa();
                        switch (zzaeW) {
                            case -1:
                            case 0:
                            case 1:
                            case 2:
                            case 3:
                            case 4:
                            case 5:
                            case 6:
                            case 7:
                            case 8:
                            case 9:
                            case 10:
                            case 11:
                            case 12:
                            case 13:
                            case 14:
                            case 15:
                            case 16:
                            case 17:
                                this.zzcxD = zzaeW;
                                break;
                            default:
                                continue;
                        }
                    case 16:
                        zzaeW = com_google_android_gms_internal_zzbyb.zzafa();
                        switch (zzaeW) {
                            case 0:
                            case 1:
                            case 2:
                            case 3:
                            case 4:
                            case 5:
                            case 6:
                            case 7:
                            case 8:
                            case 9:
                            case 10:
                            case 11:
                            case 12:
                            case 13:
                            case 14:
                            case 15:
                            case 16:
                            case 100:
                                this.zzcxE = zzaeW;
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
            if (this.zzcxD != -1) {
                zzu += zzbyc.zzL(1, this.zzcxD);
            }
            return this.zzcxE != 0 ? zzu + zzbyc.zzL(2, this.zzcxE) : zzu;
        }
    }
}
