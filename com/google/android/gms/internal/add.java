package com.google.android.gms.internal;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

final class add implements Cloneable {
    private Object value;
    private adb<?, ?> zzcsf;
    private List<adi> zzcsg = new ArrayList();

    add() {
    }

    private final byte[] toByteArray() throws IOException {
        byte[] bArr = new byte[zzn()];
        zza(acy.zzI(bArr));
        return bArr;
    }

    private add zzLN() {
        add com_google_android_gms_internal_add = new add();
        try {
            com_google_android_gms_internal_add.zzcsf = this.zzcsf;
            if (this.zzcsg == null) {
                com_google_android_gms_internal_add.zzcsg = null;
            } else {
                com_google_android_gms_internal_add.zzcsg.addAll(this.zzcsg);
            }
            if (this.value != null) {
                if (this.value instanceof adg) {
                    com_google_android_gms_internal_add.value = (adg) ((adg) this.value).clone();
                } else if (this.value instanceof byte[]) {
                    com_google_android_gms_internal_add.value = ((byte[]) this.value).clone();
                } else if (this.value instanceof byte[][]) {
                    byte[][] bArr = (byte[][]) this.value;
                    r4 = new byte[bArr.length][];
                    com_google_android_gms_internal_add.value = r4;
                    for (r2 = 0; r2 < bArr.length; r2++) {
                        r4[r2] = (byte[]) bArr[r2].clone();
                    }
                } else if (this.value instanceof boolean[]) {
                    com_google_android_gms_internal_add.value = ((boolean[]) this.value).clone();
                } else if (this.value instanceof int[]) {
                    com_google_android_gms_internal_add.value = ((int[]) this.value).clone();
                } else if (this.value instanceof long[]) {
                    com_google_android_gms_internal_add.value = ((long[]) this.value).clone();
                } else if (this.value instanceof float[]) {
                    com_google_android_gms_internal_add.value = ((float[]) this.value).clone();
                } else if (this.value instanceof double[]) {
                    com_google_android_gms_internal_add.value = ((double[]) this.value).clone();
                } else if (this.value instanceof adg[]) {
                    adg[] com_google_android_gms_internal_adgArr = (adg[]) this.value;
                    r4 = new adg[com_google_android_gms_internal_adgArr.length];
                    com_google_android_gms_internal_add.value = r4;
                    for (r2 = 0; r2 < com_google_android_gms_internal_adgArr.length; r2++) {
                        r4[r2] = (adg) com_google_android_gms_internal_adgArr[r2].clone();
                    }
                }
            }
            return com_google_android_gms_internal_add;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError(e);
        }
    }

    public final /* synthetic */ Object clone() throws CloneNotSupportedException {
        return zzLN();
    }

    public final boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof add)) {
            return false;
        }
        add com_google_android_gms_internal_add = (add) obj;
        if (this.value != null && com_google_android_gms_internal_add.value != null) {
            return this.zzcsf == com_google_android_gms_internal_add.zzcsf ? !this.zzcsf.zzcjC.isArray() ? this.value.equals(com_google_android_gms_internal_add.value) : this.value instanceof byte[] ? Arrays.equals((byte[]) this.value, (byte[]) com_google_android_gms_internal_add.value) : this.value instanceof int[] ? Arrays.equals((int[]) this.value, (int[]) com_google_android_gms_internal_add.value) : this.value instanceof long[] ? Arrays.equals((long[]) this.value, (long[]) com_google_android_gms_internal_add.value) : this.value instanceof float[] ? Arrays.equals((float[]) this.value, (float[]) com_google_android_gms_internal_add.value) : this.value instanceof double[] ? Arrays.equals((double[]) this.value, (double[]) com_google_android_gms_internal_add.value) : this.value instanceof boolean[] ? Arrays.equals((boolean[]) this.value, (boolean[]) com_google_android_gms_internal_add.value) : Arrays.deepEquals((Object[]) this.value, (Object[]) com_google_android_gms_internal_add.value) : false;
        } else {
            if (this.zzcsg != null && com_google_android_gms_internal_add.zzcsg != null) {
                return this.zzcsg.equals(com_google_android_gms_internal_add.zzcsg);
            }
            try {
                return Arrays.equals(toByteArray(), com_google_android_gms_internal_add.toByteArray());
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

    final void zza(acy com_google_android_gms_internal_acy) throws IOException {
        if (this.value != null) {
            this.zzcsf.zza(this.value, com_google_android_gms_internal_acy);
            return;
        }
        for (adi com_google_android_gms_internal_adi : this.zzcsg) {
            com_google_android_gms_internal_acy.zzcu(com_google_android_gms_internal_adi.tag);
            com_google_android_gms_internal_acy.zzK(com_google_android_gms_internal_adi.zzbws);
        }
    }

    final void zza(adi com_google_android_gms_internal_adi) {
        this.zzcsg.add(com_google_android_gms_internal_adi);
    }

    final <T> T zzb(adb<?, T> com_google_android_gms_internal_adb___T) {
        if (this.value == null) {
            this.zzcsf = com_google_android_gms_internal_adb___T;
            this.value = com_google_android_gms_internal_adb___T.zzX(this.zzcsg);
            this.zzcsg = null;
        } else if (!this.zzcsf.equals(com_google_android_gms_internal_adb___T)) {
            throw new IllegalStateException("Tried to getExtension with a different Extension.");
        }
        return this.value;
    }

    final int zzn() {
        if (this.value != null) {
            return this.zzcsf.zzav(this.value);
        }
        int i = 0;
        for (adi com_google_android_gms_internal_adi : this.zzcsg) {
            i = (com_google_android_gms_internal_adi.zzbws.length + (acy.zzcv(com_google_android_gms_internal_adi.tag) + 0)) + i;
        }
        return i;
    }
}
