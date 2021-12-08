package j$.time.format;

import j$.time.DateTimeException;
import j$.time.Period;
import j$.time.ZoneId;
import j$.time.chrono.Chronology;
import j$.time.chrono.IsoChronology;
import j$.time.format.DateTimeFormatterBuilder;
import j$.time.temporal.ChronoField;
import j$.time.temporal.IsoFields;
import j$.time.temporal.TemporalAccessor;
import j$.time.temporal.TemporalField;
import j$.time.temporal.TemporalQuery;
import j$.util.Objects;
import java.io.IOException;
import java.text.FieldPosition;
import java.text.Format;
import java.text.ParseException;
import java.text.ParsePosition;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

public final class DateTimeFormatter {
    public static final DateTimeFormatter BASIC_ISO_DATE = new DateTimeFormatterBuilder().parseCaseInsensitive().appendValue(ChronoField.YEAR, 4).appendValue(ChronoField.MONTH_OF_YEAR, 2).appendValue(ChronoField.DAY_OF_MONTH, 2).optionalStart().appendOffset("+HHMMss", "Z").toFormatter(ResolverStyle.STRICT, IsoChronology.INSTANCE);
    public static final DateTimeFormatter ISO_DATE;
    public static final DateTimeFormatter ISO_DATE_TIME;
    public static final DateTimeFormatter ISO_INSTANT = new DateTimeFormatterBuilder().parseCaseInsensitive().appendInstant().toFormatter(ResolverStyle.STRICT, (Chronology) null);
    public static final DateTimeFormatter ISO_LOCAL_DATE;
    public static final DateTimeFormatter ISO_LOCAL_DATE_TIME;
    public static final DateTimeFormatter ISO_LOCAL_TIME;
    public static final DateTimeFormatter ISO_OFFSET_DATE;
    public static final DateTimeFormatter ISO_OFFSET_DATE_TIME;
    public static final DateTimeFormatter ISO_OFFSET_TIME;
    public static final DateTimeFormatter ISO_ORDINAL_DATE = new DateTimeFormatterBuilder().parseCaseInsensitive().appendValue(ChronoField.YEAR, 4, 10, SignStyle.EXCEEDS_PAD).appendLiteral('-').appendValue(ChronoField.DAY_OF_YEAR, 3).optionalStart().appendOffsetId().toFormatter(ResolverStyle.STRICT, IsoChronology.INSTANCE);
    public static final DateTimeFormatter ISO_TIME;
    public static final DateTimeFormatter ISO_WEEK_DATE = new DateTimeFormatterBuilder().parseCaseInsensitive().appendValue(IsoFields.WEEK_BASED_YEAR, 4, 10, SignStyle.EXCEEDS_PAD).appendLiteral("-W").appendValue(IsoFields.WEEK_OF_WEEK_BASED_YEAR, 2).appendLiteral('-').appendValue(ChronoField.DAY_OF_WEEK, 1).optionalStart().appendOffsetId().toFormatter(ResolverStyle.STRICT, IsoChronology.INSTANCE);
    public static final DateTimeFormatter ISO_ZONED_DATE_TIME;
    private static final TemporalQuery<Period> PARSED_EXCESS_DAYS = DateTimeFormatter$$ExternalSyntheticLambda0.INSTANCE;
    private static final TemporalQuery<Boolean> PARSED_LEAP_SECOND = DateTimeFormatter$$ExternalSyntheticLambda1.INSTANCE;
    public static final DateTimeFormatter RFC_1123_DATE_TIME;
    private final Chronology chrono;
    private final DecimalStyle decimalStyle;
    private final Locale locale;
    private final DateTimeFormatterBuilder.CompositePrinterParser printerParser;
    /* access modifiers changed from: private */
    public final Set<TemporalField> resolverFields;
    /* access modifiers changed from: private */
    public final ResolverStyle resolverStyle;
    private final ZoneId zone;

    public static DateTimeFormatter ofPattern(String pattern) {
        return new DateTimeFormatterBuilder().appendPattern(pattern).toFormatter();
    }

    public static DateTimeFormatter ofPattern(String pattern, Locale locale2) {
        return new DateTimeFormatterBuilder().appendPattern(pattern).toFormatter(locale2);
    }

    public static DateTimeFormatter ofLocalizedDate(FormatStyle dateStyle) {
        Objects.requireNonNull(dateStyle, "dateStyle");
        return new DateTimeFormatterBuilder().appendLocalized(dateStyle, (FormatStyle) null).toFormatter(ResolverStyle.SMART, IsoChronology.INSTANCE);
    }

