package j$.util.concurrent;

import j$.util.N;
import j$.util.O;
import j$.util.P;
import j$.util.function.CLASSNAMEt;
import j$.util.function.Consumer;
import java.util.Comparator;

final class C implements P {
    long a;
    final long b;
    final double c;
    final double d;

    public /* synthetic */ boolean a(Consumer consumer) {
        return O.b(this, consumer);
    }

    public /* synthetic */ void forEachRemaining(Consumer consumer) {
        O.a(this, consumer);
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

    C(long index, long fence, double origin, double bound) {
        this.a = index;
        this.b = fence;
        this.c = origin;
        this.d = bound;
    }

    /* renamed from: b */
    public C trySplit() {
        long i = this.a;
        long m = (this.b + i) >>> 1;
        if (m <= i) {
            return null;
        }
        this.a = m;
        return new C(i, m, this.c, this.d);
    }

    public long estimateSize() {
        return this.b - this.a;
    }

    public int characteristics() {
        return 17728;
    }

    /* renamed from: j */
    public boolean tryAdvance(CLASSNAMEt consumer) {
        if (consumer != null) {
            long i = this.a;
            if (i >= this.b) {
                return false;
            }
            consumer.accept(F.b().i(this.c, this.d));
            this.a = 1 + i;
            return true;
        }
        throw null;
    }

    /* renamed from: e */
    public void forEachRemaining(CLASSNAMEt consumer) {
        long j;
        if (consumer != null) {
            long i = this.a;
            long f = this.b;
            if (i < f) {
                this.a = f;
                double o = this.c;
                double b2 = this.d;
                F rng = F.b();
                do {
                    consumer.accept(rng.i(o, b2));
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
