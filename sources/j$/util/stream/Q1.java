package j$.util.stream;

import j$.util.function.Consumer;
/* loaded from: classes2.dex */
final class Q1 extends R1 implements InterfaceCLASSNAMEy1 {
    /* JADX INFO: Access modifiers changed from: package-private */
    public Q1(InterfaceCLASSNAMEy1 interfaceCLASSNAMEy1, InterfaceCLASSNAMEy1 interfaceCLASSNAMEy12) {
        super(interfaceCLASSNAMEy1, interfaceCLASSNAMEy12);
    }

    @Override // j$.util.stream.A1
    /* renamed from: a */
    public /* synthetic */ void i(Long[] lArr, int i) {
        AbstractCLASSNAMEo1.j(this, lArr, i);
    }

    @Override // j$.util.stream.InterfaceCLASSNAMEz1
    /* renamed from: f */
    public long[] c(int i) {
        return new long[i];
    }

    @Override // j$.util.stream.A1
    public /* synthetic */ void forEach(Consumer consumer) {
        AbstractCLASSNAMEo1.m(this, consumer);
    }

    @Override // j$.util.stream.A1
    /* renamed from: h */
    public /* synthetic */ InterfaceCLASSNAMEy1 r(long j, long j2, j$.util.function.m mVar) {
        return AbstractCLASSNAMEo1.p(this, j, j2, mVar);
    }

    @Override // j$.util.stream.A1
    /* renamed from: spliterator  reason: collision with other method in class */
    public j$.util.w mo285spliterator() {
        return new CLASSNAMEh2(this);
    }

    @Override // j$.util.stream.A1
    /* renamed from: spliterator */
    public j$.util.u mo285spliterator() {
        return new CLASSNAMEh2(this);
    }
}
