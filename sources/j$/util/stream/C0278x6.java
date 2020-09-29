package j$.util.stream;

import j$.util.N;
import j$.util.Spliterator;
import j$.util.function.CLASSNAMEp;
import j$.util.function.V;
import java.util.Comparator;

/* renamed from: j$.util.stream.x6  reason: case insensitive filesystem */
abstract class CLASSNAMEx6 implements Spliterator {
    final boolean a;
    final CLASSNAMEq4 b;
    private V c;
    Spliterator d;
    G5 e;
    CLASSNAMEp f;
    long g;
    CLASSNAMEj1 h;
    boolean i;

    public /* synthetic */ boolean hasCharacteristics(int i2) {
        return N.c(this, i2);
    }

    /* access modifiers changed from: package-private */
    public abstract void k();

    /* access modifiers changed from: package-private */
    public abstract CLASSNAMEx6 l(Spliterator spliterator);

    CLASSNAMEx6(CLASSNAMEq4 ph, V v, boolean parallel) {
        this.b = ph;
        this.c = v;
        this.d = null;
        this.a = parallel;
    }

    CLASSNAMEx6(CLASSNAMEq4 ph, Spliterator spliterator, boolean parallel) {
        this.b = ph;
        this.c = null;
        this.d = spliterator;
        this.a = parallel;
    }

    /* access modifiers changed from: package-private */
    public final void h() {
        if (this.d == null) {
            this.d = (Spliterator) this.c.get();
            this.c = null;
        }
    }

    /* access modifiers changed from: package-private */
    public final boolean b() {
        CLASSNAMEj1 j1Var = this.h;
        boolean z = false;
        if (j1Var != null) {
            long j = this.g + 1;
            this.g = j;
            if (j < j1Var.count()) {
                z = true;
            }
            boolean hasNext = z;
            if (hasNext) {
                return hasNext;
            }
            this.g = 0;
            this.h.clear();
            return g();
        } else if (this.i) {
            return false;
        } else {
            h();
            k();
            this.g = 0;
            this.e.s(this.d.getExactSizeIfKnown());
            return g();
        }
    }

    public Spliterator trySplit() {
        if (!this.a || this.i) {
            return null;
        }
        h();
        Spliterator trySplit = this.d.trySplit();
        if (trySplit == null) {
            return null;
        }
        return l(trySplit);
    }

    private boolean g() {
        while (this.h.count() == 0) {
            if (this.e.u() || !this.f.a()) {
                if (this.i) {
                    return false;
                }
                this.e.r();
                this.i = true;
            }
        }
        return true;
    }

    public final long estimateSize() {
        h();
        return this.d.estimateSize();
    }

    public final long getExactSizeIfKnown() {
        h();
        if (CLASSNAMEu6.SIZED.K(this.b.r0())) {
            return this.d.getExactSizeIfKnown();
        }
        return -1;
    }

    public final int characteristics() {
        h();
        int c2 = CLASSNAMEu6.O(CLASSNAMEu6.P(this.b.r0()));
        if ((c2 & 64) != 0) {
            return (c2 & -16449) | (this.d.characteristics() & 16448);
        }
        return c2;
    }

    public Comparator getComparator() {
        if (hasCharacteristics(4)) {
            return null;
        }
        throw new IllegalStateException();
    }

    public final String toString() {
        return String.format("%s[%s]", new Object[]{getClass().getName(), this.d});
    }
}
