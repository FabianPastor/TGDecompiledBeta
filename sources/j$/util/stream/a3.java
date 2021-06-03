package j$.util.stream;

import j$.util.Spliterator;
import j$.util.function.Consumer;
import j$.util.function.J;
import j$.util.function.w;
import j$.util.r;
import j$.util.stream.S2;

final class a3<P_IN> extends W2<P_IN, Integer, S2.c> implements Spliterator.b {
    a3(T1 t1, Spliterator spliterator, boolean z) {
        super(t1, spliterator, z);
    }

    a3(T1 t1, J j, boolean z) {
        super(t1, j, z);
    }

    public /* synthetic */ boolean b(Consumer consumer) {
        return r.f(this, consumer);
    }

    /* renamed from: c */
    public void forEachRemaining(w wVar) {
        if (this.h != null || this.i) {
            do {
            } while (tryAdvance(wVar));
            return;
        }
        wVar.getClass();
        h();
        this.b.t0(new CLASSNAMEe(wVar), this.d);
        this.i = true;
    }

    public /* synthetic */ void forEachRemaining(Consumer consumer) {
        r.b(this, consumer);
    }

    /* renamed from: g */
    public boolean tryAdvance(w wVar) {
        wVar.getClass();
        boolean a = a();
        if (a) {
            S2.c cVar = (S2.c) this.h;
            long j = this.g;
            int v = cVar.v(j);
            wVar.accept((cVar.c == 0 && v == 0) ? ((int[]) cVar.e)[(int) j] : ((int[][]) cVar.f)[v][(int) (j - cVar.d[v])]);
        }
        return a;
    }

    /* access modifiers changed from: package-private */
    public void j() {
        S2.c cVar = new S2.c();
        this.h = cVar;
        this.e = this.b.u0(new L0(cVar));
        this.f = new CLASSNAMEz0(this);
    }

    /* access modifiers changed from: package-private */
    public W2 k(Spliterator spliterator) {
        return new a3(this.b, spliterator, this.a);
    }

    public Spliterator.b trySplit() {
        return (Spliterator.b) super.trySplit();
    }
}
