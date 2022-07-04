package j$.time.format;

import j$.time.Clock$TickClock$$ExternalSyntheticBackport0;
import j$.time.DateTimeException;
import j$.time.Duration$$ExternalSyntheticBackport0;
import j$.time.Duration$$ExternalSyntheticBackport1;
import j$.time.Instant;
import j$.time.LocalDate;
import j$.time.LocalDate$$ExternalSyntheticBackport0;
import j$.time.LocalDateTime;
import j$.time.ZoneId;
import j$.time.ZoneOffset;
import j$.time.chrono.ChronoLocalDate;
import j$.time.chrono.Chronology;
import j$.time.chrono.IsoChronology;
import j$.time.format.DateTimeTextProvider;
import j$.time.temporal.ChronoField;
import j$.time.temporal.IsoFields;
import j$.time.temporal.TemporalAccessor;
import j$.time.temporal.TemporalField;
import j$.time.temporal.TemporalQueries;
import j$.time.temporal.TemporalQuery;
import j$.time.temporal.ValueRange;
import j$.time.temporal.WeekFields;
import j$.time.zone.ZoneRulesProvider;
import j$.util.Objects;
import j$.util.concurrent.ConcurrentHashMap;
import java.lang.ref.SoftReference;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.text.DateFormat;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentMap;

public final class DateTimeFormatterBuilder {
    private static final Map<Character, TemporalField> FIELD_MAP;
    static final Comparator<String> LENGTH_SORT = new Comparator<String>() {
        public int compare(String str1, String str2) {
            return str1.length() == str2.length() ? str1.compareTo(str2) : str1.length() - str2.length();
        }
    };
    private static final TemporalQuery<ZoneId> QUERY_REGION_ONLY = DateTimeFormatterBuilder$$ExternalSyntheticLambda0.INSTANCE;
    private DateTimeFormatterBuilder active;
    private final boolean optional;
    private char padNextChar;
    private int padNextWidth;
    private final DateTimeFormatterBuilder parent;
    private final List<DateTimePrinterParser> printerParsers;
    private int valueParserIndex;

    interface DateTimePrinterParser {
        boolean format(DateTimePrintContext dateTimePrintContext, StringBuilder sb);

        int parse(DateTimeParseContext dateTimeParseContext, CharSequence charSequence, int i);
    }

    static {
        HashMap hashMap = new HashMap();
        FIELD_MAP = hashMap;
        hashMap.put('G', ChronoField.ERA);
        hashMap.put('y', ChronoField.YEAR_OF_ERA);
        hashMap.put('u', ChronoField.YEAR);
        hashMap.put('Q', IsoFields.QUARTER_OF_YEAR);
        hashMap.put('q', IsoFields.QUARTER_OF_YEAR);
        hashMap.put('M', ChronoField.MONTH_OF_YEAR);
        hashMap.put('L', ChronoField.MONTH_OF_YEAR);
        hashMap.put('D', ChronoField.DAY_OF_YEAR);
        hashMap.put('d', ChronoField.DAY_OF_MONTH);
        hashMap.put('F', ChronoField.ALIGNED_DAY_OF_WEEK_IN_MONTH);
        hashMap.put('E', ChronoField.DAY_OF_WEEK);
        hashMap.put('c', ChronoField.DAY_OF_WEEK);
        hashMap.put('e', ChronoField.DAY_OF_WEEK);
        hashMap.put('a', ChronoField.AMPM_OF_DAY);
        hashMap.put('H', ChronoField.HOUR_OF_DAY);
        hashMap.put('k', ChronoField.CLOCK_HOUR_OF_DAY);
        hashMap.put('K', ChronoField.HOUR_OF_AMPM);
        hashMap.put('h', ChronoField.CLOCK_HOUR_OF_AMPM);
        hashMap.put('m', ChronoField.MINUTE_OF_HOUR);
        hashMap.put('s', ChronoField.SECOND_OF_MINUTE);
        hashMap.put('S', ChronoField.NANO_OF_SECOND);
        hashMap.put('A', ChronoField.MILLI_OF_DAY);
        hashMap.put('n', ChronoField.NANO_OF_SECOND);
        hashMap.put('N', ChronoField.NANO_OF_DAY);
    }

    static /* synthetic */ ZoneId lambda$static$0(TemporalAccessor temporal) {
        ZoneId zone = (ZoneId) temporal.query(TemporalQueries.zoneId());
        if (zone == null || (zone instanceof ZoneOffset)) {
            return null;
        }
        return zone;
    }

    public static String getLocalizedDateTimePattern(FormatStyle dateStyle, FormatStyle timeStyle, Chronology chrono, Locale locale) {
        DateFormat format;
        Objects.requireNonNull(locale, "locale");
        Objects.requireNonNull(chrono, "chrono");
        if (dateStyle == null && timeStyle == null) {
            throw new IllegalArgumentException("Either dateStyle or timeStyle must be non-null");
        }
        if (timeStyle == null) {
            format = DateFormat.getDateInstance(dateStyle.ordinal(), locale);
        } else if (dateStyle == null) {
            format = DateFormat.getTimeInstance(timeStyle.ordinal(), locale);
        } else {
            format = DateFormat.getDateTimeInstance(dateStyle.ordinal(), timeStyle.ordinal(), locale);
        }
        if (format instanceof SimpleDateFormat) {
            return DateTimeFormatterBuilderHelper.transformAndroidJavaTextDateTimePattern(((SimpleDateFormat) format).toPattern());
        }
        throw new UnsupportedOperationException("Can't determine pattern from " + format);
    }

    private static int convertStyle(FormatStyle style) {
        if (style == null) {
            return -1;
        }
        return style.ordinal();
    }

    public DateTimeFormatterBuilder() {
        this.active = this;
        this.printerParsers = new ArrayList();
        this.valueParserIndex = -1;
        this.parent = null;
        this.optional = false;
    }

    private DateTimeFormatterBuilder(DateTimeFormatterBuilder parent2, boolean optional2) {
        this.active = this;
        this.printerParsers = new ArrayList();
        this.valueParserIndex = -1;
        this.parent = parent2;
        this.optional = optional2;
    }

    public DateTimeFormatterBuilder parseCaseSensitive() {
        appendInternal(SettingsParser.SENSITIVE);
        return this;
    }

    public DateTimeFormatterBuilder parseCaseInsensitive() {
        appendInternal(SettingsParser.INSENSITIVE);
        return this;
    }

    public DateTimeFormatterBuilder parseStrict() {
        appendInternal(SettingsParser.STRICT);
        return this;
    }

    public DateTimeFormatterBuilder parseLenient() {
        appendInternal(SettingsParser.LENIENT);
        return this;
    }

    public DateTimeFormatterBuilder parseDefaulting(TemporalField field, long value) {
        Objects.requireNonNull(field, "field");
        appendInternal(new DefaultValueParser(field, value));
        return this;
    }

    public DateTimeFormatterBuilder appendValue(TemporalField field) {
        Objects.requireNonNull(field, "field");
        appendValue(new NumberPrinterParser(field, 1, 19, SignStyle.NORMAL));
        return this;
    }

    public DateTimeFormatterBuilder appendValue(TemporalField field, int width) {
        Objects.requireNonNull(field, "field");
        if (width < 1 || width > 19) {
            throw new IllegalArgumentException("The width must be from 1 to 19 inclusive but was " + width);
        }
        appendValue(new NumberPrinterParser(field, width, width, SignStyle.NOT_NEGATIVE));
        return this;
    }

    public DateTimeFormatterBuilder appendValue(TemporalField field, int minWidth, int maxWidth, SignStyle signStyle) {
        if (minWidth == maxWidth && signStyle == SignStyle.NOT_NEGATIVE) {
            return appendValue(field, maxWidth);
        }
        Objects.requireNonNull(field, "field");
        Objects.requireNonNull(signStyle, "signStyle");
        if (minWidth < 1 || minWidth > 19) {
            throw new IllegalArgumentException("The minimum width must be from 1 to 19 inclusive but was " + minWidth);
        } else if (maxWidth < 1 || maxWidth > 19) {
            throw new IllegalArgumentException("The maximum width must be from 1 to 19 inclusive but was " + maxWidth);
        } else if (maxWidth >= minWidth) {
            appendValue(new NumberPrinterParser(field, minWidth, maxWidth, signStyle));
            return this;
        } else {
            throw new IllegalArgumentException("The maximum width must exceed or equal the minimum width but " + maxWidth + " < " + minWidth);
        }
    }

    public DateTimeFormatterBuilder appendValueReduced(TemporalField field, int width, int maxWidth, int baseValue) {
        Objects.requireNonNull(field, "field");
        appendValue((NumberPrinterParser) new ReducedPrinterParser(field, width, maxWidth, baseValue, (ChronoLocalDate) null));
        return this;
    }

    public DateTimeFormatterBuilder appendValueReduced(TemporalField field, int width, int maxWidth, ChronoLocalDate baseDate) {
        Objects.requireNonNull(field, "field");
        Objects.requireNonNull(baseDate, "baseDate");
        appendValue((NumberPrinterParser) new ReducedPrinterParser(field, width, maxWidth, 0, baseDate));
        return this;
    }

    private DateTimeFormatterBuilder appendValue(NumberPrinterParser pp) {
        NumberPrinterParser basePP;
        DateTimeFormatterBuilder dateTimeFormatterBuilder = this.active;
        if (dateTimeFormatterBuilder.valueParserIndex >= 0) {
            int activeValueParser = dateTimeFormatterBuilder.valueParserIndex;
            NumberPrinterParser basePP2 = (NumberPrinterParser) dateTimeFormatterBuilder.printerParsers.get(activeValueParser);
            if (pp.minWidth == pp.maxWidth && pp.signStyle == SignStyle.NOT_NEGATIVE) {
                basePP = basePP2.withSubsequentWidth(pp.maxWidth);
                appendInternal(pp.withFixedWidth());
                this.active.valueParserIndex = activeValueParser;
            } else {
                basePP = basePP2.withFixedWidth();
                this.active.valueParserIndex = appendInternal(pp);
            }
            this.active.printerParsers.set(activeValueParser, basePP);
        } else {
            dateTimeFormatterBuilder.valueParserIndex = appendInternal(pp);
        }
        return this;
    }

    public DateTimeFormatterBuilder appendFraction(TemporalField field, int minWidth, int maxWidth, boolean decimalPoint) {
        appendInternal(new FractionPrinterParser(field, minWidth, maxWidth, decimalPoint));
        return this;
    }

    public DateTimeFormatterBuilder appendText(TemporalField field) {
        return appendText(field, TextStyle.FULL);
    }

    public DateTimeFormatterBuilder appendText(TemporalField field, TextStyle textStyle) {
        Objects.requireNonNull(field, "field");
        Objects.requireNonNull(textStyle, "textStyle");
        appendInternal(new TextPrinterParser(field, textStyle, DateTimeTextProvider.getInstance()));
        return this;
    }

    public DateTimeFormatterBuilder appendText(TemporalField field, Map<Long, String> textLookup) {
        Objects.requireNonNull(field, "field");
        Objects.requireNonNull(textLookup, "textLookup");
        final DateTimeTextProvider.LocaleStore store = new DateTimeTextProvider.LocaleStore(Collections.singletonMap(TextStyle.FULL, new LinkedHashMap<>(textLookup)));
        appendInternal(new TextPrinterParser(field, TextStyle.FULL, new DateTimeTextProvider() {
            public String getText(TemporalField field, long value, TextStyle style, Locale locale) {
                return store.getText(value, style);
            }

            public Iterator<Map.Entry<String, Long>> getTextIterator(TemporalField field, TextStyle style, Locale locale) {
                return store.getTextIterator(style);
            }
        }));
        return this;
    }

    public DateTimeFormatterBuilder appendInstant() {
        appendInternal(new InstantPrinterParser(-2));
        return this;
    }

    public DateTimeFormatterBuilder appendInstant(int fractionalDigits) {
        if (fractionalDigits < -1 || fractionalDigits > 9) {
            throw new IllegalArgumentException("The fractional digits must be from -1 to 9 inclusive but was " + fractionalDigits);
        }
        appendInternal(new InstantPrinterParser(fractionalDigits));
        return this;
    }

    public DateTimeFormatterBuilder appendOffsetId() {
        appendInternal(OffsetIdPrinterParser.INSTANCE_ID_Z);
        return this;
    }

    public DateTimeFormatterBuilder appendOffset(String pattern, String noOffsetText) {
        appendInternal(new OffsetIdPrinterParser(pattern, noOffsetText));
        return this;
    }

    public DateTimeFormatterBuilder appendLocalizedOffset(TextStyle style) {
        Objects.requireNonNull(style, "style");
        if (style == TextStyle.FULL || style == TextStyle.SHORT) {
            appendInternal(new LocalizedOffsetIdPrinterParser(style));
            return this;
        }
        throw new IllegalArgumentException("Style must be either full or short");
    }

    public DateTimeFormatterBuilder appendZoneId() {
        appendInternal(new ZoneIdPrinterParser(TemporalQueries.zoneId(), "ZoneId()"));
        return this;
    }

    public DateTimeFormatterBuilder appendZoneRegionId() {
        appendInternal(new ZoneIdPrinterParser(QUERY_REGION_ONLY, "ZoneRegionId()"));
        return this;
    }

    public DateTimeFormatterBuilder appendZoneOrOffsetId() {
        appendInternal(new ZoneIdPrinterParser(TemporalQueries.zone(), "ZoneOrOffsetId()"));
        return this;
    }

    public DateTimeFormatterBuilder appendZoneText(TextStyle textStyle) {
        appendInternal(new ZoneTextPrinterParser(textStyle, (Set<ZoneId>) null));
        return this;
    }

