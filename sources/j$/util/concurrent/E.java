package j$.util.concurrent;

import j$.util.N;
import j$.util.T;
import j$.util.U;
import j$.util.function.Consumer;
import j$.util.function.J;
import java.util.Comparator;

final class E implements U {
    long a;
    final long b;
    final long c;
    final long d;

    public /* synthetic */ boolean a(Consumer consumer) {
        return T.b(this, consumer);
    }

    public /* synthetic */ void forEachRemaining(Consumer consumer) {
        T.a(this, consumer);
    }

    public /* synthetic */ Comparator getComparator() {
        N.a(this);
        throw null;
    }

    public /* synthetic */ long getExactSizeIfKnown() {
        return N.b(this);
    }

    public /* synthetic */ boolean hasCharacteristics(int i) {
        return N.c(this, i);
    }

    E(long index, long fence, long origin, long bound) {
        this.a = index;
        this.b = fence;
        this.c = origin;
        this.d = bound;
    }

    /* renamed from: b */
    public E trySplit() {
        long i = this.a;
        long m = (this.b + i) >>> 1;
        if (m <= i) {
            return null;
        }
        this.a = m;
        return new E(i, m, this.c, this.d);
    }

    public long estimateSize() {
        return this.b - this.a;
    }

    public int characteristics() {
        return 17728;
    }

    /* renamed from: i */
    public boolean tryAdvance(J consumer) {
        if (consumer != null) {
            long i = this.a;
            if (i >= this.b) {
                return false;
            }
            consumer.accept(F.b().k(this.c, this.d));
            this.a = 1 + i;
            return true;
        }
        throw null;
    }

    /* renamed from: d */
    public void forEachRemaining(J consumer) {
        long j;
        if (consumer != null) {
            long i = this.a;
            long f = this.b;
            if (i < f) {
                this.a = f;
                long o = this.c;
                long b2 = this.d;
                F rng = F.b();
                do {
                    consumer.accept(rng.k(o, b2));
                    j = 1 + i;
                    i = j;
                } while (j < f);
                return;
            }
            return;
        }
        throw null;
    }
}
