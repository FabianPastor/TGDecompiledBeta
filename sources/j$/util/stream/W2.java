package j$.util.stream;

import j$.util.Spliterator;
import j$.util.function.J;
import j$.util.function.o;
import j$.util.k;
import j$.util.stream.CLASSNAMEj1;
import java.util.Comparator;

abstract class W2<P_IN, P_OUT, T_BUFFER extends CLASSNAMEj1> implements Spliterator<P_OUT> {

    /* renamed from: a  reason: collision with root package name */
    final boolean var_a;
    final T1 b;
    private J c;
    Spliterator d;
    A2 e;
    o f;
    long g;
    CLASSNAMEj1 h;
    boolean i;

    W2(T1 t1, Spliterator spliterator, boolean z) {
        this.b = t1;
        this.c = null;
        this.d = spliterator;
        this.var_a = z;
    }

    W2(T1 t1, J j, boolean z) {
        this.b = t1;
        this.c = j;
        this.d = null;
        this.var_a = z;
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
        int g2 = T2.g(this.b.r0()) & T2.f;
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
        if (k.f(this, 4)) {
            return null;
        }
        throw new IllegalStateException();
    }

    public final long getExactSizeIfKnown() {
        g();
        if (T2.SIZED.d(this.b.r0())) {
            return this.d.getExactSizeIfKnown();
        }
        return -1;
    }

    public /* synthetic */ boolean hasCharacteristics(int i2) {
        return k.f(this, i2);
    }

    /* access modifiers changed from: package-private */
    public abstract void i();

    /* access modifiers changed from: package-private */
    public abstract W2 k(Spliterator spliterator);

    public final String toString() {
        return String.format("%s[%s]", new Object[]{getClass().getName(), this.d});
    }

    public Spliterator trySplit() {
        if (!this.var_a || this.i) {
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
