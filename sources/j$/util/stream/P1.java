package j$.util.stream;

import j$.util.function.Consumer;
/* loaded from: classes2.dex */
final class P1 extends R1 implements InterfaceCLASSNAMEw1 {
    /* JADX INFO: Access modifiers changed from: package-private */
    public P1(InterfaceCLASSNAMEw1 interfaceCLASSNAMEw1, InterfaceCLASSNAMEw1 interfaceCLASSNAMEw12) {
        super(interfaceCLASSNAMEw1, interfaceCLASSNAMEw12);
    }

    @Override // j$.util.stream.A1
    /* renamed from: a */
    public /* synthetic */ void i(Integer[] numArr, int i) {
        AbstractCLASSNAMEo1.i(this, numArr, i);
    }

    @Override // j$.util.stream.InterfaceCLASSNAMEz1
    /* renamed from: f */
    public int[] c(int i) {
        return new int[i];
    }

    @Override // j$.util.stream.A1
    public /* synthetic */ void forEach(Consumer consumer) {
        AbstractCLASSNAMEo1.l(this, consumer);
    }

    @Override // j$.util.stream.A1
    /* renamed from: h */
    public /* synthetic */ InterfaceCLASSNAMEw1 r(long j, long j2, j$.util.function.m mVar) {
        return AbstractCLASSNAMEo1.o(this, j, j2, mVar);
    }

    @Override // j$.util.stream.A1
    /* renamed from: spliterator  reason: collision with other method in class */
    public j$.util.w mo285spliterator() {
        return new CLASSNAMEg2(this);
    }

    @Override // j$.util.stream.A1
    /* renamed from: spliterator */
    public j$.util.u mo285spliterator() {
        return new CLASSNAMEg2(this);
    }
}
