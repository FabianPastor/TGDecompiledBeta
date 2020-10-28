package j$.util;

import j$.util.function.Consumer;
import j$.util.function.q;
import java.util.Comparator;

final class L implements C {
    private final double[] a;
    private int b;
    private final int c;
    private final int d;

    public L(double[] dArr, int i, int i2, int i3) {
        this.a = dArr;
        this.b = i;
        this.c = i2;
        this.d = i3 | 64 | 16384;
    }

    public /* synthetic */ boolean b(Consumer consumer) {
        return CLASSNAMEw.d(this, consumer);
    }

    public int characteristics() {
        return this.d;
    }

    /* renamed from: e */
    public void forEachRemaining(q qVar) {
        int i;
        qVar.getClass();
        double[] dArr = this.a;
        int length = dArr.length;
        int i2 = this.c;
        if (length >= i2 && (i = this.b) >= 0) {
            this.b = i2;
            if (i < i2) {
                do {
                    qVar.accept(dArr[i]);
                    i++;
                } while (i < i2);
            }
        }
    }

    public long estimateSize() {
        return (long) (this.c - this.b);
    }

    public /* synthetic */ void forEachRemaining(Consumer consumer) {
        CLASSNAMEw.a(this, consumer);
    }

    public Comparator getComparator() {
        if (CLASSNAMEk.f(this, 4)) {
            return null;
        }
        throw new IllegalStateException();
    }

    public /* synthetic */ long getExactSizeIfKnown() {
        return CLASSNAMEk.e(this);
    }

    public /* synthetic */ boolean hasCharacteristics(int i) {
        return CLASSNAMEk.f(this, i);
    }

    /* renamed from: o */
    public boolean tryAdvance(q qVar) {
        qVar.getClass();
        int i = this.b;
        if (i < 0 || i >= this.c) {
            return false;
        }
        double[] dArr = this.a;
        this.b = i + 1;
        qVar.accept(dArr[i]);
        return true;
    }

    public C trySplit() {
        int i = this.b;
        int i2 = (this.c + i) >>> 1;
        if (i >= i2) {
            return null;
        }
        double[] dArr = this.a;
        this.b = i2;
        return new L(dArr, i, i2, this.d);
    }
}
