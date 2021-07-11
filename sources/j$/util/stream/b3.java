package j$.util.stream;

import j$.util.Spliterator;
import j$.util.function.C;
import j$.util.function.Consumer;
import j$.util.function.J;
import j$.util.r;
import j$.util.stream.S2;

final class b3<P_IN> extends W2<P_IN, Long, S2.d> implements Spliterator.c {
    b3(T1 t1, Spliterator spliterator, boolean z) {
        super(t1, spliterator, z);
    }

    b3(T1 t1, J j, boolean z) {
        super(t1, j, z);
    }

    public /* synthetic */ boolean b(Consumer consumer) {
        return r.g(this, consumer);
    }

    /* renamed from: d */
    public void forEachRemaining(C c) {
        if (this.h != null || this.i) {
            do {
            } while (tryAdvance(c));
            return;
        }
        c.getClass();
        h();
        this.b.t0(new V0(c), this.d);
        this.i = true;
    }

    public /* synthetic */ void forEachRemaining(Consumer consumer) {
        r.c(this, consumer);
    }

    /* renamed from: i */
    public boolean tryAdvance(C c) {
        c.getClass();
        boolean a = a();
        if (a) {
            S2.d dVar = (S2.d) this.h;
            long j = this.g;
            int v = dVar.v(j);
            c.accept((dVar.c == 0 && v == 0) ? ((long[]) dVar.e)[(int) j] : ((long[][]) dVar.f)[v][(int) (j - dVar.d[v])]);
        }
        return a;
    }

    /* access modifiers changed from: package-private */
    public void j() {
        S2.d dVar = new S2.d();
        this.h = dVar;
        this.e = this.b.u0(new CLASSNAMEn0(dVar));
        this.f = new A0(this);
    }

    /* access modifiers changed from: package-private */
    public W2 k(Spliterator spliterator) {
        return new b3(this.b, spliterator, this.a);
    }

    public Spliterator.c trySplit() {
        return (Spliterator.c) super.trySplit();
    }
}
