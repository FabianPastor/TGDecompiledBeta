package j$.util.concurrent;

import j$.util.CLASSNAMEa;
import j$.util.function.Consumer;
import j$.util.u;
import java.util.Comparator;

final class f implements u {
    long a;
    final long b;
    final double c;
    final double d;

    f(long j, long j2, double d2, double d3) {
        this.a = j;
        this.b = j2;
        this.c = d2;
        this.d = d3;
    }

    /* renamed from: a */
    public f trySplit() {
        long j = this.a;
        long j2 = (this.b + j) >>> 1;
        if (j2 <= j) {
            return null;
        }
        this.a = j2;
        return new f(j, j2, this.c, this.d);
    }

    public /* synthetic */ boolean b(Consumer consumer) {
        return CLASSNAMEa.j(this, consumer);
    }

    public int characteristics() {
        return 17728;
    }

    /* renamed from: e */
    public void forEachRemaining(j$.util.function.f fVar) {
        fVar.getClass();
        long j = this.a;
        long j2 = this.b;
        if (j < j2) {
            this.a = j2;
            double d2 = this.c;
            double d3 = this.d;
            i b2 = i.b();
            do {
                fVar.accept(b2.d(d2, d3));
                j++;
            } while (j < j2);
        }
    }

    public long estimateSize() {
        return this.b - this.a;
    }

    public /* synthetic */ void forEachRemaining(Consumer consumer) {
        CLASSNAMEa.b(this, consumer);
    }

    public Comparator getComparator() {
        throw new IllegalStateException();
    }

    public /* synthetic */ long getExactSizeIfKnown() {
        return CLASSNAMEa.e(this);
    }

    public /* synthetic */ boolean hasCharacteristics(int i) {
        return CLASSNAMEa.f(this, i);
    }

    /* renamed from: k */
    public boolean tryAdvance(j$.util.function.f fVar) {
        fVar.getClass();
        long j = this.a;
        if (j >= this.b) {
            return false;
        }
        fVar.accept(i.b().d(this.c, this.d));
        this.a = j + 1;
        return true;
    }
}
