package j$.time;

import j$.time.format.p;
import j$.time.format.s;
import java.io.Serializable;
/* loaded from: classes2.dex */
public final class YearMonth implements Comparable<YearMonth>, Serializable {
    private final int a;
    private final int b;

    static {
        p l = new p().l(j$.time.temporal.a.YEAR, 4, 10, s.EXCEEDS_PAD);
        l.e('-');
        l.k(j$.time.temporal.a.MONTH_OF_YEAR, 2);
        l.s();
    }

    private YearMonth(int i, int i2) {
        this.a = i;
        this.b = i2;
    }

    public static YearMonth of(int i, int i2) {
        j$.time.temporal.a.YEAR.b(i);
        j$.time.temporal.a.MONTH_OF_YEAR.b(i2);
        return new YearMonth(i, i2);
    }

    @Override // java.lang.Comparable
    public int compareTo(YearMonth yearMonth) {
        YearMonth yearMonth2 = yearMonth;
        int i = this.a - yearMonth2.a;
        return i == 0 ? this.b - yearMonth2.b : i;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof YearMonth)) {
            return false;
        }
        YearMonth yearMonth = (YearMonth) obj;
        return this.a == yearMonth.a && this.b == yearMonth.b;
    }

    public int hashCode() {
        return this.a ^ (this.b << 27);
    }

    public int lengthOfMonth() {
        d a = d.a(this.b);
        j$.time.chrono.c cVar = j$.time.chrono.c.a;
        long j = this.a;
        boolean z = (3 & j) == 0 && (j % 100 != 0 || j % 400 == 0);
        a.getClass();
        int i = c.a[a.ordinal()];
        return i != 1 ? (i == 2 || i == 3 || i == 4 || i == 5) ? 30 : 31 : z ? 29 : 28;
    }

    public String toString() {
        int i;
        int abs = Math.abs(this.a);
        StringBuilder sb = new StringBuilder(9);
        if (abs < 1000) {
            int i2 = this.a;
            if (i2 < 0) {
                sb.append(i2 - 10000);
                i = 1;
            } else {
                sb.append(i2 + 10000);
                i = 0;
            }
            sb.deleteCharAt(i);
        } else {
            sb.append(this.a);
        }
        sb.append(this.b < 10 ? "-0" : "-");
        sb.append(this.b);
        return sb.toString();
    }
}
