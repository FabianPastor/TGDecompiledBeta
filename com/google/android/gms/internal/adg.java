package com.google.android.gms.internal;

import java.io.IOException;
import org.telegram.tgnet.ConnectionsManager;

public final class adg {
    private final byte[] buffer;
    private int zzcrT;
    private int zzcrU;
    private int zzcrV;
    private int zzcrW;
    private int zzcrX;
    private int zzcrY = ConnectionsManager.DEFAULT_DATACENTER_ID;
    private int zzcrZ;
    private int zzcsa = 64;
    private int zzcsb = ConnectionsManager.FileTypeFile;

    private adg(byte[] bArr, int i, int i2) {
        this.buffer = bArr;
        this.zzcrT = i;
        this.zzcrU = i + i2;
        this.zzcrW = i;
    }

    public static adg zzH(byte[] bArr) {
        return zzb(bArr, 0, bArr.length);
    }

    private final void zzLK() {
        this.zzcrU += this.zzcrV;
        int i = this.zzcrU;
        if (i > this.zzcrY) {
            this.zzcrV = i - this.zzcrY;
            this.zzcrU -= this.zzcrV;
            return;
        }
        this.zzcrV = 0;
    }

    private final byte zzLM() throws IOException {
        if (this.zzcrW == this.zzcrU) {
            throw ado.zzLR();
        }
        byte[] bArr = this.buffer;
        int i = this.zzcrW;
        this.zzcrW = i + 1;
        return bArr[i];
    }

    public static adg zzb(byte[] bArr, int i, int i2) {
        return new adg(bArr, 0, i2);
    }

    private final void zzcq(int i) throws IOException {
        if (i < 0) {
            throw ado.zzLS();
        } else if (this.zzcrW + i > this.zzcrY) {
            zzcq(this.zzcrY - this.zzcrW);
            throw ado.zzLR();
        } else if (i <= this.zzcrU - this.zzcrW) {
            this.zzcrW += i;
        } else {
            throw ado.zzLR();
        }
    }

    public final int getPosition() {
        return this.zzcrW - this.zzcrT;
    }

    public final byte[] readBytes() throws IOException {
        int zzLG = zzLG();
        if (zzLG < 0) {
            throw ado.zzLS();
        } else if (zzLG == 0) {
            return ads.zzcsx;
        } else {
            if (zzLG > this.zzcrU - this.zzcrW) {
                throw ado.zzLR();
            }
            Object obj = new byte[zzLG];
            System.arraycopy(this.buffer, this.zzcrW, obj, 0, zzLG);
            this.zzcrW = zzLG + this.zzcrW;
            return obj;
        }
    }

    public final String readString() throws IOException {
        int zzLG = zzLG();
        if (zzLG < 0) {
            throw ado.zzLS();
        } else if (zzLG > this.zzcrU - this.zzcrW) {
            throw ado.zzLR();
        } else {
            String str = new String(this.buffer, this.zzcrW, zzLG, adn.UTF_8);
            this.zzcrW = zzLG + this.zzcrW;
            return str;
        }
    }

    public final int zzLB() throws IOException {
        if (this.zzcrW == this.zzcrU) {
            this.zzcrX = 0;
            return 0;
        }
        this.zzcrX = zzLG();
        if (this.zzcrX != 0) {
            return this.zzcrX;
        }
        throw new ado("Protocol message contained an invalid tag (zero).");
    }

    public final long zzLC() throws IOException {
        return zzLH();
    }

    public final int zzLD() throws IOException {
        return zzLG();
    }

    public final boolean zzLE() throws IOException {
        return zzLG() != 0;
    }

    public final long zzLF() throws IOException {
        long zzLH = zzLH();
        return (-(zzLH & 1)) ^ (zzLH >>> 1);
    }

    public final int zzLG() throws IOException {
        byte zzLM = zzLM();
        if (zzLM >= (byte) 0) {
            return zzLM;
        }
        int i = zzLM & 127;
        byte zzLM2 = zzLM();
        if (zzLM2 >= (byte) 0) {
            return i | (zzLM2 << 7);
        }
        i |= (zzLM2 & 127) << 7;
        zzLM2 = zzLM();
        if (zzLM2 >= (byte) 0) {
            return i | (zzLM2 << 14);
        }
        i |= (zzLM2 & 127) << 14;
        zzLM2 = zzLM();
        if (zzLM2 >= (byte) 0) {
            return i | (zzLM2 << 21);
        }
        i |= (zzLM2 & 127) << 21;
        zzLM2 = zzLM();
        i |= zzLM2 << 28;
        if (zzLM2 >= (byte) 0) {
            return i;
        }
        for (int i2 = 0; i2 < 5; i2++) {
            if (zzLM() >= (byte) 0) {
                return i;
            }
        }
        throw ado.zzLT();
    }

