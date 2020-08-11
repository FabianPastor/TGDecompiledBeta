package j$.util;

import j$.util.function.A;
import j$.util.function.B;
import j$.util.function.I;
import j$.util.function.J;

/* renamed from: j$.util.w  reason: case insensitive filesystem */
public class CLASSNAMEw implements J, B {
    private long count;
    private long max = Long.MIN_VALUE;
    private long min = Long.MAX_VALUE;
    private long sum;

    public /* synthetic */ J h(J j) {
        return I.a(this, j);
    }

    public /* synthetic */ B q(B b) {
        return A.a(this, b);
    }

    public void accept(int value) {
        accept((long) value);
    }

    public void accept(long value) {
        this.count++;
        this.sum += value;
        this.min = Math.min(this.min, value);
        this.max = Math.max(this.max, value);
    }

    public void a(CLASSNAMEw other) {
        this.count += other.count;
        this.sum += other.sum;
        this.min = Math.min(this.min, other.min);
        this.max = Math.max(this.max, other.max);
    }

    public final long d() {
        return this.count;
    }

    public final long g() {
        return this.sum;
    }

    public final long f() {
        return this.min;
    }

    public final long e() {
        return this.max;
    }

    public final double c() {
        if (d() <= 0) {
            return 0.0d;
        }
        double g = (double) g();
        double d = (double) d();
        Double.isNaN(g);
        Double.isNaN(d);
        return g / d;
    }

    public String toString() {
        return String.format("%s{count=%d, sum=%d, min=%d, average=%f, max=%d}", new Object[]{getClass().getSimpleName(), Long.valueOf(d()), Long.valueOf(g()), Long.valueOf(f()), Double.valueOf(c()), Long.valueOf(e())});
    }
}
