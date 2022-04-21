package j$.time.chrono;

import j$.time.DateTimeException;
import j$.time.LocalDate$$ExternalSyntheticBackport0;
import j$.time.Period$$ExternalSyntheticBackport0;
import j$.time.Period$$ExternalSyntheticBackport1;
import j$.time.Period$$ExternalSyntheticBackport2;
import j$.time.chrono.ChronoPeriod;
import j$.time.chrono.Chronology;
import j$.time.temporal.ChronoField;
import j$.time.temporal.ChronoUnit;
import j$.time.temporal.Temporal;
import j$.time.temporal.TemporalAccessor;
import j$.time.temporal.TemporalAmount;
import j$.time.temporal.TemporalQueries;
import j$.time.temporal.TemporalUnit;
import j$.time.temporal.UnsupportedTemporalTypeException;
import j$.time.temporal.ValueRange;
import j$.util.Objects;
import java.io.DataInput;
import java.io.DataOutput;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

final class ChronoPeriodImpl implements ChronoPeriod, Serializable {
    private static final List<TemporalUnit> SUPPORTED_UNITS = Collections.unmodifiableList(Arrays.asList(new TemporalUnit[]{ChronoUnit.YEARS, ChronoUnit.MONTHS, ChronoUnit.DAYS}));
    private static final long serialVersionUID = 57387258289L;
    private final Chronology chrono;
    final int days;
    final int months;
    final int years;

    public /* synthetic */ ChronoPeriod negated() {
        return ChronoPeriod.CC.$default$negated(this);
    }

    ChronoPeriodImpl(Chronology chrono2, int years2, int months2, int days2) {
        Objects.requireNonNull(chrono2, "chrono");
        this.chrono = chrono2;
        this.years = years2;
        this.months = months2;
        this.days = days2;
    }

    public long get(TemporalUnit unit) {
        if (unit == ChronoUnit.YEARS) {
            return (long) this.years;
        }
        if (unit == ChronoUnit.MONTHS) {
            return (long) this.months;
        }
        if (unit == ChronoUnit.DAYS) {
            return (long) this.days;
        }
        throw new UnsupportedTemporalTypeException("Unsupported unit: " + unit);
    }

    public List<TemporalUnit> getUnits() {
        return SUPPORTED_UNITS;
    }

    public Chronology getChronology() {
        return this.chrono;
    }

    public boolean isZero() {
        return this.years == 0 && this.months == 0 && this.days == 0;
    }

    public boolean isNegative() {
        return this.years < 0 || this.months < 0 || this.days < 0;
    }

    public ChronoPeriod plus(TemporalAmount amountToAdd) {
        ChronoPeriodImpl amount = validateAmount(amountToAdd);
        return new ChronoPeriodImpl(this.chrono, Period$$ExternalSyntheticBackport0.m(this.years, amount.years), Period$$ExternalSyntheticBackport0.m(this.months, amount.months), Period$$ExternalSyntheticBackport0.m(this.days, amount.days));
    }

    public ChronoPeriod minus(TemporalAmount amountToSubtract) {
        ChronoPeriodImpl amount = validateAmount(amountToSubtract);
        return new ChronoPeriodImpl(this.chrono, Period$$ExternalSyntheticBackport1.m(this.years, amount.years), Period$$ExternalSyntheticBackport1.m(this.months, amount.months), Period$$ExternalSyntheticBackport1.m(this.days, amount.days));
    }

    private ChronoPeriodImpl validateAmount(TemporalAmount amount) {
        Objects.requireNonNull(amount, "amount");
        if (amount instanceof ChronoPeriodImpl) {
            ChronoPeriodImpl period = (ChronoPeriodImpl) amount;
            if (this.chrono.equals(period.getChronology())) {
                return period;
            }
            throw new ClassCastException("Chronology mismatch, expected: " + this.chrono.getId() + ", actual: " + period.getChronology().getId());
        }
        throw new DateTimeException("Unable to obtain ChronoPeriod from TemporalAmount: " + amount.getClass());
    }

    public ChronoPeriod multipliedBy(int scalar) {
        if (isZero() || scalar == 1) {
            return this;
        }
        return new ChronoPeriodImpl(this.chrono, Period$$ExternalSyntheticBackport2.m(this.years, scalar), Period$$ExternalSyntheticBackport2.m(this.months, scalar), Period$$ExternalSyntheticBackport2.m(this.days, scalar));
    }

