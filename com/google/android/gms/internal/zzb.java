package com.google.android.gms.internal;

import java.util.Collections;
import java.util.Map;

public interface zzb {

    public static class zza {
        public byte[] data;
        public String zza;
        public long zzb;
        public long zzc;
        public long zzd;
        public long zze;
        public Map<String, String> zzf = Collections.emptyMap();

        public boolean zza() {
            return this.zzd < System.currentTimeMillis();
        }

        public boolean zzb() {
            return this.zze < System.currentTimeMillis();
        }
    }

    void initialize();

    zza zza(String str);

    void zza(String str, zza com_google_android_gms_internal_zzb_zza);
}
