package j$.util.stream;

import j$.util.CLASSNAMEw;
import j$.util.D;
import j$.util.Spliterator;
import j$.util.function.Consumer;
import j$.util.function.E;
import j$.util.function.u;

final class s6 extends CLASSNAMEj6 implements D {
    s6(CLASSNAMEi4 i4Var, Spliterator spliterator, boolean z) {
        super(i4Var, spliterator, z);
    }

    s6(CLASSNAMEi4 i4Var, E e, boolean z) {
        super(i4Var, e, z);
    }

    public /* synthetic */ boolean b(Consumer consumer) {
        return CLASSNAMEw.e(this, consumer);
    }

    /* renamed from: c */
    public void forEachRemaining(u uVar) {
        if (this.h != null || this.i) {
            do {
            } while (tryAdvance(uVar));
            return;
        }
        uVar.getClass();
        g();
        this.b.t0(new CLASSNAMEe(uVar), this.d);
        this.i = true;
    }

    public /* synthetic */ void forEachRemaining(Consumer consumer) {
        CLASSNAMEw.b(this, consumer);
    }

    /* renamed from: h */
    public boolean tryAdvance(u uVar) {
        uVar.getClass();
        boolean a = a();
        if (a) {
            Y5 y5 = (Y5) this.h;
            long j = this.g;
            int w = y5.w(j);
            uVar.accept((y5.c == 0 && w == 0) ? ((int[]) y5.e)[(int) j] : ((int[][]) y5.f)[w][(int) (j - y5.d[w])]);
        }
        return a;
    }

    /* access modifiers changed from: package-private */
    public void i() {
        Y5 y5 = new Y5();
        this.h = y5;
        this.e = this.b.u0(new L0(y5));
        this.f = new A0(this);
    }

    /* access modifiers changed from: package-private */
    public CLASSNAMEj6 k(Spliterator spliterator) {
        return new s6(this.b, spliterator, this.a);
    }

    public D trySplit() {
        return (D) super.trySplit();
    }
}
