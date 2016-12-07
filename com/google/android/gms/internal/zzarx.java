package com.google.android.gms.internal;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

class zzarx implements Cloneable {
    private zzarv<?, ?> btM;
    private List<zzasc> btN = new ArrayList();
    private Object value;

    zzarx() {
    }

    private byte[] toByteArray() throws IOException {
        byte[] bArr = new byte[zzx()];
        zza(zzart.zzbe(bArr));
        return bArr;
    }

    public /* synthetic */ Object clone() throws CloneNotSupportedException {
        return cq();
    }

    public final zzarx cq() {
        zzarx com_google_android_gms_internal_zzarx = new zzarx();
        try {
            com_google_android_gms_internal_zzarx.btM = this.btM;
            if (this.btN == null) {
                com_google_android_gms_internal_zzarx.btN = null;
            } else {
                com_google_android_gms_internal_zzarx.btN.addAll(this.btN);
            }
            if (this.value != null) {
                if (this.value instanceof zzasa) {
                    com_google_android_gms_internal_zzarx.value = (zzasa) ((zzasa) this.value).clone();
                } else if (this.value instanceof byte[]) {
                    com_google_android_gms_internal_zzarx.value = ((byte[]) this.value).clone();
                } else if (this.value instanceof byte[][]) {
                    byte[][] bArr = (byte[][]) this.value;
                    r4 = new byte[bArr.length][];
                    com_google_android_gms_internal_zzarx.value = r4;
                    for (r2 = 0; r2 < bArr.length; r2++) {
                        r4[r2] = (byte[]) bArr[r2].clone();
                    }
                } else if (this.value instanceof boolean[]) {
                    com_google_android_gms_internal_zzarx.value = ((boolean[]) this.value).clone();
                } else if (this.value instanceof int[]) {
                    com_google_android_gms_internal_zzarx.value = ((int[]) this.value).clone();
                } else if (this.value instanceof long[]) {
                    com_google_android_gms_internal_zzarx.value = ((long[]) this.value).clone();
                } else if (this.value instanceof float[]) {
                    com_google_android_gms_internal_zzarx.value = ((float[]) this.value).clone();
                } else if (this.value instanceof double[]) {
                    com_google_android_gms_internal_zzarx.value = ((double[]) this.value).clone();
                } else if (this.value instanceof zzasa[]) {
                    zzasa[] com_google_android_gms_internal_zzasaArr = (zzasa[]) this.value;
                    r4 = new zzasa[com_google_android_gms_internal_zzasaArr.length];
                    com_google_android_gms_internal_zzarx.value = r4;
                    for (r2 = 0; r2 < com_google_android_gms_internal_zzasaArr.length; r2++) {
                        r4[r2] = (zzasa) com_google_android_gms_internal_zzasaArr[r2].clone();
                    }
                }
            }
            return com_google_android_gms_internal_zzarx;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError(e);
        }
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof zzarx)) {
            return false;
        }
        zzarx com_google_android_gms_internal_zzarx = (zzarx) obj;
        if (this.value != null && com_google_android_gms_internal_zzarx.value != null) {
            return this.btM == com_google_android_gms_internal_zzarx.btM ? !this.btM.bkp.isArray() ? this.value.equals(com_google_android_gms_internal_zzarx.value) : this.value instanceof byte[] ? Arrays.equals((byte[]) this.value, (byte[]) com_google_android_gms_internal_zzarx.value) : this.value instanceof int[] ? Arrays.equals((int[]) this.value, (int[]) com_google_android_gms_internal_zzarx.value) : this.value instanceof long[] ? Arrays.equals((long[]) this.value, (long[]) com_google_android_gms_internal_zzarx.value) : this.value instanceof float[] ? Arrays.equals((float[]) this.value, (float[]) com_google_android_gms_internal_zzarx.value) : this.value instanceof double[] ? Arrays.equals((double[]) this.value, (double[]) com_google_android_gms_internal_zzarx.value) : this.value instanceof boolean[] ? Arrays.equals((boolean[]) this.value, (boolean[]) com_google_android_gms_internal_zzarx.value) : Arrays.deepEquals((Object[]) this.value, (Object[]) com_google_android_gms_internal_zzarx.value) : false;
        } else {
            if (this.btN != null && com_google_android_gms_internal_zzarx.btN != null) {
                return this.btN.equals(com_google_android_gms_internal_zzarx.btN);
            }
            try {
                return Arrays.equals(toByteArray(), com_google_android_gms_internal_zzarx.toByteArray());
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

    void zza(zzart com_google_android_gms_internal_zzart) throws IOException {
        if (this.value != null) {
            this.btM.zza(this.value, com_google_android_gms_internal_zzart);
            return;
        }
        for (zzasc zza : this.btN) {
            zza.zza(com_google_android_gms_internal_zzart);
        }
    }

    void zza(zzasc com_google_android_gms_internal_zzasc) {
        this.btN.add(com_google_android_gms_internal_zzasc);
    }

    <T> T zzb(zzarv<?, T> com_google_android_gms_internal_zzarv___T) {
        if (this.value == null) {
            this.btM = com_google_android_gms_internal_zzarv___T;
            this.value = com_google_android_gms_internal_zzarv___T.zzay(this.btN);
            this.btN = null;
        } else if (!this.btM.equals(com_google_android_gms_internal_zzarv___T)) {
            throw new IllegalStateException("Tried to getExtension with a different Extension.");
        }
        return this.value;
    }

    int zzx() {
        if (this.value != null) {
            return this.btM.zzct(this.value);
        }
        int i = 0;
        for (zzasc zzx : this.btN) {
            i = zzx.zzx() + i;
        }
        return i;
    }
}
