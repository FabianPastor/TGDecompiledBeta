package com.google.android.gms.internal;

import java.io.UnsupportedEncodingException;

public class zzar extends zzp<String> {
    private final zzv<String> zzaD;

    public zzar(int i, String str, zzv<String> com_google_android_gms_internal_zzv_java_lang_String, zzu com_google_android_gms_internal_zzu) {
        super(i, str, com_google_android_gms_internal_zzu);
        this.zzaD = com_google_android_gms_internal_zzv_java_lang_String;
    }

    protected final zzt<String> zza(zzn com_google_android_gms_internal_zzn) {
        Object str;
        try {
            str = new String(com_google_android_gms_internal_zzn.data, zzam.zza(com_google_android_gms_internal_zzn.zzy));
        } catch (UnsupportedEncodingException e) {
            str = new String(com_google_android_gms_internal_zzn.data);
        }
        return zzt.zza(str, zzam.zzb(com_google_android_gms_internal_zzn));
    }

    protected final /* synthetic */ void zza(Object obj) {
        this.zzaD.zzb((String) obj);
    }
}
