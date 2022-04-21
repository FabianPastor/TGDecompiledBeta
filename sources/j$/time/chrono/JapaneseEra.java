package j$.time.chrono;

import j$.time.DateTimeException;
import j$.time.LocalDate;
import j$.time.chrono.Era;
import j$.time.format.TextStyle;
import j$.time.temporal.ChronoField;
import j$.time.temporal.Temporal;
import j$.time.temporal.TemporalField;
import j$.time.temporal.TemporalQuery;
import j$.time.temporal.ValueRange;
import j$.util.Objects;
import java.io.DataInput;
import java.io.DataOutput;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Locale;

public final class JapaneseEra implements Era, Serializable {
    static final int ERA_OFFSET = 2;
    public static final JapaneseEra HEISEI;
    private static final JapaneseEra[] KNOWN_ERAS;
    public static final JapaneseEra MEIJI;
    private static final int N_ERA_CONSTANTS;
    private static final JapaneseEra REIWA;
    public static final JapaneseEra SHOWA;
    public static final JapaneseEra TAISHO;
    private static final long serialVersionUID = 1466499369062886794L;
    private final transient String abbreviation;
    private final transient int eraValue;
    private final transient String name;
    private final transient LocalDate since;

    public /* synthetic */ Temporal adjustInto(Temporal temporal) {
        return Era.CC.$default$adjustInto(this, temporal);
    }

    public /* synthetic */ int get(TemporalField temporalField) {
        return Era.CC.$default$get(this, temporalField);
    }

    public /* synthetic */ long getLong(TemporalField temporalField) {
        return Era.CC.$default$getLong(this, temporalField);
    }

    public /* synthetic */ boolean isSupported(TemporalField temporalField) {
        return Era.CC.$default$isSupported(this, temporalField);
    }

    public /* synthetic */ Object query(TemporalQuery temporalQuery) {
        return Era.CC.$default$query(this, temporalQuery);
    }

    static {
        JapaneseEra japaneseEra = new JapaneseEra(-1, LocalDate.of(1868, 1, 1), "Meiji", "M");
        MEIJI = japaneseEra;
        JapaneseEra japaneseEra2 = new JapaneseEra(0, LocalDate.of(1912, 7, 30), "Taisho", "T");
        TAISHO = japaneseEra2;
        JapaneseEra japaneseEra3 = new JapaneseEra(1, LocalDate.of(1926, 12, 25), "Showa", "S");
        SHOWA = japaneseEra3;
        JapaneseEra japaneseEra4 = new JapaneseEra(2, LocalDate.of(1989, 1, 8), "Heisei", "H");
        HEISEI = japaneseEra4;
        JapaneseEra japaneseEra5 = new JapaneseEra(3, LocalDate.of(2019, 5, 1), "Reiwa", "R");
        REIWA = japaneseEra5;
        int value = japaneseEra5.getValue() + 2;
        N_ERA_CONSTANTS = value;
        JapaneseEra[] japaneseEraArr = new JapaneseEra[value];
        KNOWN_ERAS = japaneseEraArr;
        japaneseEraArr[0] = japaneseEra;
        japaneseEraArr[1] = japaneseEra2;
        japaneseEraArr[2] = japaneseEra3;
        japaneseEraArr[3] = japaneseEra4;
        japaneseEraArr[4] = japaneseEra5;
    }

    static JapaneseEra getCurrentEra() {
        JapaneseEra[] japaneseEraArr = KNOWN_ERAS;
        return japaneseEraArr[japaneseEraArr.length - 1];
    }

    static long shortestYearsOfEra() {
        int min = (NUM - getCurrentEra().since.getYear()) + 1;
        int lastStartYear = KNOWN_ERAS[0].since.getYear();
        int i = 1;
        while (true) {
            JapaneseEra[] japaneseEraArr = KNOWN_ERAS;
            if (i >= japaneseEraArr.length) {
                return (long) min;
            }
            JapaneseEra era = japaneseEraArr[i];
            min = Math.min(min, (era.since.getYear() - lastStartYear) + 1);
            lastStartYear = era.since.getYear();
            i++;
        }
    }

