package j$.util;

import j$.util.function.CLASSNAMEs;
import j$.util.function.CLASSNAMEt;

/* renamed from: j$.util.q  reason: case insensitive filesystem */
public class CLASSNAMEq implements CLASSNAMEt {
    private double a;
    private double b;
    private long count;
    private double max = Double.NEGATIVE_INFINITY;
    private double min = Double.POSITIVE_INFINITY;
    private double sum;

    public /* synthetic */ CLASSNAMEt p(CLASSNAMEt tVar) {
        return CLASSNAMEs.a(this, tVar);
    }

    public void accept(double value) {
        this.count++;
        this.b += value;
        h(value);
        this.min = Math.min(this.min, value);
        this.max = Math.max(this.max, value);
    }

    public void a(CLASSNAMEq other) {
        this.count += other.count;
        this.b += other.b;
        h(other.sum);
        h(other.a);
        this.min = Math.min(this.min, other.min);
        this.max = Math.max(this.max, other.max);
    }

    private void h(double value) {
        double tmp = value - this.a;
        double d = this.sum;
        double velvel = d + tmp;
        this.a = (velvel - d) - tmp;
        this.sum = velvel;
    }

    public final long d() {
        return this.count;
    }

    public final double g() {
        double tmp = this.sum + this.a;
        if (!Double.isNaN(tmp) || !Double.isInfinite(this.b)) {
            return tmp;
        }
        return this.b;
    }

    public final double f() {
        return this.min;
    }

    public final double e() {
        return this.max;
    }

    public final double c() {
        if (d() <= 0) {
            return 0.0d;
        }
        double g = g();
        double d = (double) d();
        Double.isNaN(d);
        return g / d;
    }

    public String toString() {
        return String.format("%s{count=%d, sum=%f, min=%f, average=%f, max=%f}", new Object[]{getClass().getSimpleName(), Long.valueOf(d()), Double.valueOf(g()), Double.valueOf(f()), Double.valueOf(c()), Double.valueOf(e())});
    }
}
