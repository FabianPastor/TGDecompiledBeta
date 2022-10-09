package j$.util.stream;

import j$.util.AbstractCLASSNAMEa;
import j$.util.function.Consumer;
/* loaded from: classes2.dex */
final class s4 extends AbstractCLASSNAMEf4 implements j$.util.v {
    /* JADX INFO: Access modifiers changed from: package-private */
    public s4(AbstractCLASSNAMEy2 abstractCLASSNAMEy2, j$.util.function.y yVar, boolean z) {
        super(abstractCLASSNAMEy2, yVar, z);
    }

    s4(AbstractCLASSNAMEy2 abstractCLASSNAMEy2, j$.util.u uVar, boolean z) {
        super(abstractCLASSNAMEy2, uVar, z);
    }

    @Override // j$.util.u
    public /* synthetic */ boolean b(Consumer consumer) {
        return AbstractCLASSNAMEa.l(this, consumer);
    }

    @Override // j$.util.w
    /* renamed from: d */
    public void forEachRemaining(j$.util.function.q qVar) {
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

    @Override // j$.util.u
    public /* synthetic */ void forEachRemaining(Consumer consumer) {
        AbstractCLASSNAMEa.d(this, consumer);
    }

    @Override // j$.util.w
    /* renamed from: i */
    public boolean tryAdvance(j$.util.function.q qVar) {
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

    @Override // j$.util.stream.AbstractCLASSNAMEf4
    void j() {
        Y3 y3 = new Y3();
        this.h = y3;
        this.e = this.b.v0(new r4(y3));
        this.f = new CLASSNAMEb(this);
    }

    @Override // j$.util.stream.AbstractCLASSNAMEf4
    AbstractCLASSNAMEf4 l(j$.util.u uVar) {
        return new s4(this.b, uVar, this.a);
    }

    @Override // j$.util.stream.AbstractCLASSNAMEf4, j$.util.u
    /* renamed from: trySplit  reason: collision with other method in class */
    public j$.util.v mo322trySplit() {
        return (j$.util.v) super.mo322trySplit();
    }
}