    public static DateTimeFormatter ofLocalizedTime(FormatStyle timeStyle) {
        Objects.requireNonNull(timeStyle, "timeStyle");
        return new DateTimeFormatterBuilder().appendLocalized((FormatStyle) null, timeStyle).toFormatter(ResolverStyle.SMART, IsoChronology.INSTANCE);
    }

    public static DateTimeFormatter ofLocalizedDateTime(FormatStyle dateTimeStyle) {
        Objects.requireNonNull(dateTimeStyle, "dateTimeStyle");
        return new DateTimeFormatterBuilder().appendLocalized(dateTimeStyle, dateTimeStyle).toFormatter(ResolverStyle.SMART, IsoChronology.INSTANCE);
    }

    public static DateTimeFormatter ofLocalizedDateTime(FormatStyle dateStyle, FormatStyle timeStyle) {
        Objects.requireNonNull(dateStyle, "dateStyle");
        Objects.requireNonNull(timeStyle, "timeStyle");
        return new DateTimeFormatterBuilder().appendLocalized(dateStyle, timeStyle).toFormatter(ResolverStyle.SMART, IsoChronology.INSTANCE);
    }

    static {
        DateTimeFormatter formatter = new DateTimeFormatterBuilder().appendValue(ChronoField.YEAR, 4, 10, SignStyle.EXCEEDS_PAD).appendLiteral('-').appendValue(ChronoField.MONTH_OF_YEAR, 2).appendLiteral('-').appendValue(ChronoField.DAY_OF_MONTH, 2).toFormatter(ResolverStyle.STRICT, IsoChronology.INSTANCE);
        ISO_LOCAL_DATE = formatter;
        ISO_OFFSET_DATE = new DateTimeFormatterBuilder().parseCaseInsensitive().append(formatter).appendOffsetId().toFormatter(ResolverStyle.STRICT, IsoChronology.INSTANCE);
        ISO_DATE = new DateTimeFormatterBuilder().parseCaseInsensitive().append(formatter).optionalStart().appendOffsetId().toFormatter(ResolverStyle.STRICT, IsoChronology.INSTANCE);
        DateTimeFormatter formatter2 = new DateTimeFormatterBuilder().appendValue(ChronoField.HOUR_OF_DAY, 2).appendLiteral(':').appendValue(ChronoField.MINUTE_OF_HOUR, 2).optionalStart().appendLiteral(':').appendValue(ChronoField.SECOND_OF_MINUTE, 2).optionalStart().appendFraction(ChronoField.NANO_OF_SECOND, 0, 9, true).toFormatter(ResolverStyle.STRICT, (Chronology) null);
        ISO_LOCAL_TIME = formatter2;
        ISO_OFFSET_TIME = new DateTimeFormatterBuilder().parseCaseInsensitive().append(formatter2).appendOffsetId().toFormatter(ResolverStyle.STRICT, (Chronology) null);
        ISO_TIME = new DateTimeFormatterBuilder().parseCaseInsensitive().append(formatter2).optionalStart().appendOffsetId().toFormatter(ResolverStyle.STRICT, (Chronology) null);
        DateTimeFormatter formatter3 = new DateTimeFormatterBuilder().parseCaseInsensitive().append(formatter).appendLiteral('T').append(formatter2).toFormatter(ResolverStyle.STRICT, IsoChronology.INSTANCE);
        ISO_LOCAL_DATE_TIME = formatter3;
        DateTimeFormatter formatter4 = new DateTimeFormatterBuilder().parseCaseInsensitive().append(formatter3).appendOffsetId().toFormatter(ResolverStyle.STRICT, IsoChronology.INSTANCE);
        ISO_OFFSET_DATE_TIME = formatter4;
        ISO_ZONED_DATE_TIME = new DateTimeFormatterBuilder().append(formatter4).optionalStart().appendLiteral('[').parseCaseSensitive().appendZoneRegionId().appendLiteral(']').toFormatter(ResolverStyle.STRICT, IsoChronology.INSTANCE);
        ISO_DATE_TIME = new DateTimeFormatterBuilder().append(formatter3).optionalStart().appendOffsetId().optionalStart().appendLiteral('[').parseCaseSensitive().appendZoneRegionId().appendLiteral(']').toFormatter(ResolverStyle.STRICT, IsoChronology.INSTANCE);
        Map<Long, String> dow = new HashMap<>();
        dow.put(1L, "Mon");
        dow.put(2L, "Tue");
        dow.put(3L, "Wed");
        dow.put(4L, "Thu");
        dow.put(5L, "Fri");
        dow.put(6L, "Sat");
        dow.put(7L, "Sun");
        Map<Long, String> moy = new HashMap<>();
        moy.put(1L, "Jan");
        moy.put(2L, "Feb");
        moy.put(3L, "Mar");
        moy.put(4L, "Apr");
        moy.put(5L, "May");
        moy.put(6L, "Jun");
        moy.put(7L, "Jul");
        moy.put(8L, "Aug");
        moy.put(9L, "Sep");
        moy.put(10L, "Oct");
        moy.put(11L, "Nov");
        moy.put(12L, "Dec");
        RFC_1123_DATE_TIME = new DateTimeFormatterBuilder().parseCaseInsensitive().parseLenient().optionalStart().appendText((TemporalField) ChronoField.DAY_OF_WEEK, dow).appendLiteral(", ").optionalEnd().appendValue(ChronoField.DAY_OF_MONTH, 1, 2, SignStyle.NOT_NEGATIVE).appendLiteral(' ').appendText((TemporalField) ChronoField.MONTH_OF_YEAR, moy).appendLiteral(' ').appendValue(ChronoField.YEAR, 4).appendLiteral(' ').appendValue(ChronoField.HOUR_OF_DAY, 2).appendLiteral(':').appendValue(ChronoField.MINUTE_OF_HOUR, 2).optionalStart().appendLiteral(':').appendValue(ChronoField.SECOND_OF_MINUTE, 2).optionalEnd().appendLiteral(' ').appendOffset("+HHMM", "GMT").toFormatter(ResolverStyle.SMART, IsoChronology.INSTANCE);
    }

