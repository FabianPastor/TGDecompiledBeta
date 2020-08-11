package j$.util.stream;

import j$.util.Spliterator;
import j$.util.T;
import j$.util.U;
import j$.util.function.Consumer;
import j$.util.function.J;
import j$.util.function.V;

final class M6 extends CLASSNAMEx6 implements U {
    public /* synthetic */ boolean a(Consumer consumer) {
        return T.b(this, consumer);
    }

    public /* synthetic */ void forEachRemaining(Consumer consumer) {
        T.a(this, consumer);
    }

    M6(CLASSNAMEq4 ph, V v, boolean parallel) {
        super(ph, v, parallel);
    }

    M6(CLASSNAMEq4 ph, Spliterator spliterator, boolean parallel) {
        super(ph, spliterator, parallel);
    }

    /* access modifiers changed from: package-private */
    public CLASSNAMEx6 l(Spliterator spliterator) {
        return new M6(this.b, spliterator, this.a);
    }

    /* access modifiers changed from: package-private */
    public void k() {
        CLASSNAMEo6 b = new CLASSNAMEo6();
        this.h = b;
        CLASSNAMEq4 q4Var = this.b;
        b.getClass();
        this.e = q4Var.u0(new CLASSNAMEb(b));
        this.f = new CLASSNAMEy0(this);
    }

    public /* synthetic */ boolean m() {
        return this.d.a(this.e);
    }

    public U trySplit() {
        return (U) super.trySplit();
    }

    /* renamed from: i */
    public boolean tryAdvance(J consumer) {
        consumer.getClass();
        boolean hasNext = b();
        if (hasNext) {
            consumer.accept(((CLASSNAMEo6) this.h).K(this.g));
        }
        return hasNext;
    }

    /* renamed from: d */
    public void forEachRemaining(J consumer) {
        if (this.h != null || this.i) {
            do {
            } while (tryAdvance(consumer));
            return;
        }
        consumer.getClass();
        h();
        CLASSNAMEq4 q4Var = this.b;
        consumer.getClass();
        q4Var.t0(new C(consumer), this.d);
        this.i = true;
    }
}
