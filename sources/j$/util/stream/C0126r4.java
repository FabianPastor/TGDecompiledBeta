package j$.util.stream;

import j$.util.CLASSNAMEa;
import j$.util.function.Consumer;
import j$.util.function.l;
import j$.util.function.z;
import j$.util.v;
import j$.util.y;

/* renamed from: j$.util.stream.r4  reason: case insensitive filesystem */
final class CLASSNAMEr4 extends CLASSNAMEg4 implements v {
    CLASSNAMEr4(CLASSNAMEz2 z2Var, z zVar, boolean z) {
        super(z2Var, zVar, z);
    }

    CLASSNAMEr4(CLASSNAMEz2 z2Var, y yVar, boolean z) {
        super(z2Var, yVar, z);
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
        this.b.u0(new CLASSNAMEq4(lVar), this.d);
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
            X3 x3 = (X3) this.h;
            long j = this.g;
            int w = x3.w(j);
            lVar.accept((x3.c == 0 && w == 0) ? ((int[]) x3.e)[(int) j] : ((int[][]) x3.f)[w][(int) (j - x3.d[w])]);
        }
        return a;
    }

    /* access modifiers changed from: package-private */
    public void j() {
        X3 x3 = new X3();
        this.h = x3;
        this.e = this.b.v0(new CLASSNAMEq4(x3));
        this.f = new CLASSNAMEb(this);
    }

    /* access modifiers changed from: package-private */
    public CLASSNAMEg4 l(y yVar) {
        return new CLASSNAMEr4(this.b, yVar, this.a);
    }

    public v trySplit() {
        return (v) super.trySplit();
    }
}