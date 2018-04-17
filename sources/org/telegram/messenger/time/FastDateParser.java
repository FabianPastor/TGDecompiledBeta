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
        void setCalendar(FastDateParser parser, Calendar cal, String value) {
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
        int modify(int iValue) {
            return iValue % 24;
        }
    };
    private static final Strategy MODULO_HOUR_STRATEGY = new NumberStrategy(10) {
        int modify(int iValue) {
            return iValue % 12;
        }
    };
    private static final Strategy NUMBER_MONTH_STRATEGY = new NumberStrategy(2) {
        int modify(int iValue) {
            return iValue - 1;
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

        private Strategy() {
        }

        boolean isNumber() {
            return false;
        }

        void setCalendar(FastDateParser parser, Calendar cal, String value) {
        }
    }

    private static class CopyQuotedStrategy extends Strategy {
        private final String formatField;

        CopyQuotedStrategy(String formatField) {
            super();
            this.formatField = formatField;
        }

        boolean isNumber() {
            char c = this.formatField.charAt(0);
            if (c == '\'') {
                c = this.formatField.charAt(1);
            }
            return Character.isDigit(c);
        }

        boolean addRegex(FastDateParser parser, StringBuilder regex) {
            FastDateParser.escapeRegex(regex, this.formatField, true);
            return false;
        }
    }

    private static class NumberStrategy extends Strategy {
        private final int field;

        NumberStrategy(int field) {
            super();
            this.field = field;
        }

        boolean isNumber() {
            return true;
        }

        boolean addRegex(FastDateParser parser, StringBuilder regex) {
            if (parser.isNextNumber()) {
                regex.append("(\\p{Nd}{");
                regex.append(parser.getFieldWidth());
                regex.append("}+)");
            } else {
                regex.append("(\\p{Nd}++)");
            }
            return true;
        }

        void setCalendar(FastDateParser parser, Calendar cal, String value) {
            cal.set(this.field, modify(Integer.parseInt(value)));
        }

        int modify(int iValue) {
            return iValue;
        }
    }

    private static class TextStrategy extends Strategy {
        private final int field;
        private final Map<String, Integer> keyValues;

        TextStrategy(int field, Calendar definingCalendar, Locale locale) {
            super();
            this.field = field;
            this.keyValues = FastDateParser.getDisplayNames(field, definingCalendar, locale);
        }

        boolean addRegex(FastDateParser parser, StringBuilder regex) {
            regex.append('(');
            for (String textKeyValue : this.keyValues.keySet()) {
                FastDateParser.escapeRegex(regex, textKeyValue, false).append('|');
            }
            regex.setCharAt(regex.length() - 1, ')');
            return true;
        }

        void setCalendar(FastDateParser parser, Calendar cal, String value) {
            Integer iVal = (Integer) this.keyValues.get(value);
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
                    TimeZone tz = TimeZone.getTimeZone(zone[0]);
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

        boolean addRegex(FastDateParser parser, StringBuilder regex) {
            regex.append(this.validTimeZoneChars);
            return true;
        }

        void setCalendar(FastDateParser parser, Calendar cal, String value) {
            TimeZone tz;
            if (value.charAt(0) != '+') {
                if (value.charAt(0) != '-') {
                    if (value.startsWith("GMT")) {
                        tz = TimeZone.getTimeZone(value);
                    } else {
                        tz = (TimeZone) this.tzNames.get(value);
                        if (tz == null) {
                            StringBuilder stringBuilder = new StringBuilder();
                            stringBuilder.append(value);
                            stringBuilder.append(" is not a supported timezone name");
                            throw new IllegalArgumentException(stringBuilder.toString());
                        }
                    }
                    cal.setTimeZone(tz);
                }
            }
            StringBuilder stringBuilder2 = new StringBuilder();
            stringBuilder2.append("GMT");
            stringBuilder2.append(value);
            tz = TimeZone.getTimeZone(stringBuilder2.toString());
            cal.setTimeZone(tz);
        }
    }

    protected FastDateParser(String pattern, TimeZone timeZone, Locale locale) {
        this(pattern, timeZone, locale, null);
    }

    protected FastDateParser(String pattern, TimeZone timeZone, Locale locale, Date centuryStart) {
        int centuryStartYear;
        this.pattern = pattern;
        this.timeZone = timeZone;
        this.locale = locale;
        Calendar definingCalendar = Calendar.getInstance(timeZone, locale);
        if (centuryStart != null) {
            definingCalendar.setTime(centuryStart);
            centuryStartYear = definingCalendar.get(1);
        } else if (locale.equals(JAPANESE_IMPERIAL)) {
            centuryStartYear = 0;
        } else {
            definingCalendar.setTime(new Date());
            centuryStartYear = definingCalendar.get(1) - 80;
            this.century = (centuryStartYear / 100) * 100;
            this.startYear = centuryStartYear - this.century;
            init(definingCalendar);
        }
        this.century = (centuryStartYear / 100) * 100;
        this.startYear = centuryStartYear - this.century;
        init(definingCalendar);
    }

    private void init(Calendar definingCalendar) {
        StringBuilder regex = new StringBuilder();
        List<Strategy> collector = new ArrayList();
        Matcher patternMatcher = formatPattern.matcher(this.pattern);
        if (patternMatcher.lookingAt()) {
            this.currentFormatField = patternMatcher.group();
            Strategy currentStrategy = getStrategy(this.currentFormatField, definingCalendar);
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
            if (patternMatcher.regionStart() != patternMatcher.regionEnd()) {
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("Failed to parse \"");
                stringBuilder.append(this.pattern);
                stringBuilder.append("\" ; gave up at index ");
                stringBuilder.append(patternMatcher.regionStart());
                throw new IllegalArgumentException(stringBuilder.toString());
            }
            if (currentStrategy.addRegex(this, regex)) {
                collector.add(currentStrategy);
            }
            this.currentFormatField = null;
            this.strategies = (Strategy[]) collector.toArray(new Strategy[collector.size()]);
            this.parsePattern = Pattern.compile(regex.toString());
            return;
        }
        StringBuilder stringBuilder2 = new StringBuilder();
        stringBuilder2.append("Illegal pattern character '");
        stringBuilder2.append(this.pattern.charAt(patternMatcher.regionStart()));
        stringBuilder2.append("'");
        throw new IllegalArgumentException(stringBuilder2.toString());
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
        FastDateParser other = (FastDateParser) obj;
        if (this.pattern.equals(other.pattern) && this.timeZone.equals(other.timeZone) && this.locale.equals(other.locale)) {
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
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("(The ");
            stringBuilder.append(this.locale);
            stringBuilder.append(" locale does not support dates before 1868 AD)\nUnparseable date: \"");
            stringBuilder.append(source);
            stringBuilder.append("\" does not match ");
            stringBuilder.append(this.parsePattern.pattern());
            throw new ParseException(stringBuilder.toString(), 0);
        }
        stringBuilder = new StringBuilder();
        stringBuilder.append("Unparseable date: \"");
        stringBuilder.append(source);
        stringBuilder.append("\" does not match ");
        stringBuilder.append(this.parsePattern.pattern());
        throw new ParseException(stringBuilder.toString(), 0);
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
        Strategy strategy = null;
        while (strategy < this.strategies.length) {
            int i = strategy + 1;
            this.strategies[strategy].setCalendar(this, cal, matcher.group(i));
            strategy = i;
        }
        pos.setIndex(matcher.end() + offset);
        return cal.getTime();
    }

    private static StringBuilder escapeRegex(StringBuilder regex, String value, boolean unquote) {
        regex.append("\\Q");
        int i = 0;
        while (i < value.length()) {
            char c = value.charAt(i);
            if (c != '\'') {
                if (c == '\\') {
                    i++;
                    if (i != value.length()) {
                        regex.append(c);
                        c = value.charAt(i);
                        if (c == 'E') {
                            regex.append("E\\\\E\\");
                            c = 'Q';
                        }
                    }
                }
            } else if (unquote) {
                i++;
                if (i == value.length()) {
                    return regex;
                }
                c = value.charAt(i);
            } else {
                continue;
            }
            regex.append(c);
            i++;
        }
        regex.append("\\E");
        return regex;
    }

    private static String[] getDisplayNameArray(int field, boolean isLong, Locale locale) {
        DateFormatSymbols dfs = new DateFormatSymbols(locale);
        if (field == 0) {
            return dfs.getEras();
        }
        if (field == 2) {
            return isLong ? dfs.getMonths() : dfs.getShortMonths();
        } else if (field == 7) {
            return isLong ? dfs.getWeekdays() : dfs.getShortWeekdays();
        } else if (field != 9) {
            return null;
        } else {
            return dfs.getAmPmStrings();
        }
    }

    private static void insertValuesInMap(Map<String, Integer> map, String[] values) {
        if (values != null) {
            int i = 0;
            while (i < values.length) {
                if (values[i] != null && values[i].length() > 0) {
                    map.put(values[i], Integer.valueOf(i));
                }
                i++;
            }
        }
    }

    private static Map<String, Integer> getDisplayNames(int field, Locale locale) {
        Map<String, Integer> result = new HashMap();
        insertValuesInMap(result, getDisplayNameArray(field, false, locale));
        insertValuesInMap(result, getDisplayNameArray(field, true, locale));
        return result.isEmpty() ? null : result;
    }

    private static Map<String, Integer> getDisplayNames(int field, Calendar definingCalendar, Locale locale) {
        return getDisplayNames(field, locale);
    }

    private int adjustYear(int twoDigitYear) {
        int trial = this.century + twoDigitYear;
        return twoDigitYear >= this.startYear ? trial : trial + 100;
    }

    boolean isNextNumber() {
        return this.nextStrategy != null && this.nextStrategy.isNumber();
    }

    int getFieldWidth() {
        return this.currentFormatField.length();
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
            default:
                break;
        }
        return new CopyQuotedStrategy(formatField);
    }

    private static ConcurrentMap<Locale, Strategy> getCache(int field) {
        ConcurrentMap<Locale, Strategy> concurrentMap;
        synchronized (caches) {
            if (caches[field] == null) {
                caches[field] = new ConcurrentHashMap(3);
            }
            concurrentMap = caches[field];
        }
        return concurrentMap;
    }

    private Strategy getLocaleSpecificStrategy(int field, Calendar definingCalendar) {
        ConcurrentMap<Locale, Strategy> cache = getCache(field);
        Strategy strategy = (Strategy) cache.get(this.locale);
        if (strategy == null) {
            strategy = field == 15 ? new TimeZoneStrategy(this.locale) : new TextStrategy(field, definingCalendar, this.locale);
            Strategy inCache = (Strategy) cache.putIfAbsent(this.locale, strategy);
            if (inCache != null) {
                return inCache;
            }
        }
        return strategy;
    }
}
