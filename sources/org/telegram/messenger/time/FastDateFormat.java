package org.telegram.messenger.time;

import java.text.FieldPosition;
import java.text.Format;
import java.text.ParseException;
import java.text.ParsePosition;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class FastDateFormat extends Format implements DateParser, DatePrinter {
    public static final int FULL = 0;
    public static final int LONG = 1;
    public static final int MEDIUM = 2;
    public static final int SHORT = 3;
    private static final FormatCache<FastDateFormat> cache = new FormatCache<FastDateFormat>() {
        /* access modifiers changed from: protected */
        public FastDateFormat createInstance(String pattern, TimeZone timeZone, Locale locale) {
            return new FastDateFormat(pattern, timeZone, locale);
        }
    };
    private static final long serialVersionUID = 2;
    private final FastDateParser parser;
    private final FastDatePrinter printer;

    public static FastDateFormat getInstance() {
        return cache.getInstance();
    }

    public static FastDateFormat getInstance(String pattern) {
        return cache.getInstance(pattern, (TimeZone) null, (Locale) null);
    }

    public static FastDateFormat getInstance(String pattern, TimeZone timeZone) {
        return cache.getInstance(pattern, timeZone, (Locale) null);
    }

    public static FastDateFormat getInstance(String pattern, Locale locale) {
        return cache.getInstance(pattern, (TimeZone) null, locale);
    }

    public static FastDateFormat getInstance(String pattern, TimeZone timeZone, Locale locale) {
        return cache.getInstance(pattern, timeZone, locale);
    }

    public static FastDateFormat getDateInstance(int style) {
        return cache.getDateInstance(style, (TimeZone) null, (Locale) null);
    }

    public static FastDateFormat getDateInstance(int style, Locale locale) {
        return cache.getDateInstance(style, (TimeZone) null, locale);
    }

    public static FastDateFormat getDateInstance(int style, TimeZone timeZone) {
        return cache.getDateInstance(style, timeZone, (Locale) null);
    }

    public static FastDateFormat getDateInstance(int style, TimeZone timeZone, Locale locale) {
        return cache.getDateInstance(style, timeZone, locale);
    }

    public static FastDateFormat getTimeInstance(int style) {
        return cache.getTimeInstance(style, (TimeZone) null, (Locale) null);
    }

    public static FastDateFormat getTimeInstance(int style, Locale locale) {
        return cache.getTimeInstance(style, (TimeZone) null, locale);
    }

    public static FastDateFormat getTimeInstance(int style, TimeZone timeZone) {
        return cache.getTimeInstance(style, timeZone, (Locale) null);
    }

    public static FastDateFormat getTimeInstance(int style, TimeZone timeZone, Locale locale) {
        return cache.getTimeInstance(style, timeZone, locale);
    }

    public static FastDateFormat getDateTimeInstance(int dateStyle, int timeStyle) {
        return cache.getDateTimeInstance(dateStyle, timeStyle, (TimeZone) null, (Locale) null);
    }

    public static FastDateFormat getDateTimeInstance(int dateStyle, int timeStyle, Locale locale) {
        return cache.getDateTimeInstance(dateStyle, timeStyle, (TimeZone) null, locale);
    }

    public static FastDateFormat getDateTimeInstance(int dateStyle, int timeStyle, TimeZone timeZone) {
        return getDateTimeInstance(dateStyle, timeStyle, timeZone, (Locale) null);
    }

    public static FastDateFormat getDateTimeInstance(int dateStyle, int timeStyle, TimeZone timeZone, Locale locale) {
        return cache.getDateTimeInstance(dateStyle, timeStyle, timeZone, locale);
    }

    protected FastDateFormat(String pattern, TimeZone timeZone, Locale locale) {
        this(pattern, timeZone, locale, (Date) null);
    }

    protected FastDateFormat(String pattern, TimeZone timeZone, Locale locale, Date centuryStart) {
        this.printer = new FastDatePrinter(pattern, timeZone, locale);
        this.parser = new FastDateParser(pattern, timeZone, locale, centuryStart);
    }

    public StringBuffer format(Object obj, StringBuffer toAppendTo, FieldPosition pos) {
        return this.printer.format(obj, toAppendTo, pos);
    }

    public String format(long millis) {
        return this.printer.format(millis);
    }

    public String format(Date date) {
        return this.printer.format(date);
    }

    public String format(Calendar calendar) {
        return this.printer.format(calendar);
    }

    public StringBuffer format(long millis, StringBuffer buf) {
        return this.printer.format(millis, buf);
    }

    public StringBuffer format(Date date, StringBuffer buf) {
        return this.printer.format(date, buf);
    }

    public StringBuffer format(Calendar calendar, StringBuffer buf) {
        return this.printer.format(calendar, buf);
    }

    public Date parse(String source) throws ParseException {
        return this.parser.parse(source);
    }

    public Date parse(String source, ParsePosition pos) {
        return this.parser.parse(source, pos);
    }

    public Object parseObject(String source, ParsePosition pos) {
        return this.parser.parseObject(source, pos);
    }

    public String getPattern() {
        return this.printer.getPattern();
    }

    public TimeZone getTimeZone() {
        return this.printer.getTimeZone();
    }

    public Locale getLocale() {
        return this.printer.getLocale();
    }

    public int getMaxLengthEstimate() {
        return this.printer.getMaxLengthEstimate();
    }

    public boolean equals(Object obj) {
        if (!(obj instanceof FastDateFormat)) {
            return false;
        }
        return this.printer.equals(((FastDateFormat) obj).printer);
    }

    public int hashCode() {
        return this.printer.hashCode();
    }

    public String toString() {
        return "FastDateFormat[" + this.printer.getPattern() + "," + this.printer.getLocale() + "," + this.printer.getTimeZone().getID() + "]";
    }

    /* access modifiers changed from: protected */
    public StringBuffer applyRules(Calendar calendar, StringBuffer buf) {
        return this.printer.applyRules(calendar, buf);
    }
}
