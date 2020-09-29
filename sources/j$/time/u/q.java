package j$.time.u;

import j$.time.LocalDate;
import j$.time.f;
import j$.time.g;
import j$.time.t.p;
import j$.time.t.t;

enum q implements B {
    WEEK_OF_WEEK_BASED_YEAR,
    WEEK_BASED_YEAR;
    
    /* access modifiers changed from: private */
    public static final int[] e = null;

    static {
        e = new int[]{0, 90, 181, 273, 0, 91, 182, 274};
    }

    public boolean i() {
        return true;
    }

    public boolean r() {
        return false;
    }

    public G M(w temporal) {
        return p();
    }

    /* access modifiers changed from: private */
    public static boolean a0(w temporal) {
        return p.e(temporal).equals(t.a);
    }

    /* access modifiers changed from: private */
    public static void V(w temporal) {
        if (!a0(temporal)) {
            throw new f("Resolve requires IsoChronology");
        }
    }

    /* access modifiers changed from: private */
    public static G Z(LocalDate date) {
        return G.j(1, (long) Y(X(date)));
    }

    /* access modifiers changed from: private */
    public static int Y(int wby) {
        LocalDate date = LocalDate.a0(wby, 1, 1);
        if (date.Q() == g.THURSDAY) {
            return 53;
        }
        if (date.Q() != g.WEDNESDAY || !date.V()) {
            return 52;
        }
        return 53;
    }

    /* access modifiers changed from: private */
    public static int W(LocalDate date) {
        int dow0 = date.Q().ordinal();
        boolean z = true;
        int doy0 = date.R() - 1;
        int doyThu0 = (3 - dow0) + doy0;
        int firstMonDoy0 = (doyThu0 - ((doyThu0 / 7) * 7)) - 3;
        if (firstMonDoy0 < -3) {
            firstMonDoy0 += 7;
        }
        if (doy0 < firstMonDoy0) {
            return (int) Z(date.n0(180).Y(1)).d();
        }
        int week = ((doy0 - firstMonDoy0) / 7) + 1;
        if (week != 53) {
            return week;
        }
        if (firstMonDoy0 != -3 && (firstMonDoy0 != -2 || !date.V())) {
            z = false;
        }
        if (!z) {
            return 1;
        }
        return week;
    }

    /* access modifiers changed from: private */
    public static int X(LocalDate date) {
        int year = date.U();
        int doy = date.R();
        if (doy <= 3) {
            if (doy - date.Q().ordinal() < -2) {
                return year - 1;
            }
            return year;
        } else if (doy < 363) {
            return year;
        } else {
            if (((doy - 363) - (date.V() ? 1 : 0)) - date.Q().ordinal() >= 0) {
                return year + 1;
            }
            return year;
        }
    }
}
