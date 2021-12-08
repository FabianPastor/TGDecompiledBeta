package j$.time.temporal;

import j$.time.DayOfWeek;
import j$.time.LocalDate;
import j$.util.Objects;
import j$.util.function.UnaryOperator;

public final class TemporalAdjusters {
    private TemporalAdjusters() {
    }

    public static TemporalAdjuster ofDateAdjuster(UnaryOperator<LocalDate> unaryOperator) {
        Objects.requireNonNull(unaryOperator, "dateBasedAdjuster");
        return new TemporalAdjusters$$ExternalSyntheticLambda6(unaryOperator);
    }

    public static TemporalAdjuster firstDayOfMonth() {
        return TemporalAdjusters$$ExternalSyntheticLambda7.INSTANCE;
    }

    public static TemporalAdjuster lastDayOfMonth() {
        return TemporalAdjusters$$ExternalSyntheticLambda11.INSTANCE;
    }

    public static TemporalAdjuster firstDayOfNextMonth() {
        return TemporalAdjusters$$ExternalSyntheticLambda8.INSTANCE;
    }

    public static TemporalAdjuster firstDayOfYear() {
        return TemporalAdjusters$$ExternalSyntheticLambda10.INSTANCE;
    }

    public static TemporalAdjuster lastDayOfYear() {
        return TemporalAdjusters$$ExternalSyntheticLambda12.INSTANCE;
    }

    public static TemporalAdjuster firstDayOfNextYear() {
        return TemporalAdjusters$$ExternalSyntheticLambda9.INSTANCE;
    }

    public static TemporalAdjuster firstInMonth(DayOfWeek dayOfWeek) {
        return dayOfWeekInMonth(1, dayOfWeek);
    }

    public static TemporalAdjuster lastInMonth(DayOfWeek dayOfWeek) {
        return dayOfWeekInMonth(-1, dayOfWeek);
    }

    public static TemporalAdjuster dayOfWeekInMonth(int ordinal, DayOfWeek dayOfWeek) {
        Objects.requireNonNull(dayOfWeek, "dayOfWeek");
        int dowValue = dayOfWeek.getValue();
        if (ordinal >= 0) {
            return new TemporalAdjusters$$ExternalSyntheticLambda4(dowValue, ordinal);
        }
        return new TemporalAdjusters$$ExternalSyntheticLambda5(dowValue, ordinal);
    }

    static /* synthetic */ Temporal lambda$dayOfWeekInMonth$8(int dowValue, int ordinal, Temporal temporal) {
        Temporal temp = temporal.with(ChronoField.DAY_OF_MONTH, temporal.range(ChronoField.DAY_OF_MONTH).getMaximum());
        int daysDiff = dowValue - temp.get(ChronoField.DAY_OF_WEEK);
        return temp.plus((long) ((int) (((long) (daysDiff == 0 ? 0 : daysDiff > 0 ? daysDiff - 7 : daysDiff)) - ((((long) (-ordinal)) - 1) * 7))), ChronoUnit.DAYS);
    }

    public static TemporalAdjuster next(DayOfWeek dayOfWeek) {
        return new TemporalAdjusters$$ExternalSyntheticLambda0(dayOfWeek.getValue());
    }

    static /* synthetic */ Temporal lambda$next$9(int dowValue, Temporal temporal) {
        int daysDiff = temporal.get(ChronoField.DAY_OF_WEEK) - dowValue;
        return temporal.plus((long) (daysDiff >= 0 ? 7 - daysDiff : -daysDiff), ChronoUnit.DAYS);
    }

    public static TemporalAdjuster nextOrSame(DayOfWeek dayOfWeek) {
        return new TemporalAdjusters$$ExternalSyntheticLambda1(dayOfWeek.getValue());
    }

    static /* synthetic */ Temporal lambda$nextOrSame$10(int dowValue, Temporal temporal) {
        int calDow = temporal.get(ChronoField.DAY_OF_WEEK);
        if (calDow == dowValue) {
            return temporal;
        }
        int daysDiff = calDow - dowValue;
        return temporal.plus((long) (daysDiff >= 0 ? 7 - daysDiff : -daysDiff), ChronoUnit.DAYS);
    }

    public static TemporalAdjuster previous(DayOfWeek dayOfWeek) {
        return new TemporalAdjusters$$ExternalSyntheticLambda2(dayOfWeek.getValue());
    }

    static /* synthetic */ Temporal lambda$previous$11(int dowValue, Temporal temporal) {
        int daysDiff = dowValue - temporal.get(ChronoField.DAY_OF_WEEK);
        return temporal.minus((long) (daysDiff >= 0 ? 7 - daysDiff : -daysDiff), ChronoUnit.DAYS);
    }

    public static TemporalAdjuster previousOrSame(DayOfWeek dayOfWeek) {
        return new TemporalAdjusters$$ExternalSyntheticLambda3(dayOfWeek.getValue());
    }

    static /* synthetic */ Temporal lambda$previousOrSame$12(int dowValue, Temporal temporal) {
        int calDow = temporal.get(ChronoField.DAY_OF_WEEK);
        if (calDow == dowValue) {
            return temporal;
        }
        int daysDiff = dowValue - calDow;
        return temporal.minus((long) (daysDiff >= 0 ? 7 - daysDiff : -daysDiff), ChronoUnit.DAYS);
    }
}
