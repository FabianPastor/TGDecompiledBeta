package j$.util.stream;

import j$.util.CLASSNAMEa;
import j$.util.function.Consumer;
import j$.util.function.q;
import j$.util.function.y;
import j$.util.w;

final class t4 extends CLASSNAMEg4 implements w {
    t4(CLASSNAMEz2 z2Var, y yVar, boolean z) {
        super(z2Var, yVar, z);
    }

    t4(CLASSNAMEz2 z2Var, j$.util.y yVar, boolean z) {
        super(z2Var, yVar, z);
    }

    public /* synthetic */ boolean b(Consumer consumer) {
        return CLASSNAMEa.l(this, consumer);
    }

    /* renamed from: d */
    public void forEachRemaining(q qVar) {
        if (this.h != null || this.i) {
            do {
            } while (tryAdvance(qVar));
            return;
        }
        qVar.getClass();
        h();
        this.b.u0(new s4(qVar), this.d);
        this.i = true;
    }

    public /* synthetic */ void forEachRemaining(Consumer consumer) {
        CLASSNAMEa.d(this, consumer);
    }

    /* renamed from: i */
    public boolean tryAdvance(q qVar) {
        qVar.getClass();
        boolean a = a();
        if (a) {
            Z3 z3 = (Z3) this.h;
            long j = this.g;
            int w = z3.w(j);
            qVar.accept((z3.c == 0 && w == 0) ? ((long[]) z3.e)[(int) j] : ((long[][]) z3.f)[w][(int) (j - z3.d[w])]);
        }
        return a;
    }

    /* access modifiers changed from: package-private */
    public void j() {
        Z3 z3 = new Z3();
        this.h = z3;
        this.e = this.b.v0(new s4(z3));
        this.f = new CLASSNAMEb(this);
    }

    /* access modifiers changed from: package-private */
    public CLASSNAMEg4 l(j$.util.y yVar) {
        return new t4(this.b, yVar, this.a);
    }

    public w trySplit() {
        return (w) super.trySplit();
    }
}
