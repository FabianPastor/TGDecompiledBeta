package org.telegram.messenger.time;

import java.text.Format;
import java.util.Arrays;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

abstract class FormatCache<F extends Format> {
    static final int NONE = -1;
    private static final ConcurrentMap<MultipartKey, String> cDateTimeInstanceCache = new ConcurrentHashMap(7);
    private final ConcurrentMap<MultipartKey, F> cInstanceCache = new ConcurrentHashMap(7);

    private static class MultipartKey {
        private int hashCode;
        private final Object[] keys;

        public MultipartKey(Object... objArr) {
            this.keys = objArr;
        }

        public boolean equals(Object obj) {
            return Arrays.equals(this.keys, ((MultipartKey) obj).keys);
        }

        public int hashCode() {
            if (this.hashCode == 0) {
                Object[] objArr = this.keys;
                int i = 0;
                int length = objArr.length;
                int i2 = 0;
                while (i < length) {
                    Object obj = objArr[i];
                    if (obj != null) {
                        i2 = (i2 * 7) + obj.hashCode();
                    }
                    i++;
                }
                this.hashCode = i2;
            }
            return this.hashCode;
        }
    }

    protected abstract F createInstance(String str, TimeZone timeZone, Locale locale);

    FormatCache() {
    }

    public F getInstance() {
        return getDateTimeInstance(3, 3, TimeZone.getDefault(), Locale.getDefault());
    }

    public F getInstance(String str, TimeZone timeZone, Locale locale) {
        if (str == null) {
            throw new NullPointerException("pattern must not be null");
        }
        if (timeZone == null) {
            timeZone = TimeZone.getDefault();
        }
        if (locale == null) {
            locale = Locale.getDefault();
        }
        MultipartKey multipartKey = new MultipartKey(str, timeZone, locale);
        Format format = (Format) this.cInstanceCache.get(multipartKey);
        if (format != null) {
            return format;
        }
        F createInstance = createInstance(str, timeZone, locale);
        F f = (Format) this.cInstanceCache.putIfAbsent(multipartKey, createInstance);
        return f != null ? f : createInstance;
    }

    private F getDateTimeInstance(Integer num, Integer num2, TimeZone timeZone, Locale locale) {
        if (locale == null) {
            locale = Locale.getDefault();
        }
        return getInstance(getPatternForStyle(num, num2, locale), timeZone, locale);
    }

    F getDateTimeInstance(int i, int i2, TimeZone timeZone, Locale locale) {
        return getDateTimeInstance(Integer.valueOf(i), Integer.valueOf(i2), timeZone, locale);
    }

    F getDateInstance(int i, TimeZone timeZone, Locale locale) {
        return getDateTimeInstance(Integer.valueOf(i), null, timeZone, locale);
    }

    F getTimeInstance(int i, TimeZone timeZone, Locale locale) {
        return getDateTimeInstance(null, Integer.valueOf(i), timeZone, locale);
    }

    static java.lang.String getPatternForStyle(java.lang.Integer r3, java.lang.Integer r4, java.util.Locale r5) {
        /* JADX: method processing error */
/*
Error: java.lang.NullPointerException
	at jadx.core.dex.visitors.regions.ProcessTryCatchRegions.searchTryCatchDominators(ProcessTryCatchRegions.java:75)
	at jadx.core.dex.visitors.regions.ProcessTryCatchRegions.process(ProcessTryCatchRegions.java:45)
	at jadx.core.dex.visitors.regions.RegionMakerVisitor.postProcessRegions(RegionMakerVisitor.java:63)
	at jadx.core.dex.visitors.regions.RegionMakerVisitor.visit(RegionMakerVisitor.java:58)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:31)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:17)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:282)
	at jadx.api.JavaClass.decompile(JavaClass.java:62)
	at jadx.api.JadxDecompiler.lambda$appendSourcesSave$0(JadxDecompiler.java:200)
*/
        /*
        r0 = new org.telegram.messenger.time.FormatCache$MultipartKey;
        r1 = 3;
        r1 = new java.lang.Object[r1];
        r2 = 0;
        r1[r2] = r3;
        r2 = 1;
        r1[r2] = r4;
        r2 = 2;
        r1[r2] = r5;
        r0.<init>(r1);
        r1 = cDateTimeInstanceCache;
        r1 = r1.get(r0);
        r1 = (java.lang.String) r1;
        if (r1 != 0) goto L_0x0068;
    L_0x001b:
        if (r3 != 0) goto L_0x0026;
    L_0x001d:
        r3 = r4.intValue();	 Catch:{ ClassCastException -> 0x0051 }
        r3 = java.text.DateFormat.getTimeInstance(r3, r5);	 Catch:{ ClassCastException -> 0x0051 }
        goto L_0x003d;	 Catch:{ ClassCastException -> 0x0051 }
    L_0x0026:
        if (r4 != 0) goto L_0x0031;	 Catch:{ ClassCastException -> 0x0051 }
    L_0x0028:
        r3 = r3.intValue();	 Catch:{ ClassCastException -> 0x0051 }
        r3 = java.text.DateFormat.getDateInstance(r3, r5);	 Catch:{ ClassCastException -> 0x0051 }
        goto L_0x003d;	 Catch:{ ClassCastException -> 0x0051 }
    L_0x0031:
        r3 = r3.intValue();	 Catch:{ ClassCastException -> 0x0051 }
        r4 = r4.intValue();	 Catch:{ ClassCastException -> 0x0051 }
        r3 = java.text.DateFormat.getDateTimeInstance(r3, r4, r5);	 Catch:{ ClassCastException -> 0x0051 }
    L_0x003d:
        r3 = (java.text.SimpleDateFormat) r3;	 Catch:{ ClassCastException -> 0x0051 }
        r3 = r3.toPattern();	 Catch:{ ClassCastException -> 0x0051 }
        r4 = cDateTimeInstanceCache;	 Catch:{ ClassCastException -> 0x0051 }
        r4 = r4.putIfAbsent(r0, r3);	 Catch:{ ClassCastException -> 0x0051 }
        r4 = (java.lang.String) r4;	 Catch:{ ClassCastException -> 0x0051 }
        if (r4 == 0) goto L_0x004f;
    L_0x004d:
        r1 = r4;
        goto L_0x0068;
    L_0x004f:
        r1 = r3;
        goto L_0x0068;
    L_0x0051:
        r3 = new java.lang.IllegalArgumentException;
        r4 = new java.lang.StringBuilder;
        r4.<init>();
        r0 = "No date time pattern for locale: ";
        r4.append(r0);
        r4.append(r5);
        r4 = r4.toString();
        r3.<init>(r4);
        throw r3;
    L_0x0068:
        return r1;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.time.FormatCache.getPatternForStyle(java.lang.Integer, java.lang.Integer, java.util.Locale):java.lang.String");
    }
}