    public DateTimeFormatterBuilder appendZoneText(TextStyle textStyle, Set<ZoneId> preferredZones) {
        Objects.requireNonNull(preferredZones, "preferredZones");
        appendInternal(new ZoneTextPrinterParser(textStyle, preferredZones));
        return this;
    }

    public DateTimeFormatterBuilder appendChronologyId() {
        appendInternal(new ChronoPrinterParser((TextStyle) null));
        return this;
    }

    public DateTimeFormatterBuilder appendChronologyText(TextStyle textStyle) {
        Objects.requireNonNull(textStyle, "textStyle");
        appendInternal(new ChronoPrinterParser(textStyle));
        return this;
    }

    public DateTimeFormatterBuilder appendLocalized(FormatStyle dateStyle, FormatStyle timeStyle) {
        if (dateStyle == null && timeStyle == null) {
            throw new IllegalArgumentException("Either the date or time style must be non-null");
        }
        appendInternal(new LocalizedPrinterParser(dateStyle, timeStyle));
        return this;
    }

    public DateTimeFormatterBuilder appendLiteral(char literal) {
        appendInternal(new CharLiteralPrinterParser(literal));
        return this;
    }

    public DateTimeFormatterBuilder appendLiteral(String literal) {
        Objects.requireNonNull(literal, "literal");
        if (literal.length() > 0) {
            if (literal.length() == 1) {
                appendInternal(new CharLiteralPrinterParser(literal.charAt(0)));
            } else {
                appendInternal(new StringLiteralPrinterParser(literal));
            }
        }
        return this;
    }

    public DateTimeFormatterBuilder append(DateTimeFormatter formatter) {
        Objects.requireNonNull(formatter, "formatter");
        appendInternal(formatter.toPrinterParser(false));
        return this;
    }

    public DateTimeFormatterBuilder appendOptional(DateTimeFormatter formatter) {
        Objects.requireNonNull(formatter, "formatter");
        appendInternal(formatter.toPrinterParser(true));
        return this;
    }

    public DateTimeFormatterBuilder appendPattern(String pattern) {
        Objects.requireNonNull(pattern, "pattern");
        parsePattern(pattern);
        return this;
    }

    private void parsePattern(String pattern) {
        int start = 0;
        while (start < pattern.length()) {
            char cur = pattern.charAt(start);
            if ((cur >= 'A' && cur <= 'Z') || (cur >= 'a' && cur <= 'z')) {
                int pos = start + 1;
                while (pos < pattern.length() && pattern.charAt(pos) == cur) {
                    pos++;
                }
                int count = pos - start;
                if (cur == 'p') {
                    int pad = 0;
                    if (pos < pattern.length() && (((cur = pattern.charAt(pos)) >= 'A' && cur <= 'Z') || (cur >= 'a' && cur <= 'z'))) {
                        pad = count;
                        int pos2 = pos + 1;
                        int start2 = pos;
                        while (pos2 < pattern.length() && pattern.charAt(pos2) == cur) {
                            pos2++;
                        }
                        pos = pos2;
                        count = pos2 - start2;
                    }
                    if (pad != 0) {
                        padNext(pad);
                    } else {
                        throw new IllegalArgumentException("Pad letter 'p' must be followed by valid pad pattern: " + pattern);
                    }
                }
                TemporalField field = FIELD_MAP.get(Character.valueOf(cur));
                if (field != null) {
                    parseField(cur, count, field);
                } else if (cur == 'z') {
                    if (count > 4) {
                        throw new IllegalArgumentException("Too many pattern letters: " + cur);
                    } else if (count == 4) {
                        appendZoneText(TextStyle.FULL);
                    } else {
                        appendZoneText(TextStyle.SHORT);
                    }
                } else if (cur != 'V') {
                    String str = "+0000";
                    if (cur == 'Z') {
                        if (count < 4) {
                            appendOffset("+HHMM", str);
                        } else if (count == 4) {
                            appendLocalizedOffset(TextStyle.FULL);
                        } else if (count == 5) {
                            appendOffset("+HH:MM:ss", "Z");
                        } else {
                            throw new IllegalArgumentException("Too many pattern letters: " + cur);
                        }
                    } else if (cur != 'O') {
                        int i = 0;
                        if (cur == 'X') {
                            if (count <= 5) {
                                String[] strArr = OffsetIdPrinterParser.PATTERNS;
                                if (count != 1) {
                                    i = 1;
                                }
                                appendOffset(strArr[i + count], "Z");
                            } else {
                                throw new IllegalArgumentException("Too many pattern letters: " + cur);
                            }
                        } else if (cur == 'x') {
                            if (count <= 5) {
                                if (count == 1) {
                                    str = "+00";
                                } else if (count % 2 != 0) {
                                    str = "+00:00";
                                }
                                String zero = str;
                                String[] strArr2 = OffsetIdPrinterParser.PATTERNS;
                                if (count != 1) {
                                    i = 1;
                                }
                                appendOffset(strArr2[i + count], zero);
                            } else {
                                throw new IllegalArgumentException("Too many pattern letters: " + cur);
                            }
                        } else if (cur == 'W') {
                            if (count <= 1) {
                                appendInternal(new WeekBasedFieldPrinterParser(cur, count));
                            } else {
                                throw new IllegalArgumentException("Too many pattern letters: " + cur);
                            }
                        } else if (cur == 'w') {
                            if (count <= 2) {
                                appendInternal(new WeekBasedFieldPrinterParser(cur, count));
                            } else {
                                throw new IllegalArgumentException("Too many pattern letters: " + cur);
                            }
                        } else if (cur == 'Y') {
                            appendInternal(new WeekBasedFieldPrinterParser(cur, count));
                        } else {
                            throw new IllegalArgumentException("Unknown pattern letter: " + cur);
                        }
                    } else if (count == 1) {
                        appendLocalizedOffset(TextStyle.SHORT);
                    } else if (count == 4) {
                        appendLocalizedOffset(TextStyle.FULL);
                    } else {
                        throw new IllegalArgumentException("Pattern letter count must be 1 or 4: " + cur);
                    }
                } else if (count == 2) {
                    appendZoneId();
                } else {
                    throw new IllegalArgumentException("Pattern letter count must be 2: " + cur);
                }
                start = pos - 1;
            } else if (cur == '\'') {
                int pos3 = start + 1;
                while (pos3 < pattern.length()) {
                    if (pattern.charAt(pos3) == '\'') {
                        if (pos3 + 1 >= pattern.length() || pattern.charAt(pos3 + 1) != '\'') {
                            break;
                        }
                        pos3++;
                    }
                    pos3++;
                }
                if (pos3 < pattern.length()) {
                    String str2 = pattern.substring(start + 1, pos3);
                    if (str2.length() == 0) {
                        appendLiteral('\'');
                    } else {
                        appendLiteral(str2.replace("''", "'"));
                    }
                    start = pos3;
                } else {
                    throw new IllegalArgumentException("Pattern ends with an incomplete string literal: " + pattern);
                }
            } else if (cur == '[') {
                optionalStart();
            } else if (cur == ']') {
                if (this.active.parent != null) {
                    optionalEnd();
                } else {
                    throw new IllegalArgumentException("Pattern invalid as it contains ] without previous [");
                }
            } else if (cur == '{' || cur == '}' || cur == '#') {
                throw new IllegalArgumentException("Pattern includes reserved character: '" + cur + "'");
            } else {
                appendLiteral(cur);
            }
            start++;
        }
    }

    private void parseField(char cur, int count, TemporalField field) {
        boolean standalone = false;
        switch (cur) {
            case 'D':
                if (count == 1) {
                    appendValue(field);
                    return;
                } else if (count <= 3) {
                    appendValue(field, count);
                    return;
                } else {
                    throw new IllegalArgumentException("Too many pattern letters: " + cur);
                }
            case 'E':
            case 'M':
            case 'Q':
            case 'e':
                break;
            case 'F':
                if (count == 1) {
                    appendValue(field);
                    return;
                }
                throw new IllegalArgumentException("Too many pattern letters: " + cur);
            case 'G':
                switch (count) {
                    case 1:
                    case 2:
                    case 3:
                        appendText(field, TextStyle.SHORT);
                        return;
                    case 4:
                        appendText(field, TextStyle.FULL);
                        return;
                    case 5:
                        appendText(field, TextStyle.NARROW);
                        return;
                    default:
                        throw new IllegalArgumentException("Too many pattern letters: " + cur);
                }
            case 'H':
            case 'K':
            case 'd':
            case 'h':
            case 'k':
            case 'm':
            case 's':
                if (count == 1) {
                    appendValue(field);
                    return;
                } else if (count == 2) {
                    appendValue(field, count);
                    return;
                } else {
                    throw new IllegalArgumentException("Too many pattern letters: " + cur);
                }
            case 'L':
            case 'q':
                break;
            case 'S':
                appendFraction(ChronoField.NANO_OF_SECOND, count, count, false);
                return;
            case 'a':
                if (count == 1) {
                    appendText(field, TextStyle.SHORT);
                    return;
                }
                throw new IllegalArgumentException("Too many pattern letters: " + cur);
            case 'c':
                if (count == 2) {
                    throw new IllegalArgumentException("Invalid pattern \"cc\"");
                }
                break;
            case 'u':
            case 'y':
                if (count == 2) {
                    appendValueReduced(field, 2, 2, (ChronoLocalDate) ReducedPrinterParser.BASE_DATE);
                    return;
                } else if (count < 4) {
                    appendValue(field, count, 19, SignStyle.NORMAL);
                    return;
                } else {
                    appendValue(field, count, 19, SignStyle.EXCEEDS_PAD);
                    return;
                }
            default:
                if (count == 1) {
                    appendValue(field);
                    return;
                } else {
                    appendValue(field, count);
                    return;
                }
        }
        standalone = true;
        switch (count) {
            case 1:
            case 2:
                if (cur == 'c' || cur == 'e') {
                    appendInternal(new WeekBasedFieldPrinterParser(cur, count));
                    return;
                } else if (cur == 'E') {
                    appendText(field, TextStyle.SHORT);
                    return;
                } else if (count == 1) {
                    appendValue(field);
                    return;
                } else {
                    appendValue(field, 2);
                    return;
                }
            case 3:
                appendText(field, standalone ? TextStyle.SHORT_STANDALONE : TextStyle.SHORT);
                return;
            case 4:
                appendText(field, standalone ? TextStyle.FULL_STANDALONE : TextStyle.FULL);
                return;
            case 5:
                appendText(field, standalone ? TextStyle.NARROW_STANDALONE : TextStyle.NARROW);
                return;
            default:
                throw new IllegalArgumentException("Too many pattern letters: " + cur);
        }
    }

    public DateTimeFormatterBuilder padNext(int padWidth) {
        return padNext(padWidth, ' ');
    }

    public DateTimeFormatterBuilder padNext(int padWidth, char padChar) {
        if (padWidth >= 1) {
            DateTimeFormatterBuilder dateTimeFormatterBuilder = this.active;
            dateTimeFormatterBuilder.padNextWidth = padWidth;
            dateTimeFormatterBuilder.padNextChar = padChar;
            dateTimeFormatterBuilder.valueParserIndex = -1;
            return this;
        }
        throw new IllegalArgumentException("The pad width must be at least one but was " + padWidth);
    }

    public DateTimeFormatterBuilder optionalStart() {
        this.active.valueParserIndex = -1;
        this.active = new DateTimeFormatterBuilder(this.active, true);
        return this;
    }

    public DateTimeFormatterBuilder optionalEnd() {
        DateTimeFormatterBuilder dateTimeFormatterBuilder = this.active;
        if (dateTimeFormatterBuilder.parent != null) {
            if (dateTimeFormatterBuilder.printerParsers.size() > 0) {
                DateTimeFormatterBuilder dateTimeFormatterBuilder2 = this.active;
                CompositePrinterParser cpp = new CompositePrinterParser(dateTimeFormatterBuilder2.printerParsers, dateTimeFormatterBuilder2.optional);
                this.active = this.active.parent;
                appendInternal(cpp);
            } else {
                this.active = this.active.parent;
            }
            return this;
        }
        throw new IllegalStateException("Cannot call optionalEnd() as there was no previous call to optionalStart()");
    }

    private int appendInternal(DateTimePrinterParser pp) {
        Objects.requireNonNull(pp, "pp");
        DateTimeFormatterBuilder dateTimeFormatterBuilder = this.active;
        int i = dateTimeFormatterBuilder.padNextWidth;
        if (i > 0) {
            if (pp != null) {
                pp = new PadPrinterParserDecorator(pp, i, dateTimeFormatterBuilder.padNextChar);
            }
            DateTimeFormatterBuilder dateTimeFormatterBuilder2 = this.active;
            dateTimeFormatterBuilder2.padNextWidth = 0;
            dateTimeFormatterBuilder2.padNextChar = 0;
        }
        this.active.printerParsers.add(pp);
        DateTimeFormatterBuilder dateTimeFormatterBuilder3 = this.active;
        dateTimeFormatterBuilder3.valueParserIndex = -1;
        return dateTimeFormatterBuilder3.printerParsers.size() - 1;
    }

    public DateTimeFormatter toFormatter() {
        return toFormatter(Locale.getDefault());
    }

    public DateTimeFormatter toFormatter(Locale locale) {
        return toFormatter(locale, ResolverStyle.SMART, (Chronology) null);
    }

    /* access modifiers changed from: package-private */
    public DateTimeFormatter toFormatter(ResolverStyle resolverStyle, Chronology chrono) {
        return toFormatter(Locale.getDefault(), resolverStyle, chrono);
    }

    private DateTimeFormatter toFormatter(Locale locale, ResolverStyle resolverStyle, Chronology chrono) {
        Objects.requireNonNull(locale, "locale");
        while (this.active.parent != null) {
            optionalEnd();
        }
        return new DateTimeFormatter(new CompositePrinterParser(this.printerParsers, false), locale, DecimalStyle.STANDARD, resolverStyle, (Set<TemporalField>) null, chrono, (ZoneId) null);
    }

