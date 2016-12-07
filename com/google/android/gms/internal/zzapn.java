package com.google.android.gms.internal;

public final class zzapn implements zzaou {
    private final zzapb bkM;

    public zzapn(zzapb com_google_android_gms_internal_zzapb) {
        this.bkM = com_google_android_gms_internal_zzapb;
    }

    static zzaot<?> zza(zzapb com_google_android_gms_internal_zzapb, zzaob com_google_android_gms_internal_zzaob, zzapx<?> com_google_android_gms_internal_zzapx_, zzaov com_google_android_gms_internal_zzaov) {
        Class value = com_google_android_gms_internal_zzaov.value();
        if (zzaot.class.isAssignableFrom(value)) {
            return (zzaot) com_google_android_gms_internal_zzapb.zzb(zzapx.zzr(value)).bg();
        }
        if (zzaou.class.isAssignableFrom(value)) {
            return ((zzaou) com_google_android_gms_internal_zzapb.zzb(zzapx.zzr(value)).bg()).zza(com_google_android_gms_internal_zzaob, com_google_android_gms_internal_zzapx_);
        }
        throw new IllegalArgumentException("@JsonAdapter value must be TypeAdapter or TypeAdapterFactory reference.");
    }

    public <T> zzaot<T> zza(zzaob com_google_android_gms_internal_zzaob, zzapx<T> com_google_android_gms_internal_zzapx_T) {
        zzaov com_google_android_gms_internal_zzaov = (zzaov) com_google_android_gms_internal_zzapx_T.by().getAnnotation(zzaov.class);
        return com_google_android_gms_internal_zzaov == null ? null : zza(this.bkM, com_google_android_gms_internal_zzaob, com_google_android_gms_internal_zzapx_T, com_google_android_gms_internal_zzaov);
    }
}