    public final long zzLH() throws IOException {
        long j = 0;
        for (int i = 0; i < 64; i += 7) {
            byte zzLM = zzLM();
            j |= ((long) (zzLM & 127)) << i;
            if ((zzLM & 128) == 0) {
                return j;
            }
        }
        throw ado.zzLT();
    }

    public final int zzLI() throws IOException {
        return (((zzLM() & 255) | ((zzLM() & 255) << 8)) | ((zzLM() & 255) << 16)) | ((zzLM() & 255) << 24);
    }

    public final long zzLJ() throws IOException {
        byte zzLM = zzLM();
        byte zzLM2 = zzLM();
        return ((((((((((long) zzLM2) & 255) << 8) | (((long) zzLM) & 255)) | ((((long) zzLM()) & 255) << 16)) | ((((long) zzLM()) & 255) << 24)) | ((((long) zzLM()) & 255) << 32)) | ((((long) zzLM()) & 255) << 40)) | ((((long) zzLM()) & 255) << 48)) | ((((long) zzLM()) & 255) << 56);
    }

    public final int zzLL() {
        if (this.zzcrY == ConnectionsManager.DEFAULT_DATACENTER_ID) {
            return -1;
        }
        return this.zzcrY - this.zzcrW;
    }

    public final void zza(adp com_google_android_gms_internal_adp) throws IOException {
        int zzLG = zzLG();
        if (this.zzcrZ >= this.zzcsa) {
            throw ado.zzLU();
        }
        zzLG = zzcn(zzLG);
        this.zzcrZ++;
        com_google_android_gms_internal_adp.zza(this);
        zzcl(0);
        this.zzcrZ--;
        zzco(zzLG);
    }

    public final void zza(adp com_google_android_gms_internal_adp, int i) throws IOException {
        if (this.zzcrZ >= this.zzcsa) {
            throw ado.zzLU();
        }
        this.zzcrZ++;
        com_google_android_gms_internal_adp.zza(this);
        zzcl((i << 3) | 4);
        this.zzcrZ--;
    }

    public final void zzcl(int i) throws ado {
        if (this.zzcrX != i) {
            throw new ado("Protocol message end-group tag did not match expected tag.");
        }
    }

    public final boolean zzcm(int i) throws IOException {
        switch (i & 7) {
            case 0:
                zzLG();
                return true;
            case 1:
                zzLJ();
                return true;
            case 2:
                zzcq(zzLG());
                return true;
            case 3:
                break;
            case 4:
                return false;
            case 5:
                zzLI();
                return true;
            default:
                throw new ado("Protocol message tag had invalid wire type.");
        }
        int zzLB;
        do {
            zzLB = zzLB();
            if (zzLB != 0) {
            }
            zzcl(((i >>> 3) << 3) | 4);
            return true;
        } while (zzcm(zzLB));
        zzcl(((i >>> 3) << 3) | 4);
        return true;
    }

    public final int zzcn(int i) throws ado {
        if (i < 0) {
            throw ado.zzLS();
        }
        int i2 = this.zzcrW + i;
        int i3 = this.zzcrY;
        if (i2 > i3) {
            throw ado.zzLR();
        }
        this.zzcrY = i2;
        zzLK();
        return i3;
    }

    public final void zzco(int i) {
        this.zzcrY = i;
        zzLK();
    }

    public final void zzcp(int i) {
        zzq(i, this.zzcrX);
    }

    public final byte[] zzp(int i, int i2) {
        if (i2 == 0) {
            return ads.zzcsx;
        }
        Object obj = new byte[i2];
        System.arraycopy(this.buffer, this.zzcrT + i, obj, 0, i2);
        return obj;
    }

    final void zzq(int i, int i2) {
        if (i > this.zzcrW - this.zzcrT) {
            throw new IllegalArgumentException("Position " + i + " is beyond current " + (this.zzcrW - this.zzcrT));
        } else if (i < 0) {
            throw new IllegalArgumentException("Bad position " + i);
        } else {
            this.zzcrW = this.zzcrT + i;
            this.zzcrX = i2;
        }
    }
}
