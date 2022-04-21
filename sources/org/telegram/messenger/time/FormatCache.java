package org.telegram.messenger.time;

import j$.util.concurrent.ConcurrentHashMap;
import java.text.DateFormat;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.ConcurrentMap;

abstract class FormatCache<F extends Format> {
    static final int NONE = -1;
    private static final ConcurrentMap<MultipartKey, String> cDateTimeInstanceCache = new ConcurrentHashMap(7);
    private final ConcurrentMap<MultipartKey, F> cInstanceCache = new ConcurrentHashMap(7);

    /* access modifiers changed from: protected */
    public abstract F createInstance(String str, TimeZone timeZone, Locale locale);

    FormatCache() {
    }

    public F getInstance() {
        return getDateTimeInstance(3, 3, TimeZone.getDefault(), Locale.getDefault());
    }

    public F getInstance(String pattern, TimeZone timeZone, Locale locale) {
        if (pattern != null) {
            if (timeZone == null) {
                timeZone = TimeZone.getDefault();
            }
            if (locale == null) {
                locale = Locale.getDefault();
            }
            MultipartKey key = new MultipartKey(pattern, timeZone, locale);
            F format = (Format) this.cInstanceCache.get(key);
            if (format != null) {
                return format;
            }
            F format2 = createInstance(pattern, timeZone, locale);
            F previousValue = (Format) this.cInstanceCache.putIfAbsent(key, format2);
            if (previousValue != null) {
                return previousValue;
            }
            return format2;
        }
        throw new NullPointerException("pattern must not be null");
    }

    private F getDateTimeInstance(Integer dateStyle, Integer timeStyle, TimeZone timeZone, Locale locale) {
        if (locale == null) {
            locale = Locale.getDefault();
        }
        return getInstance(getPatternForStyle(dateStyle, timeStyle, locale), timeZone, locale);
    }

    /* access modifiers changed from: package-private */
    public F getDateTimeInstance(int dateStyle, int timeStyle, TimeZone timeZone, Locale locale) {
        return getDateTimeInstance(Integer.valueOf(dateStyle), Integer.valueOf(timeStyle), timeZone, locale);
    }

    /* access modifiers changed from: package-private */
    public F getDateInstance(int dateStyle, TimeZone timeZone, Locale locale) {
        return getDateTimeInstance(Integer.valueOf(dateStyle), (Integer) null, timeZone, locale);
    }

    /* access modifiers changed from: package-private */
    public F getTimeInstance(int timeStyle, TimeZone timeZone, Locale locale) {
        return getDateTimeInstance((Integer) null, Integer.valueOf(timeStyle), timeZone, locale);
    }

    static String getPatternForStyle(Integer dateStyle, Integer timeStyle, Locale locale) {
        DateFormat formatter;
        MultipartKey key = new MultipartKey(dateStyle, timeStyle, locale);
        ConcurrentMap<MultipartKey, String> concurrentMap = cDateTimeInstanceCache;
        String pattern = (String) concurrentMap.get(key);
        if (pattern != null) {
            return pattern;
        }
        if (dateStyle == null) {
            try {
                formatter = DateFormat.getTimeInstance(timeStyle.intValue(), locale);
            } catch (ClassCastException e) {
                throw new IllegalArgumentException("No date time pattern for locale: " + locale);
            }
        } else if (timeStyle == null) {
            formatter = DateFormat.getDateInstance(dateStyle.intValue(), locale);
        } else {
            formatter = DateFormat.getDateTimeInstance(dateStyle.intValue(), timeStyle.intValue(), locale);
        }
        String pattern2 = ((SimpleDateFormat) formatter).toPattern();
        String previous = concurrentMap.putIfAbsent(key, pattern2);
        if (previous != null) {
            return previous;
        }
        return pattern2;
    }

    private static class MultipartKey {
        private int hashCode;
        private final Object[] keys;

        public MultipartKey(Object... keys2) {
            this.keys = keys2;
        }

        public boolean equals(Object obj) {
            return Arrays.equals(this.keys, ((MultipartKey) obj).keys);
        }

        public int hashCode() {
            if (this.hashCode == 0) {
                int rc = 0;
                for (Object key : this.keys) {
                    if (key != null) {
                        rc = (rc * 7) + key.hashCode();
                    }
                }
                this.hashCode = rc;
            }
            return this.hashCode;
        }
    }
}
