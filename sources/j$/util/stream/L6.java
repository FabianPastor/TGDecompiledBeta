package j$.util.stream;

import j$.util.Q;
import j$.util.S;
import j$.util.Spliterator;
import j$.util.function.B;
import j$.util.function.Consumer;
import j$.util.function.V;

final class L6 extends CLASSNAMEx6 implements S {
    public /* synthetic */ boolean a(Consumer consumer) {
        return Q.b(this, consumer);
    }

    public /* synthetic */ void forEachRemaining(Consumer consumer) {
        Q.a(this, consumer);
    }

    L6(CLASSNAMEq4 ph, V v, boolean parallel) {
        super(ph, v, parallel);
    }

    L6(CLASSNAMEq4 ph, Spliterator spliterator, boolean parallel) {
        super(ph, spliterator, parallel);
    }

    /* access modifiers changed from: package-private */
    public CLASSNAMEx6 l(Spliterator spliterator) {
        return new L6(this.b, spliterator, this.a);
    }

    /* access modifiers changed from: package-private */
    public void k() {
        CLASSNAMEm6 b = new CLASSNAMEm6();
        this.h = b;
        CLASSNAMEq4 q4Var = this.b;
        b.getClass();
        this.e = q4Var.u0(new N0(b));
        this.f = new CLASSNAMEx0(this);
    }

    public /* synthetic */ boolean m() {
        return this.d.a(this.e);
    }

    public S trySplit() {
        return (S) super.trySplit();
    }

    /* renamed from: f */
    public boolean tryAdvance(B consumer) {
        consumer.getClass();
        boolean hasNext = b();
        if (hasNext) {
            consumer.accept(((CLASSNAMEm6) this.h).K(this.g));
        }
        return hasNext;
    }

    /* renamed from: c */
    public void forEachRemaining(B consumer) {
        if (this.h != null || this.i) {
            do {
            } while (tryAdvance(consumer));
            return;
        }
        consumer.getClass();
        h();
        CLASSNAMEq4 q4Var = this.b;
        consumer.getClass();
        q4Var.t0(new CLASSNAMEm(consumer), this.d);
        this.i = true;
    }
}
