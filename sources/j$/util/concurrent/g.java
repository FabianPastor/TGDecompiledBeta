package j$.util.concurrent;

import j$.util.CLASSNAMEa;
import j$.util.function.Consumer;
import j$.util.function.l;
import j$.util.v;
import java.util.Comparator;

final class g implements v {
    long a;
    final long b;
    final int c;
    final int d;

    g(long j, long j2, int i, int i2) {
        this.a = j;
        this.b = j2;
        this.c = i;
        this.d = i2;
    }

    /* renamed from: a */
    public g trySplit() {
        long j = this.a;
        long j2 = (this.b + j) >>> 1;
        if (j2 <= j) {
            return null;
        }
        this.a = j2;
        return new g(j, j2, this.c, this.d);
    }

    public /* synthetic */ boolean b(Consumer consumer) {
        return CLASSNAMEa.k(this, consumer);
    }

    /* renamed from: c */
    public void forEachRemaining(l lVar) {
        lVar.getClass();
        long j = this.a;
        long j2 = this.b;
        if (j < j2) {
            this.a = j2;
            int i = this.c;
            int i2 = this.d;
            i b2 = i.b();
            do {
                lVar.accept(b2.e(i, i2));
                j++;
            } while (j < j2);
        }
    }

    public int characteristics() {
        return 17728;
    }

    public long estimateSize() {
        return this.b - this.a;
    }

    public /* synthetic */ void forEachRemaining(Consumer consumer) {
        CLASSNAMEa.c(this, consumer);
    }

    /* renamed from: g */
    public boolean tryAdvance(l lVar) {
        lVar.getClass();
        long j = this.a;
        if (j >= this.b) {
            return false;
        }
        lVar.accept(i.b().e(this.c, this.d));
        this.a = j + 1;
        return true;
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
}
