package j$.time;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.regex.Pattern;

public final class b implements Comparable, Serializable {
    public static final b c = new b(0, 0);
    private final long a;
    private final int b;

    static {
        BigInteger.valueOf(NUM);
        Pattern.compile("([-+]?)P(?:([-+]?[0-9]+)D)?(T(?:([-+]?[0-9]+)H)?(?:([-+]?[0-9]+)M)?(?:([-+]?[0-9]+)(?:[.,]([0-9]{0,9}))?S)?)?", 2);
    }

    private b(long j, int i) {
        this.a = j;
        this.b = i;
    }

    private static b a(long j, int i) {
        return (((long) i) | j) == 0 ? c : new b(j, i);
    }

    public static b b(long j) {
        long j2 = j / NUM;
        int i = (int) (j % NUM);
        if (i < 0) {
            i = (int) (((long) i) + NUM);
            j2--;
        }
        return a(j2, i);
    }

    public static b c(long j) {
        return a(j, 0);
    }

    public static b d(long j, long j2) {
        long j3 = j2 / NUM;
        long j4 = 0;
        if (j2 - (NUM * j3) != 0 && (((j2 ^ NUM) >> 63) | 1) < 0) {
            j3--;
        }
        long j5 = j + j3;
        boolean z = false;
        boolean z2 = (j3 ^ j) < 0;
        if ((j ^ j5) >= 0) {
            z = true;
        }
        if (z2 || z) {
            long j6 = j2 % NUM;
            if (j6 != 0) {
                if ((1 | ((j2 ^ NUM) >> 63)) <= 0) {
                    j6 += NUM;
                }
                j4 = j6;
            }
            return a(j5, (int) j4);
        }
        throw new ArithmeticException();
    }

    public int compareTo(Object obj) {
        b bVar = (b) obj;
        int i = (this.a > bVar.a ? 1 : (this.a == bVar.a ? 0 : -1));
        return i != 0 ? i : this.b - bVar.b;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof b)) {
            return false;
        }
        b bVar = (b) obj;
        return this.a == bVar.a && this.b == bVar.b;
    }

    public int hashCode() {
        long j = this.a;
        return (this.b * 51) + ((int) (j ^ (j >>> 32)));
    }

    public String toString() {
        if (this == c) {
            return "PT0S";
        }
        long j = this.a;
        long j2 = j / 3600;
        int i = (int) ((j % 3600) / 60);
        int i2 = (int) (j % 60);
        StringBuilder sb = new StringBuilder(24);
        sb.append("PT");
        if (j2 != 0) {
            sb.append(j2);
            sb.append('H');
        }
        if (i != 0) {
            sb.append(i);
            sb.append('M');
        }
        if (i2 == 0 && this.b == 0 && sb.length() > 2) {
            return sb.toString();
        }
        if (i2 >= 0 || this.b <= 0) {
            sb.append(i2);
        } else if (i2 == -1) {
            sb.append("-0");
        } else {
            sb.append(i2 + 1);
        }
        if (this.b > 0) {
            int length = sb.length();
            if (i2 < 0) {
                sb.append(NUM - ((long) this.b));
            } else {
                sb.append(((long) this.b) + NUM);
            }
            while (sb.charAt(sb.length() - 1) == '0') {
                sb.setLength(sb.length() - 1);
            }
            sb.setCharAt(length, '.');
        }
        sb.append('S');
        return sb.toString();
    }
}
