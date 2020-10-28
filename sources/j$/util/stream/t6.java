package j$.util.stream;

import j$.util.CLASSNAMEw;
import j$.util.E;
import j$.util.Spliterator;
import j$.util.function.Consumer;
import j$.util.function.y;

final class t6 extends CLASSNAMEj6 implements E {
    t6(CLASSNAMEi4 i4Var, Spliterator spliterator, boolean z) {
        super(i4Var, spliterator, z);
    }

    t6(CLASSNAMEi4 i4Var, j$.util.function.E e, boolean z) {
        super(i4Var, e, z);
    }

    public /* synthetic */ boolean b(Consumer consumer) {
        return CLASSNAMEw.f(this, consumer);
    }

    /* renamed from: d */
    public void forEachRemaining(y yVar) {
        if (this.h != null || this.i) {
            do {
            } while (tryAdvance(yVar));
            return;
        }
        yVar.getClass();
        g();
        this.b.t0(new V0(yVar), this.d);
        this.i = true;
    }

    public /* synthetic */ void forEachRemaining(Consumer consumer) {
        CLASSNAMEw.c(this, consumer);
    }

    /* access modifiers changed from: package-private */
    public void i() {
        CLASSNAMEa6 a6Var = new CLASSNAMEa6();
        this.h = a6Var;
        this.e = this.b.u0(new CLASSNAMEp0(a6Var));
        this.f = new B0(this);
    }

    /* renamed from: j */
    public boolean tryAdvance(y yVar) {
        yVar.getClass();
        boolean a = a();
        if (a) {
            CLASSNAMEa6 a6Var = (CLASSNAMEa6) this.h;
            long j = this.g;
            int w = a6Var.w(j);
            yVar.accept((a6Var.c == 0 && w == 0) ? ((long[]) a6Var.e)[(int) j] : ((long[][]) a6Var.f)[w][(int) (j - a6Var.d[w])]);
        }
        return a;
    }

    /* access modifiers changed from: package-private */
    public CLASSNAMEj6 k(Spliterator spliterator) {
        return new t6(this.b, spliterator, this.a);
    }

    public E trySplit() {
        return (E) super.trySplit();
    }
}
