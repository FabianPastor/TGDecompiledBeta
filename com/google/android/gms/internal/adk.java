package com.google.android.gms.internal;

import java.util.List;

public final class adk<M extends adj<M>, T> {
    public final int tag;
    private int type = 11;
    protected final Class<T> zzcjG;
    protected final boolean zzcsp;

    private adk(int i, Class<T> cls, int i2, boolean z) {
        this.zzcjG = cls;
        this.tag = i2;
        this.zzcsp = false;
    }

    public static <M extends adj<M>, T extends adp> adk<M, T> zza(int i, Class<T> cls, long j) {
        return new adk(11, cls, (int) j, false);
    }

    private final Object zzb(adg com_google_android_gms_internal_adg) {
        String valueOf;
        Class cls = this.zzcjG;
        try {
            adp com_google_android_gms_internal_adp;
            switch (this.type) {
                case 10:
                    com_google_android_gms_internal_adp = (adp) cls.newInstance();
                    com_google_android_gms_internal_adg.zza(com_google_android_gms_internal_adp, this.tag >>> 3);
                    return com_google_android_gms_internal_adp;
                case 11:
                    com_google_android_gms_internal_adp = (adp) cls.newInstance();
                    com_google_android_gms_internal_adg.zza(com_google_android_gms_internal_adp);
                    return com_google_android_gms_internal_adp;
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
        if (!(obj instanceof adk)) {
            return false;
        }
        adk com_google_android_gms_internal_adk = (adk) obj;
        return this.type == com_google_android_gms_internal_adk.type && this.zzcjG == com_google_android_gms_internal_adk.zzcjG && this.tag == com_google_android_gms_internal_adk.tag;
    }

    public final int hashCode() {
        return (((((this.type + 1147) * 31) + this.zzcjG.hashCode()) * 31) + this.tag) * 31;
    }

    final T zzX(List<adr> list) {
        if (list == null || list.isEmpty()) {
            return null;
        }
        return this.zzcjG.cast(zzb(adg.zzH(((adr) list.get(list.size() - 1)).zzbws)));
    }

    protected final void zza(Object obj, adh com_google_android_gms_internal_adh) {
        try {
            com_google_android_gms_internal_adh.zzcu(this.tag);
            switch (this.type) {
                case 10:
                    int i = this.tag >>> 3;
                    ((adp) obj).zza(com_google_android_gms_internal_adh);
                    com_google_android_gms_internal_adh.zzt(i, 4);
                    return;
                case 11:
                    com_google_android_gms_internal_adh.zzb((adp) obj);
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
                return (adh.zzct(i) << 1) + ((adp) obj).zzLV();
            case 11:
                return adh.zzb(i, (adp) obj);
            default:
                throw new IllegalArgumentException("Unknown type " + this.type);
        }
    }
}
