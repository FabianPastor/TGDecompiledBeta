package j$.util;

import j$.util.function.Consumer;
import j$.util.function.f;
import java.util.Comparator;

final class C implements t {
    private final double[] a;
    private int b;
    private final int c;
    private final int d;

    public C(double[] dArr, int i, int i2, int i3) {
        this.a = dArr;
        this.b = i;
        this.c = i2;
        this.d = i3 | 64 | 16384;
    }

    public /* synthetic */ boolean b(Consumer consumer) {
        return CLASSNAMEa.j(this, consumer);
    }

    public int characteristics() {
        return this.d;
    }

    /* renamed from: e */
    public void forEachRemaining(f fVar) {
        int i;
        fVar.getClass();
        double[] dArr = this.a;
        int length = dArr.length;
        int i2 = this.c;
        if (length >= i2 && (i = this.b) >= 0) {
            this.b = i2;
            if (i < i2) {
                do {
                    fVar.accept(dArr[i]);
                    i++;
                } while (i < i2);
            }
        }
    }

    public long estimateSize() {
        return (long) (this.c - this.b);
    }

    public /* synthetic */ void forEachRemaining(Consumer consumer) {
        CLASSNAMEa.b(this, consumer);
    }

    public Comparator getComparator() {
        if (CLASSNAMEa.f(this, 4)) {
            return null;
        }
        throw new IllegalStateException();
    }

    public /* synthetic */ long getExactSizeIfKnown() {
        return CLASSNAMEa.e(this);
    }

    public /* synthetic */ boolean hasCharacteristics(int i) {
        return CLASSNAMEa.f(this, i);
    }

    /* renamed from: k */
    public boolean tryAdvance(f fVar) {
        fVar.getClass();
        int i = this.b;
        if (i < 0 || i >= this.c) {
            return false;
        }
        double[] dArr = this.a;
        this.b = i + 1;
        fVar.accept(dArr[i]);
        return true;
    }

    public t trySplit() {
        int i = this.b;
        int i2 = (this.c + i) >>> 1;
        if (i >= i2) {
            return null;
        }
        double[] dArr = this.a;
        this.b = i2;
        return new C(dArr, i, i2, this.d);
    }
}
