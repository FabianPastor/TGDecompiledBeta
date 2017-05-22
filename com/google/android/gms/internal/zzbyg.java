package com.google.android.gms.internal;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

class zzbyg implements Cloneable {
    private Object value;
    private zzbye<?, ?> zzcwI;
    private List<zzbyl> zzcwJ = new ArrayList();

    zzbyg() {
    }

    private byte[] toByteArray() throws IOException {
        byte[] bArr = new byte[zzu()];
        zza(zzbyc.zzah(bArr));
        return bArr;
    }

    public /* synthetic */ Object clone() throws CloneNotSupportedException {
        return zzafs();
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof zzbyg)) {
            return false;
        }
        zzbyg com_google_android_gms_internal_zzbyg = (zzbyg) obj;
        if (this.value != null && com_google_android_gms_internal_zzbyg.value != null) {
            return this.zzcwI == com_google_android_gms_internal_zzbyg.zzcwI ? !this.zzcwI.zzckL.isArray() ? this.value.equals(com_google_android_gms_internal_zzbyg.value) : this.value instanceof byte[] ? Arrays.equals((byte[]) this.value, (byte[]) com_google_android_gms_internal_zzbyg.value) : this.value instanceof int[] ? Arrays.equals((int[]) this.value, (int[]) com_google_android_gms_internal_zzbyg.value) : this.value instanceof long[] ? Arrays.equals((long[]) this.value, (long[]) com_google_android_gms_internal_zzbyg.value) : this.value instanceof float[] ? Arrays.equals((float[]) this.value, (float[]) com_google_android_gms_internal_zzbyg.value) : this.value instanceof double[] ? Arrays.equals((double[]) this.value, (double[]) com_google_android_gms_internal_zzbyg.value) : this.value instanceof boolean[] ? Arrays.equals((boolean[]) this.value, (boolean[]) com_google_android_gms_internal_zzbyg.value) : Arrays.deepEquals((Object[]) this.value, (Object[]) com_google_android_gms_internal_zzbyg.value) : false;
        } else {
            if (this.zzcwJ != null && com_google_android_gms_internal_zzbyg.zzcwJ != null) {
                return this.zzcwJ.equals(com_google_android_gms_internal_zzbyg.zzcwJ);
            }
            try {
                return Arrays.equals(toByteArray(), com_google_android_gms_internal_zzbyg.toByteArray());
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

    void zza(zzbyc com_google_android_gms_internal_zzbyc) throws IOException {
        if (this.value != null) {
            this.zzcwI.zza(this.value, com_google_android_gms_internal_zzbyc);
            return;
        }
        for (zzbyl zza : this.zzcwJ) {
            zza.zza(com_google_android_gms_internal_zzbyc);
        }
    }

    void zza(zzbyl com_google_android_gms_internal_zzbyl) {
        this.zzcwJ.add(com_google_android_gms_internal_zzbyl);
    }

    public final zzbyg zzafs() {
        zzbyg com_google_android_gms_internal_zzbyg = new zzbyg();
        try {
            com_google_android_gms_internal_zzbyg.zzcwI = this.zzcwI;
            if (this.zzcwJ == null) {
                com_google_android_gms_internal_zzbyg.zzcwJ = null;
            } else {
                com_google_android_gms_internal_zzbyg.zzcwJ.addAll(this.zzcwJ);
            }
            if (this.value != null) {
                if (this.value instanceof zzbyj) {
                    com_google_android_gms_internal_zzbyg.value = (zzbyj) ((zzbyj) this.value).clone();
                } else if (this.value instanceof byte[]) {
                    com_google_android_gms_internal_zzbyg.value = ((byte[]) this.value).clone();
                } else if (this.value instanceof byte[][]) {
                    byte[][] bArr = (byte[][]) this.value;
                    r4 = new byte[bArr.length][];
                    com_google_android_gms_internal_zzbyg.value = r4;
                    for (r2 = 0; r2 < bArr.length; r2++) {
                        r4[r2] = (byte[]) bArr[r2].clone();
                    }
                } else if (this.value instanceof boolean[]) {
                    com_google_android_gms_internal_zzbyg.value = ((boolean[]) this.value).clone();
                } else if (this.value instanceof int[]) {
                    com_google_android_gms_internal_zzbyg.value = ((int[]) this.value).clone();
                } else if (this.value instanceof long[]) {
                    com_google_android_gms_internal_zzbyg.value = ((long[]) this.value).clone();
                } else if (this.value instanceof float[]) {
                    com_google_android_gms_internal_zzbyg.value = ((float[]) this.value).clone();
                } else if (this.value instanceof double[]) {
                    com_google_android_gms_internal_zzbyg.value = ((double[]) this.value).clone();
                } else if (this.value instanceof zzbyj[]) {
                    zzbyj[] com_google_android_gms_internal_zzbyjArr = (zzbyj[]) this.value;
                    r4 = new zzbyj[com_google_android_gms_internal_zzbyjArr.length];
                    com_google_android_gms_internal_zzbyg.value = r4;
                    for (r2 = 0; r2 < com_google_android_gms_internal_zzbyjArr.length; r2++) {
                        r4[r2] = (zzbyj) com_google_android_gms_internal_zzbyjArr[r2].clone();
                    }
                }
            }
            return com_google_android_gms_internal_zzbyg;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError(e);
        }
    }

    <T> T zzb(zzbye<?, T> com_google_android_gms_internal_zzbye___T) {
        if (this.value == null) {
            this.zzcwI = com_google_android_gms_internal_zzbye___T;
            this.value = com_google_android_gms_internal_zzbye___T.zzad(this.zzcwJ);
            this.zzcwJ = null;
        } else if (!this.zzcwI.equals(com_google_android_gms_internal_zzbye___T)) {
            throw new IllegalStateException("Tried to getExtension with a different Extension.");
        }
        return this.value;
    }

    int zzu() {
        if (this.value != null) {
            return this.zzcwI.zzaV(this.value);
        }
        int i = 0;
        for (zzbyl zzu : this.zzcwJ) {
            i = zzu.zzu() + i;
        }
        return i;
    }
}
