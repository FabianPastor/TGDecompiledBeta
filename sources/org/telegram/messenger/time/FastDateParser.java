package org.telegram.messenger.time;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.text.DateFormatSymbols;
import java.text.ParseException;
import java.text.ParsePosition;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.SortedMap;
import java.util.TimeZone;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FastDateParser implements DateParser, Serializable {
    private static final Strategy ABBREVIATED_YEAR_STRATEGY = new NumberStrategy(1) {
        /* Access modifiers changed, original: 0000 */
        public void setCalendar(FastDateParser fastDateParser, Calendar calendar, String str) {
            int parseInt = Integer.parseInt(str);
            if (parseInt < 100) {
                parseInt = fastDateParser.adjustYear(parseInt);
            }
            calendar.set(1, parseInt);
        }
    };
    private static final Strategy DAY_OF_MONTH_STRATEGY = new NumberStrategy(5);
    private static final Strategy DAY_OF_WEEK_IN_MONTH_STRATEGY = new NumberStrategy(8);
    private static final Strategy DAY_OF_YEAR_STRATEGY = new NumberStrategy(6);
    private static final Strategy HOUR_OF_DAY_STRATEGY = new NumberStrategy(11);
    private static final Strategy HOUR_STRATEGY = new NumberStrategy(10);
    static final Locale JAPANESE_IMPERIAL;
    private static final Strategy LITERAL_YEAR_STRATEGY = new NumberStrategy(1);
    private static final Strategy MILLISECOND_STRATEGY = new NumberStrategy(14);
    private static final Strategy MINUTE_STRATEGY = new NumberStrategy(12);
    private static final Strategy MODULO_HOUR_OF_DAY_STRATEGY = new NumberStrategy(11) {
        /* Access modifiers changed, original: 0000 */
        public int modify(int i) {
            return i % 24;
        }
    };
    private static final Strategy MODULO_HOUR_STRATEGY = new NumberStrategy(10) {
        /* Access modifiers changed, original: 0000 */
        public int modify(int i) {
            return i % 12;
        }
    };
    private static final Strategy NUMBER_MONTH_STRATEGY = new NumberStrategy(2) {
        /* Access modifiers changed, original: 0000 */
        public int modify(int i) {
            return i - 1;
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

    private static abstract class Strategy {
        public abstract boolean addRegex(FastDateParser fastDateParser, StringBuilder stringBuilder);

        /* Access modifiers changed, original: 0000 */
        public boolean isNumber() {
            return false;
        }

        /* Access modifiers changed, original: 0000 */
        public void setCalendar(FastDateParser fastDateParser, Calendar calendar, String str) {
        }

        private Strategy() {
        }

        /* synthetic */ Strategy(AnonymousClass1 anonymousClass1) {
            this();
        }
    }

    private static class CopyQuotedStrategy extends Strategy {
        private final String formatField;

        CopyQuotedStrategy(String str) {
            super();
            this.formatField = str;
        }

        /* Access modifiers changed, original: 0000 */
        public boolean isNumber() {
            char charAt = this.formatField.charAt(0);
            if (charAt == '\'') {
                charAt = this.formatField.charAt(1);
            }
            return Character.isDigit(charAt);
        }

        /* Access modifiers changed, original: 0000 */
        public boolean addRegex(FastDateParser fastDateParser, StringBuilder stringBuilder) {
            FastDateParser.escapeRegex(stringBuilder, this.formatField, true);
            return false;
        }
    }

    private static class NumberStrategy extends Strategy {
        private final int field;

        /* Access modifiers changed, original: 0000 */
        public boolean isNumber() {
            return true;
        }

        /* Access modifiers changed, original: 0000 */
        public int modify(int i) {
            return i;
        }

        NumberStrategy(int i) {
            super();
            this.field = i;
        }

        /* Access modifiers changed, original: 0000 */
        public boolean addRegex(FastDateParser fastDateParser, StringBuilder stringBuilder) {
            if (fastDateParser.isNextNumber()) {
                stringBuilder.append("(\\p{Nd}{");
                stringBuilder.append(fastDateParser.getFieldWidth());
                stringBuilder.append("}+)");
            } else {
                stringBuilder.append("(\\p{Nd}++)");
            }
            return true;
        }

        /* Access modifiers changed, original: 0000 */
        public void setCalendar(FastDateParser fastDateParser, Calendar calendar, String str) {
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

        /* Access modifiers changed, original: 0000 */
        public boolean addRegex(FastDateParser fastDateParser, StringBuilder stringBuilder) {
            stringBuilder.append('(');
            for (String access$100 : this.keyValues.keySet()) {
                FastDateParser.escapeRegex(stringBuilder, access$100, false);
                stringBuilder.append('|');
            }
            stringBuilder.setCharAt(stringBuilder.length() - 1, ')');
            return true;
        }

        /* Access modifiers changed, original: 0000 */
        public void setCalendar(FastDateParser fastDateParser, Calendar calendar, String str) {
            Integer num = (Integer) this.keyValues.get(str);
            if (num == null) {
                StringBuilder stringBuilder = new StringBuilder(str);
                stringBuilder.append(" not in (");
                for (String str2 : this.keyValues.keySet()) {
                    stringBuilder.append(str2);
                    stringBuilder.append(' ');
                }
                stringBuilder.setCharAt(stringBuilder.length() - 1, ')');
                throw new IllegalArgumentException(stringBuilder.toString());
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
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("(GMT[+\\-]\\d{0,1}\\d{2}|[+\\-]\\d{2}:?\\d{2}|");
            for (String access$100 : this.tzNames.keySet()) {
                FastDateParser.escapeRegex(stringBuilder, access$100, false);
                stringBuilder.append('|');
            }
            stringBuilder.setCharAt(stringBuilder.length() - 1, ')');
            this.validTimeZoneChars = stringBuilder.toString();
        }

        /* Access modifiers changed, original: 0000 */
        public boolean addRegex(FastDateParser fastDateParser, StringBuilder stringBuilder) {
            stringBuilder.append(this.validTimeZoneChars);
            return true;
        }

        /* Access modifiers changed, original: 0000 */
        public void setCalendar(FastDateParser fastDateParser, Calendar calendar, String str) {
            TimeZone timeZone;
            String str2 = "GMT";
            if (str.charAt(0) == '+' || str.charAt(0) == '-') {
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append(str2);
                stringBuilder.append(str);
                timeZone = TimeZone.getTimeZone(stringBuilder.toString());
            } else if (str.startsWith(str2)) {
                timeZone = TimeZone.getTimeZone(str);
            } else {
                timeZone = (TimeZone) this.tzNames.get(str);
                if (timeZone == null) {
                    StringBuilder stringBuilder2 = new StringBuilder();
                    stringBuilder2.append(str);
                    stringBuilder2.append(" is not a supported timezone name");
                    throw new IllegalArgumentException(stringBuilder2.toString());
                }
            }
            calendar.setTimeZone(timeZone);
        }
    }

    /*  JADX ERROR: JadxRuntimeException in pass: BlockProcessor
        jadx.core.utils.exceptions.JadxRuntimeException: Can't find immediate dominator for block B:20:0x00c9 in {9, 11, 13, 16, 17, 19} preds:[]
        	at jadx.core.dex.visitors.blocksmaker.BlockProcessor.computeDominators(BlockProcessor.java:242)
        	at jadx.core.dex.visitors.blocksmaker.BlockProcessor.processBlocksTree(BlockProcessor.java:52)
        	at jadx.core.dex.visitors.blocksmaker.BlockProcessor.visit(BlockProcessor.java:42)
        	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:27)
        	at jadx.core.dex.visitors.DepthTraversal.lambda$visit$1(DepthTraversal.java:14)
        	at java.util.ArrayList.forEach(ArrayList.java:1257)
        	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:14)
        	at jadx.core.ProcessClass.process(ProcessClass.java:32)
        	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:292)
        	at jadx.api.JavaClass.decompile(JavaClass.java:62)
        	at jadx.api.JadxDecompiler.lambda$appendSourcesSave$0(JadxDecompiler.java:200)
        */
    private void init(java.util.Calendar r7) {
        /*
        r6 = this;
        r0 = new java.lang.StringBuilder;
        r0.<init>();
        r1 = new java.util.ArrayList;
        r1.<init>();
        r2 = formatPattern;
        r3 = r6.pattern;
        r2 = r2.matcher(r3);
        r3 = r2.lookingAt();
        if (r3 == 0) goto L_0x00a3;
        r3 = r2.group();
        r6.currentFormatField = r3;
        r3 = r6.currentFormatField;
        r3 = r6.getStrategy(r3, r7);
        r4 = r2.end();
        r5 = r2.regionEnd();
        r2.region(r4, r5);
        r4 = r2.lookingAt();
        if (r4 != 0) goto L_0x008b;
        r7 = 0;
        r6.nextStrategy = r7;
        r4 = r2.regionStart();
        r5 = r2.regionEnd();
        if (r4 != r5) goto L_0x0066;
        r2 = r3.addRegex(r6, r0);
        if (r2 == 0) goto L_0x004b;
        r1.add(r3);
        r6.currentFormatField = r7;
        r7 = r1.size();
        r7 = new org.telegram.messenger.time.FastDateParser.Strategy[r7];
        r7 = r1.toArray(r7);
        r7 = (org.telegram.messenger.time.FastDateParser.Strategy[]) r7;
        r6.strategies = r7;
        r7 = r0.toString();
        r7 = java.util.regex.Pattern.compile(r7);
        r6.parsePattern = r7;
        return;
        r7 = new java.lang.IllegalArgumentException;
        r0 = new java.lang.StringBuilder;
        r0.<init>();
        r1 = "Failed to parse \"";
        r0.append(r1);
        r1 = r6.pattern;
        r0.append(r1);
        r1 = "\" ; gave up at index ";
        r0.append(r1);
        r1 = r2.regionStart();
        r0.append(r1);
        r0 = r0.toString();
        r7.<init>(r0);
        throw r7;
        r4 = r2.group();
        r5 = r6.getStrategy(r4, r7);
        r6.nextStrategy = r5;
        r5 = r3.addRegex(r6, r0);
        if (r5 == 0) goto L_0x009e;
        r1.add(r3);
        r6.currentFormatField = r4;
        r3 = r6.nextStrategy;
        goto L_0x0024;
        r7 = new java.lang.IllegalArgumentException;
        r0 = new java.lang.StringBuilder;
        r0.<init>();
        r1 = "Illegal pattern character '";
        r0.append(r1);
        r1 = r6.pattern;
        r2 = r2.regionStart();
        r1 = r1.charAt(r2);
        r0.append(r1);
        r1 = "'";
        r0.append(r1);
        r0 = r0.toString();
        r7.<init>(r0);
        throw r7;
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.time.FastDateParser.init(java.util.Calendar):void");
    }

    static {
        String str = "JP";
        JAPANESE_IMPERIAL = new Locale("ja", str, str);
    }

    protected FastDateParser(String str, TimeZone timeZone, Locale locale) {
        this(str, timeZone, locale, null);
    }

    protected FastDateParser(String str, TimeZone timeZone, Locale locale, Date date) {
        int i;
        this.pattern = str;
        this.timeZone = timeZone;
        this.locale = locale;
        Calendar instance = Calendar.getInstance(timeZone, locale);
        if (date != null) {
            instance.setTime(date);
            i = instance.get(1);
        } else if (locale.equals(JAPANESE_IMPERIAL)) {
            i = 0;
        } else {
            instance.setTime(new Date());
            i = instance.get(1) - 80;
        }
        this.century = (i / 100) * 100;
        this.startYear = i - this.century;
        init(instance);
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

    /* Access modifiers changed, original: 0000 */
    public Pattern getParsePattern() {
        return this.parsePattern;
    }

    public boolean equals(Object obj) {
        boolean z = false;
        if (!(obj instanceof FastDateParser)) {
            return false;
        }
        FastDateParser fastDateParser = (FastDateParser) obj;
        if (this.pattern.equals(fastDateParser.pattern) && this.timeZone.equals(fastDateParser.timeZone) && this.locale.equals(fastDateParser.locale)) {
            z = true;
        }
        return z;
    }

    public int hashCode() {
        return this.pattern.hashCode() + ((this.timeZone.hashCode() + (this.locale.hashCode() * 13)) * 13);
    }

    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("FastDateParser[");
        stringBuilder.append(this.pattern);
        String str = ",";
        stringBuilder.append(str);
        stringBuilder.append(this.locale);
        stringBuilder.append(str);
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
        String str2 = "\" does not match ";
        StringBuilder stringBuilder;
        if (this.locale.equals(JAPANESE_IMPERIAL)) {
            stringBuilder = new StringBuilder();
            stringBuilder.append("(The ");
            stringBuilder.append(this.locale);
            stringBuilder.append(" locale does not support dates before 1868 AD)\nUnparseable date: \"");
            stringBuilder.append(str);
            stringBuilder.append(str2);
            stringBuilder.append(this.parsePattern.pattern());
            throw new ParseException(stringBuilder.toString(), 0);
        }
        stringBuilder = new StringBuilder();
        stringBuilder.append("Unparseable date: \"");
        stringBuilder.append(str);
        stringBuilder.append(str2);
        stringBuilder.append(this.parsePattern.pattern());
        throw new ParseException(stringBuilder.toString(), 0);
    }

    public Object parseObject(String str, ParsePosition parsePosition) {
        return parse(str, parsePosition);
    }

    public Date parse(String str, ParsePosition parsePosition) {
        int index = parsePosition.getIndex();
        Matcher matcher = this.parsePattern.matcher(str.substring(index));
        if (!matcher.lookingAt()) {
            return null;
        }
        Calendar instance = Calendar.getInstance(this.timeZone, this.locale);
        instance.clear();
        int i = 0;
        while (true) {
            Strategy[] strategyArr = this.strategies;
            if (i < strategyArr.length) {
                int i2 = i + 1;
                strategyArr[i].setCalendar(this, instance, matcher.group(i2));
                i = i2;
            } else {
                parsePosition.setIndex(index + matcher.end());
                return instance.getTime();
            }
        }
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
        } else if (i != 9) {
            return null;
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
        return hashMap.isEmpty() ? null : hashMap;
    }

    private static Map<String, Integer> getDisplayNames(int i, Calendar calendar, Locale locale) {
        return getDisplayNames(i, locale);
    }

    private int adjustYear(int i) {
        int i2 = this.century + i;
        return i >= this.startYear ? i2 : i2 + 100;
    }

    /* Access modifiers changed, original: 0000 */
    public boolean isNextNumber() {
        Strategy strategy = this.nextStrategy;
        return strategy != null && strategy.isNumber();
    }

    /* Access modifiers changed, original: 0000 */
    public int getFieldWidth() {
        return this.currentFormatField.length();
    }

    /* JADX WARNING: Missing block: B:47:0x0078, code skipped:
            return new org.telegram.messenger.time.FastDateParser.CopyQuotedStrategy(r5);
     */
    private org.telegram.messenger.time.FastDateParser.Strategy getStrategy(java.lang.String r5, java.util.Calendar r6) {
        /*
        r4 = this;
        r0 = 0;
        r1 = r5.charAt(r0);
        r2 = 121; // 0x79 float:1.7E-43 double:6.0E-322;
        r3 = 2;
        if (r1 == r2) goto L_0x0080;
    L_0x000a:
        r2 = 122; // 0x7a float:1.71E-43 double:6.03E-322;
        if (r1 == r2) goto L_0x0079;
    L_0x000e:
        switch(r1) {
            case 39: goto L_0x005d;
            case 83: goto L_0x005a;
            case 87: goto L_0x0057;
            case 90: goto L_0x0079;
            case 97: goto L_0x0050;
            case 100: goto L_0x004d;
            case 104: goto L_0x004a;
            case 107: goto L_0x0047;
            case 109: goto L_0x0044;
            case 115: goto L_0x0041;
            case 119: goto L_0x003e;
            default: goto L_0x0011;
        };
    L_0x0011:
        switch(r1) {
            case 68: goto L_0x003b;
            case 69: goto L_0x0035;
            case 70: goto L_0x0032;
            case 71: goto L_0x002d;
            case 72: goto L_0x002a;
            default: goto L_0x0014;
        };
    L_0x0014:
        switch(r1) {
            case 75: goto L_0x0027;
            case 76: goto L_0x0018;
            case 77: goto L_0x0018;
            default: goto L_0x0017;
        };
    L_0x0017:
        goto L_0x0073;
    L_0x0018:
        r5 = r5.length();
        r0 = 3;
        if (r5 < r0) goto L_0x0024;
    L_0x001f:
        r5 = r4.getLocaleSpecificStrategy(r3, r6);
        goto L_0x0026;
    L_0x0024:
        r5 = NUMBER_MONTH_STRATEGY;
    L_0x0026:
        return r5;
    L_0x0027:
        r5 = HOUR_STRATEGY;
        return r5;
    L_0x002a:
        r5 = MODULO_HOUR_OF_DAY_STRATEGY;
        return r5;
    L_0x002d:
        r5 = r4.getLocaleSpecificStrategy(r0, r6);
        return r5;
    L_0x0032:
        r5 = DAY_OF_WEEK_IN_MONTH_STRATEGY;
        return r5;
    L_0x0035:
        r5 = 7;
        r5 = r4.getLocaleSpecificStrategy(r5, r6);
        return r5;
    L_0x003b:
        r5 = DAY_OF_YEAR_STRATEGY;
        return r5;
    L_0x003e:
        r5 = WEEK_OF_YEAR_STRATEGY;
        return r5;
    L_0x0041:
        r5 = SECOND_STRATEGY;
        return r5;
    L_0x0044:
        r5 = MINUTE_STRATEGY;
        return r5;
    L_0x0047:
        r5 = HOUR_OF_DAY_STRATEGY;
        return r5;
    L_0x004a:
        r5 = MODULO_HOUR_STRATEGY;
        return r5;
    L_0x004d:
        r5 = DAY_OF_MONTH_STRATEGY;
        return r5;
    L_0x0050:
        r5 = 9;
        r5 = r4.getLocaleSpecificStrategy(r5, r6);
        return r5;
    L_0x0057:
        r5 = WEEK_OF_MONTH_STRATEGY;
        return r5;
    L_0x005a:
        r5 = MILLISECOND_STRATEGY;
        return r5;
    L_0x005d:
        r6 = r5.length();
        if (r6 <= r3) goto L_0x0073;
    L_0x0063:
        r6 = new org.telegram.messenger.time.FastDateParser$CopyQuotedStrategy;
        r0 = r5.length();
        r1 = 1;
        r0 = r0 - r1;
        r5 = r5.substring(r1, r0);
        r6.<init>(r5);
        return r6;
    L_0x0073:
        r6 = new org.telegram.messenger.time.FastDateParser$CopyQuotedStrategy;
        r6.<init>(r5);
        return r6;
    L_0x0079:
        r5 = 15;
        r5 = r4.getLocaleSpecificStrategy(r5, r6);
        return r5;
    L_0x0080:
        r5 = r5.length();
        if (r5 <= r3) goto L_0x0089;
    L_0x0086:
        r5 = LITERAL_YEAR_STRATEGY;
        goto L_0x008b;
    L_0x0089:
        r5 = ABBREVIATED_YEAR_STRATEGY;
    L_0x008b:
        return r5;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.time.FastDateParser.getStrategy(java.lang.String, java.util.Calendar):org.telegram.messenger.time.FastDateParser$Strategy");
    }

    private static ConcurrentMap<Locale, Strategy> getCache(int i) {
        ConcurrentMap<Locale, Strategy> concurrentMap;
        synchronized (caches) {
            if (caches[i] == null) {
                caches[i] = new ConcurrentHashMap(3);
            }
            concurrentMap = caches[i];
        }
        return concurrentMap;
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
