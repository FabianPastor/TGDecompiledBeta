package j$.util;

import j$.util.function.A;
import j$.util.function.B;

/* renamed from: j$.util.s  reason: case insensitive filesystem */
public class CLASSNAMEs implements B {
    private long count;
    private int max = Integer.MIN_VALUE;
    private int min = Integer.MAX_VALUE;
    private long sum;

    public /* synthetic */ B q(B b) {
        return A.a(this, b);
    }

    public void accept(int value) {
        this.count++;
        this.sum += (long) value;
        this.min = Math.min(this.min, value);
        this.max = Math.max(this.max, value);
    }

    public void a(CLASSNAMEs other) {
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

    public final int f() {
        return this.min;
    }

    public final int e() {
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
        return String.format("%s{count=%d, sum=%d, min=%d, average=%f, max=%d}", new Object[]{getClass().getSimpleName(), Long.valueOf(d()), Long.valueOf(g()), Integer.valueOf(f()), Double.valueOf(c()), Integer.valueOf(e())});
    }
}
