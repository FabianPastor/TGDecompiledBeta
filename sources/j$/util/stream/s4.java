package j$.util.stream;

import j$.util.CLASSNAMEa;
import j$.util.function.Consumer;
import j$.util.function.q;
import j$.util.function.y;
import j$.util.u;
import j$.util.v;

final class s4 extends CLASSNAMEf4 implements v {
    s4(CLASSNAMEy2 y2Var, y yVar, boolean z) {
        super(y2Var, yVar, z);
    }

    s4(CLASSNAMEy2 y2Var, u uVar, boolean z) {
        super(y2Var, uVar, z);
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
        this.b.u0(new r4(qVar), this.d);
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
            Y3 y3 = (Y3) this.h;
            long j = this.g;
            int w = y3.w(j);
            qVar.accept((y3.c == 0 && w == 0) ? ((long[]) y3.e)[(int) j] : ((long[][]) y3.f)[w][(int) (j - y3.d[w])]);
        }
        return a;
    }

    /* access modifiers changed from: package-private */
    public void j() {
        Y3 y3 = new Y3();
        this.h = y3;
        this.e = this.b.v0(new r4(y3));
        this.f = new CLASSNAMEb(this);
    }

    /* access modifiers changed from: package-private */
    public CLASSNAMEf4 l(u uVar) {
        return new s4(this.b, uVar, this.a);
    }

    public v trySplit() {
        return (v) super.trySplit();
    }
}
