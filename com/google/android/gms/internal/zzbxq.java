package com.google.android.gms.internal;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

class zzbxq implements Cloneable {
    private Object value;
    private zzbxo<?, ?> zzcuG;
    private List<zzbxv> zzcuH = new ArrayList();

    zzbxq() {
    }

    private byte[] toByteArray() throws IOException {
        byte[] bArr = new byte[zzu()];
        zza(zzbxm.zzag(bArr));
        return bArr;
    }

    public /* synthetic */ Object clone() throws CloneNotSupportedException {
        return zzaeJ();
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof zzbxq)) {
            return false;
        }
        zzbxq com_google_android_gms_internal_zzbxq = (zzbxq) obj;
        if (this.value != null && com_google_android_gms_internal_zzbxq.value != null) {
            return this.zzcuG == com_google_android_gms_internal_zzbxq.zzcuG ? !this.zzcuG.zzckQ.isArray() ? this.value.equals(com_google_android_gms_internal_zzbxq.value) : this.value instanceof byte[] ? Arrays.equals((byte[]) this.value, (byte[]) com_google_android_gms_internal_zzbxq.value) : this.value instanceof int[] ? Arrays.equals((int[]) this.value, (int[]) com_google_android_gms_internal_zzbxq.value) : this.value instanceof long[] ? Arrays.equals((long[]) this.value, (long[]) com_google_android_gms_internal_zzbxq.value) : this.value instanceof float[] ? Arrays.equals((float[]) this.value, (float[]) com_google_android_gms_internal_zzbxq.value) : this.value instanceof double[] ? Arrays.equals((double[]) this.value, (double[]) com_google_android_gms_internal_zzbxq.value) : this.value instanceof boolean[] ? Arrays.equals((boolean[]) this.value, (boolean[]) com_google_android_gms_internal_zzbxq.value) : Arrays.deepEquals((Object[]) this.value, (Object[]) com_google_android_gms_internal_zzbxq.value) : false;
        } else {
            if (this.zzcuH != null && com_google_android_gms_internal_zzbxq.zzcuH != null) {
                return this.zzcuH.equals(com_google_android_gms_internal_zzbxq.zzcuH);
            }
            try {
                return Arrays.equals(toByteArray(), com_google_android_gms_internal_zzbxq.toByteArray());
            } catch (Throwable e) {
                throw new IllegalStateException(e);
            }
        }
    }

    public int hashCode() {
        try {
            return Arrays.hashCode(toByteArray()) + 527;
        } catch (Throwable e) {
            throw new IllegalStateException(e);
        }
    }

    void zza(zzbxm com_google_android_gms_internal_zzbxm) throws IOException {
        if (this.value != null) {
            this.zzcuG.zza(this.value, com_google_android_gms_internal_zzbxm);
            return;
        }
        for (zzbxv zza : this.zzcuH) {
            zza.zza(com_google_android_gms_internal_zzbxm);
        }
    }

    void zza(zzbxv com_google_android_gms_internal_zzbxv) {
        this.zzcuH.add(com_google_android_gms_internal_zzbxv);
    }

    public final zzbxq zzaeJ() {
        zzbxq com_google_android_gms_internal_zzbxq = new zzbxq();
        try {
            com_google_android_gms_internal_zzbxq.zzcuG = this.zzcuG;
            if (this.zzcuH == null) {
                com_google_android_gms_internal_zzbxq.zzcuH = null;
            } else {
                com_google_android_gms_internal_zzbxq.zzcuH.addAll(this.zzcuH);
            }
            if (this.value != null) {
                if (this.value instanceof zzbxt) {
                    com_google_android_gms_internal_zzbxq.value = (zzbxt) ((zzbxt) this.value).clone();
                } else if (this.value instanceof byte[]) {
                    com_google_android_gms_internal_zzbxq.value = ((byte[]) this.value).clone();
                } else if (this.value instanceof byte[][]) {
                    byte[][] bArr = (byte[][]) this.value;
                    r4 = new byte[bArr.length][];
                    com_google_android_gms_internal_zzbxq.value = r4;
                    for (r2 = 0; r2 < bArr.length; r2++) {
                        r4[r2] = (byte[]) bArr[r2].clone();
                    }
                } else if (this.value instanceof boolean[]) {
                    com_google_android_gms_internal_zzbxq.value = ((boolean[]) this.value).clone();
                } else if (this.value instanceof int[]) {
                    com_google_android_gms_internal_zzbxq.value = ((int[]) this.value).clone();
                } else if (this.value instanceof long[]) {
                    com_google_android_gms_internal_zzbxq.value = ((long[]) this.value).clone();
                } else if (this.value instanceof float[]) {
                    com_google_android_gms_internal_zzbxq.value = ((float[]) this.value).clone();
                } else if (this.value instanceof double[]) {
                    com_google_android_gms_internal_zzbxq.value = ((double[]) this.value).clone();
                } else if (this.value instanceof zzbxt[]) {
                    zzbxt[] com_google_android_gms_internal_zzbxtArr = (zzbxt[]) this.value;
                    r4 = new zzbxt[com_google_android_gms_internal_zzbxtArr.length];
                    com_google_android_gms_internal_zzbxq.value = r4;
                    for (r2 = 0; r2 < com_google_android_gms_internal_zzbxtArr.length; r2++) {
                        r4[r2] = (zzbxt) com_google_android_gms_internal_zzbxtArr[r2].clone();
                    }
                }
            }
            return com_google_android_gms_internal_zzbxq;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError(e);
        }
    }

    <T> T zzb(zzbxo<?, T> com_google_android_gms_internal_zzbxo___T) {
        if (this.value == null) {
            this.zzcuG = com_google_android_gms_internal_zzbxo___T;
            this.value = com_google_android_gms_internal_zzbxo___T.zzac(this.zzcuH);
            this.zzcuH = null;
        } else if (!this.zzcuG.equals(com_google_android_gms_internal_zzbxo___T)) {
            throw new IllegalStateException("Tried to getExtension with a different Extension.");
        }
        return this.value;
    }

    int zzu() {
        if (this.value != null) {
            return this.zzcuG.zzaU(this.value);
        }
        int i = 0;
        for (zzbxv zzu : this.zzcuH) {
            i = zzu.zzu() + i;
        }
        return i;
    }
}
