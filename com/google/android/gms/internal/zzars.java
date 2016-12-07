package com.google.android.gms.internal;

import java.io.IOException;
import org.telegram.tgnet.ConnectionsManager;

public final class zzars {
    private int btA;
    private int btB = ConnectionsManager.DEFAULT_DATACENTER_ID;
    private int btC;
    private int btD = 64;
    private int btE = 67108864;
    private int btw;
    private int btx;
    private int bty;
    private int btz;
    private final byte[] buffer;

    private zzars(byte[] bArr, int i, int i2) {
        this.buffer = bArr;
        this.btw = i;
        this.btx = i + i2;
        this.btz = i;
    }

    private void ch() {
        this.btx += this.bty;
        int i = this.btx;
        if (i > this.btB) {
            this.bty = i - this.btB;
            this.btx -= this.bty;
            return;
        }
        this.bty = 0;
    }

    public static int zzags(int i) {
        return (i >>> 1) ^ (-(i & 1));
    }

    public static zzars zzb(byte[] bArr, int i, int i2) {
        return new zzars(bArr, i, i2);
    }

    public static zzars zzbd(byte[] bArr) {
        return zzb(bArr, 0, bArr.length);
    }

    public static long zzct(long j) {
        return (j >>> 1) ^ (-(1 & j));
    }

    public int bU() throws IOException {
        if (cj()) {
            this.btA = 0;
            return 0;
        }
        this.btA = cd();
        if (this.btA != 0) {
            return this.btA;
        }
        throw zzarz.cu();
    }

    public void bV() throws IOException {
        int bU;
        do {
            bU = bU();
            if (bU == 0) {
                return;
            }
        } while (zzagr(bU));
    }

    public long bW() throws IOException {
        return ce();
    }

    public long bX() throws IOException {
        return ce();
    }

    public int bY() throws IOException {
        return cd();
    }

    public long bZ() throws IOException {
        return cg();
    }

    public boolean ca() throws IOException {
        return cd() != 0;
    }

    public int cb() throws IOException {
        return zzags(cd());
    }

    public long cc() throws IOException {
        return zzct(ce());
    }

    public int cd() throws IOException {
        byte ck = ck();
        if (ck >= (byte) 0) {
            return ck;
        }
        int i = ck & 127;
        byte ck2 = ck();
        if (ck2 >= (byte) 0) {
            return i | (ck2 << 7);
        }
        i |= (ck2 & 127) << 7;
        ck2 = ck();
        if (ck2 >= (byte) 0) {
            return i | (ck2 << 14);
        }
        i |= (ck2 & 127) << 14;
        ck2 = ck();
        if (ck2 >= (byte) 0) {
            return i | (ck2 << 21);
        }
        i |= (ck2 & 127) << 21;
        ck2 = ck();
        i |= ck2 << 28;
        if (ck2 >= (byte) 0) {
            return i;
        }
        for (int i2 = 0; i2 < 5; i2++) {
            if (ck() >= (byte) 0) {
                return i;
            }
        }
        throw zzarz.ct();
    }

    public long ce() throws IOException {
        long j = 0;
        for (int i = 0; i < 64; i += 7) {
            byte ck = ck();
            j |= ((long) (ck & 127)) << i;
            if ((ck & 128) == 0) {
                return j;
            }
        }
        throw zzarz.ct();
    }

    public int cf() throws IOException {
        return (((ck() & 255) | ((ck() & 255) << 8)) | ((ck() & 255) << 16)) | ((ck() & 255) << 24);
    }

    public long cg() throws IOException {
        byte ck = ck();
        byte ck2 = ck();
        return ((((((((((long) ck2) & 255) << 8) | (((long) ck) & 255)) | ((((long) ck()) & 255) << 16)) | ((((long) ck()) & 255) << 24)) | ((((long) ck()) & 255) << 32)) | ((((long) ck()) & 255) << 40)) | ((((long) ck()) & 255) << 48)) | ((((long) ck()) & 255) << 56);
    }

    public int ci() {
        if (this.btB == ConnectionsManager.DEFAULT_DATACENTER_ID) {
            return -1;
        }
        return this.btB - this.btz;
    }