    public static final TemporalQuery<Period> parsedExcessDays() {
        return PARSED_EXCESS_DAYS;
    }

    static /* synthetic */ Period lambda$static$0(TemporalAccessor t) {
        if (t instanceof Parsed) {
            return ((Parsed) t).excessDays;
        }
        return Period.ZERO;
    }

    public static final TemporalQuery<Boolean> parsedLeapSecond() {
        return PARSED_LEAP_SECOND;
    }

    static /* synthetic */ Boolean lambda$static$1(TemporalAccessor t) {
        if (t instanceof Parsed) {
            return Boolean.valueOf(((Parsed) t).leapSecond);
        }
        return Boolean.FALSE;
    }

    DateTimeFormatter(DateTimeFormatterBuilder.CompositePrinterParser printerParser2, Locale locale2, DecimalStyle decimalStyle2, ResolverStyle resolverStyle2, Set<TemporalField> resolverFields2, Chronology chrono2, ZoneId zone2) {
        this.printerParser = (DateTimeFormatterBuilder.CompositePrinterParser) Objects.requireNonNull(printerParser2, "printerParser");
        this.resolverFields = resolverFields2;
        this.locale = (Locale) Objects.requireNonNull(locale2, "locale");
        this.decimalStyle = (DecimalStyle) Objects.requireNonNull(decimalStyle2, "decimalStyle");
        this.resolverStyle = (ResolverStyle) Objects.requireNonNull(resolverStyle2, "resolverStyle");
        this.chrono = chrono2;
        this.zone = zone2;
    }

    public Locale getLocale() {
        return this.locale;
    }

    public DateTimeFormatter withLocale(Locale locale2) {
        if (this.locale.equals(locale2)) {
            return this;
        }
        return new DateTimeFormatter(this.printerParser, locale2, this.decimalStyle, this.resolverStyle, this.resolverFields, this.chrono, this.zone);
    }

    public DecimalStyle getDecimalStyle() {
        return this.decimalStyle;
    }

    public DateTimeFormatter withDecimalStyle(DecimalStyle decimalStyle2) {
        if (this.decimalStyle.equals(decimalStyle2)) {
            return this;
        }
        return new DateTimeFormatter(this.printerParser, this.locale, decimalStyle2, this.resolverStyle, this.resolverFields, this.chrono, this.zone);
    }

    public Chronology getChronology() {
        return this.chrono;
    }

