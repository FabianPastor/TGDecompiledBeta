package j$.util.stream;

import j$.util.function.Consumer;
/* JADX INFO: Access modifiers changed from: package-private */
/* renamed from: j$.util.stream.n2  reason: case insensitive filesystem */
/* loaded from: classes2.dex */
public final class CLASSNAMEn2 extends Y3 implements InterfaceCLASSNAMEy1, InterfaceCLASSNAMEr1 {
    @Override // j$.util.stream.Y3
    public j$.util.v B() {
        return super.mo289spliterator();
    }

    @Override // j$.util.function.Consumer
    /* renamed from: C */
    public /* synthetic */ void accept(Long l) {
        AbstractCLASSNAMEo1.c(this, l);
    }

    @Override // j$.util.stream.A1
    /* renamed from: D */
    public /* synthetic */ void i(Long[] lArr, int i) {
        AbstractCLASSNAMEo1.j(this, lArr, i);
    }

    @Override // j$.util.stream.A1
    /* renamed from: E */
    public /* synthetic */ InterfaceCLASSNAMEy1 r(long j, long j2, j$.util.function.m mVar) {
        return AbstractCLASSNAMEo1.p(this, j, j2, mVar);
    }

    @Override // j$.util.stream.InterfaceCLASSNAMEr1, j$.util.stream.InterfaceCLASSNAMEs1
    /* renamed from: a */
    public A1 mo287a() {
        return this;
    }

    @Override // j$.util.stream.InterfaceCLASSNAMEr1, j$.util.stream.InterfaceCLASSNAMEs1
    /* renamed from: a  reason: collision with other method in class */
    public InterfaceCLASSNAMEy1 mo287a() {
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

    @Override // j$.util.stream.Y3, j$.util.function.q
    public void accept(long j) {
        super.accept(j);
    }

    @Override // j$.util.function.Consumer
    public /* synthetic */ Consumer andThen(Consumer consumer) {
        return consumer.getClass();
    }

    @Override // j$.util.stream.InterfaceCLASSNAMEz1, j$.util.stream.A1
    /* renamed from: b  reason: collision with other method in class */
    public InterfaceCLASSNAMEz1 mo288b(int i) {
        throw new IndexOutOfBoundsException();
    }

    @Override // j$.util.stream.Z3, j$.util.stream.InterfaceCLASSNAMEz1
    public void d(Object obj, int i) {
        super.d((long[]) obj, i);
    }

    @Override // j$.util.stream.Z3, j$.util.stream.InterfaceCLASSNAMEz1
    public Object e() {
        return (long[]) super.e();
    }

    @Override // j$.util.stream.Z3, j$.util.stream.InterfaceCLASSNAMEz1
    public void g(Object obj) {
        super.g((j$.util.function.q) obj);
    }

    @Override // j$.util.stream.InterfaceCLASSNAMEm3
    public void m() {
    }

    @Override // j$.util.stream.InterfaceCLASSNAMEm3
    public void n(long j) {
        clear();
        x(j);
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
    public /* synthetic */ Object[] q(j$.util.function.m mVar) {
        return AbstractCLASSNAMEo1.g(this, mVar);
    }

    @Override // j$.util.stream.Y3, j$.util.stream.Z3, java.lang.Iterable, j$.lang.e
    /* renamed from: spliterator  reason: collision with other method in class */
    public j$.util.w mo289spliterator() {
        return super.mo289spliterator();
    }

    @Override // j$.util.stream.InterfaceCLASSNAMEz1, j$.util.stream.A1
    /* renamed from: b */
    public /* bridge */ /* synthetic */ A1 mo288b(int i) {
        mo288b(i);
        throw null;
    }

    @Override // j$.util.stream.Y3, j$.util.stream.Z3, java.lang.Iterable, j$.lang.e
    /* renamed from: spliterator */
    public j$.util.u mo289spliterator() {
        return super.mo289spliterator();
    }
}
