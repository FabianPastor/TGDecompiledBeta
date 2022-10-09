package j$.util.stream;

import j$.util.function.Consumer;
/* loaded from: classes2.dex */
final class X1 extends AbstractCLASSNAMEa2 implements InterfaceCLASSNAMEw1 {
    @Override // j$.util.stream.A1
    /* renamed from: a */
    public /* synthetic */ void i(Integer[] numArr, int i) {
        AbstractCLASSNAMEo1.i(this, numArr, i);
    }

    @Override // j$.util.stream.AbstractCLASSNAMEa2, j$.util.stream.A1
    /* renamed from: b  reason: collision with other method in class */
    public InterfaceCLASSNAMEz1 mo288b(int i) {
        throw new IndexOutOfBoundsException();
    }

    @Override // j$.util.stream.InterfaceCLASSNAMEz1
    public Object e() {
        int[] iArr;
        iArr = AbstractCLASSNAMEx2.e;
        return iArr;
    }

    @Override // j$.util.stream.AbstractCLASSNAMEa2, j$.util.stream.A1
    /* renamed from: f */
    public /* synthetic */ InterfaceCLASSNAMEw1 r(long j, long j2, j$.util.function.m mVar) {
        return AbstractCLASSNAMEo1.o(this, j, j2, mVar);
    }

    @Override // j$.util.stream.A1
    public /* synthetic */ void forEach(Consumer consumer) {
        AbstractCLASSNAMEo1.l(this, consumer);
    }

    @Override // j$.util.stream.A1
    /* renamed from: spliterator  reason: collision with other method in class */
    public j$.util.w mo285spliterator() {
        return j$.util.L.c();
    }

    @Override // j$.util.stream.AbstractCLASSNAMEa2, j$.util.stream.A1
    /* renamed from: b */
    public /* bridge */ /* synthetic */ A1 mo288b(int i) {
        mo288b(i);
        throw null;
    }

    @Override // j$.util.stream.A1
    /* renamed from: spliterator */
    public j$.util.u mo285spliterator() {
        return j$.util.L.c();
    }
}