    static final class CompositePrinterParser implements DateTimePrinterParser {
        private final boolean optional;
        private final DateTimePrinterParser[] printerParsers;

        CompositePrinterParser(List<DateTimePrinterParser> printerParsers2, boolean optional2) {
            this((DateTimePrinterParser[]) printerParsers2.toArray(new DateTimePrinterParser[printerParsers2.size()]), optional2);
        }

        CompositePrinterParser(DateTimePrinterParser[] printerParsers2, boolean optional2) {
            this.printerParsers = printerParsers2;
            this.optional = optional2;
        }

        public CompositePrinterParser withOptional(boolean optional2) {
            if (optional2 == this.optional) {
                return this;
            }
            return new CompositePrinterParser(this.printerParsers, optional2);
        }

        public boolean format(DateTimePrintContext context, StringBuilder buf) {
            int length = buf.length();
            if (this.optional) {
                context.startOptional();
            }
            try {
                for (DateTimePrinterParser pp : this.printerParsers) {
                    if (!pp.format(context, buf)) {
                        buf.setLength(length);
                        return true;
                    }
                }
                if (this.optional) {
                    context.endOptional();
                }
                return true;
            } finally {
                if (this.optional) {
                    context.endOptional();
                }
            }
        }

        public int parse(DateTimeParseContext context, CharSequence text, int position) {
            if (this.optional) {
                context.startOptional();
                int pos = position;
                for (DateTimePrinterParser pp : this.printerParsers) {
                    pos = pp.parse(context, text, pos);
                    if (pos < 0) {
                        context.endOptional(false);
                        return position;
                    }
                }
                context.endOptional(true);
                return pos;
            }
            for (DateTimePrinterParser pp2 : this.printerParsers) {
                position = pp2.parse(context, text, position);
                if (position < 0) {
                    break;
                }
            }
            return position;
        }

        public String toString() {
            StringBuilder buf = new StringBuilder();
            if (this.printerParsers != null) {
                buf.append(this.optional ? "[" : "(");
                for (DateTimePrinterParser pp : this.printerParsers) {
                    buf.append(pp);
                }
                buf.append(this.optional ? "]" : ")");
            }
            return buf.toString();
        }
    }

    static final class PadPrinterParserDecorator implements DateTimePrinterParser {
        private final char padChar;
        private final int padWidth;
        private final DateTimePrinterParser printerParser;

        PadPrinterParserDecorator(DateTimePrinterParser printerParser2, int padWidth2, char padChar2) {
            this.printerParser = printerParser2;
            this.padWidth = padWidth2;
            this.padChar = padChar2;
        }

        public boolean format(DateTimePrintContext context, StringBuilder buf) {
            int preLen = buf.length();
            if (!this.printerParser.format(context, buf)) {
                return false;
            }
            int len = buf.length() - preLen;
            if (len <= this.padWidth) {
                for (int i = 0; i < this.padWidth - len; i++) {
                    buf.insert(preLen, this.padChar);
                }
                return true;
            }
            throw new DateTimeException("Cannot print as output of " + len + " characters exceeds pad width of " + this.padWidth);
        }

        public int parse(DateTimeParseContext context, CharSequence text, int position) {
            boolean strict = context.isStrict();
            if (position > text.length()) {
                throw new IndexOutOfBoundsException();
            } else if (position == text.length()) {
                return position ^ -1;
            } else {
                int endPos = this.padWidth + position;
                if (endPos > text.length()) {
                    if (strict) {
                        return position ^ -1;
                    }
                    endPos = text.length();
                }
                int pos = position;
                while (pos < endPos && context.charEquals(text.charAt(pos), this.padChar)) {
                    pos++;
                }
                int resultPos = this.printerParser.parse(context, text.subSequence(0, endPos), pos);
                if (resultPos == endPos || !strict) {
                    return resultPos;
                }
                return (position + pos) ^ -1;
            }
        }

        public String toString() {
            String str;
            StringBuilder sb = new StringBuilder();
            sb.append("Pad(");
            sb.append(this.printerParser);
            sb.append(",");
            sb.append(this.padWidth);
            if (this.padChar == ' ') {
                str = ")";
            } else {
                str = ",'" + this.padChar + "')";
            }
            sb.append(str);
            return sb.toString();
        }
    }

    enum SettingsParser implements DateTimePrinterParser {
        SENSITIVE,
        INSENSITIVE,
        STRICT,
        LENIENT;

        public boolean format(DateTimePrintContext context, StringBuilder buf) {
            return true;
        }

        public int parse(DateTimeParseContext context, CharSequence text, int position) {
            switch (ordinal()) {
                case 0:
                    context.setCaseSensitive(true);
                    break;
                case 1:
                    context.setCaseSensitive(false);
                    break;
                case 2:
                    context.setStrict(true);
                    break;
                case 3:
                    context.setStrict(false);
                    break;
            }
            return position;
        }

        public String toString() {
            switch (ordinal()) {
                case 0:
                    return "ParseCaseSensitive(true)";
                case 1:
                    return "ParseCaseSensitive(false)";
                case 2:
                    return "ParseStrict(true)";
                case 3:
                    return "ParseStrict(false)";
                default:
                    throw new IllegalStateException("Unreachable");
            }
        }
    }

    static class DefaultValueParser implements DateTimePrinterParser {
        private final TemporalField field;
        private final long value;

        DefaultValueParser(TemporalField field2, long value2) {
            this.field = field2;
            this.value = value2;
        }

        public boolean format(DateTimePrintContext context, StringBuilder buf) {
            return true;
        }

        public int parse(DateTimeParseContext context, CharSequence text, int position) {
            if (context.getParsed(this.field) == null) {
                context.setParsedField(this.field, this.value, position, position);
            }
            return position;
        }
    }

    static final class CharLiteralPrinterParser implements DateTimePrinterParser {
        private final char literal;

        CharLiteralPrinterParser(char literal2) {
            this.literal = literal2;
        }

        public boolean format(DateTimePrintContext context, StringBuilder buf) {
            buf.append(this.literal);
            return true;
        }

        public int parse(DateTimeParseContext context, CharSequence text, int position) {
            if (position == text.length()) {
                return position ^ -1;
            }
            char ch = text.charAt(position);
            if (ch == this.literal || (!context.isCaseSensitive() && (Character.toUpperCase(ch) == Character.toUpperCase(this.literal) || Character.toLowerCase(ch) == Character.toLowerCase(this.literal)))) {
                return position + 1;
            }
            return position ^ -1;
        }

        public String toString() {
            if (this.literal == '\'') {
                return "''";
            }
            return "'" + this.literal + "'";
        }
    }

    static final class StringLiteralPrinterParser implements DateTimePrinterParser {
        private final String literal;

        StringLiteralPrinterParser(String literal2) {
            this.literal = literal2;
        }

        public boolean format(DateTimePrintContext context, StringBuilder buf) {
            buf.append(this.literal);
            return true;
        }

        public int parse(DateTimeParseContext context, CharSequence text, int position) {
            if (position > text.length() || position < 0) {
                throw new IndexOutOfBoundsException();
            }
            String str = this.literal;
            if (!context.subSequenceEquals(text, position, str, 0, str.length())) {
                return position ^ -1;
            }
            return this.literal.length() + position;
        }

        public String toString() {
            String converted = this.literal.replace("'", "''");
            return "'" + converted + "'";
        }
    }

    static class NumberPrinterParser implements DateTimePrinterParser {
        static final long[] EXCEED_POINTS = {0, 10, 100, 1000, 10000, 100000, 1000000, 10000000, NUM, NUM, 10000000000L};
        final TemporalField field;
        final int maxWidth;
        final int minWidth;
        /* access modifiers changed from: private */
        public final SignStyle signStyle;
        final int subsequentWidth;

        NumberPrinterParser(TemporalField field2, int minWidth2, int maxWidth2, SignStyle signStyle2) {
            this.field = field2;
            this.minWidth = minWidth2;
            this.maxWidth = maxWidth2;
            this.signStyle = signStyle2;
            this.subsequentWidth = 0;
        }

        protected NumberPrinterParser(TemporalField field2, int minWidth2, int maxWidth2, SignStyle signStyle2, int subsequentWidth2) {
            this.field = field2;
            this.minWidth = minWidth2;
            this.maxWidth = maxWidth2;
            this.signStyle = signStyle2;
            this.subsequentWidth = subsequentWidth2;
        }

        /* access modifiers changed from: package-private */
        public NumberPrinterParser withFixedWidth() {
            if (this.subsequentWidth == -1) {
                return this;
            }
            return new NumberPrinterParser(this.field, this.minWidth, this.maxWidth, this.signStyle, -1);
        }

        /* access modifiers changed from: package-private */
        public NumberPrinterParser withSubsequentWidth(int subsequentWidth2) {
            return new NumberPrinterParser(this.field, this.minWidth, this.maxWidth, this.signStyle, this.subsequentWidth + subsequentWidth2);
        }

        public boolean format(DateTimePrintContext context, StringBuilder buf) {
            Long valueLong = context.getValue(this.field);
            if (valueLong == null) {
                return false;
            }
            long value = getValue(context, valueLong.longValue());
            DecimalStyle decimalStyle = context.getDecimalStyle();
            String str = value == Long.MIN_VALUE ? "NUM" : Long.toString(Math.abs(value));
            if (str.length() <= this.maxWidth) {
                String str2 = decimalStyle.convertNumberToI18N(str);
                if (value >= 0) {
                    switch (AnonymousClass3.$SwitchMap$java$time$format$SignStyle[this.signStyle.ordinal()]) {
                        case 1:
                            int i = this.minWidth;
                            if (i < 19 && value >= EXCEED_POINTS[i]) {
                                buf.append(decimalStyle.getPositiveSign());
                                break;
                            }
                        case 2:
                            buf.append(decimalStyle.getPositiveSign());
                            break;
                    }
                } else {
                    switch (AnonymousClass3.$SwitchMap$java$time$format$SignStyle[this.signStyle.ordinal()]) {
                        case 1:
                        case 2:
                        case 3:
                            buf.append(decimalStyle.getNegativeSign());
                            break;
                        case 4:
                            throw new DateTimeException("Field " + this.field + " cannot be printed as the value " + value + " cannot be negative according to the SignStyle");
                    }
                }
                for (int i2 = 0; i2 < this.minWidth - str2.length(); i2++) {
                    buf.append(decimalStyle.getZeroDigit());
                }
                buf.append(str2);
                return true;
            }
            throw new DateTimeException("Field " + this.field + " cannot be printed as the value " + value + " exceeds the maximum print width of " + this.maxWidth);
        }

        /* access modifiers changed from: package-private */
        public long getValue(DateTimePrintContext context, long value) {
            return value;
        }

        /* access modifiers changed from: package-private */
        public boolean isFixedWidth(DateTimeParseContext context) {
            int i = this.subsequentWidth;
            return i == -1 || (i > 0 && this.minWidth == this.maxWidth && this.signStyle == SignStyle.NOT_NEGATIVE);
        }

        public int parse(DateTimeParseContext context, CharSequence text, int position) {
            boolean positive;
            boolean negative;
            int position2;
            int pos;
            long total;
            BigInteger totalBig;
            long total2;
            int length;
            char sign;
            char sign2;
            int i = position;
            int length2 = text.length();
            if (i == length2) {
                return i ^ -1;
            }
            char sign3 = text.charAt(position);
            int i2 = 1;
            if (sign3 == context.getDecimalStyle().getPositiveSign()) {
                if (!this.signStyle.parse(true, context.isStrict(), this.minWidth == this.maxWidth)) {
                    return i ^ -1;
                }
                position2 = i + 1;
                negative = false;
                positive = true;
            } else if (sign3 == context.getDecimalStyle().getNegativeSign()) {
                if (!this.signStyle.parse(false, context.isStrict(), this.minWidth == this.maxWidth)) {
                    return i ^ -1;
                }
                position2 = i + 1;
                negative = true;
                positive = false;
            } else if (this.signStyle == SignStyle.ALWAYS && context.isStrict()) {
                return i ^ -1;
            } else {
                position2 = i;
                negative = false;
                positive = false;
            }
            if (context.isStrict() || isFixedWidth(context)) {
                i2 = this.minWidth;
            }
            int effMinWidth = i2;
            int minEndPos = position2 + effMinWidth;
            if (minEndPos > length2) {
                return position2 ^ -1;
            }
            long total3 = 0;
            BigInteger totalBig2 = null;
            int pos2 = position2;
            int pass = 0;
            int effMaxWidth = ((context.isStrict() || isFixedWidth(context)) ? this.maxWidth : 9) + Math.max(this.subsequentWidth, 0);
            while (true) {
                if (pass >= 2) {
                    char c = sign3;
                    pos = pos2;
                    break;
                }
                int maxEndPos = Math.min(pos2 + effMaxWidth, length2);
                while (true) {
                    if (pos2 >= maxEndPos) {
                        total2 = total3;
                        length = length2;
                        sign = sign3;
                        break;
                    }
                    int pos3 = pos2 + 1;
                    length = length2;
                    char ch = text.charAt(pos2);
                    int maxEndPos2 = maxEndPos;
                    int digit = context.getDecimalStyle().convertToDigit(ch);
                    if (digit < 0) {
                        int pos4 = pos3 - 1;
                        if (pos4 < minEndPos) {
                            return position2 ^ -1;
                        }
                        pos2 = pos4;
                        int digit2 = pos2;
                        total2 = total3;
                        sign = sign3;
                    } else {
                        char c2 = ch;
                        if (pos3 - position2 > 18) {
                            if (totalBig2 == null) {
                                totalBig2 = BigInteger.valueOf(total3);
                            }
                            sign2 = sign3;
                            totalBig2 = totalBig2.multiply(BigInteger.TEN).add(BigInteger.valueOf((long) digit));
                        } else {
                            sign2 = sign3;
                            long j = total3;
                            total3 = ((long) digit) + (10 * total3);
                        }
                        pos2 = pos3;
                        maxEndPos = maxEndPos2;
                        length2 = length;
                        sign3 = sign2;
                    }
                }
                int maxEndPos3 = this.subsequentWidth;
                if (maxEndPos3 <= 0 || pass != 0) {
                    pos = pos2;
                    total3 = total2;
                } else {
                    effMaxWidth = Math.max(effMinWidth, (pos2 - position2) - maxEndPos3);
                    pos2 = position2;
                    totalBig2 = null;
                    pass++;
                    total3 = 0;
                    length2 = length;
                    sign3 = sign;
                }
            }
            if (!negative) {
                if (this.signStyle == SignStyle.EXCEEDS_PAD && context.isStrict()) {
                    int parseLen = pos - position2;
                    if (positive) {
                        if (parseLen <= this.minWidth) {
                            return (position2 - 1) ^ -1;
                        }
                    } else if (parseLen > this.minWidth) {
                        return position2 ^ -1;
                    }
                }
                total = total3;
                totalBig = totalBig2;
            } else if (totalBig2 != null) {
                if (totalBig2.equals(BigInteger.ZERO) && context.isStrict()) {
                    return (position2 - 1) ^ -1;
                }
                total = total3;
                totalBig = totalBig2.negate();
            } else if (total3 == 0 && context.isStrict()) {
                return (position2 - 1) ^ -1;
            } else {
                total = -total3;
                totalBig = totalBig2;
            }
            if (totalBig == null) {
                return setValue(context, total, position2, pos);
            }
            if (totalBig.bitLength() > 63) {
                totalBig = totalBig.divide(BigInteger.TEN);
                pos--;
            }
            return setValue(context, totalBig.longValue(), position2, pos);
        }

