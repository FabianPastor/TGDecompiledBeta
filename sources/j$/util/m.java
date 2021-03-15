package j$.util;

import j$.util.function.CLASSNAMEf;
import j$.util.function.q;

public class m implements q {

    /* renamed from: a  reason: collision with root package name */
    private double var_a;
    private double b;
    private long count;
    private double max = Double.NEGATIVE_INFINITY;
    private double min = Double.POSITIVE_INFINITY;
    private double sum;

    private void d(double d) {
        double d2 = d - this.var_a;
        double d3 = this.sum;
        double d4 = d3 + d2;
        this.var_a = (d4 - d3) - d2;
        this.sum = d4;
    }

    public void accept(double d) {
        this.count++;
        this.b += d;
        d(d);
        this.min = Math.min(this.min, d);
        this.max = Math.max(this.max, d);
    }

    public void b(m mVar) {
        this.count += mVar.count;
        this.b += mVar.b;
        d(mVar.sum);
        d(mVar.var_a);
        this.min = Math.min(this.min, mVar.min);
        this.max = Math.max(this.max, mVar.max);
    }

    public final double c() {
        double d = this.sum + this.var_a;
        return (!Double.isNaN(d) || !Double.isInfinite(this.b)) ? d : this.b;
    }

    public q k(q qVar) {
        qVar.getClass();
        return new CLASSNAMEf(this, qVar);
    }

    public String toString() {
        double d;
        Object[] objArr = new Object[6];
        objArr[0] = getClass().getSimpleName();
        objArr[1] = Long.valueOf(this.count);
        objArr[2] = Double.valueOf(c());
        objArr[3] = Double.valueOf(this.min);
        if (this.count > 0) {
            double c = c();
            double d2 = (double) this.count;
            Double.isNaN(d2);
            Double.isNaN(d2);
            d = c / d2;
        } else {
            d = 0.0d;
        }
        objArr[4] = Double.valueOf(d);
        objArr[5] = Double.valueOf(this.max);
        return String.format("%s{count=%d, sum=%f, min=%f, average=%f, max=%f}", objArr);
    }
}
