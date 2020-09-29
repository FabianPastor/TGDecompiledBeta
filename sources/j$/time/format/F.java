package j$.time.format;

import j$.time.t.q;
import j$.time.t.t;
import j$.time.u.B;
import j$.time.u.j;
import j$.util.concurrent.ConcurrentHashMap;
import java.text.DateFormatSymbols;
import java.time.format.TextStyle;
import java.time.temporal.TemporalField;
import java.util.AbstractMap;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentMap;

class F {
    private static final ConcurrentMap a = new ConcurrentHashMap(16, 0.75f, 2);
    /* access modifiers changed from: private */
    public static final Comparator b = new D();

    F() {
    }

    static F g() {
        return new F();
    }

    public String i(B field, long value, K style, Locale locale) {
        Object store = e(field, locale);
        if (store instanceof E) {
            return ((E) store).a(value, style);
        }
        return null;
    }

    public String h(q chrono, B field, long value, K style, Locale locale) {
        if (chrono == t.a || !(field instanceof j)) {
            return i(field, value, style, locale);
        }
        return null;
    }

    public Iterator k(B field, K style, Locale locale) {
        Object store = e(field, locale);
        if (store instanceof E) {
            return ((E) store).b(style);
        }
        return null;
    }

    public Iterator j(q chrono, B field, K style, Locale locale) {
        if (chrono == t.a || !(field instanceof j)) {
            return k(field, style, locale);
        }
        return null;
    }

    private Object e(B field, Locale locale) {
        Map.Entry<TemporalField, Locale> key = c(field, locale);
        Object store = a.get(key);
        if (store != null) {
            return store;
        }
        a.putIfAbsent(key, d(field, locale));
        return a.get(key);
    }

    private Object d(B field, Locale locale) {
        B b2 = field;
        Map<TextStyle, Map<Long, String>> styleMap = new HashMap<>();
        if (b2 == j.ERA) {
            DateFormatSymbols symbols = DateFormatSymbols.getInstance(locale);
            Map<Long, String> fullMap = new HashMap<>();
            Map<Long, String> narrowMap = new HashMap<>();
            String[] eraSymbols = symbols.getEras();
            for (int i = 0; i < eraSymbols.length; i++) {
                if (!eraSymbols[i].isEmpty()) {
                    fullMap.put(Long.valueOf((long) i), eraSymbols[i]);
                    narrowMap.put(Long.valueOf((long) i), f(eraSymbols[i]));
                }
            }
            if (fullMap.isEmpty() == 0) {
                styleMap.put(K.FULL, fullMap);
                styleMap.put(K.SHORT, fullMap);
                styleMap.put(K.NARROW, narrowMap);
            }
            return new E(styleMap);
        } else if (b2 == j.MONTH_OF_YEAR) {
            DateFormatSymbols symbols2 = DateFormatSymbols.getInstance(locale);
            Map<Long, String> longMap = new HashMap<>();
            Map<Long, String> narrowMap2 = new HashMap<>();
            String[] longMonths = symbols2.getMonths();
            for (int i2 = 0; i2 < longMonths.length; i2++) {
                if (!longMonths[i2].isEmpty()) {
                    longMap.put(Long.valueOf(((long) i2) + 1), longMonths[i2]);
                    narrowMap2.put(Long.valueOf(((long) i2) + 1), f(longMonths[i2]));
                }
            }
            if (longMap.isEmpty() == 0) {
                styleMap.put(K.FULL, longMap);
                styleMap.put(K.NARROW, narrowMap2);
            }
            Map<Long, String> shortMap = new HashMap<>();
            String[] shortMonths = symbols2.getShortMonths();
            for (int i3 = 0; i3 < shortMonths.length; i3++) {
                if (!shortMonths[i3].isEmpty()) {
                    shortMap.put(Long.valueOf(((long) i3) + 1), shortMonths[i3]);
                }
            }
            if (!shortMap.isEmpty()) {
                styleMap.put(K.SHORT, shortMap);
            }
            return new E(styleMap);
        } else if (b2 == j.DAY_OF_WEEK) {
            DateFormatSymbols symbols3 = DateFormatSymbols.getInstance(locale);
            Map<Long, String> longMap2 = new HashMap<>();
            String[] longSymbols = symbols3.getWeekdays();
            longMap2.put(1L, longSymbols[2]);
            longMap2.put(2L, longSymbols[3]);
            longMap2.put(3L, longSymbols[4]);
            longMap2.put(4L, longSymbols[5]);
            longMap2.put(5L, longSymbols[6]);
            longMap2.put(6L, longSymbols[7]);
            longMap2.put(7L, longSymbols[1]);
            styleMap.put(K.FULL, longMap2);
            Map<Long, String> narrowMap3 = new HashMap<>();
            narrowMap3.put(1L, f(longSymbols[2]));
            narrowMap3.put(2L, f(longSymbols[3]));
            narrowMap3.put(3L, f(longSymbols[4]));
            narrowMap3.put(4L, f(longSymbols[5]));
            narrowMap3.put(5L, f(longSymbols[6]));
            narrowMap3.put(6L, f(longSymbols[7]));
            narrowMap3.put(7L, f(longSymbols[1]));
            styleMap.put(K.NARROW, narrowMap3);
            Map<Long, String> shortMap2 = new HashMap<>();
            String[] shortSymbols = symbols3.getShortWeekdays();
            shortMap2.put(1L, shortSymbols[2]);
            shortMap2.put(2L, shortSymbols[3]);
            shortMap2.put(3L, shortSymbols[4]);
            shortMap2.put(4L, shortSymbols[5]);
            shortMap2.put(5L, shortSymbols[6]);
            shortMap2.put(6L, shortSymbols[7]);
            shortMap2.put(7L, shortSymbols[1]);
            styleMap.put(K.SHORT, shortMap2);
            return new E(styleMap);
        } else if (b2 != j.AMPM_OF_DAY) {
            return "";
        } else {
            DateFormatSymbols symbols4 = DateFormatSymbols.getInstance(locale);
            Map<Long, String> fullMap2 = new HashMap<>();
            Map<Long, String> narrowMap4 = new HashMap<>();
            String[] amPmSymbols = symbols4.getAmPmStrings();
            for (int i4 = 0; i4 < amPmSymbols.length; i4++) {
                if (!amPmSymbols[i4].isEmpty()) {
                    fullMap2.put(Long.valueOf((long) i4), amPmSymbols[i4]);
                    narrowMap4.put(Long.valueOf((long) i4), f(amPmSymbols[i4]));
                }
            }
            if (fullMap2.isEmpty() == 0) {
                styleMap.put(K.FULL, fullMap2);
                styleMap.put(K.SHORT, fullMap2);
                styleMap.put(K.NARROW, narrowMap4);
            }
            return new E(styleMap);
        }
    }

    private static String f(String string) {
        return string.substring(0, Character.charCount(string.codePointAt(0)));
    }

    /* access modifiers changed from: private */
    public static Map.Entry c(Object text, Object field) {
        return new AbstractMap.SimpleImmutableEntry(text, field);
    }
}
