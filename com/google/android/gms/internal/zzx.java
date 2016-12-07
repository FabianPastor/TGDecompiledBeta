package com.google.android.gms.internal;

import com.google.android.gms.internal.zzb.zza;
import java.util.Map;
import org.apache.http.impl.cookie.DateParseException;
import org.apache.http.impl.cookie.DateUtils;

public class zzx {
    public static String zza(Map<String, String> map) {
        return zza(map, "ISO-8859-1");
    }

    public static String zza(Map<String, String> map, String str) {
        String str2 = (String) map.get("Content-Type");
        if (str2 == null) {
            return str;
        }
        String[] split = str2.split(";");
        for (int i = 1; i < split.length; i++) {
            String[] split2 = split[i].trim().split("=");
            if (split2.length == 2 && split2[0].equals("charset")) {
                return split2[1];
            }
        }
        return str;
    }

    public static zza zzb(zzi com_google_android_gms_internal_zzi) {
        Object obj;
        long j;
        Object obj2;
        long currentTimeMillis = System.currentTimeMillis();
        Map map = com_google_android_gms_internal_zzi.zzy;
        long j2 = 0;
        long j3 = 0;
        long j4 = 0;
        String str = (String) map.get("Date");
        if (str != null) {
            j2 = zzg(str);
        }
        str = (String) map.get("Cache-Control");
        if (str != null) {
            String[] split = str.split(",");
            obj = null;
            j = 0;
            j4 = 0;
            for (String trim : split) {
                String trim2 = trim2.trim();
                if (trim2.equals("no-cache") || trim2.equals("no-store")) {
                    return null;
                }
                if (trim2.startsWith("max-age=")) {
                    try {
                        j4 = Long.parseLong(trim2.substring(8));
                    } catch (Exception e) {
                    }
                } else if (trim2.startsWith("stale-while-revalidate=")) {
                    try {
                        j = Long.parseLong(trim2.substring(23));
                    } catch (Exception e2) {
                    }
                } else if (trim2.equals("must-revalidate") || trim2.equals("proxy-revalidate")) {
                    obj = 1;
                }
            }
            j3 = j4;
            j4 = j;
            obj2 = 1;
        } else {
            obj = null;
            obj2 = null;
        }
        str = (String) map.get("Expires");
        long zzg = str != null ? zzg(str) : 0;
        str = (String) map.get("Last-Modified");
        long zzg2 = str != null ? zzg(str) : 0;
        str = (String) map.get("ETag");
        if (obj2 != null) {
            j3 = currentTimeMillis + (1000 * j3);
            j = obj != null ? j3 : (1000 * j4) + j3;
        } else if (j2 <= 0 || zzg < j2) {
            j = 0;
            j3 = 0;
        } else {
            j = (zzg - j2) + currentTimeMillis;
            j3 = j;
        }
        zza com_google_android_gms_internal_zzb_zza = new zza();
        com_google_android_gms_internal_zzb_zza.data = com_google_android_gms_internal_zzi.data;
        com_google_android_gms_internal_zzb_zza.zza = str;
        com_google_android_gms_internal_zzb_zza.zze = j3;
        com_google_android_gms_internal_zzb_zza.zzd = j;
        com_google_android_gms_internal_zzb_zza.zzb = j2;
        com_google_android_gms_internal_zzb_zza.zzc = zzg2;
        com_google_android_gms_internal_zzb_zza.zzf = map;
        return com_google_android_gms_internal_zzb_zza;
    }

    public static long zzg(String str) {
        try {
            return DateUtils.parseDate(str).getTime();
        } catch (DateParseException e) {
            return 0;
        }
    }
}
