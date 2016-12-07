package com.google.android.gms.internal;

import java.io.IOException;
import org.telegram.tgnet.ConnectionsManager;

public final class zzarc {
    private int bql;
    private int bqm;
    private int bqn;
    private int bqo;
    private int bqp;
    private int bqq = ConnectionsManager.DEFAULT_DATACENTER_ID;
    private int bqr;
    private int bqs = 64;
    private int bqt = 67108864;
    private final byte[] buffer;

    private zzarc(byte[] bArr, int i, int i2) {
        this.buffer = bArr;
        this.bql = i;
        this.bqm = i + i2;
        this.bqo = i;
    }

    private void cJ() {
        this.bqm += this.bqn;
        int i = this.bqm;
        if (i > this.bqq) {
            this.bqn = i - this.bqq;
            this.bqm -= this.bqn;
            return;
        }
        this.bqn = 0;
    }

    public static int zzahb(int i) {
        return (i >>> 1) ^ (-(i & 1));
    }

    public static zzarc zzb(byte[] bArr, int i, int i2) {
        return new zzarc(bArr, i, i2);
    }

    public static zzarc zzbd(byte[] bArr) {
        return zzb(bArr, 0, bArr.length);
    }

    public static long zzcv(long j) {
        return (j >>> 1) ^ (-(1 & j));
    }

    public int cA() throws IOException {
        return cF();
    }

    public long cB() throws IOException {
        return cI();
    }

    public boolean cC() throws IOException {
        return cF() != 0;
    }

    public int cD() throws IOException {
        return zzahb(cF());
    }

    public long cE() throws IOException {
        return zzcv(cG());
    }

    public int cF() throws IOException {
        byte cM = cM();
        if (cM >= (byte) 0) {
            return cM;
        }
        int i = cM & 127;
        byte cM2 = cM();
        if (cM2 >= (byte) 0) {
            return i | (cM2 << 7);
        }
        i |= (cM2 & 127) << 7;
        cM2 = cM();
        if (cM2 >= (byte) 0) {
            return i | (cM2 << 14);
        }
        i |= (cM2 & 127) << 14;
        cM2 = cM();
        if (cM2 >= (byte) 0) {
            return i | (cM2 << 21);
        }
        i |= (cM2 & 127) << 21;
        cM2 = cM();
        i |= cM2 << 28;
        if (cM2 >= (byte) 0) {
            return i;
        }
        for (int i2 = 0; i2 < 5; i2++) {
            if (cM() >= (byte) 0) {
                return i;
            }
        }
        throw zzarj.cV();
    }

    public long cG() throws IOException {
        long j = 0;
        for (int i = 0; i < 64; i += 7) {
            byte cM = cM();
            j |= ((long) (cM & 127)) << i;
            if ((cM & 128) == 0) {
                return j;
            }
        }
        throw zzarj.cV();
    }

    public int cH() throws IOException {
        return (((cM() & 255) | ((cM() & 255) << 8)) | ((cM() & 255) << 16)) | ((cM() & 255) << 24);
    }

    public long cI() throws IOException {
        byte cM = cM();
        byte cM2 = cM();
        return ((((((((((long) cM2) & 255) << 8) | (((long) cM) & 255)) | ((((long) cM()) & 255) << 16)) | ((((long) cM()) & 255) << 24)) | ((((long) cM()) & 255) << 32)) | ((((long) cM()) & 255) << 40)) | ((((long) cM()) & 255) << 48)) | ((((long) cM()) & 255) << 56);
    }

    public int cK() {
        if (this.bqq == ConnectionsManager.DEFAULT_DATACENTER_ID) {
            return -1;
        }
        return this.bqq - this.bqo;
    }

    public boolean cL() {
        return this.bqo == this.bqm;
    }

    public byte cM() throws IOException {
        if (this.bqo == this.bqm) {
            throw zzarj.cT();
        }
        byte[] bArr = this.buffer;
        int i = this.bqo;
        this.bqo = i + 1;
        return bArr[i];
    }

    public int cw() throws IOException {
        if (cL()) {
            this.bqp = 0;
            return 0;
        }
        this.bqp = cF();
        if (this.bqp != 0) {
            return this.bqp;
        }
        throw zzarj.cW();
    }

