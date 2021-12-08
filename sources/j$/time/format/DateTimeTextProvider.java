package j$.time.format;

import j$.time.chrono.Chronology;
import j$.time.chrono.IsoChronology;
import j$.time.temporal.ChronoField;
import j$.time.temporal.TemporalField;
import j$.util.concurrent.ConcurrentHashMap;
import java.text.DateFormatSymbols;
import java.time.format.TextStyle;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentMap;

class DateTimeTextProvider {
    private static final ConcurrentMap<Map.Entry<TemporalField, Locale>, Object> CACHE = new ConcurrentHashMap(16, 0.75f, 2);
    /* access modifiers changed from: private */
    public static final Comparator<Map.Entry<String, Long>> COMPARATOR = new Comparator<Map.Entry<String, Long>>() {
        public int compare(Map.Entry<String, Long> obj1, Map.Entry<String, Long> obj2) {
            return obj2.getKey().length() - obj1.getKey().length();
        }
    };

    DateTimeTextProvider() {
    }

    static DateTimeTextProvider getInstance() {
        return new DateTimeTextProvider();
    }

    public String getText(TemporalField field, long value, TextStyle style, Locale locale) {
        Object store = findStore(field, locale);
        if (store instanceof LocaleStore) {
            return ((LocaleStore) store).getText(value, style);
        }
        return null;
    }

    public String getText(Chronology chrono, TemporalField field, long value, TextStyle style, Locale locale) {
        if (chrono == IsoChronology.INSTANCE || !(field instanceof ChronoField)) {
            return getText(field, value, style, locale);
        }
        return null;
    }

    public Iterator<Map.Entry<String, Long>> getTextIterator(TemporalField field, TextStyle style, Locale locale) {
        Object store = findStore(field, locale);
        if (store instanceof LocaleStore) {
            return ((LocaleStore) store).getTextIterator(style);
        }
        return null;
    }

    public Iterator<Map.Entry<String, Long>> getTextIterator(Chronology chrono, TemporalField field, TextStyle style, Locale locale) {
        if (chrono == IsoChronology.INSTANCE || !(field instanceof ChronoField)) {
            return getTextIterator(field, style, locale);
        }
        return null;
    }

    private Object findStore(TemporalField field, Locale locale) {
        Map.Entry<java.time.temporal.TemporalField, Locale> key = createEntry(field, locale);
        ConcurrentMap<Map.Entry<TemporalField, Locale>, Object> concurrentMap = CACHE;
        Object store = concurrentMap.get(key);
        if (store != null) {
            return store;
        }
        concurrentMap.putIfAbsent(key, createStore(field, locale));
        return concurrentMap.get(key);
    }

    private static int toWeekDay(int calWeekDay) {
        if (calWeekDay == 1) {
            return 7;
        }
        return calWeekDay - 1;
    }

