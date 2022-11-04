package j$.util.stream;

import j$.util.function.Consumer;
/* loaded from: classes2.dex */
final class O1 extends R1 implements InterfaceCLASSNAMEu1 {
    /* JADX INFO: Access modifiers changed from: package-private */
    public O1(InterfaceCLASSNAMEu1 interfaceCLASSNAMEu1, InterfaceCLASSNAMEu1 interfaceCLASSNAMEu12) {
        super(interfaceCLASSNAMEu1, interfaceCLASSNAMEu12);
    }

    @Override // j$.util.stream.A1
    /* renamed from: a */
    public /* synthetic */ void i(Double[] dArr, int i) {
        AbstractCLASSNAMEo1.h(this, dArr, i);
    }

    @Override // j$.util.stream.InterfaceCLASSNAMEz1
    /* renamed from: f */
    public double[] c(int i) {
        return new double[i];
    }

    @Override // j$.util.stream.A1
    public /* synthetic */ void forEach(Consumer consumer) {
        AbstractCLASSNAMEo1.k(this, consumer);
    }

    @Override // j$.util.stream.A1
    /* renamed from: h */
    public /* synthetic */ InterfaceCLASSNAMEu1 r(long j, long j2, j$.util.function.m mVar) {
        return AbstractCLASSNAMEo1.n(this, j, j2, mVar);
    }

    @Override // j$.util.stream.A1
    /* renamed from: spliterator  reason: collision with other method in class */
    public j$.util.w mo289spliterator() {
        return new CLASSNAMEf2(this);
    }

    @Override // j$.util.stream.A1
    /* renamed from: spliterator */
    public j$.util.u mo289spliterator() {
        return new CLASSNAMEf2(this);
    }
}
