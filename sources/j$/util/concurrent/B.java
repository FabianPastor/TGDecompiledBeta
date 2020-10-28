package j$.util.concurrent;

import j$.util.CLASSNAMEk;
import j$.util.CLASSNAMEw;
import j$.util.D;
import j$.util.function.Consumer;
import j$.util.function.u;
import java.util.Comparator;

final class B implements D {
    long a;
    final long b;
    final int c;
    final int d;

    B(long j, long j2, int i, int i2) {
        this.a = j;
        this.b = j2;
        this.c = i;
        this.d = i2;
    }

    /* renamed from: a */
    public B trySplit() {
        long j = this.a;
        long j2 = (this.b + j) >>> 1;
        if (j2 <= j) {
            return null;
        }
        this.a = j2;
        return new B(j, j2, this.c, this.d);
    }

    public /* synthetic */ boolean b(Consumer consumer) {
        return CLASSNAMEw.e(this, consumer);
    }

    /* renamed from: c */
    public void forEachRemaining(u uVar) {
        uVar.getClass();
        long j = this.a;
        long j2 = this.b;
        if (j < j2) {
            this.a = j2;
            int i = this.c;
            int i2 = this.d;
            D b2 = D.b();
            do {
                uVar.accept(b2.e(i, i2));
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
        CLASSNAMEw.b(this, consumer);
    }

    public Comparator getComparator() {
        throw new IllegalStateException();
    }

    public /* synthetic */ long getExactSizeIfKnown() {
        return CLASSNAMEk.e(this);
    }

    /* renamed from: h */
    public boolean tryAdvance(u uVar) {
        uVar.getClass();
        long j = this.a;
        if (j >= this.b) {
            return false;
        }
        uVar.accept(D.b().e(this.c, this.d));
        this.a = j + 1;
        return true;
    }

    public /* synthetic */ boolean hasCharacteristics(int i) {
        return CLASSNAMEk.f(this, i);
    }
}
