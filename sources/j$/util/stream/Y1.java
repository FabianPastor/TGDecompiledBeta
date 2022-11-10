package j$.util.stream;

import j$.util.function.Consumer;
/* loaded from: classes2.dex */
final class Y1 extends AbstractCLASSNAMEa2 implements InterfaceCLASSNAMEy1 {
    @Override // j$.util.stream.A1
    /* renamed from: a */
    public /* synthetic */ void i(Long[] lArr, int i) {
        AbstractCLASSNAMEo1.j(this, lArr, i);
    }

    @Override // j$.util.stream.AbstractCLASSNAMEa2, j$.util.stream.A1
    /* renamed from: b  reason: collision with other method in class */
    public InterfaceCLASSNAMEz1 moNUMb(int i) {
        throw new IndexOutOfBoundsException();
    }

    @Override // j$.util.stream.InterfaceCLASSNAMEz1
    public Object e() {
        long[] jArr;
        jArr = AbstractCLASSNAMEx2.f;
        return jArr;
    }

    @Override // j$.util.stream.AbstractCLASSNAMEa2, j$.util.stream.A1
    /* renamed from: f */
    public /* synthetic */ InterfaceCLASSNAMEy1 r(long j, long j2, j$.util.function.m mVar) {
        return AbstractCLASSNAMEo1.p(this, j, j2, mVar);
    }

    @Override // j$.util.stream.A1
    public /* synthetic */ void forEach(Consumer consumer) {
        AbstractCLASSNAMEo1.m(this, consumer);
    }

    @Override // j$.util.stream.A1
    /* renamed from: spliterator  reason: collision with other method in class */
    public j$.util.w moNUMspliterator() {
        return j$.util.L.d();
    }

    @Override // j$.util.stream.AbstractCLASSNAMEa2, j$.util.stream.A1
    /* renamed from: b */
    public /* bridge */ /* synthetic */ A1 moNUMb(int i) {
        moNUMb(i);
        throw null;
    }

    @Override // j$.util.stream.A1
    /* renamed from: spliterator */
    public j$.util.u moNUMspliterator() {
        return j$.util.L.d();
    }
}
