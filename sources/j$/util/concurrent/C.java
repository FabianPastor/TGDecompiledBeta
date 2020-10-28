package j$.util.concurrent;

import j$.util.CLASSNAMEk;
import j$.util.CLASSNAMEw;
import j$.util.E;
import j$.util.function.Consumer;
import j$.util.function.y;
import java.util.Comparator;

final class C implements E {
    long a;
    final long b;
    final long c;
    final long d;

    C(long j, long j2, long j3, long j4) {
        this.a = j;
        this.b = j2;
        this.c = j3;
        this.d = j4;
    }

    /* renamed from: a */
    public C trySplit() {
        long j = this.a;
        long j2 = (this.b + j) >>> 1;
        if (j2 <= j) {
            return null;
        }
        this.a = j2;
        return new C(j, j2, this.c, this.d);
    }

    public /* synthetic */ boolean b(Consumer consumer) {
        return CLASSNAMEw.f(this, consumer);
    }

    public int characteristics() {
        return 17728;
    }

    /* renamed from: d */
    public void forEachRemaining(y yVar) {
        yVar.getClass();
        long j = this.a;
        long j2 = this.b;
        if (j < j2) {
            this.a = j2;
            long j3 = this.c;
            long j4 = this.d;
            D b2 = D.b();
            do {
                yVar.accept(b2.f(j3, j4));
                j++;
            } while (j < j2);
        }
    }

    public long estimateSize() {
        return this.b - this.a;
    }

    public /* synthetic */ void forEachRemaining(Consumer consumer) {
        CLASSNAMEw.c(this, consumer);
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

    /* renamed from: j */
    public boolean tryAdvance(y yVar) {
        yVar.getClass();
        long j = this.a;
        if (j >= this.b) {
            return false;
        }
        yVar.accept(D.b().f(this.c, this.d));
        this.a = j + 1;
        return true;
    }
}