    public ChronoPeriod normalized() {
        long monthRange = monthRange();
        if (monthRange <= 0) {
            return this;
        }
        long j = (long) this.years;
        int i = this.months;
        long totalMonths = (j * monthRange) + ((long) i);
        long splitYears = totalMonths / monthRange;
        int splitMonths = (int) (totalMonths % monthRange);
        if (splitYears == j && splitMonths == i) {
            return this;
        }
        return new ChronoPeriodImpl(this.chrono, LocalDate$$ExternalSyntheticBackport0.m(splitYears), splitMonths, this.days);
    }

    private long monthRange() {
        ValueRange startRange = this.chrono.range(ChronoField.MONTH_OF_YEAR);
        if (!startRange.isFixed() || !startRange.isIntValue()) {
            return -1;
        }
        return (startRange.getMaximum() - startRange.getMinimum()) + 1;
    }

    public Temporal addTo(Temporal temporal) {
        validateChrono(temporal);
        if (this.months == 0) {
            int i = this.years;
            if (i != 0) {
                temporal = temporal.plus((long) i, ChronoUnit.YEARS);
            }
        } else {
            long monthRange = monthRange();
            if (monthRange > 0) {
                temporal = temporal.plus((((long) this.years) * monthRange) + ((long) this.months), ChronoUnit.MONTHS);
            } else {
                int i2 = this.years;
                if (i2 != 0) {
                    temporal = temporal.plus((long) i2, ChronoUnit.YEARS);
                }
                temporal = temporal.plus((long) this.months, ChronoUnit.MONTHS);
            }
        }
        int i3 = this.days;
        if (i3 != 0) {
            return temporal.plus((long) i3, ChronoUnit.DAYS);
        }
        return temporal;
    }

    public Temporal subtractFrom(Temporal temporal) {
        validateChrono(temporal);
        if (this.months == 0) {
            int i = this.years;
            if (i != 0) {
                temporal = temporal.minus((long) i, ChronoUnit.YEARS);
            }
        } else {
            long monthRange = monthRange();
            if (monthRange > 0) {
                temporal = temporal.minus((((long) this.years) * monthRange) + ((long) this.months), ChronoUnit.MONTHS);
            } else {
                int i2 = this.years;
                if (i2 != 0) {
                    temporal = temporal.minus((long) i2, ChronoUnit.YEARS);
                }
                temporal = temporal.minus((long) this.months, ChronoUnit.MONTHS);
            }
        }
        int i3 = this.days;
        if (i3 != 0) {
            return temporal.minus((long) i3, ChronoUnit.DAYS);
        }
        return temporal;
    }

    private void validateChrono(TemporalAccessor temporal) {
        Objects.requireNonNull(temporal, "temporal");
        Chronology temporalChrono = (Chronology) temporal.query(TemporalQueries.chronology());
        if (temporalChrono != null && !this.chrono.equals(temporalChrono)) {
            throw new DateTimeException("Chronology mismatch, expected: " + this.chrono.getId() + ", actual: " + temporalChrono.getId());
        }
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof ChronoPeriodImpl)) {
            return false;
        }
        ChronoPeriodImpl other = (ChronoPeriodImpl) obj;
        if (this.years == other.years && this.months == other.months && this.days == other.days && this.chrono.equals(other.chrono)) {
            return true;
        }
        return false;
    }

    public int hashCode() {
        return ((this.years + Integer.rotateLeft(this.months, 8)) + Integer.rotateLeft(this.days, 16)) ^ this.chrono.hashCode();
    }

    public String toString() {
        if (isZero()) {
            return getChronology().toString() + " P0D";
        }
        StringBuilder buf = new StringBuilder();
        buf.append(getChronology().toString());
        buf.append(' ');
        buf.append('P');
        int i = this.years;
        if (i != 0) {
            buf.append(i);
            buf.append('Y');
        }
        int i2 = this.months;
        if (i2 != 0) {
            buf.append(i2);
            buf.append('M');
        }
        int i3 = this.days;
        if (i3 != 0) {
            buf.append(i3);
            buf.append('D');
        }
        return buf.toString();
    }

    /* access modifiers changed from: protected */
    public Object writeReplace() {
        return new Ser((byte) 9, this);
    }

    private void readObject(ObjectInputStream s) {
        throw new InvalidObjectException("Deserialization via serialization delegate");
    }

    /* access modifiers changed from: package-private */
    public void writeExternal(DataOutput out) {
        out.writeUTF(this.chrono.getId());
        out.writeInt(this.years);
        out.writeInt(this.months);
        out.writeInt(this.days);
    }

    static ChronoPeriodImpl readExternal(DataInput in) {
        return new ChronoPeriodImpl(Chronology.CC.of(in.readUTF()), in.readInt(), in.readInt(), in.readInt());
    }
}
