package j$.util.stream;

import j$.util.AbstractCLASSNAMEa;
import j$.util.function.Consumer;
import j$.util.u;
/* renamed from: j$.util.stream.q4  reason: case insensitive filesystem */
/* loaded from: classes2.dex */
final class CLASSNAMEq4 extends AbstractCLASSNAMEf4 implements u.a {
    /* JADX INFO: Access modifiers changed from: package-private */
    public CLASSNAMEq4(AbstractCLASSNAMEy2 abstractCLASSNAMEy2, j$.util.function.y yVar, boolean z) {
        super(abstractCLASSNAMEy2, yVar, z);
    }

    CLASSNAMEq4(AbstractCLASSNAMEy2 abstractCLASSNAMEy2, j$.util.u uVar, boolean z) {
        super(abstractCLASSNAMEy2, uVar, z);
    }

    @Override // j$.util.u
    public /* synthetic */ boolean b(Consumer consumer) {
        return AbstractCLASSNAMEa.k(this, consumer);
    }

    @Override // j$.util.w
    /* renamed from: c */
    public void forEachRemaining(j$.util.function.l lVar) {
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

    @Override // j$.util.u
    public /* synthetic */ void forEachRemaining(Consumer consumer) {
        AbstractCLASSNAMEa.c(this, consumer);
    }

    @Override // j$.util.w
    /* renamed from: g */
    public boolean tryAdvance(j$.util.function.l lVar) {
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

    @Override // j$.util.stream.AbstractCLASSNAMEf4
    void j() {
        W3 w3 = new W3();
        this.h = w3;
        this.e = this.b.v0(new CLASSNAMEp4(w3));
        this.f = new CLASSNAMEb(this);
    }

    @Override // j$.util.stream.AbstractCLASSNAMEf4
    AbstractCLASSNAMEf4 l(j$.util.u uVar) {
        return new CLASSNAMEq4(this.b, uVar, this.a);
    }

    @Override // j$.util.stream.AbstractCLASSNAMEf4, j$.util.u
    /* renamed from: trySplit */
    public u.a mo326trySplit() {
        return (u.a) super.mo326trySplit();
    }
}