        /* access modifiers changed from: package-private */
        public int setValue(DateTimeParseContext context, long value, int errorPos, int successPos) {
            return context.setParsedField(this.field, value, errorPos, successPos);
        }

        public String toString() {
            if (this.minWidth == 1 && this.maxWidth == 19 && this.signStyle == SignStyle.NORMAL) {
                return "Value(" + this.field + ")";
            } else if (this.minWidth == this.maxWidth && this.signStyle == SignStyle.NOT_NEGATIVE) {
                return "Value(" + this.field + "," + this.minWidth + ")";
            } else {
                return "Value(" + this.field + "," + this.minWidth + "," + this.maxWidth + "," + this.signStyle + ")";
            }
        }
    }

    /* renamed from: j$.time.format.DateTimeFormatterBuilder$3  reason: invalid class name */
    static /* synthetic */ class AnonymousClass3 {
        static final /* synthetic */ int[] $SwitchMap$java$time$format$SignStyle;

        static {
            int[] iArr = new int[SignStyle.values().length];
            $SwitchMap$java$time$format$SignStyle = iArr;
            try {
                iArr[SignStyle.EXCEEDS_PAD.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                $SwitchMap$java$time$format$SignStyle[SignStyle.ALWAYS.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
            try {
                $SwitchMap$java$time$format$SignStyle[SignStyle.NORMAL.ordinal()] = 3;
            } catch (NoSuchFieldError e3) {
            }
            try {
                $SwitchMap$java$time$format$SignStyle[SignStyle.NOT_NEGATIVE.ordinal()] = 4;
            } catch (NoSuchFieldError e4) {
            }
        }
    }

    static final class ReducedPrinterParser extends NumberPrinterParser {
        static final LocalDate BASE_DATE = LocalDate.of(2000, 1, 1);
        private final ChronoLocalDate baseDate;
        private final int baseValue;

        ReducedPrinterParser(TemporalField field, int minWidth, int maxWidth, int baseValue2, ChronoLocalDate baseDate2) {
            this(field, minWidth, maxWidth, baseValue2, baseDate2, 0);
            if (minWidth < 1 || minWidth > 10) {
                throw new IllegalArgumentException("The minWidth must be from 1 to 10 inclusive but was " + minWidth);
            } else if (maxWidth < 1 || maxWidth > 10) {
                throw new IllegalArgumentException("The maxWidth must be from 1 to 10 inclusive but was " + minWidth);
            } else if (maxWidth < minWidth) {
                throw new IllegalArgumentException("Maximum width must exceed or equal the minimum width but " + maxWidth + " < " + minWidth);
            } else if (baseDate2 != null) {
            } else {
                if (!field.range().isValidValue((long) baseValue2)) {
                    throw new IllegalArgumentException("The base value must be within the range of the field");
                } else if (((long) baseValue2) + EXCEED_POINTS[maxWidth] > 2147483647L) {
                    throw new DateTimeException("Unable to add printer-parser as the range exceeds the capacity of an int");
                }
            }
        }

        private ReducedPrinterParser(TemporalField field, int minWidth, int maxWidth, int baseValue2, ChronoLocalDate baseDate2, int subsequentWidth) {
            super(field, minWidth, maxWidth, SignStyle.NOT_NEGATIVE, subsequentWidth);
            this.baseValue = baseValue2;
            this.baseDate = baseDate2;
        }

        /* access modifiers changed from: package-private */
        public long getValue(DateTimePrintContext context, long value) {
            long absValue = Math.abs(value);
            int baseValue2 = this.baseValue;
            if (this.baseDate != null) {
                baseValue2 = Chronology.CC.from(context.getTemporal()).date(this.baseDate).get(this.field);
            }
            if (value < ((long) baseValue2) || value >= ((long) baseValue2) + EXCEED_POINTS[this.minWidth]) {
                return absValue % EXCEED_POINTS[this.maxWidth];
            }
            return absValue % EXCEED_POINTS[this.minWidth];
        }

        /* access modifiers changed from: package-private */
        public int setValue(DateTimeParseContext context, long value, int errorPos, int successPos) {
            long value2;
            long value3;
            int baseValue2 = this.baseValue;
            if (this.baseDate != null) {
                int baseValue3 = context.getEffectiveChronology().date(this.baseDate).get(this.field);
                context.addChronoChangedListener(new DateTimeFormatterBuilder$ReducedPrinterParser$$ExternalSyntheticLambda0(this, context, value, errorPos, successPos));
                baseValue2 = baseValue3;
            } else {
                DateTimeParseContext dateTimeParseContext = context;
            }
            if (successPos - errorPos != this.minWidth || value < 0) {
                value2 = value;
            } else {
                long range = EXCEED_POINTS[this.minWidth];
                long basePart = ((long) baseValue2) - (((long) baseValue2) % range);
                if (baseValue2 > 0) {
                    value3 = basePart + value;
                } else {
                    value3 = basePart - value;
                }
                if (value3 < ((long) baseValue2)) {
                    value2 = value3 + range;
                } else {
                    value2 = value3;
                }
            }
            return context.setParsedField(this.field, value2, errorPos, successPos);
        }

        /* renamed from: lambda$setValue$0$java-time-format-DateTimeFormatterBuilder$ReducedPrinterParser  reason: not valid java name */
        public /* synthetic */ void m519xdf3a601e(DateTimeParseContext context, long initialValue, int errorPos, int successPos, Chronology _unused) {
            setValue(context, initialValue, errorPos, successPos);
        }

        /* access modifiers changed from: package-private */
        public ReducedPrinterParser withFixedWidth() {
            if (this.subsequentWidth == -1) {
                return this;
            }
            return new ReducedPrinterParser(this.field, this.minWidth, this.maxWidth, this.baseValue, this.baseDate, -1);
        }

        /* access modifiers changed from: package-private */
        public ReducedPrinterParser withSubsequentWidth(int subsequentWidth) {
            return new ReducedPrinterParser(this.field, this.minWidth, this.maxWidth, this.baseValue, this.baseDate, this.subsequentWidth + subsequentWidth);
        }

        /* access modifiers changed from: package-private */
        public boolean isFixedWidth(DateTimeParseContext context) {
            if (!context.isStrict()) {
                return false;
            }
            return super.isFixedWidth(context);
        }

        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append("ReducedValue(");
            sb.append(this.field);
            sb.append(",");
            sb.append(this.minWidth);
            sb.append(",");
            sb.append(this.maxWidth);
            sb.append(",");
            Object obj = this.baseDate;
            if (obj == null) {
                obj = Integer.valueOf(this.baseValue);
            }
            sb.append(obj);
            sb.append(")");
            return sb.toString();
        }
    }

    static final class FractionPrinterParser implements DateTimePrinterParser {
        private final boolean decimalPoint;
        private final TemporalField field;
        private final int maxWidth;
        private final int minWidth;

        FractionPrinterParser(TemporalField field2, int minWidth2, int maxWidth2, boolean decimalPoint2) {
            Objects.requireNonNull(field2, "field");
            if (!field2.range().isFixed()) {
                throw new IllegalArgumentException("Field must have a fixed set of values: " + field2);
            } else if (minWidth2 < 0 || minWidth2 > 9) {
                throw new IllegalArgumentException("Minimum width must be from 0 to 9 inclusive but was " + minWidth2);
            } else if (maxWidth2 < 1 || maxWidth2 > 9) {
                throw new IllegalArgumentException("Maximum width must be from 1 to 9 inclusive but was " + maxWidth2);
            } else if (maxWidth2 >= minWidth2) {
                this.field = field2;
                this.minWidth = minWidth2;
                this.maxWidth = maxWidth2;
                this.decimalPoint = decimalPoint2;
            } else {
                throw new IllegalArgumentException("Maximum width must exceed or equal the minimum width but " + maxWidth2 + " < " + minWidth2);
            }
        }

        public boolean format(DateTimePrintContext context, StringBuilder buf) {
            Long value = context.getValue(this.field);
            if (value == null) {
                return false;
            }
            DecimalStyle decimalStyle = context.getDecimalStyle();
            BigDecimal fraction = convertToFraction(value.longValue());
            if (fraction.scale() != 0) {
                String str = decimalStyle.convertNumberToI18N(fraction.setScale(Math.min(Math.max(fraction.scale(), this.minWidth), this.maxWidth), RoundingMode.FLOOR).toPlainString().substring(2));
                if (this.decimalPoint) {
                    buf.append(decimalStyle.getDecimalSeparator());
                }
                buf.append(str);
                return true;
            } else if (this.minWidth <= 0) {
                return true;
            } else {
                if (this.decimalPoint) {
                    buf.append(decimalStyle.getDecimalSeparator());
                }
                for (int i = 0; i < this.minWidth; i++) {
                    buf.append(decimalStyle.getZeroDigit());
                }
                return true;
            }
        }

        public int parse(DateTimeParseContext context, CharSequence text, int position) {
            int pos;
            int position2 = position;
            int effectiveMin = context.isStrict() ? this.minWidth : 0;
            int effectiveMax = context.isStrict() ? this.maxWidth : 9;
            int length = text.length();
            if (position2 == length) {
                return effectiveMin > 0 ? position2 ^ -1 : position2;
            }
            if (this.decimalPoint) {
                if (text.charAt(position) != context.getDecimalStyle().getDecimalSeparator()) {
                    return effectiveMin > 0 ? position2 ^ -1 : position2;
                }
                position2++;
            }
            int minEndPos = position2 + effectiveMin;
            if (minEndPos > length) {
                return position2 ^ -1;
            }
            int maxEndPos = Math.min(position2 + effectiveMax, length);
            int pos2 = position2;
            int total = 0;
            while (true) {
                if (pos2 >= maxEndPos) {
                    CharSequence charSequence = text;
                    pos = pos2;
                    break;
                }
                int pos3 = pos2 + 1;
                int digit = context.getDecimalStyle().convertToDigit(text.charAt(pos2));
                if (digit >= 0) {
                    total = (total * 10) + digit;
                    pos2 = pos3;
                } else if (pos3 < minEndPos) {
                    return position2 ^ -1;
                } else {
                    pos = pos3 - 1;
                }
            }
            BigDecimal fraction = new BigDecimal(total).movePointLeft(pos - position2);
            BigDecimal bigDecimal = fraction;
            return context.setParsedField(this.field, convertFromFraction(fraction), position2, pos);
        }

        private BigDecimal convertToFraction(long value) {
            ValueRange range = this.field.range();
            range.checkValidValue(value, this.field);
            BigDecimal minBD = BigDecimal.valueOf(range.getMinimum());
            BigDecimal fraction = BigDecimal.valueOf(value).subtract(minBD).divide(BigDecimal.valueOf(range.getMaximum()).subtract(minBD).add(BigDecimal.ONE), 9, RoundingMode.FLOOR);
            return fraction.compareTo(BigDecimal.ZERO) == 0 ? BigDecimal.ZERO : fraction.stripTrailingZeros();
        }

        private long convertFromFraction(BigDecimal fraction) {
            ValueRange range = this.field.range();
            BigDecimal minBD = BigDecimal.valueOf(range.getMinimum());
            return fraction.multiply(BigDecimal.valueOf(range.getMaximum()).subtract(minBD).add(BigDecimal.ONE)).setScale(0, RoundingMode.FLOOR).add(minBD).longValueExact();
        }

        public String toString() {
            String decimal = this.decimalPoint ? ",DecimalPoint" : "";
            return "Fraction(" + this.field + "," + this.minWidth + "," + this.maxWidth + decimal + ")";
        }
    }

    static final class TextPrinterParser implements DateTimePrinterParser {
        private final TemporalField field;
        private volatile NumberPrinterParser numberPrinterParser;
        private final DateTimeTextProvider provider;
        private final TextStyle textStyle;

        TextPrinterParser(TemporalField field2, TextStyle textStyle2, DateTimeTextProvider provider2) {
            this.field = field2;
            this.textStyle = textStyle2;
            this.provider = provider2;
        }

        public boolean format(DateTimePrintContext context, StringBuilder buf) {
            String text;
            Long value = context.getValue(this.field);
            if (value == null) {
                return false;
            }
            Chronology chrono = (Chronology) context.getTemporal().query(TemporalQueries.chronology());
            if (chrono == null || chrono == IsoChronology.INSTANCE) {
                text = this.provider.getText(this.field, value.longValue(), this.textStyle, context.getLocale());
            } else {
                text = this.provider.getText(chrono, this.field, value.longValue(), this.textStyle, context.getLocale());
            }
            if (text == null) {
                return numberPrinterParser().format(context, buf);
            }
            buf.append(text);
            return true;
        }

        public int parse(DateTimeParseContext context, CharSequence parseText, int position) {
            Iterator<Map.Entry<String, Long>> it;
            int i = position;
            int length = parseText.length();
            if (i < 0 || i > length) {
                DateTimeParseContext dateTimeParseContext = context;
                CharSequence charSequence = parseText;
                throw new IndexOutOfBoundsException();
            }
            TextStyle style = context.isStrict() ? this.textStyle : null;
            Chronology chrono = context.getEffectiveChronology();
            if (chrono == null || chrono == IsoChronology.INSTANCE) {
                it = this.provider.getTextIterator(this.field, style, context.getLocale());
            } else {
                it = this.provider.getTextIterator(chrono, this.field, style, context.getLocale());
            }
            if (it != null) {
                while (it.hasNext()) {
                    Map.Entry<String, Long> entry = it.next();
                    String itText = entry.getKey();
                    if (context.subSequenceEquals(itText, 0, parseText, position, itText.length())) {
                        return context.setParsedField(this.field, entry.getValue().longValue(), position, i + itText.length());
                    }
                }
                if (context.isStrict()) {
                    return i ^ -1;
                }
            }
            DateTimeParseContext dateTimeParseContext2 = context;
            return numberPrinterParser().parse(context, parseText, i);
        }

        private NumberPrinterParser numberPrinterParser() {
            if (this.numberPrinterParser == null) {
                this.numberPrinterParser = new NumberPrinterParser(this.field, 1, 19, SignStyle.NORMAL);
            }
            return this.numberPrinterParser;
        }

        public String toString() {
            if (this.textStyle == TextStyle.FULL) {
                return "Text(" + this.field + ")";
            }
            return "Text(" + this.field + "," + this.textStyle + ")";
        }
    }

    static final class InstantPrinterParser implements DateTimePrinterParser {
        private static final long SECONDS_0000_TO_1970 = 62167219200L;
        private static final long SECONDS_PER_10000_YEARS = 315569520000L;
        private final int fractionalDigits;

        InstantPrinterParser(int fractionalDigits2) {
            this.fractionalDigits = fractionalDigits2;
        }

        public boolean format(DateTimePrintContext context, StringBuilder buf) {
            StringBuilder sb = buf;
            Long inSecs = context.getValue((TemporalField) ChronoField.INSTANT_SECONDS);
            Long inNanos = null;
            if (context.getTemporal().isSupported(ChronoField.NANO_OF_SECOND)) {
                inNanos = Long.valueOf(context.getTemporal().getLong(ChronoField.NANO_OF_SECOND));
            }
            if (inSecs == null) {
                return false;
            }
            long inSec = inSecs.longValue();
            int inNano = ChronoField.NANO_OF_SECOND.checkValidIntValue(inNanos != null ? inNanos.longValue() : 0);
            if (inSec >= -62167219200L) {
                long zeroSecs = (inSec - 315569520000L) + 62167219200L;
                long hi = Duration$$ExternalSyntheticBackport0.m(zeroSecs, 315569520000L) + 1;
                Long l = inSecs;
                Long l2 = inNanos;
                LocalDateTime ldt = LocalDateTime.ofEpochSecond(Clock$TickClock$$ExternalSyntheticBackport0.m(zeroSecs, 315569520000L) - 62167219200L, 0, ZoneOffset.UTC);
                if (hi > 0) {
                    sb.append('+');
                    sb.append(hi);
                }
                sb.append(ldt);
                if (ldt.getSecond() == 0) {
                    sb.append(":00");
                }
            } else {
                Long l3 = inNanos;
                long zeroSecs2 = inSec + 62167219200L;
                long hi2 = zeroSecs2 / 315569520000L;
                long lo = zeroSecs2 % 315569520000L;
                LocalDateTime ldt2 = LocalDateTime.ofEpochSecond(lo - 62167219200L, 0, ZoneOffset.UTC);
                int pos = buf.length();
                sb.append(ldt2);
                if (ldt2.getSecond() == 0) {
                    sb.append(":00");
                }
                if (hi2 < 0) {
                    if (ldt2.getYear() == -10000) {
                        sb.replace(pos, pos + 2, Long.toString(hi2 - 1));
                    } else if (lo == 0) {
                        sb.insert(pos, hi2);
                    } else {
                        sb.insert(pos + 1, Math.abs(hi2));
                    }
                }
            }
            int i = this.fractionalDigits;
            if ((i < 0 && inNano > 0) || i > 0) {
                sb.append('.');
                int div = NUM;
                int i2 = 0;
                while (true) {
                    int i3 = this.fractionalDigits;
                    if ((i3 != -1 || inNano <= 0) && ((i3 != -2 || (inNano <= 0 && i2 % 3 == 0)) && i2 >= i3)) {
                        break;
                    }
                    int digit = inNano / div;
                    sb.append((char) (digit + 48));
                    inNano -= digit * div;
                    div /= 10;
                    i2++;
                }
            }
            sb.append('Z');
            return true;
        }

        public int parse(DateTimeParseContext context, CharSequence text, int position) {
            int sec;
            int hour;
            int sec2;
            LocalDateTime ldt;
            int nano;
            int i = position;
            int maxDigits = this.fractionalDigits;
            int nano2 = 0;
            int minDigits = maxDigits < 0 ? 0 : maxDigits;
            if (maxDigits < 0) {
                maxDigits = 9;
            }
            CompositePrinterParser parser = new DateTimeFormatterBuilder().append(DateTimeFormatter.ISO_LOCAL_DATE).appendLiteral('T').appendValue(ChronoField.HOUR_OF_DAY, 2).appendLiteral(':').appendValue(ChronoField.MINUTE_OF_HOUR, 2).appendLiteral(':').appendValue(ChronoField.SECOND_OF_MINUTE, 2).appendFraction(ChronoField.NANO_OF_SECOND, minDigits, maxDigits, true).appendLiteral('Z').toFormatter().toPrinterParser(false);
            DateTimeParseContext newContext = context.copy();
            int pos = parser.parse(newContext, text, i);
            if (pos < 0) {
                return pos;
            }
            long yearParsed = newContext.getParsed(ChronoField.YEAR).longValue();
            int month = newContext.getParsed(ChronoField.MONTH_OF_YEAR).intValue();
            int day = newContext.getParsed(ChronoField.DAY_OF_MONTH).intValue();
            int hour2 = newContext.getParsed(ChronoField.HOUR_OF_DAY).intValue();
            int min = newContext.getParsed(ChronoField.MINUTE_OF_HOUR).intValue();
            Long secVal = newContext.getParsed(ChronoField.SECOND_OF_MINUTE);
            Long nanoVal = newContext.getParsed(ChronoField.NANO_OF_SECOND);
            int sec3 = secVal != null ? secVal.intValue() : 0;
            if (nanoVal != null) {
                nano2 = nanoVal.intValue();
            }
            if (hour2 == 24 && min == 0 && sec3 == 0 && nano2 == 0) {
                hour = 0;
                sec = sec3;
                sec2 = 1;
            } else if (hour2 == 23 && min == 59 && sec3 == 60) {
                context.setParsedLeapSecond();
                hour = hour2;
                sec = 59;
                sec2 = 0;
            } else {
                hour = hour2;
                sec = sec3;
                sec2 = 0;
            }
            int year = ((int) yearParsed) % 10000;
            try {
                int i2 = min;
                int i3 = year;
                try {
                    ldt = LocalDateTime.of(year, month, day, hour, min, sec, 0).plusDays((long) sec2);
                    LocalDateTime localDateTime = ldt;
                    nano = nano2;
                } catch (RuntimeException e) {
                    int i4 = nano2;
                    int i5 = sec2;
                    long j = yearParsed;
                    return i ^ -1;
                }
                try {
                    long j2 = yearParsed;
                    int days = sec2;
                    try {
                        int nano3 = nano;
                        DateTimeParseContext dateTimeParseContext = context;
                        int i6 = days;
                        int nano4 = nano3;
                        int nano5 = position;
                        return dateTimeParseContext.setParsedField(ChronoField.NANO_OF_SECOND, (long) nano4, nano5, dateTimeParseContext.setParsedField(ChronoField.INSTANT_SECONDS, ldt.toEpochSecond(ZoneOffset.UTC) + Duration$$ExternalSyntheticBackport1.m(yearParsed / 10000, 315569520000L), nano5, pos));
                    } catch (RuntimeException e2) {
                        int i7 = nano;
                        int nano6 = days;
                        return i ^ -1;
                    }
                } catch (RuntimeException e3) {
                    long j3 = yearParsed;
                    int i8 = nano;
                    int nano7 = sec2;
                    return i ^ -1;
                }
            } catch (RuntimeException e4) {
                int i9 = nano2;
                int i10 = sec2;
                long j4 = yearParsed;
                int i11 = min;
                int i12 = year;
                return i ^ -1;
            }
        }

        public String toString() {
            return "Instant()";
        }
    }

    static final class OffsetIdPrinterParser implements DateTimePrinterParser {
        static final OffsetIdPrinterParser INSTANCE_ID_Z = new OffsetIdPrinterParser("+HH:MM:ss", "Z");
        static final OffsetIdPrinterParser INSTANCE_ID_ZERO = new OffsetIdPrinterParser("+HH:MM:ss", "0");
        static final String[] PATTERNS = {"+HH", "+HHmm", "+HH:mm", "+HHMM", "+HH:MM", "+HHMMss", "+HH:MM:ss", "+HHMMSS", "+HH:MM:SS"};
        private final String noOffsetText;
        private final int type;

        OffsetIdPrinterParser(String pattern, String noOffsetText2) {
            Objects.requireNonNull(pattern, "pattern");
            Objects.requireNonNull(noOffsetText2, "noOffsetText");
            this.type = checkPattern(pattern);
            this.noOffsetText = noOffsetText2;
        }

        private int checkPattern(String pattern) {
            int i = 0;
            while (true) {
                String[] strArr = PATTERNS;
                if (i >= strArr.length) {
                    throw new IllegalArgumentException("Invalid zone offset pattern: " + pattern);
                } else if (strArr[i].equals(pattern)) {
                    return i;
                } else {
                    i++;
                }
            }
        }

        public boolean format(DateTimePrintContext context, StringBuilder buf) {
            Long offsetSecs = context.getValue((TemporalField) ChronoField.OFFSET_SECONDS);
            if (offsetSecs == null) {
                return false;
            }
            int totalSecs = LocalDate$$ExternalSyntheticBackport0.m(offsetSecs.longValue());
            if (totalSecs == 0) {
                buf.append(this.noOffsetText);
            } else {
                int absHours = Math.abs((totalSecs / 3600) % 100);
                int absMinutes = Math.abs((totalSecs / 60) % 60);
                int absSeconds = Math.abs(totalSecs % 60);
                int bufPos = buf.length();
                int output = absHours;
                buf.append(totalSecs < 0 ? "-" : "+");
                buf.append((char) ((absHours / 10) + 48));
                buf.append((char) ((absHours % 10) + 48));
                int i = this.type;
                if (i >= 3 || (i >= 1 && absMinutes > 0)) {
                    String str = ":";
                    buf.append(i % 2 == 0 ? str : "");
                    buf.append((char) ((absMinutes / 10) + 48));
                    buf.append((char) ((absMinutes % 10) + 48));
                    output += absMinutes;
                    int i2 = this.type;
                    if (i2 >= 7 || (i2 >= 5 && absSeconds > 0)) {
                        if (i2 % 2 != 0) {
                            str = "";
                        }
                        buf.append(str);
                        buf.append((char) ((absSeconds / 10) + 48));
                        buf.append((char) ((absSeconds % 10) + 48));
                        output += absSeconds;
                    }
                }
                if (output == 0) {
                    buf.setLength(bufPos);
                    buf.append(this.noOffsetText);
                }
            }
            return true;
        }

        /* JADX WARNING: Removed duplicated region for block: B:32:0x0083  */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public int parse(j$.time.format.DateTimeParseContext r19, java.lang.CharSequence r20, int r21) {
            /*
                r18 = this;
                r0 = r18
                r7 = r20
                r8 = r21
                int r9 = r20.length()
                java.lang.String r1 = r0.noOffsetText
                int r10 = r1.length()
                if (r10 != 0) goto L_0x0023
                if (r8 != r9) goto L_0x0047
                j$.time.temporal.ChronoField r2 = j$.time.temporal.ChronoField.OFFSET_SECONDS
                r3 = 0
                r1 = r19
                r5 = r21
                r6 = r21
                int r1 = r1.setParsedField(r2, r3, r5, r6)
                return r1
            L_0x0023:
                if (r8 != r9) goto L_0x0028
                r1 = r8 ^ -1
                return r1
            L_0x0028:
                java.lang.String r4 = r0.noOffsetText
                r5 = 0
                r1 = r19
                r2 = r20
                r3 = r21
                r6 = r10
                boolean r1 = r1.subSequenceEquals(r2, r3, r4, r5, r6)
                if (r1 == 0) goto L_0x0047
                j$.time.temporal.ChronoField r2 = j$.time.temporal.ChronoField.OFFSET_SECONDS
                int r6 = r8 + r10
                r3 = 0
                r1 = r19
                r5 = r21
                int r1 = r1.setParsedField(r2, r3, r5, r6)
                return r1
            L_0x0047:
                char r11 = r20.charAt(r21)
                r1 = 43
                r2 = 45
                if (r11 == r1) goto L_0x0053
                if (r11 != r2) goto L_0x00a8
            L_0x0053:
                r1 = 1
                if (r11 != r2) goto L_0x0058
                r2 = -1
                goto L_0x0059
            L_0x0058:
                r2 = 1
            L_0x0059:
                r12 = r2
                r2 = 4
                int[] r13 = new int[r2]
                int r2 = r8 + 1
                r3 = 0
                r13[r3] = r2
                boolean r2 = r0.parseNumber(r13, r1, r7, r1)
                r4 = 2
                r5 = 3
                if (r2 != 0) goto L_0x0080
                int r2 = r0.type
                if (r2 < r5) goto L_0x0070
                r2 = 1
                goto L_0x0071
            L_0x0070:
                r2 = 0
            L_0x0071:
                boolean r2 = r0.parseNumber(r13, r4, r7, r2)
                if (r2 != 0) goto L_0x0080
                boolean r2 = r0.parseNumber(r13, r5, r7, r3)
                if (r2 == 0) goto L_0x007e
                goto L_0x0080
            L_0x007e:
                r2 = 0
                goto L_0x0081
            L_0x0080:
                r2 = 1
            L_0x0081:
                if (r2 != 0) goto L_0x00a8
                long r14 = (long) r12
                r1 = r13[r1]
                long r1 = (long) r1
                r16 = 3600(0xe10, double:1.7786E-320)
                long r1 = r1 * r16
                r4 = r13[r4]
                long r3 = (long) r4
                r16 = 60
                long r3 = r3 * r16
                long r1 = r1 + r3
                r3 = r13[r5]
                long r3 = (long) r3
                long r1 = r1 + r3
                long r14 = r14 * r1
                j$.time.temporal.ChronoField r2 = j$.time.temporal.ChronoField.OFFSET_SECONDS
                r1 = 0
                r6 = r13[r1]
                r1 = r19
                r3 = r14
                r5 = r21
                int r1 = r1.setParsedField(r2, r3, r5, r6)
                return r1
            L_0x00a8:
                if (r10 != 0) goto L_0x00b9
                j$.time.temporal.ChronoField r2 = j$.time.temporal.ChronoField.OFFSET_SECONDS
                int r6 = r8 + r10
                r3 = 0
                r1 = r19
                r5 = r21
                int r1 = r1.setParsedField(r2, r3, r5, r6)
                return r1
            L_0x00b9:
                r1 = r8 ^ -1
                return r1
            */
            throw new UnsupportedOperationException("Method not decompiled: j$.time.format.DateTimeFormatterBuilder.OffsetIdPrinterParser.parse(j$.time.format.DateTimeParseContext, java.lang.CharSequence, int):int");
        }

        private boolean parseNumber(int[] array, int arrayIndex, CharSequence parseText, boolean required) {
            int value;
            int i = this.type;
            if ((i + 3) / 2 < arrayIndex) {
                return false;
            }
            int pos = array[0];
            if (i % 2 == 0 && arrayIndex > 1) {
                if (pos + 1 > parseText.length() || parseText.charAt(pos) != ':') {
                    return required;
                }
                pos++;
            }
            if (pos + 2 > parseText.length()) {
                return required;
            }
            int pos2 = pos + 1;
            char ch1 = parseText.charAt(pos);
            int pos3 = pos2 + 1;
            int pos4 = parseText.charAt(pos2);
            if (ch1 < '0' || ch1 > '9' || pos4 < 48 || pos4 > 57 || (value = ((ch1 - '0') * 10) + (pos4 - 48)) < 0 || value > 59) {
                return required;
            }
            array[arrayIndex] = value;
            array[0] = pos3;
            return false;
        }

        public String toString() {
            String converted = this.noOffsetText.replace("'", "''");
            return "Offset(" + PATTERNS[this.type] + ",'" + converted + "')";
        }
    }

    static final class LocalizedOffsetIdPrinterParser implements DateTimePrinterParser {
        private final TextStyle style;

        LocalizedOffsetIdPrinterParser(TextStyle style2) {
            this.style = style2;
        }

        private static StringBuilder appendHMS(StringBuilder buf, int t) {
            buf.append((char) ((t / 10) + 48));
            buf.append((char) ((t % 10) + 48));
            return buf;
        }

        public boolean format(DateTimePrintContext context, StringBuilder buf) {
            Long offsetSecs = context.getValue((TemporalField) ChronoField.OFFSET_SECONDS);
            if (offsetSecs == null) {
                return false;
            }
            buf.append("GMT");
            int totalSecs = LocalDate$$ExternalSyntheticBackport0.m(offsetSecs.longValue());
            if (totalSecs == 0) {
                return true;
            }
            int absHours = Math.abs((totalSecs / 3600) % 100);
            int absMinutes = Math.abs((totalSecs / 60) % 60);
            int absSeconds = Math.abs(totalSecs % 60);
            buf.append(totalSecs < 0 ? "-" : "+");
            if (this.style == TextStyle.FULL) {
                appendHMS(buf, absHours);
                buf.append(':');
                appendHMS(buf, absMinutes);
                if (absSeconds == 0) {
                    return true;
                }
                buf.append(':');
                appendHMS(buf, absSeconds);
                return true;
            }
            if (absHours >= 10) {
                buf.append((char) ((absHours / 10) + 48));
            }
            buf.append((char) ((absHours % 10) + 48));
            if (absMinutes == 0 && absSeconds == 0) {
                return true;
            }
            buf.append(':');
            appendHMS(buf, absMinutes);
            if (absSeconds == 0) {
                return true;
            }
            buf.append(':');
            appendHMS(buf, absSeconds);
            return true;
        }

        /* access modifiers changed from: package-private */
        public int getDigit(CharSequence text, int position) {
            char c = text.charAt(position);
            if (c < '0' || c > '9') {
                return -1;
            }
            return c - '0';
        }

        public int parse(DateTimeParseContext context, CharSequence text, int position) {
            int negative;
            int pos;
            int pos2;
            int m1;
            int h;
            CharSequence charSequence = text;
            int pos3 = position;
            int end = pos3 + text.length();
            if (!context.subSequenceEquals(text, pos3, "GMT", 0, "GMT".length())) {
                return position ^ -1;
            }
            int pos4 = pos3 + "GMT".length();
            if (pos4 == end) {
                return context.setParsedField(ChronoField.OFFSET_SECONDS, 0, position, pos4);
            }
            char sign = charSequence.charAt(pos4);
            if (sign == '+') {
                negative = 1;
            } else if (sign == '-') {
                negative = -1;
            } else {
                return context.setParsedField(ChronoField.OFFSET_SECONDS, 0, position, pos4);
            }
            int pos5 = pos4 + 1;
            int m = 0;
            int s = 0;
            if (this.style == TextStyle.FULL) {
                int pos6 = pos5 + 1;
                int h1 = getDigit(charSequence, pos5);
                int pos7 = pos6 + 1;
                int h2 = getDigit(charSequence, pos6);
                if (h1 >= 0 && h2 >= 0) {
                    int pos8 = pos7 + 1;
                    if (charSequence.charAt(pos7) != 58) {
                        int i = pos8;
                    } else {
                        int h3 = (h1 * 10) + h2;
                        int pos9 = pos8 + 1;
                        int pos10 = getDigit(charSequence, pos8);
                        int pos11 = pos9 + 1;
                        int m2 = getDigit(charSequence, pos9);
                        if (pos10 < 0 || m2 < 0) {
                            return position ^ -1;
                        }
                        int m3 = (pos10 * 10) + m2;
                        if (pos11 + 2 < end && charSequence.charAt(pos11) == ':') {
                            int s1 = getDigit(charSequence, pos11 + 1);
                            int s2 = getDigit(charSequence, pos11 + 2);
                            if (s1 >= 0 && s2 >= 0) {
                                s = (s1 * 10) + s2;
                                pos11 += 3;
                            }
                        }
                        m1 = s;
                        pos = pos11;
                        pos2 = h3;
                        h = m3;
                    }
                }
                return position ^ -1;
            }
            int pos12 = pos5 + 1;
            int h4 = getDigit(charSequence, pos5);
            if (h4 < 0) {
                return position ^ -1;
            }
            if (pos12 < end) {
                int h22 = getDigit(charSequence, pos12);
                if (h22 >= 0) {
                    pos12++;
                    h4 = (h4 * 10) + h22;
                }
                if (pos12 + 2 < end && charSequence.charAt(pos12) == ':' && pos12 + 2 < end && charSequence.charAt(pos12) == ':') {
                    int m12 = getDigit(charSequence, pos12 + 1);
                    int m22 = getDigit(charSequence, pos12 + 2);
                    if (m12 >= 0 && m22 >= 0) {
                        m = (m12 * 10) + m22;
                        pos12 += 3;
                        if (pos12 + 2 < end && charSequence.charAt(pos12) == ':') {
                            int s12 = getDigit(charSequence, pos12 + 1);
                            int s22 = getDigit(charSequence, pos12 + 2);
                            if (s12 >= 0 && s22 >= 0) {
                                m1 = (s12 * 10) + s22;
                                pos = pos12 + 3;
                                pos2 = h4;
                                h = m;
                            }
                        }
                    }
                }
                m1 = 0;
                pos = pos12;
                pos2 = h4;
                h = m;
            } else {
                m1 = 0;
                pos = pos12;
                pos2 = h4;
                h = 0;
            }
            return context.setParsedField(ChronoField.OFFSET_SECONDS, ((long) negative) * ((((long) pos2) * 3600) + (((long) h) * 60) + ((long) m1)), position, pos);
        }

        public String toString() {
            return "LocalizedOffset(" + this.style + ")";
        }
    }

    static final class ZoneTextPrinterParser extends ZoneIdPrinterParser {
        private static final int DST = 1;
        private static final int GENERIC = 2;
        private static final int STD = 0;
        private static final Map<String, SoftReference<Map<Locale, String[]>>> cache = new ConcurrentHashMap();
        private final Map<Locale, Map.Entry<Integer, SoftReference<PrefixTree>>> cachedTree = new HashMap();
        private final Map<Locale, Map.Entry<Integer, SoftReference<PrefixTree>>> cachedTreeCI = new HashMap();
        private Set<String> preferredZones;
        private final TextStyle textStyle;

        /* JADX WARNING: Illegal instructions before constructor call */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        ZoneTextPrinterParser(j$.time.format.TextStyle r5, java.util.Set<j$.time.ZoneId> r6) {
            /*
                r4 = this;
                j$.time.temporal.TemporalQuery r0 = j$.time.temporal.TemporalQueries.zone()
                java.lang.StringBuilder r1 = new java.lang.StringBuilder
                r1.<init>()
                java.lang.String r2 = "ZoneText("
                r1.append(r2)
                r1.append(r5)
                java.lang.String r2 = ")"
                r1.append(r2)
                java.lang.String r1 = r1.toString()
                r4.<init>(r0, r1)
                java.util.HashMap r0 = new java.util.HashMap
                r0.<init>()
                r4.cachedTree = r0
                java.util.HashMap r0 = new java.util.HashMap
                r0.<init>()
                r4.cachedTreeCI = r0
                java.lang.String r0 = "textStyle"
                java.lang.Object r0 = j$.util.Objects.requireNonNull(r5, (java.lang.String) r0)
                j$.time.format.TextStyle r0 = (j$.time.format.TextStyle) r0
                r4.textStyle = r0
                if (r6 == 0) goto L_0x005e
                int r0 = r6.size()
                if (r0 == 0) goto L_0x005e
                java.util.HashSet r0 = new java.util.HashSet
                r0.<init>()
                r4.preferredZones = r0
                java.util.Iterator r0 = r6.iterator()
            L_0x0048:
                boolean r1 = r0.hasNext()
                if (r1 == 0) goto L_0x005e
                java.lang.Object r1 = r0.next()
                j$.time.ZoneId r1 = (j$.time.ZoneId) r1
                java.util.Set<java.lang.String> r2 = r4.preferredZones
                java.lang.String r3 = r1.getId()
                r2.add(r3)
                goto L_0x0048
            L_0x005e:
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: j$.time.format.DateTimeFormatterBuilder.ZoneTextPrinterParser.<init>(j$.time.format.TextStyle, java.util.Set):void");
        }

        /* JADX WARNING: Code restructure failed: missing block: B:8:0x0026, code lost:
            if (r6 == null) goto L_0x0028;
         */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        private java.lang.String getDisplayName(java.lang.String r12, int r13, java.util.Locale r14) {
            /*
                r11 = this;
                j$.time.format.TextStyle r0 = r11.textStyle
                j$.time.format.TextStyle r1 = j$.time.format.TextStyle.NARROW
                if (r0 != r1) goto L_0x0008
                r0 = 0
                return r0
            L_0x0008:
                java.util.Map<java.lang.String, java.lang.ref.SoftReference<java.util.Map<java.util.Locale, java.lang.String[]>>> r0 = cache
                java.lang.Object r1 = r0.get(r12)
                java.lang.ref.SoftReference r1 = (java.lang.ref.SoftReference) r1
                r2 = 0
                r3 = 5
                r4 = 3
                r5 = 1
                if (r1 == 0) goto L_0x0028
                java.lang.Object r6 = r1.get()
                java.util.Map r6 = (java.util.Map) r6
                r2 = r6
                if (r6 == 0) goto L_0x0028
                java.lang.Object r6 = r2.get(r14)
                java.lang.String[] r6 = (java.lang.String[]) r6
                r7 = r6
                if (r6 != 0) goto L_0x0064
            L_0x0028:
                java.util.TimeZone r6 = java.util.TimeZone.getTimeZone(r12)
                r7 = 7
                java.lang.String[] r7 = new java.lang.String[r7]
                r8 = 0
                r7[r8] = r12
                java.lang.String r9 = r6.getDisplayName(r8, r5, r14)
                r7[r5] = r9
                java.lang.String r9 = r6.getDisplayName(r8, r8, r14)
                r10 = 2
                r7[r10] = r9
                java.lang.String r9 = r6.getDisplayName(r5, r5, r14)
                r7[r4] = r9
                java.lang.String r8 = r6.getDisplayName(r5, r8, r14)
                r9 = 4
                r7[r9] = r8
                r7[r3] = r12
                r8 = 6
                r7[r8] = r12
                if (r2 != 0) goto L_0x0059
                j$.util.concurrent.ConcurrentHashMap r8 = new j$.util.concurrent.ConcurrentHashMap
                r8.<init>()
                r2 = r8
            L_0x0059:
                r2.put(r14, r7)
                java.lang.ref.SoftReference r8 = new java.lang.ref.SoftReference
                r8.<init>(r2)
                r0.put(r12, r8)
            L_0x0064:
                switch(r13) {
                    case 0: goto L_0x007b;
                    case 1: goto L_0x0071;
                    default: goto L_0x0067;
                }
            L_0x0067:
                j$.time.format.TextStyle r0 = r11.textStyle
                int r0 = r0.zoneNameStyleIndex()
                int r0 = r0 + r3
                r0 = r7[r0]
                return r0
            L_0x0071:
                j$.time.format.TextStyle r0 = r11.textStyle
                int r0 = r0.zoneNameStyleIndex()
                int r0 = r0 + r4
                r0 = r7[r0]
                return r0
            L_0x007b:
                j$.time.format.TextStyle r0 = r11.textStyle
                int r0 = r0.zoneNameStyleIndex()
                int r0 = r0 + r5
                r0 = r7[r0]
                return r0
            */
            throw new UnsupportedOperationException("Method not decompiled: j$.time.format.DateTimeFormatterBuilder.ZoneTextPrinterParser.getDisplayName(java.lang.String, int, java.util.Locale):java.lang.String");
        }

        public boolean format(DateTimePrintContext context, StringBuilder buf) {
            ZoneId zone = (ZoneId) context.getValue(TemporalQueries.zoneId());
            int i = 0;
            if (zone == null) {
                return false;
            }
            String zname = zone.getId();
            if (!(zone instanceof ZoneOffset)) {
                TemporalAccessor dt = context.getTemporal();
                if (!dt.isSupported(ChronoField.INSTANT_SECONDS)) {
                    i = 2;
                } else if (zone.getRules().isDaylightSavings(Instant.from(dt))) {
                    i = 1;
                }
                String name = getDisplayName(zname, i, context.getLocale());
                if (name != null) {
                    zname = name;
                }
            }
            buf.append(zname);
            return true;
        }

        /* access modifiers changed from: protected */
        /* JADX WARNING: Code restructure failed: missing block: B:13:0x0049, code lost:
            if (r9 == null) goto L_0x004b;
         */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public j$.time.format.DateTimeFormatterBuilder.PrefixTree getTree(j$.time.format.DateTimeParseContext r18) {
            /*
                r17 = this;
                r0 = r17
                j$.time.format.TextStyle r1 = r0.textStyle
                j$.time.format.TextStyle r2 = j$.time.format.TextStyle.NARROW
                if (r1 != r2) goto L_0x000d
                j$.time.format.DateTimeFormatterBuilder$PrefixTree r1 = super.getTree(r18)
                return r1
            L_0x000d:
                java.util.Locale r1 = r18.getLocale()
                boolean r2 = r18.isCaseSensitive()
                java.util.Set r3 = j$.time.zone.ZoneRulesProvider.getAvailableZoneIds()
                int r4 = r3.size()
                if (r2 == 0) goto L_0x0022
                java.util.Map<java.util.Locale, java.util.Map$Entry<java.lang.Integer, java.lang.ref.SoftReference<j$.time.format.DateTimeFormatterBuilder$PrefixTree>>> r5 = r0.cachedTree
                goto L_0x0024
            L_0x0022:
                java.util.Map<java.util.Locale, java.util.Map$Entry<java.lang.Integer, java.lang.ref.SoftReference<j$.time.format.DateTimeFormatterBuilder$PrefixTree>>> r5 = r0.cachedTreeCI
            L_0x0024:
                r6 = 0
                r7 = 0
                r8 = 0
                java.lang.Object r9 = r5.get(r1)
                java.util.Map$Entry r9 = (java.util.Map.Entry) r9
                r6 = r9
                if (r9 == 0) goto L_0x004b
                java.lang.Object r9 = r6.getKey()
                java.lang.Integer r9 = (java.lang.Integer) r9
                int r9 = r9.intValue()
                if (r9 != r4) goto L_0x004b
                java.lang.Object r9 = r6.getValue()
                java.lang.ref.SoftReference r9 = (java.lang.ref.SoftReference) r9
                java.lang.Object r9 = r9.get()
                j$.time.format.DateTimeFormatterBuilder$PrefixTree r9 = (j$.time.format.DateTimeFormatterBuilder.PrefixTree) r9
                r7 = r9
                if (r9 != 0) goto L_0x00c8
            L_0x004b:
                j$.time.format.DateTimeFormatterBuilder$PrefixTree r7 = j$.time.format.DateTimeFormatterBuilder.PrefixTree.newTree(r18)
                java.text.DateFormatSymbols r9 = java.text.DateFormatSymbols.getInstance(r1)
                java.lang.String[][] r8 = r9.getZoneStrings()
                int r9 = r8.length
                r10 = 0
                r11 = 0
            L_0x005a:
                if (r11 >= r9) goto L_0x0085
                r14 = r8[r11]
                r15 = r14[r10]
                boolean r16 = r3.contains(r15)
                if (r16 != 0) goto L_0x0067
                goto L_0x0082
            L_0x0067:
                r7.add(r15, r15)
                java.lang.String r15 = j$.time.format.ZoneName.toZid(r15, r1)
                j$.time.format.TextStyle r12 = r0.textStyle
                j$.time.format.TextStyle r13 = j$.time.format.TextStyle.FULL
                if (r12 != r13) goto L_0x0076
                r12 = 1
                goto L_0x0077
            L_0x0076:
                r12 = 2
            L_0x0077:
                int r13 = r14.length
                if (r12 >= r13) goto L_0x0082
                r13 = r14[r12]
                r7.add(r13, r15)
                int r12 = r12 + 2
                goto L_0x0077
            L_0x0082:
                int r11 = r11 + 1
                goto L_0x005a
            L_0x0085:
                java.util.Set<java.lang.String> r9 = r0.preferredZones
                if (r9 == 0) goto L_0x00b7
                int r9 = r8.length
                r11 = 0
            L_0x008b:
                if (r11 >= r9) goto L_0x00b7
                r12 = r8[r11]
                r13 = r12[r10]
                java.util.Set<java.lang.String> r14 = r0.preferredZones
                boolean r14 = r14.contains(r13)
                if (r14 == 0) goto L_0x00b4
                boolean r14 = r3.contains(r13)
                if (r14 != 0) goto L_0x00a0
                goto L_0x00b4
            L_0x00a0:
                j$.time.format.TextStyle r14 = r0.textStyle
                j$.time.format.TextStyle r15 = j$.time.format.TextStyle.FULL
                if (r14 != r15) goto L_0x00a8
                r14 = 1
                goto L_0x00a9
            L_0x00a8:
                r14 = 2
            L_0x00a9:
                int r15 = r12.length
                if (r14 >= r15) goto L_0x00b4
                r15 = r12[r14]
                r7.add(r15, r13)
                int r14 = r14 + 2
                goto L_0x00a9
            L_0x00b4:
                int r11 = r11 + 1
                goto L_0x008b
            L_0x00b7:
                java.util.AbstractMap$SimpleImmutableEntry r9 = new java.util.AbstractMap$SimpleImmutableEntry
                java.lang.Integer r10 = java.lang.Integer.valueOf(r4)
                java.lang.ref.SoftReference r11 = new java.lang.ref.SoftReference
                r11.<init>(r7)
                r9.<init>(r10, r11)
                r5.put(r1, r9)
            L_0x00c8:
                return r7
            */
            throw new UnsupportedOperationException("Method not decompiled: j$.time.format.DateTimeFormatterBuilder.ZoneTextPrinterParser.getTree(j$.time.format.DateTimeParseContext):j$.time.format.DateTimeFormatterBuilder$PrefixTree");
        }
    }

    static class ZoneIdPrinterParser implements DateTimePrinterParser {
        private static volatile Map.Entry<Integer, PrefixTree> cachedPrefixTree;
        private static volatile Map.Entry<Integer, PrefixTree> cachedPrefixTreeCI;
        private final String description;
        private final TemporalQuery<ZoneId> query;

        ZoneIdPrinterParser(TemporalQuery<ZoneId> temporalQuery, String description2) {
            this.query = temporalQuery;
            this.description = description2;
        }

        public boolean format(DateTimePrintContext context, StringBuilder buf) {
            ZoneId zone = (ZoneId) context.getValue(this.query);
            if (zone == null) {
                return false;
            }
            buf.append(zone.getId());
            return true;
        }

        /* access modifiers changed from: protected */
        public PrefixTree getTree(DateTimeParseContext context) {
            Set<String> regionIds = ZoneRulesProvider.getAvailableZoneIds();
            int regionIdsSize = regionIds.size();
            Map.Entry<Integer, PrefixTree> entry = context.isCaseSensitive() ? cachedPrefixTree : cachedPrefixTreeCI;
            if (entry == null || entry.getKey().intValue() != regionIdsSize) {
                synchronized (this) {
                    AbstractMap.SimpleImmutableEntry simpleImmutableEntry = context.isCaseSensitive() ? cachedPrefixTree : cachedPrefixTreeCI;
                    if (simpleImmutableEntry == null || simpleImmutableEntry.getKey().intValue() != regionIdsSize) {
                        simpleImmutableEntry = new AbstractMap.SimpleImmutableEntry(Integer.valueOf(regionIdsSize), PrefixTree.newTree(regionIds, context));
                        if (context.isCaseSensitive()) {
                            cachedPrefixTree = simpleImmutableEntry;
                        } else {
                            cachedPrefixTreeCI = simpleImmutableEntry;
                        }
                    }
                }
            }
            return entry.getValue();
        }

        public int parse(DateTimeParseContext context, CharSequence text, int position) {
            int length = text.length();
            if (position > length) {
                throw new IndexOutOfBoundsException();
            } else if (position == length) {
                return position ^ -1;
            } else {
                char nextChar = text.charAt(position);
                if (nextChar == '+' || nextChar == '-') {
                    return parseOffsetBased(context, text, position, position, OffsetIdPrinterParser.INSTANCE_ID_Z);
                }
                if (length >= position + 2) {
                    char nextNextChar = text.charAt(position + 1);
                    if (!context.charEquals(nextChar, 'U') || !context.charEquals(nextNextChar, 'T')) {
                        if (context.charEquals(nextChar, 'G') && length >= position + 3 && context.charEquals(nextNextChar, 'M') && context.charEquals(text.charAt(position + 2), 'T')) {
                            return parseOffsetBased(context, text, position, position + 3, OffsetIdPrinterParser.INSTANCE_ID_ZERO);
                        }
                    } else if (length < position + 3 || !context.charEquals(text.charAt(position + 2), 'C')) {
                        return parseOffsetBased(context, text, position, position + 2, OffsetIdPrinterParser.INSTANCE_ID_ZERO);
                    } else {
                        return parseOffsetBased(context, text, position, position + 3, OffsetIdPrinterParser.INSTANCE_ID_ZERO);
                    }
                }
                PrefixTree tree = getTree(context);
                ParsePosition ppos = new ParsePosition(position);
                String parsedZoneId = tree.match(text, ppos);
                if (parsedZoneId != null) {
                    context.setParsed(ZoneId.of(parsedZoneId));
                    return ppos.getIndex();
                } else if (!context.charEquals(nextChar, 'Z')) {
                    return position ^ -1;
                } else {
                    context.setParsed((ZoneId) ZoneOffset.UTC);
                    return position + 1;
                }
            }
        }

        private int parseOffsetBased(DateTimeParseContext context, CharSequence text, int prefixPos, int position, OffsetIdPrinterParser parser) {
            String prefix = text.toString().substring(prefixPos, position).toUpperCase();
            if (position >= text.length()) {
                context.setParsed(ZoneId.of(prefix));
                return position;
            } else if (text.charAt(position) == '0' || context.charEquals(text.charAt(position), 'Z')) {
                context.setParsed(ZoneId.of(prefix));
                return position;
            } else {
                DateTimeParseContext newContext = context.copy();
                int endPos = parser.parse(newContext, text, position);
                if (endPos < 0) {
                    try {
                        if (parser == OffsetIdPrinterParser.INSTANCE_ID_Z) {
                            return prefixPos ^ -1;
                        }
                        context.setParsed(ZoneId.of(prefix));
                        return position;
                    } catch (DateTimeException e) {
                        return prefixPos ^ -1;
                    }
                } else {
                    context.setParsed(ZoneId.ofOffset(prefix, ZoneOffset.ofTotalSeconds((int) newContext.getParsed(ChronoField.OFFSET_SECONDS).longValue())));
                    return endPos;
                }
            }
        }

        public String toString() {
            return this.description;
        }
    }

    static class PrefixTree {
        protected char c0;
        protected PrefixTree child;
        protected String key;
        protected PrefixTree sibling;
        protected String value;

        private PrefixTree(String k, String v, PrefixTree child2) {
            this.key = k;
            this.value = v;
            this.child = child2;
            if (k.length() == 0) {
                this.c0 = 65535;
            } else {
                this.c0 = this.key.charAt(0);
            }
        }

        public static PrefixTree newTree(DateTimeParseContext context) {
            if (context.isCaseSensitive()) {
                return new PrefixTree("", (String) null, (PrefixTree) null);
            }
            return new CI("", (String) null, (PrefixTree) null);
        }

        public static PrefixTree newTree(Set<String> keys, DateTimeParseContext context) {
            PrefixTree tree = newTree(context);
            for (String k : keys) {
                tree.add0(k, k);
            }
            return tree;
        }

        public PrefixTree copyTree() {
            PrefixTree copy = new PrefixTree(this.key, this.value, (PrefixTree) null);
            PrefixTree prefixTree = this.child;
            if (prefixTree != null) {
                copy.child = prefixTree.copyTree();
            }
            PrefixTree prefixTree2 = this.sibling;
            if (prefixTree2 != null) {
                copy.sibling = prefixTree2.copyTree();
            }
            return copy;
        }

        public boolean add(String k, String v) {
            return add0(k, v);
        }

        private boolean add0(String k, String v) {
            String k2 = toKey(k);
            int prefixLen = prefixLength(k2);
            if (prefixLen != this.key.length()) {
                PrefixTree n1 = newNode(this.key.substring(prefixLen), this.value, this.child);
                this.key = k2.substring(0, prefixLen);
                this.child = n1;
                if (prefixLen < k2.length()) {
                    this.child.sibling = newNode(k2.substring(prefixLen), v, (PrefixTree) null);
                    this.value = null;
                } else {
                    this.value = v;
                }
                return true;
            } else if (prefixLen < k2.length()) {
                String subKey = k2.substring(prefixLen);
                for (PrefixTree c = this.child; c != null; c = c.sibling) {
                    if (isEqual(c.c0, subKey.charAt(0))) {
                        return c.add0(subKey, v);
                    }
                }
                PrefixTree c2 = newNode(subKey, v, (PrefixTree) null);
                c2.sibling = this.child;
                this.child = c2;
                return true;
            } else {
                this.value = v;
                return true;
            }
        }

        public String match(CharSequence text, int off, int end) {
            if (!prefixOf(text, off, end)) {
                return null;
            }
            if (this.child != null) {
                int length = this.key.length() + off;
                int off2 = length;
                if (length != end) {
                    PrefixTree c = this.child;
                    while (!isEqual(c.c0, text.charAt(off2))) {
                        c = c.sibling;
                        if (c == null) {
                        }
                    }
                    String found = c.match(text, off2, end);
                    if (found != null) {
                        return found;
                    }
                    return this.value;
                }
            }
            return this.value;
        }

        public String match(CharSequence text, ParsePosition pos) {
            int off = pos.getIndex();
            int end = text.length();
            if (!prefixOf(text, off, end)) {
                return null;
            }
            int off2 = off + this.key.length();
            if (this.child != null && off2 != end) {
                PrefixTree c = this.child;
                while (true) {
                    if (!isEqual(c.c0, text.charAt(off2))) {
                        c = c.sibling;
                        if (c == null) {
                            break;
                        }
                    } else {
                        pos.setIndex(off2);
                        String found = c.match(text, pos);
                        if (found != null) {
                            return found;
                        }
                    }
                }
            }
            pos.setIndex(off2);
            return this.value;
        }

        /* access modifiers changed from: protected */
        public String toKey(String k) {
            return k;
        }

        /* access modifiers changed from: protected */
        public PrefixTree newNode(String k, String v, PrefixTree child2) {
            return new PrefixTree(k, v, child2);
        }

        /* access modifiers changed from: protected */
        public boolean isEqual(char c1, char c2) {
            return c1 == c2;
        }

        /* access modifiers changed from: protected */
        public boolean prefixOf(CharSequence text, int off, int end) {
            if (text instanceof String) {
                return ((String) text).startsWith(this.key, off);
            }
            int off2 = this.key.length();
            if (off2 > end - off) {
                return false;
            }
            int len = 0;
            while (true) {
                int len2 = off2 - 1;
                if (off2 <= 0) {
                    return true;
                }
                int off0 = len + 1;
                char charAt = this.key.charAt(len);
                int off3 = off + 1;
                if (!isEqual(charAt, text.charAt(off))) {
                    return false;
                }
                int off4 = off3;
                len = off0;
                off = off4;
                off2 = len2;
            }
        }

        private int prefixLength(String k) {
            int off = 0;
            while (off < k.length() && off < this.key.length() && isEqual(k.charAt(off), this.key.charAt(off))) {
                off++;
            }
            return off;
        }

        private static class CI extends PrefixTree {
            private CI(String k, String v, PrefixTree child) {
                super(k, v, child);
            }

            /* access modifiers changed from: protected */
            public CI newNode(String k, String v, PrefixTree child) {
                return new CI(k, v, child);
            }

            /* access modifiers changed from: protected */
            public boolean isEqual(char c1, char c2) {
                return DateTimeParseContext.charEqualsIgnoreCase(c1, c2);
            }

            /* access modifiers changed from: protected */
            public boolean prefixOf(CharSequence text, int off, int end) {
                int off2 = this.key.length();
                if (off2 > end - off) {
                    return false;
                }
                int len = 0;
                while (true) {
                    int len2 = off2 - 1;
                    if (off2 <= 0) {
                        return true;
                    }
                    int off0 = len + 1;
                    char charAt = this.key.charAt(len);
                    int off3 = off + 1;
                    if (!isEqual(charAt, text.charAt(off))) {
                        return false;
                    }
                    int off4 = off3;
                    len = off0;
                    off = off4;
                    off2 = len2;
                }
            }
        }

        private static class LENIENT extends CI {
            private LENIENT(String k, String v, PrefixTree child) {
                super(k, v, child);
            }

            /* access modifiers changed from: protected */
            public CI newNode(String k, String v, PrefixTree child) {
                return new LENIENT(k, v, child);
            }

            private boolean isLenientChar(char c) {
                return c == ' ' || c == '_' || c == '/';
            }

            /* access modifiers changed from: protected */
            public String toKey(String k) {
                int i = 0;
                while (i < k.length()) {
                    if (isLenientChar(k.charAt(i))) {
                        StringBuilder sb = new StringBuilder(k.length());
                        sb.append(k, 0, i);
                        while (true) {
                            i++;
                            if (i >= k.length()) {
                                return sb.toString();
                            }
                            if (!isLenientChar(k.charAt(i))) {
                                sb.append(k.charAt(i));
                            }
                        }
                    } else {
                        i++;
                    }
                }
                return k;
            }

            public String match(CharSequence text, ParsePosition pos) {
                int off = pos.getIndex();
                int end = text.length();
                int len = this.key.length();
                int koff = 0;
                while (koff < len && off < end) {
                    if (isLenientChar(text.charAt(off))) {
                        off++;
                    } else {
                        int koff2 = koff + 1;
                        int off2 = off + 1;
                        if (!isEqual(this.key.charAt(koff), text.charAt(off))) {
                            return null;
                        }
                        koff = koff2;
                        off = off2;
                    }
                }
                if (koff != len) {
                    return null;
                }
                if (this.child != null && off != end) {
                    int off0 = off;
                    while (off0 < end && isLenientChar(text.charAt(off0))) {
                        off0++;
                    }
                    if (off0 < end) {
                        PrefixTree c = this.child;
                        while (true) {
                            if (!isEqual(c.c0, text.charAt(off0))) {
                                c = c.sibling;
                                if (c == null) {
                                    break;
                                }
                            } else {
                                pos.setIndex(off0);
                                String found = c.match(text, pos);
                                if (found != null) {
                                    return found;
                                }
                            }
                        }
                    }
                }
                pos.setIndex(off);
                return this.value;
            }
        }
    }

    static final class ChronoPrinterParser implements DateTimePrinterParser {
        private final TextStyle textStyle;

        ChronoPrinterParser(TextStyle textStyle2) {
            this.textStyle = textStyle2;
        }

        public boolean format(DateTimePrintContext context, StringBuilder buf) {
            Chronology chrono = (Chronology) context.getValue(TemporalQueries.chronology());
            if (chrono == null) {
                return false;
            }
            if (this.textStyle == null) {
                buf.append(chrono.getId());
                return true;
            }
            buf.append(getChronologyName(chrono, context.getLocale()));
            return true;
        }

        public int parse(DateTimeParseContext context, CharSequence text, int position) {
            String name;
            int i = position;
            if (i < 0 || i > text.length()) {
                DateTimeParseContext dateTimeParseContext = context;
                throw new IndexOutOfBoundsException();
            }
            Chronology bestMatch = null;
            int matchLen = -1;
            for (Chronology chrono : Chronology.CC.getAvailableChronologies()) {
                if (this.textStyle == null) {
                    name = chrono.getId();
                } else {
                    name = getChronologyName(chrono, context.getLocale());
                }
                int nameLen = name.length();
                if (nameLen > matchLen && context.subSequenceEquals(text, position, name, 0, nameLen)) {
                    bestMatch = chrono;
                    matchLen = nameLen;
                }
            }
            if (bestMatch == null) {
                return i ^ -1;
            }
            context.setParsed(bestMatch);
            return i + matchLen;
        }

        private String getChronologyName(Chronology chrono, Locale locale) {
            return chrono.getId();
        }
    }

    static final class LocalizedPrinterParser implements DateTimePrinterParser {
        private static final ConcurrentMap<String, DateTimeFormatter> FORMATTER_CACHE = new ConcurrentHashMap(16, 0.75f, 2);
        private final FormatStyle dateStyle;
        private final FormatStyle timeStyle;

        LocalizedPrinterParser(FormatStyle dateStyle2, FormatStyle timeStyle2) {
            this.dateStyle = dateStyle2;
            this.timeStyle = timeStyle2;
        }

        public boolean format(DateTimePrintContext context, StringBuilder buf) {
            return formatter(context.getLocale(), Chronology.CC.from(context.getTemporal())).toPrinterParser(false).format(context, buf);
        }

        public int parse(DateTimeParseContext context, CharSequence text, int position) {
            return formatter(context.getLocale(), context.getEffectiveChronology()).toPrinterParser(false).parse(context, text, position);
        }

        private DateTimeFormatter formatter(Locale locale, Chronology chrono) {
            String key = chrono.getId() + '|' + locale.toString() + '|' + this.dateStyle + this.timeStyle;
            ConcurrentMap<String, DateTimeFormatter> concurrentMap = FORMATTER_CACHE;
            DateTimeFormatter formatter = (DateTimeFormatter) concurrentMap.get(key);
            if (formatter != null) {
                return formatter;
            }
            DateTimeFormatter formatter2 = new DateTimeFormatterBuilder().appendPattern(DateTimeFormatterBuilder.getLocalizedDateTimePattern(this.dateStyle, this.timeStyle, chrono, locale)).toFormatter(locale);
            DateTimeFormatter old = concurrentMap.putIfAbsent(key, formatter2);
            if (old != null) {
                return old;
            }
            return formatter2;
        }

        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append("Localized(");
            Object obj = this.dateStyle;
            Object obj2 = "";
            if (obj == null) {
                obj = obj2;
            }
            sb.append(obj);
            sb.append(",");
            Object obj3 = this.timeStyle;
            if (obj3 != null) {
                obj2 = obj3;
            }
            sb.append(obj2);
            sb.append(")");
            return sb.toString();
        }
    }

    static final class WeekBasedFieldPrinterParser implements DateTimePrinterParser {
        private char chr;
        private int count;

        WeekBasedFieldPrinterParser(char chr2, int count2) {
            this.chr = chr2;
            this.count = count2;
        }

        public boolean format(DateTimePrintContext context, StringBuilder buf) {
            return printerParser(context.getLocale()).format(context, buf);
        }

        public int parse(DateTimeParseContext context, CharSequence text, int position) {
            return printerParser(context.getLocale()).parse(context, text, position);
        }

        private DateTimePrinterParser printerParser(Locale locale) {
            TemporalField field;
            WeekFields weekDef = WeekFields.of(locale);
            switch (this.chr) {
                case 'W':
                    field = weekDef.weekOfMonth();
                    break;
                case 'Y':
                    TemporalField field2 = weekDef.weekBasedYear();
                    if (this.count == 2) {
                        return new ReducedPrinterParser(field2, 2, 2, 0, ReducedPrinterParser.BASE_DATE, 0);
                    }
                    int i = this.count;
                    return new NumberPrinterParser(field2, i, 19, i < 4 ? SignStyle.NORMAL : SignStyle.EXCEEDS_PAD, -1);
                case 'c':
                case 'e':
                    field = weekDef.dayOfWeek();
                    break;
                case 'w':
                    field = weekDef.weekOfWeekBasedYear();
                    break;
                default:
                    throw new IllegalStateException("unreachable");
            }
            return new NumberPrinterParser(field, this.count == 2 ? 2 : 1, 2, SignStyle.NOT_NEGATIVE);
        }

        public String toString() {
            StringBuilder sb = new StringBuilder(30);
            sb.append("Localized(");
            char c = this.chr;
            if (c == 'Y') {
                int i = this.count;
                if (i == 1) {
                    sb.append("WeekBasedYear");
                } else if (i == 2) {
                    sb.append("ReducedValue(WeekBasedYear,2,2,2000-01-01)");
                } else {
                    sb.append("WeekBasedYear,");
                    sb.append(this.count);
                    sb.append(",");
                    sb.append(19);
                    sb.append(",");
                    sb.append(this.count < 4 ? SignStyle.NORMAL : SignStyle.EXCEEDS_PAD);
                }
            } else {
                switch (c) {
                    case 'W':
                        sb.append("WeekOfMonth");
                        break;
                    case 'c':
                    case 'e':
                        sb.append("DayOfWeek");
                        break;
                    case 'w':
                        sb.append("WeekOfWeekBasedYear");
                        break;
                }
                sb.append(",");
                sb.append(this.count);
            }
            sb.append(")");
            return sb.toString();
        }
    }
}
