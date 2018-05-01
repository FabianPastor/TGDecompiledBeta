package org.telegram.messenger.time;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.text.DateFormatSymbols;
import java.text.ParseException;
import java.text.ParsePosition;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.SortedMap;
import java.util.TimeZone;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FastDateParser implements Serializable, DateParser {
    private static final Strategy ABBREVIATED_YEAR_STRATEGY = new NumberStrategy(1) {
        void setCalendar(FastDateParser fastDateParser, Calendar calendar, String str) {
            str = Integer.parseInt(str);
            if (str < 100) {
                str = fastDateParser.adjustYear(str);
            }
            calendar.set(1, str);
        }
    };
    private static final Strategy DAY_OF_MONTH_STRATEGY = new NumberStrategy(5);
    private static final Strategy DAY_OF_WEEK_IN_MONTH_STRATEGY = new NumberStrategy(8);
    private static final Strategy DAY_OF_YEAR_STRATEGY = new NumberStrategy(6);
    private static final Strategy HOUR_OF_DAY_STRATEGY = new NumberStrategy(11);
    private static final Strategy HOUR_STRATEGY = new NumberStrategy(10);
    static final Locale JAPANESE_IMPERIAL = new Locale("ja", "JP", "JP");
    private static final Strategy LITERAL_YEAR_STRATEGY = new NumberStrategy(1);
    private static final Strategy MILLISECOND_STRATEGY = new NumberStrategy(14);
    private static final Strategy MINUTE_STRATEGY = new NumberStrategy(12);
    private static final Strategy MODULO_HOUR_OF_DAY_STRATEGY = new NumberStrategy(11) {
        int modify(int i) {
            return i % 24;
        }
    };
    private static final Strategy MODULO_HOUR_STRATEGY = new NumberStrategy(10) {
        int modify(int i) {
            return i % 12;
        }
    };
    private static final Strategy NUMBER_MONTH_STRATEGY = new NumberStrategy(2) {
        int modify(int i) {
            return i - 1;
        }
    };
    private static final Strategy SECOND_STRATEGY = new NumberStrategy(13);
    private static final Strategy WEEK_OF_MONTH_STRATEGY = new NumberStrategy(4);
    private static final Strategy WEEK_OF_YEAR_STRATEGY = new NumberStrategy(3);
    private static final ConcurrentMap<Locale, Strategy>[] caches = new ConcurrentMap[17];
    private static final Pattern formatPattern = Pattern.compile("D+|E+|F+|G+|H+|K+|M+|S+|W+|Z+|a+|d+|h+|k+|m+|s+|w+|y+|z+|''|'[^']++(''[^']*+)*+'|[^'A-Za-z]++");
    private static final long serialVersionUID = 2;
    private final int century;
    private transient String currentFormatField;
    private final Locale locale;
    private transient Strategy nextStrategy;
    private transient Pattern parsePattern;
    private final String pattern;
    private final int startYear;
    private transient Strategy[] strategies;
    private final TimeZone timeZone;

    private static abstract class Strategy {
        abstract boolean addRegex(FastDateParser fastDateParser, StringBuilder stringBuilder);

        boolean isNumber() {
            return false;
        }

        void setCalendar(FastDateParser fastDateParser, Calendar calendar, String str) {
        }

        private Strategy() {
        }
    }

    private static class CopyQuotedStrategy extends Strategy {
        private final String formatField;

        CopyQuotedStrategy(String str) {
            super();
            this.formatField = str;
        }

        boolean isNumber() {
            char charAt = this.formatField.charAt(0);
            if (charAt == '\'') {
                charAt = this.formatField.charAt(1);
            }
            return Character.isDigit(charAt);
        }

        boolean addRegex(FastDateParser fastDateParser, StringBuilder stringBuilder) {
            FastDateParser.escapeRegex(stringBuilder, this.formatField, true);
            return null;
        }
    }

    private static class NumberStrategy extends Strategy {
        private final int field;

        boolean isNumber() {
            return true;
        }

        int modify(int i) {
            return i;
        }

        NumberStrategy(int i) {
            super();
            this.field = i;
        }

        boolean addRegex(FastDateParser fastDateParser, StringBuilder stringBuilder) {
            if (fastDateParser.isNextNumber()) {
                stringBuilder.append("(\\p{Nd}{");
                stringBuilder.append(fastDateParser.getFieldWidth());
                stringBuilder.append("}+)");
            } else {
                stringBuilder.append("(\\p{Nd}++)");
            }
            return true;
        }

        void setCalendar(FastDateParser fastDateParser, Calendar calendar, String str) {
            calendar.set(this.field, modify(Integer.parseInt(str)));
        }
    }

    private static class TextStrategy extends Strategy {
        private final int field;
        private final Map<String, Integer> keyValues;

        TextStrategy(int i, Calendar calendar, Locale locale) {
            super();
            this.field = i;
            this.keyValues = FastDateParser.getDisplayNames(i, calendar, locale);
        }

        boolean addRegex(FastDateParser fastDateParser, StringBuilder stringBuilder) {
            stringBuilder.append('(');
            for (String access$100 : this.keyValues.keySet()) {
                FastDateParser.escapeRegex(stringBuilder, access$100, false).append('|');
            }
            stringBuilder.setCharAt(stringBuilder.length() - 1, ')');
            return true;
        }

        void setCalendar(FastDateParser fastDateParser, Calendar calendar, String str) {
            Integer num = (Integer) this.keyValues.get(str);
            if (num == null) {
                fastDateParser = new StringBuilder(str);
                fastDateParser.append(" not in (");
                for (String str2 : this.keyValues.keySet()) {
                    fastDateParser.append(str2);
                    fastDateParser.append(' ');
                }
                fastDateParser.setCharAt(fastDateParser.length() - 1, ')');
                throw new IllegalArgumentException(fastDateParser.toString());
            }
            calendar.set(this.field, num.intValue());
        }
    }

    private static class TimeZoneStrategy extends Strategy {
        private static final int ID = 0;
        private static final int LONG_DST = 3;
        private static final int LONG_STD = 1;
        private static final int SHORT_DST = 4;
        private static final int SHORT_STD = 2;
        private final SortedMap<String, TimeZone> tzNames = new TreeMap(String.CASE_INSENSITIVE_ORDER);
        private final String validTimeZoneChars;

        TimeZoneStrategy(Locale locale) {
            super();
            for (String[] strArr : DateFormatSymbols.getInstance(locale).getZoneStrings()) {
                if (!strArr[0].startsWith("GMT")) {
                    TimeZone timeZone = TimeZone.getTimeZone(strArr[0]);
                    if (!this.tzNames.containsKey(strArr[1])) {
                        this.tzNames.put(strArr[1], timeZone);
                    }
                    if (!this.tzNames.containsKey(strArr[2])) {
                        this.tzNames.put(strArr[2], timeZone);
                    }
                    if (timeZone.useDaylightTime()) {
                        if (!this.tzNames.containsKey(strArr[3])) {
                            this.tzNames.put(strArr[3], timeZone);
                        }
                        if (!this.tzNames.containsKey(strArr[4])) {
                            this.tzNames.put(strArr[4], timeZone);
                        }
                    }
                }
            }
            locale = new StringBuilder();
            locale.append("(GMT[+\\-]\\d{0,1}\\d{2}|[+\\-]\\d{2}:?\\d{2}|");
            for (String access$100 : this.tzNames.keySet()) {
                FastDateParser.escapeRegex(locale, access$100, false).append('|');
            }
            locale.setCharAt(locale.length() - 1, ')');
            this.validTimeZoneChars = locale.toString();
        }

        boolean addRegex(FastDateParser fastDateParser, StringBuilder stringBuilder) {
            stringBuilder.append(this.validTimeZoneChars);
            return true;
        }

        void setCalendar(FastDateParser fastDateParser, Calendar calendar, String str) {
            if (str.charAt(0) != '+') {
                if (str.charAt(0) != 45) {
                    if (str.startsWith("GMT") != null) {
                        fastDateParser = TimeZone.getTimeZone(str);
                    } else {
                        fastDateParser = (TimeZone) this.tzNames.get(str);
                        if (fastDateParser == null) {
                            calendar = new StringBuilder();
                            calendar.append(str);
                            calendar.append(" is not a supported timezone name");
                            throw new IllegalArgumentException(calendar.toString());
                        }
                    }
                    calendar.setTimeZone(fastDateParser);
                }
            }
            fastDateParser = new StringBuilder();
            fastDateParser.append("GMT");
            fastDateParser.append(str);
            fastDateParser = TimeZone.getTimeZone(fastDateParser.toString());
            calendar.setTimeZone(fastDateParser);
        }
    }

    protected FastDateParser(String str, TimeZone timeZone, Locale locale) {
        this(str, timeZone, locale, null);
    }

    protected FastDateParser(String str, TimeZone timeZone, Locale locale, Date date) {
        this.pattern = str;
        this.timeZone = timeZone;
        this.locale = locale;
        str = Calendar.getInstance(timeZone, locale);
        if (date != null) {
            str.setTime(date);
            timeZone = str.get(1);
        } else if (locale.equals(JAPANESE_IMPERIAL) != null) {
            timeZone = null;
        } else {
            str.setTime(new Date());
            timeZone = str.get(1) - 80;
        }
        this.century = (timeZone / 100) * 100;
        this.startYear = timeZone - this.century;
        init(str);
    }

    private void init(Calendar calendar) {
        StringBuilder stringBuilder = new StringBuilder();
        List arrayList = new ArrayList();
        Matcher matcher = formatPattern.matcher(this.pattern);
        if (matcher.lookingAt()) {
            this.currentFormatField = matcher.group();
            Strategy strategy = getStrategy(this.currentFormatField, calendar);
            while (true) {
                matcher.region(matcher.end(), matcher.regionEnd());
                if (!matcher.lookingAt()) {
                    break;
                }
                String group = matcher.group();
                this.nextStrategy = getStrategy(group, calendar);
                if (strategy.addRegex(this, stringBuilder)) {
                    arrayList.add(strategy);
                }
                this.currentFormatField = group;
                strategy = this.nextStrategy;
            }
            this.nextStrategy = null;
            if (matcher.regionStart() != matcher.regionEnd()) {
                stringBuilder = new StringBuilder();
                stringBuilder.append("Failed to parse \"");
                stringBuilder.append(this.pattern);
                stringBuilder.append("\" ; gave up at index ");
                stringBuilder.append(matcher.regionStart());
                throw new IllegalArgumentException(stringBuilder.toString());
            }
            if (strategy.addRegex(this, stringBuilder)) {
                arrayList.add(strategy);
            }
            this.currentFormatField = null;
            this.strategies = (Strategy[]) arrayList.toArray(new Strategy[arrayList.size()]);
            this.parsePattern = Pattern.compile(stringBuilder.toString());
            return;
        }
        stringBuilder = new StringBuilder();
        stringBuilder.append("Illegal pattern character '");
        stringBuilder.append(this.pattern.charAt(matcher.regionStart()));
        stringBuilder.append("'");
        throw new IllegalArgumentException(stringBuilder.toString());
    }

    public String getPattern() {
        return this.pattern;
    }

    public TimeZone getTimeZone() {
        return this.timeZone;
    }

    public Locale getLocale() {
        return this.locale;
    }

    Pattern getParsePattern() {
        return this.parsePattern;
    }

    public boolean equals(Object obj) {
        boolean z = false;
        if (!(obj instanceof FastDateParser)) {
            return false;
        }
        FastDateParser fastDateParser = (FastDateParser) obj;
        if (this.pattern.equals(fastDateParser.pattern) && this.timeZone.equals(fastDateParser.timeZone) && this.locale.equals(fastDateParser.locale) != null) {
            z = true;
        }
        return z;
    }

    public int hashCode() {
        return this.pattern.hashCode() + (13 * (this.timeZone.hashCode() + (this.locale.hashCode() * 13)));
    }

    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("FastDateParser[");
        stringBuilder.append(this.pattern);
        stringBuilder.append(",");
        stringBuilder.append(this.locale);
        stringBuilder.append(",");
        stringBuilder.append(this.timeZone.getID());
        stringBuilder.append("]");
        return stringBuilder.toString();
    }

    private void readObject(ObjectInputStream objectInputStream) throws IOException, ClassNotFoundException {
        objectInputStream.defaultReadObject();
        init(Calendar.getInstance(this.timeZone, this.locale));
    }

    public Object parseObject(String str) throws ParseException {
        return parse(str);
    }

    public Date parse(String str) throws ParseException {
        Date parse = parse(str, new ParsePosition(0));
        if (parse != null) {
            return parse;
        }
        if (this.locale.equals(JAPANESE_IMPERIAL)) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("(The ");
            stringBuilder.append(this.locale);
            stringBuilder.append(" locale does not support dates before 1868 AD)\nUnparseable date: \"");
            stringBuilder.append(str);
            stringBuilder.append("\" does not match ");
            stringBuilder.append(this.parsePattern.pattern());
            throw new ParseException(stringBuilder.toString(), 0);
        }
        stringBuilder = new StringBuilder();
        stringBuilder.append("Unparseable date: \"");
        stringBuilder.append(str);
        stringBuilder.append("\" does not match ");
        stringBuilder.append(this.parsePattern.pattern());
        throw new ParseException(stringBuilder.toString(), 0);
    }

    public Object parseObject(String str, ParsePosition parsePosition) {
        return parse(str, parsePosition);
    }

    public Date parse(String str, ParsePosition parsePosition) {
        int index = parsePosition.getIndex();
        str = this.parsePattern.matcher(str.substring(index));
        if (!str.lookingAt()) {
            return null;
        }
        Calendar instance = Calendar.getInstance(this.timeZone, this.locale);
        instance.clear();
        int i = 0;
        while (i < this.strategies.length) {
            int i2 = i + 1;
            this.strategies[i].setCalendar(this, instance, str.group(i2));
            i = i2;
        }
        parsePosition.setIndex(index + str.end());
        return instance.getTime();
    }

    private static StringBuilder escapeRegex(StringBuilder stringBuilder, String str, boolean z) {
        stringBuilder.append("\\Q");
        int i = 0;
        while (i < str.length()) {
            char charAt = str.charAt(i);
            if (charAt != '\'') {
                if (charAt == '\\') {
                    i++;
                    if (i != str.length()) {
                        stringBuilder.append(charAt);
                        charAt = str.charAt(i);
                        if (charAt == 'E') {
                            stringBuilder.append("E\\\\E\\");
                            charAt = 'Q';
                        }
                    }
                }
            } else if (z) {
                i++;
                if (i == str.length()) {
                    return stringBuilder;
                }
                charAt = str.charAt(i);
            } else {
                continue;
            }
            stringBuilder.append(charAt);
            i++;
        }
        stringBuilder.append("\\E");
        return stringBuilder;
    }

    private static String[] getDisplayNameArray(int i, boolean z, Locale locale) {
        DateFormatSymbols dateFormatSymbols = new DateFormatSymbols(locale);
        if (i == 0) {
            return dateFormatSymbols.getEras();
        }
        if (i == 2) {
            return z ? dateFormatSymbols.getMonths() : dateFormatSymbols.getShortMonths();
        } else if (i == 7) {
            return z ? dateFormatSymbols.getWeekdays() : dateFormatSymbols.getShortWeekdays();
        } else if (i != true) {
            return 0;
        } else {
            return dateFormatSymbols.getAmPmStrings();
        }
    }

    private static void insertValuesInMap(Map<String, Integer> map, String[] strArr) {
        if (strArr != null) {
            int i = 0;
            while (i < strArr.length) {
                if (strArr[i] != null && strArr[i].length() > 0) {
                    map.put(strArr[i], Integer.valueOf(i));
                }
                i++;
            }
        }
    }

    private static Map<String, Integer> getDisplayNames(int i, Locale locale) {
        Map<String, Integer> hashMap = new HashMap();
        insertValuesInMap(hashMap, getDisplayNameArray(i, false, locale));
        insertValuesInMap(hashMap, getDisplayNameArray(i, true, locale));
        return hashMap.isEmpty() != 0 ? null : hashMap;
    }

    private static Map<String, Integer> getDisplayNames(int i, Calendar calendar, Locale locale) {
        return getDisplayNames(i, locale);
    }

    private int adjustYear(int i) {
        int i2 = this.century + i;
        return i >= this.startYear ? i2 : i2 + 100;
    }

    boolean isNextNumber() {
        return this.nextStrategy != null && this.nextStrategy.isNumber();
    }

    int getFieldWidth() {
        return this.currentFormatField.length();
    }

    private Strategy getStrategy(String str, Calendar calendar) {
        switch (str.charAt(0)) {
            case '\'':
                if (str.length() > 2) {
                    return new CopyQuotedStrategy(str.substring(1, str.length() - 1));
                }
                break;
            case 'D':
                return DAY_OF_YEAR_STRATEGY;
            case 'E':
                return getLocaleSpecificStrategy(7, calendar);
            case 'F':
                return DAY_OF_WEEK_IN_MONTH_STRATEGY;
            case 'G':
                return getLocaleSpecificStrategy(0, calendar);
            case 'H':
                return MODULO_HOUR_OF_DAY_STRATEGY;
            case 'K':
                return HOUR_STRATEGY;
            case 'M':
                return str.length() >= 3 ? getLocaleSpecificStrategy(2, calendar) : NUMBER_MONTH_STRATEGY;
            case 'S':
                return MILLISECOND_STRATEGY;
            case 'W':
                return WEEK_OF_MONTH_STRATEGY;
            case 'Z':
            case 'z':
                return getLocaleSpecificStrategy(15, calendar);
            case 'a':
                return getLocaleSpecificStrategy(9, calendar);
            case 'd':
                return DAY_OF_MONTH_STRATEGY;
            case 'h':
                return MODULO_HOUR_STRATEGY;
            case 'k':
                return HOUR_OF_DAY_STRATEGY;
            case 'm':
                return MINUTE_STRATEGY;
            case 's':
                return SECOND_STRATEGY;
            case 'w':
                return WEEK_OF_YEAR_STRATEGY;
            case 'y':
                return str.length() > 2 ? LITERAL_YEAR_STRATEGY : ABBREVIATED_YEAR_STRATEGY;
            default:
                break;
        }
        return new CopyQuotedStrategy(str);
    }

    private static ConcurrentMap<Locale, Strategy> getCache(int i) {
        synchronized (caches) {
            if (caches[i] == null) {
                caches[i] = new ConcurrentHashMap(3);
            }
            i = caches[i];
        }
        return i;
    }

    private Strategy getLocaleSpecificStrategy(int i, Calendar calendar) {
        ConcurrentMap cache = getCache(i);
        Strategy strategy = (Strategy) cache.get(this.locale);
        if (strategy == null) {
            strategy = i == 15 ? new TimeZoneStrategy(this.locale) : new TextStrategy(i, calendar, this.locale);
            Strategy strategy2 = (Strategy) cache.putIfAbsent(this.locale, strategy);
            if (strategy2 != null) {
                return strategy2;
            }
        }
        return strategy;
    }
}
