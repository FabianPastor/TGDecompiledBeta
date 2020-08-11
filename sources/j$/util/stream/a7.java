package j$.util.stream;

import j$.util.Spliterator;
import j$.util.function.Consumer;
import j$.util.function.V;
import java.util.stream.SpinedBuffer;

final class a7 extends CLASSNAMEx6 {
    a7(CLASSNAMEq4 ph, V v, boolean parallel) {
        super(ph, v, parallel);
    }

    a7(CLASSNAMEq4 ph, Spliterator spliterator, boolean parallel) {
        super(ph, spliterator, parallel);
    }

    /* access modifiers changed from: package-private */
    /* renamed from: n */
    public a7 l(Spliterator spliterator) {
        return new a7(this.b, spliterator, this.a);
    }

    /* access modifiers changed from: package-private */
    public void k() {
        SpinedBuffer<P_OUT> b = new CLASSNAMEr6();
        this.h = b;
        CLASSNAMEq4 q4Var = this.b;
        b.getClass();
        this.e = q4Var.u0(new D(b));
        this.f = new E0(this);
    }

    public /* synthetic */ boolean m() {
        return this.d.a(this.e);
    }

    public boolean a(Consumer consumer) {
        consumer.getClass();
        boolean hasNext = b();
        if (hasNext) {
            consumer.accept(((CLASSNAMEr6) this.h).B(this.g));
        }
        return hasNext;
    }

    public void forEachRemaining(Consumer consumer) {
        if (this.h != null || this.i) {
            do {
            } while (a(consumer));
            return;
        }
        consumer.getClass();
        h();
        CLASSNAMEq4 q4Var = this.b;
        consumer.getClass();
        q4Var.t0(new Q0(consumer), this.d);
        this.i = true;
    }
}
