package com.google.android.gms.internal;

import android.content.ContentResolver;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.regex.Pattern;

public class zzafz {
    public static final Uri CONTENT_URI = Uri.parse("content://com.google.android.gsf.gservices");
    public static final Uri aRT = Uri.parse("content://com.google.android.gsf.gservices/prefix");
    public static final Pattern aRU = Pattern.compile("^(1|true|t|on|yes|y)$", 2);
    public static final Pattern aRV = Pattern.compile("^(0|false|f|off|no|n)$", 2);
    static HashMap<String, String> aRW;
    private static Object aRX;
    static HashSet<String> aRY = new HashSet();

    class AnonymousClass1 extends Thread {
        final /* synthetic */ ContentResolver aRZ;

        AnonymousClass1(String str, ContentResolver contentResolver) {
            this.aRZ = contentResolver;
            super(str);
        }

        public void run() {
            Looper.prepare();
            this.aRZ.registerContentObserver(zzafz.CONTENT_URI, true, new ContentObserver(this, new Handler(Looper.myLooper())) {
                final /* synthetic */ AnonymousClass1 aSa;

                public void onChange(boolean z) {
                    synchronized (zzafz.class) {
                        zzafz.aRW.clear();
                        zzafz.aRX = new Object();
                        if (!zzafz.aRY.isEmpty()) {
                            zzafz.zzb(this.aSa.aRZ, (String[]) zzafz.aRY.toArray(new String[zzafz.aRY.size()]));
                        }
                    }
                }
            });
            Looper.loop();
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

    public static String getString(ContentResolver contentResolver, String str) {
        return zza(contentResolver, str, null);
    }

    public static String zza(ContentResolver contentResolver, String str, String str2) {
        synchronized (zzafz.class) {
            zza(contentResolver);
            Object obj = aRX;
            String str3;
            if (aRW.containsKey(str)) {
                str3 = (String) aRW.get(str);
                if (str3 != null) {
                    str2 = str3;
                }
            } else {
                Iterator it = aRY.iterator();
                while (it.hasNext()) {
                    if (str.startsWith((String) it.next())) {
                        break;
                    }
                }
                Cursor query = contentResolver.query(CONTENT_URI, null, null, new String[]{str}, null);
                if (query != null) {
                    try {
                        if (query.moveToFirst()) {
                            str3 = query.getString(1);
                            synchronized (zzafz.class) {
                                if (obj == aRX) {
                                    aRW.put(str, str3);
                                }
                            }
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
                aRW.put(str, null);
                if (query != null) {
                    query.close();
                }
            }
        }
        return str2;
    }

    public static Map<String, String> zza(ContentResolver contentResolver, String... strArr) {
        Cursor query = contentResolver.query(aRT, null, null, strArr, null);
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
        if (aRW == null) {
            aRW = new HashMap();
            aRX = new Object();
            new AnonymousClass1("Gservices", contentResolver).start();
        }
    }

    public static void zzb(ContentResolver contentResolver, String... strArr) {
        Map zza = zza(contentResolver, strArr);
        synchronized (zzafz.class) {
            zza(contentResolver);
            aRY.addAll(Arrays.asList(strArr));
            for (Entry entry : zza.entrySet()) {
                aRW.put((String) entry.getKey(), (String) entry.getValue());
            }
        }
    }
}
