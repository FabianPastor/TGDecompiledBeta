package j$.util.stream;

import j$.util.C;
import j$.util.CLASSNAMEw;
import j$.util.Spliterator;
import j$.util.function.Consumer;
import j$.util.function.E;
import j$.util.function.q;

final class r6 extends CLASSNAMEj6 implements C {
    r6(CLASSNAMEi4 i4Var, Spliterator spliterator, boolean z) {
        super(i4Var, spliterator, z);
    }

    r6(CLASSNAMEi4 i4Var, E e, boolean z) {
        super(i4Var, e, z);
    }

    public /* synthetic */ boolean b(Consumer consumer) {
        return CLASSNAMEw.d(this, consumer);
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
        this.b.t0(new CLASSNAMEn0(qVar), this.d);
        this.i = true;
    }

    public /* synthetic */ void forEachRemaining(Consumer consumer) {
        CLASSNAMEw.a(this, consumer);
    }

    /* access modifiers changed from: package-private */
    public void i() {
        W5 w5 = new W5();
        this.h = w5;
        this.e = this.b.u0(new T0(w5));
        this.f = new CLASSNAMEz0(this);
    }

    /* access modifiers changed from: package-private */
    public CLASSNAMEj6 k(Spliterator spliterator) {
        return new r6(this.b, spliterator, this.a);
    }

    /* renamed from: o */
    public boolean tryAdvance(q qVar) {
        qVar.getClass();
        boolean a = a();
        if (a) {
            W5 w5 = (W5) this.h;
            long j = this.g;
            int w = w5.w(j);
            qVar.accept((w5.c == 0 && w == 0) ? ((double[]) w5.e)[(int) j] : ((double[][]) w5.f)[w][(int) (j - w5.d[w])]);
        }
        return a;
    }

    public C trySplit() {
        return (C) super.trySplit();
    }
}
