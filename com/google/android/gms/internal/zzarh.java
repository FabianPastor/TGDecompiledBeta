package com.google.android.gms.internal;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

class zzarh implements Cloneable {
    private zzarf<?, ?> bqB;
    private List<zzarm> bqC = new ArrayList();
    private Object value;

    zzarh() {
    }

    private byte[] toByteArray() throws IOException {
        byte[] bArr = new byte[zzx()];
        zza(zzard.zzbe(bArr));
        return bArr;
    }

    public final zzarh cS() {
        zzarh com_google_android_gms_internal_zzarh = new zzarh();
        try {
            com_google_android_gms_internal_zzarh.bqB = this.bqB;
            if (this.bqC == null) {
                com_google_android_gms_internal_zzarh.bqC = null;
            } else {
                com_google_android_gms_internal_zzarh.bqC.addAll(this.bqC);
            }
            if (this.value != null) {
                if (this.value instanceof zzark) {
                    com_google_android_gms_internal_zzarh.value = (zzark) ((zzark) this.value).clone();
                } else if (this.value instanceof byte[]) {
                    com_google_android_gms_internal_zzarh.value = ((byte[]) this.value).clone();
                } else if (this.value instanceof byte[][]) {
                    byte[][] bArr = (byte[][]) this.value;
                    r4 = new byte[bArr.length][];
                    com_google_android_gms_internal_zzarh.value = r4;
                    for (r2 = 0; r2 < bArr.length; r2++) {
                        r4[r2] = (byte[]) bArr[r2].clone();
                    }
                } else if (this.value instanceof boolean[]) {
                    com_google_android_gms_internal_zzarh.value = ((boolean[]) this.value).clone();
                } else if (this.value instanceof int[]) {
                    com_google_android_gms_internal_zzarh.value = ((int[]) this.value).clone();
                } else if (this.value instanceof long[]) {
                    com_google_android_gms_internal_zzarh.value = ((long[]) this.value).clone();
                } else if (this.value instanceof float[]) {
                    com_google_android_gms_internal_zzarh.value = ((float[]) this.value).clone();
                } else if (this.value instanceof double[]) {
                    com_google_android_gms_internal_zzarh.value = ((double[]) this.value).clone();
                } else if (this.value instanceof zzark[]) {
                    zzark[] com_google_android_gms_internal_zzarkArr = (zzark[]) this.value;
                    r4 = new zzark[com_google_android_gms_internal_zzarkArr.length];
                    com_google_android_gms_internal_zzarh.value = r4;
                    for (r2 = 0; r2 < com_google_android_gms_internal_zzarkArr.length; r2++) {
                        r4[r2] = (zzark) com_google_android_gms_internal_zzarkArr[r2].clone();
                    }
                }
            }
            return com_google_android_gms_internal_zzarh;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError(e);
        }
    }

    public /* synthetic */ Object clone() throws CloneNotSupportedException {
        return cS();
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof zzarh)) {
            return false;
        }
        zzarh com_google_android_gms_internal_zzarh = (zzarh) obj;
        if (this.value != null && com_google_android_gms_internal_zzarh.value != null) {
            return this.bqB == com_google_android_gms_internal_zzarh.bqB ? !this.bqB.bhd.isArray() ? this.value.equals(com_google_android_gms_internal_zzarh.value) : this.value instanceof byte[] ? Arrays.equals((byte[]) this.value, (byte[]) com_google_android_gms_internal_zzarh.value) : this.value instanceof int[] ? Arrays.equals((int[]) this.value, (int[]) com_google_android_gms_internal_zzarh.value) : this.value instanceof long[] ? Arrays.equals((long[]) this.value, (long[]) com_google_android_gms_internal_zzarh.value) : this.value instanceof float[] ? Arrays.equals((float[]) this.value, (float[]) com_google_android_gms_internal_zzarh.value) : this.value instanceof double[] ? Arrays.equals((double[]) this.value, (double[]) com_google_android_gms_internal_zzarh.value) : this.value instanceof boolean[] ? Arrays.equals((boolean[]) this.value, (boolean[]) com_google_android_gms_internal_zzarh.value) : Arrays.deepEquals((Object[]) this.value, (Object[]) com_google_android_gms_internal_zzarh.value) : false;
        } else {
            if (this.bqC != null && com_google_android_gms_internal_zzarh.bqC != null) {
                return this.bqC.equals(com_google_android_gms_internal_zzarh.bqC);
            }
            try {
                return Arrays.equals(toByteArray(), com_google_android_gms_internal_zzarh.toByteArray());
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

    void zza(zzard com_google_android_gms_internal_zzard) throws IOException {
        if (this.value != null) {
            this.bqB.zza(this.value, com_google_android_gms_internal_zzard);
            return;
        }
        for (zzarm zza : this.bqC) {
            zza.zza(com_google_android_gms_internal_zzard);
        }
    }

    void zza(zzarm com_google_android_gms_internal_zzarm) {
        this.bqC.add(com_google_android_gms_internal_zzarm);
    }

    <T> T zzb(zzarf<?, T> com_google_android_gms_internal_zzarf___T) {
        if (this.value == null) {
            this.bqB = com_google_android_gms_internal_zzarf___T;
            this.value = com_google_android_gms_internal_zzarf___T.zzay(this.bqC);
            this.bqC = null;
        } else if (!this.bqB.equals(com_google_android_gms_internal_zzarf___T)) {
            throw new IllegalStateException("Tried to getExtension with a different Extension.");
        }
        return this.value;
    }

    int zzx() {
        if (this.value != null) {
            return this.bqB.zzcu(this.value);
        }
        int i = 0;
        for (zzarm zzx : this.bqC) {
            i = zzx.zzx() + i;
        }
        return i;
    }
}
