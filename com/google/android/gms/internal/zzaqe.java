package com.google.android.gms.internal;

public final class zzaqe implements zzapl {
    private final zzaps bod;

    public zzaqe(zzaps com_google_android_gms_internal_zzaps) {
        this.bod = com_google_android_gms_internal_zzaps;
    }

    static zzapk<?> zza(zzaps com_google_android_gms_internal_zzaps, zzaos com_google_android_gms_internal_zzaos, zzaqo<?> com_google_android_gms_internal_zzaqo_, zzapm com_google_android_gms_internal_zzapm) {
        Class value = com_google_android_gms_internal_zzapm.value();
        if (zzapk.class.isAssignableFrom(value)) {
            return (zzapk) com_google_android_gms_internal_zzaps.zzb(zzaqo.zzr(value)).bj();
        }
        if (zzapl.class.isAssignableFrom(value)) {
            return ((zzapl) com_google_android_gms_internal_zzaps.zzb(zzaqo.zzr(value)).bj()).zza(com_google_android_gms_internal_zzaos, com_google_android_gms_internal_zzaqo_);
        }
        throw new IllegalArgumentException("@JsonAdapter value must be TypeAdapter or TypeAdapterFactory reference.");
    }

    public <T> zzapk<T> zza(zzaos com_google_android_gms_internal_zzaos, zzaqo<T> com_google_android_gms_internal_zzaqo_T) {
        zzapm com_google_android_gms_internal_zzapm = (zzapm) com_google_android_gms_internal_zzaqo_T.bB().getAnnotation(zzapm.class);
        return com_google_android_gms_internal_zzapm == null ? null : zza(this.bod, com_google_android_gms_internal_zzaos, com_google_android_gms_internal_zzaqo_T, com_google_android_gms_internal_zzapm);
    }
}
