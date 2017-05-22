package com.google.android.gms.internal;

public final class zzbyr implements zzbys, zzbyt, Cloneable {
    private static final byte[] zzcxT = new byte[]{(byte) 48, (byte) 49, (byte) 50, (byte) 51, (byte) 52, (byte) 53, (byte) 54, (byte) 55, (byte) 56, (byte) 57, (byte) 97, (byte) 98, (byte) 99, (byte) 100, (byte) 101, (byte) 102};
    long zzaA;
    zzbyx zzcxU;

    public /* synthetic */ Object clone() throws CloneNotSupportedException {
        return zzafT();
    }

    public void close() {
    }

    public boolean equals(Object obj) {
        long j = 0;
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof zzbyr)) {
            return false;
        }
        zzbyr com_google_android_gms_internal_zzbyr = (zzbyr) obj;
        if (this.zzaA != com_google_android_gms_internal_zzbyr.zzaA) {
            return false;
        }
        if (this.zzaA == 0) {
            return true;
        }
        zzbyx com_google_android_gms_internal_zzbyx = this.zzcxU;
        zzbyx com_google_android_gms_internal_zzbyx2 = com_google_android_gms_internal_zzbyr.zzcxU;
        int i = com_google_android_gms_internal_zzbyx.pos;
        int i2 = com_google_android_gms_internal_zzbyx2.pos;
        while (j < this.zzaA) {
            long min = (long) Math.min(com_google_android_gms_internal_zzbyx.limit - i, com_google_android_gms_internal_zzbyx2.limit - i2);
            int i3 = 0;
            while (((long) i3) < min) {
                int i4 = i + 1;
                byte b = com_google_android_gms_internal_zzbyx.data[i];
                i = i2 + 1;
                if (b != com_google_android_gms_internal_zzbyx2.data[i2]) {
                    return false;
                }
                i3++;
                i2 = i;
                i = i4;
            }
            if (i == com_google_android_gms_internal_zzbyx.limit) {
                com_google_android_gms_internal_zzbyx = com_google_android_gms_internal_zzbyx.zzcyc;
                i = com_google_android_gms_internal_zzbyx.pos;
            }
            if (i2 == com_google_android_gms_internal_zzbyx2.limit) {
                com_google_android_gms_internal_zzbyx2 = com_google_android_gms_internal_zzbyx2.zzcyc;
                i2 = com_google_android_gms_internal_zzbyx2.pos;
            }
            j += min;
        }
        return true;
    }

    public void flush() {
    }

    public int hashCode() {
        zzbyx com_google_android_gms_internal_zzbyx = this.zzcxU;
        if (com_google_android_gms_internal_zzbyx == null) {
            return 0;
        }
        int i = 1;
        do {
            int i2 = com_google_android_gms_internal_zzbyx.pos;
            while (i2 < com_google_android_gms_internal_zzbyx.limit) {
                int i3 = com_google_android_gms_internal_zzbyx.data[i2] + (i * 31);
                i2++;
                i = i3;
            }
            com_google_android_gms_internal_zzbyx = com_google_android_gms_internal_zzbyx.zzcyc;
        } while (com_google_android_gms_internal_zzbyx != this.zzcxU);
        return i;
    }

    public long read(zzbyr com_google_android_gms_internal_zzbyr, long j) {
        if (com_google_android_gms_internal_zzbyr == null) {
            throw new IllegalArgumentException("sink == null");
        } else if (j < 0) {
            throw new IllegalArgumentException("byteCount < 0: " + j);
        } else if (this.zzaA == 0) {
            return -1;
        } else {
            if (j > this.zzaA) {
                j = this.zzaA;
            }
            com_google_android_gms_internal_zzbyr.write(this, j);
            return j;
        }
    }

    public String toString() {
        return zzafU().toString();
    }

    public void write(zzbyr com_google_android_gms_internal_zzbyr, long j) {
        if (com_google_android_gms_internal_zzbyr == null) {
            throw new IllegalArgumentException("source == null");
        } else if (com_google_android_gms_internal_zzbyr == this) {
            throw new IllegalArgumentException("source == this");
        } else {
            zzbzd.checkOffsetAndCount(com_google_android_gms_internal_zzbyr.zzaA, 0, j);
            while (j > 0) {
                zzbyx com_google_android_gms_internal_zzbyx;
                if (j < ((long) (com_google_android_gms_internal_zzbyr.zzcxU.limit - com_google_android_gms_internal_zzbyr.zzcxU.pos))) {
                    com_google_android_gms_internal_zzbyx = this.zzcxU != null ? this.zzcxU.zzcyd : null;
                    if (com_google_android_gms_internal_zzbyx != null && com_google_android_gms_internal_zzbyx.zzcyb) {
                        if ((((long) com_google_android_gms_internal_zzbyx.limit) + j) - ((long) (com_google_android_gms_internal_zzbyx.zzcya ? 0 : com_google_android_gms_internal_zzbyx.pos)) <= 8192) {
                            com_google_android_gms_internal_zzbyr.zzcxU.zza(com_google_android_gms_internal_zzbyx, (int) j);
                            com_google_android_gms_internal_zzbyr.zzaA -= j;
                            this.zzaA += j;
                            return;
                        }
                    }
                    com_google_android_gms_internal_zzbyr.zzcxU = com_google_android_gms_internal_zzbyr.zzcxU.zzrz((int) j);
                }
                zzbyx com_google_android_gms_internal_zzbyx2 = com_google_android_gms_internal_zzbyr.zzcxU;
                long j2 = (long) (com_google_android_gms_internal_zzbyx2.limit - com_google_android_gms_internal_zzbyx2.pos);
                com_google_android_gms_internal_zzbyr.zzcxU = com_google_android_gms_internal_zzbyx2.zzafX();
                if (this.zzcxU == null) {
                    this.zzcxU = com_google_android_gms_internal_zzbyx2;
                    com_google_android_gms_internal_zzbyx2 = this.zzcxU;
                    com_google_android_gms_internal_zzbyx = this.zzcxU;
                    zzbyx com_google_android_gms_internal_zzbyx3 = this.zzcxU;
                    com_google_android_gms_internal_zzbyx.zzcyd = com_google_android_gms_internal_zzbyx3;
                    com_google_android_gms_internal_zzbyx2.zzcyc = com_google_android_gms_internal_zzbyx3;
                } else {
                    this.zzcxU.zzcyd.zza(com_google_android_gms_internal_zzbyx2).zzafY();
                }
                com_google_android_gms_internal_zzbyr.zzaA -= j2;
                this.zzaA += j2;
                j -= j2;
            }
        }
    }

    public zzbyr zzafT() {
        zzbyr com_google_android_gms_internal_zzbyr = new zzbyr();
        if (this.zzaA == 0) {
            return com_google_android_gms_internal_zzbyr;
        }
        com_google_android_gms_internal_zzbyr.zzcxU = new zzbyx(this.zzcxU);
        zzbyx com_google_android_gms_internal_zzbyx = com_google_android_gms_internal_zzbyr.zzcxU;
        zzbyx com_google_android_gms_internal_zzbyx2 = com_google_android_gms_internal_zzbyr.zzcxU;
        zzbyx com_google_android_gms_internal_zzbyx3 = com_google_android_gms_internal_zzbyr.zzcxU;
        com_google_android_gms_internal_zzbyx2.zzcyd = com_google_android_gms_internal_zzbyx3;
        com_google_android_gms_internal_zzbyx.zzcyc = com_google_android_gms_internal_zzbyx3;
        for (com_google_android_gms_internal_zzbyx = this.zzcxU.zzcyc; com_google_android_gms_internal_zzbyx != this.zzcxU; com_google_android_gms_internal_zzbyx = com_google_android_gms_internal_zzbyx.zzcyc) {
            com_google_android_gms_internal_zzbyr.zzcxU.zzcyd.zza(new zzbyx(com_google_android_gms_internal_zzbyx));
        }
        com_google_android_gms_internal_zzbyr.zzaA = this.zzaA;
        return com_google_android_gms_internal_zzbyr;
    }

    public zzbyu zzafU() {
        if (this.zzaA <= 2147483647L) {
            return zzry((int) this.zzaA);
        }
        throw new IllegalArgumentException("size > Integer.MAX_VALUE: " + this.zzaA);
    }

    public zzbyu zzry(int i) {
        return i == 0 ? zzbyu.zzcxW : new zzbyz(this, i);
    }
}
