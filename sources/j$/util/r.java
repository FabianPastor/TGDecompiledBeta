package j$.util;

import j$.util.function.g;
import j$.util.function.h;
import j$.util.function.u;
import j$.util.function.y;

public class r implements y, u {
    private long count;
    private long max = Long.MIN_VALUE;
    private long min = Long.MAX_VALUE;
    private long sum;

    public void accept(int i) {
        accept((long) i);
    }

    public void accept(long j) {
        this.count++;
        this.sum += j;
        this.min = Math.min(this.min, j);
        this.max = Math.max(this.max, j);
    }

    public void b(r rVar) {
        this.count += rVar.count;
        this.sum += rVar.sum;
        this.min = Math.min(this.min, rVar.min);
        this.max = Math.max(this.max, rVar.max);
    }

    public final long c() {
        return this.count;
    }

    public final long d() {
        return this.max;
    }

    public final long e() {
        return this.min;
    }

    public final long f() {
        return this.sum;
    }

    public y g(y yVar) {
        yVar.getClass();
        return new h(this, yVar);
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
        objArr[3] = Long.valueOf(this.min);
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
        objArr[5] = Long.valueOf(this.max);
        return String.format("%s{count=%d, sum=%d, min=%d, average=%f, max=%d}", objArr);
    }
}
