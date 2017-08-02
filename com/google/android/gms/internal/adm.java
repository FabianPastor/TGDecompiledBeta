package com.google.android.gms.internal;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

final class adm implements Cloneable {
    private Object value;
    private adk<?, ?> zzcsu;
    private List<adr> zzcsv = new ArrayList();

    adm() {
    }

    private final byte[] toByteArray() throws IOException {
        byte[] bArr = new byte[zzn()];
        zza(adh.zzI(bArr));
        return bArr;
    }

    private adm zzLP() {
        adm com_google_android_gms_internal_adm = new adm();
        try {
            com_google_android_gms_internal_adm.zzcsu = this.zzcsu;
            if (this.zzcsv == null) {
                com_google_android_gms_internal_adm.zzcsv = null;
            } else {
                com_google_android_gms_internal_adm.zzcsv.addAll(this.zzcsv);
            }
            if (this.value != null) {
                if (this.value instanceof adp) {
                    com_google_android_gms_internal_adm.value = (adp) ((adp) this.value).clone();
                } else if (this.value instanceof byte[]) {
                    com_google_android_gms_internal_adm.value = ((byte[]) this.value).clone();
                } else if (this.value instanceof byte[][]) {
                    byte[][] bArr = (byte[][]) this.value;
                    r4 = new byte[bArr.length][];
                    com_google_android_gms_internal_adm.value = r4;
                    for (r2 = 0; r2 < bArr.length; r2++) {
                        r4[r2] = (byte[]) bArr[r2].clone();
                    }
                } else if (this.value instanceof boolean[]) {
                    com_google_android_gms_internal_adm.value = ((boolean[]) this.value).clone();
                } else if (this.value instanceof int[]) {
                    com_google_android_gms_internal_adm.value = ((int[]) this.value).clone();
                } else if (this.value instanceof long[]) {
                    com_google_android_gms_internal_adm.value = ((long[]) this.value).clone();
                } else if (this.value instanceof float[]) {
                    com_google_android_gms_internal_adm.value = ((float[]) this.value).clone();
                } else if (this.value instanceof double[]) {
                    com_google_android_gms_internal_adm.value = ((double[]) this.value).clone();
                } else if (this.value instanceof adp[]) {
                    adp[] com_google_android_gms_internal_adpArr = (adp[]) this.value;
                    r4 = new adp[com_google_android_gms_internal_adpArr.length];
                    com_google_android_gms_internal_adm.value = r4;
                    for (r2 = 0; r2 < com_google_android_gms_internal_adpArr.length; r2++) {
                        r4[r2] = (adp) com_google_android_gms_internal_adpArr[r2].clone();
                    }
                }
            }
            return com_google_android_gms_internal_adm;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError(e);
        }
    }

    public final /* synthetic */ Object clone() throws CloneNotSupportedException {
        return zzLP();
    }

    public final boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof adm)) {
            return false;
        }
        adm com_google_android_gms_internal_adm = (adm) obj;
        if (this.value != null && com_google_android_gms_internal_adm.value != null) {
            return this.zzcsu == com_google_android_gms_internal_adm.zzcsu ? !this.zzcsu.zzcjG.isArray() ? this.value.equals(com_google_android_gms_internal_adm.value) : this.value instanceof byte[] ? Arrays.equals((byte[]) this.value, (byte[]) com_google_android_gms_internal_adm.value) : this.value instanceof int[] ? Arrays.equals((int[]) this.value, (int[]) com_google_android_gms_internal_adm.value) : this.value instanceof long[] ? Arrays.equals((long[]) this.value, (long[]) com_google_android_gms_internal_adm.value) : this.value instanceof float[] ? Arrays.equals((float[]) this.value, (float[]) com_google_android_gms_internal_adm.value) : this.value instanceof double[] ? Arrays.equals((double[]) this.value, (double[]) com_google_android_gms_internal_adm.value) : this.value instanceof boolean[] ? Arrays.equals((boolean[]) this.value, (boolean[]) com_google_android_gms_internal_adm.value) : Arrays.deepEquals((Object[]) this.value, (Object[]) com_google_android_gms_internal_adm.value) : false;
        } else {
            if (this.zzcsv != null && com_google_android_gms_internal_adm.zzcsv != null) {
                return this.zzcsv.equals(com_google_android_gms_internal_adm.zzcsv);
            }
            try {
                return Arrays.equals(toByteArray(), com_google_android_gms_internal_adm.toByteArray());
            } catch (Throwable e) {
                throw new IllegalStateException(e);
            }
        }
    }

    public final int hashCode() {
        try {
            return Arrays.hashCode(toByteArray()) + 527;
        } catch (Throwable e) {
            throw new IllegalStateException(e);
        }
    }

    final void zza(adh com_google_android_gms_internal_adh) throws IOException {
        if (this.value != null) {
            this.zzcsu.zza(this.value, com_google_android_gms_internal_adh);
            return;
        }
        for (adr com_google_android_gms_internal_adr : this.zzcsv) {
            com_google_android_gms_internal_adh.zzcu(com_google_android_gms_internal_adr.tag);
            com_google_android_gms_internal_adh.zzK(com_google_android_gms_internal_adr.zzbws);
        }
    }

    final void zza(adr com_google_android_gms_internal_adr) {
        this.zzcsv.add(com_google_android_gms_internal_adr);
    }

    final <T> T zzb(adk<?, T> com_google_android_gms_internal_adk___T) {
        if (this.value == null) {
            this.zzcsu = com_google_android_gms_internal_adk___T;
            this.value = com_google_android_gms_internal_adk___T.zzX(this.zzcsv);
            this.zzcsv = null;
        } else if (!this.zzcsu.equals(com_google_android_gms_internal_adk___T)) {
            throw new IllegalStateException("Tried to getExtension with a different Extension.");
        }
        return this.value;
    }

    final int zzn() {
        if (this.value != null) {
            return this.zzcsu.zzav(this.value);
        }
        int i = 0;
        for (adr com_google_android_gms_internal_adr : this.zzcsv) {
            i = (com_google_android_gms_internal_adr.zzbws.length + (adh.zzcv(com_google_android_gms_internal_adr.tag) + 0)) + i;
        }
        return i;
    }
}
