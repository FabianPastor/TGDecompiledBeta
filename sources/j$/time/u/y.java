package j$.time.u;

import j$.time.g;

public final class y {
    public static x c() {
        return CLASSNAMEb.a;
    }

    public static x d(g dayOfWeek) {
        return new CLASSNAMEa(dayOfWeek.getValue());
    }

    static /* synthetic */ u b(int dowValue, u temporal) {
        int calDow = temporal.i(j.DAY_OF_WEEK);
        if (calDow == dowValue) {
            return temporal;
        }
        int daysDiff = calDow - dowValue;
        return temporal.g((long) (daysDiff >= 0 ? 7 - daysDiff : -daysDiff), k.DAYS);
    }
}
