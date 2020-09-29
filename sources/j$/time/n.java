package j$.time;

import j$.time.format.J;
import j$.time.format.z;
import j$.time.u.j;
import j$.time.u.u;
import j$.time.u.x;
import java.io.Serializable;

public final class n implements u, x, Comparable, Serializable {
    static {
        new z().p(j.YEAR, 4, 10, J.EXCEEDS_PAD).E();
    }

    public static boolean A(long year) {
        return (3 & year) == 0 && (year % 100 != 0 || year % 400 == 0);
    }
}