    static long shortestDaysOfYear() {
        long min = ChronoField.DAY_OF_YEAR.range().getSmallestMaximum();
        for (JapaneseEra era : KNOWN_ERAS) {
            min = Math.min(min, (long) ((era.since.lengthOfYear() - era.since.getDayOfYear()) + 1));
            if (era.next() != null) {
                min = Math.min(min, (long) (era.next().since.getDayOfYear() - 1));
            }
        }
        return min;
    }

    private JapaneseEra(int eraValue2, LocalDate since2, String name2, String abbreviation2) {
        this.eraValue = eraValue2;
        this.since = since2;
        this.name = name2;
        this.abbreviation = abbreviation2;
    }

    /* access modifiers changed from: package-private */
    public LocalDate getSince() {
        return this.since;
    }

    public static JapaneseEra of(int japaneseEra) {
        if (japaneseEra >= MEIJI.eraValue) {
            int i = japaneseEra + 2;
            JapaneseEra[] japaneseEraArr = KNOWN_ERAS;
            if (i <= japaneseEraArr.length) {
                return japaneseEraArr[ordinal(japaneseEra)];
            }
        }
        throw new DateTimeException("Invalid era: " + japaneseEra);
    }

    public static JapaneseEra valueOf(String japaneseEra) {
        Objects.requireNonNull(japaneseEra, "japaneseEra");
        for (JapaneseEra era : KNOWN_ERAS) {
            if (era.getName().equals(japaneseEra)) {
                return era;
            }
        }
        throw new IllegalArgumentException("japaneseEra is invalid");
    }

    public static JapaneseEra[] values() {
        JapaneseEra[] japaneseEraArr = KNOWN_ERAS;
        return (JapaneseEra[]) Arrays.copyOf(japaneseEraArr, japaneseEraArr.length);
    }

    public String getDisplayName(TextStyle style, Locale locale) {
        if (getValue() <= N_ERA_CONSTANTS - 2) {
            return Era.CC.$default$getDisplayName(this, style, locale);
        }
        Objects.requireNonNull(locale, "locale");
        return style.asNormal() == TextStyle.NARROW ? getAbbreviation() : getName();
    }

    static JapaneseEra from(LocalDate date) {
        if (!date.isBefore(JapaneseDate.MEIJI_6_ISODATE)) {
            for (int i = KNOWN_ERAS.length - 1; i >= 0; i--) {
                JapaneseEra era = KNOWN_ERAS[i];
                if (date.compareTo((ChronoLocalDate) era.since) >= 0) {
                    return era;
                }
            }
            return null;
        }
        throw new DateTimeException("JapaneseDate before Meiji 6 are not supported");
    }

    private static int ordinal(int eraValue2) {
        return (eraValue2 + 2) - 1;
    }

    public int getValue() {
        return this.eraValue;
    }

    public ValueRange range(TemporalField field) {
        if (field == ChronoField.ERA) {
            return JapaneseChronology.INSTANCE.range(ChronoField.ERA);
        }
        return Era.CC.$default$range(this, field);
    }

    private String getAbbreviation() {
        return this.abbreviation;
    }

    private String getName() {
        return this.name;
    }

    /* access modifiers changed from: package-private */
    public JapaneseEra next() {
        if (this == getCurrentEra()) {
            return null;
        }
        return of(this.eraValue + 1);
    }

    public String toString() {
        return getName();
    }

    private void readObject(ObjectInputStream s) {
        throw new InvalidObjectException("Deserialization via serialization delegate");
    }

    private Object writeReplace() {
        return new Ser((byte) 5, this);
    }

    /* access modifiers changed from: package-private */
    public void writeExternal(DataOutput out) {
        out.writeByte(getValue());
    }

    static JapaneseEra readExternal(DataInput in) {
        return of(in.readByte());
    }
}
