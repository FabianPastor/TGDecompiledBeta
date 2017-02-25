package com.google.android.gms.internal;

import java.io.IOException;
import org.telegram.tgnet.ConnectionsManager;

public final class zzbxl {
    private final byte[] buffer;
    private int zzcuq;
    private int zzcur;
    private int zzcus;
    private int zzcut;
    private int zzcuu;
    private int zzcuv = ConnectionsManager.DEFAULT_DATACENTER_ID;
    private int zzcuw;
    private int zzcux = 64;
    private int zzcuy = ConnectionsManager.FileTypeFile;

    private zzbxl(byte[] bArr, int i, int i2) {
        this.buffer = bArr;
        this.zzcuq = i;
        this.zzcur = i + i2;
        this.zzcut = i;
    }

    public static long zzaZ(long j) {
        return (j >>> 1) ^ (-(1 & j));
    }

    private void zzaeA() {
        this.zzcur += this.zzcus;
        int i = this.zzcur;
        if (i > this.zzcuv) {
            this.zzcus = i - this.zzcuv;
            this.zzcur -= this.zzcus;
            return;
        }
        this.zzcus = 0;
    }

    public static zzbxl zzaf(byte[] bArr) {
        return zzb(bArr, 0, bArr.length);
    }

    public static zzbxl zzb(byte[] bArr, int i, int i2) {
        return new zzbxl(bArr, i, i2);
    }

    public static int zzqY(int i) {
        return (i >>> 1) ^ (-(i & 1));
    }

    public int getPosition() {
        return this.zzcut - this.zzcuq;
    }

    public byte[] readBytes() throws IOException {
        int zzaew = zzaew();
        if (zzaew < 0) {
            throw zzbxs.zzaeL();
        } else if (zzaew == 0) {
            return zzbxw.zzcuV;
        } else {
            if (zzaew > this.zzcur - this.zzcut) {
                throw zzbxs.zzaeK();
            }
            Object obj = new byte[zzaew];
            System.arraycopy(this.buffer, this.zzcut, obj, 0, zzaew);
            this.zzcut = zzaew + this.zzcut;
            return obj;
        }
    }

    public double readDouble() throws IOException {
        return Double.longBitsToDouble(zzaez());
    }

    public float readFloat() throws IOException {
        return Float.intBitsToFloat(zzaey());
    }

    public String readString() throws IOException {
        int zzaew = zzaew();
        if (zzaew < 0) {
            throw zzbxs.zzaeL();
        } else if (zzaew > this.zzcur - this.zzcut) {
            throw zzbxs.zzaeK();
        } else {
            String str = new String(this.buffer, this.zzcut, zzaew, zzbxr.UTF_8);
            this.zzcut = zzaew + this.zzcut;
            return str;
        }
    }

    public byte[] zzI(int i, int i2) {
        if (i2 == 0) {
            return zzbxw.zzcuV;
        }
        Object obj = new byte[i2];
        System.arraycopy(this.buffer, this.zzcuq + i, obj, 0, i2);
        return obj;
    }

    public void zza(zzbxt com_google_android_gms_internal_zzbxt) throws IOException {
        int zzaew = zzaew();
        if (this.zzcuw >= this.zzcux) {
            throw zzbxs.zzaeQ();
        }
        zzaew = zzqZ(zzaew);
        this.zzcuw++;
        com_google_android_gms_internal_zzbxt.zzb(this);
        zzqW(0);
        this.zzcuw--;
        zzra(zzaew);
    }

    public void zza(zzbxt com_google_android_gms_internal_zzbxt, int i) throws IOException {
        if (this.zzcuw >= this.zzcux) {
            throw zzbxs.zzaeQ();
        }
        this.zzcuw++;
        com_google_android_gms_internal_zzbxt.zzb(this);
        zzqW(zzbxw.zzO(i, 4));
        this.zzcuw--;
    }

    public int zzaeB() {
        if (this.zzcuv == ConnectionsManager.DEFAULT_DATACENTER_ID) {
            return -1;
        }
        return this.zzcuv - this.zzcut;
    }

    public boolean zzaeC() {
        return this.zzcut == this.zzcur;
    }

    public byte zzaeD() throws IOException {
        if (this.zzcut == this.zzcur) {
            throw zzbxs.zzaeK();
        }
        byte[] bArr = this.buffer;
        int i = this.zzcut;
        this.zzcut = i + 1;
        return bArr[i];
    }

    public int zzaen() throws IOException {
        if (zzaeC()) {
            this.zzcuu = 0;
            return 0;
        }
        this.zzcuu = zzaew();
        if (this.zzcuu != 0) {
            return this.zzcuu;
        }
        throw zzbxs.zzaeN();
    }