    public DateTimeFormatter withChronology(Chronology chrono2) {
        if (Objects.equals(this.chrono, chrono2)) {
            return this;
        }
        return new DateTimeFormatter(this.printerParser, this.locale, this.decimalStyle, this.resolverStyle, this.resolverFields, chrono2, this.zone);
    }

    public ZoneId getZone() {
        return this.zone;
    }

    public DateTimeFormatter withZone(ZoneId zone2) {
        if (Objects.equals(this.zone, zone2)) {
            return this;
        }
        return new DateTimeFormatter(this.printerParser, this.locale, this.decimalStyle, this.resolverStyle, this.resolverFields, this.chrono, zone2);
    }

    public ResolverStyle getResolverStyle() {
        return this.resolverStyle;
    }

    public DateTimeFormatter withResolverStyle(ResolverStyle resolverStyle2) {
        Objects.requireNonNull(resolverStyle2, "resolverStyle");
        if (Objects.equals(this.resolverStyle, resolverStyle2)) {
            return this;
        }
        return new DateTimeFormatter(this.printerParser, this.locale, this.decimalStyle, resolverStyle2, this.resolverFields, this.chrono, this.zone);
    }

    public Set<TemporalField> getResolverFields() {
        return this.resolverFields;
    }

    public DateTimeFormatter withResolverFields(TemporalField... resolverFields2) {
        Set<java.time.temporal.TemporalField> fields = null;
        if (resolverFields2 != null) {
            fields = Collections.unmodifiableSet(new HashSet(Arrays.asList(resolverFields2)));
        }
        if (Objects.equals(this.resolverFields, fields)) {
            return this;
        }
        return new DateTimeFormatter(this.printerParser, this.locale, this.decimalStyle, this.resolverStyle, fields, this.chrono, this.zone);
    }

    public DateTimeFormatter withResolverFields(Set<TemporalField> resolverFields2) {
        if (Objects.equals(this.resolverFields, resolverFields2)) {
            return this;
        }
        if (resolverFields2 != null) {
            resolverFields2 = Collections.unmodifiableSet(new HashSet(resolverFields2));
        }
        return new DateTimeFormatter(this.printerParser, this.locale, this.decimalStyle, this.resolverStyle, resolverFields2, this.chrono, this.zone);
    }

    public String format(TemporalAccessor temporal) {
        StringBuilder buf = new StringBuilder(32);
        formatTo(temporal, buf);
        return buf.toString();
    }

    public void formatTo(TemporalAccessor temporal, Appendable appendable) {
        Objects.requireNonNull(temporal, "temporal");
        Objects.requireNonNull(appendable, "appendable");
        try {
            DateTimePrintContext context = new DateTimePrintContext(temporal, this);
            if (appendable instanceof StringBuilder) {
                this.printerParser.format(context, (StringBuilder) appendable);
                return;
            }
            StringBuilder buf = new StringBuilder(32);
            this.printerParser.format(context, buf);
            appendable.append(buf);
        } catch (IOException ex) {
            throw new DateTimeException(ex.getMessage(), ex);
        }
    }

    public TemporalAccessor parse(CharSequence text) {
        Objects.requireNonNull(text, "text");
        try {
            return parseResolved0(text, (ParsePosition) null);
        } catch (DateTimeParseException ex) {
            throw ex;
        } catch (RuntimeException ex2) {
            throw createError(text, ex2);
        }
    }

    public TemporalAccessor parse(CharSequence text, ParsePosition position) {
        Objects.requireNonNull(text, "text");
        Objects.requireNonNull(position, "position");
        try {
            return parseResolved0(text, position);
        } catch (DateTimeParseException | IndexOutOfBoundsException ex) {
            throw ex;
        } catch (RuntimeException ex2) {
            throw createError(text, ex2);
        }
    }

    public <T> T parse(CharSequence text, TemporalQuery<T> temporalQuery) {
        Objects.requireNonNull(text, "text");
        Objects.requireNonNull(temporalQuery, "query");
        try {
            return parseResolved0(text, (ParsePosition) null).query(temporalQuery);
        } catch (DateTimeParseException ex) {
            throw ex;
        } catch (RuntimeException ex2) {
            throw createError(text, ex2);
        }
    }

