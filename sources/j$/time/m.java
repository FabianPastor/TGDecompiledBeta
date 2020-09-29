package j$.time;

import j$.CLASSNAMEp;
import j$.time.t.q;
import j$.time.t.t;
import j$.time.u.C;
import j$.time.u.E;
import j$.time.u.k;
import j$.time.u.u;
import j$.time.u.w;
import j$.time.u.z;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Collections;
import java.util.regex.Pattern;

public final class m implements z, Serializable {
    public static final m d = new m(0, 0, 0);
    private final int a;
    private final int b;
    private final int c;

    static {
        Pattern.compile("([-+]?)P(?:([-+]?[0-9]+)Y)?(?:([-+]?[0-9]+)M)?(?:([-+]?[0-9]+)W)?(?:([-+]?[0-9]+)D)?", 2);
        Collections.unmodifiableList(Arrays.asList(new E[]{k.YEARS, k.MONTHS, k.DAYS}));
    }

    public static m d(int days) {
        return a(0, 0, days);
    }

    private static m a(int years, int months, int days) {
        if ((years | months | days) == 0) {
            return d;
        }
        return new m(years, months, days);
    }

    private m(int years, int months, int days) {
        this.a = years;
        this.b = months;
        this.c = days;
    }

    public boolean c() {
        return this == d;
    }

    public int b() {
        return this.c;
    }

    public long e() {
        return (((long) this.a) * 12) + ((long) this.b);
    }

    public u i(u temporal) {
        f(temporal);
        if (this.b == 0) {
            int i = this.a;
            if (i != 0) {
                temporal = temporal.g((long) i, k.YEARS);
            }
        } else {
            long totalMonths = e();
            if (totalMonths != 0) {
                temporal = temporal.g(totalMonths, k.MONTHS);
            }
        }
        int i2 = this.c;
        if (i2 != 0) {
            return temporal.g((long) i2, k.DAYS);
        }
        return temporal;
    }

    private void f(w temporal) {
        CLASSNAMEp.a(temporal, "temporal");
        q temporalChrono = (q) temporal.r(C.a());
        if (temporalChrono != null && !t.a.equals(temporalChrono)) {
            throw new f("Chronology mismatch, expected: ISO, actual: " + temporalChrono.getId());
        }
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof m)) {
            return false;
        }
        m other = (m) obj;
        if (this.a == other.a && this.b == other.b && this.c == other.c) {
            return true;
        }
        return false;
    }

    public int hashCode() {
        return this.a + Integer.rotateLeft(this.b, 8) + Integer.rotateLeft(this.c, 16);
    }

    public String toString() {
        if (this == d) {
            return "P0D";
        }
        StringBuilder buf = new StringBuilder();
        buf.append('P');
        int i = this.a;
        if (i != 0) {
            buf.append(i);
            buf.append('Y');
        }
        int i2 = this.b;
        if (i2 != 0) {
            buf.append(i2);
            buf.append('M');
        }
        int i3 = this.c;
        if (i3 != 0) {
            buf.append(i3);
            buf.append('D');
        }
        return buf.toString();
    }
}
