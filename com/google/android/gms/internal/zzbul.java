package com.google.android.gms.internal;

import java.io.IOException;
import org.telegram.tgnet.ConnectionsManager;

public final class zzbul {
    private final byte[] buffer;
    private int zzcrN;
    private int zzcrO;
    private int zzcrP;
    private int zzcrQ;
    private int zzcrR;
    private int zzcrS = ConnectionsManager.DEFAULT_DATACENTER_ID;
    private int zzcrT;
    private int zzcrU = 64;
    private int zzcrV = 67108864;

    private zzbul(byte[] bArr, int i, int i2) {
        this.buffer = bArr;
        this.zzcrN = i;
        this.zzcrO = i + i2;
        this.zzcrQ = i;
    }

    public static long zzaV(long j) {
        return (j >>> 1) ^ (-(1 & j));
    }

    private void zzacH() {
        this.zzcrO += this.zzcrP;
        int i = this.zzcrO;
        if (i > this.zzcrS) {
            this.zzcrP = i - this.zzcrS;
            this.zzcrO -= this.zzcrP;
            return;
        }
        this.zzcrP = 0;
    }

    public static zzbul zzad(byte[] bArr) {
        return zzb(bArr, 0, bArr.length);
    }

    public static zzbul zzb(byte[] bArr, int i, int i2) {
        return new zzbul(bArr, i, i2);
    }

    public static int zzqi(int i) {
        return (i >>> 1) ^ (-(i & 1));
    }

    public int getPosition() {
        return this.zzcrQ - this.zzcrN;
    }

    public byte[] readBytes() throws IOException {
        int zzacD = zzacD();
        if (zzacD < 0) {
            throw zzbus.zzacS();
        } else if (zzacD == 0) {
            return zzbuw.zzcsp;
        } else {
            if (zzacD > this.zzcrO - this.zzcrQ) {
                throw zzbus.zzacR();
            }
            Object obj = new byte[zzacD];
            System.arraycopy(this.buffer, this.zzcrQ, obj, 0, zzacD);
            this.zzcrQ = zzacD + this.zzcrQ;
            return obj;
        }
    }

    public double readDouble() throws IOException {
        return Double.longBitsToDouble(zzacG());
    }

    public float readFloat() throws IOException {
        return Float.intBitsToFloat(zzacF());
    }

    public String readString() throws IOException {
        int zzacD = zzacD();
        if (zzacD < 0) {
            throw zzbus.zzacS();
        } else if (zzacD > this.zzcrO - this.zzcrQ) {
            throw zzbus.zzacR();
        } else {
            String str = new String(this.buffer, this.zzcrQ, zzacD, zzbur.UTF_8);
            this.zzcrQ = zzacD + this.zzcrQ;
            return str;
        }
    }

    public byte[] zzE(int i, int i2) {
        if (i2 == 0) {
            return zzbuw.zzcsp;
        }
        Object obj = new byte[i2];
        System.arraycopy(this.buffer, this.zzcrN + i, obj, 0, i2);
        return obj;
    }

    public void zza(zzbut com_google_android_gms_internal_zzbut) throws IOException {
        int zzacD = zzacD();
        if (this.zzcrT >= this.zzcrU) {
            throw zzbus.zzacX();
        }
        zzacD = zzqj(zzacD);
        this.zzcrT++;
        com_google_android_gms_internal_zzbut.zzb(this);
        zzqg(0);
        this.zzcrT--;
        zzqk(zzacD);
    }

    public void zza(zzbut com_google_android_gms_internal_zzbut, int i) throws IOException {
        if (this.zzcrT >= this.zzcrU) {
            throw zzbus.zzacX();
        }
        this.zzcrT++;
        com_google_android_gms_internal_zzbut.zzb(this);
        zzqg(zzbuw.zzK(i, 4));
        this.zzcrT--;
    }

    public boolean zzacA() throws IOException {
        return zzacD() != 0;
    }

    public int zzacB() throws IOException {
        return zzqi(zzacD());
    }

    public long zzacC() throws IOException {
        return zzaV(zzacE());
    }

    public int zzacD() throws IOException {
        byte zzacK = zzacK();
        if (zzacK >= (byte) 0) {
            return zzacK;
        }
        int i = zzacK & 127;
        byte zzacK2 = zzacK();
        if (zzacK2 >= (byte) 0) {
            return i | (zzacK2 << 7);
        }
        i |= (zzacK2 & 127) << 7;
        zzacK2 = zzacK();
        if (zzacK2 >= (byte) 0) {
            return i | (zzacK2 << 14);
        }
        i |= (zzacK2 & 127) << 14;
        zzacK2 = zzacK();
        if (zzacK2 >= (byte) 0) {
            return i | (zzacK2 << 21);
        }
        i |= (zzacK2 & 127) << 21;
        zzacK2 = zzacK();
        i |= zzacK2 << 28;
        if (zzacK2 >= (byte) 0) {
            return i;
        }
        for (int i2 = 0; i2 < 5; i2++) {
            if (zzacK() >= (byte) 0) {
                return i;
            }
        }
        throw zzbus.zzacT();
    }