    /* JADX WARNING: Code restructure failed: missing block: B:14:0x0024, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:15:0x0025, code lost:
        r1 = r5;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:16:0x0027, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:17:0x0028, code lost:
        r1 = r5;
     */
    /* JADX WARNING: Failed to process nested try/catch */
    /* JADX WARNING: Removed duplicated region for block: B:16:0x0027 A[ExcHandler: DateTimeParseException (e j$.time.format.DateTimeParseException), Splitter:B:7:0x0017] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public j$.time.temporal.TemporalAccessor parseBest(java.lang.CharSequence r6, j$.time.temporal.TemporalQuery<?>... r7) {
        /*
            r5 = this;
            java.lang.String r0 = "text"
            j$.util.Objects.requireNonNull(r6, (java.lang.String) r0)
            java.lang.String r0 = "queries"
            j$.util.Objects.requireNonNull(r7, (java.lang.String) r0)
            int r0 = r7.length
            r1 = 2
            if (r0 < r1) goto L_0x0042
            r0 = 0
            j$.time.temporal.TemporalAccessor r0 = r5.parseResolved0(r6, r0)     // Catch:{ DateTimeParseException -> 0x003f, RuntimeException -> 0x0038 }
            int r1 = r7.length     // Catch:{ DateTimeParseException -> 0x003f, RuntimeException -> 0x0038 }
            r2 = 0
        L_0x0015:
            if (r2 >= r1) goto L_0x002a
            r3 = r7[r2]     // Catch:{ DateTimeParseException -> 0x0027, RuntimeException -> 0x0024 }
            java.lang.Object r4 = r0.query(r3)     // Catch:{ RuntimeException -> 0x0020, DateTimeParseException -> 0x0027 }
            j$.time.temporal.TemporalAccessor r4 = (j$.time.temporal.TemporalAccessor) r4     // Catch:{ RuntimeException -> 0x0020, DateTimeParseException -> 0x0027 }
            return r4
        L_0x0020:
            r4 = move-exception
            int r2 = r2 + 1
            goto L_0x0015
        L_0x0024:
            r0 = move-exception
            r1 = r5
            goto L_0x003a
        L_0x0027:
            r0 = move-exception
            r1 = r5
            goto L_0x0041
        L_0x002a:
            j$.time.DateTimeException r1 = new j$.time.DateTimeException     // Catch:{ DateTimeParseException -> 0x003f, RuntimeException -> 0x0038 }
            java.lang.String r2 = "Unable to convert parsed text using any of the specified queries"
            r1.<init>(r2)     // Catch:{ DateTimeParseException -> 0x003f, RuntimeException -> 0x0038 }
            throw r1     // Catch:{ DateTimeParseException -> 0x0035, RuntimeException -> 0x0032 }
        L_0x0032:
            r0 = move-exception
            r1 = r5
            goto L_0x003a
        L_0x0035:
            r0 = move-exception
            r1 = r5
            goto L_0x0041
        L_0x0038:
            r0 = move-exception
            r1 = r5
        L_0x003a:
            j$.time.format.DateTimeParseException r2 = r1.createError(r6, r0)
            throw r2
        L_0x003f:
            r0 = move-exception
            r1 = r5
        L_0x0041:
            throw r0
        L_0x0042:
            java.lang.IllegalArgumentException r0 = new java.lang.IllegalArgumentException
            java.lang.String r1 = "At least two queries must be specified"
            r0.<init>(r1)
            goto L_0x004b
        L_0x004a:
            throw r0
        L_0x004b:
            goto L_0x004a
        */
        throw new UnsupportedOperationException("Method not decompiled: j$.time.format.DateTimeFormatter.parseBest(java.lang.CharSequence, j$.time.temporal.TemporalQuery[]):j$.time.temporal.TemporalAccessor");
    }

    private DateTimeParseException createError(CharSequence text, RuntimeException ex) {
        String abbr;
        if (text.length() > 64) {
            abbr = text.subSequence(0, 64).toString() + "...";
        } else {
            abbr = text.toString();
        }
        return new DateTimeParseException("Text '" + abbr + "' could not be parsed: " + ex.getMessage(), text, 0, ex);
    }

    /* access modifiers changed from: private */
    public TemporalAccessor parseResolved0(CharSequence text, ParsePosition position) {
        String abbr;
        ParsePosition pos = position != null ? position : new ParsePosition(0);
        DateTimeParseContext context = parseUnresolved0(text, pos);
        if (context != null && pos.getErrorIndex() < 0 && (position != null || pos.getIndex() >= text.length())) {
            return context.toResolved(this.resolverStyle, this.resolverFields);
        }
        if (text.length() > 64) {
            abbr = text.subSequence(0, 64).toString() + "...";
        } else {
            abbr = text.toString();
        }
        if (pos.getErrorIndex() >= 0) {
            throw new DateTimeParseException("Text '" + abbr + "' could not be parsed at index " + pos.getErrorIndex(), text, pos.getErrorIndex());
        }
        throw new DateTimeParseException("Text '" + abbr + "' could not be parsed, unparsed text found at index " + pos.getIndex(), text, pos.getIndex());
    }

    public TemporalAccessor parseUnresolved(CharSequence text, ParsePosition position) {
        DateTimeParseContext context = parseUnresolved0(text, position);
        if (context == null) {
            return null;
        }
        return context.toUnresolved();
    }

    /* access modifiers changed from: private */
    public DateTimeParseContext parseUnresolved0(CharSequence text, ParsePosition position) {
        Objects.requireNonNull(text, "text");
        Objects.requireNonNull(position, "position");
        DateTimeParseContext context = new DateTimeParseContext(this);
        int pos = this.printerParser.parse(context, text, position.getIndex());
        if (pos < 0) {
            position.setErrorIndex(pos ^ -1);
            return null;
        }
        position.setIndex(pos);
        return context;
    }

    /* access modifiers changed from: package-private */
    public DateTimeFormatterBuilder.CompositePrinterParser toPrinterParser(boolean optional) {
        return this.printerParser.withOptional(optional);
    }

    public Format toFormat() {
        return new ClassicFormat(this, (TemporalQuery<?>) null);
    }

    public Format toFormat(TemporalQuery<?> temporalQuery) {
        Objects.requireNonNull(temporalQuery, "parseQuery");
        return new ClassicFormat(this, temporalQuery);
    }

    public String toString() {
        String pattern = this.printerParser.toString();
        return pattern.startsWith("[") ? pattern : pattern.substring(1, pattern.length() - 1);
    }

    static class ClassicFormat extends Format {
        private final DateTimeFormatter formatter;
        private final TemporalQuery<?> parseType;

        public ClassicFormat(DateTimeFormatter formatter2, TemporalQuery<?> temporalQuery) {
            this.formatter = formatter2;
            this.parseType = temporalQuery;
        }

        public StringBuffer format(Object obj, StringBuffer toAppendTo, FieldPosition pos) {
            Objects.requireNonNull(obj, "obj");
            Objects.requireNonNull(toAppendTo, "toAppendTo");
            Objects.requireNonNull(pos, "pos");
            if (obj instanceof TemporalAccessor) {
                pos.setBeginIndex(0);
                pos.setEndIndex(0);
                try {
                    this.formatter.formatTo((TemporalAccessor) obj, toAppendTo);
                    return toAppendTo;
                } catch (RuntimeException ex) {
                    throw new IllegalArgumentException(ex.getMessage(), ex);
                }
            } else {
                throw new IllegalArgumentException("Format target must implement TemporalAccessor");
            }
        }

        public Object parseObject(String text) {
            Objects.requireNonNull(text, "text");
            try {
                TemporalQuery<?> temporalQuery = this.parseType;
                if (temporalQuery == null) {
                    return this.formatter.parseResolved0(text, (ParsePosition) null);
                }
                return this.formatter.parse((CharSequence) text, temporalQuery);
            } catch (DateTimeParseException ex) {
                throw new ParseException(ex.getMessage(), ex.getErrorIndex());
            } catch (RuntimeException ex2) {
                throw ((ParseException) new ParseException(ex2.getMessage(), 0).initCause(ex2));
            }
        }

        public Object parseObject(String text, ParsePosition pos) {
            Objects.requireNonNull(text, "text");
            try {
                DateTimeParseContext context = this.formatter.parseUnresolved0(text, pos);
                if (context == null) {
                    if (pos.getErrorIndex() < 0) {
                        pos.setErrorIndex(0);
                    }
                    return null;
                }
                try {
                    TemporalAccessor resolved = context.toResolved(this.formatter.resolverStyle, this.formatter.resolverFields);
                    TemporalQuery<?> temporalQuery = this.parseType;
                    if (temporalQuery == null) {
                        return resolved;
                    }
                    return resolved.query(temporalQuery);
                } catch (RuntimeException e) {
                    pos.setErrorIndex(0);
                    return null;
                }
            } catch (IndexOutOfBoundsException e2) {
                if (pos.getErrorIndex() < 0) {
                    pos.setErrorIndex(0);
                }
                return null;
            }
        }
    }
}
