package com.google.android.gms.internal;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;
import java.util.Map.Entry;

final class zzai {
    public String key;
    public long size;
    public String zza;
    public long zzb;
    public long zzc;
    public long zzd;
    public long zze;
    public Map<String, String> zzf;

    private zzai() {
    }

    public zzai(String str, zzc com_google_android_gms_internal_zzc) {
        this.key = str;
        this.size = (long) com_google_android_gms_internal_zzc.data.length;
        this.zza = com_google_android_gms_internal_zzc.zza;
        this.zzb = com_google_android_gms_internal_zzc.zzb;
        this.zzc = com_google_android_gms_internal_zzc.zzc;
        this.zzd = com_google_android_gms_internal_zzc.zzd;
        this.zze = com_google_android_gms_internal_zzc.zze;
        this.zzf = com_google_android_gms_internal_zzc.zzf;
    }

    public static zzai zzf(InputStream inputStream) throws IOException {
        zzai com_google_android_gms_internal_zzai = new zzai();
        if (zzag.zzb(inputStream) != 538247942) {
            throw new IOException();
        }
        com_google_android_gms_internal_zzai.key = zzag.zzd(inputStream);
        com_google_android_gms_internal_zzai.zza = zzag.zzd(inputStream);
        if (com_google_android_gms_internal_zzai.zza.equals("")) {
            com_google_android_gms_internal_zzai.zza = null;
        }
        com_google_android_gms_internal_zzai.zzb = zzag.zzc(inputStream);
        com_google_android_gms_internal_zzai.zzc = zzag.zzc(inputStream);
        com_google_android_gms_internal_zzai.zzd = zzag.zzc(inputStream);
        com_google_android_gms_internal_zzai.zze = zzag.zzc(inputStream);
        com_google_android_gms_internal_zzai.zzf = zzag.zze(inputStream);
        return com_google_android_gms_internal_zzai;
    }

    public final boolean zza(OutputStream outputStream) {
        try {
            zzag.zza(outputStream, 538247942);
            zzag.zza(outputStream, this.key);
            zzag.zza(outputStream, this.zza == null ? "" : this.zza);
            zzag.zza(outputStream, this.zzb);
            zzag.zza(outputStream, this.zzc);
            zzag.zza(outputStream, this.zzd);
            zzag.zza(outputStream, this.zze);
            Map map = this.zzf;
            if (map != null) {
                zzag.zza(outputStream, map.size());
                for (Entry entry : map.entrySet()) {
                    zzag.zza(outputStream, (String) entry.getKey());
                    zzag.zza(outputStream, (String) entry.getValue());
                }
            } else {
                zzag.zza(outputStream, 0);
            }
            outputStream.flush();
            return true;
        } catch (IOException e) {
            zzab.zzb("%s", e.toString());
            return false;
        }
    }
}