    public long zzacE() throws IOException {
        long j = 0;
        for (int i = 0; i < 64; i += 7) {
            byte zzacK = zzacK();
            j |= ((long) (zzacK & 127)) << i;
            if ((zzacK & 128) == 0) {
                return j;
            }
        }
        throw zzbus.zzacT();
    }

    public int zzacF() throws IOException {
        return (((zzacK() & 255) | ((zzacK() & 255) << 8)) | ((zzacK() & 255) << 16)) | ((zzacK() & 255) << 24);
    }

    public long zzacG() throws IOException {
        byte zzacK = zzacK();
        byte zzacK2 = zzacK();
        return ((((((((((long) zzacK2) & 255) << 8) | (((long) zzacK) & 255)) | ((((long) zzacK()) & 255) << 16)) | ((((long) zzacK()) & 255) << 24)) | ((((long) zzacK()) & 255) << 32)) | ((((long) zzacK()) & 255) << 40)) | ((((long) zzacK()) & 255) << 48)) | ((((long) zzacK()) & 255) << 56);
    }

    public int zzacI() {
        if (this.zzcrS == ConnectionsManager.DEFAULT_DATACENTER_ID) {
            return -1;
        }
        return this.zzcrS - this.zzcrQ;
    }

    public boolean zzacJ() {
        return this.zzcrQ == this.zzcrO;
    }

    public byte zzacK() throws IOException {
        if (this.zzcrQ == this.zzcrO) {
            throw zzbus.zzacR();
        }
        byte[] bArr = this.buffer;
        int i = this.zzcrQ;
        this.zzcrQ = i + 1;
        return bArr[i];
    }

    public int zzacu() throws IOException {
        if (zzacJ()) {
            this.zzcrR = 0;
            return 0;
        }
        this.zzcrR = zzacD();
        if (this.zzcrR != 0) {
            return this.zzcrR;
        }
        throw zzbus.zzacU();
    }

    public void zzacv() throws IOException {
        int zzacu;
        do {
            zzacu = zzacu();
            if (zzacu == 0) {
                return;
            }
        } while (zzqh(zzacu));
    }

    public long zzacw() throws IOException {
        return zzacE();
    }

    public long zzacx() throws IOException {
        return zzacE();
    }

    public int zzacy() throws IOException {
        return zzacD();
    }

    public long zzacz() throws IOException {
        return zzacG();
    }

    public void zzqg(int i) throws zzbus {
        if (this.zzcrR != i) {
            throw zzbus.zzacV();
        }
    }

    public boolean zzqh(int i) throws IOException {
        switch (zzbuw.zzqA(i)) {
            case 0:
                zzacy();
                return true;
            case 1:
                zzacG();
                return true;
            case 2:
                zzqm(zzacD());
                return true;
            case 3:
                zzacv();
                zzqg(zzbuw.zzK(zzbuw.zzqB(i), 4));
                return true;
            case 4:
                return false;
            case 5:
                zzacF();
                return true;
            default:
                throw zzbus.zzacW();
        }
    }

    public int zzqj(int i) throws zzbus {
        if (i < 0) {
            throw zzbus.zzacS();
        }
        int i2 = this.zzcrQ + i;
        int i3 = this.zzcrS;
        if (i2 > i3) {
            throw zzbus.zzacR();
        }
        this.zzcrS = i2;
        zzacH();
        return i3;
    }

    public void zzqk(int i) {
        this.zzcrS = i;
        zzacH();
    }

    public void zzql(int i) {
        if (i > this.zzcrQ - this.zzcrN) {
            throw new IllegalArgumentException("Position " + i + " is beyond current " + (this.zzcrQ - this.zzcrN));
        } else if (i < 0) {
            throw new IllegalArgumentException("Bad position " + i);
        } else {
            this.zzcrQ = this.zzcrN + i;
        }
    }

    public void zzqm(int i) throws IOException {
        if (i < 0) {
            throw zzbus.zzacS();
        } else if (this.zzcrQ + i > this.zzcrS) {
            zzqm(this.zzcrS - this.zzcrQ);
            throw zzbus.zzacR();
        } else if (i <= this.zzcrO - this.zzcrQ) {
            this.zzcrQ += i;
        } else {
            throw zzbus.zzacR();
        }
    }
}
