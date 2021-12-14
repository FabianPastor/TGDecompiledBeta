package j$.util.stream;

import j$.util.CLASSNAMEa;
import j$.util.function.Consumer;
import j$.util.function.f;
import j$.util.function.z;
import j$.util.u;
import j$.util.y;

/* renamed from: j$.util.stream.p4  reason: case insensitive filesystem */
final class CLASSNAMEp4 extends CLASSNAMEg4 implements u {
    CLASSNAMEp4(CLASSNAMEz2 z2Var, z zVar, boolean z) {
        super(z2Var, zVar, z);
    }

    CLASSNAMEp4(CLASSNAMEz2 z2Var, y yVar, boolean z) {
        super(z2Var, yVar, z);
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
        this.b.u0(new CLASSNAMEo4(fVar), this.d);
        this.i = true;
    }

    public /* synthetic */ void forEachRemaining(Consumer consumer) {
        CLASSNAMEa.b(this, consumer);
    }

    /* access modifiers changed from: package-private */
    public void j() {
        V3 v3 = new V3();
        this.h = v3;
        this.e = this.b.v0(new CLASSNAMEo4(v3));
        this.f = new CLASSNAMEb(this);
    }

    /* renamed from: k */
    public boolean tryAdvance(f fVar) {
        fVar.getClass();
        boolean a = a();
        if (a) {
            V3 v3 = (V3) this.h;
            long j = this.g;
            int w = v3.w(j);
            fVar.accept((v3.c == 0 && w == 0) ? ((double[]) v3.e)[(int) j] : ((double[][]) v3.f)[w][(int) (j - v3.d[w])]);
        }
        return a;
    }

    /* access modifiers changed from: package-private */
    public CLASSNAMEg4 l(y yVar) {
        return new CLASSNAMEp4(this.b, yVar, this.a);
    }

    public u trySplit() {
        return (u) super.trySplit();
    }
}
