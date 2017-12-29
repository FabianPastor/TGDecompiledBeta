package com.google.android.gms.internal;

import java.io.IOException;
import org.telegram.tgnet.ConnectionsManager;

public final class zzfjj {
    private final byte[] buffer;
    private int zzpfm;
    private int zzpfn = 64;
    private int zzpfo = ConnectionsManager.FileTypeFile;
    private int zzpfr;
    private int zzpft;
    private int zzpfu = ConnectionsManager.DEFAULT_DATACENTER_ID;
    private final int zzpfw;
    private final int zzpmz;
    private int zzpna;
    private int zzpnb;

    private zzfjj(byte[] bArr, int i, int i2) {
        this.buffer = bArr;
        this.zzpmz = i;
        int i3 = i + i2;
        this.zzpna = i3;
        this.zzpfw = i3;
        this.zzpnb = i;
    }

    private final void zzcwq() {
        this.zzpna += this.zzpfr;
        int i = this.zzpna;
        if (i > this.zzpfu) {
            this.zzpfr = i - this.zzpfu;
            this.zzpna -= this.zzpfr;
            return;
        }
        this.zzpfr = 0;
    }

    private final byte zzcwr() throws IOException {
        if (this.zzpnb == this.zzpna) {
            throw zzfjr.zzdai();
        }
        byte[] bArr = this.buffer;
        int i = this.zzpnb;
        this.zzpnb = i + 1;
        return bArr[i];
    }

    private final void zzku(int i) throws IOException {
        if (i < 0) {
            throw zzfjr.zzdaj();
        } else if (this.zzpnb + i > this.zzpfu) {
            zzku(this.zzpfu - this.zzpnb);
            throw zzfjr.zzdai();
        } else if (i <= this.zzpna - this.zzpnb) {
            this.zzpnb += i;
        } else {
            throw zzfjr.zzdai();
        }
    }

    public static zzfjj zzn(byte[] bArr, int i, int i2) {
        return new zzfjj(bArr, 0, i2);
    }

    public final int getPosition() {
        return this.zzpnb - this.zzpmz;
    }

    public final String readString() throws IOException {
        int zzcwi = zzcwi();
        if (zzcwi < 0) {
            throw zzfjr.zzdaj();
        } else if (zzcwi > this.zzpna - this.zzpnb) {
            throw zzfjr.zzdai();
        } else {
            String str = new String(this.buffer, this.zzpnb, zzcwi, zzfjq.UTF_8);
            this.zzpnb = zzcwi + this.zzpnb;
            return str;
        }
    }

    public final void zza(zzfjs com_google_android_gms_internal_zzfjs) throws IOException {
        int zzcwi = zzcwi();
        if (this.zzpfm >= this.zzpfn) {
            throw zzfjr.zzdal();
        }
        zzcwi = zzks(zzcwi);
        this.zzpfm++;
        com_google_android_gms_internal_zzfjs.zza(this);
        zzkp(0);
        this.zzpfm--;
        zzkt(zzcwi);
    }

    public final byte[] zzal(int i, int i2) {
        if (i2 == 0) {
            return zzfjv.zzpnv;
        }
        Object obj = new byte[i2];
        System.arraycopy(this.buffer, this.zzpmz + i, obj, 0, i2);
        return obj;
    }

    final void zzam(int i, int i2) {
        if (i > this.zzpnb - this.zzpmz) {
            throw new IllegalArgumentException("Position " + i + " is beyond current " + (this.zzpnb - this.zzpmz));
        } else if (i < 0) {
            throw new IllegalArgumentException("Bad position " + i);
        } else {
            this.zzpnb = this.zzpmz + i;
            this.zzpft = i2;
        }
    }

    public final int zzcvt() throws IOException {
        if (this.zzpnb == this.zzpna) {
            this.zzpft = 0;
            return 0;
        }
        this.zzpft = zzcwi();
        if (this.zzpft != 0) {
            return this.zzpft;
        }
        throw new zzfjr("Protocol message contained an invalid tag (zero).");
    }

