package j$.util.concurrent;

import j$.util.C;
import j$.util.CLASSNAMEk;
import j$.util.CLASSNAMEw;
import j$.util.function.Consumer;
import j$.util.function.q;
import java.util.Comparator;

final class A implements C {
    long a;
    final long b;
    final double c;
    final double d;

    A(long j, long j2, double d2, double d3) {
        this.a = j;
        this.b = j2;
        this.c = d2;
        this.d = d3;
    }

    /* renamed from: a */
    public A trySplit() {
        long j = this.a;
        long j2 = (this.b + j) >>> 1;
        if (j2 <= j) {
            return null;
        }
        this.a = j2;
        return new A(j, j2, this.c, this.d);
    }

    public /* synthetic */ boolean b(Consumer consumer) {
        return CLASSNAMEw.d(this, consumer);
    }

    public int characteristics() {
        return 17728;
    }

    /* renamed from: e */
    public void forEachRemaining(q qVar) {
        qVar.getClass();
        long j = this.a;
        long j2 = this.b;
        if (j < j2) {
            this.a = j2;
            double d2 = this.c;
            double d3 = this.d;
            D b2 = D.b();
            do {
                qVar.accept(b2.d(d2, d3));
                j++;
            } while (j < j2);
        }
    }

    public long estimateSize() {
        return this.b - this.a;
    }

    public /* synthetic */ void forEachRemaining(Consumer consumer) {
        CLASSNAMEw.a(this, consumer);
    }

    public Comparator getComparator() {
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
        long j = this.a;
        if (j >= this.b) {
            return false;
        }
        qVar.accept(D.b().d(this.c, this.d));
        this.a = j + 1;
        return true;
    }
}
