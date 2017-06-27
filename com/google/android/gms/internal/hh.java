package com.google.android.gms.internal;

import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.regex.Pattern;

public class hh {
    private static Uri CONTENT_URI = Uri.parse("content://com.google.android.gsf.gservices");
    private static Uri zzbTY = Uri.parse("content://com.google.android.gsf.gservices/prefix");
    private static Pattern zzbTZ = Pattern.compile("^(1|true|t|on|yes|y)$", 2);
    private static Pattern zzbUa = Pattern.compile("^(0|false|f|off|no|n)$", 2);
    private static final AtomicBoolean zzbUb = new AtomicBoolean();
    private static HashMap<String, String> zzbUc;
    private static HashMap<String, Boolean> zzbUd = new HashMap();
    private static HashMap<String, Integer> zzbUe = new HashMap();
    private static HashMap<String, Long> zzbUf = new HashMap();
    private static HashMap<String, Float> zzbUg = new HashMap();
    private static Object zzbUh;
    private static boolean zzbUi;
    private static String[] zzbUj = new String[0];

    public static long getLong(ContentResolver contentResolver, String str, long j) {
        Object zzb = zzb(contentResolver);
        Long l = (Long) zza(zzbUf, str, Long.valueOf(0));
        if (l != null) {
            return l.longValue();
        }
        long j2;
        String zza = zza(contentResolver, str, null);
        if (zza == null) {
            Object obj = l;
            j2 = 0;
        } else {
            Long valueOf;
            try {
                long parseLong = Long.parseLong(zza);
                valueOf = Long.valueOf(parseLong);
                j2 = parseLong;
            } catch (NumberFormatException e) {
                valueOf = l;
                j2 = 0;
            }
        }
        HashMap hashMap = zzbUf;
        synchronized (hh.class) {
            if (zzb == zzbUh) {
                hashMap.put(str, obj);
                zzbUc.remove(str);
            }
        }
        return j2;
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private static <T> T zza(HashMap<String, T> hashMap, String str, T t) {
        synchronized (hh.class) {
            if (hashMap.containsKey(str)) {
                T t2 = hashMap.get(str);
                if (t2 == null) {
                    t2 = t;
                }
            } else {
                return null;
            }
        }
    }

    public static String zza(ContentResolver contentResolver, String str, String str2) {
        String str3 = null;
        synchronized (hh.class) {
            zza(contentResolver);
            Object obj = zzbUh;
            String str4;
            if (zzbUc.containsKey(str)) {
                str4 = (String) zzbUc.get(str);
                if (str4 != null) {
                    str3 = str4;
                }
            } else {
                String[] strArr = zzbUj;
                int length = strArr.length;
                int i = 0;
                while (i < length) {
                    if (str.startsWith(strArr[i])) {
                        if (!zzbUi || zzbUc.isEmpty()) {
                            zzc(contentResolver, zzbUj);
                            if (zzbUc.containsKey(str)) {
                                str4 = (String) zzbUc.get(str);
                                if (str4 != null) {
                                    str3 = str4;
                                }
                            }
                        }
                    } else {
                        i++;
                    }
                }
                Cursor query = contentResolver.query(CONTENT_URI, null, null, new String[]{str}, null);
                if (query != null) {
                    try {
                        if (query.moveToFirst()) {
                            str4 = query.getString(1);
                            if (str4 != null && str4.equals(null)) {
                                str4 = null;
                            }
                            zza(obj, str, str4);
                            if (str4 != null) {
                                str3 = str4;
                            }
                            if (query != null) {
                                query.close();
                            }
                        }
                    } catch (Throwable th) {
                        if (query != null) {
                            query.close();
                        }
                    }
                }
                zza(obj, str, null);
                if (query != null) {
                    query.close();
                }
            }
        }
        return str3;
    }

    private static Map<String, String> zza(ContentResolver contentResolver, String... strArr) {
        Cursor query = contentResolver.query(zzbTY, null, null, strArr, null);
        Map<String, String> treeMap = new TreeMap();
        if (query != null) {
            while (query.moveToNext()) {
                try {
                    treeMap.put(query.getString(0), query.getString(1));
                } finally {
                    query.close();
                }
            }
        }
        return treeMap;
    }

    private static void zza(ContentResolver contentResolver) {
        if (zzbUc == null) {
            zzbUb.set(false);
            zzbUc = new HashMap();
            zzbUh = new Object();
            zzbUi = false;
            contentResolver.registerContentObserver(CONTENT_URI, true, new hi(null));
        } else if (zzbUb.getAndSet(false)) {
            zzbUc.clear();
            zzbUd.clear();
            zzbUe.clear();
            zzbUf.clear();
            zzbUg.clear();
            zzbUh = new Object();
            zzbUi = false;
        }
    }

    private static void zza(Object obj, String str, String str2) {
        synchronized (hh.class) {
            if (obj == zzbUh) {
                zzbUc.put(str, str2);
            }
        }
    }

    private static Object zzb(ContentResolver contentResolver) {
        Object obj;
        synchronized (hh.class) {
            zza(contentResolver);
            obj = zzbUh;
        }
        return obj;
    }

    public static void zzb(ContentResolver contentResolver, String... strArr) {
        if (strArr.length != 0) {
            synchronized (hh.class) {
                String[] strArr2;
                zza(contentResolver);
                HashSet hashSet = new HashSet((((zzbUj.length + strArr.length) << 2) / 3) + 1);
                hashSet.addAll(Arrays.asList(zzbUj));
                ArrayList arrayList = new ArrayList();
                for (Object obj : strArr) {
                    if (hashSet.add(obj)) {
                        arrayList.add(obj);
                    }
                }
                if (arrayList.isEmpty()) {
                    strArr2 = new String[0];
                } else {
                    zzbUj = (String[]) hashSet.toArray(new String[hashSet.size()]);
                    strArr2 = (String[]) arrayList.toArray(new String[arrayList.size()]);
                }
                if (!zzbUi || zzbUc.isEmpty()) {
                    zzc(contentResolver, zzbUj);
                } else if (strArr2.length != 0) {
                    zzc(contentResolver, strArr2);
                }
            }
        }
    }

    private static void zzc(ContentResolver contentResolver, String[] strArr) {
        zzbUc.putAll(zza(contentResolver, strArr));
        zzbUi = true;
    }
}
