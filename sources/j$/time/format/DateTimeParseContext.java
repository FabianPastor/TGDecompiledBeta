package j$.time.format;

import j$.time.ZoneId;
import j$.time.chrono.Chronology;
import j$.time.chrono.IsoChronology;
import j$.time.temporal.TemporalAccessor;
import j$.time.temporal.TemporalField;
import j$.util.Objects;
import j$.util.function.Consumer;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Set;

final class DateTimeParseContext {
    private boolean caseSensitive = true;
    private ArrayList<Consumer<Chronology>> chronoListeners;
    private DateTimeFormatter formatter;
    private final ArrayList<Parsed> parsed;
    private boolean strict = true;

    DateTimeParseContext(DateTimeFormatter formatter2) {
        ArrayList<Parsed> arrayList = new ArrayList<>();
        this.parsed = arrayList;
        this.chronoListeners = null;
        this.formatter = formatter2;
        arrayList.add(new Parsed());
    }

    /* access modifiers changed from: package-private */
    public DateTimeParseContext copy() {
        DateTimeParseContext newContext = new DateTimeParseContext(this.formatter);
        newContext.caseSensitive = this.caseSensitive;
        newContext.strict = this.strict;
        return newContext;
    }

    /* access modifiers changed from: package-private */
    public Locale getLocale() {
        return this.formatter.getLocale();
    }

    /* access modifiers changed from: package-private */
    public DecimalStyle getDecimalStyle() {
        return this.formatter.getDecimalStyle();
    }

    /* access modifiers changed from: package-private */
    public Chronology getEffectiveChronology() {
        Chronology chrono = currentParsed().chrono;
        if (chrono != null) {
            return chrono;
        }
        Chronology chrono2 = this.formatter.getChronology();
        if (chrono2 == null) {
            return IsoChronology.INSTANCE;
        }
        return chrono2;
    }

    /* access modifiers changed from: package-private */
    public boolean isCaseSensitive() {
        return this.caseSensitive;
    }

    /* access modifiers changed from: package-private */
    public void setCaseSensitive(boolean caseSensitive2) {
        this.caseSensitive = caseSensitive2;
    }

    /* access modifiers changed from: package-private */
    public boolean subSequenceEquals(CharSequence cs1, int offset1, CharSequence cs2, int offset2, int length) {
        if (offset1 + length > cs1.length() || offset2 + length > cs2.length()) {
            return false;
        }
        if (isCaseSensitive()) {
            for (int i = 0; i < length; i++) {
                if (cs1.charAt(offset1 + i) != cs2.charAt(offset2 + i)) {
                    return false;
                }
            }
            return true;
        }
        for (int i2 = 0; i2 < length; i2++) {
            char ch1 = cs1.charAt(offset1 + i2);
            char ch2 = cs2.charAt(offset2 + i2);
            if (ch1 != ch2 && Character.toUpperCase(ch1) != Character.toUpperCase(ch2) && Character.toLowerCase(ch1) != Character.toLowerCase(ch2)) {
                return false;
            }
        }
        return true;
    }

    /* access modifiers changed from: package-private */
    public boolean charEquals(char ch1, char ch2) {
        if (isCaseSensitive()) {
            return ch1 == ch2;
        }
        return charEqualsIgnoreCase(ch1, ch2);
    }

    static boolean charEqualsIgnoreCase(char c1, char c2) {
        return c1 == c2 || Character.toUpperCase(c1) == Character.toUpperCase(c2) || Character.toLowerCase(c1) == Character.toLowerCase(c2);
    }

    /* access modifiers changed from: package-private */
    public boolean isStrict() {
        return this.strict;
    }

    /* access modifiers changed from: package-private */
    public void setStrict(boolean strict2) {
        this.strict = strict2;
    }

    /* access modifiers changed from: package-private */
    public void startOptional() {
        this.parsed.add(currentParsed().copy());
    }

    /* access modifiers changed from: package-private */
    public void endOptional(boolean successful) {
        if (successful) {
            ArrayList<Parsed> arrayList = this.parsed;
            arrayList.remove(arrayList.size() - 2);
            return;
        }
        ArrayList<Parsed> arrayList2 = this.parsed;
        arrayList2.remove(arrayList2.size() - 1);
    }

    private Parsed currentParsed() {
        ArrayList<Parsed> arrayList = this.parsed;
        return arrayList.get(arrayList.size() - 1);
    }

    /* access modifiers changed from: package-private */
    public Parsed toUnresolved() {
        return currentParsed();
    }

    /* access modifiers changed from: package-private */
    public TemporalAccessor toResolved(ResolverStyle resolverStyle, Set<TemporalField> resolverFields) {
        Parsed parsed2 = currentParsed();
        parsed2.chrono = getEffectiveChronology();
        parsed2.zone = parsed2.zone != null ? parsed2.zone : this.formatter.getZone();
        return parsed2.resolve(resolverStyle, resolverFields);
    }

    /* access modifiers changed from: package-private */
    public Long getParsed(TemporalField field) {
        return currentParsed().fieldValues.get(field);
    }

    /* access modifiers changed from: package-private */
    public int setParsedField(TemporalField field, long value, int errorPos, int successPos) {
        Objects.requireNonNull(field, "field");
        Long old = currentParsed().fieldValues.put(field, Long.valueOf(value));
        return (old == null || old.longValue() == value) ? successPos : errorPos ^ -1;
    }

    /* access modifiers changed from: package-private */
    public void setParsed(Chronology chrono) {
        Objects.requireNonNull(chrono, "chrono");
        currentParsed().chrono = chrono;
        ArrayList<Consumer<Chronology>> arrayList = this.chronoListeners;
        if (arrayList != null && !arrayList.isEmpty()) {
            Consumer[] consumerArr = (Consumer[]) this.chronoListeners.toArray(new Consumer[1]);
            this.chronoListeners.clear();
            for (Consumer accept : consumerArr) {
                accept.accept(chrono);
            }
        }
    }

    /* access modifiers changed from: package-private */
    public void addChronoChangedListener(Consumer<Chronology> consumer) {
        if (this.chronoListeners == null) {
            this.chronoListeners = new ArrayList<>();
        }
        this.chronoListeners.add(consumer);
    }

    /* access modifiers changed from: package-private */
    public void setParsed(ZoneId zone) {
        Objects.requireNonNull(zone, "zone");
        currentParsed().zone = zone;
    }

    /* access modifiers changed from: package-private */
    public void setParsedLeapSecond() {
        currentParsed().leapSecond = true;
    }

    public String toString() {
        return currentParsed().toString();
    }
}
