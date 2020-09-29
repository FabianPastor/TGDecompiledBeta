package j$.time;

import j$.CLASSNAMEe;
import j$.CLASSNAMEf;
import j$.CLASSNAMEi;
import j$.time.u.k;
import j$.time.u.u;
import j$.time.u.z;
import java.io.Serializable;
import java.math.BigInteger;
import java.util.regex.Pattern;

public final class h implements z, Comparable, Serializable {
    public static final h c = new h(0, 0);
    private final long a;
    private final int b;

    static {
        BigInteger.valueOf(NUM);
        Pattern.compile("([-+]?)P(?:([-+]?[0-9]+)D)?(T(?:([-+]?[0-9]+)H)?(?:([-+]?[0-9]+)M)?(?:([-+]?[0-9]+)(?:[.,]([0-9]{0,9}))?S)?)?", 2);
    }

    public static h K(long seconds) {
        return r(seconds, 0);
    }

    public static h L(long seconds, long nanoAdjustment) {
        return r(CLASSNAMEe.a(seconds, CLASSNAMEf.a(nanoAdjustment, NUM)), (int) CLASSNAMEi.a(nanoAdjustment, NUM));
    }

    public static h A(long nanos) {
        long secs = nanos / NUM;
        int nos = (int) (nanos % NUM);
        if (nos < 0) {
            nos = (int) (((long) nos) + NUM);
            secs--;
        }
        return r(secs, nos);
    }

    private static h r(long seconds, int nanoAdjustment) {
        if ((((long) nanoAdjustment) | seconds) == 0) {
            return c;
        }
        return new h(seconds, nanoAdjustment);
    }

    private h(long seconds, int nanos) {
        this.a = seconds;
        this.b = nanos;
    }

    public long x() {
        return this.a;
    }

    public u i(u temporal) {
        long j = this.a;
        if (j != 0) {
            temporal = temporal.g(j, k.SECONDS);
        }
        int i = this.b;
        if (i != 0) {
            return temporal.g((long) i, k.NANOS);
        }
        return temporal;
    }

    /* renamed from: p */
    public int compareTo(h otherDuration) {
        int cmp = (this.a > otherDuration.a ? 1 : (this.a == otherDuration.a ? 0 : -1));
        if (cmp != 0) {
            return cmp;
        }
        return this.b - otherDuration.b;
    }

    public boolean equals(Object otherDuration) {
        if (this == otherDuration) {
            return true;
        }
        if (!(otherDuration instanceof h)) {
            return false;
        }
        h other = (h) otherDuration;
        if (this.a == other.a && this.b == other.b) {
            return true;
        }
        return false;
    }

    public int hashCode() {
        long j = this.a;
        return ((int) (j ^ (j >>> 32))) + (this.b * 51);
    }

    public String toString() {
        if (this == c) {
            return "PT0S";
        }
        long j = this.a;
        long hours = j / 3600;
        int minutes = (int) ((j % 3600) / 60);
        int secs = (int) (j % 60);
        StringBuilder buf = new StringBuilder(24);
        buf.append("PT");
        if (hours != 0) {
            buf.append(hours);
            buf.append('H');
        }
        if (minutes != 0) {
            buf.append(minutes);
            buf.append('M');
        }
        if (secs == 0 && this.b == 0 && buf.length() > 2) {
            return buf.toString();
        }
        if (secs >= 0 || this.b <= 0) {
            buf.append(secs);
        } else if (secs == -1) {
            buf.append("-0");
        } else {
            buf.append(secs + 1);
        }
        if (this.b > 0) {
            int pos = buf.length();
            if (secs < 0) {
                buf.append(NUM - ((long) this.b));
            } else {
                buf.append(((long) this.b) + NUM);
            }
            while (buf.charAt(buf.length() - 1) == '0') {
                buf.setLength(buf.length() - 1);
            }
            buf.setCharAt(pos, '.');
        }
        buf.append('S');
        return buf.toString();
    }
}
