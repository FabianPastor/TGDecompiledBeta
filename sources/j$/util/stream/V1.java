package j$.util.stream;

import j$.util.function.Consumer;
/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: classes2.dex */
public final class V1 extends U3 implements InterfaceCLASSNAMEu1, InterfaceCLASSNAMEp1 {
    @Override // j$.util.stream.U3
    public j$.util.t B() {
        return super.mo293spliterator();
    }

    @Override // j$.util.function.Consumer
    /* renamed from: C */
    public /* synthetic */ void accept(Double d) {
        AbstractCLASSNAMEo1.a(this, d);
    }

    @Override // j$.util.stream.A1
    /* renamed from: D */
    public /* synthetic */ void i(Double[] dArr, int i) {
        AbstractCLASSNAMEo1.h(this, dArr, i);
    }

    @Override // j$.util.stream.A1
    /* renamed from: E */
    public /* synthetic */ InterfaceCLASSNAMEu1 r(long j, long j2, j$.util.function.m mVar) {
        return AbstractCLASSNAMEo1.n(this, j, j2, mVar);
    }

    @Override // j$.util.stream.InterfaceCLASSNAMEp1, j$.util.stream.InterfaceCLASSNAMEs1
    /* renamed from: a */
    public A1 mo291a() {
        return this;
    }

    @Override // j$.util.stream.InterfaceCLASSNAMEp1, j$.util.stream.InterfaceCLASSNAMEs1
    /* renamed from: a  reason: collision with other method in class */
    public InterfaceCLASSNAMEu1 mo291a() {
        return this;
    }

    @Override // j$.util.stream.U3, j$.util.function.f
    public void accept(double d) {
        super.accept(d);
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

    @Override // j$.util.function.Consumer
    public /* synthetic */ Consumer andThen(Consumer consumer) {
        return consumer.getClass();
    }

    @Override // j$.util.stream.InterfaceCLASSNAMEz1, j$.util.stream.A1
    /* renamed from: b  reason: collision with other method in class */
    public InterfaceCLASSNAMEz1 mo292b(int i) {
        throw new IndexOutOfBoundsException();
    }

    @Override // j$.util.stream.Z3, j$.util.stream.InterfaceCLASSNAMEz1
    public void d(Object obj, int i) {
        super.d((double[]) obj, i);
    }

    @Override // j$.util.stream.Z3, j$.util.stream.InterfaceCLASSNAMEz1
    public Object e() {
        return (double[]) super.e();
    }

    @Override // j$.util.stream.Z3, j$.util.stream.InterfaceCLASSNAMEz1
    public void g(Object obj) {
        super.g((j$.util.function.f) obj);
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

    @Override // j$.util.stream.U3, j$.util.stream.Z3, java.lang.Iterable, j$.lang.e
    /* renamed from: spliterator  reason: collision with other method in class */
    public j$.util.w mo293spliterator() {
        return super.mo293spliterator();
    }

    @Override // j$.util.stream.InterfaceCLASSNAMEz1, j$.util.stream.A1
    /* renamed from: b */
    public /* bridge */ /* synthetic */ A1 mo292b(int i) {
        mo292b(i);
        throw null;
    }

    @Override // j$.util.stream.U3, j$.util.stream.Z3, java.lang.Iterable, j$.lang.e
    /* renamed from: spliterator */
    public j$.util.u mo293spliterator() {
        return super.mo293spliterator();
    }
}
