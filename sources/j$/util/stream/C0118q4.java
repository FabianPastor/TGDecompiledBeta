package j$.util.stream;

import j$.util.CLASSNAMEa;
import j$.util.function.Consumer;
import j$.util.function.l;
import j$.util.function.y;
import j$.util.u;

/* renamed from: j$.util.stream.q4  reason: case insensitive filesystem */
final class CLASSNAMEq4 extends CLASSNAMEf4 implements u.a {
    CLASSNAMEq4(CLASSNAMEy2 y2Var, y yVar, boolean z) {
        super(y2Var, yVar, z);
    }

    CLASSNAMEq4(CLASSNAMEy2 y2Var, u uVar, boolean z) {
        super(y2Var, uVar, z);
    }

    public /* synthetic */ boolean b(Consumer consumer) {
        return CLASSNAMEa.k(this, consumer);
    }

    /* renamed from: c */
    public void forEachRemaining(l lVar) {
        if (this.h != null || this.i) {
            do {
            } while (tryAdvance(lVar));
            return;
        }
        lVar.getClass();
        h();
        this.b.u0(new CLASSNAMEp4(lVar), this.d);
        this.i = true;
    }

    public /* synthetic */ void forEachRemaining(Consumer consumer) {
        CLASSNAMEa.c(this, consumer);
    }

    /* renamed from: g */
    public boolean tryAdvance(l lVar) {
        lVar.getClass();
        boolean a = a();
        if (a) {
            W3 w3 = (W3) this.h;
            long j = this.g;
            int w = w3.w(j);
            lVar.accept((w3.c == 0 && w == 0) ? ((int[]) w3.e)[(int) j] : ((int[][]) w3.f)[w][(int) (j - w3.d[w])]);
        }
        return a;
    }

    /* access modifiers changed from: package-private */
    public void j() {
        W3 w3 = new W3();
        this.h = w3;
        this.e = this.b.v0(new CLASSNAMEp4(w3));
        this.f = new CLASSNAMEb(this);
    }

    /* access modifiers changed from: package-private */
    public CLASSNAMEf4 l(u uVar) {
        return new CLASSNAMEq4(this.b, uVar, this.a);
    }

    public u.a trySplit() {
        return (u.a) super.trySplit();
    }
}