    public final boolean zzcvz() throws IOException {
        return zzcwi() != 0;
    }

    public final int zzcwi() throws IOException {
        byte zzcwr = zzcwr();
        if (zzcwr >= (byte) 0) {
            return zzcwr;
        }
        int i = zzcwr & 127;
        byte zzcwr2 = zzcwr();
        if (zzcwr2 >= (byte) 0) {
            return i | (zzcwr2 << 7);
        }
        i |= (zzcwr2 & 127) << 7;
        zzcwr2 = zzcwr();
        if (zzcwr2 >= (byte) 0) {
            return i | (zzcwr2 << 14);
        }
        i |= (zzcwr2 & 127) << 14;
        zzcwr2 = zzcwr();
        if (zzcwr2 >= (byte) 0) {
            return i | (zzcwr2 << 21);
        }
        i |= (zzcwr2 & 127) << 21;
        zzcwr2 = zzcwr();
        i |= zzcwr2 << 28;
        if (zzcwr2 >= (byte) 0) {
            return i;
        }
        for (int i2 = 0; i2 < 5; i2++) {
            if (zzcwr() >= (byte) 0) {
                return i;
            }
        }
        throw zzfjr.zzdak();
    }

    public final int zzcwk() {
        if (this.zzpfu == ConnectionsManager.DEFAULT_DATACENTER_ID) {
            return -1;
        }
        return this.zzpfu - this.zzpnb;
    }

    public final long zzcwn() throws IOException {
        long j = 0;
        for (int i = 0; i < 64; i += 7) {
            byte zzcwr = zzcwr();
            j |= ((long) (zzcwr & 127)) << i;
            if ((zzcwr & 128) == 0) {
                return j;
            }
        }
        throw zzfjr.zzdak();
    }

    public final int zzcwo() throws IOException {
        return (((zzcwr() & 255) | ((zzcwr() & 255) << 8)) | ((zzcwr() & 255) << 16)) | ((zzcwr() & 255) << 24);
    }

    public final long zzcwp() throws IOException {
        byte zzcwr = zzcwr();
        byte zzcwr2 = zzcwr();
        return ((((((((((long) zzcwr2) & 255) << 8) | (((long) zzcwr) & 255)) | ((((long) zzcwr()) & 255) << 16)) | ((((long) zzcwr()) & 255) << 24)) | ((((long) zzcwr()) & 255) << 32)) | ((((long) zzcwr()) & 255) << 40)) | ((((long) zzcwr()) & 255) << 48)) | ((((long) zzcwr()) & 255) << 56);
    }

    public final void zzkp(int i) throws zzfjr {
        if (this.zzpft != i) {
            throw new zzfjr("Protocol message end-group tag did not match expected tag.");
        }
    }

    public final boolean zzkq(int i) throws IOException {
        switch (i & 7) {
            case 0:
                zzcwi();
                return true;
            case 1:
                zzcwp();
                return true;
            case 2:
                zzku(zzcwi());
                return true;
            case 3:
                break;
            case 4:
                return false;
            case 5:
                zzcwo();
                return true;
            default:
                throw new zzfjr("Protocol message tag had invalid wire type.");
        }
        int zzcvt;
        do {
            zzcvt = zzcvt();
            if (zzcvt != 0) {
            }
            zzkp(((i >>> 3) << 3) | 4);
            return true;
        } while (zzkq(zzcvt));
        zzkp(((i >>> 3) << 3) | 4);
        return true;
    }

    public final int zzks(int i) throws zzfjr {
        if (i < 0) {
            throw zzfjr.zzdaj();
        }
        int i2 = this.zzpnb + i;
        int i3 = this.zzpfu;
        if (i2 > i3) {
            throw zzfjr.zzdai();
        }
        this.zzpfu = i2;
        zzcwq();
        return i3;
    }

    public final void zzkt(int i) {
        this.zzpfu = i;
        zzcwq();
    }

    public final void zzmg(int i) {
        zzam(i, this.zzpft);
    }
}