    public boolean cj() {
        return this.btz == this.btx;
    }

    public byte ck() throws IOException {
        if (this.btz == this.btx) {
            throw zzarz.cr();
        }
        byte[] bArr = this.buffer;
        int i = this.btz;
        this.btz = i + 1;
        return bArr[i];
    }

    public int getPosition() {
        return this.btz - this.btw;
    }

    public byte[] readBytes() throws IOException {
        int cd = cd();
        if (cd < 0) {
            throw zzarz.cs();
        } else if (cd == 0) {
            return zzasd.btY;
        } else {
            if (cd > this.btx - this.btz) {
                throw zzarz.cr();
            }
            Object obj = new byte[cd];
            System.arraycopy(this.buffer, this.btz, obj, 0, cd);
            this.btz = cd + this.btz;
            return obj;
        }
    }

    public double readDouble() throws IOException {
        return Double.longBitsToDouble(cg());
    }

    public float readFloat() throws IOException {
        return Float.intBitsToFloat(cf());
    }

    public String readString() throws IOException {
        int cd = cd();
        if (cd < 0) {
            throw zzarz.cs();
        } else if (cd > this.btx - this.btz) {
            throw zzarz.cr();
        } else {
            String str = new String(this.buffer, this.btz, cd, zzary.UTF_8);
            this.btz = cd + this.btz;
            return str;
        }
    }

    public void zza(zzasa com_google_android_gms_internal_zzasa) throws IOException {
        int cd = cd();
        if (this.btC >= this.btD) {
            throw zzarz.cx();
        }
        cd = zzagt(cd);
        this.btC++;
        com_google_android_gms_internal_zzasa.zzb(this);
        zzagq(0);
        this.btC--;
        zzagu(cd);
    }

    public void zza(zzasa com_google_android_gms_internal_zzasa, int i) throws IOException {
        if (this.btC >= this.btD) {
            throw zzarz.cx();
        }
        this.btC++;
        com_google_android_gms_internal_zzasa.zzb(this);
        zzagq(zzasd.zzak(i, 4));
        this.btC--;
    }

    public byte[] zzae(int i, int i2) {
        if (i2 == 0) {
            return zzasd.btY;
        }
        Object obj = new byte[i2];
        System.arraycopy(this.buffer, this.btw + i, obj, 0, i2);
        return obj;
    }

    public void zzagq(int i) throws zzarz {
        if (this.btA != i) {
            throw zzarz.cv();
        }
    }

    public boolean zzagr(int i) throws IOException {
        switch (zzasd.zzahk(i)) {
            case 0:
                bY();
                return true;
            case 1:
                cg();
                return true;
            case 2:
                zzagw(cd());
                return true;
            case 3:
                bV();
                zzagq(zzasd.zzak(zzasd.zzahl(i), 4));
                return true;
            case 4:
                return false;
            case 5:
                cf();
                return true;
            default:
                throw zzarz.cw();
        }
    }

    public int zzagt(int i) throws zzarz {
        if (i < 0) {
            throw zzarz.cs();
        }
        int i2 = this.btz + i;
        int i3 = this.btB;
        if (i2 > i3) {
            throw zzarz.cr();
        }
        this.btB = i2;
        ch();
        return i3;
    }

    public void zzagu(int i) {
        this.btB = i;
        ch();
    }

    public void zzagv(int i) {
        if (i > this.btz - this.btw) {
            throw new IllegalArgumentException("Position " + i + " is beyond current " + (this.btz - this.btw));
        } else if (i < 0) {
            throw new IllegalArgumentException("Bad position " + i);
        } else {
            this.btz = this.btw + i;
        }
    }

    public void zzagw(int i) throws IOException {
        if (i < 0) {
            throw zzarz.cs();
        } else if (this.btz + i > this.btB) {
            zzagw(this.btB - this.btz);
            throw zzarz.cr();
        } else if (i <= this.btx - this.btz) {
            this.btz += i;
        } else {
            throw zzarz.cr();
        }
    }
}
