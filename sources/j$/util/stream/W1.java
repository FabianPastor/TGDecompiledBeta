package j$.util.stream;

import j$.util.function.Consumer;
/* loaded from: classes2.dex */
final class W1 extends AbstractCLASSNAMEa2 implements InterfaceCLASSNAMEu1 {
    @Override // j$.util.stream.A1
    /* renamed from: a */
    public /* synthetic */ void i(Double[] dArr, int i) {
        AbstractCLASSNAMEo1.h(this, dArr, i);
    }

    @Override // j$.util.stream.AbstractCLASSNAMEa2, j$.util.stream.A1
    /* renamed from: b  reason: collision with other method in class */
    public InterfaceCLASSNAMEz1 mo292b(int i) {
        throw new IndexOutOfBoundsException();
    }

    @Override // j$.util.stream.InterfaceCLASSNAMEz1
    public Object e() {
        double[] dArr;
        dArr = AbstractCLASSNAMEx2.g;
        return dArr;
    }

    @Override // j$.util.stream.AbstractCLASSNAMEa2, j$.util.stream.A1
    /* renamed from: f */
    public /* synthetic */ InterfaceCLASSNAMEu1 r(long j, long j2, j$.util.function.m mVar) {
        return AbstractCLASSNAMEo1.n(this, j, j2, mVar);
    }

    @Override // j$.util.stream.A1
    public /* synthetic */ void forEach(Consumer consumer) {
        AbstractCLASSNAMEo1.k(this, consumer);
    }

    @Override // j$.util.stream.A1
    /* renamed from: spliterator  reason: collision with other method in class */
    public j$.util.w mo289spliterator() {
        return j$.util.L.b();
    }

    @Override // j$.util.stream.AbstractCLASSNAMEa2, j$.util.stream.A1
    /* renamed from: b */
    public /* bridge */ /* synthetic */ A1 mo292b(int i) {
        mo292b(i);
        throw null;
    }

    @Override // j$.util.stream.A1
    /* renamed from: spliterator */
    public j$.util.u mo289spliterator() {
        return j$.util.L.b();
    }
}
