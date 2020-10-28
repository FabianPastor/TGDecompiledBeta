package j$.util;

import j$.util.function.g;
import j$.util.function.u;

/* renamed from: j$.util.p  reason: case insensitive filesystem */
public class CLASSNAMEp implements u {
    private long count;
    private int max = Integer.MIN_VALUE;
    private int min = Integer.MAX_VALUE;
    private long sum;

    public void accept(int i) {
        this.count++;
        this.sum += (long) i;
        this.min = Math.min(this.min, i);
        this.max = Math.max(this.max, i);
    }

    public void b(CLASSNAMEp pVar) {
        this.count += pVar.count;
        this.sum += pVar.sum;
        this.min = Math.min(this.min, pVar.min);
        this.max = Math.max(this.max, pVar.max);
    }

    public final long c() {
        return this.count;
    }

    public final int d() {
        return this.max;
    }

    public final int e() {
        return this.min;
    }

    public final long f() {
        return this.sum;
    }

    public u l(u uVar) {
        uVar.getClass();
        return new g(this, uVar);
    }

    public String toString() {
        double d;
        Object[] objArr = new Object[6];
        objArr[0] = getClass().getSimpleName();
        objArr[1] = Long.valueOf(this.count);
        objArr[2] = Long.valueOf(this.sum);
        objArr[3] = Integer.valueOf(this.min);
        long j = this.count;
        if (j > 0) {
            double d2 = (double) this.sum;
            double d3 = (double) j;
            Double.isNaN(d2);
            Double.isNaN(d3);
            Double.isNaN(d2);
            Double.isNaN(d3);
            d = d2 / d3;
        } else {
            d = 0.0d;
        }
        objArr[4] = Double.valueOf(d);
        objArr[5] = Integer.valueOf(this.max);
        return String.format("%s{count=%d, sum=%d, min=%d, average=%f, max=%d}", objArr);
    }
}
