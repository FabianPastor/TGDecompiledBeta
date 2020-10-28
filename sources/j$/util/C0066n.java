package j$.util;

import j$.util.function.f;
import j$.util.function.q;

/* renamed from: j$.util.n  reason: case insensitive filesystem */
public class CLASSNAMEn implements q {
    private double a;
    private double b;
    private long count;
    private double max = Double.NEGATIVE_INFINITY;
    private double min = Double.POSITIVE_INFINITY;
    private double sum;

    private void g(double d) {
        double d2 = d - this.a;
        double d3 = this.sum;
        double d4 = d3 + d2;
        this.a = (d4 - d3) - d2;
        this.sum = d4;
    }

    public void accept(double d) {
        this.count++;
        this.b += d;
        g(d);
        this.min = Math.min(this.min, d);
        this.max = Math.max(this.max, d);
    }

    public void b(CLASSNAMEn nVar) {
        this.count += nVar.count;
        this.b += nVar.b;
        g(nVar.sum);
        g(nVar.a);
        this.min = Math.min(this.min, nVar.min);
        this.max = Math.max(this.max, nVar.max);
    }

    public final long c() {
        return this.count;
    }

    public final double d() {
        return this.max;
    }

    public final double e() {
        return this.min;
    }

    public final double f() {
        double d = this.sum + this.a;
        return (!Double.isNaN(d) || !Double.isInfinite(this.b)) ? d : this.b;
    }

    public q k(q qVar) {
        qVar.getClass();
        return new f(this, qVar);
    }

    public String toString() {
        double d;
        Object[] objArr = new Object[6];
        objArr[0] = getClass().getSimpleName();
        objArr[1] = Long.valueOf(this.count);
        objArr[2] = Double.valueOf(f());
        objArr[3] = Double.valueOf(this.min);
        if (this.count > 0) {
            double f = f();
            double d2 = (double) this.count;
            Double.isNaN(d2);
            Double.isNaN(d2);
            d = f / d2;
        } else {
            d = 0.0d;
        }
        objArr[4] = Double.valueOf(d);
        objArr[5] = Double.valueOf(this.max);
        return String.format("%s{count=%d, sum=%f, min=%f, average=%f, max=%f}", objArr);
    }
}
