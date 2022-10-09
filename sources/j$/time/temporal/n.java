package j$.time.temporal;

import java.io.Serializable;
/* loaded from: classes2.dex */
public final class n implements Serializable {
    private final long a;
    private final long b;
    private final long c;
    private final long d;

    private n(long j, long j2, long j3, long j4) {
        this.a = j;
        this.b = j2;
        this.c = j3;
        this.d = j4;
    }

    public static n c(long j, long j2) {
        if (j <= j2) {
            return new n(j, j, j2, j2);
        }
        throw new IllegalArgumentException("Minimum value must be less than maximum value");
    }

    public static n d(long j, long j2, long j3) {
        if (j <= j) {
            if (j2 > j3) {
                throw new IllegalArgumentException("Smallest maximum value must be less than largest maximum value");
            }
            if (j > j3) {
                throw new IllegalArgumentException("Minimum value must be less than maximum value");
            }
            return new n(j, j, j2, j3);
        }
        throw new IllegalArgumentException("Smallest minimum value must be less than largest minimum value");
    }

    public long a(long j, k kVar) {
        if (j >= this.a && j <= this.d) {
            return j;
        }
        throw new j$.time.a("Invalid value for " + kVar + " (valid values " + this + "): " + j);
    }

    public boolean b() {
        return this.a == this.b && this.c == this.d;
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof n)) {
            return false;
        }
        n nVar = (n) obj;
        return this.a == nVar.a && this.b == nVar.b && this.c == nVar.c && this.d == nVar.d;
    }

    public int hashCode() {
        long j = this.a;
        long j2 = this.b;
        long j3 = j + (j2 << 16) + (j2 >> 48);
        long j4 = this.c;
        long j5 = j3 + (j4 << 32) + (j4 >> 32);
        long j6 = this.d;
        long j7 = j5 + (j6 << 48) + (j6 >> 16);
        return (int) (j7 ^ (j7 >>> 32));
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(this.a);
        if (this.a != this.b) {
            sb.append('/');
            sb.append(this.b);
        }
        sb.append(" - ");
        sb.append(this.c);
        if (this.c != this.d) {
            sb.append('/');
            sb.append(this.d);
        }
        return sb.toString();
    }
}
