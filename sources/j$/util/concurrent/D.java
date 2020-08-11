package j$.util.concurrent;

import j$.util.N;
import j$.util.Q;
import j$.util.S;
import j$.util.function.B;
import j$.util.function.Consumer;
import java.util.Comparator;

final class D implements S {
    long a;
    final long b;
    final int c;
    final int d;

    public /* synthetic */ boolean a(Consumer consumer) {
        return Q.b(this, consumer);
    }

    public /* synthetic */ void forEachRemaining(Consumer consumer) {
        Q.a(this, consumer);
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

    D(long index, long fence, int origin, int bound) {
        this.a = index;
        this.b = fence;
        this.c = origin;
        this.d = bound;
    }

    /* renamed from: b */
    public D trySplit() {
        long i = this.a;
        long m = (this.b + i) >>> 1;
        if (m <= i) {
            return null;
        }
        this.a = m;
        return new D(i, m, this.c, this.d);
    }

    public long estimateSize() {
        return this.b - this.a;
    }

    public int characteristics() {
        return 17728;
    }

    /* renamed from: f */
    public boolean tryAdvance(B consumer) {
        if (consumer != null) {
            long i = this.a;
            if (i >= this.b) {
                return false;
            }
            consumer.accept(F.b().j(this.c, this.d));
            this.a = 1 + i;
            return true;
        }
        throw null;
    }

    /* renamed from: c */
    public void forEachRemaining(B consumer) {
        long j;
        if (consumer != null) {
            long i = this.a;
            long f = this.b;
            if (i < f) {
                this.a = f;
                int o = this.c;
                int b2 = this.d;
                F rng = F.b();
                do {
                    consumer.accept(rng.j(o, b2));
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
