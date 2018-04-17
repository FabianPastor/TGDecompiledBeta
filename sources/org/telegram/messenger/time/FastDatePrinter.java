package org.telegram.messenger.time;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.text.DateFormatSymbols;
import java.text.FieldPosition;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class FastDatePrinter implements Serializable, DatePrinter {
    public static final int FULL = 0;
    public static final int LONG = 1;
    public static final int MEDIUM = 2;
    public static final int SHORT = 3;
    private static final ConcurrentMap<TimeZoneDisplayKey, String> cTimeZoneDisplayCache = new ConcurrentHashMap(7);
    private static final long serialVersionUID = 1;
    private final Locale mLocale;
    private transient int mMaxLengthEstimate;
    private final String mPattern;
    private transient Rule[] mRules;
    private final TimeZone mTimeZone;

    private interface Rule {
        void appendTo(StringBuffer stringBuffer, Calendar calendar);

        int estimateLength();
    }

    private static class TimeZoneDisplayKey {
        private final Locale mLocale;
        private final int mStyle;
        private final TimeZone mTimeZone;

        TimeZoneDisplayKey(TimeZone timeZone, boolean daylight, int style, Locale locale) {
            this.mTimeZone = timeZone;
            if (daylight) {
                this.mStyle = Integer.MIN_VALUE | style;
            } else {
                this.mStyle = style;
            }
            this.mLocale = locale;
        }

        public int hashCode() {
            return (((this.mStyle * 31) + this.mLocale.hashCode()) * 31) + this.mTimeZone.hashCode();
        }

        public boolean equals(Object obj) {
            boolean z = true;
            if (this == obj) {
                return true;
            }
            if (!(obj instanceof TimeZoneDisplayKey)) {
                return false;
            }
            TimeZoneDisplayKey other = (TimeZoneDisplayKey) obj;
            if (!this.mTimeZone.equals(other.mTimeZone) || this.mStyle != other.mStyle || !this.mLocale.equals(other.mLocale)) {
                z = false;
            }
            return z;
        }
    }

    private static class CharacterLiteral implements Rule {
        private final char mValue;

        CharacterLiteral(char value) {
            this.mValue = value;
        }

        public int estimateLength() {
            return 1;
        }

        public void appendTo(StringBuffer buffer, Calendar calendar) {
            buffer.append(this.mValue);
        }
    }

    private interface NumberRule extends Rule {
        void appendTo(StringBuffer stringBuffer, int i);
    }

    private static class StringLiteral implements Rule {
        private final String mValue;

        StringLiteral(String value) {
            this.mValue = value;
        }

        public int estimateLength() {
            return this.mValue.length();
        }

        public void appendTo(StringBuffer buffer, Calendar calendar) {
            buffer.append(this.mValue);
        }
    }

    private static class TextField implements Rule {
        private final int mField;
        private final String[] mValues;

        TextField(int field, String[] values) {
            this.mField = field;
            this.mValues = values;
        }

        public int estimateLength() {
            int max = 0;
            int i = this.mValues.length;
            while (true) {
                i--;
                if (i < 0) {
                    return max;
                }
                int len = this.mValues[i].length();
                if (len > max) {
                    max = len;
                }
            }
        }

        public void appendTo(StringBuffer buffer, Calendar calendar) {
            buffer.append(this.mValues[calendar.get(this.mField)]);
        }
    }

    private static class TimeZoneNameRule implements Rule {
        private final String mDaylight;
        private final Locale mLocale;
        private final String mStandard;
        private final int mStyle;

        TimeZoneNameRule(TimeZone timeZone, Locale locale, int style) {
            this.mLocale = locale;
            this.mStyle = style;
            this.mStandard = FastDatePrinter.getTimeZoneDisplay(timeZone, false, style, locale);
            this.mDaylight = FastDatePrinter.getTimeZoneDisplay(timeZone, true, style, locale);
        }

        public int estimateLength() {
            return Math.max(this.mStandard.length(), this.mDaylight.length());
        }

        public void appendTo(StringBuffer buffer, Calendar calendar) {
            TimeZone zone = calendar.getTimeZone();
            if (!zone.useDaylightTime() || calendar.get(16) == 0) {
                buffer.append(FastDatePrinter.getTimeZoneDisplay(zone, false, this.mStyle, this.mLocale));
            } else {
                buffer.append(FastDatePrinter.getTimeZoneDisplay(zone, true, this.mStyle, this.mLocale));
            }
        }
    }

    private static class TimeZoneNumberRule implements Rule {
        static final TimeZoneNumberRule INSTANCE_COLON = new TimeZoneNumberRule(true);
        static final TimeZoneNumberRule INSTANCE_NO_COLON = new TimeZoneNumberRule(false);
        final boolean mColon;

        TimeZoneNumberRule(boolean colon) {
            this.mColon = colon;
        }

        public int estimateLength() {
            return 5;
        }

        public void appendTo(StringBuffer buffer, Calendar calendar) {
            int offset = calendar.get(15) + calendar.get(16);
            if (offset < 0) {
                buffer.append('-');
                offset = -offset;
            } else {
                buffer.append('+');
            }
            int hours = offset / 3600000;
            buffer.append((char) ((hours / 10) + 48));
            buffer.append((char) ((hours % 10) + 48));
            if (this.mColon) {
                buffer.append(':');
            }
            int minutes = (offset / 60000) - (60 * hours);
            buffer.append((char) ((minutes / 10) + 48));
            buffer.append((char) ((minutes % 10) + 48));
        }
    }

    private static class PaddedNumberField implements NumberRule {
        private final int mField;
        private final int mSize;

        PaddedNumberField(int field, int size) {
            if (size < 3) {
                throw new IllegalArgumentException();
            }
            this.mField = field;
            this.mSize = size;
        }

        public int estimateLength() {
            return 4;
        }

        public void appendTo(StringBuffer buffer, Calendar calendar) {
            appendTo(buffer, calendar.get(this.mField));
        }

        public final void appendTo(StringBuffer buffer, int value) {
            int i;
            if (value < 100) {
                i = this.mSize;
                while (true) {
                    i--;
                    if (i >= 2) {
                        buffer.append('0');
                    } else {
                        buffer.append((char) ((value / 10) + 48));
                        buffer.append((char) ((value % 10) + 48));
                        return;
                    }
                }
            }
            if (value < 1000) {
                i = 3;
            } else {
                i = Integer.toString(value).length();
            }
            int i2 = this.mSize;
            while (true) {
                i2--;
                if (i2 >= i) {
                    buffer.append('0');
                } else {
                    buffer.append(Integer.toString(value));
                    return;
                }
            }
        }
    }

    private static class TwelveHourField implements NumberRule {
        private final NumberRule mRule;

        TwelveHourField(NumberRule rule) {
            this.mRule = rule;
        }

        public int estimateLength() {
            return this.mRule.estimateLength();
        }

        public void appendTo(StringBuffer buffer, Calendar calendar) {
            int value = calendar.get(10);
            if (value == 0) {
                value = calendar.getLeastMaximum(10) + 1;
            }
            this.mRule.appendTo(buffer, value);
        }

        public void appendTo(StringBuffer buffer, int value) {
            this.mRule.appendTo(buffer, value);
        }
    }

    private static class TwentyFourHourField implements NumberRule {
        private final NumberRule mRule;

        TwentyFourHourField(NumberRule rule) {
            this.mRule = rule;
        }

        public int estimateLength() {
            return this.mRule.estimateLength();
        }

        public void appendTo(StringBuffer buffer, Calendar calendar) {
            int value = calendar.get(11);
            if (value == 0) {
                value = calendar.getMaximum(11) + 1;
            }
            this.mRule.appendTo(buffer, value);
        }

        public void appendTo(StringBuffer buffer, int value) {
            this.mRule.appendTo(buffer, value);
        }
    }

    private static class TwoDigitMonthField implements NumberRule {
        static final TwoDigitMonthField INSTANCE = new TwoDigitMonthField();

        TwoDigitMonthField() {
        }

        public int estimateLength() {
            return 2;
        }

        public void appendTo(StringBuffer buffer, Calendar calendar) {
            appendTo(buffer, calendar.get(2) + 1);
        }

        public final void appendTo(StringBuffer buffer, int value) {
            buffer.append((char) ((value / 10) + 48));
            buffer.append((char) ((value % 10) + 48));
        }
    }

    private static class TwoDigitNumberField implements NumberRule {
        private final int mField;

        TwoDigitNumberField(int field) {
            this.mField = field;
        }

        public int estimateLength() {
            return 2;
        }

        public void appendTo(StringBuffer buffer, Calendar calendar) {
            appendTo(buffer, calendar.get(this.mField));
        }

        public final void appendTo(StringBuffer buffer, int value) {
            if (value < 100) {
                buffer.append((char) ((value / 10) + 48));
                buffer.append((char) ((value % 10) + 48));
                return;
            }
            buffer.append(Integer.toString(value));
        }
    }

    private static class TwoDigitYearField implements NumberRule {
        static final TwoDigitYearField INSTANCE = new TwoDigitYearField();

        TwoDigitYearField() {
        }

        public int estimateLength() {
            return 2;
        }

        public void appendTo(StringBuffer buffer, Calendar calendar) {
            appendTo(buffer, calendar.get(1) % 100);
        }

        public final void appendTo(StringBuffer buffer, int value) {
            buffer.append((char) ((value / 10) + 48));
            buffer.append((char) ((value % 10) + 48));
        }
    }

    private static class UnpaddedMonthField implements NumberRule {
        static final UnpaddedMonthField INSTANCE = new UnpaddedMonthField();

        UnpaddedMonthField() {
        }

        public int estimateLength() {
            return 2;
        }

        public void appendTo(StringBuffer buffer, Calendar calendar) {
            appendTo(buffer, calendar.get(2) + 1);
        }

        public final void appendTo(StringBuffer buffer, int value) {
            if (value < 10) {
                buffer.append((char) (value + 48));
                return;
            }
            buffer.append((char) ((value / 10) + 48));
            buffer.append((char) ((value % 10) + 48));
        }
    }

    private static class UnpaddedNumberField implements NumberRule {
        private final int mField;

        UnpaddedNumberField(int field) {
            this.mField = field;
        }

        public int estimateLength() {
            return 4;
        }

        public void appendTo(StringBuffer buffer, Calendar calendar) {
            appendTo(buffer, calendar.get(this.mField));
        }

        public final void appendTo(StringBuffer buffer, int value) {
            if (value < 10) {
                buffer.append((char) (value + 48));
            } else if (value < 100) {
                buffer.append((char) ((value / 10) + 48));
                buffer.append((char) ((value % 10) + 48));
            } else {
                buffer.append(Integer.toString(value));
            }
        }
    }

    protected FastDatePrinter(String pattern, TimeZone timeZone, Locale locale) {
        this.mPattern = pattern;
        this.mTimeZone = timeZone;
        this.mLocale = locale;
        init();
    }

    private void init() {
        List<Rule> rulesList = parsePattern();
        this.mRules = (Rule[]) rulesList.toArray(new Rule[rulesList.size()]);
        int len = 0;
        int i = this.mRules.length;
        while (true) {
            i--;
            if (i >= 0) {
                len += this.mRules[i].estimateLength();
            } else {
                this.mMaxLengthEstimate = len;
                return;
            }
        }
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    protected List<Rule> parsePattern() {
        String[] strArr;
        DateFormatSymbols symbols = new DateFormatSymbols(this.mLocale);
        List<Rule> rules = new ArrayList();
        String[] ERAs = symbols.getEras();
        String[] months = symbols.getMonths();
        String[] shortMonths = symbols.getShortMonths();
        String[] weekdays = symbols.getWeekdays();
        String[] shortWeekdays = symbols.getShortWeekdays();
        String[] AmPmStrings = symbols.getAmPmStrings();
        int length = this.mPattern.length();
        int i = 1;
        int[] indexRef = new int[1];
        int i2 = 0;
        int i3 = 0;
        while (i3 < length) {
            indexRef[i2] = i3;
            String token = parseToken(r0.mPattern, indexRef);
            i3 = indexRef[i2];
            int tokenLen = token.length();
            if (tokenLen == 0) {
                DateFormatSymbols dateFormatSymbols = symbols;
                strArr = weekdays;
                return rules;
            }
            char c = token.charAt(i2);
            i2 = 4;
            switch (c) {
                case '\'':
                    dateFormatSymbols = symbols;
                    strArr = weekdays;
                    String sub = token.substring(1);
                    if (sub.length() == 1) {
                        i2 = 0;
                        symbols = new CharacterLiteral(sub.charAt(0));
                        break;
                    }
                    i2 = 0;
                    symbols = new StringLiteral(sub);
                    continue;
                case 'D':
                    dateFormatSymbols = symbols;
                    strArr = weekdays;
                    symbols = selectNumberRule(6, tokenLen);
                    break;
                case 'E':
                    dateFormatSymbols = symbols;
                    strArr = weekdays;
                    symbols = new TextField(7, tokenLen < 4 ? shortWeekdays : strArr);
                    break;
                case 'F':
                    dateFormatSymbols = symbols;
                    strArr = weekdays;
                    symbols = selectNumberRule(8, tokenLen);
                    break;
                case 'G':
                    dateFormatSymbols = symbols;
                    strArr = weekdays;
                    symbols = new TextField(0, ERAs);
                    break;
                case 'H':
                    dateFormatSymbols = symbols;
                    strArr = weekdays;
                    symbols = selectNumberRule(11, tokenLen);
                    break;
                case 'K':
                    dateFormatSymbols = symbols;
                    strArr = weekdays;
                    symbols = selectNumberRule(10, tokenLen);
                    break;
                case 'M':
                    dateFormatSymbols = symbols;
                    strArr = weekdays;
                    if (tokenLen < 4) {
                        if (tokenLen != 3) {
                            if (tokenLen != 2) {
                                symbols = UnpaddedMonthField.INSTANCE;
                                break;
                            }
                            symbols = TwoDigitMonthField.INSTANCE;
                        } else {
                            symbols = new TextField(2, shortMonths);
                        }
                    } else {
                        symbols = new TextField(2, months);
                    }
                case 'S':
                    dateFormatSymbols = symbols;
                    strArr = weekdays;
                    symbols = selectNumberRule(14, tokenLen);
                    break;
                case 'W':
                    dateFormatSymbols = symbols;
                    strArr = weekdays;
                    symbols = selectNumberRule(4, tokenLen);
                    break;
                case 'Z':
                    dateFormatSymbols = symbols;
                    strArr = weekdays;
                    if (tokenLen != 1) {
                        symbols = TimeZoneNumberRule.INSTANCE_COLON;
                        break;
                    }
                    symbols = TimeZoneNumberRule.INSTANCE_NO_COLON;
                case 'a':
                    dateFormatSymbols = symbols;
                    strArr = weekdays;
                    symbols = new TextField(9, AmPmStrings);
                    break;
                case 'd':
                    dateFormatSymbols = symbols;
                    strArr = weekdays;
                    symbols = selectNumberRule(5, tokenLen);
                    break;
                case 'h':
                    dateFormatSymbols = symbols;
                    strArr = weekdays;
                    symbols = new TwelveHourField(selectNumberRule(10, tokenLen));
                    break;
                case 'k':
                    dateFormatSymbols = symbols;
                    strArr = weekdays;
                    symbols = new TwentyFourHourField(selectNumberRule(11, tokenLen));
                    break;
                case 'm':
                    dateFormatSymbols = symbols;
                    strArr = weekdays;
                    symbols = selectNumberRule(12, tokenLen);
                    break;
                case 's':
                    dateFormatSymbols = symbols;
                    strArr = weekdays;
                    symbols = selectNumberRule(13, tokenLen);
                    break;
                case 'w':
                    dateFormatSymbols = symbols;
                    strArr = weekdays;
                    symbols = selectNumberRule(3, tokenLen);
                    break;
                case 'y':
                    dateFormatSymbols = symbols;
                    strArr = weekdays;
                    if (tokenLen != 2) {
                        if (tokenLen >= 4) {
                            i2 = tokenLen;
                        }
                        symbols = selectNumberRule(1, i2);
                        break;
                    }
                    symbols = TwoDigitYearField.INSTANCE;
                case 'z':
                    if (tokenLen < 4) {
                        dateFormatSymbols = symbols;
                        strArr = weekdays;
                        symbols = new TimeZoneNameRule(r0.mTimeZone, r0.mLocale, 0);
                        break;
                    }
                    dateFormatSymbols = symbols;
                    strArr = weekdays;
                    symbols = new TimeZoneNameRule(r0.mTimeZone, r0.mLocale, i);
                default:
                    StringBuilder stringBuilder = new StringBuilder();
                    stringBuilder.append("Illegal pattern component: ");
                    stringBuilder.append(token);
                    throw new IllegalArgumentException(stringBuilder.toString());
            }
            i2 = 0;
            rules.add(symbols);
            i3++;
            symbols = dateFormatSymbols;
            weekdays = strArr;
            i = 1;
        }
        strArr = weekdays;
        return rules;
    }

    protected String parseToken(String pattern, int[] indexRef) {
        int i;
        StringBuilder buf = new StringBuilder();
        int i2 = indexRef[0];
        int length = pattern.length();
        char c = pattern.charAt(i2);
        char c2;
        if ((c < 'A' || c > 'Z') && (c < 'a' || c > 'z')) {
            buf.append('\'');
            i = i2;
            boolean inLiteral = false;
            while (i < length) {
                c2 = pattern.charAt(i);
                if (c2 != '\'') {
                    if (!inLiteral && ((c2 >= 'A' && c2 <= 'Z') || (c2 >= 'a' && c2 <= 'z'))) {
                        i--;
                        break;
                    }
                    buf.append(c2);
                } else if (i + 1 >= length || pattern.charAt(i + 1) != '\'') {
                    inLiteral = !inLiteral;
                } else {
                    i++;
                    buf.append(c2);
                }
                i++;
            }
        } else {
            buf.append(c);
            while (i2 + 1 < length && pattern.charAt(i2 + 1) == c) {
                buf.append(c);
                i2++;
            }
            c2 = c;
            i = i2;
        }
        indexRef[0] = i;
        return buf.toString();
    }

    protected NumberRule selectNumberRule(int field, int padding) {
        switch (padding) {
            case 1:
                return new UnpaddedNumberField(field);
            case 2:
                return new TwoDigitNumberField(field);
            default:
                return new PaddedNumberField(field, padding);
        }
    }

    public StringBuffer format(Object obj, StringBuffer toAppendTo, FieldPosition pos) {
        if (obj instanceof Date) {
            return format((Date) obj, toAppendTo);
        }
        if (obj instanceof Calendar) {
            return format((Calendar) obj, toAppendTo);
        }
        if (obj instanceof Long) {
            return format(((Long) obj).longValue(), toAppendTo);
        }
        String str;
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Unknown class: ");
        if (obj == null) {
            str = "<null>";
        } else {
            str = obj.getClass().getName();
        }
        stringBuilder.append(str);
        throw new IllegalArgumentException(stringBuilder.toString());
    }

    public String format(long millis) {
        Calendar c = newCalendar();
        c.setTimeInMillis(millis);
        return applyRulesToString(c);
    }

    private String applyRulesToString(Calendar c) {
        return applyRules(c, new StringBuffer(this.mMaxLengthEstimate)).toString();
    }

    private GregorianCalendar newCalendar() {
        return new GregorianCalendar(this.mTimeZone, this.mLocale);
    }

    public String format(Date date) {
        Calendar c = newCalendar();
        c.setTime(date);
        return applyRulesToString(c);
    }

    public String format(Calendar calendar) {
        return format(calendar, new StringBuffer(this.mMaxLengthEstimate)).toString();
    }

    public StringBuffer format(long millis, StringBuffer buf) {
        return format(new Date(millis), buf);
    }

    public StringBuffer format(Date date, StringBuffer buf) {
        Calendar c = newCalendar();
        c.setTime(date);
        return applyRules(c, buf);
    }

    public StringBuffer format(Calendar calendar, StringBuffer buf) {
        return applyRules(calendar, buf);
    }

    protected StringBuffer applyRules(Calendar calendar, StringBuffer buf) {
        for (Rule rule : this.mRules) {
            rule.appendTo(buf, calendar);
        }
        return buf;
    }

    public String getPattern() {
        return this.mPattern;
    }

    public TimeZone getTimeZone() {
        return this.mTimeZone;
    }

    public Locale getLocale() {
        return this.mLocale;
    }

    public int getMaxLengthEstimate() {
        return this.mMaxLengthEstimate;
    }

    public boolean equals(Object obj) {
        boolean z = false;
        if (!(obj instanceof FastDatePrinter)) {
            return false;
        }
        FastDatePrinter other = (FastDatePrinter) obj;
        if (this.mPattern.equals(other.mPattern) && this.mTimeZone.equals(other.mTimeZone) && this.mLocale.equals(other.mLocale)) {
            z = true;
        }
        return z;
    }

    public int hashCode() {
        return this.mPattern.hashCode() + (13 * (this.mTimeZone.hashCode() + (this.mLocale.hashCode() * 13)));
    }

    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("FastDatePrinter[");
        stringBuilder.append(this.mPattern);
        stringBuilder.append(",");
        stringBuilder.append(this.mLocale);
        stringBuilder.append(",");
        stringBuilder.append(this.mTimeZone.getID());
        stringBuilder.append("]");
        return stringBuilder.toString();
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        init();
    }

    static String getTimeZoneDisplay(TimeZone tz, boolean daylight, int style, Locale locale) {
        TimeZoneDisplayKey key = new TimeZoneDisplayKey(tz, daylight, style, locale);
        String value = (String) cTimeZoneDisplayCache.get(key);
        if (value != null) {
            return value;
        }
        value = tz.getDisplayName(daylight, style, locale);
        String prior = (String) cTimeZoneDisplayCache.putIfAbsent(key, value);
        if (prior != null) {
            return prior;
        }
        return value;
    }
}