    public void cx() throws IOException {
        int cw;
        do {
            cw = cw();
            if (cw == 0) {
                return;
            }
        } while (zzaha(cw));
    }

    public long cy() throws IOException {
        return cG();
    }

    public long cz() throws IOException {
        return cG();
    }

    public int getPosition() {
        return this.bqo - this.bql;
    }

    public byte[] readBytes() throws IOException {
        int cF = cF();
        if (cF < 0) {
            throw zzarj.cU();
        } else if (cF == 0) {
            return zzarn.bqM;
        } else {
            if (cF > this.bqm - this.bqo) {
                throw zzarj.cT();
            }
            Object obj = new byte[cF];
            System.arraycopy(this.buffer, this.bqo, obj, 0, cF);
            this.bqo = cF + this.bqo;
            return obj;
        }
    }

    public double readDouble() throws IOException {
        return Double.longBitsToDouble(cI());
    }

    public float readFloat() throws IOException {
        return Float.intBitsToFloat(cH());
    }

    public String readString() throws IOException {
        int cF = cF();
        if (cF < 0) {
            throw zzarj.cU();
        } else if (cF > this.bqm - this.bqo) {
            throw zzarj.cT();
        } else {
            String str = new String(this.buffer, this.bqo, cF, zzari.UTF_8);
            this.bqo = cF + this.bqo;
            return str;
        }
    }

    public void zza(zzark com_google_android_gms_internal_zzark) throws IOException {
        int cF = cF();
        if (this.bqr >= this.bqs) {
            throw zzarj.cZ();
        }
        cF = zzahc(cF);
        this.bqr++;
        com_google_android_gms_internal_zzark.zzb(this);
        zzagz(0);
        this.bqr--;
        zzahd(cF);
    }

    public void zza(zzark com_google_android_gms_internal_zzark, int i) throws IOException {
        if (this.bqr >= this.bqs) {
            throw zzarj.cZ();
        }
        this.bqr++;
        com_google_android_gms_internal_zzark.zzb(this);
        zzagz(zzarn.zzaj(i, 4));
        this.bqr--;
    }

    public byte[] zzad(int i, int i2) {
        if (i2 == 0) {
            return zzarn.bqM;
        }
        Object obj = new byte[i2];
        System.arraycopy(this.buffer, this.bql + i, obj, 0, i2);
        return obj;
    }

    public void zzagz(int i) throws zzarj {
        if (this.bqp != i) {
            throw zzarj.cX();
        }
    }

    public boolean zzaha(int i) throws IOException {
        switch (zzarn.zzaht(i)) {
            case 0:
                cA();
                return true;
            case 1:
                cI();
                return true;
            case 2:
                zzahf(cF());
                return true;
            case 3:
                cx();
                zzagz(zzarn.zzaj(zzarn.zzahu(i), 4));
                return true;
            case 4:
                return false;
            case 5:
                cH();
                return true;
            default:
                throw zzarj.cY();
        }
    }

    public int zzahc(int i) throws zzarj {
        if (i < 0) {
            throw zzarj.cU();
        }
        int i2 = this.bqo + i;
        int i3 = this.bqq;
        if (i2 > i3) {
            throw zzarj.cT();
        }
        this.bqq = i2;
        cJ();
        return i3;
    }

    public void zzahd(int i) {
        this.bqq = i;
        cJ();
    }

    public void zzahe(int i) {
        if (i > this.bqo - this.bql) {
            throw new IllegalArgumentException("Position " + i + " is beyond current " + (this.bqo - this.bql));
        } else if (i < 0) {
            throw new IllegalArgumentException("Bad position " + i);
        } else {
            this.bqo = this.bql + i;
        }
    }

    public void zzahf(int i) throws IOException {
        if (i < 0) {
            throw zzarj.cU();
        } else if (this.bqo + i > this.bqq) {
            zzahf(this.bqq - this.bqo);
            throw zzarj.cT();
        } else if (i <= this.bqm - this.bqo) {
            this.bqo += i;
        } else {
            throw zzarj.cT();
        }
    }
}
