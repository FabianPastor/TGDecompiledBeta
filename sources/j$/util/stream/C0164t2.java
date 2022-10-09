package j$.util.stream;

import j$.util.function.Consumer;
/* JADX INFO: Access modifiers changed from: package-private */
/* renamed from: j$.util.stream.t2  reason: case insensitive filesystem */
/* loaded from: classes2.dex */
public final class CLASSNAMEt2 extends CLASSNAMEa4 implements A1, InterfaceCLASSNAMEs1 {
    @Override // j$.util.stream.InterfaceCLASSNAMEs1
    /* renamed from: a */
    public A1 mo287a() {
        return this;
    }

    @Override // j$.util.stream.InterfaceCLASSNAMEm3
    public /* synthetic */ void accept(double d) {
        AbstractCLASSNAMEo1.f(this);
        throw null;
    }

    @Override // j$.util.stream.InterfaceCLASSNAMEm3
    public /* synthetic */ void accept(int i) {
        AbstractCLASSNAMEo1.d(this);
        throw null;
    }

    @Override // j$.util.stream.InterfaceCLASSNAMEm3, j$.util.stream.InterfaceCLASSNAMEl3, j$.util.function.q
    public /* synthetic */ void accept(long j) {
        AbstractCLASSNAMEo1.e(this);
        throw null;
    }

    @Override // j$.util.stream.CLASSNAMEa4, j$.util.function.Consumer
    public void accept(Object obj) {
        super.accept(obj);
    }

    @Override // j$.util.stream.A1
    /* renamed from: b */
    public A1 mo288b(int i) {
        throw new IndexOutOfBoundsException();
    }

    @Override // j$.util.stream.CLASSNAMEa4, j$.lang.e
    public void forEach(Consumer consumer) {
        super.forEach(consumer);
    }

    @Override // j$.util.stream.CLASSNAMEa4, j$.util.stream.A1
    public void i(Object[] objArr, int i) {
        super.i(objArr, i);
    }

    @Override // j$.util.stream.InterfaceCLASSNAMEm3
    public void m() {
    }

    @Override // j$.util.stream.InterfaceCLASSNAMEm3
    public void n(long j) {
        clear();
        u(j);
    }

    @Override // j$.util.stream.InterfaceCLASSNAMEm3
    public /* synthetic */ boolean o() {
        return false;
    }

    @Override // j$.util.stream.A1
    public /* synthetic */ int p() {
        return 0;
    }

    @Override // j$.util.stream.A1
    public Object[] q(j$.util.function.m mVar) {
        long count = count();
        if (count < NUM) {
            Object[] objArr = (Object[]) mVar.apply((int) count);
            i(objArr, 0);
            return objArr;
        }
        throw new IllegalArgumentException("Stream size exceeds max array size");
    }

    @Override // j$.util.stream.A1
    public /* synthetic */ A1 r(long j, long j2, j$.util.function.m mVar) {
        return AbstractCLASSNAMEo1.q(this, j, j2, mVar);
    }

    @Override // j$.util.stream.CLASSNAMEa4, java.lang.Iterable, j$.lang.e
    /* renamed from: spliterator */
    public j$.util.u mo289spliterator() {
        return super.mo289spliterator();
    }
}
