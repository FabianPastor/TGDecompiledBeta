package com.google.android.gms.internal;

public final class zzfjn<M extends zzfjm<M>, T> {
    public final int tag;
    private int type;
    protected final Class<T> zznfk;
    protected final boolean zzpnd;

    public final boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof zzfjn)) {
            return false;
        }
        zzfjn com_google_android_gms_internal_zzfjn = (zzfjn) obj;
        return this.type == com_google_android_gms_internal_zzfjn.type && this.zznfk == com_google_android_gms_internal_zzfjn.zznfk && this.tag == com_google_android_gms_internal_zzfjn.tag && this.zzpnd == com_google_android_gms_internal_zzfjn.zzpnd;
    }

    public final int hashCode() {
        return (this.zzpnd ? 1 : 0) + ((((((this.type + 1147) * 31) + this.zznfk.hashCode()) * 31) + this.tag) * 31);
    }

    protected final void zza(Object obj, zzfjk com_google_android_gms_internal_zzfjk) {
        try {
            com_google_android_gms_internal_zzfjk.zzmi(this.tag);
            switch (this.type) {
                case 10:
                    int i = this.tag >>> 3;
                    ((zzfjs) obj).zza(com_google_android_gms_internal_zzfjk);
                    com_google_android_gms_internal_zzfjk.zzz(i, 4);
                    return;
                case 11:
                    com_google_android_gms_internal_zzfjk.zzb((zzfjs) obj);
                    return;
                default:
                    throw new IllegalArgumentException("Unknown type " + this.type);
            }
        } catch (Throwable e) {
            throw new IllegalStateException(e);
        }
        throw new IllegalStateException(e);
    }

    protected final int zzcs(Object obj) {
        int i = this.tag >>> 3;
        switch (this.type) {
            case 10:
                return (zzfjk.zzlg(i) << 1) + ((zzfjs) obj).zzho();
            case 11:
                return zzfjk.zzb(i, (zzfjs) obj);
            default:
                throw new IllegalArgumentException("Unknown type " + this.type);
        }
    }
}
