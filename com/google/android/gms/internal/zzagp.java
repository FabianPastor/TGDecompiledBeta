package com.google.android.gms.internal;

import android.content.ContentResolver;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.regex.Pattern;

public class zzagp {
    public static final Uri CONTENT_URI = Uri.parse("content://com.google.android.gsf.gservices");
    public static final Uri aVg = Uri.parse("content://com.google.android.gsf.gservices/prefix");
    public static final Pattern aVh = Pattern.compile("^(1|true|t|on|yes|y)$", 2);
    public static final Pattern aVi = Pattern.compile("^(0|false|f|off|no|n)$", 2);
    private static final AtomicBoolean aVj = new AtomicBoolean();
    static HashMap<String, String> aVk;
    private static Object aVl;
    private static boolean aVm;
    static String[] aVn = new String[0];

    class AnonymousClass1 extends ContentObserver {
        AnonymousClass1(Handler handler) {
            super(handler);
        }

        public void onChange(boolean z) {
            zzagp.aVj.set(true);
        }
    }

    public static long getLong(ContentResolver contentResolver, String str, long j) {
        String string = getString(contentResolver, str);
        if (string != null) {
            try {
                j = Long.parseLong(string);
            } catch (NumberFormatException e) {
            }
        }
        return j;
    }

    @Deprecated
    public static String getString(ContentResolver contentResolver, String str) {
        return zza(contentResolver, str, null);
    }

    public static String zza(ContentResolver contentResolver, String str, String str2) {
        synchronized (zzagp.class) {
            zza(contentResolver);
            Object obj = aVl;
            String str3;
            if (aVk.containsKey(str)) {
                str3 = (String) aVk.get(str);
                if (str3 != null) {
                    str2 = str3;
                }
            } else {
                String[] strArr = aVn;
                int length = strArr.length;
                int i = 0;
                while (i < length) {
                    if (str.startsWith(strArr[i])) {
                        if (!aVm || aVk.isEmpty()) {
                            zzc(contentResolver, aVn);
                            if (aVk.containsKey(str)) {
                                str3 = (String) aVk.get(str);
                                if (str3 != null) {
                                    str2 = str3;
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
                            str3 = query.getString(1);
                            if (str3 != null && str3.equals(str2)) {
                                str3 = str2;
                            }
                            zza(obj, str, str3);
                            if (str3 != null) {
                                str2 = str3;
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
        return str2;
    }

    public static Map<String, String> zza(ContentResolver contentResolver, String... strArr) {
        Cursor query = contentResolver.query(aVg, null, null, strArr, null);
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
        if (aVk == null) {
            aVj.set(false);
            aVk = new HashMap();
            aVl = new Object();
            aVm = false;
            contentResolver.registerContentObserver(CONTENT_URI, true, new AnonymousClass1(new Handler(Looper.getMainLooper())));
        } else if (aVj.getAndSet(false)) {
            aVk.clear();
            aVl = new Object();
            aVm = false;
        }
    }

    private static void zza(Object obj, String str, String str2) {
        synchronized (zzagp.class) {
            if (obj == aVl) {
                aVk.put(str, str2);
            }
        }
    }

    public static void zzb(ContentResolver contentResolver, String... strArr) {
        if (strArr.length != 0) {
            synchronized (zzagp.class) {
                zza(contentResolver);
                String[] zzk = zzk(strArr);
                if (!aVm || aVk.isEmpty()) {
                    zzc(contentResolver, aVn);
                } else if (zzk.length != 0) {
                    zzc(contentResolver, zzk);
                }
            }
        }
    }

    private static void zzc(ContentResolver contentResolver, String[] strArr) {
        aVk.putAll(zza(contentResolver, strArr));
        aVm = true;
    }

    private static String[] zzk(String[] strArr) {
        HashSet hashSet = new HashSet((((aVn.length + strArr.length) * 4) / 3) + 1);
        hashSet.addAll(Arrays.asList(aVn));
        ArrayList arrayList = new ArrayList();
        for (Object obj : strArr) {
            if (hashSet.add(obj)) {
                arrayList.add(obj);
            }
        }
        if (arrayList.isEmpty()) {
            return new String[0];
        }
        aVn = (String[]) hashSet.toArray(new String[hashSet.size()]);
        return (String[]) arrayList.toArray(new String[arrayList.size()]);
    }
}
