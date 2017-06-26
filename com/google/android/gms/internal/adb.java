package com.google.android.gms.internal;

import java.util.List;

public final class adb<M extends ada<M>, T> {
    public final int tag;
    private int type = 11;
    protected final Class<T> zzcjC;
    protected final boolean zzcsa;

    private adb(int i, Class<T> cls, int i2, boolean z) {
        this.zzcjC = cls;
        this.tag = i2;
        this.zzcsa = false;
    }

    public static <M extends ada<M>, T extends adg> adb<M, T> zza(int i, Class<T> cls, long j) {
        return new adb(11, cls, (int) j, false);
    }

    private final Object zzb(acx com_google_android_gms_internal_acx) {
        String valueOf;
        Class cls = this.zzcjC;
        try {
            adg com_google_android_gms_internal_adg;
            switch (this.type) {
                case 10:
                    com_google_android_gms_internal_adg = (adg) cls.newInstance();
                    com_google_android_gms_internal_acx.zza(com_google_android_gms_internal_adg, this.tag >>> 3);
                    return com_google_android_gms_internal_adg;
                case 11:
                    com_google_android_gms_internal_adg = (adg) cls.newInstance();
                    com_google_android_gms_internal_acx.zza(com_google_android_gms_internal_adg);
                    return com_google_android_gms_internal_adg;
                default:
                    throw new IllegalArgumentException("Unknown type " + this.type);
            }
        } catch (Throwable e) {
            valueOf = String.valueOf(cls);
            throw new IllegalArgumentException(new StringBuilder(String.valueOf(valueOf).length() + 33).append("Error creating instance of class ").append(valueOf).toString(), e);
        } catch (Throwable e2) {
            valueOf = String.valueOf(cls);
            throw new IllegalArgumentException(new StringBuilder(String.valueOf(valueOf).length() + 33).append("Error creating instance of class ").append(valueOf).toString(), e2);
        } catch (Throwable e22) {
            throw new IllegalArgumentException("Error reading extension field", e22);
        }
    }

    public final boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof adb)) {
            return false;
        }
        adb com_google_android_gms_internal_adb = (adb) obj;
        return this.type == com_google_android_gms_internal_adb.type && this.zzcjC == com_google_android_gms_internal_adb.zzcjC && this.tag == com_google_android_gms_internal_adb.tag;
    }

    public final int hashCode() {
        return (((((this.type + 1147) * 31) + this.zzcjC.hashCode()) * 31) + this.tag) * 31;
    }

    final T zzX(List<adi> list) {
        if (list == null || list.isEmpty()) {
            return null;
        }
        return this.zzcjC.cast(zzb(acx.zzH(((adi) list.get(list.size() - 1)).zzbws)));
    }

    protected final void zza(Object obj, acy com_google_android_gms_internal_acy) {
        try {
            com_google_android_gms_internal_acy.zzcu(this.tag);
            switch (this.type) {
                case 10:
                    int i = this.tag >>> 3;
                    ((adg) obj).zza(com_google_android_gms_internal_acy);
                    com_google_android_gms_internal_acy.zzt(i, 4);
                    return;
                case 11:
                    com_google_android_gms_internal_acy.zzb((adg) obj);
                    return;
                default:
                    throw new IllegalArgumentException("Unknown type " + this.type);
            }
        } catch (Throwable e) {
            throw new IllegalStateException(e);
        }
        throw new IllegalStateException(e);
    }

    protected final int zzav(Object obj) {
        int i = this.tag >>> 3;
        switch (this.type) {
            case 10:
                return (acy.zzct(i) << 1) + ((adg) obj).zzLT();
            case 11:
                return acy.zzb(i, (adg) obj);
            default:
                throw new IllegalArgumentException("Unknown type " + this.type);
        }
    }
}
