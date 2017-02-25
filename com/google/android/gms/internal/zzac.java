package com.google.android.gms.internal;

import com.google.android.gms.internal.zzn.zza;
import com.google.android.gms.internal.zzn.zzb;
import java.io.UnsupportedEncodingException;

public class zzac extends zzl<String> {
    private final zzb<String> zzaG;

    public zzac(int i, String str, zzb<String> com_google_android_gms_internal_zzn_zzb_java_lang_String, zza com_google_android_gms_internal_zzn_zza) {
        super(i, str, com_google_android_gms_internal_zzn_zza);
        this.zzaG = com_google_android_gms_internal_zzn_zzb_java_lang_String;
    }

    protected zzn<String> zza(zzj com_google_android_gms_internal_zzj) {
        Object str;
        try {
            str = new String(com_google_android_gms_internal_zzj.data, zzy.zza(com_google_android_gms_internal_zzj.zzz));
        } catch (UnsupportedEncodingException e) {
            str = new String(com_google_android_gms_internal_zzj.data);
        }
        return zzn.zza(str, zzy.zzb(com_google_android_gms_internal_zzj));
    }

    protected /* synthetic */ void zza(Object obj) {
        zzi((String) obj);
    }

    protected void zzi(String str) {
        this.zzaG.zzb(str);
    }
}
