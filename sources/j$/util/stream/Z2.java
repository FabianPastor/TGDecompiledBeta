package j$.util.stream;

import j$.util.Spliterator;
import j$.util.function.Consumer;
import j$.util.function.J;
import j$.util.function.q;
import j$.util.s;
import j$.util.stream.S2;

final class Z2<P_IN> extends W2<P_IN, Double, S2.b> implements Spliterator.a {
    Z2(T1 t1, Spliterator spliterator, boolean z) {
        super(t1, spliterator, z);
    }

    Z2(T1 t1, J j, boolean z) {
        super(t1, j, z);
    }

    public /* synthetic */ boolean b(Consumer consumer) {
        return s.d(this, consumer);
    }

    /* renamed from: e */
    public void forEachRemaining(q qVar) {
        if (this.h != null || this.i) {
            do {
            } while (tryAdvance(qVar));
            return;
        }
        qVar.getClass();
        g();
        this.b.t0(new CLASSNAMEl0(qVar), this.d);
        this.i = true;
    }

    public /* synthetic */ void forEachRemaining(Consumer consumer) {
        s.a(this, consumer);
    }

    /* access modifiers changed from: package-private */
    public void i() {
        S2.b bVar = new S2.b();
        this.h = bVar;
        this.e = this.b.u0(new T0(bVar));
        this.f = new CLASSNAMEy0(this);
    }

    /* access modifiers changed from: package-private */
    public W2 k(Spliterator spliterator) {
        return new Z2(this.b, spliterator, this.var_a);
    }

    /* renamed from: o */
    public boolean tryAdvance(q qVar) {
        qVar.getClass();
        boolean a2 = a();
        if (a2) {
            S2.b bVar = (S2.b) this.h;
            long j = this.g;
            int w = bVar.w(j);
            qVar.accept((bVar.c == 0 && w == 0) ? ((double[]) bVar.e)[(int) j] : ((double[][]) bVar.f)[w][(int) (j - bVar.d[w])]);
        }
        return a2;
    }

    public Spliterator.a trySplit() {
        return (Spliterator.a) super.trySplit();
    }
}
