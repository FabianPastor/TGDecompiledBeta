package j$.util;

import j$.util.function.j;
import j$.util.function.k;
import j$.util.function.o;
import j$.util.function.p;

/* renamed from: j$.util.i  reason: case insensitive filesystem */
public class CLASSNAMEi implements p, k {
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

    public void b(CLASSNAMEi iVar) {
        this.count += iVar.count;
        this.sum += iVar.sum;
        this.min = Math.min(this.min, iVar.min);
        this.max = Math.max(this.max, iVar.max);
    }

    public p f(p pVar) {
        pVar.getClass();
        return new o(this, pVar);
    }

    public k l(k kVar) {
        kVar.getClass();
        return new j(this, kVar);
    }

    public String toString() {
        double d;
        Object[] objArr = new Object[6];
        objArr[0] = CLASSNAMEi.class.getSimpleName();
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
