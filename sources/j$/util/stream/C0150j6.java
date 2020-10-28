package j$.util.stream;

import j$.util.CLASSNAMEk;
import j$.util.Spliterator;
import j$.util.function.E;
import j$.util.function.o;
import java.util.Comparator;

/* renamed from: j$.util.stream.j6  reason: case insensitive filesystem */
abstract class CLASSNAMEj6 implements Spliterator {
    final boolean a;
    final CLASSNAMEi4 b;
    private E c;
    Spliterator d;
    CLASSNAMEt5 e;
    o f;
    long g;
    CLASSNAMEj1 h;
    boolean i;

    CLASSNAMEj6(CLASSNAMEi4 i4Var, Spliterator spliterator, boolean z) {
        this.b = i4Var;
        this.c = null;
        this.d = spliterator;
        this.a = z;
    }

    CLASSNAMEj6(CLASSNAMEi4 i4Var, E e2, boolean z) {
        this.b = i4Var;
        this.c = e2;
        this.d = null;
        this.a = z;
    }

    private boolean f() {
        while (this.h.count() == 0) {
            if (this.e.p() || !this.f.a()) {
                if (this.i) {
                    return false;
                }
                this.e.m();
                this.i = true;
            }
        }
        return true;
    }

    /* access modifiers changed from: package-private */
    public final boolean a() {
        CLASSNAMEj1 j1Var = this.h;
        boolean z = false;
        if (j1Var != null) {
            long j = this.g + 1;
            this.g = j;
            if (j < j1Var.count()) {
                z = true;
            }
            if (z) {
                return z;
            }
            this.g = 0;
            this.h.clear();
            return f();
        } else if (this.i) {
            return false;
        } else {
            g();
            i();
            this.g = 0;
            this.e.n(this.d.getExactSizeIfKnown());
            return f();
        }
    }

    public final int characteristics() {
        g();
        int g2 = CLASSNAMEg6.g(this.b.r0()) & CLASSNAMEg6.k;
        return (g2 & 64) != 0 ? (g2 & -16449) | (this.d.characteristics() & 16448) : g2;
    }

    public final long estimateSize() {
        g();
        return this.d.estimateSize();
    }

    /* access modifiers changed from: package-private */
    public final void g() {
        if (this.d == null) {
            this.d = (Spliterator) this.c.get();
            this.c = null;
        }
    }

    public Comparator getComparator() {
        if (CLASSNAMEk.f(this, 4)) {
            return null;
        }
        throw new IllegalStateException();
    }

    public final long getExactSizeIfKnown() {
        g();
        if (CLASSNAMEg6.SIZED.d(this.b.r0())) {
            return this.d.getExactSizeIfKnown();
        }
        return -1;
    }

    public /* synthetic */ boolean hasCharacteristics(int i2) {
        return CLASSNAMEk.f(this, i2);
    }

    /* access modifiers changed from: package-private */
    public abstract void i();

    /* access modifiers changed from: package-private */
    public abstract CLASSNAMEj6 k(Spliterator spliterator);

    public final String toString() {
        return String.format("%s[%s]", new Object[]{getClass().getName(), this.d});
    }

    public Spliterator trySplit() {
        if (!this.a || this.i) {
            return null;
        }
        g();
        Spliterator trySplit = this.d.trySplit();
        if (trySplit == null) {
            return null;
        }
        return k(trySplit);
    }
}
