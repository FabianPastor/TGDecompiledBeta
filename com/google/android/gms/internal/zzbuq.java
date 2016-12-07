package com.google.android.gms.internal;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

class zzbuq implements Cloneable {
    private Object value;
    private zzbuo<?, ?> zzcsd;
    private List<zzbuv> zzcse = new ArrayList();

    zzbuq() {
    }

    private byte[] toByteArray() throws IOException {
        byte[] bArr = new byte[zzv()];
        zza(zzbum.zzae(bArr));
        return bArr;
    }

    public /* synthetic */ Object clone() throws CloneNotSupportedException {
        return zzacQ();
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof zzbuq)) {
            return false;
        }
        zzbuq com_google_android_gms_internal_zzbuq = (zzbuq) obj;
        if (this.value != null && com_google_android_gms_internal_zzbuq.value != null) {
            return this.zzcsd == com_google_android_gms_internal_zzbuq.zzcsd ? !this.zzcsd.zzciF.isArray() ? this.value.equals(com_google_android_gms_internal_zzbuq.value) : this.value instanceof byte[] ? Arrays.equals((byte[]) this.value, (byte[]) com_google_android_gms_internal_zzbuq.value) : this.value instanceof int[] ? Arrays.equals((int[]) this.value, (int[]) com_google_android_gms_internal_zzbuq.value) : this.value instanceof long[] ? Arrays.equals((long[]) this.value, (long[]) com_google_android_gms_internal_zzbuq.value) : this.value instanceof float[] ? Arrays.equals((float[]) this.value, (float[]) com_google_android_gms_internal_zzbuq.value) : this.value instanceof double[] ? Arrays.equals((double[]) this.value, (double[]) com_google_android_gms_internal_zzbuq.value) : this.value instanceof boolean[] ? Arrays.equals((boolean[]) this.value, (boolean[]) com_google_android_gms_internal_zzbuq.value) : Arrays.deepEquals((Object[]) this.value, (Object[]) com_google_android_gms_internal_zzbuq.value) : false;
        } else {
            if (this.zzcse != null && com_google_android_gms_internal_zzbuq.zzcse != null) {
                return this.zzcse.equals(com_google_android_gms_internal_zzbuq.zzcse);
            }
            try {
                return Arrays.equals(toByteArray(), com_google_android_gms_internal_zzbuq.toByteArray());
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

    void zza(zzbum com_google_android_gms_internal_zzbum) throws IOException {
        if (this.value != null) {
            this.zzcsd.zza(this.value, com_google_android_gms_internal_zzbum);
            return;
        }
        for (zzbuv zza : this.zzcse) {
            zza.zza(com_google_android_gms_internal_zzbum);
        }
    }

    void zza(zzbuv com_google_android_gms_internal_zzbuv) {
        this.zzcse.add(com_google_android_gms_internal_zzbuv);
    }

    public final zzbuq zzacQ() {
        zzbuq com_google_android_gms_internal_zzbuq = new zzbuq();
        try {
            com_google_android_gms_internal_zzbuq.zzcsd = this.zzcsd;
            if (this.zzcse == null) {
                com_google_android_gms_internal_zzbuq.zzcse = null;
            } else {
                com_google_android_gms_internal_zzbuq.zzcse.addAll(this.zzcse);
            }
            if (this.value != null) {
                if (this.value instanceof zzbut) {
                    com_google_android_gms_internal_zzbuq.value = (zzbut) ((zzbut) this.value).clone();
                } else if (this.value instanceof byte[]) {
                    com_google_android_gms_internal_zzbuq.value = ((byte[]) this.value).clone();
                } else if (this.value instanceof byte[][]) {
                    byte[][] bArr = (byte[][]) this.value;
                    r4 = new byte[bArr.length][];
                    com_google_android_gms_internal_zzbuq.value = r4;
                    for (r2 = 0; r2 < bArr.length; r2++) {
                        r4[r2] = (byte[]) bArr[r2].clone();
                    }
                } else if (this.value instanceof boolean[]) {
                    com_google_android_gms_internal_zzbuq.value = ((boolean[]) this.value).clone();
                } else if (this.value instanceof int[]) {
                    com_google_android_gms_internal_zzbuq.value = ((int[]) this.value).clone();
                } else if (this.value instanceof long[]) {
                    com_google_android_gms_internal_zzbuq.value = ((long[]) this.value).clone();
                } else if (this.value instanceof float[]) {
                    com_google_android_gms_internal_zzbuq.value = ((float[]) this.value).clone();
                } else if (this.value instanceof double[]) {
                    com_google_android_gms_internal_zzbuq.value = ((double[]) this.value).clone();
                } else if (this.value instanceof zzbut[]) {
                    zzbut[] com_google_android_gms_internal_zzbutArr = (zzbut[]) this.value;
                    r4 = new zzbut[com_google_android_gms_internal_zzbutArr.length];
                    com_google_android_gms_internal_zzbuq.value = r4;
                    for (r2 = 0; r2 < com_google_android_gms_internal_zzbutArr.length; r2++) {
                        r4[r2] = (zzbut) com_google_android_gms_internal_zzbutArr[r2].clone();
                    }
                }
            }
            return com_google_android_gms_internal_zzbuq;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError(e);
        }
    }

    <T> T zzb(zzbuo<?, T> com_google_android_gms_internal_zzbuo___T) {
        if (this.value == null) {
            this.zzcsd = com_google_android_gms_internal_zzbuo___T;
            this.value = com_google_android_gms_internal_zzbuo___T.zzZ(this.zzcse);
            this.zzcse = null;
        } else if (!this.zzcsd.equals(com_google_android_gms_internal_zzbuo___T)) {
            throw new IllegalStateException("Tried to getExtension with a different Extension.");
        }
        return this.value;
    }

    int zzv() {
        if (this.value != null) {
            return this.zzcsd.zzaR(this.value);
        }
        int i = 0;
        for (zzbuv zzv : this.zzcse) {
            i = zzv.zzv() + i;
        }
        return i;
    }
}
