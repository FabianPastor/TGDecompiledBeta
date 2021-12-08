package j$.time.temporal;

public interface Temporal extends TemporalAccessor {
    boolean isSupported(TemporalUnit temporalUnit);

    Temporal minus(long j, TemporalUnit temporalUnit);

    Temporal minus(TemporalAmount temporalAmount);

    Temporal plus(long j, TemporalUnit temporalUnit);

    Temporal plus(TemporalAmount temporalAmount);

    long until(Temporal temporal, TemporalUnit temporalUnit);

    Temporal with(TemporalAdjuster temporalAdjuster);

    Temporal with(TemporalField temporalField, long j);

    /* renamed from: j$.time.temporal.Temporal$-CC  reason: invalid class name */
    public final /* synthetic */ class CC {
        public static Temporal $default$with(Temporal _this, TemporalAdjuster adjuster) {
            return adjuster.adjustInto(_this);
        }

        public static Temporal $default$plus(Temporal _this, TemporalAmount amount) {
            return amount.addTo(_this);
        }

        public static Temporal $default$minus(Temporal _this, TemporalAmount amount) {
            return amount.subtractFrom(_this);
        }

        public static Temporal $default$minus(Temporal _this, long amountToSubtract, TemporalUnit unit) {
            return amountToSubtract == Long.MIN_VALUE ? _this.plus(Long.MAX_VALUE, unit).plus(1, unit) : _this.plus(-amountToSubtract, unit);
        }
    }
}
