package j$.util.stream;

import j$.util.AbstractCLASSNAMEa;
import j$.util.function.Consumer;
/* renamed from: j$.util.stream.o4  reason: case insensitive filesystem */
/* loaded from: classes2.dex */
final class CLASSNAMEo4 extends AbstractCLASSNAMEf4 implements j$.util.t {
    /* JADX INFO: Access modifiers changed from: package-private */
    public CLASSNAMEo4(AbstractCLASSNAMEy2 abstractCLASSNAMEy2, j$.util.function.y yVar, boolean z) {
        super(abstractCLASSNAMEy2, yVar, z);
    }

    CLASSNAMEo4(AbstractCLASSNAMEy2 abstractCLASSNAMEy2, j$.util.u uVar, boolean z) {
        super(abstractCLASSNAMEy2, uVar, z);
    }

    @Override // j$.util.u
    public /* synthetic */ boolean b(Consumer consumer) {
        return AbstractCLASSNAMEa.j(this, consumer);
    }

    @Override // j$.util.w
    /* renamed from: e */
    public void forEachRemaining(j$.util.function.f fVar) {
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

    @Override // j$.util.u
    public /* synthetic */ void forEachRemaining(Consumer consumer) {
        AbstractCLASSNAMEa.b(this, consumer);
    }

    @Override // j$.util.stream.AbstractCLASSNAMEf4
    void j() {
        U3 u3 = new U3();
        this.h = u3;
        this.e = this.b.v0(new CLASSNAMEn4(u3));
        this.f = new CLASSNAMEb(this);
    }

    @Override // j$.util.w
    /* renamed from: k */
    public boolean tryAdvance(j$.util.function.f fVar) {
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

    @Override // j$.util.stream.AbstractCLASSNAMEf4
    AbstractCLASSNAMEf4 l(j$.util.u uVar) {
        return new CLASSNAMEo4(this.b, uVar, this.a);
    }

    @Override // j$.util.stream.AbstractCLASSNAMEf4, j$.util.u
    /* renamed from: trySplit */
    public j$.util.t mo326trySplit() {
        return (j$.util.t) super.mo326trySplit();
    }
}
