package org.telegram.messenger.time;

import j$.util.concurrent.ConcurrentHashMap;
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
import java.util.concurrent.ConcurrentMap;

public class FastDatePrinter implements DatePrinter, Serializable {
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

    private interface NumberRule extends Rule {
        void appendTo(StringBuffer stringBuffer, int i);
    }

    private interface Rule {
        void appendTo(StringBuffer stringBuffer, Calendar calendar);

        int estimateLength();
    }

    protected FastDatePrinter(String pattern, TimeZone timeZone, Locale locale) {
        this.mPattern = pattern;
        this.mTimeZone = timeZone;
        this.mLocale = locale;
        init();
    }

    private void init() {
        List<Rule> rulesList = parsePattern();
        Rule[] ruleArr = (Rule[]) rulesList.toArray(new Rule[rulesList.size()]);
        this.mRules = ruleArr;
        int len = 0;
        int i = ruleArr.length;
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

    /* access modifiers changed from: protected */
    public List<Rule> parsePattern() {
        String[] weekdays;
        DateFormatSymbols symbols;
        Rule rule;
        DateFormatSymbols symbols2 = new DateFormatSymbols(this.mLocale);
        List<Rule> rules = new ArrayList<>();
        String[] ERAs = symbols2.getEras();
        String[] months = symbols2.getMonths();
        String[] shortMonths = symbols2.getShortMonths();
        String[] weekdays2 = symbols2.getWeekdays();
        String[] shortWeekdays = symbols2.getShortWeekdays();
        String[] AmPmStrings = symbols2.getAmPmStrings();
        int length = this.mPattern.length();
        int i = 1;
        int[] indexRef = new int[1];
        int i2 = 0;
        while (true) {
            if (i2 < length) {
                indexRef[0] = i2;
                String token = parseToken(this.mPattern, indexRef);
                int i3 = indexRef[0];
                int tokenLen = token.length();
                if (tokenLen == 0) {
                    DateFormatSymbols dateFormatSymbols = symbols2;
                    String[] strArr = weekdays2;
                } else {
                    int i4 = 4;
                    switch (token.charAt(0)) {
                        case '\'':
                            symbols = symbols2;
                            weekdays = weekdays2;
                            String sub = token.substring(1);
                            if (sub.length() != 1) {
                                rule = new StringLiteral(sub);
                                break;
                            } else {
                                rule = new CharacterLiteral(sub.charAt(0));
                                break;
                            }
                        case 'D':
                            symbols = symbols2;
                            weekdays = weekdays2;
                            rule = selectNumberRule(6, tokenLen);
                            break;
                        case 'E':
                            symbols = symbols2;
                            weekdays = weekdays2;
                            rule = new TextField(7, tokenLen < 4 ? shortWeekdays : weekdays);
                            break;
                        case 'F':
                            symbols = symbols2;
                            weekdays = weekdays2;
                            rule = selectNumberRule(8, tokenLen);
                            break;
                        case 'G':
                            symbols = symbols2;
                            weekdays = weekdays2;
                            rule = new TextField(0, ERAs);
                            break;
                        case 'H':
                            symbols = symbols2;
                            weekdays = weekdays2;
                            rule = selectNumberRule(11, tokenLen);
                            break;
                        case 'K':
                            symbols = symbols2;
                            weekdays = weekdays2;
                            rule = selectNumberRule(10, tokenLen);
                            break;
                        case 'L':
                            symbols = symbols2;
                            weekdays = weekdays2;
                            if (tokenLen < 4) {
                                if (tokenLen != 3) {
                                    if (tokenLen != 2) {
                                        rule = UnpaddedMonthField.INSTANCE;
                                        break;
                                    } else {
                                        rule = TwoDigitMonthField.INSTANCE;
                                        break;
                                    }
                                } else {
                                    rule = new TextField(2, shortMonths);
                                    break;
                                }
                            } else {
                                rule = new TextField(2, months);
                                break;
                            }
                        case 'M':
                            symbols = symbols2;
                            weekdays = weekdays2;
                            if (tokenLen < 4) {
                                if (tokenLen != 3) {
                                    if (tokenLen != 2) {
                                        rule = UnpaddedMonthField.INSTANCE;
                                        break;
                                    } else {
                                        rule = TwoDigitMonthField.INSTANCE;
                                        break;
                                    }
                                } else {
                                    rule = new TextField(2, shortMonths);
                                    break;
                                }
                            } else {
                                rule = new TextField(2, months);
                                break;
                            }
                        case 'S':
                            symbols = symbols2;
                            weekdays = weekdays2;
                            rule = selectNumberRule(14, tokenLen);
                            break;
                        case 'W':
                            symbols = symbols2;
                            weekdays = weekdays2;
                            rule = selectNumberRule(4, tokenLen);
                            break;
                        case 'Z':
                            symbols = symbols2;
                            weekdays = weekdays2;
                            if (tokenLen != 1) {
                                rule = TimeZoneNumberRule.INSTANCE_COLON;
                                break;
                            } else {
                                rule = TimeZoneNumberRule.INSTANCE_NO_COLON;
                                break;
                            }
                        case 'a':
                            symbols = symbols2;
                            weekdays = weekdays2;
                            rule = new TextField(9, AmPmStrings);
                            break;
                        case 'd':
                            symbols = symbols2;
                            weekdays = weekdays2;
                            rule = selectNumberRule(5, tokenLen);
                            break;
                        case 'h':
                            symbols = symbols2;
                            weekdays = weekdays2;
                            rule = new TwelveHourField(selectNumberRule(10, tokenLen));
                            break;
                        case 'k':
                            symbols = symbols2;
                            weekdays = weekdays2;
                            rule = new TwentyFourHourField(selectNumberRule(11, tokenLen));
                            break;
                        case 'm':
                            symbols = symbols2;
                            weekdays = weekdays2;
                            rule = selectNumberRule(12, tokenLen);
                            break;
                        case 's':
                            symbols = symbols2;
                            weekdays = weekdays2;
                            rule = selectNumberRule(13, tokenLen);
                            break;
                        case 'w':
                            symbols = symbols2;
                            weekdays = weekdays2;
                            rule = selectNumberRule(3, tokenLen);
                            break;
                        case 'y':
                            symbols = symbols2;
                            weekdays = weekdays2;
                            if (tokenLen != 2) {
                                if (tokenLen >= 4) {
                                    i4 = tokenLen;
                                }
                                rule = selectNumberRule(1, i4);
                                break;
                            } else {
                                rule = TwoDigitYearField.INSTANCE;
                                break;
                            }
                        case 'z':
                            if (tokenLen < 4) {
                                symbols = symbols2;
                                weekdays = weekdays2;
                                rule = new TimeZoneNameRule(this.mTimeZone, this.mLocale, 0);
                                break;
                            } else {
                                symbols = symbols2;
                                weekdays = weekdays2;
                                rule = new TimeZoneNameRule(this.mTimeZone, this.mLocale, i);
                                break;
                            }
                        default:
                            DateFormatSymbols dateFormatSymbols2 = symbols2;
                            String[] strArr2 = weekdays2;
                            throw new IllegalArgumentException("Illegal pattern component: " + token);
                    }
                    rules.add(rule);
                    i2 = i3 + 1;
                    symbols2 = symbols;
                    weekdays2 = weekdays;
                    i = 1;
                }
            } else {
                String[] strArr3 = weekdays2;
            }
        }
        return rules;
    }

    /* access modifiers changed from: protected */
    /* JADX WARNING: Code restructure failed: missing block: B:30:0x0068, code lost:
        r2 = r2 - 1;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public java.lang.String parseToken(java.lang.String r14, int[] r15) {
        /*
            r13 = this;
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            r1 = 0
            r2 = r15[r1]
            int r3 = r14.length()
            char r4 = r14.charAt(r2)
            r5 = 90
            r6 = 65
            if (r4 < r6) goto L_0x0018
            if (r4 <= r5) goto L_0x0020
        L_0x0018:
            r7 = 122(0x7a, float:1.71E-43)
            r8 = 97
            if (r4 < r8) goto L_0x0036
            if (r4 > r7) goto L_0x0036
        L_0x0020:
            r0.append(r4)
        L_0x0023:
            int r5 = r2 + 1
            if (r5 >= r3) goto L_0x0070
            int r5 = r2 + 1
            char r5 = r14.charAt(r5)
            if (r5 != r4) goto L_0x0070
            r0.append(r4)
            int r2 = r2 + 1
            goto L_0x0023
        L_0x0036:
            r9 = 39
            r0.append(r9)
            r10 = 0
        L_0x003c:
            if (r2 >= r3) goto L_0x0070
            char r4 = r14.charAt(r2)
            r11 = 1
            if (r4 != r9) goto L_0x005e
            int r12 = r2 + 1
            if (r12 >= r3) goto L_0x0057
            int r12 = r2 + 1
            char r12 = r14.charAt(r12)
            if (r12 != r9) goto L_0x0057
            int r2 = r2 + 1
            r0.append(r4)
            goto L_0x006e
        L_0x0057:
            if (r10 != 0) goto L_0x005b
            r12 = 1
            goto L_0x005c
        L_0x005b:
            r12 = 0
        L_0x005c:
            r10 = r12
            goto L_0x006e
        L_0x005e:
            if (r10 != 0) goto L_0x006b
            if (r4 < r6) goto L_0x0064
            if (r4 <= r5) goto L_0x0068
        L_0x0064:
            if (r4 < r8) goto L_0x006b
            if (r4 > r7) goto L_0x006b
        L_0x0068:
            int r2 = r2 + -1
            goto L_0x0070
        L_0x006b:
            r0.append(r4)
        L_0x006e:
            int r2 = r2 + r11
            goto L_0x003c
        L_0x0070:
            r15[r1] = r2
            java.lang.String r1 = r0.toString()
            return r1
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.time.FastDatePrinter.parseToken(java.lang.String, int[]):java.lang.String");
    }

    /* access modifiers changed from: protected */
    public NumberRule selectNumberRule(int field, int padding) {
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
        StringBuilder sb = new StringBuilder();
        sb.append("Unknown class: ");
        sb.append(obj == null ? "<null>" : obj.getClass().getName());
        throw new IllegalArgumentException(sb.toString());
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

    /* access modifiers changed from: protected */
    public StringBuffer applyRules(Calendar calendar, StringBuffer buf) {
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
        if (!(obj instanceof FastDatePrinter)) {
            return false;
        }
        FastDatePrinter other = (FastDatePrinter) obj;
        if (!this.mPattern.equals(other.mPattern) || !this.mTimeZone.equals(other.mTimeZone) || !this.mLocale.equals(other.mLocale)) {
            return false;
        }
        return true;
    }

    public int hashCode() {
        return this.mPattern.hashCode() + ((this.mTimeZone.hashCode() + (this.mLocale.hashCode() * 13)) * 13);
    }

    public String toString() {
        return "FastDatePrinter[" + this.mPattern + "," + this.mLocale + "," + this.mTimeZone.getID() + "]";
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        init();
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

    private static class PaddedNumberField implements NumberRule {
        private final int mField;
        private final int mSize;

        PaddedNumberField(int field, int size) {
            if (size >= 3) {
                this.mField = field;
                this.mSize = size;
                return;
            }
            throw new IllegalArgumentException();
        }

        public int estimateLength() {
            return 4;
        }

        public void appendTo(StringBuffer buffer, Calendar calendar) {
            appendTo(buffer, calendar.get(this.mField));
        }

        public final void appendTo(StringBuffer buffer, int value) {
            int digits;
            if (value < 100) {
                int i = this.mSize;
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
            } else {
                if (value < 1000) {
                    digits = 3;
                } else {
                    digits = Integer.toString(value).length();
                }
                int i2 = this.mSize;
                while (true) {
                    i2--;
                    if (i2 >= digits) {
                        buffer.append('0');
                    } else {
                        buffer.append(Integer.toString(value));
                        return;
                    }
                }
            }
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

    static String getTimeZoneDisplay(TimeZone tz, boolean daylight, int style, Locale locale) {
        TimeZoneDisplayKey key = new TimeZoneDisplayKey(tz, daylight, style, locale);
        ConcurrentMap<TimeZoneDisplayKey, String> concurrentMap = cTimeZoneDisplayCache;
        String value = (String) concurrentMap.get(key);
        if (value != null) {
            return value;
        }
        String value2 = tz.getDisplayName(daylight, style, locale);
        String prior = concurrentMap.putIfAbsent(key, value2);
        if (prior != null) {
            return prior;
        }
        return value2;
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
            int minutes = (offset / 60000) - (hours * 60);
            buffer.append((char) ((minutes / 10) + 48));
            buffer.append((char) ((minutes % 10) + 48));
        }
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
            if (this == obj) {
                return true;
            }
            if (!(obj instanceof TimeZoneDisplayKey)) {
                return false;
            }
            TimeZoneDisplayKey other = (TimeZoneDisplayKey) obj;
            if (!this.mTimeZone.equals(other.mTimeZone) || this.mStyle != other.mStyle || !this.mLocale.equals(other.mLocale)) {
                return false;
            }
            return true;
        }
    }
}