    private Object createStore(TemporalField field, Locale locale) {
        TemporalField temporalField = field;
        Map<TextStyle, Map<Long, String>> styleMap = new HashMap<>();
        if (temporalField == ChronoField.ERA) {
            DateFormatSymbols symbols = DateFormatSymbols.getInstance(locale);
            Map<Long, String> fullMap = new HashMap<>();
            Map<Long, String> narrowMap = new HashMap<>();
            String[] eraSymbols = symbols.getEras();
            for (int i = 0; i < eraSymbols.length; i++) {
                if (!eraSymbols[i].isEmpty()) {
                    fullMap.put(Long.valueOf((long) i), eraSymbols[i]);
                    narrowMap.put(Long.valueOf((long) i), firstCodePoint(eraSymbols[i]));
                }
            }
            if (fullMap.isEmpty() == 0) {
                styleMap.put(TextStyle.FULL, fullMap);
                styleMap.put(TextStyle.SHORT, fullMap);
                styleMap.put(TextStyle.NARROW, narrowMap);
            }
            return new LocaleStore(styleMap);
        } else if (temporalField == ChronoField.MONTH_OF_YEAR) {
            DateFormatSymbols symbols2 = DateFormatSymbols.getInstance(locale);
            Map<Long, String> longMap = new HashMap<>();
            Map<Long, String> narrowMap2 = new HashMap<>();
            String[] longMonths = symbols2.getMonths();
            for (int i2 = 0; i2 < longMonths.length; i2++) {
                if (!longMonths[i2].isEmpty()) {
                    longMap.put(Long.valueOf(((long) i2) + 1), longMonths[i2]);
                    narrowMap2.put(Long.valueOf(((long) i2) + 1), firstCodePoint(longMonths[i2]));
                }
            }
            if (longMap.isEmpty() == 0) {
                styleMap.put(TextStyle.FULL, longMap);
                styleMap.put(TextStyle.NARROW, narrowMap2);
            }
            Map<Long, String> shortMap = new HashMap<>();
            String[] shortMonths = symbols2.getShortMonths();
            for (int i3 = 0; i3 < shortMonths.length; i3++) {
                if (!shortMonths[i3].isEmpty()) {
                    shortMap.put(Long.valueOf(((long) i3) + 1), shortMonths[i3]);
                }
            }
            if (!shortMap.isEmpty()) {
                styleMap.put(TextStyle.SHORT, shortMap);
            }
            return new LocaleStore(styleMap);
        } else if (temporalField == ChronoField.DAY_OF_WEEK) {
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
            styleMap.put(TextStyle.FULL, longMap2);
            Map<Long, String> narrowMap3 = new HashMap<>();
            narrowMap3.put(1L, firstCodePoint(longSymbols[2]));
            narrowMap3.put(2L, firstCodePoint(longSymbols[3]));
            narrowMap3.put(3L, firstCodePoint(longSymbols[4]));
            narrowMap3.put(4L, firstCodePoint(longSymbols[5]));
            narrowMap3.put(5L, firstCodePoint(longSymbols[6]));
            narrowMap3.put(6L, firstCodePoint(longSymbols[7]));
            narrowMap3.put(7L, firstCodePoint(longSymbols[1]));
            styleMap.put(TextStyle.NARROW, narrowMap3);
            Map<Long, String> shortMap2 = new HashMap<>();
            String[] shortSymbols = symbols3.getShortWeekdays();
            shortMap2.put(1L, shortSymbols[2]);
            shortMap2.put(2L, shortSymbols[3]);
            shortMap2.put(3L, shortSymbols[4]);
            shortMap2.put(4L, shortSymbols[5]);
            shortMap2.put(5L, shortSymbols[6]);
            shortMap2.put(6L, shortSymbols[7]);
            shortMap2.put(7L, shortSymbols[1]);
            styleMap.put(TextStyle.SHORT, shortMap2);
            return new LocaleStore(styleMap);
        } else if (temporalField != ChronoField.AMPM_OF_DAY) {
            return "";
        } else {
            DateFormatSymbols symbols4 = DateFormatSymbols.getInstance(locale);
            Map<Long, String> fullMap2 = new HashMap<>();
            Map<Long, String> narrowMap4 = new HashMap<>();
            String[] amPmSymbols = symbols4.getAmPmStrings();
            for (int i4 = 0; i4 < amPmSymbols.length; i4++) {
                if (!amPmSymbols[i4].isEmpty()) {
                    fullMap2.put(Long.valueOf((long) i4), amPmSymbols[i4]);
                    narrowMap4.put(Long.valueOf((long) i4), firstCodePoint(amPmSymbols[i4]));
                }
            }
            if (fullMap2.isEmpty() == 0) {
                styleMap.put(TextStyle.FULL, fullMap2);
                styleMap.put(TextStyle.SHORT, fullMap2);
                styleMap.put(TextStyle.NARROW, narrowMap4);
            }
            return new LocaleStore(styleMap);
        }
    }

    private static String firstCodePoint(String string) {
        return string.substring(0, Character.charCount(string.codePointAt(0)));
    }

    /* access modifiers changed from: private */
    public static <A, B> Map.Entry<A, B> createEntry(A text, B field) {
        return new AbstractMap.SimpleImmutableEntry(text, field);
    }

    static final class LocaleStore {
        private final Map<TextStyle, List<Map.Entry<String, Long>>> parsable;
        private final Map<TextStyle, Map<Long, String>> valueTextMap;

        LocaleStore(Map<TextStyle, Map<Long, String>> valueTextMap2) {
            this.valueTextMap = valueTextMap2;
            Map<TextStyle, List<Map.Entry<String, Long>>> map = new HashMap<>();
            List<Map.Entry<String, Long>> allList = new ArrayList<>();
            for (Map.Entry<TextStyle, Map<Long, String>> vtmEntry : valueTextMap2.entrySet()) {
                Map<String, Map.Entry<String, Long>> reverse = new HashMap<>();
                for (Map.Entry<Long, String> entry : vtmEntry.getValue().entrySet()) {
                    reverse.put(entry.getValue(), DateTimeTextProvider.createEntry(entry.getValue(), entry.getKey()));
                }
                List<Map.Entry<String, Long>> list = new ArrayList<>(reverse.values());
                Collections.sort(list, DateTimeTextProvider.COMPARATOR);
                map.put((TextStyle) vtmEntry.getKey(), list);
                allList.addAll(list);
                map.put((Object) null, allList);
            }
            Collections.sort(allList, DateTimeTextProvider.COMPARATOR);
            this.parsable = map;
        }

        /* access modifiers changed from: package-private */
        public String getText(long value, TextStyle style) {
            Map<Long, String> map = this.valueTextMap.get(style);
            if (map != null) {
                return map.get(Long.valueOf(value));
            }
            return null;
        }

        /* access modifiers changed from: package-private */
        public Iterator<Map.Entry<String, Long>> getTextIterator(TextStyle style) {
            List<Map.Entry<String, Long>> list = this.parsable.get(style);
            if (list != null) {
                return list.iterator();
            }
            return null;
        }
    }
}
