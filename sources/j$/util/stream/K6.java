package j$.util.stream;

import j$.util.O;
import j$.util.P;
import j$.util.Spliterator;
import j$.util.function.CLASSNAMEt;
import j$.util.function.Consumer;
import j$.util.function.V;

final class K6 extends CLASSNAMEx6 implements P {
    public /* synthetic */ boolean a(Consumer consumer) {
        return O.b(this, consumer);
    }

    public /* synthetic */ void forEachRemaining(Consumer consumer) {
        O.a(this, consumer);
    }

    K6(CLASSNAMEq4 ph, V v, boolean parallel) {
        super(ph, v, parallel);
    }

    K6(CLASSNAMEq4 ph, Spliterator spliterator, boolean parallel) {
        super(ph, spliterator, parallel);
    }

    /* access modifiers changed from: package-private */
    public CLASSNAMEx6 l(Spliterator spliterator) {
        return new K6(this.b, spliterator, this.a);
    }

    /* access modifiers changed from: package-private */
    public void k() {
        CLASSNAMEk6 b = new CLASSNAMEk6();
        this.h = b;
        CLASSNAMEq4 q4Var = this.b;
        b.getClass();
        this.e = q4Var.u0(new CLASSNAMEc1(b));
        this.f = new CLASSNAMEw0(this);
    }

    public /* synthetic */ boolean m() {
        return this.d.a(this.e);
    }

    public P trySplit() {
        return (P) super.trySplit();
    }

    /* renamed from: j */
    public boolean tryAdvance(CLASSNAMEt consumer) {
        consumer.getClass();
        boolean hasNext = b();
        if (hasNext) {
            consumer.accept(((CLASSNAMEk6) this.h).K(this.g));
        }
        return hasNext;
    }

    /* renamed from: e */
    public void forEachRemaining(CLASSNAMEt consumer) {
        if (this.h != null || this.i) {
            do {
            } while (tryAdvance(consumer));
            return;
        }
        consumer.getClass();
        h();
        CLASSNAMEq4 q4Var = this.b;
        consumer.getClass();
        q4Var.t0(new S0(consumer), this.d);
        this.i = true;
    }
}