    public void zzaeo() throws IOException {
        int zzaen;
        do {
            zzaen = zzaen();
            if (zzaen == 0) {
                return;
            }
        } while (zzqX(zzaen));
    }

    public long zzaep() throws IOException {
        return zzaex();
    }

    public long zzaeq() throws IOException {
        return zzaex();
    }

    public int zzaer() throws IOException {
        return zzaew();
    }

    public long zzaes() throws IOException {
        return zzaez();
    }

    public boolean zzaet() throws IOException {
        return zzaew() != 0;
    }

    public int zzaeu() throws IOException {
        return zzqY(zzaew());
    }

    public long zzaev() throws IOException {
        return zzaZ(zzaex());
    }

    public int zzaew() throws IOException {
        byte zzaeD = zzaeD();
        if (zzaeD >= (byte) 0) {
            return zzaeD;
        }
        int i = zzaeD & 127;
        byte zzaeD2 = zzaeD();
        if (zzaeD2 >= (byte) 0) {
            return i | (zzaeD2 << 7);
        }
        i |= (zzaeD2 & 127) << 7;
        zzaeD2 = zzaeD();
        if (zzaeD2 >= (byte) 0) {
            return i | (zzaeD2 << 14);
        }
        i |= (zzaeD2 & 127) << 14;
        zzaeD2 = zzaeD();
        if (zzaeD2 >= (byte) 0) {
            return i | (zzaeD2 << 21);
        }
        i |= (zzaeD2 & 127) << 21;
        zzaeD2 = zzaeD();
        i |= zzaeD2 << 28;
        if (zzaeD2 >= (byte) 0) {
            return i;
        }
        for (int i2 = 0; i2 < 5; i2++) {
            if (zzaeD() >= (byte) 0) {
                return i;
            }
        }
        throw zzbxs.zzaeM();
    }

    public long zzaex() throws IOException {
        long j = 0;
        for (int i = 0; i < 64; i += 7) {
            byte zzaeD = zzaeD();
            j |= ((long) (zzaeD & 127)) << i;
            if ((zzaeD & 128) == 0) {
                return j;
            }
        }
        throw zzbxs.zzaeM();
    }

    public int zzaey() throws IOException {
        return (((zzaeD() & 255) | ((zzaeD() & 255) << 8)) | ((zzaeD() & 255) << 16)) | ((zzaeD() & 255) << 24);
    }

    public long zzaez() throws IOException {
        byte zzaeD = zzaeD();
        byte zzaeD2 = zzaeD();
        return ((((((((((long) zzaeD2) & 255) << 8) | (((long) zzaeD) & 255)) | ((((long) zzaeD()) & 255) << 16)) | ((((long) zzaeD()) & 255) << 24)) | ((((long) zzaeD()) & 255) << 32)) | ((((long) zzaeD()) & 255) << 40)) | ((((long) zzaeD()) & 255) << 48)) | ((((long) zzaeD()) & 255) << 56);
    }

    public void zzqW(int i) throws zzbxs {
        if (this.zzcuu != i) {
            throw zzbxs.zzaeO();
        }
    }

    public boolean zzqX(int i) throws IOException {
        switch (zzbxw.zzrq(i)) {
            case 0:
                zzaer();
                return true;
            case 1:
                zzaez();
                return true;
            case 2:
                zzrc(zzaew());
                return true;
            case 3:
                zzaeo();
                zzqW(zzbxw.zzO(zzbxw.zzrr(i), 4));
                return true;
            case 4:
                return false;
            case 5:
                zzaey();
                return true;
            default:
                throw zzbxs.zzaeP();
        }
    }

    public int zzqZ(int i) throws zzbxs {
        if (i < 0) {
            throw zzbxs.zzaeL();
        }
        int i2 = this.zzcut + i;
        int i3 = this.zzcuv;
        if (i2 > i3) {
            throw zzbxs.zzaeK();
        }
        this.zzcuv = i2;
        zzaeA();
        return i3;
    }

    public void zzra(int i) {
        this.zzcuv = i;
        zzaeA();
    }

    public void zzrb(int i) {
        if (i > this.zzcut - this.zzcuq) {
            throw new IllegalArgumentException("Position " + i + " is beyond current " + (this.zzcut - this.zzcuq));
        } else if (i < 0) {
            throw new IllegalArgumentException("Bad position " + i);
        } else {
            this.zzcut = this.zzcuq + i;
        }
    }

    public void zzrc(int i) throws IOException {
        if (i < 0) {
            throw zzbxs.zzaeL();
        } else if (this.zzcut + i > this.zzcuv) {
            zzrc(this.zzcuv - this.zzcut);
            throw zzbxs.zzaeK();
        } else if (i <= this.zzcur - this.zzcut) {
            this.zzcut += i;
        } else {
            throw zzbxs.zzaeK();
        }
    }
}
