package org.telegram.messenger.time;

import j$.util.DesugarTimeZone;
import j$.util.concurrent.ConcurrentHashMap;
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
import java.util.concurrent.ConcurrentMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FastDateParser implements DateParser, Serializable {
    private static final Strategy ABBREVIATED_YEAR_STRATEGY = new NumberStrategy(1) {
        /* access modifiers changed from: package-private */
        public void setCalendar(FastDateParser parser, Calendar cal, String value) {
            int iValue = Integer.parseInt(value);
            if (iValue < 100) {
                iValue = parser.adjustYear(iValue);
            }
            cal.set(1, iValue);
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
        /* access modifiers changed from: package-private */
        public int modify(int iValue) {
            return iValue % 24;
        }
    };
    private static final Strategy MODULO_HOUR_STRATEGY = new NumberStrategy(10) {
        /* access modifiers changed from: package-private */
        public int modify(int iValue) {
            return iValue % 12;
        }
    };
    private static final Strategy NUMBER_MONTH_STRATEGY = new NumberStrategy(2) {
        /* access modifiers changed from: package-private */
        public int modify(int iValue) {
            return iValue - 1;
        }
    };
    private static final Strategy SECOND_STRATEGY = new NumberStrategy(13);
    private static final Strategy WEEK_OF_MONTH_STRATEGY = new NumberStrategy(4);
    private static final Strategy WEEK_OF_YEAR_STRATEGY = new NumberStrategy(3);
    private static final ConcurrentMap<Locale, Strategy>[] caches = new ConcurrentMap[17];
    private static final Pattern formatPattern = Pattern.compile("D+|E+|F+|G+|H+|K+|M+|L+|S+|W+|Z+|a+|d+|h+|k+|m+|s+|w+|y+|z+|''|'[^']++(''[^']*+)*+'|[^'A-Za-z]++");
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

    protected FastDateParser(String pattern2, TimeZone timeZone2, Locale locale2) {
        this(pattern2, timeZone2, locale2, (Date) null);
    }

    protected FastDateParser(String pattern2, TimeZone timeZone2, Locale locale2, Date centuryStart) {
        int centuryStartYear;
        this.pattern = pattern2;
        this.timeZone = timeZone2;
        this.locale = locale2;
        Calendar definingCalendar = Calendar.getInstance(timeZone2, locale2);
        if (centuryStart != null) {
            definingCalendar.setTime(centuryStart);
            centuryStartYear = definingCalendar.get(1);
        } else if (locale2.equals(JAPANESE_IMPERIAL)) {
            centuryStartYear = 0;
        } else {
            definingCalendar.setTime(new Date());
            centuryStartYear = definingCalendar.get(1) - 80;
        }
        int i = (centuryStartYear / 100) * 100;
        this.century = i;
        this.startYear = centuryStartYear - i;
        init(definingCalendar);
    }

    private void init(Calendar definingCalendar) {
        StringBuilder regex = new StringBuilder();
        List<Strategy> collector = new ArrayList<>();
        Matcher patternMatcher = formatPattern.matcher(this.pattern);
        if (patternMatcher.lookingAt()) {
            String group = patternMatcher.group();
            this.currentFormatField = group;
            Strategy currentStrategy = getStrategy(group, definingCalendar);
            while (true) {
                patternMatcher.region(patternMatcher.end(), patternMatcher.regionEnd());
                if (!patternMatcher.lookingAt()) {
                    break;
                }
                String nextFormatField = patternMatcher.group();
                this.nextStrategy = getStrategy(nextFormatField, definingCalendar);
                if (currentStrategy.addRegex(this, regex)) {
                    collector.add(currentStrategy);
                }
                this.currentFormatField = nextFormatField;
                currentStrategy = this.nextStrategy;
            }
            this.nextStrategy = null;
            if (patternMatcher.regionStart() == patternMatcher.regionEnd()) {
                if (currentStrategy.addRegex(this, regex)) {
                    collector.add(currentStrategy);
                }
                this.currentFormatField = null;
                this.strategies = (Strategy[]) collector.toArray(new Strategy[collector.size()]);
                this.parsePattern = Pattern.compile(regex.toString());
                return;
            }
            throw new IllegalArgumentException("Failed to parse \"" + this.pattern + "\" ; gave up at index " + patternMatcher.regionStart());
        }
        throw new IllegalArgumentException("Illegal pattern character '" + this.pattern.charAt(patternMatcher.regionStart()) + "'");
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

    /* access modifiers changed from: package-private */
    public Pattern getParsePattern() {
        return this.parsePattern;
    }

    public boolean equals(Object obj) {
        if (!(obj instanceof FastDateParser)) {
            return false;
        }
        FastDateParser other = (FastDateParser) obj;
        if (!this.pattern.equals(other.pattern) || !this.timeZone.equals(other.timeZone) || !this.locale.equals(other.locale)) {
            return false;
        }
        return true;
    }

    public int hashCode() {
        return this.pattern.hashCode() + ((this.timeZone.hashCode() + (this.locale.hashCode() * 13)) * 13);
    }

    public String toString() {
        return "FastDateParser[" + this.pattern + "," + this.locale + "," + this.timeZone.getID() + "]";
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        init(Calendar.getInstance(this.timeZone, this.locale));
    }

    public Object parseObject(String source) throws ParseException {
        return parse(source);
    }

    public Date parse(String source) throws ParseException {
        Date date = parse(source, new ParsePosition(0));
        if (date != null) {
            return date;
        }
        if (this.locale.equals(JAPANESE_IMPERIAL)) {
            throw new ParseException("(The " + this.locale + " locale does not support dates before 1868 AD)\nUnparseable date: \"" + source + "\" does not match " + this.parsePattern.pattern(), 0);
        }
        throw new ParseException("Unparseable date: \"" + source + "\" does not match " + this.parsePattern.pattern(), 0);
    }

    public Object parseObject(String source, ParsePosition pos) {
        return parse(source, pos);
    }

    public Date parse(String source, ParsePosition pos) {
        int offset = pos.getIndex();
        Matcher matcher = this.parsePattern.matcher(source.substring(offset));
        if (!matcher.lookingAt()) {
            return null;
        }
        Calendar cal = Calendar.getInstance(this.timeZone, this.locale);
        cal.clear();
        int i = 0;
        while (true) {
            Strategy[] strategyArr = this.strategies;
            if (i < strategyArr.length) {
                int i2 = i + 1;
                strategyArr[i].setCalendar(this, cal, matcher.group(i2));
                i = i2;
            } else {
                pos.setIndex(matcher.end() + offset);
                return cal.getTime();
            }
        }
    }

    /* access modifiers changed from: private */
    public static StringBuilder escapeRegex(StringBuilder regex, String value, boolean unquote) {
        regex.append("\\Q");
        int i = 0;
        while (i < value.length()) {
            char c = value.charAt(i);
            switch (c) {
                case '\'':
                    if (unquote) {
                        i++;
                        if (i != value.length()) {
                            c = value.charAt(i);
                            break;
                        } else {
                            return regex;
                        }
                    } else {
                        continue;
                    }
                case '\\':
                    i++;
                    if (i != value.length()) {
                        regex.append(c);
                        c = value.charAt(i);
                        if (c != 'E') {
                            break;
                        } else {
                            regex.append("E\\\\E\\");
                            c = 'Q';
                            break;
                        }
                    } else {
                        break;
                    }
            }
            regex.append(c);
            i++;
        }
        regex.append("\\E");
        return regex;
    }

    private static String[] getDisplayNameArray(int field, boolean isLong, Locale locale2) {
        DateFormatSymbols dfs = new DateFormatSymbols(locale2);
        switch (field) {
            case 0:
                return dfs.getEras();
            case 2:
                return isLong ? dfs.getMonths() : dfs.getShortMonths();
            case 7:
                return isLong ? dfs.getWeekdays() : dfs.getShortWeekdays();
            case 9:
                return dfs.getAmPmStrings();
            default:
                return null;
        }
    }

    private static void insertValuesInMap(Map<String, Integer> map, String[] values) {
        if (values != null) {
            for (int i = 0; i < values.length; i++) {
                if (values[i] != null && values[i].length() > 0) {
                    map.put(values[i], Integer.valueOf(i));
                }
            }
        }
    }

    private static Map<String, Integer> getDisplayNames(int field, Locale locale2) {
        Map<String, Integer> result = new HashMap<>();
        insertValuesInMap(result, getDisplayNameArray(field, false, locale2));
        insertValuesInMap(result, getDisplayNameArray(field, true, locale2));
        if (result.isEmpty()) {
            return null;
        }
        return result;
    }

    /* access modifiers changed from: private */
    public static Map<String, Integer> getDisplayNames(int field, Calendar definingCalendar, Locale locale2) {
        return getDisplayNames(field, locale2);
    }

    /* access modifiers changed from: private */
    public int adjustYear(int twoDigitYear) {
        int trial = this.century + twoDigitYear;
        return twoDigitYear >= this.startYear ? trial : trial + 100;
    }

    /* access modifiers changed from: package-private */
    public boolean isNextNumber() {
        Strategy strategy = this.nextStrategy;
        return strategy != null && strategy.isNumber();
    }

    /* access modifiers changed from: package-private */
    public int getFieldWidth() {
        return this.currentFormatField.length();
    }

    private static abstract class Strategy {
        /* access modifiers changed from: package-private */
        public abstract boolean addRegex(FastDateParser fastDateParser, StringBuilder sb);

        private Strategy() {
        }

        /* access modifiers changed from: package-private */
        public boolean isNumber() {
            return false;
        }

        /* access modifiers changed from: package-private */
        public void setCalendar(FastDateParser parser, Calendar cal, String value) {
        }
    }

    private Strategy getStrategy(String formatField, Calendar definingCalendar) {
        switch (formatField.charAt(0)) {
            case '\'':
                if (formatField.length() > 2) {
                    return new CopyQuotedStrategy(formatField.substring(1, formatField.length() - 1));
                }
                break;
            case 'D':
                return DAY_OF_YEAR_STRATEGY;
            case 'E':
                return getLocaleSpecificStrategy(7, definingCalendar);
            case 'F':
                return DAY_OF_WEEK_IN_MONTH_STRATEGY;
            case 'G':
                return getLocaleSpecificStrategy(0, definingCalendar);
            case 'H':
                return MODULO_HOUR_OF_DAY_STRATEGY;
            case 'K':
                return HOUR_STRATEGY;
            case 'L':
            case 'M':
                return formatField.length() >= 3 ? getLocaleSpecificStrategy(2, definingCalendar) : NUMBER_MONTH_STRATEGY;
            case 'S':
                return MILLISECOND_STRATEGY;
            case 'W':
                return WEEK_OF_MONTH_STRATEGY;
            case 'Z':
            case 'z':
                return getLocaleSpecificStrategy(15, definingCalendar);
            case 'a':
                return getLocaleSpecificStrategy(9, definingCalendar);
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
                return formatField.length() > 2 ? LITERAL_YEAR_STRATEGY : ABBREVIATED_YEAR_STRATEGY;
        }
        return new CopyQuotedStrategy(formatField);
    }

    private static ConcurrentMap<Locale, Strategy> getCache(int field) {
        ConcurrentMap<Locale, Strategy> concurrentMap;
        ConcurrentMap<Locale, Strategy>[] concurrentMapArr = caches;
        synchronized (concurrentMapArr) {
            if (concurrentMapArr[field] == null) {
                concurrentMapArr[field] = new ConcurrentHashMap(3);
            }
            concurrentMap = concurrentMapArr[field];
        }
        return concurrentMap;
    }

    private Strategy getLocaleSpecificStrategy(int field, Calendar definingCalendar) {
        Strategy strategy;
        ConcurrentMap<Locale, Strategy> cache = getCache(field);
        Strategy strategy2 = (Strategy) cache.get(this.locale);
        if (strategy2 == null) {
            if (field == 15) {
                strategy = new TimeZoneStrategy(this.locale);
            } else {
                strategy = new TextStrategy(field, definingCalendar, this.locale);
            }
            strategy2 = strategy;
            Strategy inCache = cache.putIfAbsent(this.locale, strategy2);
            if (inCache != null) {
                return inCache;
            }
        }
        return strategy2;
    }

    private static class CopyQuotedStrategy extends Strategy {
        private final String formatField;

        CopyQuotedStrategy(String formatField2) {
            super();
            this.formatField = formatField2;
        }

        /* access modifiers changed from: package-private */
        public boolean isNumber() {
            char c = this.formatField.charAt(0);
            if (c == '\'') {
                c = this.formatField.charAt(1);
            }
            return Character.isDigit(c);
        }

        /* access modifiers changed from: package-private */
        public boolean addRegex(FastDateParser parser, StringBuilder regex) {
            StringBuilder unused = FastDateParser.escapeRegex(regex, this.formatField, true);
            return false;
        }
    }

    private static class TextStrategy extends Strategy {
        private final int field;
        private final Map<String, Integer> keyValues;

        TextStrategy(int field2, Calendar definingCalendar, Locale locale) {
            super();
            this.field = field2;
            this.keyValues = FastDateParser.getDisplayNames(field2, definingCalendar, locale);
        }

        /* access modifiers changed from: package-private */
        public boolean addRegex(FastDateParser parser, StringBuilder regex) {
            regex.append('(');
            for (String textKeyValue : this.keyValues.keySet()) {
                FastDateParser.escapeRegex(regex, textKeyValue, false).append('|');
            }
            regex.setCharAt(regex.length() - 1, ')');
            return true;
        }

        /* access modifiers changed from: package-private */
        public void setCalendar(FastDateParser parser, Calendar cal, String value) {
            Integer iVal = this.keyValues.get(value);
            if (iVal == null) {
                StringBuilder sb = new StringBuilder(value);
                sb.append(" not in (");
                for (String textKeyValue : this.keyValues.keySet()) {
                    sb.append(textKeyValue);
                    sb.append(' ');
                }
                sb.setCharAt(sb.length() - 1, ')');
                throw new IllegalArgumentException(sb.toString());
            }
            cal.set(this.field, iVal.intValue());
        }
    }

    private static class NumberStrategy extends Strategy {
        private final int field;

        NumberStrategy(int field2) {
            super();
            this.field = field2;
        }

        /* access modifiers changed from: package-private */
        public boolean isNumber() {
            return true;
        }

        /* access modifiers changed from: package-private */
        public boolean addRegex(FastDateParser parser, StringBuilder regex) {
            if (parser.isNextNumber()) {
                regex.append("(\\p{Nd}{");
                regex.append(parser.getFieldWidth());
                regex.append("}+)");
                return true;
            }
            regex.append("(\\p{Nd}++)");
            return true;
        }

        /* access modifiers changed from: package-private */
        public void setCalendar(FastDateParser parser, Calendar cal, String value) {
            cal.set(this.field, modify(Integer.parseInt(value)));
        }

        /* access modifiers changed from: package-private */
        public int modify(int iValue) {
            return iValue;
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
            for (String[] zone : DateFormatSymbols.getInstance(locale).getZoneStrings()) {
                if (!zone[0].startsWith("GMT")) {
                    TimeZone tz = DesugarTimeZone.getTimeZone(zone[0]);
                    if (!this.tzNames.containsKey(zone[1])) {
                        this.tzNames.put(zone[1], tz);
                    }
                    if (!this.tzNames.containsKey(zone[2])) {
                        this.tzNames.put(zone[2], tz);
                    }
                    if (tz.useDaylightTime()) {
                        if (!this.tzNames.containsKey(zone[3])) {
                            this.tzNames.put(zone[3], tz);
                        }
                        if (!this.tzNames.containsKey(zone[4])) {
                            this.tzNames.put(zone[4], tz);
                        }
                    }
                }
            }
            StringBuilder sb = new StringBuilder();
            sb.append("(GMT[+\\-]\\d{0,1}\\d{2}|[+\\-]\\d{2}:?\\d{2}|");
            for (String id : this.tzNames.keySet()) {
                FastDateParser.escapeRegex(sb, id, false).append('|');
            }
            sb.setCharAt(sb.length() - 1, ')');
            this.validTimeZoneChars = sb.toString();
        }

        /* access modifiers changed from: package-private */
        public boolean addRegex(FastDateParser parser, StringBuilder regex) {
            regex.append(this.validTimeZoneChars);
            return true;
        }

        /* access modifiers changed from: package-private */
        public void setCalendar(FastDateParser parser, Calendar cal, String value) {
            TimeZone tz;
            if (value.charAt(0) == '+' || value.charAt(0) == '-') {
                tz = DesugarTimeZone.getTimeZone("GMT" + value);
            } else if (value.startsWith("GMT")) {
                tz = DesugarTimeZone.getTimeZone(value);
            } else {
                tz = (TimeZone) this.tzNames.get(value);
                if (tz == null) {
                    throw new IllegalArgumentException(value + " is not a supported timezone name");
                }
            }
            cal.setTimeZone(tz);
        }
    }
}
