package j$.util.concurrent;

import j$.util.CLASSNAMEa;
import j$.util.function.Consumer;
import j$.util.function.p;
import j$.util.w;
import java.util.Comparator;

final class h implements w {
    long a;
    final long b;
    final long c;
    final long d;

    h(long j, long j2, long j3, long j4) {
        this.a = j;
        this.b = j2;
        this.c = j3;
        this.d = j4;
    }

    /* renamed from: a */
    public h trySplit() {
        long j = this.a;
        long j2 = (this.b + j) >>> 1;
        if (j2 <= j) {
            return null;
        }
        this.a = j2;
        return new h(j, j2, this.c, this.d);
    }

    public /* synthetic */ boolean b(Consumer consumer) {
        return CLASSNAMEa.l(this, consumer);
    }

    public int characteristics() {
        return 17728;
    }

    /* renamed from: d */
    public void forEachRemaining(p pVar) {
        pVar.getClass();
        long j = this.a;
        long j2 = this.b;
        if (j < j2) {
            this.a = j2;
            long j3 = this.c;
            long j4 = this.d;
            i b2 = i.b();
            do {
                pVar.accept(b2.f(j3, j4));
                j++;
            } while (j < j2);
        }
    }

    public long estimateSize() {
        return this.b - this.a;
    }

    public /* synthetic */ void forEachRemaining(Consumer consumer) {
        CLASSNAMEa.d(this, consumer);
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

    /* renamed from: i */
    public boolean tryAdvance(p pVar) {
        pVar.getClass();
        long j = this.a;
        if (j >= this.b) {
            return false;
        }
        pVar.accept(i.b().f(this.c, this.d));
        this.a = j + 1;
        return true;
    }
}
