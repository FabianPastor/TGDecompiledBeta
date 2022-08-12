package j$.util.stream;

import j$.util.CLASSNAMEa;
import j$.util.function.Consumer;
import j$.util.function.f;
import j$.util.function.y;
import j$.util.t;
import j$.util.u;

/* renamed from: j$.util.stream.o4  reason: case insensitive filesystem */
final class CLASSNAMEo4 extends CLASSNAMEf4 implements t {
    CLASSNAMEo4(CLASSNAMEy2 y2Var, y yVar, boolean z) {
        super(y2Var, yVar, z);
    }

    CLASSNAMEo4(CLASSNAMEy2 y2Var, u uVar, boolean z) {
        super(y2Var, uVar, z);
    }

    public /* synthetic */ boolean b(Consumer consumer) {
        return CLASSNAMEa.j(this, consumer);
    }

    /* renamed from: e */
    public void forEachRemaining(f fVar) {
        if (this.h != null || this.i) {
            do {
            } while (tryAdvance(fVar));
            return;
        }
        fVar.getClass();
        h();
        this.b.u0(new CLASSNAMEn4(fVar), this.d);
        this.i = true;
    }

    public /* synthetic */ void forEachRemaining(Consumer consumer) {
        CLASSNAMEa.b(this, consumer);
    }

    /* access modifiers changed from: package-private */
    public void j() {
        U3 u3 = new U3();
        this.h = u3;
        this.e = this.b.v0(new CLASSNAMEn4(u3));
        this.f = new CLASSNAMEb(this);
    }

    /* renamed from: k */
    public boolean tryAdvance(f fVar) {
        fVar.getClass();
        boolean a = a();
        if (a) {
            U3 u3 = (U3) this.h;
            long j = this.g;
            int w = u3.w(j);
            fVar.accept((u3.c == 0 && w == 0) ? ((double[]) u3.e)[(int) j] : ((double[][]) u3.f)[w][(int) (j - u3.d[w])]);
        }
        return a;
    }

    /* access modifiers changed from: package-private */
    public CLASSNAMEf4 l(u uVar) {
        return new CLASSNAMEo4(this.b, uVar, this.a);
    }

    public t trySplit() {
        return (t) super.trySplit();
    }
}
