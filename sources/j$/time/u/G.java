package j$.time.u;

import j$.time.f;
import java.io.Serializable;

public final class G implements Serializable {
    private final long a;
    private final long b;
    private final long c;
    private final long d;

    public static G j(long min, long max) {
        if (min <= max) {
            return new G(min, min, max, max);
        }
        throw new IllegalArgumentException("Minimum value must be less than maximum value");
    }

    public static G k(long min, long maxSmallest, long maxLargest) {
        return l(min, min, maxSmallest, maxLargest);
    }

    public static G l(long minSmallest, long minLargest, long maxSmallest, long maxLargest) {
        if (minSmallest > minLargest) {
            throw new IllegalArgumentException("Smallest minimum value must be less than largest minimum value");
        } else if (maxSmallest > maxLargest) {
            throw new IllegalArgumentException("Smallest maximum value must be less than largest maximum value");
        } else if (minLargest <= maxLargest) {
            return new G(minSmallest, minLargest, maxSmallest, maxLargest);
        } else {
            throw new IllegalArgumentException("Minimum value must be less than maximum value");
        }
    }

    private G(long minSmallest, long minLargest, long maxSmallest, long maxLargest) {
        this.a = minSmallest;
        this.b = minLargest;
        this.c = maxSmallest;
        this.d = maxLargest;
    }

    public boolean f() {
        return this.a == this.b && this.c == this.d;
    }

    public long e() {
        return this.a;
    }

    public long d() {
        return this.d;
    }

    public boolean g() {
        return e() >= -2147483648L && d() <= 2147483647L;
    }

    public boolean i(long value) {
        return value >= e() && value <= d();
    }

    public boolean h(long value) {
        return g() && i(value);
    }

    public long b(long value, B field) {
        if (i(value)) {
            return value;
        }
        throw new f(c(field, value));
    }

    public int a(long value, B field) {
        if (h(value)) {
            return (int) value;
        }
        throw new f(c(field, value));
    }

    private String c(B field, long value) {
        if (field != null) {
            return "Invalid value for " + field + " (valid values " + this + "): " + value;
        }
        return "Invalid value (valid values " + this + "): " + value;
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof G)) {
            return false;
        }
        G other = (G) obj;
        if (this.a == other.a && this.b == other.b && this.c == other.c && this.d == other.d) {
            return true;
        }
        return false;
    }

    public int hashCode() {
        long j = this.a;
        long j2 = this.b;
        long j3 = j + (j2 << 16) + (j2 >> 48);
        long j4 = this.c;
        long j5 = j3 + (j4 << 32) + (j4 >> 32);
        long j6 = this.d;
        long hash = j5 + (j6 << 48) + (j6 >> 16);
        return (int) ((hash >>> 32) ^ hash);
    }

    public String toString() {
        StringBuilder buf = new StringBuilder();
        buf.append(this.a);
        if (this.a != this.b) {
            buf.append('/');
            buf.append(this.b);
        }
        buf.append(" - ");
        buf.append(this.c);
        if (this.c != this.d) {
            buf.append('/');
            buf.append(this.d);
        }
        return buf.toString();
    }
}
